/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
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
import org.gdms.driver.DataSet
import org.gdms.data.schema.Metadata
import org.gdms.driver.DriverException
import org.gdms.sql.engine.commands.OutputCommand
import org.gdms.sql.engine.commands.QueryOutputCommand
import org.gdms.sql.engine.operations.Operation
import org.gdms.sql.engine.operations.Scan
import org.gdms.sql.engine.step.functions.FunctionsStep
import org.gdms.sql.engine.step.builder.BuilderStep
import org.gdms.sql.engine.step.physicalJoin.PhysicalJoinOptimStep
import org.orbisgis.progress.ProgressMonitor

/**
 * Represents a ready-to-execute execution graph.
 *
 * <ul>
 * <li>The <tt>prepare</tt> method must be called first.</li>
 * <li>Then <tt>getResultMetadata</tt> can be called to get the result metadata
 * before actually running the query.</li>
 * <li>Then <tt>execute can be called (once)</tt> and returns a <code>DataSet</code> object
 * ready to be read.</tt>
 * <li>Then <tt>cleanUp</tt> must be called to free any remaining resources. Note
 * that the <code>DataSet</code> object returned by <tt>execute</tt> remains
 * accessible after this call.
 * </ul>
 *
 * The above list can be repeted any number of times in order to re-execute
 * a query.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
class ExecutionGraph(op: Operation, p: Properties = null) {

  private var r: DataSet = null
  private var dsf: DataSourceFactory = null
  private var start: OutputCommand = null
  private var opened: Boolean = false
  private var pm: Option[ProgressMonitor] = None
  
  private val refs: Array[String] = { op match {
      case q: QueryOutputCommand =>op.allChildren flatMap {c => c match {
            case s: Scan => s.table :: Nil
            case _ => Nil
          } } toArray   
      case _ => Array.empty
    }
  }
  
  def setProgressMonitor(p: ProgressMonitor) {
    if (p != null) {
      pm = Some(p)
    } else {
      pm = None
    }
  }

  /**
   * Prepares the query for execution.
   * @param dsf the <code>DataSourceFactory</code> against which this query
   *       will be executed.
   */
  def prepare(dsf: DataSourceFactory): Unit = {
    if (!opened) {
        // for readability
        implicit val pp = p
        
        start = {            (op, dsf) >=:
                 FunctionsStep >=:
                 PhysicalJoinOptimStep >=: 
                 BuilderStep
        }

      this.dsf = dsf;
      opened = true
      start.prepare(dsf)
      r = start.getResult
    }
  }

  /**
   * Runs the query and returns the result.
   * @return the result of the query
   */
  @throws(classOf[DriverException])
  def execute(): DataSet = {
    if (!opened) {
      throw new DriverException("ExecutionGraph is closed. Cannot execute a closed graph.")
    }
    try {
      start.execute(pm)
    } catch {
      case e: Exception => throw new DriverException(e)
    }
    r
  }

  /**
   * Cleans up the query and any associatd resource.
   */
  def cleanUp() = {
    if (opened) {
      start.cleanUp
      
      // IMPORTANT: this lets the GC do its work
      r = null
      start = null
      
      opened = false
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
}
