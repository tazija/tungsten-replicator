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

import java.util.Properties;

/**
 * Denotes a monitor.  A single monitor thread runs in parallel with each benchmark
 * scenario run.  The monitor thread starts before the scenario threads and finishes 
 * after them.  It can be used for on-going status collecting or any other task
 * that seems useful. 
 * 
 * @author rhodges
 */
public interface Monitor extends Runnable
{
  /**
   * Prepare the monitor for execution. 
   * 
   * @throws Exception Thrown in the event of a failure
   */
  public void prepare(Properties properties) throws Exception;

  /**
   * Perform monitoring task.  The task must terminate on receiving
   * an interrupt. 
   * 
   * {@inheritDoc}
   * @see java.lang.Runnable#run()
   */
  public void run();

  /**
   * Release resources for monitor.  This call is made after the monitor 
   * task thread ends. 
   * 
   * @throws Exception Thrown in the event that cleanup was unsuccessful
   */
  public void cleanup() throws Exception;
}