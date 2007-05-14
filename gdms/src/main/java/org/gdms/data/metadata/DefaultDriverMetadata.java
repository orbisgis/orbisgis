package org.gdms.data.metadata;

import java.util.ArrayList;
import java.util.HashMap;

import org.gdms.data.driver.DriverException;
import org.gdms.data.edition.MetadataEditionSupport;

public class DefaultDriverMetadata implements DriverMetadata {

	private ArrayList<String> names = new ArrayList<String>();

	private ArrayList<String> typeNames = new ArrayList<String>();

	private ArrayList<HashMap<String, String>> params = new ArrayList<HashMap<String, String>>();

	private String[] pks = new String[0];

	public void addField(String name, String typeName, String[] paramNames,
			String[] paramValues) {
		names.add(name);
		typeNames.add(typeName);
		HashMap<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < paramValues.length; i++) {
			map.put(paramNames[i], paramValues[i]);
		}
		this.params.add(map);
	}

	public void addField(String name, String typeName) {
		addField(name, typeName, new String[0], new String[0]);
	}

	public String getFieldType(int fieldId) {
		return typeNames.get(fieldId);
	}

	public String getFieldName(int fieldId) {
		return names.get(fieldId);
	}

	public int getFieldCount() {
		return names.size();
	}

	public String getFieldParam(int fieldId, String paramName) {
		return params.get(fieldId).get(paramName);
	}

	public String[] getParamNames(int fieldId) {
		HashMap<String, String> fieldParams = params.get(fieldId);
		return MetadataEditionSupport.getStrings(fieldParams.keySet()
				.iterator(), fieldParams.size());
	}

	public String[] getParamValues(int fieldId) {
		HashMap<String, String> fieldParams = params.get(fieldId);
		return MetadataEditionSupport.getStrings(fieldParams.values()
				.iterator(), fieldParams.size());
	}

	public HashMap<String, String> getFieldParams(int fieldId)
			throws DriverException {
		return params.get(fieldId);
	}

	public void setFieldType(int fieldIndex, String driverType) {
		typeNames.set(fieldIndex, driverType);
	}

	public void setPrimaryKey(String[] pks) {
		this.pks = pks;
	}

	public String[] getPrimaryKeys() {
		return pks;
	}

	public void addAll(DriverMetadata dmd) throws DriverException {
		for (int i = 0; i < dmd.getFieldCount(); i++) {
			addField(dmd.getFieldName(i), dmd.getFieldType(i), dmd
					.getParamNames(i), dmd.getParamValues(i));
		}

		this.setPrimaryKey(dmd.getPrimaryKeys());
	}
}
