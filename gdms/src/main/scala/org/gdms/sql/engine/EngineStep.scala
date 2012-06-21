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

import java.util.Properties
import org.apache.log4j.Logger
import org.gdms.sql.engine.GdmSQLPredef._

trait EngineStep[I, O] {
  def >=:(input: I)(implicit p: Properties): O = doOperation(input)(p)
  
  def >=:[A](e: EngineStep[A, I])(implicit p: Properties): EngineStep[A, O] = {
    new EngineStep[A, O] {
      def doOperation(input: A)(implicit p: Properties): O = {
        (input >=: e) >=: EngineStep.this
      }
    }
  }
  
  protected def doOperation(input: I)(implicit p: Properties): O
}

abstract class AbstractEngineStep[I, O](name: String) extends EngineStep[I, O] {
  
  protected val LOG: Logger = Logger.getLogger(this.getClass)
  
  override def >=:(input: I)(implicit p: Properties): O = {
    if (isPropertyTurnedOn(Flags.EXPLAIN)) { 
      LOG.info("Starting task: " + name)
    }
    val o = doOperation(input)
    if (isPropertyTurnedOn(Flags.EXPLAIN)) {
      LOG.info("Finished task: " + name)
      LOG.info("With: " + o)
    }
    
    o
  }
}