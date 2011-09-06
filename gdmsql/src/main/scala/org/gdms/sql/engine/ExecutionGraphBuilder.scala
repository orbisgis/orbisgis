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

package org.gdms.sql.engine

import org.gdms.sql.engine.logical.LogicPlanBuilder
import org.gdms.sql.engine.logical.LogicPlanOptimizer
import org.gdms.sql.engine.operations._
import org.gdms.sql.engine.parsing.GdmSQLParser._
import org.antlr.runtime.tree.CommonTree
import org.gdms.sql.engine.commands._

/**
 * Main entry point for the Scala SQL engine.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
object ExecutionGraphBuilder {

  /**
   * Buids an ExecutionGraph from an AST
   *
   * @param tree an AST
   * @return an abstract execution graph
   */
  def build(tree: CommonTree): Array[ExecutionGraph] = {
    // parsing
    val cs = parseStatement(tree)
    
    // validating types & stuff that do not need a dsf
    cs foreach (_.validate)
    
    // building ExecutionGraph objects
    cs map (new ExecutionGraph(_))
  }

  private def parseStatement(tree: CommonTree): Array[Operation] = {
    val a = (0 until tree.getChildCount) map (tree.getChild) map (LogicPlanBuilder.buildLogicPlan) toArray;
    a foreach(LogicPlanOptimizer.optimiseJoins)
    a
  }
  
}
