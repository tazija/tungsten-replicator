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
 * Initial developer(s): Hannu Alamäki.
 * Contributor(s):
 */

package com.continuent.bristlecone.benchmark.db;

import java.sql.Types;

//import sun.rmi.runtime.GetThreadPoolAction;

public class SqlDialectForOracle extends AbstractSqlDialect
{

    @Override
    public boolean supportsJdbcUrl(String url)
    {
        return (url.startsWith("jdbc:oracle:thin"));
    }

    public String getDriver()
    {
        return "oracle.jdbc.driver.OracleDriver";
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.continuent.bristlecone.benchmark.db.SqlDialect#getSetDefaultSchema(java.lang.String)
     */
    public String getSetDefaultSchema(String schema)
    {
        return "ALTER SESSION SET CURRENT_SCHEMA=" + schema;
    }

    @Override
    public String implementationColumnSpecification(Column col)
    {
        switch (col.getType())
        {
            case Types.DOUBLE :
                return col.getName() + " BINARY_DOUBLE ";
            case Types.TIME :
                col.setType(Types.DATE);
                return super.implementationColumnSpecification(col);
            default :
                return super.implementationColumnSpecification(col);
        }

    }

    @Override
    public String implementationTypeName(int type)
    {
        switch (type)
        {
            case Types.TIME :
                return "date";
            case java.sql.Types.TINYINT :
            case java.sql.Types.BIGINT :
            case AdditionalTypes.UTINYINT :
            case AdditionalTypes.USMALLINT :
            case AdditionalTypes.UMEDIUMINT :
            case AdditionalTypes.UINT :
            case AdditionalTypes.UBIGINT :
            case AdditionalTypes.MEDIUMINT :
                return "number";
            case AdditionalTypes.XML :
                return "xmltype";
            default :
                return super.implementationTypeName(type);
        }
    }

    @Override
    public String implementationAutoIncrementKeyword()
    {
        return "";
    }

    /**
     * Returns a generic SELECT for all columns and rows in a sorted,
     * deterministic way. This is over ridden in Oracle since Oracle needs
     * special introducer for XML columns
     */
    @Override
    public String getSelectAllSorted(Table t)
    {
        Column cols[] = t.getColumns();
        int ncols = cols.length;
        int i = 0;
        int usedColumns = 0;
        String SQL = "select ";

        for (i = 0; i < ncols; i++)
        {
            Column col = cols[i];
            if (i > 0)
                SQL += ", ";
            if (col.getType() == AdditionalTypes.XML)
            {
                /*
                 * When column is NULL we want to return NULL since getclobval()
                 * does not work on NULL
                 */
                /* decode(samplecol, null, null, xmltype.getclobval(samplecol)) */
                // SQL += "decode(" + col.getName() +
                // ", NULL, NULL, xmltype.getclobval(" + col.getName() + "))";
                SQL += "xmltype.getclobval(" + col.getName() + ")";
            }
            else
            {
                SQL += col.getName();
            }
        }

        SQL += " from " + t.getName();

        String orderBy = "";
        for (i = 0; i < ncols; i++)
        {
            Column col = cols[i];
            /* do not sort blobs or clobs */
            if (col.getType() == java.sql.Types.BLOB
                    || col.getType() == java.sql.Types.CLOB
                    || col.getType() == AdditionalTypes.XML)
                continue;
            usedColumns++;
            if (usedColumns > 1)
                orderBy += ", ";
            orderBy += (i + 1);
        }
        return SQL + (orderBy.length() > 0 ? " order by " + orderBy : "");
    }

    @Override
    public String getInsert(Table t)
    {
        // Start of INSERT command.
        StringBuffer sb = new StringBuffer();
        sb.append("insert into ");
        sb.append(t.getName());

        sb.append(" (");

        StringBuffer parameterList = new StringBuffer();
        parameterList.append(" values(");

        Column[] columns = t.getColumns();
        for (int i = 0; i < columns.length; ++i)
        {
            if (!columns[i].isAutoIncrement())
            {
                sb.append(columns[i].getName());
                /*
                 * Only way to insert into XML type is using XMLTYPE()
                 * introducer
                 */
                if (columns[i].getType() == AdditionalTypes.XML)
                {
                    parameterList
                            .append("XMLTYPE(nvl(?, '<ifnullsworked></ifnullsworked>'))");
                }
                else
                {
                    parameterList.append("?");
                }
                if (i < columns.length - 1)
                {
                    sb.append(", ");
                    parameterList.append(", ");
                }
            }
        }
        parameterList.append(")");
        sb.append(") ");
        sb.append(parameterList);

        return sb.toString();
    }

    public String getCreateAutoincrementSurrogateSequence(Table table)
    {
        if (!isTableUsingAutoincrement(table))
            return null;

        String sql = "create sequence "
                + getAutoIncSurrogateSequenceName(table);
        sql += " start with 1 increment by 1 nomaxvalue";
        return sql;

    }

    public boolean isTableUsingAutoincrement(Table table)
    {
        if (getAutoincColumn(table) != null)
            return true;
        else
            return false;
    }

    private Column getAutoincColumn(Table table)
    {
        if (table.getColumns() == null)
            return null;

        for (Column col : table.getColumns())
        {
            if (col.isAutoIncrement())
                return col;
        }
        return null;
    }

    public String getAutoIncSurrogateSequenceName(Table table)
    {
        String name = "ai" + table.getName();
        return name.toUpperCase();
    }

    public String getAutoIncSurrogateTriggerName(Table table)
    {
        String name = "trigg" + table.getName();
        return name.toUpperCase();
    }

    public String getDropAutoincrementSurrogateSequence(Table table)
    {
        if (isTableUsingAutoincrement(table))
            return getDropSequence(getAutoIncSurrogateSequenceName(table));
        else
            return null;
    }

    public String getCreateAutoincrementSurrogateTrigger(Table table)
    {
        Column autoIncCol = getAutoincColumn(table);
        if (autoIncCol == null)
            return null;
        String sql = "CREATE OR REPLACE TRIGGER  \""
                + getAutoIncSurrogateTriggerName(table) + "\"\n";
        sql += "BEFORE\n";
        sql += "insert on \"" + table.getName().toUpperCase() + "\"\n";
        sql += "for each row\n";
        sql += "BEGIN\n";
        sql += "SELECT " + getAutoIncSurrogateSequenceName(table)
                + ".NEXTVAL\n";
        sql += "INTO   :NEW." + autoIncCol.getName() + "\n";
        sql += "FROM   DUAL;\n";
        sql += "END;";
        return sql;
    }

    public String getDropAutoincrementSurrogateTrigger(Table table)
    {
        if (isTableUsingAutoincrement(table))
            return getDropTrigger(getAutoIncSurrogateTriggerName(table));
        else
            return null;
    }

    public String getDropSequence(String sequenceName)
    {
        return "DROP SEQUENCE " + sequenceName.toUpperCase();
    }

    public String getDropTrigger(String triggerName)
    {
        return "DROP TRIGGER " + triggerName;
    }

    public String implementationSpecificSuffix(Column c)
    {
        return super.implementationSpecifcSuffix(c);
    }

}
