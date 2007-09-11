package org.orbisgis.plugin.view3d.geometries;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.gdms.driver.solene.Geometry3DUtilities;

import com.jme.bounding.BoundingBox;
import com.jme.scene.TriMesh;
import com.jme.util.geom.BufferUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;

/**
 * This class is the javaMonkey representation of a JTS polygon
 * 
 * @author Samuel CHEMLA
 * 
 */
public class Polygon3D extends TriMesh {
//	// Vertex positions for the mesh
//	public FloatBuffer vertexes = null;
//
//	// Normal directions for each vertex position
//	public FloatBuffer normals = null;
//
//	// Color for each vertex position
//	public FloatBuffer colors = null;
//
//	// Texture Coordinates for each position
//	public FloatBuffer texCoords = null;
//
//	// The indexes of Vertex/Normal/Color/TexCoord sets. Every 3
//	// makes a triangle.
//	public IntBuffer indexes = null;
//
//	// Number of vertexes
//	int size = 0;

	public Polygon3D(Polygon polygon) {
		
//		 Vertex positions for the mesh
		 FloatBuffer vertexes = null;

		// Normal directions for each vertex position
		 FloatBuffer normals = null;

		// Color for each vertex position
		 FloatBuffer colors = null;

		// Texture Coordinates for each position
		 FloatBuffer texCoords = null;

		// The indexes of Vertex/Normal/Color/TexCoord sets. Every 3
		// makes a triangle.
		 IntBuffer indexes = null;

		// Number of vertexes
		int size = 0;
		
		size = polygon.getNumPoints();
		vertexes = BufferUtils.createFloatBuffer(3 * size);
		normals = BufferUtils.createFloatBuffer(3 * size);
		colors = BufferUtils.createFloatBuffer(4 * size);
		texCoords = BufferUtils.createFloatBuffer(2 * size);
		indexes = BufferUtils.createIntBuffer(3 * size);

		/**
		 * Compute normals. Normally we simply compute the normal of the
		 * polygon, but if no z coordinate is given we will get Errors from
		 * Geometry3Dutilities.computeNormal(). So we check the z value of the
		 * first coordinate. If it exists (!=NaN) we assume all the z values are
		 * given. If it is NaN, we assume no z value is given so we stick on the
		 * ground
		 */
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
			// If no z value is given we set it to 0
			if (Double.isNaN(coord[i].z)) {
				System.err
						.println("WARNING : no 3D data found, setting z to 0");
				coord[i].z = 0;
			}

			vertexes.put((float) coord[i].x).put((float) coord[i].y).put(
					(float) coord[i].z);

			colors.put(1).put(0).put(0).put(1);
			normals.put(normalx).put(normaly).put(normalz);
			
			//TODO : textures are not well applied...
			texCoords.put((float) coord[i].x).put((float) coord[i].y);
		}

		// TODO : Here (should) come the triangulation !!!
		for (int i = 0; i < size - 1; i++) {
			indexes.put(0);
			indexes.put(i);
			indexes.put(i + 1);
		}
		indexes.put(0);
		indexes.put(size);
		indexes.put(size);

		vertexes.flip();
		normals.flip();
		colors.flip();
		texCoords.flip();
		indexes.flip();

		// Feed the information to the TriMesh
		reconstruct(vertexes, normals, colors, texCoords, indexes);

		// Create a BoundingBox
		setModelBound(new BoundingBox());
		updateModelBound();
	}
}
