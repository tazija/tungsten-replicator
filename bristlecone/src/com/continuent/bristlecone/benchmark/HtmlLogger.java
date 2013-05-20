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
 * Implements a logger that prints benchmark results in a nice HTML format. 
 * 
 * @author rhodges
 */
public class HtmlLogger implements ResultLogger
{
  private static Logger logger = Logger.getLogger(HtmlLogger.class);  

  private PrintStream htmlOut = null;
  private boolean printedHeader = false;
  
  private static String STYLES = "body {\n"
  + "    font:normal 68% verdana,arial,helvetica;\n"
  + "    color:#000000;\n"
  + "}\n"
  + "table tr td, table tr th {\n"
  + "    font-size: 68%;\n"
  + "}\n"
  + "table.details tr th{\n"
  + "    font-weight: bold;\n"
  + "    text-align:left;\n"
  + "    background:#a6caf0;\n"
  + "}\n"
  + "table.details tr td{\n"
  + "    background:#eeeee0;\n"
  + "}\n"
  + "h1 {\n"
  + "    margin: 0px 0px 5px; font: 165% verdana,arial,helvetica\n"
  + "}\n"
  + "h2 {\n"
  + "    margin-top: 1em; margin-bottom: 0.5em; font: bold 125% verdana,arial,helvetica\n"
  + "}\n";
  
  /** Creates a new logger. */
  public HtmlLogger(String outputFileName)
  {
    File htmlFile = new File(outputFileName);
    logger.info("Writing HTML output to file: " + htmlFile.toString());
    if (htmlFile.exists())
    {
      logger.info("Deleting previous file");
      htmlFile.delete();
    }

    try
    {
      this.htmlOut = new PrintStream(new FileOutputStream(htmlFile, true));
    }
    catch (FileNotFoundException e)
    {
      String msg = "Unable to open HTML report file: " + htmlFile.getAbsolutePath();
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
  public void resultGenerated(Config config)
  {
    ConfigMetadata metadata = config.getMetadata();

    // Print header with fixed fields the first time through. 
    if (! printedHeader)
    {
      htmlOut.println("<html>");
      htmlOut.println("<head>");
      htmlOut.println("<title>Benchmark Report</title>");
      htmlOut.println("<style type=\"text/css\">");
      htmlOut.println(STYLES);
      htmlOut.println("</style>");
      htmlOut.println("</head>");
      htmlOut.println("<body>");
      htmlOut.println("<h1>Benchmark Test Report</h1>");
      
      // Write Values for fixed fields.
      htmlOut.println("<h2>Fixed Benchmark Configuration Values</h2>");
      htmlOut.println("<table class=\"details\" border=\"0\" cellpadding=\"5\" cellspacing=\"2\" width=\"95%\">");
      htmlOut.println("<tr>");
      htmlOut.println("<th align=\"left\">Name</th><th>Value</th>");
      htmlOut.println("</tr>");
      
      Iterator<String> iter = metadata.propertyNames();
      while (iter.hasNext())
      {
        String name = iter.next();
        ConfigPropertyMetadata cpm = metadata.getPropertyMetadataAsserted(name);
        if (! cpm.isVariable() && ! cpm.isOutput())
        {
          htmlOut.println("<tr>");
          htmlOut.println("<td>" + name + "</td><td>" + config.getProperty(name) + "</td>");
          htmlOut.println("</tr>");
        }
      }
      htmlOut.println("</table>");

      // Print header row for remaining properties. 
      htmlOut.println("<h2>Benchmark Runs</h2>");
      htmlOut.println("<table class=\"details\" border=\"0\" cellpadding=\"5\" cellspacing=\"2\" width=\"95%\">");
      htmlOut.println("<tr>");

      iter = metadata.propertyNames();
      while (iter.hasNext())
      {
        String name = iter.next();
        ConfigPropertyMetadata cpm = metadata.getPropertyMetadataAsserted(name);
        if (cpm.isVariable() || cpm.isOutput())
        {
          htmlOut.println("<th align=\"left\">" + name + "</th>");
        }
      }
      htmlOut.println("</tr>");
      
      printedHeader = true;
    }
    
    // Write data for non-fixed or output fields.   
    htmlOut.println("<tr>");
    Iterator<String> iter = metadata.propertyNames();
    while (iter.hasNext())
    {
      String name = iter.next();
      ConfigPropertyMetadata cpm = metadata.getPropertyMetadataAsserted(name);
      if (cpm.isVariable() || cpm.isOutput())
      {
        htmlOut.println("<td>" + config.getProperty(name) + "</td>");
      }
    }
    htmlOut.println("</tr>");
  }
  
  /* (non-Javadoc)
   * @see com.continuent.bristlecone.benchmark.BenchmarkResultLogger#cleanup()
   */
  public void cleanup()
  {
    htmlOut.println("</table>");
    htmlOut.println("</body>");
    htmlOut.println("</html>");
    htmlOut.close();
  }
}