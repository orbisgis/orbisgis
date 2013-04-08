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

package org.gdms.data.stream;

import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Utility class in order to manage URI
 * @author Nicolas Fortin
 */
public class URIUtility {
    public static final String ENCODING = "UTF-8";
    private static final Logger LOGGER = Logger.getLogger(URIUtility.class);

    /**
     * Read the Query part of an URI.
     * @param uri URI to split
     * @return Key/Value pairs of query, the key is lowercase and value may be null
     * @throws UnsupportedEncodingException
     */
    public static Map<String,String> getQueryKeyValuePairs(URI uri) throws UnsupportedEncodingException {
        Map<String,String> queryParameters = new HashMap<String, String>();
        StringTokenizer stringTokenizer = new StringTokenizer(uri.getRawQuery(), "&");
        while (stringTokenizer.hasMoreTokens()) {
            String keyValue = stringTokenizer.nextToken().trim();
            if(!keyValue.isEmpty()) {
                int equalPos = keyValue.indexOf("=");
                // If there is no value
                if(equalPos==-1 || equalPos == keyValue.length() - 1) {
                    queryParameters.put(URLDecoder.decode(keyValue, ENCODING).toLowerCase(),null);
                } else {
                    String key = URLDecoder.decode(keyValue.substring(0,equalPos), ENCODING);
                    String value = URLDecoder.decode(keyValue.substring(equalPos+1), ENCODING);
                    queryParameters.put(key.toLowerCase(),value);
                }
            }
        }
        return queryParameters;
    }

    /**
     * Create the Query part of an URI
     * @param parameters Parameters to read
     * @param keys map property to read
     * @return Query part of an URI
     */
    public static String getConcatenatedParameters(Map<String,String> parameters, String... keys) {
        StringBuilder keyValues = new StringBuilder();
        for(String key : keys) {
            try {
                String value = parameters.get(key.toLowerCase().trim());
                if(value!=null) {
                    if(keyValues.length()!=0) {
                        keyValues.append("&");
                    }
                    keyValues.append(URLEncoder.encode(key.toUpperCase(),ENCODING));
                    keyValues.append("=");
                    keyValues.append(URLEncoder.encode(value,ENCODING));
                }
            } catch (UnsupportedEncodingException ex) {
                LOGGER.error(ex.getLocalizedMessage(),ex);
            }
        }
        return keyValues.toString();
    }
}
