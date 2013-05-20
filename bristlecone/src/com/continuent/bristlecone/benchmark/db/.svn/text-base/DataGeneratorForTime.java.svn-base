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

import java.sql.Time;
import com.continuent.bristlecone.benchmark.db.DataGenerator;

/**
 * Generates time values in the range of 0 to 23:59:59.999. Values are limited
 * to milliseconds due to Java semantics.
 * 
 * @author alamäki
 */
public class DataGeneratorForTime implements DataGenerator
{
    private static long maxTime = 24 * 60 * 60 * 1000;

    /** Generate next time. */
    public Object generate()
    {
        long sign = 1;
        if (Math.random() < 0.5)
            sign = -1;
        long timeValue = sign * (long) (Math.random() * maxTime);
        return new Time(timeValue);
    }
}