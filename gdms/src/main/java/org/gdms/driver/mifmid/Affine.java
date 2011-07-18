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
public class Affine {

        //**************************************************************************
        //********************         variables statiques        ******************
        //**************************************************************************
        private static final DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.ENGLISH);
        private static final DecimalFormat format = new DecimalFormat("#0.000000000000", dfs);
        //**************************************************************************
        //********************         variables globales         ******************
        //**************************************************************************
        String unitname;
        Double[] params = new Double[6];

        //**************************************************************************
        //**************************************************************************
        //********************          Les constructeurs      ********************
        //**************************************************************************
        //**************************************************************************
        public Affine() {
        }

        @Override
        public String toString() {
                StringBuilder sb = new StringBuilder(" Affine Units ");
                sb.append(unitname);
                for (int i = 0; i < 6; i++) {
                        if (!(params[i] == null)) {
                                sb.append(", ").append(format.format(params[i].doubleValue()));
                        }
                }
                return sb.toString();
        }
}
