package org.geoalgorithm.orbisgis.topology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.IntersectionMatrix;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.operation.linemerge.LineMerger;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;
import com.vividsolutions.jts.operation.union.UnaryUnionOp;

public class NetworkGraph {

	// private SpatialDataSourceDecorator sds;

	GeometryFactory gf = new GeometryFactory();

	public Collection edges;

	private DataSourceFactory dsf = new DataSourceFactory();

	private static String ID = "id";

	private static String LEFT_FACE = "left_polygon";

	private static String RIGHT_FACE = "right_polygon";

	private static String INITIAL_NODE = "start_node";

	private static String FINAL_NODE = "end_node";

	public final static Integer MINUS_ONE = new Integer(-1);

	public NetworkGraph() {

	}

	// ************************************************
	// Create edge layer/
	// ************************************************
	public ObjectMemoryDriver createEdges(SpatialDataSourceDecorator sds, IProgressMonitor pm) {
		// Schema edge
		try {
			pm.startTask("Create edges graph");

			sds.open();

			DefaultMetadata edgeMedata = new DefaultMetadata(new Type[] {
					TypeFactory.createType(Type.GEOMETRY,
							new Constraint[] { new GeometryConstraint(
									GeometryConstraint.LINESTRING) }),
					TypeFactory.createType(Type.INT) }, new String[] {
					"the_geom", ID });

			ObjectMemoryDriver edgeDriver = new ObjectMemoryDriver(edgeMedata);

			// Get linear elements from all geometries in the layer

			Geometry geom = getLines(sds);
			GeometryCollection gc = geom instanceof GeometryCollection ? (GeometryCollection) geom
					: gf.createGeometryCollection(new Geometry[] { geom });

			// Create the edge layer by merging lines between 3+ order nodes
			// (Merged lines are multilines)
			LineMerger lineMerger = new LineMerger();
			for (int i = 0; i < gc.getNumGeometries(); i++) {
				lineMerger.add(gc.getGeometryN(i));
			}
			sds.close();
			edges = lineMerger.getMergedLineStrings();
			int no = 1;
			for (Iterator it = edges.iterator(); it.hasNext();) {
				edgeDriver.addValues(new Value[] {
						ValueFactory.createValue((Geometry) it.next()),
						ValueFactory.createValue(new Integer(no++)) });

			}

			return edgeDriver;
		} catch (DriverException e) {
			e.printStackTrace();
		}
		return null;
	}

	// ************************************************
	// extract lines from a feature collection
	// ************************************************
	public Geometry getLines(SpatialDataSourceDecorator sds)
			throws DriverException {

		LineNoder linenoder = new LineNoder(sds);
		Collection lines = linenoder.getLines();

		final Geometry nodedGeom = linenoder.getNodeLines((List) lines);
		final Collection<Geometry> nodedLines = linenoder.toLines(nodedGeom);

		return UnaryUnionOp.union(nodedLines);

	}

	// ************************************************
	// Create node layer
	// ************************************************
	public ObjectMemoryDriver createNodes(ObjectMemoryDriver omedges)
			throws DriverException, NonEditableDataSourceException {

		DefaultMetadata edgeMedata = new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.GEOMETRY),
				TypeFactory.createType(Type.INT) }, new String[] { "the_geom",
				ID });

		ObjectMemoryDriver nodeDriver = new ObjectMemoryDriver(edgeMedata);

		SpatialDataSourceDecorator sdsEdges = new SpatialDataSourceDecorator(
				dsf.getDataSource(omedges));

		// Create the node Layer
		Map nodes = new HashMap();
		int no = 1;

		sdsEdges.open();
		long rowCount = sdsEdges.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			Coordinate[] cc = sdsEdges.getGeometry(i).getCoordinates();
			nodes.put(cc[0], new MyFeature(no++, gf.createPoint(cc[0])));
			nodes.put(cc[cc.length - 1], new MyFeature((no++) - 1, gf
					.createPoint(cc[cc.length - 1])));

		}

		no = 0;

		for (Iterator it = nodes.values().iterator(); it.hasNext();) {

			no++;
			MyFeature myFeature = (MyFeature) it.next();
			Point geom = (Point) myFeature.getGeometry();
			nodeDriver.addValues(new Value[] { ValueFactory.createValue(geom),
					ValueFactory.createValue(new Integer(no)) });

			nodes.put(geom.getCoordinate(), new MyFeature(no, geom));

		}

		sdsEdges.addField(INITIAL_NODE, TypeFactory.createType(Type.INT));

		sdsEdges.addField(FINAL_NODE, TypeFactory.createType(Type.INT));

		sdsEdges.commit();


		for (int i = 0; i < rowCount; i++) {

			Geometry currentGeom = sdsEdges.getGeometry(i);

			Coordinate[] cc = currentGeom.getCoordinates();

			sdsEdges.setFieldValue(i, sdsEdges
					.getFieldIndexByName(INITIAL_NODE), ValueFactory
					.createValue(((MyFeature) nodes.get(cc[0])).getValue()));
			sdsEdges.setFieldValue(i, sdsEdges.getFieldIndexByName(FINAL_NODE),
					ValueFactory.createValue(((MyFeature) nodes
							.get(cc[cc.length - 1])).getValue()));

		}
		sdsEdges.commit();
		sdsEdges.close();

		return nodeDriver;
	}


	// Returns the index of c in the geometry g or -1 if c is not a vertex of g
	private int getIndex(Coordinate c, Geometry g) {
		Coordinate[] cc = g.getCoordinates();
		for (int i = 0; i < cc.length; i++) {
			if (cc[i].equals(c))
				return i;
		}
		return -1;
	}

	/**
	 * A quick search for geometries, using an envelope comparison.
	 *
	 * @param envelope
	 *            the envelope to query against
	 * @return geometries whose envelopes intersect the given envelope
	 */

	public List query(SpatialDataSourceDecorator sdsFaces, Envelope envelope)
			throws DriverException {

		if (!envelope.intersects(sdsFaces.getFullExtent())) {
			return new ArrayList();
		}

		ArrayList queryResult = new ArrayList();

		long rowCount = sdsFaces.getRowCount();

		for (int j = 0; j < rowCount; j++) {

			Geometry geom = sdsFaces.getGeometry(j);
			MyFeature myFeature = new MyFeature(sdsFaces.getInt(j, sdsFaces
					.getFieldIndexByName(ID)), geom);

			if (geom.getEnvelopeInternal().intersects(envelope)) {
				queryResult.add(myFeature);
			}
		}

		return queryResult;
	}
}
