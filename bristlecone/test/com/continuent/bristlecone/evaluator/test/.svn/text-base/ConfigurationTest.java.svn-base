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

package com.continuent.bristlecone.evaluator.test;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import com.continuent.bristlecone.evaluator.Configuration;
import com.continuent.bristlecone.evaluator.EvaluatorException;
import com.continuent.bristlecone.evaluator.TableGroup;
import com.continuent.bristlecone.evaluator.ThreadConfiguration;
import com.continuent.bristlecone.evaluator.XMLWriter;

public class ConfigurationTest extends TestCase
{
  File      testFile;
  HashMap<String, Object>   evalAtts = new HashMap<String, Object>();
  HashMap<String, Object>   dbAtts   = new HashMap<String, Object>();
  ArrayList<HashMap<String, Object>> tbls     = new ArrayList<HashMap<String, Object>>();

  protected void setUp() throws Exception
  {
    super.setUp();
    evalAtts.put("name", getName());
    evalAtts.put("testDuration", "11");
    evalAtts.put("autoCommit", "false");
    evalAtts.put("xmlFile", "xml");
    evalAtts.put("htmlFile", "html");
    evalAtts.put("statusInterval", "7");

    dbAtts.put("url", "jdbc:pcluster://node1,node2/test");
    dbAtts.put("driver", "com.continuent.pcluster.driver.Driver");
    dbAtts.put("user", "user");
    dbAtts.put("password", "pw");

    HashMap<String, Object> tblAtts = new HashMap<String, Object>();
    tblAtts.put("name", "tg1");
    tblAtts.put("size", "111");
    ArrayList<HashMap<String, String>> threads = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> tdAtts = new HashMap<String, String>();
    tdAtts.put("name", "td1");
    tdAtts.put("readSize", "1");
    tdAtts.put("deletes", "1");
    tdAtts.put("inserts", "2");
    tdAtts.put("updates", "3");
    tdAtts.put("thinkTime", "234");
    tdAtts.put("rampUpInterval", "11");
    tdAtts.put("rampUpIncrement", "7");
    threads.add(tdAtts);
    tblAtts.put("threads", threads);
    tbls.add(tblAtts);
  }

  protected void tearDown() throws Exception
  {
    if (testFile != null)
    {
      testFile.delete();
    }
    super.tearDown();
  }

  public void testNoXml() throws Exception
  {
    try
    {
      Configuration test = new Configuration("fileDoesNotExist");
      fail("Created configuration from none existent xml file "
          + test.getName());
    }
    catch (EvaluatorException e)
    {
      assertTrue(e.getMessage().startsWith("Could not read the configuration file"));
    }

  }
  
  public void testAllValues() throws Exception
  {
    File xmlFile = createXml();
    Configuration test1 = new Configuration(xmlFile);
    assertAllValues(test1);
    
    FileReader reader = new FileReader(xmlFile);
    Configuration test2 = new Configuration(reader);
    assertAllValues(test2);
  }
  
  private void assertAllValues(Configuration test) throws Exception
  {
    assertEquals(getName(), test.getName());
    assertEquals(11, test.getTestDuration());
    assertEquals(false, test.isAutoCommit());
    assertEquals("xml", test.getXmlFile());
    assertEquals(7, test.getStatusInterval());
    assertEquals("jdbc:pcluster://node1,node2/test", test.getUrl());
    assertEquals("com.continuent.pcluster.driver.Driver", test.getDriver());
    assertEquals("user", test.getUser());
    assertEquals("pw", test.getPassword());
    TableGroup tg = (TableGroup)test.getTableGroups().get(0);
    ThreadConfiguration td = (ThreadConfiguration)tg.getThreads().get(0);
    assertEquals("tg1", tg.getTableName());
    assertEquals(111, tg.getTableSize());
    assertEquals("td1", td.getName());
    assertEquals(1, td.getReadSize());
    assertEquals(1, td.getDeletePercentage());
    assertEquals(2, td.getInsertPercentage());
    assertEquals(3, td.getUpdatePercentage());
    assertEquals(234, td.getThinkTime());
    assertEquals(11, td.getRampUpInterval());
    assertEquals(7, td.getRampUpIncrement());
  }

  public void testBadXml() throws Exception
  {
    try
    {
      tbls = new ArrayList<HashMap<String, Object>>();
      Configuration test = new Configuration(createXml());
      fail("Created configuration from bad xml file " + test.getName());
    }
    catch (EvaluatorException e)
    {
      assertEquals("Invalid Configuration", e.getMessage());
    }
  }
  
  public void testBadValue() throws Exception
  {
    try
    {
      HashMap<String, Object> tbl = tbls.get(0);
      tbl.put("size", "NotANumber");
      Configuration test = new Configuration(createXml());
      fail("Created configuration from bad xml file " + test.getName());
    }
    catch (EvaluatorException e)
    {
      assertTrue(e.getMessage().startsWith("Invalid configuration file"));
    }
  }

  public void testBadBooleanValue() throws Exception
  {
    try
    {
      evalAtts.put("autoCommit", "maybe");
      Configuration test = new Configuration(createXml());
      fail("Created configuration from bad xml file " + test.getName());
    }
    catch (EvaluatorException e)
    {
      assertEquals("Invalid Configuration", e.getMessage());
    }
  }
  
  public void testDefaultConfig() throws Exception
  {
    evalAtts = new HashMap<String, Object>();
    evalAtts.put("name", getName());
    tbls = new ArrayList<HashMap<String, Object>>();
    HashMap<String, Object> tbl = new HashMap<String, Object>();
    tbls.add(tbl);
    tbl.put("name", "tableGroup");

    List<HashMap<String, String>> tds = new ArrayList<HashMap<String, String>>();
    tbl.put("threads", tds);
    HashMap<String, String> td = new HashMap<String, String>();
    tds.add(td);
    td.put("name", "threadGroup");
    File f = createXml();
    Configuration test = new Configuration(f);
    assertEquals(test.getTestDuration(), 10);
    assertTrue(test.isAutoCommit());
    assertEquals(test.getXmlFile(), "");
    assertEquals(test.getStatusInterval(), 2);
  }


  private void addAttribute(XMLWriter w, String id, HashMap<String, Object> values)
  {
    String v = (String) values.get(id);
    if (v != null)
    {
      w.addAttribute(id, v);
    }
  }

  private File createXml() throws Exception
  {
    testFile = File.createTempFile(getName(), ".xml");
    XMLWriter w = new XMLWriter(testFile.getPath());
    w.setSystem("EvaluatorConfiguration", "file:///foo");
    w.startTag("EvaluatorConfiguration");
    addAttribute(w, "name", evalAtts);
    addAttribute(w, "testDuration", evalAtts);
    addAttribute(w, "statusInterval", evalAtts);
    addAttribute(w, "xmlFile", evalAtts);
    addAttribute(w, "autoCommit", evalAtts);
    w.startTag("Database");
    addAttribute(w, "url", dbAtts);
    addAttribute(w, "driver", dbAtts);
    addAttribute(w, "user", dbAtts);
    addAttribute(w, "password", dbAtts);
    w.endTag(); // Database
    for (Iterator<HashMap<String, Object>> tbli = tbls.iterator(); tbli.hasNext();)
    {
      w.startTag("TableGroup");
      HashMap<String, Object> tbl = (HashMap<String, Object>) tbli.next();
      addAttribute(w, "name", tbl);
      addAttribute(w, "size", tbl);
      @SuppressWarnings("unchecked")
      List<HashMap<String, Object>> l = (List<HashMap<String, Object>>) tbl.get("threads");
      for (Iterator<HashMap<String, Object>> tdi = l.iterator(); tdi.hasNext();)
      {
        w.startTag("ThreadGroup");
        HashMap<String, Object> td = (HashMap<String, Object>) tdi.next();
        addAttribute(w, "name", td);
        addAttribute(w, "readSize", td);
        addAttribute(w, "deletes", td);
        addAttribute(w, "inserts", td);
        addAttribute(w, "updates", td);
        addAttribute(w, "thinkTime", td);
        addAttribute(w, "rampUpInterval", td);
        addAttribute(w, "rampUpIncrement", td);
        w.endTag(); // ThreadGroup
      }
      w.endTag(); // TableGroup
    }
    w.endTag(); // EvaluatorConfiguration

    return testFile;
  }
}
