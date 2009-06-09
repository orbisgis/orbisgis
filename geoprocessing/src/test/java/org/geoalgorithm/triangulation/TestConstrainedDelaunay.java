package org.geoalgorithm.triangulation;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.jdelaunay.delaunay.Delaunay;
import org.jdelaunay.delaunay.DelaunayError;
import org.jdelaunay.delaunay.MyEdge;
import org.jdelaunay.delaunay.MyMesh;
import org.jdelaunay.delaunay.MyPoint;
import org.jdelaunay.delaunay.MyTriangle;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class TestConstrainedDelaunay {

	public static DataSourceFactory dsf = new DataSourceFactory();

	// public static String path =
	// "/home/bocher/data/BD_Topo_Nantes_shp/BATI_surface.shp";
	//public static String path = "data/chezinecourbe.shp";

 //public static String path = "data/courbesZ.shp";


	 public static String path = "data/courbes3D.gdms";


	// public static String path = "data/small_courbes.shp";

	private static MyMesh aMesh;

	private static ArrayList<MyPoint> points;

	private static LinkedList<MyEdge> breaklines;

	// public static String path = "data/courbesdem10_zone.shp";

	// public static String path = "data/multilinestring2d.shp";

	// public static String path = "data/cantons.shp";

	/**
	 * @param args
	 * @throws DriverException
	 * @throws DataSourceCreationException
	 * @throws DriverLoadException
	 * @throws DelaunayError
	 */
	public static void main(String[] args) throws DriverLoadException,
			DataSourceCreationException, DriverException, DelaunayError {

		long startComputation = Calendar.getInstance().getTime().getTime();

		points = new ArrayList<MyPoint>();

		breaklines = new LinkedList<MyEdge>();

		DataSource mydata = dsf.getDataSource(new File(path));

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(mydata);
		sds.open();

		System.out.println("Start prepare data ");
		int gid = -1;

		// int zField = sds.getFieldIndexByName("altitude");

		for (int i = 0; i < sds.getRowCount(); i++) {

			Geometry geom = sds.getGeometry(i);

			// double zValue = sds.getFieldValue(i, zField).getAsDouble();
			for (int j = 0; j < geom.getNumGeometries(); j++) {

				Geometry subGeom = geom.getGeometryN(j);

				if (subGeom.getDimension() == 0) {

					for (int k = 0; k < subGeom.getCoordinates().length; k++) {

						Coordinate coord = subGeom.getCoordinates()[k];
						gid++;
						points.add(new MyPoint(coord.x, coord.y, coord.z, gid));
					}

				}

				else if (subGeom instanceof LineString) {

					LineString lineString = (LineString) subGeom;

					gid++;

					getBreaklines(lineString, gid);

				}

				else if (subGeom instanceof Polygon) {

					Polygon p = (Polygon) subGeom;

					gid++;

					getBreaklines(p.getExteriorRing(), gid);
				}

			}

		}

		sds.close();

		System.out
				.println("End prepare data "
						+ (Calendar.getInstance().getTime().getTime() - startComputation));

		System.out.println("Nb : " + breaklines.size());

		aMesh = new MyMesh();
		aMesh.setPointsRef(points);
		aMesh.setEdges(breaklines);

		Delaunay delaunay = new Delaunay(aMesh);

		delaunay.setVerbose(true);
		aMesh.setStart();

		// process triangularization
		delaunay.processDelaunay();

		delaunay.removeFlatTriangles();

		// Refine Mesh
		delaunay.setRefinment(Delaunay.refinement_minAngle);
		delaunay.refineMesh();

		/*MyDrawing aff = new MyDrawing();
		aff.setTitle("Mesh");
		aff.add(aMesh);
		aMesh.setAffiche(aff);*/

		 saveAll(delaunay);

		aMesh.setEnd();
		System.out.println("Temps de triangulation " + aMesh.getDuration());

	}

	private static void getBreaklines(LineString lineString, int gid) {
		Coordinate[] coords = lineString.getCoordinates();

		MyPoint p1 = new MyPoint(coords[0].x, coords[0].y, coords[0].z, gid);
		MyPoint p0;
		points.add(p1);

		for (int k = 1; k < coords.length; k++) {

			p0 = p1;
			gid++;
			p1 = new MyPoint(coords[k].x, coords[k].y, coords[k].z, gid);

			points.add(p1);
			MyEdge edge = new MyEdge(p0, p1);
			breaklines.add(edge);
		}

	}

	public static void saveAll(Delaunay delaunay) throws DriverException {

		getResults(delaunay);

		File gdmsFile = new File("/tmp/tinedges.gdms");
		gdmsFile.delete();
		dsf.getSourceManager().register("edges", gdmsFile);

		DataSource ds = dsf.getDataSource(driverEdges);
		ds.open();
		dsf.saveContents("edges", ds);
		ds.close();

		gdmsFile = new File("/tmp/tinfaces.gdms");
		gdmsFile.delete();
		dsf.getSourceManager().register("faces", gdmsFile);

		ds = dsf.getDataSource(driverFaces);
		ds.open();
		dsf.saveContents("faces", ds);
		ds.close();

		gdmsFile = new File("/tmp/tinnodes.gdms");
		gdmsFile.delete();
		dsf.getSourceManager().register("nodes", gdmsFile);

		ds = dsf.getDataSource(driverNodes);
		ds.open();
		dsf.saveContents("nodes", ds);
		ds.close();

	}

	private static ObjectMemoryDriver driverNodes;

	private static ObjectMemoryDriver driverEdges;

	private static ObjectMemoryDriver driverFaces;

	public static void getResults(Delaunay delaunay) throws DriverException {

		Metadata metadata = new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.STRING),
				TypeFactory.createType(Type.GEOMETRY) }, new String[] { "gid",
				"start_n", "end_n", "left_t", "right_t", "type", "the_geom" });

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
