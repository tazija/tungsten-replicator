/**
 * Bristlecone Test Tools for Databases
 * Copyright (C) 2011 Continuent Inc.
 * Contact: bristlecone@lists.forge.continuent.org
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of version 2 of the GNU General Public License as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA
 *
 * Initial developer(s): Robert Hodges
 * Contributor(s): Linas Virbalas
 */

package com.continuent.bristlecone.croc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.apache.log4j.Logger;

import com.continuent.bristlecone.benchmark.db.Column;
import com.continuent.bristlecone.benchmark.db.Table;
import com.continuent.bristlecone.benchmark.db.TableHelper;

/**
 * Implements a generic flush capability between master and slave servers using
 * a generic heartbeat table. This allows clients to ensure liveness of
 * replication and ensure that the slave is caught up.
 * 
 * @author rhodges
 */
public class LivenessChecker
{
    private static final Logger logger       = Logger.getLogger(LivenessChecker.class);

    // Parameters.
    CrocContext                 context;

    // Heartbeat table and database access variables.
    private Table               heartbeatTab;
    private Connection          masterConn;
    private PreparedStatement   updateStmt;
    private Connection          slaveConn;
    private Statement           slaveStmt;
    private String              selectSQL;
    private long                currentSeqno = 0;

    // Random test key used to prevent lamentable failures due to
    // confused replication.
    private int                 key;

    /** Create a new liveness checker. */
    public LivenessChecker(CrocContext context)
    {
        this.context = context;
    }

    /**
     * Perform basic initialization.
     */
    public void prepare() throws Exception
    {
        // Define heartbeat table.
        heartbeatTab = new Table();
        heartbeatTab.setName("monitor_heartbeat");
        Column id = new Column();
        id.setName("id");
        id.setType(Types.INTEGER);
        id.setPrimaryKey(true);
        heartbeatTab.addColumn(id);

        Column seqno = new Column();
        seqno.setName("seqno");
        seqno.setType(Types.BIGINT);
        heartbeatTab.addColumn(seqno);

        Column updateTime = new Column();
        updateTime.setName("update_time");
        updateTime.setType(Types.BIGINT);
        heartbeatTab.addColumn(updateTime);

        // Create a random key for the heartbeat table. This helps
        // avoid problems if replication is confused.
        key = (int) (Math.random() * 10000.);

        // Open connection to master and create table.
        logger.info("Creating master heartbeat table: "
                + heartbeatTab.getName());
        TableHelper masterTableHelper = new TableHelper(context.getMasterUrl(),
                context.getMasterUser(), context.getMasterPassword(),
                context.getDefaultSchema());
        masterTableHelper.create(heartbeatTab, true);
        masterConn = masterTableHelper.getConnection();

        // Create statement to update the table on the master.
        String updateSQL = "UPDATE monitor_heartbeat SET seqno=?, update_time=? WHERE id="
                + key;
        updateStmt = masterConn.prepareStatement(updateSQL);

        // Create statement to read from the table on the slave. If there is no
        // DDL replication create the slave table explicitly.
        TableHelper slaveTableHelper = new TableHelper(context.getSlaveUrl(),
                context.getSlaveUser(), context.getSlavePassword(),
                context.getDefaultSchema());
        slaveConn = slaveTableHelper.getConnection();
        if (!context.isDdlReplication())
        {
            logger.info("Creating slave heartbeat table: "
                    + heartbeatTab.getName());
            slaveTableHelper.create(heartbeatTab, true);
            if (context.isStageTables())
            {
                logger.debug("Creating staging table for slave heartbeat table: "
                        + heartbeatTab.getName());
                TableHelper stageTableHelper = new TableHelper(
                        context.getSlaveStageUrl(), context.getSlaveUser(),
                        context.getSlavePassword(), context.getDefaultSchema());
                stageTableHelper.createStageTable(heartbeatTab, true,
                        context.isNewStageFormat());
            }
        }
        slaveStmt = slaveConn.createStatement();
        selectSQL = "SELECT seqno FROM monitor_heartbeat WHERE id=" + key;

        // Insert a single row in the heartbeat table with the random
        // key.
        Object[] args = {key, currentSeqno++, System.currentTimeMillis()};
        masterTableHelper.insert(heartbeatTab, args);
    }

    /** Flush between master and slave. */
    public boolean flush(int timeout) throws CrocException
    {
        boolean isFlushed = false;
        long now = System.currentTimeMillis();
        long timeoutMillis = now + (timeout * 1000);

        // Update on the master.
        currentSeqno++;
        try
        {
            updateStmt.setLong(1, currentSeqno);
            updateStmt.setLong(2, now);
            int rows = updateStmt.executeUpdate();
            if (logger.isDebugEnabled())
            {
                logger.debug("Updated master heartbeat table: id=" + key
                        + " seqno=" + currentSeqno + " update_time=" + now
                        + " rows=" + rows);
            }
            if (rows == 0)
            {
                throw new CrocException(
                        "Heartbeat table update did not affect any rows");
            }
        }
        catch (SQLException e)
        {
            throw new CrocException("Unable to update master heartbeat table: "
                    + e.getMessage(), e);
        }

        // Poll slave sequence number until it matches.
        while (System.currentTimeMillis() < timeoutMillis)
        {
            // Check the slave sequence number.
            long slaveSeqno = selectSeqno();
            if (slaveSeqno >= currentSeqno)
            {
                isFlushed = true;
                break;
            }

            // Bide a wee.
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                logger.warn("Wait for slave catch-up was terminated");
            }
        }

        // Return flush status.
        return isFlushed;
    }

    // Select the seqno from the slave heartbeat table or return -1 if we are
    // unable to find it.
    private long selectSeqno()
    {
        ResultSet rs = null;
        long result = -1;
        try
        {
            rs = slaveStmt.executeQuery(selectSQL);
            while (rs.next())
            {
                result = rs.getLong(1);
            }
        }
        catch (SQLException e)
        {
        }
        finally
        {
            closeResultSet(rs);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.continuent.bristlecone.benchmark.Monitor#cleanup()
     */
    public void cleanup()
    {
        closeStatement(updateStmt);
        closeStatement(slaveStmt);
        closeConnection(masterConn);
        closeConnection(slaveConn);
    }

    // Private routine to close a JDBC result set.
    private void closeResultSet(ResultSet rs)
    {
        if (rs != null)
        {
            try
            {
                rs.close();
            }
            catch (SQLException e)
            {
            }
        }
    }

    // Private routine to close a JDBC statement.
    private void closeStatement(Statement s)
    {
        if (s != null)
        {
            try
            {
                s.close();
            }
            catch (SQLException e)
            {
            }
        }
    }

    // Private routine to close a JDBC connection.
    private void closeConnection(Connection c)
    {
        if (c != null)
        {
            try
            {
                c.close();
            }
            catch (SQLException e)
            {
            }
        }
    }
}