package org.orbisgis.geoview.tools;

import junit.framework.TestCase;

import org.orbisgis.tools.Primitive;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.instances.LineTool;
import org.orbisgis.tools.instances.MultilineTool;
import org.orbisgis.tools.instances.MultipointTool;
import org.orbisgis.tools.instances.MultipolygonTool;
import org.orbisgis.tools.instances.PointTool;
import org.orbisgis.tools.instances.PolygonTool;
import org.orbisgis.tools.instances.SelectionTool;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class InsertToolTest extends TestCase {

	private TestEditionContext ec;

	private ToolManager tm;

	private GeometryFactory gf = ToolManager.toolsGeometryFactory;

	private Geometry p1 = gf.createPoint(new Coordinate(50, 50));

	private Geometry p2 = gf.createLineString(new Coordinate[] {
			new Coordinate(65, 65), new Coordinate(85, 85) });

	public void testAddPoint() throws Exception {
		ec.geometryType = Primitive.POINT_GEOMETRY_TYPE;
		long rc = ec.features.size();
		tm.setTool(new PointTool());
		tm.setValues(new double[] { 0, 0 });
		tm.transition("point");
		tm.setValues(new double[] { 100, 100 });
		tm.transition("point");

		assertTrue(ec.features.size() - 2 == rc);
	}

	public void testAddMultiPoint() throws Exception {
		long rc = ec.features.size();
		tm.setTool(new MultipointTool());
		tm.setValues(new double[] { 0, 0 });
		tm.transition("point");
		tm.setValues(new double[] { 100, 100 });
		tm.transition("point");
		tm.transition("t");

		assertTrue(ec.features.size() - 1 == rc);
	}

	public void testAddLine() throws Exception {
		long rc = ec.features.size();
		tm.setTool(new LineTool());
		tm.setValues(new double[] { 0, 0 });
		tm.transition("point");
		tm.setValues(new double[] { 100, 100 });
		tm.transition("point");
		tm.transition("t");

		assertTrue(ec.features.size() - 1 == rc);

		tm.setValues(new double[] { 0, 0 });
		tm.transition("point");
		tm.setValues(new double[] { 100, 100 });
		tm.transition("point");
		tm.setValues(new double[] { 100, 0 });
		tm.transition("point");
		tm.setValues(new double[] { 0, 100 });
		tm.transition("point");
		tm.transition("t");

		assertTrue(ec.features.size() - 2 == rc);
	}

	public void testAddPolygon() throws Exception {
		long rc = ec.features.size();
		tm.setTool(new PolygonTool());
		tm.setValues(new double[] { 0, 0 });
		tm.transition("point");
		tm.setValues(new double[] { 100, 100 });
		tm.transition("point");
		tm.setValues(new double[] { 100, 0 });
		tm.transition("point");
		tm.transition("t");

		assertTrue(ec.features.size() - 1 == rc);
	}

	public void testAddBadPolygon() throws Exception {
		long rc = ec.features.size();
		tm.setTool(new PolygonTool());
		tm.setValues(new double[] { 0, 0 });
		tm.transition("point");
		tm.setValues(new double[] { 100, 100 });
		tm.transition("point");
		tm.setValues(new double[] { 100, 0 });
		tm.transition("point");
		tm.setValues(new double[] { 49, 61 });
		tm.transition("point");
		try {
			tm.transition("t");
			assertTrue(false);
		} catch (TransitionException e) {
			assertTrue(ec.features.size() == rc);
		}
	}

	public void testAddMultiLine() throws Exception {
		long rc = ec.features.size();
		tm.setTool(new MultilineTool());
		tm.setValues(new double[] { 0, 0 });
		tm.transition("point");
		tm.setValues(new double[] { 100, 100 });
		tm.transition("point");
		tm.transition("l");
		tm.setValues(new double[] { 50, 0 });
		tm.transition("point");
		tm.setValues(new double[] { 50, 100 });
		tm.transition("point");
		tm.transition("t");

		assertTrue(ec.features.size() - 1 == rc);
	}

	 public void testAddMultiPolygon() throws Exception {
		long rc = ec.features.size();
		tm.setTool(new MultipolygonTool());
		tm.setValues(new double[] { 0, 0 });
		tm.transition("point");
		tm.setValues(new double[] { 100, 100 });
		tm.transition("point");
		tm.setValues(new double[] { 100, 0 });
		tm.transition("point");
		tm.transition("p");
		tm.setValues(new double[] { 50, 50 });
		tm.transition("point");
		tm.setValues(new double[] { 100, 101 });
		tm.transition("point");
		tm.setValues(new double[] { 50, 1010 });
		tm.transition("point");
		tm.transition("t");

		assertTrue(ec.features.size() - 1 == rc);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ec = new TestEditionContext(Primitive.LINE_GEOMETRY_TYPE);
		ec.features.add(p1);
		ec.features.add(p2);
		tm = new ToolManager(new SelectionTool(), ec);
	}
}
