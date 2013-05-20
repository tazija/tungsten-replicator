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


/**
 * Basic scenario test to check various aspects of scenario processing. 
 * 
 * @author rhodges
 */
public class ComplexScenario extends SimpleScenario
{
  public static int calledGlobalPrepare = 0;
  public static int calledGlobalCleanup = 0;
  
  private String propString;
  private long propLong;
  private int propInt;
  private boolean propBoolean;
  private char propChar;
  private float propFloat;
  private double propDouble;

  public ComplexScenario()
  {
  }
  
  public boolean isPropBoolean()
  {
    return propBoolean;
  }

  public char getPropChar()
  {
    return propChar;
  }

  public double getPropDouble()
  {
    return propDouble;
  }

  public float getPropFloat()
  {
    return propFloat;
  }

  public int getPropInt()
  {
    return propInt;
  }

  public long getPropLong()
  {
    return propLong;
  }

  public String getPropString()
  {
    return propString;
  }

  public void setPropBoolean(boolean propBoolean)
  {
    this.propBoolean = propBoolean;
  }

  public void setPropChar(char propChar)
  {
    this.propChar = propChar;
  }

  public void setPropDouble(double propDouble)
  {
    this.propDouble = propDouble;
  }

  public void setPropFloat(float propFloat)
  {
    this.propFloat = propFloat;
  }

  public void setPropInt(int propInt)
  {
    this.propInt = propInt;
  }

  public void setPropLong(long propLong)
  {
    this.propLong = propLong;
  }

  public void setPropString(String propString)
  {
    this.propString = propString;
  }

  public static synchronized void clearCounters()
  {
    SimpleScenario.clearCounters();
    calledGlobalPrepare = 0;
    calledGlobalCleanup = 0;
  }

  public void globalPrepare() throws Exception
  {
    calledGlobalPrepare++;
  }

  public void globalCleanup() throws Exception
  {
    calledGlobalCleanup++;
  }
}