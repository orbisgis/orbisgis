package org.orbisgis.postgis_jts_osgi;

import org.postgis.jts.JtsWrapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Driver that provide directly the JTS Object when calling {@link java.sql.ResultSet#getObject(int)}
 * @author Nicolas Fortin
 */
public class Driver extends JtsWrapper {

    private static final String POSTGIS_PROTOCOL = "jdbc:postgres_jts:";
    public static final String POSTGIS_H2PROTOCOL = "jdbc:postgresql_h2:";

    static {
        try {
            // Try to register ourself to the DriverManager
            java.sql.DriverManager.registerDriver(new Driver());
        } catch (SQLException e) {
            logger.log(Level.WARNING, "PostGIS H2 compatible Driver", e);
        }
    }

    public Driver() throws SQLException {
        super();
    }

    /**
     * Mangles the PostGIS URL to return the original PostGreSQL URL
     */
    public static String mangleURL(String url) throws SQLException {
        if (url.startsWith(POSTGIS_H2PROTOCOL)) {
            return POSTGIS_PROTOCOL + url.substring(POSTGIS_H2PROTOCOL.length());
        } else {
            throw new SQLException("Unknown protocol or subprotocol in url " + url);
        }
    }

    /**
     * Check whether the driver thinks he can handle the given URL.
     *
     * @see java.sql.Driver#acceptsURL
     * @param url the URL of the driver
     * @return true if this driver accepts the given URL
     * @exception SQLException Passed through from the underlying PostgreSQL
     *                driver, should not happen.
     */
    public boolean acceptsURL(String url) throws SQLException {
        try {
            url = mangleURL(url);
        } catch (SQLException e) {
            return false;
        }
        return super.acceptsURL(url);
    }

    /**
     * Returns our own CVS version plus postgres Version
     */
    public static String getVersion() {
        return "H2 compatible driver, wrapping pg " + org.postgresql.Driver.getVersion();
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        return new ConnectionWrapper(super.connect(POSTGIS_PROTOCOL + url.substring(POSTGIS_H2PROTOCOL.length()), info));
    }
}
