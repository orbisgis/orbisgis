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
package org.gdms.sql.engine

import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.util.Properties
import org.gdms.data.DataSourceFactory
import org.gdms.data.schema.Metadata
import org.gdms.driver.DataSet
import org.gdms.driver.DriverException
import org.gdms.sql.engine.commands.OutputCommand
import org.gdms.sql.engine.operations._
import org.gdms.sql.engine.step.builder.BuilderStep
import org.gdms.sql.engine.step.functions.FunctionsStep
import org.gdms.sql.engine.step.physicalJoin.PhysicalJoinOptimStep
import org.orbisgis.progress.ProgressMonitor

/**
 * An executable SQL Statement.
 * 
 * @param op the operation tree of this statement
 * @param sql the original SQL string
 * @param p some flags and properties
 * @author AntoineGourlay
 * @since 0.4
 */
class SQLStatement(sql: String, op: Operation)(implicit p: Properties) {
  
  // sql string for display and serialization
  private val finalSql = sql + ';'
  
  // holds the actual command that will be executed
  private var com: OutputCommand = _
  
  // the result data set
  private var r: DataSet = _
  
  private var dsf: Option[DataSourceFactory] = None
  private var pm: Option[ProgressMonitor] = None
  
  // true if this statement in a 'dirty' state
  private var preparedButNotCleaned: Boolean = false
  
  // tables that this statement references
  private lazy val refs: Array[String] = { op.allChildren flatMap {
      case s: Scan => s.table :: Nil
      case c: CustomQueryScan => c.tables.flatMap (_.fold(_ :: Nil, _ => Nil))
      case _ => Nil
    } toArray   
  }
  
  def setDataSourceFactory(dsf: DataSourceFactory) {
    this.dsf = if (dsf == null) None else Some(dsf)
  }
  
  def setProgressMonitor(p: ProgressMonitor) {
    pm = if (p != null) Some(p) else None
  }

  def prepare() { 
    if (!preparedButNotCleaned) {
      if (dsf.isEmpty) {
        throw new DriverException("The SQLCommand should be initialized with a DSF.")
      }
      
      // duplicates the Operation tree before using it
      com = (op.duplicate, dsf.get)   >=: 
      FunctionsStep         >=: // resolve functions, process aggregates
      PhysicalJoinOptimStep >=: // choose join methods (indexes)
      BuilderStep               // build Command tree
          
      // final validation, reference checking, type checking, etc.
      com.prepare(dsf.get)
      
      // gets a reference to the ouput (the command has not been executed yet)
      r = com.getResult
      
      preparedButNotCleaned = true
    }
  }
  
  @throws(classOf[DriverException])
  def execute() = { 
    try {
      com.execute(pm)
    } catch {
      case e: Exception => throw new DriverException(e)
    }
    r 
  }
  
  def cleanUp()() {
    if (preparedButNotCleaned) {
      com.cleanUp()
      preparedButNotCleaned = false
      
      // IMPORTANT: this lets the GC do its work
      r = null
      com = null
    }
  }
  
  /**
   * Gets the result metadata of this query.
   * @return the metadata of the result of this query
   */
  def getResultMetadata(): Metadata = { r match {
      case null => null
      case _ => r.getMetadata
    }
  }
  
  def getReferencedSources(): Array[String] = refs
  
  def getSQL(): String = finalSql
  
  def save(out: OutputStream) {
    var o: ObjectOutputStream = null
    var o2: ObjectOutputStream = null
    
    try {
      o = new ObjectOutputStream(out)
      o.writeObject(sql)
      o.flush
      o2 = new ObjectOutputStream(out)
      o2.writeObject(op)
      o2.flush
    } finally {
      if (o != null) o.close
      if (o2 != null) o2.close
    }
  }
}

object SQLStatement {
  def load(i: InputStream, p: Properties): SQLStatement = {
    var o: ObjectInputStream = null
    var o2: ObjectInputStream = null
    
    try {
      o = new ObjectInputStream(i)
      val sql = o.readObject.asInstanceOf[String]
      o2 = new ObjectInputStream(i)
      val ope = o2.readObject.asInstanceOf[Operation]
      
      new SQLStatement(sql, ope)(p)
    } finally {
      if (o != null) o.close
      if (o2 != null) o2.close
    }
  }
}