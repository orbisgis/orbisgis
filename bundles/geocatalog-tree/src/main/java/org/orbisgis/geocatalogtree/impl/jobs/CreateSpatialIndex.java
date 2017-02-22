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
package org.orbisgis.geocatalogtree.impl.jobs;

import java.awt.Component;
import org.h2gis.utilities.JDBCUtilities;
import org.orbisgis.commons.progress.ProgressMonitor;
import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.dbjobs.api.DatabaseView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import org.orbisgis.geocatalogtree.impl.nodes.TableAndField;
import org.orbisgis.sif.components.SQLMessageDialog;

/**
 * Create spatial index on a field
 * @author Nicolas Fortin
 */
public class CreateSpatialIndex extends SwingWorkerPM {

   
    private final DatabaseView databaseView;
    private final DataSource dataSource;
    private static final I18n I18N = I18nFactory.getI18n(CreateSpatialIndex.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateSpatialIndex.class);
    private final List<TableAndField> tablesAndField;


    public CreateSpatialIndex(List<TableAndField> tablesAndField, DatabaseView databaseView, DataSource dataSource) {
        this.tablesAndField = tablesAndField;
        this.databaseView = databaseView;
        this.dataSource = dataSource;
        setTaskName(I18N.tr("Create spatial index"));
    }

    @Override
    protected Object doInBackground() throws Exception {
        try(Connection connection = dataSource.getConnection();
            Statement st = connection.createStatement()) {
            PropertyChangeListener listener = EventHandler.create(PropertyChangeListener.class, st, "cancel");
            getProgressMonitor().addPropertyChangeListener(ProgressMonitor.PROP_CANCEL, listener);
            try {
                boolean isH2 = JDBCUtilities.isH2DataBase(connection.getMetaData());
                String query = getSQLCreateSpatialIndex(tablesAndField, isH2);
                LOGGER.info(I18N.tr("Create spatial index query:\n{0}", query));
                st.execute(query);
            } finally {
                getProgressMonitor().removePropertyChangeListener(listener);
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }
        return null;
    }
    
    /**
     * Create the SQL query
     * 
     * @param tablesAndField
     * @param isH2
     * @return 
     */
    private static String getSQLCreateSpatialIndex(List<TableAndField> tablesAndField, boolean isH2) {
        StringBuilder sb = new StringBuilder();        
        for (TableAndField tableAndField : tablesAndField) {
            if( sb.length()>0 ) {
                sb.append("\n");
            }
            sb.append("CREATE ");
            if (isH2) {
                sb.append("SPATIAL INDEX ON ");
            } else {
                sb.append("INDEX ON ");
            }
            sb.append(tableAndField.getTable());
            if (isH2) {
                sb.append("(");
                sb.append(tableAndField.getFieldName());
                sb.append(")");
            } else {
                sb.append(" USING GIST(");
                sb.append(tableAndField.getFieldName());
                sb.append(")");
            }
            sb.append(";");
        }
        return sb.toString();
    }

    @Override
    protected void done() {
        HashSet<String> tables = new HashSet<>();
        for (TableAndField tableAndField : tablesAndField) {
            tables.add(tableAndField.getTable());           
        }    
        databaseView.onDatabaseUpdate(DatabaseView.DB_ENTITY.TABLE.name(), tables.toArray(new String[tables.size()]));
    }
    
    public static CreateSpatialIndex onMenuCreateSpatialIndex(DataSource dataSource, List<TableAndField> indexIdentifier, Component parentComponent, DatabaseView dbView, boolean isH2) throws SQLException {
        if (indexIdentifier.isEmpty()) {
            return null;
        }
        String message = I18N.tr("Are you sure to create a spatial index on the selected column ?");
        SQLMessageDialog.CHOICE option = SQLMessageDialog.showModal(null, I18N.tr("Create spatial index"), message,
                getSQLCreateSpatialIndex(indexIdentifier, isH2));
        if (option == SQLMessageDialog.CHOICE.OK) {
            return new CreateSpatialIndex(indexIdentifier, dbView, dataSource);
        } else {
            return null;
        }
    }
    
}
