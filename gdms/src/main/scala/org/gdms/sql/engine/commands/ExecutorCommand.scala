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
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
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

import org.gdms.data.DataSource
import org.gdms.data.schema.Metadata
import org.gdms.data.types.Type
import org.gdms.sql.engine.SemanticException
import org.gdms.sql.evaluator.{CastEvaluator, Expression}
import org.gdms.sql.function._
import org.gdms.sql.function.executor.ExecutorFunction
import org.gdms.sql.function.table.TableFunction
import org.orbisgis.progress.NullProgressMonitor
import org.gdms.sql.engine.GdmSQLPredef._
import org.orbisgis.progress.ProgressMonitor

/**
 * Command dedicated to running {@link ExecutorFunction} functions.
 * 
 * @param name name of the function
 * @param params parameters
 * @author Antoine Gourlay
 * @since 0.1
 */
class ExecutorCommand(name: String, tables: Seq[Either[String, OutputCommand]], params: Seq[Expression]) extends Command with OutputCommand with ExpressionCommand {

  // finds the actual child commands
  children = tables flatMap (_.right toSeq) toList
  
  // splits params into tables and scalar
  val exp = params
  
  // holds the actual function instance
  private var function: ExecutorFunction = _
  
  // holds all tables opened before given to the function
  private var openedTables: List[Either[DataSource, OutputCommand]] = Nil

  override def doPrepare() = {
    // check if the function exists and is of correct type
    val f = dsf.getFunctionManager.getFunction(name)
    if (f == null) {
      throw new FunctionException("The function " + name + " does not exist.")
    } else {
      f  match {
        case _: ScalarFunction | _: AggregateFunction => throw new SemanticException("The function '" + name + "' cannot be used here. Syntax is:" +
                                                                                     " SELECT " + name + "(...) FROM myTable;")
        case e: ExecutorFunction => function = e
        case t: TableFunction => throw new SemanticException("The function '" + name
                                                             + "' cannot be used here. Syntax is: SELECT * FROM " +
                                                             name + "(...);")
        case _ => throw new SemanticException("Unknown function: '" + name + "'.")
      }
    }
    
    // initialize expressions
    super.doPrepare()
    
    // functions to open the inputs and get their metadata
    def forDs: String => Metadata = { s =>
      val ds = dsf.getDataSource(s)
      ds.open
      openedTables = Left(ds) :: openedTables
      ds.getMetadata
    }
    def forOut: OutputCommand => Metadata = {  o =>
      openedTables = Right(o) :: openedTables
      o.getMetadata
    }

    // opens everything and gets all metadata
    val dss = tables map (_ fold(forDs, forOut))
    
    // reverse: they were added in reverse order above
    openedTables = openedTables reverse
    
    // validates tables & parameter types
    FunctionValidator.failIfTablesDoNotMatchSignature(dss toArray, f.getFunctionSignatures)
    
    val fs = FunctionValidator.failIfTypesDoNotMatchSignature(params map(_.evaluator.sqlType) toArray,
                                                              f.getFunctionSignatures)
    // infers the type of direct NULL values as the expected type of the first
    // function signature that matches
    params.zip(fs.getArguments.filter(_.isScalar)) foreach { a => a._1.evaluator.sqlType match {
        case Type.NULL =>
          val targetType = a._2.asInstanceOf[ScalarArgument].getTypeCode
          a._1.evaluator = CastEvaluator(Expression(a._1.evaluator), targetType)
        case _ =>
      }
    }
    
  }

  protected final def doWork(r: Iterator[RowStream])(implicit pm: Option[ProgressMonitor]) = {
    pm.map(_.startTask("Executing", 0))
    
    // evaluates the function
    function.evaluate(dsf, openedTables map (_ fold(identity, _.getResult)) toArray, params map (_.evaluate(emptyRow)) toArray, new NullProgressMonitor)

    pm.map(_.endTask)
    Iterator.empty
  }
  
  override def doCleanUp() {
    // close sources
    openedTables flatMap (_.left toSeq) foreach (_.close)
    openedTables = Nil
    
    super.doCleanUp()
  }

  val getResult = null

  // no result
  override val getMetadata = null
}
