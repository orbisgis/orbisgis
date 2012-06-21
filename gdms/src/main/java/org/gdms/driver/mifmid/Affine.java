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
