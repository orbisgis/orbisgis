package org.gdms.geotoolsAdapter;

import java.net.URI;

import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.gdms.spatial.GeometryValue;
import org.gdms.spatial.SpatialDataSource;
import org.geotools.data.shapefile.shp.JTSUtilities;
import org.geotools.data.shapefile.shp.ShapeType;
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

	private SpatialDataSource sds;

	private Metadata md;

	public FeatureTypeAdapter(SpatialDataSource sds) {
		this.sds = sds;
		try {
			this.md = sds.getMetadata();
		} catch (DriverException e) {
			throw new RuntimeException();
		}
	}

	public Feature create(Object[] attributes) throws IllegalAttributeException {
		throw new Error();
	}

	public Feature create(Object[] attributes, String featureID)
			throws IllegalAttributeException {
		throw new Error();
	}

	public Feature duplicate(Feature feature) throws IllegalAttributeException {
		throw new Error();
	}

	public int find(AttributeType type) {
		throw new Error();
	}

	public int find(String attName) {
		throw new Error();
	}

	public FeatureType[] getAncestors() {
		throw new Error();
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
			return getAttributeType(sds.getFieldIndexByName(xPath));
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}
	}

	public AttributeType getAttributeType(int position) {
		try {
			int fieldType = md.getFieldType(position).getTypeCode();
			switch (fieldType) {
			case Type.DOUBLE:
				return new NumericAttributeType(md.getFieldName(position),
						Double.class, true, null, null);
			case Type.INT:
				return new NumericAttributeType(md.getFieldName(position),
						Integer.class, true, null, null);
			case Type.FLOAT:
				return new NumericAttributeType(md.getFieldName(position),
						Float.class, true, null, null);
			case Type.SHORT:
				return new NumericAttributeType(md.getFieldName(position),
						Short.class, true, null, null);
			case Type.BYTE:
				return new NumericAttributeType(md.getFieldName(position),
						Byte.class, true, null, null);
			case Type.LONG:
				return new NumericAttributeType(md.getFieldName(position),
						Long.class, true, null, null);
			case Type.BOOLEAN:
				return new NumericAttributeType(md.getFieldName(position),
						Byte.class, true, null, null);
			case Type.STRING:
				return new TextualAttributeType(md.getFieldName(position),
						true, -1, -1, null, null);
			case Type.DATE:
			case Type.TIMESTAMP:
			case Type.TIME:
				return new TemporalAttributeType(md.getFieldName(position),
						true, -1, -1, null, null);
			case Type.BINARY:
				return new TextualAttributeType(md.getFieldName(position),
						true, -1, -1, null, null);
			case Type.GEOMETRY:
				// TODO we use the default CRS in MapContext
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
		throw new Error();
	}

	public GeometryAttributeType getDefaultGeometry() {
		try {
			final int spatialFieldId = sds.getFieldIndexByName(sds
					.getDefaultGeometry());
			final GeometryValue geometryValue = (GeometryValue) sds
					.getFieldValue(0, spatialFieldId);
			final Geometry geometry = geometryValue.getGeom();
			final ShapeType shapeType = JTSUtilities
					.findBestGeometryType(geometry);
			return new GeometryAttributeTypeAdapter(shapeType.id);
		} catch (DriverException e) {
			throw new Error();
		}
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
		throw new Error();
	}

	public boolean isAbstract() {
		throw new Error();
	}

	public boolean isDescendedFrom(FeatureType type) {
		throw new Error();
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