package org.gdms.sql.instruction;

import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;



/**
 * Clase que representa un campo en un DataSource. La clase por s� sola no
 * identifica al campo, ya que la tabla a la que pertenece el campo viene
 * definida por un �ndice entero. Esto se debe a que para definir un campo es
 * necesario tambi�n un array de tablas sobre las que se aplica dicho �ndice.
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class Field extends AbstractExpression implements Expression {
	private DataSource[] tables;
	private int dataSourceIndex;
	private int fieldId;
	private DataSource dataSource;

	/**
	 * Indice de la tabla donde se encuentra el campo al que este objeto hace
	 * referencia
	 *
	 * @return
	 */
	public int getDataSourceIndex() {
		return dataSourceIndex;
	}

	/**
	 * Identificador del campo al que este objeto hace referencia
	 *
	 * @return
	 */
	public int getFieldId() {
		return fieldId;
	}

	/**
	 * Establece el �ndice dentr de un array de tablas del campo al que este
	 * objeto hace referencia
	 *
	 * @param source
	 */
	public void setDataSourceIndex(int source) {
		dataSourceIndex = source;
	}

	/**
	 * Establece el identificador al que este objeto hace referencia
	 *
	 * @param i
	 */
	public void setFieldId(int i) {
		fieldId = i;
	}

	/**
	 * Obtiene la colecci�n de tablas sobre las que los �ndices dataSourceIndex
	 * y fieldId son v�lidos
	 *
	 * @return
	 */
	public DataSource[] getTables() {
		return tables;
	}

	/**
	 * Establece la colecci�n de tablas sobre las que se definen los �ndices
	 * dataSourceIndex y fieldId
	 *
	 * @param sources
	 */
	public void setTables(DataSource[] sources) {
		tables = sources;
	}

	/**
	 * Devuelve el �ndice que ocupa el campo en la colecci�n de los campos de
	 * todas las tablas del array tables.
	 *
	 * @return �ndice absoluto del campo
	 *
	 * @throws DriverException Si se produce un error accediendo a las tablas
	 */
	public int getAbsoluteIndex() throws DriverException {
		int acum = 0;

		for (int table = 0; table < dataSourceIndex; table++) {
			acum += tables[table].getDataSourceMetadata().getFieldCount();
		}

		acum += fieldId;

		return acum;
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#evaluate(long)
	 */
	public Value evaluate(long row) throws EvaluationException {
		try {
            return dataSource.getFieldValue(row, getAbsoluteIndex());
        } catch (DriverException e) {
            throw new EvaluationException(e);
        }
	}

	/**
	 * Obtiene la fuente de datos sobre la que obtiene su valor este campo en
	 * su m�todo evaluate
	 *
	 * @return
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * Establece la fuente de datos sobre la que obtiene su valor este campo en
	 * su m�todo evaluate
	 *
	 * @param source
	 */
	public void setDataSource(DataSource source) {
		dataSource = source;
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#getFieldName()
	 */
	public String getFieldName() {
		return null;
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#isLiteral()
	 */
	public boolean isLiteral() {
		return false;
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#simplify()
	 */
	public void simplify() {
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#calculateLiteralCondition()
	 */
	public void calculateLiteralCondition() {
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#getType()
	 */
	public int getType() throws DriverException {
        return dataSource.getDataSourceMetadata().getFieldType(getAbsoluteIndex());
	}
}
