package org.orbisgis.plugin.view3d.geometries;

import java.awt.Color;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.util.geom.BufferUtils;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
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
			vertices[idx++] = new Vector3f(-edge, coord, 0f);
			vertices[idx++] = new Vector3f(+edge, coord, 0f);
			vertices[idx++] = new Vector3f(coord, -edge, 0f);
			vertices[idx++] = new Vector3f(coord, +edge, 0f);
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

	public Node processGeometry(com.vividsolutions.jts.geom.Geometry geometry) {
		Node m = new Node();

		if (geometry instanceof Polygon) {
			Polygon polygone = (Polygon) geometry;
			m.attachChild(new Polygon3D(polygone));

		} else if (geometry instanceof MultiPolygon) {
			MultiPolygon multi = (MultiPolygon) geometry;
			int lengh = multi.getNumGeometries();
			for (int i = 0; i < lengh; i++) {
				m.attachChild(processGeometry(multi.getGeometryN(i)));
			}

		} else if (geometry instanceof LineString) {
			LineString lineString = (LineString) geometry;
			m.attachChild(new LineString3D(lineString));

		} else if (geometry instanceof MultiLineString) {
			MultiLineString multi = (MultiLineString) geometry;
			int lengh = multi.getNumGeometries();
			for (int i = 0; i < lengh; i++) {
				m.attachChild(processGeometry(multi.getGeometryN(i)));
			}

		} else if (geometry instanceof Point) {
			Point point = (Point) geometry;
			m.attachChild(new Point3D(point));

		} else if (geometry instanceof MultiPoint) {
			MultiPoint multi = (MultiPoint) geometry;
			int lengh = multi.getNumGeometries();
			for (int i = 0; i < lengh; i++) {
				m.attachChild(processGeometry(multi.getGeometryN(i)));
			}

		} else
			throw new Error("Geometry not yet supported");

		return m;
	}
}