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

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.continuent.bristlecone.benchmark.db.SqlDialect;
import com.continuent.bristlecone.benchmark.db.Table;

/**
 * Perform updates on a table in a manner designed to provoke deadlocks. 
 * This scenario performance updates on arbitrary table rows 
 * 
 * @author rhodges
 */
public class DeadlockScenario extends ScenarioBase
{
  private static Logger logger = Logger.getLogger(DeadlockScenario.class);
  
  private int operations = 1;
  private long delaymillis = 0; 
  private boolean autocommit = false;

  private String tag;
  private int execCount;
  protected PreparedStatement[] pstmtArray;

  /** 
   * Set the number of operations per transaction.  Must be at least
   * 2 to trigger deadlocks. 
   */
  public void setOperations(int operations)
  {
    this.operations = operations;
  }
  
  /**
   * Set the number of milliseconds to delay between transactions.  0 
   * means no delay.  
   */
  public void setDelaymillis(long delaymillis)
  {
    this.delaymillis = delaymillis;
  }
  
  /** Determine whether to use autocommit or actual transactions. */
  public void setAutocommit(boolean autocommit)
  {
    this.autocommit = autocommit;
  }
  
  /** Create a prepared statement array. */
  public void prepare() throws Exception
  {
    // Tag for updating records. 
    tag = Thread.currentThread().getName();

    SqlDialect dialect = helper.getSqlDialect(); 
    Table tables[] = tableSet.getTables();
    pstmtArray = new PreparedStatement[tables.length];
    for (int i = 0; i < tables.length; i++)
    {
      String sql = dialect.getUpdateByKey(tables[i]);
      pstmtArray[i] = conn.prepareStatement(sql);
    }
    
    if (logger.isDebugEnabled())
    {
      logger.debug("Tag for this scenario: " + tag);
      logger.debug("Operations: " + operations);
      logger.debug("Delay millis: " + delaymillis);
      logger.debug("Autocommit setting:" + autocommit);
    }
  }

  /** Execute an interation. */
  public void iterate(long iterationCount) throws Exception
  {
    // Ensure we have the proper autocommit setting. 
    conn.setAutoCommit(autocommit);
    
    // This code is likely to deadlock, so we need to ensure we catch 
    // SQLExceptions properly and record them nicely.  
    try
    {
      // Loop through and perform the indicated number of transactions. 
      for (int i = 0; i < operations; i++)
      {
        // Pick a table and key at random.
        int index = (int) (Math.random() * pstmtArray.length);
        int key = (int) (Math.random() * this.datarows);
        PreparedStatement pstmt = pstmtArray[index];

        // If we have a delay and this is the second or greater 
        // update, delay now. 
        if (i > 0 && delaymillis > 0)
        {
          Thread.sleep(delaymillis);
          if (logger.isDebugEnabled())
            logger.debug("Delayed between transactions");
        }

        // Do the update.  
        pstmt.setInt(1, execCount++);
        pstmt.setString(2, tag);
        pstmt.setInt(3, key);
        pstmt.execute();

        if (logger.isDebugEnabled())
        {
          logger.debug("Updated row: table=" + tableSet.getTables()[index].getName() 
              + " key=" + key + " i=" + i + " tag=" + tag);
        }
      }
      
      // Commit if we are using transactions. 
      if (! autocommit)
      {
        conn.commit();
        if (logger.isDebugEnabled())
        {
          logger.debug("Committing transaction");
        }
      }
    }
    catch (SQLException e)
    {
      // If we are transactional, roll back. 
      if (! autocommit)
        conn.rollback();
      if (logger.isDebugEnabled())
        logger.debug("Operation failed with SQL exception: " + e.getMessage());

      throw e;
    }
  }

  /** Clean up resources used by scenario. */
  public void cleanup() throws Exception
  {
    // Clean up connections. 
    for (int i = 0; i < pstmtArray.length; i++)
      pstmtArray[i].close();
    if (conn != null)
      conn.close();
  }
}