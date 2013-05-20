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
import com.continuent.bristlecone.benchmark.db.DataGenerator;
import com.continuent.bristlecone.benchmark.db.DataGeneratorFactory;
import com.continuent.bristlecone.benchmark.db.Table;
import com.continuent.bristlecone.benchmark.db.TableHelper;
import com.continuent.bristlecone.croc.CrocContext;
import com.continuent.bristlecone.croc.CrocException;
import com.continuent.bristlecone.croc.Loader;

/**
 * This class defines a loader that tests inserts across multiple data types.
 * 
 * @author <a href="mailto:robert.hodges@continuent.com">Robert Hodges</a>
 * @version 1.0
 */
public class MultiTypeInsert implements Loader
{
    Table         multiTable;
    DataGenerator fCharGenerator;
    DataGenerator fVarcharGenerator;
    DataGenerator fDatetimeGenerator;
    DataGenerator fDateGenerator;

    /** Instantiate and load tables. */
    public MultiTypeInsert()
    {
        // Define table.
        multiTable = new Table("multi_type_insert");
        Column id = new Column();
        id.setName("id");
        id.setType(Types.INTEGER);
        id.setPrimaryKey(true);
        multiTable.addColumn(id);

        Column fChar = new Column();
        fChar.setName("f_char");
        fChar.setType(Types.CHAR);
        fChar.setLength(10);
        multiTable.addColumn(fChar);

        Column fVarchar = new Column();
        fVarchar.setName("f_varchar");
        fVarchar.setType(Types.VARCHAR);
        fVarchar.setLength(10);
        multiTable.addColumn(fVarchar);

        Column fDatetime = new Column();
        fDatetime.setName("f_datetime");
        fDatetime.setType(Types.TIMESTAMP);
        multiTable.addColumn(fDatetime);

        Column fDate = new Column();
        fDate.setName("f_date");
        fDate.setType(Types.DATE);
        multiTable.addColumn(fDate);

        // Allocate data generators.
        DataGeneratorFactory factory = DataGeneratorFactory.getInstance();
        fCharGenerator = factory.getGenerator(fChar);
        fVarcharGenerator = factory.getGenerator(fVarchar);
        fDatetimeGenerator = factory.getGenerator(fDatetime);
        fDateGenerator = factory.getGenerator(fDate);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.continuent.bristlecone.croc.Loader#getTables()
     */
    public List<Table> getTables()
    {
        ArrayList<Table> tables = new ArrayList<Table>();
        tables.add(multiTable);
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
            String insertSql = helper.getSqlDialect().getInsert(multiTable);
            pstmt = conn.prepareStatement(insertSql);

            // Add 100 rows.
            for (int i = 0; i < 100; i++)
            {
                pstmt.setInt(1, i);
                pstmt.setObject(2, fCharGenerator.generate());
                pstmt.setObject(3, fVarcharGenerator.generate());
                pstmt.setObject(4, fDatetimeGenerator.generate());
                pstmt.setObject(5, fDateGenerator.generate());
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