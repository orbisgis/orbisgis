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

import org.gdms.data.types.IncompatibleTypesException
import org.gdms.data.types.Type
import org.gdms.data.types.TypeFactory

/**
 * Base evaluator for all numeric-returning operations.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
abstract class NumericEvaluator(e1: Expression, e2: Expression) extends Evaluator {
  def sqlType = TypeFactory.getBroaderType(e1.evaluator.sqlType, e2.evaluator.sqlType)
  override val childExpressions = List(e1,e2)
  override def doValidate = {
    childExpressions map { e =>
      TypeFactory.isNumerical(e.evaluator.sqlType) } reduceLeft(_ && _) match {
      case false => throw new IncompatibleTypesException
      case true =>
    }
  }
}

/**
 * Evaluator for value1 + value2.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class AddEvaluator(val e1: Expression,val e2: Expression) extends NumericEvaluator(e1, e2) {
  def eval = s => e1.evaluate(s) sum e2.evaluate(s)
  override def toString = "(" + e1 + " + " + e2 + ")"
  def doCopy = copy()
}

object + {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: AddEvaluator => Some((a.e1, a.e2))
      case _ => None
    }
  }
}

/**
 * Evaluator for -value1.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class OppositeEvaluator(e1: Expression) extends Evaluator {
  def sqlType = e1.evaluator.sqlType
  def eval = s => e1.evaluate(s) opposite
  override val childExpressions = e1 :: List.empty
  override def toString = "-(" + e1 + ")"
  def doCopy = copy()
}

object - {
  def unapply(e: Expression) = {
    e match {
      case a + (b: OppositeEvaluator) => Some(a, b.e1)
      case _ => None
    }
  }
}

/**
 * Evaluator for value1 * value2.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class MultiplyEvaluator(val e1: Expression,val e2: Expression) extends NumericEvaluator(e1, e2) {
  def eval = s => e1.evaluate(s) multiply e2.evaluate(s)
  override def toString = "(" + e1 + " * " + e2 + ")"
  def doCopy = copy()
}

object x {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: MultiplyEvaluator => Some((a.e1, a.e2))
      case _ => None
    }
  }
}

/**
 * Evaluator for 1/value1.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class InverseEvaluator(e1: Expression) extends Evaluator {
  val sqlType = Type.DOUBLE
  def eval = s => e1.evaluate(s) inverse
  override val childExpressions = e1 :: List.empty
  override def toString = "Inverse(" + e1 + ")"
  def doCopy = copy()
}

object inv {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: InverseEvaluator => Some(a.e1)
      case _ => None
    }
  }
}

/**
 * Evaluator for value1 / value2.
 */
case class DivideEvaluator(e1: Expression, e2: Expression) extends NumericEvaluator(e1, e2) {
  def eval = s => e1.evaluate(s) multiply (e2.evaluate(s) inverse)
  override val childExpressions = e1 :: e2 :: Nil
  override def toString = "(" + e1 + " / " + e2 + ")"
  override def sqlType = {
    val t = super.sqlType
    if (t == Type.FLOAT) Type.DOUBLE else t
  }
  def doCopy = copy()
}

object / {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: DivideEvaluator => Some((a.e1, a.e2))
      case _ => None
    }
  }
}

/**
 * Evaluator for value1 < value2.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class LessThanEvaluator(val e1: Expression,val e2: Expression) extends NumericEvaluator(e1, e2) {
  override val sqlType = Type.BOOLEAN
  def eval = s => e1.evaluate(s) less e2.evaluate(s)
  override val childExpressions = List(e1,e2)
  override def toString = "(" + e1 + " < " + e2 + ")"
  def doCopy = copy()
}

object < {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: LessThanEvaluator => Some((a.e1, a.e2))
      case _ => None
    }
  }
}

/**
 * Evaluator for value1 <= value2.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class LessEqualThanEvaluator(val e1: Expression,val e2: Expression) extends NumericEvaluator(e1, e2) {
  override val sqlType = Type.BOOLEAN
  def eval = s => e1.evaluate(s) lessEqual e2.evaluate(s)
  override val childExpressions = List(e1,e2)
  override def toString = "(" + e1 + " <= " + e2 + ")"
  def doCopy = copy()
}

object <= {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: LessEqualThanEvaluator => Some((a.e1, a.e2))
      case _ => None
    }
  }
}

/**
 * Evaluator for value1 > value2.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class GreaterThanEvaluator(val e1: Expression,val e2: Expression) extends NumericEvaluator(e1, e2) {
  override val sqlType = Type.BOOLEAN
  def eval = s => e1.evaluate(s) greater e2.evaluate(s)
  override val childExpressions = List(e1,e2)
  override def toString = "(" + e1 + " > " + e2 + ")"
  def doCopy = copy()
}

object > {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: GreaterThanEvaluator => Some((a.e1, a.e2))
      case _ => None
    }
  }
}

/**
 * Evaluator for value1 >= value2.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class GreaterEqualThanEvaluator(val e1: Expression,val e2: Expression) extends NumericEvaluator(e1, e2) {
  override val sqlType = Type.BOOLEAN
  def eval = s => e1.evaluate(s) greaterEqual e2.evaluate(s)
  override val childExpressions = List(e1,e2)
  override def toString = "(" + e1 + " >= " + e2 + ")"
  def doCopy = copy()
}

object >= {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: GreaterEqualThanEvaluator => Some((a.e1, a.e2))
      case _ => None
    }
  }
}

/**
 * Evaluator for value1 % value2.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class ModuloEvaluator(val e1: Expression,val e2: Expression) extends NumericEvaluator(e1, e2) {
  def eval = s => e1.evaluate(s) remainder e2.evaluate(s)
  override def toString = "(" + e1 + " % " + e2 + ")"
  def doCopy = copy()
}

object % {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: ModuloEvaluator => Some((a.e1, a.e2))
      case _ => None
    }
  }
}

/**
 * Evaluator for value1 ^ value2.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class ExponentEvaluator(val e1: Expression,val e2: Expression) extends NumericEvaluator(e1, e2) {
  def eval = s => e1.evaluate(s) pow e2.evaluate(s)
  override def toString = "(" + e1 + " ^ " + e2 + ")"
  def doCopy = copy()
}

object ^ {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: ExponentEvaluator => Some((a.e1, a.e2))
      case _ => None
    }
  }
}