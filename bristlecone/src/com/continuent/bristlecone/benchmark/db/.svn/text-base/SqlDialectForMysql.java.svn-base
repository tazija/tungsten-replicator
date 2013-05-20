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

package com.continuent.bristlecone.benchmark.db;

/**
 * MySQL DBMS dialect information.
 * 
 * @author rhodges
 */
public class SqlDialectForMysql extends AbstractSqlDialect
{
    /** Return MySQL driver. */
    public String getDriver()
    {
        return "com.mysql.jdbc.Driver";
    }

    /** Returns true if the JDBC URL looks like a PostgreSQL URL. */
    public boolean supportsJdbcUrl(String url)
    {
        return (url.startsWith("jdbc:mysql"));
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.continuent.bristlecone.benchmark.db.SqlDialect#getSetDefaultSchema(java.lang.String)
     */
    public String getSetDefaultSchema(String schema)
    {
        return "use " + schema;
    }

    /**
     * Implements create table that supports defining database engine.
     */
    public String getCreateTable(Table t)
    {
        String sql = super.getCreateTable(t);
        if (t.getDatabaseEngine() != null)
            sql += " ENGINE= " + t.getDatabaseEngine();

        return sql;
    }

    /**
     * Transforms negative fetch sizes to Integer.MIN_VALUE, which prompts
     * row-by-row streaming of result sets.
     */
    public int implementationConvertFetchSize(int fetchSize)
    {
        if (fetchSize < 0)
            return Integer.MIN_VALUE;
        else
            return fetchSize;
    }

    /** Add support for specialized MySQL BLOB/CLOB names. */
    public String implementationTypeName(int type)
    {
        switch (type)
        {
            case java.sql.Types.BLOB :
                return "longblob";
            case java.sql.Types.CLOB :
                return "longtext";
            case java.sql.Types.BIGINT :
                return "bigint";
            case AdditionalTypes.UTINYINT :
            case AdditionalTypes.UNTINYINT :
                return "tinyint unsigned";
            case AdditionalTypes.USMALLINT :
            case AdditionalTypes.UNSMALLINT :
                return "smallint unsigned";
            case AdditionalTypes.UMEDIUMINT :
            case AdditionalTypes.UNMEDIUMINT :
                return "mediumint unsigned";
            case AdditionalTypes.UINT :
            case AdditionalTypes.UNINT :
                return "int unsigned";
            case AdditionalTypes.UBIGINT :
            case AdditionalTypes.UNBIGINT :
                return "bigint unsigned";
            case AdditionalTypes.MEDIUMINT :
                return "mediumint";
            case java.sql.Types.TIMESTAMP :
                // Allow null value for timestamps
                return "timestamp null";
            default :
                return super.implementationTypeName(type);
        }
    }

    public String implementationSpecificSuffix(Column c)
    {
        String retval = "";

        switch (c.getType())
        {
            case AdditionalTypes.ENUM :
                retval += "(";
                for (int i = 0; i < c.getLength(); i++)
                {
                    if (i != 0)
                        retval += ", ";
                    retval += "'enum" + i + "'";
                }
                retval += ")";
                break;
            case AdditionalTypes.SET :
                retval += "(";
                for (int i = 0; i < c.getLength(); i++)
                {
                    if (i != 0)
                        retval += ", ";
                    retval += "'setele" + i + "'";
                }
                retval += ")";
                break;
            default :
                retval = super.implementationSpecifcSuffix(c);
                break;
        }
        return retval;
    }
}
