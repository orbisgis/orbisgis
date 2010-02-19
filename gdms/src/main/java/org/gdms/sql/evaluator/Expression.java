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
package org.gdms.sql.evaluator;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.strategies.IncompatibleTypesException;

public interface Expression {

	/**
	 * Evaluates this expression tree. If the tree contains field references and
	 * no DataSource was specified it throws NullPointerException
	 * 
	 * @return The result of the evaluation
	 * @throws EvaluationException
	 *             If there is some problem evaluating the expression
	 */
	Value evaluate() throws EvaluationException;

	/**
	 * Does nothing if the types of the expression are valid. If there is an
	 * operation with the wrong types at its input it raises an exception
	 * 
	 * @throws IncompatibleTypesException
	 * @throws DriverException
	 */
	void validateTypes() throws IncompatibleTypesException, DriverException;

	/**
	 * Gets the type of the expression. It is one of the constants in
	 * {@link Type}
	 * 
	 * @return
	 * @throws DriverException
	 */
	Type getType() throws DriverException;

	/**
	 * Gets an array of the field references that are in the tree. If there is
	 * no field reference it returns an empty array, never null
	 * 
	 * @return
	 */
	Field[] getFieldReferences();

	/**
	 * Gets an array with the names of the functions referenced in the tree
	 * 
	 * @return An array of functions. It may be empty
	 */
	FunctionOperator[] getFunctionReferences();

	/**
	 * Gets the child expression at the specified index
	 * 
	 * @param index
	 * @return
	 */
	public Expression getChild(int index);

	/**
	 * Gets the number of children this expression has
	 * 
	 * @return
	 */
	public int getChildCount();

	/**
	 * Gets all the children of this expression
	 * 
	 * @return
	 */
	public Expression[] getChildren();

	/**
	 * Returns a exact copy of the expression
	 * 
	 * @return
	 */
	public Expression cloneExpression();

	/**
	 * Gets the path of the field reference instance in the expression tree
	 * 
	 * @param field
	 * @return
	 */
	Expression[] getPath(Field field);

	/**
	 * Replaces in this expression tree the occurrences of expression1 by
	 * expression2.
	 * 
	 * @param expression1
	 * @param expression2
	 * @return true if expression1 was found and successfully replaced. False
	 *         otherwise
	 */
	boolean replace(Expression expression1, Expression expression2);

	/**
	 * True if this expression always returns the same value
	 * 
	 * @return
	 */
	boolean isLiteral();

	/**
	 * Transforms the top level logical operators in this expression by applying
	 * the rule: a | b -> !(!a & !b). The rule is applied to the AND, OR and NOT
	 * operators
	 * 
	 * @return Expression
	 */
	public Expression changeOrForNotAnd();

	public Expression[] splitAnds();

}
