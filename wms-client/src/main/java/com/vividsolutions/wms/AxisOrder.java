/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI 
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * Copyright (C) 2003 Vivid Solutions
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */

package com.vividsolutions.wms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import org.apache.log4j.Logger;

/**
 * An enum class with two values to determine if coordinates are given in the 
 * Longitude / Latitude order or in the Latitude / Longitude order in a 
 * particular CRS (or SRS).
 * @author Michael Michaud michael.michaud@free.fr
 */
public enum AxisOrder {
    
    LATLON, LONLAT;
    
    private static Logger LOG = Logger.getLogger(AxisOrder.class);
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
