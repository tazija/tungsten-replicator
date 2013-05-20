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

package com.continuent.bristlecone.croc;

/**
 * Contains shared data for croc runs. This class defines a CrocContext
 * 
 * @author <a href="mailto:jussi-pekka.kurikka@continuent.com">Jussi-Pekka
 *         Kurikka</a>
 * @version 1.0
 */
public interface CrocContext
{
    public String getMasterUrl();

    public String getMasterUser();

    public String getMasterPassword();

    public String getSlaveUrl();

    public String getSlaveUser();

    public String getSlavePassword();

    public String getDefaultSchema();

    public boolean isDdlReplication();

    public int getTimeout();

    public boolean isStageTables();

    public boolean isNewStageFormat();

    public String getSlaveStageUrl();
}
