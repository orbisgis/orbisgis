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
package org.gdms.sql.engine

import java.io.{ObjectOutputStream, OutputStream, IOException, File, FileOutputStream}
import java.util.Properties
import scala.collection.mutable.{Map => MutMap}
import org.gdms.data.DataSourceFactory
import org.gdms.data.schema.Metadata
import org.gdms.data.values.Value
import org.gdms.driver.{DataSet, DriverException}
import org.gdms.sql.engine.commands.OutputCommand
import org.gdms.sql.engine.operations._
import org.gdms.sql.engine.step.builder.BuilderStep
import org.gdms.sql.engine.step.functions.FunctionsStep
import org.gdms.sql.engine.step.params.ParamsStep
import org.gdms.sql.engine.step.physicalJoin.PhysicalJoinOptimStep
import org.orbisgis.progress.ProgressMonitor

/**
 * An executable SQL Statement.
 * 
 * The specific workflow to use with a statement is:
 * {{{
 * // set a DSF once
 * st.setDataSourceFactory(dsf)
 * 
 * // then do the following for every execution
 * 
 * // prepare the statement (final validation + open resources)
 * st.prepare()
 * // run the statement
 * val ds = st.execute()
 * // use ds ...
 * println(ds.getInt(0,0))
 * ...
 * // clean the statement (closes resources)
 * st.cleanUp()
 * }}}
 * 
 * @param op the operation tree of this statement
 * @param sql the original SQL string
 * @param p some flags and properties
 * @author AntoineGourlay
 * @since 0.4
 */
sealed class SQLStatement private[engine] (sql: String, private[engine] val op: Operation)(implicit p: Properties) {
  
  // holds the actual command that will be executed
  private var com: OutputCommand = _
  
  // the result data set
  private var r: DataSet = _
  
  private var dsf: Option[DataSourceFactory] = None
  private var pm: Option[ProgressMonitor] = None
  
  // true if this statement in a 'dirty' state
  private var preparedButNotCleaned: Boolean = false
  
  // tables that this statement references
  private lazy val refs: Seq[String] = { op.allChildren flatMap {
      case s: Scan => s.table :: Nil
      case c: CustomQueryScan => c.tables.flatMap (_.fold(_ :: Nil, _ => Nil))
      case _ => Nil
    }
  }
  
  private val vParams = MutMap[String, Value]()
  private val fParams = MutMap[String, String]()
  private val tParams = MutMap[String, String]()
  
  /**
   * Sets a value parameter.
   * @param name name of the parameter
   * @param v a constant value
   */
  def setValueParameter(name: String, v: Value) {
    vParams.put(name, v)
  }
  
  /**
   * Sets a field parameter.
   * @param name name of the parameter
   * @param fieldName name of the field
   */
  def setFieldParameter(name: String, fieldName: String) {
    fParams.put(name, fieldName)
  }
  
  /**
   * Sets a table parameter.
   * @param name name of the parameter
   * @param tableName name of the table
   */
  def setTableParameter(name: String, tableName: String) {
    tParams.put(name, tableName)
  }
  
  /**
   * Sets the DSF to use for this statement.
   * @param dsf the DSF to use
   */
  def setDataSourceFactory(dsf: DataSourceFactory) {
    this.dsf = if (dsf == null) None else Some(dsf)
  }
  
  /**
   * Sets a progress monitor to use for this statement's execution.
   * @param p a progress monitor
   */
  def setProgressMonitor(p: ProgressMonitor) {
    pm = if (p != null) Some(p) else None
  }

  /**
   * Prepares the statement.
   * 
   * Subsequent calls (without a call to cleanUp in between) are ignored.
   */
  def prepare() { 
    if (!preparedButNotCleaned) {
      if (dsf.isEmpty) {
        throw new DriverException("The SQLCommand should be initialized with a DSF.")
      }
      
      // duplicates the Operation tree before using it
      com = (op.duplicate   >=: new ParamsStep(vParams, fParams, tParams), 
             dsf.get)       >=: 
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
  
  /**
   * Executes the statement.
   * 
   * @return the resulting dataset (if any)
   * @throws DriverException if there is any error executing the statement
   */
  @throws(classOf[DriverException])
  def execute() = { 
    try {
      com.execute(pm)
    } catch {
      case e: Exception => throw new DriverException(e)
    }
    r 
  }
  
  /**
   * Cleans up resources.
   * 
   * This resets this statement to its original state. Its content cannot be read anymore,
   * and the statement can be reused with a call to prepare().
   * Subsequent call to this method are ignored.
   */
  def cleanUp()() {
    if (preparedButNotCleaned) {
      com.cleanUp()
      preparedButNotCleaned = false
      
      vParams.clear
      fParams.clear
      tParams.clear
      
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
  
  /**
   * Gets all gdms sources referenced by this statement.
   */
  def getReferencedSources(): Array[String] = (refs ++ tParams.values).toArray
  
  /**
   * Gets the SQL string representation of this statement
   */
  def getSQL: String = sql
  
  /**
   * Saves this statement to a file in compiled form.
   * 
   * The compiled statement can be reloaded with
   * {{{
   * val s = Engine.load(...)
   * s.setDataSourceFactory(dsf)
   * s.prepare()
   * s.execute()
   * s.cleanUp()
   * }}}
   * 
   * @throw IOException if there is any error writing
   */
  @throws(classOf[IOException])
  def save(out: File) {
    save(new FileOutputStream(out))
  }
  
  /**
   * Saves this statement to a stream in compiled form.
   * 
   * The compiled statement can be reloaded with
   * {{{
   * val s = Engine.load(...)
   * s.setDataSourceFactory(dsf)
   * s.prepare()
   * s.execute()
   * s.cleanUp()
   * }}}
   * 
   * @throw IOException if there is any error writing
   */
  @throws(classOf[IOException])
  def save(out: OutputStream) {
    var o: ObjectOutputStream = null
    var o2: ObjectOutputStream = null
    
    try {
      o = new ObjectOutputStream(out)
      o.writeUTF(getSQL)
      o.flush
      o2 = new ObjectOutputStream(out)
      o2.writeObject(op)
      o2.flush
    } finally {
      if (o != null) o.close
      if (o2 != null) o2.close
      out.close // just to be sure
    }
  }
}