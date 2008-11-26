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

public class GEdge extends GNode {
	private GMap<GVertex> vertices;
	public int incidentVertex1;
	public int incidentVertex2;
	private int incidentTriangleLeft = -1;
	private int incidentTriangleRight = -1;
	private Double azimuth;
	private Double steepestSlope;

	public GEdge() {
	}

	public GEdge(final GMap<GVertex> vertices, final int incidentVertex1,
			final int incidentVertex2) {
		this.vertices = vertices;
		if (incidentVertex1 < incidentVertex2) {
			this.incidentVertex1 = incidentVertex1;
			this.incidentVertex2 = incidentVertex2;
		} else {
			this.incidentVertex1 = incidentVertex2;
			this.incidentVertex2 = incidentVertex1;
		}
	}

	@Override
	public double getAzimuth() {
		if (null == azimuth) {
			final GVertex p = vertices.get(incidentVertex1);
			final GVertex q = vertices.get(incidentVertex2);
			double adj;
			double opp;
			if (q.z > p.z) {
				adj = p.x - q.x;
				opp = p.y - q.y;
			} else {
				adj = q.x - p.x;
				opp = q.y - p.y;
			}
			if (0 == opp) {
				azimuth = Math.signum(adj) * 90;
			} else {
				azimuth = Math.atan(adj / opp) * Math.PI / 180;
			}
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
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.DOUBLE),
				TypeFactory.createType(Type.DOUBLE) }, new String[] { "gid",
				"incident_vertex_1", "incident_vertex_2",
				"incident_triangle_left", "incident_triangle_right",
				"steepest_slope", "azimuth" });
	}

	@Override
	public double getSteepestSlope() {
		if (null == steepestSlope) {
			final GVertex p = vertices.get(incidentVertex1);
			final GVertex q = vertices.get(incidentVertex2);
			final double dz = q.z - p.z;
			final double dxy = Math.sqrt((q.x - p.x) * (q.x - p.x)
					+ (q.y - p.y) * (q.y - p.y));
			steepestSlope = (0 == dxy) ? 0 : dz / dxy;
		}
		return steepestSlope;
	}

	public void setIncidentTriangleLeft(final int incidentTriangleLeft) {
		this.incidentTriangleLeft = incidentTriangleLeft;
	}

	public void setIncidentTriangleRight(final int incidentTriangleRight) {
		this.incidentTriangleRight = incidentTriangleRight;
	}

	@Override
	public void store(Integer gid, DataSource dataSource)
			throws DriverException {
		dataSource.insertFilledRow(new Value[] { ValueFactory.createValue(gid),
				ValueFactory.createValue(incidentVertex1),
				ValueFactory.createValue(incidentVertex2),
				ValueFactory.createValue(incidentTriangleLeft),
				ValueFactory.createValue(incidentTriangleRight),
				ValueFactory.createValue(getSteepestSlope()),
				ValueFactory.createValue(getAzimuth()) });
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + incidentVertex1;
		result = prime * result + incidentVertex2;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GEdge other = (GEdge) obj;
		if (incidentVertex1 != other.incidentVertex1)
			return false;
		if (incidentVertex2 != other.incidentVertex2)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return new Formatter().format(Locale.US,
				"[%d -> %d] left = %d, right = %d\n", incidentVertex1,
				incidentVertex2, incidentTriangleLeft, incidentTriangleRight)
				.toString();
	}
}