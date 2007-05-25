package org.gdms.sql.strategies;

import org.gdms.data.InternalDataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.persistence.OperationLayerMemento;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.EvaluationException;
import org.gdms.sql.instruction.Expression;

/**
 * InternalDataSource que a�ade caracter�sticas de proyecci�n sobre campos al InternalDataSource
 * subyacente.
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class ProjectionDataSourceDecorator extends AbstractSecondaryDataSource {
	private InternalDataSource source;

	private Expression[] fields;

	private String[] aliases;

	/**
	 * Creates a new ProjectionDataSourceDecorator object.
	 *
	 * @param source
	 *            InternalDataSource origen de la informaci�n
	 * @param fields
	 *            Con los �ndices de los campos proyectados
	 * @param aliases
	 *            Nombres asignados en la instrucci�n a los campos
	 */
	public ProjectionDataSourceDecorator(InternalDataSource source, Expression[] fields,
			String[] aliases) {
		this.source = source;
		this.fields = fields;
		this.aliases = aliases;
	}

	/**
	 * Dado el �ndice de un campo en la tabla proyecci�n, se devuelve el �ndice
	 * real en el InternalDataSource subyacente
	 *
	 * @param index
	 *            �ndice del campo cuyo �ndice en el InternalDataSource subyacente se
	 *            quiere obtener
	 *
	 * @return �ndice del campo en el InternalDataSource subyacente
	 */
	private Expression getFieldByIndex(int index) {
		return fields[index];
	}

	/**
	 * @see com.hardcode.gdbms.data.DataSource#
	 */
	public void cancel() throws DriverException {
		source.cancel();
		super.cancel();
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
		 * Se comprueba si dicho �ndice est� mapeado o la ProjectionDataSourceDecorator
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
	public void open() throws DriverException {
		source.open();
		super.open();
	}

	/**
	 * @see org.gdms.driver.ObjectDriver#getFieldType(int)
	 */
	public int getFieldType(int i) throws DriverException {
		throw new UnsupportedOperationException(
				"cannot get the field type of an expression");
	}

	/**
	 * @see org.gdms.data.InternalDataSource#getMemento()
	 */
	public Memento getMemento() throws MementoException {
		return new OperationLayerMemento(getName(), new Memento[] { source
				.getMemento() }, getSQL());
	}

	public Metadata getOriginalMetadata() throws DriverException {
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
	public InternalDataSource cloneDataSource() {
		return new ProjectionDataSourceDecorator(source, fields, aliases);
	}

	public Value getOriginalFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		try {
			return getFieldByIndex(fieldId).evaluate(rowIndex);
		} catch (EvaluationException e) {
			throw new DriverException(e);
		}
	}

	public long getOriginalRowCount() throws DriverException {
		return source.getRowCount();
	}

}