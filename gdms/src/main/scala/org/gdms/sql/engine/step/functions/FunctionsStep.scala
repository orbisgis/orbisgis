/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
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
package org.gdms.sql.engine.step.functions

import java.util.Properties
import org.gdms.data.DataSourceFactory
import org.gdms.sql.engine.AbstractEngineStep
import org.gdms.sql.engine.SemanticException
import org.gdms.sql.engine.operations._
import org.gdms.sql.evaluator._
import org.gdms.sql.function.AggregateFunction
import org.gdms.sql.function.ScalarFunction
import org.gdms.sql.function.executor.ExecutorFunction
import org.gdms.sql.function.table.TableFunction

/**
 * Step P0: Function processing.
 * 
 * The function names are converted into the actual function instances.
 * This validates than scalar, aggregate and table functions exist and are in the right place.
 * 
 * The projections are processed to convert aggregation and grouping into the corresponding query elements.
 * This validates than selected fields are referenced in the GROUP BY clause (if there is one).
 */
case object FunctionsStep extends AbstractEngineStep[(Operation, DataSourceFactory), (Operation, DataSourceFactory)]("Processing aggregates & groups") {
  def doOperation(op: (Operation, DataSourceFactory))  (implicit p: Properties): (Operation, DataSourceFactory) = {
    
    markFunctions(op._1, op._2)
    processAggregates(op._1)
    
    op
  }
  
  def markFunctions(op: Operation, dsf: DataSourceFactory) {
    (op :: op.allChildren) foreach { 
      case c @ CustomQueryScan(name, exp, _, _) => {
          val f = dsf.getFunctionManager.getFunction(name)
          f  match {
            case _: ScalarFunction | _: AggregateFunction => throw new SemanticException("The function '" + name + "' cannot be used here. Syntax is:" +
                                                                                         " SELECT " + name + "(...) FROM myTable;")
            case e: ExecutorFunction => throw new SemanticException("The function '" + name
                                                                    + "' cannot be used here. Syntax is: EXECUTE " +
                                                                    name + "(...);")
            case t: TableFunction => c.function = t
            case _ => throw new SemanticException("Unknown function: '" + name + "'.")
          }
        }
        exp foreach (markFunctions(_, dsf))
      case ExpressionOperation(exp) =>
        exp foreach (markFunctions(_, dsf))
      case _ =>
    }
  }
  
  def markFunctions(e: Expression, dsf: DataSourceFactory) {
    e.evaluator match {
      case fe @ FunctionEvaluator(name, l) => {
          val f = dsf.getFunctionManager.getFunction(name)
          f  match {
            case s: ScalarFunction => fe.f = s
            case a: AggregateFunction => e.evaluator = AggregateEvaluator(a, l)
            case e: ExecutorFunction => throw new SemanticException("The function '" + name
                                                                    + "' cannot be used here. Syntax is: EXECUTE " +
                                                                    name + "(...);")
            case t: TableFunction => throw new SemanticException("The function '" + name + "' cannot be used here." +
                                                                 "Syntax is: SELECT ... FROM " + name + "(...);")
            case _ => throw new SemanticException("Unknown function: '" + name + "'.")
          }
        }
      case _ => 
    }
    
    e foreach (markFunctions(_, dsf))
  }
  
  def processAggregates(op: Operation) {
    op.allChildren foreach { case p @ Projection(exp, ch) =>
        // replaces aggregate functions by fields and returns the aggregates
        var i = -2
        def replaceAggregateFunctions(e: (Expression, Option[String])): Seq[(Expression, Option[String])] = {
          e._1 match {
            case agg(f, li) => {
                val func = e._1.evaluator
                i = i + 1
                val name = e._2.getOrElse(f.getName + (if (i == -1) "" else i))
                e._1.evaluator = FieldEvaluator(name)
                (Expression(func), Some(name)) :: Nil}
            case e => e.children flatMap (ex => replaceAggregateFunctions((ex, None)))
          }
        }
        
        var gr: Option[Grouping] = None
        var top: Operation = p
        
        // finds a grouping and its parent
        def find(ch: Operation) {
          ch match {
            case a @ Grouping(_,_) => gr = Some(a)
            case l: LimitOffset => top = l; find(l.child)
            case s: Sort => top = s; find(s.child)
            case fl @ Filter(_, f, true) => top = fl; find(f)
            case _ => top = p
          }
        }
        find(ch)
        
        // process grouping
        if (gr.isDefined) {
          // there is a Grouping
          val group = gr.get
          
          // directly referenced fields/aliases in GROUP BY
          val aliases = group.exp flatMap (_._1 match {
              case field(name,_) => Some(name)
              case _ => None
            })
          
          // selected items with aliases
          val fieldsAl = exp filter (_._2.isDefined)
                    
          // converts SELECT toto + 12 as titi FROM ... GROUP BY titi
          // into something like (pseudo-SQL): SELECT titi FROM ... GROUP BY toto + 12 as titi
          fieldsAl foreach {f =>
            val al = f._2.get
            if (aliases.contains(al)) {
              val t = f._1.evaluator
              // we replace the evaluator with a FieldEvaluator with the alias name
              f._1.evaluator = FieldEvaluator(al)
              
              // we replace the Field in the GROUP BY clause by the actual expression
              group.exp = group.exp map {g => g._1 match {
                  case field(name,_) if name == al => (Expression(t), Some(al))
                  case _ => g
                }}
            }
          }
          
          // directly selected fields
          val selFields = exp flatMap (_._1 match {
              case field(name, _) => Some(name)
              case star(_,_) => throw new SemanticException("Selected alls field using the STAR '*' is not allowed with a GROUP BY clause.")
              case _ => None
            })
          
          // check directly selected fields are referenced in GROUP BY clause
          selFields foreach {n => 
            if (!aliases.contains(n)) {
              throw new SemanticException("field " + n + " cannot be selected because it is not present in the GROUP BY clause.")
            }
          }
        }
        
        val aggF = p.exp flatMap (replaceAggregateFunctions)
        
        if (!aggF.isEmpty) {
          // special case of a Projection with Aggregated functions
          // an Aggregate is inserted afterwards, and the functions are
          // replaces with fields in the Projection
          // there is some AggregateEvaluator expressions
          
          // we process projected fields. If they stayed this far, it means they are indeed referenced in the GROUP BY
          // and they may need to be kept by the aggregate.
          p.exp flatMap (_._1.allChildren) foreach { e => e match {
              case field(s, _) => if (s.startsWith("$")) {
                  e.evaluator = FieldEvaluator(s.substring(1))
                }
              case _ =>
            }
          }
          
          val c = new Aggregate(aggF, gr.getOrElse(p.child))
          
          top.children = List(c)
        }
      case _ =>
        
    }
  }
}
