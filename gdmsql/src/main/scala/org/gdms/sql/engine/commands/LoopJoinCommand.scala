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

import org.gdms.data.schema.DefaultMetadata
import org.gdms.sql.engine.GdmSQLPredef._
import scalaz.concurrent.Promise
import scalaz.Scalaz._

/**
 * This command performs a basic block-nested loop cross join for 2 tables.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
class LoopJoinCommand extends Command {
  protected final def doWork(r: Iterable[Iterable[Promise[Iterable[Row]]]]) = {
    //This method just concats two 'rows' into one, inside the Iterable objects
    val doReduce = (i: Iterable[Row], j: Iterable[Row]) => (i <**> j) ((a, b) => Row(a ++ b))

    val left = r.head
    val right = r.tail.head
    // for every batch in left, we take avery batch in right and apply
    // the doReduce function within the Promise objects
    left flatMap (p => right map { q => (p <**> q)(doReduce) } )
  }

  protected override final def doPrepare = {
    // we are going to join, so we want table rows in small batches
    children foreach { _ match {
      case c: ScanCommand => c.groupSize = 10
      case _ =>
    } }
  }

  
  override def getMetadata = {
    val d = new DefaultMetadata()
    children foreach { c => addAndRename(d, c.getMetadata) }
    SQLMetadata("", d)
  }
  
  private def addAndRename(d: DefaultMetadata, m: SQLMetadata) {
    // fields are given an internal name 'field$table'
    // for reference by expressions upper in the query tree
    m.getFieldNames.zipWithIndex foreach { n =>
        d.addField(n._1 + "$" + m.table,m.getFieldType(n._2))
    }
  }
}