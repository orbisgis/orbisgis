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

import java.util.regex.Pattern
import org.gdms.data.types.IncompatibleTypesException
import org.gdms.data.types.Type
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
  override val childExpressions = e1 :: e2 :: List.empty
  override def doValidate = {
    childExpressions map
    {_.evaluator.sqlType == Type.STRING} reduceLeft (_ || _) match {
      case true =>
      case false => throw new IncompatibleTypesException
    }
  }
  override def toString = "(" + e1 + " || " + e2 + ")"
  def doCopy = copy()
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
        if (caseInsensitive) {
          pattern = TextUtils.buildLikePattern(e2.evaluate(null).getAsString, true)
        } else {
          pattern = TextUtils.buildLikePattern(e2.evaluate(null).getAsString)
        }
      }
      loaded = true
    }
    
    if (pattern == null) {
      e1.evaluate(s).like(e2.evaluate(s), caseInsensitive)
    } else {
      e1.evaluate(s) matches pattern
    }
  } 
  override val childExpressions = e1 :: e2 :: List.empty
  override def doValidate = {
    childExpressions map
    {_.evaluator.sqlType == Type.STRING} reduceLeft (_ && _) match {
      case true =>
      case false => throw new IncompatibleTypesException
    }
  }
  override def toString = "(" + e1 + " LIKE " + e2 + ")"
  def doCopy = copy()
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
        pattern = TextUtils.buildSimilarToPattern(e2.evaluate(null).getAsString)
      }
      loaded = true
    }
    
    if (pattern == null) {
      e1.evaluate(s) similarTo e2.evaluate(s)
    } else {
      e1.evaluate(s) matches pattern
    }
  } 
  override val childExpressions = e1 :: e2 :: List.empty
  override def doValidate = {
    childExpressions map
    {_.evaluator.sqlType == Type.STRING} reduceLeft (_ && _) match {
      case true =>
      case false => throw new IncompatibleTypesException
    }
  }
  override def toString = "(" + e1 + " LIKE " + e2 + ")"
  def doCopy = copy()
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
          pattern = Pattern.compile(e2.evaluate(null).getAsString, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)
        } else {
          pattern = Pattern.compile(e2.evaluate(null).getAsString)
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
  override val childExpressions = e1 :: e2 :: List.empty
  override def doValidate = {
    childExpressions map
    {_.evaluator.sqlType == Type.STRING} reduceLeft (_ && _) match {
      case true =>
      case false => throw new IncompatibleTypesException
    }
  }
  override def toString = "(" + e1 + " LIKE " + e2 + ")"
  def doCopy = copy()
}

object matches {
  def unapply(e: Expression) = {
    e.evaluator match {
      case a: POSIXEvaluator => Some((a.e1, a.e2, a.caseInsensitive))
      case _ => None
    }
  }
}