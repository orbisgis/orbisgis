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
package org.gdms.sql.engine.commands

import org.gdms.data.DataSourceFactory
import org.gdms.sql.engine.GdmSQLPredef._
import org.orbisgis.progress.ProgressMonitor

/**
 * Base class for all commands.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
abstract class Command() {

  /**
   * Children of this command
   */
  var children: List[Command] = List.empty

  /**
   * The current DSF. It is set up during prepare(), *before* doPrepare() is called.
   */
  protected var dsf: DataSourceFactory = null

  /**
   * Executes this command.
   * 
   * All child command are executed (lazily) and given to the doWork() method for processing.
   * 
   * @param pm an optional ProgressMonitor for reporting status
   */
  def execute(implicit pm: Option[ProgressMonitor]): RowStream = {
    
    // start this one and return the promise of its result
    doWork ((for (c <- children.view) yield { c.execute }).toIterator)
  }

  /**
   * Main method that commands need to implement.
   * 
   * @param r the result of execute being called on the children of this command
   * @param pm an optional ProgressMonitor for reporting statuc
   */
  protected def doWork(r: Iterator[RowStream])(implicit pm: Option[ProgressMonitor]) : RowStream

  /**
   * Override this method to do something specific when the query has finished executing, after all children
   * are cleaned up.
   */
  protected def doCleanUp : Unit = {}

  /**
   * Override this method to do something specific when the query has finised executing, before all children
   * are cleaned up.
   */
  protected def preDoCleanUp: Unit = {}

  /**
   * Cleans any resources left open.
   */
  final def cleanUp: Unit = {
    preDoCleanUp
    children foreach( _.cleanUp )
    doCleanUp
    dsf = null
  }

  /**
   * Gets the query ready for treatement with the given DataSourceFactory.
   */
  final def prepare(dsf: DataSourceFactory): Unit = {
    this.dsf = dsf
    preDoPrepare
    children foreach( _.prepare(dsf) )
    doPrepare
  }

  /**
   * Override this method to do something specific right before the query starts, after all children
   * have been prepared.
   *
   * The DataSourceFactory is set at this point and can be used to validate table names, etc.
   */
  protected def doPrepare : Unit = {}
  
  /**
   * Override this method to do something specific right before the query starts, before all children
   * have been prepared.
   *
   * This DataSourceFactory is set at this point and can be used to validate table names, etc.
   */
  protected def preDoPrepare : Unit = {}

  /**
   * Returns the resulting metadata. Override this method to provide a specific metadata.
   * 
   * By default it returns the Metadata of the first child of this command.
   */
  def getMetadata: SQLMetadata = children.head.getMetadata

  /**
   * Adds c as a child command and returns this.
   * 
   * @param c a command to add as child
   */
  def withChild(c: Command): Command.this.type = {
    children = c :: children
    this
  }
  
  /**
   * Adds cc as children of this command and returns this.
   * 
   * @param cc commands to add as children
   */
  def withChildren(cc: Seq[Command]): Command.this.type  = {
    children = cc ++: children
    this
  }

  override def toString = {
    this.getClass.getName + " (" + children + ")"
  }

}
