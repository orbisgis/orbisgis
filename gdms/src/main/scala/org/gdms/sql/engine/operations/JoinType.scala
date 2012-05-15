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
package org.gdms.sql.engine.operations

import org.gdms.sql.evaluator.Expression

/**
 * Represents a type of join.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
sealed abstract class JoinType {
  def duplicate: JoinType
}

/**
 * Represents an outer left join on some expression.
 * 
 * @param cond the join condition, or None for a Natural join
 * @author Antoine Gourlay
 * @since 0.1
 */
case class OuterLeft(cond: Option[Expression]) extends JoinType {
  override def toString = "OuterLeft on(" + cond + ")"
  def duplicate: OuterLeft = OuterLeft(cond map (_.duplicate))
}

/**
 * Represents an outer full join on some expression.
 * 
 * @param cond the join condition, or None for a Natural join
 * @author Antoine Gourlay
 * @since 0.1
 */
case class OuterFull(cond: Option[Expression]) extends JoinType {
  override def toString = "OuterFull on(" + cond + ")"
  def duplicate: OuterFull = OuterFull(cond map (_.duplicate))
}

/**
 * Represents an inner join on some expression.
 * 
 * @param cond the join condition
 * @author Antoine Gourlay
 * @since 0.1
 */
case class Inner(var cond: Expression, var spatial: Boolean = false, var withIndexOn: Option[(String, Expression, Boolean)] = None) extends JoinType {
  override def toString = "Inner on(" + cond + ") spatial=" + spatial + " withIndexOn=" + withIndexOn
  def duplicate: Inner = Inner(cond.duplicate, spatial, withIndexOn map (a => (a._1, a._2.duplicate, a._3)))
}

/**
 * Represents a cross join.
 * 
 * @author Antoine Gourlay
 * @since 0.1
 */
case object Cross extends JoinType {
  override def toString = "Cross"
  def duplicate = this
}

/**
 * Represents an inner natural join.
 * 
 * @author Antoine Gourlay
 * @since 0.1
 */
case object Natural extends JoinType {
  override def toString = "Natural"
  def duplicate = this
}
