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

package org.gdms.sql.engine.commands.ddl

import org.gdms.data.DataSourceFactory
import org.gdms.data.NoSuchTableException
import org.gdms.data.types.TypeFactory
import org.gdms.data.values.SQLValueFactory
import org.gdms.sql.engine.SemanticException
import org.gdms.sql.engine.commands._
import org.gdms.sql.engine.operations._
import scalaz.concurrent.Promise

/**
 * Command for altering the actual schema of a table (columns and types).
 * 
 * @author Antoine Gourlay
 * @since 0.1
 */
class AlterTableCommand(name: String, elems: Seq[AlterElement]) extends Command with OutputCommand {

  override def doPrepare = {
    if (!dsf.getSourceManager.exists(name)) {
      throw new NoSuchTableException(name)
    }
  }
  
  private def buildType(str: String) = {
    TypeFactory.createType(SQLValueFactory.getTypeCodeFromSqlIdentifier(str))
  }

  protected final def doWork(r: Iterable[Iterable[Promise[Iterable[Row]]]]) = {
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
            throw new UnsupportedOperationException("This is not supported yet because of lack of support from GDMS.")
//            // rename old column
//            val tempName = n
//            ds.setFieldName(ds.getFieldIndexByName(n), tempName)
//            
//            // create new column
//            ds.addField(n, buildType(newType))
        }
        case RenameColumn(n, newName) => {
            ds.setFieldName(ds.getFieldIndexByName(n), newName)
        }
      }}
    
    ds.commit
    ds.close

    null
  }

  val getResult = null

  // no result
  override val getMetadata = null
}