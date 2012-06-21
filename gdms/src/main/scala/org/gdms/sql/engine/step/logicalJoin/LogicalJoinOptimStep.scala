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
package org.gdms.sql.engine.step.logicalJoin

import java.util.Properties
import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.sql.engine.AbstractEngineStep
import org.gdms.sql.engine.logical.LogicPlanOptimizer
import org.gdms.sql.engine.operations._
import org.gdms.sql.evaluator.func
import org.gdms.sql.function.SpatialIndexedFunction

/**
 * Step 3: Basic join optimizations that do not require access to the DSF.
 * 
 * Filters above cross joins are converted into inner joins on the filtering expression.
 * Inner joins on spatial indexed functions are tagged as 'spatial indexed' for later.
 * 
 */
case object LogicalJoinOptimStep extends AbstractEngineStep[Operation, Operation]("Basic join replacement optimization")
                                    with LogicPlanOptimizer {
  
  def doOperation(op: Operation)(implicit p: Properties): Operation = {
    if (!isPropertyTurnedOff(Flags.OPTIMIZEJOINS)) {
      if (isPropertyTurnedOn(Flags.EXPLAIN)) {
        LOG.info("Optimizing cross joins.")
      }
      optimizeCrossJoins(op)
      
      if (isPropertyTurnedOn(Flags.EXPLAIN)) {
        LOG.info("Tagging spatial joins.")
      }
      optimizeSpatialIndexedJoins(op)
    }
    op
  }
 
  /**
   * Translates a ^(Filter CrossJoin) into an InnerJoin on the filtering expression.
   */
  private def optimizeCrossJoins(o: Operation): Unit = {
    replaceOperationFromBottom(o, {
        // replace Cross Join with a Filter above to a Inner Join on the filtering expression
        case Filter(c,j @ Join(joinType,_,_),_) => {
            j.joinType = joinType match {
              case Cross => Inner(c)
              case Inner(ex, s, a) => Inner(ex & c, s, a)
              case OuterLeft(cond) => OuterLeft(cond.map(_ & c))
              case OuterFull(cond) => OuterLeft(cond.map(_ & c))
              case Natural => Natural
            }
            
            j
          }
        case a => a
      })
  }
  
  /**
   * Tags an InnerJoin with a SpatialIndexedFunction in its expression as spatial.
   */
  private def optimizeSpatialIndexedJoins(o: Operation) {
    matchOperationFromBottom(o, {
        // finds if there is a SpatialIndexedFunction in the Expression
        case Join(i @ Inner(ex,false,None), c, _) => c match {
            case _: Join =>
            case _ => matchExpressionAndAny(ex, {
                  // we have a spatial indexed join
                  case func(_,_: SpatialIndexedFunction,_) => i.spatial = true
                  case _ =>
                })
          }
        case _ =>
      })
  }
}
