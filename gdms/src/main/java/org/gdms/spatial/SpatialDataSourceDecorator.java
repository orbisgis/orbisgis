package org.gdms.spatial;

import java.util.HashMap;
import java.util.Map;

import org.gdms.data.AbstractDataSourceDecorator;
import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.CRSConstraint;
import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.Type;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class SpatialDataSourceDecorator extends AbstractDataSourceDecorator
		implements SpatialDataSource {

	private int spatialFieldIndex = -1;

	private Map<String, CoordinateReferenceSystem> crsMap = new HashMap<String, CoordinateReferenceSystem>();

	public SpatialDataSourceDecorator(DataSource dataSource)
			throws DriverException {
		super(dataSource);
	}

	public Envelope getFullExtent() throws DriverException {
		Number[] xScope = getScope(ReadOnlyDriver.X);
		Number[] yScope = getScope(ReadOnlyDriver.Y);
		if ((xScope != null) && (yScope != null)) {
			return new Envelope(new Coordinate(xScope[0]
					.doubleValue(), yScope[0].doubleValue()),
					new Coordinate(xScope[1].doubleValue(), yScope[1]
							.doubleValue()));
		} else {
			return null;
		}
	}

	public Geometry getGeometry(long rowIndex) throws DriverException {
		if (getDataSource().isNull(rowIndex, getSpatialFieldIndex())) {
			return null;
		} else {
			return ((GeometryValue) getDataSource().getFieldValue(rowIndex,
					getSpatialFieldIndex())).getGeom();
		}
	}

	public int getSpatialFieldIndex() throws DriverException {
		if (spatialFieldIndex == -1) {
			Metadata m = getMetadata();
			for (int i = 0; i < m.getFieldCount(); i++) {
				if (m.getFieldType(i).getTypeCode() == Type.GEOMETRY) {
					spatialFieldIndex = i;
					break;
				}
			}
		}

		return spatialFieldIndex;
	}

	public String getDefaultGeometry() throws DriverException {
		return getMetadata().getFieldName(getSpatialFieldIndex());
	}

	public Geometry getGeometry(String fieldName, long rowIndex)
			throws DriverException {
		String defaultGeometry = getDefaultGeometry();
		setDefaultGeometry(fieldName);
		Geometry ret = getGeometry(rowIndex);
		setDefaultGeometry(defaultGeometry);

		return ret;
	}

	public void setDefaultGeometry(String fieldName) throws DriverException {
		spatialFieldIndex = getFieldIndexByName(fieldName);
	}

	public CoordinateReferenceSystem getCRS(String fieldName)
			throws DriverException {
		if (fieldName == null) {
			fieldName = getFieldName(getSpatialFieldIndex());
		}
		if (crsMap.containsKey(fieldName)) {
			return crsMap.get(fieldName);
		} else {
			// delegate to the driver layer
			CRSConstraint crsConstraint = (CRSConstraint) getMetadata()
					.getFieldType(getFieldIndexByName(fieldName))
					.getConstraint(ConstraintNames.CRS);
			if (null == crsConstraint) {
				// TODO ??? setCRS(NullCRS.singleton, fieldName);
				return NullCRS.singleton;
			} else {
				setCRS(crsConstraint.getCRS(), fieldName);
				return crsConstraint.getCRS();
			}
		}
	}

	public void setCRS(final CoordinateReferenceSystem crs,
			final String fieldName) {
		crsMap.put(fieldName, crs);
	}

	public void setGeometry(long rowIndex, Geometry geom)
			throws DriverException {
		setFieldValue(rowIndex, getSpatialFieldIndex(), ValueFactory
				.createValue(geom));
	}

	public void setGeometry(String fieldName, long rowIndex, Geometry geom)
			throws DriverException {
		setFieldValue(rowIndex, getFieldIndexByName(fieldName), ValueFactory
				.createValue(geom));
	}
}