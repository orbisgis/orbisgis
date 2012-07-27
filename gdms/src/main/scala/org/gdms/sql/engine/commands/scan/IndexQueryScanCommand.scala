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
package org.gdms.sql.engine.commands.scan

import org.gdms.data.DataSource
import org.gdms.data.DataSourceFactory
import org.gdms.data.indexes.IndexQuery
import org.gdms.data.schema.Metadata
import org.gdms.sql.engine.commands._
import org.gdms.sql.engine.GdmSQLPredef._
import org.orbisgis.progress.NullProgressMonitor
import org.orbisgis.progress.ProgressMonitor

/**
 * Scan command based on an index query.
 * 
 * @param table table to scan
 * @param alias optional alias for the result set
 * @param query the index query
 * @author Antoine Gourlay
 * @since 0.3
 */
class IndexQueryScanCommand(table: String, alias: Option[String] = None, var query: IndexQuery) extends Command {
  // holds the DataSource to scan
  var ds: DataSource = _

  // the result set metadata
  var metadata: Metadata = _
  
  override protected def doCleanUp() = {
    // closes the DataSource
    if (ds != null) ds.close
  }

  override protected def doPrepare() = {
    ds = dsf.getDataSource(table, DataSourceFactory.NORMAL)
    ds.open
    metadata = ds.getMetadata    
  }

  protected def doWork(r: Iterator[RowStream])(implicit pm: Option[ProgressMonitor]) = {
    if (query != null) {
      // builds the index if it does not exist
      if (!dsf.getIndexManager.isIndexed(ds, query.getFieldNames)) {
        dsf.getIndexManager.buildIndex(ds, query.getFieldNames, pm.getOrElse(new NullProgressMonitor))
      }
      
      // queries the index
      val a = dsf.getIndexManager.queryIndex(ds, query)
      // returns the result
      for (i <- a.par.view.toIterator) yield Row(i, ds.getRow(i))
    } else {
      // there is no query... this is weird
      Iterator.empty
    }
  }

  override def getMetadata = SQLMetadata(alias.getOrElse(table), metadata)
}
