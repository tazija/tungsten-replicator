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

package com.continuent.bristlecone.benchmark.test;

import java.util.Properties;

import com.continuent.bristlecone.benchmark.Monitor;

/**
 * Most basic scenario with counters for required instance methods.
 * 
 * @author rhodges
 */
public class SimpleMonitor implements Monitor
{
    public static int        calledPrepare = 0;
    public static int        calledRun     = 0;
    public static int        calledCleanup = 0;

    public static synchronized void clearCounters()
    {
        calledPrepare = 0;
        calledRun = 0;
        calledCleanup = 0;
    }

    public SimpleMonitor()
    {
        super();
    }

    public void prepare(Properties p)
    {
        calledPrepare++;
    }

    public void run()
    {
        calledRun++;
        try
        {
            while (!Thread.interrupted())
            {
                Thread.sleep(10);
            }
        }
        catch (InterruptedException e)
        {
        }
    }

    public void cleanup()
    {
        calledCleanup++;
    }
}