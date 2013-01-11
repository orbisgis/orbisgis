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
package org.gdms.sql.engine.commands.ddl

import org.gdms.data.NoSuchTableException
import org.gdms.sql.engine.commands._
import org.gdms.sql.engine.GdmSQLPredef._
import org.orbisgis.progress.ProgressMonitor

/**
 * Command for dropping one or several tables. If <code>purge</code> is specified, the actual resource behind
 * the Source is actually deleted.
 * 
 * @param names names of the tables to drop
 * @param ifExists throws no error if a table does not exist
 * @param purge true if the source have to be deleted
 * @author Antoine Gourlay
 * @since 0.1
 */
class DropTablesCommand(names: Seq[String], ifExists: Boolean, purge: Boolean) extends Command with OutputCommand {

  override def doPrepare() = {
    if (!ifExists) {
      // checks that tables do exist
      names foreach { n =>
        if (!dsf.getSourceManager.exists(n)) {
          throw new NoSuchTableException(n)
        }
      }
    }
  }
  
  protected final def doWork(r: Iterator[RowStream])(implicit pm: Option[ProgressMonitor]) = {
    if (purge) {
      names foreach dsf.getSourceManager.delete}
    else {
      names foreach dsf.getSourceManager.remove}

    Iterator.empty
  }

  val getResult = null

  // no result
  override val getMetadata = null
}
