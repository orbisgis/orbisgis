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
import org.orbisgis.core.renderer.se.parameter.string.Recode2String
import org.orbisgis.core.renderer.se.parameter.string.StringAttribute
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral
import org.orbisgis.core.renderer.se.parameter.string.StringParameter
import org.orbisgis.legend.structure.parameter.AbstractAttributedRPLegend

class RecodedString extends AbstractAttributedRPLegend with RecodedLegend {

  var parameter : StringParameter = new StringLiteral

  def size : Int = {
    parameter match {
      case _ : StringLiteral => 0
      case a : Recode2String => a.getNumMapItem
    }
  }

  def this(param : StringParameter) = {
    this
    setParameter(param)
  }

  def getParameter : StringParameter = parameter

  /**
   * Sets paramter to s
   * @param s
   * @throws IllegalArgumentException if s is neither a Recode2String nor a StringLiteral
   */
  def setParameter(s : SeParameter) : Unit = s match {
    case a : StringLiteral => parameter = a
    case b : Recode2String => 
      parameter = b
      field = getValueReference.getColumnName
    case _ => throw new IllegalArgumentException("This class must be built from a  string recode or literal.")
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
  def getItemValue(i : Int) : String = parameter match {
    case c : StringLiteral => c.getValue(null)
    case a : Recode2String =>
      Option(a.getMapItemValue(i)).map(_.getValue(null)).getOrElse(null)
  }

  /**
   * Gets the value used when there is no match for a given parameter.
   * @return 
   */
  def getFallbackValue() : String = parameter match {
    case c : StringLiteral => c.getValue(null)
    case a : Recode2String => a.getFallbackValue().getValue(null)
  }

  /**
   * Gets the Double value, if any, associated to {@code key} in the inner {@code
   * Recode}.
   * @param key
   * @return
   */
  def getItemValue(i : String) : String = parameter match {
    case c : StringLiteral => c.getValue(null)
    case a :Recode2String =>
      a.getMapItemValue(i).getValue(null)
  }
  /**
   * Gets the ith key of the inner {@code Recode}.
   * @param i
   * @return
   */
  def getKey(i : Int) : String = parameter match {
    case c : StringLiteral => ""
    case a : Recode2String => a.getMapItemKey(i)
  }

  /**
   * Sets the ith key of the inner {@code Recode}.
   * @param i
   * @param newKey
   */
  def setKey(i : Int, key : String) = parameter match {
    case c : StringLiteral => throw new UnsupportedOperationException("A literal does not have a ith key.")
    case a : Recode2String => a.setKey(i, key)
  }

  /**
   * Adds an item in the inner {@code Recode}.
   * @param key
   * @param value
   */
  def addItem(key : String, value : String) = parameter match {
    case c : StringLiteral =>
      val temp = new Recode2String(c,new StringAttribute(field))
      temp.addMapItem(key, new StringLiteral(value))
      parameter = temp
    case a : Recode2String => a.addMapItem(key, new StringLiteral(value))
  }

  /**
   * Removes an item from the inner {@code Recode}.
   * @param i
   */
  def  removeItem(i : Int) = parameter match {
    case c : StringLiteral => throw new UnsupportedOperationException(
        "You can't remove an item from a literal.")
    case a : Recode2String =>
      a.removeMapItem(i)
      if(a.getNumMapItem == 0){
        parameter = new StringLiteral(a.getFallbackValue.getValue(null))
      }
  }

  /**
   * Removes an item from the inner {@code Recode}.
   * @param key
   */
  def  removeItem(key : String) = parameter match {
    case c : StringLiteral => throw new UnsupportedOperationException(
        "You can't remove an item from a literal.")
    case a : Recode2String =>
      a.removeMapItem(key)
      if(a.getNumMapItem == 0){
        parameter = new StringLiteral(a.getFallbackValue.getValue(null))
      }
  }
}
