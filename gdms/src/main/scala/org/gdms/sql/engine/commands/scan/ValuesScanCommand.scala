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
package org.gdms.sql.engine.commands.scan


import org.gdms.data.schema.DefaultMetadata
import org.gdms.sql.engine.SemanticException
import org.gdms.sql.engine.commands.SQLMetadata
import org.gdms.sql.engine.commands._
import org.gdms.sql.evaluator.Expression
import org.gdms.data.types.TypeFactory
import org.gdms.sql.engine.GdmSQLPredef._
import org.orbisgis.progress.ProgressMonitor

/**
 * Constant table scan.
 * 
 * The expressions used must 
 * - not reference any field of any kind
 * - be scalar expressions (no aggregate, table-like...)
 *
 * @param exps sequence of rows (that are themselves just sequences of expression)
 * @param alias an optional alias for this table
 * @author Antoine Gourlay
 * @since 0.3
 */
class ValuesScanCommand(exps: Seq[Seq[Expression]], alias: Option[String], internal: Boolean = true) 
extends Command with ExpressionCommand {

  private val m: DefaultMetadata = new DefaultMetadata()
  
  protected val exp = exps flatten

  protected final def doWork(r: Iterator[RowStream])(implicit pm: Option[ProgressMonitor]) = {
    exps.par.view map(evaluate) toIterator
  }
  
  override def doPrepare = {
    super.doPrepare
    
    // check for compatible types of elements in rows
    val types = exps.head map (_.evaluator.sqlType)
    exps.tail foreach {e =>
      val tt = e map (_.evaluator.sqlType)
      types zip tt foreach {zz => 
        if (!TypeFactory.canBeCastTo(zz._2, zz._1)) {
          throw new SemanticException("Rows must all have the same types as the first row, or must have types that " +
                                      "can be implicitly casted to the ones of the first row.")
        }
      }
    }
    
    // give names to the fields (either internal and external names)
    var k = -1;
    m.clear
    val prefix = if (internal) "$exp" else "exp"
    exps.head map (_.evaluator.sqlType) foreach {i =>
      k = k + 1
      m.addField(prefix + k, i)
    }
  }
  
  private def evaluate(a: Seq[Expression]): Row = Row(a map (_.evaluate(emptyRow)))
  
  override lazy val getMetadata = SQLMetadata(alias.getOrElse(""), m)
  
}
