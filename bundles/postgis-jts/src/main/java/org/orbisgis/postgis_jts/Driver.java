/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.postgis_jts;

import org.postgis.jts.JtsWrapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Driver that provide directly the JTS Object when calling {@link java.sql.ResultSet#getObject(int)}
 * To create linked table on H2 database to PostGIS db:
 * CREATE LINKED TABLE mytable('org.orbisgis.postgis_jts.Driver',
 * 'jdbc:postgresql_h2://serverdomain:5432/databasename', 'user', 'password', '(select * from mytable)');
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
