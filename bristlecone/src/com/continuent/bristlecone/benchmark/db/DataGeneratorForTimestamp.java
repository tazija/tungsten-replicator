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
 * Contributor(s): Hannu Alamäki
 */

package com.continuent.bristlecone.benchmark.db;

import java.sql.Timestamp;

/**
 * Generates TimeStamp values
 * 
 * @author alamäki
 */
public class DataGeneratorForTimestamp implements DataGenerator
{
    // 40 year interval for date generation in milliseconds.
    private static long intervalMillis = 40L * 365L * 24L * 3600L * 1000L;

    public DataGeneratorForTimestamp()
    {
    }

    /** Generate next timestamp. */
    public Object generate()
    {
        long timeValue = (long) ((Math.random() * intervalMillis));
        return new Timestamp(timeValue);
    }
}