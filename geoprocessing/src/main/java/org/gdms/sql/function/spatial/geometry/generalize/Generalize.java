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
package org.gdms.sql.function.spatial.geometry.generalize;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.spatial.geometry.AbstractSpatialFunction;
import org.geoalgorithm.jts.operation.DouglasPeuckerGeneralization;
import org.geoalgorithm.jts.operation.ISAGeneralization;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 *
 * Reduce the number of points in a polygon or a line. By default the algoritm
 * is Douglas Peucker but you can use another method named ISA.
 *
 */

public class Generalize extends AbstractSpatialFunction {

	GeometryFactory factory = new GeometryFactory();

	public Value evaluate(Value[] args) throws FunctionException {
		if (args[0].isNull() || args[1].isNull()) {
			return ValueFactory.createNullValue();

		} else {
			final Geometry geom = args[0].getAsGeometry();
			final double maxDistance = args[1].getAsDouble();
			Geometry result = null;
			if (args.length == 3) {
				final String method = args[2].toString();

				if (method.toLowerCase().equals("isa")) {
					ISAGeneralization isaGeneralization = new ISAGeneralization(
							geom, maxDistance);
					result = isaGeneralization.reducePoints();

				} else if (method.toLowerCase().equals("douglas")) {

					DouglasPeuckerGeneralization douglasPeuckerGeneralization = new DouglasPeuckerGeneralization(
							geom, maxDistance);

					result = douglasPeuckerGeneralization.reducePoints();
				}
			} else {

				DouglasPeuckerGeneralization douglasPeuckerGeneralization = new DouglasPeuckerGeneralization(
						geom, maxDistance);

				result = douglasPeuckerGeneralization.reducePoints();
			}
			return ValueFactory.createValue(result);

		}

	}

	public String getDescription() {

		return "Reduce the number of points in a polygon or a line. By default the algoritm is Douglas Peucker"
				+ "but you can use another method named ISA.";
	}

	public String getName() {
		return "Generalize";
	}

	public String getSqlOrder() {

		return "select Generalize(the_geom,maxdistance, [method]) from myTable;";
	}

	public boolean isAggregate() {

		return false;
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] {
				new Arguments(Argument.GEOMETRY, Argument.NUMERIC),
				new Arguments(Argument.GEOMETRY, Argument.NUMERIC,
						Argument.STRING) };
	}

	public boolean isDesaggregate() {
		return false;
	}

}