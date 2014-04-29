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

import org.gdms.data.types.Type
import org.gdms.data.values.{Value, ValueFactory}
import org.gdms.sql.function.{AggregateFunction, FunctionException, FunctionValidator, ScalarArgument}

/**
 * Evaluator for GDMS Aggregated functions.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class AggregateEvaluator(f: AggregateFunction, l: List[Expression]) extends Evaluator with DsfEvaluator {
  def eval = s => {
    f.evaluate(dsf, l map ( _.evaluate(s)): _*)
    ValueFactory.createNullValue[Value]
  }
  val finalValue = () => f.getAggregateResult
  def sqlType = f.getType(l.map(_.evaluator.sqlType) toArray)
  override val childExpressions = l
  override def doPreValidate() = {
    if (f == null) throw new FunctionException("The function does not exist.")
  }
  override def doValidate() = {
    val fs = FunctionValidator.failIfTypesDoNotMatchSignature(l.map(_.evaluator.sqlType) toArray,
                                                              f.getFunctionSignatures)
    
    // infers the type of direct NULL values as the expected type of the first
    // function signature that matches
    l.zip(fs.getArguments) foreach { a => a._1.evaluator.sqlType match {
        case Type.NULL =>
          val targetType = a._2.asInstanceOf[ScalarArgument].getTypeCode
          a._1.evaluator = CastEvaluator(Expression(a._1.evaluator), targetType)
        case _ =>
      }
    }
  }
  override def toString = "Func(" + f.getName + "(" + l + "))"
  
  def duplicate: AggregateEvaluator = {
    if (dsf == null) {
      throw new FunctionException("Internal Error: duplicating an uninitialized AggregateEvaluator")
    } else {
      AggregateEvaluator(dsf.getFunctionManager.getFunction(f.getName).asInstanceOf[AggregateFunction], l map (_.duplicate))
    }
  }
}