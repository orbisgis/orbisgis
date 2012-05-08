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

import org.gdms.data.schema.DefaultMetadata
import org.gdms.data.types.IncompatibleTypesException
import org.gdms.data.types.TypeFactory
import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.sql.engine.SemanticException
import org.orbisgis.progress.ProgressMonitor

/**
 * Merges together the result sets of two commands.
 *  
 * @author Antoine Gourlay
 * @since 0.3
 */
class UnionCommand extends Command {

  // a flatten is all it takes, provided that doPrepare checks for any problems
  def doWork(r: Iterator[RowStream])(implicit pm: Option[ProgressMonitor]) = r flatten
  
  private val metadata: DefaultMetadata = new DefaultMetadata
  
  override def doPrepare {
    val me = children map(_.getMetadata)
    
    // checks that they all have the same number of fields
    val c = me.head.getFieldCount
    me.tail.find(_.getFieldCount != c) match {
      case Some(m) => throw new SemanticException("Cannot create the union of two tables with a different number" +
                                                  "of columns. One is " + c + " and an other is " + m.getFieldCount)
      case None =>
    }
    
    // checks that all types are compatible: implicit widening is allowed.
    val types = (0 until c) map (i => me map(_.getFieldType(i).getTypeCode))
    val finaltypes = types map {t =>
      var wider = t.head
      t.tail foreach { tt =>
        if (!TypeFactory.canBeCastTo(tt, wider)) {
          if (TypeFactory.canBeCastTo(wider, tt)) {
            wider = tt
          } else {
            throw new IncompatibleTypesException("Cannot create the union of two columns with types '" +
                                                 TypeFactory.getTypeName(wider) + "' and '" +
                                                 TypeFactory.getTypeName(tt) + "'.")
          }
        }
      }
      wider
    }
    
    metadata.clear
    // keep the field names of the first command (this is completely arbitrary)
    finaltypes.zipWithIndex foreach {t =>
      metadata.addField(me.head.getFieldName(t._2), t._1)
    }
  }
  
  override def getMetadata = SQLMetadata("", metadata)
}
