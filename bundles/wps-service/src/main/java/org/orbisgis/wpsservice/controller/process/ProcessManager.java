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
import net.opengis.ows._2.CodeType;
import net.opengis.wps._2_0.InputDescriptionType;
import net.opengis.wps._2_0.OutputDescriptionType;
import net.opengis.wps._2_0.ProcessDescriptionType;
import org.orbisgis.corejdbc.DataSourceService;
import org.orbisgis.wpsgroovyapi.attributes.DescriptionTypeAttribute;
import org.orbisgis.wpsgroovyapi.process.Process;
import org.orbisgis.wpsservice.LocalWpsService;
import org.orbisgis.wpsservice.controller.parser.ParserController;
import org.orbisgis.wpsservice.controller.utils.CancelClosure;
import org.orbisgis.wpsservice.controller.utils.WpsSql;
import org.orbisgis.wpsservice.model.MalformedScriptException;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    private DataSourceService dataSourceService;
    private LocalWpsService wpsService;
    private Map<ProcessDescriptionType, CancelClosure> closureMap;

    /**
     * Main constructor.
     */
    public ProcessManager(DataSourceService dataSourceService, LocalWpsService wpsService){
        processIdList = new ArrayList<>();
        parserController = new ParserController(wpsService);
        this.dataSourceService = dataSourceService;
        this.wpsService = wpsService;
        this.closureMap = new HashMap<>();
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
    public ProcessDescriptionType addLocalScript(URI uri, String category, boolean isDefault){
        File f = new File(uri);
        //Test that the script name is not only '.groovy'
        if (f.getName().endsWith(".groovy") && f.getName().length()>7) {
            //Ensure that the process does not already exists.
            if(getProcess(uri) == null) {
                //Parse the process
                AbstractMap.SimpleEntry<ProcessDescriptionType, Class> entry;
                try {
                    entry = parserController.parseProcess(f.getAbsolutePath());
                } catch (MalformedScriptException e) {
                    LoggerFactory.getLogger(ProcessManager.class).error("Unable to parse the process '"+uri+"'.", e);
                    return null;
                }
                //Check if the process has been well parsed
                if (entry != null && entry.getKey() != null && entry.getValue() != null) {
                    //Save the process in a ProcessIdentifier
                    ProcessIdentifier pi = new ProcessIdentifier(entry.getValue(), entry.getKey(), uri,
                            f.getParentFile().toURI());
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
    public GroovyObject executeProcess(ProcessDescriptionType process,
                                       Map<URI, Object> dataMap){
        GroovyObject groovyObject = createProcess(process, dataMap);
        if(groovyObject != null) {
            if (dataSourceService != null) {
                WpsSql sql = new WpsSql(dataSourceService);
                CancelClosure closure = new CancelClosure(this);
                closureMap.put(process, closure);
                sql.withStatement(closure);
                groovyObject.setProperty("sql", sql);
            }
            groovyObject.setProperty("logger", LoggerFactory.getLogger(ProcessManager.class));
            groovyObject.setProperty("isH2", wpsService.isH2());
            groovyObject.invokeMethod("processing", null);
            retrieveData(process, groovyObject, dataMap);
        }
        return groovyObject;
    }

    /**
     * Retrieve the data from the groovy object and store the into the dataMap.
     * @param process Process that has generate the groovy object.
     * @param groovyObject GroovyObject containing the processed data.
     * @param dataMap Map linking the data and their identifier.
     */
    private void retrieveData(ProcessDescriptionType process, GroovyObject groovyObject, Map<URI, Object> dataMap){
        ProcessIdentifier pi = null;
        for(ProcessIdentifier proId : processIdList){
            if(proId.getProcessDescriptionType().getIdentifier().equals(process.getIdentifier())){
                pi = proId;
            }
        }
        if(pi == null){
            return;
        }
        try {
            for(InputDescriptionType i : process.getInput()) {
                Field f = getField(pi.getClazz(), i.getIdentifier().getValue());
                f.setAccessible(true);
                dataMap.put(URI.create(i.getIdentifier().getValue()), f.get(groovyObject));
            }
            for(OutputDescriptionType o : process.getOutput()) {
                Field f = getField(pi.getClazz(), o.getIdentifier().getValue());
                f.setAccessible(true);
                dataMap.put(URI.create(o.getIdentifier().getValue()), f.get(groovyObject));
            }
        } catch (IllegalAccessException e) {
            LoggerFactory.getLogger(ProcessManager.class).error(e.getMessage());
        }
    }

    /**
     * Create a groovy object corresponding to the process with the given data.
     * @param process Process that will generate the groovy object.
     * @param dataMap Map of the data for the process.
     * @return A groovy object representing the process with the given data.
     */
    private GroovyObject createProcess(ProcessDescriptionType process, Map<URI, Object> dataMap){
        ProcessIdentifier pi = null;
        for(ProcessIdentifier proId : processIdList){
            if(proId.getProcessDescriptionType().getIdentifier().equals(process.getIdentifier())){
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
            for(InputDescriptionType i : process.getInput()) {
                Field f = getField(pi.getClazz(), i.getIdentifier().getValue());
                if(f != null) {
                    f.setAccessible(true);
                    Object data = dataMap.get(URI.create(i.getIdentifier().getValue()));
                    if(Number.class.isAssignableFrom(f.getType()) && data != null) {
                        try {
                            Method valueOf = f.getType().getMethod("valueOf", String.class);
                            if (valueOf != null) {
                                valueOf.setAccessible(true);
                                data = valueOf.invoke(this, data.toString());
                            }
                        } catch (NoSuchMethodException | InvocationTargetException e) {
                            LoggerFactory.getLogger(ProcessManager.class)
                                    .warn("Unable to convert the LiteralData to the good script type");
                        }
                    }
                    f.set(groovyObject, data);
                }
            }
            for(OutputDescriptionType o : process.getOutput()) {
                Field f = getField(pi.getClazz(), o.getIdentifier().getValue());
                if(f != null) {
                    f.setAccessible(true);
                    f.set(groovyObject, dataMap.get(URI.create(o.getIdentifier().getValue())));
                }
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
    public ProcessDescriptionType getProcess(URI identifier){
        for(ProcessIdentifier pi : processIdList){
            if(pi.getURI().equals(identifier)){
                return pi.getProcessDescriptionType();
            }
        }
        for(ProcessIdentifier pi : processIdList){
            for(InputDescriptionType input : pi.getProcessDescriptionType().getInput()) {
                if (input.getIdentifier().getValue().equals(identifier.toString())) {
                    return pi.getProcessDescriptionType();
                }
            }
            for(OutputDescriptionType output : pi.getProcessDescriptionType().getOutput()) {
                if (output.getIdentifier().getValue().equals(identifier.toString())) {
                    return pi.getProcessDescriptionType();
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
    private Field getField(Class clazz, String identifier){
        for(Field f : clazz.getDeclaredFields()){
            for(Annotation a : f.getDeclaredAnnotations()){
                if(a instanceof DescriptionTypeAttribute){
                    if(((DescriptionTypeAttribute)a).identifier().equals(identifier)){
                        return f;
                    }
                    if(identifier.endsWith(":input:"+((DescriptionTypeAttribute) a).title().replaceAll("[^a-zA-Z0-9_]", "_")) ||
                            identifier.endsWith(":output:"+f.getName().replaceAll("[^a-zA-Z0-9_]", "_"))){
                        return f;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Remove the given process.
     * @param process Process to remove.
     */
    public void removeProcess(ProcessDescriptionType process) {
        ProcessIdentifier toRemove = null;
        for(ProcessIdentifier pi : processIdList){
            if(pi.getProcessDescriptionType().getIdentifier().getValue().equals(process.getIdentifier().getValue())){
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

    /**
     * Returns the ProcessIdentifier containing the process with the given CodeType.
     * @param identifier CodeType used as identifier of a process.
     * @return The process.
     */
    public ProcessIdentifier getProcessIdentifier(CodeType identifier){
        for(ProcessIdentifier pi : processIdList){
            if(pi.getProcessDescriptionType().getIdentifier().getValue().equals(identifier.getValue())){
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
        String str = "";
        for(ProcessIdentifier pi : processIdList){
            if(str.isEmpty()){
                str+=pi.getURI();
            }
            else{
                str+=";"+pi.getURI();
            }
        }
        return str;
    }

    public void cancelProcess(ProcessDescriptionType process){
        closureMap.get(process).cancel();
    }
}
