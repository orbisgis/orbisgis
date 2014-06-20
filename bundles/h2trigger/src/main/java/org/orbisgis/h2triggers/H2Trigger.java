/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.h2triggers;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Single trigger class for H2 local connections.
 * @author Nicolas Fortin
 */
public class H2Trigger implements Trigger {
    private static TriggerFactory triggerFactory;
    private Trigger wrappedTrigger;

    /**
     * Add a triggerFactory linked with connection url
     * @param triggerFactory TriggerListener instance or null to unset
     */
    public static void setTriggerFactory(TriggerFactory triggerFactory) {
        H2Trigger.triggerFactory = triggerFactory;
    }

    @Override
    public void init(Connection conn, String schemaName, String triggerName, String tableName, boolean before, int type) throws SQLException {
        if(triggerFactory != null) {
            wrappedTrigger = triggerFactory.createTrigger(conn, schemaName, triggerName, tableName, before, type);
        }
    }

    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
        if(wrappedTrigger != null) {
            wrappedTrigger.fire(conn, oldRow, newRow);
        }
    }

    @Override
    public void close() throws SQLException {
        if(wrappedTrigger != null) {
            wrappedTrigger.close();
        }
    }

    @Override
    public void remove() throws SQLException {
        if(wrappedTrigger != null) {
            wrappedTrigger.remove();
        }
    }
}
