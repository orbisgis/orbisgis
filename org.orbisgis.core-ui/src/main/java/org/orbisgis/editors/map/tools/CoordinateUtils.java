package org.orbisgis.editors.map.tools;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;

public class CoordinateUtils {

	public static ArrayList<Coordinate> removeDuplicated(
			ArrayList<Coordinate> points) {
		ArrayList<Coordinate> ret = new ArrayList<Coordinate>();
		for (int i = 0; i < points.size() - 1; i++) {
			if (!points.get(i).equals(points.get(i + 1))) {
				ret.add(points.get(i));
			}
		}
		ret.add(points.get(points.size() - 1));
		return ret;
	}

}
