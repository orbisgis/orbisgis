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
package org.orbisgis.dbjobs.jobs;

import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.commons.progress.ProgressMonitor;
import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.corejdbc.MetaData;
import org.orbisgis.dbjobs.api.DatabaseView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Drop provided table reference
 * @author Nicolas Fortin
 */
public class DropTable extends SwingWorkerPM {
    private static final Logger LOGGER = LoggerFactory.getLogger("gui." + DropTable.class);
    private static final I18n I18N = I18nFactory.getI18n(DropTable.class);
    private DataSource dataSource;
    private String[] tableToDelete;
    private DatabaseView dbView;


    /**
     * Constructor
     * @param dataSource SQL DataSource
     * @param tableToDelete Tables identifier
     * @param dbView GUI to update
     */
    public DropTable(DataSource dataSource, String[] tableToDelete, DatabaseView dbView) {
        this.dataSource = dataSource;
        this.tableToDelete = tableToDelete;
        this.dbView = dbView;
        setTaskName(I18N.tr("Drop selected tables"));
    }

    @Override
    protected Object doInBackground() throws Exception {
        try(Connection connection = dataSource.getConnection();
            Statement st = connection.createStatement()) {
            this.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL,
                    EventHandler.create(PropertyChangeListener.class, st, "cancel"));
            connection.setAutoCommit(false);
            ProgressMonitor dropPm = this.getProgressMonitor().startTask(tableToDelete.length);
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
                    return null;
                }
                dropPm.endTask();
            }
            connection.commit();
        } catch (SQLException ex) {
            LOGGER.error(I18N.trc("Tables are database tables, drop means delete tables", "Cannot drop the tables"), ex);
        }
        return null;
    }

    @Override
    protected void done() {
        dbView.onDatabaseUpdate(DatabaseView.DB_ENTITY.TABLE.name(), tableToDelete);
    }


    /**
     * The user can drop table using GUI
     * @param dataSource JDBC DataSource
     * @param tableIdentifier List of table to remove {@link org.h2gis.utilities.TableLocation}
     * @param parentComponent Parent component for dialogs
     * @param dbView GUI to update
     * @return instance of DropTable Job to execute. Null if user cancel
     */
    public static DropTable onMenuRemoveSource(DataSource dataSource, List<String> tableIdentifier, Component parentComponent, DatabaseView dbView) {
        int countExternalTable = 0;
        int countSystemTable = 0;
        int countOther = 0;
        ArrayList<String> sources = new ArrayList<String>();
        List<String> reservedTables = java.util.Arrays.asList("spatial_ref_sys", "geography_columns", "geometry_columns", "raster_columns", "raster_overviews");
        try (Connection connection = dataSource.getConnection()) {
            for (String tableName : tableIdentifier) {
                MetaData.TableType tableType = MetaData.getTableType(connection, tableName);
                if (tableType.equals(MetaData.TableType.EXTERNAL)) {
                    countExternalTable++;
                    sources.add(tableName);
                } else if (tableType.equals(MetaData.TableType.SYSTEM_TABLE)) {
                    countSystemTable++;
                } else if (reservedTables.contains(tableName.toLowerCase())){
                    countSystemTable++;
                }else {
                    sources.add(tableName);
                    countOther++;
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }
        //We display a warning because some SYSTEM_TABLE have been selected.
        if (countSystemTable > 0) {
            JOptionPane.showMessageDialog(parentComponent, I18N.tr("Cannot remove permanently a table system."), I18N.tr
                    ("Remove GeoCatalog tables"), JOptionPane.WARNING_MESSAGE);
        } else {
            //We display the table type
            StringBuilder sb = new StringBuilder(I18N.tr("Do you want..."));
            if (countOther > 0) {
                sb.append(I18N.trn("\n...to remove permanently {0} table", "\n...to remove permanently {0} tables", countOther, countOther));
            }
            if (countExternalTable > 0) {
                sb.append(I18N.trn("\n...to disconnect {0} external table", "\n...to disconnect {0} external tables", countExternalTable, countExternalTable));
            }
            sb.append("?");
            int option = JOptionPane.showConfirmDialog(parentComponent,
                    sb.toString(),
                    I18N.tr("Delete GeoCatalog tables"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (option == JOptionPane.YES_OPTION) {
                return new DropTable(dataSource, sources.toArray(new String[sources.size()])
                        , dbView);
            }
        }
        return null;
    }
}
