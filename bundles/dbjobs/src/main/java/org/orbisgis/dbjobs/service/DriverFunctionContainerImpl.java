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
package org.orbisgis.dbjobs.service;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.h2gis.api.DriverFunction;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.dbjobs.api.DatabaseView;
import org.orbisgis.dbjobs.api.DriverFunctionContainer;
import org.orbisgis.dbjobs.jobs.ImportFiles;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.OpenFilePanel;
import org.orbisgis.sif.components.OpenFolderPanel;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Manage driver functions.
 * @author Nicolas Fortin
 */
@Component
public class DriverFunctionContainerImpl implements DriverFunctionContainer {
    private static final I18n I18N = I18nFactory.getI18n(DriverFunctionContainerImpl.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(DriverFunctionContainerImpl.class);
    private List<DriverFunction> fileDrivers = new LinkedList<>();
    private ExecutorService executorService = null;
    private DataManager dataManager;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Reference
    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public void unsetDataManager(DataManager dataManager) {
        this.dataManager = null;
    }

    public void unsetExecutorService(ExecutorService executorService) {
        this.executorService = null;
    }

    @Override
    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, policyOption =
            ReferencePolicyOption.GREEDY)
    public void addDriverFunction(DriverFunction driverFunction) {
        fileDrivers.add(driverFunction);
    }

    @Override
    public void removeDriverFunction(DriverFunction driverFunction) {
        fileDrivers.remove(driverFunction);
    }

    @Override
    public List<DriverFunction> getDriverFunctionList() {
        return Collections.unmodifiableList(fileDrivers);
    }

    @Override
    public DriverFunction getImportDriverFromExt(String ext,DriverFunction.IMPORT_DRIVER_TYPE type ) {
        for(DriverFunction driverFunction : fileDrivers) {
            if(driverFunction.getImportDriverType() == type) {
                for(String fileExt : driverFunction.getImportFormats()) {
                    if(fileExt.equalsIgnoreCase(ext)) {
                        return driverFunction;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public DriverFunction getExportDriverFromExt(String ext,DriverFunction.IMPORT_DRIVER_TYPE type ) {
        for(DriverFunction driverFunction : fileDrivers) {
            if(driverFunction.getImportDriverType() == type) {
                for(String fileExt : driverFunction.getExportFormats()) {
                    if(fileExt.equalsIgnoreCase(ext)) {
                        return driverFunction;
                    }
                }
            }
        }
        return null;
    }

    /**
     * The user can load several files from a folder
     * @param type Driver type
     */
    @Override
    public void addFilesFromFolder(DatabaseView dbView, DriverFunction.IMPORT_DRIVER_TYPE type, String schema) {
        String message;
        if(type == DriverFunction.IMPORT_DRIVER_TYPE.COPY) {
            message = I18N.tr("Select the folder to import");
        } else {
            message = I18N.tr("Select the folder to open");
        }

        OpenFolderPanel folderSourcePanel = new OpenFolderPanel("Geocatalog.LinkFolder" ,message);
        for(DriverFunction driverFunction : fileDrivers) {
            try {
                if(driverFunction.getImportDriverType() == type) {
                    for(String fileExt : driverFunction.getImportFormats()) {
                        folderSourcePanel.addFilter(fileExt, driverFunction.getFormatDescription(fileExt));
                    }
                }
            } catch (Exception ex) {
                LOGGER.debug(ex.getLocalizedMessage(), ex);
            }
        }
        folderSourcePanel.loadState();
        if (UIFactory.showDialog(folderSourcePanel, true, true)) {
            File directory = folderSourcePanel.getSelectedFile();
            Collection files = org.apache.commons.io.FileUtils.listFiles(directory,
                    new ImportFileFilter(folderSourcePanel.getSelectedFilter()), DirectoryFileFilter.DIRECTORY);
            List<File> fileToLoad = new ArrayList<>(files.size());
            for (Object file : files) {
                if(file instanceof File) {
                    fileToLoad.add((File)file);
                }
            }
            // for each folder, we apply the method processFolder.
            // We use the filter selected by the user in the panel
            // to succeed in this operation.
            executeJob(new ImportFiles(dbView, this, fileToLoad, dataManager, type, schema));
        }
    }


    private void executeJob(SwingWorker worker) {
        if(executorService == null) {
            worker.execute();
        } else {
            executorService.execute(worker);
        }
    }

    @Override
    public void importFile(DatabaseView dbView, DriverFunction.IMPORT_DRIVER_TYPE type, String schema) {
        String panelMessage;
        if(type == DriverFunction.IMPORT_DRIVER_TYPE.COPY) {
            panelMessage = I18N.tr("Select the file to import");
        } else {
            panelMessage = I18N.tr("Select the file to open");
        }
        OpenFilePanel linkSourcePanel = new OpenFilePanel("Geocatalog.LinkFile" ,panelMessage);
        for(DriverFunction driverFunction : fileDrivers) {
            try {
                if(driverFunction.getImportDriverType() == type) {
                    for(String fileExt : driverFunction.getImportFormats()) {
                        linkSourcePanel.addFilter(fileExt, driverFunction.getFormatDescription(fileExt));
                    }
                }
            } catch (Exception ex) {
                LOGGER.debug(ex.getLocalizedMessage(), ex);
            }
        }
        linkSourcePanel.loadState();
        //Ask SIF to open the dialog
        if (UIFactory.showDialog(linkSourcePanel, true, true)) {
            // We can retrieve the files that have been selected by the user
            List<File> files = Arrays.asList(linkSourcePanel.getSelectedFiles());
            executeJob(new ImportFiles(dbView, this, files, dataManager, type, schema));
        }
    }

    @Override
    public void addFilesFromFolder(DatabaseView dbView, DriverFunction.IMPORT_DRIVER_TYPE type) {
        addFilesFromFolder(dbView, type, null);
    }

    @Override
    public void importFile(DatabaseView dbView, DriverFunction.IMPORT_DRIVER_TYPE type) {
        importFile(dbView, type, null);
    }

    private static class ImportFileFilter implements IOFileFilter {
        private FileFilter fileFilter;

        private ImportFileFilter(FileFilter fileFilter) {
            this.fileFilter = fileFilter;
        }

        @Override
        public boolean accept(File file) {
            return fileFilter.accept(file);
        }

        @Override
        public boolean accept(File dir, String name) {
            return accept(new File(dir, name));
        }
    }
}
