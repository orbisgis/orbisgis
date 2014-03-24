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
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
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

import org.gdms.data.schema.{DefaultMetadata, Metadata}
import org.gdms.data.values.Value
import org.gdms.sql.engine.SemanticException
import org.gdms.sql.engine.operations.Operation
import org.gdms.sql.engine.commands.Row
import org.gdms.sql.engine.GdmSQLPredef._

/**
 * This class reprensents any abstract SQL scalar expression.
 *
 * The value of the expression is based on an <tt>Evaluator</tt> object that
 * provides at runtime a row value.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
sealed class Expression(var evaluator: Evaluator) extends Iterable[Expression] with Serializable {

  /**
   * Evaluates this expression
   *
   * @param s the current input row in this scalar expression
   */
  def evaluate(s: Row): Value = evaluator.eval(s)

  def and(e: Expression) = new Expression(AndEvaluator(this,e))

  def &(e: Expression) = and(e)

  def or(e: Expression) = new Expression(OrEvaluator(this,e))

  def |(e: Expression) = or(e)

  def not() = new Expression(NotEvaluator(this))

  def unary_!() = not()
  
  def isStatic: Boolean = evaluator.isInstanceOf[StaticEvaluator]

  def opposite() = new Expression(OppositeEvaluator(this))

  def  unary_-() = opposite()

  def plus(e: Expression) = new Expression(AddEvaluator(this, e))

  def +(e: Expression) = plus(e)

  def minus(e: Expression) = plus(e.opposite)

  def -(e :Expression) = minus(e)

  def times(e: Expression) = new Expression(MultiplyEvaluator(this,e))

  def *(e: Expression) = times(e)

  def inverse() = new Expression(InverseEvaluator(this))

  def divide(e: Expression) = new Expression(DivideEvaluator(this,e))
  
  def /(e: Expression) = divide(e)

  def sqlEquals(e: Expression) = new Expression(EqualsEvaluator(this,e))

  def lessThan(e: Expression) = new Expression(LessThanEvaluator(this,e))

  def <(e: Expression) = lessThan(e)
  
  def lessEqualsThan(e: Expression) = new Expression(LessEqualThanEvaluator(this,e))

  def <=(e: Expression) = lessEqualsThan(e)

  def greaterThan(e: Expression) = new Expression(GreaterThanEvaluator(this,e))

  def >(e: Expression) = greaterThan(e)

  def greaterEqualsThan(e: Expression) = new Expression(GreaterEqualThanEvaluator(this,e))

  def >=(e: Expression) = greaterEqualsThan(e)

  def concatWith(e: Expression) = new Expression(StringConcatEvaluator(this,e))
  
  def ||(e: Expression) = concatWith(e)

  def like(e: Expression, caseInsensitive: Boolean = false) = {
    new Expression(LikeEvaluator(this,e, caseInsensitive))
  }
  
  def ~(e: Expression, caseInsensitive: Boolean = false) = {
    new Expression(POSIXEvaluator(this, e, caseInsensitive))
  }
  
  def similarTo(e: Expression) = new Expression(SimilarToEvaluator(this,e))

  def modulo(e: Expression) = new Expression(ModuloEvaluator(this,e))

  def %(e: Expression) = modulo(e)

  def exponent(e: Expression) = new Expression(ExponentEvaluator(this,e))

  def ^(e: Expression) = exponent(e)

  def isNull = new Expression(IsNullEvaluator(this))

  def in(e: Seq[Expression]) = new Expression(InListEvaluator(this,e))
  
  def in(o: Operation) = new Expression(InEvaluator(this, o))
  
  def toType(t: Int) = new Expression(CastEvaluator(this, t))
  
  def ->(t: Int) = toType(t)

  def iterator = evaluator.childExpressions.iterator

  def validate(): Unit = evaluator.validate()
  
  def preValidate(): Unit = evaluator.preValidate()
  
  def cleanUp() = {
    evaluator.cleanUp()
    evaluator match {
      case OuterFieldEvaluator(n, t) => evaluator = FieldEvaluator(n ,t)
      case _ =>
    }
  }

  override def toString = "[" + evaluator.toString + "]"
  
  def prepared(r: Row) = new Expression(PreparedEvaluator(r, this))
  
  def duplicate = new Expression(evaluator.duplicate)
  
  def children = evaluator.childExpressions
  
  def allChildren: List[Expression] = {
    children.toList flatMap(e => e :: e.allChildren)
  }
}

object Expression {
  /**
   * Builds a static Expression that always hold the value <tt>v</tt>.
   *
   * @param v a constant value
   */
  def apply(v: Value) = {
    new Expression(StaticEvaluator(v)) {
    }
  }

  /**
   * Builds an expression that evaluates a function, with a given list
   * of parameters
   *
   * @param f a GDMS function
   * @param a list of expression as parameters
   */
  def apply(name: String, l: List[Expression]) = new Expression(FunctionEvaluator(name, l))

  /**
   * Builds an expression backed by the given operator
   *
   * @param e an operator
   */
  def apply(e: Evaluator) = new Expression(e)

  /**
   * Utility function to get the resulting metadata from a array of expressions
   *
   * @param a an array of expressions
   */
  def metadataFor(a: Seq[(Expression, Option[String])]): Metadata = {
    val m = new DefaultMetadata()
    a.zipWithIndex foreach { e => {
        val name = e._1._2.getOrElse(
          e._1._1.evaluator match {
            case ag: AggregateEvaluator => ag.f.getName
            case fi: FieldEvaluator => {
                fi.table match {
                  case None => fi.name
                  case Some(t) => fi.name + '$' + t
                }
              }
            case _ => "exp" + e._2
          }
        )
        if (m.getFieldIndex(name) != -1) {
          throw new SemanticException("There already is a field or alias '" + name + "'.")
        }
        m.addField(name , e._1._1.evaluator.sqlType)} }
    m
  }
}

/**
 * Utility object for creating fields.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
object Field {
  /**
   * Builds a special expression evaluated to a field with the name <tt>name</tt>
   *
   * @param a name of an accessible field in the fiven context
   */
  def apply(name: String) = {
    if (name == "oid") {
      new Expression(OidEvaluator)
    } else {
      new Expression(FieldEvaluator(name))
    }
  }

  def apply(name: String, table: String) = {
    new Expression(FieldEvaluator(name, Some(table)))
  }
  
  def star(except: Seq[Either[String, String]], table: Option[String]) = {
    new Expression(StarFieldEvaluator(except, table))
  }
}