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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.continuent.bristlecone.benchmark.impl.BenchmarkThread;
import com.continuent.bristlecone.benchmark.impl.Config;
import com.continuent.bristlecone.benchmark.impl.ConfigMetadata;
import com.continuent.bristlecone.benchmark.impl.ConfigPropertyMetadata;
import com.continuent.bristlecone.benchmark.impl.ConfigWrapper;
import com.continuent.bristlecone.benchmark.impl.PropertyManager;
import com.continuent.bristlecone.benchmark.impl.Utilities;

/**
 * This class defines a simple performance benchmark for SQL update and query
 * operations. It is designed to provide low-level measurements of latency for
 * commonly occurring operations.
 * <p>
 * The performance test is controlled by a set of properties that are defined in
 * the BenchmarkConfig class.
 * 
 * @author <a href="mailto:robert.hodges@continuent.com">Robert Hodges</a>
 * @version 1.0
 */
public class Benchmark
{
  private static Logger logger = Logger.getLogger(Benchmark.class);

  /** Name of properties definition for the test. */
  protected String props = "benchmark.properties";

  /** Name of the text report output file, if desired. */
  protected String textOutputFile = null;
  
  /** Name of the CSV output file, if desired. */
  protected String csvOutputFile = null;
  
  /** Name of the HTML output file, if desired. */
  protected String htmlOutputFile = null;
  
  /** List of loggers to be informed of test results. */
  List<ResultLogger> loggers = new ArrayList<ResultLogger>();

  /** Creates a new Benchmark instance. */
  public Benchmark()
  {
  }

  public void setProps(String props)
  {
    this.props = props;
  }
  
  public void setText(String text)
  {
    this.textOutputFile = text;
  }

  public void setCsv(String csv)
  {
    this.csvOutputFile = csv;
  }

  public void setHtml(String html)
  {
    this.htmlOutputFile = html;
  }

  /**
   * Runs a benchmark test. Control parameters must be set before this method is
   * called.
   * 
   * @throws BenchmarkException Thrown if there is an unrecoverable error
   */
  public void go() throws BenchmarkException
  {
    // Add loggers as specified by client.  
    if (textOutputFile != null)
      addLogger(new TextLogger(textOutputFile));
    if (this.csvOutputFile != null)
      addLogger(new CsvLogger(csvOutputFile));
    if (this.htmlOutputFile != null)
      addLogger(new HtmlLogger(htmlOutputFile));
    
    // Load the properties file. 
    PropertyManager pm = new PropertyManager();
    Properties scenarioProperties = pm.loadProperties(new File(props));

    // Find the scenario class and load it.  There currently can only be one
    // class for a scenario file. 
    logger.info("Checking scenario class");
    String scenarioClassName = scenarioProperties.getProperty("scenario");
    if (scenarioClassName == null)
    {
      throw new BenchmarkException("scenario property not found; cannot load scenario class!");
    }
    Class<?> scenarioClass = Utilities.loadClass(scenarioClassName);

    // Load the benchmark properties file and explode it out to a cross product
    // of property sets.
    Vector<String> splitPropertyNames = new Vector<String>();
    Vector<Properties> propertiesList = pm.propertiesCrossProduct(new File(props), 
        splitPropertyNames);
    
    // Use one of the "exploded" property files to define property metadata 
    // entries.  We have to use an exploded file to pull in properties from 
    // an included property file. 
    logger.info("Processing property files and metadata");
    ConfigMetadata metadata = new ConfigMetadata();
    metadata.initialize(propertiesList.elementAt(0), scenarioClass);
    
    // Add the split property names as dynamic properties.  This completes 
    // metadata preparation. 
    Iterator<String> splitNames = splitPropertyNames.iterator();
    while (splitNames.hasNext())
    {
      String splitName = splitNames.next();
      metadata.getPropertyMetadataAsserted(splitName).setVariable(true);
    }

    // Initialize loggers. 
    Iterator<ResultLogger> loggerIter = loggers.iterator();
    while (loggerIter.hasNext())
    {
      ResultLogger bLogger = loggerIter.next();
      logger.info("Initializing logger: " + bLogger.getClass().getName());
      bLogger.init(metadata);
    }
    
    // Iterate through each of the property files.
    Iterator<Properties> propertiesListIterator = propertiesList.iterator();
    while (propertiesListIterator.hasNext())
    {
      // Create the benchmark configuration with a wrapper. 
      Properties bProperties = propertiesListIterator.next();
      runBenchmark(metadata, bProperties);
    }

    // Call loggers to let them know the test is done. 
    loggerIter = loggers.iterator();
    while (loggerIter.hasNext())
    {
      ResultLogger bLogger = loggerIter.next();
      bLogger.cleanup();
    }
  }
  
  /** Runs a test. */
  public void runBenchmark(ConfigMetadata metadata, Properties bProperties)
  {
    // Setup for running a benchmark
    logger.info("+++++ Starting benchmark run +++++");
    Config config = new Config(bProperties, metadata);
    ConfigWrapper configWrapper = new ConfigWrapper(config);
    logger.info("Input variables: " + listVariableValues(metadata, bProperties));

    // Instantiate monitor class. 
    Monitor monitor = null;
    if (configWrapper.getMonitorClass() != null)
    {
        Class<?> monitorClass = Utilities.loadClass(configWrapper.getMonitorClass());
        monitor = (Monitor) Utilities.instantiateClass(monitorClass);
    }
    
    // Instantiate and set properties on all scenario instances. 
    logger.info("Instantiating and initializing " + configWrapper.getThreads()
        + " scenario instances");
    Class<?> scenarioClass = Utilities.loadClass(configWrapper.getScenarioClass());
    Scenario[] scenarioArray = new Scenario[(int) configWrapper.getThreads()];
    for (int i = 0; i < scenarioArray.length; i++)
    {
      Scenario scenarioInst = (Scenario) Utilities.instantiateClass(scenarioClass);
      metadata.setProperties(bProperties, scenarioInst);
      try
      {
        scenarioInst.initialize(bProperties);
      }
      catch (Exception e)
      {
        String msg = "Unable to initialize scenario class: "
            + scenarioClass.getName();
        throw new BenchmarkException(msg, e);
      }
      scenarioArray[i] = scenarioInst; 
    }
    
    // Prepare the monitor for operation. 
    if (monitor != null)
    {
        try
        {
            monitor.prepare(bProperties);
        }
        catch (Exception e)
        {
            throw new BenchmarkException("Unable to prepare monitor", e);
        }
    }
    
    // Call the global init method on a selected benchmark scenario. 
    logger.debug("Invoking globalPrepare method");
    try
    {
      scenarioArray[0].globalPrepare();
    }
    catch (Exception e)
    {
      throw new BenchmarkException("Global initialization failed", e);
    }

    // Prepare threads. 
    logger.info("Creating threads and invoking scenario prepare() methods");
    BenchmarkThread threadArray[] = new BenchmarkThread[(int) configWrapper.getThreads()];
    for (int i = 0; i < threadArray.length; i++)
    {
      String name = "PBenchmark-" + i;
      BenchmarkThread bt = new BenchmarkThread(name, scenarioArray[i], configWrapper);
      bt.prepare();
      threadArray[i] = bt;
    }

    // Start the monitor. 
    Thread monitorThread = null;
    if (monitor != null)
    {
        monitorThread = new Thread(monitor, "monitor");
        monitorThread.start();
    }
    
    // Start all threads.
    logger.debug("Starting threads");
    for (int i = 0; i < threadArray.length; i++)
    {
      threadArray[i].start();
    }

    // Start all threads and collect results when they are done.
    long actualIterations = 0;
    long actualSQLExceptions = 0;
    int actualOtherExceptions = 0;

    long start = System.currentTimeMillis();
    for (int i = 0; i < threadArray.length; i++)
    {
      BenchmarkThread pbt = threadArray[i];
      try
      {
        pbt.join();
      }
      catch (InterruptedException e)
      {
        logger.warn("Scenario interrupted while waiting for thread to complete", e);
      }

      Exception exception = pbt.getException();
      if (exception != null)
      {
        logger.warn("Thread ended with exception", exception);
      }

      // Add the various counts to the total. 
      actualIterations += pbt.getIterationCount();
      actualSQLExceptions += pbt.getSqlExceptionCount();
      if (exception != null)
      {
        actualOtherExceptions++;
      }
    }
    logger.info("Threads completed execution");

    // Terminate monitor using an interrupt.  This counts in the test 
    // duration, so if the monitor is doing something unrelated it needs
    // to stop quickly. 
    if (monitor != null)
    {
      try
      {
        monitorThread.interrupt();
        monitorThread.join();
        monitor.cleanup();
      }
      catch (InterruptedException e)
      {
        logger.warn("Monitor thread join operation was interrupted");
      }
      catch (Exception e)
      {
        throw new BenchmarkException("Unable to terminate monitor", e);
      }
    }
    
    // Compute and add result values to the config instance.  Note extra 
    // code to deal with durations that equate to 0 seconds. 
    double actualDuration = (System.currentTimeMillis() - start) / 1000;
    double actualAvgDuration = actualDuration / actualIterations;
    
    double actualAvgOpsSec; 
    if (actualDuration == 0)
      actualAvgOpsSec = Double.MAX_VALUE;
    else 
      actualAvgOpsSec = actualIterations / actualDuration;

    configWrapper.setActualIterations((int) actualIterations);
    configWrapper.setActualDuration(actualDuration);
    configWrapper.setActualAvgDuration(actualAvgDuration);
    configWrapper.setActualAvgOpsSecond(actualAvgOpsSec);
    configWrapper.setActualSQLExceptions(actualSQLExceptions);
    configWrapper.setActualOtherExceptions(actualOtherExceptions);
    logger.info("Output: " + listOutputValues(metadata, configWrapper.getProperties()));
          
    // Clean up the threads.
    logger.debug("Invoking scenario cleanup() methods");
    for (int i = 0; i < threadArray.length; i++)
    {
      threadArray[i].cleanup();
    }

    // Call the global cleanUp method on the scenario class.
    logger.debug("Invoking globalCleanup() method");
    try
    {
      scenarioArray[0].globalCleanup();
    }
    catch (Exception e)
    {
      logger.warn("Global cleanup failed", e);
    }

    // Call all loggers to give them results. 
    Iterator<ResultLogger> loggerIter = loggers.iterator();
    while (loggerIter.hasNext())
    {
      ResultLogger bLogger = loggerIter.next();
      bLogger.resultGenerated(config);
    }
    
    logger.info("----- Benchmark run complete -----");
  }
  
  /** Adds a logger, which will be invoked in order. */
  public void addLogger(ResultLogger logger)
  {
    loggers.add(logger);
  }
  
  /** Removes a logger if it is present in the current logger list. */
  public boolean removeLogger(ResultLogger logger)
  {
    return loggers.remove(logger);
  }

  /** 
   * Logs output values for a benchmark run. 
   */
  protected String listOutputValues(ConfigMetadata metadata, Properties properties)
  {
    StringBuffer sb = new StringBuffer();
    Iterator<String> iter = metadata.propertyNames();
    int index = 0;
    while (iter.hasNext())
    {
      String name = iter.next();
      ConfigPropertyMetadata cpm = metadata.getPropertyMetadataAsserted(name);
      if (cpm.isOutput())
      {
        if (index++ > 0)
          sb.append(" ");
        sb.append(name).append("=").append(properties.getProperty(name));
      }
    }
    return sb.toString();
  }

  /** 
   * Logs output values for a benchmark run. 
   */
  protected String listVariableValues(ConfigMetadata metadata, Properties properties)
  {
    StringBuffer sb = new StringBuffer();
    Iterator<String> iter = metadata.propertyNames();
    int index = 0;
    while (iter.hasNext())
    {
      String name = iter.next();
      ConfigPropertyMetadata cpm = metadata.getPropertyMetadataAsserted(name);
      if (cpm.isVariable())
      {
        if (index++ > 0)
          sb.append(" ");
        sb.append(name).append("=").append(properties.getProperty(name));
      }
    }
    return sb.toString();
  }

  //  sFind a method that matches the provided argument types or return null. 
  protected Method findMethod(Class<?> c, String name, Class<?>[] argTypes)
  {
    logger.debug("Looking for static method: class=" + c + " name=" + name);
    Method m = null;
    try
    {
      m = c.getMethod(name, argTypes);
    }
    catch (NoSuchMethodException e)
    {
      String msg = "Did not find method " + name + " on class " + c;
      logger.debug(msg);
    }
    return m;
  }
}