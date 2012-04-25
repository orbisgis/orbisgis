/**
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
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
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
 **/
package org.gdms.sql.function;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;

/**
 * Base interface for aggregated functions
 * @author Antoine Gourlay
 */
public interface AggregateFunction extends Function {

        /**
         * Method called to obtain the result of an aggregate function. If this
         * method returns null the last return value of the
         * method is used as aggregated result.
         *
         * @return
         */
        Value getAggregateResult();

        /**
	 * Evaluates the function. FunctionValidator contains several static methods
	 * that can help in the validation of the input parameters
	 *
         * @param dsf 
         * @param args
	 *            list of arguments
	 *
	 * @throws FunctionException
	 *             If some error happens and the execution of the query should
	 *             be stopped
	 */
	void evaluate(DataSourceFactory dsf, Value... args) throws FunctionException;

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
