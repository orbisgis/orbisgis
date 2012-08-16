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
package org.gdms.sql.engine.logical

import org.gdms.sql.engine.operations._
import org.gdms.sql.evaluator.Evaluators._
import org.gdms.sql.evaluator.Expression

trait LogicPlanOptimizer { 

  final def matchExpressionAndAny(e: Expression, f: Expression => Unit): Unit = {
    e match { 
      case a & b => {
          matchExpressionAndAny(a, f)
          matchExpressionAndAny(b, f)
        }
      case _ => f(e)
    }
  }
  
  final def matchOperation(o: Operation, c: Operation => Boolean, f: Operation => Unit): Unit = {
    if (c(o)) f(o)
    o.children foreach (matchOperation(_, c, f))
  }
  
  final def matchOperationAndStop(o: Operation, c: Operation => Boolean, f: Operation => Unit): Unit = {
    if (c(o)) f(o)
    else o.children foreach (matchOperationAndStop(_, c, f))
  }
  
  final def matchOperationFromBottom(o: Operation, c: Operation => Boolean, f: Operation => Unit): Unit = {
    o.children foreach (matchOperationFromBottom(_, c, f))
    if (c(o)) f(o)
  }
  
  final def matchOperationFromBottom(o: Operation, f: Operation => Unit): Unit = {
    o.children foreach (matchOperationFromBottom(_, f))
    f(o)
  }
  
  final def processOperationFromBottom(o: Operation, f: Operation => Unit): Unit = {
    o.children foreach (processOperationFromBottom(_, f))
    f(o)
  }
  
  final def replaceOperation(o: Operation, c: Operation => Boolean, f: Operation => Operation): Unit = {
    o.children = o.children map { ch => if (c(ch)) f(ch) else ch }
    o.children foreach (replaceOperation(_, c, f))
  }
  
  final def replaceOperationAndStop(o: Operation, c: Operation => Boolean, f: Operation => Operation): Unit = {
    o.children = o.children map { ch => if (c(ch)) f(ch) else {
        replaceOperationAndStop(ch, c, f)
        ch
      } }
  }
  
  final def replaceOperationFromBottom(o: Operation, c: Operation => Boolean, f: Operation => Operation): Unit = {
    o.children foreach (replaceOperationFromBottom(_, c, f))
    o.children = o.children map { ch => if (c(ch)) f(ch) else ch }
  }
  
  final def replaceOperationFromBottom(o: Operation, f: Operation => Operation): Unit = {
    o.children foreach (replaceOperationFromBottom(_, f))
    o.children = o.children map f
  }
  
  final def replaceOperationFromBottomAndStop(o: Operation, c: Operation => Boolean, f: Operation => Operation): Boolean = {
    var r = false
    
    o.children = o.children map { ch => 
      r = replaceOperationFromBottomAndStop(ch, c, f)
      if (!r && c(ch)) {
        r = true
        f(ch)
      } else ch
    }
    r
  }    
}