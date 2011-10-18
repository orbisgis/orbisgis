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

package org.gdms.sql.engine.commands.join

import org.gdms.data.indexes.DefaultSpatialIndexQuery
import org.gdms.data.schema.DefaultMetadata
import org.gdms.data.schema.MetadataUtilities
import org.gdms.data.types.Type
import org.gdms.data.types.TypeFactory
import org.gdms.data.values.Value
import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.sql.engine.commands.Row
import org.gdms.sql.engine.SemanticException
import org.gdms.sql.engine.commands.Command
import org.gdms.sql.engine.commands.ExpressionCommand
import org.gdms.sql.engine.commands.scan.IndexQueryScanCommand
import org.gdms.sql.engine.commands.SQLMetadata
import org.gdms.sql.evaluator.Expression

/**
 * Performs a spatial indexed join between two spatial tables.
 *
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
  
  protected final def doWork(r: Iterator[RowStream]): RowStream = {
    for (r <- small.execute ; s <- queryIndex(r); t <- filter(r ++ s)) yield t
  }
  
  private def queryIndex(r: Row) = {
    val env = r(smallSpatialField).getAsGeometry.getEnvelopeInternal
    big.query = new DefaultSpatialIndexQuery(env, bigSpatialFieldName)
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
  
  val exp = expr :: Nil
  
  override def getMetadata = {
    val d = new DefaultMetadata()
    List(small, big) foreach { c => addAndRename(d, c.getMetadata) }
    SQLMetadata("", d)
  }
  
  private def addAndRename(d: DefaultMetadata, m: SQLMetadata) {
    // fields are given an internal name 'field$table'
    // for reference by expressions upper in the query tree
    m.getFieldNames.zipWithIndex foreach { n =>
      d.addField(n._1 + "$" + m.table,m.getFieldType(n._2))
    }
  }
  
  override def doPrepare = {
    // identifiated the small and big
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
    
    smallSpatialField = MetadataUtilities.getGeometryFieldIndex(small.getMetadata)
    bigSpatialFieldName  = big.getMetadata.getFieldName(MetadataUtilities.getGeometryFieldIndex(big.getMetadata))
    
    super.doPrepare
    
    expr.evaluator.sqlType match {
      case Type.BOOLEAN =>
      case i =>throw new SemanticException("The join expression does not return a Boolean. Type: " +
                                           TypeFactory.getTypeName(i))
    }
    
    children = Nil
  }
  
  override def preDoCleanUp = {
    children = List(small, big)
  }
}