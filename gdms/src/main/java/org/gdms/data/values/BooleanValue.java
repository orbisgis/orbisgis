package org.gdms.data.values;

import java.io.Serializable;

import org.gdms.data.types.Type;
import org.gdms.sql.instruction.IncompatibleTypesException;

/**
 * Wrapper para booleanos
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class BooleanValue extends AbstractValue implements Serializable {
	public Value greater(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (!(value instanceof BooleanValue)) {
			throw new IncompatibleTypesException();
		}

		return ValueFactory.createValue((!((BooleanValue) value).value)
				&& this.value);
	}

	public Value greaterEqual(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (!(value instanceof BooleanValue)) {
			throw new IncompatibleTypesException();
		}

		return ValueFactory
				.createValue(!(((BooleanValue) value).value && !this.value));
	}

	public Value less(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(true);
		}

		if (!(value instanceof BooleanValue)) {
			throw new IncompatibleTypesException();
		}

		return ValueFactory.createValue(((BooleanValue) value).value
				&& !(this.value));
	}

	public Value lessEqual(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(true);
		}

		if (!(value instanceof BooleanValue)) {
			throw new IncompatibleTypesException();
		}

		return ValueFactory
				.createValue(!(!((BooleanValue) value).value && this.value));
	}

	private boolean value;

	/**
	 * Creates a new BooleanValue object.
	 * 
	 * @param value
	 *            Valor booleano que tendr� este objeto
	 */
	BooleanValue(boolean value) {
		this.value = value;
	}

	/**
	 * Creates a new BooleanValue object.
	 */
	BooleanValue() {
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param value
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws IncompatibleTypesException
	 *             DOCUMENT ME!
	 */
	public Value equals(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof BooleanValue) {
			return ValueFactory
					.createValue(this.value == ((BooleanValue) value).value);
		} else {
			throw new IncompatibleTypesException();
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param value
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws IncompatibleTypesException
	 *             DOCUMENT ME!
	 */
	public Value notEquals(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof BooleanValue) {
			return ValueFactory
					.createValue(this.value != ((BooleanValue) value).value);
		} else {
			throw new IncompatibleTypesException();
		}
	}

	/**
	 * Establece el valor de este objeto
	 * 
	 * @param value
	 */
	public void setValue(boolean value) {
		this.value = value;
	}

	/**
	 * Obtiene el valor de este objeto
	 * 
	 * @return
	 */
	public boolean getValue() {
		return value;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "" + value;
	}

	/**
	 * @see org.gdms.sql.instruction.Operations#and(org.gdms.sql.instruction.BooleanValue)
	 */
	public Value and(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof BooleanValue) {
			Value ret = ValueFactory.createValue(this.value
					&& ((BooleanValue) value).getValue());

			return ret;
		} else {
			throw new IncompatibleTypesException();
		}
	}

	/**
	 * @see org.gdms.sql.instruction.Operations#and(org.gdms.sql.instruction.BooleanValue)
	 */
	public Value or(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (value instanceof BooleanValue) {
			Value ret = ValueFactory.createValue(this.value
					|| ((BooleanValue) value).getValue());

			return ret;
		} else {
			throw new IncompatibleTypesException();
		}
	}

	/**
	 * @see org.gdms.data.values.Value#doHashCode()
	 */
	public int doHashCode() {
		return Boolean.valueOf(value).hashCode();
	}

	/**
	 * @see org.gdms.data.values.Value#getStringValue(org.gdms.data.values.ValueWriter)
	 */
	public String getStringValue(ValueWriter writer) {
		return writer.getStatementString(value);
	}

	/**
	 * @see org.gdms.data.values.Value#inversa()
	 */
	public Value inversa() throws IncompatibleTypesException {
		return ValueFactory.createValue(!value);
	}

	/**
	 * @see org.gdms.data.values.Value#getType()
	 */
	public int getType() {
		return Type.BOOLEAN;
	}
}