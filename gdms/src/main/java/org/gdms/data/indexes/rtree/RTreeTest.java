package org.gdms.data.indexes.rtree;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import junit.framework.TestCase;

public class RTreeTest extends TestCase {

	public void testRTree() throws Exception {
		GeometryFactory gf = new GeometryFactory();
		RTree rtree = new DiskRTree(3, 128);
		Point p2 = gf.createPoint(new Coordinate(2, 2));
		rtree.insert(p2, -1);
		for (int i = 0; i < 118; i++) {
			System.out.println(i);
			Point p1 = gf.createPoint(new Coordinate(i, i));
			rtree.insert(p1, i);
			System.out.println("\n" + rtree);
			rtree.checkTree();
		}
		int[] rows = rtree.getRow(new Envelope(new Coordinate(0, 0),
				new Coordinate(40, 40)));
		// int[] rows = rtree.getRow(p4.getEnvelopeInternal());
		for (int i : rows) {
			System.out.println(i);
		}
	}
}
