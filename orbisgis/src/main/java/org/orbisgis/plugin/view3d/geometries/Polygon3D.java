package org.orbisgis.plugin.view3d.geometries;

import org.gdms.driver.solene.Geometry3DUtilities;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;

public class Polygon3D extends TriMesh {
	// Vertex positions for the mesh
	public Vector3f[] vertexes = null;

	// Normal directions for each vertex position
	public Vector3f[] normals = null;

	// Color for each vertex position
	public ColorRGBA[] colors = null;

	// Texture Coordinates for each position
	public Vector2f[] texCoords = null;

	// The indexes of Vertex/Normal/Color/TexCoord sets. Every 3
	// makes a triangle.
	public int[] indexes = null;

	// Number of vertexes
	int size = 0;

	public Polygon3D(Polygon polygon) {
		size = polygon.getNumPoints();
		vertexes = new Vector3f[size];
		normals = new Vector3f[size];
		colors = new ColorRGBA[size];
		texCoords = new Vector2f[size];
		indexes = new int[3 * size];

		Coordinate[] coord = polygon.getCoordinates();
		Coordinate normal;
		if (!Double.isNaN(coord[0].z)) {
			normal = Geometry3DUtilities.computeNormal(polygon);
		} else {
			normal = new Coordinate(0, 0, 1);
		}

		float normalx = (float) normal.x;
		float normaly = (float) normal.y;
		float normalz = (float) normal.z;

		for (int i = 0; i < size; i++) {
			if (Double.isNaN(coord[i].z)) {
				System.err.println("WARNING : no 3D data found, setting z to 0");
				coord[i].z = 0;
			}

			vertexes[i] = new Vector3f((float) coord[i].x, (float) coord[i].y,
					(float) coord[i].z);

			colors[i] = new ColorRGBA(1, 0, 0, 1);
			normals[i] = new Vector3f(normalx, normaly, normalz);
			texCoords[i] = new Vector2f(0, 0);
		}

		// TODO : Here comes the triangulation !!!
		for (int i = 0; i < size - 1; i++) {
			indexes[3 * i] = 0;
			indexes[3 * i + 1] = i;
			indexes[3 * i + 2] = i + 1;
		}

		indexes[3 * (size - 1)] = 0;
		indexes[3 * (size - 1) + 1] = size;
		indexes[3 * (size - 1) + 2] = size;

	}
}
