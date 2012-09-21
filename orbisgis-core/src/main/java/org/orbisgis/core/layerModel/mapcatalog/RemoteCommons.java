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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Nicolas Fortin
 */
public class RemoteCommons {

        public static void putParameters(OutputStream out, String key, String value, String encoding) throws IOException {
                Writer writer = new OutputStreamWriter(out, encoding);
                writer.write(key);
                writer.write("=");
                writer.write(URLEncoder.encode(value, encoding));
                writer.write("&");
                writer.close();
        }        
        /**
         * Send via the output stream the provided parameters
         * @param out Out stream
         * @param parameters
         * @param encoding
         * @throws IOException  
         */
        public static void putParameters(OutputStream out, Map<String, String> parameters, String encoding) throws IOException {
                Writer writer = new OutputStreamWriter(out, encoding);
                for (Entry<String,String> entry : parameters.entrySet()) {
                        writer.write(entry.getKey());
                        writer.write("=");
                        writer.write(URLEncoder.encode(entry.getValue(), encoding));
                        writer.write("&");
                }
                writer.close();
        }

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
}
