package org.gdms.source.stream;

import java.io.Serializable;

/**
 * Class that contains the information to identify a stream
 *
 * @author Vincent Dépériers
 */
public class StreamSource implements Serializable {

    private static final long serialVersionUID = 0L;
    private String m_host;
    private int m_port;
    private String m_target;
    private String m_layerName;
    private String schemaName;
    private String m_imageFormat;
    private String m_user;
    private String m_password;
    private String m_prefix;
    private String m_srs;
    
    public StreamSource(String host, int port, String target, String layerName, String prefix) {
        this.m_host = host;
        this.m_port = port;
        this.m_target = target;
        this.m_layerName = layerName;
        this.m_prefix = prefix;
    }
    
    public StreamSource(String host, int port, String target, String layerName,
            String user, String password, String prefix) {
        this(host, port, target, layerName, prefix);
        this.m_user = user;
        this.m_password = password;
    }

    public StreamSource(String host, int port, String target, String layerName,
            String user, String password, String schemaName, String prefix) {
        this(host, port, target, layerName, user, password, prefix);
        this.schemaName = schemaName;
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

    public String getTarget() {
        return this.m_target;
    }

    public void setTarget(String target) {
        this.m_target = target;
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

    public String getSchemaName() {
        return schemaName;
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
        return m_host + "//" + m_target + "-" + m_port + "-" + m_layerName + "-" + m_user + "-" + m_password + "-" + m_imageFormat;
    }

    public String getDbms() {
        return m_host + ":" + m_port + "//" + m_target + "//request=getMap&layers=" + m_layerName;
    }

    public String getPrefix() {
        return m_prefix;
    }

    public void setPrefix(String prefix) {
        this.m_prefix = prefix;
    }
}
