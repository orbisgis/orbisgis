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

import collection.JavaConversions._
import org.gdms.data.schema.DefaultMetadata
import org.gdms.data.types.Type
import org.gdms.data.types.TypeFactory
import org.gdms.data.values.ValueFactory
import org.gdms.driver.memory.MemoryDataSetDriver
import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.sql.engine.commands._
import org.orbisgis.progress.ProgressMonitor

class ShowParamCommand(parameter: Option[String]) extends Command with OutputCommand {

  var res: MemoryDataSetDriver = null
  val m = new DefaultMetadata(Array(TypeFactory.createType(Type.STRING), TypeFactory.createType(Type.STRING)), Array("Name", "Value"))
  
  override def doPrepare = {
    res = new MemoryDataSetDriver(m)
  }
  
  protected final def doWork(r: Iterator[RowStream])(implicit pm: Option[ProgressMonitor]) = {
    parameter match {
      case Some(p) => {
          res.addValues(ValueFactory.createValue(p), ValueFactory.createValue(dsf.getSqlEngine.getProperties.getProperty(p)))
      }
      case None => {
          dsf.getSqlEngine.getProperties.entrySet foreach {a => 
            res.addValues(ValueFactory.createValue(a.getKey.asInstanceOf[String]), ValueFactory.createValue(a.getValue.asInstanceOf[String]))
          }
        }
    }
    
    null
  }
  
  def getResult = res
  
  override val getMetadata = SQLMetadata("",m)
}
