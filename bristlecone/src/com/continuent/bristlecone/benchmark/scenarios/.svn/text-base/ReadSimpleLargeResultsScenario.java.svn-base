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

import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.continuent.bristlecone.benchmark.db.SqlDialect;
import com.continuent.bristlecone.benchmark.db.Table;

/**
 * Implements a scenario that runs selects resulting in huge query result sets. 
 * This case implements queries that do a cross product query on tables
 * in the table set.  The result set size is therefore the square of the 
 * datarows parameter (table row length).  The scenario includes an optional
 * fetchsize property that can be used to control fetch size when retrieving 
 * statement results. <p>
 * 
 * This scenario is useful for testing efficiency and performance when 
 * handling very large result sets.  
 * 
 * @author rhodges
 */
public class ReadSimpleLargeResultsScenario extends ScenarioBase
{
  private static final Logger logger = Logger.getLogger(ReadSimpleLargeResultsScenario.class);

  private int fetchsize = 0;
  
  /** 
   * Sets the number of rows used in the JDBC fetchRows property on Statement
   * instances.  The following table illustrates how these values are used: <p>
   * <table>
   * <tr><td>Value</td><td>Action</td></tr>
   * <tr><td>0</td><td>Fetch rows are not used</td></tr>
   * <tr><td>Positive</td><td>Set fetch rows to value</td></tr>
   * <tr><td>Negative</td><td>Set fetch rows to Integer.MIN_VALUE (forces row-by-row return)</td></tr>
   * </table>
   * <p>
   * If the value is non-zero, we define a statement with forward-only cursor
   * positioning.  Note that Integer.MIN_VALUE usage is peculiar to MySQL.  Most
   * drivers reject fetch size values less than 0.  
   */
  public void setFetchsize(int fetchsize)
  {
    this.fetchsize = fetchsize;
  }
  
  /** Prepare does nothing. */
  public void prepare() throws Exception
  {
  }

  /** Execute an interation. */
  public void iterate(long iterationCount) throws Exception
  {
    // Pick a table at random on which to operate.
    int index = (int) (Math.random() * tables);
    Table table = tableSet.getTables()[index];

    // Generate a statement using a cross product select. 
    SqlDialect sqlDialect = helper.getSqlDialect();
    String select = sqlDialect.getSelectCrossProduct(table); 
    int convertedFetchSize = sqlDialect.implementationConvertFetchSize(fetchsize);
    if (logger.isDebugEnabled())
    {
      logger.debug("Select statement for table cross product: " + select);
      logger.debug("Statement fetch size: input=" + fetchsize + " converted=" + convertedFetchSize);
    }

    Statement stmt = null;
    ResultSet rs = null;
    try
    {
      // Begin a transaction.  This is required by PostgreSQL for fetchSize to work.  
      conn.setAutoCommit(false);
      if (fetchsize == 0)
        stmt = conn.createStatement();
      else
      {
        stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        stmt.setFetchSize(convertedFetchSize);
      }
       
      rs = stmt.executeQuery(select);
      int count = 0;
      while (rs.next())
      {
        count++;
      }
      if (logger.isDebugEnabled())
        logger.debug("Rows selected: " + count);
    }
    finally
    {
      if (rs != null)
        rs.close();
      if (stmt != null)
        stmt.close();
      conn.rollback();
    }
  }

  /** Clean up resources used by scenario. */
  public void cleanup() throws Exception
  {
  }
}