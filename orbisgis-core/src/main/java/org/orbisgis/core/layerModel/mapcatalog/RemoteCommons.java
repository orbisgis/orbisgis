/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.layerModel.mapcatalog;

import java.net.HttpURLConnection;
import java.util.List;

/**
 *
 * @author Nicolas Fortin
 */
public class RemoteCommons {
        private static final String CHARSET_KEY = "charset=";

        /**
         * Check that the provided list ends with the provided items
         * @param hierarchy
         * @param items
         * @return 
         */
        public static boolean endsWith(List<String> hierarchy,String... items) {
                if(hierarchy.size()<items.length) {
                        return false;
                }
                final int firstI = hierarchy.size()-items.length;
                for(int i=firstI;i<hierarchy.size();i++) {
                        if(!hierarchy.get(i).equals(items[i-firstI])) {
                                return false;
                        }
                }
                return true;
        }
        
        /**
         * Parse the connection content type and return the content charset
         * @param connection
         * @return Connection charset or "utf-8" if unknown
         */
        public static String getConnectionCharset(HttpURLConnection connection) {
                String contentType = connection.getContentType();
                int charsetIndex = contentType.indexOf(CHARSET_KEY);
                if(charsetIndex!=-1) {
                        int charsetEndIndex = contentType.indexOf(";", charsetIndex);
                        if(charsetEndIndex==-1) {
                                return contentType.substring(charsetIndex+CHARSET_KEY.length());
                        } else {
                                return contentType.substring(charsetIndex+CHARSET_KEY.length(),charsetEndIndex);                                        
                        }
                }
                return "UTF-8";
        }
}
