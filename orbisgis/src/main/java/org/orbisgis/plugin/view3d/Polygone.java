package org.orbisgis.plugin.view3d;
import org.gdms.driver.solene.Geometry3DUtilities;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;

public class Polygone extends TriMesh {
	// Vertex positions for the mesh
	Vector3f[] vertexes = null;

	// Normal directions for each vertex position
	Vector3f[] normals = null;

	// Color for each vertex position
	ColorRGBA[] colors = null;

	// Texture Coordinates for each position
	Vector2f[] texCoords = null;

	// The indexes of Vertex/Normal/Color/TexCoord sets. Every 3
	// makes a triangle.
	int[] indexes = null;

	// Number of vertexes
	int size = 0;

	Polygone(Polygon polygon) {
		size = polygon.getNumPoints();
		vertexes = new Vector3f[size];
		normals = new Vector3f[size];
		colors = new ColorRGBA[size];
		texCoords = new Vector2f[size];
		indexes = new int[3*size];

		Coordinate[] coord = polygon.getCoordinates();

		Coordinate normal = Geometry3DUtilities.computeNormal(polygon);
		float normalx = (float) normal.x;
		float normaly = (float) normal.y;
		float normalz = (float) normal.z;

		for (int i = 0; i < size; i++) {
			vertexes[i] = new Vector3f((float) coord[i].x, (float) coord[i].y,
					(float) coord[i].z);
			
			colors[i] = new ColorRGBA(1, 0, 0, 1);
			normals[i] = new Vector3f(normalx, normaly, normalz);
			texCoords[i] = new Vector2f(0, 0);
		}
		
		for (int i = 0; i < size-1; i++) {
			indexes[3*i] = 0;
			indexes[3*i+1] = i;
			indexes[3*i+2] = i+1;
		}
		
		indexes[3*(size-1)] = 0;
		indexes[3*(size-1)+1] = size;
		indexes[3*(size-1)+2] = size;

	}
}
