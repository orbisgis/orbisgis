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
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
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

import org.gdms.data.indexes.DefaultSpatialIndexQuery
import org.gdms.data.schema.{DefaultMetadata, MetadataUtilities}
import org.gdms.data.types.{Type, TypeFactory}
import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.sql.engine.SemanticException
import org.gdms.sql.engine.commands.{Command, ExpressionCommand, Row, SQLMetadata}
import org.gdms.sql.engine.commands.scan.IndexQueryScanCommand
import org.gdms.sql.evaluator.Evaluators._
import org.gdms.sql.evaluator.{Expression, FieldEvaluator}
import org.gdms.sql.function.SpatialIndexedFunction
import org.orbisgis.progress.ProgressMonitor

/**
 * Performs a spatial indexed join between two spatial tables.
 *
 * @param joining expression
 * @author Antoine Gourlay
 * @since 0.3
 */
class SpatialIndexedJoinCommand(expr: Expression) extends Command with ExpressionCommand {
  
  // command that will be looped upon
  var small: Command = null
  var smallSpatialField: Int = -1
  
  // command whose index will be queried
  var big: IndexQueryScanCommand = null
  var bigSpatialFieldName: String = null
  
  // fields to drop after the filter but before its gets upper in the tree
  // this should be refactored into something somewhat cleaner...
  var dropped: List[Int] = Nil
  
  var queryExpression: Expression = null
  
  private val d = new DefaultMetadata()
  
  protected final def doWork(r: Iterator[RowStream])(implicit pm: Option[ProgressMonitor]): RowStream = {
    // if we need to drop somethink, lets drop it, else we do nothing
    val clean: (Row) => Row = if (dropped.isEmpty) identity _ else drop _
    
    for (r <- small.execute ; s <- queryIndex(r); t <- filter(r ++ s)) yield clean(t)
  }
  
  private def drop(r: Row) = {
    // drops columns indexes contained in 'dropped'
    Row(r.indices filterNot(i => dropped.contains(i)) map(i => r(i)))
  }
  
  private def queryIndex(r: Row)(implicit pm: Option[ProgressMonitor]) = {
    // gets the envelope of the current geometry value
    val env = r(smallSpatialField).getAsGeometry.getEnvelopeInternal
    // creates a query for that envelope
    big.query = new DefaultSpatialIndexQuery(bigSpatialFieldName, env)
    
    // runs the query
    big.execute
  }
  
  private def filter(r: Row) = {
    val e = expr.evaluate(r).getAsBoolean
    if (e != null && e == true) {
      r :: Nil
    } else {
      Nil
    }
  }
  
  val exp = Seq(expr)
  
  override def getMetadata = SQLMetadata("", d)
  
  private def addAndRename(d: DefaultMetadata, m: SQLMetadata) {
    // fields are given an internal name 'field$table'
    // for reference by expressions upper in the query tree
    m.getFieldNames.zipWithIndex foreach { n =>
      if (n._1.startsWith("$")) {
        // internal-use field (starts with '$') --> is not moved upwards.
        dropped = n._2 :: dropped
      } else {
        d.addField(n._1 + "$" + m.table,m.getFieldType(n._2))
      }
    }
  }
  
  override def doPrepare() = {
    // identifiated the small and big commands
    // small: IndexQueryScanCommand
    // big: the other one
    children.head match {
      case a: IndexQueryScanCommand => {
          big = a
          small = children.tail.head
        }
      case b => {
          small = b
          big = children.tail.head.asInstanceOf[IndexQueryScanCommand]
        }
    }
    
    // get useful field indexes and names
    smallSpatialField = MetadataUtilities.getGeometryFieldIndex(small.getMetadata)
    bigSpatialFieldName  = big.getMetadata.getFieldName(MetadataUtilities.getGeometryFieldIndex(big.getMetadata))
    
    // reorder children in the iteration order (small then big)
    // this is important for ExpressionCommand.doPrepare()() to do its work correctly
    children = List(small, big)
    
    super.doPrepare()
    
    // check the filter expression is indeed a boolean predicate
    expr.evaluator.sqlType match {
      case Type.BOOLEAN =>
      case i =>throw new SemanticException("The join expression does not return a Boolean. Type: " +
                                           TypeFactory.getTypeName(i))
    }
    
    // looks for the query expression (the SpatialIndexedFunction)
    findQueryExpression(expr)
    if (queryExpression == null) {
      // this should never happen: this query plan would not have been selected when compiling the query
      // if there was no query expression found in there...
      throw new IllegalStateException("Internal error: Could not find any expression to query the index.")
    }
    
    children = Nil
    
    d.clear
    List(small, big) foreach { c => addAndRename(d, c.getMetadata) }
  }
  
  private def findQueryExpression(e: Expression) {
    var done = false
    e match {
      // a spatial indexed function, perfect, we look for the spatial field that matters to us
      case func(_, f: SpatialIndexedFunction, li) => {
          li foreach {el =>
            if (!done && matchSmallSpatialField(el)) {
              queryExpression = el
              done = true
            }
          }
        }
        // and AND condition, we look on both sides
      case _ & _ => e map (findQueryExpression)
      case _ =>
    }
  }
  
  private def matchSmallSpatialField(e: Expression): Boolean = {
    if (e.evaluator.isInstanceOf[FieldEvaluator]) {
      val f = e.evaluator.asInstanceOf[FieldEvaluator]
      // checks if this field is indeed the smallSpatialField of the right table
      f.table match {
        case Some(t) if t == small.getMetadata.table => {
            f.name == small.getMetadata.getFieldName(smallSpatialField)
          }
        case _ => false
      }
    } else {
      // else we look deeper
      e.map(matchSmallSpatialField).fold(false)(_ || _)
    }
  }
  
  override def preDoCleanUp() = {
    children = List(small, big)
  }
}