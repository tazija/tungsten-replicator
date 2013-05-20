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
import java.util.ArrayList;
import java.util.List;

public class TableGroup implements Runnable
{
    private int                       tableSize;
    private String                    tableName;

    private List<ThreadConfiguration> threads   = new ArrayList<ThreadConfiguration>();

    private String                    dataSourceName;
    private int                       smallKey;
    private int                       bigkey;
    private String                    joinedTableName;
    private String                    base1TableName;
    private String                    base2TableName;
    private RowFactory                rowFactory;
    private boolean                   initializeDDL;
    private String                    truncateTable;

    public TableGroup(String tableName, int size)
    {
        this.tableName = tableName;
        this.joinedTableName = tableName + "3";
        this.base1TableName = tableName + "1";
        this.base2TableName = tableName + "2";
        this.tableSize = size;
        this.smallKey = size - 1;
        this.bigkey = size * 1000;
    }

    public String getTableName()
    {
        return tableName;
    }

    public int getTableSize()
    {
        return tableSize;
    }

    public void run()
    {
        // TODO Auto-generated method stub

    }

    public void addThreadGroup(ThreadConfiguration tc)
    {
        threads.add(tc);
    }

    public List getThreads()
    {
        return threads;
    }

    public void setThreads(List<ThreadConfiguration> threads)
    {
        this.threads = threads;
    }

    public synchronized int getSmallKey()
    {
        return smallKey;
    }

    public synchronized int getBigkey(Connection conn)
            throws EvaluatorException
    {
        bigkey += 1000;
        if (bigkey >= tableSize * 1000)
        {
            smallKey++;
            rowFactory.addRow(this, smallKey, conn);
            bigkey = 0;
        }
        return bigkey;
    }

    public String getJoinedTableName()
    {
        return joinedTableName;
    }

    public String getBase1TableName()
    {
        return base1TableName;
    }

    public String getBase2TableName()
    {
        return base2TableName;
    }

    public int getValueRange()
    {
        return 10 * getTableSize();
    }

    public void setRowFactory(RowFactory rowFactory)
    {
        this.rowFactory = rowFactory;
    }

    public boolean isInitializeDDL()
    {
        return initializeDDL;
    }

    public void setInitializeDDL(boolean initializeDDL)
    {
        this.initializeDDL = initializeDDL;
    }

    public String getTruncateTable()
    {
        return truncateTable;
    }

    public void setTruncateTable(String truncateTable)
    {
        this.truncateTable = truncateTable;
    }

    /**
     * Returns the dataSourceName value.
     * 
     * @return Returns the dataSourceName.
     */
    public String getDataSourceName()
    {
        return dataSourceName;
    }

    /**
     * Sets the dataSourceName value.
     * 
     * @param dataSourceName The dataSourceName to set.
     */
    public void setDataSourceName(String dataSourceName)
    {
        this.dataSourceName = dataSourceName;
    }
}
