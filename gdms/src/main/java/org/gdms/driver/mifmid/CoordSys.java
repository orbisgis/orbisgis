/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.driver.mifmid;

import java.util.StringTokenizer;

/** 
 * Classe permettant de lire et d'�crire les informations sur le Syst�me de
 * coordonn�es contenues dans un fichier MIF
 * @author Micha�l Michaud
 * @version 4.3 (2009-06-13)
 */
public class CoordSys {

        //**************************************************************************
        //********************         variables statiques        ******************
        //**************************************************************************
        //**************************************************************************
        //********************         variables globales         ******************
        //**************************************************************************
        String type = "Earth";
        Projection projection;
        Affine affine;
        Bounds bounds;

        //**************************************************************************
        //**************************************************************************
        //********************          Les constructeurs      ********************
        //**************************************************************************
        //**************************************************************************
        public CoordSys(String coordSysString) {
                StringTokenizer st = new StringTokenizer(coordSysString, " ,");
                String token;
                while (st.hasMoreTokens()) {
                        token = st.nextToken();
                        if (token.equalsIgnoreCase("CoordSys")) {
                                continue;
                        } else if (token.equalsIgnoreCase("Earth")) {
                                type = "Earth";
                                token = st.nextToken();
                                if (token.equals("Projection")) {
                                        projection = new Projection();
                                        projection.type = st.nextToken();
                                        projection.datum = st.nextToken();
                                        // 999, EllipsoidNumber, dX, dY, dZ
                                        if (projection.datum.startsWith("999")) {
                                                projection.datum += (", " + st.nextToken());
                                                projection.datum += (", " + st.nextToken());
                                                projection.datum += (", " + st.nextToken());
                                                projection.datum += (", " + st.nextToken());
                                        }
                                        // 9999, EllipsoidNumber, dX, dY, dZ, EX, EY, EZ, m, PrimeMeridian
                                        if (projection.datum.startsWith("9999")) {
                                                projection.datum += (", " + st.nextToken());
                                                projection.datum += (", " + st.nextToken());
                                                projection.datum += (", " + st.nextToken());
                                                projection.datum += (", " + st.nextToken());
                                                projection.datum += (", " + st.nextToken());
                                        }
                                        //projection.unitname = st.nextToken();
                                        int i = 0;
                                        while (st.hasMoreTokens()) {
                                                token = st.nextToken(" ,()");
                                                if (token.equalsIgnoreCase("Affine")) {
                                                        break;
                                                }
                                                if (token.equalsIgnoreCase("Bounds")) {
                                                        break;
                                                }
                                                if (i == 0) {
                                                        projection.unitname = token;
                                                        i++;
                                                } else {
                                                        projection.params[i++] = Double.valueOf(token);
                                                }
                                        }
                                }
                                if (token.equalsIgnoreCase("Affine")) {
                                        affine = new Affine();
                                        token = st.nextToken(" ,()");
                                        if (token.equalsIgnoreCase("Units")) {
                                                affine.unitname = st.nextToken();
                                        }
                                        int i = 0;
                                        while (st.hasMoreTokens()) {
                                                token = st.nextToken(" ,()");
                                                if (token.equalsIgnoreCase("Bounds")) {
                                                        break;
                                                }
                                                affine.params[i++] = Double.valueOf(token);
                                        }
                                }
                                if (token.equalsIgnoreCase("Bounds")) {
                                        bounds = new Bounds();
                                        bounds.minx = Double.valueOf(st.nextToken(" ,()"));
                                        bounds.miny = Double.valueOf(st.nextToken(" ,()"));
                                        bounds.maxx = Double.valueOf(st.nextToken(" ,()"));
                                        bounds.maxy = Double.valueOf(st.nextToken(" ,()"));
                                }
//                        } else if (token.equalsIgnoreCase("Nonearth")) {
//                        } else if (token.equalsIgnoreCase("Layout")) {
//                        } else if (token.equalsIgnoreCase("Table")) {
//                        } else if (token.equalsIgnoreCase("Window")) {
                        }
                }
        }

        @Override
        public String toString() {
                StringBuilder sb = new StringBuilder("CoordSys ");
                if (type.equalsIgnoreCase("EARTH")) {
                        sb.append("Earth ");
                        if (projection != null) {
                                sb.append(projection.toString());
                        }
                        if (affine != null) {
                                sb.append(affine.toString());
                        }
                        if (bounds != null) {
                                sb.append(bounds.toString());
                        }
                }
                return sb.toString();
        }
}
