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

import org.gdms.data.DataSource
import org.gdms.data.DataSourceFactory
import org.gdms.data.schema.Metadata
import scalaz.concurrent.Promise
import scalaz.Scalaz._
import org.gdms.sql.engine.GdmSQLPredef._

/**
 * Default table scan command.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
class ScanCommand(table: String, alias: Option[String] = None, edition: Boolean = false) extends Command {
  var ds: DataSource = null

  var metadata: Metadata = null
  var groupSize : Int = 500

  override protected def doCleanUp = {
    // close the DataSource
    if (ds != null) ds.close
  }

  override protected def doPrepare = {
    ds = if (edition) dsf.getDataSource(table, DataSourceFactory.EDITABLE)
    else dsf.getDataSource(table, DataSourceFactory.NORMAL)
    ds.open
    metadata = ds.getMetadata
  }

  protected def doWork(r: Iterable[Iterable[Promise[Iterable[Row]]]]) = {
    // 1. iterate until ds.getRowCount
    // 2. iterate lazily
    // 3. group them by groupSize
    // 3. return a promise of a group of rows
    // 4. return the (lazy) iterator for this collection
    (0l until ds.getRowCount).view grouped(groupSize) map { s => 
      promise {  s.map { i => Row(ds.getRow(i), i) } toIterable }
    } toIterable
  }

  def commit = ds.commit

  override def getMetadata = SQLMetadata(alias.getOrElse(table), metadata)
}
