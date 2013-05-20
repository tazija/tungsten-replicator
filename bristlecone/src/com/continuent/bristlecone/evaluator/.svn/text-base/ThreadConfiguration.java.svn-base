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
import java.util.Random;

public class ThreadConfiguration
{
    int                deletePercentage;
    int                updatePercentage;
    int                insertPercentage;
    int                count;

    /**
     * Collects execution statistics for this group. Every method that
     * manipulates this structure must be synchronized.
     */
    private Statistics stats      = null;

    private String     dataSource = null;
    private String     name;
    private int        readSize;
    private TableGroup tableGroup;
    private int        thinkTime;
    private int        rampUpInterval;
    private int        rampUpIncrement;
    private int        reconnectInterval;
    private String     queryFormat;
    private boolean    procsCreated;

    public ThreadConfiguration(TableGroup tableGroup)
    {
        this.tableGroup = tableGroup;
        stats = new Statistics();
    }

    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }

    public int getDeletePercentage()
    {
        return deletePercentage;
    }

    public void setDeletePercentage(int deletePercentage)
    {
        this.deletePercentage = deletePercentage;
    }

    public int getInsertPercentage()
    {
        return insertPercentage;
    }

    public void setInsertPercentage(int insertPercentage)
    {
        this.insertPercentage = insertPercentage;
    }

    public int getUpdatePercentage()
    {
        return updatePercentage;
    }

    public void setUpdatePercentage(int updatePercentage)
    {
        this.updatePercentage = updatePercentage;
    }

    public synchronized boolean isDeleteRequired(Random rand)
    {
        boolean ret = deletePercentage > rand.nextInt(100);

        if (ret)
        {
            stats.addDelete();
        }
        return ret;
    }

    public synchronized boolean isUpdateRequired(Random rand)
    {
        boolean ret = updatePercentage > rand.nextInt(100);
        if (ret)
        {
            stats.addUpdate();
        }

        return ret;
    }

    public synchronized boolean isInsertRequired(Random rand)
    {
        boolean ret = insertPercentage > rand.nextInt(100);
        if (ret)
        {
            stats.addInsert();
        }

        return ret;
    }

    public synchronized void addRowsRead(int rowsRead, long duration)
    {
        stats.addRowsRead(rowsRead);
        stats.addResponseTime(duration);
        stats.addQuery();
    }

    public synchronized Statistics getStatistics()
    {
        return stats;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setReadSize(int i)
    {
        this.readSize = i;
    }

    public int getReadSize()
    {
        return readSize;
    }

    public int getSmallKey()
    {
        return tableGroup.getSmallKey();
    }

    public int getBigKey(Connection conn) throws EvaluatorException
    {
        return tableGroup.getBigkey(conn);
    }

    public String getBase1TableName()
    {
        return tableGroup.getBase1TableName();
    }

    public String getBase2TableName()
    {
        return tableGroup.getBase2TableName();
    }

    public String getJoinedTableName()
    {
        return tableGroup.getJoinedTableName();
    }

    public int getThinkTime()
    {
        return thinkTime;
    }

    public void setThinkTime(int thinkTime)
    {
        this.thinkTime = thinkTime;
    }

    public int getValueRange()
    {
        return tableGroup.getValueRange();
    }

    public int getReadRange()
    {
        return (int) (Math.floor(Math.sqrt(readSize) * 10));
    }

    public int getRampUpInterval()
    {
        return rampUpInterval;
    }

    public void setRampUpInterval(int rampUpInterval)
    {
        this.rampUpInterval = rampUpInterval;
    }

    public int getRampUpIncrement()
    {
        return rampUpIncrement;
    }

    public void setRampUpIncrement(int rampUpIncrement)
    {
        this.rampUpIncrement = rampUpIncrement;
    }

    public synchronized void threadStarted()
    {
        stats.addThread();
    }

    public int getReconnectInterval()
    {
        return reconnectInterval;
    }

    public void setReconnectInterval(int reconnectInterval)
    {
        this.reconnectInterval = reconnectInterval;
    }

    public String getQueryFormat()
    {
        return queryFormat;
    }

    public void setQueryFormat(String queryFormat)
    {
        this.queryFormat = queryFormat;
    }

    public boolean isProcsCreated()
    {
        return procsCreated;
    }

    public void setProcsCreated(boolean procsCreated)
    {
        this.procsCreated = procsCreated;
    }

    public TableGroup getTableGroup()
    {
        return tableGroup;
    }

    /**
     * Returns the dataSource value.
     * 
     * @return Returns the dataSource.
     */
    public String getDataSource()
    {
        return dataSource;
    }

    /**
     * Sets the dataSource value.
     * 
     * @param dataSource The dataSource to set.
     */
    public void setDataSource(String dataSource)
    {
        this.dataSource = dataSource;
    }

    /**
     * Sets the tableGroup value.
     * 
     * @param tableGroup The tableGroup to set.
     */
    public void setTableGroup(TableGroup tableGroup)
    {
        this.tableGroup = tableGroup;
    }

}
