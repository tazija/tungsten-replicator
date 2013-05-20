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
import java.util.Stack;

/**
 * Writes neatly formatted XML to the specified file.
 * 
 * @author <a href="mailto:ralph.hannus@continuent.com">Ralph Hannus</a>
 */
public class XMLWriter
{
  protected PrintStream out;
  protected Stack<String> tags = new Stack<String>();
  boolean open;
  int attrCount;
  
  /**
   * Constructs a new XMLWriter object. It opens the specied file 
   * for writing.
   * 
   * @param path the path name for the output file
   * @throws EvaluatorException is thrown when the specifed file
   *    could not be opened for writing.
   */
  public XMLWriter(String path) throws EvaluatorException
  {
    File file = new File(path);
    try
    {
      out = new PrintStream(new FileOutputStream(file));
    }
    catch (FileNotFoundException e)
    {
      throw new EvaluatorException("Could not create XML file: " + path, e);
    }
  }
  
  private void indent(int count)
  {
    for (int i = 0; i < count; i++)
    {
      out.print("    ");
    }
  }
  
  /**
   * Starts a new XML tag. If open tag tag exists, it is
   * closed.
   * 
   * @param tag
   */
  public void startTag(String tag)
  {
    if (open)
    {
      closeTag();
    }
    indent(tags.size());
    out.print("<");
    out.print(tag);
    tags.push(tag);
    open = true;
    attrCount =0;
  }
  
  private void closeTag()
  {
    out.println(">");
    open = false;
  }
  
  public void addData(String data)
  {
    if (open)
    {
      out.print(">");
      open = false;
    }
    out.print(data);
  }
  public void endTag()
  {

    String tag = (String)tags.pop();
    if (open)
    {
      out.println("/>");
      open = false;
    }
    else
    {
      indent(tags.size());
      out.print("</");
      out.print(tag);
      out.println(">");
    }
    if (tags.size() == 0)
    {
      out.close();
    }
  }
 
  public void addAttribute(String name, String value)
  {
    if (attrCount > 0 && (attrCount % 3 == 0))
    {
      out.println();
      indent(tags.size() + 1);
    }
    attrCount ++;
    out.print(" ");
    out.print(name);
    out.print("=\"");
    out.print(value);
    out.print("\"");
  }
  
  public void addAttribute(String name, int value)
  {
    if (attrCount > 0 && (attrCount % 3 == 0))
    {
      out.println();
      indent(tags.size() + 1);
    }
    attrCount ++;
    out.print(" ");
    out.print(name);
    out.print("=\"");
    out.print(value);
    out.print("\"");
  }

  public void setSystem(String root, String path)
  {
    out.print("<!DOCTYPE ");
    out.print(root);
    out.print(" SYSTEM \"");
    out.print(path);
    out.println("\" >");
    
  }
}
