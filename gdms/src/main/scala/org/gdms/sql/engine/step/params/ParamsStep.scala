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

package org.gdms.sql.engine.step.params

import java.util.Properties
import scala.collection.mutable.{Map => MutMap}
import org.gdms.data.values.Value
import org.gdms.sql.engine.{AbstractEngineStep, SemanticException}
import org.gdms.sql.engine.logical.LogicPlanOptimizer
import org.gdms.sql.engine.operations.{Operation, ExpressionOperation, Scan, ParamTable, Projection}
import org.gdms.sql.evaluator.{StaticEvaluator, param, FieldEvaluator, star, StarFieldEvaluator}

/**
 * Param replacement step: Replaces all parameters with values
 * 
 * Parameters are replaced with constant evaluators, field evaluators of table scans, and checked to
 * be sure no parameter is left unset.
 * 
 * @author Antoine Gourlay
 * @since 2.0
 */
class ParamsStep(vParams: MutMap[String, Value], fParams: MutMap[String, String],
                 tParams: MutMap[String, String]) 
extends AbstractEngineStep[Operation, Operation]("Parameter remplacement") with LogicPlanOptimizer {

  def doOperation(op: Operation)(implicit p: Properties): Operation = {
    def processExpressionOp(e: ExpressionOperation) = {
      e.expr foreach { ee => ee :: ee.allChildren foreach { ex => ex match {
            case star(vals, t) =>
              val clean = vals map { _ match {
                  case Right(pName) => fParams.get(pName) match {
                      case Some(par) => Left(par)
                      case None => throw new SemanticException("No value was given for parameter named '" + pName + "'!")
                    }
                  case a => a
                }}
              ex.evaluator = StarFieldEvaluator(clean, t)
            case param(m) => 
              val value = vParams.get(m)
              if (value.isDefined) {
                ex.evaluator = StaticEvaluator(value.get)
              } else {
                val field = fParams.get(m)
                if (field.isDefined) {
                  ex.evaluator = FieldEvaluator(field.get)
                } else {
                  throw new SemanticException("No value was given for parameter named '" + m + "'!")
                }
              }
            case _ =>
          }} }
      e
    }
    
    replaceOperationFromBottom(op, {
        case ParamTable(n, alias) => tParams.get(n) match {
            case Some(table) => Scan(table, alias)
            case None => throw new SemanticException("No value was given for table parameter named '" + n + "'!")
          }
        case p: Projection =>
          val newexps = p.exp map { e =>
           e._2 match {
             case Some(Right(pName)) => fParams.get(pName) match {
                      case Some(par) => (e._1, Some(Left(par)))
                      case None => throw new SemanticException("No value was given for parameter named '" + pName + "'!")
                    }
             case _ => e
           }
          }
          processExpressionOp(Projection(newexps, p.child))
        case e: ExpressionOperation => processExpressionOp(e)
        case a => a
      })
    
    op match {
      case e: ExpressionOperation => processExpressionOp(e)
      case _ =>
    }
    
    op
  }
}
