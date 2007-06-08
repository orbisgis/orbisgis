package org.gdms.data.metadata;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;

public class DefaultMetadata implements Metadata {

	private List<Type> fieldsTypes;

	private List<String> fieldsNames;

	public DefaultMetadata() {
		this.fieldsTypes = new LinkedList<Type>();
		this.fieldsNames = new LinkedList<String>();
	}

	public DefaultMetadata(Type[] fieldsTypes, String[] fieldsNames) {
		this.fieldsTypes = new LinkedList<Type>(Arrays.asList(fieldsTypes));
		this.fieldsNames = new LinkedList<String>(Arrays.asList(fieldsNames));
	}

	public DefaultMetadata(final Metadata originalMetadata)
			throws DriverException {
		this();
		final int fc = originalMetadata.getFieldCount();

		for (int i = 0; i < fc; i++) {
			fieldsTypes.add(originalMetadata.getFieldType(i));
			fieldsNames.add(originalMetadata.getFieldName(i));
		}
	}

	public int getFieldCount() {
		return fieldsTypes.size();
	}

	public Type getFieldType(int fieldId) {
		return fieldsTypes.get(fieldId);
	}

	public String getFieldName(int fieldId) {
		return fieldsNames.get(fieldId);
	}

	public void addField(final String fieldName, final int typeCode)
			throws InvalidTypeException {
		fieldsNames.add(fieldName);
		fieldsTypes.add(TypeFactory.createType(typeCode));
	}

	public void addField(final String fieldName, final int typeCode,
			final Constraint[] constraints) throws InvalidTypeException {
		fieldsNames.add(fieldName);
		fieldsTypes.add(TypeFactory.createType(typeCode, constraints));
	}

	public void addField(final String fieldName, final int typeCode,
			final String typeName) throws InvalidTypeException {
		fieldsNames.add(fieldName);
		fieldsTypes.add(TypeFactory.createType(typeCode, typeName));
	}

	public void addField(final String fieldName, final int typeCode,
			final String typeName, final Constraint[] constraints)
			throws InvalidTypeException {
		fieldsNames.add(fieldName);
		fieldsTypes
				.add(TypeFactory.createType(typeCode, typeName, constraints));
	}

	public void addField(int index, String fieldName, int typeCode)
			throws InvalidTypeException {
		fieldsNames.add(index, fieldName);
		fieldsTypes.add(index, TypeFactory.createType(typeCode));
	}
}