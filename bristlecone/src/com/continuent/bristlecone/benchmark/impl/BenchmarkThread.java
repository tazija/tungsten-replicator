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

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.continuent.bristlecone.benchmark.BenchmarkException;
import com.continuent.bristlecone.benchmark.Scenario;

/**
 * @author rhodges
 *
 */
public class BenchmarkThread extends Thread
{
  private static Logger logger = Logger.getLogger(BenchmarkThread.class);
  
  // Scenario and configuration properties. 
  protected ConfigWrapper wrapper;
  protected Scenario scenario;
  
  // Properties set by test and harvested later. 
  protected long elapsed = 0;
  protected long iterationCount = 0;
  protected long sqlExceptionCount = 0;
  protected Exception exception; 
  
  /**
   * Creates a new thread using the indicated config. 
   */
  public BenchmarkThread(String name, Scenario scenario, 
      ConfigWrapper wrapper)
  {
    super(name);
    this.scenario = scenario;
    this.wrapper = wrapper;
  }

  public long getElapsed()
  {
    return elapsed;
  }

  public Exception getException()
  {
    return exception;
  }

  public long getIterationCount()
  {
    return iterationCount;
  }
  
  public long getSqlExceptionCount()
  {
    return sqlExceptionCount;
  }

  /** Initialize the thread.  */
  public void prepare() throws BenchmarkException
  {
    try {
      scenario.prepare();
    }
    catch (Exception e)
    {
      String msg = "Scenario init() method call failed: " + scenario.getClass();
      logger.debug(msg, e);
      throw new BenchmarkException(msg, e);
    }
  }
  
  /** Run operations until we are done. */
  public void run()
  {
    if (logger.isDebugEnabled())
      logger.debug("Starting thread: " + Thread.currentThread().getName());
    
    long start = System.currentTimeMillis();
    long end; 
    iterationCount = 0;
    try
    {
      String boundType = wrapper.getBound();
      if (ConfigWrapper.METHOD_ITERATIONS.equals(boundType))
      {
        long testIterations = wrapper.getIterations();
        if (logger.isDebugEnabled())
        {
          logger.debug("Running scenario using iterations:  iterations=" + testIterations);
        }
        while (iterationCount < testIterations)
        {
          iterationCount++;
          logger.debug("Invoking next iteration, count=" + iterationCount);
          
          try
          {
            scenario.iterate(iterationCount);
          }
          catch (SQLException e)
          {
            this.sqlExceptionCount++;
            if (logger.isDebugEnabled())
              logger.debug("Caught SQLException in scenario", e); 
          }
        }
        logger.debug("Iteration count exceeded; terminating iterations");
      }
      else if (ConfigWrapper.METHOD_DURATION.equals(boundType))
      {
        long testDurationMillis   = wrapper.getDuration() * 1000;
        if (logger.isDebugEnabled())
        {
          logger.debug("Running scenario using duration in seconds:  duration=" 
              + testDurationMillis / 1000);
        }
        end = System.currentTimeMillis();
        while ((end - start) < testDurationMillis)
        {
          iterationCount++;
          logger.debug("Invoking next iteration, count=" + iterationCount);
          try
          {
            scenario.iterate(iterationCount);
          }
          catch (SQLException e)
          {
            this.sqlExceptionCount++;
            if (logger.isDebugEnabled())
              logger.debug("Caught SQLException in scenario", e); 
          }
          end = System.currentTimeMillis();
        }
        logger.debug("Time limit exceeded; terminating iterations");
      }
      else
        throw new BenchmarkException("Unrecognized bound type: " + boundType);
    }
    catch (Exception e)
    {
      logger.error("Scenario thread " + Thread.currentThread().getName() 
          + " failed with exception", e);
      exception = e;
    }
    end = System.currentTimeMillis();
    this.elapsed = end - start;
    
    if (logger.isDebugEnabled())
      logger.debug("Ending thread: " + Thread.currentThread().getName());
  }

  /** Clean up. */
  public void cleanup() throws BenchmarkException
  {
    try {
      scenario.cleanup();
    }
    catch (Exception e)
    {
      String msg = "Scenario cleanup() method call failed: " + scenario.getClass();
      logger.debug(msg, e);
      throw new BenchmarkException(msg, e);
    }
  }
}
