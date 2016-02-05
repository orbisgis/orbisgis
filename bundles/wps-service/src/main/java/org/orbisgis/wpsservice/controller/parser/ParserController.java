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
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import org.orbisgis.orbistoolbox.model.*;
import org.orbisgis.wpsservice.model.*;
import org.orbisgis.wpsgroovyapi.attributes.InputAttribute;
import org.orbisgis.wpsgroovyapi.attributes.OutputAttribute;
import org.orbisgis.wpsservice.model.Process;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * This controller manage the different Parser and is able to parse a script into a process.
 *
 * @author Sylvain PALOMINOS
 **/

public class ParserController {

    /** Parser list */
    private List<Parser> parserList;
    private DefaultParser defaultParser;
    private ProcessParser processParser;
    private GroovyClassLoader groovyClassLoader;

    public ParserController(){
        //Instantiate the parser list
        parserList = new ArrayList<>();
        parserList.add(new RawDataParser());
        parserList.add(new LiteralDataParser());
        parserList.add(new BoundingBoxParser());
        parserList.add(new DataStoreParser());
        parserList.add(new DataFieldParser());
        parserList.add(new FieldValueParser());
        parserList.add(new EnumerationParser());
        defaultParser = new DefaultParser();
        processParser = new ProcessParser();
        groovyClassLoader = new GroovyShell().getClassLoader();
    }

    public AbstractMap.SimpleEntry<Process, Class> parseProcess(String processPath){
        //Retrieve the class corresponding to the Groovy script.
        Class clazz;
        File process = new File(processPath);
        try {
            groovyClassLoader.clearCache();
            clazz = groovyClassLoader.parseClass(process);
        } catch (IOException|GroovyRuntimeException e) {
            LoggerFactory.getLogger(ParserController.class).error("Can not parse the process : '"+processPath+"'");
            LoggerFactory.getLogger(ParserController.class).error(e.getMessage());
            return null;
        }
        //Retrieve the list of input and output of the script.
        List<Input> inputList = new ArrayList<>();
        List<Output> outputList = new ArrayList<>();
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
                            inputList.add(parser.parseInput(f, defaultValue, process.getAbsolutePath()));
                            parsed = true;
                        }
                    }
                    if(!parsed){
                        inputList.add(defaultParser.parseInput(f, defaultValue, process.getAbsolutePath()));
                    }
                }
                if(a instanceof OutputAttribute){
                    //Find the good parser and parse the output.
                    boolean parsed = false;
                    for(Parser parser : parserList){
                        if(f.getAnnotation(parser.getAnnotation())!= null){
                            outputList.add(parser.parseOutput(f, process.getAbsolutePath()));
                            parsed = true;
                        }
                    }
                    if(!parsed){
                        outputList.add(defaultParser.parseOutput(f, process.getAbsolutePath()));
                    }
                }
            }
        }
        //Then parse the process
        try {
            Process p = processParser.parseProcess(inputList,
                    outputList,
                    clazz.getDeclaredMethod("processing"),
                    process.getAbsolutePath());
            link(p);
            return new AbstractMap.SimpleEntry<>(p, clazz);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Links the input and output with the 'parent'.
     * i.e. : The DataStore contains a list of DataField related.
     * @param p
     */
    private void link(Process p){
        //Link the DataField with its DataStore
        for(Input i : p.getInput()){
            if(i.getDataDescription() instanceof DataField){
                DataField dataField = (DataField)i.getDataDescription();
                for(Input dataStore : p.getInput()){
                    if(dataStore.getIdentifier().equals(dataField.getDataStoreIdentifier())){
                        ((DataStore)dataStore.getDataDescription()).addDataField(dataField);
                    }
                }
            }
        }
        //Link the FieldValue with its DataField and its DataStore
        for(Input i : p.getInput()){
            if(i.getDataDescription() instanceof FieldValue){
                FieldValue fieldValue = (FieldValue)i.getDataDescription();
                for(Input input : p.getInput()){
                    if(input.getIdentifier().equals(fieldValue.getDataFieldIdentifier())){
                        DataField dataField = (DataField)input.getDataDescription();
                        dataField.addFieldValue(fieldValue);
                        fieldValue.setDataStoredIdentifier(dataField.getDataStoreIdentifier());
                    }
                }
            }
        }
    }
}
