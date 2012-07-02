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

import org.gdms.sql.engine.SemanticException
import org.gdms.sql.evaluator.Expression
import org.gdms.data.types.Type
import org.gdms.data.types.TypeFactory
import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.sql.evaluator.agg
import org.orbisgis.progress.ProgressMonitor

/**
 * Base class for row-level filtering of a row stream.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
abstract class FilterCommand extends Command {
  protected final def doWork(r: Iterator[RowStream])(implicit pm: Option[ProgressMonitor]) = {
    r.next filter (filterExecute)
  }

  /**
   * This method perform the actual row-level filter.
   */
  protected def filterExecute: Row => Boolean

}

/**
 * Filter command for use in Where/Having statements.
 * 
 * @param e filtering expression
 * @author Antoine Gourlay
 * @since 0.1
 */
class ExpressionFilterCommand(e: Expression) extends FilterCommand with ExpressionCommand {

  protected val exp = Seq(e)

  protected def filterExecute: Row => Boolean = { r =>
    val ev = e.evaluate(r)
    if (ev.isNull) false else ev.getAsBoolean
  }
  
  override def doPrepare {
    // no aggregate function is allowed in a WHERE clause
    // this check cannot be done in Filter Operation because aggregates are resolved later.
    // HAVING clauses are handled during the function step (aggregates are allowed in these)
    def check (ex: Expression) {
      ex match {
        case agg(f,_) => throw new SemanticException("No aggregate function is allowed in a WHERE clause."
                                                     + " Found function '" + f.getName + "'.")
        case _ => ex.children map check
      }
    }
    
    check(e)
    
    // prepares the expression
    super.doPrepare
    
    // checks that the expression is indeed a predicate
    e.evaluator.sqlType match {
      case Type.BOOLEAN | Type.NULL =>
      case i => throw new SemanticException("The filtering expression does not return a Boolean. Type: " +
                                           TypeFactory.getTypeName(i))
    }
  }

}