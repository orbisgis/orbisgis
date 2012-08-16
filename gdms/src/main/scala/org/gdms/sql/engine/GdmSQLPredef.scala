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
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
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
package org.gdms.sql.engine

import java.util.Properties
import org.gdms.data.schema.Metadata
import scala.collection.mutable.ArrayOps
import org.gdms.data.values.Value
import org.gdms.sql.engine.commands.{Row, SQLMetadata}

/**
 * Holds all the predefined implicits and values for use in GdmSQL
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
object GdmSQLPredef extends GdmSQLPredefLow {
  
  type RowStream = Iterator[Row]

  // Rows
  implicit def rowToInnerArray(row: Row): Array[Value] = row.array
  val emptyRow = Row(Array.empty[Value])

  // SQLMetadata
  implicit def meToSQLMe(m: Metadata)(implicit s: SQLMetadata) = SQLMetadata(s.table, m)
  implicit def sqlMeTome(s: SQLMetadata) = s.m
  
  // Engine flags
  val Flags = EngineFlags
  
  // Engine flag utility methods
  def isPropertyValue(name: String, value: String)(implicit p: Properties) = {
    p != null && {p.getProperty(name) match {
        case a if a == value => true
        case _ => false
      }}
  }
  
  def isPropertyTurnedOn(name: String)(implicit p: Properties) = {
    isPropertyValue(name, "true")
  }
  
    
  def isPropertyTurnedOff(name: String)(implicit p: Properties) = {
    isPropertyValue(name, "false")
  }
}

/**
 * Holds the predefined implicits with a lower visibility than the ones in {@link GdmSQLPredef}.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
sealed class GdmSQLPredefLow {

  // Rows
  //implicit def rowToSeq(row: Row): Seq[Value] = row.array
  implicit def rowToInnerArrayOps(row: Row): ArrayOps[Value] = refArrayOps(row.array)
}