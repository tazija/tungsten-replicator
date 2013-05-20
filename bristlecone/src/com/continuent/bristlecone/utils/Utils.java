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

package com.continuent.bristlecone.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Utils
{
    final static String NEWLINE = "\n";

    public static void describeInstance(Object object)
    {
        Class<?> clazz = object.getClass();

        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        Field[] fields = clazz.getDeclaredFields();
        Method[] methods = clazz.getDeclaredMethods();

        System.out.println("Description for class: " + clazz.getName());
        System.out.println();
        System.out.println("Summary");
        System.out.println("-----------------------------------------");
        System.out.println("Constructors: " + (constructors.length));
        System.out.println("Fields: " + (fields.length));
        System.out.println("Methods: " + (methods.length));

        System.out.println();
        System.out.println();
        System.out.println("Details");
        System.out.println("-----------------------------------------");

        if (constructors.length > 0)
        {
            System.out.println();
            System.out.println("Constructors:");
            for (Constructor<?> constructor : constructors)
            {
                System.out.println(constructor);
            }
        }

        if (fields.length > 0)
        {
            System.out.println();
            System.out.println("Fields:");
            for (Field field : fields)
            {
                System.out.println(field);
            }
        }

        if (methods.length > 0)
        {
            System.out.println();
            System.out.println("Methods:");
            for (Method method : methods)
            {
                System.out.println(method);
            }
        }
    }

    public static String describeValues(Object object)
    {
        StringBuilder builder = new StringBuilder();

        Class<?> clazz = object.getClass();

        Field[] fields = clazz.getDeclaredFields();

        if (fields.length > 0)
        {
            builder.append(NEWLINE).append(
                    "-----------------------------------------")
                    .append(NEWLINE);
            for (Field field : fields)
            {
                builder.append(field.getName());
                builder.append(" = ");
                try
                {
                    field.setAccessible(true);
                    builder.append(field.get(object)).append(NEWLINE);
                }
                catch (IllegalAccessException e)
                {
                    builder.append("(Exception Thrown: " + e + ")");
                }
            }
        }

        return builder.toString();
    }

}
