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

import java.io.File;
import javax.swing.JButton;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;
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
import org.orbisgis.core.ui.pluginSystem.workbench.OrbisConfiguration;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;
import org.orbisgis.core.ui.plugins.views.mapEditor.MapEditorPlugIn;
import org.orbisgis.core.ui.preferences.lookandfeel.images.IconLoader;
import org.orbisgis.core.ui.wizards.OpenGdmsFolderPanel;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.I18N;

public class AddFromFolderPlugIn extends AbstractPlugIn {

        private JButton btn;

        public AddFromFolderPlugIn() {
                btn = new JButton(IconLoader.getIcon("folder_add.png"));
                btn.setToolTipText(I18N.getString("orbisgis.org.orbisgis.ui.addFromFolderPlugIn"));
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
                bm.backgroundOperation(new AddFromFolderJob());
                return true;
        }

        @Override
        public boolean isEnabled() {
                btn.setEnabled(true);
                return true;
        }

        /**
         * A background job to add some datasources from a folder in the TOC
         * without freezing the renderer
         */
        private final class AddFromFolderJob implements BackgroundJob {

                private MapContext mc = null;

                @Override
                public void run(ProgressMonitor pm) {
                        IEditor editor = Services.getService(EditorManager.class).getActiveEditor();
                        if (editor != null && editor instanceof MapEditorPlugIn) {
                                mc = (MapContext) editor.getElement().getObject();
                        }
                        final OpenGdmsFolderPanel folderPanel = new OpenGdmsFolderPanel(I18N.getString("orbisgis.org.core.folderAdd"));
                        if (UIFactory.showDialog(new UIPanel[]{folderPanel})) {

                                File[] files = folderPanel.getSelectedFiles();
                                for (final File file : files) {
                                        // for each folder, we apply the method processFolder.
                                        // We use the filter selected by the user in the panel
                                        // to succeed in this operation.
                                        BackgroundManager bm = Services.getService(BackgroundManager.class);
                                        bm.backgroundOperation(new BackgroundJob() {

                                                @Override
                                                public String getTaskName() {
                                                        return I18N.getString("orbisgis.org.core.addFromFolder");
                                                }

                                                @Override
                                                public void run(ProgressMonitor pm) {
                                                        processFolder(file, folderPanel.getSelectedFilter(), mc, pm);
                                                }
                                        });

                                }
                        }


                }

                @Override
                public String getTaskName() {
                        return I18N.getString("orbisgis.org.orbisgis.ui.addFromFolderPlugIn.addingFile");
                }
        }

        /**
         * the method that actually process the content of a directory, or a file.
         * If the file is acceptable by the FileFilter, it is processed
         *
         * @param file
         * @param pm
         */
        private void processFolder(File file, FileFilter filter, MapContext mc, ProgressMonitor pm) {
                if (file.isDirectory()) {
                        pm.startTask(file.getName(), 100);
                        for (File content : file.listFiles()) {
                                if (pm.isCancelled()) {
                                        break;
                                }
                                processFolder(content, filter, mc, pm);
                        }
                        pm.endTask();
                } else {
                        if (filter.accept(file) && OrbisConfiguration.isFileEligible(file)) {
                                DataManager dm = (DataManager) Services.getService(DataManager.class);
                                SourceManager sourceManager = dm.getSourceManager();
                                try {
                                        String name = sourceManager.getUniqueName(FilenameUtils.removeExtension(file.getName()));
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
