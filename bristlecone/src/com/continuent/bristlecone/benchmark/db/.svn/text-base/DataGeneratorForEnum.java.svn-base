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
 * Generates enum values. 
 * 
 * @author Scott Martin
 *
 */
public class DataGeneratorForEnum implements DataGenerator
{
  private int numberOfDistinctValues; 
 
  /** Create a new instance recording numberOfDistinctValues */
  DataGeneratorForEnum(int numberOfDistinctValues)
  {
    this.numberOfDistinctValues = numberOfDistinctValues; 
  }
  
  /** 
   * Return a random enum based on the numberOfDistinctValues
   */
  public Object generate()
  {
      return ((int)(Math.random() * numberOfDistinctValues)) + 1;
  }
}
