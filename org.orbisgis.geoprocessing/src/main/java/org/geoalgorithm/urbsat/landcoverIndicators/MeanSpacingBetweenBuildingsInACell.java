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
package org.geoalgorithm.urbsat.landcoverIndicators;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;

/*
 * select creategrid(...,...) from build1; => grid select
 * intersection(g.the_geom,b.the_geom),g.index from grid as g, build1 as b where
 * intersects(g.the_geom,b.the_geom); => build2 select geomUnion(the_geom),index
 * from build2 group by index; => build3 select
 * MeanSpace(g.the_geom,b.the_geom),g.index from grid as g, build3 as b;
 */

public class MeanSpacingBetweenBuildingsInACell implements Function {
	public Value evaluate(Value[] args) throws FunctionException {
		if (args[0].isNull() || args[1].isNull()) {
			return ValueFactory.createNullValue();
		} else {
			final Geometry geomGrid = args[0].getAsGeometry();
			final Geometry geomBuild = args[1].getAsGeometry();

			final Geometry noBuildSpace = geomGrid.difference(geomBuild);
			final double s = noBuildSpace.getArea();
			final double p = noBuildSpace.getLength();

			final double result = 0.25 * p - 0.5
					* Math.sqrt(0.25 * p * p - 4 * s);
			return ValueFactory.createValue(result);
		}
	}

	public String getDescription() {
		return "Calculate mean spacing between buildings (grid.the_geom, build.the_geom)";
	}

	public String getName() {
		return "MEANSPACING";
	}

	public boolean isAggregate() {
		return false;
	}

	public String getSqlOrder() {
		return "select MEANSPACING(a.the_geom,intersection(a.the_geom,b.the_geom)) from grid as a, build as b where intersects(a.the_geom,b.the_geom);";
		// return "select MeanSpacing(a.the_geom,b.the_geom) from grid as a,
		// build as b where intersects(a.the_geom,b.the_geom);";
	}

	public Type getType(Type[] argsTypes) {
		return TypeFactory.createType(Type.DOUBLE);
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.GEOMETRY,
				Argument.GEOMETRY) };
	}

	public Value getAggregateResult() {
		return null;
	}

}