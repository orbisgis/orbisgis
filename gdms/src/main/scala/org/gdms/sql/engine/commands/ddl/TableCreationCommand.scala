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
package org.gdms.sql.engine.commands.ddl

import org.gdms.data.file.FileSourceCreation
import org.gdms.data.schema.DefaultMetadata
import org.gdms.data.types.Constraint
import org.gdms.data.values.SQLValueFactory
import org.gdms.sql.engine.SemanticException
import org.gdms.sql.engine.commands._
import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.sql.engine.operations.ConstraintType
import org.orbisgis.progress.ProgressMonitor

/**
 * Command for creating an empty table by specifying its column names and types.
 * 
 * @param name name of the new table
 * @param cols column names, types and constraints
 * @author Antoine Gourlay
 * @since 0.1
 */
class TableCreationCommand(name: String, cols: Seq[(String, String, Seq[ConstraintType])]) extends Command with OutputCommand {

  // holds the column names, gdms type code and constraints to create
  private var initcols : Seq[(String, Int, Array[Constraint])] = Nil
  
  override def doPrepare {
    initcols = cols map (c =>
      (c._1,
       // maps a type name to a gdms type code
       try {
          SQLValueFactory.getTypeCodeFromSqlIdentifier(c._2)
        } catch {
          case i: IllegalArgumentException => throw new SemanticException("Unknown type: '" +
                                                                          c._2 + "'.")
        }
       , c._3.map(_.constraint) toArray))
    
  }
  
  protected final def doWork(r: Iterator[RowStream])(implicit pm: Option[ProgressMonitor]) = {
    val m = new DefaultMetadata
    
    initcols foreach {c =>
      if (c._3.isEmpty) {
        m.addField(c._1, c._2)
      } else {
        m.addField(c._1, c._2, c._3: _*)
      }
    }
    
    val f = new FileSourceCreation(dsf.getResultFile, m)
    val dsd = dsf.createDataSource(f)
    dsf.getSourceManager.register(name, dsd)

    Iterator.empty
  }
  
  override def doCleanUp = initcols = Nil
  
  val getResult = null

  // no result
  override val getMetadata = null
}
