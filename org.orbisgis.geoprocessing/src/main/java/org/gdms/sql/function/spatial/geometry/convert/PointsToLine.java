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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

public class PointsToLine implements Function {
	private final static Value nullValue = ValueFactory.createNullValue();
	private final static GeometryFactory gf = new GeometryFactory();
	private List<Coordinate> coords = new LinkedList<Coordinate>();

	public Value evaluate(Value[] args) throws FunctionException {
		if (!args[0].isNull()) {
			Geometry geometry = args[0].getAsGeometry();
			if (geometry instanceof Point) {
				coords.add(((Point) geometry).getCoordinate());
			} else if (geometry instanceof MultiPoint) {
				Coordinate[] tmp = ((MultiPoint) geometry).getCoordinates();
				coords.addAll(Arrays.asList(tmp));
			} else {
				throw new FunctionException(
						"PointsToLine function only processes [Multi]Point as input geometry!");
			}
		}
		return nullValue;
	}

	public String getDescription() {
		return "Convert an ordered set of [Multi]Points in a single LineString geometry";
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.GEOMETRY) };
	}

	public String getName() {
		return "PointsToLine";
	}

	public String getSqlOrder() {
		return "select PointsToLine(the_geom) from mylayer";
	}

	public Type getType(Type[] argsTypes) throws InvalidTypeException {
		return TypeFactory.createType(Type.GEOMETRY);
	}

	public boolean isAggregate() {
		return true;
	}

	public Value getAggregateResult() {
		if (coords.size() > 2) {
			return ValueFactory.createValue(gf.createLineString(coords
					.toArray(new Coordinate[0])));
		} else {
			return nullValue;
		}
	}

	public boolean isDesaggregate() {
		return false;
	}
}