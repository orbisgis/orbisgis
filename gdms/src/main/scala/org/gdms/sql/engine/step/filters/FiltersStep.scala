/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
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

package org.gdms.sql.engine.step.filters

import java.util.Properties
import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.sql.engine.AbstractEngineStep
import org.gdms.sql.engine.logical.LogicPlanOptimizer
import org.gdms.sql.engine.operations._
import org.gdms.sql.engine.step.logicalJoin.LogicalJoinOptimStep
import org.gdms.sql.engine.step.validate.ValidationStep
import org.gdms.sql.evaluator._
import org.gdms.sql.function.SpatialIndexedFunction

/**
 * Step 4: Filter expression handling.
 * 
 * - Filter expressions on spatial indexed function (that are not joins) are converted into joins with a constant
 * table, to make use later of indexed-based joins.
 * - Processes subqueries all the way to validation (included).
 */
case object FiltersStep extends AbstractEngineStep[Operation, Operation]("filter expression optimization")
                           with LogicPlanOptimizer {

  def doOperation(op: Operation)(implicit p: Properties): Operation = {
    if (!isPropertyTurnedOff(Flags.OPTIMIZEFILTERS)) {
      if (isPropertyTurnedOn(Flags.EXPLAIN)) {
        LOG.info("Optimizing spatial indexed filter expressions with constants.")
      }
      optimizeSpatialIndexedFilterExpressions(op)
    }
    
    processSubQueries(op)
    
    op
  }
  
  def processSubQueries(o: Operation)(implicit p: Properties) {
    o.children foreach processSubQueries
    
    o match {
      case Filter(f, _, _) => processExp(f)
      case Projection(f,_) => f foreach (e => processExp(e._1))
      case Join(Inner(f,_,_),_,_) => processExp(f)
      case _ =>
    }
  }
  
  private def processExp(f: Expression)(implicit p: Properties) {
    (f :: f.allChildren) foreach { e => e.evaluator match {
        case e @ ExistsEvaluator(op) => e.o = processOp(op)
        case e @ InEvaluator(_, op) => e.o = processOp(op)
        case _ =>
      }
    }
  }
  
  private def processOp(o: Operation)(implicit p: Properties) = {
    o                    >=:
    LogicalJoinOptimStep >=:
    FiltersStep          >=:
    ValidationStep
  }
  
  /**
   * Optimises filter expressions.
   * 
   * For now, only takes care of the transformation :
   *  -     Filter(SpatialIndexedFunction(FieldA, Constant) <- ScanA 
   *    ==> Join(SpatialIndexedFunction(FieldA, FieldB)) <-  IndexScanA
   *                                                         ValuesScanB
   *    This enables Spatial Join optimization when working on a single table and a constant.
   */
  def optimizeSpatialIndexedFilterExpressions(o: Operation) {
    o.children foreach optimizeSpatialIndexedFilterExpressions
    
    o match {
      case f @ Filter(e, sc @ Scan(_,_,_), false) => {
          var ok = false
          matchExpressionAndAny(e, {ex => ex match {
                case func(_, _: SpatialIndexedFunction,_) => true
                case _ => false
              }}, {e => ok = true})
          if (ok) {
            // an index query scan on the table
            val isc = IndexQueryScan(sc.table, sc.alias)
            var va: ValuesScan = null
            
            replaceEvaluatorAndAny(f.e, {ex => ex match {
                  case func(_, _: SpatialIndexedFunction,_) => true
                  case _ => false
                }}, {ex=>
                // we have a spatial indexed filter that is not a join
                // Filter <- Scan
                // will become
                // Join(inner, spatial) <- IndexScan
                //                      <- ValuesScan
                var inc = -1;
                // we keep fields and replace the rest with new fields on constant expressions
                val se: Seq[Evaluator] = ex.childExpressions flatMap {c =>
                  c match {
                    case field(_,_) => None
                    case _ => {
                        inc = inc + 1
                        val oldeval = c.evaluator
                        c.evaluator = FieldEvaluator("$exp" + inc, Some("$$"))
                        Some(oldeval)
                      }
                  }
                } 
              
                // a constant expression value scan on the expressions created above
                va = ValuesScan((se map (new Expression(_))) :: Nil, Some("$$"))
                //j.children = v :: j.children
            
                ex 
              })
            
            // a spatial join on the filter expression
            val j = Join(Inner(e, true), isc, va)
            f.children = List(j)
          }
        }
      case _ =>
    }
  }
}
