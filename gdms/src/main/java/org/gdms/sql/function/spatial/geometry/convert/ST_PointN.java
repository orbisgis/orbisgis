/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info _at_ orbisgis.org
 *  
 * 
 */
package org.gdms.sql.function.spatial.geometry.convert;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

public class ST_PointN implements Function {

	public Value evaluate(DataSourceFactory dsf, Value[] args)
			throws FunctionException {
		final Geometry geom = args[0].getAsGeometry();

		if (geom instanceof LineString) {
			LineString line = (LineString) geom;
			return ValueFactory.createValue(line.getPointN(args[1].getAsInt()));

		}
		return ValueFactory.createNullValue();
	}

	public String getName() {
		return "ST_PointN";
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.GEOMETRY, Argument.INT) };
	}

	public boolean isAggregate() {
		return false;
	}

	public String getDescription() {
		return "Returns the N point of a LINESTRING geometry as a POINT or NULL if the input parameter is not a LINESTRING.";
	}

	public String getSqlOrder() {
		return "select ST_PointN(the_geom, index) from myTable;";
	}

	@Override
	public Value getAggregateResult() {
		return null;
	}

	@Override
	public Type getType(Type[] argsTypes) throws InvalidTypeException {
		return TypeFactory.createType(Type.GEOMETRY);
	}

}