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

import java.awt.geom.Point2D;

import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.operation.hydrology.D8OpAllOutlets;
import org.grap.processing.operation.hydrology.D8OpAllWatersheds;
import org.grap.processing.operation.hydrology.D8OpWatershedFromOutletIndex;
import org.grap.processing.operation.hydrology.D8OpWatershedsWithThreshold;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class D8Watershed implements Function {
	public Value evaluate(Value[] args) throws FunctionException {

		try {
			GeoRaster grD8Direction = args[0].getAsRaster();
			// compute all watersheds
			if (args.length == 1) {
				final Operation allWatersheds = new D8OpAllWatersheds();
				final GeoRaster grAllWatersheds = grD8Direction
						.doOperation(allWatersheds);
				return ValueFactory.createValue(grAllWatersheds);
			}

			else if (args.length == 2) {

				// Compute watershed using and outlet as a geometry
				if (args[1].getType() == Type.GEOMETRY) {

					Geometry geom = args[1].getAsGeometry();

					if (geom instanceof Point) {

						Point point = (Point) geom;
						double x = point.getX();
						double y = point.getY();
						
						int outletIndex = (int) x
								+ (int) y
								* grD8Direction.getMetadata().getNCols();

						final Operation watershedFromOutletIndex = new D8OpWatershedFromOutletIndex(
								outletIndex);

						return ValueFactory.createValue(grD8Direction
								.doOperation(watershedFromOutletIndex));

					}
				}
			} else {

				if (args[2].getType() == Type.INT) {
					GeoRaster grD8Accumulation = args[1].getAsRaster();
					int watershedThreshold = args[2].getAsInt();
					final Operation allWatersheds = new D8OpAllWatersheds();
					final GeoRaster grAllWatersheds = grD8Direction
							.doOperation(allWatersheds);
					// find all outlets
					final Operation allOutlets = new D8OpAllOutlets();
					final GeoRaster grAllOutlets = grD8Direction
							.doOperation(allOutlets);
					// extract some "big" watersheds
					D8OpWatershedsWithThreshold d8OpWatershedsWithThreshold = new D8OpWatershedsWithThreshold(
							grAllWatersheds, grAllOutlets, watershedThreshold);

					return ValueFactory.createValue(grD8Accumulation
							.doOperation(d8OpWatershedsWithThreshold));

				}
			}

		} catch (OperationException e) {
			throw new FunctionException("Cannot do the operation", e);
		}
		return null;
	}

	public String getDescription() {
		return "Compute all  watersheds or using a threshold integer accumulation value";
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] { TypeFactory
				.createType(Type.RASTER) }, new String[] { "raster" });
	}

	public String getName() {
		return "D8Watershed";
	}

	public String getSqlOrder() {
		return "select D8Watershed(dir.raster, acc.raster[, value]) from dir, acc;";
	}

	public void validateTypes(Type[] types) throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, types, 1, 2, 3);
		FunctionValidator.failIfNotOfType(this, types[0], Type.RASTER);
		if (types.length == 2) {
			FunctionValidator.failIfNotOfType(this, types[1], Type.GEOMETRY);
		}
		if (types.length == 3) {
			FunctionValidator.failIfNotOfType(this, types[1], Type.RASTER);
			FunctionValidator.failIfNotNumeric(this, types[2]);

		}

	}

	public Type getType(Type[] argsTypes) throws InvalidTypeException {
		return TypeFactory.createType(Type.RASTER);
	}

	public boolean isAggregate() {
		return false;
	}
}