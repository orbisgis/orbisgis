package org.gdms.geotoolsAdapter;

import java.net.URI;

import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.geotools.factory.FactoryConfigurationError;
import org.geotools.feature.AttributeType;
import org.geotools.feature.AttributeTypeFactory;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;
import org.geotools.feature.FeatureTypeBuilder;
import org.geotools.feature.GeometryAttributeType;
import org.geotools.feature.IllegalAttributeException;
import org.geotools.feature.SchemaException;
import org.geotools.feature.type.GeometricAttributeType;
import org.geotools.feature.type.NumericAttributeType;
import org.geotools.feature.type.TemporalAttributeType;
import org.geotools.feature.type.TextualAttributeType;

import com.vividsolutions.jts.geom.Geometry;

public class FeatureTypeAdapter implements FeatureType {

	// private SpatialDataSourceDecorator sds;

	private Metadata md;

	private int spatialFieldIndex;

	// public FeatureTypeAdapter(SpatialDataSourceDecorator sds) {
	// this.sds = sds;
	// try {
	// this.md = sds.getMetadata();
	// } catch (DriverException e) {
	// throw new RuntimeException();
	// }
	// }

	public FeatureTypeAdapter(final Metadata metadata,
			final int spatialFieldIndex) {
		this.md = metadata;
		this.spatialFieldIndex = spatialFieldIndex;
	}

	public Feature create(Object[] attributes) throws IllegalAttributeException {
		AttributeType[] types = getAttributeTypes();
		FeatureType featureType;
		try {
			featureType = FeatureTypeBuilder.newFeatureType(types,
					getTypeName(), null, false, null,
					getGeometryAttribute(types));
			return featureType.create(attributes);
		} catch (FactoryConfigurationError e) {
			throw new Error(e);
		} catch (SchemaException e) {
			throw new Error(e);
		}
	}

	private AttributeType getGeometryAttribute(AttributeType[] types) {
		for (AttributeType type : types) {
			if (type.getType().isAssignableFrom(Geometry.class)) {
				return type;
			}
		}

		return null;
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
			for (int i = 0; i < md.getFieldCount(); i++) {
				if (md.getFieldName(i).equals(xPath)) {
					return getAttributeType(i);
				}
			}
			return getAttributeType(-1);
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
				return AttributeTypeFactory.newAttributeType(md.getFieldName(position), Boolean.class);
//				md.getFieldName(position),
//						Byte.class, true, null, null);
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
		final int fc = getAttributeCount();
		final AttributeType[] result = new AttributeType[fc];
		for (int fieldId = 0; fieldId < fc; fieldId++) {
			result[fieldId] = getAttributeType(fieldId);
		}
		return result;
	}

	public GeometryAttributeType getDefaultGeometry() {
		// if (-1 == spatialFieldIndex) {
		// return new GeometryAttributeTypeAdapter("dbfVirtualField",
		// GeometryConstraint.POINT_2D);
		// } else {
		GeometryConstraint c;
		try {
			c = (GeometryConstraint) md.getFieldType(spatialFieldIndex)
					.getConstraint(ConstraintNames.GEOMETRY);
			return new GeometryAttributeTypeAdapter(md
					.getFieldName(spatialFieldIndex), c.getGeometryType());
			// final ShapeType shapeType = c.findBestGeometryType();
			// return new GeometryAttributeTypeAdapter(md
			// .getFieldName(spatialFieldIndex), shapeType);
		} catch (DriverException e) {
			throw new Error();
		}
		// }
		// try {
		// final String spatialFieldName = sds.getDefaultGeometry();
		// final int spatialFieldId = sds
		// .getFieldIndexByName(spatialFieldName);
		// final GeometryValue geometryValue = (GeometryValue) sds
		// .getFieldValue(0, spatialFieldId);
		// final Geometry geometry = geometryValue.getGeom();
		// final ShapeType shapeType = JTSUtilities
		// .findBestGeometryType(geometry);
		// return new GeometryAttributeTypeAdapter(spatialFieldName, shapeType);
		// } catch (DriverException e) {
		// throw new Error();
		// }
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