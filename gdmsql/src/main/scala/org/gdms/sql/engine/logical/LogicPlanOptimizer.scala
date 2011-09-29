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

package org.gdms.sql.engine.logical

import org.gdms.sql.engine.operations._
import org.gdms.sql.evaluator.AndEvaluator
import org.gdms.sql.evaluator.Evaluator
import org.gdms.sql.evaluator.FunctionEvaluator
import org.gdms.sql.evaluator.Expression
import org.gdms.sql.function.SpatialIndexedFunction

object LogicPlanOptimizer {

  def matchExpression(e: Expression, c: Evaluator => Boolean, f: Expression => Unit): Unit = {
    if (c(e.evaluator)) (f(e))
    e foreach (matchExpression(_, c, f))
  }
  
  def matchExpressionAndAny(e: Expression, c: Evaluator => Boolean, f: Expression => Unit): Unit = {
    if (c(e.evaluator)) {
      f(e)
    } else if (e.evaluator.isInstanceOf[AndEvaluator]) {
      e foreach (matchExpressionAndAny(_, c, f))
    }
  }
  
  def replaceEvaluator(e: Expression, c: Evaluator => Boolean, f: Evaluator => Evaluator): Unit = {
    matchExpression(e, c, i => i.evaluator = f(i.evaluator))
  }
  
  def matchOperation(o: Operation, c: Operation => Boolean, f: Operation => Unit): Unit = {
    if (c(o)) f(o)
    o.children foreach (matchOperation(_, c, f))
  }
  
  def matchOperationFromBottom(o: Operation, c: Operation => Boolean, f: Operation => Unit): Unit = {
    o.children foreach (matchOperation(_, c, f))
    if (c(o)) f(o)
  }
  
  def replaceOperation(o: Operation, c: Operation => Boolean, f: Operation => Operation): Unit = {
    o.children = o.children map { ch => if (c(ch)) f(ch) else ch }
    o.children foreach (replaceOperation(_, c, f))
  }
  
  def replaceOperationFromBottom(o: Operation, c: Operation => Boolean, f: Operation => Operation): Unit = {
    o.children foreach (replaceOperation(_, c, f))
    o.children = o.children map { ch => if (c(ch)) f(ch) else ch }
  }
  
  
  def pushDownSelections(o: Operation): Unit = {
    replaceOperationFromBottom(o, _.isInstanceOf[Filter], {op => 
        val filter = op.asInstanceOf[Filter]
        var nexexps = Nil
        replaceEvaluator(filter.e, _.isInstanceOf[AndEvaluator], {ev =>
            ev
          })
        // not finished
        op
      })
  }
  
  def optimizeCrossJoins(o: Operation): Unit = {
    replaceOperationFromBottom(o, {ch =>
        // gets Filter -> Join
        ch.isInstanceOf[Filter] && ch.children.find(_.isInstanceOf[Join]).isDefined
      }, {ch =>
        // replace Cross Join with a Filter above to a Inner Join on the filtering expression
        
        val join = ch.children.find(_.isInstanceOf[Join]).get.asInstanceOf[Join]
        val filter = ch.asInstanceOf[Filter]
        join.joinType = join.joinType match {
          case Cross() => Inner(filter.e)
          case Inner(ex, s) => Inner(ex & filter.e, s)
        }
        join
      })
  }
  
  def optimizeSpatialIndexedJoins(o: Operation) {
    matchOperationFromBottom(o, {ch =>
        // gets Join(Inner(_))
        ch.isInstanceOf[Join] && (ch.asInstanceOf[Join].joinType match {
            case Inner(_, _) => true
            case _ => false
          })
      }, {ch =>
        // finds if there is a SpatialIndexedFunction in the Expression
        val join = ch.asInstanceOf[Join]
        join.joinType match {
          case a @ Inner(ex, false) => {
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
