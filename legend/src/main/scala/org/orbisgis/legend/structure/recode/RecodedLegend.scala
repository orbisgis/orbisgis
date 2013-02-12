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

import org.orbisgis.core.renderer.se.parameter.Recode
import org.orbisgis.core.renderer.se.parameter.ValueReference
import org.orbisgis.core.renderer.se.parameter.string.StringAttribute
import org.orbisgis.legend.structure.parameter.ParameterLegend
import org.orbisgis.legend.structure.recode.`type`.TypeEvent
import org.orbisgis.legend.structure.recode.`type`.TypeListener
import scala.collection.mutable.ArrayBuffer

abstract trait RecodedLegend extends ParameterLegend {

  var field : String = ""
  private val  listeners : ArrayBuffer[TypeListener] = ArrayBuffer.empty[TypeListener]

  /**
   * Gets the {@code ValueReference} used to retrieve the input values for this
   * value classification.
   */
  def getValueReference : ValueReference = getParameter match {
    case c : Recode[_,_] => val l = c.getLookupValue
      l match {
        case a : ValueReference => a
        case _ => throw new ClassCastException("We're not working with an authorized Recode2String")
      }
    case _ => null
  }

  /**
   * Sets the field used to make the analysis
   * @param s
   */
  def setField(s : String) = {
    field = s
    getParameter match {
      case c : Recode[_,_] => c.setLookupValue(new StringAttribute(s))
    }
  }

  def addListener(l : TypeListener) : Unit = listeners += l

  def fireTypeChanged() : Unit = {
    val te : TypeEvent = new TypeEvent(this)
    listeners foreach (_.typeChanged(te))
  }
}
