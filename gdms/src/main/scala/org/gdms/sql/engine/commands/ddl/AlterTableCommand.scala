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

import org.gdms.data.DataSourceFactory
import org.gdms.data.NoSuchTableException
import org.gdms.data.types.TypeFactory
import org.gdms.data.values.SQLValueFactory
import org.gdms.sql.engine.SemanticException
import org.gdms.sql.engine.commands._
import org.gdms.sql.engine.operations._
import org.gdms.sql.engine.GdmSQLPredef._
import org.orbisgis.progress.ProgressMonitor

/**
 * Command for altering the actual schema of a table (columns and types).
 * 
 * @param name a table to alter
 * @param elems some altering elements
 * @author Antoine Gourlay
 * @since 0.1
 */
class AlterTableCommand(name: String, elems: Seq[AlterElement]) extends Command with OutputCommand {

  override def doPrepare = {
    // checks that the table actually exists
    if (!dsf.getSourceManager.exists(name)) {
      throw new NoSuchTableException(name)
    }
  }
  
  /**
   * Builds a gdms Type object from an SQL type name.
   * 
   * @param str an SQL type name
   */
  private def buildType(str: String) = {
    TypeFactory.createType(SQLValueFactory.getTypeCodeFromSqlIdentifier(str))
  }

  protected final def doWork(r: Iterator[RowStream])(implicit pm: Option[ProgressMonitor]) = {
    val ds = dsf.getDataSource(name, DataSourceFactory.EDITABLE)
    
    ds.open
    elems foreach { e => e match {
        case AddColumn(n, t) => ds.addField(n, buildType(t))
        case DropColumn(n, ifExists) => {
            val index = ds.getFieldIndexByName(n)
            
            if (index != -1) {
              ds.removeField(index)
            } else if (!ifExists) {
              throw new SemanticException("Column '" + n + "' does not exist in table '" + name + "'")
            }
          }
        case AlterTypeOfColumn(n, newType, exp) => {
            throw new UnsupportedOperationException("Not supported yet.")
        }
        case RenameColumn(n, newName) => {
            ds.setFieldName(ds.getFieldIndexByName(n), newName)
        }
      }}
    
    ds.commit
    ds.close

    Iterator.empty
  }

  val getResult = null

  // no result
  override val getMetadata = null
}