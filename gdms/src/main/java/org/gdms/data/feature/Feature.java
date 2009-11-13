package org.gdms.data.feature;

import org.gdms.data.metadata.Metadata;
import org.gdms.data.metadata.MetadataUtilities;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.grap.model.GeoRaster;

import com.vividsolutions.jts.geom.Geometry;

public class Feature implements IFeature {

	private Metadata metadata;
	private Value[] values;
	private int spatialFieldIndex = -1;

	public Feature(Metadata metadata) throws DriverException {
		this.metadata = metadata;
		this.spatialFieldIndex = MetadataUtilities
				.getSpatialFieldIndex(metadata);
		values = new Value[metadata.getFieldCount()];

	}

	public void setValues(Value[] values) {
		this.values = values;
	}

	public Geometry getGeometry() {
		return values[spatialFieldIndex].getAsGeometry();
	}

	public GeoRaster getRaster() {
		return values[spatialFieldIndex].getAsRaster();
	}

	public Value[] getValues() {
		return values;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void addValue(String fieldName, Value value) throws DriverException {
		addValue(metadata.getFieldIndex(fieldName), value);
	}

	public void addValue(int fieldIndex, Value value) {
		values[fieldIndex] = value;
	}

	public Value getValue(String fieldName) throws DriverException {
		return getValue(metadata.getFieldIndex(fieldName));
	}

	public Value getValue(int fieldIndex) {
		return values[fieldIndex];
	}

}
