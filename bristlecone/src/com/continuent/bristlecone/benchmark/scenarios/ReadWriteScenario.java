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
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.continuent.bristlecone.benchmark.db.Column;
import com.continuent.bristlecone.benchmark.db.DataGenerator;
import com.continuent.bristlecone.benchmark.db.DataGeneratorFactory;
import com.continuent.bristlecone.benchmark.db.SqlDialect;
import com.continuent.bristlecone.benchmark.db.Table;
import com.continuent.bristlecone.benchmark.db.TableSet;
import com.continuent.bristlecone.benchmark.db.TableSetHelper;

/**
 * This scenario models a complex transaction in which reads are used to 
 * compute values that are then inserted.  The reads are structured to ensure
 * that the DBMS performs table scans to select read values.  <p>
 * 
 * @author rhodges
 */
public class ReadWriteScenario extends ScenarioBase
{
  // For this implementation we use the standard properties but override the 
  // table population algorithm. 

  private static final Logger logger = Logger.getLogger(ReadScalingInvertedKeysScenario.class);

  TableSet readTableSet; 
  TableSet writeTableSet;
  PreparedStatement readArray[];
  PreparedStatement writeArray[];
  DataGenerator stringDataGenerator; 
  
  private int operations = 1;
  private int selectrows = 1;
  private boolean autocommit = false;
  
  /** 
   * Set the number of operations per transaction.  There is a read/write 
   * pair for each operation.  
   */
  public void setOperations(int operations)
  {
    this.operations = operations;
  }
  
  /** 
   * Defines the number of rows selected for running aggregates used to populate
   * the write table. 
   */
  public void setSelectrows(int selectrows)
  {
    this.selectrows = selectrows;
  }

  /** Determine whether to use autocommit or actual transactions. */
  public void setAutocommit(boolean autocommit)
  {
    this.autocommit = autocommit;
  }

  /**
   * Perform basic initialization. 
   */
  public void initialize(Properties properties) throws Exception
  {
    Column[] readCols = new Column[3]; 
    readCols[0] = new Column();
    readCols[0].setName("mykey");
    readCols[0].setType(Types.INTEGER);
    readCols[0].setPrimaryKey(true);

    readCols[1] = new Column();
    readCols[1].setName("myint");
    readCols[1].setType(Types.INTEGER);

    readCols[2] = new Column();
    readCols[2].setName("mypayload");
    readCols[2].setType(Types.VARCHAR);
    readCols[2].setLength(datawidth);

    readTableSet = new TableSet("benchmark_scenario_read", tables, 
        datarows, readCols);

    Column[] writeCols = new Column[3]; 
    writeCols[0] = new Column();
    writeCols[0].setName("mykey");
    writeCols[0].setType(Types.INTEGER);
    writeCols[0].setPrimaryKey(true);
    writeCols[0].setAutoIncrement(true);

    writeCols[1] = new Column();
    writeCols[1].setName("mysum");
    writeCols[1].setType(Types.SMALLINT);

    writeCols[2] = new Column();
    writeCols[2].setName("mypayload");
    writeCols[2].setType(Types.VARCHAR);
    writeCols[2].setLength(datawidth);

    writeTableSet = new TableSet("benchmark_scenario_write", tables, 
        datarows, writeCols);

    helper = new TableSetHelper(url, user, password); 
    conn = helper.getConnection();
    stringDataGenerator = DataGeneratorFactory.getInstance().getGenerator(writeCols[2]);
  }

  /** Configure test tables. */
  public void globalPrepare() throws Exception
  {
    if (reusedata)
    {
      logger.info("Reusing read tables");
    }
    else
    {
      // Create and populate tables. 
      logger.info("Creating and populating read tables...");
      helper.createAll(readTableSet);
      helper.populateAll(readTableSet);

      logger.info("Creating write tables...");
      helper.createAll(writeTableSet);
    }

    // Run analyze command if supplied. 
    if (analyzeCmd != null)
    {
      logger.info("Running analyze command: " + analyzeCmd);
      helper.execute(analyzeCmd);
    }
  }
  
  /** Create a prepared statement arrays for reads and writes. */
  public void prepare() throws Exception
  {
    SqlDialect dialect = helper.getSqlDialect(); 

    Table tables[] = readTableSet.getTables();
    readArray = new PreparedStatement[tables.length];
    for (int i = 0; i < tables.length; i++)
    {
      String sql = "SELECT sum(myint) FROM " + tables[i].getName() 
                 + " WHERE mykey >= ? AND mykey <= ?"; 
      readArray[i] = conn.prepareStatement(sql);
    }
    
    tables = writeTableSet.getTables();
    writeArray = new PreparedStatement[tables.length];
    for (int i = 0; i < tables.length; i++)
    {
      String sql = dialect.getInsert(tables[i]);
      writeArray[i] = conn.prepareStatement(sql);
    }
  }

  /** Execute an interation. */
  public void iterate(long iterationCount) throws Exception
  {
    // Ensure we have the proper autocommit setting. 
    conn.setAutoCommit(autocommit);

    // Pick a table and key at random.
    int index = (int) (Math.random() * tables);
    int key = (int) (Math.random() * this.datarows);
    
    int sum[] = new int[operations];

      // Loop through and perform the indicated number of reads. 
      for (int i = 0; i < operations; i++)
      {
        PreparedStatement pstmt = readArray[index];
        pstmt.setInt(1, key);
        pstmt.setInt(2, key + selectrows);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next())
        {
          sum[i] = rs.getInt(1);
        }
        
        rs.close();
      }
        
      // Now generate an equivalent number of writes. 
      for (int i = 0; i < operations; i++)
      {
        PreparedStatement pstmt = writeArray[index];
        pstmt.setInt(1, sum[i]);
        pstmt.setObject(2, stringDataGenerator.generate());
        pstmt.execute();
      }
      
      // Commit if we are using transactions. 
      if (! autocommit)
        conn.commit();
  }

  /** Clean up resources used by scenario. */
  public void cleanup() throws Exception
  {
    for (int i = 0; i < tables; i++)
    {
      this.readArray[i].close();
      this.writeArray[i].close();
    }
    if (conn != null)
      conn.close();
  }
}