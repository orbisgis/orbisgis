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
package org.orbisgis.core.ui.plugins.editors.mapEditor;

import javax.swing.JButton;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.source.SourceManager;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.editor.IEditor;

import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;
import org.orbisgis.core.ui.plugins.views.geocatalog.newSourceWizards.db.ConnectionPanel;
import org.orbisgis.core.ui.plugins.views.geocatalog.newSourceWizards.db.TableSelectionPanel;
import org.orbisgis.core.ui.plugins.views.mapEditor.MapEditorPlugIn;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.I18N;

public class AddDataBasePlugIn extends AbstractPlugIn {

        private JButton btn;

        public AddDataBasePlugIn() {
                btn = new JButton(OrbisGISIcon.GEOCATALOG_DB);
                btn.setToolTipText(I18N.getString("orbisgis.org.orbisgis.ui.addDataBasePlugIn"));
        }

        public void initialize(PlugInContext context) throws Exception {
                WorkbenchContext wbcontext = context.getWorkbenchContext();
                wbcontext.getWorkbench().getFrame().getMainToolBar().addPlugIn(
                        this, btn, context);
        }

        @Override
        public boolean execute(PlugInContext context) throws Exception {
                BackgroundManager bm = Services.getService(BackgroundManager.class);
                bm.backgroundOperation(new AddDataBaseJob());
                return true;
        }

        @Override
        public boolean isEnabled() {
                btn.setEnabled(true);
                return true;
        }

        /**
         * A background job to add some tables from a database in the TOC
         * without freezing the renderer
         */
        private final class AddDataBaseJob implements BackgroundJob {

                @Override
                public void run(ProgressMonitor pm) {
                        IEditor editor = Services.getService(EditorManager.class).getActiveEditor();
                        MapContext mc = null;
                        if (editor != null && editor instanceof MapEditorPlugIn) {
                                mc = (MapContext) editor.getElement().getObject();
                        }
                        final ConnectionPanel firstPanel = new ConnectionPanel();
                        final TableSelectionPanel secondPanel = new TableSelectionPanel(
                                firstPanel);
                        if (UIFactory.showDialog(new UIPanel[]{firstPanel, secondPanel})) {
                                for (DBSource dBSource : secondPanel.getSelectedDBSources()) {
                                        DataManager dm = (DataManager) Services.getService(DataManager.class);
                                        SourceManager sm = dm.getSourceManager();
                                        String name = sm.getUniqueName(dBSource.getTableName().toString());
                                        sm.register(name, new DBTableSourceDefinition(dBSource));
                                        try {
                                                if (mc != null) {
                                                        mc.getLayerModel().addLayer(dm.createLayer(name));
                                                }
                                        } catch (LayerException e) {
                                                Services.getErrorManager().warning(I18N.getString("orbisgis.org.orbisgis.core.model.source.table.noSpatial") + "n/"
                                                        + I18N.getString("orbisgis.org.orbisgis.core.geocatalog.sourceAdded"));

                                        }
                                }
                        }


                }

                @Override
                public String getTaskName() {
                        return I18N.getString("orbisgis.org.orbisgis.ui.addDataBasePlugIn.addingTable");
                }
        }
}
