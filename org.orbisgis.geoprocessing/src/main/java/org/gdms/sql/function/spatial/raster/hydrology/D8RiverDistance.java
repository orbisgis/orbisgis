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

import java.io.IOException;

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
import org.grap.processing.operation.hydrology.D8OpRiverDistance;

public class D8RiverDistance implements Function {
	public Value evaluate(Value[] args) throws FunctionException {
		final GeoRaster grD8Direction = args[0].getAsRaster();
		final GeoRaster grD8Accumulation = args[1].getAsRaster();
		int riverThreshold = args[2].getAsInt();

		try {
			final Operation riverDistance = new D8OpRiverDistance(
					grD8Accumulation, riverThreshold);
			return ValueFactory.createValue(grD8Direction
					.doOperation(riverDistance));
		} catch (OperationException e) {
			throw new FunctionException("Cannot do the operation", e);
		} catch (IOException e) {
			throw new FunctionException("Accumulation grid is not readable", e);
		}
	}

	public String getDescription() {
		return "Calculate the maximum length to the river using a GRAY16/32 DEM slopes "
				+ "directions and a GRAY16/32 DEM slopes accumulations as input tables";
	}

	public String getName() {
		return "D8RiverDistance";
	}

	public String getSqlOrder() {
		return "select D8RiverDistance(d.raster, a.raster, RiverThreshold) as raster from direction d, accumulation a;";
	}

	public Type getType(Type[] argsTypes) throws InvalidTypeException {
		return TypeFactory.createType(Type.RASTER);
	}

	public boolean isAggregate() {
		return false;
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.RASTER,
				Argument.RASTER, Argument.NUMERIC) };
	}

	public Value getAggregateResult() {
		return null;
	}

	public boolean isDesaggregate() {
		return false;
	}
}