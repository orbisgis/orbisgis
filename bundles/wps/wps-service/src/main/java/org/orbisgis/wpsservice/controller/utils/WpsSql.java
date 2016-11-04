package org.orbisgis.wpsservice.controller.utils;

import groovy.sql.Sql;

import javax.sql.DataSource;
import java.util.logging.Level;

/**
 * Subclass of the Groovy Sql class.
 * The only difference is that the log is shutdown.
 *
 * @author Sylvain PALOMINOS
 */
public class WpsSql extends Sql {
    public WpsSql(DataSource dataSource) {
        super(dataSource);
        LOG.setLevel(Level.OFF);
    }
}
