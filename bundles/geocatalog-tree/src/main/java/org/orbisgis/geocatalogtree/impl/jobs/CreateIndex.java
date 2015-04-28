/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */

package org.orbisgis.geocatalogtree.impl.jobs;

import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.commons.progress.ProgressMonitor;
import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.dbjobs.api.DatabaseView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Create an index on a field
 * @author Erwan Bocher
 */
public class CreateIndex extends SwingWorkerPM {

    private TableLocation table;
    private String field;
    private DatabaseView databaseView;
    private DataSource dataSource;
    private static final I18n I18N = I18nFactory.getI18n(CreateIndex.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateIndex.class);


    public CreateIndex(TableLocation table, String field, DatabaseView databaseView, DataSource dataSource) {
        this.table = table;
        this.field = field;
        this.databaseView = databaseView;
        this.dataSource = dataSource;
        setTaskName(I18N.tr("Create index"));
    }
    
    @Override
    protected Object doInBackground() throws Exception {
        try (Connection connection = dataSource.getConnection();
                Statement st = connection.createStatement()) {
            PropertyChangeListener listener = EventHandler.create(PropertyChangeListener.class, st, "cancel");
            getProgressMonitor().addPropertyChangeListener(ProgressMonitor.PROP_CANCEL, listener);
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("CREATE INDEX ON ");
                sb.append(table.toString(JDBCUtilities.isH2DataBase(connection.getMetaData())));
                sb.append("(");
                sb.append(field);
                sb.append(")");
                LOGGER.info(I18N.tr("Create index query:\n{0}", sb.toString()));
                st.execute(sb.toString());
            } finally {
                getProgressMonitor().removePropertyChangeListener(listener);
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }
        return null;
    }

    @Override
    protected void done() {
        databaseView.onDatabaseUpdate(DatabaseView.DB_ENTITY.TABLE.name(), table.toString());
    }
}
