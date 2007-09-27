/**
 *
 */
package org.gdms.driver.shapefile;

import java.util.ArrayList;

import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;

class DBFMetadata implements Metadata {

	private ArrayList<Integer> mapping;

	private Metadata metadata;

	public DBFMetadata(Metadata metadata) throws DriverException {
		this.metadata = metadata;

		mapping = new ArrayList<Integer>();
		for (int i = 0; i < metadata.getFieldCount(); i++) {
			if (metadata.getFieldType(i).getTypeCode() != Type.GEOMETRY) {
				mapping.add(i);
			}
		}
		if (mapping.size() + 1 != metadata.getFieldCount()) {
			throw new IllegalArgumentException("The data source "
					+ "has more than one spatial field");
		}
	}

	public int getFieldCount() throws DriverException {
		return metadata.getFieldCount() - 1;
	}

	public String getFieldName(int fieldId) throws DriverException {
		return metadata.getFieldName(mapping.get(fieldId));
	}

	public Type getFieldType(int fieldId) throws DriverException {
		return metadata.getFieldType(mapping.get(fieldId));
	}

	public ArrayList<Integer> getMapping() {
		return mapping;
	}

}