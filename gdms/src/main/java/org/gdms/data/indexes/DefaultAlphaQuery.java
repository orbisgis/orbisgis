package org.gdms.data.indexes;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;

public class DefaultAlphaQuery implements AlphaQuery {

	private Value min;
	private boolean minIncluded;
	private boolean maxIncluded;
	private Value max;
	private String fieldName;

	public DefaultAlphaQuery(String fieldName, Value exactValue) {
		this(fieldName, exactValue, true, exactValue, true);
	}

	public DefaultAlphaQuery(String fieldName, Value min, boolean minIncluded,
			Value max, boolean maxIncluded) {
		this.min = min;
		this.minIncluded = minIncluded;
		this.max = max;
		this.maxIncluded = maxIncluded;
		this.fieldName = fieldName;

		if (this.min == null) {
			this.min = ValueFactory.createNullValue();
		}

		if (this.max == null) {
			this.max = ValueFactory.createNullValue();
		}
	}

	public String getFieldName() {
		return fieldName;
	}

	public boolean isStrict() {
		return true;
	}

	public Value getMin() {
		return min;
	}

	public boolean isMinIncluded() {
		return minIncluded;
	}

	public boolean isMaxIncluded() {
		return maxIncluded;
	}

	public Value getMax() {
		return max;
	}

}
