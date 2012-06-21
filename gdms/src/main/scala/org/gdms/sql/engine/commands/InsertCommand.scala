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

import org.gdms.data.DataSource
import org.gdms.data.DataSourceFactory
import org.gdms.data.types.Type
import org.gdms.data.types.TypeFactory
import org.gdms.data.schema.DefaultMetadata
import org.gdms.data.types.IncompatibleTypesException
import org.gdms.sql.engine.SemanticException
import org.gdms.data.values.Value
import org.gdms.data.values.ValueFactory
import org.gdms.driver.memory.MemoryDataSetDriver
import org.gdms.sql.engine.GdmSQLPredef._
import org.orbisgis.progress.ProgressMonitor

/**
 * Main command for static multi-row insert of expressions.
 *
 * @param table to insert into
 * @param names of the fields (if fields are specified)
 * @author Antoine Gourlay
 * @since 0.1
 */
class InsertCommand(table: String, fields: Option[Seq[String]]) extends Command with OutputCommand {

  // mapping between index in the column index in the input rows and column index in the table
  private var rightOrder: Array[(Int, Int)] = _
  
  // holds the DataSource to insert into
  var ds: DataSource = _
  
  // holds the result set that will containt the number of inserted rows
  var res: MemoryDataSetDriver = _
  
  // number of inserted rows
  var ro: Long = 0

  override def doPrepare = {
    // opens the DataSource in edition
    ds = dsf.getDataSource(table, DataSourceFactory.EDITABLE)
    ds.open

    val m = ds.getMetadata
    
    // checks that all fields (if specified) exist
    // if so, populates the mapping between the input and the table indexes
    fields match {
      case Some(f) => {
          val r = f map (s => (s, m.getFieldIndex(s)))
          r foreach {i => if (i._2 == -1) {
              throw new SemanticException("There is no field '" + i._1 + "' in table '" + table + "'.")
            }}
          
          rightOrder = r.map(_._2).zipWithIndex toArray
        }
      case _ =>
    }
    
    val chm = children.head.getMetadata
    
    // checks that the input has the expected number of fields
    val expCount = if (rightOrder == null) {
      // either the number of fields of the table...
      m.getFieldCount
    } else {
      // ... or the number of specified fields
      rightOrder.length
    }
    if (chm.getFieldCount != expCount) {
      throw new SemanticException("There are " + chm.getFieldCount + " fields specified. Expected " + expCount + "fields to insert.")
    }
    
    
    // expected types
    val types = (0 until m.getFieldCount) map (m.getFieldType(_).getTypeCode)
    
    // types of the input
    val inTypes = if (rightOrder == null) {
      // either directly the types of the input...
      (0 until chm.getFieldCount) map (chm.getFieldType(_).getTypeCode)
    } else {
      // ... or the ones of the input remapped in the right order
      rightOrder map(r => chm.getFieldType(r._1).getTypeCode) toSeq
    }
    
    // checks that input types can be implicitly cast into the table types
    (types zip inTypes) foreach { _ match {
        case (a, b) if !TypeFactory.canBeCastTo(b, a) =>{
            ds.close
            throw new IncompatibleTypesException("type " + TypeFactory.getTypeName(b) + " cannot be cast to "
                                                 + TypeFactory.getTypeName(a))
          }
        case _ =>
      }
    }
    
    // creates the result set that will containt the number of inserted rows
    res = new MemoryDataSetDriver(new DefaultMetadata(Array(TypeFactory.createType(Type.LONG)), Array("Inserted")))
  }
  
  /**
   * Reorders some input values according to the insertion order.
   * 
   * TODO: Maybe this could be improved, it currently allocates a new array...
   */
  private def order(a: Array[Value]): Array[Value] = {
    if (rightOrder == null) a else {
      val out = new Array[Value](rightOrder.length)
      rightOrder foreach (i => out(i._1) = a(i._2))
      out
    }
  }

  protected final def doWork(r: Iterator[RowStream])(implicit pm: Option[ProgressMonitor]) = {
    pm.map(_.startTask("Inserting", 0))
    
    // insert (reordered) rows
    r.next foreach { e =>
      ro = ro + 1
      ds.insertFilledRow(order(e))
    }
    
    // returns the number of inserted rows
    res.addValues(ValueFactory.createValue(ro))
    ro = 0
    
    pm.map(_.endTask)
    Iterator.empty
  }

  override def doCleanUp = {
    ds.commit
    ds.close
  }

  def getResult = res

  override lazy val getMetadata = SQLMetadata("",getResult.getMetadata)
}
