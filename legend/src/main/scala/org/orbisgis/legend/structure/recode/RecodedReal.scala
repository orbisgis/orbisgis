/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 * 
 * This file is part of OrbisGIS.
 * 
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 * 
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */

package org.orbisgis.legend.structure.recode

import org.orbisgis.core.renderer.se.parameter.SeParameter
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral
import org.orbisgis.core.renderer.se.parameter.real.RealParameter
import org.orbisgis.core.renderer.se.parameter.real.Recode2Real
import org.orbisgis.core.renderer.se.parameter.string.StringAttribute
import org.orbisgis.legend.structure.parameter.AbstractAttributeLegend
import org.orbisgis.legend.structure.parameter.NumericLegend

class RecodedReal extends AbstractAttributeLegend with RecodedLegend with NumericLegend {

  var parameter : RealParameter = new RealLiteral

  def this(param : RealParameter) = {
    this
    setParameter(param)
  }

  def getParameter : RealParameter = parameter

  /**
   * Sets parameter to s
   * @param s
   * @throws IllegalArgumentException if s is neither a Recode2String nor a StringLiteral
   */
  def setParameter(s : SeParameter) : Unit = s match {
    case a : RealLiteral =>
      parameter = a
      fireTypeChanged()
    case b : Recode2Real=>
      parameter = b
      field = getValueReference.getColumnName
      fireTypeChanged()
    case _ => throw new IllegalArgumentException("This class must be built from a  string recode or literal.")
  }

  def size : Int = {
    parameter match {
      case _ : RealLiteral => 0
      case a : Recode2Real => a.getNumMapItem
    }
  }

  /**
   * Gets the field used to make the analysis
   * @return
   */
  override def getLookupFieldName : String = field

  /**
   * Gets the field used to make the analysis
   * @return
   */
  override def setLookupFieldName(s: String) = setField(s)

  /**
   * Gets the Double value, if any, associated to {@code key} in the inner {@code
   * Recode}.
   * @param i
   * @return
   */
  def getItemValue(i : Int) : Double = parameter match {
    case c : RealLiteral => c.getValue(null)
    case a :Recode2Real =>
      a.getMapItemValue(i).getValue(null)
  }

  /**
   * Gets the Double value, if any, associated to {@code key} in the inner {@code
   * Recode}.
   * @param key
   * @return
   */
  def getItemValue(i : String) : Double = parameter match {
    case c : RealLiteral => c.getValue(null)
    case a :Recode2Real =>
      Option(a.getMapItemValue(i)).map(_.getValue(null) : Double).getOrElse(Double.NaN)
  }

  /**
   * Gets the value used when there is no match for a given parameter.
   * @return
   */
  def getFallbackValue() : Double = parameter match {
    case c : RealLiteral => c.getValue(null)
    case a : Recode2Real=> a.getFallbackValue().getValue(null)
  }
  
  /**
   * Gets the ith key of the inner {@code Recode}.
   * @param i
   * @return
   */
  def getKey(i : Int) : String = parameter match {
    case c : RealLiteral => ""
    case a : Recode2Real => a.getMapItemKey(i)
  }

  /**
   * Sets the ith key of the inner {@code Recode}.
   * @param i
   * @param newKey
   */
  def setKey(i : Int, key : String) = parameter match {
    case c : RealLiteral => throw new UnsupportedOperationException("A literal does not have a ith key.")
    case a : Recode2Real => a.setKey(i, key)
  }

  /**
   * Adds an item in the inner {@code Recode}.
   * @param key
   * @param value
   */
  def addItem(key : String, value : Double) = parameter match {
    case c : RealLiteral =>
      val temp : Recode2Real = new Recode2Real(c,new StringAttribute(field))
      temp.addMapItem(key, new RealLiteral(value))
      setParameter(temp)
    case a : Recode2Real => a.addMapItem(key, new RealLiteral(value))
  }

  /**
   * Removes an item from the inner {@code Recode}.
   * @param i
   */
  def  removeItem(i : Int) = parameter match {
    case c : RealLiteral => throw new UnsupportedOperationException(
        "You can't remove an item from a literal.")
    case a : Recode2Real =>
      a.removeMapItem(i)
      if(a.getNumMapItem == 0){
        val cl : RealLiteral = new RealLiteral(a.getFallbackValue.getValue(null))
        setParameter(cl)
      }
  }

  /**
   * Removes an item from the inner {@code Recode}.
   * @param key
   */
  def  removeItem(key : String) = parameter match {
    case c : RealLiteral => throw new UnsupportedOperationException(
        "You can't remove an item from a literal.")
    case a : Recode2Real =>
      a.removeMapItem(key)
      if(a.getNumMapItem == 0){
        val cl : RealLiteral = new RealLiteral(a.getFallbackValue.getValue(null))
        setParameter(cl)
      }
  }
}
