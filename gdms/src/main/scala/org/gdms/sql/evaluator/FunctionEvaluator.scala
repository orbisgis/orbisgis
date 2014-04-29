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
import org.gdms.sql.function.{FunctionValidator, FunctionException ,ScalarFunction, ScalarArgument}

/**
 * Evaluator for GDMS scalar functions.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class FunctionEvaluator(name: String, l: List[Expression]) extends Evaluator with DsfEvaluator {
  var f: ScalarFunction = null
  def eval = s => f.evaluate(dsf, l map ( _.evaluate(s)): _*)
  def sqlType = f.getType(l.map( _.evaluator.sqlType ) toArray)
  override val childExpressions = l
  override def doValidate() = {
    if (f == null) throw new FunctionException("Internal error: failed to initialized function for '" + name + "'.")
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
  override def toString = if (f == null) name else f.getName + "(" + l + ")"
  
  def duplicate: FunctionEvaluator = {
    val ret = FunctionEvaluator(name, l map (_.duplicate))
    if (dsf != null) {
      ret.dsf = dsf
      // if initialized, set a new the function instance on duplication - #699
      if (f != null) {
        ret.f = dsf.getFunctionManager.getFunction(name).asInstanceOf[ScalarFunction]
      }
    }
    ret
  }  
  override def doCleanUp() = {
    super.doCleanUp()
    f == null
  }
}
