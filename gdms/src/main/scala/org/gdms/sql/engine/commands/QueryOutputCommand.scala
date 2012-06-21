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

import java.io.File
import org.gdms.data.DataSourceFactory
import org.gdms.data.schema.DefaultMetadata
import org.gdms.driver.DiskBufferDriver
import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.sql.engine.SemanticException
import org.orbisgis.progress.ProgressMonitor

/**
 * Output command that caches to disk the result.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
class QueryOutputCommand extends Command with OutputCommand {

  // driver for writing to disk
  private var driver: DiskBufferDriver = _
  // file on disk
  // TODO: maybe this could be refactored out of here
  // into a caching service in Gdms
  var resultFile: File = _

  protected override def doPrepare = {
    if (resultFile == null) {
      resultFile = new File(dsf.getTempFile("gdms"))
    }
    driver = new DiskBufferDriver(resultFile, getMetadata)
  }

  protected def doWork(r: Iterator[RowStream])(implicit pm: Option[ProgressMonitor]) = {    
    pm.map(_.startTask("Writing", 0))
    // write line by line
    for (s <- r; a <- s) {
      driver.addValues(a.array:_*)
    }
    driver.writingFinished
    
    // open is important: the result can be read at any time after a call to execute()
    driver.open
    
    pm.map(_.endTask)
    Iterator.empty
  }
  
  /**
   * To be called as a prepare() call when this is used as a materialization intermediate.
   * 
   * Calling this does the same as calling prepare() except that sub commands are not prepared
   * (they are assumed to be already prepared).
   */
  def materialize(dsf: DataSourceFactory) {
    this.dsf = dsf
    doPrepare
  }
  
  /**
   * To be called after execute to get back a RowStream from a materialized intermediate.
   * 
   * TODO: maybe this could be refactored out of here...
   */
  def iterate() = {
    for (i <- (0l until driver.getRowCount).par.view.toIterator) yield {
      Row(i, driver.getRow(i))
    }
  }
  
  override def getMetadata = {
    val m = super.getMetadata
    
    val d = new DefaultMetadata()
    
    // cleanes up the internal field names: table references are dropped.
    (0 until m.getFieldCount) foreach {i =>
      val f = m.getFieldName(i).takeWhile(_ != '$')
      if (d.getFieldIndex(f) == -1) {
        d.addField(f, m.getFieldType(i).getTypeCode)
      } else {
        // ambiguous implicit field reference.
        throw new SemanticException("There already is a field or alias '" + f + "'.")
      }
    }
    
    SQLMetadata("", d)
  }
  
  protected override def doCleanUp = {
    if (driver.isOpen) {
      driver.close
    }
    driver = null
  }

  def getResult = driver
}