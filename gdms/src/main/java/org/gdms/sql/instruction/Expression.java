package org.gdms.sql.instruction;

import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;


/**
 * Interfaz a implementar sobre los nodos
 *
 * @author Fernando Gonz�lez Cort�s
 */
public interface Expression {
    /**
     * Eval�a la expresi�n para la fila row y realiza el cacheado del resultado
     * en caso de que la expression sea un literal
     *
     * @param row fila para la que se evalua la expresi�n
     *
     * @return Valor resultante de evaluar la expresi�n para la fila row
     *
     * @throws EvaluationException Si se produce alg�n error sem�ntico
     */
    Value evaluateExpression(long row) throws EvaluationException;

    /**
     * Obtiene el nombre del campo en el que consiste la expresi�n. En el caso
     * de que la expresi�n conste de alguna operaci�n o no contenga ninguna
     * referencia a un campo se devolver� null.
     *
     * @return Nombre del campo
     */
    String getFieldName();

    /**
     * Checks if this expression is an aggregate function. It is, implements
     * the Function interface and its isAggregate method returns true
     *
     * @return boolean
     */
    boolean isAggregated();

    /**
     * Simplifica las expresiones del �rbol de adaptadores
     */
    void simplify();

    /**
     * Eval�a la expresi�n para la fila row
     *
     * @param row fila para la que se evalua la expresi�n
     *
     * @return Valor resultante de evaluar la expresi�n para la fila row
     *
     * @throws EvaluationException Si se produce alg�n error sem�ntico
     */
    Value evaluate(long row) throws EvaluationException;

    /**
     * Indica si los operandos de esta expresi�n son siempre los mismos o
     * pueden cambiar. Puede cambiar cuando el operando es una funcion o una
     * referencia a un campo y no debe cambiar en el resto de casos
     *
     * @return true si esta expresi�n va a devolver siempre el mismo valor
     */
    boolean isLiteral();

    /**
     * Gets the type of the expression
     *
     * @return
     * @throws DriverException If the type is finally asked to a driver
     * and the call fails.
     */
    int getType() throws DriverException;

}
