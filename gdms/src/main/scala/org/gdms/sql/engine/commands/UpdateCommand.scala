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

import org.gdms.sql.engine.SemanticException
import org.gdms.sql.engine.commands.scan.ScanCommand
import org.gdms.sql.evaluator.Expression
import org.gdms.data.DataSource
import org.gdms.data.schema.DefaultMetadata
import org.gdms.data.types.IncompatibleTypesException
import org.gdms.data.types.Type
import org.gdms.data.types.TypeFactory
import org.gdms.data.values.ValueFactory
import org.gdms.driver.memory.MemoryDataSetDriver
import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.sql.evaluator.Field
import org.orbisgis.progress.ProgressMonitor

/**
 * Update command.
 *
 * This command takes a list of tuples (field_name, value) to update in the
 * datasource from the underlying ScanCommand.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
class UpdateCommand(e: Seq[(String, Expression)]) extends Command with ExpressionCommand with OutputCommand {

  // DataSource from the ScanCommand
  var ds: DataSource = null

  // number of updated rows
  var ro: Long = 0
  
  // holds the result set that will containt the number of updated rows
  var res: MemoryDataSetDriver = null

  // for ExpressionCommand to properly init the expressions
  override def exp = expr
  private var expr = e map( _._2 )

  protected def doWork(r: Iterator[RowStream])(implicit pm: Option[ProgressMonitor]) = {
    pm.map(_.startTask("Updating", 0))
    val m = children.head.getMetadata
    // function that will update a row
    val set = setRow(ds, e map { t => (m.getFieldIndex(t._1),t._2) }) _

    // update all input rows
    r.next foreach set
    
    // return the number of updated rows
    res.addValues(ValueFactory.createValue(ro))
    ro = 0
    
    pm.map(_.endTask)
    Iterator.empty
  }

  /**
   * Changes the value of an input row.
   */
  private def setRow(ds: DataSource, e: Seq[(Int, Expression)])(r: Row) = {
    ro = ro + 1
    e foreach { exp => ds.setFieldValue(r.rowId.get, exp._1, exp._2.evaluate(r)) }
  }

  override def doPrepare = {
    // finds the scan and get the DataSource
    def find(ch: Seq[Command]): Option[Command] = ch.filter {
      _.isInstanceOf[ScanCommand] }.headOption
    def recfind(ch: Seq[Command]): Option[Command] = find(ch) match {
      case a @ Some(x) => a
      case None => recfind(ch.flatMap(_.children))
    }
    val c = recfind(children).get.asInstanceOf[ScanCommand]
    ds = c.ds
    
    // checks for wrong field names on the left-hand side of the assignment
    val m = c.getMetadata
    e foreach { ee =>
      if (m.getFieldIndex(ee._1) == -1) {
        throw new SemanticException("There is no field '" + ee._1 + "' in the table '" + m.table + "'.")
      }
    }
    
    // this inits the expressions
    super.doPrepare
    
    // checks that the updated expressions can be implicitely cast to the column type.
    e foreach {ee =>
      val t = m.getFieldType(m.getFieldIndex(ee._1)).getTypeCode
      val s = ee._2.evaluator.sqlType
      if (!TypeFactory.canBeCastTo(s, t)) {
        throw new IncompatibleTypesException("The field '" + ee._1 + "' cannot be assigned: the expression cannot be implicitly cast " +
                                    "from type '" + TypeFactory.getTypeName(s) + "' to type '" + TypeFactory.getTypeName(t) + "'")
      }
    }
    
    res = new MemoryDataSetDriver(new DefaultMetadata(Array(TypeFactory.createType(Type.LONG)), Array("Updated")))
  }

  override def preDoCleanUp = {
    // commit before the close() from ScanCommand
    ds.commit
  }

  def getResult = res

  // no output
  override lazy val getMetadata = SQLMetadata("",getResult.getMetadata)
}
