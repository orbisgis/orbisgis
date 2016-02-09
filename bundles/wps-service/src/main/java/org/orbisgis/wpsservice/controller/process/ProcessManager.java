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

package org.orbisgis.wpsservice.controller.process;

import groovy.lang.GroovyObject;
import groovy.sql.Sql;
import org.orbisgis.corejdbc.DataSourceService;
import org.orbisgis.wpsgroovyapi.attributes.DescriptionTypeAttribute;
import org.orbisgis.wpsservice.LocalWpsService;
import org.orbisgis.wpsservice.controller.parser.ParserController;
import org.orbisgis.wpsservice.model.Input;
import org.orbisgis.wpsservice.model.Output;
import org.orbisgis.wpsservice.model.Process;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private DataSourceService dataSourceService;
    private LocalWpsService wpsService;

    /**
     * Main constructor.
     */
    public ProcessManager(DataSourceService dataSourceService, LocalWpsService wpsService){
        processIdList = new ArrayList<>();
        parserController = new ParserController(wpsService);
        this.dataSourceService = dataSourceService;
        this.wpsService = wpsService;
    }

    /**
     * Adds a local source to the toolbox and get all the groovy script.
     * @param uri URI to the local source.
     */
    public void addLocalSource(URI uri, String category, boolean isDefault){
        File folder = new File(uri);
        if(!folder.exists() || !folder.isDirectory()){
            return;
        }
        for(File f : folder.listFiles()){
            addLocalScript(f.toURI(), category, isDefault);
        }
    }

    /**
     * Add a local script.
     * @param uri URI of the local script.
     * @return The process corresponding to the script.
     */
    public Process addLocalScript(URI uri, String category, boolean isDefault){
        File f = new File(uri);
        //Test that the script name is not only '.groovy'
        if (f.getName().endsWith(".groovy") && f.getName().length()>7) {
            //Ensure that the process does not already exists.
            if(getProcess(uri) == null) {
                //Parse the process
                AbstractMap.SimpleEntry<Process, Class> entry = parserController.parseProcess(f.getAbsolutePath());
                //Check if the process has been well parsed
                if (entry != null && entry.getKey() != null && entry.getValue() != null) {
                    //Save the process in a ProcessIdentifier
                    ProcessIdentifier pi = new ProcessIdentifier(entry.getValue(),entry.getKey(),uri,f.getParentFile().toURI()
                    );
                    pi.setCategory(category);
                    pi.setDefault(isDefault);
                    processIdList.add(pi);
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
     * @param dataMap Map containing the data for the process.
     * @return The groovy object on which the 'processing' method will be called.
     */
    public GroovyObject executeProcess(Process process,
                                       Map<URI, Object> dataMap){
        GroovyObject groovyObject = createProcess(process, dataMap);
        groovyObject.setProperty("sql", new Sql(dataSourceService));
        groovyObject.setProperty("logger", LoggerFactory.getLogger(ProcessManager.class));
        groovyObject.setProperty("isH2", wpsService.isH2());
        groovyObject.invokeMethod("processing", null);
        return groovyObject;
    }

    /**
     * Create a groovy object corresponding to the process with the given data.
     * @param process Process that will generate the groovy object.
     * @param dataMap Map of the data for the process.
     * @return A groovy object representing the process with the given data.
     */
    private GroovyObject createProcess(Process process, Map<URI, Object> dataMap){
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
                f.set(groovyObject, dataMap.get(i.getIdentifier()));
            }
            for(Output o : process.getOutput()) {
                Field f = getField(pi.getClazz(), o.getIdentifier());
                f.setAccessible(true);
                f.set(groovyObject, dataMap.get(o.getIdentifier()));
            }
        } catch (IllegalAccessException e) {
            LoggerFactory.getLogger(ProcessManager.class).error(e.getMessage());
            return null;
        }
        return groovyObject;
    }

    /**
     * Return the process corresponding to the given identifier.
     * The identifier can the the one of the process or an input or an output.
     * @param identifier Identifier of the desired process.
     * @return The process.
     */
    public Process getProcess(URI identifier){
        for(ProcessIdentifier pi : processIdList){
            if(pi.getURI().equals(identifier)){
                return pi.getProcess();
            }
        }
        for(ProcessIdentifier pi : processIdList){
            for(Input input : pi.getProcess().getInput()) {
                if (input.getIdentifier().equals(identifier)) {
                    return pi.getProcess();
                }
            }
            for(Output output : pi.getProcess().getOutput()) {
                if (output.getIdentifier().equals(identifier)) {
                    return pi.getProcess();
                }
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

    public ProcessIdentifier getProcessIdentifier(URI processURI){
        for(ProcessIdentifier pi : processIdList){
            if(pi.getURI().equals(processURI)){
                return pi;
            }
        }
        return null;
    }

    public List<ProcessIdentifier> getAllProcessIdentifier(){
        return processIdList;
    }

    public List<ProcessIdentifier> getProcessIdentifierFromParent(URI parent){
        List<ProcessIdentifier> piList = new ArrayList<>();
        for(ProcessIdentifier pi : processIdList){
            if(pi.getParent().equals(parent)){
                piList.add(pi);
            }
        }
        return piList;
    }

    public String getListSourcesAsString(){
        List<String> sourceList = new ArrayList<>();
        for(ProcessIdentifier pi : processIdList){
            if(!sourceList.contains(new File(pi.getParent()).toURI().toString())){
                sourceList.add(new File(pi.getParent()).toURI().toString());
            }
        }
        String str = "";
        for(String source : sourceList){
            if(str.isEmpty()){
                str+=source;
            }
            else{
                str+=";"+source;
            }
        }
        return str;
    }
}
