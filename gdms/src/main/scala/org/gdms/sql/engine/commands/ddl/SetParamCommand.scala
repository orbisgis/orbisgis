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
package org.gdms.sql.engine.commands.ddl

import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.sql.engine.commands._
import org.orbisgis.progress.ProgressMonitor

/**
 * Sets a runtime parameter to  some string value.
 * 
 * @param parameter name of parameter; if None, all params are reset to default
 * @param value the value; if None, the parameter is reset to default
 * @author Antoine Gourlay
 * @since 0.3
 */
class SetParamCommand(parameter: Option[String], value: Option[String]) extends Command with OutputCommand {
  
  protected final def doWork(r: Iterator[RowStream])(implicit pm: Option[ProgressMonitor]) = {
    parameter match {
      case Some(p) => {
          value match {
            case Some(v) => {
                dsf.getProperties.setProperty(p, v)
              }
            case None => {
                dsf.getProperties.remove(p)
              }
          }
        }
      case None => dsf.getProperties.clear
    }
    
    Iterator.empty
  }
  
  def getResult = null
  
  override val getMetadata = null
}
