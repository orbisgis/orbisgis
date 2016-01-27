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

package org.orbisgis.orbistoolbox.controller.parser;

import org.orbisgis.orbistoolbox.controller.processexecution.utils.FormatFactory;
import org.orbisgis.orbistoolbox.model.*;
import org.orbisgis.orbistoolbox.view.ToolBox;
import org.orbisgis.orbistoolboxapi.annotations.model.DataStoreAttribute;
import org.orbisgis.orbistoolboxapi.annotations.model.DescriptionTypeAttribute;
import org.orbisgis.orbistoolboxapi.annotations.model.InputAttribute;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser for the groovy DataStore annotations.
 *
 * @author Sylvain PALOMINOS
 **/

public class DataStoreParser implements Parser{

    @Override
    public Input parseInput(Field f, Object defaultValue, String processId) {
        //Instantiate the DataStore and its formats
        DataStoreAttribute dataStoreAttribute = f.getAnnotation(DataStoreAttribute.class);
        List<Format> formatList;
        List<String> importableFormat;
        boolean isFile = false;
        boolean isGeocatalog = false;
        boolean isDataBase = false;

        if(dataStoreAttribute.isSpatial()){
            importableFormat = new ArrayList<>(ToolBox.getImportableFormat(true).keySet());
        }
        else{
            importableFormat = new ArrayList<>(ToolBox.getImportableFormat(false).keySet());
        }
        //If there is extension, test if it is recognized by OrbisGIS and register it.
        if(dataStoreAttribute.extensions().length!=0) {
            List<String> validFormats = new ArrayList<>();
            for(String extension : dataStoreAttribute.extensions()){
                if(extension.equals(FormatFactory.GEOCATALOG_EXTENSION)){
                    isGeocatalog = true;
                }
                else if(extension.equals(FormatFactory.DATABASE_EXTENSION)){
                    isDataBase = true;
                }
                else if(importableFormat.contains(extension)){
                    isFile = true;
                    validFormats.add(extension);
                }
                else{
                    LoggerFactory.getLogger(DataStoreParser.class).warn("The format '" + extension + "' is not supported");
                }
            }
            formatList = FormatFactory.getFormatsFromExtensions(validFormats);
        }
        //Else add all the extensions.
        else{
            isGeocatalog = true;
            isDataBase = true;
            isFile = true;
            formatList = FormatFactory.getFormatsFromExtensions(importableFormat);
        }

        //If there is no file format enable, add the "other" format to the format list but it won't be visible.
        if(formatList.isEmpty()) {
            formatList.add(FormatFactory.getFormatFromExtension(FormatFactory.OTHER_EXTENSION));
        }
        formatList.get(0).setDefaultFormat(true);

        //Instantiate the DataStore
        DataStore dataStore = ObjectAnnotationConverter.annotationToObject(dataStoreAttribute, formatList);
        dataStore.setIsDataBase(isDataBase);
        dataStore.setIsGeocatalog(isGeocatalog);
        dataStore.setIsFile(isFile);

        Input input;
        try {
            //Instantiate the returned input
            input = new Input(f.getName(),
                    URI.create(processId + ":input:" + f.getName()),
                    dataStore);
        } catch (MalformedScriptException e) {
            LoggerFactory.getLogger(DataStoreParser.class).error(e.getMessage());
            return null;
        }

        ObjectAnnotationConverter.annotationToObject(f.getAnnotation(InputAttribute.class), input);
        ObjectAnnotationConverter.annotationToObject(f.getAnnotation(DescriptionTypeAttribute.class), input);

        return input;
    }

    @Override
    public Output parseOutput(Field f, String processId) {
        //Instantiate the DataStore and its formats
        DataStoreAttribute dataStoreAttribute = f.getAnnotation(DataStoreAttribute.class);
        List<Format> formatList;
        List<String> exportableGeoFormat;
        boolean isFile = false;
        boolean isGeocatalog = false;
        boolean isDataBase = false;

        if(dataStoreAttribute.isSpatial()){
            exportableGeoFormat = new ArrayList<>(ToolBox.getExportableFormat(true).keySet());
        }
        else{
            exportableGeoFormat = new ArrayList<>(ToolBox.getExportableFormat(false).keySet());
        }

        //If there is extension, test if it is recognized by OrbisGIS and register it.
        if(dataStoreAttribute.extensions().length!=0) {
            List<String> validFormats = new ArrayList<>();
            for(String extension : dataStoreAttribute.extensions()){
                if(extension.equals(FormatFactory.GEOCATALOG_EXTENSION)){
                    isGeocatalog = true;
                }
                else if(extension.equals(FormatFactory.DATABASE_EXTENSION)){
                    isDataBase = true;
                }
                else if(exportableGeoFormat.contains(extension)){
                    isFile = true;
                    validFormats.add(extension);
                }
                else{
                    LoggerFactory.getLogger(DataStoreParser.class).warn("The format '" + extension + "' is not supported");
                }
            }
            formatList = FormatFactory.getFormatsFromExtensions(validFormats);
        }
        //Else add all the extensions.
        else{
            isGeocatalog = true;
            isDataBase = true;
            isFile = true;
            formatList = FormatFactory.getFormatsFromExtensions(exportableGeoFormat);
        }
        formatList.get(0).setDefaultFormat(true);

        //Instantiate the DataStore
        DataStore dataStore = ObjectAnnotationConverter.annotationToObject(dataStoreAttribute, formatList);
        dataStore.setIsDataBase(isDataBase);
        dataStore.setIsGeocatalog(isGeocatalog);
        dataStore.setIsFile(isFile);

        Output output;
        try {
            //Instantiate the returned output
            output = new Output(f.getName(),
                    URI.create(processId + ":output:" + f.getName()),
                    dataStore);
        } catch (MalformedScriptException e) {
            LoggerFactory.getLogger(DataStoreParser.class).error(e.getMessage());
            return null;
        }

        ObjectAnnotationConverter.annotationToObject(f.getAnnotation(DescriptionTypeAttribute.class), output);

        return output;
    }

    @Override
    public Class getAnnotation() {
        return DataStoreAttribute.class;
    }
}
