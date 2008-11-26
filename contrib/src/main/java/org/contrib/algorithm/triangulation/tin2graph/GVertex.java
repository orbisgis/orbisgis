package org.contrib.algorithm.triangulation.tin2graph;

import java.util.Formatter;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;

import com.vividsolutions.jts.geom.Coordinate;

public class GVertex extends GNode {
	public double x;
	public double y;
	public double z;
	private Set<Integer> incidentEdges;

	public GVertex() {
	}

	public GVertex(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		incidentEdges = new HashSet<Integer>();
	}

	public GVertex(Coordinate coordinate) {
		this(coordinate.x, coordinate.y, coordinate.z);
	}

	public boolean addAGedge(int edgeIdx) {
		return incidentEdges.add(edgeIdx);
	}

	@Override
	public double getAzimuth() {
		return Double.NaN;
	}

	public Coordinate getCoordinate() {
		return new Coordinate(x, y, z);
	}

	@Override
	public Metadata getMetadata() {
		return new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.DOUBLE),
				TypeFactory.createType(Type.DOUBLE),
				TypeFactory.createType(Type.DOUBLE),
				TypeFactory.createType(Type.COLLECTION), }, new String[] {
				"gid", "x", "y", "z", "incident_edges" });
	}

	@Override
	public double getSteepestSlope() {
		return Double.NaN;
	}

	@Override
	public void store(Integer gid, DataSource dataSource)
			throws DriverException {
		final Integer[] tmp = incidentEdges.toArray(new Integer[0]);
		final Value[] collectionOfValues = new Value[incidentEdges.size()];

		for (int i = 0; i < collectionOfValues.length; i++) {
			collectionOfValues[i] = ValueFactory.createValue(tmp[i]);
		}

		dataSource.insertFilledRow(new Value[] { ValueFactory.createValue(gid),
				ValueFactory.createValue(x), ValueFactory.createValue(y),
				ValueFactory.createValue(z),
				ValueFactory.createValue(collectionOfValues) });
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		GVertex other = (GVertex) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return new Formatter().format(Locale.US,
				"[%.1f %.1f %.1f] list = %s\n", x, y, z,
				incidentEdges.toString()).toString();
	}
}