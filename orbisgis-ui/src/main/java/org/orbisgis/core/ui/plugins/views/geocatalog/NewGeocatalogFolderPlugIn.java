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
 * info_at_orbisgis.org
 */
package org.orbisgis.core.ui.plugins.views.geocatalog;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.source.SourceManager;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.OrbisConfiguration;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.preferences.lookandfeel.images.IconLoader;
import org.orbisgis.core.ui.wizards.OpenGdmsFolderPanel;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.FileUtils;
import org.orbisgis.utils.I18N;

/**
 * This plugin is used when the user wants to open the files contained in a
 * folder using the geocatalog. It will open a panel dedicated to the selection
 * of the wanted folders. This panel will then return the selected folders to
 * this PlugIn
 * 
 * @author alexis, jean-yves
 */
public class NewGeocatalogFolderPlugIn extends AbstractPlugIn {

        /**
         * The method responsible of the execution of this plugin. It retrieves the
         * folder selected by the user and analyze their content to add the eligible
         * files to the geocatalog.
         *
         * @param context
         * @return
         * @throws Exception
         */
        @Override
        public boolean execute(PlugInContext context) throws Exception {
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
                                                processFolder(file, folderPanel.getSelectedFilter(), pm);
                                        }
                                });

                        }
                }
                return true;
        }

        /**
         * Plugin initialization.
         *
         * @param context
         * @throws Exception
         */
        @Override
        public void initialize(PlugInContext context) throws Exception {
                WorkbenchContext wbContext = context.getWorkbenchContext();
                WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getGeocatalog();
                context.getFeatureInstaller().addPopupMenuItem(
                        frame,
                        this,
                        new String[]{Names.POPUP_GEOCATALOG_ADD,
                                Names.POPUP_GEOCATALOG_FOLDER},
                        Names.POPUP_GEOCATALOG_ADD, false,
                        IconLoader.getIcon("folder_add.png"), wbContext);

        }

        /**
         * Used directly in the geocatalog, always enabled.
         *
         * @return
         */
        @Override
        public boolean isEnabled() {
                return true;
        }

        /**
         * the method that actually process the content of a directory, or a file.
         * If the file is acceptable by the FileFilter, it is processed
         *
         * @param file
         * @param pm
         */
        private void processFolder(File file, FileFilter filter, ProgressMonitor pm) {
                if (file.isDirectory()) {
                        pm.startTask(file.getName(), 100);
                        for (File content : file.listFiles()) {
                                if (pm.isCancelled()) {
                                        break;
                                }
                                processFolder(content, filter, pm);
                        }
                        pm.endTask();
                } else {
                        if (filter.accept(file) && OrbisConfiguration.isFileEligible(file)) {
                                DataManager dm = (DataManager) Services.getService(DataManager.class);
                                SourceManager sourceManager = dm.getSourceManager();
                                try {
                                        String name = sourceManager.getUniqueName(FileUtils.getFileNameWithoutExtensionU(file));
                                        sourceManager.register(name, file);
                                } catch (SourceAlreadyExistsException e) {
                                        ErrorMessages.error(ErrorMessages.SourceAlreadyRegistered
                                                + ": ", e);
                                }
                        }
                }
        }
}
