/**
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
package org.orbisgis.view.sqlconsole.actions;

import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.driver.DriverException;
import org.gdms.sql.engine.Engine;
import org.gdms.sql.engine.ParseException;
import org.gdms.sql.engine.SQLStatement;
import org.gdms.sql.engine.SemanticException;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.sqlconsole.ui.SQLConsolePanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Execute SQL script in a background process
 */
public class ExecuteScriptProcess implements BackgroundJob {

        private String script;
        private static final Logger LOGGER = Logger.getLogger("gui."+ExecuteScriptProcess.class);
        protected final static I18n I18N = I18nFactory.getI18n(ExecuteScriptProcess.class);
                
        private SQLConsolePanel panel;
        private MapContext vc;

        public ExecuteScriptProcess(String script,MapContext mapContext) {
                this(script,null,mapContext);
        }

        public ExecuteScriptProcess(String script, SQLConsolePanel panel, MapContext mapContext) {
                this.script = script;
                this.vc = mapContext;
                this.panel = panel;
        }

        @Override
        public String getTaskName() {
                return I18N.tr("Executing script");
        }
        
        private void showPanelMessage(final String message) {
                if (panel != null) {
                        SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                        panel.setStatusMessage(message);
                                }
                        });
                }
        }

        @Override
        public void run(ProgressMonitor pm) {

                DataManager dataManager = Services.getService(DataManager.class);
                DataSourceFactory dsf = dataManager.getDataSourceFactory();
                SQLStatement[] statements;

                long t1 = System.currentTimeMillis();
                try {
                        try {
                                statements = Engine.parseScript(script, dsf.getProperties()).getStatements();
                        } catch (ParseException e) {
                                LOGGER.error(I18N.tr("Cannot parse script"), e);
                                showPanelMessage(I18N.tr("Failed to parse the script."));
                                return;
                        }

                        for (int i = 0; i < statements.length; i++) {

                                SQLStatement st = statements[i];
                                boolean spatial;
                                LOGGER.info(I18N.tr("Running instruction {0}/{1} :",(i + 1),statements.length));
                                LOGGER.info(st.getSQL());
                                try {
                                        st.setDataSourceFactory(dsf);
                                        st.setProgressMonitor(pm);
                                        st.prepare();
                                        Metadata metadata = st.getResultMetadata();
                                        if (metadata != null) {
                                                spatial = MetadataUtilities.isSpatial(metadata);

                                                DataSource ds = dsf.getDataSource(st,
                                                        DataSourceFactory.DEFAULT, pm);
                                                if (pm.isCancelled()) {
                                                        break;
                                                }

                                                if (spatial && vc != null) {
                                                        //SQL request is a select with geometries
                                                        //A new layer will be created and shown into the MapEditor
                                                        try {
                                                                final ILayer layer = dataManager.createLayer(ds);

                                                                vc.getLayerModel().insertLayer(layer, 0);

                                                        } catch (LayerException e) {
                                                                LOGGER.error(
                                                                        I18N.tr("Impossible to create the layer:{0}",ds.getName()), e);
                                                                break;
                                                        }
                                                } else {
                                                        //The select return only non-geometrical data
                                                        //the result is shown in the GUI console
                                                        ds.open();
                                                        StringBuilder aux = new StringBuilder();
                                                        int fc = ds.getMetadata().getFieldCount();
                                                        int rc = (int) ds.getRowCount();

                                                        for (int j = 0; j < fc; j++) {
                                                                aux.append(ds.getFieldName(j));
                                                                aux.append("\t");
                                                        }
                                                        aux.append("\n");
                                                        for (int row = 0; row < rc; row++) {
                                                                for (int j = 0; j < fc; j++) {
                                                                        aux.append(ds.getFieldValue(row, j).toString());
                                                                        aux.append("\t");
                                                                }
                                                                aux.append("\n");
                                                                if (row > 100) {
                                                                        aux.append("and more... total ");
                                                                        aux.append(rc);
                                                                        aux.append(" rows\n");
                                                                        break;
                                                                }
                                                        }
                                                        ds.close();
                                                        LOGGER.info(aux.toString());
                                                }
                                        } else {
                                                try {
                                                        st.execute();
                                                } finally {
                                                        st.cleanUp();
                                                }
                                                if (pm.isCancelled()) {
                                                        break;
                                                }

                                        }
                                } catch (DataSourceCreationException e) {
                                        LOGGER.error(
                                                I18N.tr("Cannot create the DataSource:{0}"
                                                , st.getSQL()), e);
                                        break;
                                }

                                // DO NOT REMOVE
                                // lets the GC remove a statement while the following ones are executed,
                                // since we have no use for it now.
                                statements[i] = null;

                                pm.progressTo(100 * i / statements.length);
                        }

                } catch (DriverException e) {
                        LOGGER.error(I18N.tr("Data access error:"), e);
                } catch (SemanticException e) {
                        LOGGER.error(I18N.tr("SQL Semantic Error"), e);
                }

                long t2 = System.currentTimeMillis();
                double lastExecTime = ((t2 - t1) / 1000.0);
                String message = I18N.tr("Execution time: {0}",lastExecTime);
                LOGGER.debug(message);
                showPanelMessage(message);
        }
}
