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
package org.gdms.sql.engine.commands

import org.gdms.sql.evaluator.Expression
import org.gdms.sql.evaluator.AggregateEvaluator
import org.gdms.data.values.Value
import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.sql.evaluator.Evaluators._
import org.orbisgis.progress.ProgressMonitor
import scala.collection.mutable.HashMap

/**
 * Command for evaluating a list of aggregated expressions over sub-commands.
 *
 * This command evaluates the aggregate for every row and then returns a single row
 * with the aggregated results.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
class AggregateCommand(expression: Seq[(Expression, Option[String])], grouping: Seq[(Expression, Option[String])]) extends Command with ExpressionCommand {
  
  protected def doWork(r: Iterator[RowStream])(implicit pm: Option[ProgressMonitor]) = {
    pm.map(_.startTask("Aggregating", 0))
    val res = (if (grouping.isEmpty) {
        // there is no grouping expressions, i.e. no GROUP BY, only aggregated expressions
        // this will return a single row
        doEvaluation(Nil, r.next)
      }
    else {
      // first we group the input
      // then for each group we evaluate the aggregate expressions
      // this will return one row per group
      group(r.next) flatMap(p => doEvaluation(p._1, p._2))
    })
    pm.map(_.endTask)
    res
  }
  
  // holds the grouped rows as a list, associated to a value of the grouping
  // expression (the Seq[Value])
  private var groups = new HashMap[Seq[Value], List[Row]]
  
  private def group(i: RowStream): Iterator[(Seq[Value], RowStream)] = {
    // add all input rows to the map
    i foreach (add)
    
    // returns the groups
    groups.toStream.map(p => (p._1,p._2.toIterator)).par toIterator
  }
  
  /**
   * Adds a row to the grouping map
   */
  private def add(r: Row) {
    // evaluate the grouping expression
    val h = grouping map (_._1.evaluate(r));
    
    // put it at the right place in the map
    groups.put(h, r :: groups.get(h).getOrElse(Nil))
  }
  
  /**
   * Evaluates aggregates on the group of values
   * @param g the grouping Row
   * @param i the Row that were grouped together
   */
  private def doEvaluation(g: Seq[Value], i: RowStream) = {
    def findAggregateFunctions(e: Expression): Seq[Expression] = {
      e match {
        case a @ agg(_, _) => a.duplicate :: Nil
        case b => b.children flatMap (findAggregateFunctions)
      }
    }
    // gets all aggregate functions
    val expCopy = expression map (_._1) flatMap(findAggregateFunctions)
    
    // evaluates all and forget the results:
    i foreach { scalarExecute(_, expCopy) }

    // returns a single row, with both the grouping expression and the result of the
    // evaluation of the aggregated functions
    List(row(g, expCopy)).toIterator
  }
  
  // evaluation method
  private def scalarExecute(s: Row, exp: Seq[Expression]): Unit = exp foreach( _.evaluate(s) )
  
  // builds a row of aggregated final values from the aggregate expressions
  private def row(g: Seq[Value], exp: Seq[Expression]) = Row(g ++ exp.flatMap(mapAggregateToResults))
  
  private def mapAggregateToResults(e: Expression): Seq[Value] = {
   e.evaluator match {
     // gets the final value of an aggregate
      case a: AggregateEvaluator => a.finalValue() :: Nil
      case d => d.childExpressions flatMap(mapAggregateToResults)
    }
  }

  // expressions to be initialized by ExpressionCommand
  val exp = (expression ++ grouping) map (_._1)

  // the result of this command is all available fields, i.e. grouped expressions + aggregated values
  override def getMetadata = SQLMetadata("", Expression.metadataFor(grouping ++ expression))
}
