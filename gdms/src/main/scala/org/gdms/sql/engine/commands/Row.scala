/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
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
 * @param rowId an optional row id
 * @param array an array of Value objects (the actual content of the row)
 * @author Antoine Gourlay
 * @since 0.1
 */
final class Row(val rowId: Option[Long], var array: Array[Value]) {
  
  override def hashCode = {
    // used to group & filter duplicates of rows
    12 + Arrays.deepHashCode(array.asInstanceOf[Array[Object]])
  }
  
  override def equals(o: Any) = {
    // used to group & filter duplicates of rows
    if (o.isInstanceOf[Array[Value]]) {
      val a = o.asInstanceOf[Array[Object]]
      Arrays.deepEquals(a, array.asInstanceOf[Array[Object]])
    } else {
      false
    }
  }
  
  /**
   * Concatenates two rows. Drops any row id present.
   * 
   * @param r another row to add at the end of this one
   */
  def ++(r: Row) = {
    new Row(None, array ++ r)
  }
  
  /**
   * Concatenates two rows. Drops any row id present.
   * 
   * @param r another row to add at the end of this one
   */
  def ++(a: Array[Value]) = {
    new Row(None, array ++ a)
  }
}

object Row {
  def apply(i: Long, a: Seq[Value]) = new Row(Some(i),a toArray)
  def apply(a: Seq[Value]) = new Row(None, a toArray)
  
  val empty: Row = new Row(None, Array.empty)
}