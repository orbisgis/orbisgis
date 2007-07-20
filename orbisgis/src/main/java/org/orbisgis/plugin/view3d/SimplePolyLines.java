package org.orbisgis.plugin.view3d;

/* Auteur: Nicolas JANEY         */
/* nicolas.janey@univ-fcomte.fr  */
/* Novembre 2001                 */

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.behaviors.mouse.*;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import javax.media.j3d.*;
import javax.media.j3d.GeometryStripArray.*;
import javax.vecmath.*;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;

public class SimplePolyLines extends Applet {

	private int aff = 0;

	private String[] mess = new String[7];

	private PointArray pa;

	private LineArray la;

	private TriangleArray ta;

	private QuadArray qa;

	private static Shape3D shape;

	private LineStripArray lsa;

	private TriangleStripArray tsa;

	private TriangleFanArray tfa;

	public BranchGroup createSceneGraph(SimpleUniverse u) {
		BranchGroup objRoot = new BranchGroup();
		BoundingSphere bounds;
		bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		{
			TransformGroup objTrans1 = new TransformGroup();
			objRoot.addChild(objTrans1);
			objTrans1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			objTrans1.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
			objTrans1.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
			MouseRotate behavior = new MouseRotate(objTrans1);
			objTrans1.addChild(behavior);
			behavior.setSchedulingBounds(bounds);
			Appearance a = new Appearance();
			PolygonAttributes attr = new PolygonAttributes();
			attr.setCullFace(PolygonAttributes.CULL_NONE);
			a.setPolygonAttributes(attr);
			shape = new Shape3D(pa, a);
			shape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
			TransformGroup objTrans2 = new TransformGroup();
			objTrans1.addChild(objTrans2);
			Transform3D t3D = new Transform3D();
			t3D.setRotation(new AxisAngle4d(1.0, 1.0, 0.0, Math.PI / 5.0));
			objTrans2.setTransform(t3D);
			objTrans2.addChild(shape);
		}
		objRoot.compile();
		return objRoot;
	}

	public SimplePolyLines() {
		setLayout(new BorderLayout());
		GraphicsConfiguration config;
		config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D c = new Canvas3D(config);
		Container ct = new Container();
		ct.setLayout(new FlowLayout());

		add("Center", c);

		add("South", ct);
		SimpleUniverse u = new SimpleUniverse(c);
		BranchGroup scene = createSceneGraph(u);
		u.getViewingPlatform().setNominalViewingTransform();
		u.addBranchGraph(scene);
	}

	public static void main(String[] args) {
		new MainFrame(new SimplePolyLines(), 250, 250);

		DataSourceFactory dsf = new DataSourceFactory();
		File src = new File("../../datas2tests/cir/volume_unitaire.cir");
		/*
		 * try { shape.setGeometry(toPolyon3D(new WKTReader().read("POLYGON ((0
		 * 0 0, 0.5 0 0, 0.5 0.5 0, 0 0.5 0, 0 0 0))"))); } catch
		 * (ParseException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		try {
			DataSource ds = dsf.getDataSource(src);
			SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
			sds.open();
			for (long row = 0; row < sds.getRowCount(); row++) {
				shape.setGeometry(toPolyon3D(sds.getGeometry(row)));
			}
			sds.cancel();
		} catch (DataSourceCreationException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (DriverException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

	}

	public static QuadArray toPolyon3D(
			com.vividsolutions.jts.geom.Geometry geometry) {
		QuadArray polygon1 = null;
		if (geometry instanceof Polygon) {
			Polygon p = (Polygon) geometry;
			Coordinate[] summits = p.getCoordinates();
			polygon1 = new QuadArray(summits.length - 1, QuadArray.COORDINATES
					| QuadArray.COLOR_3);
			for (int i = 0; i < summits.length - 1; i++) {
				System.out.printf("[%d] %g %g %g\n", i, summits[i].x,
						summits[i].y, summits[i].z);
				polygon1.setCoordinate(i, new Point3f((float) summits[i].x/4,
						(float) summits[i].y/4, (float) summits[i].z/3));
				polygon1.setColor(i, new Color3f(1f, 0f, 1f));
			}
		}
		return polygon1;

	}
}
