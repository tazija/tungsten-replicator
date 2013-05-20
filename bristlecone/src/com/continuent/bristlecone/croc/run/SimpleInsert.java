/**
 * Bristlecone Test Tools for Databases
 * Copyright (C) 2011 Continuent Inc.
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

package com.continuent.bristlecone.croc.run;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.continuent.bristlecone.benchmark.db.Column;
import com.continuent.bristlecone.benchmark.db.Table;
import com.continuent.bristlecone.benchmark.db.TableHelper;
import com.continuent.bristlecone.croc.CrocContext;
import com.continuent.bristlecone.croc.CrocException;
import com.continuent.bristlecone.croc.Loader;

/**
 * This class defines a very basic loader that loads into a table with a single
 * primary key column.
 * 
 * @author <a href="mailto:robert.hodges@continuent.com">Robert Hodges</a>
 * @version 1.0
 */
public class SimpleInsert implements Loader
{
    Table simpleTable;

    /** Instantiate and load tables. */
    public SimpleInsert()
    {
        simpleTable = new Table("simple_insert");
        Column id = new Column();
        id.setName("id");
        id.setType(Types.INTEGER);
        id.setPrimaryKey(true);
        simpleTable.addColumn(id);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.continuent.bristlecone.croc.Loader#getTables()
     */
    public List<Table> getTables()
    {
        ArrayList<Table> tables = new ArrayList<Table>();
        tables.add(simpleTable);
        return tables;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.continuent.bristlecone.croc.Loader#load(com.continuent.bristlecone.croc.CrocContext)
     */
    public void load(CrocContext context) throws CrocException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        TableHelper helper = new TableHelper(context.getMasterUrl(),
                context.getMasterUser(), context.getMasterPassword(),
                context.getDefaultSchema());
        try
        {
            // Set up connection and prepared statement.
            conn = helper.getConnection();
            String insertSql = helper.getSqlDialect().getInsert(simpleTable);
            pstmt = conn.prepareStatement(insertSql);

            // Add 100 rows.
            for (int i = 0; i < 100; i++)
            {
                pstmt.setInt(1, i);
                pstmt.execute();
            }
        }
        catch (SQLException e)
        {
            throw new CrocException("Unable to load data: " + e.getMessage(), e);
        }
        finally
        {
            helper.releaseStatement(pstmt);
            helper.releaseConnection(conn);
        }
    }
}