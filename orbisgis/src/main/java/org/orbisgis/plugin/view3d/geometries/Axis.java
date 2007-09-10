package org.orbisgis.plugin.view3d.geometries;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.Text;
import com.jme.scene.state.TextureState;

public class Axis extends Node {

	private Line xline = null;

	private Line yline = null;

	private Line zline = null;

	private Vector3f[] x = null;

	private Vector3f[] y = null;

	private Vector3f[] z = null;
	
	private ColorRGBA[] red = new ColorRGBA[2];

	private final Vector3f zero = new Vector3f(0, 0, 0);

	private final int size = 100;

	public Axis() {
		
		red[0] = new ColorRGBA(1,0,0,0);
		red[1] = new ColorRGBA(1,0,0,0);

		x = new Vector3f[2];
		y = new Vector3f[2];
		z = new Vector3f[2];

		x[0] = zero;
		y[0] = zero;
		z[0] = zero;

		x[1] = new Vector3f(size, 0, 0);
		y[1] = new Vector3f(0, size, 0);
		z[1] = new Vector3f(0, 0, size);

		xline = new Line("x", x, null, red, null);
		yline = new Line("y", y, null, null, null);
		zline = new Line("z", z, null, null, null);

		attachChild(xline);
		attachChild(yline);
		attachChild(zline);
	}
}
