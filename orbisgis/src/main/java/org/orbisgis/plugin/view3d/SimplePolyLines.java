package org.orbisgis.plugin.view3d;

/* Auteur: Nicolas JANEY         */
/* nicolas.janey@univ-fcomte.fr  */
/* Novembre 2001                 */

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.io.File;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Point3d;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;

import com.hardcode.driverManager.DriverLoadException;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class SimplePolyLines extends JFrame{
	private static Shape3D shape = null;

	public BranchGroup createSceneGraph(SimpleUniverse u) {
		BranchGroup objRoot = new BranchGroup();
		BoundingSphere bounds;
		bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
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
		shape = new Shape3D();
		shape.setAppearance(a);
		shape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
		TransformGroup objTrans2 = new TransformGroup();
		objTrans1.addChild(objTrans2);
		Transform3D t3D = new Transform3D();
		t3D.setRotation(new AxisAngle4d(1.0, 1.0, 0.0, Math.PI / 5.0));
		objTrans2.setTransform(t3D);
		objTrans2.addChild(shape);
		objRoot.compile();
		return objRoot;
	}

	public SimplePolyLines() {
		/**
		 * Initializes the main frame
		 */
		setLayout(new BorderLayout());
		GraphicsConfiguration config;
		config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D c = new Canvas3D(config);
		add("Center", c);
		
		/**
		 * Creates the scene
		 */
		SimpleUniverse u = new SimpleUniverse(c);
		BranchGroup scene = createSceneGraph(u);
		u.getViewingPlatform().setNominalViewingTransform();
		u.addBranchGraph(scene);
		
		/**
		 * Displays the frame
		 */
		setSize(400, 400);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) throws DriverLoadException,
			DataSourceCreationException, DriverException {
		
		new SimplePolyLines();
		
		DataSourceFactory dsf = new DataSourceFactory();
		File src = new File("../../datas2tests/cir/volume_unitaire.cir");
		DataSource ds = dsf.getDataSource(src);
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);

		GeomUtilities.fromSpatialDatasourceToShape3D(shape, sds);

	}
}