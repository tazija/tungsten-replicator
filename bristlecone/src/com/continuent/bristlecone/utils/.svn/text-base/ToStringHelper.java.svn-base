
package com.continuent.bristlecone.utils;

import java.lang.reflect.Field;

public class ToStringHelper
{

    static public String toString(Object obj)
    {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        //result.append(obj.getClass().getName());
        result.append("{");
        result.append(newLine);

        // determine fields declared in this class only (no fields of
        // superclass)
        Field[] fields = obj.getClass().getDeclaredFields();

        // print field names paired with their values
        for (Field field : fields)
        {
            field.setAccessible(true);
            try
            {
                result.append(field.getName());
                result.append("=");
                result.append(field.get(obj));
            }
            catch (IllegalAccessException ex)
            {
                System.out.println(ex);
            }
            result.append(newLine);
        }
        result.append("}");

        return result.toString();
    }
}
