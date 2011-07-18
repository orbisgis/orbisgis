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

import org.gdms.data.DataSource
import org.gdms.data.DataSourceFactory
import org.gdms.data.types.TypeFactory
import org.gdms.data.types.IncompatibleTypesException
import org.gdms.sql.engine.SemanticException
import org.gdms.sql.evaluator.Expression
import scalaz.concurrent.Promise

/**
 * Main command for static multi-row insert of expressions.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
class StaticInsertCommand(table: String, exps: Seq[Array[Expression]], fields: Option[Seq[String]])
extends Command with OutputCommand with ExpressionCommand {

  // expressions to init (ExpressionCommand)
  protected def exp = exps flatten
  
  private var rightOrder: Array[(Int, Int)] = null
  
  var ds: DataSource = null

  override def doPrepare = {
    ds = dsf.getDataSource(table, DataSourceFactory.EDITABLE)
    ds.open

    val m = ds.getMetadata
    
    fields match {
      case Some(f) => {
          val r = f map (s => (s, m.getFieldIndex(s)))
          r foreach {i => if (i._2 == -1) {
              throw new SemanticException("There is no field '" + i._1 + "' in table '" + table + "'.")
            }}
          
          rightOrder = r.map(_._2).zipWithIndex toArray
      }
      case None => {
          val r = m.getFieldCount
          exps foreach (a => if (a.length != r) {
              throw new SemanticException("There are " + a.length + " fields specified. Expected " + r + "fields to insert.")
            })
      }
    }
    
    val types = (0 until m.getFieldCount) map (m.getFieldType(_).getTypeCode)
    val exTypes = order(exps.head) map (_.evaluator.sqlType)
    (types zip exTypes) foreach { _ match {
        case (a, b) if !TypeFactory.canBeCastTo(b, a) =>{
            ds.close
            throw new IncompatibleTypesException("type " + TypeFactory.getTypeName(b) + " cannot be cast to "
                                           + TypeFactory.getTypeName(a))
          }
        case _ =>
      }
    }
  }
  
  private def order(a: Array[Expression]): Array[Expression] = {
    if (rightOrder == null) a else {
      val out = new Array[Expression](rightOrder.length)
      rightOrder foreach (i => out(i._1) = a(i._2))
      out
    }
  }

  protected final def doWork(r: Iterable[Iterable[Promise[Iterable[Row]]]]) = {
    // we eval each Array (= row) and give it to insertFilledRow
    exps foreach { e => ds.insertFilledRow((order(e) map ( _.evaluate(Array.empty) ))) }
    null
  }

  override def doCleanUp = {
    ds.commit
    ds.close
  }

  val getResult = null

  // no result
  override val getMetadata = null
}
