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

import java.util.concurrent.atomic.AtomicLong;

/**
 * Implements a statistical counter for tracking TPC-B activity
 */
public class TPCBStatistics
{
    private static final TPCBStatistics instance        = new TPCBStatistics();
    private AtomicLong                  numberOfTPCBs   = new AtomicLong(0);
    private AtomicLong                  numberOfQueries = new AtomicLong(0);

    // Singleton.
    private TPCBStatistics()
    {
    }

    /** Return statistics instance. */
    public static synchronized TPCBStatistics getInstance()
    {
        return instance;
    }

    /**
     * Clear statistics.
     */
    public void initialize()
    {
        numberOfTPCBs.set(0);
        numberOfQueries.set(0);
    }
    
    public void incrementTPCBCount()
    {
        numberOfTPCBs.incrementAndGet();
    }
    
    public void incrementQueryCount()
    {
        numberOfQueries.incrementAndGet();
    }
    
    public long getTPCBCount()
    {
        return numberOfTPCBs.longValue();
    }
    
    public long getQueryCount()
    {
        return numberOfQueries.longValue();
    }
}
