/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 * <p/>
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 * <p/>
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p/>
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 * <p/>
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.orbistoolbox.controller.processexecution.dataprocessing;

import org.h2gis.h2spatialapi.DriverFunction;
import org.orbisgis.corejdbc.H2GISProgressMonitor;
import org.orbisgis.orbistoolbox.controller.processexecution.ExecutionWorker;
import org.orbisgis.orbistoolbox.controller.processexecution.utils.FormatFactory;
import org.orbisgis.orbistoolbox.model.*;
import org.orbisgis.orbistoolbox.view.ToolBox;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Pre and postProcessing of the GeoData.
 *
 * @author Sylvain PALOMINOS
 **/

public class GeoDataProcessing implements ProcessingData{
    private Map<URI, Object> saveMap = new HashMap<>();

    @Override
    public Class<? extends DataDescription> getDataClass() {
        return GeoData.class;
    }

    public void preProcessing(DescriptionType inputOrOutput, ExecutionWorker executionWorker) {
        Map<URI, Object> dataMap = executionWorker.getDataMap();
        ToolBox toolBox = executionWorker.getToolBox();
        //If the descriptionType is an input, try to import the input file in OrbisGIS
        if (inputOrOutput instanceof Input) {
            Input input = (Input)inputOrOutput;
            if (input.getDataDescription() instanceof GeoData) {
                //Get the GeoData
                GeoData geoData = ((GeoData) input.getDataDescription());
                //Find the default format and the sql format
                for (Format format : geoData.getFormats()) {
                    if (format.isDefaultFormat() && !format.getMimeType().equals(FormatFactory.SQL_MIMETYPE)) {
                        //Load the geoFile in OrbisGIS and put in the inputDataMap the table name.
                        File geoFile = new File((String) dataMap.get(input.getIdentifier()));
                        String tableName = null;
                        try {
                            tableName = toolBox.getDataManager().registerDataSource(geoFile.toURI());
                        } catch (SQLException e) {
                            LoggerFactory.getLogger(GeoDataProcessing.class).error(e.getMessage());
                        }
                        dataMap.put(input.getIdentifier(), tableName);
                    }
                }
            }
        }
        //If the descriptionType is an output, save the file path, and get the table name.
        if (inputOrOutput instanceof Output) {
            Output output = (Output) inputOrOutput;
            if (output.getDataDescription() instanceof GeoData) {
                //Save the output geoFile path and replace it in the output by the table name.
                saveMap.put(output.getIdentifier(), dataMap.get(output.getIdentifier()));
                File geoFile = new File((String) dataMap.get(output.getIdentifier()));
                String tableName = geoFile.getName().replaceFirst("[.][^.]+$", "").toUpperCase();
                dataMap.put(output.getIdentifier(), tableName);
            }
        }
    }

    public void postProcessing(DescriptionType inputOrOutput, ExecutionWorker executionWorker){
        Map<URI, Object> dataMap = executionWorker.getDataMap();
        ToolBox toolBox = executionWorker.getToolBox();
        if (inputOrOutput instanceof Output) {
            Output output = (Output) inputOrOutput;
            if (output.getDataDescription() instanceof GeoData) {
                //Get the GeoData
                GeoData geoData = ((GeoData) output.getDataDescription());
                //Find the default format
                for (Format format : geoData.getFormats()) {
                    if (format.isDefaultFormat() && !format.getMimeType().equals(FormatFactory.SQL_MIMETYPE)) {
                        //Thank to the saved path, export the table as a geoFile.
                        URI uri = output.getIdentifier();
                        String extension = saveMap.get(uri).toString().substring(
                                saveMap.get(uri).toString().lastIndexOf('.')+1);
                        DriverFunction export =
                                toolBox.getDriverFunctionContainer().getExportDriverFromExt(
                                extension, DriverFunction.IMPORT_DRIVER_TYPE.COPY);
                        try {
                            export.exportTable(toolBox.getDataManager().getDataSource().getConnection(),
                                    dataMap.get(uri).toString(),
                                    new File(saveMap.get(uri).toString()),
                                    new H2GISProgressMonitor(executionWorker.getProgressMonitor()));
                        } catch (SQLException|IOException e) {
                            LoggerFactory.getLogger(GeoDataProcessing.class).error(e.getMessage());
                        }
                        dataMap.put(uri, saveMap.get(uri));
                    }
                }
            }
        }
    }
}
