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
 * Initial developer(s): Robert Hodges
 * Contributor(s):
 */

package com.continuent.bristlecone.benchmark.db;

/**
 * Derby embedded SQL dialect information.
 * 
 * @author rhodges
 */
public class SqlDialectForDerby extends AbstractSqlDialect
{
    /** Return the HSQLDB driver. */
    public String getDriver()
    {
        return "org.apache.derby.jdbc.EmbeddedDriver";
    }

    /** Returns true if the JDBC URL looks like a Derby URL. */
    public boolean supportsJdbcUrl(String url)
    {
        return (url.startsWith("jdbc:derby"));
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.continuent.bristlecone.benchmark.db.SqlDialect#getSetDefaultSchema(java.lang.String)
     */
    public String getSetDefaultSchema(String schema)
    {
        return "SET CURRENT SCHEMA=" + schema;
    }

    /**
     * HSQLDB uses "identity" as keyword for auto_increment.
     */
    public String implementationAutoIncrementKeyword()
    {
        return "identity";
    }

    /** Add support for specialized Derby type names. */
    public String implementationTypeName(int type)
    {
        return super.implementationTypeName(type);
    }

    public String implementationSpecificSuffix(Column c)
    {
        return super.implementationSpecifcSuffix(c);
    }
}