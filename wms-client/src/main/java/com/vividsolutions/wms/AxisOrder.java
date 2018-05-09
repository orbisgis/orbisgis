/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */

package com.vividsolutions.wms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * An enum class with two values to determine if coordinates are given in the 
 * Longitude / Latitude order or in the Latitude / Longitude order in a 
 * particular CRS (or SRS).
 * @author Michael Michaud michael.michaud@free.fr
 */
public enum AxisOrder {
    
    LATLON, LONLAT;
    
    private static Logger LOG = LoggerFactory.getLogger(AxisOrder.class);
    public static final List<String> LATLONCRS = new ArrayList<String>();
    public static boolean initialized = false;
    
    public static AxisOrder getAxisOrder(String srs) {
        init();
        return Collections.binarySearch(LATLONCRS, srs.toUpperCase()) >= 0 ?
        LATLON : LONLAT;
    }
    
    private static void init() {
        if (initialized) return;
        InputStream is = null;
        BufferedReader br = null;
        try {
            is = AxisOrder.class.getResourceAsStream("latlonaxisorder.csv");
            br = new BufferedReader(new InputStreamReader(is));
            String namespace = "EPSG";
            String line = null;
            while (null != (line = br.readLine())) {
                line = line.trim();
                if (line.length() == 0) continue;
                char firstChar = line.charAt(0);
                if (Character.isLetter(firstChar)) {
                    namespace = line.toUpperCase();
                }
                else if (Character.isDigit(firstChar)) {
                    try {
                        LATLONCRS.add(namespace + ":" + line);
                        //System.out.println(namespace + ":" + line);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            initialized = true;
            LOG.info("LatLon CRS list has been initialized for WMS 1.3.0");
        }
        catch(IOException ioe) {
            System.out.println("Initialization of 'latlonaxisorder.csv' failed !");
            LOG.error("Initialization of 'latlonaxisorder.csv' failed !");
        }
        finally {
            if (br != null) try {br.close();} catch(IOException e){}
            if (is != null) try {is.close();} catch(IOException e){}
            Collections.sort(LATLONCRS);
        }
    }
  
}
