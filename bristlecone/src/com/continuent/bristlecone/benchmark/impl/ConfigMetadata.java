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

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.continuent.bristlecone.benchmark.BenchmarkException;
import com.continuent.bristlecone.benchmark.Scenario;

/**
 * Metadata for entire tuple.  Includes metadata for each individual property. 
 * 
 * @author rhodges
 */
public class ConfigMetadata
{
  private static Logger logger = Logger.getLogger(ConfigMetadata.class);

  Map<String, ConfigPropertyMetadata> metadata;
  
  /** Creates a new instance. */
  public ConfigMetadata()
  {
    metadata = new TreeMap<String, ConfigPropertyMetadata>();
  }
  
  /** 
   * Load metadata definitions from the scenario properties file and add
   * default metadata definitions used by framework. Also located setters for 
   * setting metadata in scenario classes. 
   * 
   * @param properties
   */
  public void initialize(Properties properties, Class scenarioClass)
  {
    // Scan the properties file and add metadata for any property key we find. 
    Enumeration keys = properties.propertyNames();
    while (keys.hasMoreElements())
    {
      String key = (String) keys.nextElement();
      addMetadata(key, null, false, false, null); 
    }
    
    // Add defaults for standard metadata properties, if they are not present.  
    addMetadataIfAbsent("scenario", "interations", false, false, null);
    addMetadataIfAbsent("bound", "iterations", false, false, null);
    addMetadataIfAbsent("iterations", "1", false, false, null);
    addMetadataIfAbsent("duration", "1", false, false, null);
    addMetadataIfAbsent("threads", "1", false, false, null);

    addMetadataOrFail("actualDuration", "-1", true);
    addMetadataOrFail("actualIterations", "-1", true);
    addMetadataOrFail("actualAvgDuration", "0.0", true);
    addMetadataOrFail("actualAvgOpsSec", "0.0", true);
    addMetadataOrFail("actualSQLExceptions", "0", true);
    addMetadataOrFail("actualOtherExceptions", "0", true);
    
    // Find setters for properties on Scenario class. 
    Iterator<String> metadataNames = propertyNames();
    while (metadataNames.hasNext())
    {
      String name = metadataNames.next();
      ConfigPropertyMetadata cpm = getPropertyMetadata(name);
      if (cpm.isOutput())
        continue;

      // Construct setter name. 
      StringBuffer setterNameBuffer = new StringBuffer();
      setterNameBuffer.append("set");
      setterNameBuffer.append(Character.toUpperCase(name.charAt(0)));
      if (name.length() > 1)
        setterNameBuffer.append(name.substring(1));
      String setterName = setterNameBuffer.toString();
      
      // Find a setter on the scenario class if it exists. 
      Method setter = findSetterMethod(scenarioClass, setterName);
      if (setter != null)
      {
        cpm.setSetter(setter);
      }
    }
  }

  protected void addMetadataOrFail(String name, String defaultValue, boolean output)
  {
    boolean added = addMetadataIfAbsent(name, defaultValue, output, false, null);
    if (! added)
      throw new BenchmarkException("Could not add property metadata for reserved key; ensure "
          + "this key is not used in scenario properties: key=" + name);
  }

  /**
   * Find a setter method that matches the return type, has a single argument, and 
   * a void return value. 
   */
  protected Method findSetterMethod(Class c, String name)
  {
    logger.debug("Looking for setter method: class=" + c + " name=" + name);
    Method[] methods = c.getMethods();
    for (Method m: methods)
    {
      if (! m.getName().equals(name))
        continue;
      /*
      else if (! (m.getReturnType() == Void.class))
        continue;
      */
      else if (m.getParameterTypes().length != 1)
        continue;
      
      return m; 
    }
    return null;
  }

  /** Adds property metadata one element at a time. */
  public void addMetadata(String name, String defaultValue, 
      boolean output, boolean variable, Method setter)
  {
    if (logger.isDebugEnabled())
    {
      logger.debug("Adding metadata for property: name=" + name
          + " defaultValue=" + defaultValue + " output=" + output
          + " variable=" + variable + " setter=" + setter);
    }
    ConfigPropertyMetadata cpm = new ConfigPropertyMetadata();
    cpm.setName(name);
    cpm.setDefaultValue(defaultValue);
    cpm.setOutput(output);
    cpm.setVariable(variable);
    cpm.setSetter(setter);
    metadata.put(cpm.getName(), cpm);
  }
  
  /** 
   * Adds property metadata only if the named value does not already exist.  
   * 
   * @return True if the metadata were added
   */
  public synchronized boolean addMetadataIfAbsent(String name, String defaultValue, 
      boolean output, boolean variable, Method setter)
  {
    if (metadata.get(name) == null)
    {
      addMetadata(name, defaultValue, output, variable, setter);
      return true;
    }
    else 
      return false;
  }

  /** Returns a sorted list of property names. */
  public Iterator<String> propertyNames()
  {
    return metadata.keySet().iterator();
  }

  /** 
   * Fetches property metdata with assertion checking to ensure we are not 
   * getting a null pointer.  This can be used to fetch metadata either to 
   * read it or to write it. 
   */
  public ConfigPropertyMetadata getPropertyMetadataAsserted(String name)
  {
    ConfigPropertyMetadata cpm = metadata.get(name);
    if (cpm == null)
      throw new BenchmarkException("Attempt to access non-existent property metadata:  key=" 
          + name);
    return cpm;
  }

  /** 
   * Fetches property metadata without null assertion checking. 
   */
  public ConfigPropertyMetadata getPropertyMetadata(String name)
  {
    return metadata.get(name);
  }
  
  /** 
   * Invokes scenario class setter methods with actual property values. 
   */
  public void setProperties(Properties scenarioProps, Scenario scenario)
  {
    Enumeration keys = scenarioProps.propertyNames();
    while (keys.hasMoreElements())
    {
      String key = (String) keys.nextElement();
      ConfigPropertyMetadata cpm = getPropertyMetadataAsserted(key);
      Method setter = cpm.getSetter();
      if (setter != null)
      {
        String value = scenarioProps.getProperty(key);
        if (logger.isDebugEnabled())
        {
          logger.debug("Setting scenario argument: key=" + key + " value=" + value);
        }

        Object arg = null;
        Class[] argTypes = setter.getParameterTypes();
        Class arg0Type = argTypes[0];
        if (arg0Type == String.class)
          arg = value;
        else if (arg0Type == Integer.TYPE)
          arg = new Integer(value);
        else if (arg0Type == Long.TYPE)
          arg = new Long(value);
        else if (arg0Type == Boolean.TYPE)
          arg = new Boolean(value);
        else if (arg0Type == Character.TYPE)
          arg = new Character(value.charAt(0));
        else if (arg0Type == Float.TYPE)
          arg = new Float(value);
        else if (arg0Type == Double.TYPE)
          arg = new Double(value);
        else
          throw new BenchmarkException("Unsupported property type: key=" + key
              + " type=" + arg0Type);
        
        // Now set the value. 
        try
        {
          setter.invoke(scenario, new Object[] {arg});
        }
        catch (Exception e)
        {
          throw new BenchmarkException("Unable to set property: key=" + key
              + " value = " + value, e);
        }
      }
    }
  }
}