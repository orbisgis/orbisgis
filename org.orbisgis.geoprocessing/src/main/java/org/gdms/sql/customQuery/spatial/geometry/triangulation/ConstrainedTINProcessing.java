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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.geoalgorithm.jts.operation.UniqueSegmentsExtracter;
import org.jdelaunay.delaunay.Delaunay;
import org.jdelaunay.delaunay.DelaunayError;
import org.jdelaunay.delaunay.MyEdge;
import org.jdelaunay.delaunay.MyMesh;
import org.jdelaunay.delaunay.MyPoint;
import org.jdelaunay.delaunay.MyTriangle;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class ConstrainedTINProcessing implements CustomQuery {

	private ObjectMemoryDriver driverNodes;
	private ObjectMemoryDriver driverEdges;
	private ObjectMemoryDriver driverFaces;
	private ArrayList<MyPoint> points;

	LinkedList<MyEdge> breaklines;

	public String getName() {
		return "CDT";
	}

	public String getSqlOrder() {
		return "select CDT(a.the_geom, a.gid, b.the_geom,b.gid) from points as a, lines as b;";
	}

	public String getDescription() {
		return "Build a constrained TIN";
	}

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		try {
			final SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
					tables[0]);
			sds.open();

			final String spatialFieldNamePoints = values[0].toString();
			sds.setDefaultGeometry(spatialFieldNamePoints);

			if (values.length == 2) {
				String fieldValue = values[1].toString();
				getData(sds, sds.getFieldIndexByName(fieldValue));

			} else {
				getData(sds);
			}

			sds.close();

			MyMesh aMesh = new MyMesh();

			aMesh.setPoints(points);
			Delaunay delaunay = new Delaunay(aMesh);

			try {
				// process triangularization
				delaunay.processDelaunay();
				// Refine Mesh
				delaunay.refineMesh();

			} catch (DelaunayError e) {
				e.printStackTrace();
			}

			getResults(delaunay);

			dsf.getSourceManager().register(
					dsf.getSourceManager().getUniqueName("tinedges"),
					driverEdges);

			dsf.getSourceManager().register(
					dsf.getSourceManager().getUniqueName("tinnodes"),
					driverNodes);

			dsf.getSourceManager().register(
					dsf.getSourceManager().getUniqueName("tinfaces"),
					driverFaces);

			return null;
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		} catch (DriverException e) {
			throw new ExecutionException(e);
		}
	}

	private void getData(SpatialDataSourceDecorator sds, int fieldIndex)
			throws IncompatibleTypesException, DriverException {

		ArrayList<MyPoint> points = null;

		breaklines = new LinkedList<MyEdge>();

		for (int i = 0; i < sds.getRowCount(); i++) {

			Geometry geom = sds.getGeometry(i);
			String type = sds.getFieldValue(i, fieldIndex).getAsString();
			for (int j = 0; j < geom.getNumGeometries(); j++) {

				Geometry subGeom = geom.getGeometryN(j);

				if (subGeom.getDimension() == 0) {

					for (int k = 0; k < subGeom.getCoordinates().length; k++) {
						Coordinate coord = subGeom.getCoordinates()[k];

						points
								.add(new MyPoint(coord.x, coord.y, coord.z,
										type));

					}

				}

				else {

					UniqueSegmentsExtracter uniqueSegmentsExtracter = new UniqueSegmentsExtracter(
							geom);
					List<LineString> list = uniqueSegmentsExtracter
							.getSegmentAsLineString();

					LineString ls = (LineString) list.get(0);
					Coordinate coord0 = ls.getCoordinates()[0];
					MyPoint p1 = new MyPoint(coord0.x, coord0.y, coord0.z, type);
					MyPoint p0;
					points.add(p1);

					for (int l = 0; l < list.size(); l++) {

						ls = (LineString) list.get(l);

						Coordinate coord1 = ls.getCoordinates()[1];

						p0 = p1;
						p1 = new MyPoint(coord1.x, coord1.y, coord1.z, type);

						points.add(p1);
						MyEdge edge = new MyEdge(p0, p1, type);
						breaklines.add(edge);

					}

				}

			}

		}
	}

	private void getData(SpatialDataSourceDecorator sds)
			throws IncompatibleTypesException, DriverException {

		points = new ArrayList<MyPoint>();

		breaklines = new LinkedList<MyEdge>();

		for (int i = 0; i < sds.getRowCount(); i++) {

			Geometry geom = sds.getGeometry(i);
			for (int j = 0; j < geom.getNumGeometries(); j++) {

				Geometry subGeom = geom.getGeometryN(j);

				if (subGeom.getDimension() == 0) {

					for (int k = 0; k < subGeom.getCoordinates().length; k++) {
						Coordinate coord = subGeom.getCoordinates()[k];

						points.add(new MyPoint(coord.x, coord.y, coord.z));

					}

				}

				else {

					UniqueSegmentsExtracter uniqueSegmentsExtracter = new UniqueSegmentsExtracter(
							geom);
					List<LineString> list = uniqueSegmentsExtracter
							.getSegmentAsLineString();

					LineString ls = (LineString) list.get(0);
					Coordinate coord0 = ls.getCoordinates()[0];
					MyPoint p1 = new MyPoint(coord0.x, coord0.y, coord0.z);
					MyPoint p0;
					points.add(p1);

					for (int l = 0; l < list.size(); l++) {

						ls = (LineString) list.get(l);

						Coordinate coord1 = ls.getCoordinates()[1];

						p0 = p1;
						p1 = new MyPoint(coord1.x, coord1.y, coord1.z);

						points.add(p1);
						MyEdge edge = new MyEdge(p0, p1);
						breaklines.add(edge);

					}

				}

			}

		}
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return null;
	}

	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.GEOMETRY };
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.GEOMETRY),
				new Arguments(Argument.GEOMETRY, Argument.STRING) };
	}

	public void getResults(Delaunay delaunay) throws DriverException {

		Metadata metadata = new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.STRING),
				TypeFactory.createType(Type.GEOMETRY) }, new String[] { "gid",
				"start_node", "end_node", "left_triangle", "right_triangle",
				"type", "the_geom" });

		driverEdges = new ObjectMemoryDriver(metadata);

		GeometryFactory gf = new GeometryFactory();

		ArrayList<MyPoint> points = delaunay.getMesh().getPoints();

		ArrayList<MyEdge> edges = delaunay.getMesh().getEdges();

		LinkedList<MyTriangle> triangles = delaunay.getMesh().getTriangles();

		for (int i = 0; i < edges.size(); i++) {

			MyEdge edge = edges.get(i);

			MyPoint p1 = edge.point(0);

			MyPoint p2 = edge.point(1);

			Coordinate[] coords = new Coordinate[] {
					new Coordinate(p1.getX(), p1.getY(), p1.getZ()),
					new Coordinate(p2.getX(), p2.getY(), p2.getZ()) };

			Geometry line = gf.createLineString(coords);



			int startIdPoints = edge.getStart().getGid();

			int endIdPoints = edge.getEnd().getGid();

			int leftId = -1;
			if (edge.getLeft() == null) {

			} else {
				leftId = edge.getLeft().getGid();
			}

			int rightId = -1;
			if (edge.getRight() == null) {

			} else {
				rightId = edge.getRight().getGid();
			}

			String edgeType = edge.getEdgeType();

			driverEdges.addValues(new Value[] {
					ValueFactory.createValue(edge.getGid()),
					ValueFactory.createValue(startIdPoints),
					ValueFactory.createValue(endIdPoints),
					ValueFactory.createValue(leftId),
					ValueFactory.createValue(rightId),
					ValueFactory.createValue(edgeType),
					ValueFactory.createValue(line) });

		}

		metadata = new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.STRING),
				TypeFactory.createType(Type.GEOMETRY) }, new String[] { "gid",
				"type", "the_geom" });

		driverNodes = new ObjectMemoryDriver(metadata);

		for (MyPoint aPoint : points) {
			int id = aPoint.getGid();

			Point point = gf.createPoint(new Coordinate(aPoint.x, aPoint.y,
					aPoint.z));

			driverNodes.addValues(new Value[] { ValueFactory.createValue(id),
					ValueFactory.createValue(aPoint.type),
					ValueFactory.createValue(point) });

		}

		metadata = new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.STRING),
				TypeFactory.createType(Type.GEOMETRY) }, new String[] { "gid",
				"type", "the_geom", "edge1", "edge2", "edge3" });

		driverFaces = new ObjectMemoryDriver(metadata);

		for (MyTriangle aTriangle : triangles) {

			MyPoint[] pts = aTriangle.points;

			Coordinate[] coords = new Coordinate[] {
					new Coordinate(pts[0].x, pts[0].y, pts[0].z),
					new Coordinate(pts[1].x, pts[1].y, pts[1].z),
					new Coordinate(pts[2].x, pts[2].y, pts[2].z),
					new Coordinate(pts[0].x, pts[0].y, pts[0].z) };

			Polygon polygon = gf.createPolygon(gf.createLinearRing(coords),
					null);

			driverFaces.addValues(new Value[] {
					ValueFactory.createValue(aTriangle.getGid()),
					ValueFactory.createValue(""),
					ValueFactory.createValue(polygon),
					ValueFactory.createValue(aTriangle.edge(0).getGid()),
					ValueFactory.createValue(aTriangle.edge(1).getGid()),
					ValueFactory.createValue(aTriangle.edge(2).getGid()) });

		}

	}
}