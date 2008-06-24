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
package org.gdms.manual;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.postgis.LineString;
import org.postgis.MultiLineString;
import org.postgis.MultiPoint;
import org.postgis.MultiPolygon;
import org.postgis.Point;
import org.postgis.Polygon;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

public class CursorsTest {

	private static GeometryFactory gf = new GeometryFactory();
//	private static WKBReader reader = new WKBReader();
//	private static WKTWriter writer = new WKTWriter();
//	private static JtsBinaryParser parser = new JtsBinaryParser();

	public static void main(String[] args) throws Exception {
		Class.forName("org.postgresql.Driver");

		Connection con = DriverManager.getConnection(
				"jdbc:postgresql://127.0.0.1/gdms", "postgres", "postgres");
		Statement st = con.createStatement(ResultSet.TYPE_FORWARD_ONLY,
				ResultSet.CONCUR_READ_ONLY);
		ResultSet res = st
				.executeQuery("SELECT \"gid\", \"nom_comm\", \"statut\", "
						+ "\"code_cant\", \"code_arr\", \"code_dept\", \"code_reg\", \"codcom\","
						+ " \"codecant\", \"aire\", \"code_reg1\", \"code_dep\", \"code_com\", "
						+ "\"intitule\", \"impot98\", \"irmoy98\", \"nfoy98\", \"nfoyi98\", "
						+ "\"nfoyni98\", \"rnet98\", \"rneti98\", \"rnetmy98\", \"rnetni98\", "
						+ "\"rnet98m2\",\"the_geom\" as the_geom FROM \"communes\" "
						+ "ORDER BY \"gid\";");
		long t1 = System.currentTimeMillis();
//		PrintWriter fis = new PrintWriter(
//				new FileOutputStream("output2"));
		while (res.next()) {

			//PostgreSQL -> JTS directly
//			String bytes = res.getString(25);
//			com.vividsolutions.jts.geom.Geometry geom = parser.parse(bytes);
//			fis.println(writer.write(geom));

			//PostgreSQL -> AsBinary -> JTS
//			byte[] bytes = res.getBytes(25);
//			com.vividsolutions.jts.geom.Geometry geom = reader.read(bytes);
//			fis.println(writer.write(geom));

			// PGgeometry geom = (PGgeometry) res.getObject(25);
			// Geometry g = geom.getGeometry();
			// if (g.type == Geometry.POINT) {
			// buildPoint((Point) g);
			// } else if (g.type == Geometry.MULTIPOINT) {
			// buildMultipoint((MultiPoint) g);
			// } else if (g.type == Geometry.LINESTRING) {
			// buildLinestring((LineString) g);
			// } else if (g.type == Geometry.MULTILINESTRING) {
			// buildMultilinestring((MultiLineString) g);
			// } else if (g.type == Geometry.POLYGON) {
			// buildPolygon((Polygon) g);
			// } else if (g.type == Geometry.MULTIPOLYGON) {
			// buildMultipolygon((MultiPolygon) g);
			// }
		}
//		fis.close();
		long t2 = System.currentTimeMillis();
		System.out.println("Tiempo: " + ((t2 - t1) / 1000.0));
		res.close();
		st.close();
		con.close();
	}

//	private com.vividsolutions.jts.geom.Geometry WKB2Geometry(byte[] wkbBytes)
//			throws Exception {
//		// convert the byte[] to a JTS Geometry object
//
//		if (wkbBytes == null) // DJB: null value from database --> null
//			// geometry
//			// (the same behavior as WKT). NOTE: sending back a
//			// GEOMETRYCOLLECTION(EMPTY) is also a possibility,
//			// but this is not the same as NULL
//			return null;
//		// return g_temp; // for testing only!
//		WKBReader wkbr = new WKBReader();
//		// WKBReader wkbr = new WKBReader( );
//
//		com.vividsolutions.jts.geom.Geometry g = wkbr.read(wkbBytes);
//		return g;
//		// return new WKBReader().read(wkbBytes);
//	}

	public static byte getFromChar(char c) {
		if (c <= '9') {
			return (byte) (c - '0');
		} else if (c <= 'F') {
			return (byte) (c - 'A' + 10);
		} else {
			return (byte) (c - 'a' + 10);
		}
	}

//	private byte[] hexToBytes(String wkb) {
//		// convert the String of hex values to a byte[]
//		byte[] wkbBytes = new byte[wkb.length() / 2];
//
//		for (int i = 0; i < wkbBytes.length; i++) {
//			byte b1 = getFromChar(wkb.charAt(i * 2));
//			byte b2 = getFromChar(wkb.charAt((i * 2) + 1));
//			wkbBytes[i] = (byte) ((b1 << 4) | b2);
//		}
//
//		return wkbBytes;
//	}

	public static com.vividsolutions.jts.geom.MultiPolygon buildMultipolygon(
			MultiPolygon g) {
		Polygon[] pols = g.getPolygons();
		com.vividsolutions.jts.geom.Polygon[] jtsPols = new com.vividsolutions.jts.geom.Polygon[pols.length];
		for (int i = 0; i < pols.length; i++) {
			jtsPols[i] = buildPolygon(pols[i]);
		}

		return gf.createMultiPolygon(jtsPols);
	}

	public static com.vividsolutions.jts.geom.Polygon buildPolygon(Polygon g) {
		// TODO First is shell??
		LinearRing shell = buildLinearRing(g.getRing(0));
		LinearRing[] holes = new LinearRing[g.numRings() - 1];
		for (int i = 0; i < holes.length; i++) {
			holes[i] = buildLinearRing(g.getRing(i + 1));
		}

		return gf.createPolygon(shell, holes);
	}

	public static LinearRing buildLinearRing(org.postgis.LinearRing ring) {
		Point[] points = ring.getPoints();
		Coordinate[] coords = new Coordinate[points.length];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = buildCoordinate(points[i]);
		}
		return gf.createLinearRing(coords);
	}

	public static Coordinate buildCoordinate(Point point) {
		// TODO Always use Z?
		return new Coordinate(point.x, point.y, point.z);
	}

	public static com.vividsolutions.jts.geom.MultiLineString buildMultilinestring(
			MultiLineString g) {
		LineString[] linestrings = g.getLines();
		com.vividsolutions.jts.geom.LineString[] jtsLineStrings = new com.vividsolutions.jts.geom.LineString[linestrings.length];
		for (int i = 0; i < linestrings.length; i++) {
			jtsLineStrings[i] = buildLinestring(linestrings[i]);
		}

		return gf.createMultiLineString(jtsLineStrings);
	}

	public static com.vividsolutions.jts.geom.LineString buildLinestring(
			LineString g) {
		Point[] points = g.getPoints();
		Coordinate[] coords = new Coordinate[points.length];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = buildCoordinate(points[i]);
		}
		return gf.createLineString(coords);
	}

	public static com.vividsolutions.jts.geom.MultiPoint buildMultipoint(
			MultiPoint g) {
		Point[] points = g.getPoints();
		com.vividsolutions.jts.geom.Point[] jtsPoints = new com.vividsolutions.jts.geom.Point[points.length];
		for (int i = 0; i < points.length; i++) {
			jtsPoints[i] = buildPoint(points[i]);
		}

		return gf.createMultiPoint(jtsPoints);
	}

	public static com.vividsolutions.jts.geom.Point buildPoint(Point g) {
		return gf.createPoint(buildCoordinate(g));
	}
}
