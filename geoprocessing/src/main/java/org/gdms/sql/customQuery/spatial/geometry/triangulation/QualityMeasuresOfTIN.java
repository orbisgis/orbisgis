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
package org.gdms.sql.customQuery.spatial.geometry.triangulation;

import java.util.Arrays;

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
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.function.Arguments;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.Triangle;

public class QualityMeasuresOfTIN implements CustomQuery {
	private final static GeometryFactory gf = new GeometryFactory();

	private class TriangleCircumCircle {
		static final double EPSILON = 1E-4;

		Coordinate centre;
		double radius;
		Envelope envelope;

		TriangleCircumCircle(final Coordinate p0, final Coordinate p1,
				final Coordinate p2) {
			centre = Triangle.circumcentre(p0, p1, p2);
			radius = Math.sqrt((centre.x - p0.x) * (centre.x - p0.x)
					+ (centre.y - p0.y) * (centre.y - p0.y));
			envelope = new Envelope(centre.x - radius, centre.x + radius,
					centre.y - radius, centre.y + radius);
		}
	}

	private class Stat {
		private int numberOfValues = 0;
		private double sumOfValues = 0;
		private double sumOfSquareValues = 0;
		private double minValue = Double.POSITIVE_INFINITY;
		private double maxValue = Double.NEGATIVE_INFINITY;
		private Double average = null;
		private Double stdDeviation = null;

		public void addValue(final double value) {
			sumOfValues += value;
			sumOfSquareValues += value * value;
			minValue = Math.min(minValue, value);
			maxValue = Math.max(maxValue, value);
			numberOfValues++;
		}

		public void addValues(final double[] values) {
			for (double value : values) {
				addValue(value);
			}
		}

		public double[] analyze() {
			if (null == average) {
				average = sumOfValues / numberOfValues;
				stdDeviation = Math.sqrt(sumOfSquareValues / numberOfValues
						- average * average);
			}
			return new double[] { numberOfValues, minValue, maxValue, average,
					stdDeviation };
		}
	}

	private double[] fromCoordinateToAngles(Coordinate[] c) {
		final double[] angles = new double[] {
				Angle.toDegrees(Angle.angleBetween(c[0], c[1], c[2])),
				Angle.toDegrees(Angle.angleBetween(c[1], c[2], c[0])),
				Angle.toDegrees(Angle.angleBetween(c[2], c[0], c[1])) };
		Arrays.sort(angles);
		return angles;
	}

	private double[] fromCoordinateToEdgesLength(Coordinate[] coordinates) {
		Point p = gf.createPoint(coordinates[0]);
		Point q = gf.createPoint(coordinates[1]);
		Point r = gf.createPoint(coordinates[2]);
		final double[] edgesLength = new double[] { p.distance(q),
				q.distance(r), r.distance(p) };
		Arrays.sort(edgesLength);
		return edgesLength;
	}

	@SuppressWarnings("unchecked")
	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		SpatialDataSourceDecorator inSds = new SpatialDataSourceDecorator(
				tables[0]);
		try {
			inSds.open();
			long rowCount = inSds.getRowCount();

			Stat anglesStat = new Stat();
			Stat perimeterStat = new Stat();
			Stat areasStat = new Stat();
			Stat circumRadiusStat = new Stat();
			Stat edgesStat = new Stat();

			for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				final Geometry geometry = inSds.getGeometry(rowIndex);
				if ((geometry instanceof Polygon)
						&& (4 == geometry.getNumPoints())) {
					final Coordinate[] coordinates = geometry.getCoordinates();

					final double[] angles = fromCoordinateToAngles(coordinates);
					anglesStat.addValues(angles);

					final double perimeter = ((Polygon) geometry).getLength();
					perimeterStat.addValue(perimeter);

					final double area = ((Polygon) geometry).getArea();
					areasStat.addValue(area);

					final TriangleCircumCircle tcc = new TriangleCircumCircle(
							coordinates[0], coordinates[1], coordinates[2]);
					circumRadiusStat.addValue(tcc.radius);

					final double[] edgesLength = fromCoordinateToEdgesLength(coordinates);
					edgesStat.addValues(edgesLength);
				}
			}
			inSds.close();

			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					getMetadata(null));

			driver.addValues(new Value[] { ValueFactory.createValue("angles"),
					ValueFactory.createValue(anglesStat.analyze()[1]),
					ValueFactory.createValue(anglesStat.analyze()[2]),
					ValueFactory.createValue(anglesStat.analyze()[3]),
					ValueFactory.createValue(anglesStat.analyze()[4]) });

			driver.addValues(new Value[] {
					ValueFactory.createValue("perimeters"),
					ValueFactory.createValue(perimeterStat.analyze()[1]),
					ValueFactory.createValue(perimeterStat.analyze()[2]),
					ValueFactory.createValue(perimeterStat.analyze()[3]),
					ValueFactory.createValue(perimeterStat.analyze()[4]) });

			driver.addValues(new Value[] { ValueFactory.createValue("areas"),
					ValueFactory.createValue(areasStat.analyze()[1]),
					ValueFactory.createValue(areasStat.analyze()[2]),
					ValueFactory.createValue(areasStat.analyze()[3]),
					ValueFactory.createValue(areasStat.analyze()[4]) });

			driver.addValues(new Value[] {
					ValueFactory.createValue("circum radius"),
					ValueFactory.createValue(circumRadiusStat.analyze()[1]),
					ValueFactory.createValue(circumRadiusStat.analyze()[2]),
					ValueFactory.createValue(circumRadiusStat.analyze()[3]),
					ValueFactory.createValue(circumRadiusStat.analyze()[4]) });

			driver.addValues(new Value[] {
					ValueFactory.createValue("edges length"),
					ValueFactory.createValue(edgesStat.analyze()[1]),
					ValueFactory.createValue(edgesStat.analyze()[2]),
					ValueFactory.createValue(edgesStat.analyze()[3]),
					ValueFactory.createValue(edgesStat.analyze()[4]) });

			return driver;
		} catch (DriverException e) {
			throw new ExecutionException(e);
		}
	}

	public String getDescription() {
		return "This custom query operates some quality measures on a set of triangles";
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.STRING),
				TypeFactory.createType(Type.DOUBLE),
				TypeFactory.createType(Type.DOUBLE),
				TypeFactory.createType(Type.DOUBLE),
				TypeFactory.createType(Type.DOUBLE) }, new String[] { "label",
				"min", "max", "average", "standard deviation" });
	}

	public String getName() {
		return "QualityMeasuresOfTIN";
	}

	public String getSqlOrder() {
		return "select QualityMeasuresOfTIN() from mytin;";
	}

	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.GEOMETRY };
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments() };
	}
}