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

import org.apache.commons.io.FilenameUtils;
import org.h2gis.api.DriverFunction;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.commons.progress.ProgressMonitor;
import org.orbisgis.commons.utils.FileUtils;
import org.orbisgis.corejdbc.H2GISProgressMonitor;
import org.orbisgis.dbjobs.api.DatabaseView;
import org.orbisgis.dbjobs.api.DriverFunctionContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * This job load a bunch of files into a DataSource
 * @author Nicolas Fortin
 */
public class ImportFiles extends SwingWorkerPM {
    private static final I18n I18N = I18nFactory.getI18n(ImportFiles.class);
    private static Logger LOGGER = LoggerFactory.getLogger(ImportFiles.class);

    private DatabaseView dbView;
    private List<File> files;
    private DriverFunctionContainer driverFunctionContainer;
    private DataManager dataManager;
    private DriverFunction.IMPORT_DRIVER_TYPE driverType;
    private String schema;

    /**
     * Import file into database into the default schema
     * @param dbView GUI to refresh when the operation is done
     * @param driverFunctionContainer Container of file drivers.
     * @param files Files to import
     * @param dataManager DataManager that hold datasource
     * @param driverType Type of import
     */
    public ImportFiles(DatabaseView dbView, DriverFunctionContainer driverFunctionContainer, List<File> files, DataManager dataManager, DriverFunction.IMPORT_DRIVER_TYPE driverType) {
        this.dbView = dbView;
        this.driverFunctionContainer = driverFunctionContainer;
        this.files = files;
        this.dataManager = dataManager;
        this.driverType = driverType;
        setTaskName(I18N.tr("Import file"));
        schema = "";
    }

    /**
     * Import file into database into the specified schema
     * @param dbView GUI to refresh when the operation is done
     * @param driverFunctionContainer Container of file drivers.
     * @param files Files to import
     * @param dataManager DataManager that hold datasource
     * @param driverType Type of import
     * @param schema Schema name where to create tables
     */
    public ImportFiles(DatabaseView dbView, DriverFunctionContainer driverFunctionContainer, List<File> files, DataManager dataManager, DriverFunction.IMPORT_DRIVER_TYPE driverType, String schema) {
        this.dbView = dbView;
        this.driverFunctionContainer = driverFunctionContainer;
        this.files = files;
        this.dataManager = dataManager;
        this.driverType = driverType;
        this.schema = schema;
        setTaskName(I18N.tr("Import file"));
    }

    @Override
    protected Object doInBackground() throws Exception {
        long deb = System.currentTimeMillis();
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            ProgressMonitor filePm = this.getProgressMonitor().startTask(files.size());
            boolean isH2 = JDBCUtilities.isH2DataBase(connection.getMetaData());
            for(File file : files) {
                String ext = FilenameUtils.getExtension(file.getName());
                DriverFunction driverFunction = driverFunctionContainer.getImportDriverFromExt(ext, driverType);
                if(driverFunction != null) {
                    String tableNameTest = TableLocation.capsIdentifier(FileUtils.getNameFromURI(file.toURI()), isH2);
                    if(tableNameTest == null) {
                        tableNameTest = FileUtils.getNameFromURI(file.toURI());
                    }
                    String tableName = dataManager.findUniqueTableName(new TableLocation("", schema ,tableNameTest)
                            .toString(isH2));
                    driverFunction.importFile(connection, tableName ,file, new H2GISProgressMonitor(filePm));
                } else {
                    LOGGER.error(I18N.tr("No driver found for {0} extension", ext));
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(I18N.tr("Cannot import the file.\nCause : {0}", ex.getMessage()), ex);
            // Print additional information
            while((ex = ex.getNextException()) != null) {
                LOGGER.error(ex.getLocalizedMessage());
            }
        } catch (IOException ex) {
            LOGGER.error(I18N.tr("Cannot import the file.\nCause : {0}", ex.getMessage()), ex);
        }
        LOGGER.info(I18N.tr("Importation done in {0} sec", (System.currentTimeMillis() - deb) / 1000d));
        dbView.onDatabaseUpdate(DatabaseView.DB_ENTITY.SCHEMA.name(), "PUBLIC");
        return null;
    }

    @Override
    public String toString() {
        return super.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}

