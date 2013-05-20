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

import com.continuent.bristlecone.utils.ToStringHelper;

/**
 * Definition of a SQL column.
 * 
 * @author rhodges
 */
public class Column
{
    private String  name;
    private int     type;
    private int     length;
    private int     precision;
    private boolean isPrimaryKey;
    private boolean isAutoIncrement;
    private boolean indexed;

    /** Instantiate empty column to be filled out by accessors. */
    public Column()
    {
    }

    /** Short form to generate definition. */
    public Column(String name, int type)
    {
        this.name = name;
        this.type = type;
    }

    /** Longer form for character types. */
    public Column(String name, int type, int length)
    {
        this.name = name;
        this.type = type;
        this.length = length;
    }

    /** Full form for definitions. */
    public Column(String name, int type, int length, int precision,
            boolean isPrimaryKey, boolean isAutoIncrement)
    {
        this.name = name;
        this.type = type;
        this.length = length;
        this.precision = precision;
        this.isPrimaryKey = isPrimaryKey;
        this.isAutoIncrement = isAutoIncrement;
    }

    /** Returns true if this is an autoincrement column. */
    public boolean isAutoIncrement()
    {
        return isAutoIncrement;
    }

    /** Sets the column to be autoincrement. */
    public void setAutoIncrement(boolean isAutoIncrement)
    {
        this.isAutoIncrement = isAutoIncrement;
    }

    /** Returns true if this column is a primary key. */
    public boolean isPrimaryKey()
    {
        return isPrimaryKey;
    }

    /** Sets the primary key (true if primary). */
    public void setPrimaryKey(boolean isPrimaryKey)
    {
        this.isPrimaryKey = isPrimaryKey;
    }

    /** Returns the length of this column. */
    public int getLength()
    {
        return length;
    }

    /** Sets the length of the column or -1 if not used. */
    public void setLength(int length)
    {
        this.length = length;
    }

    /** Returns the column name. */
    public String getName()
    {
        return name;
    }

    /** Sets the column name. */
    public void setName(String name)
    {
        this.name = name;
    }

    /** Returns the column precision. */
    public int getPrecision()
    {
        return precision;
    }

    /** Sets the precision of this column or -1 if not used. */
    public void setPrecision(int precision)
    {
        this.precision = precision;
    }

    /** Returns the column type. */
    public int getType()
    {
        return type;
    }

    /** Sets the column type, which must be a value from java.sql.Type. */
    public void setType(int type)
    {
        this.type = type;
    }

    /** Returns true if this column has an index. */
    public boolean isIndexed()
    {
        return indexed;
    }

    /** Set property to true to add an extra index on this column. */
    public void setIndexed(boolean indexed)
    {
        this.indexed = indexed;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return ToStringHelper.toString(this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#clone()
     */
    public Column clone()
    {
        Column newCol = new Column();
        newCol.setName(name);
        newCol.setType(type);
        newCol.setLength(length);
        newCol.setPrecision(precision);
        newCol.setPrimaryKey(isPrimaryKey);
        newCol.setAutoIncrement(isAutoIncrement);
        newCol.setIndexed(indexed);
        return newCol;
    }
}