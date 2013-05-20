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

/**
 * 
 * This class defines a AdditionalTypes
 * 
 * These are types that are not part of java.sql.Types but are used in some database
 * vendors.  An example would be a column type in Oracle that has no corresponding
 * java.sql.Type.
 * 
 * @author <a href="mailto:scott.martin@continuent.com">Scott Martin</a>
 * @version 1.0
 */
public class AdditionalTypes
{
    /**
     * We have started the types at 1500 as it appears to be available
     * address space in java.sql.Types.
     */
    
    /**
     *  Oracle specific types.
     */
    public final static int TIMESTAMPLOCAL     =  1500; // TIMESTAMP WITH LOCAL TIME ZONE
    public final static int XML                =  1501; // Oracle's XML column

    /**
     *  Unsigned MySQL types
     */
    public final static int UTINYINT    =  1510; // UNSIGNED TINYINT   0-255
    public final static int USMALLINT   =  1511; // UNSIGNED SMALLINT  0-65535
    public final static int UMEDIUMINT  =  1512; // UNSIGNED MEDIUMINT 0-16777215
    public final static int UINT        =  1513; // UNSIGNED INT       0-4294967295
    public final static int UBIGINT     =  1514; // UNSIGNED BIGINT    0-18446744073709551615

    public final static int MEDIUMINT   =  1515; // MySQL's "mediumint" col type
    public final static int ENUM        =  1516; // MySQL's enum col type
    public final static int SET         =  1517; // MySQL's set col type
    public final static int YEAR        =  1518; // MySQL's year col type
    
    /**
     *  Unsigned "narrow" MySQL types.  These types represent one half the range of
     *  the true unsigned types listed above.
     */
    public final static int UNTINYINT    =  1520; // UNSIGNED TINYINT   0-127
    public final static int UNSMALLINT   =  1521; // UNSIGNED SMALLINT  0-32767
    public final static int UNMEDIUMINT  =  1522; // UNSIGNED MEDIUMINT 0-8388607
    public final static int UNINT        =  1523; // UNSIGNED INT       0-2147483647
    public final static int UNBIGINT     =  1524; // UNSIGNED BIGINT    0-9223372036854775807



    // Prevent instantiation
    private AdditionalTypes() {}
}


