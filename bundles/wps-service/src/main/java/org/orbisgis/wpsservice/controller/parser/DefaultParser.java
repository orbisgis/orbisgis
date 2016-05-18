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

import net.opengis.wps._2_0.InputDescriptionType;
import net.opengis.wps._2_0.OutputDescriptionType;
import org.orbisgis.wpsgroovyapi.attributes.InputAttribute;
import org.orbisgis.wpsservice.LocalWpsService;
import org.orbisgis.wpsservice.model.*;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Default parse. This Parser is able to parse any input or output as a raw data, literal data or bounding box.
 * But the DataDescription returned is basic.
 *
 * @author Sylvain PALOMINOS
 **/
@Deprecated
public class DefaultParser implements Parser {

    private LocalWpsService wpsService;

    public void setLocalWpsService(LocalWpsService wpsService){
        this.wpsService = wpsService;
    }

    @Override
    public InputDescriptionType parseInput(Field f, Object defaultValue, String processId) {
        DataDescription data;
        List<Format> formatList = new ArrayList<>();
        List<LiteralDataDomain> lddList = new ArrayList<>();

        try {
            //Create a format list with an empty format
            Format format = new Format("no/mimetype", URI.create("no/mimetype"));
            format.setDefaultFormat(true);
            formatList.add(format);

            //Check if the type of the field is Boolean, Character, Byte ... to instantiate a LiteralData
            List<Values> valueList = new ArrayList<>();
            PossibleLiteralValuesChoice plvc;

            if (f.getType().equals(Boolean.class)) {
                valueList.add(new Value<>(true));
                valueList.add(new Value<>(false));
                plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.BOOLEAN, new Value<>(false)));

                data = new LiteralData(formatList, lddList, new LiteralValue());
            } else if (f.getType().equals(Character.class)) {
                valueList.add(new Range(Character.MIN_VALUE, Character.MAX_VALUE, 1));
                plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.UNSIGNED_BYTE, new Value<>(' ')));

                data = new LiteralData(formatList, lddList, new LiteralValue());
            } else if (f.getType().equals(Byte.class)) {
                valueList.add(new Range(Byte.MIN_VALUE, Byte.MAX_VALUE, 1));
                plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.BYTE, new Value<>(0)));

                data = new LiteralData(formatList, lddList, new LiteralValue());
            } else if (f.getType().equals(Short.class)) {
                valueList.add(new Range(Short.MIN_VALUE, Short.MAX_VALUE, 1));
                plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.SHORT, new Value<>(0)));

                data = new LiteralData(formatList, lddList, new LiteralValue());
            } else if (f.getType().equals(Integer.class)) {
                valueList.add(new Range(Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
                plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.INTEGER, new Value<>(0)));

                data = new LiteralData(formatList, lddList, new LiteralValue());
            } else if (f.getType().equals(Long.class)) {
                valueList.add(new Range(Long.MIN_VALUE, Long.MAX_VALUE, 1));
                plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.LONG, new Value<>(0)));

                data = new LiteralData(formatList, lddList, new LiteralValue());
            } else if (f.getType().equals(Float.class)) {
                valueList.add(new Range(Float.MIN_VALUE, Float.MAX_VALUE, 1));
                plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.FLOAT, new Value<>(0)));

                data = new LiteralData(formatList, lddList, new LiteralValue());
            } else if (f.getType().equals(Double.class)) {
                valueList.add(new Range(Double.MIN_VALUE, Double.MAX_VALUE, 1));
                plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.DOUBLE, new Value<>(0)));

                data = new LiteralData(formatList, lddList, new LiteralValue());
            } else if (f.getType().equals(String.class)) {
                plvc = new PossibleLiteralValuesChoice();
                lddList.add(new LiteralDataDomain(plvc, DataType.STRING, new Value<>("")));

                data = new LiteralData(formatList, lddList, new LiteralValue());
            }
            //If the field can not be parsed as a LiteralData, parse it as a RawData
            else {
                data = null;
            }

            //Instantiate the returned input
            Input input = new Input(f.getName(),
                    URI.create(processId + ":input:" + f.getName()),
                    data);
            input.setMinOccurs(0);
            input.setMaxOccurs(1);
            input.setMinOccurs(1);

            return null;
        } catch (MalformedScriptException e) {
            LoggerFactory.getLogger(DefaultParser.class).error(e.getMessage());
            return null;
        }
    }

    @Override
    public OutputDescriptionType parseOutput(Field f, String processId) {
        DataDescription data;
        List<Format> formatList = new ArrayList<>();
        List<LiteralDataDomain> lddList = new ArrayList<>();

        try {
            //Create a format list with an empty format
            Format format = new Format("no/mimetype", URI.create("no/mimetype"));
            format.setDefaultFormat(true);
            formatList.add(format);

            //Check if the type of the field is Boolean, Character, Byte ... to instantiate a LiteralData
            List<Values> valueList = new ArrayList<>();
            PossibleLiteralValuesChoice plvc;

            if (f.getType().equals(Boolean.class)) {
                valueList.add(new Value<>(true));
                valueList.add(new Value<>(false));
                plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.BOOLEAN, new Value<>(false)));

                data = new LiteralData(formatList, lddList, new LiteralValue());
            } else if (f.getType().equals(Character.class)) {
                valueList.add(new Range(Character.MIN_VALUE, Character.MAX_VALUE, 1));
                plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.UNSIGNED_BYTE, new Value<>(' ')));

                data = new LiteralData(formatList, lddList, new LiteralValue());
            } else if (f.getType().equals(Byte.class)) {
                valueList.add(new Range(Byte.MIN_VALUE, Byte.MAX_VALUE, 1));
                plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.BYTE, new Value<>(0)));

                data = new LiteralData(formatList, lddList, new LiteralValue());
            } else if (f.getType().equals(Short.class)) {
                valueList.add(new Range(Short.MIN_VALUE, Short.MAX_VALUE, 1));
                plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.SHORT, new Value<>(0)));

                data = new LiteralData(formatList, lddList, new LiteralValue());
            } else if (f.getType().equals(Integer.class)) {
                valueList.add(new Range(Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
                plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.INTEGER, new Value<>(0)));

                data = new LiteralData(formatList, lddList, new LiteralValue());
            } else if (f.getType().equals(Long.class)) {
                valueList.add(new Range(Long.MIN_VALUE, Long.MAX_VALUE, 1));
                plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.LONG, new Value<>(0)));

                data = new LiteralData(formatList, lddList, new LiteralValue());
            } else if (f.getType().equals(Float.class)) {
                valueList.add(new Range(Float.MIN_VALUE, Float.MAX_VALUE, 1));
                plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.FLOAT, new Value<>(0)));

                data = new LiteralData(formatList, lddList, new LiteralValue());
            } else if (f.getType().equals(Double.class)) {
                valueList.add(new Range(Double.MIN_VALUE, Double.MAX_VALUE, 1));
                plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.DOUBLE, new Value<>(0)));

                data = new LiteralData(formatList, lddList, new LiteralValue());
            } else if (f.getType().equals(String.class)) {
                plvc = new PossibleLiteralValuesChoice();
                lddList.add(new LiteralDataDomain(plvc, DataType.STRING, new Value<>("")));

                data = new LiteralData(formatList, lddList, new LiteralValue());
            }
            //If the field can not be parsed as a LiteralData, parse it as a RawData
            else {
                data = null;
            }
            //Instantiate the returned output
            Output output = new Output(f.getName(),
                    URI.create(processId + ":output:" + f.getName()),
                    data);

            return null;
        } catch(MalformedScriptException e){
            LoggerFactory.getLogger(DefaultParser.class).error(e.getMessage());
            return null;
        }
    }

    @Override
    public Class getAnnotation() {
        return InputAttribute.class;
    }

}
