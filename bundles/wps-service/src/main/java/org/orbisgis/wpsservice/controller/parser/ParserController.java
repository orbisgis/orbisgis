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

package org.orbisgis.wpsservice.controller.parser;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import net.opengis.wps._2_0.InputDescriptionType;
import net.opengis.wps._2_0.OutputDescriptionType;
import net.opengis.wps._2_0.ProcessDescriptionType;
import net.opengis.wps._2_0.ProcessOffering;
import org.orbisgis.wpsgroovyapi.attributes.InputAttribute;
import org.orbisgis.wpsgroovyapi.attributes.OutputAttribute;
import org.orbisgis.wpsservice.controller.process.ProcessIdentifier;
import org.orbisgis.wpsservice.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * This controller manage the different Parser and is able to parse a script into a process.
 *
 * @author Sylvain PALOMINOS
 **/

public class ParserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParserController.class);

    /** Parser list */
    private List<Parser> parserList;
    private ProcessParser processParser;
    private GroovyClassLoader groovyClassLoader;

    public ParserController(){
        //Instantiate the parser list
        parserList = new ArrayList<>();
        parserList.add(new LiteralDataParser());
        parserList.add(new BoundingBoxParser());
        parserList.add(new DataStoreParser());
        parserList.add(new DataFieldParser());
        parserList.add(new FieldValueParser());
        parserList.add(new EnumerationParser());
        parserList.add(new RawDataParser());
        parserList.add(new GeometryParser());
        parserList.add(new PasswordParser());
        processParser = new ProcessParser();
        groovyClassLoader = new GroovyShell().getClassLoader();
    }

    public Class getProcessClass(URI sourceFileURI){
        try {
            File groovyFile = new File(sourceFileURI);
            groovyClassLoader.clearCache();
            return groovyClassLoader.parseClass(groovyFile);
        } catch (IOException e) {
            LOGGER.error("Can not parse the process : '"+sourceFileURI+"'");
        }
        return null;
    }

    /**
     * Parse a groovy file under a wps process and the groovy class representing the script.
     * @param processPath String path of the file to parse.
     * @return An entry with the process and the class object.
     * @throws MalformedScriptException
     */
    public ProcessOffering parseProcess(String processPath) throws MalformedScriptException {
        //Retrieve the class corresponding to the Groovy script.
        File process = new File(processPath);
        Class clazz = getProcessClass(process.toURI());
        if(clazz == null){
            return null;
        }
        //Retrieve the list of input and output of the script.
        List<InputDescriptionType> inputList = new ArrayList<>();
        List<OutputDescriptionType> outputList = new ArrayList<>();
        Object scriptObject = null;
        try {
            scriptObject = clazz.newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            LoggerFactory.getLogger(ParserController.class).error(
                    "Unable to create a new instance of the groovy script.\n" + e.getMessage());
        }

        for(Field f : clazz.getDeclaredFields()){
            f.setAccessible(true);
            for(Annotation a : f.getDeclaredAnnotations()){
                if(a instanceof InputAttribute){
                    Object defaultValue = null;
                    if(scriptObject != null) {
                        try {
                            f.setAccessible(true);
                            defaultValue = f.get(scriptObject);
                        } catch (IllegalAccessException e) {
                            LoggerFactory.getLogger(ParserController.class).error(
                                    "Unable to retrieve the default value of the field : "+ f + ".\n" + e.getMessage());
                        }
                    }
                    //Find the good parser and parse the input.
                    boolean parsed = false;
                    for(Parser parser : parserList){
                        if(f.getAnnotation(parser.getAnnotation())!= null){
                            InputDescriptionType input = parser.parseInput(f, defaultValue, process.toURI());
                            if(input.getInput() != null && !input.getInput().isEmpty()){
                                for(InputDescriptionType in : input.getInput()){
                                    inputList.add(in);
                                }
                            }
                            else{
                                inputList.add(input);
                            }
                            parsed = true;
                        }
                    }
                    if(!parsed){
                        throw new MalformedScriptException(ParserController.class, a.toString(),
                                "Unable to find the Parser fo the annotation "+a.toString());
                    }
                }
                if(a instanceof OutputAttribute){
                    //Find the good parser and parse the output.
                    boolean parsed = false;
                    for(Parser parser : parserList){
                        if(f.getAnnotation(parser.getAnnotation())!= null){
                            OutputDescriptionType output = parser.parseOutput(f, process.toURI());
                            if(output.getOutput() != null && !output.getOutput().isEmpty()){
                                for(OutputDescriptionType out : output.getOutput()){
                                    outputList.add(out);
                                }
                            }
                            else{
                                outputList.add(output);
                            }
                            parsed = true;
                        }
                    }
                    if(!parsed){
                        throw new MalformedScriptException(ParserController.class, a.toString(),
                                "Unable to find the Parser fo the annotation "+a.toString());
                    }
                }
            }
        }
        //Then parse the process
        try {
            ProcessOffering p = processParser.parseProcess(inputList,
                    outputList,
                    clazz.getDeclaredMethod("processing"),
                    process.toURI());
            link(p.getProcess());
            return p;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Links the input and output with the 'parent'.
     * i.e. : The DataStore contains a list of DataField related.
     * @param p Process to link.
     */
    private void link(ProcessDescriptionType p){
        //Link the DataField with its DataStore
        for(InputDescriptionType i : p.getInput()){
            if(i.getDataDescription().getValue() instanceof DataField){
                DataField dataField = (DataField)i.getDataDescription().getValue();
                for(InputDescriptionType dataStore : p.getInput()){
                    if(dataStore.getIdentifier().getValue().equals(dataField.getDataStoreIdentifier().toString())){
                        ((DataStore)dataStore.getDataDescription().getValue()).addDataField(dataField);
                    }
                }
            }
        }
        //Link the FieldValue with its DataField and its DataStore
        for(InputDescriptionType i : p.getInput()){
            if(i.getDataDescription().getValue() instanceof FieldValue){
                FieldValue fieldValue = (FieldValue)i.getDataDescription().getValue();
                for(InputDescriptionType input : p.getInput()){
                    if(input.getIdentifier().getValue().equals(fieldValue.getDataFieldIdentifier().toString())){
                        DataField dataField = (DataField)input.getDataDescription().getValue();
                        dataField.addFieldValue(fieldValue);
                        fieldValue.setDataStoredIdentifier(dataField.getDataStoreIdentifier());
                    }
                }
            }
        }
        //Link the DataField with its DataStore
        for(OutputDescriptionType o : p.getOutput()){
            if(o.getDataDescription().getValue() instanceof DataField){
                DataField dataField = (DataField)o.getDataDescription().getValue();
                for(OutputDescriptionType dataStore : p.getOutput()){
                    if(dataStore.getIdentifier().getValue().equals(dataField.getDataStoreIdentifier().toString())){
                        ((DataStore)dataStore.getDataDescription().getValue()).addDataField(dataField);
                    }
                }
            }
        }
        //Link the FieldValue with its DataField and its DataStore
        for(OutputDescriptionType o : p.getOutput()){
            if(o.getDataDescription().getValue() instanceof FieldValue){
                FieldValue fieldValue = (FieldValue)o.getDataDescription().getValue();
                for(OutputDescriptionType output : p.getOutput()){
                    if(output.getIdentifier().getValue().equals(fieldValue.getDataFieldIdentifier().toString())){
                        DataField dataField = (DataField)output.getDataDescription().getValue();
                        dataField.addFieldValue(fieldValue);
                        fieldValue.setDataStoredIdentifier(dataField.getDataStoreIdentifier());
                    }
                }
            }
        }
    }
}
