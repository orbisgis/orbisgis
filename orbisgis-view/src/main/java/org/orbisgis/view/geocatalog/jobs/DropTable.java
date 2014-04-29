/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
 * For more information, please consult: <http://www.orbisgis.org/> or contact
 * directly: info_at_ orbisgis.org
 */
package org.orbisgis.view.geocatalog.jobs;

import org.apache.log4j.Logger;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.viewapi.geocatalog.ext.GeoCatalogExt;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import javax.swing.*;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Drop provided table reference
 * @author Nicolas Fortin
 */
public class DropTable implements BackgroundJob, Runnable {
    private static final Logger LOGGER = Logger.getLogger("gui."+DropTable.class);
    private static final I18n I18N = I18nFactory.getI18n(DropTable.class);
    private DataSource dataSource;
    private String[] tableToDelete;
    private GeoCatalogExt geocatalog;


    /**
     * Constructor
     * @param dataSource SQL DataSource
     * @param tableToDelete Tables identifier
     * @param geocatalog Geocatalog
     */
    public DropTable(DataSource dataSource, String[] tableToDelete, GeoCatalogExt geocatalog) {
        this.dataSource = dataSource;
        this.tableToDelete = tableToDelete;
        this.geocatalog = geocatalog;
    }


    @Override
    public void run(ProgressMonitor pm) {
        try(Connection connection = dataSource.getConnection();
            Statement st = connection.createStatement()) {
            pm.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL,
                    EventHandler.create(PropertyChangeListener.class, st, "cancel"));
            connection.setAutoCommit(false);
            ProgressMonitor dropPm = pm.startTask(tableToDelete.length);
            for (String resource : tableToDelete) {
                TableLocation tableLocation = TableLocation.parse(resource,
                        JDBCUtilities.isH2DataBase(connection.getMetaData()));
                try {
                    LOGGER.info(String.format("drop table %s", tableLocation));
                    st.execute(String.format("drop table %s", tableLocation));
                } catch (SQLException ex) {
                    LOGGER.error(I18N.tr("Cannot remove the source {0}", resource), ex);
                    connection.rollback();
                    connection.setAutoCommit(true);
                    return;
                }
                dropPm.endTask();
            }
            connection.commit();
            SwingUtilities.invokeAndWait(this);
        } catch (SQLException ex) {
            LOGGER.error(I18N.trc("Tables are database tables, drop means delete tables", "Cannot drop the tables"), ex);
        } catch (InvocationTargetException | InterruptedException ex) {
            LOGGER.trace(ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    public void run() {
        geocatalog.refreshSourceList();
    }

    @Override
    public String getTaskName() {
        return I18N.tr("Drop selected tables");
    }
}
