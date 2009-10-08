package org.gdms.model;

import java.util.Date;
import java.util.Iterator;

import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.ObjectMemoryDriver;

import com.vividsolutions.jts.geom.Geometry;

public class FeatureCollectionModelUtils {

	public static Metadata getMetadata(FeatureSchema fs) throws DriverException {
		DefaultMetadata metadata = new DefaultMetadata();
		for (int i = 0; i < fs.getAttributeCount(); i++) {
			metadata.addField(getFieldName(fs, i), getFieldType(fs, i));
		}
		return metadata;
	}

	public static String getFieldName(FeatureSchema fs, int fieldId)
			throws DriverException {
		return fs.getAttributeName(fieldId);
	}

	public static Type getFieldType(FeatureSchema fs, int i)
			throws DriverException {

		AttributeType at = fs.getAttributeType(i);
		if (at == AttributeType.DATE) {
			return TypeFactory.createType(Type.DATE);
		} else if (at == AttributeType.DOUBLE) {
			return TypeFactory.createType(Type.DOUBLE);
		} else if (at == AttributeType.GEOMETRY) {
			return TypeFactory.createType(Type.GEOMETRY);
		} else if (at == AttributeType.INTEGER) {
			return TypeFactory.createType(Type.INT);
		} else if (at == AttributeType.STRING) {
			return TypeFactory.createType(Type.STRING);
		} else if (at == AttributeType.OBJECT) {
			return TypeFactory.createType(Type.STRING);
		}

		throw new RuntimeException("OpenUMP attribute type unknow"); //$NON-NLS-1$
	}

	static Value[] getValues(Feature feature) {

		FeatureSchema fs = feature.getSchema();
		Value[] values = new Value[fs.getAttributeCount()];

		for (int i = 0; i < feature.getAttributes().length; i++) {

			Object o = feature.getAttribute(i);
			if (o == null) {
				values[i] = ValueFactory.createNullValue();
			} else {
				if (o instanceof Geometry) {
					if (((Geometry) o).isEmpty()) {
						values[i] = ValueFactory.createNullValue();
					} else {
						values[i] = ValueFactory.createValue((Geometry) o);
					}
				} else if (o instanceof Value) {
					values[i] = (Value) o;
				} else {
					AttributeType at = fs.getAttributeType(i);
					if (at == AttributeType.DATE) {
						values[i] = ValueFactory.createValue((Date) o);
					} else if (at == AttributeType.DOUBLE) {
						values[i] = ValueFactory.createValue((Double) o);
					} else if (at == AttributeType.INTEGER) {
						values[i] = ValueFactory.createValue((Integer) o);
					} else if (at == AttributeType.STRING) {
						values[i] = ValueFactory.createValue((String) o);
					} else if (at == AttributeType.OBJECT) {
						values[i] = ValueFactory.createValue(o.toString());
					}
				}
			}
		}

		return values;
	}

	public static ObjectMemoryDriver getObjectMemoryDriver(FeatureCollection fc)
			throws DriverException {

		ObjectMemoryDriver driver = new ObjectMemoryDriver(getMetadata(fc
				.getFeatureSchema()));

		Iterator iterator = fc.iterator();
		for (Iterator ia = iterator; ia.hasNext();) {
			Feature feature = (Feature) ia.next();

			driver.addValues(FeatureCollectionModelUtils.getValues(feature));

		}

		return driver;

	}

}
