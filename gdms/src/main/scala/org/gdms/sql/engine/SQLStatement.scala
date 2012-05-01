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

package org.gdms.sql.engine

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

class SQLStatement(sql: String, var op: Operation)(implicit p: Properties) {
  
  private val finalSql = sql + ';'
  private var com: OutputCommand = _
  private var r: DataSet = _
  private var dsf: Option[DataSourceFactory] = None
  private var pm: Option[ProgressMonitor] = None
  private var preparedButNotCleaned: Boolean = false
  
  private lazy val refs: Array[String] = { op.allChildren flatMap {_ match {
        case s: Scan => s.table :: Nil
        case c: CustomQueryScan => c.tables.flatMap (_.fold(_ :: Nil, _ => Nil))
        case _ => Nil
      }    
    } toArray   
  }
  
  def setDataSourceFactory(dsf: DataSourceFactory) {
    this.dsf = if (dsf == null) {None} else Some(dsf)
  }
  
  def setProgressMonitor(p: ProgressMonitor) {
    pm = if (p != null) Some(p) else None
  }

  def prepare() { 
    if (!preparedButNotCleaned) {
      if (dsf.isEmpty) {
        throw new DriverException("The SQLCommand should be initialized with a DSF")
      }
    
      com = (op, dsf.get)   >=: 
      FunctionsStep         >=: // resolve functions, process aggregates
      PhysicalJoinOptimStep >=: // choose join methods (indexes)
      BuilderStep               // build Command tree
          
      com.prepare(dsf.get)
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
  
  def cleanUp() {
    if (preparedButNotCleaned) {
      com.cleanUp
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
}