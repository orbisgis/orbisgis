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
import org.orbisgis.orbistoolboxapi.annotations.model.DescriptionTypeAttribute;
import org.orbisgis.orbistoolboxapi.annotations.model.GeoDataAttribute;
import org.orbisgis.orbistoolboxapi.annotations.model.InputAttribute;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Parser for groovy GeoData annotations.
 *
 * @author Sylvain PALOMINOS
 **/

public class GeoDataParser implements Parser{

    @Override
    public Input parseInput(Field f, String processId) {
        //Instantiate the GeoData and its formats
        GeoDataAttribute geoDataAttribute = f.getAnnotation(GeoDataAttribute.class);
        List<Format> formatList;
        List<String> importableGeoFormat = new ArrayList<>(ToolBox.getImportableGeoFormat().keySet());
        if(geoDataAttribute.extensions().length!=0) {
            for(String extension : geoDataAttribute.extensions()){
                if(!importableGeoFormat.contains(extension)){
                    LoggerFactory.getLogger(GeoDataParser.class).warn("The format '" + extension + "' is not supported");
                }
            }
            formatList = FormatFactory.getFormatsFromExtensions(geoDataAttribute.extensions());
        }
        else{
            formatList = FormatFactory.getFormatsFromExtensions(importableGeoFormat);
            formatList.add(FormatFactory.getFormatFromExtension(FormatFactory.SQL_EXTENSION));
        }
        formatList.get(0).setDefaultFormat(true);
        GeoData geoData = ObjectAnnotationConverter.annotationToObject(geoDataAttribute, formatList);

        Input input;
        try {
            //Instantiate the returned input
            input = new Input(f.getName(),
                    URI.create(processId + ":input:" + f.getName()),
                    geoData);
        } catch (MalformedScriptException e) {
            LoggerFactory.getLogger(GeoDataParser.class).error(e.getMessage());
            return null;
        }

        ObjectAnnotationConverter.annotationToObject(f.getAnnotation(InputAttribute.class), input);
        ObjectAnnotationConverter.annotationToObject(f.getAnnotation(DescriptionTypeAttribute.class), input);

        return input;
    }

    @Override
    public Output parseOutput(Field f, String processId) {
        //Instantiate the GeoData and its formats
        GeoDataAttribute geoDataAttribute = f.getAnnotation(GeoDataAttribute.class);
        List<Format> formatList;
        List<String> exportableGeoFormat = new ArrayList<>(ToolBox.getExportableGeoFormat().keySet());
        if(geoDataAttribute.extensions().length!=0) {
            for(String extension : geoDataAttribute.extensions()){
                if(!exportableGeoFormat.contains(extension)){
                    LoggerFactory.getLogger(GeoDataParser.class).warn("The format '" + extension + "' is not supported");
                }
            }
            formatList = FormatFactory.getFormatsFromExtensions(geoDataAttribute.extensions());
        }
        else{
            formatList = FormatFactory.getFormatsFromExtensions(exportableGeoFormat);
            formatList.add(FormatFactory.getFormatFromExtension(FormatFactory.SQL_EXTENSION));
        }
        formatList.get(0).setDefaultFormat(true);
        GeoData geoData = ObjectAnnotationConverter.annotationToObject(geoDataAttribute, formatList);

        Output output;
        try {
            //Instantiate the returned output
            output = new Output(f.getName(),
                    URI.create(processId + ":output:" + f.getName()),
                    geoData);
        } catch (MalformedScriptException e) {
            LoggerFactory.getLogger(GeoDataParser.class).error(e.getMessage());
            return null;
        }

        ObjectAnnotationConverter.annotationToObject(f.getAnnotation(DescriptionTypeAttribute.class), output);

        return output;
    }

    @Override
    public Class getAnnotation() {
        return GeoDataAttribute.class;
    }
}
