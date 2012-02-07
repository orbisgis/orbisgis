/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package orb.orbisgis.core.ui.plugins.ows;

/**
 *
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

    public String getDb() {
        return db;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

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
