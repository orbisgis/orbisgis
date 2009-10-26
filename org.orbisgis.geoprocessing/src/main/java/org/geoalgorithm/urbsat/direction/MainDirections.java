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
package org.geoalgorithm.urbsat.direction;

import java.util.HashMap;
import java.util.Map;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.metadata.MetadataUtilities;
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
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

public class MainDirections implements CustomQuery {
	private final static GeometryFactory geometryFactory = new GeometryFactory();

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		try {
			final int nbOfClasses = values[0].getAsInt();
			final double subdivisionOfAngle = Math.PI / nbOfClasses;
			final SpatialDataSourceDecorator inSds = new SpatialDataSourceDecorator(
					tables[0]);

			final Coordinate origin = inSds.getFullExtent().centre();
			// final double radius = 0.5 * Math.sqrt(inSds.getFullExtent()
			// .getWidth()
			// * inSds.getFullExtent().getWidth()
			// + inSds.getFullExtent().getHeight()
			// * inSds.getFullExtent().getHeight());
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					getMetadata(null));
			final Map<Integer, Double> distancesAccumulations = new HashMap<Integer, Double>();

			final long rowCount = inSds.getRowCount();
			for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {

				if (rowIndex / 100 == rowIndex / 100.0) {
					if (pm.isCancelled()) {
						break;
					} else {
						pm.progressTo((int) (100 * rowIndex / rowCount));
					}
				}

				final Geometry g = inSds.getGeometry(rowIndex);
				if (null != g) {
					if (g instanceof LineString) {
						cumulateDistances(distancesAccumulations,
								subdivisionOfAngle, (LineString) g);
					} else if (g instanceof GeometryCollection) {
						cumulateDistances(distancesAccumulations,
								subdivisionOfAngle, (GeometryCollection) g);
					} else {
						throw new ExecutionException(
								"MainDirections only operates on (Multi)LineString spatial field !");
					}
				}
			}

			double sum = 0;
			for (Integer key : distancesAccumulations.keySet()) {
				sum += distancesAccumulations.get(key);
			}

			for (Integer key : distancesAccumulations.keySet()) {
				final double theta = (key + 0.5) * subdivisionOfAngle;
				final double percent = distancesAccumulations.get(key) / sum;
				final LineString edge = geometryFactory
						.createLineString(new Coordinate[] {
								origin,
								polar2cartesian(origin, distancesAccumulations
										.get(key), theta) });

				driver.addValues(new Value[] { ValueFactory.createValue(edge),
						ValueFactory.createValue(theta),
						ValueFactory.createValue(percent) });
			}

			return driver;
		} catch (AlreadyClosedException e) {
			throw new ExecutionException(e);
		} catch (DriverException e) {
			throw new ExecutionException(e);
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		}
	}

	private void cumulateDistances(
			final Map<Integer, Double> distancesAccumulations,
			final double subdivisionOfAngle, final LineString lineString) {
		final Coordinate[] coordinates = lineString.getCoordinates();

		if (1 < coordinates.length) {
			for (int i = 1; i < coordinates.length; i++) {
				final Coordinate polar = cartesian2polar(new Coordinate(
						coordinates[i].x - coordinates[i - 1].x,
						coordinates[i].y - coordinates[i - 1].y));
				final double r = polar.x;
				double theta = polar.y;
				if (theta >= Math.PI) {
					theta = theta - Math.PI;
				}
				final int thetaClass = (int) (theta / subdivisionOfAngle);

				if (distancesAccumulations.containsKey(thetaClass)) {
					distancesAccumulations.put(thetaClass, r
							+ distancesAccumulations.get(thetaClass));
				} else {
					distancesAccumulations.put(thetaClass, r);
				}
			}
		}
	}

	private void cumulateDistances(
			final Map<Integer, Double> distancesAccumulations,
			final double subdivisionOfAngle,
			final GeometryCollection geometryCollection)
			throws ExecutionException {
		final int nbOfGeometries = geometryCollection.getNumGeometries();

		for (int i = 0; i < nbOfGeometries; i++) {
			final Geometry g = geometryCollection.getGeometryN(i);
			if (g instanceof LineString) {
				cumulateDistances(distancesAccumulations, subdivisionOfAngle,
						(LineString) g);
			} else if (g instanceof GeometryCollection) {
				cumulateDistances(distancesAccumulations, subdivisionOfAngle,
						(GeometryCollection) g);
			} else {
				throw new ExecutionException(
						"MainDirections only operates on (Multi)LineString spatial field !");
			}
		}
	}

	public String getName() {
		return "MainDirections";
	}

	public String getDescription() {
		return "Calculate the main directions from a (Mult)LineString dataset";
	}

	public String getSqlOrder() {
		return "select MainDirections(<nbOfDirections>) from myTable;";
	}

	private Coordinate polar2cartesian(final Coordinate origin, final double r,
			final double theta) {
		return new Coordinate(origin.x + r * Math.cos(theta), origin.y + r
				* Math.sin(theta));
	}

	private Coordinate cartesian2polar(final Coordinate coordinate) {
		final double r = Math.sqrt(coordinate.x * coordinate.x + coordinate.y
				* coordinate.y);
		double theta;
		if (0 == coordinate.x) {
			if (0 < coordinate.y) {
				theta = 0.5 * Math.PI;
			} else if (0 > coordinate.y) {
				theta = 1.5 * Math.PI;
			} else {
				theta = Double.NaN;
			}
		} else if (0 > coordinate.x) {
			theta = Math.atan(coordinate.y / coordinate.x) + Math.PI;
		} else {
			if (0 > coordinate.y) {
				theta = Math.atan(coordinate.y / coordinate.x) + 2 * Math.PI;
			} else {
				theta = Math.atan(coordinate.y / coordinate.x);
			}
		}
		return new Coordinate(r, theta);
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.GEOMETRY),
				TypeFactory.createType(Type.DOUBLE),
				TypeFactory.createType(Type.DOUBLE) }, new String[] {
				"the_geom", "theta", "percent" });
	}

	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.GEOMETRY };
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.NUMERIC) };
	}
}