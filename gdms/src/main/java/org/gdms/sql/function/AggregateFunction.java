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
package org.gdms.sql.function;

import org.gdms.data.DataSourceFactory;
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
	int getType(int[] argsTypes);
}
