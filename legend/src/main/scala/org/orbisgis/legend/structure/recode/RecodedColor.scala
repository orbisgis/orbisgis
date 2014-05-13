/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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

import java.awt.Color
import org.orbisgis.coremap.renderer.se.parameter.SeParameter
import org.orbisgis.coremap.renderer.se.parameter.color.ColorLiteral
import org.orbisgis.coremap.renderer.se.parameter.color.ColorParameter
import org.orbisgis.coremap.renderer.se.parameter.color.Recode2Color
import org.orbisgis.coremap.renderer.se.parameter.string.StringAttribute
import org.orbisgis.legend.structure.parameter.AbstractAttributeLegend

class RecodedColor extends AbstractAttributeLegend with RecodedLegend {
  
  private var parameter : ColorParameter = new ColorLiteral

  /**
   * Tries to build a RecodedColor using the {@code ColorParameter} given in
   * argument.
   * @param param
   */
  def this(param : ColorParameter) = {
    this
    setParameter(param)
  }

  /**
   * Gets the underlying parameter.
   * @return
   */
  def getParameter : ColorParameter = parameter

  /**
   * Sets parameter to s
   * @param s
   * @throws IllegalArgumentException if s is neither a Recode2String nor a StringLiteral
   */
  def setParameter(s : SeParameter) : Unit = s match {
    case a : ColorLiteral =>
      parameter = a
      fireTypeChanged()
    case b : Recode2Color=>
      parameter = b
      field = getValueReference.getColumnName
      fireTypeChanged()
    case _ => throw new IllegalArgumentException("This class must be built from a  string recode or literal.")
  }

  /**
   * Gets the number of elements registered in this analysis.
   * @return
   */
  def size : Int = {
    parameter match {
      case _ : ColorLiteral => 0
      case a :Recode2Color => a.getNumMapItem
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
   * Gets the Color value, if any, associated to {@code key} in the inner {@code
   * Recode}.
   * @param i
   * @return
   */
  def getItemValue(i : Int) : Color = parameter match {
    case c : ColorLiteral => c.getColor(null)
    case a :Recode2Color => 
      Option(a.getMapItemValue(i)).map(_.getColor(null)).getOrElse(null)
  }

  /**
   * Gets the Color value, if any, associated to {@code key} in the inner {@code
   * Recode}.
   * @param i
   * @return
   */
  def getItemValue(i : String) : Color = parameter match {
    case c : ColorLiteral => c.getColor(null)
    case a :Recode2Color =>
      Option(a.getMapItemValue(i)).map(_.getColor(null)).getOrElse(null)
  }

  /**
   * Gets the value used when there is no match for a given parameter.
   * @return
   */
  def getFallbackValue() : Color = parameter match {
    case c : ColorLiteral => c.getColor(null)
    case a : Recode2Color=> a.getFallbackValue().getColor(null)
  }

  /**
   * Sets the value that is used when no match is found for a given parameter.
   * @param c
   */
  def setFallbackValue(c : Color) = parameter match {
    case cl : ColorLiteral => cl.setColor(c)
    case rc : Recode2Color => rc.setFallbackValue(new ColorLiteral(c))
  }

  /**
   * Gets the ith key of the inner {@code Recode}.
   * @param i
   * @return
   */
  def getKey(i : Int) : String = parameter match {
    case c : ColorLiteral => ""
    case a : Recode2Color => a.getMapItemKey(i)
  }

  /**
   * Sets the ith key of the inner {@code Recode}.
   * @param i
   * @param key
   */
  def setKey(i : Int, key : String) = parameter match {
    case c : ColorLiteral => throw new UnsupportedOperationException("A literal does not have a ith key.")
    case a : Recode2Color => a.setKey(i, key)
  }

  /**
   * Adds an item in the inner {@code Recode}.
   * @param key
   * @param value
   */
  def addItem(key : String, value : Color) = parameter match {
    case c : ColorLiteral =>
      val temp : Recode2Color = new Recode2Color(c,new StringAttribute(field))
      temp.addMapItem(key, new ColorLiteral(value))
      setParameter(temp)
    case a : Recode2Color => a.addMapItem(key, new ColorLiteral(value))
  }

  /**
   * Removes an item from the inner {@code Recode}.
   * @param i
   */
  def  removeItem(i : Int) = parameter match {
    case a : Recode2Color =>
      a.removeMapItem(i)
      if(a.getNumMapItem == 0){
        val cl : ColorLiteral = new ColorLiteral(a.getFallbackValue.getColor(null))
        setParameter(cl)
      }
    case _ =>
  }

  /**
   * Removes an item from the inner {@code Recode}.
   * @param key
   */
  def  removeItem(key : String) = parameter match {
    case a : Recode2Color =>
      a.removeMapItem(key)
      if(a.getNumMapItem == 0){
        val cl : ColorLiteral = new ColorLiteral(a.getFallbackValue.getColor(null))
        setParameter(cl)
      }
    case _ =>
  }


}
