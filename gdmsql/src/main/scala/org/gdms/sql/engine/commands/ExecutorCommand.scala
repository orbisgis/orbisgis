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

package org.gdms.sql.engine.commands

import org.gdms.data.types.TypeFactory
import org.gdms.sql.evaluator.Expression
import org.gdms.sql.function.FunctionException
import org.gdms.sql.function.FunctionManager
import org.gdms.sql.function.FunctionValidator
import org.gdms.sql.function.executor.ExecutorFunction
import org.orbisgis.progress.NullProgressMonitor
import scalaz.concurrent.Promise

/**
 * Command dedicated to running {@link ExecutorFunction} functions
 * @author Antoine Gourlay
 * @since 0.1
 */
class ExecutorCommand(name: String, params: List[Expression]) extends Command with OutputCommand with ExpressionCommand {
  val exp = params

  private var function: ExecutorFunction = null

  override def doPrepare = {
    // check is the function exists and is of correct type
    val f = FunctionManager.getFunction(name)
    if (f == null) {
      throw new FunctionException("The function does not exist.")
    } else if (!f.isExecutor) {
      throw new FunctionException("This function cannot be called with CALL or EXECUTE.")
    }
    function = f.asInstanceOf[ExecutorFunction]

    // validates params
    FunctionValidator.failIfTypesDoNotMatchSignature(
      params.map { e => TypeFactory.createType(e.evaluator.sqlType) } toArray,
      function.getFunctionSignatures)
  }

  protected final def doWork(r: Iterable[Iterable[Promise[Iterable[Row]]]]) = {
    function.evaluate(dsf, Array.empty, params map (_.evaluate(null)) toArray, new NullProgressMonitor)

    null
  }

  val getResult = null

  // no result
  override val getMetadata = null
}
