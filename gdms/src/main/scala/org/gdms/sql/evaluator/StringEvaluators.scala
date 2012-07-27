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

import java.util.regex.Pattern
import org.gdms.data.types.IncompatibleTypesException
import org.gdms.data.types.Type
import org.gdms.sql.engine.GdmSQLPredef._
import org.orbisgis.utils.TextUtils

/**
 * Evaluator for concat operations of any values.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class StringConcatEvaluator(e1: Expression, e2: Expression) extends Evaluator { 
  val sqlType = Type.STRING
  def eval = s => e1.evaluate(s) concatWith e2.evaluate(s)
  override val childExpressions = List(e1, e2)
  override def doValidate() = {
    childExpressions map
    {_.evaluator.sqlType == Type.STRING} reduceLeft (_ || _) match {
      case true =>
      case false => throw new IncompatibleTypesException
    }
  }
  override def toString = "(" + e1 + " || " + e2 + ")"
  def duplicate: StringConcatEvaluator = StringConcatEvaluator(e1.duplicate, e2.duplicate)
}

object || {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: StringConcatEvaluator => Some((a.e1, a.e2))
      case _ => None
    }
  }
}

/**
 * Evaluator for like operation on string values.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class LikeEvaluator(e1: Expression, e2: Expression, caseInsensitive: Boolean) extends Evaluator {
  val sqlType = Type.BOOLEAN
  private var pattern: Pattern = null
  private var loaded = false
  def eval = { s => 
    if (!loaded){
      if (e2.evaluator.isInstanceOf[StaticEvaluator]) {
        pattern = TextUtils.buildLikePattern(e2.evaluate(emptyRow).getAsString, caseInsensitive)
      }
      loaded = true
    }
    
    if (pattern == null) {
      e1.evaluate(s).like(e2.evaluate(s), caseInsensitive)
    } else {
      e1.evaluate(s) matches pattern
    }
  } 
  override val childExpressions = List(e1, e2)
  override def doValidate() = {
    childExpressions map
    {_.evaluator.sqlType == Type.STRING} reduceLeft (_ && _) match {
      case true =>
      case false => throw new IncompatibleTypesException
    }
  }
  override def doCleanUp() = {
    pattern = null
    loaded = false
  }
  override def toString = "(" + e1 + " LIKE " + e2 + ")"
  def duplicate: LikeEvaluator = LikeEvaluator(e1.duplicate, e2.duplicate, caseInsensitive)
}

object like {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: LikeEvaluator => Some((a.e1, a.e2, a.caseInsensitive))
      case _ => None
    }
  }
}


/**
 * Evaluator for similar to operation on string values.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class SimilarToEvaluator(e1: Expression, e2: Expression) extends Evaluator {
  val sqlType = Type.BOOLEAN
  private var pattern: Pattern = null
  private var loaded = false
  def eval = { s =>  
    if (!loaded){
      if (e2.evaluator.isInstanceOf[StaticEvaluator]) {
        pattern = TextUtils.buildSimilarToPattern(e2.evaluate(emptyRow).getAsString)
      }
      loaded = true
    }
    
    if (pattern == null) {
      e1.evaluate(s) similarTo e2.evaluate(s)
    } else {
      e1.evaluate(s) matches pattern
    }
  } 
  override val childExpressions = List(e1, e2)
  override def doValidate() = {
    childExpressions map
    {_.evaluator.sqlType == Type.STRING} reduceLeft (_ && _) match {
      case true =>
      case false => throw new IncompatibleTypesException
    }
  }
  override def doCleanUp() = {
    pattern = null
    loaded = false
  }
  override def toString = "(" + e1 + " LIKE " + e2 + ")"
  def duplicate: SimilarToEvaluator = SimilarToEvaluator(e1.duplicate, e2.duplicate)
}

object similarTo {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: SimilarToEvaluator => Some((a.e1, a.e2))
      case _ => None
    }
  }
}

/**
 * Evaluator for POSIX Regexp operation on string values.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class POSIXEvaluator(e1: Expression, e2: Expression, caseInsensitive: Boolean) extends Evaluator {
  val sqlType = Type.BOOLEAN
  private var pattern: Pattern = null
  private var loaded = false
  def eval = { s =>  
    if (!loaded){
      if (e2.evaluator.isInstanceOf[StaticEvaluator]) {
        if (caseInsensitive) {
          pattern = Pattern.compile(e2.evaluate(emptyRow).getAsString, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)
        } else {
          pattern = Pattern.compile(e2.evaluate(emptyRow).getAsString)
        }
      }
      loaded = true
    }
    
    if (pattern == null) {
      e1.evaluate(s) matches e2.evaluate(s)
    } else {
      e1.evaluate(s) matches pattern
    }
  } 
  override val childExpressions = List(e1, e2)
  override def doValidate() = {
    childExpressions map
    {_.evaluator.sqlType == Type.STRING} reduceLeft (_ && _) match {
      case true =>
      case false => throw new IncompatibleTypesException
    }
  }
  override def doCleanUp() = {
    pattern = null
    loaded = false
  }
  override def toString = "(" + e1 + " LIKE " + e2 + ")"
  def duplicate: POSIXEvaluator = POSIXEvaluator(e1.duplicate, e2.duplicate, caseInsensitive)
}

object matches {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: POSIXEvaluator => Some((a.e1, a.e2, a.caseInsensitive))
      case _ => None
    }
  }
}