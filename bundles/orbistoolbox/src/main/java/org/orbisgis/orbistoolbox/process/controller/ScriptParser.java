/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 *
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 *
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.orbistoolbox.process.controller;

import groovy.lang.GroovyClassLoader;
import org.orbisgis.orbistoolbox.process.model.*;
import org.orbisgis.orbistoolbox.process.model.Process;
import org.orbisgis.orbistoolboxapi.annotations.*;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;

/**
 * Class able to parse a groovy script and returning the corresponding process.
 *
 * @author Sylvain PALOMINOS
 */

public class ScriptParser {

    /**
     *
     * This method is able to read a WPS script, verify if the annotation ar correctly used.
     * Once done it instantiate the inputs and outputs of the process.
     * Then create and return the process
     * @param scriptAbsolutePath Path to the .groovy file.
     * @return The process from the groovy script.
     */
    public Process parseScript(String scriptAbsolutePath) throws IOException {
        ClassLoader parent = getClass().getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);
        Class groovyClass = loader.parseClass(new File(scriptAbsolutePath));

        Method processMethod = null;

        for(Method m : groovyClass.getDeclaredMethods()){
            for(Annotation a : m.getDeclaredAnnotations()){
                if(a instanceof ProcessAttribute){
                    processMethod = m;
                }
            }
        }

        if(processMethod == null){
            //TODO : throw error because the script if wrong.
        }

        List<Field> outputFieldList = new ArrayList<>();
        List<Field> inputFieldList = new ArrayList<>();

        for(Field f : groovyClass.getDeclaredFields()){
            for(Annotation a : f.getDeclaredAnnotations()){
                if(a instanceof OutputAttribute){
                    outputFieldList.add(f);
                }
            }
        }
        for(Field f : groovyClass.getDeclaredFields()){
            for(Annotation a : f.getDeclaredAnnotations()){
                if(a instanceof InputAttribute){
                    inputFieldList.add(f);
                }
            }
        }

        List<Input> inputList = getInputList(inputFieldList);
        List<Output> outputList = getOutputList(outputFieldList);

        Process process = getProcess(processMethod, inputList, outputList);

        return process;
    }

    private Process getProcess(Method processMethod, List<Input> inputList, List<Output> outputList){
    }

    private List<Input> getInputList(List<Field> fieldList){
        List<Input> inputList = new ArrayList<>();

        for(Field f : fieldList){

            List<Format> formatList = new ArrayList<>();
            List<LiteralDataDomain> lddList = new ArrayList<>();

            Format format = new Format("no/mimetype", URI.create("no/mimetype"));
            formatList.add(format);

            DataDescription dataDescription;

            if(f.getType().equals(Boolean.class)){
                List<Values> valueList = new ArrayList<>();
                valueList.add(new Value<>(true));
                valueList.add(new Value<>(false));
                PossibleLiteralValuesChoice plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.BOOLEAN, new Value<>(false)));

                LiteralValue literalValue = new LiteralValue();

                dataDescription = new LiteralData(formatList, lddList, literalValue);
            }
            else if(f.getType().equals(Character.class)){
                List<Values> valueList = new ArrayList<>();
                valueList.add(new Range(Character.MIN_VALUE, Character.MAX_VALUE, 1));
                PossibleLiteralValuesChoice plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.UNSIGNED_BYTE, new Value<>(' ')));

                LiteralValue literalValue = new LiteralValue();

                dataDescription = new LiteralData(formatList, lddList, literalValue);
            }
            else if(f.getType().equals(Byte.class)){
                List<Values> valueList = new ArrayList<>();
                valueList.add(new Range(Byte.MIN_VALUE, Byte.MAX_VALUE, 1));
                PossibleLiteralValuesChoice plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.BYTE, new Value<>(0)));

                LiteralValue literalValue = new LiteralValue();

                dataDescription = new LiteralData(formatList, lddList, literalValue);
            }
            else if(f.getType().equals(Short.class)){
                List<Values> valueList = new ArrayList<>();
                valueList.add(new Range(Short.MIN_VALUE, Short.MAX_VALUE, 1));
                PossibleLiteralValuesChoice plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.SHORT, new Value<>(0)));

                LiteralValue literalValue = new LiteralValue();

                dataDescription = new LiteralData(formatList, lddList, literalValue);
            }
            else if(f.getType().equals(Integer.class)){
                List<Values> valueList = new ArrayList<>();
                valueList.add(new Range(Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
                PossibleLiteralValuesChoice plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.INTEGER, new Value<>(0)));

                LiteralValue literalValue = new LiteralValue();

                dataDescription = new LiteralData(formatList, lddList, literalValue);
            }
            else if(f.getType().equals(Long.class)){
                List<Values> valueList = new ArrayList<>();
                valueList.add(new Range(Long.MIN_VALUE, Long.MAX_VALUE, 1));
                PossibleLiteralValuesChoice plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.LONG, new Value<>(0)));

                LiteralValue literalValue = new LiteralValue();

                dataDescription = new LiteralData(formatList, lddList, literalValue);
            }
            else if(f.getType().equals(Float.class)){
                List<Values> valueList = new ArrayList<>();
                valueList.add(new Range(Float.MIN_VALUE, Float.MAX_VALUE, 1));
                PossibleLiteralValuesChoice plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.FLOAT, new Value<>(0)));

                LiteralValue literalValue = new LiteralValue();

                dataDescription = new LiteralData(formatList, lddList, literalValue);
            }
            else if(f.getType().equals(Double.class)){
                List<Values> valueList = new ArrayList<>();
                valueList.add(new Range(Double.MIN_VALUE, Double.MAX_VALUE, 1));
                PossibleLiteralValuesChoice plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.DOUBLE, new Value<>(0)));

                LiteralValue literalValue = new LiteralValue();

                dataDescription = new LiteralData(formatList, lddList, literalValue);
            }
            else if(f.getType().equals(String.class)){
                PossibleLiteralValuesChoice plvc = new PossibleLiteralValuesChoice();
                lddList.add(new LiteralDataDomain(plvc, DataType.STRING, new Value<>("")));

                LiteralValue literalValue = new LiteralValue();

                dataDescription = new LiteralData(formatList, lddList, literalValue);
            }
            else{
                RawData rawData = new RawData(formatList);
                rawData.setData(f, f.getType());
                dataDescription = rawData;
            }
            Input input = new Input(f.getName(), URI.create("orbisgis:wps:input:"+f.getName()), dataDescription);
            InputAttribute inputAttribute = f.getAnnotation(InputAttribute.class);
            WpsInput wpsInput = (WpsInput)inputAttribute;
            input.setMinOccurs(0);
            input.setMaxOccurs(wpsInput.maxOccurs());
            input.setMinOccurs(wpsInput.minOccurs());
            DescriptionTypeAttribute dta = (DescriptionTypeAttribute)inputAttribute;
            if(!dta.title().equals("")){
                input.setTitle(dta.title());
            }
            if(!dta.abstrac().equals("")){
                input.setAbstrac(dta.abstrac());
            }
            if(!dta.identifier().equals("")){
                input.setIdentifier(URI.create(dta.identifier()));
            }
            if(!dta.keywords().equals("")){
                input.setKeywords(Arrays.asList(dta.keywords().split(",")));
            }
            //TODO : implements for metadata.
            if(!dta.metadata().equals("")){
                input.setMetadata(null);
            }
            inputList.add(input);
        }
        return inputList;
    }

    private List<Output> getOutputList(List<Field> fieldList){
        List<Output> outputList = new ArrayList<>();

        for(Field f : fieldList){

            List<Format> formatList = new ArrayList<>();
            List<LiteralDataDomain> lddList = new ArrayList<>();

            Format format = new Format("no/mimetype", URI.create("no/mimetype"));
            formatList.add(format);

            DataDescription dataDescription;

            if(f.getType().equals(Boolean.class)){
                List<Values> valueList = new ArrayList<>();
                valueList.add(new Value<>(true));
                valueList.add(new Value<>(false));
                PossibleLiteralValuesChoice plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.BOOLEAN, new Value<>(false)));

                LiteralValue literalValue = new LiteralValue();

                dataDescription = new LiteralData(formatList, lddList, literalValue);
            }
            else if(f.getType().equals(Character.class)){
                List<Values> valueList = new ArrayList<>();
                valueList.add(new Range(Character.MIN_VALUE, Character.MAX_VALUE, 1));
                PossibleLiteralValuesChoice plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.UNSIGNED_BYTE, new Value<>(' ')));

                LiteralValue literalValue = new LiteralValue();

                dataDescription = new LiteralData(formatList, lddList, literalValue);
            }
            else if(f.getType().equals(Byte.class)){
                List<Values> valueList = new ArrayList<>();
                valueList.add(new Range(Byte.MIN_VALUE, Byte.MAX_VALUE, 1));
                PossibleLiteralValuesChoice plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.BYTE, new Value<>(0)));

                LiteralValue literalValue = new LiteralValue();

                dataDescription = new LiteralData(formatList, lddList, literalValue);
            }
            else if(f.getType().equals(Short.class)){
                List<Values> valueList = new ArrayList<>();
                valueList.add(new Range(Short.MIN_VALUE, Short.MAX_VALUE, 1));
                PossibleLiteralValuesChoice plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.SHORT, new Value<>(0)));

                LiteralValue literalValue = new LiteralValue();

                dataDescription = new LiteralData(formatList, lddList, literalValue);
            }
            else if(f.getType().equals(Integer.class)){
                List<Values> valueList = new ArrayList<>();
                valueList.add(new Range(Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
                PossibleLiteralValuesChoice plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.INTEGER, new Value<>(0)));

                LiteralValue literalValue = new LiteralValue();

                dataDescription = new LiteralData(formatList, lddList, literalValue);
            }
            else if(f.getType().equals(Long.class)){
                List<Values> valueList = new ArrayList<>();
                valueList.add(new Range(Long.MIN_VALUE, Long.MAX_VALUE, 1));
                PossibleLiteralValuesChoice plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.LONG, new Value<>(0)));

                LiteralValue literalValue = new LiteralValue();

                dataDescription = new LiteralData(formatList, lddList, literalValue);
            }
            else if(f.getType().equals(Float.class)){
                List<Values> valueList = new ArrayList<>();
                valueList.add(new Range(Float.MIN_VALUE, Float.MAX_VALUE, 1));
                PossibleLiteralValuesChoice plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.FLOAT, new Value<>(0)));

                LiteralValue literalValue = new LiteralValue();

                dataDescription = new LiteralData(formatList, lddList, literalValue);
            }
            else if(f.getType().equals(Double.class)){
                List<Values> valueList = new ArrayList<>();
                valueList.add(new Range(Double.MIN_VALUE, Double.MAX_VALUE, 1));
                PossibleLiteralValuesChoice plvc = new PossibleLiteralValuesChoice(valueList);
                lddList.add(new LiteralDataDomain(plvc, DataType.DOUBLE, new Value<>(0)));

                LiteralValue literalValue = new LiteralValue();

                dataDescription = new LiteralData(formatList, lddList, literalValue);
            }
            else if(f.getType().equals(String.class)){
                PossibleLiteralValuesChoice plvc = new PossibleLiteralValuesChoice();
                lddList.add(new LiteralDataDomain(plvc, DataType.STRING, new Value<>("")));

                LiteralValue literalValue = new LiteralValue();

                dataDescription = new LiteralData(formatList, lddList, literalValue);
            }
            else{
                RawData rawData = new RawData(formatList);
                rawData.setData(f, f.getType());
                dataDescription = rawData;
            }
            Output output = new Output(f.getName(), URI.create("orbisgis:wps:input:" + f.getName()), dataDescription);
            OutputAttribute outputAttribute = f.getAnnotation(OutputAttribute.class);
            DescriptionTypeAttribute dta = (DescriptionTypeAttribute)outputAttribute;
            if(!dta.title().equals("")){
                output.setTitle(dta.title());
            }
            if(!dta.abstrac().equals("")){
                output.setAbstrac(dta.abstrac());
            }
            if(!dta.identifier().equals("")){
                output.setIdentifier(URI.create(dta.identifier()));
            }
            if(!dta.keywords().equals("")){
                output.setKeywords(Arrays.asList(dta.keywords().split(",")));
            }
            //TODO : implements for metadata.
            if(!dta.metadata().equals("")){
                output.setMetadata(null);
            }
            outputList.add(output);
        }
        return outputList;
    }
}
