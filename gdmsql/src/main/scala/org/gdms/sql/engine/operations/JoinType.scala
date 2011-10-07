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

package org.gdms.sql.engine.operations

import org.gdms.sql.evaluator.Expression

/**
 * Represents a type of join.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
abstract class JoinType

/**
 * Represents an outer left join on some expression.
 * 
 * @param cond the join condition, or None for a Natural join
 * @author Antoine Gourlay
 * @since 0.1
 */
case class OuterLeft(cond: Option[Expression]) extends JoinType {
  override def toString = "OuterLeft on(" + cond + ")"
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
}

/**
 * Represents an inner join on some expression.
 * 
 * @param cond the join condition
 * @author Antoine Gourlay
 * @since 0.1
 */
case class Inner(cond: Expression, var spatial: Boolean = false) extends JoinType {
  override def toString = "Inner on(" + cond + ") spatial=" + spatial
}

/**
 * Represents a cross join.
 * 
 * @author Antoine Gourlay
 * @since 0.1
 */
case class Cross() extends JoinType {
  override def toString = "Cross"
}

/**
 * Represents an inner natural join.
 * 
 * @author Antoine Gourlay
 * @since 0.1
 */
case class Natural() extends JoinType {
  override def toString = "Natural"
}