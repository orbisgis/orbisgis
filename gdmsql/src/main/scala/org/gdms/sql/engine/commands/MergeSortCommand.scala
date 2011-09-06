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

import org.gdms.data.types.IncompatibleTypesException
import org.gdms.data.types.Type
import org.gdms.data.types.TypeFactory
import org.gdms.sql.engine.GdmSQLPredef._
import collection.JavaConversions._
import org.gdms.sql.evaluator.Expression

/**
 * Command that implements a simple non-parallel merge sort.
 * 
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

  // dumps into an array and sorts the array using the usual java modified mergesort
  // guaranteed O(n*log(n))
  protected final def doWork(r: Iterator[RowStream]) = r.next.toList.sorted.toIterator
  
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