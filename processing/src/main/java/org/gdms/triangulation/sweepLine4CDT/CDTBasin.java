package org.gdms.triangulation.sweepLine4CDT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

public class CDTBasin {
	private static final GeometryFactory gf = new GeometryFactory();
	private List<CDTVertex> slVertices;
	private List<Integer> u = null;
	private int basinLeftBorder;
	private int basinBed;
	private int basinRightBorder;
	private PSLG pslg;

	public CDTBasin(final List<CDTVertex> slVertices,
			final int basinLeftBorder, final int basinBed,
			final int basinRightBorder, final PSLG pslg) {
		this.slVertices = slVertices;
		this.basinLeftBorder = basinLeftBorder;
		this.basinBed = basinBed;
		this.basinRightBorder = basinRightBorder;
		this.pslg = pslg;
		this.u = new ArrayList<Integer>(basinRightBorder - basinLeftBorder + 1);
		for (int i = basinLeftBorder; i <= basinRightBorder; i++) {
			this.u.add(i);
		}
		Collections.sort(u, new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {
				double deltaY = slVertices.get(o1).getCoordinate().y
						- slVertices.get(o2).getCoordinate().y;
				if (deltaY > 0) {
					return -1;
				} else if (deltaY < 0) {
					return 1;
				} else {
					double deltaX = slVertices.get(o1).getCoordinate().x
							- slVertices.get(o2).getCoordinate().x;
					return (int) deltaX;
				}
			}
		});
	}

	protected int getBasinLeftBorder() {
		return basinLeftBorder;
	}

	protected int getBasinBed() {
		return basinBed;
	}

	protected int getBasinRightBorder() {
		return basinRightBorder;
	}

	protected LinearRing getLinearRing() {
		Coordinate[] coordinates = new Coordinate[basinRightBorder
				- basinLeftBorder + 1];
		for (int i = basinLeftBorder, j = 0; i <= basinRightBorder; i++, j++) {
			coordinates[j] = slVertices.get(i).getCoordinate();
		}
		LineString tmpLs = gf.createLineString(coordinates);
		CoordinateList tmpCl = new CoordinateList(tmpLs.getCoordinates());
		tmpCl.closeRing();
		return gf.createLinearRing(tmpCl.toCoordinateArray());
	}

	protected Polygon getPolygon() {
		return gf.createPolygon(getLinearRing(), null);
	}

	protected void normalize() {
		while (!getLinearRing().isValid()) {
			int shift = u.get(0);
			u.remove(0);
			if (shift < basinBed) {
				basinLeftBorder++;
			} else if (shift > basinBed) {
				basinRightBorder--;
			} else {
				throw new RuntimeException("Unreachable code !");
			}
		}
	}

	protected List<CDTEdge> meshIntoEdges() {
		// CDTBasin must be normalized !
		final List<CDTEdge> result = new ArrayList<CDTEdge>();
		final Polygon p = getPolygon();

		final Stack<Integer> s = new Stack<Integer>();
		s.add(u.get(0));
		s.add(u.get(1));
		final int n = u.size();
		for (int j = 2; j < n - 1; j++) {
			final int top = s.peek();
			if (((top > basinBed) && (u.get(j) < basinBed))
					|| ((top < basinBed) && (u.get(j) > basinBed))) {
				// both vertices are on different chains
				while (!s.empty()) {
					if (s.size() > 1) {
						result.add(new CDTEdge(u.get(j), s.pop()));
					} else {
						s.pop();
					}
				}
				s.push(u.get(j - 1));
				s.push(u.get(j));
			} else {
				int previous = s.pop();
				while (!s.empty()) {
					int current = s.peek();
					if (p.contains(gf.createLineString(new Coordinate[] {
							slVertices.get(u.get(j)).getCoordinate(),
							slVertices.get(current).getCoordinate() }))) {
						result.add(new CDTEdge(u.get(j), s.pop()));
						previous = current;
					} else {
						break;
					}
				}
				s.push(previous);
				s.push(u.get(j));
			}
		}
		for (int i = 1; i < s.size() - 1; i++) {
			result.add(new CDTEdge(u.get(n - 1), s.get(i)));
		}
		return result;
	}

	protected List<CDTTriangle> meshIntoTriangles() {
		// CDTBasin must be normalized !
		final List<CDTEdge> edges = meshIntoEdges();
		final List<CDTTriangle> result = new ArrayList<CDTTriangle>();
		CDTEdge prev = new CDTEdge(basinLeftBorder, basinRightBorder);
		for (CDTEdge edge : edges) {
			if (prev.getBegin() == edge.getBegin()) {
				result.add(0, new CDTTriangle(slVertices.get(prev.getBegin()),
						slVertices.get(prev.getEnd()), slVertices.get(edge
								.getEnd()), pslg));
			} else if (prev.getEnd() == edge.getEnd()) {
				result.add(0, new CDTTriangle(slVertices.get(prev.getBegin()),
						slVertices.get(prev.getEnd()), slVertices.get(edge
								.getBegin()), pslg));
			} else {
				throw new RuntimeException("Unreachable code !");
			}
			prev = edge;
		}
		result.add(0, new CDTTriangle(slVertices.get(prev.getBegin()),
				slVertices.get(prev.getEnd()), slVertices.get(basinBed), pslg));
		return result;
	}

	public void fillIn() {
		normalize();
		List<CDTTriangle> cdtTriangles = meshIntoTriangles();
		for (CDTTriangle cdtTriangle : cdtTriangles) {
			pslg.legalizeAndAdd(cdtTriangle);
		}
	}
	// protected void printU() {
	// for (int uItem : u) {
	// System.out.printf("[%d] %s\n", uItem, slVertices.get(uItem)
	// .getCoordinate());
	// }
	// System.out.println();
	// }
}