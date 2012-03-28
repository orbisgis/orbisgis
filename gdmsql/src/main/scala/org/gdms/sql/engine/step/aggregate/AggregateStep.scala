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
package org.gdms.sql.engine.step.aggregate

import java.util.Properties
import org.gdms.sql.engine.AbstractEngineStep
import org.gdms.sql.engine.SemanticException
import org.gdms.sql.engine.operations._
import org.gdms.sql.evaluator._

/**
 * Step 2: Aggregation & Grouping.
 * 
 * The projections are processed to convert aggregation and grouping into the corresponding query elements.
 * This validates than selected fields are referenced in the GROUP BY clause (if there is one).
 */
case object AggregateStep extends AbstractEngineStep[Operation, Operation]("Processing aggregates & groups") {
  def doOperation(op: Operation)(implicit p: Properties): Operation = {
    op.allChildren foreach { case p @ Projection(exp, ch) =>
        def replaceAggregateFunctions(e: (Expression, Option[String])): Seq[(Expression, Option[String])] = {
          e._1 match {
            case agg(f, li) => {
                val func = e._1.evaluator
                val name = e._2.getOrElse(f.getName)
                e._1.evaluator = FieldEvaluator("$" + name)
                (Expression(func), Some(name)) :: Nil}
            case e => e.children flatMap (ex => replaceAggregateFunctions((ex, None)))
          }
        }
        
        var gr: Option[Grouping] = None
        var sr: Option[Sort] = None
        
        ch match {
          case a @ Grouping(_,_) => gr = Some(a)
          case s @ Sort(_, a @ Grouping(_, _)) => gr = Some(a); sr = Some(s);
          case _ =>
        }
        
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
                    
          // directly selected fields
          val selFields = exp flatMap (_._1 match {
              case field(name, _) => Some(name)
              case _ => None
            })
          
          // check directly selected fields are referenced in GROUP BY clause
          selFields foreach {n => 
            if (!aliases.contains(n)) {
              throw new SemanticException("field " + n + " cannot be selected because it is not present in the GROUP BY clause.")
            }
          }
          
          // converts SELECT toto + 12 as titi FROM ... GROUP BY titi
          // into something like (pseudo-SQL): SELECT titi FROM ... GROUP BY toto + 12 as titi
          fieldsAl foreach {f =>
            if (aliases.contains(f._2.get)) {
              val t = f._1.evaluator
              // we replace the evaluator with a FieldEvaluator with the alias name
              f._1.evaluator = FieldEvaluator(f._2.get)
              
              // we replace the Field in the GROUP BY clause by the actual expression
              group.exp.find(g => g._1.evaluator match {
                  case a: FieldEvaluator => f._2.get == a.name
                  case _ => false
                }).get._1.evaluator = t
            }
          }
        }
        
        val aggF = p.exp flatMap (replaceAggregateFunctions)
        
        if (!aggF.isEmpty) {
          // special case of a Projection with Aggregated functions
          // an Aggregate is inserted afterwards, and the functions are
          // replaces with fields in the Projection
          // there is some AggregateEvaluator expressions
          
          // we get all projected fields. If they stayed this far, it means they are indeed referenced in the GROUP BY
          // and they may need to be kept by the aggregate.
          val projFields = p.exp flatMap { e => e._1 match {
              case field(s, _) => if (s.startsWith("$")) {
                  e._1.evaluator = FieldEvaluator(s.substring(1))
                  None
                } else Some(e)
              case _ => None
            }
          }
          
          val c = new Aggregate(aggF ++ projFields, gr.getOrElse(p.child))
          if (sr.isDefined) {
            sr.get.child = c
          } else {
            p.child = c
          }
        }
      case _ =>
        
    }
    op
  }
}
