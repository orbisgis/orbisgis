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

package org.gdms.sql.engine.commands.scan

import org.gdms.data.DataSource
import org.gdms.data.DataSourceFactory
import org.gdms.data.indexes.IndexQuery
import org.gdms.data.schema.Metadata
import org.gdms.sql.engine.commands._
import org.gdms.sql.engine.GdmSQLPredef._
import org.orbisgis.progress.NullProgressMonitor

class IndexQueryScanCommand(table: String, alias: Option[String] = None, var query: IndexQuery) extends Command {
  var ds: DataSource = null

  var metadata: Metadata = null
  
  private var end: Long = -1
  
  override protected def doCleanUp = {
    // close the DataSource
    if (ds != null) ds.close
  }

  override protected def doPrepare = {
    ds = dsf.getDataSource(table, DataSourceFactory.NORMAL)
    ds.open
    metadata = ds.getMetadata
    end = ds.getRowCount
    
  }

  protected def doWork(r: Iterator[RowStream]) = {
    if (query != null) {
      if (!dsf.getIndexManager.isIndexed(ds, query.getFieldName)) {
        dsf.getIndexManager.buildIndex(ds, query.getFieldName, new NullProgressMonitor)
      }
      
      val a = dsf.getIndexManager.queryIndex(ds, query)
      for (i <- a.par.view.toIterator) yield Row(i, ds.getRow(i))
    } else {
      null
    }
  }

  def commit = ds.commit

  override def getMetadata = SQLMetadata(alias.getOrElse(table), metadata)
}
