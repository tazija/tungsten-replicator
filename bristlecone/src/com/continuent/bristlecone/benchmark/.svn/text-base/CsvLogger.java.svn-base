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
 * Implements a logger used to generate CSV output. 
 * 
 * @author rhodges
 */
public class CsvLogger implements ResultLogger
{
  private static Logger logger = Logger.getLogger(Benchmark.class);  

  private PrintStream cvsOut;
  
  /** Creates a new CVS logger. */
  public CsvLogger(String outputFileName)
  {
    File csvFile = new File(outputFileName);
    logger.info("Writing CSV output to file: " + csvFile.toString());

    if (csvFile.exists())
    {
      logger.info("Deleting previous file");
      csvFile.delete();
    }

    try
    {
      this.cvsOut = new PrintStream(new FileOutputStream(csvFile, true));
    }
    catch (FileNotFoundException e)
    {
      String msg = "Unable to write to csv file: " + csvFile.getAbsolutePath();
      throw new BenchmarkException(msg, e);
    }
  }

  // Initializes CVS output. 
  /* (non-Javadoc)
   * @see com.continuent.bristlecone.benchmark.BenchmarkResultLogger#init()
   */
  public void init(ConfigMetadata metadata)
  {
    int index = 0;

    // Write headers for all fixed fields. 
    Iterator<String> iter = metadata.propertyNames();
    while (iter.hasNext())
    {
      String name = iter.next();
      ConfigPropertyMetadata cpm = metadata.getPropertyMetadataAsserted(name);
      if (! cpm.isVariable() && ! cpm.isOutput())
        printPropertyName(name, index++);
    }

    // Write headers for all dynamic fields. 
    iter = metadata.propertyNames();
    while (iter.hasNext())
    {
      String name = iter.next();
      ConfigPropertyMetadata cpm = metadata.getPropertyMetadataAsserted(name);
      if (cpm.isVariable())
        printPropertyName(name, index++);
    }
    
    // Write headers for all output fields. 
    iter = metadata.propertyNames();
    while (iter.hasNext())
    {
      String name = iter.next();
      ConfigPropertyMetadata cpm = metadata.getPropertyMetadataAsserted(name);
      if (cpm.isOutput())
        printPropertyName(name, index++);
    }
    
    // Print the end of line. 
    cvsOut.println();
  }
  
  /* (non-Javadoc)
   * @see com.continuent.bristlecone.benchmark.BenchmarkResultLogger#resultGenerated(com.continuent.bristlecone.benchmark.Tuple)
   */
  public void resultGenerated(Config tuple)
  {
    ConfigMetadata metadata = tuple.getMetadata();
    int index = 0;

    // Write data for all fixed fields. 
    Iterator<String> iter = metadata.propertyNames();
    while (iter.hasNext())
    {
      String name = iter.next();
      ConfigPropertyMetadata cpm = metadata.getPropertyMetadataAsserted(name);
      if (! cpm.isVariable() && ! cpm.isOutput())
        printPropertyValue(tuple.getProperty(name), index++);
    }

    // Write data for all dynamic fields. 
    iter = metadata.propertyNames();
    while (iter.hasNext())
    {
      String name = iter.next();
      ConfigPropertyMetadata cpm = metadata.getPropertyMetadataAsserted(name);
      if (cpm.isVariable())
        printPropertyValue(tuple.getProperty(name), index++);
    }
    
    // Write data for all output fields. 
    iter = metadata.propertyNames();
    while (iter.hasNext())
    {
      String name = iter.next();
      ConfigPropertyMetadata cpm = metadata.getPropertyMetadataAsserted(name);
     if (cpm.isOutput())
        printPropertyValue(tuple.getProperty(name), index++);
    }
    
    // Print the end of line. 
    cvsOut.println();
  }
  
  /* (non-Javadoc)
   * @see com.continuent.bristlecone.benchmark.BenchmarkResultLogger#cleanup()
   */
  public void cleanup()
  {
    cvsOut.close();
  }

  // Print property name with comma preceding after first name. 
  private void printPropertyName(String name, int index)
  {
    if (index > 0)
      cvsOut.print(", ");
    cvsOut.print(name);
  }

  // Print quoted property value with comma preceding after first name. 
  private void printPropertyValue(String value, int index)
  {
    if (index > 0)
      cvsOut.print(", ");
    if (value == null)
    {
      // Null values are possible due to cross products.  
      // Fix for BRI-4. 
      cvsOut.print("null");
    }
    else
    {
      // Print actual values one character at a time.  We 
      // may want to escale some characters at some point. 
      for (int i = 0; i < value.length(); i++)
      {
        char c = value.charAt(i);
        cvsOut.print(c);
      }
    }
  }
}
