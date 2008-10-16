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
package org.contrib.gdms.sql.customQuery.spatial.geometry.tin;

import org.contrib.algorithm.triangulation.tin2.Triangle;
import org.contrib.algorithm.triangulation.tin2.Triangulation;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.InvalidTypeException;
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

public class Tin2 implements CustomQuery {

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		SpatialDataSourceDecorator inSds = new SpatialDataSourceDecorator(
				tables[0]);
		try {
			// populate and mesh the Planar Straight-Line Graph using the unique
			// table as input data
			final long t0 = System.currentTimeMillis();

			final Triangulation triangulation;
			if (1 == values.length) {
				triangulation = new Triangulation(inSds, values[0]
						.getAsString());
			} else {
				triangulation = new Triangulation(inSds, values[0]
						.getAsString(), values[1].getAsString());
			}
			inSds.close();
			triangulation.mesh(pm);
			System.err.println("Triangulation process:"
					+ (System.currentTimeMillis() - t0) + " ms");

			// convert the resulting TIN into a data source
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					getMetadata(null));
			for (int triIdx : triangulation.getTriangles().keySet()) {
				final Triangle triangle = triangulation.getTriangles().get(
						triIdx);

				driver.addValues(new Value[] {
						ValueFactory.createValue(triIdx),
						ValueFactory.createValue(triangle.getPolygon()),
						ValueFactory.createValue(triangle.getNeighbour1()),
						ValueFactory.createValue(triangle.getNeighbour2()),
						ValueFactory.createValue(triangle.getNeighbour3())

				});
			}
			return driver;
		} catch (DriverException e) {
			throw new ExecutionException(e);
		}
	}

	public String getDescription() {
		return "This custom query is an implementation of a Constrained Delaunay Triangulation";
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		try {
			return new DefaultMetadata(new Type[] {
					TypeFactory.createType(Type.INT),
					TypeFactory.createType(Type.GEOMETRY,
							new Constraint[] { new GeometryConstraint(
									GeometryConstraint.POLYGON) }),
					TypeFactory.createType(Type.INT),
					TypeFactory.createType(Type.INT),
					TypeFactory.createType(Type.INT) }, new String[] { "gid",
					"the_geom", "neighbour1", "neighbour2", "neighbour3" });
		} catch (InvalidTypeException e) {
			throw new DriverException(
					"InvalidTypeException in metadata instantiation", e);
		}
	}

	public String getName() {
		return "Tin2";
	}

	public String getSqlOrder() {
		return "select Tin2(the_geom [, optionalGid]) from mydatasource";
	}

	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.GEOMETRY };
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] {
				new Arguments(Argument.GEOMETRY, Argument.WHOLE_NUMBER),
				new Arguments(Argument.GEOMETRY) };
	}
}