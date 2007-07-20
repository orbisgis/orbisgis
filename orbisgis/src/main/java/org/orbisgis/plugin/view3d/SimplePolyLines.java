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

		Point3d[] pts = new Point3d[8];
		pts[0] = new Point3d(0.0, 0.0, -0.1);
		pts[1] = new Point3d(0.5, 0.2, -0.3);
		pts[2] = new Point3d(-0.2, 0.6, -0.4);
		pts[3] = new Point3d(0.3, 0.7, 0.5);
		pts[4] = new Point3d(-0.4, -0.5, 0.1);
		pts[5] = new Point3d(0.3, -0.6, 0.6);
		pts[6] = new Point3d(-0.5, 0.2, -0.2);
		pts[7] = new Point3d(-0.4, 0.7, 0.2);
		Point3d[] pts2 = new Point3d[6];
		pts2[0] = new Point3d(0.0, 0.0, -0.1);
		pts2[1] = new Point3d(0.5, 0.2, -0.3);
		pts2[2] = new Point3d(-0.2, 0.6, -0.4);
		pts2[3] = new Point3d(0.3, 0.7, 0.5);
		pts2[4] = new Point3d(-0.4, -0.5, 0.1);
		pts2[5] = new Point3d(0.3, -0.6, 0.6);
		Color3f[] couls = new Color3f[8];
		couls[0] = new Color3f(0.0f, 0.0f, 1.0f);
		couls[1] = new Color3f(1.0f, 0.0f, 0.0f);
		couls[2] = new Color3f(0.0f, 1.0f, 0.0f);
		couls[3] = new Color3f(1.0f, 0.0f, 1.0f);
		couls[4] = new Color3f(1.0f, 1.0f, 0.0f);
		couls[5] = new Color3f(1.0f, 1.0f, 1.0f);
		couls[6] = new Color3f(1.0f, 0.0f, 1.0f);
		couls[7] = new Color3f(1.0f, 0.0f, 0.0f);
		Color3f[] couls2 = new Color3f[6];
		couls2[0] = new Color3f(0.0f, 0.0f, 1.0f);
		couls2[1] = new Color3f(1.0f, 0.0f, 0.0f);
		couls2[2] = new Color3f(0.0f, 1.0f, 0.0f);
		couls2[3] = new Color3f(1.0f, 0.0f, 1.0f);
		couls2[4] = new Color3f(1.0f, 1.0f, 0.0f);
		couls2[5] = new Color3f(1.0f, 1.0f, 1.0f);
		pa = new PointArray(8, PointArray.COORDINATES | PointArray.COLOR_3);
		pa.setCoordinates(0, pts);
		pa.setColors(0, couls);

		la = new LineArray(8, LineArray.COORDINATES | LineArray.COLOR_3);
		la.setCoordinates(0, pts);
		la.setColors(0, couls);
		ta = new TriangleArray(6, TriangleArray.COORDINATES
				| TriangleArray.COLOR_3);
		ta.setCoordinates(0, pts2);
		ta.setColors(0, couls2);
		qa = new QuadArray(8, QuadArray.COORDINATES | QuadArray.COLOR_3);
		qa.setCoordinates(0, pts);
		qa.setColors(0, couls);
		int count[] = new int[1];
		count[0] = 8;
		lsa = new LineStripArray(8, LineStripArray.COORDINATES
				| LineStripArray.COLOR_3, count);
		lsa.setCoordinates(0, pts);
		lsa.setColors(0, couls);
		tsa = new TriangleStripArray(8, TriangleStripArray.COORDINATES
				| TriangleStripArray.COLOR_3, count);
		tsa.setCoordinates(0, pts);
		tsa.setColors(0, couls);
		tfa = new TriangleFanArray(8, TriangleFanArray.COORDINATES
				| TriangleFanArray.COLOR_3, count);
		tfa.setCoordinates(0, pts);
		tfa.setColors(0, couls);
		mess[0] = "        Points        ";
		mess[1] = "      Segments        ";
		mess[2] = "      Triangles       ";
		mess[3] = "        Quads         ";
		mess[4] = "   Ligne polygonale   ";
		mess[5] = "     Triangle fan     ";
		mess[6] = "    Triangle strip    ";
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
		File src = new File(
				"../../datas2tests/cir/volume_unitaire.cir");
		/*
		try {
			shape.setGeometry(toPolyon3D(new WKTReader().read("POLYGON ((0 0 0, 0.5 0 0, 0.5 0.5 0, 0 0.5 0, 0 0 0))")));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
		
		try {
			DataSource ds = dsf.getDataSource(src);
			SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
					ds);
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

	public static QuadArray toPolyon3D(com.vividsolutions.jts.geom.Geometry geometry) {
		QuadArray polygon1 = null;
		if (geometry instanceof Polygon) {
			Polygon p = (Polygon) geometry;
			Coordinate[] summits = p.getCoordinates();
			polygon1 = new QuadArray(summits.length - 1, QuadArray.COORDINATES
					| QuadArray.COLOR_3);
			for (int i = 0; i < summits.length - 1; i++) {
				polygon1.setCoordinate(i, new Point3f((float) summits[i].x,
						(float) summits[i].y, (float) summits[i].z));
				polygon1.setColor(i, new Color3f(1f,0f,1f));
			}
		}
		return polygon1;

	}
}
