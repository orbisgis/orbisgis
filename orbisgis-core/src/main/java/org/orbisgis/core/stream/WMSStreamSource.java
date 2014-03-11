/**
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
package org.orbisgis.core.stream;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Contains the information to identify a stream. We retain only the needed
 * information gathered from the input URI. Consequently, some WMS request
 * parameters are useless and forgotten :
 * <ul>
 * <li>TIME</li>
 * <li>WIDTH</li>
 * <li>HEIGHT</li>
 * <li>BBOX</li>
 * <li>TRANSPARENT</li>
 * <li>BGCOLOR</li>
 * <li>ELEVATION</li>
 * <li>EXCEPTIONS</li>
 * </ul>
 *
 * <p>All the other keys are kept. They are put in two distinct maps. The first
 * one is dedicated to WMS keys. The second map is dedicated to the keys that
 * are not described in the WMS specifications. They are kept in order to
 * preserve our ability to give mandatory information to some WMS server
 * implementations.</p>
 * <p>This strategy makes this source quite resistant to user input. Indeed,
 * we can accept about any valid GetMap request as an input and obtain a valid
 * WMSStreamSource.
 *
 * @author Antoine Gourlay
 * @author Vincent Dépériers
 * @author Alexis Guéganno
 */
public final class WMSStreamSource implements Serializable {
        /**
         * Default HTTP port : 80
         */
        public static final int DEFAULT_PORT = 80;
        private static final long serialVersionUID = 144456789L;
        public static final String SERVICE_NAME = "wms";
        /** Layers name */
        public static final String LAYER_PARAMETER = "layers";
        /** Image type ex:image/png */
        public static final String OUTPUTFORMAT_PARAMETER = "format";
        /** CRS replace SRS from this version */
        public static final WMSVersion CRS_BEGINNING_VERSION = WMSVersion.fromString("1.3.0");
        /** Coordinate reference system ex:EPSG:27572 Version &gt;= 1.3.0 */
        public static final String CRS_PARAMETER = "crs"; // Version >= 1.3.0
        /** Spatial reference system ex:EPSG:27572 Version &lt; 1.3.0 */
        public static final String SRS_PARAMETER = "srs"; // Version < 1.3.0
        /** SERVICE, must be WMS */
        public static final String SERVICE_PARAMETER = "service";
        /** WMS server version */
        public static final String VERSION_PARAMETER = "version";
        /**
         * WMS keys that we want to ignore
         */
        public static final String TIME_PARAMETER = "time";
        public static final String WIDTH_PARAMETER = "width";
        public static final String HEIGHT_PARAMETER = "height";
        public static final String BBOX_PARAMETER = "bbox";
        public static final String TRANSPARENT_PARAMETER = "TRANSPARENT";
        public static final String BGCOLOR_PARAMETER = "BGCOLOR";
        public static final String ELEVATION_PARAMETER = "elevation";
        public static final String EXCEPTIONS_PARAMETER = "exceptions";
        public static final String REQUEST_PARAMETER = "request";
        public static final Set<String> WMS_KEYS;
        public static final Set<String> IGNORED_WMS_KEYS;
        static {
            HashSet<String> keys = new HashSet<String>();
            keys.add(LAYER_PARAMETER);
            keys.add(OUTPUTFORMAT_PARAMETER);
            keys.add(CRS_PARAMETER);
            keys.add(SRS_PARAMETER);
            keys.add(SERVICE_PARAMETER);
            keys.add(VERSION_PARAMETER);
            WMS_KEYS = Collections.unmodifiableSet(keys);
            HashSet<String> ignored = new HashSet<String>();
            ignored.add(TIME_PARAMETER);
            ignored.add(WIDTH_PARAMETER);
            ignored.add(HEIGHT_PARAMETER);
            ignored.add(BBOX_PARAMETER);
            ignored.add(TRANSPARENT_PARAMETER);
            ignored.add(BGCOLOR_PARAMETER);
            ignored.add(ELEVATION_PARAMETER);
            ignored.add(EXCEPTIONS_PARAMETER);
            ignored.add(REQUEST_PARAMETER);
            IGNORED_WMS_KEYS = Collections.unmodifiableSet(ignored);
        }

        private String scheme="http";
        private String host;
        private int port;
        private String path="";
        private WMSVersion version;
        private Map<String,String> wmsParameters = new HashMap<String, String>();
        private Map<String,String> otherParameters = new HashMap<String, String>();


        /**
         * Short constructor, imageFormat is "image/png", crs is "EPSG:4326", version 1.3.0
         * @param scheme Protocol ex:http
         * @param host Host name or IP, ex: services.orbisgis.org
         * @param port Service port, -1 for default
         * @param path URL path
         * @param layerName Layers, separated by a comma character
         * @param service Service (ex: WMS)
         */
        @Deprecated
        public WMSStreamSource(String scheme, String host, int port, String path, String layerName, String service) {
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
        @Deprecated
        public WMSStreamSource(String scheme, String host, int port, String path, String layerName, String service,
                               String imageFormat, String crs, String version) {
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
                wmsParameters.put(LAYER_PARAMETER,layerName);
                wmsParameters.put(SERVICE_PARAMETER,service);
                wmsParameters.put(OUTPUTFORMAT_PARAMETER,imageFormat);
                if(this.version.equals(WMSVersion.VERSION_1_3_0)) {
                    wmsParameters.put(SRS_PARAMETER,crs);
                } else {
                    wmsParameters.put(CRS_PARAMETER,crs);
                }
        }

        /**
         * Set the service version
         * @param version Service version "x.y.z"
         */
        public void setVersion(String version) {
            wmsParameters.put(VERSION_PARAMETER,version);
            this.version = WMSVersion.fromString(version);
        }

        /**
         * @return The stream version x.y.z
         */
        public String getVersion() {
            return wmsParameters.get(VERSION_PARAMETER);
        }
        /**
         * Construct the stream source from an URI
         * @param uri Stream uri
         * @throws IllegalArgumentException If the URI does not contain a required query, fragment
         * @throws UnsupportedEncodingException If the URI contains non-utf8 characters
         */
        public WMSStreamSource(URI uri) throws IllegalArgumentException, UnsupportedEncodingException {
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
            feedParameters(URIUtility.getQueryKeyValuePairs(uri));
            // If version is not set in the URI, find the appropriate one by using the projection system variable
            String vers = wmsParameters.get(VERSION_PARAMETER);
            if(vers==null) {
                String srs = wmsParameters.get(SRS_PARAMETER);
                String crs = wmsParameters.get(CRS_PARAMETER);
                if(crs!=null || srs==null) {
                    setVersion("1.3.0");
                } else {
                    setVersion("1.1.1");
                }
            } else {
                setVersion(vers);
            }
            if(wmsParameters.get(SERVICE_PARAMETER) == null){
                wmsParameters.put(SERVICE_PARAMETER, SERVICE_NAME);
            }
        }

        private void feedParameters(Map<String,String> keyValues){
            for(Map.Entry<String,String> entry : keyValues.entrySet()){
                String key = entry.getKey();
                if(!IGNORED_WMS_KEYS.contains(key)){
                    if(WMS_KEYS.contains(key)){
                        wmsParameters.put(key, entry.getValue());
                    } else {
                        otherParameters.put(key, entry.getValue());
                    }
                }
            }

        }

        /**
         * Gets a String that gathers all the HTTP parameters not related to the
         * WMS query.
         * @return
         */
        private String getOtherParameters(){
            Set<String> keys = otherParameters.keySet();
            String[] toArray = keys.toArray(new String[keys.size()]);
            return URIUtility.getConcatenatedParameters( otherParameters, toArray);
        }

        /**
         * Gets a String gathering all the HTTP parameters linked to the WMS
         * query.
         * @return
         */
        private String getQuery() {
            if(version.equals(WMSVersion.VERSION_1_3_0)) {
                return URIUtility.getConcatenatedParameters(
                        wmsParameters, SERVICE_PARAMETER, LAYER_PARAMETER, CRS_PARAMETER, VERSION_PARAMETER, OUTPUTFORMAT_PARAMETER);
            } else {
                return URIUtility.getConcatenatedParameters(
                        wmsParameters, SERVICE_PARAMETER, LAYER_PARAMETER, SRS_PARAMETER, VERSION_PARAMETER, OUTPUTFORMAT_PARAMETER);
            }
        }

        public URI toServerURI(){
            try {
                return new URI(scheme,null,host,port,path,getQuery(),null);
            } catch (URISyntaxException ex) {
                return null;
            }
        }

        /**
         * @return URI equivalent of this request
         */
        public URI toURI() {
            try {
                StringBuilder sb = new StringBuilder();
                String o = getOtherParameters();
                if(o!=null && !o.isEmpty()){
                    sb.append(o).append("&");
                }
                sb.append(getQuery());
                return new URI(scheme,null,host,port,path,sb.toString(),null);
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
         * 
         * @return URI query as a map of key, values. We only have pairs not related
         * to the WMS query here.
         */
        public Map<String,String> getOthersQueryMap(){
            return Collections.unmodifiableMap(otherParameters);
        }

        /**
         * @return URI query as a map of key, values. We only have pairs related
         * to the WMS query here.
         */
        public Map<String,String> getQueryMap() {
            return Collections.unmodifiableMap(wmsParameters);
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
                return wmsParameters.get(LAYER_PARAMETER);
        }

        /**
         * Sets the name of the layer of the source.
         *
         * @param layerName a new layer name
         */
        public void setLayerName(String layerName) {
                wmsParameters.put(LAYER_PARAMETER,layerName);
        }

        /**
         * @return the format of the image, as a MIME type
         */
        public String getImageFormat() {
                return wmsParameters.get(OUTPUTFORMAT_PARAMETER);
        }

        /**
         * Sets the format (MIME type) of the image.
         *
         * @param imageFormat a MIME format string.
         */
        public void setImageFormat(String imageFormat) {
                wmsParameters.put(OUTPUTFORMAT_PARAMETER,imageFormat);
        }

        /**
         * Sets the srs of the source.
         * If version < 1.3.0
         * @param srs a new SRS String
         */
        public void setSRS(String srs) {
                wmsParameters.put(SRS_PARAMETER,srs);
        }

        /**
         * @return the srs of the source
         */
        public String getSRS() {
                return getReferenceSystem();
        }
        private String getReferenceSystem() {
            if(version.equals(WMSVersion.VERSION_1_3_0)) {
                return wmsParameters.get(CRS_PARAMETER);
            } else {
                return wmsParameters.get(SRS_PARAMETER);
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
                wmsParameters.put(CRS_PARAMETER,crs);
        }

        @Override
        public String toString() {
                return toURI().toString();
        }

        /**
         * @return the prefix of the source
         */
        public String getStreamType() {
                return wmsParameters.get(SERVICE_PARAMETER);
        }

        /**
         * Sets the prefix of the source.
         *
         * @param type a new prefix
         */
        public void setStreamType(String type) {
                wmsParameters.put(SERVICE_PARAMETER,type);
        }

        @Override
        public boolean equals(Object obj) {
                if (obj instanceof WMSStreamSource) {
                        final WMSStreamSource other = (WMSStreamSource) obj;
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
                            String value = wmsParameters.get(entry.getKey());
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
        result = 31 * result + wmsParameters.hashCode();
        return result;
    }
}
