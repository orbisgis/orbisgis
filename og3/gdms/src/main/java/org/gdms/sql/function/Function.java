/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.sql.function;

import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;

/**
 * Interface to be implemented to create a function. The name will be the string
 * used in the SQL to refeer the function. A function will be created once for
 * each instruction execution.
 */
public interface Function {
	/**
	 * Evaluates the function. FunctionValidator contains several static methods
	 * that can help in the validation of the input parameters
	 *
	 * @param args
	 *            list of arguments
	 *
	 * @return the result value
	 *
	 * @throws FunctionException
	 *             If some error happens and the execution of the query should
	 *             be stopped
	 */
	public Value evaluate(Value... args) throws FunctionException;

	/**
	 * Gets the name of the function. This name will be used in SQL statements
	 *
	 * @return
	 */
	public String getName();

	/**
	 * @return true if the function is an aggregate function: count, avg, ... in
	 *         standard SQL
	 */
	public boolean isAggregate();

	/**
	 * Method called to obtain the result of an aggregate function. If this
	 * method returns null the last return value of the
	 * {@link #evaluate(Value[])} method is used as aggregated result. This
	 * method is not called at all if this is not an aggregated function (
	 * {@link #isAggregate()} == true)
	 *
	 * @return
	 */
	public Value getAggregateResult();

	/**
	 * Gets the type of the result this function provides.
	 *
	 * @param argsTypes
	 * @return The type of the function
	 */
	public Type getType(Type[] argsTypes) throws InvalidTypeException;

	/**
	 * Usage description.
	 *
	 * @return
	 */

	public String getDescription();

	/**
	 * Example of use.
	 *
	 * @return
	 */
	public String getSqlOrder();

	/**
	 * Gets all the possible combinations of arguments this function can take
	 */
	public Arguments[] getFunctionArguments();
}
