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

import java.util.Collection;
import java.util.Vector;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;

/**
 * This appender works much like log4j's Socket Appender. The main difference is
 * that it sends strings to remote clients rather than sending serialized
 * LoggingEvent objects. This approach has the advantages of being considerably
 * faster (serialization is not cheap) and of not requiring the client
 * application to be coupled to log4j at all.
 * <p>
 * This appender takes only one "parameter," which specifies the port number
 * (defaults to 9999). Set it with:
 * 
 * <PRE>
*  log4j.appender.R=com.holub.log4j.RemoteAppender;
*  ...
*  log4j.appender.R.Port=1234
*  </PRE>
 */

public class TestHookAppender extends AppenderSkeleton
{

    private static Collection<String> errors      = new Vector<String>();
    private static boolean            exitOnError = false;

    // The iterator across the "clients" Collection must
    // support a "remove()" method.

    public boolean requiresLayout()
    {
        return true;
    }

    /** Called once all the options have been set. */
    public void activateOptions()
    {
        super.activateOptions();
    }

    /**
     * Actually do the logging. The AppenderSkeleton's doAppend() method calls
     * append() to do the actual logging after it takes care of required
     * housekeeping operations.
     */

    @Override
    public synchronized void append(LoggingEvent event)
    {

        if (this.layout == null)
        {
            errorHandler.error("No layout for appender " + name, null,
                    ErrorCode.MISSING_LAYOUT);
            return;
        }

        String message = this.layout.format(event);

//        if (event.getLevel() == Priority.ERROR)
//        {
//            errors.add(message);
//            if (exitOnError == true)
//            {
//                System.exit(1);
//            }
//        }
    }

    /**
     * @return tru if there is an error, otherwise false.
     */
    public boolean hasError()
    {
        return (errors.size() > 0 ? true : false);
    }

    /**
     * @return the collection of errors
     */
    public Collection<String> getErrors()
    {
        return errors;
    }

    /**
     * @param flag
     */
    public void setExitOnError(boolean flag)
    {
        exitOnError = flag;
    }

    @Override
    public void close()
    {
        // TODO Auto-generated method stub

    }

}
