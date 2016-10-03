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
import net.opengis.ows._2.MetadataType;
import net.opengis.wps._2_0.*;
import org.orbisgis.corejdbc.DataSourceService;
import org.orbisgis.wpsgroovyapi.attributes.DescriptionTypeAttribute;
import org.orbisgis.wpsservice.LocalWpsServer;
import org.orbisgis.wpsservice.WpsServer;
import org.orbisgis.wpsservice.controller.parser.ParserController;
import org.orbisgis.wpsservice.controller.utils.CancelClosure;
import org.orbisgis.wpsservice.controller.utils.WpsSql;
import org.orbisgis.wpsservice.model.DataField;
import org.orbisgis.wpsservice.model.FieldValue;
import org.orbisgis.wpsservice.model.Enumeration;
import org.orbisgis.wpsservice.model.MalformedScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

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
    private WpsServer wpsService;
    private Map<UUID, CancelClosure> closureMap;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessManager.class);
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(ProcessManager.class);

    /**
     * Main constructor.
     */
    public ProcessManager(DataSourceService dataSourceService, WpsServer wpsService){
        processIdList = new ArrayList<>();
        parserController = new ParserController();
        this.dataSourceService = dataSourceService;
        this.wpsService = wpsService;
        this.closureMap = new HashMap<>();
    }

    public ProcessIdentifier addScript(URI scriptUri, String[] category, boolean isRemovable, String nodePath){
        File f = new File(scriptUri);
        if(!f.exists()){
            LOGGER.error(I18N.tr("The script file doesn't exists."));
            return null;
        }
        //Test that the script name is not only '.groovy'
        if (f.getName().endsWith(".groovy") && f.getName().length()>7) {
            //Ensure that the process does not already exists.
            //Parse the process
            ProcessOffering processOffering = null;
            try {
                processOffering = parserController.parseProcess(f.getAbsolutePath());
                if(processOffering != null){
                    MetadataType isRemovableMetadata = new MetadataType();
                    isRemovableMetadata.setTitle(LocalWpsServer.ProcessProperty.IS_REMOVABLE.name());
                    isRemovableMetadata.setRole(LocalWpsServer.ProcessProperty.ROLE.name());
                    isRemovableMetadata.setAbstractMetaData(isRemovable);
                    processOffering.getProcess().getMetadata().add(isRemovableMetadata);
                    if(nodePath != null) {
                        MetadataType nodePathMetadata = new MetadataType();
                        nodePathMetadata.setTitle(LocalWpsServer.ProcessProperty.NODE_PATH.name());
                        nodePathMetadata.setRole(LocalWpsServer.ProcessProperty.ROLE.name());
                        nodePathMetadata.setAbstractMetaData(nodePath);
                        processOffering.getProcess().getMetadata().add(nodePathMetadata);
                    }
                    if(category != null) {
                        MetadataType iconArrayMetadata = new MetadataType();
                        iconArrayMetadata.setTitle(LocalWpsServer.ProcessProperty.ICON_ARRAY.name());
                        iconArrayMetadata.setRole(LocalWpsServer.ProcessProperty.ROLE.name());
                        String iconString = "";
                        for (String icon : category) {
                            if (!iconString.isEmpty()) {
                                iconString += ";";
                            }
                            iconString += icon;
                        }
                        iconArrayMetadata.setAbstractMetaData(iconString);
                        processOffering.getProcess().getMetadata().add(iconArrayMetadata);
                    }
                }
            } catch (MalformedScriptException e) {
                LOGGER.error(I18N.tr("Unable to parse the process '{0}'.", scriptUri), e);
            }
            //If the process is not already registered
            if(processOffering != null) {
                //Save the process in a ProcessIdentifier
                ProcessIdentifier pi = new ProcessIdentifier(processOffering, scriptUri, f.getParentFile().toURI(),
                        nodePath);
                pi.setCategory(category);
                pi.setRemovable(isRemovable);
                processIdList.add(pi);
                return pi;
            }
        }
        return null;
    }

    /**
     * Adds a local source to the toolbox and get all the groovy script.
     * @param uri URI to the local source.
     */
    public List<ProcessIdentifier> addLocalSource(URI uri, String[] category){
        List<ProcessIdentifier> piList = new ArrayList<>();
        File folder = new File(uri);
        if(folder.exists() && folder.isDirectory()){
            for(File f : folder.listFiles()){
                piList.add(addScript(f.toURI(), category, true, "localhost"));
            }
        }
        return piList;
    }

    /**
     * Execute the given process with the given data.
     * @param jobId UUID of the job to execute.
     * @param processIdentifier ProcessIdentifier of the process to execute.
     * @param dataMap Map containing the data for the process.
     * @param propertiesMap Map containing the properties for the GroovyObject.
     * @return The groovy object on which the 'processing' method will be called.
     */
    public GroovyObject executeProcess(
            UUID jobId,
            ProcessIdentifier processIdentifier,
            Map<URI, Object> dataMap,
            Map<String, Object> propertiesMap){

        ProcessDescriptionType process = processIdentifier.getProcessDescriptionType();
        Class clazz = parserController.getProcessClass(processIdentifier.getSourceFileURI());
        GroovyObject groovyObject = createProcess(process, clazz, dataMap);
        if(groovyObject != null) {
            CancelClosure closure = new CancelClosure(this);
            closureMap.put(jobId, closure);
            if (dataSourceService != null) {
                WpsSql sql = new WpsSql(dataSourceService);
                sql.withStatement(closure);
                groovyObject.setProperty("sql", sql);
                groovyObject.setProperty("isH2", wpsService.getDatabase().equals(WpsServer.Database.H2));
            }
            groovyObject.setProperty("logger", LoggerFactory.getLogger(ProcessManager.class));
            for(Map.Entry<String, Object> entry : propertiesMap.entrySet()){
                groovyObject.setProperty(entry.getKey(), entry.getValue());
            }
            groovyObject.invokeMethod("processing", null);
            retrieveData(process, clazz, groovyObject, dataMap);
        }
        return groovyObject;
    }

    /**
     * Retrieve the data from the groovy object and store the into the dataMap.
     * @param process Process that has generate the groovy object.
     * @param groovyObject GroovyObject containing the processed data.
     * @param dataMap Map linking the data and their identifier.
     */
    private void retrieveData(ProcessDescriptionType process, Class clazz, GroovyObject groovyObject, Map<URI, Object> dataMap){
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
                Field f = getField(clazz, i.getIdentifier().getValue());
                f.setAccessible(true);
                dataMap.put(URI.create(i.getIdentifier().getValue()), f.get(groovyObject));
            }
            for(OutputDescriptionType o : process.getOutput()) {
                Field f = getField(clazz, o.getIdentifier().getValue());
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
    private GroovyObject createProcess(ProcessDescriptionType process, Class clazz, Map<URI, Object> dataMap){
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
            groovyObject = (GroovyObject) clazz.newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            LoggerFactory.getLogger(ProcessManager.class).error(e.getMessage());
            return null;
        }
        try {
            for(InputDescriptionType i : process.getInput()) {
                Field f = getField(clazz, i.getIdentifier().getValue());
                if(f != null) {
                    f.setAccessible(true);
                    Object data = dataMap.get(URI.create(i.getIdentifier().getValue()));
                    //If the descriptionType contains a FieldValue, a DataField or an Enumeration, parse the value
                    // which is coma separated.
                    DataDescriptionType dataDescriptionType = i.getDataDescription().getValue();
                    if(dataDescriptionType instanceof FieldValue ||
                            dataDescriptionType instanceof DataField ||
                            dataDescriptionType instanceof Enumeration){
                        if(data != null) {
                            data = data.toString().split("\\t");
                        }
                    }
                    if(Number.class.isAssignableFrom(f.getType()) && data != null) {
                        try {
                            Method valueOf = f.getType().getMethod("valueOf", String.class);
                            if (valueOf != null) {
                                valueOf.setAccessible(true);
                                data = valueOf.invoke(this, data.toString());
                            }
                        } catch (NoSuchMethodException | InvocationTargetException e) {
                            LOGGER.warn(I18N.tr("Unable to convert the LiteralData to the good script type."));
                        }
                    }
                    f.set(groovyObject, data);
                }
            }
            for(OutputDescriptionType o : process.getOutput()) {
                Field f = getField(clazz, o.getIdentifier().getValue());
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
    public ProcessDescriptionType getProcess(CodeType identifier){
        for(ProcessIdentifier pi : processIdList){
            if(pi.getProcessDescriptionType().getIdentifier().getValue().equals(identifier.getValue())){
                return pi.getProcessDescriptionType();
            }
        }
        for(ProcessIdentifier pi : processIdList){
            for(InputDescriptionType input : pi.getProcessDescriptionType().getInput()) {
                if (input.getIdentifier().getValue().equals(identifier.getValue())) {
                    return pi.getProcessDescriptionType();
                }
            }
            for(OutputDescriptionType output : pi.getProcessDescriptionType().getOutput()) {
                if (output.getIdentifier().getValue().equals(identifier.getValue())) {
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
                            identifier.endsWith(":output:"+((DescriptionTypeAttribute) a).title().replaceAll("[^a-zA-Z0-9_]", "_"))){
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

    /**
     * Returns all the process identifiers.
     * @return All the process identifiers.
     */
    public List<ProcessIdentifier> getAllProcessIdentifier(){
        return processIdList;
    }

    /**
     * Returns a string containing all the sources add to the service with all the URI separated by a ;.
     * @return A string containing all the sources add to the service with all the URI separated by a ;.
     */
    public String getListSourcesAsString(){
        String str = "";
        for(ProcessIdentifier pi : processIdList){
            if(pi.isRemovable()) {
                if (str.isEmpty()) {
                    str += pi.getSourceFileURI();
                } else {
                    str += ";" + pi.getSourceFileURI();
                }
            }
        }
        return str;
    }

    /**
     * Cancel the job corresponding to the jobID.
     * @param jobId Id of the job to cancel.
     */
    public void cancelProcess(UUID jobId){
        closureMap.get(jobId).cancel();
    }
}
