/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
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

import org.gdms.data.NoSuchTableException
import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.sql.engine.SemanticException
import org.gdms.sql.evaluator._

/**
 * Command for evaluating expressions for every rows of a data stream.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
class ProjectionCommand(var expression: Array[(Expression, Option[String])]) extends ScalarCommand with ExpressionCommand {

  override def doPrepare = {
    // replace STAR by fields
    val stars = expression.filter(_._1 match {
        case star(_, _) => true
        case _ => false
      })
    
    val metadata = children map (_.getMetadata)
    
    stars foreach { s =>
      val ev = s._1.evaluator.asInstanceOf[StarFieldEvaluator]
      val optName = ev.table
      optName match {
        case None => {
            metadata map (addAllFields(_, s._1, ev.except))
          }
        case Some(name) => {
            val table = optName.get
            val m = metadata.find(_.table == table)
            if (!m.isDefined) {
              // check forwarded fields
              val beg = '$' + table
              var newexp = metadata flatMap {me => 
                val n = me.getFieldNames map (_.split('$'))
                n filter(t => t.length > 1 && t(1).endsWith(table) && !ev.except.contains(t(0))) map {t =>
                  (Field(t(0), table), Some(t(0)))
                }}
              if (newexp.isEmpty) {
                throw new NoSuchTableException(table)
              }
              insertFields(s._1, newexp)
            } else {
              addAllFields(m.get, s._1, ev.except)
            }
          }
      }
    }
    
    // added check to prevent creating 0-columned tables
    // this can occur when using the STAR EXCEPT functionnality
    if (expression.length == 0) {
      throw new SemanticException("The resulting dataset has no column! There must be at least one.")
    }
    
    // expressions initialisation
    super.doPrepare
  }
  
  private def addAllFields(m: SQLMetadata, s: Expression, except: Seq[String]) {
    // new fields
    var newexp = m.getFieldNames flatMap {n => 
      if (except.contains(n)) {
        List.empty
      } else {
        (Field(n, m.table), None) :: List.empty
      }
    }
      
    insertFields(s, newexp)
  }
  
  private def insertFields(s: Expression, newexp: Seq[(Expression, Option[String])]) {
    // split expression list
    var before = expression takeWhile(_._1.evaluator != s.evaluator)
    var after = expression drop(before.length +1)
    expression = before ++ newexp ++ after
  }
  
  protected override def exp: Seq[Expression] = expression map ( _._1)

  protected def scalarExecute = a => {
    Row(expression map( _._1.evaluate(a)))
  }

  private implicit def inM = children.head.getMetadata

  override def getMetadata = SQLMetadata(children.head.getMetadata.table,Expression.metadataFor(expression))
}
