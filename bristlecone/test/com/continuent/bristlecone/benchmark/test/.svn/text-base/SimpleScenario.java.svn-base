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

package com.continuent.bristlecone.benchmark.test;

import java.util.Properties;

import com.continuent.bristlecone.benchmark.ScenarioAdapter;

/** 
 * Most basic scenario with counters for required instance methods. 
 *   
 * @author rhodges
 */
public class SimpleScenario extends ScenarioAdapter
{
  public static int calledInitialize = 0;
  public static int calledPrepare = 0;
  public static int calledIterate = 0;
  public static int calledCleanup = 0;
  
  private String simple; 

  public static synchronized void clearCounters()
  {
    calledInitialize = 0;
    calledPrepare = 0;
    calledIterate = 0;
    calledCleanup = 0;
  }

  public SimpleScenario()
  {
    super();
  }

  public void setSimple(String simple)
  {
    this.simple = simple; 
  }
  
  public String getSimple()
  {
    return simple;
  }
  
  public void initialize(Properties properties) throws Exception
  {
    calledInitialize++;
  }
  
  public void prepare() throws Exception
  {
    calledPrepare++;
  }

  public void cleanup() throws Exception
  {
    calledCleanup++;
  }

  public void iterate(long iterationCount) throws Exception
  {
    calledIterate++;
  }
}