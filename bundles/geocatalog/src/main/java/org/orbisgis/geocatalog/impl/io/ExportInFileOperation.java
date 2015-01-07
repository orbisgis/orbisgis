/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.geocatalog.impl.io;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;
import org.h2gis.h2spatialapi.DriverFunction;
import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.corejdbc.H2GISProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Export a table into a local file.
 */
public class ExportInFileOperation extends SwingWorkerPM {

        private static final I18n I18N = I18nFactory.getI18n(ExportInFileOperation.class);
        private static final Logger LOGGER = LoggerFactory.getLogger(ExportInFileOperation.class);
        private File savedFile;
        private String sourceName;
        private DriverFunction driverFunction;
        private DataSource dataSource;

        /**
         * This class is used to export a source on disk.
         *
         * @param sourceName Table identifier
         * @param savedFile Destination
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
            driverFunction.exportTable(connection, sourceName , savedFile, new H2GISProgressMonitor(this));
        } catch (SQLException | IOException ex) {
            LOGGER.error(I18N.tr("Cannot create the file"), ex);
        }
        return null;
    }
}
