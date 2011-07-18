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

import org.gdms.data.NoSuchTableException
import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.sql.evaluator.Expression
import org.gdms.sql.evaluator.Field
import org.gdms.sql.evaluator.FieldEvaluator

/**
 * Command for evaluating expressions for every rows of a data stream.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
class ProjectionCommand(var expression: Array[(Expression, Option[String])]) extends ScalarCommand with ExpressionCommand {

  override def doPrepare = {
    // replace STAR by fields
    val stars = expression.filter(_._1.evaluator match {
        case FieldEvaluator("*", _) => true
        case _ => false
      })
    
    val metadata = children map (_.getMetadata)
    
    stars foreach { s =>
      val optName = s._1.evaluator.asInstanceOf[FieldEvaluator].table
      optName match {
        case None => {
            metadata map (addAllFields(_, s._1))
          }
        case Some(name) => {
            val name = optName.get
            val m = metadata.find(_.table == name)
            if (!m.isDefined) {
              throw new NoSuchTableException(name)
            }
            addAllFields(m.get, s._1)
          }
      }
    }
    
    // expressions initialisation
    super.doPrepare
  }
  
  private def addAllFields(m: SQLMetadata, s: Expression) {
    // new fields
    var newexp = m.getFieldNames map (n => (Field(n, m.table), None))
      
    // split expression list
    var before = expression takeWhile(_._1.evaluator != s.evaluator)
    var after = expression drop(before.length +1)
    expression = before ++ newexp ++ after
  }
  
  protected override def exp: Seq[Expression] = expression map ( _._1)

  protected def scalarExecute = implicit s => expression map( _._1.evaluate(s) )

  private implicit def inM = children.head.getMetadata

  override def getMetadata = SQLMetadata(children.head.getMetadata.table,Expression.metadataFor(expression))
}
