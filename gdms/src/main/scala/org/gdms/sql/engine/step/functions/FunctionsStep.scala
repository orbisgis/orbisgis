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
package org.gdms.sql.engine.step.functions

import java.util.Properties
import org.gdms.data.DataSourceFactory
import org.gdms.sql.engine.{AbstractEngineStep, SemanticException}
import org.gdms.sql.engine.operations._
import org.gdms.sql.evaluator.{Expression, FieldEvaluator, FunctionEvaluator, AggregateEvaluator}
import org.gdms.sql.evaluator.Evaluators._
import org.gdms.sql.function.{AggregateFunction, ScalarFunction}
import org.gdms.sql.function.executor.ExecutorFunction
import org.gdms.sql.function.table.TableFunction

/**
 * Step P0: Function processing.
 * 
 * The function names are converted into the actual function instances.
 * This validates than scalar, aggregate and table functions exist and are in the right place.
 * 
 * The aggregates inside having clauses are pushed down to an Aggregate clause.
 * 
 * The projections are processed to convert aggregation and grouping into the corresponding query elements.
 * This validates than selected fields are referenced in the GROUP BY clause (if there is one).
 */
case object FunctionsStep extends AbstractEngineStep[(Operation, DataSourceFactory), (Operation, DataSourceFactory)]("Processing aggregates & groups") {
  def doOperation(op: (Operation, DataSourceFactory))  (implicit p: Properties): (Operation, DataSourceFactory) = {
    
    markFunctions(op._1, op._2)
    havingAggregation(op._1)
    processAggregates(op._1)
    
    op
  }
  
  private def havingAggregation(op: Operation) {
    // push down aggregates in Having into the Grouping clause
    op match {
      case f @ Filter(e, a, true) =>
        // replaces aggregate functions by fields and returns the aggregates
        var i = -2
        def replaceAggregateFunctions(e: Expression): Seq[(Expression, Option[Either[String, String]])] = {
          e match {
            case agg(f, li) => 
              val func = e.evaluator
              i = i + 1
              val name = '$' + f.getName + (if (i == -1) "" else i)
              e.evaluator = FieldEvaluator(name)
              (Expression(func), Some(Left(name))) :: Nil
            case e => e.children flatMap replaceAggregateFunctions
          }
        }
          
        val res = e flatMap (replaceAggregateFunctions) toList
        
        if (!res.isEmpty) {
          a match {
            case g @ Aggregate(e, _) => g.exp = res ::: e
            case _ => f.child = Aggregate(res, a)
          }
        }
        
      case _ => op.children map havingAggregation
    }
  }
  
  private def markFunctions(op: Operation, dsf: DataSourceFactory) {
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
  
  private def markFunctions(e: Expression, dsf: DataSourceFactory) {
    e.evaluator match {
      case fe @ FunctionEvaluator(name, l) => 
        dsf.getFunctionManager.getFunction(name)  match {
          case s: ScalarFunction => fe.f = s
          case a: AggregateFunction => e.evaluator = AggregateEvaluator(a, l)
          case e: ExecutorFunction => throw new SemanticException("The function '" + name
                                                                  + "' cannot be used here. Syntax is: EXECUTE " +
                                                                  name + "(...);")
          case t: TableFunction => throw new SemanticException("The function '" + name + "' cannot be used here." +
                                                               "Syntax is: SELECT ... FROM " + name + "(...);")
          case _ => throw new SemanticException("Unknown function: '" + name + "'.")
        }
      case _ => 
    }
    
    e foreach (markFunctions(_, dsf))
  }
  
  private def processAggregates(op: Operation) {
    op.allChildren foreach { case p @ Projection(exp, ch) =>
        var aliases: List[Either[String, String]] = Nil
        
        // replaces aggregate functions by fields and returns the aggregates
        var i = -2
        def replaceAggregateFunctions(e: (Expression, Option[Either[String, String]])): Seq[(Expression, Option[Either[String, String]])] = {
          e._1 match {
            case agg(f, _) => 
              val func = e._1.evaluator
              i = i + 1
              val name = e._2.getOrElse(Left(f.getName + (if (i == -1) "" else i)))
              aliases = name :: aliases
              e._1.evaluator = FieldEvaluator(name.left.get)
              (Expression(func), Some(name)) :: Nil
            case e => e.children flatMap (ex => replaceAggregateFunctions((ex, None)))
          }
        }
        
        var gr: Option[Grouping] = None
        var top: Operation = p
        
        // finds a grouping and its parent
        def find(ch: Operation) {
          ch match {
            case a : Grouping => gr = Some(a)
            case b : Aggregate => top = b; find(b.child)
            case l: LimitOffset => top = l; find(l.child)
            case s: Sort => top = s; find(s.child)
            case fl @ Filter(_, f, true) => top = fl; find(f)
            case _ => top = p
          }
        }
        find(ch)
        
        // process grouping
        gr match {
          case Some(group) =>
            // there is a Grouping
            
            // finds directly referenced fields/aliases in GROUP BY
            aliases = group.exp flatMap (a =>
              a._2 orElse {
                a._1 match {
                  case field(name, _) => Some(Left(name))
                  case _ => None
                }
              }
            )
          
            // finds selected items with aliases
            val fieldsAl = exp filter (_._2.isDefined)
                    
            // converts SELECT toto + 12 as titi FROM ... GROUP BY titi
            // into something like (pseudo-SQL): SELECT titi FROM ... GROUP BY toto + 12 as titi
            fieldsAl foreach {f =>
              val al = f._2.get
              if (aliases.contains(al)) {
                val t = f._1.evaluator
                // we replace the evaluator with a FieldEvaluator with the alias name
                f._1.evaluator = FieldEvaluator(al.left.get)
              
                // we replace the Field in the GROUP BY clause by the actual expression
                val alname = al.merge
                group.exp = group.exp map {g => g._1 match {
                    case field(name,_) if name == alname => (Expression(t), Some(al))
                    case _ => g
                  }}
              }
            }
          case None =>
        }
        
        val aggF = p.exp flatMap replaceAggregateFunctions
        
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
          
          top match {
            case a: Aggregate =>
              a.exp = aggF ::: a.exp
            case _ =>
              val c = new Aggregate(aggF, gr.getOrElse(p.child))
              top.children = List(c)
          }
        }
        
        if (gr.isDefined || !aggF.isEmpty) {
          // checks directly selected fields are referenced in GROUP BY clause
          exp foreach (_._1 match {
              case field(name, _) => 
                if (!aliases.contains(Left(name))) {
                  throw new SemanticException("field " + name + " cannot be selected because it is not present in the GROUP BY clause.")
                }
              case star(_,_) => throw new SemanticException("Selected alls field using the STAR '*' is not allowed with" + 
                                                            " a GROUP BY clause and/or an aggregate function.")
              case _ =>
            })}
      case _ =>
    }
  }
}
