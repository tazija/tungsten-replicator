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

import org.apache.log4j.Logger;

import com.continuent.bristlecone.benchmark.BenchmarkException;

/**
 * Utility methods used in benchmark processing.  
 * 
 * @author rhodges
 */
public class Utilities
{
  private static Logger logger = Logger.getLogger(Utilities.class);

  // Load a class with appropriate logging and failure handling.
  public static Class<?> loadClass(String name)
  {
    logger.debug("Loading class: " + name);
    Class<?> c = null;
    try
    {
      c = Class.forName(name);
    }
    catch (Exception e)
    {
      String msg = "Unable to load class; see log for more details: " + name;
      logger.debug(msg, e);
      throw new BenchmarkException(msg, e);
    }
    return c;
  }

  // Instantiate instance with appropriate error checking. 
  public static Object instantiateClass(Class<?> c) throws BenchmarkException
  {
    try
    {
      return c.newInstance();
    }
    catch (IllegalAccessException e)
    {
      String msg = "Class default constructor is not accessible: "
            + c.getClass();
      throw new BenchmarkException(msg, e);
    }
    catch (InstantiationException e)
    {
      String msg = "Unable to instantiate class: "
            + c.getClass();
      throw new BenchmarkException(msg, e);
    }
    catch (Exception e)
    {
      String msg = "Unable to initialize class: "
            + c.getName();
      throw new BenchmarkException(msg, e);
    }
  }
}
