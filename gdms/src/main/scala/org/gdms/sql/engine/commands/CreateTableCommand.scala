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

import Row._
import java.io.File
import org.gdms.data.SourceAlreadyExistsException
import org.gdms.sql.engine.GdmSQLPredef._
import org.orbisgis.progress.ProgressMonitor

/**
 * This command creates a table with the name <tt>name</tt> from the result
 * of its first child (which has to be an OutputCommand).
 *
 *@param name the name of the table to create
 * @author Antoine Gourlay
 * @since 0.1
 */
class CreateTableCommand(name: String) extends Command with OutputCommand {
  
  private var resultFile: File = _

  override def preDoPrepare = {
    // register the new source
    // this will throw an exception if a source with that name already exists
    if (dsf.getSourceManager.exists(name)) {
      throw new SourceAlreadyExistsException("The source '" + name + "' already exists.")
    }
     
    resultFile = dsf.getResultFile
    
    // this checks if the underlying command is already materializing the results
    // if yes, then we do need to write ourselves.
    val o = children.head
    o match {
      case q: QueryOutputCommand => {
          // the output will write for us
          q.resultFile = resultFile
        }
      case _ => {
          // gdms will write for us
        }
    }
  }
  
  protected final def doWork(r: Iterator[RowStream])(implicit pm: Option[ProgressMonitor]) = {
    dsf.getSourceManager.register(name, resultFile)
    val o = children.head
    o match {
      case q: QueryOutputCommand => {
          // the output has written for us already          
        }
      case q: OutputCommand => {
          // gdms is writing for us
          dsf.saveContents(name, q.getResult)
        }
    }
    
    Iterator.empty
  }
  
  val getResult = null

  // no result
  override val getMetadata = null
}
