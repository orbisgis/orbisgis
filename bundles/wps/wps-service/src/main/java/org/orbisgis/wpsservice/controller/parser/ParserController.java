/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */

package org.orbisgis.wpsservice.controller.parser;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import net.opengis.wps._2_0.InputDescriptionType;
import net.opengis.wps._2_0.OutputDescriptionType;
import net.opengis.wps._2_0.ProcessDescriptionType;
import net.opengis.wps._2_0.ProcessOffering;
import org.orbisgis.wpsgroovyapi.attributes.InputAttribute;
import org.orbisgis.wpsgroovyapi.attributes.OutputAttribute;
import org.orbisgis.wpsservice.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URI;
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
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(ParserController.class);

    public ParserController(){
        //Instantiate the parser list
        parserList = new ArrayList<>();
        parserList.add(new LiteralDataParser());
        parserList.add(new BoundingBoxParser());
        parserList.add(new JDBCTableParser());
        parserList.add(new JDBCTableFieldParser());
        parserList.add(new JDBCTableFieldValueParser());
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
        } catch (Exception e) {
            LOGGER.error(I18N.tr("Can not parse the process : {0}\n Cause : {1}.", sourceFileURI, e.getLocalizedMessage()));
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
        File processFile = new File(processPath);
        Class clazz = getProcessClass(processFile.toURI());
        if(clazz == null){
            return null;
        }

        //Parse the process
        ProcessOffering processOffering;
        try {
            processOffering = processParser.parseProcess(clazz.getDeclaredMethod("processing"), processFile.toURI());
            link(processOffering.getProcess());
        } catch (NoSuchMethodException e) {
            return null;
        }
        ProcessDescriptionType process = processOffering.getProcess();

        //Retrieve the list of input and output of the script.
        List<InputDescriptionType> inputList = new ArrayList<>();
        List<OutputDescriptionType> outputList = new ArrayList<>();
        Object scriptObject = null;
        try {
            scriptObject = clazz.newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            LOGGER.error(I18N.tr("Unable to create a new instance of the groovy script.\nCause : {0}", e.getMessage()));
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
                            LOGGER.error(I18N.tr("Unable to retrieve the default value of the field : {0}.\n" +
                                    "Cause : {1}.", f, e.getMessage()));
                        }
                    }
                    //Find the good parser and parse the input.
                    boolean parsed = false;
                    for(Parser parser : parserList){
                        if(f.getAnnotation(parser.getAnnotation())!= null){
                            InputDescriptionType input = parser.parseInput(f, defaultValue,
                                    URI.create(process.getIdentifier().getValue()));
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
                                I18N.tr("Unable to find the Parser fo the annotation {0}.", a.toString()));
                    }
                }
                if(a instanceof OutputAttribute){
                    Object defaultValue = null;
                    if(scriptObject != null) {
                        try {
                            f.setAccessible(true);
                            defaultValue = f.get(scriptObject);
                        } catch (IllegalAccessException e) {
                            LOGGER.error(I18N.tr("Unable to retrieve the default value of the field : {0}.\n" +
                                    "Cause : {1}.", f, e.getMessage()));
                        }
                    }
                    //Find the good parser and parse the output.
                    boolean parsed = false;
                    for(Parser parser : parserList){
                        if(f.getAnnotation(parser.getAnnotation())!= null){
                            OutputDescriptionType output = parser.parseOutput(f, defaultValue,
                                    URI.create(process.getIdentifier().getValue()));
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
                                I18N.tr("Unable to find the Parser for the annotation {0}.", a.toString()));
                    }
                }
            }
        }
        process.getOutput().clear();
        process.getOutput().addAll(outputList);
        process.getInput().clear();
        process.getInput().addAll(inputList);

        return processOffering;
    }

    /**
     * Links the input and output with the 'parent'.
     * i.e. : The JDBCTable contains a list of JDBCTableField related.
     * @param p Process to link.
     */
    private void link(ProcessDescriptionType p){
        //Link the JDBCTableField with its JDBCTable
        for(InputDescriptionType i : p.getInput()){
            if(i.getDataDescription().getValue() instanceof JDBCTableField){
                JDBCTableField jdbcTableField = (JDBCTableField)i.getDataDescription().getValue();
                for(InputDescriptionType jdbcTable : p.getInput()){
                    if(jdbcTable.getIdentifier().getValue().equals(jdbcTableField.getJDBCTableIdentifier().toString())){
                        ((JDBCTable)jdbcTable.getDataDescription().getValue()).addJDBCTableField(jdbcTableField);
                    }
                }
            }
        }
        //Link the JDBCTableFieldValue with its JDBCTableField and its JDBCTable
        for(InputDescriptionType i : p.getInput()){
            if(i.getDataDescription().getValue() instanceof JDBCTableFieldValue){
                JDBCTableFieldValue jdbcTableFieldValue = (JDBCTableFieldValue)i.getDataDescription().getValue();
                for(InputDescriptionType input : p.getInput()){
                    if(input.getIdentifier().getValue().equals(jdbcTableFieldValue.getJDBCTableFieldIdentifier().toString())){
                        JDBCTableField jdbcTableField = (JDBCTableField)input.getDataDescription().getValue();
                        jdbcTableField.addJDBCTableFieldValue(jdbcTableFieldValue);
                        jdbcTableFieldValue.setJDBCTableIdentifier(jdbcTableField.getJDBCTableIdentifier());
                    }
                }
            }
        }
        //Link the JDBCTableField with its JDBCTable
        for(OutputDescriptionType o : p.getOutput()){
            if(o.getDataDescription().getValue() instanceof JDBCTableField){
                JDBCTableField jdbcTableField = (JDBCTableField)o.getDataDescription().getValue();
                for(OutputDescriptionType jdbcTable : p.getOutput()){
                    if(jdbcTable.getIdentifier().getValue().equals(jdbcTableField.getJDBCTableIdentifier().toString())){
                        ((JDBCTable)jdbcTable.getDataDescription().getValue()).addJDBCTableField(jdbcTableField);
                    }
                }
            }
        }
        //Link the JDBCTableFieldValue with its JDBCTableField and its JDBCTable
        for(OutputDescriptionType o : p.getOutput()){
            if(o.getDataDescription().getValue() instanceof JDBCTableFieldValue){
                JDBCTableFieldValue jdbcTableFieldValue = (JDBCTableFieldValue)o.getDataDescription().getValue();
                for(OutputDescriptionType output : p.getOutput()){
                    if(output.getIdentifier().getValue().equals(jdbcTableFieldValue.getJDBCTableFieldIdentifier().toString())){
                        JDBCTableField jdbcTableField = (JDBCTableField)output.getDataDescription().getValue();
                        jdbcTableField.addJDBCTableFieldValue(jdbcTableFieldValue);
                        jdbcTableFieldValue.setJDBCTableIdentifier(jdbcTableField.getJDBCTableIdentifier());
                    }
                }
            }
        }
    }
}
