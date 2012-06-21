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
package org.gdms.sql.engine.commands.join

import org.gdms.data.schema.DefaultMetadata
import org.gdms.data.types.Type
import org.gdms.data.types.TypeFactory
import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.sql.engine.SemanticException
import org.gdms.sql.engine.commands.Command
import org.gdms.sql.engine.commands.ExpressionCommand
import org.gdms.sql.engine.commands.SQLMetadata
import org.gdms.sql.evaluator.Expression
import org.gdms.sql.evaluator.Field
import org.orbisgis.progress.ProgressMonitor

/**
 * Performs a basic loop join command.
 * 
 * @param expr expression to join on, if there is one
 * @param natural if it is a natural join
 * @param if it is an outerLeft join
 * @author Antoine Gourlay
 * @since 0.1
 */
class ExpressionBasedLoopJoinCommand(private var expr: Option[Expression], natural: Boolean = false, outerLeft: Boolean = false)
extends Command with ExpressionCommand with JoinCommand {
  
  override def doPrepare() {
    
    // special support for NATURAL joins
    if (natural) {
      val m1 = children.head.getMetadata
      val m2 = children(1).getMetadata
      
      // finds fields in common between the two tables
      val idxs = (0 until m1.getFieldCount).toSeq flatMap {j =>
        val n = m1.getFieldName(j)
        m2.getFieldIndex(n) match {
          case -1 => Nil
          case i => {
              if (m1.getFieldType(j).getTypeCode == m2.getFieldType(i).getTypeCode) {
                n :: Nil
              } else {
                Nil
              }
            }
        }
      }
      
      if (!idxs.isEmpty) {
        // creates the join expression for all fields in common
        val f = Field(idxs.head, m1.table) sqlEquals Field(idxs.head, m2.table)
        if (!idxs.tail.isEmpty) {
          expr = Some(f & buildExpression(idxs.tail, m1, m2))
        } else {
          expr = Some(f)
        }
      }
    }
    
    // initialize expressions
    if (expr.isDefined) {
      super.doPrepare
      
      // check the expression is a boolean predicate
      expr.get.evaluator.sqlType match {
        case Type.BOOLEAN =>
        case i =>throw new SemanticException("The join expression does not return a Boolean. Type: " +
                                             TypeFactory.getTypeName(i))
      }
    }
    
  }
  
  /**
   * Builds an expression like Field(tata.toto) == Field(tutu.toto) for use as filtering.
   * 
   * Note: this could be refactored as a tail-recursive function, but we will never have billions of 
   * fields anyway...
   */
  private def buildExpression(elems: Seq[String], m1: SQLMetadata, m2: SQLMetadata): Expression = {
    val f = Field(elems.head, m1.table) sqlEquals Field(elems.head, m2.table)
    if (!elems.tail.isEmpty) {
      f & buildExpression(elems.tail, m1, m2)
    } else {
      f
    }
  }
  
  override def execute(implicit pm: Option[ProgressMonitor]) = {
    def left = children.head.execute
    def right = children.tail.head.execute
    
    if (expr.isDefined) {
      if (outerLeft) {
        // (LEFT) OUTER JOIN
        doLeftOuterJoin(left, right, expr.get)
      } else {
        // INNER JOIN
        doInnerJoin(left, right, expr.get)
        
      }
    } else {
      // CROSS JOIN
      doCrossJoin(left, right)
    }
    
  }
  
  // this one does nothing and is not used in this case
  protected final def doWork(r: Iterator[RowStream])(implicit pm: Option[ProgressMonitor]): RowStream = null
  
  def exp = expr.toSeq
  
  override def getMetadata = {
    val d = new DefaultMetadata()
    children foreach { c => addAndRename(d, c.getMetadata) }
    SQLMetadata("", d)
  }
  
  private def addAndRename(d: DefaultMetadata, m: SQLMetadata) {
    // fields are given an internal name 'field$table'
    // for reference by expressions upper in the query tree
    if (m.table != "") {
      m.getFieldNames.zipWithIndex foreach { n =>
        d.addField(n._1 + "$" + m.table,m.getFieldType(n._2))
      }
    } else {
      m.getFieldNames.zipWithIndex foreach { n =>
        d.addField(n._1,m.getFieldType(n._2))
      }
    }
    
  }
}