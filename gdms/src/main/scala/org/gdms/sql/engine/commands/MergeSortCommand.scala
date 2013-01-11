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

import org.gdms.data.types.{IncompatibleTypesException, Type, TypeFactory}
import org.gdms.sql.engine.GdmSQLPredef._
import collection.JavaConversions._
import org.gdms.sql.evaluator.Expression
import org.orbisgis.progress.ProgressMonitor

/**
 * Command that implements a simple non-parallel merge sort.
 * 
 * @author Antoine Gourlay
 * @param names a sequence of (String, Boolean). The String is the name of a field to use for the sort, and the Boolean
 *    is True if the sort is descending, False if it is ascending.
 */
class MergeSortCommand(names: Seq[(Expression, Boolean)]) extends Command with ExpressionCommand {
  
  val exp = names map (_._1)
  
  protected override final def doPrepare() = {
    // init sorting expressions
    super.doPrepare()
    
    exp foreach { e =>
      // checks if the expressions can be sorted
      val code = e.evaluator.sqlType
      if ((code & Type.GEOMETRY) != 0 || code == Type.RASTER) {
        throw new IncompatibleTypesException("Cannot sort using the expression '" + e.evaluator + "' because it is of type " +
                                             TypeFactory.getTypeName(code))
      }
    }
  }

  protected final def doWork(r: Iterator[RowStream])(implicit pm: Option[ProgressMonitor]) = {
    pm.map(_.startTask("Sorting", 0))
    // dumps into an array and sorts the array using the usual java modified mergesort
    // guaranteed O(n*log(n))
    val res = r.next.toList.sorted.toIterator
    
    pm.map(_.endTask)
    res
  }
  
  // this is used by .sorted on the list of rows
  private implicit val order: Ordering[Row] = new Ordering[Row] {
    def compare(x: Row, y: Row): Int = {
      // we drop indexes which leads to equality between the two rows
      val r = names.dropWhile(i => i._1.evaluate(x).compareTo(i._1.evaluate(y)) == 0)
      r.headOption match {
        case None => 0 // the rows are equals
        case Some((a, b)) => ( (if (b) -1 else 1) * a.evaluate(x).compareTo(a.evaluate(y)) ) // this expression gives the final order between x and y
      }
    }
  }
}