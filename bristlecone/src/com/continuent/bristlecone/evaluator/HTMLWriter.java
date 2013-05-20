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

public class HTMLWriter extends XMLWriter
{

  public HTMLWriter(String path, String title) throws EvaluatorException
  {
    super(path);
    startTag("HTML");
    addTitle(title);
  }

  private void addTitle(String title)
  {
    startTag("HEAD");
    startTag("TITLE");
    addData(title);
    endTag();
    endTag();
    startTag("BODY");
  }
  public void startTable()
  {
    startTag("TABLE");
    addAttribute("border", 1);
  }
  
  private void closeData()
  {
    if (tags.peek().equals("TD") || tags.peek().equals("TH"))
    {
      endTag();
    }
  }
  private void closeRow()
  {
    closeData();
    if (tags.peek().equals("TR"))
    {
      endTag();
    }
  }
  
  public void addTableRow()
  {
    closeRow();
    startTag("TR");
  }
  
  public void addTableData(String data)
  {
    closeData();
    startTag("TD");
    super.addData(data);
  }
  public void addTableData(int data)
  {
    closeData();
    startTag("TD");
    addAttribute("align", "right");
    super.addData("" + data);
  }
  public void addTableHead(String label)
  {
    closeData();
    startTag("TH");
    super.addData(label);
  }
  public void endTable()
  {
    closeRow();
    endTag(); 
  }
  public void close()
  {    
    endTag(); //body
    endTag(); //html
  }
}
