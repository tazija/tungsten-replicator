/**
 * Tungsten Scale-Out Stack
 * Copyright (C) 2011-2013 Continuent Inc.
 * Contact: tungsten@continuent.org
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
 * Contributor(s): 
 */

package com.continuent.tungsten.replicator.thl;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.continuent.tungsten.replicator.ReplicatorException;
import com.continuent.tungsten.replicator.channel.ShardChannelTable;
import com.continuent.tungsten.replicator.conf.ReplicatorRuntime;
import com.continuent.tungsten.replicator.consistency.ConsistencyTable;
import com.continuent.tungsten.replicator.database.Database;
import com.continuent.tungsten.replicator.database.DatabaseFactory;
import com.continuent.tungsten.replicator.database.Table;
import com.continuent.tungsten.replicator.event.ReplDBMSHeader;
import com.continuent.tungsten.replicator.event.ReplDBMSHeaderData;
import com.continuent.tungsten.replicator.heartbeat.HeartbeatTable;
import com.continuent.tungsten.replicator.plugin.PluginContext;
import com.continuent.tungsten.replicator.shard.ShardTable;

/**
 * Encapsulates management of catalog tables used by THL.
 * 
 * @author <a href="mailto:robert.hodges@continuent.com">Robert Hodges</a>
 * @version 1.0
 */
public class CatalogManager
{
    private static Logger     logger = Logger.getLogger(CatalogManager.class);

    private ReplicatorRuntime runtime;
    private String            url;
    private String            user;
    private String            password;
    private String            metadataSchema;
    private String            vendor;
    private boolean           privileged;

    // private Database conn;
    private CommitSeqnoTable  commitSeqnoTable;
    private ShardChannelTable channelTable;

    // Dummy task ID. This is used for updates of the trep_commit_seqno when
    // operating as a master.
    private int               taskId = 0;

    /**
     * Creates a new Catalog manager.
     * 
     * @param runtime runtime
     * @throws ReplicatorException
     */
    public CatalogManager(ReplicatorRuntime runtime) throws ReplicatorException
    {
        this.runtime = runtime;
    }

    /**
     * Set DBMS connection information. Connections will be allocated as needed.
     * 
     * @param url Database url
     * @param user Database user name
     * @param password Database user password
     * @param metadataSchema Name of replication service schema
     * @param vendor Used when it's impossible to distinguish correct DBMS
     *            vendor from URL only. This is especially the case with
     *            Greenplum, when URL is the same as for PostgreSQL. Thus
     *            values: "postgresql" for PostgreSQL, "greenplum" for
     *            Greenplum, null for MySQL.
     * @throws ReplicatorException
     */
    public void connect(String url, String user, String password,
            String metadataSchema, String vendor) throws ReplicatorException
    {
        this.url = url;
        this.user = user;
        this.password = password;
        this.metadataSchema = metadataSchema;
        this.vendor = vendor;
        this.privileged = runtime.isMaster()
                || runtime.isPrivilegedSlaveUpdate();
    }

    /**
     * Returns a connection using the current URL information.
     * 
     * @throws THLException
     */
    private Database getConnection() throws THLException
    {
        try
        {
            // Connect and log updates only if requested.
            Database conn = DatabaseFactory.createDatabase(url, user, password,
                    privileged, vendor);
            conn.connect(runtime.logReplicatorUpdates());
            return conn;
        }
        catch (SQLException e)
        {
            throw new THLException("Unable to connect to DBMS: url=" + url
                    + " message=" + e.getMessage(), e);
        }
    }

    /**
     * Releases a connection.
     */
    private void releaseConnection(Database conn)
    {
        conn.close();
    }

    /**
     * Prepare catalog schema schema.
     * 
     * @param context Replicator plugin context
     * @throws ReplicatorException thrown on failure
     */
    public void prepareSchema(PluginContext context) throws ReplicatorException
    {
        // Allocate a connection to the DBMS.
        Database conn = getConnection();
        try
        {
            // Set default schema if supported.
            if (conn.supportsUseDefaultSchema() && metadataSchema != null)
            {
                if (conn.supportsCreateDropSchema())
                    conn.createSchema(metadataSchema);
                conn.useDefaultSchema(metadataSchema);
            }

            // Create tables, allowing schema changes to be logged if requested.
            if (conn.supportsControlSessionLevelLogging())
            {
                if (runtime.logReplicatorUpdates())
                {
                    logger.info("Logging schema creation");
                    conn.controlSessionLevelLogging(false);
                }
                else
                    conn.controlSessionLevelLogging(true);
            }

            // Create commit seqno table.
            commitSeqnoTable = new CommitSeqnoTable(conn, metadataSchema,
                    runtime.getTungstenTableType(),
                    runtime.nativeSlaveTakeover());
            commitSeqnoTable.initializeTable(context.getChannels());

            // Create consistency table
            Table consistency = ConsistencyTable
                    .getConsistencyTableDefinition(metadataSchema);
            conn.createTable(consistency, false, runtime.getTungstenTableType());

            // Create heartbeat table.
            HeartbeatTable heartbeatTable = new HeartbeatTable(metadataSchema,
                    runtime.getTungstenTableType(), runtime.getServiceName());
            heartbeatTable.initializeHeartbeatTable(conn);

            // Create shard table if it does not exist
            ShardTable shardTable = new ShardTable(metadataSchema,
                    runtime.getTungstenTableType());
            shardTable.initializeShardTable(conn);

            // Create channel table.
            channelTable = new ShardChannelTable(metadataSchema,
                    runtime.getTungstenTableType());
            channelTable.initializeShardTable(conn, context.getChannels());
        }
        catch (SQLException e)
        {
            throw new THLException(e);
        }

        // Should release connection here but we do not or it will cause updates
        // on trep_commit_seqno to fail.
        // This can lead to a connection leak ! Connection will now be released
        // when releasing the CatalogMManager object
    }

    /**
     * Updates the trep_commit_seqno table with latest event.
     */
    public void updateCommitSeqnoTable(THLEvent event) throws SQLException
    {
        // Recreate header data.
        long applyLatency = (System.currentTimeMillis() - event
                .getSourceTstamp().getTime()) / 1000;
        ReplDBMSHeaderData header = new ReplDBMSHeaderData(event.getSeqno(),
                event.getFragno(), event.getLastFrag(), event.getSourceId(),
                event.getEpochNumber(), event.getEventId(), event.getShardId(),
                event.getSourceTstamp(), applyLatency);

        commitSeqnoTable.updateLastCommitSeqno(taskId, header, applyLatency);
    }

    /**
     * Close database connection.
     */
    public void close(PluginContext context)
    {
        // Reduce tasks in task table if possible. If tasks are reduced,
        // clear the channel table.
        Database conn = null;
        try
        {
            // Check if table is null (this happens if database is not
            // available)
            if (commitSeqnoTable != null)
            {
                // Get a connection first...
                conn = getConnection();

                int channels = context.getChannels();
                boolean reduced = commitSeqnoTable.reduceTasks(conn, channels);

                if (reduced && channelTable != null)
                {
                    channelTable.reduceAssignments(conn, channels);
                }
            }
        }
        catch (Exception e)
        {
            logger.warn("Unable to reduce tasks information", e);
        }
        finally
        {
            if (conn != null)
                releaseConnection(conn);
        }
        commitSeqnoTable.release();
    }

    /**
     * Return the minimum last applied event as stored in the CommitSeqnoTable.
     * 
     * @return the last applied event, or null if nothing was found
     * @throws ReplicatorException
     */
    public ReplDBMSHeader getMinLastEvent() throws ReplicatorException
    {
        if (commitSeqnoTable == null)
            return null;

        try
        {
            return commitSeqnoTable.minCommitSeqno();
        }
        catch (SQLException e)
        {
            throw new THLException(e);
        }
    }
}