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

package org.gdms.sql.engine.commands

import java.util.TreeSet
import org.gdms.data.DataSource
import org.gdms.data.schema.DefaultMetadata
import org.gdms.data.types.Type
import org.gdms.data.types.TypeFactory
import org.gdms.data.values.ValueFactory
import scalaz.concurrent.Promise
import org.gdms.driver.memory.MemoryDataSetDriver
import scalaz.Scalaz._

/**
 * Delete command.
 *
 * This class deletes (from the underlying datasource) any row given to it.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
class DeleteCommand extends Command with OutputCommand {

  // ds from the ScanCommand
  var ds: DataSource = null

  // number of deleted rows
  var ro: Long = 0
  
  var indexes: TreeSet[Long] = new TreeSet
  
  var res: MemoryDataSetDriver = null

  protected def doWork(r: Iterable[Iterable[Promise[Iterable[Row]]]]) = {
    val m = children.head.getMetadata
    // gets the promises and apply "markRow" on them
    r.head foreach { _.get foreach (markRow(_)) }
    
    indexes foreach (deleteRow(_))
    
    null
  }

  override def doPrepare = {
    // this inits the ScanCommand
    super.doPrepare

    // then we find it and get the DataSource
    def find(ch: Seq[Command]): Option[Command] = ch.filter {
      _.isInstanceOf[ScanCommand] } headOption
    def recfind(ch: Seq[Command]): Option[Command] = find(ch) match {
      case a @ Some(x) => a
      case None => recfind(ch flatMap(_.children))
    }
    val c = recfind(children).get.asInstanceOf[ScanCommand]
    ds = c.ds
    
    res = new MemoryDataSetDriver(new DefaultMetadata(Array(TypeFactory.createType(Type.LONG)), Array("Deleted")))
  }

  // commit before the close() from ScanCommand
  override def preDoCleanUp = {
    ds.commit

    res.addValues(ValueFactory.createValue(ro))
  }

  def getResult = res.getTable("main")

  // no output
  override def getMetadata = SQLMetadata("",getResult.getMetadata)
  
  private def markRow(r: Row) {
    indexes.add(r.rowId.get)
  }
  
  private def deleteRow(l: Long) {
    ds.deleteRow(l - ro)
    ro = ro + 1
  }
}
