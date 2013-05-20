/**
 * Bristlecone Test Tools for Databases
 * Copyright (C) 2006-2010 Continuent Inc.
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
 * Initial developer(s): Scott Martin.
 * Contributor(s):
 */

package com.continuent.bristlecone.benchmark.tpcb;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.io.Serializable;
import java.util.Iterator;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.log.Logger;

import com.continuent.bristlecone.benchmark.db.SqlDialect;
import com.continuent.bristlecone.benchmark.db.Table;


/**
 * 
 * This class defines a TPCBClient
 * 
 * This class implements the three primary functions needed by the JMeter load generator
 * 
 *  setupTest
 *  runTest
 *  teardownTest
 *  getDefaultParameters
 *  
 *  This class implements the TPCB benchmark to be driven from JMeter.
 *  How to use this class within JMeter is better described in the associated README.
 * 
 * @author <a href="mailto:scott.martin@continuent.com">Scott Martin</a>
 * @version 1.0
 */
public class TPCBClient 
    extends AbstractJavaSamplerClient 
    implements Serializable
{
    private static final long serialVersionUID = -27L;
    private String databaseURL = "jdbc:mysql://happy:3306/tpcb?user=realuser&password=realpass";
    private int numberOfTPCBs = 0;
    private int numberOfQueries = 0;
    private boolean updateBranch = true;
    private boolean updateTeller = true;
    private boolean updateAccount = true;
    private boolean insertHistory = true;
    private Configuration configuration;
    private int queryPCT = 0;
    private DatabaseConnection connection;
    private PreparedStatement branchUpdate;
    private PreparedStatement tellerUpdate;
    private PreparedStatement accountUpdate;
    private PreparedStatement accountQuery;
    private PreparedStatement historyInsert;
    private Logger logger;
    private boolean createTables = false;
    
    public TPCBClient()
    {
        logger = getLogger();
    }
    
    /**
     * The is the routine called "once per transaction".  Or "once per unit of work".
     * {@inheritDoc}
     * @see org.apache.jmeter.protocol.java.sampler.JavaSamplerClient#runTest(org.apache.jmeter.protocol.java.sampler.JavaSamplerContext)
     */
    public SampleResult runTest(JavaSamplerContext context)
    {
        SampleResult result = new SampleResult();
        
        /* Gather sample start and end times for Jmeter to consume */
        result.sampleStart();
        try
        {
            executeOneTransaction();
        }
        catch (Exception e)
        {
            // One can also re-throw the exception.  JMeter can handle errors.
        }
        result.sampleEnd();
        return result;
        
    }
    
    /**
     * Ran once by JMeter to setup the test before the run.
     * {@inheritDoc}
     * @see org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient#setupTest(org.apache.jmeter.protocol.java.sampler.JavaSamplerContext)
     */
    public void setupTest(JavaSamplerContext context)
    {
        int numberOfBranches = 10;
        int tellersPerBranch = 10;
        int accountsPerBranch = 10000;

        Iterator<?> i = context.getParameterNamesIterator();
        
        for (; i.hasNext();)
        {
            String arg = (String)i.next();  
            String value = context.getParameter(arg);

            //logger.info(arg + " = " + value);
            
            if (arg.equals("databaseURL")) {databaseURL = value; continue;}
            if (arg.equals("numberOfBranches")) {numberOfBranches = Integer.parseInt(value); continue;}
            if (arg.equals("tellersPerBranch")) {tellersPerBranch = Integer.parseInt(value); continue;}
            if (arg.equals("accountsPerBranch")) {accountsPerBranch = Integer.parseInt(value); continue;}
            if (arg.equals("updateBranch")) {updateBranch = Boolean.parseBoolean(value); continue;}
            if (arg.equals("updateTeller")) {updateTeller = Boolean.parseBoolean(value); continue;}
            if (arg.equals("updateAccount")) {updateAccount = Boolean.parseBoolean(value); continue;}
            if (arg.equals("insertHistory")) {insertHistory = Boolean.parseBoolean(value); continue;}
            if (arg.equals("createTables")) {createTables = Boolean.parseBoolean(value); continue;}
            if (arg.equals("queryPCT")) {queryPCT = Integer.parseInt(value); continue;}

            //logger.info("Warning: Unrecognized parameter = " + arg);

        }
        
        configuration = new Configuration(numberOfBranches, tellersPerBranch, accountsPerBranch);      
        connection = new DatabaseConnection(databaseURL);
        connection.connect();
        
        try 
        {
            if (createTables) createAndPopulate();
        }
        catch (SQLException e)
        {
            logger.info("Error while creating tables : " + e);
            return;
        }
        
        prepareStatements();        
    }
    
    /**
     * Ran at end of test to tear down state.
     * {@inheritDoc}
     * @see org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient#teardownTest(org.apache.jmeter.protocol.java.sampler.JavaSamplerContext)
     */
    public void teardownTest(JavaSamplerContext context)
    {
        logger.info("TPCBs   = " + numberOfTPCBs);   
        logger.info("Queries = " + numberOfQueries);  
        connection.close();
    }
    
    /**
     * Called by the JMeter GUI to populate the fields.
     * {@inheritDoc}
     * @see org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient#getDefaultParameters()
     */
    public Arguments getDefaultParameters() 
    {
        Arguments args = new Arguments();
        args.addArgument("databaseURL", "jdbc:mysql://localhost:3306/tpcb?user=realuser&password=realpass");
        args.addArgument("numberOfBranches", "10");
        args.addArgument("tellersPerBranch", "10");
        args.addArgument("accountsPerBranch", "10000");
        args.addArgument("updateBranch", "true");
        args.addArgument("updateTeller", "true");
        args.addArgument("updateAccount", "true");
        args.addArgument("insertHistory", "true");
        args.addArgument("createTables", "false");
        args.addArgument("queryPCT", "0");
        return args;      
    }
        
    private void prepareStatements()
    {
        String SQL;
        
        try 
        {
            SQL = "update branch set branch_balance = branch_balance + ? where branch_id = ?";
            branchUpdate = connection.prepareStatement(SQL);
            SQL = "update teller set teller_balance = teller_balance + ? where teller_id = ?";
            tellerUpdate = connection.prepareStatement(SQL);
            SQL = "update account set account_balance = account_balance + ? where account_id = ?";
            accountUpdate = connection.prepareStatement(SQL);
            SQL = "select account_balance from account where account_id = ?";
            accountQuery = connection.prepareStatement(SQL);
            SQL = "insert into history values(?, ?, ?, ?, now(), ?)";
            historyInsert = connection.prepareStatement(SQL);
        }
        catch (Exception e)
        {
            getLogger().info("Exception = " + e);
        }
    }
    
    /**
     * 
     * Execute one unit of work.  This modified TPCB benchmark allows for a certain percentage of the
     * units of work to be a query instead of a normal TPCB transaction (three updates, plus insert).
     * The insert into the history table is also optional since an object growing in an unbounded way
     * can be a nuisance when performance testing is all that is of interest.
     *
     */
    private void executeOneTransaction()
    {
        int amount = 10;
        int branchID = 1;
        int tellerID = 1;
        int accountID = 1;
        int debitRange = 10000; /* debit plus or minus 10,000 each time */
        String filler = "0123456789";
        boolean performQuery = false;
        
        // select random account, then compute teller and branch associated with the account.
        accountID = (int)(Math.random() * (double)(configuration.getNumberOfBranches() * 
                configuration.getAccountsPerBranch()));
        tellerID = accountID * configuration.getTellersPerBranch() / configuration.getAccountsPerBranch();
        branchID = accountID / configuration.getAccountsPerBranch();
        amount = (int)(Math.random() * (double)(debitRange * 2)) - debitRange;
                   
        try {
            if (Math.random()  * 100 <= queryPCT) performQuery = true;
            else performQuery = false;
            
            if (performQuery)
            {
                //logger.info("QUERY: ano = " + accountID);
                numberOfQueries++;
                accountQuery.setInt(1, accountID);
                accountQuery.execute();                
            } else {
                //logger.info("TPCB: bno = " + branchID + " tno = " + tellerID + " ano = " + 
                //        accountID + " amount = " + amount);
                numberOfTPCBs++;
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
                
                if (updateBranch) branchUpdate.execute();
                if (updateTeller) tellerUpdate.execute();
                if (updateAccount) accountUpdate.execute();
                if (insertHistory) historyInsert.execute();
                connection.commit();
            }
            
        }
        catch (Exception e)
        {
            logger.info("exception during transaction " + e);
        }
        
    }
    
    public Configuration getConfiguration()
    {
        return configuration;
    }
    
    /**
     * 
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
        /* create table branch (branch_id int, branch_balance int, filler varchar(100)); */
        String insert = dialect.getInsert(configuration.getBranchTable());
        logger.info("insert into table with " + insert);     
        PreparedStatement insertStatement = connection.prepareStatement(insert);    
        for (int i = 0; i < configuration.getNumberOfBranches(); i++)
        {
            insertStatement.setObject(1, i, java.sql.Types.INTEGER);
            insertStatement.setObject(2, 0, java.sql.Types.INTEGER);
            insertStatement.setObject(3, filler100, java.sql.Types.VARCHAR); 
            insertStatement.execute();
        }
        
        /* teller table */
        /* create table teller (teller_id int, branch_id int, teller_balance int, filler varchar(100)); */
        insert = dialect.getInsert(configuration.getTellerTable());
        logger.info("insert into table with " + insert);     
        insertStatement = connection.prepareStatement(insert);  
        for (int i = 0; i < configuration.getNumberOfTellers(); i++)
        {
            insertStatement.setObject(1, i, java.sql.Types.INTEGER);
            insertStatement.setObject(2, i / 10, java.sql.Types.INTEGER);
            insertStatement.setObject(3, 0, java.sql.Types.INTEGER);
            insertStatement.setObject(4, filler100, java.sql.Types.VARCHAR); 
            insertStatement.execute();
        }
        
        /* account table */
        /* create table account (account_id int, branch_id int, account_balance int, filler varchar(100)); */
        insert = dialect.getInsert(configuration.getAccountTable());
        logger.info("insert into table with " + insert);     
        insertStatement = connection.prepareStatement(insert);  
        for (int i = 0; i < configuration.getNumberOfAccounts(); i++)
        {
            insertStatement.setObject(1, i, java.sql.Types.INTEGER);
            insertStatement.setObject(2, i / configuration.getAccountsPerBranch(), java.sql.Types.INTEGER);
            insertStatement.setObject(3, 0, java.sql.Types.INTEGER);
            insertStatement.setObject(4, filler100, java.sql.Types.VARCHAR); 
            insertStatement.execute();
        }
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
