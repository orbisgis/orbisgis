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

package org.gdms.sql.engine.commands

import scalaz.concurrent.Promise
import org.gdms.data.types.IncompatibleTypesException
import org.gdms.data.types.Type
import org.gdms.data.types.TypeFactory
import org.gdms.sql.engine.GdmSQLPredef._
import collection.JavaConversions._
import org.gdms.sql.evaluator.Expression
import scalaz.Scalaz._

/**
 * Command that implements a parallel merge sort.
 * 
 * The algorithm works in three steps:
 * <ul>
 * <li>First, all incoming sets of rows are sorted, using the usual Java modified mergesort algorithm. Sets are sorted
 * concurrently, each sort is in guarranteed O(n*long(n)).</li>
 * <li>Then (actually, probably while the sorts are running), the list of sets is arranged into a signe resulting set.
 * One set out of two is ask to merge (see below) with the other once it has finished running its inner sort. This is
 * done until there is only one set left.</li>
 * <li>Finally, the actual merge between two sorted lists is done with the usual merge algorithm. This is roughly O(n).</li>
 * </ul>
 * @author Antoine Gourlay
 * @param names a sequence of (String, Boolean). The String is the name of a field to use for the sort, and the Boolean
 *    is True if the sort is descending, False if it is ascending.
 */
class MergeSortCommand(names: Seq[(Expression, Boolean)]) extends Command with ExpressionCommand {
  
  val exp = names map (_._1)
  
  protected override final def doPrepare = {
    super.doPrepare
    exp foreach { e =>
      // maybe the field cannot be sorted
      val code = e.evaluator.sqlType
      if (code == Type.GEOMETRY || code == Type.RASTER) {
        throw new IncompatibleTypesException("Cannot sort using the expression '" + e.evaluator + "' because it is of type " +
                                             TypeFactory.getTypeName(code))
      }
    }
  }

  protected final def doWork(r: Iterable[Iterable[Promise[Iterable[Row]]]]) = {
    // sorting of the groups
    val sorted = r.head map (_ map (sort))
    
    divideAndConquer(sorted) :: Nil
  }
  
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
  
  private def divideAndConquer(i: Iterable[Promise[Iterable[Row]]]): Promise[Iterable[Row]] = {
    // simple binary division of merge tasks
    
    val a = i.head
    val j = i.tail
    j.headOption match {
      case None => a
      case Some(b) => {
          // merge a & b into abis
          val abis = a map (it => merge(it, b.get))
          if (j.tail.nonEmpty) {
            // if there is more, we merge abis and the result of the merge of the rest
            divideAndConquer(j.tail) map (it => merge(it, abis.get))
          } else ( abis )
        }
    }
  }
  
  private def merge(l: Iterable[Row], r: Iterable[Row]): Iterable[Row] = {
    // simple merge ; supposed to be roughly in O(n)
        
    def next(d: Iterable[Row], e: Iterable[Row]): Stream[Row] = {
      // if any list is empty, we return the other, it is already sorted
      if (d.isEmpty) { e toStream }
      else if (e.isEmpty) { d toStream }
      else {
        // else we compare and keep the lowest one of the two (ASC by default)
        val x = d.head
        val y = e.head
        val c = order.compare(x, y)
        if (c < 0) {
          x #:: next(d.tail, e)
        } else if (c > 0) {
          y #:: next(d, e.tail)
        } else {
          // they are equal, we directly keep them both, it saves a call to next
          x #:: y #:: next(d.tail, e.tail)
        }
      }
    }
    
    // force actually computes the whole stream
    next(l, r) force
  }
  
  // dumps into an array and sorts the array using the usual java modified mergesort
  // guaranteed O(n*log(n))
  private def sort(it: Iterable[Row]): Iterable[Row] =  it.toList.sorted
}
