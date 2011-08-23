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
package org.gdms.sql.function.spatial.geometry.create;

import org.gdms.data.SQLDataSourceFactory;
import org.gdms.sql.function.FunctionException;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.generic.GenericObjectDriver;
import org.gdms.sql.function.table.TableDefinition;
import org.gdms.sql.function.ScalarArgument;
import org.orbisgis.progress.ProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import org.apache.log4j.Logger;
import org.gdms.driver.DataSet;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.table.AbstractTableFunction;
import org.gdms.sql.function.table.TableFunctionSignature;

public final class ST_RandomGeometry extends AbstractTableFunction {
	private static final RandomGeometryUtilities RGU = new RandomGeometryUtilities();
	private static final Envelope ENVELOPE = new Envelope(new Coordinate(),
			new Coordinate(10000, 10000));;

        private static final Logger LOG = Logger.getLogger(ST_RandomGeometry.class);

        @Override
	public DataSet evaluate(SQLDataSourceFactory dsf, DataSet[] tables,
			Value[] values, ProgressMonitor pm) throws FunctionException {
            LOG.trace("Evaluating");
		final String choice = values[0].getAsString();
		final int numberOfItems = (1 == values.length) ? 1 : values[1]
				.getAsInt();

		try {
			final GenericObjectDriver driver = new GenericObjectDriver(
					getMetadata(null));

			if (choice.equalsIgnoreCase("point")) {
				for (int i = 0; i < numberOfItems; i++) {
					driver.addValues(new Value[] { ValueFactory.createValue(i),
							ValueFactory.createValue(RGU.nextPoint(ENVELOPE)) });
				}
			} else if (choice.equalsIgnoreCase("linestring")) {
				for (int i = 0; i < numberOfItems; i++) {
					driver
							.addValues(new Value[] {
									ValueFactory.createValue(i),
									ValueFactory.createValue(RGU
											.nextLineString(ENVELOPE)) });
				}
			} else if (choice.equalsIgnoreCase("linearring")) {
				for (int i = 0; i < numberOfItems; i++) {
					driver
							.addValues(new Value[] {
									ValueFactory.createValue(i),
									ValueFactory.createValue(RGU
											.nextLinearRing(ENVELOPE)) });
				}
			} else if (choice.equalsIgnoreCase("polygon")) {
				for (int i = 0; i < numberOfItems; i++) {
					driver.addValues(new Value[] {
							ValueFactory.createValue(i),
							ValueFactory
									.createValue(RGU.nextNoHolePolygon(ENVELOPE)) });
					// .nextPolygon()) });
				}
			} else if (choice.equalsIgnoreCase("misc")) {
				for (int i = 0; i < numberOfItems; i++) {
					driver.addValues(new Value[] { ValueFactory.createValue(i),
							ValueFactory.createValue(RGU.nextGeometry(ENVELOPE)) });
				}
			} else {
				throw new FunctionException(
						"Given type must be misc, point, linestring, linearring or polygon !");
			}
			return driver.getTable("main");
		} catch (DriverException e) {
			throw new FunctionException(e);
		}
	}

        @Override
	public String getDescription() {
		return "Returns randomly choosen geometries of given type";
	}

        @Override
	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.GEOMETRY) }, new String[] { "gid",
				"the_geom" });
	}

        @Override
	public String getName() {
		return "ST_RandomGeometry";
	}

        @Override
	public String getSqlOrder() {
		return "select ST_RandomGeometry('misc|point|linestring|linearring|polygon'[, number]);";
	}

	@Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new TableFunctionSignature(TableDefinition.GEOMETRY,
                                ScalarArgument.STRING),
                                new TableFunctionSignature(TableDefinition.GEOMETRY,
                                ScalarArgument.STRING,
                                ScalarArgument.INT)
                        };
        }
}