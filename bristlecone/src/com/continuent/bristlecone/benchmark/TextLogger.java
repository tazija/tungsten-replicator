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

package com.continuent.bristlecone.benchmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.continuent.bristlecone.benchmark.impl.Config;
import com.continuent.bristlecone.benchmark.impl.ConfigMetadata;
import com.continuent.bristlecone.benchmark.impl.ConfigPropertyMetadata;

/**
 * @author rhodges
 *
 */
public class TextLogger implements ResultLogger
{
  private static Logger logger = Logger.getLogger(TextLogger.class);  

  private PrintStream textOut = null;
  private boolean printedHeader = false;
  
  /** Creates a new logger. */
  public TextLogger(String outputFileName)
  {
    File textFile = new File(outputFileName);
    logger.info("Writing text output to file: " + textFile.toString());
    try
    {
      this.textOut = new PrintStream(new FileOutputStream(textFile, true));
    }
    catch (FileNotFoundException e)
    {
      String msg = "Unable to write to text report file: " + textFile.getAbsolutePath();
      throw new BenchmarkException(msg, e);
    }
  }

  // Initializes CVS output. 
  /* (non-Javadoc)
   * @see com.continuent.bristlecone.benchmark.BenchmarkResultLogger#init()
   */
  public void init(ConfigMetadata metadata)
  {
  }
  
  /* (non-Javadoc)
   * @see com.continuent.bristlecone.benchmark.BenchmarkResultLogger#resultGenerated(com.continuent.bristlecone.benchmark.Tuple)
   */
  public void resultGenerated(Config tuple)
  {
    ConfigMetadata metadata = tuple.getMetadata();

    // Print header with fixed fields the first time through. 
    if (! printedHeader)
    {
      // Write Values for fixed fields.
      textOut.println("=== FIXED BENCHMARK CONFIGURATION VALUES ===");
      Iterator<String> iter = metadata.propertyNames();
      while (iter.hasNext())
      {
        String name = iter.next();
        ConfigPropertyMetadata cpm = metadata.getPropertyMetadataAsserted(name);
        if (! cpm.isVariable() && ! cpm.isOutput())
          textOut.println(name + ": " + tuple.getProperty(name));
      }
      textOut.println();

      // Print header row for remaining properties. 
      iter = metadata.propertyNames();
      int index = 0;
      while (iter.hasNext())
      {
        String name = iter.next();
        ConfigPropertyMetadata cpm = metadata.getPropertyMetadataAsserted(name);
        if (cpm.isVariable() || cpm.isOutput())
          printPropertyName(name, index++);
      }
      textOut.println();
      
      printedHeader = true;
    }
    
    // Write data for non-fixed or output fields.   
    Iterator<String> iter = metadata.propertyNames();
    int index = 0;
    while (iter.hasNext())
    {
      String name = iter.next();
      ConfigPropertyMetadata cpm = metadata.getPropertyMetadataAsserted(name);
      if (cpm.isVariable() || cpm.isOutput())
        printPropertyValue(tuple.getProperty(name), index++);
    }

    // Print the end of line. 
    textOut.println();
  }
  
  // Print property name with tab preceding after first name. 
  private void printPropertyName(String name, int index)
  {
    if (index > 0)
      textOut.print("\t");
    textOut.print(name);
  }

  // Print quoted property value with tab preceding after first name. 
  private void printPropertyValue(String value, int index)
  {
    if (index > 0)
      textOut.print("\t");
    for (int i = 0; i < value.length(); i++)
    {
      char c = value.charAt(i);
      if (c == '\t')
        textOut.print("\\t");
      else
        textOut.print(c);
    }
  }

  /* (non-Javadoc)
   * @see com.continuent.bristlecone.benchmark.BenchmarkResultLogger#cleanup()
   */
  public void cleanup()
  {
    textOut.close();
  }
}