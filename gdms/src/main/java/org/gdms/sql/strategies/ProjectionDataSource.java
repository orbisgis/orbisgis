package org.gdms.sql.strategies;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.persistence.OperationLayerMemento;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.EvaluationException;
import org.gdms.sql.instruction.Expression;

/**
 * DataSource que a�ade caracter�sticas de proyecci�n sobre campos al DataSource
 * subyacente.
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class ProjectionDataSource extends AbstractSecondaryDataSource {
	private DataSource source;

	private Expression[] fields;

	private String[] aliases;

	/**
	 * Creates a new ProjectionDataSource object.
	 * 
	 * @param source
	 *            DataSource origen de la informaci�n
	 * @param fields
	 *            Con los �ndices de los campos proyectados
	 * @param aliases
	 *            Nombres asignados en la instrucci�n a los campos
	 */
	public ProjectionDataSource(DataSource source, Expression[] fields,
			String[] aliases) {
		this.source = source;
		this.fields = fields;
		this.aliases = aliases;
	}

	/**
	 * Dado el �ndice de un campo en la tabla proyecci�n, se devuelve el �ndice
	 * real en el DataSource subyacente
	 * 
	 * @param index
	 *            �ndice del campo cuyo �ndice en el DataSource subyacente se
	 *            quiere obtener
	 * 
	 * @return �ndice del campo en el DataSource subyacente
	 */
	private Expression getFieldByIndex(int index) {
		return fields[index];
	}

	/**
	 * @see com.hardcode.gdbms.data.DataSource#
	 */
	public void rollBackTrans() throws DriverException {
		source.rollBackTrans();
	}

	/**
	 * @see com.hardcode.gdbms.data.DataSource#
	 */
	public int getFieldCount() throws DriverException {
		return fields.length;
	}

	/**
	 * @see com.hardcode.gdbms.data.DataSource#
	 */
	public int getFieldIndexByName(String fieldName) throws DriverException {
		/*
		 * Se comprueba si dicho �ndice est� mapeado o la ProjectionDataSource
		 * no lo tiene
		 */
		for (int i = 0; i < fields.length; i++) {
			if (fieldName.compareTo(fields[i].getFieldName()) == 0) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * @see com.hardcode.gdbms.data.DataSource#
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		try {
			return getFieldByIndex(fieldId).evaluate(rowIndex);
		} catch (EvaluationException e) {
			throw new DriverException(e);
		}
	}

	/**
	 * @see com.hardcode.gdbms.data.DataSource#
	 */
	public long getRowCount() throws DriverException {
		return source.getRowCount();
	}

	/**
	 * @see com.hardcode.gdbms.data.DataSource#
	 */
	public void beginTrans() throws DriverException {
		source.beginTrans();
	}

	/**
	 * @see org.gdms.driver.ObjectDriver#getFieldType(int)
	 */
	public int getFieldType(int i) throws DriverException {
		throw new UnsupportedOperationException(
				"cannot get the field type of an expression");
	}

	/**
	 * @see org.gdms.data.DataSource#getMemento()
	 */
	public Memento getMemento() throws MementoException {
		return new OperationLayerMemento(getName(), new Memento[] { source
				.getMemento() }, getSQL());
	}

	public Metadata getDataSourceMetadata() throws DriverException {
		return new Metadata() {

			public Boolean isReadOnly(int fieldId) throws DriverException {
				return true;
			}

			public String[] getPrimaryKey() throws DriverException {
				return new String[0];
			}

			public String getFieldName(int fieldId) throws DriverException {
				if (aliases[fieldId] != null) {
					return aliases[fieldId];
				} else {
					String name = fields[fieldId].getFieldName();

					if (name == null) {
						return "unknown" + fieldId;
					} else {
						return name;
					}
				}
			}

			public int getFieldType(int fieldId) throws DriverException {
				return fields[fieldId].getType();
			}

			public int getFieldCount() throws DriverException {
				return fields.length;
			}

		};
	}

	public boolean isOpen() {
		return source.isOpen();
	}

	@Override
	public DataSource cloneDataSource() {
		return new ProjectionDataSource(source, fields, aliases);
	}

	public Value getOriginalFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return getFieldValue(rowIndex, fieldId);
	}
}