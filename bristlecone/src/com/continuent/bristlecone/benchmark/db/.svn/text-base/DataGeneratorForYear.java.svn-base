/**
 * Bristlecone Test Tools for Databases
 * Copyright (C) 2006-2010 Continuent Inc.
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
 * Initial developer(s): Scott Martin.
 * Contributor(s):
 */

package com.continuent.bristlecone.benchmark.db;

/**
 * Generates year values. 
 * 
 * @author scott.martin@continuent.
 *
 */
public class DataGeneratorForYear implements DataGenerator
{
    private static final int lowestYear = 1901; // inclusive
    private static final int highestYear = 2155; // inclusive

    /** Create a new instance with an upper bound. */
  DataGeneratorForYear()
  {
  }
  
  /** 
   * Generate a string based on a repeating sequence of characters 
   * 'repeat' places in length. 
   */
  public Object generate()
  {
      int retval = 0;
      retval = (int)(Math.random() * (highestYear - lowestYear  + 1)) + lowestYear;
      return retval;
  }
}
