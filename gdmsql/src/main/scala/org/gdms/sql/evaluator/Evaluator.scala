/** OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
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
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */

package org.gdms.sql.evaluator

import org.gdms.data.SQLDataSourceFactory
import org.gdms.data.types.TypeFactory
import org.gdms.data.values.Value
import org.gdms.data.values.ValueFactory
import org.gdms.data.values.SQLValueFactory
import org.gdms.sql.engine.UnknownFieldException
import org.gdms.sql.function.AggregateFunction
import org.gdms.sql.function.FunctionException
import org.gdms.sql.function.FunctionManager
import org.gdms.sql.function.FunctionValidator
import org.gdms.sql.function.ScalarFunction

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
  def eval: (Array[Value]) => Value

  /**
   * Return type of the <tt>eval</tt> function
   */
  def sqlType: Int

  def childExpressions: Seq[Expression] = List.empty

  final def validate: Unit = {
    childExpressions map (_ validate)
    if (!canPreValidate) doPreValidate
    doValidate
  }
  
  final def preValidate: Unit = {
    childExpressions map (_ preValidate)
    if (canPreValidate) doPreValidate
  }
  
  private def canPreValidate: Boolean = childExpressions.find(_.evaluator.isInstanceOf[FieldEvaluator]) == None
  
  protected def doPreValidate: Unit = {}

  protected def doValidate: Unit = {}
  
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
  var dsf: SQLDataSourceFactory = null
  
  override def postDuplicate(n: Evaluator) = {
    if (dsf != null) {
      n.asInstanceOf[DsfEvaluator].dsf = dsf
    }
  }
}

/**
 * Evaluator for GDMS scalar functions.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class FunctionEvaluator(f: ScalarFunction, l: List[Expression]) extends Evaluator with DsfEvaluator {
  def eval = s => f.evaluate(dsf, l map ( _.evaluate(s)): _*)
  def sqlType = f.getType(l.map { e => TypeFactory.createType(e.evaluator.sqlType) } toArray).getTypeCode
  override val childExpressions = l
  override def doPreValidate = {
    if (f == null) throw new FunctionException("The function does not exist.")
    FunctionValidator.failIfTypesDoNotMatchSignature(
      l.map { e => TypeFactory.createType(e.evaluator.sqlType) } toArray,
      f.getFunctionSignatures)
  }
  override def toString = f.getName + "(" + l + ")"
  
  def doCopy = {
    FunctionEvaluator(FunctionManager.getFunction(f.getName).asInstanceOf[ScalarFunction], l)
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
    FunctionValidator.failIfTypesDoNotMatchSignature(
      l.map { e => TypeFactory.createType(e.evaluator.sqlType) } toArray,
      f.getFunctionSignatures)
  }
  override def toString = f.getName + "(" + l + ")"
  
  def doCopy = {
    AggregateEvaluator(FunctionManager.getFunction(f.getName).asInstanceOf[AggregateFunction], l)
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
}

case class CastEvaluator(e: Expression, sqlType: Int) extends Evaluator {
  override val childExpressions = e :: Nil
  def eval = s => e.evaluate(s).toType(sqlType)
  def doCopy = copy()
}