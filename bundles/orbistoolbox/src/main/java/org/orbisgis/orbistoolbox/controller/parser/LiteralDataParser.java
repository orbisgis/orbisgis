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

import org.orbisgis.orbistoolbox.model.*;
import org.orbisgis.orbistoolboxapi.annotations.input.LiteralDataInput;
import org.orbisgis.orbistoolboxapi.annotations.model.*;
import org.orbisgis.orbistoolboxapi.annotations.output.LiteralDataOutput;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Sylvain PALOMINOS
 **/

public class LiteralDataParser implements Parser {
    @Override
    public Input parseInput(Field f, String processName) {
        DataDescription data;

        LiteralDataAttribute literalDataAttribute = f.getAnnotation(LiteralDataAttribute.class);

        List<Format> formatList = new ArrayList<>();
        for(FormatAttribute formatAttribute : literalDataAttribute.formats()){
            Format format = new Format(formatAttribute.mimeType(), URI.create(formatAttribute.schema()));
            format.setDefaultFormat(formatAttribute.isDefaultFormat());
            format.setMaximumMegaBytes(formatAttribute.maximumMegaBytes());
            formatList.add(format);
        }

        List<LiteralDataDomain> lddList = new ArrayList<>();
        for(LiteralDataDomainAttribute literalDataDomainAttribute : literalDataAttribute.validDomains()){
            PossibleLiteralValuesChoice possibleLiteralValuesChoice = null;
            if(literalDataDomainAttribute.plvc().allowedValues().length != 0){
                List<Values> valuesList = new ArrayList<>();
                for(ValuesAttribute va : literalDataDomainAttribute.plvc().allowedValues()){
                    if(va.type().equals(ValuesType.VALUE)){
                        valuesList.add(new Value<>(va.value()));
                    }
                    else{
                        if(va.spacing().equals("")) {
                            valuesList.add(new Range(
                                    Double.parseDouble(va.minimum()),
                                    Double.parseDouble(va.maximum()))
                            );
                        }
                        else{
                            valuesList.add(new Range(
                                    Double.parseDouble(va.minimum()),
                                    Double.parseDouble(va.maximum()),
                                    Double.parseDouble(va.spacing()))
                            );
                        }
                    }
                }
                possibleLiteralValuesChoice = new PossibleLiteralValuesChoice(valuesList);
            }

            DataType dataType = DataType.valueOf(literalDataDomainAttribute.dataType().name());

            Values defaultValue = null;
            ValuesAttribute va = literalDataDomainAttribute.defaultValue();
            if(va.type().equals(ValuesType.VALUE)){
                defaultValue = new Value<>(va.value());
            }
            else{
                if(va.spacing().equals("")) {
                    defaultValue = new Range(Double.parseDouble(va.minimum()), Double.parseDouble(va.maximum()));
                }
                else{
                    defaultValue = new Range(
                                    Double.parseDouble(va.minimum()),
                                    Double.parseDouble(va.maximum()),
                                    Double.parseDouble(va.spacing())
                    );
                }
            }

            LiteralDataDomain literalDataDomain = new LiteralDataDomain(
                    possibleLiteralValuesChoice,
                    dataType,
                    defaultValue
            );
        }


        LiteralValue literalValue = new LiteralValue();
        literalValue.setUom(URI.create(literalDataAttribute.valueAttribute().uom()));
        literalValue.setDataType(DataType.valueOf(literalDataAttribute.valueAttribute().dataType().name()));

        data = new LiteralData(formatList, lddList, literalValue);

        //Instantiate the returned input
        Input input = new Input(f.getName(),
                URI.create("orbisgis:wps:"+processName+":input:"+f.getName()),
                data);

        //Read the InputAttribute annotation to set the Input non mandatory attributes
        InputAttribute wpsInput = f.getAnnotation(InputAttribute.class);
        input.setMinOccurs(0);
        input.setMaxOccurs(wpsInput.maxOccurs());
        input.setMinOccurs(wpsInput.minOccurs());

        //Read the DescriptionTypeAttribute annotation to set the Input non mandatory attributes
        DescriptionTypeAttribute descriptionTypeAttribute = f.getAnnotation(DescriptionTypeAttribute.class);
        if(!descriptionTypeAttribute.title().equals("")){
            input.setTitle(descriptionTypeAttribute.title());
        }
        if(!descriptionTypeAttribute.abstrac().equals("")){
            input.setAbstrac(descriptionTypeAttribute.abstrac());
        }
        if(!descriptionTypeAttribute.identifier().equals("")){
            input.setIdentifier(URI.create(descriptionTypeAttribute.identifier()));
        }
        if(!descriptionTypeAttribute.keywords().equals("")){
            input.setKeywords(Arrays.asList(descriptionTypeAttribute.keywords().split(",")));
        }
        //TODO : implements for metadata.
        if(!descriptionTypeAttribute.metadata().equals("")){
            input.setMetadata(null);
        }

        return input;
    }

    @Override
    public Output parseOutput(Field f, String processName) {
        DataDescription data;

        LiteralDataAttribute literalDataAttribute = f.getAnnotation(LiteralDataAttribute.class);

        List<Format> formatList = new ArrayList<>();
        for(FormatAttribute formatAttribute : literalDataAttribute.formats()){
            Format format = new Format(formatAttribute.mimeType(), URI.create(formatAttribute.schema()));
            format.setDefaultFormat(formatAttribute.isDefaultFormat());
            format.setMaximumMegaBytes(formatAttribute.maximumMegaBytes());
            formatList.add(format);
        }

        List<LiteralDataDomain> lddList = new ArrayList<>();
        for(LiteralDataDomainAttribute literalDataDomainAttribute : literalDataAttribute.validDomains()){
            PossibleLiteralValuesChoice possibleLiteralValuesChoice = null;
            if(literalDataDomainAttribute.plvc().allowedValues().length != 0){
                List<Values> valuesList = new ArrayList<>();
                for(ValuesAttribute va : literalDataDomainAttribute.plvc().allowedValues()){
                    if(va.type().equals(ValuesType.VALUE)){
                        valuesList.add(new Value<>(va.value()));
                    }
                    else{
                        if(va.spacing().equals("")) {
                            valuesList.add(new Range(
                                            Double.parseDouble(va.minimum()),
                                            Double.parseDouble(va.maximum()))
                            );
                        }
                        else{
                            valuesList.add(new Range(
                                            Double.parseDouble(va.minimum()),
                                            Double.parseDouble(va.maximum()),
                                            Double.parseDouble(va.spacing()))
                            );
                        }
                    }
                }
                possibleLiteralValuesChoice = new PossibleLiteralValuesChoice(valuesList);
            }

            DataType dataType = DataType.valueOf(literalDataDomainAttribute.dataType().name());

            Values defaultValue = null;
            ValuesAttribute va = literalDataDomainAttribute.defaultValue();
            if(va.type().equals(ValuesType.VALUE)){
                defaultValue = new Value<>(va.value());
            }
            else{
                if(va.spacing().equals("")) {
                    defaultValue = new Range(Double.parseDouble(va.minimum()), Double.parseDouble(va.maximum()));
                }
                else{
                    defaultValue = new Range(
                            Double.parseDouble(va.minimum()),
                            Double.parseDouble(va.maximum()),
                            Double.parseDouble(va.spacing())
                    );
                }
            }

            LiteralDataDomain literalDataDomain = new LiteralDataDomain(
                    possibleLiteralValuesChoice,
                    dataType,
                    defaultValue
            );
        }


        LiteralValue literalValue = new LiteralValue();
        literalValue.setUom(URI.create(literalDataAttribute.valueAttribute().uom()));
        literalValue.setDataType(DataType.valueOf(literalDataAttribute.valueAttribute().dataType().name()));

        data = new LiteralData(formatList, lddList, literalValue);

        //Instantiate the returned output
        Output output = new Output(f.getName(),
                URI.create("orbisgis:wps:"+processName+":output:"+f.getName()),
                data);

        //Read the DescriptionTypeAttribute annotation to set the Output non mandatory attributes
        OutputAttribute wpsOutput = f.getAnnotation(OutputAttribute.class);
        DescriptionTypeAttribute descriptionTypeAttribute = f.getAnnotation(DescriptionTypeAttribute.class);
        if(!descriptionTypeAttribute.title().equals("")){
            output.setTitle(descriptionTypeAttribute.title());
        }
        if(!descriptionTypeAttribute.abstrac().equals("")){
            output.setAbstrac(descriptionTypeAttribute.abstrac());
        }
        if(!descriptionTypeAttribute.identifier().equals("")){
            output.setIdentifier(URI.create(descriptionTypeAttribute.identifier()));
        }
        if(!descriptionTypeAttribute.keywords().equals("")){
            output.setKeywords(Arrays.asList(descriptionTypeAttribute.keywords().split(",")));
        }
        //TODO : implements for metadata.
        if(!descriptionTypeAttribute.metadata().equals("")){
            output.setMetadata(null);
        }

        return output;
    }

    @Override
    public Class<? extends Annotation> getAnnotationInput() {
        return LiteralDataInput.class;
    }

    @Override
    public Class<? extends Annotation> getAnnotationOutput() {
        return LiteralDataOutput.class;
    }
}
