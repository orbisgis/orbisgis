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
package org.gdms.sql.function.spatial.mixed;

import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.RasterValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class ToEnvelope implements Function {
	private static final GeometryFactory gf = new GeometryFactory();

	public Value evaluate(Value[] args) throws FunctionException {
		Envelope grEnv;
		if (args[0] instanceof RasterValue) {
			grEnv = args[0].getAsRaster().getMetadata().getEnvelope();
		} else {
			grEnv = args[0].getAsGeometry().getEnvelopeInternal();
		}
		return ValueFactory.createValue(toGeometry(grEnv));
	}

	private static Geometry toGeometry(final Envelope envelope) {
		if ((0 == envelope.getWidth()) || (0 == envelope.getHeight())) {
			if (0 == envelope.getWidth() + envelope.getHeight()) {
				return gf.createPoint(new Coordinate(envelope.getMinX(),
						envelope.getMinY()));
			}
			return gf.createLineString(new Coordinate[] {
					new Coordinate(envelope.getMinX(), envelope.getMinY()),
					new Coordinate(envelope.getMaxX(), envelope.getMaxY()) });
		}

		return gf
				.createPolygon(gf
						.createLinearRing(new Coordinate[] {
								new Coordinate(envelope.getMinX(), envelope
										.getMinY()),
								new Coordinate(envelope.getMinX(), envelope
										.getMaxY()),
								new Coordinate(envelope.getMaxX(), envelope
										.getMaxY()),
								new Coordinate(envelope.getMaxX(), envelope
										.getMinY()),
								new Coordinate(envelope.getMinX(), envelope
										.getMinY()) }), null);
	}

	public String getDescription() {
		return "Computes the envelope of the parameter and returns a geometry";
	}

	public String getName() {
		return "Envelope";
	}

	public String getSqlOrder() {
		return "select Envelope(raster) as raster from mytif; ---OR--- select Envelope(the_geom) from mytable;";
	}

	public Type getType(Type[] argsTypes) throws InvalidTypeException {
		return TypeFactory.createType(Type.GEOMETRY);
	}

	public boolean isAggregate() {
		return false;
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.GEOMETRY),
				new Arguments(Argument.RASTER) };
	}

	@Override
	public Value getAggregateResult() {
		return null;
	}

}