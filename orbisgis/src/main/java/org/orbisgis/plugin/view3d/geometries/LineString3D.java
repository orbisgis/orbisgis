package org.orbisgis.plugin.view3d.geometries;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Line;
import com.jme.util.geom.BufferUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;

public class LineString3D extends Line {
	// Vertex positions for the mesh
	public Vector3f[] vertexes = null;

	// Normal directions for each vertex position
	// TODO : the normals are set to (0,0,1) for the moment...
	public Vector3f[] normals = null;

	// Color for each vertex position
	public ColorRGBA[] colors = null;

	// Texture Coordinates for each position
	public Vector2f[] texCoords = null;

	// Number of vertexes
	int size = 0;

	public LineString3D(LineString lineString) {

		// This will connect all the points.
		// Last point won't be connected to first one
		setMode(Line.CONNECTED);

		size = lineString.getNumPoints();
		vertexes = new Vector3f[size];
		normals = new Vector3f[size];
		colors = new ColorRGBA[size];
		texCoords = new Vector2f[size];

		Coordinate[] coord = lineString.getCoordinates();

		for (int i = 0; i < size; i++) {
			// If no z value is given we set it to 0
			if (Double.isNaN(coord[i].z)) {
				System.err
						.println("WARNING : no 3D data found, setting z to 0");
				coord[i].z = 0;
			}

			vertexes[i] = new Vector3f((float) coord[i].x, (float) coord[i].y,
					(float) coord[i].z);

			colors[i] = new ColorRGBA(1, 0, 0, 1);
			normals[i] = new Vector3f(0, 0, 1);
			texCoords[i] = new Vector2f(0, 0);
		}

		// Feed the informations
		reconstruct(BufferUtils.createFloatBuffer(vertexes), BufferUtils
				.createFloatBuffer(normals), BufferUtils
				.createFloatBuffer(colors), BufferUtils
				.createFloatBuffer(texCoords));

		// Create a bounds
		setModelBound(new BoundingBox());
		updateModelBound();

	}
}
