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
 * @author Vincent Dépériers
 */
public class StreamSource implements Serializable {

    private static final long serialVersionUID = 123456789L;
    private String m_host;
    private int m_port;
    private String m_layerName;
    //private String schemaName;
    private String m_imageFormat;
    private String m_user;
    private String m_password;
    private String m_prefix;
    private String m_srs;
    
    public StreamSource(String host, int port, String layerName, String prefix) {
        this(host, port, layerName, "", "", prefix);
    }
    
    public StreamSource(String host, int port, String layerName,
            String user, String password, String prefix) {
        this.m_host = host;
        this.m_port = port;
        this.m_layerName = layerName;
        this.m_prefix = prefix;
        this.m_user = user;
        this.m_password = password;
    }
    
   public StreamSource(String host, int port, String layerName,
            String user, String password, String prefix, String imageFormat, String srs) {
        this(host, port, layerName, user, password, prefix);
        this.m_imageFormat = imageFormat;
        this.m_srs = srs;
    }

    public String getHost() {
        return this.m_host;
    }

    public void setHost(String host) {
        this.m_host = host;
    }

    public int getPort() {
        return m_port;
    }

    public void setPort(int port) {
        this.m_port = port;
    }

    public String getLayerName() {
        return this.m_layerName;
    }

    public void setLayerName(String layerName) {
        this.m_layerName = layerName;
    }

    public String getImageFormat() {
        return this.m_imageFormat;
    }

    public void setImageFormat(String imageFormat) {
        this.m_imageFormat = imageFormat;
    }

    public String getUser() {
        return m_user;
    }

    public void setUser(String user) {
        this.m_user = user;
    }

    public String getPassword() {
        return m_password;
    }

    public void setPassword(String password) {
        this.m_password = password;
    }

    public void setSRS(String srs) {
        this.m_srs = srs;
    }

    public String getSRS() {
        return m_srs;
    }
    /**
     * Returns a human-readable description of this DBSource
     *
     * @return a description String
     */
    @Override
    public String toString() {
        return m_host + ":" + m_port + "-" + m_layerName + "-" + m_user + "-" + m_password + "-" + m_imageFormat;
    }

    public String getDbms() {
        return m_host + ":" + m_port + "//request=getMap&layers=" + m_layerName;
    }

    public String getPrefix() {
        return m_prefix;
    }

    public void setPrefix(String prefix) {
        this.m_prefix = prefix;
    }
}
