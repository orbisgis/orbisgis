package org.urbsat.function;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureResults;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.AttributeType;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;
import org.geotools.feature.IllegalAttributeException;

import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.algorithm.LineIntersector;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFactory;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

public class test2 {
	// buildings : HashMap de batiments contenant l'id et l'arraylist des
	// propriétés de chaque batiments
	public static HashMap<String, ArrayList> buildings;

	public static HashMap<String, ArrayList> block;

	public static ArrayList<ArrayList<Geometry>> grille;

	public static void main(String[] args) throws IOException,
			NoSuchElementException, IllegalAttributeException, ParseException {

		buildings = recuptab("C:\\Documents and Settings\\thebaud\\Bureau\\STH_docs\\batiments.shp");
		block = recuptab("C:\\Documents and Settings\\thebaud\\Bureau\\STH_docs\\quartiers.shp");
		recuptab("C:\\Documents and Settings\\thebaud\\Bureau\\STH_docs\\test.shp");
		// appartenance();

		// calculAngleFacade("batiments.1");
		// grille = creerEnveloppe();
		/*
		 * BufferedReader inr = new BufferedReader(new
		 * InputStreamReader(System.in)); String s = inr.readLine(); //int I =
		 * Integer.valueOf(s).intValue(); System.out.println(s);
		 */
		// donnerStats(1, 1);
		// creationLsAngle(0);
	cacheParBati();
		//testVent();
		
	}

	// extrait les données du fichier shapfile, recupere les batiments pour les
	// mettres dans
	// un HashMap ayant pour clé l'idée du batiment et pour valeur un arraylist
	// contenant les données du batiment
	public static HashMap<String, ArrayList> recuptab(String URI)
			throws IOException, NoSuchElementException,
			IllegalAttributeException {
		System.out.println("ifffffezod");
		File f = new File(URI);
		URL shapeURL = f.toURL();

		ShapefileDataStore store = new ShapefileDataStore(shapeURL);
		String name = store.getTypeNames()[0];
		FeatureSource source = store.getFeatureSource(name);
		FeatureResults fsShape = source.getFeatures();

		// print out a feature type header and wait for user input
		FeatureType ft = source.getSchema();
		HashMap<String, ArrayList> toMap = new HashMap<String, ArrayList>();
		// entrées des noms dans la hashmap
		
		toMap.put("names", new ArrayList());
		for (int i = 0; i < ft.getAttributeCount(); i++) {
			AttributeType at = ft.getAttributeType(i);
			if (!Geometry.class.isAssignableFrom(at.getType()))
				toMap.get("names").add(at.getName());
		}
		System.out.println();
		toMap.put("classes", new ArrayList());
		for (int i = 0; i < ft.getAttributeCount(); i++) {
			AttributeType at = ft.getAttributeType(i);
			if (!Geometry.class.isAssignableFrom(at.getType()))
				// System.out.print(at);
				toMap.get("classes").add(at.getType().getName());
		}

		FeatureReader reader = fsShape.reader();
		while (reader.hasNext()) {
			Feature feature = reader.next();
			// System.out.print(feature.getID() + "\t");
			toMap.put(feature.getID(), new ArrayList());
			for (int i = 0; i < feature.getNumberOfAttributes(); i++) {
				Object attribute = feature.getAttribute(i);
				if (!(attribute instanceof Geometry))
					toMap.get(feature.getID()).add(attribute);
			}

		}
		reader.close();

		reader = fsShape.reader();
		toMap.get("names").add("geometry");
		Feature feature = null;
		while (reader.hasNext()) {
			feature = reader.next();

			/*
			 * System.out.print(feature.getID() + "\t");
			 * System.out.println(feature.getDefaultGeometry());
			 * System.out.println();
			 */

			toMap.get(feature.getID()).add(feature.getDefaultGeometry());

		}
		toMap.get("classes").add(
				feature.getDefaultGeometry().getClass().getName());
		reader.close();
		System.out.println(toMap);
		System.out.println("iezod");
		return toMap;
		
	}

	public static void appartenance() {
		// definir a quel quartiers appartiennent les batiments
		// System.out.println(buildings.get("names").indexOf("geometry"));
		// System.out.println(block.get("names").indexOf("geometry"));
		for (Entry<String, ArrayList> entryBlock : block.entrySet()) {
			String cleBlock = entryBlock.getKey();
			ArrayList valeurBlock = entryBlock.getValue();
			if (valeurBlock.get(2) instanceof Geometry) {

				for (Entry<String, ArrayList> entry : buildings.entrySet()) {
					String cleBuilding = entry.getKey();
					ArrayList valeurBuilding = entry.getValue();
					if (valeurBuilding.get(3) instanceof Geometry) {

						Geometry thisBlock = (Geometry) valeurBlock.get(2);
						Geometry thisBuilding = (Geometry) valeurBuilding
								.get(3);
						if (thisBlock.contains(thisBuilding)) {
							System.out.println("le batiment " + cleBuilding
									+ " appartient au quartier " + cleBlock);
						}
					}
				}
			}
		}

	}
	
	
	//eclate un Polygon en Plusieurs LineString representant un segment par facade
	public static ArrayList<LineString> toLineString(Geometry geo) {
		/*
		 * for (Entry<String, ArrayList> entry : buildings.entrySet()) { String
		 * cleBuilding = entry.getKey(); ArrayList valeurBuilding =
		 * entry.getValue();
		 */
		// if (valeurBuilding.get(3) instanceof Geometry) {
		// Geometry thisBuilding = (Geometry) valeurBuilding.get(3);
		ArrayList<LineString> ls = new ArrayList<LineString>();
		MultiPolygon polybuilding = (MultiPolygon) geo;

		MultiLineString lsBuilding = (MultiLineString) polybuilding
				.getBoundary();
		// System.out.println(lsBuilding);
		Coordinate[] tab = lsBuilding.getCoordinates();
		// faire les linestring
		GeometryFactory geom = new GeometryFactory();
		int i = 0;
		boolean boucle = true;
		while (boucle) {
			Coordinate[] tab2 = new Coordinate[2];
			tab2[0] = tab[i];
			tab2[1] = tab[i + 1];
			LineString hey3 = geom.createLineString(tab2);
			ls.add(hey3);
			i++;
			try {
				tab[i + 1] = tab[i + 1];
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {
				boucle = false;
			}
		}
		return ls;
	}
	
	//calcul un angle pour une facade donnée d'un batiment
	public static void calculAngleFacade(String batiment) throws ParseException {
		Geometry build = (Geometry) buildings.get(batiment).get(3);
		ArrayList<LineString> ls = toLineString(build);

		// calcul l'angle d'une facade avec une line string arbitraire
		// represantant la direction du vent
		LineString test1 = creationLsAngle(90);

		// recuperation des vecteurs par rapport au linestring
		Coordinate[] cor = test1.getCoordinates();
		Coordinate[] cor2 = ls.get(2).getCoordinates();
		System.out.println(test1);
		System.out.println(ls.get(1));

		// cos a = (XaXb+YaYb+ZaZb) / sqrt((Xa²+Ya²+Za²)(Xb²+Yb²+Zb² ))
		double ax = cor[1].x - cor[0].x;
		double ay = cor[1].y - cor[0].y;
		double bx = cor2[1].x - cor2[0].x;
		double by = cor2[1].y - cor2[0].y;

		;

		double cosa = (ax * bx + ay * by)
				/ Math.sqrt((ax * ax + ay * ay) * (bx * bx + by * by));

		double acosa = Math.acos(cosa);

		double acosad = Angle.toDegrees(acosa);
		System.out.println("l'angle serait de " + acosad + " degres");

	}

	public static ArrayList<ArrayList<Geometry>> creerEnveloppe() {
		Envelope env = new Envelope();
		Geometry global = null;
		for (Entry<String, ArrayList> entry : buildings.entrySet()) {

			if (entry.getValue().get(3) instanceof Geometry) {
				Geometry toEnv = (Geometry) entry.getValue().get(3);
				Coordinate[] cheick = toEnv.getCoordinates();
				for (int i = 0; i < cheick.length; i++) {
					env.expandToInclude(cheick[i]);
				}

			}
			// System.out.println(env);
			GeometryFactory fact = new GeometryFactory();
			global = fact.toGeometry(env);
			// System.out.println(global);
		}

		// creation de la grille
		double largeur = env.getMinX();
		ArrayList<ArrayList<Geometry>> grille = new ArrayList<ArrayList<Geometry>>();
		while (largeur < env.getMaxX()) {
			double lahauteur = 0;
			double lalargeur = 0;
			double hauteur = env.getMaxY();
			ArrayList<Geometry> haut = new ArrayList<Geometry>();

			while (hauteur > env.getMinY()) {

				if (hauteur - 300 < env.getMinY()) {
					lahauteur = env.getMinY();
				} else {
					lahauteur = hauteur - 300;
				}
				if (largeur + 100 > env.getMaxX()) {
					lalargeur = env.getMaxX();
				} else {
					lalargeur = largeur + 300;
				}
				Envelope xy = new Envelope(largeur, lalargeur, hauteur,
						lahauteur);
				hauteur = hauteur - 300;

				GeometryFactory fact = new GeometryFactory();
				Geometry global2 = fact.toGeometry(xy);
				haut.add(global2);

			}
			grille.add(haut);
			largeur = largeur + 300;

		}
		return grille;
	}

	public static void donnerStats(int x, int y) {
		Geometry portion = grille.get(x).get(y);
		System.out.println(portion);
		Geometry fusion = null;
		for (Entry<String, ArrayList> entry : buildings.entrySet()) {

			if (entry.getValue().get(3) instanceof Geometry) {
				Geometry bat = (Geometry) entry.getValue().get(3);
				if (fusion != null) {
					fusion = portion.intersection(bat).union(fusion);
				} else {
					fusion = portion.intersection(bat);
				}

			}

		}
		System.out.println(fusion);

		System.out
				.println("pourcentage d'occupation du territoire au coordonnées "
						+ x
						+ " et "
						+ y
						+ " est de "
						+ (Math
								.floor(((fusion.getArea() / portion.getArea()) * 10000)) / 100)
						+ "%");
	}

	public static Polygon testprbati(LineString facade) throws ParseException {
		LineString test1 = creationLsAngle(270);
		Coordinate[] cordl = test1.getCoordinates();
		double cordX = cordl[1].x;
		double cordY = cordl[1].y;
		/*
		 * Geometry build = (Geometry) buildings.get("batiments.1").get(3);
		 * ArrayList<LineString> ls = toLineString(build); LineString facade =
		 * ls.get(3);
		 */
		// System.out.println(build);
		Point pt = facade.getStartPoint();
		Point pt2 = facade.getEndPoint();
		//System.out.println(pt);
		GeometryFactory fact = new GeometryFactory();
		Coordinate[] cord = new Coordinate[5];
		//System.out.println(cord);
		Coordinate pt3 = new Coordinate();
		double tomul = facade.getLength() / (test1.getLength()*0.1);
		pt3.x = pt.getCoordinate().x + cordX * tomul;
		pt3.y = pt.getCoordinate().y + cordY * tomul;
		Coordinate pt4 = new Coordinate();
		pt4.x = pt2.getCoordinate().x + cordX * tomul;
		pt4.y = pt2.getCoordinate().y + cordY * tomul;
		cord[0] = pt.getCoordinate();
		cord[1] = pt2.getCoordinate();
		cord[3] = pt3;
		cord[2] = pt4;
		cord[4] = pt.getCoordinate();
		LinearRing lr = fact.createLinearRing(cord);
		// polygone representant lespace dombre du batiment...
		Polygon ld = fact.createPolygon(lr, null);
		//System.out.println(ld);

		return ld;

	}

	public static LineString creationLsAngle(double angle)
			throws ParseException {

		if (angle > 360 || angle < 0) {
			angle = angle % 360;
		}

		double nangle = 0;

		nangle = (450 - angle) % 360;
	//	System.out.println(nangle);

		double enradian = Math.toRadians(nangle);
		double corx = Math.cos(enradian);
		BigDecimal enra = new BigDecimal(corx);
		BigDecimal enra2 = enra.setScale(8, BigDecimal.ROUND_DOWN);
		corx = enra2.doubleValue();

		double sinx = Math.sin(enradian);
		BigDecimal senra = new BigDecimal(sinx);
		BigDecimal senra2 = senra.setScale(8, BigDecimal.ROUND_DOWN);
		sinx = senra2.doubleValue();

		//System.out.println(corx);
		//System.out.println(sinx);

		Coordinate c1 = new Coordinate(corx * 10, sinx * 10);
		Coordinate[] mals = new Coordinate[2];
		mals[0] = new Coordinate(0, 0);
		mals[1] = c1;
		GeometryFactory fact = new GeometryFactory();
		LineString ligneangle = fact.createLineString(mals);
		//System.out.println(ligneangle);

		return ligneangle;

	}

	public static void cacheParBati() throws ParseException {
		Geometry build = (Geometry) buildings.get("batiments.4").get(3);
		ArrayList<LineString> ls = toLineString(build);
		int i = 0;
		Geometry caches = null;
		System.out.println(build);
		while (i<ls.size()) {
			Polygon ombre = testprbati(ls.get(i));
			
			//Geometry ombre2= ombre.intersection(build);
			Geometry ombreWtFacade = ombre.difference(ls.get(i).buffer(0.01, 0, 2));
			/*System.out.println(ombre);
			System.out.println(ls.get(i));
			System.out.println(ombreWtFacade);*/
			if (caches != null) {
				caches = caches.union(ombreWtFacade);
				//System.out.println(caches);
			} else {

				caches = ombreWtFacade;
				//System.out.println(caches);

			}
			i++;
		}
		
		System.out.println(caches);
		
		i=0;
		while (i<ls.size()) {
			if (caches.intersection(ls.get(i)).getLength()> ls.get(i).getLength()*0.98) {
				System.out.println("la facade "+i+" est cache");
			}
			else 
				if (caches.intersection(ls.get(i)).getLength()>= ls.get(i).getLength()*0.4 ) {
					System.out.println("la facade "+i+" est partiellement cacbe");
				}
				else{
				System.out.println("la facade "+i+" est decouverte");
				
			}
			
			
			i++;
		}
	}
	
	public static void 	testVent() throws ParseException {
		Geometry build = (Geometry) buildings.get("batiments.1").get(3);
		LineString test1 = creationLsAngle(0);
		Coordinate[] cordl = test1.getCoordinates();
		double cordX = cordl[1].x;
		double cordY = cordl[1].y;
		Coordinate[] listCord =build.getCoordinates();
		GeometryFactory fact = new GeometryFactory();
		ArrayList<Point> mesPoints = new ArrayList<Point>();
		int i=listCord.length;
		while (i>0) {
			System.out.println(listCord[i-1]);
			
			Point p1 =fact.createPoint(listCord[i-1]);
			mesPoints.add(p1);
			i--;
		}
		int first=0;
		double max =0;
		i=0;
		while (i<mesPoints.size()) {
			
			if (mesPoints.get(i).getX()>max) {
				max=mesPoints.get(i).getX();
				first=i;
				System.out.println(first);
			}
			i++;
		}
		i=0;
		while (i< build.getNumPoints()-1) {
			if (first == build.getNumPoints()-1) {
				first=0;
				}
			System.out.println(first);
			double vectx = mesPoints.get(first+1).getX()-mesPoints.get(first).getX();
			double vecty = mesPoints.get(first+1).getY()-mesPoints.get(first).getY();
			
			
			double vectNx = vecty;
			double vectNy = -vectx;
			
			System.out.println(vectx+" "+vecty);
			
			double ww = cordX*vectNx+vectNy*cordY;
			double uu = Math.sqrt(cordX*cordX+cordY*cordY) * Math.sqrt(vectNx*vectNx+vectNy*vectNy);
			double cosangle = ww/uu;
			System.out.println(cosangle);
			i++;
			first++;
		}
		System.out.println(build);
		
		
		
	}
	
}
