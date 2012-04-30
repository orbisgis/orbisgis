/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.core.ui.plugins.views.sqlConsole.actions;

import org.apache.log4j.Logger;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editors.map.MapContextManager;
import org.orbisgis.core.ui.plugins.views.output.OutputManager;
import org.orbisgis.core.ui.plugins.views.sqlConsole.ui.SQLConsolePanel;
import org.orbisgis.progress.ProgressMonitor;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.driver.DriverException;
import org.gdms.sql.engine.Engine;
import org.gdms.sql.engine.ParseException;
import org.gdms.sql.engine.SQLStatement;

public class ExecuteScriptProcess implements BackgroundJob {

        private String script;
        private static final Logger logger = Logger.getLogger(ExecuteScriptProcess.class);
        private SQLConsolePanel panel;

        public ExecuteScriptProcess(String script) {
                this.script = script;
        }

        public ExecuteScriptProcess(String script, SQLConsolePanel panel) {
                this.script = script;
                this.panel = panel;
        }

        @Override
        public String getTaskName() {
                return "Executing script";
        }

        @Override
        public void run(ProgressMonitor pm) {

                DataManager dataManager = (DataManager) Services.getService(DataManager.class);
                DataSourceFactory dsf = dataManager.getDataSourceFactory();
                SQLStatement[] statements = null;

                long t1 = System.currentTimeMillis();
                try {
                        logger.debug("Preparing script: " + script);
                        try {
                                statements = Engine.parse(script, dsf.getProperties());
                        } catch (ParseException e) {
                                Services.getErrorManager().error("Cannot parse script", e);
                                if (panel != null) {
                                        panel.setStatusMessage("Failed to parse the script.");
                                }
                                return;
                        }

                        MapContext vc = ((MapContextManager) Services.getService(MapContextManager.class)).getActiveMapContext();

                        for (int i = 0; i < statements.length; i++) {

                                SQLStatement st = statements[i];
                                boolean spatial;
                                logger.info("Running instruction " + (i + 1) + " / " + statements.length + ".");
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

                                                        try {
                                                                final ILayer layer = dataManager.createLayer(ds);

                                                                vc.getLayerModel().insertLayer(layer, 0);

                                                        } catch (LayerException e) {
                                                                Services.getErrorManager().error(
                                                                        "Impossible to create the layer:"
                                                                        + ds.getName(), e);
                                                                break;
                                                        }
                                                } else {
                                                        OutputManager om = Services.getService(OutputManager.class);

                                                        ds.open();
                                                        StringBuilder aux = new StringBuilder();
                                                        int fc = ds.getMetadata().getFieldCount();
                                                        int rc = (int) ds.getRowCount();

                                                        for (int j = 0; j < fc; j++) {
                                                                om.print(ds.getFieldName(j));
                                                                om.print("\t");
                                                        }
                                                        om.println("");
                                                        for (int row = 0; row < rc; row++) {
                                                                for (int j = 0; j < fc; j++) {
                                                                        om.print(ds.getFieldValue(row, j).toString());
                                                                        om.print("\t");
                                                                }
                                                                om.println("");
                                                                if (row > 100) {
                                                                        om.println("and more... total " + rc
                                                                                + " rows");
                                                                        break;
                                                                }
                                                        }
                                                        ds.close();


                                                        om.println(aux.toString());
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
                                        Services.getErrorManager().error(
                                                "Cannot create the DataSource:"
                                                + st.getSQL(), e);
                                        break;
                                }

                                // DO NOT REMOVE
                                // lets the GC remove a statement while the following ones are executed,
                                // since we have no use for it now.
                                statements[i] = null;

                                pm.progressTo(100 * i / statements.length);
                        }

                } catch (DriverException e) {
                        Services.getErrorManager().error("Data access error:", e);
                }

                long t2 = System.currentTimeMillis();
                double lastExecTime = ((t2 - t1) / 1000.0);
                logger.debug("Execution time: " + lastExecTime);
                if (panel != null) {
                        panel.setStatusMessage("Execution time: " + lastExecTime);
                }
        }
}
