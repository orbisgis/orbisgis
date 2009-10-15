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
package org.geoalgorithm.orbisgis.grid;

import org.gdms.data.AlreadyClosedException;
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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

public class CreateWebGrid implements CustomQuery {
	private final static double DPI = 2 * Math.PI;
	private final static GeometryFactory GF = new GeometryFactory();

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		try {
			final double deltaR = values[0].getAsDouble();
			final double deltaT = values[1].getAsDouble();

			final SpatialDataSourceDecorator inSds = new SpatialDataSourceDecorator(
					tables[0]);

			// built the driver for the resulting datasource and register it...
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					getMetadata(null));
			final Envelope envelope = inSds.getFullExtent();
			createGrid(driver, envelope, deltaR, deltaT, pm);

			return driver;
		} catch (AlreadyClosedException e) {
			throw new ExecutionException(e);
		} catch (DriverException e) {
			throw new ExecutionException(e);
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		}
	}

	public String getName() {
		return "CreateWebGrid";
	}

	public String getDescription() {
		return "Calculate a regular grid that may be optionnaly oriented";
	}

	public String getSqlOrder() {
		return "select " + getName() + "(4000,1000) from myTable;";
	}

	private void createGrid(final ObjectMemoryDriver driver,
			final Envelope env, double deltaR, double deltaT,
			final IProgressMonitor pm) throws DriverException {
		final double R = 0.5 * Math.sqrt(env.getWidth() * env.getWidth()
				+ env.getHeight() * env.getHeight());
		final Coordinate centroid = env.centre();
		final double perimeter = DPI * R;
		final int Nr = (int) Math.ceil(R / deltaR);
		deltaR = R / Nr; // TODO : to be comment
		final int Nt = (int) Math.ceil(perimeter / (2 * deltaT));
		deltaT = DPI / Nt;

		int gridCellIndex = 0;
		for (int t = 0; t < Nt; t++) {

			if (t / 100 == t / 100.0) {
				if (pm.isCancelled()) {
					break;
				} else {
					pm.progressTo((int) (100 * t / Nt));
				}
			}

			for (int r = 0; r < Nr; r++) {
				createGridCell(driver, centroid, r, t, gridCellIndex, deltaR,
						deltaT);
				gridCellIndex++;
			}
		}
	}

	private void createGridCell(final ObjectMemoryDriver driver,
			final Coordinate centroid, final int r, final int t,
			final int gridCellIndex, final double deltaR, final double deltaT) {
		final Coordinate[] summits = new Coordinate[5];
		summits[0] = polar2cartesian(centroid, r, t, deltaR, deltaT);
		summits[1] = polar2cartesian(centroid, r + 1, t, deltaR, deltaT);
		summits[2] = polar2cartesian(centroid, r + 1, t + 1, deltaR, deltaT);
		summits[3] = polar2cartesian(centroid, r, t + 1, deltaR, deltaT);
		summits[4] = summits[0];
		createGridCell(driver, summits, gridCellIndex);
	}

	private Coordinate polar2cartesian(final Coordinate centroid, final int r,
			final int t, final double deltaR, final double deltaT) {
		final double rr = r * deltaR;
		final double tt = t * deltaT;
		return new Coordinate(centroid.x + rr * Math.cos(tt), centroid.y + rr
				* Math.sin(tt));
	}

	private void createGridCell(final ObjectMemoryDriver driver,
			final Coordinate[] summits, final int gridCellIndex) {
		final LinearRing g = GF.createLinearRing(summits);
		final Geometry gg = GF.createPolygon(g, null);
		driver.addValues(new Value[] { ValueFactory.createValue(gg),
				ValueFactory.createValue(gridCellIndex) });
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.GEOMETRY),
				TypeFactory.createType(Type.INT) }, new String[] { "the_geom",
				"gid" });
	}

	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.GEOMETRY };
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.NUMERIC,
				Argument.NUMERIC) };
	}
}