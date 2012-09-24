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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.renderer.se.common.Description;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Remote map context abstraction
 * @author Nicolas Fortin
 */
public abstract class RemoteMapContext {
        private static final String ENCODING = "utf-8";
        private static final String GET_CONTEXT = "/context/";
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
        
        private static String readStream(InputStream in) throws IOException {
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                        stringBuilder.append(line);
                }
                bufferedReader.close();
                return stringBuilder.toString();
        }

        /**
         * Using old API, this reader need to extract sub nodes before returning
         * @param completeIn 
         * @param encoding 
         * @return
         * @throws IOException  
         */
        public InputStream extractMapContent(InputStream completeIn, String encoding) throws IOException {
                // Read the entire DATA
                String data = readStream(completeIn);
                ByteArrayInputStream in;
                if(encoding != null) {
                        in = new ByteArrayInputStream(data.getBytes(encoding));
                } else {
                        in = new ByteArrayInputStream(data.getBytes());
                }
                XMLInputFactory factory = XMLInputFactory.newInstance();                
                // Parse Data
                XMLStreamReader parser;
                try {
                        parser = factory.createXMLStreamReader(in);
                        // Fill workspaces
                        InputStream mapStream = parseXML(parser,data);
                        parser.close();
                        return mapStream;
                } catch(XMLStreamException ex) {
                        throw new IOException(I18N.tr("Invalid XML content"),ex);
                }                             
        }
        /**
         * Return the stream of the map content node
         * This call may take a long time to execute.
         * Using old API, this reader need to extract sub nodes before returning
         * the input stream that correspond to the map content
         * @return
         * @throws IOException  
         */
        protected InputStream getMapContent() throws IOException {
                // Construct request
                URL requestWorkspacesURL =
                        new URL(cParams.getApiUrl()+GET_CONTEXT+id);
                // Establish connection
                HttpURLConnection connection = (HttpURLConnection) requestWorkspacesURL.openConnection();

                
                
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setConnectTimeout(cParams.getConnectionTimeOut());
                OutputStream out = connection.getOutputStream();
                RemoteCommons.putParameters(out,"workspace",workspaceName,ENCODING);
                out.close();                
                
                
		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        throw new IOException(I18N.tr("HTTP Error {0} message : {1}",connection.getResponseCode(),connection.getResponseMessage()));
                }
                return extractMapContent(connection.getInputStream(),connection.getContentEncoding());
        }

        /**
         * Read the parser extract the map content data
         * @param parser Opened parser
         * @param data 
         * @throws XMLStreamException 
         * @return Map content data or null if the map content data is empty
         */
        public InputStream parseXML(XMLStreamReader parser,String data) throws XMLStreamException {
                List<String> hierarchy = new ArrayList<String>();
                Location begin=null;
                Location end=null;
                for (int event = parser.next();
                        event != XMLStreamConstants.END_DOCUMENT;
                        event = parser.next()) {
                        // For each XML elements
                        switch (event) {
                                case XMLStreamConstants.START_ELEMENT:
                                        hierarchy.add(parser.getLocalName());
                                        break;
                                case XMLStreamConstants.END_ELEMENT:
                                        if(RemoteCommons.endsWith(hierarchy, "result", "status")) {
                                                begin = parser.getLocation();
                                        } else if (RemoteCommons.endsWith(hierarchy, "result", "OWSContext")) {
                                                end = parser.getLocation();
                                        }
                                        hierarchy.remove(hierarchy.size() - 1);
                                        break;
                        }
                }
                if(begin!=null && end!=null) {
                        String mapContent = data.substring(begin.getCharacterOffset(), end.getCharacterOffset());
                        return new ByteArrayInputStream(mapContent.getBytes());
                } else {
                        throw new XMLStreamException(I18N.tr("Incomplete map context response"));
                }
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
