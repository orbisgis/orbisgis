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
package org.gdms.sql.engine.commands

import java.util.TreeSet
import org.gdms.data.DataSource
import org.gdms.data.schema.DefaultMetadata
import org.gdms.data.types.{Type, TypeFactory}
import org.gdms.data.values.ValueFactory
import org.gdms.driver.memory.MemoryDataSetDriver
import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.sql.engine.commands.scan.ScanCommand
import org.orbisgis.progress.ProgressMonitor
import scala.collection.JavaConversions._

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
  var ds: DataSource = _

  // number of deleted rows
  var ro: Long = 0
  
  // holds the rowIndexes of the rows to delete
  // they are deleted during execute() but the DataSource
  // is actually committed during preDoCleanUp() (the query could be aborded)
  var indexes: TreeSet[Long] = new TreeSet
  
  // holds the result set that will containt the number of deleted rows
  var res: MemoryDataSetDriver = _

  protected def doWork(r: Iterator[RowStream])(implicit pm: Option[ProgressMonitor]) = {
    pm.map(_.startTask("Deleting", 0))
    val m = children.head.getMetadata
    
    // mark rows to delete
    r.next foreach (markRow)
    
    // delete them
    indexes.descendingIterator foreach (deleteRow)
    indexes.clear
    
    // return the number of rows that were deleted
    res.addValues(ValueFactory.createValue(ro))
    ro = 0
    
    pm.map(_.endTask)
    Iterator.empty
  }

  override def doPrepare() = {
    // this inits the ScanCommand
    super.doPrepare()

    // then we find it and get the DataSource
    def find(ch: Seq[Command]): Option[Command] = ch.filter (_.isInstanceOf[ScanCommand]) headOption
    def recfind(ch: Seq[Command]): Option[Command] = find(ch) match {
      case a @ Some(x) => a
      case None => recfind(ch flatMap(_.children))
    }
    val c = recfind(children).get.asInstanceOf[ScanCommand]
    ds = c.ds
    
    res = new MemoryDataSetDriver(new DefaultMetadata(Array(TypeFactory.createType(Type.LONG)), Array("Deleted")))
  }

  override def preDoCleanUp() = {
    // commit before the close() from ScanCommand
    ds.commit()
  }
  
  def getResult = res

  override lazy val getMetadata = SQLMetadata("",getResult.getMetadata)
  
  /**
   * Marks a row to be deleted.
   */
  private def markRow(r: Row) {
    indexes.add(r.rowId.get)
  }
  
  /**
   * Deletes a row from the DataSource.
   */
  private def deleteRow(l: Long) {
    ds.deleteRow(l)
    ro = ro + 1
  }
}
