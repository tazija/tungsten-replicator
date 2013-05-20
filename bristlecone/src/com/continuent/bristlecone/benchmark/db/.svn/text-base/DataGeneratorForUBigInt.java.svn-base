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
 * Initial developer(s): Scott Martin
 * Contributor(s):
 */

package com.continuent.bristlecone.benchmark.db;

import java.math.BigInteger;
import java.lang.Long;

/**
 * Generates unsigned big int values between values. 
 * 0-18446744073709551615 (0xFFFFFFFF)
 * @author scott.martin
 *
 */
public class DataGeneratorForUBigInt implements DataGenerator
{
    private BigInteger max;
    // The range of the unsigned types is being cut in half since it is
    // known that we are unable to replicate unsigned types with the
    // high bit on.  It is more useful to have a test case that is
    // expected to pass than one that is expected to fail.
    private boolean    testFullRange = true; // Set this to false to cut range in half

    /** Create a new instance with an upper bound. */
    DataGeneratorForUBigInt()
    {
        this.max = new BigInteger("18446744073709551615");
    }

    /** Create a new instance with an upper bound. */
    DataGeneratorForUBigInt(String s)
    {
        this(s, true);
    }
    
    /** Create a new instance with an upper bound. */
    DataGeneratorForUBigInt(String s, boolean testFullRange)
    {
        this.testFullRange = testFullRange;
        this.max = new BigInteger(s);
        if (!this.testFullRange)
        {
            BigInteger one = new BigInteger("1");
            BigInteger two = new BigInteger("2");
            
            /* if the max is odd, add one to it before dividing by two
             * as it was not clear to me how BigInteger.divide() handled rounding.
             */
            if (this.max.remainder(two).compareTo(one) == 0)
                this.max = this.max.add(one);
            
            this.max = this.max.divide(two);
        }
    }

    /**
     * Generate next value up to the boundary value. As the class BigInteger
     * does not have the ability to multiply a BigInteger times a fractional
     * value, we will do the fractional math using longs. This routine will not
     * generate every BigInteger between this.max and 0, only long.MAX_VALUE
     * distinct representatives equally spaced in the BigInteger address space.
     */
    public Object generate()
    {
        BigInteger retval;

        Long totalRepresentativeValues = 9223372036854775807L;
        Long currentRepresentativeValue = (long) (Math.random() * totalRepresentativeValues
                .longValue());

        BigInteger currentRepresentativeValueBig = new BigInteger(
                currentRepresentativeValue.toString());
        BigInteger totalRepresentativeValuesBig = new BigInteger(
                totalRepresentativeValues.toString());

        // retval = max * currentRepresentative / totalNumberOfRepresentatives
        retval = this.max;
        retval = retval.multiply(currentRepresentativeValueBig);
        retval = retval.divide(totalRepresentativeValuesBig);

        return retval;
    }
}
