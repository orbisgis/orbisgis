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

package org.orbisgis.orbistoolbox.controller.parser;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import org.orbisgis.orbistoolbox.model.Input;
import org.orbisgis.orbistoolbox.model.Process;
import org.orbisgis.orbistoolbox.model.Output;
import org.orbisgis.orbistoolboxapi.annotations.model.InputAttribute;
import org.orbisgis.orbistoolboxapi.annotations.model.OutputAttribute;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
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
        parserList = new ArrayList<>();
        parserList.add(new RawDataParser());
        parserList.add(new LiteralDataParser());
        parserList.add(new BoundingBoxParser());
        parserList.add(new GeoDataParser());
        parserList.add(new GeometryParser());
        defaultParser = new DefaultParser();
        processParser = new ProcessParser();
        groovyClassLoader = new GroovyShell().getClassLoader();
    }

    public AbstractMap.SimpleEntry<Process, Class> parseProcess(String processPath){
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
        List<Input> inputList = new ArrayList<>();
        List<Output> outputList = new ArrayList<>();

        for(Field f : clazz.getDeclaredFields()){
            for(Annotation a : f.getDeclaredAnnotations()){
                if(a instanceof InputAttribute){
                    boolean parsed = false;
                    for(Parser parser : parserList){
                        if(f.getAnnotation(parser.getAnnotation())!= null){
                            inputList.add(parser.parseInput(f, process.getAbsolutePath()));
                            parsed = true;
                        }
                    }
                    if(!parsed){
                        inputList.add(defaultParser.parseInput(f, process.getAbsolutePath()));
                    }
                }
                if(a instanceof OutputAttribute){
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
        try {
            Process p = processParser.parseProcess(inputList,
                    outputList,
                    clazz.getDeclaredMethod("processing"),
                    process.getAbsolutePath());
            return new AbstractMap.SimpleEntry<>(p, clazz);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
