/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Antoine GOURLAY, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.gdms.sql.function.spatial.geometry.other;

import java.util.HashMap;
import java.util.Map;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.SQLDataSourceFactory;
import org.gdms.sql.function.FunctionException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.generic.GenericObjectDriver;
import org.gdms.sql.function.FunctionSignature;
import org.orbisgis.progress.ProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.driver.DriverUtilities;
import org.gdms.driver.DataSet;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.table.AbstractTableFunction;
import org.gdms.sql.function.table.TableArgument;
import org.gdms.sql.function.table.TableDefinition;
import org.gdms.sql.function.table.TableFunctionSignature;

public final class ST_MainDirections extends AbstractTableFunction {
	private static final GeometryFactory geometryFactory = new GeometryFactory();

        @Override
	public DataSet evaluate(SQLDataSourceFactory dsf, DataSet[] tables,
			Value[] values, ProgressMonitor pm) throws FunctionException {
		try {
			final int nbOfClasses = values[0].getAsInt();
			final double subdivisionOfAngle = Math.PI / nbOfClasses;
			final DataSet inSds = tables[0];

                        final long rowCount = inSds.getRowCount();
                        pm.startTask("Computing", rowCount);

			final Coordinate origin = DriverUtilities.getFullExtent(inSds).centre();
			// final double radius = 0.5 * Math.sqrt(inSds.getFullExtent()
			// .getWidth()
			// * inSds.getFullExtent().getWidth()
			// + inSds.getFullExtent().getHeight()
			// * inSds.getFullExtent().getHeight());
			final GenericObjectDriver driver = new GenericObjectDriver(
					getMetadata(null));
			final Map<Integer, Double> distancesAccumulations = new HashMap<Integer, Double>();

                        final int spatialFieldIndex = MetadataUtilities.getSpatialFieldIndex(inSds.getMetadata());
			for (long i = 0; i < rowCount; i++) {

				if (i >= 100 && i % 100 == 0) {
					if (pm.isCancelled()) {
						break;
					} else {
						pm.progressTo(i);
					}
				}

				final Geometry g = inSds.getFieldValue(i, spatialFieldIndex).getAsGeometry();
				if (null != g) {
					if (g instanceof LineString) {
						cumulateDistances(distancesAccumulations,
								subdivisionOfAngle, (LineString) g);
					} else if (g instanceof GeometryCollection) {
						cumulateDistances(distancesAccumulations,
								subdivisionOfAngle, (GeometryCollection) g);
					} else {
						throw new FunctionException(
								"MainDirections only operates on (Multi)LineString spatial field !");
					}
				}
			}
                        pm.progressTo(rowCount);
                        pm.endTask();

			double sum = 0;
			for (Double value : distancesAccumulations.values()) {
				sum += value;
			}

			for (Map.Entry<Integer, Double> entry : distancesAccumulations.entrySet()) {
				final double theta = (entry.getKey() + 0.5) * subdivisionOfAngle;
				final double percent = entry.getValue() / sum;
				final LineString edge = geometryFactory
						.createLineString(new Coordinate[] {
								origin,
								polar2cartesian(origin, entry.getValue(), theta) });

				driver.addValues(new Value[] { ValueFactory.createValue(edge),
						ValueFactory.createValue(theta),
						ValueFactory.createValue(percent) });
			}

			return driver.getTable(DriverManager.DEFAULT_SINGLE_TABLE_NAME);
		} catch (AlreadyClosedException e) {
			throw new FunctionException(e);
		} catch (DriverException e) {
			throw new FunctionException(e);
		} catch (DriverLoadException e) {
			throw new FunctionException(e);
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
					theta -= Math.PI;
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
			throws FunctionException {
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
				throw new FunctionException(
						"MainDirections only operates on (Multi)LineString spatial field !");
			}
		}
	}

        @Override
	public String getName() {
		return "ST_MainDirections";
	}

        @Override
	public String getDescription() {
		return "Calculate the main directions from a (Mult)LineString dataset";
	}

        @Override
	public String getSqlOrder() {
		return "select ST_MainDirections(<nbOfDirections>) from myTable;";
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

        @Override
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

	@Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[] {
                        new TableFunctionSignature(TableDefinition.GEOMETRY,
                                new TableArgument(TableDefinition.GEOMETRY),
                                ScalarArgument.DOUBLE)
                };
        }
}