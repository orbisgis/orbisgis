package org.gdms.geotoolsAdapter;

import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.geotools.feature.AttributeType;
import org.geotools.feature.IllegalAttributeException;
import org.geotools.filter.Filter;

public class AttributeTypeAdapter implements AttributeType {

	private Metadata md;
	private int position;

	public AttributeTypeAdapter(Metadata md, int position) {
		this.md = md;
		this.position = position;
	}

	public Object createDefaultValue() {
		throw new RuntimeException();
	}

	public Object duplicate(Object src) throws IllegalAttributeException {
		throw new RuntimeException();
	}

	public int getMaxOccurs() {
		throw new RuntimeException();
	}

	public int getMinOccurs() {
		throw new RuntimeException();
	}

	public String getName() {
		try {
			return md.getFieldName(position);
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}
	}

	public Filter getRestriction() {
		throw new RuntimeException();
	}

	public Class getType() {
		throw new RuntimeException();
	}

	public boolean isNillable() {
		throw new RuntimeException();
	}

	public Object parse(Object value) throws IllegalArgumentException {
		//TODO
		return value;
	}

	public void validate(Object obj) throws IllegalArgumentException {
		//TODO
	}

}
