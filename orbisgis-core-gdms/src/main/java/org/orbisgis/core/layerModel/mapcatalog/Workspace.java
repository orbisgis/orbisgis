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
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.renderer.se.common.LocalizedText;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Workspace structure and reader.
 * 
 * The workspace currently use Apache http.client with the old API
 * java.net can be used for upload easily with the new API.
 * @author Nicolas Fortin
 */
public class Workspace  {
        private static final I18n I18N = I18nFactory.getI18n(Workspace.class);        
        private static final Logger LOGGER = Logger.getLogger(Workspace.class);
        private ConnectionProperties cParams;
        private String workspaceName;
        private static final DateFormat FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
        /**
         * Construct a workspake identifier
         * @param cParams
         * @param workspaceName 
         */
        public Workspace(ConnectionProperties cParams, String workspaceName) {
                this.cParams = cParams;
                this.workspaceName = workspaceName;
        }
        
        /**
         * 
         * @param dateStr The date with the format Workspace.FORMAT
         * @return 
         */
        public static Date parseDate(String dateStr) throws ParseException {
                return FORMAT.parse(dateStr);
        }

        private int parsePublishResponse(XMLStreamReader parser) throws XMLStreamException {
                List<String> hierarchy = new ArrayList<String>();
                for (int event = parser.next();
                        event != XMLStreamConstants.END_DOCUMENT;
                        event = parser.next()) {
                        // For each XML elements
                        switch(event) {
                                case XMLStreamConstants.START_ELEMENT:
                                        hierarchy.add(parser.getLocalName());
                                        // Parse attributes
                                        if(RemoteCommons.endsWith(hierarchy, "context")) {
                                                for (int attributeId = 0; attributeId < parser.getAttributeCount(); attributeId++) {
                                                        String attributeName = parser.getAttributeLocalName(attributeId);
                                                        if (attributeName.equals("id")) {
                                                                return Integer.parseInt(parser.getAttributeValue(attributeId));
                                                        }
                                                }
                                        }
                                        break;
                                case XMLStreamConstants.END_ELEMENT:
                                        hierarchy.remove(hierarchy.size()-1);
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
        public int publishMapContext(MapContext mapContext, Integer mapContextId) throws IOException  {
                // Construct request
                URL requestWorkspacesURL;
                if(mapContextId==null) {
                        // Post a new map context
                        requestWorkspacesURL = new URL(RemoteCommons.getUrlPostContext(cParams, workspaceName));
                } else {
                        // Update an existing map context
                        requestWorkspacesURL = new URL(RemoteCommons.getUrlUpdateContext(cParams, workspaceName, mapContextId));
                }
                // Establish connection
                HttpURLConnection connection = (HttpURLConnection) requestWorkspacesURL.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setConnectTimeout(cParams.getConnectionTimeOut());
                connection.addRequestProperty("Content-Type", "text/xml");
                OutputStream out = connection.getOutputStream();
                mapContext.write(out); // Send map context
                out.close();

                // Get response
                int responseCode = connection.getResponseCode();
                if (!((responseCode == HttpURLConnection.HTTP_CREATED && mapContextId == null)
                        || (responseCode == HttpURLConnection.HTTP_OK && mapContextId != null))) {
                        throw new IOException(I18N.tr("HTTP Error {0} message : {1} with the URL {2}", connection.getResponseCode(), connection.getResponseMessage(), requestWorkspacesURL));
                }

                if(mapContextId==null) {
                        // Get response content
                        BufferedReader in = new BufferedReader(
                                            new InputStreamReader(
                                            connection.getInputStream(),
                                RemoteCommons.getConnectionCharset(connection)));


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
                } else {
                        return mapContextId;
                }
        }
        
        /**
         * Read the parser and feed the provided list with workspaces
         * @param mapContextList Writable, empty list of RemoteMapContext
         * @param parser Opened parser
         * @throws XMLStreamException 
         */
        public void parseXML(List<RemoteMapContext> mapContextList,XMLStreamReader parser) throws XMLStreamException, UnsupportedEncodingException {
                List<String> hierarchy = new ArrayList<String>();
                RemoteMapContext curMapContext = null;
                Locale curLocale = null;
                StringBuilder characters = new StringBuilder();
                for (int event = parser.next();
                        event != XMLStreamConstants.END_DOCUMENT;
                        event = parser.next()) {
                        // For each XML elements
                        switch(event) {
                                case XMLStreamConstants.START_ELEMENT:
                                        hierarchy.add(parser.getLocalName());
                                        if(RemoteCommons.endsWith(hierarchy,"contexts","context")) {
                                                curMapContext = new RemoteOwsMapContext(cParams);
                                                curMapContext.setWorkspaceName(workspaceName);
                                        }
                                        // Parse attributes
                                        for (int attributeId = 0; attributeId < parser.getAttributeCount(); attributeId++) {
                                                String attributeName = parser.getAttributeLocalName(attributeId);
                                                if (attributeName.equals("id")) {
                                                        curMapContext.setId(Integer.parseInt(parser.getAttributeValue(attributeId)));
                                                } else if (attributeName.equals("date")) {
                                                        String attributeValue = parser.getAttributeValue(attributeId);
                                                        try {
                                                                curMapContext.setDate(parseDate(attributeValue));
                                                        } catch (ParseException ex) {
                                                                LOGGER.warn(I18N.tr("Cannot parse the provided date {0}",attributeValue),ex);
                                                        }
                                                } else if (attributeName.equals("lang")) {
                                                         curLocale=LocalizedText.forLanguageTag(parser.getAttributeValue(attributeId));
                                                }
                                        }
                                        break;
                                case XMLStreamConstants.END_ELEMENT:
                                        if(RemoteCommons.endsWith(hierarchy,"contexts","context")) {
                                                mapContextList.add(curMapContext);
                                                curMapContext = null;
                                        } else if (RemoteCommons.endsWith(hierarchy,"contexts","context","title")) {
                                                Locale descLocale = Locale.getDefault();
                                                if(curLocale!=null) {
                                                        descLocale = curLocale;
                                                }
                                                curMapContext.getDescription().addTitle(descLocale,StringEscapeUtils.unescapeHtml(characters.toString().trim()));                                                
                                        } else if(RemoteCommons.endsWith(hierarchy,"contexts","context","abstract")) {
                                                Locale descLocale = Locale.getDefault();
                                                if(curLocale!=null) {
                                                        descLocale = curLocale;
                                                }
                                                curMapContext.getDescription().addAbstract(descLocale, StringEscapeUtils.unescapeHtml(characters.toString().trim()));
                                        }
                                        characters = new StringBuilder();
                                        curLocale = null;
                                        hierarchy.remove(hierarchy.size()-1);
                                        break;
                                case XMLStreamConstants.CHARACTERS:
                                        characters.append(StringEscapeUtils.unescapeHtml(parser.getText()));
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
                        new URL(RemoteCommons.getUrlContextList(cParams, workspaceName));
                // Establish connection
                HttpURLConnection connection = (HttpURLConnection) requestWorkspacesURL.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(cParams.getConnectionTimeOut());

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        throw new IOException(I18N.tr("HTTP Error {0} message : {1}", connection.getResponseCode(), connection.getResponseMessage()));
                }

                XMLInputFactory factory = XMLInputFactory.newInstance();

                // Read the response content
                BufferedReader in = new BufferedReader(
                                    new InputStreamReader(
                                    connection.getInputStream(),
                        RemoteCommons.getConnectionCharset(connection)));
                
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
                return contextList;
        }
}
