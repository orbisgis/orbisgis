package org.gdms.data.feature;

import org.gdms.data.values.Value;

/**
 * <code>IFeature</code> is the base interface for features
 *
 */

public interface IFeature {

	void setValues(Value[] values);

	Value[] getValues();

	Value getValue(int fieldIndex);

	void addValue(int fieldIndex, Value value);

}
