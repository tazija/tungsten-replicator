/**
 * Tungsten Scale-Out Stack
 * Copyright (C) 2007-2010 Continuent Inc.
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
 * Initial developer(s): Scott Martin
 * Contributor(s): 
 */

package com.continuent.bristlecone.benchmark.tpcb;

import com.continuent.bristlecone.benchmark.db.Column;
import com.continuent.bristlecone.benchmark.db.Table;

/**
 * A configuration holds the data describing a particular tpcb database layout.
 * It is not much more than a simple structure.
 * 
 * @author smartin
 */
public class Configuration
{
    private static Table accountTable;
    private static Table tellerTable;
    private static Table branchTable;
    private static Table historyTable;
    private int          numberOfBranches;
    private int          tellersPerBranch;
    private int          accountsPerBranch;

    public Configuration(int numberOfBranches, int tellersPerBranch,
            int accountsPerBranch)
    {
        this.numberOfBranches = numberOfBranches;
        this.tellersPerBranch = tellersPerBranch;
        this.accountsPerBranch = accountsPerBranch;
        createTableDefinitions();
    }

    public Configuration()
    {
        this(1, 10, 10000);
    }

    /**
     * Create the bristlecone Table definitions for the TPCB tables. The data
     * tables themselves are not created in the RDBMS - only the java
     * representation of them is. The somewhat cumbersome names of the columns
     * where taken directly from the TPCB specification found at
     * http://www.tpc.org/tpcb/spec/tpcb_current.pdf
     */
    private static void createTableDefinitions()
    {
        /* account table */
        Column aAccountID = new Column("account_id", java.sql.Types.INTEGER, 0,
                0, true, false); // primary key
        Column aBranchID = new Column("branch_id", java.sql.Types.INTEGER);
        Column aAccountBalance = new Column("account_balance",
                java.sql.Types.INTEGER);
        Column aFiller = new Column("filler", java.sql.Types.VARCHAR, 100);
        Column aTimeStamp = new Column("time_stamp", java.sql.Types.TIMESTAMP);
        Column[] accountColumns = {aAccountID, aBranchID, aAccountBalance,
                aFiller, aTimeStamp};
        accountTable = new Table("account", accountColumns);

        /* teller table */
        Column tTellerID = new Column("teller_id", java.sql.Types.INTEGER, 0,
                0, true, false); // primary key
        Column tBranchID = new Column("branch_id", java.sql.Types.INTEGER);
        Column tTellerBalance = new Column("teller_balance",
                java.sql.Types.INTEGER);
        Column tFiller = new Column("filler", java.sql.Types.VARCHAR, 100);
        Column tTimeStamp = new Column("time_stamp", java.sql.Types.TIMESTAMP);
        Column[] tellerColumns = {tTellerID, tBranchID, tTellerBalance,
                tFiller, tTimeStamp};
        tellerTable = new Table("teller", tellerColumns);

        /* branch table */
        Column bBranchID = new Column("branch_id", java.sql.Types.INTEGER, 0,
                0, true, false); // primary key
        Column bBranchBalance = new Column("branch_balance",
                java.sql.Types.INTEGER);
        Column bFiller = new Column("filler", java.sql.Types.VARCHAR, 100);
        Column bTimeStamp = new Column("time_stamp", java.sql.Types.TIMESTAMP);
        Column[] branchColumns = {bBranchID, bBranchBalance, bFiller,
                bTimeStamp};
        branchTable = new Table("branch", branchColumns);

        /* history table */
        Column hAccountID = new Column("account_id", java.sql.Types.INTEGER);
        Column hTellerID = new Column("teller_id", java.sql.Types.INTEGER);
        Column hBranchID = new Column("branch_id", java.sql.Types.INTEGER);
        Column hAmount = new Column("amount", java.sql.Types.INTEGER);
        Column hTimeStamp = new Column("time_stamp", java.sql.Types.TIMESTAMP);
        Column hFiller = new Column("filler", java.sql.Types.VARCHAR, 50);
        Column[] historyColumns = {hAccountID, hTellerID, hBranchID, hAmount,
                hTimeStamp, hFiller};
        historyTable = new Table("history", historyColumns);
    }

    public Table getAccountTable()
    {
        return accountTable;
    }

    public Table getTellerTable()
    {
        return tellerTable;
    }

    public Table getBranchTable()
    {
        return branchTable;
    }

    public Table getHistoryTable()
    {
        return historyTable;
    }

    public int getNumberOfBranches()
    {
        return numberOfBranches;
    }

    public int getAccountsPerBranch()
    {
        return accountsPerBranch;
    }

    public int getTellersPerBranch()
    {
        return tellersPerBranch;
    }

    public int getNumberOfTellers()
    {
        return numberOfBranches * tellersPerBranch;
    }

    public int getNumberOfAccounts()
    {
        return numberOfBranches * accountsPerBranch;
    }
}
