/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.spatial;

import java.util.HashMap;
import java.util.Map;

import org.gdms.data.AbstractDataSourceDecorator;
import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.CRSConstraint;
import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.Type;
import org.gdms.data.values.NullValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;
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
		if (fieldValue instanceof NullValue) {
			return null;
		} else {
			return ((GeometryValue) fieldValue).getGeom();
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
				if (m.getFieldType(i).getTypeCode() == Type.GEOMETRY) {
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
	 * Set the field name for the getGeometry(int) method. If this method is not
	 * called, the default geometry is the first spatial field
	 *
	 * @param fieldName
	 * @throws DriverException
	 */
	public void setDefaultGeometry(String fieldName) throws DriverException {
		spatialFieldIndex = getFieldIndexByName(fieldName);
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
					.getConstraint(ConstraintNames.CRS);
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
}
