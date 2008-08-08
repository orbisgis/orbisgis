package org.gdms.triangulation.sweepLine4CDT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.strtree.STRtree;

public class CDTOrderedSetOfVertices {
	private static final GeometryFactory gf = new GeometryFactory();
	private List<Coordinate> arrayList;
	private SortedSet<Coordinate> sortedSet;
	private SpatialIndex spatialIndex;

	/**
	 * Each Vertex embeds also all the edges (as a sorted set of normalized
	 * LineSegments) that reach it (I mean: the point that corresponds to this
	 * Vertex is the end of each edge of this set).
	 */

	public CDTOrderedSetOfVertices() {
		sortedSet = new TreeSet<Coordinate>(new Comparator<Coordinate>() {
			public int compare(Coordinate o1, Coordinate o2) {
				if (o1.y > o2.y) {
					return 1;
				}
				if (o1.y < o2.y) {
					return -1;
				}
				if (o1.x < o2.x) {
					return -1;
				}
				if (o1.x > o2.x) {
					return 1;
				}
				return 0;
			}
		});
	}

	public boolean add(final Coordinate vertex) {
		if (null == sortedSet) {
			throw new RuntimeException("Add must not be called after a get !");
		}
		return sortedSet.add(vertex);
	}

	public boolean add(final Coordinate[] vertices) {
		if (null == sortedSet) {
			throw new RuntimeException("Add must not be called after a get !");
		}
		return sortedSet.addAll(Arrays.asList(vertices));
	}

	private List<Coordinate> getArrayList() {
		if (null == arrayList) {
			arrayList = new ArrayList<Coordinate>(sortedSet);
			// build the spatial index...
			spatialIndex = new STRtree(10);
			for (int i = 0; i < arrayList.size(); i++) {
				spatialIndex.insert(gf.createPoint(arrayList.get(i))
						.getEnvelopeInternal(), new Integer(i));
			}
			sortedSet = null;
		}
		return arrayList;
	}

	public Coordinate get(final int i) {
		return getArrayList().get(i);
	}

	public Envelope getEnvelope(final int i) {
		return getPoint(i).getEnvelopeInternal();
	}

	public Point getPoint(final int i) {
		return gf.createPoint(get(i));
	}

	public void print() {
		for (int i = 0; i < getArrayList().size(); i++) {
			System.out
					.printf("[%6d] x = %.2f\ty=%.2f\n", i, get(i).x, get(i).y);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Integer> query(final Envelope envelope) {
		if (null == spatialIndex) {
			getArrayList();
		}
		return spatialIndex.query(envelope);
	}

	public Integer search(final Coordinate coordinate) {
		List<Integer> sublist = query(gf.createPoint(coordinate)
				.getEnvelopeInternal());
		for (int index : sublist) {
			if (get(index).equals3D(coordinate)) {
				return index;
			}
		}
		return null;
	}

	public int size() {
		return getArrayList().size();
	}
}