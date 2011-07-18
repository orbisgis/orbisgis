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
import org.gdms.sql.evaluator.FieldEvaluator
import org.gdms.sql.engine.SemanticException
import org.gdms.sql.engine.UnknownFieldException
import org.gdms.sql.evaluator.DsfEvaluator
import org.gdms.sql.engine.GdmSQLPredef._

/**
 * This trait takes care of initializing the expressions a Command might handle.
 *
 * Implementing classes must implement <tt>exp</tt> to be the whole set of Expression
 * to init.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
trait ExpressionCommand extends Command {

  protected def exp: Seq[Expression]
  
  private def getFieldMetadata(c: Command): Seq[SQLMetadata] = { c match {
      case s: ScanCommand => s.getMetadata :: (s.children flatMap getFieldMetadata)
      case s: CustomQueryScanCommand => s.getMetadata :: (s.children flatMap getFieldMetadata)
      case a: AggregateCommand => a.getMetadata :: Nil
      case _ => c.children flatMap getFieldMetadata
  }
  }

  protected override def doPrepare = {
    // prevent ambiguous field names
    
    
     val allM = children flatMap getFieldMetadata
    
    // set the index of every field in the expressions
    exp.foreach { e =>
      testNoDuplicateFields(e, allM)
      setDsf(e)
      children map (_.getMetadata) foreach (setFields(e, _))
    }

    // validation
    exp foreach (_ validate)
  }
  
  private def testNoDuplicateFields(e: Expression, m: Seq[SQLMetadata]): Unit = {
    e.evaluator match {
      case FieldEvaluator(name, None) => {
          val found = m.filter (_.getFieldIndex(name) != -1)
          found.size match {
            case 1 =>
            case 0 => throw new UnknownFieldException(name)
            case _ =>
              throw new SemanticException("Field name '" + name + "' is ambiguous.")
          }
        }
      case a => a.childExpressions foreach (testNoDuplicateFields(_, m))
    }
  }

  /**
   * Resolves fields against metadata objects of child commands
   */
  private def setFields(e: Expression, m: SQLMetadata): Unit = {
    e.evaluator match {
      case f: FieldEvaluator => {
          f.table match {
            case None => {
                m.getFieldIndex(f.name) match {
                  case -1 =>
                  case i =>
                    f.index = i
                    f.sqlType = m.getFieldType(i).getTypeCode
                }
              }
            case Some(t) if m.table == t => m.getFieldIndex(f.name) match {
                case -1 => throw new SemanticException("There is no field '" + f.name + "' in table" + t + ".")
                case i =>
                  f.index = i
                  f.sqlType = m.getFieldType(i).getTypeCode
              }
            case _ =>
          }
        }
      case _ => // not a field, do nothing
    }
    e.foreach { setFields(_, m) }
  }

  /**
   * Sets DataSourceFactory to expressions that need it
   */
  private def setDsf(e: Expression): Unit = {
    e.evaluator match {
      case f: DsfEvaluator => f.dsf = dsf
      case _ => // not a function, do nothing
    }
    e.foreach ( setDsf(_) )
  }
}
