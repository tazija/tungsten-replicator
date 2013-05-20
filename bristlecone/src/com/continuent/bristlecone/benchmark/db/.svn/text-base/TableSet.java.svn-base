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

import java.util.ArrayList;
import java.util.List;

import com.continuent.bristlecone.utils.ToStringHelper;


/**
 * Describes the content of a set of tables, which have the same schema
 * but each have a different name. 
 * 
 * @author rhodges
 *
 */
public class TableSet
{
  private final String namePrefix;
  private final int count;
  private final int rows;
  private final Column[] columns;
  private Table[] tables;
  private List<DataGenerator> dataGenerators;
  
  public TableSet(String namePrefix, int count, int rows, Column[] columns)
  {
    this.namePrefix = namePrefix;
    this.count = count;
    this.rows = rows;
    this.columns = columns;
  }

  /** Returns the table column definitions. */
  public Column[] getColumns()
  {
    return columns;
  }

  /** Returns the number of tables in the group. */
  public int getCount()
  {
    return count;
  }

  /** Returns the prefix for each table name in the group. */
  public String getNamePrefix()
  {
    return namePrefix;
  }

  /** Returns the number of rows desired in each table. */
  public int getRows()
  {
    return rows;
  }
  
  /** Returns a list of table definitions. */
  public synchronized Table[] getTables()
  {
    if (tables == null)
    {
      Table[] array = new Table[count];
      for (int i = 0; i < count; i++)
      {
        Table t = new Table(this.namePrefix + i, columns);
        array[i] = t;
      }
      tables = array;
    }
    return tables;
  }
  
  /** Returns a random list of tables.  This is handy for join tests. */
  public Table[] getRandomTables(int howMany)
  {
    // Ensure the number of tables requested is not more than we have. 
    getTables();
    if (howMany > tables.length)
      throw new Error("Caller requested too many random tables: requested=" 
          + howMany + " available=" + tables.length);
    
    // Generate index of the first table and start reading tables from 
    // that point.  
    Table[] randomTables = new Table[howMany];
    int index = (int) Math.random() * tables.length;
    for (int i = 0; i < howMany; i++)
    {
      randomTables[i] = tables[index++];
      if (index > tables.length)
        index = 0;
    }
    return randomTables;
  }

  
  public synchronized List<DataGenerator> getDataGenerators()
  {
    if (dataGenerators == null)
    {
      // Set up column generators for data. 
      Column[] columns = getColumns();
      dataGenerators = new ArrayList<DataGenerator>();
      
      for (int i = 0; i < columns.length; i++)
      {
        if (! columns[i].isAutoIncrement())
        {
          dataGenerators.add(DataGeneratorFactory.getInstance().getGenerator(columns[i]));
        }
      }
    }
    return dataGenerators; 
  }
  
  @Override public String toString()
  {
      return ToStringHelper.toString(this);
  }
}
