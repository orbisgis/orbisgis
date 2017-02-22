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

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.io.FilenameUtils;
import org.h2gis.api.DriverFunction;
import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.corejdbc.H2GISProgressMonitor;
import org.orbisgis.dbjobs.api.DriverFunctionContainer;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.SaveFilePanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Export a table into a local file.
 */
public class ExportInFileOperation extends SwingWorkerPM {

        private static final I18n I18N = I18nFactory.getI18n(ExportInFileOperation.class);
        private static final Logger LOGGER = LoggerFactory.getLogger("gui."+ExportInFileOperation.class);
        private File savedFile;
        private String sourceName;
        private DriverFunction driverFunction;
        private DataSource dataSource;

       /**
        * This class is used to export a source on disk.
        *
        * @param sourceName Table identifier
        * @param savedFile Destination
        * @param driverFunction
        * @param dataSource
        */
        public ExportInFileOperation(String sourceName, File savedFile, DriverFunction driverFunction, DataSource dataSource) {
                this.sourceName = sourceName;
                this.savedFile = savedFile;
                this.driverFunction = driverFunction;
                this.dataSource = dataSource;
                setTaskName(I18N.tr("Save the source in a file."));
        }

    @Override
    protected Object doInBackground() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            driverFunction.exportTable(connection, sourceName , savedFile, new H2GISProgressMonitor(this.getProgressMonitor()));
            LOGGER.info(I18N.tr("The file {0} has been saved.", savedFile.getAbsolutePath()));
        } catch (SQLException | IOException ex) {
            LOGGER.error(I18N.tr("Cannot create the file.\nCause : {0}", ex.getMessage()), ex);
        }
        return null;
    }


    public static ExportInFileOperation saveInfile(DataSource dataSource, List<String> tables, DriverFunctionContainer driverFunctionContainer) {
        for (String source : tables) {
            final SaveFilePanel outfilePanel = new SaveFilePanel(
                    "Geocatalog.SaveInFile",
                    I18N.tr("Save the source : {0}", source));
            for(DriverFunction driverFunction : driverFunctionContainer.getDriverFunctionList()) {
                for(String fileExt : driverFunction.getExportFormats()) {
                    outfilePanel.addFilter(fileExt, driverFunction.getFormatDescription(fileExt));
                }
            }
            outfilePanel.loadState();
            if (UIFactory.showDialog(outfilePanel, true, true)) {
                final File savedFile = outfilePanel.getSelectedFile().getAbsoluteFile();
                return new ExportInFileOperation(source, savedFile,
                        driverFunctionContainer.getExportDriverFromExt(FilenameUtils.getExtension(savedFile.getName()),
                                DriverFunction.IMPORT_DRIVER_TYPE.COPY), dataSource);
            }
        }
        return null;
    }
}
