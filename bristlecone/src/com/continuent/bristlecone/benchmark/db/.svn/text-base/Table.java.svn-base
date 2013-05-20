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
 * Implements a definition of a single table with columns.
 * 
 * @author rhodges
 */
public class Table
{
  private String       name;
  private List<Column> columns        = new ArrayList<Column>();
  private String       databaseEngine = null;

  /** Creates an anonymous table definition. */
  public Table()
  {
  }

  /** Creates a table with name only. Columns must be added. */
  public Table(String name)
  {
    this.name = name;
  }

  /** Creates a table with name and columns. */
  public Table(String name, Column[] columns)
  {
    this.name = name;
    for (Column col : columns)
    {
      this.columns.add(col);
    }
  }

  /** Returns the table name. */
  public String getName()
  {
    return name;
  }

  /** Sets the table name. */
  public void setName(String name)
  {
    this.name = name;
  }

  /** Adds a column to the end of the column list. */
  public void addColumn(Column col)
  {
    columns.add(col);
  }

  /** Returns the table column definitions. */
  public Column[] getColumns()
  {
    return columns.toArray(new Column[columns.size()]);
  }

  /**
   * Returns a specific column using the name as index or null if no s such name
   * can be found.
   */
  public Column getColumn(String name)
  {
    for (Column col : columns)
    {
      if (name.equals(col.getName()))
        return col;
    }
    return null;
  }

  /** Returns the primary key column if there is one. */
  public Column getPrimaryKey()
  {
    for (Column col : columns)
    {
      if (col.isPrimaryKey())
        return col;
    }
    return null;
  }

  /** Sets the primary key if the column exists. */
  public boolean setPrimaryKey(String name)
  {
    for (Column col : columns)
    {
      if (name.equals(col.getName()))
      {
        col.setPrimaryKey(true);
        return true;
      }
    }
    return false;
  }

  /** Sets name of database engine. */
  public void setDatabaseEngine(String databaseEngine)
  {
    this.databaseEngine = databaseEngine;
  }

  /** Return the database engine name if it is set, or null if not. */
  public String getDatabaseEngine()
  {
    return databaseEngine;
  }

  @Override
  public String toString()
  {
    return ToStringHelper.toString(this);
  }
  
  /** Make a copy of this table. */
  public Table clone()
  {
      Column[] colCopy = getColumns();
      Table tableCopy = new Table(name, colCopy);
      tableCopy.setDatabaseEngine(databaseEngine);
      return tableCopy;
  }
}