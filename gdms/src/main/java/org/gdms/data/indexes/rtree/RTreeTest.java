package org.gdms.data.indexes.rtree;

import java.io.File;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class RTreeTest {

	public static void main(String[] args) throws Exception {
		GeometryFactory gf = new GeometryFactory();
		RTree rtree = new DiskRTree(3, 128);
		File idxFile = new File("/tmp/example.rtidx");
		idxFile.delete();
		rtree.newIndex(idxFile);
		Point p2 = gf.createPoint(new Coordinate(2, 2));
		rtree.insert(p2, -1);
		int iterations = 3000;
		for (int i = 0; i < iterations; i++) {
			System.out.println(i);
			Point p1 = gf.createPoint(new Coordinate(i, i));
			rtree.insert(p1, i);
//			System.out.println("\n" + rtree);
			rtree.checkTree();
			rtree.getRow(new Envelope(new Coordinate(0, 0),
					new Coordinate(400, 400)));
		}
//		int[] rows = rtree.getRow(new Envelope(new Coordinate(0, 0),
//				new Coordinate(400, 400)));
//		for (int i : rows) {
//			System.out.println(i);
//		}
		rtree.delete(gf.createPoint(new Coordinate(2, 2)), -1);
		rtree.checkTree();
		for (int i = 0; i < iterations; i++) {
			System.out.println(i);
			Point p1 = gf.createPoint(new Coordinate(i, i));
			rtree.delete(p1, i);
//			System.out.println("\n" + rtree);
			rtree.checkTree();
		}
	}
}
