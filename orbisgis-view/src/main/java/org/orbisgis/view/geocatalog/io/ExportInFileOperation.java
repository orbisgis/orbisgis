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
package org.orbisgis.view.geocatalog.io;

import java.io.File;

import javax.swing.JOptionPane;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.gdms.data.DataSourceCreationException;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;
import org.orbisgis.progress.ProgressMonitor;
import org.xnap.commons.i18n.I18n;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.geocatalog.Catalog;
import org.xnap.commons.i18n.I18nFactory;

public class ExportInFileOperation implements BackgroundJob {

        private static final I18n I18N = I18nFactory.getI18n(ExportInFileOperation.class);
        private static final Logger LOGGER = Logger.getLogger(ExportInFileOperation.class);
        private File savedFile;
        private DataSourceFactory dsf;
        private String sourceName;
        private Catalog catalog;

        /**
         * This class is used to export a source on disk.
         *
         * @param dsf
         * @param sourceName
         * @param savedFile
         * @param frame
         */
        public ExportInFileOperation(DataSourceFactory dsf, String sourceName,
                File savedFile, Catalog frame) {
                this.sourceName = sourceName;
                this.savedFile = savedFile;
                this.dsf = dsf;
                this.catalog = frame;
        }

        @Override
        public String getTaskName() {
                return I18N.tr("Save the source in a file.");
        }

        @Override
        public void run(ProgressMonitor pm) {   
                String fileName = FilenameUtils.removeExtension(savedFile.getName());
                
                final FileSourceDefinition def = new FileSourceDefinition(savedFile, DriverManager.DEFAULT_SINGLE_TABLE_NAME);
                final SourceManager sourceManager = dsf.getSourceManager();
                if (sourceManager.exists(fileName)) {
                        fileName = sourceManager.getUniqueName(fileName);
                }
                sourceManager.register(fileName, def);
                try {  
                        dsf.saveContents(fileName, dsf.getDataSource(sourceName), pm);
                        JOptionPane.showMessageDialog(catalog,
                                I18N.tr("The datasource has been exported and added to the geocatalog with the name " + fileName));
                } catch (NoSuchTableException e) {
                        LOGGER.error(I18N.tr("Cannot create the file"), e);
                } catch (DataSourceCreationException e) {
                        LOGGER.error(I18N.tr("Cannot create the file"), e);
                } catch (DriverException e) {
                        LOGGER.error(I18N.tr("Cannot create the file"), e);
                } catch (DriverLoadException e) {
                        LOGGER.error(I18N.tr("Cannot read the source"), e);
                }
        }
}
