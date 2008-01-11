/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.sql.instruction;

import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

/**
 * Interfaz a implementar sobre los nodos
 *
 * @author Fernando Gonz�lez Cort�s
 */
public interface Expression {
	/**
	 * Evaluates the expression for the specified row and catches the result
	 * if it is a literal
	 *
	 * @param row
	 *            fila para la que se evalua la expresion
	 *
	 * @return Valor resultante de evaluar la expresion para la fila row
	 *
	 * @throws EvaluationException
	 *             Si se produce algun error semantico
	 */
	Value evaluateExpression() throws EvaluationException;

	/**
	 * Gets the name of the field of this expression if it is directly a field
	 * reference. In case it is not, either because it contains some operation
	 * or because it doesn't contains any field reference, it returns null.
	 *
	 * @return Nombre del campo
	 */
	String getFieldName();

	/**
	 * Gets the name of the table this expression references. if, and only if,
	 * getFieldName returns null, this method returns null.
	 *
	 * @return Nombre del campo
	 * @throws DriverException
	 */
	String getFieldTable() throws DriverException;

	/**
	 * Checks if this expression is an aggregate function. It is, implements the
	 * Function interface and its isAggregate method returns true
	 *
	 * @return boolean
	 */
	boolean isAggregated();

	/**
	 * Simplifica las expresiones del �rbol de adaptadores
	 */
	void simplify();

	/**
	 * Evaluates the expression for the specified row
	 *
	 * @return Valor resultante de evaluar la expresion para la fila row
	 *
	 * @throws EvaluationException
	 *             Si se produce algun error semantico
	 */
	Value evaluate() throws EvaluationException;

	/**
	 *
	 * @return true if this expression always returns the same value
	 */
	boolean isLiteral();

	/**
	 * Gets the type of the expression
	 *
	 * @return
	 * @throws DriverException
	 *             If the type is finally asked to a driver and the call fails.
	 */
	int getType() throws DriverException;

	/**
	 * Filters the DataSource taking into account the current state
	 * of the dynamic loop
	 *
	 * @param from
	 * @return
	 * @throws DriverException
	 */
	Iterator<PhysicalDirection> filter(DataSource from) throws DriverException;
}
