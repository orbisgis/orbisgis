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

import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;
import org.orbisgis.orbistoolbox.model.*;
import org.orbisgis.orbistoolbox.model.Process;
import org.orbisgis.orbistoolboxapi.annotations.model.DescriptionTypeAttribute;
import org.orbisgis.orbistoolboxapi.annotations.model.OutputAttribute;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URI;
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

    public void preProcessing(DescriptionType inputOrOutput,
                              Map<URI, Object> inputDataMap,
                              Map<URI, Object> outputDataMap,
                              GroovyShell shell) {
        if (inputOrOutput instanceof Input) {
            Input input = (Input)inputOrOutput;
            if (input.getDataDescription() instanceof GeoData) {
                //Get the GeoData
                GeoData geoData = ((GeoData) input.getDataDescription());
                Format tableFormat = null;
                //Find the default format and the sql format
                for (Format format : geoData.getFormats()) {
                    if(format.getMimeType().equals(GeoData.sqlTableMimeType)){
                        tableFormat = format;
                    }
                    if (format.isDefaultFormat()) {
                        if (format.getMimeType().equals(GeoData.shapeFileMimeType)) {
                            //Load the shape file in OrbisGIS and put in the inputDataMap the table name.
                            File shapeFile = new File((String) inputDataMap.get(input.getIdentifier()));
                            String tableName = shapeFile.getName().replaceFirst("[.][^.]+$", "").toUpperCase();
                            String script = "import groovy.sql.Sql\n" +
                                    "sql = Sql.newInstance(grv_ds)\n" +
                                    "sql.execute(\"DROP TABLE IF EXISTS " + tableName + "\")\n" +
                                    "sql.execute(\"CALL SHPRead('" + shapeFile.getAbsolutePath() + "','" + tableName + "');\")";
                            shell.evaluate(script);
                            inputDataMap.put(input.getIdentifier(), tableName);
                        }
                    }
                }
                tableFormat.setDefaultFormat(true);
            }
        }
        if (inputOrOutput instanceof Output) {
            Output output = (Output) inputOrOutput;
            if (output.getDataDescription() instanceof GeoData) {
                //Save the output shape file path and replace it in the output by the table name.
                saveMap.put(output.getIdentifier(), outputDataMap.get(output.getIdentifier()));
                File shapeFile = new File((String) outputDataMap.get(output.getIdentifier()));
                String tableName = shapeFile.getName().replaceFirst("[.][^.]+$", "").toUpperCase();
                outputDataMap.put(output.getIdentifier(), tableName);
            }
        }
    }

    public void postProcessing(DescriptionType inputOrOutput,
                               Map<URI, Object> inputDataMap,
                               Map<URI, Object> outputDataMap,
                               GroovyShell shell,
                               GroovyObject groovyObject){
        if (inputOrOutput instanceof Output) {
            Output output = (Output) inputOrOutput;
            if (output.getDataDescription() instanceof GeoData) {
                //Get the GeoData
                GeoData geoData = ((GeoData) output.getDataDescription());
                //Find the default format
                for (Format format : geoData.getFormats()) {
                    if (format.isDefaultFormat()) {
                        if (format.getMimeType().equals(GeoData.shapeFileMimeType)) {
                            //Thank to the saved path, export the table as a shapefile.
                            URI uri = output.getIdentifier();
                            String script = "import groovy.sql.Sql\n" +
                                    "sql = Sql.newInstance(grv_ds)\n" +
                                    "sql.execute(\"CALL SHPWrite('" + saveMap.get(uri) + "','" + outputDataMap.get(uri) + "');\")";
                            shell.evaluate(script);
                            outputDataMap.put(uri, saveMap.get(uri));
                        }
                    }
                }
            }
        }
    }
}
