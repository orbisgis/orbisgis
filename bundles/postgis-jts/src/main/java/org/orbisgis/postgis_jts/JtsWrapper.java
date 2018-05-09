package org.orbisgis.postgis_jts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.postgresql.Driver;
import org.postgresql.PGConnection;

public class JtsWrapper extends Driver {
    protected static final Logger logger = Logger.getLogger("org.postgis.DriverWrapper");
    private static final String POSTGRES_PROTOCOL = "jdbc:postgresql:";
    private static final String POSTGIS_PROTOCOL = "jdbc:postgres_jts:";
    public static final String REVISION = "$Revision$";

    public JtsWrapper() {
    }

    public Connection connect(String url, Properties info) throws SQLException {
        url = mangleURL(url);
        Connection result = super.connect(url, info);
        addGISTypes((PGConnection)result);
        return result;
    }

    public static void addGISTypes(PGConnection pgconn) throws SQLException {
        pgconn.addDataType("geometry", JtsGeometry.class);
    }

    public static String mangleURL(String url) throws SQLException {
        if (url.startsWith("jdbc:postgres_jts:")) {
            return "jdbc:postgresql:" + url.substring("jdbc:postgres_jts:".length());
        } else {
            throw new SQLException("Unknown protocol or subprotocol in url " + url);
        }
    }

    public boolean acceptsURL(String url) throws SQLException {
        try {
            url = mangleURL(url);
        } catch (SQLException var3) {
            return false;
        }

        return super.acceptsURL(url);
    }

    public int getMajorVersion() {
        return super.getMajorVersion();
    }

    public int getMinorVersion() {
        return super.getMinorVersion();
    }

    public static String getVersion() {
        return "JtsGisWrapper $Revision$, wrapping " + Driver.getVersion();
    }

    static {
        try {
            DriverManager.registerDriver(new JtsWrapper());
        } catch (SQLException var1) {
            logger.log(Level.WARNING, "Error registering PostgreSQL Jts Wrapper Driver", var1);
        }

    }
}
