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
package org.gdms.sql.function.spatial.raster.hydrology;

import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.operation.hydrology.D8OpSlope;
import org.grap.processing.operation.hydrology.D8OpSlopeInDegrees;
import org.grap.processing.operation.hydrology.D8OpSlopeInRadians;

public class D8Slope implements Function {
	public Value evaluate(Value[] args) throws FunctionException {
		final GeoRaster geoRasterSrc = args[0].getAsRaster();
		try {
			Operation slopesOp;
			if (2 == args.length) {
				if (args[1].toString().toLowerCase().equals("radian")) {
					slopesOp = new D8OpSlopeInRadians();
				} else if (args[1].toString().toLowerCase().equals("degree")) {
					slopesOp = new D8OpSlopeInDegrees();
				} else if (args[1].toString().toLowerCase().equals("percent")) {
					slopesOp = new D8OpSlope();
				} else {
					throw new FunctionException(
							"You have to choose between: radian, degree or percent!");
				}
			} else {
				slopesOp = new D8OpSlope();
			}
			// compute the slopes directions
			return ValueFactory.createValue(geoRasterSrc.doOperation(slopesOp));
		} catch (OperationException e) {
			throw new FunctionException("Cannot do the operation", e);
		}
	}

	public String getDescription() {
		return "Compute the slopes using a GRAY16/32 DEM as input table. "
				+ "Default unit is percent, but it is also possible to specify "
				+ "it as: radian, degree, percent.";
	}

	public String getName() {
		return "D8Slope";
	}

	public String getSqlOrder() {
		return "select D8Slope(raster[, 'radian'|'degree'|'percent' ]) as raster from mydem;";
	}

	public Type getType(Type[] argsTypes) throws InvalidTypeException {
		return TypeFactory.createType(Type.RASTER);
	}

	public boolean isAggregate() {
		return false;
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.RASTER),
				new Arguments(Argument.RASTER, Argument.STRING) };
	}

	public Value getAggregateResult() {
		return null;
	}

	public boolean isDesaggregate() {
		return false;
	}
}