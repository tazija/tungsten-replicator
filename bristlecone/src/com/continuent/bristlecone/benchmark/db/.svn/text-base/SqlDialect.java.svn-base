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
 * Interface to generate statements for specific DBMS implementations.
 * 
 * @author rhodges
 */
public interface SqlDialect
{
    /**
     * Returns the name of the JDBC driver class.
     */
    public String getDriver();

    /**
     * Returns true if this is the right implementation to use for the given
     * JDBC URL.
     */
    public boolean supportsJdbcUrl(String url);

    /** Returns a statement to set default schema to the given name. */
    public String getSetDefaultSchema(String schema);

    /** Returns a CREATE TABLE statement for the given table. */
    public String getCreateTable(Table t);

    /** Returns a CREATE INDEX statement for the given table and column. */
    public String getCreateIndex(Table t, Column c);

    /** Returns a DROP TABLE statement for the given table. */
    public String getDropTable(Table t);

    /**
     * Returns an INSERT statement with parameters for non-autoincrement fields.
     */
    public String getInsert(Table t);

    /** Returns an UPDATE statement that updates a single record by key value. */
    public String getUpdateByKey(Table t);

    /**
     * Returns an UPDATE statement that updates a single record by key value.
     * Where key is in defined column.
     */
    public String getUpdateByKey(Table t, Column keyColumn);

    /**
     * Returns an UPDATE statement that updates a all records where key column
     * value is like value given as second statement parameter
     */
    public String getUpdateByKeyLike(Table t, Column keyColumn);

    /** Returns a DELETE statement that finds the record by its key. */
    public String getDeleteByKey(Table t);

    /**
     * Returns a DELETE statement that finds the record by its key. Where key is
     * in defined column.
     */
    public String getDeleteByKey(Table t, Column keyColumn);

    /**
     * Like getDeleteByKey(Table t, Column keyColumn) but uses LIKE instead of =
     */
    public String getDeleteByKeyLike(Table t, Column keyColumn);

    /**
     * Returns a DELETE / TRUNCATE statement that deletes all records in the
     * table.
     */
    public String getDeleteAll(Table t);

    /** Returns a SELECT statement to fetch all rows and all columns. */
    public String getSelectAll(Table t);

    /**
     * Returns a SELECT statement to fetch all rows and all columns in a sorted,
     * deterministic way.
     */
    public String getSelectAllSorted(Table t);

    /**
     * Return all rows of the cross product select of the table on itself. This
     * scans and returns N x N rows where N is the table row count.
     */
    public String getSelectCrossProduct(Table table);

    /**
     * Return count of rows of the cross product select of the table on itself.
     * This scans N x N rows where N is the table row count and returns a single
     * row.
     */
    public String getSelectCrossProductCount(Table table);

    /**
     * Returns a SELECT statement to fetch a query that performs a cross product
     * on a set of tables limited by high and low key values, which must be
     * supplied as prepared statement parameters. This query hits the database
     * server relatively hard.
     */
    public String getSelectCrossProductCount(Table[] tables);

    /**
     * Returns a SELECT statement to fetch a row using its primary key, which
     * must be supplied as a prepared statement parameter.
     */
    public String getSelectByKey(Table t);

    /**
     * Returns a SELECT statement to fetch one or more rows by a column value,
     * which must be supplied as a prepared statement paremeter.
     */
    public String getSelectByColumn(Table t, Column c);

    /**
     * Returns a SELECT statement to fetch one or more rows with a LIMIT. Not
     * all DBMS implementations can handle a limit.
     */
    public String getSelectByColumnWithLimit(Table t, Column c, int limit);

    /**
     * Returns true if the given type requires a transaction to update.
     * 
     * @param type int type value from java.sql.Type
     */
    public boolean implementationUpdateRequiresTransaction(int type);

    /**
     * Returns true if this DBMS implementation supports a limit clause.
     */
    public boolean implementationSupportsLimitClause();

    /**
     * Returns a column specification for use in CREATE TABLE.
     */
    public String implementationColumnSpecification(Column col);

    /**
     * Translates java.sql.Type values to implementation-specific names.
     */
    public String implementationTypeName(int type);

    /**
     * If the column needs any implementatoin specific suffix, return it here
     */
    public String implementationSpecificSuffix(Column c);

    /**
     * Returns true if this type requires a length specification.
     */
    public boolean implementationTypeNeedsLength(int type);

    /**
     * Returns true if this type requires a precision specification.
     */
    public boolean implementationTypeNeedsPrecision(int type);

    /**
     * Transforms a value to a conformant fetch size for the implementation. If
     * the value is already legal it is left unchanged. This method allows us to
     * deal with non-standard implementations like MySQL Connector/J that use
     * Integer.MIN_VALUE to trigger row-by-row streaming.
     */
    public int implementationConvertFetchSize(int fetchSize);

    /**
     * Returns true if this DBMS implementation supports indexes. Data
     * warehouses like Vertica do not.
     */
    public boolean implementationSupportsIndexes();

    /**
     * Returns true if this DBMS implementation requires supplementary DDL
     * commands to create tables.
     */
    public boolean implementationSupportsSupplementaryTableDdl();

    /**
     * Return supplementary table creation command.
     */
    public String getSupplementaryTableDdl(Table table);
}