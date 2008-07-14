/*
 * 12/02/2001 - 18:02:17
 *
 * Library geometrie3D - librairie incluant les principales géométries 3D
 * Copyright (C) 2001 Michaël MICHAUD
 * michael.michaud@free.fr
 * 
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.gdms.triangulation.michaelm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

// Version 0.6 :
// a - implementation de la methode hashCode de la
// classe PointT indispensable pour garantir l'unicité des points
// dans le HashSet
// b - l'ordre est défini par une distance croissante par rapport
// au point de coordonnees xIni, yIni (propriete statique initialisee
// dans le constructeur).
// c - tout le plan est triangulé, les triangles situés à l'exterieur
// de l'enveloppe convexe sont constitués de deux points de l'enveloppe
// et de l'objet INFINI (un PointT d'indice i = -1)

// Version 0.5 :
// Remplacement de la propriété booleenne "valide" des Triangle(s)
// par un entier permettant de signaler un segment de contrainte

/** Classe définissant une triangulation sur un tableau de Point3D. */
public class Triangulation {

	/** Les constantes. */
	private static long chrono;

	// ****************************************************************************
	// ****************************************************************************
	// ******************** Les variables ********************
	// ****************************************************************************
	// ****************************************************************************

	/** Tableau de sommets à trianguler. */
	public Coordinate[] pts;

	/** Tableau de références, sans doublon et trié à trianguler. */
	PointT[] pts_i;

	/**
	 * Tableau des lignes de contraintes. Chaque objet du tableau est une liste
	 * de PointT formant une ligne. Les segments composant ces lignes doivent se
	 * retrouver dans la triangulation.
	 */
	int[][] contraintes;

	/** Triangles constituant la triangulation. */
	List triangles;

	/**
	 * Permet de fixer une résolution (2 points ne seront pas séparés par une
	 * distance supérieure à res).
	 */
	// double res = 0.0;
	/** Tableau de lignes de contraintes. */
	// Ligne3D[] lignes;
	/**
	 * Indique s'il faut trianguler l'intérieur d'un polygone et exclure tous
	 * les triangles appartenant à l'enveloppe convexe mais n'appartenant pas au
	 * polygone.
	 */
	// private boolean onlyPoly = false;
	// private Ligne3DF_T poly;
	public static int delT = 1;
	Triangle t0; // triangle quelconque, exterieur à l'enveloppe convexe
	Triangle tCourant; // triangle courant
	private final PointT INFINI = new PointT(-1);

	private static double xIni = 0.0;
	private static double yIni = 0.0;
	private static int recursivityBound = 0;

	private Logger log = Logger.getLogger("Triangulation");

	// ****************************************************************************
	// ****************************************************************************
	// ******************** Les constructeurs ********************
	// ****************************************************************************
	// ****************************************************************************

	/**
	 * Constructeur de l'objet Triangulation.
	 * 
	 * @param points
	 *            tableau des points à trianguler.
	 * @param contraintes
	 *            tableau des segments de contrainte (paires de PointT)
	 */
	public Triangulation(Coordinate[] points, int[][] contraintes) {
		try {
			FileHandler fh = new FileHandler("Triangulation.log");
			fh.setFormatter(new SimpleFormatter());
			log.addHandler(fh);
			log.setLevel(Level.FINE);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		// Jeu de points unique en XY et trié suivant l'axe des x
		Set hs = new TreeSet();
		this.pts = points;
		// Initialisation d'un "centroïde" obtenu en moyennant
		// les coordonnées d'un point sur mille
		int count = 0;
		for (int i = 0; i < pts.length;) {
			xIni = xIni + pts[i].x;
			yIni = yIni + pts[i].y;
			i = i + 1000;
			count++;
		}
		xIni = xIni / count;
		yIni = yIni / count;
		long t0 = System.currentTimeMillis();
		// Les points de pts sont ordonnés et les doublons sont éliminés
		for (int i = 0; i < points.length; i++) {
			if (hs.add(new PointT(i)))
				count++;
		}
		long t1 = System.currentTimeMillis();
		System.out.println("Temps de chargement " + ((t1 - t0) / 1000.0)
				+ " secondes");
		this.contraintes = contraintes;
		this.pts_i = (PointT[]) hs.toArray(new PointT[] {});
		System.out.println("taille pts   : " + pts.length);
		System.out.println("taille pts_i : " + pts_i.length);
		hs = null;
		// Initialisation de la liste qui contiendra les triangles
		triangles = new ArrayList();
	}

	public void trianguler() {
		// Initialisation
		init();
		long t1 = System.currentTimeMillis();
		chrono = t1;
		// insertion des points à trianguler
		for (int i = 3; i < pts_i.length; i++) {
			inserer(pts_i[i]);
			// System.out.println("inserer point " + i);
			// Enlever les triangles détruits (Triangle.valide = false)
			if (delT > 100000) {
				nettoyerTriangles();
				delT = 0;
			}
			// Timer
			// if (i==1000 || i==10000 || i==100000 || i==1000000) {
			if (i % 10000 == 0) {
				System.out.print(i + " points triangulés en ");
				System.out.println((System.currentTimeMillis() - t1) / 1000.0
						+ " s");
			}
		}
		// Insertion des contraintes
		if (contraintes != null) {
			int breakLines = 0;
			t1 = System.currentTimeMillis();
			for (int i = 0; i < contraintes.length; i++) {
				for (int j = 0; j < contraintes[i].length - 1; j++) {
					breakLines++;
					PointT p1 = locateP(tCourant, pts[contraintes[i][j]]);
					PointT p2 = locateP(tCourant, pts[contraintes[i][j + 1]]);
					// contraindre la triangulation sur le segment p1-p2
					recursivityBound = 0;
					if (p1 != null && p2 != null && p1.i != p2.i) {
						// System.out.println("Ajouter contrainte " + p1.i + "-"
						// + p2.i);
						contraindre(p1, p2);
					} else {
						System.out.println("null");
					}
					if (breakLines % 10000 == 0) {
						System.out.print(breakLines + " breakLines added en ");
						System.out.println((System.currentTimeMillis() - t1)
								/ 1000.0 + " s");
					}
				}
			}
		}
		nettoyerTriangles();
		long t2 = System.currentTimeMillis();
		System.out.println(pts.length + " points triangulés en "
				+ ((t2 - t1) / 1000.0) + " secondes");
	}

	public void init() {
		// Initialise le premier triangle (le plus à gauche)
		Triangle t = new Triangle(pts_i[0], pts_i[1], pts_i[2]);
		// Initialise les triangles exterieurs à l'envelope convexe
		t0 = new Triangle(t.ppp[0], t.ppp[2], INFINI);
		Triangle t1 = new Triangle(t.ppp[2], t.ppp[1], INFINI);
		Triangle t2 = new Triangle(t.ppp[1], t.ppp[0], INFINI);
		// Crée les liens entre les 4 triangles initiaux
		t0.ttt[0] = t;
		t.ttt[2] = t0;
		t1.ttt[0] = t;
		t.ttt[1] = t1;
		t2.ttt[0] = t;
		t.ttt[0] = t2;
		t0.ttt[1] = t1;
		t1.ttt[2] = t0;
		t1.ttt[1] = t2;
		t2.ttt[2] = t1;
		t2.ttt[1] = t0;
		t0.ttt[2] = t2;
		triangles.add(t);
		triangles.add(t0);
		triangles.add(t1);
		triangles.add(t2);
		tCourant = t0;
		// printEnvelope();
	}

	/**
	 * insérer un point dans la triangulation
	 * 
	 * @param p
	 *            point à inserer dans la triangulation.
	 */
	public void inserer(PointT p) {
		// log.finer("Add point " + p.toString());
		int[] indices = addExtPoint(p);
		// Appliquer la regle de Delaunay sur tous les nouveaux triangles
		for (int i = indices[0] + 1; i < indices[1] - 1; i++) {
			recursivityBound = 0;
			delaunay((Triangle) triangles.get(i));
		}
	}

	/**
	 * Procédure permettant d'éliminer les triangles obsoletes et de libérer
	 * ainsi la mémoire.
	 */
	private void nettoyerTriangles() {
		List temp = new ArrayList();
		Triangle[] tt = new Triangle[triangles.size()];
		tt = (Triangle[]) triangles.toArray(tt);
		int size = triangles.size();
		for (int i = 0; i < size; i++) {
			// Triangle t = (Triangle)triangles.get(i);
			Triangle t = tt[i];
			if (t.property != -1) {
				temp.add(t);
			} else {
				// t.ttt[0] = null;
				// t.ttt[1] = null;
				// t.ttt[2] = null;
				t.ttt = null;
				t = null;
			}
		}
		triangles = temp;
		temp = null;
	}

	/**
	 * Recherche les points de l'enveloppe convexe visibles du point p et
	 * s'appuie dessus pour créer de nouveaux triangles.
	 * 
	 * @param p
	 *            point de vue
	 * @return l'indice du premier triangle créé dans la List triangles
	 */
	public int addPointOnRight(PointT p) {
		int indiceDebut = triangles.size();
		boolean first = true; // Premier segment visible ?
		boolean visible = false; // Visibilite d'un segment de l'enveloppe
		Triangle tp = null; // Triangle précédent
		Triangle outsideT = t0; // Premier triangle exterieur à l'enveloppe
		Triangle tIn = null; // Triangle entre le nouveau point et le segment
		// visible
		Triangle tOut1 = null; // Premier nouveau segment d'enveloppe
		Triangle tOut2 = null; // Deuxieme nouveau segment d'enveloppe
		while (true) {
			// Test la visibilité des segments de l'enveloppe
			if (ccw(pts[outsideT.ppp[0].i].x, pts[outsideT.ppp[0].i].y,
					pts[outsideT.ppp[1].i].x, pts[outsideT.ppp[1].i].y,
					pts[p.i].x, pts[p.i].y) > 0) {
				visible = true;
				// Nouveau triangle entre p et le segment visible
				tIn = new Triangle(outsideT.ppp[0], outsideT.ppp[1], p);
				outsideT.property = -1;
				lierC(tIn, 0, outsideT.ttt[0]);
				if (first) {
					// Nouveau triangle exterieur s'appuyant sur l'enveloppe
					tOut1 = new Triangle(outsideT.ppp[0], p, INFINI);
					tIn.ttt[2] = tOut1;
					tOut1.ttt[0] = tIn;
					tOut1.ttt[2] = outsideT.ttt[2];
					outsideT.ttt[2].ttt[1] = tOut1;
					if (outsideT.ppp[0].i == pts_i[0].i) {
						t0.property = -1;
						t0 = tOut1;
					}
					triangles.add(tOut1);
				} else {
					tIn.ttt[2] = tp;
					tp.ttt[1] = tIn;
				}
				tp = tIn;
				triangles.add(tIn);
				first = false;
			} else {
				if (visible) {
					visible = false;
					break;
				}
			}
			// triangle exterieur suivant dans le sens horaire
			outsideT = outsideT.ttt[1];
		}
		if (!visible) {
			// Nouveau triangle exterieur s'appuyant sur l'enveloppe
			tOut2 = new Triangle(p, outsideT.ppp[0], INFINI);
			tIn.ttt[1] = tOut2;
			tOut2.ttt[0] = tIn;
			tOut2.ttt[1] = outsideT;
			outsideT.ttt[2] = tOut2;
			tOut2.ttt[2] = tOut1;
			tOut1.ttt[1] = tOut2;
			triangles.add(tOut2);
		}
		return indiceDebut;
	}

	/**
	 * Recherche les points de l'enveloppe convexe visibles du point p et
	 * s'appuie dessus pour créer de nouveaux triangles.
	 * 
	 * @param p
	 *            point de vue
	 * @return l'indice du premier triangle créé dans la List triangles
	 */
	public int[] addExtPoint(PointT p) {
		int indiceDebut = triangles.size();
		int indiceFin = indiceDebut;
		boolean first = true; // Premier segment visible ?
		boolean visible = false; // Visibilite d'un segment de l'enveloppe
		boolean ready = false; // Visibilite d'un segment de l'enveloppe
		Triangle tp = null; // Triangle précédent
		Triangle outsideT = t0; // Premier triangle exterieur à l'enveloppe
		Triangle tIn = null; // Triangle entre le nouveau point et le segment
		// visible
		Triangle tOut1 = null; // Premier nouveau segment d'enveloppe
		Triangle tOut2 = null; // Deuxieme nouveau segment d'enveloppe
		// System.out.println("p.i " + p.i);
		while (true) {
			// Test la visibilité des segments de l'enveloppe
			if (ccw(pts[outsideT.ppp[0].i].x, pts[outsideT.ppp[0].i].y,
					pts[outsideT.ppp[1].i].x, pts[outsideT.ppp[1].i].y,
					pts[p.i].x, pts[p.i].y) > 0) {
				if (ready) {
					visible = true;
					// Nouveau triangle entre p et le segment visible
					tIn = new Triangle(outsideT.ppp[0], outsideT.ppp[1], p);
					outsideT.property = -1;
					delT++;
					lierC(tIn, 0, outsideT.ttt[0]);
					if (first) {
						// Nouveau triangle exterieur s'appuyant sur l'enveloppe
						tOut1 = new Triangle(outsideT.ppp[0], p, INFINI);
						tIn.ttt[2] = tOut1;
						tOut1.ttt[0] = tIn;
						tOut1.ttt[2] = outsideT.ttt[2];
						outsideT.ttt[2].ttt[1] = tOut1;
						// t0.valide=false;
						t0 = tOut1;
						triangles.add(tOut1);
						first = false;
					} else {
						tIn.ttt[2] = tp;
						tp.ttt[1] = tIn;
						if (!tp.isValid()) {
							log.warning("insertion tp " + tp.toStringAll()
									+ " invalide");
						}
					}
					tp = tIn;
					triangles.add(tIn);
				}
			} else {
				if (visible) {
					break;
				} else {
					ready = true;
				}
			}
			// triangle exterieur suivant dans le sens horaire
			outsideT = outsideT.ttt[1];
		}
		tOut2 = new Triangle(p, outsideT.ppp[0], INFINI);
		tIn.ttt[1] = tOut2;
		tOut2.ttt[0] = tIn;
		tOut2.ttt[1] = outsideT;
		outsideT.ttt[2] = tOut2;
		tOut2.ttt[2] = tOut1;
		tOut1.ttt[1] = tOut2;
		if (!tIn.isValid())
			System.out.println("insertion tIn " + tIn.toStringAll()
					+ " invalide");
		if (!tOut1.isValid())
			System.out.println("insertion tOut1 " + tOut1.toStringAll()
					+ " invalide");
		if (!tOut2.isValid())
			System.out.println("insertion tOut2 " + tOut2.toStringAll()
					+ " invalide");
		triangles.add(tOut2);
		indiceFin = triangles.size();
		return new int[] { indiceDebut, indiceFin };
	}

	/**
	 * Méthode itérative principale. Vérifie la propriété de Delaunay sur les
	 * triangles adjacents. Au cas ou la propriété n'est pas vérifiée, effectue
	 * un flip et réitère l'opération sur les nouveaux triangles.
	 * 
	 * @param t
	 *            triangle à vérifier et, le cas échéant à transformer en
	 *            triangle de Delaunay
	 */
	private void delaunay(Triangle t) {
		// De chaque coté du triangle
		if (recursivityBound++ > 10) {
			return;
		}
		// System.out.print(".");
		int cote = 0;
		for (cote = 0; cote < 3; cote++) {
			// Récupérer le point opposé
			int i = t.getOpposite(cote);
			// si cote est un segment de contrainte, passe au cote suivant
			int prop = cote == 0 ? 1 : cote == 1 ? 2 : 4;
			if ((t.property & prop) == prop)
				continue;
			// S'il existe et est situé dans le cercle circonscrit
			if (i != -1
					&& fastInCircle(t.ppp[0], t.ppp[1], t.ppp[2],
							t.ttt[cote].ppp[i]) > 0) {
				PointT p = t.ttt[cote].ppp[i];
				// Les 2 triangles sont annulés;
				t.property = -1;
				t.ttt[cote].property = -1;
				delT += 2;
				Triangle t1 = new Triangle(p, t.ppp[(cote + 2) % 3],
						t.ppp[cote]);
				Triangle t2 = new Triangle(p, t.ppp[(cote + 1) % 3],
						t.ppp[(cote + 2) % 3]);
				// Liaison entre les nouveaux triangles et leurs voisins
				t1.ttt[0] = t2;
				t2.ttt[2] = t1;
				lierC(t1, 1, t.ttt[(cote + 2) % 3]);
				lierC(t1, 2, t.ttt[cote].ttt[(i + 2) % 3]);
				lierC(t2, 1, t.ttt[(cote + 1) % 3]);
				lierC(t2, 0, t.ttt[cote].ttt[i]);
				triangles.add(t1);
				triangles.add(t2);
				tCourant = t2;
				// if (recursivityBound++>10) {return;}
				delaunay(t1);
				// if (recursivityBound++>10) {return;}
				delaunay(t2);
				break;
			} else
				;
		}
	}

	/**
	 * Return a positive value if the point pd lies inside the circle passing
	 * through pa, pb, and pc; a negative value if it lies outside; and zero if
	 * the four points are cocircular. The points pa, pb, and pc must be in
	 * counterclockwise order, or the sign of the result will be reversed.
	 */
	private double fastInCircle(PointT p1, PointT p2, PointT p3, PointT p4) {
		double adx, ady, bdx, bdy, cdx, cdy;
		double abdet, bcdet, cadet;
		double alift, blift, clift;

		adx = pts[p1.i].x - pts[p4.i].x;
		ady = pts[p1.i].y - pts[p4.i].y;
		bdx = pts[p2.i].x - pts[p4.i].x;
		bdy = pts[p2.i].y - pts[p4.i].y;
		cdx = pts[p3.i].x - pts[p4.i].x;
		cdy = pts[p3.i].y - pts[p4.i].y;

		abdet = adx * bdy - bdx * ady;
		bcdet = bdx * cdy - cdx * bdy;
		cadet = cdx * ady - adx * cdy;
		alift = adx * adx + ady * ady;
		blift = bdx * bdx + bdy * bdy;
		clift = cdx * cdx + cdy * cdy;

		return alift * bcdet + blift * cadet + clift * abdet;
	}

	/**
	 * Cette fonction permet de déterminer si le triangle p0-p1-p2 tourne dans
	 * le sens des aiguilles d'une montre (renvoie -1), ou dans le sens inverse
	 * (renvoie 1). La fonction renvoie 0 si le triangle est plat.
	 */
	public static int ccw(double p0x, double p0y, double p1x, double p1y,
			double p2x, double p2y) {
		double dx1 = p1x - p0x;
		double dy1 = p1y - p0y;
		double dx2 = p2x - p0x;
		double dy2 = p2y - p0y;
		if (dx1 * dy2 > dy1 * dx2)
			return 1;
		else if (dx1 * dy2 < dy1 * dx2)
			return -1;
		else {
			if (dx1 * dx2 < 0 || dy1 * dy2 < 0)
				return -1;
			else if (dx1 * dx1 + dy1 * dy1 >= dx2 * dx2 + dy2 * dy2)
				return 0;
			else
				return 1;
		}
	}

	/** lier 2 triangles. */
	private boolean lier(Triangle T1, Triangle T2) {
		// if (T1==null || T2==null) return false;
		try {
			if (T1.ppp[0].i == T2.ppp[0].i && T1.ppp[1].i == T2.ppp[2].i) {
				T1.ttt[0] = T2;
				T2.ttt[2] = T1;
				return true;
			} else if (T1.ppp[0].i == T2.ppp[2].i && T1.ppp[1].i == T2.ppp[1].i) {
				T1.ttt[0] = T2;
				T2.ttt[1] = T1;
				return true;
			} else if (T1.ppp[0].i == T2.ppp[1].i && T1.ppp[1].i == T2.ppp[0].i) {
				T1.ttt[0] = T2;
				T2.ttt[0] = T1;
				return true;
			} else if (T1.ppp[1].i == T2.ppp[0].i && T1.ppp[2].i == T2.ppp[2].i) {
				T1.ttt[1] = T2;
				T2.ttt[2] = T1;
				return true;
			} else if (T1.ppp[1].i == T2.ppp[2].i && T1.ppp[2].i == T2.ppp[1].i) {
				T1.ttt[1] = T2;
				T2.ttt[1] = T1;
				return true;
			} else if (T1.ppp[1].i == T2.ppp[1].i && T1.ppp[2].i == T2.ppp[0].i) {
				T1.ttt[1] = T2;
				T2.ttt[0] = T1;
				return true;
			} else if (T1.ppp[2].i == T2.ppp[0].i && T1.ppp[0].i == T2.ppp[2].i) {
				T1.ttt[2] = T2;
				T2.ttt[2] = T1;
				return true;
			} else if (T1.ppp[2].i == T2.ppp[2].i && T1.ppp[0].i == T2.ppp[1].i) {
				T1.ttt[2] = T2;
				T2.ttt[1] = T1;
				return true;
			} else if (T1.ppp[2].i == T2.ppp[1].i && T1.ppp[0].i == T2.ppp[0].i) {
				T1.ttt[2] = T2;
				T2.ttt[0] = T1;
				return true;
			} else {
				return false;
			}
		} catch (NullPointerException npe) {
			return false;
		}
	}

	/** lier les triangles T1 et T2 adjacents à T1 par son coté c. */
	private boolean lierC(Triangle T1, int c, Triangle T2) {
		// if (T1==null || T2==null) return false;
		try {
			int c_prop = c == 0 ? 1 : c == 1 ? 2 : 4;
			if (T1.ppp[c].i == T2.ppp[0].i) {
				if ((T2.property & 4) == 4 && (T1.property & c_prop) != c_prop)
					T1.property += c_prop;
				T1.ttt[c] = T2;
				T2.ttt[2] = T1;
				return true;
			} else if (T1.ppp[c].i == T2.ppp[2].i) {
				if ((T2.property & 2) == 2 && (T1.property & c_prop) != c_prop)
					T1.property += c_prop;
				T1.ttt[c] = T2;
				T2.ttt[1] = T1;
				return true;
			} else if (T1.ppp[c].i == T2.ppp[1].i) {
				if ((T2.property & 1) == 1 && (T1.property & c_prop) != c_prop)
					T1.property += c_prop;
				T1.ttt[c] = T2;
				T2.ttt[0] = T1;
				return true;
			} else {
				return false;
			}
		} catch (NullPointerException npe) {
			log.severe("NullPointerException dans la méthode lierC");
			return false;
		}
	}

	/**
	 * Insère un nouveau point ne faisant pas partie de la liste ordonnée
	 * initiale.
	 * 
	 * @param tIni
	 *            triangle quelconque à partir duquel le point p est localisé
	 *            par le jeu des relations d'adjacence.
	 * @param p
	 *            point à ajouter à la triangulation.
	 */
	public void insert(Triangle tIni, Coordinate p) {
		Triangle t = locate(tIni, p);
		// Le point p est situé en dehors de l'enveloppe
		if (t.ppp[2].i == -1) {
			Coordinate[] ptsTemp = new Coordinate[pts.length + 1];
			System.arraycopy(pts, 0, ptsTemp, 0, pts.length);
			this.pts = ptsTemp;
			ptsTemp = null;
			PointT[] pts_iTemp = new PointT[pts_i.length + 1];
			System.arraycopy(pts_i, 0, pts_iTemp, 0, pts_i.length);
			pts_i = pts_iTemp;
			pts_iTemp = null;
			pts[pts.length - 1] = p;
			pts_i[pts_i.length - 1] = new PointT(pts.length - 1);
			inserer(pts_i[pts_i.length - 1]);
			return;
		}
		// Le point p est situé à l'intérieur de l'enveloppe
		double ccw0 = ccw(p.x, p.y, pts[t.ppp[0].i].x, pts[t.ppp[0].i].y,
				pts[t.ppp[1].i].x, pts[t.ppp[1].i].y);
		double ccw1 = ccw(p.x, p.y, pts[t.ppp[1].i].x, pts[t.ppp[1].i].y,
				pts[t.ppp[2].i].x, pts[t.ppp[2].i].y);
		double ccw2 = ccw(p.x, p.y, pts[t.ppp[2].i].x, pts[t.ppp[2].i].y,
				pts[t.ppp[0].i].x, pts[t.ppp[0].i].y);
		if (ccw0 <= 0 || ccw1 <= 0 || ccw2 <= 0)
			return;
		if (t.ppp[2].i == -1)
			return;
		if (pts[t.ppp[0].i].x == p.x && pts[t.ppp[0].i].y == p.y)
			return;
		if (pts[t.ppp[1].i].x == p.x && pts[t.ppp[1].i].y == p.y)
			return;
		if (pts[t.ppp[2].i].x == p.x && pts[t.ppp[2].i].y == p.y)
			return;
		if (pts[t.ppp[0].i].x == p.x && pts[t.ppp[0].i].y == p.y)
			return;
		if (pts[t.ppp[1].i].x == p.x && pts[t.ppp[1].i].y == p.y)
			return;
		if (pts[t.ppp[2].i].x == p.x && pts[t.ppp[2].i].y == p.y)
			return;
		if (pts[t.ppp[0].i].x == p.x && pts[t.ppp[0].i].y == p.y)
			return;
		if (pts[t.ppp[1].i].x == p.x && pts[t.ppp[1].i].y == p.y)
			return;
		if (pts[t.ppp[2].i].x == p.x && pts[t.ppp[2].i].y == p.y)
			return;
		Coordinate[] ptsTemp = new Coordinate[pts.length + 1];
		System.arraycopy(pts, 0, ptsTemp, 0, pts.length);
		this.pts = ptsTemp;
		ptsTemp = null;
		PointT[] pts_iTemp = new PointT[pts_i.length + 1];
		System.arraycopy(pts_i, 0, pts_iTemp, 0, pts_i.length);
		pts_i = pts_iTemp;
		pts_iTemp = null;
		pts[pts.length - 1] = p;
		pts_i[pts_i.length - 1] = new PointT(pts.length - 1);
		Triangle tn0 = new Triangle(t.ppp[0], t.ppp[1], pts_i[pts_i.length - 1]);
		Triangle tn1 = new Triangle(t.ppp[1], t.ppp[2], pts_i[pts_i.length - 1]);
		Triangle tn2 = new Triangle(t.ppp[2], t.ppp[0], pts_i[pts_i.length - 1]);
		t.property = -1;
		delT++;
		lierC(tn0, 0, t.ttt[0]);
		lierC(tn1, 0, t.ttt[1]);
		lierC(tn2, 0, t.ttt[2]);
		tn0.ttt[1] = tn1;
		tn1.ttt[2] = tn0;
		tn1.ttt[1] = tn2;
		tn2.ttt[2] = tn1;
		tn2.ttt[1] = tn0;
		tn0.ttt[2] = tn2;
		triangles.add(tn0);
		triangles.add(tn1);
		triangles.add(tn2);
		recursivityBound = 0;
		delaunay(tn0);
		recursivityBound = 0;
		delaunay(tn1);
		recursivityBound = 0;
		delaunay(tn2);
	}

	/**
	 * Fonction booleenne renvoyant vrai s'il existe déjà une arete de triangle
	 * entre le point p1 appartenant au triangle t1 et le point p2. Cette
	 * fonction est utilisée comme condition d'arrêt lors de la subdivision des
	 * segments de contrainte.
	 * 
	 * @param p1
	 *            premier point
	 * @param t1
	 *            un triangle dont p1 constitue un sommet.
	 * @param p2
	 *            point dont on veut savoir s'il est connecté à p1.
	 * @return un Triangle reliant p1 et p2 ou null sinon
	 */
	public Triangle isLinked(PointT p1, Triangle t1, PointT p2) {
		int pos = 0;
		if (p1.i == t1.ppp[0].i)
			pos = 0;
		else if (p1.i == t1.ppp[1].i)
			pos = 1;
		else if (p1.i == t1.ppp[2].i)
			pos = 2;
		else
			return null;
		if (p2.i == t1.ppp[(pos + 1) % 3].i)
			return t1;
		if (p2.i == t1.ppp[(pos + 2) % 3].i)
			return t1;
		int indice = t1.ppp[(pos + 1) % 3].i;
		Triangle t2 = t1.ttt[(pos + 2) % 3];
		while (true) {
			if (p1.i == t2.ppp[0].i)
				pos = 0;
			else if (p1.i == t2.ppp[1].i)
				pos = 1;
			else if (p1.i == t2.ppp[2].i)
				pos = 2;
			else
				return null;
			if (p2.i == t2.ppp[(pos + 1) % 3].i)
				return t2;
			if (p2.i == t2.ppp[(pos + 2) % 3].i)
				return t2;
			if (t2.ppp[(pos + 1) % 3].i == indice)
				break;
			t2 = t2.ttt[(pos + 2) % 3];
		}
		return null;
	}

	/** Ajouter une contrainte sur le segment p1-p2. */
	public void contraindre(PointT p1, PointT p2) {
		// log.fine("Contraindre sur : " + p1.toString() + " - " +
		// p2.toString());
		if (recursivityBound++ > 10) {
			return;
		}
		if (pts[p1.i].distance(pts[p2.i]) < 0.01) {
			return;
		}
		// Triangle t1 = locate(tCourant, p1);
		// Triangle t2 = locate(tCourant, p2);
		Triangle t1 = locate(tCourant, pts[p1.i]);
		Triangle t2 = locate(tCourant, pts[p2.i]);
		// log.info("Ajouter contrainte " + p1.i + "-" + p2.i + " de coordonnees
		// " + p1 + " " + p2);
		Triangle t3 = isLinked(p2, t2, p1);
		if (null != t3) {
			if (t3.ppp[0].i == p1.i && t3.ppp[1].i == p2.i) {
				t3.setContrainte(0);
			} else if (t3.ppp[1].i == p1.i && t3.ppp[2].i == p2.i) {
				t3.setContrainte(1);
			} else if (t3.ppp[2].i == p1.i && t3.ppp[0].i == p2.i) {
				t3.setContrainte(2);
			} else if (t3.ppp[0].i == p1.i && t3.ppp[2].i == p2.i) {
				t3.setContrainte(2);
			} else if (t3.ppp[1].i == p1.i && t3.ppp[0].i == p2.i) {
				t3.setContrainte(0);
			} else if (t3.ppp[2].i == p1.i && t3.ppp[1].i == p2.i) {
				t3.setContrainte(1);
			}
			return;
		}
		// if (p1.i==t2.ppp[0].i || p1.i==t2.ppp[1].i || p1.i==t2.ppp[2].i)
		// return;
		Coordinate pi = new Coordinate((pts[p1.i].x + pts[p2.i].x) / 2,
				(pts[p1.i].y + pts[p2.i].y) / 2,
				(pts[p1.i].z + pts[p2.i].z) / 2);
		// if (Math.abs(t1.isAligned(pi)*t2.isAligned(pi))<0.000001) return;
		insert(tCourant, pi);
		int indice_i = pts_i.length - 1;
		contraindre(pts_i[indice_i], p1);
		contraindre(pts_i[indice_i], p2);
		// recursivityBound = 0;
	}

	/**
	 * Localise le triangle contenant le point p.
	 * 
	 * @param tIni
	 *            un triangle quelconque à partir duquel va s'effectuer la
	 *            localisation.
	 * @param p
	 *            point à localiser.
	 * @return un triangle contenant p (inclusion stricte ou limite. Retourne
	 *         null si p est à l'exterieur de l'enveloppe convexe
	 */
	public Triangle locate(Triangle tIni, Coordinate p) {
		Triangle t = tIni;
		Triangle[] lastTriangles = new Triangle[3];
		lastTriangles[0] = tIni;
		// S'il s'agit d'un triangle périphérique (ppp[2]=-1)
		// partir d'un triangle adjacent non périphérique
		if (t.ppp[2].i == -1)
			t = t.ttt[0];
		int loop = 0;
		while (t.ppp[2].i != -1) {
			int next = nextTriangle(t, p);
			if (next < 0) {
				break;
			} else {
				t = t.ttt[next];
				lastTriangles[loop % 3] = t;
				if (t.ppp[2].i == -1) {
					log.info("locate " + p.toString() + " : EXTERIEUR");
					return t;
				}
			}
			/*
			 * if (++loop>10000) { log.warning("More than 10000 loops to find : " +
			 * p.toString()); break; }
			 */
			if (++loop % 100 == 0 && lastTriangles[0] != null
					&& lastTriangles[1] != null && lastTriangles[2] != null) {
				if (lastTriangles[2].equals(lastTriangles[1])
						|| lastTriangles[2].equals(lastTriangles[0])) {
					log.warning("Break the infinite loop to : " + p.toString());
					break;
				}
			}
		}
		// log.info("locate " + p.toString() + " : " + t.toString());
		return t;
	}

	/**
	 * Localise le triangle contenant le point p.
	 * 
	 * @param tIni
	 *            un triangle quelconque à partir duquel va s'effectuer la
	 *            localisation.
	 * @param p
	 *            point à localiser.
	 * @return un triangle contenant p (inclusion stricte ou limite.
	 */
	/*
	 * public Triangle locate(Triangle tIni, PointT p) { Triangle t = tIni;
	 * Triangle previous = tIni; Triangle previousprevious = tIni; if
	 * (t.ppp[2].i==-1) t = t.ttt[0]; // Compteur pour éviter de rentrer dans
	 * une boucle infinie dans // certaines configurations non encore
	 * identifiées int loop = 0; while (t.ppp[2].i!=-1) { if (++loop>10000) {
	 * //System.out.println("Point " + p.i + " (" + pts[p.i].toString() + ")");
	 * //System.out.println("Triangle " + tIni.toStringAll());
	 * System.out.println("Point recherché " + Integer.toString(p.i) +
	 * pts[p.i]); Coordinate[] cccc = new Coordinate[]{pts[t.ppp[0].i],
	 * pts[t.ppp[1].i], pts[t.ppp[2].i], pts[t.ppp[0].i]}; GeometryFactory gf =
	 * new GeometryFactory(); System.out.println("Triangle " +
	 * Integer.toString(t.ppp[0].i) + "-" + Integer.toString(t.ppp[1].i) + "-" +
	 * Integer.toString(t.ppp[2].i)); Polygon poly =
	 * gf.createPolygon(gf.createLinearRing(cccc), new LinearRing[0]);
	 * System.out.println(poly); //System.out.println("locate (Triangle, PointT) " +
	 * t.toString()); if (loop>10010) return null; } int next = nextTriangle(t,
	 * p); if (next<0) {break;} else { previousprevious = previous; previous =
	 * t; t = t.ttt[next]; // Compare previous and next to check if we don't
	 * enter an // infinite loop (25/08/03)
	 * if(previousprevious.ppp[0].i==t.ppp[0].i &&
	 * previousprevious.ppp[1].i==t.ppp[1].i){ break; } if (t==null ||
	 * t.ppp[2].i==-1) { log.info("locate " + p.toString() + " : EXTERIEUR");
	 * return null; } } } return t; }
	 */

	/** localise le triangle contenant le point p. */
	public PointT locateP(Triangle tIni, Coordinate p) {
		int loop = 0;
		Triangle t = tIni;
		Triangle[] lastTriangles = new Triangle[3];
		lastTriangles[0] = t;
		if (t.ppp[2].i == -1)
			t = t.ttt[0];
		while (t.ppp[2].i != -1) {
			int next = nextTriangle(t, p);
			if (next == -111)
				return t.ppp[0];
			else if (next == -222)
				return t.ppp[1];
			else if (next == -333)
				return t.ppp[2];
			else if (next == -11)
				return null;
			else if (next == -22)
				return null;
			else if (next == -33)
				return null;
			else if (next == -1)
				return null;
			else {
				t = t.ttt[next];
				lastTriangles[loop % 3] = t;
			}
			loop++;
			if (loop % 100 == 0 && lastTriangles[0] != null
					&& lastTriangles[1] != null && lastTriangles[2] != null) {
				if (lastTriangles[2].equals(lastTriangles[1])
						|| lastTriangles[2].equals(lastTriangles[0])
						|| (loop > 10000)) {
					System.out.println("Recherche de p = " + p.toString());
					System.out.println("Triangle0 = "
							+ lastTriangles[0].toStringAll());
					System.out.println("Triangle1 = "
							+ lastTriangles[1].toStringAll());
					System.out.println("Triangle2 = "
							+ lastTriangles[2].toStringAll());
					log.warning("Break the infinite loop to : " + p.toString());
					break;
				}
			}
		}
		return null;
	}

	/**
	 * Trouve le triangle adjacent à t dans la direction de p.
	 * 
	 * @param t
	 *            triangle de départ
	 * @param p
	 *            point indiquant la direction
	 * @return un entier donnant l'information suivante -111, -222, -333 : point
	 *         p sur un sommet de t -11, -22, -33 : point p sur un coté de t -1 :
	 *         point p à l'interieur 0, 1 ou 2 : coté du triangle suivant
	 */
	public int nextTriangle(Triangle t, Coordinate p) {
		// ccw0<0 || ccw1<0 || ccw2<0 ==> p strictement à l'exterieur
		int ccw0 = ccw(pts[t.ppp[0].i].x, pts[t.ppp[0].i].y, pts[t.ppp[1].i].x,
				pts[t.ppp[1].i].y, p.x, p.y);
		if (ccw0 < 0)
			return 0;
		int ccw1 = ccw(pts[t.ppp[1].i].x, pts[t.ppp[1].i].y, pts[t.ppp[2].i].x,
				pts[t.ppp[2].i].y, p.x, p.y);
		if (ccw1 < 0)
			return 1;
		int ccw2 = ccw(pts[t.ppp[2].i].x, pts[t.ppp[2].i].y, pts[t.ppp[0].i].x,
				pts[t.ppp[0].i].y, p.x, p.y);
		if (ccw2 < 0)
			return 2;
		// ccw0>0 && ccw1>0 && ccw2>0 ==> p strictement à l'interieur
		if (ccw0 > 0 && ccw1 > 0 && ccw2 > 0)
			return -1;
		else {
			// cas où p est un sommet du triangle
			if (pts[t.ppp[0].i].equals2D(p))
				return -111;
			else if (pts[t.ppp[1].i].equals2D(p))
				return -222;
			else if (pts[t.ppp[2].i].equals2D(p))
				return -333;
			// cas où p est sur un coté du triangle
			else if (ccw0 == 0)
				return -11;
			else if (ccw1 == 0)
				return -22;
			else if (ccw2 == 0)
				return -33;
			else
				return -2; // ERREUR
		}
	}

	/**
	 * Trouve le triangle adjacent à t dans la direction de p.
	 * 
	 * @param t
	 *            triangle de départ
	 * @param p
	 *            point indiquant la direction
	 * @return un entier donnant l'information suivante -111, -222, -333 : point
	 *         p sur un sommet de t -11, -22, -33 : point p sur un coté de t -1 :
	 *         point p à l'interieur 0, 1 ou 2 : coté du triangle suivant
	 */
	public int nextTriangle(Triangle t, PointT p) {
		// p est un des sommets du triangle t
		if (t.ppp[0].i == p.i)
			return -111;
		else if (t.ppp[1].i == p.i)
			return -222;
		else if (t.ppp[2].i == p.i)
			return -333;
		// p n'est pas un sommet du triangle t
		else {
			int ccw0 = ccw(pts[t.ppp[0].i].x, pts[t.ppp[0].i].y,
					pts[t.ppp[1].i].x, pts[t.ppp[1].i].y, pts[p.i].x,
					pts[p.i].y);
			if (ccw0 < 0)
				return 0;
			int ccw1 = ccw(pts[t.ppp[1].i].x, pts[t.ppp[1].i].y,
					pts[t.ppp[2].i].x, pts[t.ppp[2].i].y, pts[p.i].x,
					pts[p.i].y);
			if (ccw1 < 0)
				return 1;
			int ccw2 = ccw(pts[t.ppp[2].i].x, pts[t.ppp[2].i].y,
					pts[t.ppp[0].i].x, pts[t.ppp[0].i].y, pts[p.i].x,
					pts[p.i].y);
			if (ccw0 < 2)
				return 2;
			// ccw0>0 && ccw1>0 && ccw2>0 ==> p strictement à l'interieur
			if (ccw0 > 0 && ccw1 > 0 && ccw2 > 0)
				return -1;
			// cas où p est sur un coté du triangle
			if (ccw0 == 0)
				return -11;
			if (ccw1 == 0)
				return -22;
			if (ccw2 == 0)
				return -33;
			return -2; // ERREUR
		}
	}

	private PointT[] getEnvelope() {
		ArrayList envelope = new ArrayList();
		envelope.add(t0.ppp[0]);
		Triangle t = t0;
		while (t.ppp[1].i != t0.ppp[0].i) {
			t = t.ttt[1];
			envelope.add(t.ppp[0]);
		}
		envelope.add(t.ppp[1]);
		return (PointT[]) envelope.toArray(new PointT[] {});
	}

	public Coordinate[] getTriangulatedPoints() {
		List nouveau = new ArrayList();
		for (int i = 0; i < triangles.size(); i++) {
			Triangle t = (Triangle) triangles.get(i);
			if (null != t && t.property != -1 && t.ppp[2].i != -1) {
				nouveau.add(pts[t.ppp[0].i]);
				nouveau.add(pts[t.ppp[1].i]);
				nouveau.add(pts[t.ppp[2].i]);
			}
		}
		return (Coordinate[]) nouveau.toArray(new Coordinate[] {});
	}

	public List getValidTriangles(Geometry g) {
		List list = new ArrayList();
		for (int i = 0; i < triangles.size(); i++) {
			Triangle t = (Triangle) triangles.get(i);
			// Elimine les triangles nuls, les triangles invalide
			// et les triangles exterieurs à l'enveloppe convexe
			if (null != t && t.property != -1 && t.ppp[2].i != -1) {
				log.info(Integer.toString(t.ppp[0].i));
				log.info(Integer.toString(t.ppp[1].i));
				log.info(Integer.toString(t.ppp[2].i));
				log
						.info("triangles size "
								+ Integer.toString(triangles.size()));
				if (g == null)
					list.add(t);
				else {
					com.vividsolutions.jts.geom.Triangle gt = new com.vividsolutions.jts.geom.Triangle(
							pts[t.ppp[0].i], pts[t.ppp[1].i], pts[t.ppp[2].i]);
					Point p = new GeometryFactory().createPoint(gt.inCentre());
					if (g.distance(p) < 0.001)
						list.add(t);
				}
			}
		}
		return list;
	}

	public Coordinate[] getUpdatedPointArray() {
		return this.pts;
	}

	public String toString() {
		return "non implémenté";
	}

	// Classe PointT définissant l'indice d'un points dans le tableau.
	// L'interface comparable permet de trier les points
	// et la surcharge des méthodes equals() et hashcode() de garantir
	// leur unicité en 2D
	public class PointT implements Comparable {

		public int i = 0; // indice dans le tableau de point

		public PointT(int i) {
			this.i = i;
		}

		/** interface comparable pour le tri des objets suivant l'axe des x. */
		public int compareTo(Object o) {
			if (o instanceof PointT && i >= 0 && ((PointT) o).i >= 0) {
				double dx1 = pts[i].x - xIni;
				double dy1 = pts[i].y - yIni;
				double dx2 = pts[((PointT) o).i].x - xIni;
				double dy2 = pts[((PointT) o).i].y - yIni;
				double r = (dx1 * dx1 + dy1 * dy1) - (dx2 * dx2 + dy2 * dy2);
				if (r > 0)
					return 1;
				else if (r < 0)
					return -1;
				else {
					double dx = dx2 - dx1;
					if (dx > 0)
						return 1;
					else if (dx < 0)
						return -1;
					else {
						double dy = dy2 - dy1;
						return dy > 0 ? 1 : dy < 0 ? -1 : 0;
					}
				}
			} else
				return -1;
		}

		/** Surcharge de la méthode equals. */
		public boolean equals(Object obj) {
			if (obj instanceof PointT) {
				PointT p = (PointT) obj;
				return (pts[this.i].x == pts[p.i].x && pts[this.i].y == pts[p.i].y);
			} else
				return false;
		}

		public int hashCode() {
			return (new Double(pts[i].x)).hashCode()
					+ (new Double(pts[i].y)).hashCode();
		}

		public String toString() {
			return pts[i].toString();
		}

	}

	/** Classe Triangle défini un triangle et ses voisins. */
	public class Triangle {
		// points d'angle dans le sens trigo (anti-horaire)
		public PointT[] ppp = new PointT[3];
		// triangles voisins (T1 à droite de p[0]-p[1])
		Triangle[] ttt = new Triangle[3];
		// indique si le triangle est valide
		// boolean valide = true;
		// Propriété du triangle (remplace et enrichie le booleen valide)
		int property = 0;

		// property = -1 : obsolète (correspond à valide = false)
		// property = 0 : normal (triangle actif sujet à modification)
		// property = 1 : le coté 0 est un segment de contrainte
		// property = 2 : le coté 1 est un segment de contrainte
		// property = 4 : le coté 2 est un segment de contrainte
		// remarque : à enrichir avec des notions de frontières
		// exterieures et interieures

		Triangle(PointT p1, PointT p2, PointT p3) {
			if (p3.i == -1
					|| ccw(pts[p1.i].x, pts[p1.i].y, pts[p2.i].x, pts[p2.i].y,
							pts[p3.i].x, pts[p3.i].y) >= 0) {
				this.ppp[0] = p1;
				this.ppp[1] = p2;
				this.ppp[2] = p3;
			} else {
				this.ppp[0] = p1;
				this.ppp[2] = p2;
				this.ppp[1] = p3;
			}
		}

		/**
		 * Cherche l'indice (0,1 ou 2) du point opposé à ce Triangle dans le
		 * Triangle adjacent au coté i-(i+1)
		 * 
		 * @param cote
		 *            numero du triangle adjacent dont on charche le sommet
		 *            opposé.
		 * @return le numero d'ordre du sommet opposé au Triangle dans le
		 *         triangle adjacent par le cote numero "cote"
		 */
		public int getOpposite(int cote) {
			// Test si i est valide et non nul
			try {
				int indice = ppp[cote].i;
				if (ttt[cote].ppp[2].i == -1)
					return -1;
				// This est opposé à ttt[cote] par son cote 0
				else if (ttt[cote].ppp[1].i == indice) {
					return 2;
				}
				// This est opposé à ttt[cote] par son cote 1
				else if (ttt[cote].ppp[2].i == indice) {
					return 0;
				}
				// This est opposé à ttt[cote] par son cote 2
				else if (ttt[cote].ppp[0].i == indice) {
					return 1;
				} else
					return -1;
			} catch (NullPointerException npe) {
				log.severe("NullPointerException dans le triangle "
						+ this.toStringAll());
				return -1;
			}
		}

		/**
		 * Ajoute une contrainte sur un coté et met à jour le triangle adjacent.
		 */
		public void setContrainte(int cote) {
			if (cote == 0 && ((property & 1) != 1))
				property += 1;
			else if (cote == 1 && ((property & 2) != 2))
				property += 2;
			else if (cote == 2 && ((property & 4) != 4))
				property += 4;
			else
				;
			int coteOpp = (getOpposite(cote) + 1) % 3;
			if (coteOpp == 0 && ((ttt[cote].property & 1) != 1))
				ttt[cote].property += 1;
			else if (coteOpp == 1 && ((ttt[cote].property & 2) != 2))
				ttt[cote].property += 2;
			else if (coteOpp == 2 && ((ttt[cote].property & 4) != 4))
				ttt[cote].property += 4;
			else
				;
		}

		/** Teste la validité d'un triangle. */
		public boolean isValid() {
			if (ppp != null) {
				if (ppp[0] == null)
					return false;
				if (ppp[1] == null)
					return false;
				if (ppp[2] == null)
					return false;
			} else
				return false;
			if (ttt != null) {
				if (ttt[0] == null)
					return false;
				if (ttt[1] == null)
					return false;
				if (ttt[2] == null)
					return false;
			} else
				return false;
			if (property == -1)
				return false;
			return true;
		}

		/** Représentation d'un triangle par les numéro de ses 3 sommets. */
		public String toString() {
			StringBuffer sb = new StringBuffer("Triangle ");
			if (ppp != null && ppp[0] != null)
				sb.append(ppp[0].i + "-");
			else
				sb.append("null-");
			if (ppp != null && ppp[1] != null)
				sb.append(ppp[1].i + "-");
			else
				sb.append("null-");
			if (ppp != null && ppp[2] != null)
				sb.append(ppp[2].i);
			else
				sb.append("null");
			return sb.toString();
		}

		/**
		 * Représentation d'un triangle par les numéro de ses 3 sommets ainsi
		 * que par les sommets des trois Triangles adjacents.
		 */
		public String toStringAll() {
			StringBuffer sb = new StringBuffer(this.toString() + "\n");
			sb.append("   (voisins : ");
			if (ttt != null && ttt[0] != null)
				sb.append(ttt[0].toString());
			sb.append(" - ");
			if (ttt != null && ttt[1] != null)
				sb.append(ttt[1].toString());
			sb.append(" - ");
			if (ttt != null && ttt[2] != null)
				sb.append(ttt[2].toString());
			sb.append(")");
			return sb.toString();
		}

		/**
		 * Ce test d'égalité ne renvoie true que si les deux triangles pointent
		 * sur les mêmes PointT, dans le même ordre (pas de test d'égalité des
		 * coordonnées).
		 */
		public boolean equals(Triangle t) {
			return (ppp[0].i == t.ppp[0].i && ppp[1].i == t.ppp[1].i && ppp[2].i == t.ppp[2].i);
		}
	}

}