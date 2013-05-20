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

package com.continuent.bristlecone.evaluator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Random;

import org.apache.log4j.Logger;

public class EvaluatorThread extends Thread
{
    private static Logger    logger          = Logger
                                                     .getLogger(EvaluatorThread.class);
    protected Evaluator      eval;

    private int              count;
    private int              rowsRead;
    private int              updates;
    private int              inserts;
    private int              deletes;

    private String           id;
    Connection               conn;
    ThreadConfiguration      conf;
    PreparedStatement        select          = null;
    PreparedStatement        delete          = null;
    PreparedStatement        update          = null;
    PreparedStatement        insert          = null;
    PreparedStatement        current         = null;

    private Random           rand;

    private int              sleepBeforeStart;

    private boolean          started;

    private int              deleted;

    private int              updated;

    private static final int FOREVER = -1;
    private static final int MAX_RETRY_COUNT = FOREVER;
    private static final int SECONDS_BETWEEN_RETRY = 10;

    public EvaluatorThread(Evaluator eval, ThreadConfiguration threadGroup,
            String id) throws EvaluatorException
    {
        setName(id);
        this.eval = eval;
        this.id = id;
        this.conf = threadGroup;
        connect();
    }

    private void reconnect() throws EvaluatorException
    {
        this.select = null;

        try
        {
            conn.close();
        }
        catch (SQLException e)
        {
            // ignore
        }

        connect();

    }

    private String makeSelect(String low, String high, String low2, String high2)
    {
        String joinedTable = conf.getJoinedTableName();
        String base1 = conf.getBase1TableName();
        String base2 = conf.getBase2TableName();

        return "select c.* from " + joinedTable + " c join " + base1
                + " a on c.k1 = a.k1 join " + base2
                + " b on c.k2 = b.k2 where a.value between " + low + " and "
                + high + " and b.value between " + low2 + " and " + high2;

    }

    private void addSemanticInfo(Connection conn, String procName, String table)
            throws SQLException
    {
        PreparedStatement s = conn
                .prepareStatement("delete from sequoiaSABase where objectName = ?");
        s.setString(1, procName);
        s.executeUpdate();
        s = conn
                .prepareStatement("delete from sequoiaSAReferences where baseObjectName = ?");
        s.setString(1, procName);
        s.executeUpdate();
        s = conn
                .prepareStatement("insert into sequoiaSABase values(?, 1, ?, 0, ?, ?, ?, ?, 0, 0, 0, 0, ?, ?)");
        boolean isUpdate = procName.startsWith("update");
        boolean isDelete = procName.startsWith("delete");
        boolean isInsert = procName.startsWith("insert");
        boolean isSelect = !(isUpdate | isDelete | isInsert);
        boolean causallyDependent = !isSelect;
        boolean commutative = isSelect;
        s.setString(1, procName);
        s.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
        s.setBoolean(3, isSelect);
        s.setBoolean(4, isInsert);
        s.setBoolean(5, isUpdate);
        s.setBoolean(6, isDelete);
        s.setBoolean(7, causallyDependent);
        s.setBoolean(8, commutative);
        s.executeUpdate();

        if (!isSelect)
        {
            s = conn
                    .prepareStatement("insert into sequoiaSAReferences values(?, ?, 2, ?, ?, ?, ?, 0, 0, 0)");
            s.setString(1, procName);
            s.setString(2, table);
            s.setBoolean(3, isSelect);
            s.setBoolean(4, isInsert);
            s.setBoolean(5, isUpdate);
            s.setBoolean(6, isDelete);
            s.executeUpdate();
        }
    }

    private void createProcs(Connection conn) throws SQLException
    {
        synchronized (conf)
        {
            Statement s = conn.createStatement();
            String selectProc = "get_" + conf.getJoinedTableName();
            String deleteProc = "delete_" + conf.getJoinedTableName();
            String insertProc = "insert_" + conf.getJoinedTableName();
            String updateProc = "update_" + conf.getJoinedTableName();
            if (!conf.isProcsCreated()
                    && conf.getQueryFormat().equals("sybaseProcedure"))
            {
                if (conf.getTableGroup().isInitializeDDL()
                        || !eval.isTableAvailable(conn, "sequoiaSABase"))
                {
                    String createSABase = "CREATE TABLE dbo.sequoiaSABase"
                            + "("
                            + "    objectName              varchar(255) NOT NULL,"
                            + "    objectType              int          NOT NULL,"
                            + "    lastUpdate              datetime     NOT NULL,"
                            + "    paramCount              int          DEFAULT 0 NOT NULL,"
                            + "    hasSelect               bit          DEFAULT 0 NOT NULL,"
                            + "    hasInsert               bit          DEFAULT 0 NOT NULL,"
                            + "    hasUpdate               bit          DEFAULT 0 NOT NULL,"
                            + "    hasDelete               bit          DEFAULT 0 NOT NULL,"
                            + "    hasReplace              bit          DEFAULT 0 NOT NULL,"
                            + "    hasDDLWrite             bit          DEFAULT 0 NOT NULL,"
                            + "    hasTransaction          bit          DEFAULT 0 NOT NULL,"
                            + "    hasUniqueWriteReference bit          DEFAULT 0 NOT NULL,"
                            + "    isCausallyDependent     bit          DEFAULT 0 NOT NULL,"
                            + "    isCommutative           bit          DEFAULT 0 NOT NULL,"
                            + "    CONSTRAINT PK_SequoiaSybase"
                            + "   PRIMARY KEY CLUSTERED (objectName)" + ")";
                    eval.executeSQLIgnoreErrors(conn, createSABase);
                }

                if (conf.getTableGroup().isInitializeDDL()
                        || !eval.isTableAvailable(conn, "sequoiaSAReferences"))
                {
                    String createSARef = "CREATE TABLE dbo.sequoiaSAReferences"
                            + "("
                            + "    baseObjectName       varchar(255) NOT NULL,"
                            + "    objectName           varchar(255) NOT NULL,"
                            + "    objectType           int          NOT NULL,"
                            + "    referencedInSelect   bit          DEFAULT 0 NOT NULL,"
                            + "    referencedInInsert   bit          DEFAULT 0 NOT NULL,"
                            + "    referencedInUpdate   bit          DEFAULT 0 NOT NULL,"
                            + "    referencedInDelete   bit          DEFAULT 0 NOT NULL,"
                            + "    referencedInReplace  bit          DEFAULT 0 NOT NULL,"
                            + "    referencedInDDLWrite bit          DEFAULT 0 NOT NULL,"
                            + "    referencedInDDLRead  bit          DEFAULT 0 NOT NULL,"
                            + "    CONSTRAINT PK_SequoiaSybase"
                            + "    PRIMARY KEY CLUSTERED (baseObjectName,objectName)"
                            + " )" + "LOCK ALLPAGES";
                    eval.executeSQLIgnoreErrors(conn, createSARef);
                }

                String proc;

                if (conf.getTableGroup().isInitializeDDL()
                        || !eval.isStoredProcedureAvailable(conn, selectProc))
                {
                    proc = "create procedure " + selectProc
                            + " (@a int, @b int, @c int, @d int) as begin ";
                    proc += makeSelect("@a", "@b", "@c", "@d");
                    proc += " end";
                    logger.debug(proc);
                    eval.executeSQLIgnoreErrors(conn, "drop procedure "
                            + selectProc);

                    addSemanticInfo(conn, selectProc, conf.getJoinedTableName());
                    s.execute(proc);
                }

                if (conf.getTableGroup().isInitializeDDL()
                        || !eval.isStoredProcedureAvailable(conn, deleteProc))
                {
                    eval.executeSQLIgnoreErrors(conn, "drop procedure "
                            + deleteProc);
                    proc = "create procedure " + deleteProc
                            + " (@a int, @b int ) as begin delete from "
                            + conf.getJoinedTableName()
                            + " where k1 = @a and k2 = @b end";
                    logger.debug(proc);

                    addSemanticInfo(conn, deleteProc, conf.getJoinedTableName());
                    s.execute(proc);
                }

                if (conf.getTableGroup().isInitializeDDL()
                        || !eval.isStoredProcedureAvailable(conn, insertProc))
                {
                    eval.executeSQLIgnoreErrors(conn, "drop procedure "
                            + insertProc);
                    proc = "create procedure "
                            + insertProc
                            + " (@a int, @b int, @c datetime, @d datetime, @e int) as begin "
                            + " insert into "
                            + conf.getJoinedTableName()
                            + " (k1, k2, changed, created, value) values (@a, @b, @c, @d, @e) end";
                    logger.debug(proc);
                    addSemanticInfo(conn, insertProc, conf.getJoinedTableName());
                    s.execute(proc);
                }

                if (conf.getTableGroup().isInitializeDDL()
                        || !eval.isStoredProcedureAvailable(conn, updateProc))
                {
                    addSemanticInfo(conn, updateProc, conf.getJoinedTableName());
                    eval.executeSQLIgnoreErrors(conn, "drop procedure "
                            + updateProc);
                    proc = "create procedure "
                            + updateProc
                            + " (@a int, @b datetime, @c int, @d int)  as begin "
                            + " update "
                            + conf.getJoinedTableName()
                            + " set value = @a, changed = @b where k1 = @c and k2 = @d end";
                    logger.debug(proc);
                    s.execute(proc);
                }

                s.close();
            }
            select = conn.prepareCall("{call " + selectProc + "(?, ?, ?, ?)}");
            delete = conn.prepareCall("{call " + deleteProc + "(?, ?)}");
            update = conn.prepareCall("{call " + updateProc + "(?, ?, ?, ?)}");
            insert = conn.prepareCall("{call " + insertProc
                    + "(?, ?, ?, ?, ?)}");
            conf.setProcsCreated(true);
        }

    }

    private void connect() throws EvaluatorException
    {

        logger.debug("thread id=" + id + ", connecting to dataSource="
                + conf.getDataSource());

        int retryCount = 0;

        /*
         * We'll try to get a connection up to MAX_RETRY times,
         * with a small sleep in between.
         */
        do
        {
            try
            {
                conn = eval.getConnection(eval.getConfiguration()
                        .getDataSource(conf.getDataSource()));
                break;
            }
            catch (EvaluatorException e)
            {
                 _sleep(SECONDS_BETWEEN_RETRY * 1000);
                 logger.info(String.format("THREAD %s: Connect retry %d", id, retryCount + 1));
            }
        }
        while (retryCount++ < MAX_RETRY_COUNT || MAX_RETRY_COUNT == FOREVER);

        try
        {
            String joinedTable = conf.getJoinedTableName();
            String base1 = conf.getBase1TableName();
            String base2 = conf.getBase2TableName();
            if (!conf.getQueryFormat().equals("sql"))
            {
                createProcs(conn);
            }
            else
            {
                select = conn
                        .prepareStatement("select c.* from "
                                + joinedTable
                                + " c join "
                                + base1
                                + " a on c.k1 = a.k1 join "
                                + base2
                                + " b on c.k2 = b.k2 where a.value between ? and ? and b.value between ? and ?");

                delete = conn.prepareStatement("delete from " + joinedTable
                        + " where k1 = ? and k2 = ?");

                update = conn
                        .prepareStatement("update "
                                + joinedTable
                                + " set value = ?, changed = ? where k1 = ? and k2 = ?");
                insert = conn.prepareStatement("insert into " + joinedTable
                        + " values (?, ?, ?, ?, ?)");

                // select.setCursorName("cursorName");
            }

        }
        catch (SQLException e)
        {
            throw new EvaluatorException("Could not create SQL statements", e);
        }
    }

    public void run()
    {
        while (!eval.isReadyToStart())
        {
            try
            {
                sleep(1000);
            }
            catch (InterruptedException e)
            {

            }
        }
        if (getSleepBeforeStart() > 0)
        {
            try
            {
                sleep(getSleepBeforeStart());
            }
            catch (InterruptedException e)
            {
            }
        }
        conf.threadStarted();
        rand = new Random(this.id.hashCode());
        long startTime = System.currentTimeMillis();
        long reconnectInterval = conf.getReconnectInterval() * 1000;

        while (!eval.isReadyToStop())
        {
            try
            {
                if (reconnectInterval > 0
                        && System.currentTimeMillis() - startTime > reconnectInterval)
                {
                    startTime = System.currentTimeMillis();
                    reconnect();
                }
                if (conf.getThinkTime() > 0)
                {
                    sleep(rand.nextInt(conf.getThinkTime() * 2));
                }
                runABatch(rand.nextInt(conf.getValueRange()));
                count++;
            }
            catch (EvaluatorException e)
            {
                eval.addFailure(e);
                logger.error(e);
                try
                {
                    reconnect();
                }
                catch (EvaluatorException e1)
                {
                    logger.error(e1);
                    break;
                }
            }
            catch (InterruptedException e)
            {
            }
        }
        if (conn != null)
        {
            try
            {
                conn.close();
            }
            catch (SQLException e)
            {
            }
        }
        logger
                .debug(id + ": " + count + " queries, " + rowsRead
                        + " rows read,  " + updates + "/" + updated
                        + " updates, " + deletes + "/" + deleted + " deletes, "
                        + inserts + " inserts");
    }

    private boolean runABatch(int value) throws EvaluatorException
    {
        /*
         * Do half the selects before the updates and half after the updates.
         */
        int doSelectFirst = rand.nextInt(1);
        try
        {
            if (doSelectFirst == 1)
                doSelect(value);
            int smallKey = -1;
            if (conf.isInsertRequired(rand))
            {
                current = insert;

                int k2;
                synchronized (conf.getTableGroup())
                {
                    k2 = conf.getBigKey(conn);
                    smallKey = conf.getSmallKey();
                }
                int k1 = smallKey;
                insert.setInt(1, k1);
                insert.setInt(2, k2);
                Timestamp now = new Timestamp(System.currentTimeMillis());
                insert.setTimestamp(3, now);
                insert.setTimestamp(4, now);
                insert.setInt(5, rand.nextInt(1000));
                processResults(insert);

                inserts++;
                // logger.debug("Row inserted " + k1 + "." + k2);
            }

            if (conf.isUpdateRequired(rand))
            {
                current = update;
                Timestamp now = new Timestamp(System.currentTimeMillis());
                update.setInt(1, rand.nextInt(conf.getValueRange()));
                update.setTimestamp(2, now);
                if (smallKey < 0)
                    smallKey = conf.getSmallKey();
                int k1 = rand.nextInt(smallKey);
                int k2 = rand.nextInt(conf.getTableGroup().getTableSize()) * 1000;
                update.setInt(3, k1);
                update.setInt(4, k2);
                processResults(update);
                updates++;
            }
            if (conf.isDeleteRequired(rand))
            {
                current = delete;
                if (smallKey < 0)
                    smallKey = conf.getSmallKey();
                int k1 = rand.nextInt(smallKey);
                int k2 = rand.nextInt(conf.getTableGroup().getTableSize()) * 1000;
                delete.setInt(1, k1);
                delete.setInt(2, k2);
                processResults(delete);
                deletes++;
            }

            if (doSelectFirst == 0)
                doSelect(value);

            if (!conn.getAutoCommit())
            {
                conn.commit();
            }
        }
        catch (SQLException e)
        {
            throw new EvaluatorException("test batch failed", e);
        }
        catch (Throwable t)
        {
            throw new EvaluatorException("Unexpected test batch failed:" + t, t);
        }
        finally
        {
            current = null;
        }
        return true;
    }

    private void doSelect(int value) throws SQLException
    {
        int readSize = conf.getReadRange();
        select.setInt(1, value);
        select.setInt(2, value + readSize);
        select.setInt(3, value);
        select.setInt(4, value + readSize);

        current = select;
        long queryStart = System.currentTimeMillis();
        int i = processResults(select);
        rowsRead += i;
        conf.addRowsRead(i, System.currentTimeMillis() - queryStart);

    }

    private int processResults(PreparedStatement s) throws SQLException
    {
        int result = 0;
        boolean hasResultSet = s.execute();
        boolean done = false;
        while (!done)
        {
            if (hasResultSet)
            {
                ResultSet rs = s.getResultSet();
                while (rs.next())
                {
                    Object obj = rs.getObject(1);
                    if (false)
                    {
                        logger.debug("Retrieved: " + obj);
                    }
                    result++;
                }
            }
            else
            {
                int updateCount = s.getUpdateCount();
                done = updateCount == -1;
            }
            hasResultSet = s.getMoreResults();
        }
        return result;
    }

    public PreparedStatement getCurrent()
    {
        return current;
    }

    public void setSleepBeforeStart(int i)
    {
        this.sleepBeforeStart = i;
    }

    public int getSleepBeforeStart()
    {
        return sleepBeforeStart;
    }

    public boolean isStarted()
    {
        return started;
    }
    
    private void _sleep(int milliseconds)
    {
        try
        {
            Thread.sleep(milliseconds);
        }
        catch(InterruptedException i)
        {
            //
        }
    }

}
