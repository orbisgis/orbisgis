/*
 * Created on 12-oct-2004
 */
package org.gdms.sql.instruction;

import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;



/**
 * Adaptador
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class LValueTermAdapter extends AbstractExpression {
	//	private InternalDataSource[] tables;
	//private InternalDataSource source;
	private Field field = null;

	/**
	 * @see org.gdms.sql.instruction.Expression#getFieldName()
	 */
	public String getFieldName() {
		return Utilities.getText(getEntity());
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#simplify()
	 */
	public void simplify() {
	}

	/**
	 * @see org.gdms.sql.instruction.CachedExpression#evaluate(long)
	 */
	public Value evaluate(long row) throws EvaluationException {
		try {
            return getField().evaluateExpression(row);
        } catch (DriverException e) {
            throw new EvaluationException(e);
        } catch (SemanticException e) {
            throw new EvaluationException(e);
        }
	}

	/**
	 * Obtiene el campo al que referencia este adaptador
	 *
	 * @return Field
	 *
	 * @throws AmbiguousFieldNameException Si hay varios campos con el mismo
	 * 		   nombre y no se especific� la tabla
	 * @throws FieldNotFoundException Si no hay ning�n campo con ese nombre
	 * @throws DriverException Si se produjo un error en el driver
	 * @throws SemanticException Si existe alg�n error sem�ntico en la
	 * 		   instrucci�n
	 */
	private Field getField()
		throws AmbiguousFieldNameException, FieldNotFoundException,
			DriverException, SemanticException {
		if (field == null) {
			field = FieldFactory.createField(getInstructionContext()
												 .getFromTables(),
					getFieldName(), getInstructionContext().getDs());
		}

		return field;
	}

	/**
	 * @see org.gdms.sql.instruction.CachedExpression#isLiteral()
	 */
	public boolean isLiteral() {
		return false;
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#calculateLiteralCondition()
	 */
	public void calculateLiteralCondition() {
	}

	/**
	 * @see org.gdbms.engine.instruction.Expression#getType()
	 */
	public int getType() throws DriverException {
		try {
			return getField().getType();
		} catch (AmbiguousFieldNameException e) {
			throw new DriverException(e);
		} catch (FieldNotFoundException e) {
			throw new DriverException(e);
		} catch (SemanticException e) {
			throw new DriverException(e);
		}
	}
}
