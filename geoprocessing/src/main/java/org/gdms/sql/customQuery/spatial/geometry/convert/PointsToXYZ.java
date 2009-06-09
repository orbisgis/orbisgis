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
package org.gdms.sql.customQuery.spatial.geometry.convert;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class PointsToXYZ implements CustomQuery {
	public String getName() {
		return "PointsToXYZ";
	}

	public String getSqlOrder() {
		return "select PointsToXYZ(the_geom [, a_numeric_field_name]) from myTable;";
	}

	public String getDescription() {
		return "Extract X Y Z coordinates from a point. By default the z value corresponding to the geometry, but"
				+ "the user can choose corresponding numeric field in the table.";
	}

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		try {
			final SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
					tables[0]);
			sds.open();

			final String spatialFieldName = values[0].toString();
			sds.setDefaultGeometry(spatialFieldName);

			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					getMetadata(null));
			final long rowCount = sds.getRowCount();

			if (2 == values.length) {
				// the height field name is explicitly provided
				final int heightFieldIndex = sds.getFieldIndexByName(values[1]
						.toString());

				for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {

					if (rowIndex / 100 == rowIndex / 100.0) {
						if (pm.isCancelled()) {
							break;
						} else {
							pm.progressTo((int) (100 * rowIndex / rowCount));
						}
					}

					final Geometry geometry = sds.getGeometry(rowIndex);
					if (geometry instanceof Point) {
						final Point p = (Point) geometry;
						final double x = p.getCoordinate().x;
						final double y = p.getCoordinate().y;
						final double z = sds.getFieldValue(rowIndex,
								heightFieldIndex).getAsDouble();
						driver.addValues(new Value[] {
								ValueFactory.createValue(x),
								ValueFactory.createValue(y),
								ValueFactory.createValue(z) });
					}
				}
			} else {
				// no height field name is provided, the default z value is
				// extracted using the geometry itself
				for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {

					if (rowIndex / 100 == rowIndex / 100.0) {
						if (pm.isCancelled()) {
							break;
						} else {
							pm.progressTo((int) (100 * rowIndex / rowCount));
						}
					}

					final Geometry geometry = sds.getGeometry(rowIndex);
					if (geometry instanceof Point) {
						final Point p = (Point) geometry;
						final double x = p.getCoordinate().x;
						final double y = p.getCoordinate().y;
						final double z = p.getCoordinate().z;
						driver.addValues(new Value[] {
								ValueFactory.createValue(x),
								ValueFactory.createValue(y),
								ValueFactory.createValue(z) });
					}
				}
			}
			sds.close();
			return driver;
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		} catch (DriverException e) {
			throw new ExecutionException(e);
		}
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.DOUBLE),
				TypeFactory.createType(Type.DOUBLE),
				TypeFactory.createType(Type.DOUBLE) }, new String[] { "x", "y",
				"z" });
	}

	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.GEOMETRY };
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] {
				new Arguments(Argument.GEOMETRY, Argument.NUMERIC),
				new Arguments(Argument.GEOMETRY) };
	}
}