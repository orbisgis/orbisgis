/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.spatial.geometry.AbstractAggregateSpatialFunction;

/**
 * Convert an ordered set of [Multi]Points in a single LineString geometry.
 */
public final class ST_PointsToLine extends AbstractAggregateSpatialFunction {
	private static final Value NULLVALUE = ValueFactory.createNullValue();
	private static final GeometryFactory GF = new GeometryFactory();
	private List<Coordinate> coords = new LinkedList<Coordinate>();

        @Override
	public void evaluate(DataSourceFactory dsf,Value[] args) throws FunctionException {
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
	}

        @Override
	public String getDescription() {
		return "Convert an ordered set of [Multi]Points in a single LineString geometry";
	}

	@Override
	public String getName() {
		return "ST_PointsToLine";
	}

        @Override
	public String getSqlOrder() {
		return "select ST_PointsToLine(the_geom) from mylayer";
	}

        @Override
	public Type getType(Type[] argsTypes) {
		return TypeFactory.createType(Type.GEOMETRY);
	}

        @Override
	public Value getAggregateResult() {
		if (coords.size() > 2) {
			return ValueFactory.createValue(GF.createLineString(coords
					.toArray(new Coordinate[coords.size()])));
		} else {
			return NULLVALUE;
		}
	}
}