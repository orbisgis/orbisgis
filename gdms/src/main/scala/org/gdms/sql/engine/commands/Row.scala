/** OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
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
 *
 * or contact directly:
 * info@orbisgis.org
 */

package org.gdms.sql.engine.commands

import java.util.Arrays
import org.gdms.data.values.Value
import org.gdms.sql.engine.GdmSQLPredef._

/**
 * Wrapper for a row.
 *
 * A Row contains both the values and the original rowId if still present.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
final class Row(val rowId: Option[Long], var array: Array[Value]) {
  
  def map(f: Array[Value] => Array[Value]): Row = {
    new Row(rowId, f(array))
  }
  
  override def hashCode = {
    12 + Arrays.deepHashCode(array.asInstanceOf[Array[Object]])
  }
  
  override def equals(o: Any) = {
    if (o.isInstanceOf[Array[Value]]) {
      val a = o.asInstanceOf[Array[Object]]
      Arrays.deepEquals(a, array.asInstanceOf[Array[Object]])
    } else {
      false
    }
  }
  
  def ++(r: Row) = {
    new Row(None, array ++ r)
  }
  
  def ++(a: Array[Value]) = {
    new Row(None, array ++ a)
  }
}

object Row {
  def apply(i: Long, a: Seq[Value]) = new Row(Some(i),a toArray)
  def apply(a: Seq[Value]) = new Row(None, a toArray)
  
  val empty: Row = new Row(None, Array.empty)
}