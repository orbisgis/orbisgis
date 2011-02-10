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
package org.gdms.sql.customQuery.spatial.geometry.create;

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
import org.gdms.driver.DiskBufferDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class ST_CreatePointsGrid implements CustomQuery {
	private final static GeometryFactory geometryFactory = new GeometryFactory();

	private double deltaX;
	private double deltaY;

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		try {
			deltaX = values[0].getAsDouble();
			deltaY = values[1].getAsDouble();			
			final SpatialDataSourceDecorator inSds = new SpatialDataSourceDecorator(
					tables[0]);

			// built the driver for the resulting datasource and register it...
			final DiskBufferDriver driver = new DiskBufferDriver(dsf,
					getMetadata(null));
			if (3 == values.length) {
				createGrid(driver, inSds.getFullExtent(), pm);

			} else {
				createGrid(driver, inSds.getFullExtent(), pm);

			}
			
			driver.writingFinished();
			
			return driver;
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		} catch (DriverException e) {
			throw new ExecutionException(e);
		}
	}

	public String getName() {
		return "ST_CreatePointsGrid";
	}

	public String getDescription() {
		return "Calculate a regular points grid. Use a geometry to exclude some area.";
	}

	public String getSqlOrder() {
		return "select " + getName() + "(4000,1000, [the_geom]) from myTable;";
	}

	private void createGrid(final DiskBufferDriver driver, final Envelope env,
			final IProgressMonitor pm) throws DriverException {
		final int nbX = new Double(Math.ceil((env.getMaxX() - env.getMinX())
				/ deltaX)).intValue();
		final int nbY = new Double(Math.ceil((env.getMaxY() - env.getMinY())
				/ deltaY)).intValue();
		int gridCellIndex = 0;
		double x = env.centre().x - (deltaX * nbX) / 2;
		for (int i = 0; i < nbX; i++, x += deltaX) {

			if (i / 100 == i / 100.0) {
				if (pm.isCancelled()) {
					break;
				} else {
					pm.progressTo((int) (100 * i / nbX));
				}
			}

			double y = env.centre().y - (deltaY * nbY) / 2;
			for (int j = 0; j < nbY; j++, y += deltaY) {
				gridCellIndex++;
				Geometry g = geometryFactory.createPoint(new Coordinate(x, y));
				driver.addValues(new Value[] { ValueFactory.createValue(g),
						ValueFactory.createValue(gridCellIndex) });
			}
		}

	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.GEOMETRY),
				TypeFactory.createType(Type.INT) }, new String[] { "the_geom",
				"gid" });
	}

	public TableDefinition[] getTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.GEOMETRY };
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] {
				new Arguments(Argument.NUMERIC, Argument.NUMERIC),
				new Arguments(Argument.NUMERIC, Argument.NUMERIC,
						Argument.GEOMETRY) };
	}
}