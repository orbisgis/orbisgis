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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.orbisgis.core.layerModel.MapContext;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Workspace structure and reader
 * @author Nicolas Fortin
 */
public class Workspace  {
        private static final String ENCODING = "utf-8";
        private static final String LIST_CONTEXT = "/context";
        private static final String PUBLISH_CONTEXT = "/context/save";
        private static final I18n I18N = I18nFactory.getI18n(Workspace.class);
        private ConnectionProperties cParams;
        private String workspaceName;
        private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        public Workspace(ConnectionProperties cParams, String workspaceName) {
                this.cParams = cParams;
                this.workspaceName = workspaceName;
        }
        /**
         * <result>
    <status>
        <code></code>
        <message></message>
    </status>
    (<id></id>)
</result>
         */
        
        private int parsePublishResponse(XMLStreamReader parser) throws XMLStreamException {
                List<String> hierarchy = new ArrayList<String>();
                for (int event = parser.next();
                        event != XMLStreamConstants.END_DOCUMENT;
                        event = parser.next()) {
                        // For each XML elements
                        switch(event) {
                                case XMLStreamConstants.START_ELEMENT:
                                        hierarchy.add(parser.getLocalName());
                                        break;
                                case XMLStreamConstants.END_ELEMENT:
                                        hierarchy.remove(hierarchy.size()-1);
                                        break;
                                case XMLStreamConstants.CHARACTERS:
                                        if(RemoteCommons.endsWith(hierarchy,"result","id")) {
                                                return Integer.parseInt(parser.getText());
                                        }
                                        break;
                        }                               
                }                
                throw new XMLStreamException("Bad response on publishing a map context");
        }
        /**
         * Add a mapcontext to the workspace
         * @param mapContext
         * @return The ID of the published map context
         * @throws IOException 
         */
        public int publishMapContext(MapContext mapContext) throws IOException  {
                // Construct request
                URL requestWorkspacesURL =
                        new URL(cParams.getApiUrl()+PUBLISH_CONTEXT);
                // Establish connection
                HttpURLConnection connection = (HttpURLConnection) requestWorkspacesURL.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setConnectTimeout(cParams.getConnectionTimeOut());
                OutputStream out = connection.getOutputStream();
                RemoteCommons.putParameters(out,"workspace",workspaceName,ENCODING);
                // Send MapContext DATA
                Writer writer = new OutputStreamWriter(out, ENCODING);
                writer.write("owc=");
                writer.close();
                ByteArrayOutputStream mapData = new ByteArrayOutputStream();
                mapContext.write(mapData);
                // Encode the XML end write to the output stream
                out.write(URLEncoder.encode(mapData.toString(ENCODING),ENCODING).getBytes(ENCODING));
                out.flush();
                out.close();
                // Get response
		if (!(connection.getResponseCode() == HttpURLConnection.HTTP_CREATED
                        || connection.getResponseCode() == HttpURLConnection.HTTP_OK )) { //Old api response
                        throw new IOException(I18N.tr("HTTP Error {0} message : {1}",connection.getResponseCode(),connection.getResponseMessage()));
                }
                
                // Get response content
                BufferedReader in = new BufferedReader(
                                    new InputStreamReader(
                                    connection.getInputStream()));
                
                
                XMLInputFactory factory = XMLInputFactory.newInstance();
                
                // Parse Data
                XMLStreamReader parser;
                try {
                        parser = factory.createXMLStreamReader(in);
                        // Fill workspaces
                        int resId = parsePublishResponse(parser);
                        parser.close();
                        return resId;
                } catch(XMLStreamException ex) {
                        throw new IOException(I18N.tr("Invalid XML content"),ex);
                }
        }
        
        /**
         * Read the parser and feed the provided list with workspaces
         * @param mapContextList Writable, empty list of RemoteMapContext
         * @param parser Opened parser
         * @throws XMLStreamException 
         */
        public void parseXML(List<RemoteMapContext> mapContextList,XMLStreamReader parser) throws XMLStreamException {
                List<String> hierarchy = new ArrayList<String>();
                RemoteMapContext curMapContext = null;
                for (int event = parser.next();
                        event != XMLStreamConstants.END_DOCUMENT;
                        event = parser.next()) {
                        // For each XML elements
                        switch(event) {
                                case XMLStreamConstants.START_ELEMENT:
                                        hierarchy.add(parser.getLocalName());
                                        if(RemoteCommons.endsWith(hierarchy,"items","item")) {
                                                curMapContext = new RemoteOwsMapContext(cParams);
                                                curMapContext.setWorkspaceName(workspaceName);
                                        }
                                        break;
                                case XMLStreamConstants.END_ELEMENT:
                                        if(RemoteCommons.endsWith(hierarchy,"items","item")) {
                                                mapContextList.add(curMapContext);
                                        }
                                        hierarchy.remove(hierarchy.size()-1);
                                        break;
                                case XMLStreamConstants.CHARACTERS:
                                        if(RemoteCommons.endsWith(hierarchy,"items","item","id")) {
                                                curMapContext.setId(Integer.parseInt(parser.getText()));
                                        } else if(RemoteCommons.endsWith(hierarchy,"items","item","title")) {
                                                curMapContext.getDescription().addTitle(Locale.getDefault(), parser.getText());                                                
                                        } else if(RemoteCommons.endsWith(hierarchy,"items","item","abstract")) {
                                                curMapContext.getDescription().addAbstract(Locale.getDefault(), parser.getText());                                                
                                        } else if(RemoteCommons.endsWith(hierarchy,"items","item","date")) {
                                                try {
                                                        curMapContext.setDate(FORMAT.parse(parser.getText()));
                                                } catch( ParseException ex) {
                                                        // Silently ignore the date parse failure
                                                }
                                        }
                                        break;
                        }                               
                }                
        }
        
        /**
         * Return the workspace name, non localized
         * @return 
         */
        public String getWorkspaceName() {
                return workspaceName;
        }
        
        /**
         * Retrieve the list of MapContext linked with this workspace
         * This call may take a long time to execute.
         * @return
         * @throws IOException Connection failure 
         */
        public List<RemoteMapContext> getMapContextList() throws IOException {
                List<RemoteMapContext> contextList = new ArrayList<RemoteMapContext>();
                // Construct request
                URL requestWorkspacesURL =
                        new URL(cParams.getApiUrl()+LIST_CONTEXT);
                // Establish connection
                HttpURLConnection connection = (HttpURLConnection) requestWorkspacesURL.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setConnectTimeout(cParams.getConnectionTimeOut());
                OutputStream out = connection.getOutputStream();
                RemoteCommons.putParameters(out,"workspace",workspaceName,ENCODING);
                out.close();
                
                // Send parameters
                

		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        throw new IOException(I18N.tr("HTTP Error {0} message : {1}",connection.getResponseCode(),connection.getResponseMessage()));
                }
                
                // Read the response content
                BufferedReader in = new BufferedReader(
                                    new InputStreamReader(
                                    connection.getInputStream()));
                
                
                XMLInputFactory factory = XMLInputFactory.newInstance();
                
                // Parse Data
                XMLStreamReader parser;
                try {
                        parser = factory.createXMLStreamReader(in);
                        // Fill workspaces
                        parseXML(contextList, parser);
                        parser.close();
                } catch(XMLStreamException ex) {
                        throw new IOException(I18N.tr("Invalid XML content"),ex);
                }
                //URLEncoder.encode(args[1], ENCODING);
                return contextList;
        }
}
