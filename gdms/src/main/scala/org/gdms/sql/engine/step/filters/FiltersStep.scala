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
    (f :: f.allChildren) foreach { _.evaluator match {
        case e : QueryEvaluator => e.op = processOp(e.op)
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
      case f @ Filter(e, sc: Scan, false) => {
          // checks if it is a optimizable spatial filter
          
          matchExpressionAndAny(e, {
              case fun @ func(_, _: SpatialIndexedFunction,_) => {
                  
                  matchExpressionAndAny(f.e, {ex => ex match {
                        case func(_, _: SpatialIndexedFunction,_) => 
                          // we have a spatial indexed filter that is not a join
                          // Filter <- Scan
                          // will become
                          // Join(inner, spatial) <- IndexScan
                          //                      <- ValuesScan
                          var inc = -1;
                          // we keep fields and replace the rest with new fields on constant expressions
                          val se: Seq[Expression] = ex.children flatMap {c => c match {
                              case field(_,_) => None
                              case _ => 
                                inc = inc + 1
                                val oldeval = c.evaluator
                                c.evaluator = FieldEvaluator("$exp" + inc, Some("$$"))
                                Some(Expression(oldeval))
                                
                            }}
                            
                          val va = ValuesScan(Seq(se), Some("$$"))
                          val isc = IndexQueryScan(sc.table, sc.alias)
                          val j = Join(Inner(e, true), isc, va)
                          f.children = List(j)
                        case _ =>
                      }
                    })
                }
              case _ =>
            })
        }
      case _ =>
    }
  }
}
