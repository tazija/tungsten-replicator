/**
 * Bristlecone Test Tools for Databases
 * Copyright (C) 2010 Continuent Inc.
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
 * Initial developer(s): Robert Hodges
 * Contributor(s):
 */

package com.continuent.bristlecone.benchmark.tpcb;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.continuent.bristlecone.benchmark.Monitor;

/**
 * Implements a monitor to track and print out current status of TPCB run.
 * 
 * @author rhodges
 */
public class TPCBMonitor implements Monitor
{
    private static final Logger logger = Logger.getLogger(TPCBMonitor.class);

    TPCBStatistics              stats;

    /**
     * Perform basic initialization.
     */
    public void prepare(Properties properties) throws Exception
    {
        stats = TPCBStatistics.getInstance();
    }

    /** Perform monitoring. */
    public void run()
    {
        long currentTPCBCount = stats.getTPCBCount();
        long currentQueryCount = stats.getQueryCount();

        try
        {
            while (!Thread.interrupted())
            {
                // Bide a wee.
                Thread.sleep(2000);

                // Collect latest counts and print deltas.
                long newTPCBCount = stats.getTPCBCount();
                long newQueryCount = stats.getQueryCount();

                logger.info("TPC-B Statistics: write xacts="
                        + (newTPCBCount - currentTPCBCount) + " r/o xacts="
                        + (newQueryCount - currentQueryCount));

                currentTPCBCount = newTPCBCount;
                currentQueryCount = newQueryCount;
            }
        }
        catch (InterruptedException e)
        {
            logger.info("Monitor thread was interrupted");
        }
        logger.info("Monitor task is finished");
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.continuent.bristlecone.benchmark.Monitor#cleanup()
     */
    public void cleanup() throws Exception
    {
    }
}