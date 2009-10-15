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
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.strategies.DiskBufferDriver;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

public class BigCreateGrid implements CustomQuery {
	private final static GeometryFactory geometryFactory = new GeometryFactory();

	private boolean isAnOrientedGrid;

	private double deltaX;
	private double deltaY;
	private double cosAngle;
	private double sinAngle;
	private double cosInvAngle;
	private double sinInvAngle;
	private double llcX;
	private double llcY;

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		try {
			deltaX = values[0].getAsDouble();
			deltaY = values[1].getAsDouble();
			final SpatialDataSourceDecorator inSds = new SpatialDataSourceDecorator(
					tables[0]);

			// built the driver for the resulting datasource and register it...
			DiskBufferDriver driver = new DiskBufferDriver(dsf,
					getMetadata(null));

			if (3 == values.length) {
				isAnOrientedGrid = true;
				final double angle = (values[2].getAsDouble() * Math.PI) / 180;
				createGrid(driver, prepareOrientedGrid(inSds, angle), pm);
			} else {
				isAnOrientedGrid = false;
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
		return "BigCreateGrid";
	}

	public String getDescription() {
		return "Calculate a regular grid that may be optionnaly oriented";
	}

	public String getSqlOrder() {
		return "select " + getName() + "(4000,1000[,15]) from myTable;";
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
				final Coordinate[] summits = new Coordinate[5];
				summits[0] = invTranslateAndRotate(x, y);
				summits[1] = invTranslateAndRotate(x + deltaX, y);
				summits[2] = invTranslateAndRotate(x + deltaX, y + deltaY);
				summits[3] = invTranslateAndRotate(x, y + deltaY);
				summits[4] = invTranslateAndRotate(x, y);
				createGridCell(driver, summits, gridCellIndex);
			}
		}
	}

	private Envelope prepareOrientedGrid(
			final SpatialDataSourceDecorator inSds, final double angle)
			throws DriverException {
		double xMin = Double.MAX_VALUE;
		double xMax = Double.MIN_VALUE;
		double yMin = Double.MAX_VALUE;
		double yMax = Double.MIN_VALUE;

		cosAngle = Math.cos(angle);
		sinAngle = Math.sin(angle);
		cosInvAngle = Math.cos(-angle);
		sinInvAngle = Math.sin(-angle);
		final Envelope env = inSds.getFullExtent();
		llcX = env.getMinX();
		llcY = env.getMinY();

		final int rowCount = (int) inSds.getRowCount();
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			final Geometry g = inSds.getGeometry(rowIndex);
			final Coordinate[] allCoordinates = g.getCoordinates();
			for (Coordinate inCoordinate : allCoordinates) {
				final Coordinate outCoordinate = translateAndRotate(inCoordinate);
				if (outCoordinate.x < xMin) {
					xMin = outCoordinate.x;
				}
				if (outCoordinate.x > xMax) {
					xMax = outCoordinate.x;
				}
				if (outCoordinate.y < yMin) {
					yMin = outCoordinate.y;
				}
				if (outCoordinate.y > yMax) {
					yMax = outCoordinate.y;
				}
			}
		}
		return new Envelope(xMin, xMax, yMin, yMax);
	}

	private final Coordinate translateAndRotate(final Coordinate inCoordinate) {
		// do the rotation after the translation in the local coordinates system
		final double x = inCoordinate.x - llcX;
		final double y = inCoordinate.y - llcY;
		return new Coordinate(cosAngle * x - sinAngle * y, sinAngle * x
				+ cosAngle * y, inCoordinate.z);
	}

	private final Coordinate invTranslateAndRotate(final double x,
			final double y) {
		if (isAnOrientedGrid) {
			// do the (reverse) translation after the (reverse) rotation
			final double localX = cosInvAngle * x - sinInvAngle * y;
			final double localY = sinInvAngle * x + cosInvAngle * y;
			return new Coordinate(localX + llcX, localY + llcY);
		} else {
			return new Coordinate(x, y);
		}
	}

	private void createGridCell(final DiskBufferDriver driver,
			final Coordinate[] summits, final int gridCellIndex)
			throws DriverException {
		final LinearRing g = geometryFactory.createLinearRing(summits);
		final Geometry gg = geometryFactory.createPolygon(g, null);
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
		return new Arguments[] {
				new Arguments(Argument.NUMERIC, Argument.NUMERIC),
				new Arguments(Argument.NUMERIC, Argument.NUMERIC,
						Argument.NUMERIC) };
	}
}