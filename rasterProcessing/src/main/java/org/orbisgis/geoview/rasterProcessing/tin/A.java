package org.orbisgis.geoview.rasterProcessing.tin;

import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class A {
	public static void main(String[] args) throws ParseException {
		String wkt = "Polygon((0 0, 1 0, 1 1, 0 1, 0 0))";
		Polygon p = (Polygon) new WKTReader().read(wkt);

		System.out.println(p.isValid());
		System.out.println(p.isRectangle());
		System.out.println(p.toText());
		System.out.println(p.getExteriorRing().getNumPoints());
	}
}
