package org.contrib.algorithm.triangulation.tin2;

import java.util.Formatter;

import com.vividsolutions.jts.geom.Coordinate;

public class Vertex {

	private Coordinate coordinate;
	private Integer gid;

	public Vertex(final Coordinate coordinate, final Integer gid) {
		this.coordinate = coordinate;
		this.gid = gid;
	}

	public Vertex(final Coordinate coordinate) {
		this(coordinate, null);
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public Integer getGid() {
		return gid;
	}

	@Override
	public String toString() {
		return new Formatter().format("[%d] %s", gid, coordinate.toString())
				.toString();
	}
}