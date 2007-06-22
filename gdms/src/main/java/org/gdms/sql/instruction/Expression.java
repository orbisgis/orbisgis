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
