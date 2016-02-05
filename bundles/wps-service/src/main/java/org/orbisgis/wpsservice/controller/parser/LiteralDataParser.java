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

import org.orbisgis.wpsservice.controller.utils.ObjectAnnotationConverter;
import org.orbisgis.orbistoolbox.model.*;
import org.orbisgis.wpsgroovyapi.attributes.*;
import org.orbisgis.wpsservice.model.*;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.net.URI;

/**
 * @author Sylvain PALOMINOS
 **/

public class LiteralDataParser implements Parser {
    @Override
    public Input parseInput(Field f, Object defaultValue, String processId) {
        LiteralData data = ObjectAnnotationConverter.annotationToObject(f.getAnnotation(LiteralDataAttribute.class));

        try {
            //Instantiate the returned input
            Input input = new Input(f.getName(),
                    URI.create(processId + ":input:" + f.getName()),
                    data);

            ObjectAnnotationConverter.annotationToObject(f.getAnnotation(InputAttribute.class), input);
            ObjectAnnotationConverter.annotationToObject(f.getAnnotation(DescriptionTypeAttribute.class), input);

            //Get the type of the field to use it as the input type
            if(f.getType().equals(Integer.class)){
                if(defaultValue != null && data != null) {
                    data.setValue(new LiteralValue(new Value<>((Integer) defaultValue)));
                }
                data.getValue().setDataType(DataType.INTEGER);
            }
            else if(f.getType().equals(Double.class)){
                if(defaultValue != null && data != null) {
                    data.setValue(new LiteralValue(new Value<>((Double) defaultValue)));
                }
                data.getValue().setDataType(DataType.DOUBLE);
            }
            else if(f.getType().equals(String.class)){
                if(defaultValue != null && data != null) {
                    data.setValue(new LiteralValue(new Value<>((String) defaultValue)));
                }
                data.getValue().setDataType(DataType.STRING);
            }
            else if(f.getType().equals(Boolean.class)){
                if(defaultValue != null && data != null) {
                    data.setValue(new LiteralValue(new Value<>((Boolean) defaultValue)));
                }
                data.getValue().setDataType(DataType.BOOLEAN);
            }
            else if(f.getType().equals(Byte.class)){
                if(defaultValue != null && data != null) {
                    data.setValue(new LiteralValue(new Value<>((Byte) defaultValue)));
                }
                data.getValue().setDataType(DataType.BYTE);
            }
            else if(f.getType().equals(Float.class)){
                if(defaultValue != null && data != null) {
                    data.setValue(new LiteralValue(new Value<>((Float) defaultValue)));
                }
                data.getValue().setDataType(DataType.FLOAT);
            }
            else if(f.getType().equals(Long.class)){
                if(defaultValue != null && data != null) {
                    data.setValue(new LiteralValue(new Value<>((Long) defaultValue)));
                }
                data.getValue().setDataType(DataType.LONG);
            }
            else if(f.getType().equals(Short.class)){
                if(defaultValue != null && data != null) {
                    data.setValue(new LiteralValue(new Value<>((Short) defaultValue)));
                }
                data.getValue().setDataType(DataType.SHORT);
            }
            else if(f.getType().equals(Character.class)){
                if(defaultValue != null && data != null) {
                    data.setValue(new LiteralValue(new Value<>((Character) defaultValue)));
                }
                data.getValue().setDataType(DataType.UNSIGNED_BYTE);
            }

            return input;
        } catch (MalformedScriptException e) {
            LoggerFactory.getLogger(LiteralDataParser.class).error(e.getMessage());
            return null;
        }
    }

    @Override
    public Output parseOutput(Field f, String processId) {
        DataDescription data = ObjectAnnotationConverter.annotationToObject(f.getAnnotation(LiteralDataAttribute.class));

        try {
            //Instantiate the returned output
            Output output = new Output(f.getName(),
                    URI.create(processId + ":output:" + f.getName()),
                    data);

            ObjectAnnotationConverter.annotationToObject(f.getAnnotation(DescriptionTypeAttribute.class), output);

            return output;
        } catch (MalformedScriptException e) {
            LoggerFactory.getLogger(LiteralDataParser.class).error(e.getMessage());
            return null;
        }
    }

    @Override
    public Class getAnnotation() {
        return LiteralDataAttribute.class;
    }

}
