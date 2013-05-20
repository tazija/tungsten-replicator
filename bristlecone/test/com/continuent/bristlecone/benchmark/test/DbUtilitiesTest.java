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

package com.continuent.bristlecone.benchmark.test;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Properties;

import junit.framework.TestCase;

import com.continuent.bristlecone.benchmark.db.Column;
import com.continuent.bristlecone.benchmark.db.SqlDialect;
import com.continuent.bristlecone.benchmark.db.SqlDialectFactory;
import com.continuent.bristlecone.benchmark.db.Table;
import com.continuent.bristlecone.benchmark.db.TableHelper;
import com.continuent.bristlecone.benchmark.db.TableSet;
import com.continuent.bristlecone.benchmark.db.TableSetHelper;

/**
 * Implements a unit test designed to check database utility functions,
 * specifically, creating, loading with data, and deleting tables using TableSet
 * definitions.
 * 
 * @author rhodges
 */
public class DbUtilitiesTest extends TestCase
{
  String driver;
  String url;
  String login;
  String password;

  // Does nothing for now
  protected void setUp() throws Exception
  {
    // Set test.properties file name.
    String testPropertiesName = System.getProperty("test.properties");
    if (testPropertiesName == null)
      testPropertiesName = "test.properties";

    // Load properties file.
    Properties props = new Properties();
    File f = new File(testPropertiesName);
    if (f.canRead())
    {
      FileInputStream fis = new FileInputStream(f);
      props.load(fis);
      fis.close();
    }

    // Set values used for test.
    driver = props.getProperty("driver", "org.hsqldb.jdbcDriver");
    url = props.getProperty("url",
        "jdbc:hsqldb:file:build/testdb/testdb;shutdown=true");
    login = props.getProperty("login", "sa");
    password = props.getProperty("password", null);
  }

  protected void tearDown() throws Exception
  {
    super.tearDown();
  }

  /**
   * Instantiate a column and confirm that values are correctly returned.
   */
  public void testColumn()
  {
    Column c = new Column("name", Types.BIGINT, 2, 1, true, false);
    assertEquals("name", c.getName());
    assertEquals(Types.BIGINT, c.getType());
    assertEquals(2, c.getLength());
    assertEquals(1, c.getPrecision());
    assertEquals(true, c.isPrimaryKey());
    assertEquals(false, c.isAutoIncrement());
  }

  /**
   * Generate a table and check that values are correctly returned.
   */
  public void testTable()
  {
    Table t = allTypesTable("testTable1");
    assertNotNull(t);
    assertEquals("Checking table name", "testTable1", t.getName());

    Column key = t.getPrimaryKey();
    assertNotNull("Checking that key is not null", key);
    assertEquals("Checking key name", "t_integer", key.getName());

    Column[] cols = t.getColumns();
    assertNotNull("Checking columns", cols);
    assertEquals("Checking column number", 8, cols.length);
  }

  /**
   * Generate a table with all types and confirm that all non-prepared
   * statements work properly.
   */
  public void testSqlDialect1() throws Exception
  {
    // Get data for test.
    Table t = allTypesTable("testSqlDialect");
    SqlDialect dialect = SqlDialectFactory.getInstance().getDialect(url);
    Connection conn = getConnection();

    assertNotNull("Checking table", t);
    assertNotNull("Checking dialect", dialect);
    assertNotNull("Checking connection", conn);

    // Drop table in case it already exists
    Statement stmt = null;
    try
    {
      stmt = conn.createStatement();
      stmt.executeUpdate(dialect.getDropTable(t));
    }
    catch (SQLException e)
    {
    }
    finally
    {
      if (stmt != null)
      {
        stmt.close();
        stmt = null;
      }
    }

    // Test table creation.
    stmt = conn.createStatement();
    String createTable = dialect.getCreateTable(t);
    stmt.execute(createTable);

    // Select from the table.
    String selectAll = dialect.getSelectAll(t);
    stmt.execute(selectAll);

    // Delete everything from the table.
    String deleteAll = dialect.getDeleteAll(t);
    stmt.execute(deleteAll);

    // Drop the table.
    String deleteTable = dialect.getDropTable(t);
    stmt.execute(deleteTable);

    // Clean up.
    stmt.close();
    conn.close();
  }

  /**
   * Generate a table and confirm that cross product selects work properly.
   */
  public void testSqlDialect2() throws Exception
  {
    // Get data for test.
    Table t = simpleTable("testSqlDialect2");
    SqlDialect dialect = SqlDialectFactory.getInstance().getDialect(url);
    Connection conn = getConnection();

    assertNotNull("Checking table", t);
    assertNotNull("Checking dialect", dialect);
    assertNotNull("Checking connection", conn);

    // Drop table in case it already exists
    Statement stmt = null;
    try
    {
      stmt = conn.createStatement();
      stmt.executeUpdate(dialect.getDropTable(t));
    }
    catch (SQLException e)
    {
    }
    finally
    {
      if (stmt != null)
      {
        stmt.close();
        stmt = null;
      }
    }

    // Test table creation.
    stmt = conn.createStatement();
    String createTable = dialect.getCreateTable(t);
    stmt.execute(createTable);

    // Test index generation.
    String createIndex = dialect.getCreateIndex(t, t.getColumn("t_varchar"));
    stmt.execute(createIndex);

    // Test table insert.
    String insert = dialect.getInsert(t);
    PreparedStatement pstmt = conn.prepareStatement(insert);
    pstmt.setInt(1, 1);
    pstmt.setString(2, "data");
    pstmt.execute();

    // Test table update.
    String update = dialect.getUpdateByKey(t);
    pstmt = conn.prepareStatement(update);
    pstmt.setString(1, "data2");
    pstmt.setInt(2, 1);
    pstmt.execute();

    // Test select.
    String select = dialect.getSelectByKey(t);
    pstmt = conn.prepareStatement(select);
    pstmt.setInt(1, 1);
    ResultSet rs = pstmt.executeQuery();
    boolean ok = false;
    int count = 0;
    while (rs.next())
    {
      count++;
      if ("data2".equals(rs.getString(2)))
      {
        ok = true;
      }
    }
    rs.close();

    assertEquals("Row count should be one", 1, count);
    assertEquals("Found the expected value from update", true, ok);

    // Select rows from the table using a select cross product.
    String selectCrossProduct = dialect.getSelectCrossProduct(t);
    rs = stmt.executeQuery(selectCrossProduct);
    rs.close();

    // Select count(*) from the table using a select cross product.
    String selectCrossProductCount = dialect.getSelectCrossProductCount(t);
    rs = stmt.executeQuery(selectCrossProductCount);
    rs.close();

    // Drop the table.
    String deleteTable = dialect.getDropTable(t);
    stmt.execute(deleteTable);

    // Clean up.
    pstmt.close();
    stmt.close();
    conn.close();
  }

  /**
   * Shows that we can create and populate tables for a TableSet.
   */
  public void testDataGeneration1() throws Exception
  {
    // Get data for test.
    TableSet ts = allTypesTableSet("testDG1_", 10, 100);
    TableSetHelper tsHelper = new TableSetHelper(url, login, password);

    assertNotNull("Checking table", ts);

    // Create new tables.
    tsHelper.createAll(ts);

    // Populate data.
    tsHelper.populateAll(ts);

    // Drop tables and go home.
    tsHelper.dropAll(ts, false);
  }

  /**
   * Shows that a table helper can create and drop a table with various data
   * types.  All combinations of add/drop are verified. 
   */
  public void testTableHelper1() throws Exception
  {
    Table table = allTypesTable("testTH1");
    TableHelper helper = new TableHelper(url, login, password);
    assertNotNull("Checking table", table);
    
    // Create and drop table.  Should work no matter what. 
    helper.create(table, true);
    
    // Create it again.  This works because we drop first. 
    helper.create(table, true);
    
    // This should fail because the table already exists. 
    try
    {
      helper.create(table, false);
      throw new Exception("Able to add table when it already exists: " + table.getName());
    }
    catch (SQLException e)
    {
    }
    
    // Drop the table.  This should work. 
    helper.drop(table, false);
    
    // This works because we ignore errors from dropping a non-existent table.
    helper.drop(table, true);
    
    // This works because the table is deleted. 
    helper.create(table, false);
  }

  /**
   * Shows that a table helper can insert and delete rows. 
   */
  public void testTableHelper2() throws Exception
  {
    Column[] cols = new Column[]{
        new Column("t_key", Types.INTEGER, 0, 0, true, false),
        new Column("t_char", Types.CHAR, 10),
        new Column("t_double", Types.DOUBLE),
        new Column("t_float", Types.FLOAT),
        new Column("t_smallint", Types.SMALLINT),
        new Column("t_varchar", Types.VARCHAR, 10)
    };

    Table table = new Table("testTH2", cols);
    TableHelper helper = new TableHelper(url, login, password);
    assertNotNull("Checking table", table);
    
    // Create the table. 
    helper.create(table, true);

    // Insert a bunch of rows.
    int i = 0;
    for (i = 0; i < 5; i++)
    {
      try
      {
        Object[] values = new Object[6];
        values[0] = i;
        values[1] = new Integer(i).toString();
        values[2] = (double) i;
        values[3] = (float) i;
        values[4] = i;
        values[5] = new Integer(i).toString();
        helper.insert(table, values);
      }
      catch (Exception e)
      {
        throw new Exception("Insert failed on row " + i, e);
      }
    }
      
    // Delete rows.
    for (i = 0; i < 5; i++)
    {
      try
      {
        Object[] values = new Object[1];
        values[0] = i;
        helper.delete(table, values);
      }
      catch (Exception e)
      {
        throw new Exception("Delete failed on row " + i, e);
      }
    }
  }

  // Create column definitions for all supported types.
  private Column[] allTypes()
  {
    Column[] cols = new Column[]{
        new Column("t_integer", Types.INTEGER, 0, 0, true, false),
        new Column("t_blob", Types.BLOB, 100),
        new Column("t_char", Types.CHAR, 10),
        new Column("t_clob", Types.CLOB, 100),
        new Column("t_double", Types.DOUBLE),
        new Column("t_float", Types.FLOAT),
        new Column("t_smallint", Types.SMALLINT),
        new Column("t_varchar", Types.VARCHAR, 10)};
    return cols;
  }

  // Create table definition containing an integer key and a varchar column.
  private Table simpleTable(String name)
  {
    Column[] cols = new Column[2];
    cols[0] = new Column("t_integer", Types.INTEGER, 0, 0, true, false);
    cols[1] = new Column("t_varchar", Types.VARCHAR, 10);
    cols[1].setIndexed(true);

    Table t = new Table(name, cols);
    return t;
  }

  // Create table definition containing all supported types.
  private Table allTypesTable(String name)
  {
    Column[] cols = allTypes();
    Table t = new Table(name, cols);
    return t;
  }

  // Create table set definition containing all supported types.
  private TableSet allTypesTableSet(String prefix, int count, int rows)
  {
    Column[] cols = allTypes();
    return new TableSet(prefix, count, rows, cols);
  }

  // Returns a JDBC connection instance.
  private Connection getConnection() throws Exception
  {
    Connection c;
    Class.forName(driver);
    c = DriverManager.getConnection(url, login, password);
    return c;
  }
}
