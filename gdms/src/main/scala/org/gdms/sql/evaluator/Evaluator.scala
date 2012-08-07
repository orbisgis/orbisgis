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
package org.gdms.sql.evaluator

import org.gdms.data.DataSourceFactory
import org.gdms.data.types.Type
import org.gdms.data.values.Value
import org.gdms.data.values.ValueFactory
import org.gdms.sql.engine.SemanticException
import org.gdms.sql.engine.UnknownFieldException
import org.gdms.sql.engine.operations.Operation
import org.gdms.sql.function.AggregateFunction
import org.gdms.sql.function.FunctionException
import org.gdms.sql.function.FunctionValidator
import org.gdms.sql.function.ScalarArgument
import org.gdms.sql.function.ScalarFunction
import org.gdms.sql.engine.commands.Command
import org.gdms.sql.engine.commands.ExpressionCommand
import org.gdms.sql.engine.commands.QueryOutputCommand
import org.gdms.sql.engine.commands.Row
import org.gdms.sql.engine.GdmSQLPredef._
import org.orbisgis.progress.ProgressMonitor

/**
 * Base class for all expression evaluators.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
abstract class Evaluator {
  /**
   * Function defining the evaluation
   */
  def eval: (Row) => Value

  /**
   * Return type of the <tt>eval</tt> function
   */
  def sqlType: Int

  def childExpressions: Seq[Expression] = List.empty

  final def validate(): Unit = {
    childExpressions map (_ validate())
    doValidate()
  }
  
  final def preValidate(): Unit = {
    childExpressions map (_ preValidate())
    doPreValidate()
  }
  
  final def cleanUp() {
    childExpressions map (_ cleanUp())
    doCleanUp()
  }
  
  protected def doPreValidate(): Unit = {}

  protected def doValidate(): Unit = {}
  
  protected def doCleanUp(): Unit = {}
  
  def duplicate: Evaluator
}

/**
 * Evaluator for constant values
 */
case class StaticEvaluator(v: Value) extends Evaluator {
  def eval = s =>  v
  val sqlType = v.getType
  override def toString = v.toString
  def duplicate: StaticEvaluator = copy()
}

object cons {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: StaticEvaluator => Some(a.v)
      case _ => None
    }
  }
}

trait DsfEvaluator extends Evaluator {
  var dsf: DataSourceFactory = null
  
  override def doCleanUp() = dsf = null
}

/**
 * Evaluator for GDMS scalar functions.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class FunctionEvaluator(name: String, l: List[Expression]) extends Evaluator with DsfEvaluator {
  var f: ScalarFunction = null
  def eval = s => f.evaluate(dsf, l map ( _.evaluate(s)): _*)
  def sqlType = f.getType(l.map( _.evaluator.sqlType ) toArray)
  override val childExpressions = l
  override def doValidate() = {
    if (f == null) throw new FunctionException("Internal error: failed to initialized function for '" + name + "'.")
    val fs = FunctionValidator.failIfTypesDoNotMatchSignature(l.map(_.evaluator.sqlType) toArray,
                                                              f.getFunctionSignatures)
    
    // infers the type of direct NULL values as the expected type of the first
    // function signature that matches
    l.zip(fs.getArguments) foreach { a => a._1.evaluator.sqlType match {
        case Type.NULL =>
          val targetType = a._2.asInstanceOf[ScalarArgument].getTypeCode
          a._1.evaluator = CastEvaluator(Expression(a._1.evaluator), targetType)
        case _ =>
      }
    }
  }
  override def toString = if (f == null) name else f.getName + "(" + l + ")"
  
  def duplicate: FunctionEvaluator = {
    val ret = FunctionEvaluator(name, l map (_.duplicate))
    if (dsf != null) {
      ret.dsf = dsf
      // if initialized, set a new the function instance on duplication - #699
      if (f != null) {
        ret.f = dsf.getFunctionManager.getFunction(name).asInstanceOf[ScalarFunction]
      }
    }
    ret
  }  
  override def doCleanUp() = {
    super.doCleanUp()
    f == null
  }
}

object func {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: FunctionEvaluator => Some((a.name, a.f, a.l))
      case _ => None
    }
  }
}

/**
 * Evaluator for GDMS Aggregated functions.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class AggregateEvaluator(f: AggregateFunction, l: List[Expression]) extends Evaluator with DsfEvaluator {
  def eval = s => {
    f.evaluate(dsf, l map ( _.evaluate(s)): _*)
    ValueFactory.createNullValue[Value]
  }
  val finalValue = () => f.getAggregateResult
  def sqlType = f.getType(l.map(_.evaluator.sqlType) toArray)
  override val childExpressions = l
  override def doPreValidate() = {
    if (f == null) throw new FunctionException("The function does not exist.")
  }
  override def doValidate() = {
    val fs = FunctionValidator.failIfTypesDoNotMatchSignature(l.map(_.evaluator.sqlType) toArray,
                                                              f.getFunctionSignatures)
    
    // infers the type of direct NULL values as the expected type of the first
    // function signature that matches
    l.zip(fs.getArguments) foreach { a => a._1.evaluator.sqlType match {
        case Type.NULL =>
          val targetType = a._2.asInstanceOf[ScalarArgument].getTypeCode
          a._1.evaluator = CastEvaluator(Expression(a._1.evaluator), targetType)
        case _ =>
      }
    }
  }
  override def toString = "Func(" + f.getName + "(" + l + "))"
  
  def duplicate: AggregateEvaluator = {
    if (dsf == null) {
      throw new FunctionException("Internal Error: duplicating an uninitialized AggregateEvaluator")
    } else {
      AggregateEvaluator(dsf.getFunctionManager.getFunction(f.getName).asInstanceOf[AggregateFunction], l map (_.duplicate))
    }
  }
}

object agg {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: AggregateEvaluator => Some((a.f, a.l))
      case _ => None
    }
  }
}

/**
 * Evaluator for fields. The index is initialized by the Projection or WhereFilter Command.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class FieldEvaluator(name: String, table: Option[String] = None) extends Evaluator {
  def eval = _(index)
  var sqlType = -1
  var index: Int = -1
  override def doValidate() = index match {
    case -1 => throw new UnknownFieldException(name)
    case _ =>
  }
  override def toString = "Field(" + (if (table.isDefined) table.get + "." else "") + name + ")"
  
  def duplicate: FieldEvaluator = {
    val c = copy()
    c.sqlType = sqlType
    c.index = index
    c
  }
  
  override def doCleanUp() = {
    sqlType = -1
    index = -1
  }
}
 
object field {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: FieldEvaluator => Some((a.name, a.table))
      case _ => None
    }
  }
}
  
/**
 * Evaluator for field referenced from an outer level (in a subquery).
 * 
 * @author Antoine Gourlay
 * @since 0.3
 */
case class OuterFieldEvaluator(name: String, table: Option[String]) extends Evaluator {
  def setValue(r: Array[Value]) {value = r(index)}
  private var value: Value = null
  def eval = s => value
  var sqlType = -1
  var index: Int = -1
  override def doValidate() = index match {
    case -1 => throw new UnknownFieldException(name)
    case _ =>
  }
  
  def duplicate: OuterFieldEvaluator = {
    val c = copy()
    c.sqlType = sqlType
    c.index = index
    c.value = value
    c
  }
  
  override def toString = "OuterField(" + (if (table.isDefined) table.get + "." else "") + name + ")"
  
  override def doCleanUp() = {
    sqlType = -1
    index = -1
    value = null
  }
}

object outerField {
  def unapply(e: Expression) = {
    e.evaluator match {
      case OuterFieldEvaluator(n, t) => Some((n, t))
      case _ => None
    }
  }
}

/**
 * Evaluator for star fields. This placeholder is replaced during ProjectionCommand.prepare().
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class StarFieldEvaluator(except: Seq[String], table: Option[String]) extends Evaluator {
  def eval = throw new UnsupportedOperationException
  val sqlType = -1
  override def toString = "StarField(except=" + except + ")"
  def duplicate: StarFieldEvaluator = copy()
}
  
object star {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: StarFieldEvaluator => Some((a.except, a.table))
      case _ => None
    }
  }
}

/**
 * Evaluator for Oid special field.
 * 
 * @author Antoine Gourlay
 * @since 0.1
 */
case object OidEvaluator extends Evaluator {
  def eval = r => {
    if (r.rowId.isDefined) {
      ValueFactory.createValue(r.rowId.get)
    } else {
      ValueFactory.createNullValue[Value]
    }
  }
  def sqlType = Type.LONG
  def duplicate = this
}
  
object oid {
  def unapply(e: Expression) = {
    e.evaluator match {
      case OidEvaluator => true
      case _ => false
    }
  }
}

/**
 * Evaluator for "e :: type" or "CAST (e AS type)".
 * 
 * @author Antoine Gourlay
 * @since 0.3
 */
case class CastEvaluator(e: Expression, sqlType: Int) extends Evaluator {
  override val childExpressions = e :: Nil
  def eval = s => e.evaluate(s).toType(sqlType)
  def duplicate: CastEvaluator = CastEvaluator(e.duplicate, sqlType)
}
  
object castTo {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: CastEvaluator => Some((a.e, a.sqlType))
      case _ => None
    }
  }
}

/**
 * Special Evaluator for pre-computed but not constant expressions.
 * 
 * @author Antoine Gourlay
 * @since 0.3
 */
case class PreparedEvaluator(r: Row, e: Expression) extends Evaluator {
  override val childExpressions = e :: Nil
  def eval = _ => e.evaluate(r)
  def duplicate = PreparedEvaluator(r, e.duplicate)
  def sqlType = e.evaluator.sqlType
}

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

/**
 * Evaluator for scalar subquery values.
 * 
 * @author Antoine Gourlay
 * @since 2.0
 */
case class QueryToScalarEvaluator(var op: Operation) extends QueryEvaluator {
  
  private var returnType: Int = -1
  def sqlType = returnType

  override val childExpressions = Nil
    
  def eval = s => {
    val ex = evalInner(s)
    if (ex.hasNext) {
      ex.next.array(0)
    } else {
      ValueFactory.createNullValue[Value]
    }
  }
  
  override def doValidate() = {
    super.doValidate()
    
    returnType = command.getMetadata.getFieldType(0).getTypeCode
  }
  
  override def doCleanUp() = {
    super.doCleanUp()
    returnType = -1
  }
  
  def duplicate: QueryToScalarEvaluator = {
    val c = QueryToScalarEvaluator(op.duplicate)
    c.dsf = dsf
    c
  }
}

case class ParamEvaluator(name: String) extends Evaluator {
  val sqlType = -1
  
  def eval = s => sys.error("Internal Error! A parameter placeholder survived until execution.")
  
  val index: Int = -1
  override def doValidate() = throw new SemanticException("Parameter @{" + name + "} has not been set.")
  override def toString = "Param(" + name + ")"
  
  def duplicate: ParamEvaluator = copy()
}

object param {
  def unapply(e: Expression) = {
    e.evaluator match {
      case ParamEvaluator(n) => Some(n)
      case _ => None
    }
  }
}