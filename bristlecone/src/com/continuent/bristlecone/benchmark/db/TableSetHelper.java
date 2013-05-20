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

package com.continuent.bristlecone.benchmark.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.continuent.bristlecone.benchmark.BenchmarkException;

/**
 * Implements methods to create, populate, and drop sets of tables containing
 * test data.  
 * 
 * @author rhodges
 */
public class TableSetHelper extends TableHelper
{
  private static Logger logger = Logger.getLogger(TableSetHelper.class);
  
  /** 
   * Creates a new instance. 
   * 
   * @param url JDBC URL of database where tables live 
   * @param login
   * @param password
   * @throws BenchmarkException If JDBC driver cannot be loaded or we can't 
   *         find the SqlDialect. 
   */
  public TableSetHelper(String url, String login, String password)
  {
    super(url, login, password);
  }
  
  /** 
   * Creates all tables in a table set.  Any existing tables are dropped. 
   */
  public void createAll(TableSet tableSet) throws SQLException
  {
    // Drop tables, ignoring errors. 
    dropAll(tableSet, true);
    
    int count = tableSet.getCount();
    Table[] tables = tableSet.getTables();
   
    for (int i = 0; i < count; i++)
    {
      create(tables[i], true);
    }
  }

  /** Populates tables in the TableSet with random data. */
  public void populateAll(TableSet tableSet) throws SQLException
  {
    // Set up to write to tables. 
    int count = tableSet.getCount();
    int rows = tableSet.getRows();
    Table[] tables = tableSet.getTables();
    Connection conn = getConnection();

    // Populate each table in succession.  
    for (int i = 0; i < count; i++)
    {
      String insertSql = sqlDialect.getInsert(tables[i]);
      PreparedStatement pstmt = conn.prepareStatement(insertSql);
      
      try
      {
        for (int r = 0; r < rows; r++)
        {
          generateParameters(tableSet, pstmt);
          pstmt.executeUpdate();
        }
      }
      catch (SQLException e)
      {
        logger.warn("Table propagation failed: " + insertSql, e);
        releaseStatement(pstmt);
        releaseConnection(conn);
        throw e;
      }
      finally
      {
        releaseStatement(pstmt);
      }
    }
    
    // Clean up. 
    releaseConnection(conn);
  }
  
  /**
   * Generate parameters for a prepared statement from the associated
   * table set. 
   */
  public void generateParameters(TableSet tableSet, PreparedStatement ps) 
     throws SQLException
  {
    List<DataGenerator> generators = tableSet.getDataGenerators();
    int index = 1;
    for (DataGenerator dg: generators)
    {
      ps.setObject(index++, dg.generate());
    }
  }

  /** 
   * Drop all tables in the table set, optionally ignoring errors
   * due to non-existing tables. 
   */
  public void dropAll(TableSet tableSet, boolean ignore) throws SQLException
  {
    int count = tableSet.getCount();
    Table[] tables = tableSet.getTables();
    
    for (int i = 0; i < count; i++)
    {
      drop(tables[i], ignore);
    }
  }
}
