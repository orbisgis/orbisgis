package org.orbisgis.view.geocatalog.jobs;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.h2gis.h2spatialapi.DriverFunction;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.coreapi.api.DataManager;
import org.orbisgis.coreapi.api.DriverFunctionContainer;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.FileUtils;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.H2GISProgressMonitor;
import org.orbisgis.viewapi.geocatalog.ext.GeoCatalogExt;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * This job load a bunch of files into a DataSource
 * @author Nicolas Fortin
 */
public class ImportFiles implements BackgroundJob {
    private static final I18n I18N = I18nFactory.getI18n(ImportFiles.class);
    private static Logger LOGGER = Logger.getLogger(ImportFiles.class);

    private GeoCatalogExt catalog;
    private List<File> files;
    private DriverFunctionContainer driverFunctionContainer;
    private DataManager dataManager;
    private DriverFunction.IMPORT_DRIVER_TYPE driverType;

    public ImportFiles(GeoCatalogExt catalog, DriverFunctionContainer driverFunctionContainer, List<File> files, DataManager dataManager, DriverFunction.IMPORT_DRIVER_TYPE driverType) {
        this.catalog = catalog;
        this.driverFunctionContainer = driverFunctionContainer;
        this.files = files;
        this.dataManager = dataManager;
        this.driverType = driverType;
    }

    @Override
    public String getTaskName() {
        return I18N.tr("Import file");
    }

    @Override
    public void run(ProgressMonitor pm) {
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            ProgressMonitor filePm = pm.startTask(files.size());
            for(File file : files) {
                String ext = FilenameUtils.getExtension(file.getName());
                DriverFunction driverFunction = driverFunctionContainer.getDriverFromExt(ext, driverType);
                if(driverFunction != null) {
                    TableLocation tableName = new TableLocation("","",dataManager.findUniqueTableName(FileUtils.getNameFromURI(file.toURI())));
                    driverFunction.importFile(connection, tableName.toString() ,file, new H2GISProgressMonitor(filePm));
                } else {
                    LOGGER.error(I18N.tr("No driver found for {0} extension", ext));
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(I18N.tr("Cannot import the file"), ex);
            // Print additional information
            while((ex = ex.getNextException()) != null) {
                LOGGER.error(ex.getLocalizedMessage());
            }

        } catch (IOException ex) {
            LOGGER.error(I18N.tr("Cannot import the file"), ex);
        }
        catalog.refreshSourceList();
    }
}

