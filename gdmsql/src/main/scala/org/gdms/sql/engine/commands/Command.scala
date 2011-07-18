/** OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
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
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.sql.engine.commands

import org.gdms.data.SQLDataSourceFactory
import scalaz.concurrent.Promise
import scalaz.Scalaz._

/**
 * Base class for all commands
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
abstract class Command() {

  /**
   * Children of this command
   */
  var children: List[Command] = List.empty

  protected var dsf: SQLDataSourceFactory = null

  // Row represents a row
  // Iterable[Row] represents a batch of rows (possibly empty, or reduced to one row)
  // Promise[Iterable[Row]] represents the promise of a future batch of row.
  // Its computation is currently running or already done
  // Iterable[Promise[Iterable[Row]]] represents a whole dataset.

  final def execute(): Iterable[Promise[Iterable[Row]]] = {

    // start sub-commands first
    val list = children map ( _.execute )

    // start this one and return the promise of its result
    doWork(list)
  }

  /**
   * Main method that commands need to implement
   */
  protected def doWork(r: Iterable[Iterable[Promise[Iterable[Row]]]]) : Iterable[Promise[Iterable[Row]]]

  /**
   * Override this method to do something specific when the query has finished executing, after all children
   */
  protected def doCleanUp : Unit = {}

  /**
   * Override this method to do something specific when the query has finised executing, before all children
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
   * Gets the query ready for treatement with the given DataSourceFactory. Also performs
   * some final query validations
   */
  final def prepare(dsf: SQLDataSourceFactory): Unit = {
    this.dsf = dsf
    children foreach( _.prepare(dsf) )
    doPrepare
  }

  /**
   * Override this method to do something specific right before the query starts.
   *
   * This DataSourceFactory is set at this point and can be used to validate table names, etc.
   */
  protected def doPrepare : Unit = {}

  protected def validate : Unit = {}

  /**
   * Returns the resulting metadata. Override this method to provide a specific metadata.
   */
  def getMetadata: SQLMetadata = children.head.getMetadata

  def addAsChildrenOf(c: Command) : Command = {
    if (c == null) { this }
    else { 
      c.children = this :: c.children
      c }
  }

  override def toString = {
    this.getClass.getName + " (" + children + ")"
  }

}
