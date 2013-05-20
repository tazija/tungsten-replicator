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

import com.continuent.bristlecone.benchmark.impl.Config;
import com.continuent.bristlecone.benchmark.impl.ConfigMetadata;

/**
 * Defines a listener that accepts the result of a benchmark scenario and
 * logs output.   
 * 
 * @author rhodges
 *
 */
public interface ResultLogger
{
  /** 
   * Called to tell listener to get ready to receive results. 
   * 
   * @param metadata Tuple metadata
   */
  public void init(ConfigMetadata metadata); 
  
  /** 
   * Called each time a scenario finishes and generates a result. 
   * 
   * @param tuple A Tuple instance containing config params and results
   */
  public void resultGenerated(Config tuple);
  
  /** 
   * Called to tell listener all results have been generated and run 
   * is over. 
   */
  public void cleanup();
}