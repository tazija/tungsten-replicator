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
public class DataGeneratorForXML implements DataGenerator
{
  private static String values = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
  /* These specials could be expanded but for now just use "normal" ones */
  //private static String special = "~!@#$%^&*()_+`-={}[]|:;<>,.?/\'\\\"";
  private int length; 
  private int repeat;
  //private boolean useSpecial = true; /* For now, always true, placed here for expansion */
  private int remainingLength = 0;
 
  /** 
   * Create a new instance with an upper bound. 
   * @param length - Caution, length is only used as a rough estimate for termination.
   * It is not strictly honored.  Some returned values may be shorter that length and some
   * may be longer
   * @param repeat - ignored.
   */
  
  DataGeneratorForXML(int length, int repeat)
  {
    this.length = length; 
    this.repeat = repeat;
  }
  
  /**
   * 
   * Generate XML clauses with potential subclause resembling something of the form
   * <tok1> stuff <tok2> more stuff </tok2> </tok1>.
   * 
   * @param recursionLevel  - The current depth of recursion.
   * @return - Valid XML string for testing purposes.
   */
  private String createClause(int recursionLevel)
  {
      int subClauses;
      String retVal = "";
      
      if (recursionLevel == 0) remainingLength = length;

      if (remainingLength < 0) return "";
      
      char[] clauseNameCA = new char[repeat];
      for (int i = 0; i < repeat; i++)
      {
          int index = (int) (Math.random() * values.length());
          clauseNameCA[i] = values.charAt(index);
      }
      
      String clauseName = new String(clauseNameCA); 
 
      clauseNameCA = new char[repeat];
      for (int i = 0; i < repeat; i++)
      {
          int index = (int) (Math.random() * values.length());
          clauseNameCA[i] = values.charAt(index);
      }
      
      String clauseValue = new String(clauseNameCA); 
           
      retVal += "<" + clauseName + ">";
      retVal += clauseValue;
  
      remainingLength -= clauseName.length() + clauseValue.length() + 2;
      
      int pct = (int) (Math.random() * 100);
  
      /* Pontentially generate sub clause */
      subClauses = 0;
      if (pct > 70) subClauses = 1;
      if (pct > 80) subClauses = 2;
      if (pct > 95) subClauses = 10;
      
      if (remainingLength < 0) subClauses = 0;
      
      for (int i = 0; i < subClauses; i++)
      {
          retVal += createClause(recursionLevel + 1);
          retVal += clauseValue;
          remainingLength -= clauseValue.length();
          if (remainingLength < 0) break;
      }

      retVal += "</" + clauseName + ">";
      remainingLength -= clauseName.length() + 3;
      
      return retVal;
  }
  
  /** 
   * Generate a string based on a repeating sequence of characters 
   * 'repeat' places in length. 
   */
  public Object generate()
  {
      return createClause(0);
  }
}
