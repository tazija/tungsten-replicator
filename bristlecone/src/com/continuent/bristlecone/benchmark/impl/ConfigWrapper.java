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

package com.continuent.bristlecone.benchmark.impl;

import java.util.Properties;


/**
 * Wrapper class to provide typesafe access to benchmark configuration 
 * data. 
 * 
 * @author rhodges
 */
public class ConfigWrapper
{
  public static final String METHOD_ITERATIONS = "iterations";
  public static final String METHOD_DURATION   = "duration";

  protected Config configData; 

  /**
   * A new instance. 
   */
  public ConfigWrapper(Config config)
  {
    this.configData = config;
  }
  
  /**
   * Returns underlying configuration data. 
   */
  public Config getConfigurationData()
  {
    return configData;
  }

  /** 
   * Returns the scenario class name. 
   */
  public String getScenarioClass()
  {
    return configData.getProperty("scenario");
  }
  
  /**
   * Returns the monitor class name or null if not specified. 
   */
  public String getMonitorClass()
  {
    return configData.getProperty("monitor");
  }

  /** 
   * Returns the method for bounding runs, which is either "duration" 
   * or "iterations"
   */
  public String getBound()
  {
    return configData.getProperty("bound");
  }

  /** Return the number of iterations or 0 if unspecified. */
  public long getIterations()
  {
    return configData.getPropertyAsLong("iterations");
  }

  
  /** Return the duration in seconds or 0 if unspecified. */
  public long getDuration()
  {
    return configData.getPropertyAsLong("duration");
  }

  /** Return the number of threads. */
  public long getThreads()
  {
    return configData.getPropertyAsLong("threads");
  }  
  
  /** Set the actual duration of the run. */
  public void setActualDuration(double duration)
  {
    configData.setProperty("actualDuration", duration);
  }
  
  /** Set the actua number of iterations in the run. */
  public void setActualIterations(long iterations)
  {
    configData.setProperty("actualIterations", iterations);
  }

  /** Set the average duration per operation. */
  public void setActualAvgDuration(double avgDuration)
  {
    configData.setProperty("actualAvgDuration", avgDuration);
  }
  
  /** Set the average number of operations per second. */
  public void setActualAvgOpsSecond(double avgOpsSec)
  {
    configData.setProperty("actualAvgOpsSec", avgOpsSec);
  }
  
  /** Set the actual number of SQL exceptions during the run. */
  public void setActualSQLExceptions(long sqlExceptions)
  {
    configData.setProperty("actualSQLExceptions", sqlExceptions);
  }

  /** Set the actual number of non-SQL exceptions during the run. */
  public void setActualOtherExceptions(long otherExceptions)
  {
    configData.setProperty("actualOtherExceptions", otherExceptions);
  }
  
  /** Returns a Properties instance containing current underlying values. */
  public Properties getProperties()
  {
    return configData.toProperties();
  }
}