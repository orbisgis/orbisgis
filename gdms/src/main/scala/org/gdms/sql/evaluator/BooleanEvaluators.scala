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

import org.gdms.data.types.IncompatibleTypesException
import org.gdms.data.types.Type
import org.gdms.data.types.TypeFactory
import org.gdms.data.values.Value
import org.gdms.data.values.ValueFactory
import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.sql.engine.SemanticException
import org.gdms.sql.engine.commands.Command
import org.gdms.sql.engine.commands.ExpressionCommand
import org.gdms.sql.engine.operations.Operation
import org.orbisgis.progress.ProgressMonitor

/**
 * Base evaluator for boolean comparisons.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
abstract sealed class BooleanEvaluator extends Evaluator {
  val sqlType = Type.BOOLEAN
  override def doValidate = {
    childExpressions map ( _.evaluator.sqlType ) find
    (_ != Type.BOOLEAN) match {
      case Some(_) => throw new IncompatibleTypesException
      case None =>
    }
  }
}

/**
 * Evaluator for value1 and value2.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class AndEvaluator(e1: Expression, e2: Expression) extends BooleanEvaluator {
  def eval = s => e1.evaluate(s) and e2.evaluate(s)
  override val childExpressions = e1 :: e2 :: List.empty
  override def toString = "(" + e1 + " AND " + e2 + ")"
  def doCopy = copy()
}

object & {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: AndEvaluator => Some((a.e1, a.e2))
      case _ => None
    }
  }
}

/**
 * Evaluator for value1 or value2.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class OrEvaluator(e1: Expression, e2: Expression) extends BooleanEvaluator {
  def eval = s => e1.evaluate(s) or e2.evaluate(s)
  override val childExpressions = e1 :: e2 :: List.empty
  override def toString = "(" + e1 + " OR " + e2 + ")"
  def doCopy = copy()
}

object | {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: OrEvaluator => Some((a.e1, a.e2))
      case _ => None
    }
  }
}

/**
 * Evaluator for "not value1".
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class NotEvaluator(e1: Expression) extends BooleanEvaluator {
  def eval = s => e1.evaluate(s) not
  override val childExpressions = e1 :: List.empty
  override def toString = "NOT (" + e1 + ")"
  def doCopy = copy()
}

object ! {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: NotEvaluator => Some(a.e1)
      case _ => None
    }
  }
}

/**
 * Evaluator for value1 = value2.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class EqualsEvaluator(e1: Expression, e2: Expression) extends BooleanEvaluator {
  def eval = s => e1.evaluate(s) equals e2.evaluate(s)
  override val childExpressions = e1 :: e2 :: List.empty
  override val doPreValidate = {}
  override def toString = "(" + e1 + " = " + e2 + ")"
  override def doValidate = {
    val t1 = e1.evaluator.sqlType
    val t2 = e2.evaluator.sqlType
    if (!TypeFactory.canBeCastTo(t1, t2) && !TypeFactory.canBeCastTo(t2, t1)) {
      throw new IncompatibleTypesException("Expressions of type " + TypeFactory.getTypeName(t1) + " and type"
                                           + TypeFactory.getTypeName(t2) + " cannot be compared.")
    }
  }
  def doCopy = copy()
}

object === {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: EqualsEvaluator => Some((a.e1, a.e2))
      case _ => None
    }
  }
}

/**
 * Evaluator for value1 IS NULL.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class IsNullEvaluator(e1: Expression) extends BooleanEvaluator {
  def eval = s => ValueFactory.createValue(e1.evaluate(s) isNull)
  override val childExpressions = e1 :: List.empty
  override val doValidate = {}
  override def toString = "ISNULL (" + e1 + ")"
  def doCopy = copy()
}

object isNull {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: IsNullEvaluator => Some(a.e1)
      case _ => None
    }
  }
}

/**
 * Evaluator for value1 IN (value2, value3, value4, ...).
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class InListEvaluator(e1: Expression, e2:Seq[Expression]) extends BooleanEvaluator {
  def eval = s => {
    val v:Seq[Value] = e2.map { _.evaluate(s) }
    val v0 = e1.evaluate(s)
    v.find { _.isNull } match {
      case Some(_) => ValueFactory.createNullValue[Value]
      case None => ValueFactory.createValue(v contains v0)
    }
  }
  override val childExpressions = e1 :: e2.toList
  override def doValidate = {
    val t = e1.evaluator.sqlType
    e2 foreach (e => if (!TypeFactory.canBeCastTo(e.evaluator.sqlType, t)) {
        throw new IncompatibleTypesException("Value of type '" + TypeFactory.getTypeName(e.evaluator.sqlType)
                                             + "' cannot be cast to type '" + TypeFactory.getTypeName(t))
      })
  }
  override def toString = "(" + e1 + " IN (" + e2 + ")"
  def doCopy = copy()
}

object inList {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: InListEvaluator => Some((a.e1, a.e2))
      case _ => None
    }
  }
}

/**
 * Evaluator for WHERE EXISTS (SELECT ...).
 * 
 * @author Antoine Gourlay
 * @since 0.3
 */
case class ExistsEvaluator(var o: Operation) extends BooleanEvaluator with DsfEvaluator {
  
  var command: Command = null
  var pm: Option[ProgressMonitor] = None
  
  override val childExpressions = Nil
  
  private def evalInner(s: Array[Value]) = {
    evals foreach (_.setValue(s))
    command.execute(pm)
  }
  
  def eval = s => ValueFactory.createValue(!evalInner(s).isEmpty)
  override def doPreValidate = o.validate
  override def doValidate = {
    command.prepare(dsf)
    
    findOuterFieldEvals(command)
  }
  
  private var evals: List[OuterFieldEvaluator]= Nil
  
  private def findOuterFieldEvals(c: Command) { 
    c match {
      case e: ExpressionCommand => evals = e.outerFieldEval ::: evals
      case _ =>
    }
    c.children foreach (findOuterFieldEvals)
  }
  
  def doCopy = ExistsEvaluator(o)
}

object exists {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: ExistsEvaluator => Some(a.o)
      case _ => None
    }
  }
}

/**
 * Evaluator for "value IN (SELECT ...)".
 * 
 * @author Antoine Gourlay
 * @since 0.3
 */
case class InEvaluator(e: Expression, var o: Operation) extends BooleanEvaluator with DsfEvaluator {
  var command: Command = null
  var pm: Option[ProgressMonitor] = None
  
  override val childExpressions = e :: Nil
  
  private def evalInner(s: Array[Value]) = {
    evals foreach (_.setValue(s))
    command.execute(pm)
  }
  
  def eval = s => {
    val b = e.evaluate(s)
    var ret: Value = ValueFactory.FALSE
    
    if (b.isNull) {
      ret = b
    } else {
      var break: Boolean = false
      val ex = evalInner(s)
      while (!break && ex.hasNext) {
        val next = ex.next.array(0).equals(b)
        if (next.isNull) {
          ret = ValueFactory.createNullValue[Value]
        } else if (next.getAsBoolean) {
          ret = ValueFactory.TRUE
          break = true
        }
      }
    }
    ret
  }
  override def doPreValidate = o.validate
  override def doValidate = {
    command.prepare(dsf)
    
    if (command.getMetadata.getFieldCount > 1) {
      throw new SemanticException("There can only be one selected field in an IN subquery.")
    }
    
    findOuterFieldEvals(command)
  }
  
  private var evals: List[OuterFieldEvaluator]= Nil
  
  private def findOuterFieldEvals(c: Command) { 
    c match {
      case e: ExpressionCommand => evals = e.outerFieldEval ::: evals
      case _ =>
    }
    c.children foreach (findOuterFieldEvals)
  }
  
  def doCopy = InEvaluator(e, o)
}

object in {
  def unapply(e: Expression) = {
    e.evaluator match {
      case InEvaluator(ex, o) => Some((ex, o))
      case _ => None
    }
  }
}