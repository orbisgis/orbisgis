/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.gdms.sql.function;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;

/**
 * Interface to be implemented to create a function. The name will be the string
 * used in the SQL to refeer the function. A function will be created once for
 * each instruction execution.
 */
public interface ScalarFunction extends Function {
	/**
	 * Evaluates the function. FunctionValidator contains several static methods
	 * that can help in the validation of the input parameters
	 *
         * @param dsf
         * @param args
	 *            list of arguments
	 *
	 * @return the result value
	 *
	 * @throws FunctionException
	 *             If some error happens and the execution of the query should
	 *             be stopped
	 */
	Value evaluate(DataSourceFactory dsf, Value... args) throws FunctionException;

	

	/**
	 * Gets the type of the result this function provides for a specific
         * set of arguments.
	 *
	 * @param argsTypes
	 * @return The type of the function
         * @throws InvalidTypeException if there is no FunctionSignature matching
         * the given Type[]
	 */
	Type getType(Type[] argsTypes);
}
