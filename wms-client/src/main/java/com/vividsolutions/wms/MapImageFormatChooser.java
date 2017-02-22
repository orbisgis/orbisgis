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

import java.util.Arrays;

/**
 * A Utility class to select the optimal Map Format based on some preferences.
 * @author  Chris Hodgson chodgson@refractions.net
 */
public class MapImageFormatChooser {
  
    private boolean transparencyRequired;
    private boolean useLossy;
    public static final String[][] IMAGE_FORMATS = {
          {"GIF", "PNG", "JPEG"},        			// for WMS 1.0.0
          {"image/gif", "image/png", "image/jpeg"}	// for WMS 1.1.x, WMS 1.3.x
    };

    // set image formats to the default WMS (1.0.0)
    private String[] imageFormats = IMAGE_FORMATS[0];

    /** 
     * Creates a new instance of MapImageFormatChooser.
     */
    public MapImageFormatChooser() {
        this( WMService.WMS_1_1_1 );
    }
    
    /** 
     * Creates a new instance of MapImageFormatChooser.
     */
    public MapImageFormatChooser(String wmsVersion) {
        this.transparencyRequired = false;
        this.useLossy = false;
        if( wmsVersion.compareTo(WMService.WMS_1_0_0) > 0){
            imageFormats = IMAGE_FORMATS[1];
        } else {
            imageFormats = IMAGE_FORMATS[0];
        }
    }
  
    /**
     * Returns true if the specified format is known by the MapFormatChooser, false 
     * otherwise. The MapFormatChooser can only reliably select between formats 
     * which it knows; it will only return an unknown format if there are no known 
     * formats to select from. [UT] changed to accept WMS 1.0 and 1.1.1 image formats 
     * @param format the format which is in question
     * @return true if the specified format is known by the MapFormatChooser, false 
     *         otherwise
     */
    static public boolean isKnownFormat( String format ) {
        for (int i = 0; i < IMAGE_FORMATS.length; i++) {
            for (int j = 0; j < IMAGE_FORMATS[i].length; j++) {
                if( format.equals( IMAGE_FORMATS[i][j] ) ) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Sets whether tranparency is required in the image format.
     * If transparency is required it takes priority over lossy compression.
     * However, if no format that supports transparency is available, the next best
     * format will be selected.
     * @param transparencyRequired true if the image format chosen needs to support 
     *                             transparency
     */
    public void setTransparencyRequired( boolean transparencyRequired ) {
        this.transparencyRequired = transparencyRequired;
    }
    
    /**
     * Sets whether lossy compression is preferred over non-lossy compression.
     * Lossy compression would generally only be preferred over a slow connection,
     * and also note that image formats which use lossy compression generally don't
     * support transparency - in this case the transparency requirement takes priority.
     * @param useLossy true if lossy compression is preferrable
     */
    public void setPreferLossyCompression( boolean useLossy ) {
        this.useLossy = useLossy;
    }
    
    /**
     * Returns a format String from the Array of available formats which best
     * matches the requirements and preferences specified. If there are no image
     * formats available which can be used by the WMS image handling code, then 
     * null is returned.
     * @param formats the array of available formats to choose from
     * @return the chosen format string from Array of available formats, or null 
     *         if none of the available formats are known
     */
    public String chooseFormat( String[] formats ) {
        if( formats.length == 0 ) {
            throw new IllegalArgumentException();
        }
        String[] order = new String[3];
        if( transparencyRequired ) {
            order[0] = imageFormats[1]; //png
            order[1] = imageFormats[0]; //gif
            order[2] = imageFormats[2]; //jpg
        } else if( useLossy ) {
            order[0] = imageFormats[2]; //jpg
            order[1] = imageFormats[1]; //png
            order[2] = imageFormats[0]; //gif
        } else {
            order[0] = imageFormats[1]; //png
            order[1] = imageFormats[2]; //jpg
            order[2] = imageFormats[0]; //gif
        }
        Arrays.sort( formats );
        for( int i = 0; i < order.length; i++ ) {
            if( Arrays.binarySearch( formats, order[i] ) >= 0 ) {
                return order[i];
            }
        }
        return null;
    }
}
