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

package org.orbisgis.orbistoolbox.controller;

import groovy.lang.GroovyObject;
import org.orbisgis.orbistoolbox.controller.parser.ParserController;
import org.orbisgis.orbistoolbox.model.*;
import org.orbisgis.orbistoolbox.model.Process;
import org.orbisgis.orbistoolboxapi.annotations.model.DescriptionTypeAttribute;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.*;

/**
 * Class used to manage process.
 * It manages the sources (remote or local) and keeps the list of instantiated processes.
 *
 * @author Sylvain PALOMINOS
 **/

public class ProcessManager {
    /** List of process identifier*/
    private List<ProcessIdentifier> processIdList;
    /** Controller used to parse process */
    private ParserController parserController;

    /**
     * Main constructor.
     */
    public ProcessManager(){
        processIdList = new ArrayList<>();
        parserController = new ParserController();
    }

    /**
     * Adds a local source to the toolbox and get all the groovy script.
     * @param path Path to the local source.
     */
    public void addLocalSource(String path){
        File folder = new File(path);
        if(!folder.exists() || !folder.isDirectory()){
            return;
        }
        for(File f : folder.listFiles()){
            addLocalScript(f);
        }
    }

    /**
     * Add a local script.
     * @param f File of the local script.
     * @return The process corresponding to the script.
     */
    public Process addLocalScript(File f){
        //Test that the script name is not only '.groovy'
        if (f.getName().endsWith(".groovy") && f.getName().length()>7) {
            //Ensure that the process does not already exists.
            if(getProcess(f) == null) {
                //Parse the process
                AbstractMap.SimpleEntry<Process, Class> entry = parserController.parseProcess(f.getAbsolutePath());
                //Check if the process has been well parsed
                if (entry != null && entry.getKey() != null && entry.getValue() != null) {
                    //Save the process in a ProcessIdentifier
                    processIdList.add(new ProcessIdentifier(
                            entry.getValue(),
                            entry.getKey(),
                            f.getAbsolutePath()
                    ));
                    //return the process
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    /**
     * Execute the given process with the given data.
     * @param process Process to execute.
     * @param inputDataMap Map containing the data for the process.
     * @return The groovy object on which the 'processing' method will be called.
     */
    public GroovyObject executeProcess(Process process,
                                       Map<URI, Object> inputDataMap,
                                       Map<URI, Object> outputDataMap,
                                       Map<String, Object> properties){
        GroovyObject groovyObject = createProcess(process, inputDataMap, outputDataMap);
        for(Map.Entry<String, Object> variable : properties.entrySet()) {
            groovyObject.setProperty("grv_" + variable.getKey(), variable.getValue());
        }
        groovyObject.invokeMethod("processing", null);
        return groovyObject;
    }

    /**
     * Create a groovy object corresponding to the process with the given data.
     * @param process Process that will generate the groovy object.
     * @param inputDataMap Map of the data for the process.
     * @return A groovy object representing the process with the given data.
     */
    private GroovyObject createProcess(Process process, Map<URI, Object> inputDataMap, Map<URI, Object> outputDataMap){
        ProcessIdentifier pi = null;
        for(ProcessIdentifier proId : processIdList){
            if(proId.getProcess().getIdentifier().equals(process.getIdentifier())){
                pi = proId;
            }
        }
        if(pi == null){
            return null;
        }
        GroovyObject groovyObject;
        try {
            groovyObject = (GroovyObject) pi.getClazz().newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            LoggerFactory.getLogger(ProcessManager.class).error(e.getMessage());
            return null;
        }
        try {
            for(Input i : process.getInput()) {
                Field f = getField(pi.getClazz(), i.getIdentifier());
                f.setAccessible(true);
                f.set(groovyObject, inputDataMap.get(i.getIdentifier()));
            }
            for(Output o : process.getOutput()) {
                Field f = getField(pi.getClazz(), o.getIdentifier());
                f.setAccessible(true);
                f.set(groovyObject, outputDataMap.get(o.getIdentifier()));
            }
        } catch (IllegalAccessException e) {
            LoggerFactory.getLogger(ProcessManager.class).error(e.getMessage());
            return null;
        }
        return groovyObject;
    }

    /**
     * Return the process corresponding to the given identifier.
     * @param identifier Identifier of the desired process.
     * @return The process.
     */
    public Process getProcess(URI identifier){
        for(ProcessIdentifier pi : processIdList){
            if(pi.getProcess().getIdentifier().equals(identifier)){
                return pi.getProcess();
            }
        }
        return null;
    }

    public Process getProcess(File f){
        for(ProcessIdentifier pi : processIdList){
            if(pi.getAbsolutePath().equals(f.getAbsolutePath())){
                return pi.getProcess();
            }
        }
        return null;
    }

    /**
     * Return the field of the given class corresponding to the given identifier.
     * @param clazz Class where is the field.
     * @param identifier Identifier of the field.
     * @return The field.
     */
    private Field getField(Class clazz, URI identifier){
        for(Field f : clazz.getDeclaredFields()){
            for(Annotation a : f.getDeclaredAnnotations()){
                if(a instanceof DescriptionTypeAttribute){
                    if(URI.create(((DescriptionTypeAttribute)a).identifier()).equals(identifier)){
                        return f;
                    }
                }
                if(identifier.toString().endsWith(":input:"+f.getName()) || identifier.toString().endsWith(":output:"+f.getName())){
                    return f;
                }
            }
        }
        return null;
    }

    /**
     * Remove the given process.
     * @param process Process to remove.
     */
    public void removeProcess(Process process) {
        ProcessIdentifier toRemove = null;
        for(ProcessIdentifier pi : processIdList){
            if(pi.getProcess().equals(process)){
                toRemove = pi;
            }
        }
        if(toRemove != null){
            processIdList.remove(toRemove);
        }
    }
}
