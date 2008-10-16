package org.contrib.algorithm.triangulation.tin2;

import java.util.Formatter;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

public class VertexPtr {
	private List<Vertex> vertices;
	private int index;

	public VertexPtr(final List<Vertex> vertices, final int index) {
		this.vertices = vertices;
		this.index = index;
	}

	public Coordinate getCoordinate() {
		return vertices.get(index).getCoordinate();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
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
		VertexPtr other = (VertexPtr) obj;
		if (index != other.index)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return new Formatter().format("[%d] %s", index,
				getCoordinate().toString()).toString();
	}
}