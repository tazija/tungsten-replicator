/**
 * Bristlecone Test Tools for Databases
 * Copyright (C) 2006-2009 Continuent Inc.
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
 * Initial developer(s): Scott Martin.
 * Contributor(s):
 */

package com.continuent.bristlecone.benchmark.db;

import java.math.BigDecimal;
import java.lang.Long;
//import org.apache.log4j.Logger;


/**
 * Generates decimal values. 
 * 
 * @author scott.martin
 *
 */
public class DataGeneratorForDecimal implements DataGenerator
{
  //private static Logger     logger                  = Logger.getLogger(DataGeneratorForDecimal.class);
    
  private static final BigDecimal max = new BigDecimal(Long.MAX_VALUE);
  private static final BigDecimal one = new BigDecimal(1);
  private static final BigDecimal ten = new BigDecimal(10);
  int length = 10;
  int precision = 0;
 
  /** Create a new instance. */
  DataGeneratorForDecimal(int length, int precision)
  {
      this.length = length;
      this.precision = precision;
  }
  
  /** Generate a representative value */
  public Object generate()
  {
    boolean positive = (Math.random() >= 0.5) ? true : false;
    BigDecimal rand = new BigDecimal(Math.random());
    BigDecimal absvalue = max.multiply(rand);
    BigDecimal left = absvalue.divideToIntegralValue(one);
    int i = 1;
    BigDecimal retval = ten;
   
    /* Return only the number of digits permitted */
    left = left.stripTrailingZeros();
    for (;left.toPlainString().length() > length;)
    {
        left = left.divideToIntegralValue(ten);
        left = left.stripTrailingZeros();
    }
    absvalue = left;
    
    /* Shift decimal random number of places */
    int placesRight = (int)(Math.random() * (double)(length + 1));
    for (i = 0; i < placesRight; i++)
    {
        absvalue = absvalue.divide(ten);
        absvalue = absvalue.stripTrailingZeros();
    }

    /* half the time return negative values */
    if (positive) retval = absvalue;
    else retval = absvalue.negate();
    return retval;
  }
}