/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.ows;

/**
 * Value object aimed at defining the properties of a data source. It is
 * mainly used in {@link OwsContextUtils} for storing the extracted data source
 * properties from JAXB.
 * @author cleglaun
 */
public class DbConnectionString {

    private final String host;
    private final int port;
    private final String db;
    private final String table;

    
    public DbConnectionString(String host, int port, String db, String table) {
        this.host = host;
        this.port = port;
        this.db = db;
        this.table = table;
    }

    /**
     * Gets the database name.
     * @return 
     */
    public String getDb() {
        return db;
    }

    /**
     * Gets the host hame.
     * @return 
     */
    public String getHost() {
        return host;
    }

    /**
     * Gets the port number.
     * @return 
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets the table name.
     * @return 
     */
    public String getTable() {
        return table;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DbConnectionString other = (DbConnectionString) obj;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.host != null ? this.host.hashCode() : 0);
        hash = 11 * hash + this.port;
        hash = 11 * hash + (this.db != null ? this.db.hashCode() : 0);
        hash = 11 * hash + (this.table != null ? this.table.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "DbConnectionString{" + "host=" + host + ", port=" + port + ", db=" + db + ", table=" + table + '}';
    }
}
