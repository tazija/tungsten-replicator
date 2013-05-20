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

/**
 * Generates set values. 
 * 
 * @author Scott Martin
 *
 */
public class DataGeneratorForSet implements DataGenerator
{
  private int numberOfDistinctValues; 
 
  /** Create a new instance recording numberOfDistinctValues */
  DataGeneratorForSet(int numberOfDistinctValues)
  {
    this.numberOfDistinctValues = numberOfDistinctValues; 
  }
  
  /** 
   * Return a random set based on the numberOfDistinctValues
   * This needs to be coordinated with the legal set values found in
   * SqlDialectForMysql.implementationSpecificSuffix()
   * 
   * Sets look like 'setele1,setele2', 'setele10', or 'setele3, setele4, setele3'
   * 
   */
  public Object generate()
  {
      String retval="";
      int numberOfElements = (int)(Math.random() * numberOfDistinctValues) + 1;
      for (int i = 0; i < numberOfElements; i++)
      {
          int thisElement = (int)(Math.random() * numberOfDistinctValues);
          if (i != 0) retval += ",";
          retval += "setele" + thisElement;
      }
      return retval;
  }
}
