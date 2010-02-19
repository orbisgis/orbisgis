package org.gdms.data.feature;

import org.gdms.data.values.Value;

public interface IFeature {

	void setValues(Value[] values);

	Value[] getValues();

	Value getValue(int fieldIndex);

	void addValue(int fieldIndex, Value value);

}
