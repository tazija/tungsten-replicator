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

import com.continuent.bristlecone.benchmark.db.Table;

/**
 * Implements a scenario that updates rows in a fixed table.  The update 
 * statement uses a subselect to set the value of the mydata field to the
 * average of some number of randomly selected rows.  The size of the 
 * subselect is controlled by the selectrows parameter.<p>
 * 
 * This scenario is useful for testing scaling of updates that have embedded
 * reads--for example in a master/slave scenario such an update would run 
 * slower on the master and faster on a slave assuming only the result is 
 * propagated.  If the whole statement is propagated, update speed should be
 * identical on both master and slave.<p>
 *
 * This scenario is parameterized by the usual base scenario parameters. 
 * 
 * @author rhodges
 */
public class WriteComplexScenario extends ScenarioBase
{
  private int selectrows;
  protected PreparedStatement[] pstmtArray;

  /** 
   * Defines the number of rows selected for running aggregates used to populate
   * the write table. 
   */
  public void setSelectrows(int selectrows)
  {
    this.selectrows = selectrows;
  }

  /** Create a prepared statement array. */
  public void prepare() throws Exception
  {
    Table tables[] = tableSet.getTables();
    pstmtArray = new PreparedStatement[tables.length];
    for (int i = 0; i < tables.length; i++)
    {
      String sql = "UPDATE " + tables[i].getName() 
      + " t1 SET t1.mydata = (SELECT avg(t2.myint) FROM " + tables[i].getName() 
      + " t2 WHERE t2.mykey >= ? AND t2.mykey <= ?) WHERE t1.mykey = ?"; ;
      pstmtArray[i] = conn.prepareStatement(sql);
    }
  }

  /** Execute an interation. */
  public void iterate(long iterationCount) throws Exception
  {
    // Pick a table at random on which to operate.
    int index = (int) (Math.random() * pstmtArray.length);
    int key = (int) (Math.random() * this.datarows);

    // Run the update. 
    PreparedStatement pstmt = pstmtArray[index];
    pstmt.setInt(1, key);
    pstmt.setInt(2, key + selectrows);
    pstmt.setInt(3, key);
    pstmt.execute();
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