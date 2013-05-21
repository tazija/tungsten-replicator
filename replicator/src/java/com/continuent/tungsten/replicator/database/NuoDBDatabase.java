/**
 * Copyright (c) 2012, NuoDB, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of NuoDB, Inc. nor the names of its contributors may
 *       be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL NUODB, INC. BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.continuent.tungsten.replicator.database;

import com.continuent.tungsten.common.csv.CsvWriter;
import com.continuent.tungsten.common.csv.NullPolicy;
import com.continuent.tungsten.replicator.ReplicatorException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.sql.*;
import java.util.ArrayList;

/**
 * @author Sergey Bushik
 */
public class NuoDBDatabase extends AbstractDatabase {

    private Logger logger = Logger.getLogger(getClass());


    public NuoDBDatabase() throws SQLException {
        dbms = DBMS.NUODB;
        dbDriver = "com.nuodb.jdbc.Driver";
    }

    @Override
    public SqlOperationMatcher getSqlNameMatcher() throws ReplicatorException {
        return new MySQLOperationMatcher();
    }

    @Override
    public ArrayList<String> getSchemas() throws SQLException {
        ArrayList<String> result = new ArrayList<String>();
        ResultSet schemas = null;
        try {
            schemas = getDatabaseMetaData().getSchemas();
            while (schemas.next()) {
                result.add(schemas.getString("TABLE_SCHEM"));
            }
        } finally {
            if (schemas != null) {
                schemas.close();
            }
        }
        return result;
    }

    @Override
    protected String columnToTypeString(Column column, String tableType) {
        String type;
        switch (column.getType()) {
            case Types.BIT:
                type = "BOOLEAN";
                break;
            case Types.BOOLEAN:
                type = "BOOLEAN";
                break;
            case Types.TINYINT:
                type = "SMALLINT";
                break;
            case Types.SMALLINT:
                type = "SMALLINT";
                break;
            case Types.INTEGER:
                type = "INT";
                break;
            case Types.BIGINT:
                type = "BIGINT";
                break;
            case Types.NUMERIC:
                type = "NUMERIC";
                break;
            case Types.REAL:
                type = "REAL";
                break;
            case Types.FLOAT:
                type = "FLOAT";
                break;
            case Types.DECIMAL:
                type = "DECIMAL";
                break;
            case Types.CHAR:
                type = "CHAR(" + column.getLength() + ")";
                break;
            case Types.VARCHAR:
                type = "VARCHAR(" + column.getLength() + ")";
                break;
            case Types.LONGVARCHAR:
                type = "VARCHAR(" + column.getLength() + ")";
                break;
            case Types.BINARY:
                type = "BINARY(" + column.getLength() + ")";
                break;
            case Types.VARBINARY:
                type = "VARBINARY(" + column.getLength() + ")";
                break;
            case Types.LONGVARBINARY:
                type = "VARBINARY(" + column.getLength() + ")";
                break;
            case Types.DATE:
                type = "DATE";
                break;
            case Types.TIME:
                type = "TIME";
                break;
            case Types.TIMESTAMP:
                type = "TIMESTAMP";
                break;
            case Types.CLOB:
                type = "CLOB";
                break;
            case Types.BLOB:
                type = "BLOB";
                break;
            case Types.NCHAR:
                type = "NCHAR(" + column.getLength() + ")";
                break;
            case Types.NVARCHAR:
                type = "NVARCHAR(" + column.getLength() + ")";
                break;
            case Types.NCLOB:
                type = "NCLOB";
                break;
            default:
                type = "UNKNOWN";
                break;
        }
        return type;
    }

    @Override
    public String getNowFunction() {
        return "NOW()";
    }

    @Override
    public String getTimeDiff(String column1, String column2) {
        String timeDiff = "";
        if (column1 == null) {
            timeDiff += "?";
        } else {
            timeDiff += column1;
        }
        timeDiff += " - ";
        if (column2 == null) {
            timeDiff += "?";
        } else {
            timeDiff += column2;
        }
        return timeDiff;
    }

    @Override
    public CsvWriter getCsvWriter(BufferedWriter writer) {
        CsvWriter csv = new CsvWriter(writer);
        csv.setQuoteChar('"');
        csv.setQuoted(true);
        csv.setNullPolicy(NullPolicy.skip);
        csv.setEscapedChars("\\");
        csv.setSuppressedChars("\n");
        csv.setEscapeChar('\\');
        csv.setWriteHeaders(false);
        return csv;
    }

    @Override
    protected ResultSet getTablesResultSet(DatabaseMetaData databaseMetaData, String schemaName,
                                           boolean baseTablesOnly) throws SQLException {
        String types[] = null;
        if (baseTablesOnly) {
            types = new String[]{"TABLE"};
        }
        return databaseMetaData.getTables(null, schemaName, null, types);
    }

    @Override
    protected ResultSet getPrimaryKeyResultSet(DatabaseMetaData databaseMetaData, String schemaName,
                                               String tableName) throws SQLException {
        return databaseMetaData.getPrimaryKeys(null, schemaName, tableName);
    }

    @Override
    public ResultSet getColumnsResultSet(DatabaseMetaData databaseMetaData, String schemaName,
                                         String tableName) throws SQLException {
        return databaseMetaData.getColumns(null, schemaName, tableName, null);
    }

    @Override
    public String getDatabaseObjectName(String name) {
        return "\"" + name + "\"";
    }

    @Override
    public boolean supportsUseDefaultSchema() {
        return true;
    }

    @Override
    public boolean supportsCreateDropSchema() {
        return true;
    }

    @Override
    public void createSchema(String schema) throws SQLException {
        execute("CREATE SCHEMA " + getDatabaseObjectName(schema));
    }

    @Override
    public void dropSchema(String schema) throws SQLException {
        execute("DROP SCHEMA " + getDatabaseObjectName(schema));
    }

    @Override
    public String getUseSchemaQuery(String schema) {
        return "USE " + getDatabaseObjectName(schema);
    }

    @Override
    public void useDefaultSchema(String schema) throws SQLException {
        execute(getUseSchemaQuery(schema));
        this.defaultSchema = schema;
    }

    public void createTable(Table table, boolean replace) throws SQLException {
        if (!isTableExists(table) || replace) {
            super.createTable(table, replace);
        }
    }

    private boolean isTableExists(Table table) {
        String query = getSelectTableQuery(table);
        Statement statement = null;
        ResultSet tables = null;
        boolean exists = false;
        try {
            debug(query);
            statement = dbConn.createStatement();
            tables = statement.executeQuery(query);
            exists = tables.next();
        } catch (SQLException exception) {
            error(exception, query);
        } finally {
            close(tables);
            close(statement);
        }
        return exists;
    }

    protected void debug(String query) {
        if (logger.isDebugEnabled()) {
            logger.debug(query);
        }
    }

    protected void error(SQLException exception, String query) {
        if (logger.isEnabledFor(Level.ERROR)) {
            logger.error("Query failed " + query + " with a message" + exception.getMessage());
        }
    }

    protected void close(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException exception) {
                if (logger.isEnabledFor(Level.ERROR)) {
                    logger.warn("Unable to close result set", exception);
                }
            }
        }
    }

    protected void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException exception) {
                if (logger.isEnabledFor(Level.ERROR)) {
                    logger.warn("Unable to close statement", exception);
                }
            }
        }
    }

    protected String getSelectTableQuery(Table table) {
        return "SELECT * FROM SYSTEM.TABLES WHERE SCHEMA='" + table.getSchema() + "' AND TABLENAME='" + table.getName() + "'";
    }

    public void dropTable(Table table) {
        try {
            execute("DROP TABLE IF EXISTS " + table.getSchema() + "." + table.getName());
        } catch (SQLException e) {
            if (logger.isDebugEnabled())
                logger.debug("Unable to drop table; this may be expected", e);
        }
    }
}
