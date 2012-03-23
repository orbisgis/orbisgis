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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** 
 * Classe permettant de lire et d'�crire les informations sur le Système de
 * coordonn�es contenues dans un fichier MIF
 * @author Micha�l Michaud
 * @version 4.3 (2009-06-13)
 */
public class Projection {

        //**************************************************************************
        //********************         variables statiques        ******************
        //**************************************************************************
        private static final DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.ENGLISH);
        private static final DecimalFormat format = new DecimalFormat("#0.000000000000", dfs);
        //**************************************************************************
        //********************         variables globales         ******************
        //**************************************************************************
        // type is a positive integer value representing which coordinate system to use
        // type =  1 pour Longitude/Latitude
        // type =  3 pour Lambert Conformal Conic
        // type =  8 pour Transverse Mercator, �galement appel� Gauss�Kr�ger
        // type = 10 pour Mercator
        // ... voir annexe J du Manuel de Reference de MapInfo
        String type;
        // datum is a positive integer value identifying which datum to reference
        // datum = 9999 pour definir un datum personnalis�
        // datum =   28 pour Europe 1950 (ED 50)
        // datum =   33 pour Geodetic Reference System 1980 (GRS 80)
        // datum =  104 pour World Geodetic System 1984 (WGS 84)
        // datum =  107 pour Nouvelle Triangulation Francaise (NTF) Greenwich Prime Meridian Modified Clarke 1880
        // datum = 1002 pour Nouvelle Triangulation Fran�aise (NTF) Greenwich Prime Meridian Modified Clarke
        String datum;
        // unitname is a string representing a distance unit of measure
        String unitname;
        Double[] params = new Double[9];

        public Projection() {
        }

        public int getParameterNumber() {
                for (int i = 0; i < 9; i++) {
                        if (params[i] == null) {
                                return i;
                        }
                }
                return 9;
        }

        @Override
        public String toString() {
                StringBuilder sb = new StringBuilder("Projection ");
                sb.append(type).append(", ").append(datum);
                if (unitname != null) {
                        sb.append(", ").append(unitname);
                }
                for (int i = 0; i < 9; i++) {
                        if (!(params[i] == null)) {
                                sb.append(", ").append(format.format(params[i].doubleValue()));
                        }
                }
                return sb.toString();
        }
}
