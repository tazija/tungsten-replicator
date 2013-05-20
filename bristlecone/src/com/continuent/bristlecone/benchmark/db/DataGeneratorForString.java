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
 * Generates integer values. 
 * 
 * @author rhodges
 *
 */
public class DataGeneratorForString implements DataGenerator
{
  private static String values = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  /* These specials could be expanded but for now just use "normal" ones */
  private static String special = "~!@#$%^&*()_+`-={}[]|:;<>,.?/\'\\\"";
  private int length; 
  private int repeat;
  private boolean useSpecial = true; /* For now, always true, placed here for expansion */
 
  /** Create a new instance with an upper bound. */
  DataGeneratorForString(int length, int repeat)
  {
    this.length = length; 
    this.repeat = repeat;
  }
  
  /** 
   * Generate a string based on a repeating sequence of characters 
   * 'repeat' places in length. 
   */
  public Object generate()
  {
    char[] base = new char[repeat];
    for (int i = 0; i < repeat; i++)
    {
      /* 20% of the time, if useSpecial is enabled, insert special character */
      if (useSpecial && Math.random() < 0.2)
      {
        int index = (int) (Math.random() * special.length());
        base[i] = special.charAt(index);
      } else {
        int index = (int) (Math.random() * values.length());
        base[i] = values.charAt(index);
      }
    }
    
    char[] generatedValues = new char[length];
    for (int i = 0; i < length; i++)
    {
      int index = i % base.length;
      generatedValues[i] = base[index];
    }
    
    return new String(generatedValues);
  }
}
