package org.gdms.geotoolsAdapter;

import java.net.URI;

import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.spatial.PTTypes;
import org.gdms.spatial.SpatialDataSource;
import org.geotools.feature.AttributeType;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;
import org.geotools.feature.GeometryAttributeType;
import org.geotools.feature.IllegalAttributeException;
import org.geotools.feature.type.GeometricAttributeType;
import org.geotools.feature.type.NumericAttributeType;
import org.geotools.feature.type.TemporalAttributeType;
import org.geotools.feature.type.TextualAttributeType;

import com.vividsolutions.jts.geom.Geometry;

public class FeatureTypeAdapter implements FeatureType {

	private SpatialDataSource ds;

	private Metadata md;

	public FeatureTypeAdapter(SpatialDataSource ds) {
		this.ds = ds;
		try {
			this.md = ds.getDataSourceMetadata();
		} catch (DriverException e) {
			throw new RuntimeException();
		}
	}

	public Feature create(Object[] attributes) throws IllegalAttributeException {
		throw new RuntimeException();
	}

	public Feature create(Object[] attributes, String featureID)
			throws IllegalAttributeException {
		throw new RuntimeException();
	}

	public Feature duplicate(Feature feature) throws IllegalAttributeException {
		throw new RuntimeException();
	}

	public int find(AttributeType type) {
		throw new RuntimeException();
	}

	public int find(String attName) {
		throw new RuntimeException();
	}

	public FeatureType[] getAncestors() {
		throw new RuntimeException();
	}

	public int getAttributeCount() {
		try {
			return md.getFieldCount();
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}
	}

	public AttributeType getAttributeType(String xPath) {
		try {
			return getAttributeType(ds.getFieldIndexByName(xPath));
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}
	}

	public AttributeType getAttributeType(int position) {
		try {
			int fieldType = md.getFieldType(position);
			switch (fieldType) {
			case Value.DOUBLE:
				return new NumericAttributeType(md.getFieldName(position),
						Double.class, true, null, null);
			case Value.INT:
				return new NumericAttributeType(md.getFieldName(position),
						Integer.class, true, null, null);
			case Value.FLOAT:
				return new NumericAttributeType(md.getFieldName(position),
						Float.class, true, null, null);
			case Value.SHORT:
				return new NumericAttributeType(md.getFieldName(position),
						Short.class, true, null, null);
			case Value.BYTE:
				return new NumericAttributeType(md.getFieldName(position),
						Byte.class, true, null, null);
			case Value.LONG:
				return new NumericAttributeType(md.getFieldName(position),
						Long.class, true, null, null);
			case Value.BOOLEAN:
				return new NumericAttributeType(md.getFieldName(position),
						Byte.class, true, null, null);
			case Value.STRING:
				return new TextualAttributeType(md.getFieldName(position),
						true, -1, -1, null, null);
			case Value.DATE:
			case Value.TIMESTAMP:
			case Value.TIME:
				return new TemporalAttributeType(md.getFieldName(position),
						true, -1, -1, null, null);
			case Value.BINARY:
				return new TextualAttributeType(md.getFieldName(position),
						true, -1, -1, null, null);
			case PTTypes.GEOMETRY:
				//TODO we use the default CRS in MapContext
				return new GeometricAttributeType(md.getFieldName(position),
						Geometry.class, true, null,
						GeometryAttributeTypeAdapter.currentCRS, null);
			}

			throw new RuntimeException("unrecognized type");
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}
	}

	public AttributeType[] getAttributeTypes() {
		throw new RuntimeException();
	}

	public GeometryAttributeType getDefaultGeometry() {
		return new GeometryAttributeTypeAdapter();
	}

	public URI getNamespace() {
		// TODO: No idea what is this used for
		return URI.create("");
	}

	public String getTypeName() {
		// TODO Maybe this string have to match something in the SLD xml
		return "unique type name";
	}

	public boolean hasAttributeType(String xPath) {
		throw new RuntimeException();
	}

	public boolean isAbstract() {
		throw new RuntimeException();
	}

	public boolean isDescendedFrom(FeatureType type) {
		throw new RuntimeException();
	}

	public boolean isDescendedFrom(URI nsURI, String typeName) {
		/*
		 * TODO If we return true the style is applied to the datasource. could
		 * we have a sld with more than one featuretype for our data source? I
		 * don't think so because the sld is created taking into account the
		 * DataSource type
		 */
		return true;
	}

}
