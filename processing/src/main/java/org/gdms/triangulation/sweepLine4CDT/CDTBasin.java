/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.triangulation.sweepLine4CDT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
		final Set<CDTTriangle> result = new HashSet<CDTTriangle>();
		final Map<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();
		for (int i = basinLeftBorder; i <= basinRightBorder; i++) {
			final Set<Integer> tmpSet = new HashSet<Integer>();
			if (i > basinLeftBorder) {
				tmpSet.add(i - 1);
			}
			if (i < basinRightBorder) {
				tmpSet.add(i + 1);
			}
			map.put(i, tmpSet);
		}
		final Set<Integer> tmpSetLeft = new HashSet<Integer>();
		tmpSetLeft.add(basinRightBorder);
		map.put(basinLeftBorder, tmpSetLeft);

		final Set<Integer> tmpSetRight = new HashSet<Integer>();
		tmpSetRight.add(basinLeftBorder);
		map.put(basinRightBorder, tmpSetRight);

		final List<CDTEdge> edges = meshIntoEdges();
		for (CDTEdge edge : edges) {
			map.get(edge.getBegin()).add(edge.getEnd());
			map.get(edge.getEnd()).add(edge.getBegin());
		}

		for (CDTEdge edge : edges) {
			Set<Integer> beginSet = map.get(edge.getBegin());
			Set<Integer> endSet = map.get(edge.getEnd());
			if (beginSet.size() < endSet.size()) {
				for (int idxOfThe3rdVtx : beginSet) {
					if (endSet.contains(idxOfThe3rdVtx)) {
						result.add(new CDTTriangle(slVertices.get(edge
								.getBegin()), slVertices.get(edge.getEnd()),
								slVertices.get(idxOfThe3rdVtx), pslg));
					}
				}
			} else {
				for (int idxOfThe3rdVtx : endSet) {
					if (beginSet.contains(idxOfThe3rdVtx)) {
						result.add(new CDTTriangle(slVertices.get(edge
								.getBegin()), slVertices.get(edge.getEnd()),
								slVertices.get(idxOfThe3rdVtx), pslg));
					}
				}
			}
		}
		return Arrays.asList(result.toArray(new CDTTriangle[0]));
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