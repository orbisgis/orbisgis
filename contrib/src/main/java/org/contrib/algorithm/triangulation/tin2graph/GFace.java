package org.contrib.algorithm.triangulation.tin2graph;

import java.util.Formatter;
import java.util.Locale;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;

import com.vividsolutions.jts.geom.Coordinate;

public class GFace extends GNode {
	private GMap<GVertex> setOfVertices;
	private int[] vtx;
	private int[] edg;
	private Double azimuth;
	private Double steepestSlope;
	private Coordinate slope;

	public GFace() {
	}

	public GFace(final GMap<GVertex> vertices, GMap<GEdge> edges,
			final int[] vtx, final int[] edg) {
		this.setOfVertices = vertices;
		this.vtx = vtx;
		this.edg = edg;
	}

	@Override
	public double getAzimuth() {
		if (null == azimuth) {
			final Coordinate s = getSlope();
			azimuth = (0 == s.y) ? Math.signum(s.x) * 90 : Math.atan(s.x / s.y)
					* Math.PI / 180;
		}
		return azimuth;
	}

	@Override
	public Metadata getMetadata() {
		return new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.DOUBLE),
				TypeFactory.createType(Type.DOUBLE) }, new String[] { "gid",
				"incident_edge_1", "incident_edge_2", "incident_edge_3",
				"steepest_slope", "azimuth" });
	}

	private static Coordinate sub(final Coordinate u, final Coordinate v) {
		return new Coordinate(v.x - u.x, v.y - u.y, v.z - u.z);
	}

	private static Coordinate cross(final Coordinate u, final Coordinate v) {
		return new Coordinate(u.y * v.z - u.z * v.y, u.z * v.x - u.x * v.z, u.x
				* v.y - u.y * v.x);
	}

	private static double length(final Coordinate u) {
		return Math.sqrt(scalarProduct(u, u));
	}

	private static Coordinate normalize(final Coordinate u) {
		final double tmp = length(u);
		return new Coordinate(u.x / tmp, u.y / tmp, u.z / tmp);
	}

	private static double scalarProduct(Coordinate u, Coordinate v) {
		return u.x * v.x + u.y * v.y + u.z * v.z;
	}

	private static Coordinate centroid(Coordinate p, Coordinate q, Coordinate r) {
		final double x = (p.x + q.x + r.x) / 3;
		final double y = (p.y + q.y + r.y) / 3;
		final double z = (p.z + q.z + r.z) / 3;
		return new Coordinate(x, y, z);
	}

	private Coordinate getSlope() {
		if (null == slope) {
			final Coordinate p = setOfVertices.get(vtx[0]).getCoordinate();
			final Coordinate q = setOfVertices.get(vtx[1]).getCoordinate();
			final Coordinate r = setOfVertices.get(vtx[2]).getCoordinate();
			// compute the centroid
			final Coordinate c = centroid(p, q, r);
			// define a plane through those three points and evaluate its normal
			final Coordinate n = normalize(cross(sub(p, q), sub(p, r)));
			final double aabb = n.x * n.x + n.y * n.y;
			if (0 != aabb) {
				// proj corresponds to the projection of the centroid into the
				// intersection line between the ground and the triangle plane
				final Coordinate proj = new Coordinate(c.x + (n.x * n.z * c.z)
						/ aabb, c.y + (n.y * n.z * c.z) / aabb, 0);
				return normalize(sub(c, proj));
			} else {
				// TODO: is it a good idea ?
				return new Coordinate(Double.NaN, Double.NaN, Double.NaN);
			}
		}
		return slope;
	}

	@Override
	public double getSteepestSlope() {
		if (null == steepestSlope) {
			final Coordinate slopeTmp = getSlope();
			double dxy = Math.sqrt(slopeTmp.x * slopeTmp.x + slopeTmp.y
					* slopeTmp.y);
			steepestSlope = (0 == dxy) ? 0 : Math.abs(slopeTmp.z) / dxy;
		}
		return steepestSlope;
	}

	@Override
	public void store(Integer gid, DataSource dataSource)
			throws DriverException {
		dataSource.insertFilledRow(new Value[] { ValueFactory.createValue(gid),
				ValueFactory.createValue(edg[0]),
				ValueFactory.createValue(edg[1]),
				ValueFactory.createValue(edg[2]),
				ValueFactory.createValue(getSteepestSlope()),
				ValueFactory.createValue(getAzimuth()) });
	}

	@Override
	public String toString() {
		return new Formatter().format(Locale.US,
				"[%d %d %d] edges = %d %d %d\n", vtx[0], vtx[1], vtx[2],
				edg[0], edg[1], edg[2]).toString();
	}
}