/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.data;

import java.util.HashMap;
import java.util.Map;

import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.CRSConstraint;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.NullCRS;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;
import org.grap.model.GeoRaster;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class SpatialDataSourceDecorator extends AbstractDataSourceDecorator {

	private int spatialFieldIndex = -1;

	private Map<String, CoordinateReferenceSystem> crsMap = new HashMap<String, CoordinateReferenceSystem>();

	public SpatialDataSourceDecorator(DataSource dataSource) {
		super(dataSource);
	}



	/**
	 * Gets the full extent of the data accessed
	 *
	 * @return Rectangle2D
	 *
	 * @throws DriverException
	 *             if the operation fails
	 */
	public Envelope getFullExtent() throws DriverException {
		Number[] xScope = getScope(ReadOnlyDriver.X);
		Number[] yScope = getScope(ReadOnlyDriver.Y);
		if ((xScope != null) && (yScope != null)) {
			return new Envelope(new Coordinate(xScope[0].doubleValue(),
					yScope[0].doubleValue()), new Coordinate(xScope[1]
					.doubleValue(), yScope[1].doubleValue()));
		} else {
			return null;
		}
	}

	public GeoRaster getRaster(long rowIndex) throws DriverException {
		Value fieldValue = getDataSource().getFieldValue(rowIndex,
				getSpatialFieldIndex());
		if (fieldValue.isNull()) {
			return null;
		} else {
			return fieldValue.getAsRaster();
		}
	}

	/**
	 * Gets the default geometry of the DataSource as a JTS geometry or null if
	 * the row doesn't have a geometry value
	 *
	 * @param rowIndex
	 * @return
	 * @throws DriverException
	 */
	public Geometry getGeometry(long rowIndex) throws DriverException {
		Value fieldValue = getDataSource().getFieldValue(rowIndex,
				getSpatialFieldIndex());
		if (fieldValue.isNull()) {
			return null;
		} else {
			return fieldValue.getAsGeometry();
		}
	}

	/**
	 * Returns the index of the field containing spatial data
	 *
	 * @return
	 * @throws DriverException
	 */
	public int getSpatialFieldIndex() throws DriverException {
		if (spatialFieldIndex == -1) {
			Metadata m = getMetadata();
			for (int i = 0; i < m.getFieldCount(); i++) {
				int typeCode = m.getFieldType(i).getTypeCode();
				if ((typeCode == Type.GEOMETRY) || (typeCode == Type.RASTER)) {
					spatialFieldIndex = i;
					break;
				}
			}
		}

		return spatialFieldIndex;
	}

	/**
	 * Returns the name of the field which is the default geometry. If the data
	 * source contains only one spatial field, the default geometry is that
	 * field initially
	 *
	 * @return
	 * @throws DriverException
	 */

	public String getDefaultGeometry() throws DriverException {
		return getMetadata().getFieldName(getSpatialFieldIndex());
	}

	/**
	 * Get the geometry in the specified field in the specified row of the data
	 * source
	 *
	 * @param fieldName
	 * @param rowIndex
	 * @return
	 * @throws DriverException
	 */
	public Geometry getGeometry(String fieldName, long rowIndex)
			throws DriverException {
		String defaultGeometry = getDefaultGeometry();
		setDefaultGeometry(fieldName);
		Geometry ret = getGeometry(rowIndex);
		setDefaultGeometry(defaultGeometry);

		return ret;
	}

	/**
	 * Get the raster in the specified field in the specified row of the data
	 * source
	 *
	 * @param fieldName
	 * @param rowIndex
	 * @return
	 * @throws DriverException
	 */
	public GeoRaster getRaster(String fieldName, long rowIndex)
			throws DriverException {
		String defaultGeometry = getDefaultGeometry();
		setDefaultGeometry(fieldName);
		GeoRaster ret = getRaster(rowIndex);
		setDefaultGeometry(defaultGeometry);

		return ret;
	}

	/**
	 * Set the field name for the getGeometry(int) method. If this method is not
	 * called, the default geometry is the first spatial field
	 *
	 * @param fieldName
	 * @throws DriverException
	 */
	public void setDefaultGeometry(String fieldName) throws DriverException {
		final int tmpSpatialFieldIndex = getFieldIndexByName(fieldName);
		if (-1 == tmpSpatialFieldIndex) {
			throw new DriverException(fieldName + " is not a field !");
		} else {
			int fieldType = getMetadata().getFieldType(tmpSpatialFieldIndex)
					.getTypeCode();
			if ((fieldType == Type.GEOMETRY) || (fieldType == Type.RASTER)) {
				spatialFieldIndex = tmpSpatialFieldIndex;
			} else {
				throw new DriverException(fieldName
						+ " is not a spatial field !");
			}
		}
	}

	/**
	 * Returns the CRS of the geometric field that is given as parameter
	 *
	 * @param fieldName
	 *
	 * @return
	 * @throws DriverException
	 */
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
					.getConstraint(Constraint.CRS);
			if (null == crsConstraint) {
				return NullCRS.singleton;
			} else {
				setCRS(crsConstraint.getCRS(), fieldName);
				return crsConstraint.getCRS();
			}
		}
	}

	/**
	 * Sets the CRS of the geometric field that is given as 2nd parameter
	 *
	 * @param crs
	 * @param fieldName
	 */
	public void setCRS(final CoordinateReferenceSystem crs,
			final String fieldName) {
		crsMap.put(fieldName, crs);
	}

	/**
	 * Sets the default geometry of the DataSource to a JTS geometry
	 *
	 * @param rowIndex
	 * @param geom
	 * @return
	 * @throws DriverException
	 */
	public void setGeometry(long rowIndex, Geometry geom)
			throws DriverException {
		setFieldValue(rowIndex, getSpatialFieldIndex(), ValueFactory
				.createValue(geom));
	}

	/**
	 * Set the geometry in the specified field in the specified row of the data
	 * source
	 *
	 * @param fieldName
	 * @param rowIndex
	 * @param geom
	 * @return
	 * @throws DriverException
	 */
	public void setGeometry(String fieldName, long rowIndex, Geometry geom)
			throws DriverException {
		setFieldValue(rowIndex, getFieldIndexByName(fieldName), ValueFactory
				.createValue(geom));
	}

	/**
	 * Returns true if the default geometry is vectorial and false otherwise
	 *
	 * @return
	 * @throws DriverException
	 */
	public boolean isDefaultVectorial() throws DriverException {
		Type fieldType = getMetadata().getFieldType(getSpatialFieldIndex());
		return fieldType.getTypeCode() == Type.GEOMETRY;
	}

	/**
	 * Returns true if the default geometry is raster and false otherwise
	 *
	 * @return
	 * @throws DriverException
	 */
	public boolean isDefaultRaster() throws DriverException {
		Type fieldType = getMetadata().getFieldType(getSpatialFieldIndex());
		return fieldType.getTypeCode() == Type.RASTER;
	}
}
