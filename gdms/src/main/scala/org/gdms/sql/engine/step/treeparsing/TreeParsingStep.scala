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
package org.gdms.sql.engine.step.treeparsing

import java.util.Properties
import org.antlr.runtime.tree.CommonTree
import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.sql.engine.AbstractEngineStep
import org.gdms.sql.engine.operations.Operation

/**
 * Step 2: The AST Parsing.
 * 
 * This step parses the AST into a sequence of:
 *  - an operation describing the command
 *  - the SQL string corresponding to the command.
 *  
 * @author Antoine Gourlay
 * @since 0.3
 */
case object TreeParsingStep extends AbstractEngineStep[(CommonTree, String), Seq[(Operation, String)]]("AST Parsing") {
  
  // regexp for splitting the SQL String
  private val spl = """;""".r
  
  def doOperation(ts: (CommonTree, String))(implicit p: Properties) = {
    val tree = ts._1
    if (isPropertyTurnedOn(Flags.EXPLAIN)) {
      LOG.info("Parsing tree: " + tree.toStringTree)
    }
    
    // splits the SQL String
    val sts = spl.split(ts._2) map (_.trim)
    
    // all commands
    val a = (0 until tree.getChildCount) map (tree.getChild);
    
    // builds the final sequence of tuples
    val b = (if (sts.length != tree.getChildCount) {
      LOG.warn("SQL instruction and tree count do not match!")
      // we discard sql strings but never discard operations
      a.zipAll(sts, null, "").filterNot(_._1 == null)
    } else {
      a.zip(sts)
    }) map (t => (LogicPlanBuilder.buildOperationTree(t._2, t._1), t._2))
    
    if (isPropertyTurnedOn(Flags.EXPLAIN)) {
      LOG.info("Parsed logical execution tree.")
      b foreach (LOG.debug)
    }
    
    b
  }
}