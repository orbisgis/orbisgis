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

import java.io.Serializable;

/**
 * Contains the information to identify a stream.
 *
 * @author Antoine Gourlay
 * @author Vincent Dépériers
 */
public class StreamSource implements Serializable {

        private static final long serialVersionUID = 123456789L;
        private String host;
        private int port;
        private String layerName;
        private String imageFormat;
        private String type;
        private String srs;

        /**
         * Creates a new Stream Source with the host, the port,the name of the layer and the prefix.
         *
         * @param host
         * @param port
         * @param layerName
         * @param prefix
         */
        public StreamSource(String host, int port, String layerName, String prefix) {
                this(host, port, layerName, prefix, "image/png", "");
        }

        /**
         * Creates a new Stream Source with the host, the port,the name of the layer ,
         * the prefix,the format of the image and the srs.
         *
         * @param host
         * @param port
         * @param layerName
         * @param prefix
         * @param imageFormat
         * @param srs
         */
        public StreamSource(String host, int port, String layerName, String prefix,
                String imageFormat, String srs) {
                this.host = host;
                this.port = port;
                this.layerName = layerName;
                this.type = prefix;
                this.imageFormat = imageFormat;
                this.srs = srs;
        }

        /**
         * @return the host of the source.
         */
        public String getHost() {
                return this.host;
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
                return this.layerName;
        }

        /**
         * Sets the name of the layer of the source.
         *
         * @param layerName a new layer name
         */
        public void setLayerName(String layerName) {
                this.layerName = layerName;
        }

        /**
         * @return the format of the image
         */
        public String getImageFormat() {
                return this.imageFormat;
        }

        /**
         * Sets the format of the image.
         *
         * @param imageFormat a new MIME format string
         */
        public void setImageFormat(String imageFormat) {
                this.imageFormat = imageFormat;
        }

        /**
         * Sets the srs of the source.
         *
         * @param srs a new SRS String
         */
        public void setSRS(String srs) {
                this.srs = srs;
        }

        /**
         * @return the srs of the source
         */
        public String getSRS() {
                return srs;
        }

        @Override
        public String toString() {
                return type + "-" + host + ":" + port + "-" + layerName + "-" + imageFormat + "-" + srs;
        }

        public String getDbms() {
                return host + ":" + port + "//request=getMap&layers=" + layerName;
        }

        /**
         * @return the prefix of the source
         */
        public String getStreamType() {
                return type;
        }

        /**
         * Sets the prefix of the source.
         *
         * @param type a new prefix
         */
        public void setStreamType(String type) {
                this.type = type;
        }

        @Override
        public boolean equals(Object obj) {
                if (obj instanceof StreamSource) {
                        final StreamSource other = (StreamSource) obj;
                        if ((this.host == null) ? (other.host != null) : !this.host.equals(other.host)) {
                                return false;
                        }
                        if (this.port != other.port) {
                                return false;
                        }
                        if ((this.layerName == null) ? (other.layerName != null) : !this.layerName.equals(other.layerName)) {
                                return false;
                        }
                        if ((this.imageFormat == null) ? (other.imageFormat != null) : !this.imageFormat.equals(other.imageFormat)) {
                                return false;
                        }
                        if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type)) {
                                return false;
                        }
                        if ((this.srs == null) ? (other.srs != null) : !this.srs.equals(other.srs)) {
                                return false;
                        }
                        return true;
                }
                return false;
        }

        @Override
        public int hashCode() {
                int hash = 7;
                hash = 73 * hash + (this.host != null ? this.host.hashCode() : 0);
                hash = 73 * hash + this.port;
                hash = 73 * hash + (this.layerName != null ? this.layerName.hashCode() : 0);
                hash = 73 * hash + (this.imageFormat != null ? this.imageFormat.hashCode() : 0);
                hash = 73 * hash + (this.type != null ? this.type.hashCode() : 0);
                hash = 73 * hash + (this.srs != null ? this.srs.hashCode() : 0);
                return hash;
        }
}
