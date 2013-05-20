/**
 * Bristlecone Test Tools for Databases
 * Copyright (C) 2010 Continuent Inc.
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
 * Initial developer(s): Scott Martin
 * Contributor(s): Robert Hodges
 */

package com.continuent.bristlecone.benchmark.tpcb;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;

import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.log4j.Logger;

import com.continuent.bristlecone.benchmark.Scenario;
import com.continuent.bristlecone.benchmark.db.SqlDialect;
import com.continuent.bristlecone.benchmark.db.Table;

/**
 * This class defines a TPCBClient. It is adapted from Scott's original JMeter
 * test.
 * 
 * @author <a href="mailto:scott.martin@continuent.com">Scott Martin</a>
 * @version 1.0
 */
public class TPCBScenario implements Scenario
{
    private static final Logger logger                = Logger
                                                              .getLogger(TPCBScenario.class);
    // TPC-B scenario parameters.
    private String              url;
    private String              password;
    private String              user;
    private boolean             reusedata;
    private int                 numberOfBranches      = 10;
    private int                 tellersPerBranch      = 10;
    private int                 accountsPerBranch     = 10000;
    private boolean             updateBranch          = true;
    private boolean             updateTeller          = true;
    private boolean             updateAccount         = true;
    private boolean             insertHistory         = true;
    private int                 queryPCT              = 0;
    private int                 thinkMillis           = 0;
    private float               randomizationPct      = 0;
    private int                 connectionRefreshRate = 0;

    // Statistics.
    private TPCBStatistics      statistics;

    // Variables to control the test.
    private Configuration       configuration;
    private DatabaseConnection  connection;
    private PreparedStatement   branchUpdate;
    private PreparedStatement   tellerUpdate;
    private PreparedStatement   accountUpdate;
    private PreparedStatement   accountQuery;
    private PreparedStatement   historyInsert;
    private long                xactCount;

    // Setters for TPC-B parameters.
    public void setUrl(String url)
    {
        this.url = url;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public void setReusedata(boolean reusedata)
    {
        this.reusedata = reusedata;
    }

    public void setNumberOfBranches(int numberOfBranches)
    {
        this.numberOfBranches = numberOfBranches;
    }

    public void setTellersPerBranch(int tellersPerBranch)
    {
        this.tellersPerBranch = tellersPerBranch;
    }

    public void setAccountsPerBranch(int accountsPerBranch)
    {
        this.accountsPerBranch = accountsPerBranch;
    }

    public void setUpdateBranch(boolean updateBranch)
    {
        this.updateBranch = updateBranch;
    }

    public void setUpdateTeller(boolean updateTeller)
    {
        this.updateTeller = updateTeller;
    }

    public void setUpdateAccount(boolean updateAccount)
    {
        this.updateAccount = updateAccount;
    }

    public void setInsertHistory(boolean insertHistory)
    {
        this.insertHistory = insertHistory;
    }

    public void setQueryPCT(int queryPCT)
    {
        this.queryPCT = queryPCT;
    }

    public void setThinkMillis(int thinkMillis)
    {
        this.thinkMillis = thinkMillis;
    }

    public void setRandomizationPct(float randomizationPct)
    {
        this.randomizationPct = randomizationPct;
    }

    public void setConnectionRefreshRate(int connectionRefreshRate)
    {
        this.connectionRefreshRate = connectionRefreshRate;
    }

    // BENCHMARK API -- LISTED IN CALL ORDER.

    /**
     * Initialize variables. {@inheritDoc}
     * 
     * @see com.continuent.bristlecone.benchmark.Scenario#initialize(java.util.Properties)
     */
    public void initialize(Properties properties) throws Exception
    {
        // Get stats.
        statistics = TPCBStatistics.getInstance();

        // Login to the database.
        configuration = new Configuration(numberOfBranches, tellersPerBranch,
                accountsPerBranch);
        connection = new DatabaseConnection(url, user, password);
        connection.connect();
    }

    /**
     * Generate data (optionally) and zero out stats.
     */
    public void globalPrepare() throws Exception
    {
        // Create tables if this is the first time through.
        try
        {
            if (reusedata)
                logger.info("Reusing existing test data");
            else
                createAndPopulate();
        }
        catch (SQLException e)
        {
            logger.info("Error while creating tables : " + e);
            throw e;
        }

        statistics.initialize();
    }

    /**
     * Set up for the test. {@inheritDoc}
     * 
     * @see com.continuent.bristlecone.benchmark.Scenario#prepare()
     */
    public void prepare() throws Exception
    {
        prepareStatements();
    }

    /**
     * Execute a single TPC-B transaction. {@inheritDoc}
     * 
     * @see com.continuent.bristlecone.benchmark.Scenario#iterate(long)
     */
    public void iterate(long iterationCount) throws Exception
    {
        // If think time is enabled, wait for a [possibly random] interval.
        if (thinkMillis > 0)
        {
            int variance = (int) (thinkMillis * randomizationPct / 100.);
            long thinkTime = thinkMillis + variance;
            Thread.sleep(thinkTime);
        }

        // Execute a transaction or query.
        executeOneTransaction();
    }

    public void cleanup() throws Exception
    {
        connection.close();
    }

    public void globalCleanup() throws Exception
    {
        logger.info("Total TPCBs   = " + statistics.getTPCBCount());
        logger.info("Total Queries = " + statistics.getQueryCount());
    }

    /**
     * Ran at end of test to tear down state. {@inheritDoc}
     * 
     * @see org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient#teardownTest(org.apache.jmeter.protocol.java.sampler.JavaSamplerContext)
     */
    public void teardownTest(JavaSamplerContext context)
    {
    }

    // Create prepared statements for the run.
    private void prepareStatements()
    {
        String SQL;

        try
        {
            SQL = "update branch set branch_balance = branch_balance + ?, time_stamp = now() where branch_id = ?";
            branchUpdate = connection.prepareStatement(SQL);
            SQL = "update teller set teller_balance = teller_balance + ?, time_stamp = now() where teller_id = ?";
            tellerUpdate = connection.prepareStatement(SQL);
            SQL = "update account set account_balance = account_balance + ?, time_stamp = now() where account_id = ?";
            accountUpdate = connection.prepareStatement(SQL);
            SQL = "select account_balance from account where account_id = ?";
            accountQuery = connection.prepareStatement(SQL);
            SQL = "insert into history values(?, ?, ?, ?, now(), ?)";
            historyInsert = connection.prepareStatement(SQL);
        }
        catch (Exception e)
        {
            logger.info("Exception = " + e);
        }
    }

    /**
     * Execute one unit of work. This modified TPCB benchmark allows for a
     * certain percentage of the units of work to be a query instead of a normal
     * TPCB transaction (three updates, plus insert). The insert into the
     * history table is also optional since an object growing in an unbounded
     * way can be a nuisance when performance testing is all that is of
     * interest.
     */
    private void executeOneTransaction() throws Exception
    {
        int amount = 10;
        int branchID = 1;
        int tellerID = 1;
        int accountID = 1;
        int debitRange = 10000; /* debit plus or minus 10,000 each time */
        String filler = "0123456789";
        boolean performQuery = false;

        // select random account, then compute teller and branch associated with
        // the account.
        accountID = (int) (Math.random() * (double) (configuration
                .getNumberOfBranches() * configuration.getAccountsPerBranch()));
        tellerID = accountID * configuration.getTellersPerBranch()
                / configuration.getAccountsPerBranch();
        branchID = accountID / configuration.getAccountsPerBranch();
        amount = (int) (Math.random() * (double) (debitRange * 2)) - debitRange;

        try
        {
            // Refresh the connection if necessary.
            xactCount++;
            if (connectionRefreshRate > 0
                    && xactCount % connectionRefreshRate == 0)
            {
                connection.connect();
                prepareStatements();
            }

            // Decide whether to read or write.
            if (Math.random() * 100 <= queryPCT)
                performQuery = true;
            else
                performQuery = false;

            if (performQuery)
            {
                // logger.info("QUERY: ano = " + accountID);
                statistics.incrementQueryCount();
                accountQuery.setInt(1, accountID);
                accountQuery.execute();
            }
            else
            {
                // logger.info("TPCB: bno = " + branchID + " tno = " + tellerID
                // + " ano = " +
                // accountID + " amount = " + amount);
                statistics.incrementTPCBCount();
                branchUpdate.setInt(1, amount);
                branchUpdate.setInt(2, branchID);
                tellerUpdate.setInt(1, amount);
                tellerUpdate.setInt(2, tellerID);
                accountUpdate.setInt(1, amount);
                accountUpdate.setInt(2, accountID);
                historyInsert.setInt(1, accountID);
                historyInsert.setInt(2, tellerID);
                historyInsert.setInt(3, branchID);
                historyInsert.setInt(4, amount);
                historyInsert.setString(5, filler);

                if (updateBranch)
                    branchUpdate.execute();
                if (updateTeller)
                    tellerUpdate.execute();
                if (updateAccount)
                    accountUpdate.execute();
                if (insertHistory)
                    historyInsert.execute();
                connection.commit();
            }

        }
        catch (Exception e)
        {
            logger.info("exception during transaction " + e);
            throw e;
        }
    }

    public Configuration getConfiguration()
    {
        return configuration;
    }

    /**
     * create and populate the 4 TPCB tables (branch, teller, account, history).
     * 
     * @throws SQLException
     */
    private void createAndPopulate() throws SQLException
    {
        String filler100 = createFiller(100);

        logger.info("Creating and populating tables.");
        SqlDialect dialect = connection.getDialect();

        createTable(configuration.getAccountTable());
        createTable(configuration.getTellerTable());
        createTable(configuration.getBranchTable());
        createTable(configuration.getHistoryTable());

        /* branch table */
        /*
         * create table branch (branch_id int, branch_balance int, filler
         * varchar(100));
         */
        String insert = dialect.getInsert(configuration.getBranchTable());
        logger.info("insert into table with " + insert);
        PreparedStatement insertStatement = connection.prepareStatement(insert);
        for (int i = 0; i < configuration.getNumberOfBranches(); i++)
        {
            if (i > 0 && i % 100 == 0)
            {
                connection.commit();
                logger.info("Inserted " + i + " rows...");
            }
            insertStatement.setObject(1, i, java.sql.Types.INTEGER);
            insertStatement.setObject(2, 0, java.sql.Types.INTEGER);
            insertStatement.setObject(3, filler100, java.sql.Types.VARCHAR);
            insertStatement.setObject(4, new Timestamp(System
                    .currentTimeMillis()), java.sql.Types.TIMESTAMP);
            insertStatement.execute();
        }
        connection.commit();

        /* teller table */
        /*
         * create table teller (teller_id int, branch_id int, teller_balance
         * int, filler varchar(100));
         */
        insert = dialect.getInsert(configuration.getTellerTable());
        logger.info("insert into table with " + insert);
        insertStatement = connection.prepareStatement(insert);
        for (int i = 0; i < configuration.getNumberOfTellers(); i++)
        {
            if (i > 0 && i % 1000 == 0)
            {
                connection.commit();
                logger.info("Inserted " + i + " rows...");
            }

            insertStatement.setObject(1, i, java.sql.Types.INTEGER);
            insertStatement.setObject(2, i / 10, java.sql.Types.INTEGER);
            insertStatement.setObject(3, 0, java.sql.Types.INTEGER);
            insertStatement.setObject(4, filler100, java.sql.Types.VARCHAR);
            insertStatement.setObject(5, new Timestamp(System
                    .currentTimeMillis()), java.sql.Types.TIMESTAMP);
            insertStatement.execute();
        }
        connection.commit();

        /* account table */
        /*
         * create table account (account_id int, branch_id int, account_balance
         * int, filler varchar(100));
         */
        insert = dialect.getInsert(configuration.getAccountTable());
        logger.info("insert into table with " + insert);
        insertStatement = connection.prepareStatement(insert);
        for (int i = 0; i < configuration.getNumberOfAccounts(); i++)
        {
            if (i > 0 && i % 10000 == 0)
            {
                connection.commit();
                logger.info("Inserted " + i + " rows...");
            }

            insertStatement.setObject(1, i, java.sql.Types.INTEGER);
            insertStatement.setObject(2, i
                    / configuration.getAccountsPerBranch(),
                    java.sql.Types.INTEGER);
            insertStatement.setObject(3, 0, java.sql.Types.INTEGER);
            insertStatement.setObject(4, filler100, java.sql.Types.VARCHAR);
            insertStatement.setObject(5, new Timestamp(System
                    .currentTimeMillis()), java.sql.Types.TIMESTAMP);
            insertStatement.execute();
        }
        connection.commit();
    }

    private static String createFiller(int size)
    {
        char[] ca = new char[size];

        java.util.Arrays.fill(ca, 'X');

        return new String(ca);
    }

    private void createTable(Table t) throws SQLException
    {
        SqlDialect dialect = connection.getDialect();

        String dropTable = dialect.getDropTable(t);

        try
        {
            connection.execute(dropTable);
        }
        catch (SQLException e)
        {
            // ignore since table might not exist
        }

        String createTable = dialect.getCreateTable(t);

        System.out.println("Creating table with " + createTable);
        connection.execute(createTable);
    }

}