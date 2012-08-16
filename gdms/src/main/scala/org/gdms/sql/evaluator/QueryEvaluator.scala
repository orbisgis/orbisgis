/*
 * The GDMS library (Generic Datasources Management System)
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

package org.gdms.sql.evaluator

import org.gdms.data.values.Value
import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.sql.engine.SemanticException
import org.gdms.sql.engine.commands.{Command, QueryOutputCommand, ExpressionCommand}
import org.gdms.sql.engine.operations.Operation
import org.orbisgis.progress.ProgressMonitor

trait QueryEvaluator extends Evaluator with DsfEvaluator {
  
  var op: Operation
  
  var command: Command = null
  var pm: Option[ProgressMonitor] = None
  protected var materialized = false
  protected var matOut: QueryOutputCommand = null
  protected var evals: List[OuterFieldEvaluator]= Nil
  
  override def doPreValidate() = op.validate()
  
  override def doValidate() = {
    command.prepare(dsf)
    
    if (command.getMetadata.getFieldCount > 1) {
      throw new SemanticException("There can only be one selected field in an scalar subquery.")
    }
    
    findOuterFieldEvals(command)
    
    if (evals.isEmpty) {
      // subquery result can be cached: it does not depend on the outer query
      matOut = new QueryOutputCommand
      matOut.children = List(command)
      matOut.materialize(dsf)
    }
  }
  override def doCleanUp() = {
    super.doCleanUp()
    
    if (matOut != null) {
      matOut.cleanUp()
      matOut = null
    } else {
      command.cleanUp()
    }
    
    command = null
    pm = None
    materialized = false
    evals = Nil
  }
  
  protected def evalInner(s: Array[Value]) = {
    // independent inner query
    if (evals.isEmpty) {
      // materialize once
      if (materialized == false) {
        matOut.execute(pm)
        materialized = true
      }
      
      // iterate the result
      matOut.iterate()
    } else {
      // set outer field references and execute
      evals foreach (_.setValue(s))
      command.execute(pm)
    }
  }
  
  protected def findOuterFieldEvals(c: Command) { 
    c match {
      case e: ExpressionCommand => evals = e.outerFieldEval ::: evals
      case _ =>
    }
    c.children foreach (findOuterFieldEvals)
  }
}
