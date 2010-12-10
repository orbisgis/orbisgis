/*
 * Library offering read and write capabilities for MifMid format
 * Copyright (C) 2009 Micha�l MICHAUD
 * michael.michaud@free.fr
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

package org.gdms.driver.mifmid;

import java.util.StringTokenizer;
import java.util.List;


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
            if (token.equalsIgnoreCase("CoordSys")) continue;
            else if (token.equalsIgnoreCase("Earth")) {
                type = "Earth";
                token = st.nextToken();
                if (token.equals("Projection")) {
                    projection = new Projection();
                    projection.type = st.nextToken();
                    projection.datum = st.nextToken();
                    // 999, EllipsoidNumber, dX, dY, dZ
                    if (projection.datum.startsWith("999")) {
                        projection.datum += (", "+st.nextToken());
                        projection.datum += (", "+st.nextToken());
                        projection.datum += (", "+st.nextToken());
                        projection.datum += (", "+st.nextToken());
                    }
                    // 9999, EllipsoidNumber, dX, dY, dZ, EX, EY, EZ, m, PrimeMeridian
                    if (projection.datum.startsWith("9999")) {
                        projection.datum += (", "+st.nextToken());
                        projection.datum += (", "+st.nextToken());
                        projection.datum += (", "+st.nextToken());
                        projection.datum += (", "+st.nextToken());
                        projection.datum += (", "+st.nextToken());
                    }
                    //projection.unitname = st.nextToken();
                    int i = 0;
                    while (st.hasMoreTokens()) {
                        token = st.nextToken(" ,()");
                        if (token.equalsIgnoreCase("Affine")) break;
                        if (token.equalsIgnoreCase("Bounds")) break;
                        if (i==0) {
                            projection.unitname = token;
                            i++;
                        }
                        else projection.params[i++] = new Double(token);
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
                        if (token.equalsIgnoreCase("Bounds")) break;
                        affine.params[i++] = new Double(token);
                    }
                }
                if (token.equalsIgnoreCase("Bounds")) {
                    bounds = new Bounds();
                    bounds.minx = new Double(st.nextToken(" ,()"));
                    bounds.miny = new Double(st.nextToken(" ,()"));
                    bounds.maxx = new Double(st.nextToken(" ,()"));
                    bounds.maxy = new Double(st.nextToken(" ,()"));
                }
                else {}
            }
            else if (token.equalsIgnoreCase("Nonearth")) {
            }
            else if (token.equalsIgnoreCase("Layout")) {
            }
            else if (token.equalsIgnoreCase("Table")) {
            }
            else if (token.equalsIgnoreCase("Window")) {
            }
            else {}
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("CoordSys ");
        if (type.equalsIgnoreCase("EARTH")) {
            sb.append("Earth ");
            if (projection!=null) sb.append(projection.toString());
            if (affine!=null) sb.append(affine.toString());
            if (bounds!=null) sb.append(bounds.toString());
        }
        else {}
        return sb.toString();
    }

}
