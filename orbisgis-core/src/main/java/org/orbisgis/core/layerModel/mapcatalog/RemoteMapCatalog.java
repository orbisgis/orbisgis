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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * The remote map catalog is a Representational State Transfer Map Catalog client.
 * All informations 
 * @author Nicolas Fortin
 */
public class RemoteMapCatalog {
        private ConnectionProperties cParams;
        private static final String ENCODING = "UTF-8";
        private static final String LIST_WORKSPACES = "/workspaces";
        
        /**
         * Constructor
         * @param connectionProperties Connection properties to the remote MapCatalog
         */
        public RemoteMapCatalog(ConnectionProperties connectionProperties) {
                this.cParams = connectionProperties;
        }
        
        
        
        public Workspace getDefaultWorkspace() {
                return new Workspace(remoteCatalogUri,"default");
        }
        /***
         * Request the workspaces synchronously, this call may take a long time to execute
         * @return A list of workspaces
         * @throws IOException The request fail
         */
        public List<Workspace> getWorkspaces() throws IOException {
                List<Workspace> workspaces = new ArrayList<Workspace>();
                // Construct request
                URL requestWorkspacesURL =
                        new URL(cParams.getApiUrl()+LIST_WORKSPACES);
                URLConnection connection = requestWorkspacesURL.openConnection();
                connection.setUseCaches(false);
                // Read the response content
                BufferedReader in = new BufferedReader(
                                    new InputStreamReader(
                                    connection.getInputStream()));
                String decodedString;
                while ((decodedString = in.readLine()) != null) {
                        System.out.println(decodedString);
                }
                in.close();
                connection.
                //URLEncoder.encode(args[1], ENCODING);
                return workspaces;
        }
}
