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

/**
 * @author rhodges
 *
 */
public class SqlDialectFactory
{
  private static final SqlDialectFactory instance = new SqlDialectFactory();
  
  // Declare and initialized dialect support. 
  private static final List<SqlDialect> dialects;
  static
  {
    List<SqlDialect> al = new ArrayList<SqlDialect>();
    al.add(new SqlDialectForMysql());
    al.add(new SqlDialectForPostgreSQL());
    al.add(new SqlDialectForVertica());
    al.add(new SqlDialectForPCluster());
    al.add(new SqlDialectForMCluster());
    al.add(new SqlDialectForHSQLDB());
    al.add(new SqlDialectForOracle());
    al.add(new SqlDialectForDerby());
    dialects = al;
  }

  /** 
   * Returns factory instance. 
   */
  public static SqlDialectFactory getInstance()
  {
    return instance;
  }

  /**
   * Return a dialect that processes the given JDBC URL or null if no match
   * can be found. 
   * 
   * @param url A JDBC URL
   */
  public SqlDialect getDialect(String url)
  {
    int count = dialects.size();
    for (int i = 0; i < count; i++)
    {
      if (dialects.get(i).supportsJdbcUrl(url))
        return dialects.get(i);
    }
    return null;
  }
}