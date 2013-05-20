/**
 * Bristlecone Test Tools for Databases
 * Copyright (C) 2006-2007 Continuent Inc.
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
 * Initial developer(s): Robert Hodges and Ralph Hannus.
 * Contributor(s):
 */

package com.continuent.bristlecone.benchmark.monitors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.continuent.bristlecone.benchmark.Monitor;
import com.continuent.bristlecone.benchmark.db.Column;
import com.continuent.bristlecone.benchmark.db.Table;
import com.continuent.bristlecone.benchmark.db.TableHelper;

/**
 * This class implements a monitor that tracks slave latency during the test and
 * holds up the test completion until the slave is up to date with the master.
 * It is useful for tests that measure time for all transactions to reach the
 * slave DBMS.
 * 
 * @author rhodges
 */
public class SlaveMonitor implements Monitor
{
    private static final Logger logger = Logger.getLogger(SlaveMonitor.class);

    // Control variables.
    private boolean             done;

    // Heartbeat table and database access variables.
    private Table               heartbeatTab;
    private Connection          masterConn;
    private PreparedStatement   updateStmt;
    private Connection          slaveConn;
    private Statement           slaveStmt;
    private String              selectSQL;

    // Random test key used to prevent lamentable failures due to
    // confused replication.
    private int                 key;

    /**
     * Perform basic initialization.
     */
    public void prepare(Properties properties) throws Exception
    {
        // Fetch connection parameters.
        String url = properties.getProperty("url");
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");
        String replicaUrl = properties.getProperty("replicaUrl");
        if (replicaUrl == null)
            replicaUrl = properties.getProperty("monitorReplicaUrl");

        // Define heartbeat table.
        heartbeatTab = new Table();
        heartbeatTab.setName("monitor_heartbeat");
        Column id = new Column();
        id.setName("id");
        id.setType(Types.INTEGER);
        heartbeatTab.addColumn(id);
        Column updateTime = new Column();
        updateTime.setName("update_time");
        updateTime.setType(Types.BIGINT);
        heartbeatTab.addColumn(updateTime);

        // Open connection to master and create table.
        logger.info("Creating master heartbeat table: "
                + heartbeatTab.getName());
        TableHelper masterTableHelper = new TableHelper(url, user, password);
        masterTableHelper.create(heartbeatTab, true);
        masterConn = masterTableHelper.getConnection();

        // Insert a single row in the heartbeat table with the random
        // key.
        key = (int) (Math.random() * 10000.);
        Object[] args = {key, System.currentTimeMillis()};
        masterTableHelper.insert(heartbeatTab, args);

        // Create statement to update the table on the master.
        String updateSQL = "UPDATE monitor_heartbeat SET update_time=? WHERE id="
                + key;
        updateStmt = masterConn.prepareStatement(updateSQL);

        // Create statement to read from the table on the slave.
        TableHelper slaveTableHelper = new TableHelper(replicaUrl, user,
                password);
        slaveConn = slaveTableHelper.getConnection();
        slaveStmt = slaveConn.createStatement();
        selectSQL = "SELECT update_time FROM monitor_heartbeat WHERE id=" + key;

        // Wait for the random key to return from the table. This ensures
        // that we will not be confused by stale replicated data on the
        // slave.
        while (selectTime() == -1)
        {
            Thread.sleep(1000);
        }
    }

    /** Perform monitoring. */
    public void run()
    {
        long lastUpdateTime = -1;

        try
        {
            while (!Thread.interrupted())
            {
                long now = System.currentTimeMillis();

                // Update on the master.
                try
                {
                    updateStmt.setLong(1, now);
                    updateStmt.execute();
                }
                catch (SQLException e)
                {
                    logger.warn("Unable to update slave heartbeat table: "
                            + e.getMessage());
                    if (logger.isDebugEnabled())
                        logger.debug("SQL failure", e);
                }
                lastUpdateTime = now;

                // Sleep for a bit to see if replicator gets the event over
                // right away.
                Thread.sleep(100);

                // Fetch the latest value from the slave and compute lag.
                long slaveTime = this.selectTime();
                if (slaveTime >= 0)
                {
                    double lag = (now - slaveTime) / 1000.0;
                    logger.info("Current slave lag in seconds: " + lag);
                }
                else
                    logger
                            .warn("Unable to read slave heartbeat table to compute lag!");

                // Sleep again for a bit.
                Thread.sleep(900);
            }
        }
        catch (InterruptedException e)
        {
            logger.info("Monitor thread was interrupted");
        }

        // Now wait until the master and slave tables match. At this point we
        // don't update any more.
        logger.info("Waiting for slave to catch up with master");
        while (!done)
        {
            long slaveTime = this.selectTime();
            if (slaveTime >= 0)
            {
                double lag = (lastUpdateTime - slaveTime) / 1000.0;
                logger.info("Current slave lag in seconds: " + lag);
            }
            else
                logger
                        .warn("Unable to read slave heartbeat table to compute lag!");

            if (slaveTime >= lastUpdateTime)
            {
                logger.info("Slave is caught up with master");
                done = true;
            }

            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                logger.warn("Wait for slave catch-up was terminated");
            }
        }

        logger.info("Monitor task is finished");
    }

    // Select the time from the slave heartbeat table or return -1 if we are
    // unable to find it.
    private long selectTime()
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
    public void cleanup() throws Exception
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