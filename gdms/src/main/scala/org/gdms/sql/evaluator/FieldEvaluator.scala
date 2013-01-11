/*
 * The GDMS library (Generic Datasources Management System)
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

package org.gdms.sql.evaluator

import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.sql.engine.UnknownFieldException

/**
 * Evaluator for fields. The index is initialized by the Projection or WhereFilter Command.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class FieldEvaluator(name: String, table: Option[String] = None) extends Evaluator {
  def eval = _(index)
  var sqlType = -1
  var index: Int = -1
  override def doValidate() = index match {
    case -1 => throw new UnknownFieldException(name)
    case _ =>
  }
  override def toString = "Field(" + (if (table.isDefined) table.get + "." else "") + name + ")"
  
  def duplicate: FieldEvaluator = {
    val c = copy()
    c.sqlType = sqlType
    c.index = index
    c
  }
  
  override def doCleanUp() = {
    sqlType = -1
    index = -1
  }
}