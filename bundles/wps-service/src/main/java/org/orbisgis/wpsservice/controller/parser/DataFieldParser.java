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

import org.orbisgis.wpsgroovyapi.attributes.DataFieldAttribute;
import org.orbisgis.wpsgroovyapi.attributes.DescriptionTypeAttribute;
import org.orbisgis.wpsgroovyapi.attributes.InputAttribute;
import org.orbisgis.wpsservice.LocalWpsService;
import org.orbisgis.wpsservice.controller.utils.FormatFactory;
import org.orbisgis.wpsservice.controller.utils.ObjectAnnotationConverter;
import org.orbisgis.wpsservice.model.*;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.net.URI;

/**
 * Parser for the DataField input/output annotations.
 *
 * @author Sylvain PALOMINOS
 **/

public class DataFieldParser implements Parser {

    private LocalWpsService wpsService;

    public void setLocalWpsService(LocalWpsService wpsService){
        this.wpsService = wpsService;
    }

    @Override
    public Input parseInput(Field f, Object defaultValue, String processId) {
        //Instantiate the DataField object
        DataFieldAttribute dataFieldAttribute = f.getAnnotation(DataFieldAttribute.class);
        Format format = FormatFactory.getFormatFromExtension(FormatFactory.OTHER_EXTENSION);
        URI dataStoreUri = URI.create(processId + ":input:" + dataFieldAttribute.dataStore());
        DataField dataField = ObjectAnnotationConverter.annotationToObject(dataFieldAttribute, format, dataStoreUri);

        //Instantiate the returned input
        Input input;
        try {
            input = new Input(f.getName(),
                    URI.create(processId + ":input:" + f.getName()),
                    dataField);
        } catch (MalformedScriptException e) {
            LoggerFactory.getLogger(DataFieldParser.class).error(e.getMessage());
            return null;
        }

        ObjectAnnotationConverter.annotationToObject(f.getAnnotation(InputAttribute.class), input);
        ObjectAnnotationConverter.annotationToObject(f.getAnnotation(DescriptionTypeAttribute.class), input);

        return input;
    }

    @Override
    public Output parseOutput(Field f, String processId) {
        //Instantiate the DataField object
        DataFieldAttribute dataFieldAttribute = f.getAnnotation(DataFieldAttribute.class);
        Format format = FormatFactory.getFormatFromExtension(FormatFactory.OTHER_EXTENSION);
        URI dataStoreUri = URI.create(processId + ":output:" + dataFieldAttribute.dataStore());
        DataField dataField = ObjectAnnotationConverter.annotationToObject(dataFieldAttribute, format, dataStoreUri);

        //Instantiate the returned output
        Output output;
        try {
            output = new Output(f.getName(),
                    URI.create(processId + ":output:" + f.getName()),
                    dataField);
        } catch (MalformedScriptException e) {
            LoggerFactory.getLogger(DataFieldParser.class).error(e.getMessage());
            return null;
        }

        ObjectAnnotationConverter.annotationToObject(f.getAnnotation(DescriptionTypeAttribute.class), output);

        return output;
    }

    @Override
    public Class getAnnotation() {
        return DataFieldAttribute.class;
    }
}
