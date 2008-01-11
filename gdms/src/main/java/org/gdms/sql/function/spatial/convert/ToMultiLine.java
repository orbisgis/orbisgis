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
package org.gdms.sql.function.spatial.convert;

import java.util.LinkedList;
import java.util.List;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class ToMultiLine implements Function {
	private class PointException extends Exception {
		// this (internal) exception is only thrown in case of (Multi)Point
		// geometry... When such an exception is catched, a NullValue is
		// returned.
	}

	public Function cloneFunction() {
		return new ToMultiLine();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		final Geometry geometry = args[0].getAsGeometry();
		final List<LineString> allLineString = new LinkedList<LineString>();

		try {
			toMultiLineString(geometry, allLineString);
		} catch (PointException e) {
			// return ValueFactory.createNullValue();
			allLineString.clear();
		}

		final MultiLineString multiLineString = new GeometryFactory()
				.createMultiLineString(allLineString.toArray(new LineString[0]));
		return ValueFactory.createValue(multiLineString);
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
		return "Convert any GDMS default spatial field into a MultiLineString";
	}

	public String getName() {
		return "ToMultiLine";
	}

	public int getType(int[] paramTypes) {
		return paramTypes[0];
	}

	public boolean isAggregate() {
		return false;
	}

	public String getSqlOrder() {
		return "select ToMultiLine(the_geom) from myTable;";
	}
}