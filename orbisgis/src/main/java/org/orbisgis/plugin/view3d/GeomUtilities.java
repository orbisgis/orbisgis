package org.orbisgis.plugin.view3d;

import javax.media.j3d.PointArray;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

public class GeomUtilities {
	public static void fromSpatialDatasourceToShape3D(final Shape3D shape,
			final SpatialDataSourceDecorator sds) throws DriverException {
		sds.open();
		for (long row = 0; row < sds.getRowCount(); row++) {
			System.out.printf("Row number %d\n", row);
			shape.addGeometry(GeomUtilities.fromJTSPolygonToJ3dQuadArray(sds
					.getGeometry(row)));
		}
		sds.cancel();
	}

	public static QuadArray fromJTSPolygonToJ3dQuadArray(final Geometry geometry) {
		QuadArray result = null;

		if (geometry instanceof Polygon) {
			Polygon p = (Polygon) geometry;
			// if (!p.isEmpty() && p.isValid()) {
			final Coordinate[] nodes = p.getCoordinates();
			result = new QuadArray(nodes.length - 1, QuadArray.COORDINATES
					| QuadArray.COLOR_3);
			for (int i = 0; i < nodes.length - 1; i++) {
				result.setCoordinate(i, new Point3d(nodes[i].x / 3,
						nodes[i].y / 3, nodes[i].z / 3));
				result.setColor(i, new Color3f(1f, 0f, 0f));
			}
			// }
		}
		return result;
	}

	public static void draw(final Shape3D shape,
			final SpatialDataSourceDecorator sds) throws DriverException {
		sds.open();
		QuadArray qa = null;
		Point3d [] pts = null;
		Color3f [] couls = null;
		PointArray pa = null;
		TriangleArray ta = null;
		
		for (long row = 0; row < sds.getRowCount(); row++) {
			Geometry geometry = sds.getGeometry(row);
			if (geometry instanceof Polygon) {
				Polygon p = (Polygon) geometry;
				Coordinate[] nodes = p.getCoordinates();
				int numNodes = nodes.length-1;
				int trigPoints = (numNodes-3)*3+3;
				System.out.println("NumNodes = " + numNodes + " trigPoints = " + trigPoints);
				pts = new Point3d[trigPoints];
				couls = new Color3f[trigPoints];
				//qa = new QuadArray(numNodes,QuadArray.COORDINATES|QuadArray.COLOR_3);
				//pa = new PointArray(numNodes,PointArray.COORDINATES|PointArray.COLOR_3);
				ta = new TriangleArray(trigPoints,TriangleArray.COORDINATES|TriangleArray.COLOR_3);
				
				
				for (int i = 0; i < 3; i++) {
					pts[i] = new Point3d(nodes[i].x/2,nodes[i].y/2,nodes[i].z/2);
					couls[i] = new Color3f(1.0f,0.0f,1.0f);
					System.out.println("i="+i);
				}
				
				for (int i = 3; i < numNodes; i=i+3) {
					System.out.println("i="+i);
					pts[i] = pts[0];
					pts[i+1] = pts[i-1];
					pts[i+2] = new Point3d(nodes[i].x/2,nodes[i].y/2,nodes[i].z/2);
					couls[i] = new Color3f(1.0f,0.0f,1.0f);
					couls[i+1] = new Color3f(1.0f,0.0f,1.0f);
					couls[i+2] = new Color3f(1.0f,0.0f,1.0f);
				}
				
				ta.setCoordinates(0,pts);
			    ta.setColors(0,couls);
				shape.addGeometry(ta);
			}

		}
		sds.cancel();

	}

}