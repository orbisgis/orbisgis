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
		throw new Error();
	}

	public Object duplicate(Object src) throws IllegalAttributeException {
		throw new Error();
	}

	public int getMaxOccurs() {
		throw new Error();
	}

	public int getMinOccurs() {
		throw new Error();
	}

	public String getName() {
		try {
			return md.getFieldName(position);
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}
	}

	public Filter getRestriction() {
		throw new Error();
	}

	public Class getType() {
		throw new Error();
	}

	public boolean isNillable() {
		throw new Error();
	}

	public Object parse(Object value) throws IllegalArgumentException {
		// TODO
		return value;
	}

	public void validate(Object obj) throws IllegalArgumentException {
		// TODO
	}
}