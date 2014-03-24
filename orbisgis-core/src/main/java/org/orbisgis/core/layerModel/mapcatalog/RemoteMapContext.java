/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.renderer.se.common.Description;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Remote map context abstraction
 * @author Nicolas Fortin
 */
public abstract class RemoteMapContext {
        private int id = 0;
        private Description description = new Description();
        private ConnectionProperties cParams;
        private transient String workspaceName; // Not serialised
        private Date date;
        private static final I18n I18N = I18nFactory.getI18n(RemoteMapContext.class);

        /**
         * Constructor
         * @param cParams Connection parameters
         */
        public RemoteMapContext(ConnectionProperties cParams) {
                this.cParams = cParams;
        }
        
        /**
         * Delete this map context on the remote server.
         * This call may take a long time to execute.
         */
        public void delete() throws IOException {
                // Construct request
                String url = RemoteCommons.getUrlDeleteContext(cParams, workspaceName, id);
                URL requestWorkspacesURL =
                        new URL(url);
                // Establish connection
                HttpURLConnection connection = (HttpURLConnection) requestWorkspacesURL.openConnection();
    
                connection.setRequestMethod("DELETE");
                connection.setConnectTimeout(cParams.getConnectionTimeOut());                
                
		if (!(connection.getResponseCode() == HttpURLConnection.HTTP_OK || connection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT)) {
                        throw new IOException(I18N.tr("HTTP Error {0} message : {1} while removing the map context from {2}",connection.getResponseCode(),connection.getResponseMessage(),url));
                }                
        }
        
        @Override
        public boolean equals(Object obj) {                
                if(!(obj instanceof RemoteMapContext)) {
                        return false;
                }         
                final RemoteMapContext other = (RemoteMapContext) obj;
                if (this.id != other.id) {
                        return false;
                }
                if (this.description != other.description && (this.description == null || !this.description.equals(other.description))) {
                        return false;
                }
                if (this.cParams != other.cParams && (this.cParams == null || !this.cParams.equals(other.cParams))) {
                        return false;
                }
                if (this.date != other.date && (this.date == null || !this.date.equals(other.date))) {
                        return false;
                }
                return true;
        }

        @Override
        public int hashCode() {
                int hash = 7;
                hash = 67 * hash + this.id;
                hash = 67 * hash + (this.description != null ? this.description.hashCode() : 0);
                hash = 67 * hash + (this.cParams != null ? this.cParams.hashCode() : 0);
                hash = 67 * hash + (this.workspaceName != null ? this.workspaceName.hashCode() : 0);
                hash = 67 * hash + (this.date != null ? this.date.hashCode() : 0);
                return hash;
        }

        /**
         * 
         * @return The Map id
         */
        public int getId() {
                return id;
        }

        

        /**
         * Set the workspace name associated with this id
         * @param workspaceName 
         */
        public void setWorkspaceName(String workspaceName) {
                this.workspaceName = workspaceName;
        }

        
        /**
         * 
         * @param description 
         */
        public void setDescription(Description description) {
                this.description = description;
        }

        /**
         * Get remote map context Id
         * @param id 
         */
        public void setId(int id) {
                this.id = id;
        }

        /**
         * 
         * @return Date of the document
         */
        public Date getDate() {
                return date;
        }

        /**
         * Set the date
         * @param date Date of the document
         */
        public void setDate(Date date) {
                this.date = date;
        }
        
        /**
         * Request Map Description, the description is buffered.
         * @return Map description
         */
        public Description getDescription() {
                return description;
        }
        
        /**
         * Return the stream of the map content node
         * This call may take a long time to execute.
         * @return
         * @throws IOException  
         */
        public HttpURLConnection getMapContent() throws IOException {
                // Construct request
                String url = RemoteCommons.getUrlContext(cParams, workspaceName, id);
                URL requestWorkspacesURL =
                        new URL(url);
                // Establish connection
                HttpURLConnection connection = (HttpURLConnection) requestWorkspacesURL.openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(cParams.getConnectionTimeOut());

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        throw new IOException(I18N.tr("HTTP Error {0} message : {1} while downloading from {2}", connection.getResponseCode(), connection.getResponseMessage(), url));
                }
                return connection;
        }
        
        /**
         * Connect to the server and request the map content.
         * This call may take a long time to execute.
         * @return
         * @throws IOException The request fail 
         */
        public abstract MapContext getMapContext() throws IOException;
        /**
         * 
         * @return The file extension used to save this kind of map context
         */
        public abstract String getFileExtension();
}
