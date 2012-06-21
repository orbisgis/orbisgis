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

import org.gdms.data.types.IncompatibleTypesException
import org.gdms.data.types.Type
import org.gdms.data.types.TypeFactory
import org.gdms.data.values.Value
import org.gdms.data.values.ValueFactory
import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.sql.engine.operations.Operation

/**
 * Base evaluator for boolean comparisons.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
abstract sealed class BooleanEvaluator extends Evaluator {
  val sqlType = Type.BOOLEAN
  override def doValidate = {
    childExpressions foreach { _.evaluator.sqlType match {
        case Type.BOOLEAN =>
        case _ => throw new IncompatibleTypesException
      }}
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
  override val childExpressions = List(e1, e2)
  override def toString = "(" + e1 + " AND " + e2 + ")"
  def duplicate: AndEvaluator = AndEvaluator(e1.duplicate, e2.duplicate)
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
  override val childExpressions = List(e1, e2)
  override def toString = "(" + e1 + " OR " + e2 + ")"
  def duplicate: OrEvaluator = OrEvaluator(e1.duplicate, e2.duplicate)
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
  override val childExpressions = List(e1)
  override def toString = "NOT (" + e1 + ")"
  def duplicate: NotEvaluator = NotEvaluator(e1.duplicate)
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
  override val childExpressions = List(e1, e2)
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
  def duplicate: EqualsEvaluator = EqualsEvaluator(e1.duplicate, e2.duplicate)
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
  override val childExpressions = List(e1)
  override val doValidate = {}
  override def toString = "ISNULL (" + e1 + ")"
  def duplicate: IsNullEvaluator = IsNullEvaluator(e1.duplicate)
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
  override def toString = "(" + e1 + " IN (" + e2 + "))"
  def duplicate: InListEvaluator = InListEvaluator(e1.duplicate, e2 map (_.duplicate))
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
case class ExistsEvaluator(var op: Operation) extends BooleanEvaluator with QueryEvaluator {
  
  override val childExpressions = Nil
  
  def eval = s => ValueFactory.createValue(!evalInner(s).isEmpty)
  
  def duplicate: ExistsEvaluator = {
    val c = ExistsEvaluator(op.duplicate)
    c.dsf = dsf
    c
  }
}

object exists {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: ExistsEvaluator => Some(a.op)
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
 case class InEvaluator(e: Expression, var op: Operation) extends BooleanEvaluator with QueryEvaluator {
  
    override val childExpressions = List(e)
  
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
    
  def duplicate: InEvaluator = {
      val c = InEvaluator(e.duplicate, op.duplicate)
      c.dsf = dsf
      c
    }
  }

 object in {
    def unapply(e: Expression) = {
      e.evaluator match {
        case i : InEvaluator => Some((i.e, i.op))
        case _ => None
      }
    }
  }