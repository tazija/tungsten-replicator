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

package com.continuent.bristlecone.evaluator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Writes neatly formatted CSV to the specified file.
 * 
 * @author <a href="mailto:robert.hodges@continuent.com">Robert Hodges</a>
 */
public class CsvWriter
{
  protected PrintStream out;
  int colIndex;
  boolean open;
  
  /**
   * Constructs a new CsvWriter object. It opens the specified file 
   * for writing.
   * 
   * @param path the path name for the output file
   * @throws EvaluatorException is thrown when the specifed file
   *    could not be opened for writing.
   */
  public CsvWriter(String path) throws EvaluatorException
  {
    File file = new File(path);
    try
    {
      out = new PrintStream(new FileOutputStream(file));
    }
    catch (FileNotFoundException e)
    {
      throw new EvaluatorException("Could not create CSV file: " + path, e);
    }
  }
  
  /** 
   * Starts the CSV header. 
   */
  public void startHeader()
  {
    colIndex = 0;
  }

  /**
   * Closes the CSV header. 
   */
  public void endHeader()
  {
    out.println();
  }
  
  /** 
   * Adds a header title. 
   */
  public void addHeader(String name)
  {
    if (colIndex++ > 0)
    {
      out.print(", ");
    }
    out.print("\"" + name + "\"");
  }

  /** 
   * Starts a data row. 
   */
  public void startData()
  {
    colIndex = 0;
  }

  /**
   * Closes the data row. 
   */
  public void endData()
  {
    out.println();
  }
  
  /** 
   * Adds a data value. 
   */
  public void addData(String value)
  {
    if (colIndex++ > 0)
    {
      out.print(", ");
    }
    String value2 = value.replace("\"", " ");
    out.print("\"" + value2 + "\"");
  }
  
  /** 
   * Adds a data value. 
   */
  public void addData(int value)
  {
    if (colIndex++ > 0)
    {
      out.print(", ");
    }
    out.print(value);
  }
  
  /**
   * Closes the file. 
   */
  public void close()
  {
    out.close();
  }
}