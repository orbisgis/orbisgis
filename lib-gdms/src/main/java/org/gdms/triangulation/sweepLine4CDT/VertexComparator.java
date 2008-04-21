package org.gdms.triangulation.sweepLine4CDT;

import java.util.Comparator;

public class VertexComparator implements Comparator<Vertex> {
	public int compare(Vertex v1, Vertex v2) {
		return v1.getCoordinate().compareTo(v2.getCoordinate());
	}
}