/*
 * The GDMS library (Generic Datasources Management System)
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

import java.io.{ObjectOutputStream, OutputStream, IOException}
import org.gdms.data.DataSourceFactory
import org.gdms.data.values.Value
import org.orbisgis.progress.ProgressMonitor

class SQLScript(private[engine] val sts: Seq[SQLStatement]) {

  def getSQL = sts.map(_.getSQL).mkString("\n")
  
  def setDataSourceFactory(dsf: DataSourceFactory): Unit = {
    sts foreach (_.setDataSourceFactory(dsf))
  }
  
  def setProgressMonitor(p: ProgressMonitor) {
    sts foreach (_.setProgressMonitor(p))
  }
  
  def setValueParameter(name: String, v: Value) {
    sts foreach (_.setValueParameter(name, v))
  }
  
  def setFieldParameter(name: String, fieldName: String) {
    sts foreach (_.setFieldParameter(name, fieldName))
  }
  
  def setTableParameter(name: String, tableName: String) {
    sts foreach (_.setTableParameter(name, tableName))
  }
  
  def execute() {
    sts foreach { s =>
      s.prepare
      s.execute
      s.cleanUp
    }
  }
  
  def getSize: Int = sts.size
  
  @throws(classOf[IOException])
  def save(out: OutputStream) {
    var objs: List[ObjectOutputStream] = Nil
    try {
      val o = new ObjectOutputStream(out)
      o.writeInt(sts.size)
      o.flush
      objs = List(o)
      
      sts map { s =>
          val o = new ObjectOutputStream(out)
          objs = o :: objs
          
          o.writeUTF(s.getSQL)
          o.flush
          
          val o2 = new ObjectOutputStream(out)
          objs = o2 :: objs
          
          o2.writeObject(s.op)
          o2.flush
      }
      
    } finally {
      objs map (_.close)
      out.close // just to be sure
    }
  }
}
