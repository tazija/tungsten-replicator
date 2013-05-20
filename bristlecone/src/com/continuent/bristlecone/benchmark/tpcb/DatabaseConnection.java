/**
 * Tungsten Scale-Out Stack
 * Copyright (C) 2007-2010 Continuent Inc.
 * Contact: tungsten@continuent.org
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

package com.continuent.bristlecone.benchmark.tpcb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.continuent.bristlecone.benchmark.db.SqlDialect;
import com.continuent.bristlecone.benchmark.db.SqlDialectFactory;

/**
 * Manages connection to the database. This class currently does little more
 * than the underlying Java class - Connection.
 */
public class DatabaseConnection
{
    private static final Logger logger = Logger
                                               .getLogger(DatabaseConnection.class);
    private Connection          connection;
    private String              dbUri;
    private String              user;
    private String              password;
    private SqlDialect          dialect;

    public DatabaseConnection(String url)
    {
        this(url, null, null);
    }

    public DatabaseConnection(String url, String user, String password)
    {
        this.dbUri = url;
        this.user = user;
        this.password = password;
        this.dialect = SqlDialectFactory.getInstance().getDialect(dbUri);
    }

    public void connect()
    {
        close();
        initDbConnection();
    }

    public Connection getConnection()
    {
        return connection;
    }

    public SqlDialect getDialect()
    {
        return dialect;
    }

    public void close()
    {
        if (connection != null)
        {
            try
            {
                connection.close();
            }
            catch (Exception e)
            {
            }
            finally
            {
                connection = null;
            }
        }
    }

    private void initDbConnection()
    {
        try
        {
            logger.debug("Connecting to database via:" + dbUri, null);
            Class.forName(dialect.getDriver()).newInstance();
            if (user == null)
                connection = DriverManager.getConnection(dbUri);
            else
                connection = DriverManager.getConnection(dbUri, user, password);
            connection.setAutoCommit(false);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to connect to database: uri="
                    + dbUri + " user=" + user + " password=" + password, e);
        }
    }

    public Statement createStatement() throws SQLException
    {
        return connection.createStatement();
    }

    public PreparedStatement prepareStatement(String SQL) throws SQLException
    {
        return connection.prepareStatement(SQL);
    }

    public void commit() throws SQLException
    {
        connection.commit();
    }

    public void execute(String SQL) throws SQLException
    {
        PreparedStatement statement = prepareStatement(SQL);
        statement.execute();
    }
}
