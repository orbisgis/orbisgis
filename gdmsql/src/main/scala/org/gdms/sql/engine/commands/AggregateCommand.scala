/** OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
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
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
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
  protected def doWork(r: Iterator[RowStream]) = {
    if (grouping.isEmpty) doEvaluation(Nil, r.next)
    else {
      group(r.next) flatMap(p => doEvaluation(p._1, p._2))
    }
  }
  
  private var groups = new HashMap[Seq[Value], List[Row]]
  
  private def group(i: RowStream): Iterator[(Seq[Value], RowStream)] = {
    i foreach (add)
    groups.toStream.map(p => (p._1,p._2.toIterator)).par toIterator
  }
  
  private def add(r: Row) {
    val h = grouping map (_._1.evaluate(r));
    groups.put(h, r :: groups.get(h).getOrElse(Nil))
  }
  
  private def doEvaluation(g: Seq[Value], i: RowStream) = {
    def findAggregateFunctions(e: Expression): Seq[Expression] = {
      e.evaluator match {
        case a: AggregateEvaluator => Expression(a.duplicate) :: Nil
        case e => e.childExpressions flatMap (findAggregateFunctions)
      }
    }
    val expCopy = expression map (_._1) flatMap(findAggregateFunctions)
    // evaluates all and forget the results
    i foreach { scalarExecute(_, expCopy) }

      
    List(row(g, expCopy)).toIterator
  }
  
  // evaluation method
  private def scalarExecute(s: Row, exp: Seq[Expression]): Unit = exp foreach( _.evaluate(s) )
  
  // builds a row from the aggregated values
  private def row(g: Seq[Value], exp: Seq[Expression]) = Row(g ++ exp.flatMap(mapAggregateToResults))
  
  private def mapAggregateToResults(e: Expression): Seq[Value] = {
    e.evaluator match {
      case a: AggregateEvaluator => a.finalValue() :: Nil
      case d => d.childExpressions flatMap(mapAggregateToResults)
    }
  }

  val exp = (expression ++ grouping) map (_._1)

  override def getMetadata = SQLMetadata(children.head.getMetadata.table, Expression.metadataFor(grouping ++ expression))
}
