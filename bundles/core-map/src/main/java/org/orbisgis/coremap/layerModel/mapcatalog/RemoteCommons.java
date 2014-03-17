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
package org.orbisgis.coremap.layerModel.mapcatalog;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.List;

/**
 * Collection of static methods for doing common operation during
 * transaction with the remote API.
 * @author Nicolas Fortin
 */
public class RemoteCommons {
        private static final String ENCODING = "UTF-8";
        private static final String CHARSET_KEY = "charset=";
        private static final String WORKSPACES = "/workspaces";
        private static final String CONTEXTS = "/contexts";        
        
        /**
         * API Get workspace list.
         * GET /workspaces
         * @param cParams Connection parameters
         * @return URL of the server 
         */
        public static String getUrlWorkspaceList(ConnectionProperties cParams) {
                return cParams.getApiUrl() + WORKSPACES;
        }
        
        /**
         * API Get context list.
         * GET /workspaces/:name/contexts
         * @param cParams Connection parameters
         * @param workspaceName Workspace name
         * @return URL of the server
         */
        public static String getUrlContextList(ConnectionProperties cParams,String workspaceName) throws IOException {
                return getUrlWorkspaceList(cParams)+"/"+URLEncoder.encode(workspaceName, ENCODING)+CONTEXTS;
        }
        /**
         * API Post context list.
         * POST /workspaces/:name/contexts
         * @param cParams Connection parameters
         * @param workspaceName Workspace name
         * @return URL of the server
         */
        public static String getUrlPostContext(ConnectionProperties cParams,String workspaceName) throws IOException {
                return getUrlContextList(cParams,workspaceName);
        }
        
        /**
         * API Get a context.
         * GET /workspaces/:name/contexts/:id
         * @param cParams Connection parameters
         * @return URL of the server
         */
        public static String getUrlContext(ConnectionProperties cParams,String workspaceName,Integer contextId) throws IOException {
                return getUrlContextList(cParams,workspaceName)+"/"+contextId;
        }
        /**
         * API Update a context.
         * POST /workspaces/:name/contexts/:id
         * @param cParams Connection parameters
         * @return URL of the server
         */
        public static String getUrlUpdateContext(ConnectionProperties cParams,String workspaceName,Integer contextId) throws IOException {
                return getUrlContext(cParams,workspaceName,contextId);
        }
        /**
         * API Remove a context.
         * DELETE /workspaces/:name/contexts/:id
         * @param cParams Connection parameters
         * @return URL of the server
         */
        public static String getUrlDeleteContext(ConnectionProperties cParams,String workspaceName,Integer contextId) throws IOException {
                return getUrlContext(cParams,workspaceName,contextId);
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
