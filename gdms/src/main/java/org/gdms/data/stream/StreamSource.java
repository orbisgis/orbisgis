/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.data.stream;

import org.geotools.util.Version;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains the information to identify a stream.
 *
 * @author Antoine Gourlay
 * @author Vincent Dépériers
 */
public class StreamSource implements Serializable {
        private static final int DEFAULT_PORT = 80;
        private static final long serialVersionUID = 144456789L;
        /** Layers name */
        public static final String LAYER_PARAMETER = "layers";
        /** Image type ex:image/png */
        public static final String OUTPUTFORMAT_PARAMETER = "outputformat";
        /** CRS replace SRS from this version */
        public static final Version CRS_BEGINNING_VERSION = new Version("1.3.0");
        /** Coordinate reference system ex:EPSG:27572 Version >= 1.3.0 */
        public static final String CRS_PARAMETER = "crs"; // Version >= 1.3.0
        /** Spatial reference system ex:EPSG:27572 Version < 1.3.0 */
        public static final String SRS_PARAMETER = "srs"; // Version < 1.3.0
        /** SERVICE ex:WMS */
        public static final String SERVICE_PARAMETER = "service";
        /** WMS server version */
        public static final String VERSION_PARAMETER = "version";

        private String scheme="http";
        private String host;
        private int port;
        private String path="";
        private Version version;
        private Map<String,String> parameters = new HashMap<String, String>();


        /**
         * Short constructor, imageFormat is "image/png", crs is "EPSG:4326", version 1.3.0
         * @param scheme Protocol ex:http
         * @param host Host name or IP, ex: services.orbisgis.org
         * @param port Service port, -1 for default
         * @param path URL path
         * @param layerName Layers, separated by a comma character
         * @param service Service (ex: WMS)
         */
        public StreamSource(String scheme, String host, int port, String path, String layerName, String service) {
                this(scheme,host, port, path, layerName, service, "image/png", "EPSG:4326", "1.3.0");
        }

        /**
         * Complete constructor.
         * @param scheme Protocol ex:http
         * @param host Host name or IP, ex: services.orbisgis.org
         * @param port Service port, -1 for default
         * @param path URL path
         * @param layerName Layers, separated by a comma character
         * @param service Service (ex: WMS)
         * @param imageFormat Output format of the service ex:image/png
         * @param crs Spatial reference system ex:
         * @param version Wms server version ex: 1.3.0
         */
        public StreamSource(String scheme, String host, int port, String path, String layerName, String service,
                String imageFormat, String crs,String version) {
                this.scheme = scheme;
                this.host = host;
                this.path = path;
                setVersion(version);
                if(port<=0) { //invalid port
                    try {
                        this.port = URI.create(scheme+"://dummy.org").toURL().getDefaultPort();
                    } catch (MalformedURLException ex) {
                        this.port = DEFAULT_PORT;
                    }
                }
                parameters.put(LAYER_PARAMETER,layerName);
                parameters.put(SERVICE_PARAMETER,service);
                parameters.put(OUTPUTFORMAT_PARAMETER,imageFormat);
                if(this.version.compareTo(CRS_BEGINNING_VERSION)<0) {
                    parameters.put(SRS_PARAMETER,crs);
                } else {
                    parameters.put(CRS_PARAMETER,crs);
                }
        }

        /**
         * Set the service version
         * @param version Service version "x.y.z"
         */
        public void setVersion(String version) {
            parameters.put(VERSION_PARAMETER,version);
            this.version = new Version(version);
        }

        /**
         * @return The stream version x.y.z
         */
        public String getVersion() {
            return parameters.get(VERSION_PARAMETER);
        }
        /**
         * Construct the stream source from an URI
         * @param uri Stream uri
         * @throws IllegalArgumentException If the URI does not contain a required query, fragment
         * @throws UnsupportedEncodingException If the URI contains non-utf8 characters
         */
        public StreamSource(URI uri) throws IllegalArgumentException, UnsupportedEncodingException {
            host = uri.getHost();
            port = uri.getPort();
            scheme = uri.getScheme();
            path = uri.getPath();
            if(port==-1) {
                try {
                    port = uri.toURL().getDefaultPort();
                } catch (MalformedURLException ex) {
                    // Not a url
                    port = DEFAULT_PORT;
                }
            }
            parameters = new HashMap<String, String>(URIUtility.getQueryKeyValuePairs(uri));
            // If version is not set in the URI, find the appropriate one by using the projection system variable
            String version = parameters.get(VERSION_PARAMETER);
            if(version==null) {
                String srs = parameters.get(SRS_PARAMETER);
                String crs = parameters.get(CRS_PARAMETER);
                if(crs!=null || srs==null) {
                    setVersion("1.3.0");
                } else {
                    setVersion("1.1.1");
                }
            } else {
                setVersion(version);
            }
        }
        private String getQuery() {
            if(version.compareTo(CRS_BEGINNING_VERSION)<0) {
                return URIUtility.getConcatenatedParameters(parameters, SERVICE_PARAMETER, LAYER_PARAMETER, SRS_PARAMETER, VERSION_PARAMETER, OUTPUTFORMAT_PARAMETER);
            } else {
                return URIUtility.getConcatenatedParameters(parameters, SERVICE_PARAMETER, LAYER_PARAMETER, CRS_PARAMETER, VERSION_PARAMETER, OUTPUTFORMAT_PARAMETER);
            }
        }
        /**
         * @return URI equivalent of this request
         */
        public URI toURI() {
            try {
                return new URI(scheme,null,host,port,path,getQuery(),null);
            } catch (URISyntaxException ex) {
                return null;
            }
        }
        /**
         * @return the host of the source.
         */
        public String getHost() {
                return this.host;
        }

        /**
         * @return URI query as a map of key, values
         */
        public Map<String,String> getQueryMap() {
            return Collections.unmodifiableMap(parameters);
        }
        /**
         * @return The path of the service
         */
        public String getPath() {
                return path;
        }

        /**
         * @return The protocol of the stream (ex: http)
         */
        public String getScheme() {
            return scheme;
        }
        /**
         * Sets the host of the source.
         *
         * @param host a new host
         */
        public void setHost(String host) {
                this.host = host;
        }

        /**
         * @return the port of the source
         */
        public int getPort() {
                return port;
        }

        /**
         * Sets the port of the source.
         *
         * @param port a new port number
         */
        public void setPort(int port) {
                this.port = port;
        }

        /**
         * @return the name of the layer of the source
         */
        public String getLayerName() {
                return parameters.get(LAYER_PARAMETER);
        }

        /**
         * Sets the name of the layer of the source.
         *
         * @param layerName a new layer name
         */
        public void setLayerName(String layerName) {
                parameters.put(LAYER_PARAMETER,layerName);
        }

        /**
         * @return the format of the image, as a MIME type
         */
        public String getImageFormat() {
                return parameters.get(OUTPUTFORMAT_PARAMETER);
        }

        /**
         * Sets the format (MIME type) of the image.
         *
         * @param imageFormat a MIME format string.
         */
        public void setImageFormat(String imageFormat) {
                parameters.put(OUTPUTFORMAT_PARAMETER,imageFormat);
        }

        /**
         * Sets the srs of the source.
         * If version < 1.3.0
         * @param srs a new SRS String
         */
        public void setSRS(String srs) {
                parameters.put(SRS_PARAMETER,srs);
        }

        /**
         * @return the srs of the source
         */
        public String getSRS() {
                return getReferenceSystem();
        }
        private String getReferenceSystem() {
            if(version.compareTo(CRS_BEGINNING_VERSION)<0) {
                return parameters.get(SRS_PARAMETER);
            } else {
                return parameters.get(CRS_PARAMETER);
            }
        }
        /**
         * @return the crs of the source
         */
        public String getCRS() {
                return getReferenceSystem();
        }

        /**
         * Sets the crs of the source.
         * If version >= 1.3.0
         * @param crs a new CRS String
         */
        public void setCRS(String crs) {
                parameters.put(CRS_PARAMETER,crs);
        }

        @Override
        public String toString() {
                return toURI().toString();
        }

        /**
         * @return the prefix of the source
         */
        public String getStreamType() {
                return parameters.get(SERVICE_PARAMETER);
        }

        /**
         * Sets the prefix of the source.
         *
         * @param type a new prefix
         */
        public void setStreamType(String type) {
                parameters.put(SERVICE_PARAMETER,type);
        }

        @Override
        public boolean equals(Object obj) {
                if (obj instanceof StreamSource) {
                        final StreamSource other = (StreamSource) obj;
                        if ((this.host == null) ? (other.host != null) : !this.host.equals(other.host)) {
                                return false;
                        }
                        if((this.path == null) ? (other.path != null) : !this.path.equals(other.path)) {
                                return false;
                        }
                        if (this.port != other.port) {
                                return false;
                        }
                        // Compare query
                        for(Map.Entry<String,String> entry : other.getQueryMap().entrySet()) {
                            String value = parameters.get(entry.getKey());
                            if((value==null && entry.getValue()!=null) ||
                                    !(value!=null && value.equals(entry.getValue()))) {
                                return false;
                            }
                        }
                        return true;
                } else {
                    return false;
                }
        }

    @Override
    public int hashCode() {
        int result = scheme.hashCode();
        result = 31 * result + host.hashCode();
        result = 31 * result + port;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + parameters.hashCode();
        return result;
    }
}
