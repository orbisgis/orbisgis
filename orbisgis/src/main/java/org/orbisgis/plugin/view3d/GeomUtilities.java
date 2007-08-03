package org.orbisgis.plugin.view3d;

import java.awt.Color;

import org.gdms.spatial.SpatialDataSourceDecorator;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.util.geom.BufferUtils;
import com.vividsolutions.jts.geom.Polygon;

/**
 * This is an utility class.
 * 
 * @author Samuel CHEMLA
 * 
 */
public class GeomUtilities {

	// Facteur de reduction en metres
	public int reductionFacteur = 500;

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

	/**
	 * This can be useful to change the background color of the simple canvas
	 * implemenor...
	 * 
	 * @param color
	 * @return
	 */
	public ColorRGBA makeColorRGBA(Color color) {
		return new ColorRGBA(color.getRed() / 255f, color.getGreen() / 255f,
				color.getBlue() / 255f, color.getAlpha() / 255f);
	}
	
	protected Polygon3D processGeometry(com.vividsolutions.jts.geom.Geometry geometry) {
		Polygon3D m = null;
		
		if (geometry instanceof Polygon) {
			Polygon polygone = (Polygon) geometry;
			m = new Polygon3D(polygone);

			Vector3f[] vertexes = m.vertexes;
			// Normal directions for each vertex position
			Vector3f[] normals = m.normals;
			// Color for each vertex position
			ColorRGBA[] colors = m.colors;
			// Texture Coordinates for each position
			Vector2f[] texCoords = m.texCoords;
			// The indexes of Vertex/Normal/Color/TexCoord sets. Every 3
			// makes a triangle.
			int[] indexes = m.indexes;
			// Feed the information to the TriMesh
			m.reconstruct(BufferUtils.createFloatBuffer(vertexes),
					BufferUtils.createFloatBuffer(normals), BufferUtils
							.createFloatBuffer(colors), BufferUtils
							.createFloatBuffer(texCoords), BufferUtils
							.createIntBuffer(indexes));
			// Create a bounds
			m.setModelBound(new BoundingBox());
			m.updateModelBound();
		}
		return m;
	}

}