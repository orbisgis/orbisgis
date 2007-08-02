package org.orbisgis.plugin.view3d;

import org.gdms.spatial.SpatialDataSourceDecorator;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;

/**
 * This is an utility class.
 * @author Samuel CHEMLA
 *
 */
public class GeomUtilities {

	// Facteur de reduction en metres
	public int reductionFacteur = 500;

	SpatialDataSourceDecorator sds = null;

	public GeomUtilities(SpatialDataSourceDecorator sds) {
		this.sds = sds;
	}
	
	public GeomUtilities() {
		
	}
	
	public Geometry createGrid(int numLines, float spacing) {
		Vector3f[] vertices = new Vector3f[numLines * 2 * 2];
		float edge = numLines / 2 * spacing;
		for (int ii = 0, idx = 0; ii < numLines; ii++) {
			float coord = (ii - numLines / 2) * spacing;
			vertices[idx++] = new Vector3f(-edge, 0f, coord);
			vertices[idx++] = new Vector3f(+edge, 0f, coord);
			vertices[idx++] = new Vector3f(coord, 0f, -edge);
			vertices[idx++] = new Vector3f(coord, 0f, +edge);
		}
		Geometry grid = new com.jme.scene.Line("grid", vertices, null, null,
				null);
		grid.getBatch(0).getDefaultColor().set(ColorRGBA.darkGray);
		return grid;
	}

}