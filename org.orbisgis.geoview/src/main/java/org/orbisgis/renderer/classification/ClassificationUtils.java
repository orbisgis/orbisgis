package org.orbisgis.renderer.classification;

import java.util.Arrays;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;

public class ClassificationUtils {

	public static double[] getSortedValues(DataSource ds, String fieldName)
			throws DriverException {
		double[] values = new double[(int) ds.getRowCount()];

		int fieldIndex = ds.getFieldIndexByName(fieldName);
		for (int i = 0; i < values.length; i++) {
			values[i] = ds.getFieldValue(i, fieldIndex).getAsDouble();
		}
		Arrays.sort(values);
		return values;

	}

}
