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

import java.io.File;
import javax.swing.JButton;
import org.gdms.data.SourceAlreadyExistsException;
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
import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.OrbisConfiguration;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;
import org.orbisgis.core.ui.plugins.views.mapEditor.MapEditorPlugIn;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.core.ui.wizards.OpenGdmsFilePanel;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.FileUtils;
import org.orbisgis.utils.I18N;

public class AddFilePlugIn extends AbstractPlugIn {

        private JButton btn;

        public AddFilePlugIn() {
                btn = new JButton(OrbisGISIcon.GEOCATALOG_FILE);
                btn.setToolTipText(I18N.getString("orbisgis.org.orbisgis.ui.addFilePlugIn"));
        }

        @Override
        public void initialize(PlugInContext context) throws Exception {
                WorkbenchContext wbcontext = context.getWorkbenchContext();
                wbcontext.getWorkbench().getFrame().getMainToolBar().addPlugIn(
                        this, btn, context);
        }

        @Override
        public boolean execute(PlugInContext context) throws Exception {
                BackgroundManager bm = Services.getService(BackgroundManager.class);
                bm.backgroundOperation(new AddFileJob());
                return true;
        }

        @Override
        public boolean isEnabled() {
                btn.setEnabled(true);
                return true;
        }

        /**
         * A background job to add several files in the TOC
         * without freezing the renderer
         */
        private final class AddFileJob implements BackgroundJob {

                @Override
                public void run(ProgressMonitor pm) {
                        IEditor editor = Services.getService(EditorManager.class).getActiveEditor();
                        MapContext mc = null;
                        if (editor != null && editor instanceof MapEditorPlugIn) {
                                mc = (MapContext) editor.getElement().getObject();
                        }
                        OpenGdmsFilePanel filePanel = new OpenGdmsFilePanel(I18N.getString("orbisgis.org.core.selectFileAdd"));
                        if (UIFactory.showDialog(
                                new UIPanel[]{filePanel
                                })) {
                                // We can retrieve the files that have been selected by the user
                                File[] files = filePanel.getSelectedFiles();
                                int count = files.length;

                                for (int i = 0; i < count; i++) {
                                        File file = files[i];
                                        if (i / 100 == i / 100.0) {
                                                if (pm.isCancelled()) {
                                                        break;
                                                } else {
                                                        pm.progressTo(100 * i / count);
                                                }
                                        }
                                        // For each file, we ensure that we have a driver
                                        // that can be used to read it. If we don't, we don't
                                        // open the file.
                                        if (OrbisConfiguration.isFileEligible(file)) {
                                                DataManager dm = (DataManager) Services.getService(DataManager.class);
                                                SourceManager sourceManager = dm.getSourceManager();
                                                try {
                                                        String name = sourceManager.getUniqueName(FileUtils.getFileNameWithoutExtensionU(file));
                                                        sourceManager.register(name, file);
                                                        if (mc != null) {
                                                                mc.getLayerModel().addLayer(dm.createLayer(name));
                                                        }
                                                } catch (LayerException e) {
                                                        Services.getErrorManager().warning(I18N.getString("orbisgis.org.orbisgis.core.model.source.file.noSpatial") + "n/"
                                                                + I18N.getString("orbisgis.org.orbisgis.core.geocatalog.sourceAdded"));

                                                } catch (SourceAlreadyExistsException e) {
                                                        ErrorMessages.error(ErrorMessages.SourceAlreadyRegistered
                                                                + ": ", e);
                                                }
                                        }
                                }
                        }


                }

                @Override
                public String getTaskName() {
                        return I18N.getString("orbisgis.org.orbisgis.ui.addFilePlugIn.addingFile");
                }
        }
}
