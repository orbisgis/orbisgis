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

import scalaz.concurrent.Promise
import scalaz.concurrent.Promise._
import org.gdms.sql.engine.GdmSQLPredef._

/**
 * Main command for artificially limiting/offseting some dataset.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
class LimitOffsetCommand(lim: Int, off: Int) extends Command {

  protected def doWork(r: Iterable[Iterable[Promise[Iterable[Row]]]]) = {
    val res = if (off > 0) doOffset(r.head) else r.head
    if (lim > 0) doLimit(res) else res
  }

  private def doOffset(r: Iterable[Promise[Iterable[Row]]]): Iterable[Promise[Iterable[Row]]] = {
    var i = off
    
    val res = for(p <- r) yield {
      if (i > 0) {
        val ret = p.get flatMap {r =>
          i=i-1
          if (i >= 0) Nil else r :: Nil
        }
        promise(ret)
      }
      else p
    }

    res
  }

  private def doLimit(r: Iterable[Promise[Iterable[Row]]]): Iterable[Promise[Iterable[Row]]] = {
    var i = lim
    for(p <- r.view.takeWhile(_=>i > 0)) yield {
      val ret = p.get.view.takeWhile(_=>i > 0) map { r => i=i-1; r }
      promise(ret.force)
    }
  }
}
