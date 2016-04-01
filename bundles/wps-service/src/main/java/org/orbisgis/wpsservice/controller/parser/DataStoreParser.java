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

package org.orbisgis.wpsservice.controller.parser;

import net.opengis.ows.v_2_0.CodeType;
import net.opengis.wps.v_2_0.Format;
import net.opengis.wps.v_2_0.InputDescriptionType;
import net.opengis.wps.v_2_0.OutputDescriptionType;
import org.orbisgis.wpsgroovyapi.attributes.DataStoreAttribute;
import org.orbisgis.wpsgroovyapi.attributes.DescriptionTypeAttribute;
import org.orbisgis.wpsgroovyapi.attributes.InputAttribute;
import org.orbisgis.wpsservice.LocalWpsService;
import org.orbisgis.wpsservice.controller.utils.FormatFactory;
import org.orbisgis.wpsservice.controller.utils.ObjectAnnotationConverter;
import org.orbisgis.wpsservice.model.DataStore;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser for the groovy DataStore annotations.
 *
 * @author Sylvain PALOMINOS
 **/

public class DataStoreParser implements Parser{

    private LocalWpsService wpsService;

    public void setLocalWpsService(LocalWpsService wpsService){
        this.wpsService = wpsService;
    }

    @Override
    public InputDescriptionType parseInput(Field f, Object defaultValue, String processId) {
        //Instantiate the DataStore and its formats
        DataStoreAttribute dataStoreAttribute = f.getAnnotation(DataStoreAttribute.class);
        List<Format> formatList;
        List<String> importableFormat;
        boolean isFile = false;
        boolean isGeocatalog = false;
        boolean isDataBase = false;

        if(dataStoreAttribute.isSpatial()){
            importableFormat = new ArrayList<>(wpsService.getImportableFormat(true).keySet());
        }
        else{
            importableFormat = new ArrayList<>(wpsService.getImportableFormat(false).keySet());
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
            formatList.add(FormatFactory.getFormatFromExtension(FormatFactory.TEXT_EXTENSION));
        }
        formatList.get(0).setDefault(true);

        //Instantiate the DataStore
        DataStore dataStore = ObjectAnnotationConverter.annotationToObject(dataStoreAttribute, formatList);
        dataStore.setIsDataBase(isDataBase);
        dataStore.setIsGeocatalog(isGeocatalog);
        dataStore.setIsFile(isFile);

        InputDescriptionType input = new InputDescriptionType();
        QName qname = new QName("http://orbisgis.org", "data_store");
        JAXBElement<DataStore> jaxbElement = new JAXBElement<>(qname, DataStore.class, dataStore);
        input.setDataDescription(jaxbElement);

        ObjectAnnotationConverter.annotationToObject(f.getAnnotation(InputAttribute.class), input);
        ObjectAnnotationConverter.annotationToObject(f.getAnnotation(DescriptionTypeAttribute.class), input);

        if(input.getIdentifier() == null){
            CodeType codeType = new CodeType();
            codeType.setValue(processId+":input:"+input.getTitle());
            input.setIdentifier(codeType);
        }

        return input;
    }

    @Override
    public OutputDescriptionType parseOutput(Field f, String processId) {
        //Instantiate the DataStore and its formats
        DataStoreAttribute dataStoreAttribute = f.getAnnotation(DataStoreAttribute.class);
        List<Format> formatList;
        List<String> exportableGeoFormat;
        boolean isFile = false;
        boolean isGeocatalog = false;
        boolean isDataBase = false;

        if(dataStoreAttribute.isSpatial()){
            exportableGeoFormat = new ArrayList<>(wpsService.getExportableFormat(true).keySet());
        }
        else{
            exportableGeoFormat = new ArrayList<>(wpsService.getExportableFormat(false).keySet());
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
        formatList.get(0).setDefault(true);

        //Instantiate the DataStore
        DataStore dataStore = ObjectAnnotationConverter.annotationToObject(dataStoreAttribute, formatList);
        dataStore.setIsDataBase(isDataBase);
        dataStore.setIsGeocatalog(isGeocatalog);
        dataStore.setIsFile(isFile);

        OutputDescriptionType output = new OutputDescriptionType();
        QName qname = new QName("http://orbisgis.org", "data_store");
        JAXBElement<DataStore> jaxbElement = new JAXBElement<>(qname, DataStore.class, dataStore);
        output.setDataDescription(jaxbElement);

        ObjectAnnotationConverter.annotationToObject(f.getAnnotation(DescriptionTypeAttribute.class), output);

        if(output.getIdentifier() == null){
            CodeType codeType = new CodeType();
            codeType.setValue(processId+":output:"+output.getTitle());
            output.setIdentifier(codeType);
        }

        return output;
    }

    @Override
    public Class getAnnotation() {
        return DataStoreAttribute.class;
    }
}
