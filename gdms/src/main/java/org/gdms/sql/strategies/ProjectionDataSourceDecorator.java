package org.gdms.sql.strategies;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.persistence.OperationLayerMemento;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.Adapter;
import org.gdms.sql.instruction.EvaluationException;
import org.gdms.sql.instruction.Expression;

/**
 * DataSource que a�ade caracter�sticas de proyecci�n sobre campos al DataSource
 * subyacente.
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class ProjectionDataSourceDecorator extends AbstractSecondaryDataSource {
	private DataSource source;

	private Expression[] fields;

	private String[] aliases;

	/**
	 * Creates a new ProjectionDataSourceDecorator object.
	 *
	 * @param source
	 *            DataSource origen de la informaci�n
	 * @param fields
	 *            Con los �ndices de los campos proyectados
	 * @param aliases
	 *            Nombres asignados en la instrucci�n a los campos
	 */
	public ProjectionDataSourceDecorator(DataSource source,
			Expression[] fields, String[] aliases) {
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
	 * @see org.gdms.data.DataSource#
	 */
	public void cancel() throws DriverException {
		source.cancel();
	}

	/**
	 * @see org.gdms.data.DataSource#
	 */
	public int getFieldCount() throws DriverException {
		return fields.length;
	}

	/**
	 * @see org.gdms.data.DataSource#
	 */
	public int getFieldIndexByName(String fieldName) throws DriverException {
		/*
		 * Se comprueba si dicho �ndice est� mapeado o la
		 * ProjectionDataSourceDecorator no lo tiene
		 */
		for (int i = 0; i < fields.length; i++) {
			if (fieldName.compareTo(fields[i].getFieldName()) == 0) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * @see org.gdms.data.DataSource#
	 */
	public void open() throws DriverException {
		source.open();
	}

	/**
	 * @see org.gdms.driver.ObjectDriver#getFieldType(int)
	 */
	public Type getFieldType(int i) throws DriverException {
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

	public Metadata getMetadata() throws DriverException {
		return new Metadata() {

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

			public Type getFieldType(int fieldId) throws DriverException {
				try {
					return TypeFactory.createType(fields[fieldId].getType());
				} catch (InvalidTypeException e) {
					throw new DriverException("");
				}
				// return fields[fieldId].getType();
			}

			public int getFieldCount() throws DriverException {
				return fields.length;
			}

		};
	}

	public boolean isOpen() {
		return source.isOpen();
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		try {
			Expression exp = getFieldByIndex(fieldId);
			Adapter adapter = (Adapter) exp;
			adapter.getInstructionContext().setNestedForIndexes(
					new int[] { (int) rowIndex });
			return exp.evaluate();
		} catch (EvaluationException e) {
			throw new DriverException(e);
		}
	}

	public long getRowCount() throws DriverException {
		return source.getRowCount();
	}

}