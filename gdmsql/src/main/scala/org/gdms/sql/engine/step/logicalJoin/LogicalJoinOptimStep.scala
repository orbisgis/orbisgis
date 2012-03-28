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
package org.gdms.sql.engine.step.logicalJoin

import java.util.Properties
import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.sql.engine.AbstractEngineStep
import org.gdms.sql.engine.logical.LogicPlanOptimizer
import org.gdms.sql.engine.operations._
import org.gdms.sql.evaluator.FunctionEvaluator
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
    replaceOperationFromBottom(o, {ch =>
        // gets Filter -> Join
        ch.isInstanceOf[Filter] && ch.children.find(_.isInstanceOf[Join]).isDefined
      }, {ch =>
        // replace Cross Join with a Filter above to a Inner Join on the filtering expression
        
        val join = ch.children.find(_.isInstanceOf[Join]).get.asInstanceOf[Join]
        val filter = ch.asInstanceOf[Filter]
        join.joinType = join.joinType match {
          case Cross() => Inner(filter.e)
          case Inner(ex, s, a) => Inner(ex & filter.e, s, a)
          case OuterLeft(cond) => OuterLeft(cond.map(_ & filter.e))
          case OuterFull(cond) => OuterLeft(cond.map(_ & filter.e))
          case a @ Natural() => a
        }
        join
      })
  }
  
  /**
   * Tags an InnerJoin with a SpatialIndexedFunction in its expression as spatial.
   */
  private def optimizeSpatialIndexedJoins(o: Operation) {
    matchOperationFromBottom(o, {ch =>
        // gets Join(Inner(_))
        ch.isInstanceOf[Join] && (ch.asInstanceOf[Join].joinType match {
            case Inner(_, _, None) => {
                ch.children.filter(_.isInstanceOf[Join]).isEmpty
              }
            case _ => false
          })
      }, {ch =>
        // finds if there is a SpatialIndexedFunction in the Expression
        val join = ch.asInstanceOf[Join]
        join.joinType match {
          case a @ Inner(ex, false, None) => {
              matchExpressionAndAny(ex, {e =>
                  e.isInstanceOf[FunctionEvaluator] && e.asInstanceOf[FunctionEvaluator].f.isInstanceOf[SpatialIndexedFunction]
                }, {e=>
                  // we have a spatial indexed join
                  a.spatial = true
                })
            }
          case _ => 
        }
      })
  }
}
