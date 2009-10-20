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
package org.gdms.sql.function.spatial.geometry.convert;

import java.util.LinkedList;
import java.util.List;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.spatial.geometry.AbstractSpatialFunction;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class ToMultiLine extends AbstractSpatialFunction {
	private class PointException extends Exception {
		// this (internal) exception is only thrown in case of (Multi)Point
		// geometry... When such an exception is catched, a NullValue is
		// returned.
	}

	public Value evaluate(Value[] args) throws FunctionException {
		if (args[0].isNull()) {
			return ValueFactory.createNullValue();
		} else {

			final Geometry geometry = args[0].getAsGeometry();
			final List<LineString> allLineString = new LinkedList<LineString>();

			try {
				toMultiLineString(geometry, allLineString);
			} catch (PointException e) {
				// return ValueFactory.createNullValue();
				allLineString.clear();
			}

			final MultiLineString multiLineString = new GeometryFactory()
					.createMultiLineString(allLineString
							.toArray(new LineString[0]));
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

	public String getDescription() {
		return "Convert a geometry into a MultiLineString";
	}

	public String getName() {
		return "ToMultiLine";
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.GEOMETRY) };
	}

	public boolean isAggregate() {
		return false;
	}

	public String getSqlOrder() {
		return "select ToMultiLine(the_geom) from myTable;";
	}

}