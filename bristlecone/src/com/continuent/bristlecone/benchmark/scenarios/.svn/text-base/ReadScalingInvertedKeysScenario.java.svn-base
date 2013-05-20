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
import java.sql.Statement;
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
 * Generates reads from table(s) that invert the key values through a 
 * secondary unique index column that partitions values such that adjacent 
 * values are very unlikely to be found on the same page.  This is ideally 
 * suited for testing buffer cache utilization, in which very simple queries 
 * that take little CPU to run will cause buffer cache misses.  <p>
 * 
 * The table used for this case has the following form: 
 * <ol>
 * <li>mykey - Primary key numbered sequentially from one to N where N is the 
 * table length</li>
 * <li>mykey2 - Alternate key numbered from one to N</li>
 * <li>mypayload - Data column to "fatten up" rows so they take more space.  
 * The size of the column is defined by the datawidth parameter. 
 * </ol>
 * 
 * The values of mykey2 are computed using the following algorithm.  Suppose
 * that datarows is the length of the table, and n is the value of mykey  
 * for a particular row.  We define the partition size as variable "step".  
 * Then the value of mykey2 is determined as follows: 
 * 
 * <pre><code>mykey2 = ((n % step) * datarows / step) + (n / step)</pre></code>
 *       
 * The preceding expression permutes the values of mykey2 so that for 
 * datarows=10 and step=2, instead of values running 1,2,3,4,5,...,10 we see 
 * 1,6,2,7,3,8,4,9,5,10.  The "step" is therefore the number of rows you skip
 * before the next sequential mykey value appears.  The query that runs 
 * against the table so generated appears as follows: <p>
 * 
 * <pre><code>SELECT * FROM table where mykey2 >= ? AND mykey2 <= ?</code></pre>
 * 
 * We choose a random value for the starting value and select the end 
 * value by adding selectRows to it.  By defining datawidth and step 
 * appropriately, it is easy to ensure that every row selected is on a 
 * separate disk page.  This will force a high number of buffer cache misses 
 * if datarows is large.  <p>
 * 
 * This scenario can also be parameterized by standard values such as number of 
 * clients, number of tables, etc.<p>
 * 
 * @author rhodges
 */
public class ReadScalingInvertedKeysScenario extends ScenarioBase
{
  // For this implementation we use the standard properties but override the 
  // table population algorithm. 
  private static final Logger logger = Logger.getLogger(ReadScalingInvertedKeysScenario.class);

  private int step = 1;  
  private int selectrows = 1;
  protected PreparedStatement[] pstmtArray;
  
  /** Set the number of rows to skip between sequential mykey2 values. */
  public void setStep(int step)
  {
    this.step = step;
  }
  
  /** 
   * Defines the number of rows selected for running aggregates, which
   * affects the amount of work the DBMS engine much perform. 
   */
  public void setSelectrows(int selectrows)
  {
    this.selectrows = selectrows;
  }

  /**
   * Perform basic initialization. 
   */
  public void initialize(Properties properties) throws Exception
  {
    Column[] columns = new Column[] {
        new Column("mykey", Types.INTEGER, -1, -1, true, false),
        new Column("mykey2", Types.INTEGER),
        new Column("mypayload", Types.VARCHAR, (int) datawidth)
      };
    tableSet = new TableSet("benchmark_scenario_", tables, 
        datarows, columns);
    helper = new TableSetHelper(url, user, password); 
    conn = helper.getConnection();
  }

  /** Configure test tables. */
  public void globalPrepare() throws Exception
  {
    SqlDialect dialect = helper.getSqlDialect();
    int partition = datarows / step;

    // Create tables. 
    if (reusedata)
    {
      logger.info("Reusing test tables...");
    }
    else
    {
      logger.info("Creating test tables...");
      helper.createAll(tableSet);
      
      // We now have to populate the tables ourselves. 
      for (int i = 0; i < tableSet.getCount(); i++)
      {
        Table table = tableSet.getTables()[i];
        Column col3 = table.getColumn("mypayload");
        DataGenerator generator = DataGeneratorFactory.getInstance().getGenerator(col3);

        String insert = dialect.getInsert(table);
        PreparedStatement pstmt = conn.prepareStatement(insert);
        
        for (int j = 0; j < datarows; j++)
        {
          // Compute mykey2 value. 
          int mykey2 = ((j % step) * partition) + (j / step);

          pstmt.setInt(1, j);
          pstmt.setInt(2, mykey2);
          pstmt.setObject(3, generator.generate());
          pstmt.execute();
        }
        pstmt.close();
        
        // Add an index on mykey2.
        String createIndex = "create unique index " + table.getName() 
          + "_mykey2 on " + table.getName() + " (mykey2)";
        Statement stmt = conn.createStatement();
        stmt.execute(createIndex);
        stmt.close();
      }
    }

    // Run analyze command if supplied. 
    if (analyzeCmd != null)
    {
      logger.info("Running analyze command: " + analyzeCmd);
      helper.execute(analyzeCmd);
    }
  }
  
  
  /** Create a prepared statement array. */
  public void prepare() throws Exception
  {
    Table tables[] = tableSet.getTables();
    pstmtArray = new PreparedStatement[tables.length];
    for (int i = 0; i < tables.length; i++)
    {
      String sql = "SELECT * FROM " + tables[i].getName() 
                 + " WHERE mykey2 >= ? AND mykey2 <= ?"; 
      pstmtArray[i] = conn.prepareStatement(sql);
    }
  }

  /** Execute an interation. */
  public void iterate(long iterationCount) throws Exception
  {
    // Pick a table and key at random.
    int index = (int) (Math.random() * pstmtArray.length);
    int key1 = (int) (Math.random() * this.datarows);
    int key2 = key1 + selectrows;
    PreparedStatement pstmt = pstmtArray[index];
    
    // Do the query.
    pstmt.setInt(1, key1);
    pstmt.setInt(2, key2);
    pstmt.executeQuery();
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
