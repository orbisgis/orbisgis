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

package org.gdms.sql.evaluator

import org.gdms.data.values.{Value, ValueFactory}
import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.sql.engine.operations.Operation

/**
 * Evaluator for scalar subquery values.
 * 
 * @author Antoine Gourlay
 * @since 2.0
 */
case class QueryToScalarEvaluator(var op: Operation) extends QueryEvaluator {
  
  private var returnType: Int = -1
  def sqlType = returnType

  override val childExpressions = Nil
    
  def eval = s => {
    val ex = evalInner(s)
    if (ex.hasNext) {
      ex.next.array(0)
    } else {
      ValueFactory.createNullValue[Value]
    }
  }
  
  override def doValidate() = {
    super.doValidate()
    
    returnType = command.getMetadata.getFieldType(0).getTypeCode
  }
  
  override def doCleanUp() = {
    super.doCleanUp()
    returnType = -1
  }
  
  def duplicate: QueryToScalarEvaluator = {
    val c = QueryToScalarEvaluator(op.duplicate)
    c.dsf = dsf
    c
  }
}