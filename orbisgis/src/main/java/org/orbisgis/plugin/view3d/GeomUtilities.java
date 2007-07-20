package org.orbisgis.plugin.view3d;

import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
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
}