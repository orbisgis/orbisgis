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
package org.gdms.sql.evaluator

import org.gdms.data.DataSourceFactory
import org.gdms.data.types.Type
import org.gdms.data.types.TypeFactory
import org.gdms.data.values.Value
import org.gdms.data.values.ValueFactory
import org.gdms.sql.engine.UnknownFieldException
import org.gdms.sql.function.AggregateFunction
import org.gdms.sql.function.FunctionException
import org.gdms.sql.function.FunctionValidator
import org.gdms.sql.function.ScalarFunction
import org.gdms.sql.engine.commands.Row
import org.gdms.sql.engine.GdmSQLPredef._

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

  final def validate: Unit = {
    childExpressions map (_ validate)
    doValidate
  }
  
  final def preValidate: Unit = {
    childExpressions map (_ preValidate)
    doPreValidate
  }
  
  final def cleanUp {
    childExpressions map (_ cleanUp)
    doCleanUp
  }
  
  protected def doPreValidate: Unit = {}

  protected def doValidate: Unit = {}
  
  protected def doCleanUp: Unit = {}
  
  protected def doCopy: Evaluator
  
  protected def postDuplicate(n: Evaluator): Unit = {}
   
  final def duplicate: Evaluator = {
    val c = doCopy
    c.childExpressions map (e => e.evaluator = e.evaluator.duplicate)
    postDuplicate(c)
    c
  }
}

/**
 * Evaluator for constant values
 */
case class StaticEvaluator(v: Value) extends Evaluator {
  def eval = s =>  v
  val sqlType = v.getType
  override def toString = v.toString
  def doCopy = copy()
}

trait DsfEvaluator extends Evaluator {
  var dsf: DataSourceFactory = null
  
  override def postDuplicate(n: Evaluator) = {
    if (dsf != null) {
      n.asInstanceOf[DsfEvaluator].dsf = dsf
    }
  }
  
  override def doCleanUp = dsf = null
}

object cons {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: StaticEvaluator => Some(a.v)
      case _ => None
    }
  }
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
  def sqlType = f.getType(l.map { e => TypeFactory.createType(e.evaluator.sqlType) } toArray).getTypeCode
  override val childExpressions = l
  override def doValidate = {
    if (f == null) throw new FunctionException("Internal error: failed to initialized function for '" + name + "'.")
    FunctionValidator.failIfTypesDoNotMatchSignature(
      l.map { e => TypeFactory.createType(e.evaluator.sqlType) } toArray,
      f.getFunctionSignatures)
  }
  override def toString = if (f == null) name else f.getName + "(" + l + ")"
  
  def doCopy = {
    FunctionEvaluator(name, l)
  }  
  override def doCleanUp = {
    super.doCleanUp
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
  def sqlType = f.getType(l.map { e => TypeFactory.createType(e.evaluator.sqlType) } toArray).getTypeCode
  override val childExpressions = l
  override def doPreValidate = {
    if (f == null) throw new FunctionException("The function does not exist.")
  }
  override def doValidate = {
    FunctionValidator.failIfTypesDoNotMatchSignature(
      l.map { e => TypeFactory.createType(e.evaluator.sqlType) } toArray,
      f.getFunctionSignatures)
  }
  override def toString = f.getName + "(" + l + ")"
  
  def doCopy = {
    AggregateEvaluator(dsf.getFunctionManager.getFunction(f.getName).asInstanceOf[AggregateFunction], l)
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
  override def doValidate = index match {
    case -1 => throw new UnknownFieldException(name)
    case _ =>
  }
  override def toString = "Field(" + (if (table.isDefined) table.get + "." else "") + name + ")"
  
  override def postDuplicate(n: Evaluator) = {
    val f = n.asInstanceOf[FieldEvaluator]
    f.sqlType = sqlType
    f.index = index
  }
  def doCopy = copy()
  
  override def doCleanUp = {
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
  override def doValidate = index match {
    case -1 => throw new UnknownFieldException(name)
    case _ =>
  }
  def doCopy = copy()
  override def postDuplicate(n: Evaluator) = {
    val f = n.asInstanceOf[FieldEvaluator]
    f.sqlType = sqlType
    f.index = index
  }
  override def toString = "OuterField(" + (if (table.isDefined) table.get + "." else "") + name + ")"
  
  override def doCleanUp = {
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
  def doCopy = copy()
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
case class OidEvaluator() extends Evaluator {
  def eval = r => {
    if (r.rowId.isDefined) {
      ValueFactory.createValue(r.rowId.get)
    } else {
      ValueFactory.createNullValue[Value]
    }
  }
  val sqlType = Type.LONG
  def doCopy = new OidEvaluator
}
  
object oid {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: OidEvaluator => true
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
  def doCopy = copy()
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
  def doCopy = copy()
  def sqlType = e.evaluator.sqlType
}