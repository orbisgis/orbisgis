/*
 * The GDMS library (Generic Datasources Management System)
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

object Evaluators {
  object cons {
    def unapply(e: Expression) = {
      e.evaluator match {
        case a: StaticEvaluator => Some(a.v)
        case _ => None
      }
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
  
  object agg {
    def unapply(e: Expression) = {
      e.evaluator match {
        case a: AggregateEvaluator => Some((a.f, a.l))
        case _ => None
      }
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
  
  object outerField {
    def unapply(e: Expression) = {
      e.evaluator match {
        case OuterFieldEvaluator(n, t) => Some((n, t))
        case _ => None
      }
    }
  }
  
  object star {
    def unapply(e: Expression) = {
      e.evaluator match {
        case a: StarFieldEvaluator => Some((a.except, a.table))
        case _ => None
      }
    }
  }
  
  object oid {
    def unapply(e: Expression) = {
      e.evaluator match {
        case OidEvaluator => true
        case _ => false
      }
    }
  }
  
  object castTo {
    def unapply(e: Expression) = {
      e.evaluator match {
        case a: CastEvaluator => Some((a.e, a.sqlType))
        case _ => None
      }
    }
  }
  
  object param {
    def unapply(e: Expression) = {
      e.evaluator match {
        case ParamEvaluator(n) => Some(n)
        case _ => None
      }
    }
  }
  
  object & {
    def unapply(e: Expression) = {
      e.evaluator match {
        case a: AndEvaluator => Some((a.e1, a.e2))
        case _ => None
      }
    }
  }

  object | {
    def unapply(e: Expression) = {
      e.evaluator match {
        case a: OrEvaluator => Some((a.e1, a.e2))
        case _ => None
      }
    }
  }

  object ! {
    def unapply(e: Expression) = {
      e.evaluator match {
        case a: NotEvaluator => Some(a.e1)
        case _ => None
      }
    }
  }

  object === {
    def unapply(e: Expression) = {
      e.evaluator match {
        case a: EqualsEvaluator => Some((a.e1, a.e2))
        case _ => None
      }
    }
  }

  object isNull {
    def unapply(e: Expression) = {
      e.evaluator match {
        case a: IsNullEvaluator => Some(a.e1)
        case _ => None
      }
    }
  }

  object inList {
    def unapply(e: Expression) = {
      e.evaluator match {
        case a: InListEvaluator => Some((a.e1, a.e2))
        case _ => None
      }
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

  object in {
    def unapply(e: Expression) = {
      e.evaluator match {
        case i : InEvaluator => Some((i.e, i.op))
        case _ => None
      }
    }
  }

  object + {
    def unapply(e: Expression) = {
      e.evaluator match {
        case a: AddEvaluator => Some((a.e1, a.e2))
        case _ => None
      }
    }
  }

  object - {
    def unapply(e: Expression) = {
      e match {
        case a + (b: OppositeEvaluator) => Some((Some(a), b.e1))
        case b: OppositeEvaluator => Some((None, b.e1))
        case _ => None
      }
    }
  }

  object x {
    def unapply(e: Expression) = {
      e.evaluator match {
        case a: MultiplyEvaluator => Some((a.e1, a.e2))
        case _ => None
      }
    }
  }

  object inv {
    def unapply(e: Expression) = {
      e.evaluator match {
        case a: InverseEvaluator => Some(a.e1)
        case _ => None
      }
    }
  }

  object / {
    def unapply(e: Expression) = {
      e.evaluator match {
        case a: DivideEvaluator => Some((a.e1, a.e2))
        case _ => None
      }
    }
  }

  object < {
    def unapply(e: Expression) = {
      e.evaluator match {
        case a: LessThanEvaluator => Some((a.e1, a.e2))
        case _ => None
      }
    }
  }

  object <= {
    def unapply(e: Expression) = {
      e.evaluator match {
        case a: LessEqualThanEvaluator => Some((a.e1, a.e2))
        case _ => None
      }
    }
  }

  object > {
    def unapply(e: Expression) = {
      e.evaluator match {
        case a: GreaterThanEvaluator => Some((a.e1, a.e2))
        case _ => None
      }
    }
  }

  object >= {
    def unapply(e: Expression) = {
      e.evaluator match {
        case a: GreaterEqualThanEvaluator => Some((a.e1, a.e2))
        case _ => None
      }
    }
  }

  object % {
    def unapply(e: Expression) = {
      e.evaluator match {
        case a: ModuloEvaluator => Some((a.e1, a.e2))
        case _ => None
      }
    }
  }

  object ^ {
    def unapply(e: Expression) = {
      e.evaluator match {
        case a: ExponentEvaluator => Some((a.e1, a.e2))
        case _ => None
      }
    }
  }

  object || {
    def unapply(e: Expression) = {
      e.evaluator match {
        case a: StringConcatEvaluator => Some((a.e1, a.e2))
        case _ => None
      }
    }
  }

  object like {
    def unapply(e: Expression) = {
      e.evaluator match {
        case a: LikeEvaluator => Some((a.e1, a.e2, a.caseInsensitive))
        case _ => None
      }
    }
  }

  object similarTo {
    def unapply(e: Expression) = {
      e.evaluator match {
        case a: SimilarToEvaluator => Some((a.e1, a.e2))
        case _ => None
      }
    }
  }

  object matches {
    def unapply(e: Expression) = {
      e.evaluator match {
        case a: POSIXEvaluator => Some((a.e1, a.e2, a.caseInsensitive))
        case _ => None
      }
    }
  }
}
