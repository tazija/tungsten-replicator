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

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import com.continuent.bristlecone.benchmark.BenchmarkException;

/**
 * Implements a specialized class similar to a Properties object in order
 * to hold configuration data and results for benchmarks.  This 
 * class has the following special capabilities: 
 * <li>
 * <ol>The class is fully thread-safe.  All accesses to property data are 
 *     synchronized.</ol>
 * <ol>The class provides metadata on stored properties to aid output.</ol>
 * </li>
 * One instance of this class is generated for each benchmark test case.  
 * 
 * @author rhodges
 */
public class Config
{
  private SortedMap<String, String> synchronizedMap;
  private ConfigMetadata metadata;

  /** Creates a new instance from an ordinary properties object. */
  public Config(Properties properties, ConfigMetadata metadata)
  {
    // Load properties.  Looping seems to be only typesafe way to do so 
    // as of JDK 1.5. 
    SortedMap<String,String> propertyMap = new TreeMap<String, String>();
    Iterator iter = properties.keySet().iterator(); 
    while (iter.hasNext())
    {
      String key = (String) iter.next();
      propertyMap.put(key, properties.getProperty(key));
    }
    init(propertyMap, metadata);
  }
  
  /** Creates a new instance from an arbitrary Map instance. */
  public Config(Map<String, String> map, ConfigMetadata metadata)
  {
    SortedMap<String, String> propertyMap = new TreeMap<String, String>(map);
    init(propertyMap, metadata);
  }
  
  /** 
   * Initializes the properties by supplying defaults and 
   * ensuring all properties have metadata values. 
   */
  private void init(SortedMap<String, String> propertyMap, ConfigMetadata metadata) 
    throws BenchmarkException
  {
    // Store instance variables. 
    synchronizedMap = Collections.synchronizedSortedMap(propertyMap);
    this.metadata = metadata;
    
    // Assign default values for any missing properties. 
    Iterator<String> names = metadata.propertyNames();
    while (names.hasNext())
    {
      String name = names.next();
      if (synchronizedMap.get(name) == null)
        synchronizedMap.put(name, metadata.getPropertyMetadata(name).getDefaultValue());
    }
  }
  
  /** Returns metadata on the stored properties. */
  public ConfigMetadata getMetadata()
  {
    return metadata;
  }
  
  /** Returns a synchronized copy of the underlying map. */
  public SortedMap getMap()
  {
    return synchronizedMap;
  }
  
  /** Returns a sorted list of property names. */
  public Iterator<String> propertyNames()
  {
    return synchronizedMap.keySet().iterator();
  }

  /** Returns a simple property value. */
  public String getProperty(String name)
  {
    return synchronizedMap.get(name);
  }
  
  /** Returns a property value as a string with optional default.  */
  public String getProperty(String name, String defaultValue)
  {
    String value = synchronizedMap.get(name);
    if (value == null)
      return defaultValue;
    else 
      return value;
  }

  /** Read a property value and return converted long value. */
  public long getPropertyAsLong(String name)
  {
    String s = getProperty(name);
    return Long.parseLong(s);
  }
  
  /** Read a property value and return converted integer value. */
  public int getPropertyAsInt(String name)
  {
    String s = getProperty(name);
    return Integer.parseInt(s);
  }
  
  /** Read a property value and return as converted double value. */
  public double getPropertyAsDouble(String name)
  {
    String s = getProperty(name);
    return Double.parseDouble(s);
  }

  /** Sets a string property. */
  public void setProperty(String name, String value)
  {
    synchronizedMap.put(name, value);
  }
  
  /** Sets an int property. */
  public void setProperty(String name, int value)
  {
    synchronizedMap.put(name, new Integer(value).toString());
  }
  
  /** Sets a double property. */
  public void setProperty(String name, double value)
  {
    synchronizedMap.put(name, new Double(value).toString());
  }
  
  /**
   * Returns a Properties instance derived from this instance. 
   */
  public Properties toProperties()
  {
    Properties p = new Properties();
    Iterator<String> propertyNames = this.propertyNames();
    while (propertyNames.hasNext())
    {
      String name = propertyNames.next();
      p.setProperty(name, getProperty(name));
    }
    return p;
  }
}