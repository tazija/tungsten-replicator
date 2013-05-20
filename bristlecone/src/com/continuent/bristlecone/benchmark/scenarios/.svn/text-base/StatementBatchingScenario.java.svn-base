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

package com.continuent.bristlecone.benchmark.scenarios;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.continuent.bristlecone.benchmark.Scenario;
import com.continuent.bristlecone.benchmark.db.Column;
import com.continuent.bristlecone.benchmark.db.DataGenerator;
import com.continuent.bristlecone.benchmark.db.Table;
import com.continuent.bristlecone.benchmark.db.TableSet;
import com.continuent.bristlecone.benchmark.db.TableSetHelper;

/**
 * Implements a scenario that tests performance effects of batching INSERT
 * statements. Tests may choose the number of statements to batch in each
 * transaction. Inserts are non-conflicting (i.e., should never deadlock).
 * <p/>
 * This scenario can be parameterized by the usual options such as tables,
 * datarows, etc.
 * 
 * @author rhodges
 */
public class StatementBatchingScenario implements Scenario
{
    private static final Logger logger         = Logger.getLogger(StatementBatchingScenario.class);

    // Scenario properties.
    /** Url of the database on which we are running the test. */
    protected String            url;

    /** Database user name. */
    protected String            user;

    /** Database password (leaving it null equates to empty password). */
    protected String            password       = "";

    /** Datatype of the payload column in the benchmark table. */
    protected String            datatype       = "varchar";

    /**
     * Column width of the payload column, e.g., 10 for varchar equates to
     * varchar(10).
     */
    protected int               datawidth      = 10;

    /** Number of inserts per batch. */
    protected int               batchSize      = 1;

    /**
     * Number of inserts per transaction. These will be inserted in batches
     * whose size is determined by batchSize.
     */
    private int                 insertsPerXact = 1;

    // Implementation data for scenario
    protected TableSet          tableSet;
    protected TableSetHelper    helper;
    protected Connection        conn           = null;

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public void setDataWidth(int datawidth)
    {
        this.datawidth = datawidth;
    }

    public void setDatatype(String datatype)
    {
        this.datatype = datatype;
    }

    public void setInsertsPerXact(int insertsPerXact)
    {
        this.insertsPerXact = insertsPerXact;
    }

    public void setBatchSize(int batchSize)
    {
        this.batchSize = batchSize;
    }

    public void initialize(Properties properties) throws Exception
    {
        // Define table layout.
        Column[] columns = new Column[3];
        columns[0] = new Column("mykey", Types.INTEGER, -1, -1, false, false);
        columns[1] = new Column("mythread", Types.VARCHAR, 50);
        columns[1].setIndexed(true);
        columns[2] = new Column("mypayload", Types.VARCHAR, (int) datawidth);

        // Set up helper classes.
        tableSet = new TableSet("benchmark_scenario_", 1, 0, columns);
        helper = new TableSetHelper(url, user, password);
        conn = helper.getConnection();
    }

    /** Create test tables. */
    public void globalPrepare() throws Exception
    {
        logger.info("Creating test tables...");
        helper.createAll(tableSet);
    }

    /**
     * Empty.
     */
    public void prepare() throws Exception
    {
        // Does nothing.
    }

    /** Execute an interation. */
    public void iterate(long iterationCount) throws Exception
    {
        // Prepare insert statement.
        Table tables[] = tableSet.getTables();
        StringBuffer stmtBuffer = new StringBuffer();

        // Select a table to update.
        int index = (int) (tables.length * Math.random());

        // Begin transaction.
        conn.setAutoCommit(false);
        Statement stmt = conn.createStatement();

        // Loop through writes.
        int insertCount = 0;
        for (int i = 0; i < this.insertsPerXact; i++)
        {
            // Add generate data.
            List<DataGenerator> generators = tableSet.getDataGenerators();
            Long myKey = (Long) generators.get(0).generate();
            String myThread = "bstmt_" + Thread.currentThread().getName() + "_"
                    + iterationCount;
            // String myPayload = (String) generators.get(2).generate();
            String myPayload = "123456789*";

            // Add a semi-colon if necessary to batch.
            if (insertCount > 0)
                stmtBuffer.append(";");

            // Add a SQL INSERT statement.
            stmtBuffer.append("INSERT INTO ").append(tables[index].getName())
                    .append(" VALUES(");
            stmtBuffer.append(myKey).append(", ");
            stmtBuffer.append("'").append(myThread).append("', ");
            stmtBuffer.append("'").append(myPayload).append("')");
            insertCount++;

            // See if it's time to submit.
            if (insertCount >= batchSize)
            {
                // Submit SQL.
                String sql = stmtBuffer.toString();
                stmt.executeUpdate(sql);
                // if (rows != insertCount)
                // {
                //    throw new Exception("Invalid # of rows: insertCount="
                //            + insertCount + " updatedRows=" + rows);
                //}

                // Get ready for another batch.
                insertCount = 0;
                stmtBuffer = new StringBuffer();
            }
        }

        // Submit the batched statements.
        if (insertCount > 0)
        {
            String sql = stmtBuffer.toString();
            stmt.executeUpdate(sql);
        }

        // Commit transaction.
        conn.commit();
        stmt.close();
    }

    /** Clean up resources used by scenario. */
    public void cleanup() throws Exception
    {
        // Clean up connection.
        if (conn != null)
            conn.close();
    }

    public void globalCleanup() throws Exception
    {
        // Does nothing.
    }
}