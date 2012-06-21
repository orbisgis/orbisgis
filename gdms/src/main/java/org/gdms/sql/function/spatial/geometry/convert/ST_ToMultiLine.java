/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.sql.function.spatial.geometry.convert;

import java.util.LinkedList;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.spatial.geometry.AbstractScalarSpatialFunction;

/**
 * Convert a geometry into a MultiLineString
 */
public final class ST_ToMultiLine extends AbstractScalarSpatialFunction {
	private static final class PointException extends Exception {
		// this (internal) exception is only thrown in case of (Multi)Point
		// geometry... When such an exception is catched, a NullValue is
		// returned.
	}

        @Override
	public Value evaluate(DataSourceFactory dsf,Value... args) throws FunctionException {
		if (args[0].isNull()) {
			return ValueFactory.createNullValue();
		} else {

			final Geometry geometry = args[0].getAsGeometry();
			final List<LineString> allLineString = new LinkedList<LineString>();

			try {
				toMultiLineString(geometry, allLineString);
			} catch (PointException e) {
				allLineString.clear();
			}

			final MultiLineString multiLineString = new GeometryFactory()
					.createMultiLineString(allLineString
							.toArray(new LineString[allLineString.size()]));
			return ValueFactory.createValue(multiLineString);

		}
	}

	private void toMultiLineString(final LineString lineString,
			final List<LineString> allLineString) {
		allLineString.add(lineString);
	}

	private void toMultiLineString(final Polygon polygon,
			final List<LineString> allLineString) {
		allLineString.add(polygon.getExteriorRing());
		final int nbOfHoles = polygon.getNumInteriorRing();
		for (int i = 0; i < nbOfHoles; i++) {
			allLineString.add(polygon.getInteriorRingN(i));
		}
	}

	private void toMultiLineString(final GeometryCollection geometryCollection,
			final List<LineString> allLineString) throws PointException {
		final int nbOfLinesStrings = geometryCollection.getNumGeometries();
		for (int i = 0; i < nbOfLinesStrings; i++) {
			toMultiLineString(geometryCollection.getGeometryN(i), allLineString);
		}
	}

	private void toMultiLineString(final Geometry geometry,
			final List<LineString> allLineString) throws PointException {
		if ((geometry instanceof Point) || (geometry instanceof MultiPoint)) {
			throw new PointException();
		} else if (geometry instanceof LineString) {
			toMultiLineString((LineString) geometry, allLineString);
		} else if (geometry instanceof Polygon) {
			toMultiLineString((Polygon) geometry, allLineString);
		} else if (geometry instanceof GeometryCollection) {
			toMultiLineString((GeometryCollection) geometry, allLineString);
		}
	}

        @Override
	public String getDescription() {
		return "Convert a geometry into a MultiLineString";
	}

        @Override
	public String getName() {
		return "ST_ToMultiLine";
	}

        @Override
	public String getSqlOrder() {
		return "select ST_ToMultiLine(the_geom) from myTable;";
	}

}