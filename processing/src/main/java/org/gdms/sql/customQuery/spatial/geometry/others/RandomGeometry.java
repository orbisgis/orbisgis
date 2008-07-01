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
package org.gdms.sql.customQuery.spatial.geometry.others;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class RandomGeometry implements CustomQuery {
	private static final RandomGeometryUtilities rgu = new RandomGeometryUtilities();
	private static final Envelope env = new Envelope(new Coordinate(),
			new Coordinate(10000, 10000));;

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		final String choice = values[0].getAsString();
		final int numberOfItems = (1 == values.length) ? 1 : values[1]
				.getAsInt();

		try {
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					getMetadata(null));

			if (choice.equalsIgnoreCase("point")) {
				for (int i = 0; i < numberOfItems; i++) {
					driver.addValues(new Value[] { ValueFactory.createValue(i),
							ValueFactory.createValue(rgu.nextPoint(env)) });
				}
			} else if (choice.equalsIgnoreCase("linestring")) {
				for (int i = 0; i < numberOfItems; i++) {
					driver
							.addValues(new Value[] {
									ValueFactory.createValue(i),
									ValueFactory.createValue(rgu
											.nextLineString(env)) });
				}
			} else if (choice.equalsIgnoreCase("linearring")) {
				for (int i = 0; i < numberOfItems; i++) {
					driver
							.addValues(new Value[] {
									ValueFactory.createValue(i),
									ValueFactory.createValue(rgu
											.nextLinearRing(env)) });
				}
			} else if (choice.equalsIgnoreCase("polygon")) {
				for (int i = 0; i < numberOfItems; i++) {
					driver.addValues(new Value[] {
							ValueFactory.createValue(i),
							ValueFactory
									.createValue(rgu.nextNoHolePolygon(env)) });
					// .nextPolygon()) });
				}
			} else if (choice.equalsIgnoreCase("misc")) {
				for (int i = 0; i < numberOfItems; i++) {
					driver.addValues(new Value[] { ValueFactory.createValue(i),
							ValueFactory.createValue(rgu.nextGeometry(env)) });
				}
			} else {
				throw new ExecutionException(
						"Given type must be misc, point, linestring, linearring or polygon !");
			}
			return driver;
		} catch (DriverException e) {
			throw new ExecutionException(e);
		}
	}

	public String getDescription() {
		return "Returns randomly choosen geometries of given type";
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.GEOMETRY) }, new String[] { "gid",
				"the_geom" });
	}

	public String getName() {
		return "RandomGeometry";
	}

	public String getSqlOrder() {
		return "select RandomGeometry('misc|point|linestring|linearring|polygon'[, number]);";
	}

	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[0];
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.STRING,
				Argument.WHOLE_NUMBER) };
	}
}