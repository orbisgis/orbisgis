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
package org.gdms.sql.function.spatial.geometry.create;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import org.apache.log4j.Logger;
import org.orbisgis.progress.ProgressMonitor;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.table.AbstractTableFunction;
import org.gdms.sql.function.table.TableDefinition;
import org.gdms.sql.function.table.TableFunctionSignature;

public final class ST_RandomGeometry extends AbstractTableFunction {
	private static final RandomGeometryUtilities RGU = new RandomGeometryUtilities();
	private static final Envelope ENVELOPE = new Envelope(new Coordinate(),
			new Coordinate(10000, 10000));;

        private static final Logger LOG = Logger.getLogger(ST_RandomGeometry.class);

        @Override
	public DataSet evaluate(DataSourceFactory dsf, DataSet[] tables,
			Value[] values, ProgressMonitor pm) throws FunctionException {
            LOG.trace("Evaluating");
		final String choice = values[0].getAsString();
		final int numberOfItems = (1 == values.length) ? 1 : values[1]
				.getAsInt();

		try {
			final MemoryDataSetDriver driver = new MemoryDataSetDriver(
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
			return driver;
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
		return "select * from ST_RandomGeometry('misc|point|linestring|linearring|polygon'[, number]);";
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