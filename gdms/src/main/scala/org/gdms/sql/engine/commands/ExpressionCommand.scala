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

import org.gdms.sql.evaluator.Expression
import org.gdms.sql.evaluator.FieldEvaluator
import org.gdms.sql.engine.SemanticException
import org.gdms.sql.evaluator.DsfEvaluator
import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.sql.evaluator._

/**
 * This trait takes care of initializing the expressions a Command might handle.
 *
 * Implementing classes must implement <tt>exp</tt> to be the whole set of Expression
 * to init.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
trait ExpressionCommand extends Command {

  protected def exp: Seq[Expression]
  
  protected override def doPrepare() = {
    // all child metadata
    val allM = children map (_.getMetadata)
    
    // sets the index & DSF of every field in the expressions
    exp.foreach { e =>
      // sets the DSF everywhere, this is rather straight-forward
      setDsf(e)
      
      // indexes are offseted so as to be the index in the row resulting of the concatenation
      // of the rows of the child metadata objects (in the same order)
      var offset = 0
      for (m <- allM) {
        // sets actual field references (not from an outer query)
        setFields(e, m, offset, outer = false)
        offset = offset + m.getFieldCount
      }
    }
    
    // at this point, all valid but not referenced fields come from an outer query
    if (!outerReference.isEmpty) {
      exp.foreach { e =>
        var offset = 0
        for (m <- outerReference) {
          // sets field references from outer references
          setFields(e, m, offset, outer = true)
          offset = offset + m.getFieldCount
        }
      }
    }
    
    // at this point all valid fields have been initialized.
    
    // now we find the inner queries and give them the outer metadatas
    exp foreach (pushIntoSubQuery(_, allM))

    // validation
    // this will fail if any field is left un-initialized
    // this will also validate subqueries
    exp foreach (_ validate())
  }
  
  // holds references to the metadata of the outer query
  // this is set during prepare() of the outer query, by pushIntoSubQuery.
  private var outerReference: List[SQLMetadata] = Nil
  
  // holds references to the actual fields in this query that reference things from
  // the outer query. This is set during prepare() of the inner query (this), by SetFields
  var outerFieldEval: List[OuterFieldEvaluator] = Nil
  
  /**
   * Push available metadata of this (the outer query) to any inner query
   * so that they can validate() themselves
   */
  private def pushIntoSubQuery(exp: Expression, allM: List[SQLMetadata]) {
    // process children first
    exp.children foreach (pushIntoSubQuery(_, allM))
    
    exp.evaluator match {
      case i: QueryEvaluator => {
          if (i.command == null) {
            // the command was not initialized by the engine, which means it does not expect it here
            throw new IllegalStateException("Error: there cannot be a subquery in this clause.")
          }
          processSubCommand(i.command, allM)
        }
      case _ =>
    }
  }
  
  // sets the outerReference field of any ExpressionCommand in the inner query
  private def processSubCommand(c: Command, allM: List[SQLMetadata]) {
    // process children first
    c.children foreach (processSubCommand(_, allM))
    
    c match {
      case e: ExpressionCommand => {
          e.outerReference = allM ::: e.outerReference
        }
      case _ =>
    }
  }
  
   
  /**
   * Resolves fields against metadata objects of child commands (or outer metadata if outer is 
   * set to true).
   */
  private def setFields(e: Expression, m: SQLMetadata, offset: Int, outer: Boolean): Unit = {
    e.evaluator match {
      case f: FieldEvaluator => {
          f.table match {
            case None => {
                // no table name specified
                m.getFieldIndex(f.name) match {
                  case -1 => {
                      // the field is not directly referenced (maybe through a join?)
                      
                      // if it is indirectly referenced, it has the form "fieldName$tableName"
                      val pot = m.getFieldNames filter(_.startsWith(f.name))
                      pot.size match {
                        case 1 => {
                            // there is a single field in the join with that name. Success.
                            val i = m.getFieldIndex(pot.head)
                            if (!outer) {
                              f.index = i + offset
                              f.sqlType = m.getFieldType(i).getTypeCode
                            } else {
                              val g = OuterFieldEvaluator(f.name, f.table)
                              g.index = i + offset
                              g.sqlType = m.getFieldType(i).getTypeCode
                              outerFieldEval = g :: outerFieldEval
                              e.evaluator = g
                            }
                          }
                        case 0 => // there is no field with that name, maybe it will be resolved later.
                          // if not, it will fail at validation
                          
                          // there are too many fields with that name. Failure (ambiguous).
                        case i => throw new SemanticException("Field name '" + f.name + "' is ambiguous.")
                      }
                    }
                  case i =>
                    // the field is directly referenced.
                    if (f.index != -1) {
                      // this was already intialized by a previous call to setFields: ambiguous field!
                      throw new SemanticException("Field name '" + f.name + "' is ambiguous.")
                    }
                    
                    // Success
                    if (!outer) {
                      f.index = i + offset
                      f.sqlType = m.getFieldType(i).getTypeCode
                    } else {
                      val g = OuterFieldEvaluator(f.name, f.table)
                      g.index = i + offset
                      g.sqlType = m.getFieldType(i).getTypeCode
                      outerFieldEval = g :: outerFieldEval
                      e.evaluator = g
                    }
                }
              }
              
              // the table is direcly referenced...
            case Some(t) if m.table == t => m.getFieldIndex(f.name) match {
                // ... but there is no such field in that table. Failure (unknown field).
                case -1 => throw new SemanticException("There is no field '" + f.name + "' in table " + t + ".")
                case i =>
                  // ... and there is a field with that name. Success.
                  if (!outer) {
                    f.index = i + offset
                    f.sqlType = m.getFieldType(i).getTypeCode
                  } else {
                    val g = OuterFieldEvaluator(f.name, f.table)
                    g.index = i + offset
                    g.sqlType = m.getFieldType(i).getTypeCode
                    outerFieldEval = g :: outerFieldEval
                    e.evaluator = g
                  }
              }
              
              // a table is referenced, but indirectly, we look for an internal field name for it
              // the internal field name has the form "fieldName$tableName"
            case Some(t) => m.getFieldIndex(f.name + "$" + t) match {
                // there is none. Do nothing, it will be referenced later or will fail with validation.
                case -1 => 
                case i =>
                  // there is one, we keep it. Success.
                  if (!outer) {
                    f.index = i + offset
                    f.sqlType = m.getFieldType(i).getTypeCode
                  } else {
                    val g = OuterFieldEvaluator(f.name, f.table)
                    g.index = i + offset
                    g.sqlType = m.getFieldType(i).getTypeCode
                    outerFieldEval = g :: outerFieldEval
                    e.evaluator = g
                  }
              }
            case _ =>
          }
        }
      case _ => // not a field, do nothing
    }
    // process sub expressions
    e.foreach { setFields(_, m, offset, outer) }
  }

  /**
   * Sets DataSourceFactory to expressions that need it
   */
  private def setDsf(e: Expression): Unit = {
    e.evaluator match {
      case f: DsfEvaluator => f.dsf = dsf
      case _ => // not a function, do nothing
    }
    e.foreach (setDsf)
  }
  
  override def doCleanUp() {
    // reset state of expressions
    exp foreach (_ cleanUp())
    
    // reset state of this command
    outerReference = Nil
    outerFieldEval = Nil
  }
}
