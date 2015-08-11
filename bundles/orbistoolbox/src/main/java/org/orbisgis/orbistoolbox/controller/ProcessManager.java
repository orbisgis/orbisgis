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
import org.orbisgis.orbistoolbox.model.Input;
import org.orbisgis.orbistoolbox.model.Process;
import org.orbisgis.orbistoolboxapi.annotations.model.DescriptionTypeAttribute;

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
            if(f.getName().endsWith(".groovy")){
                AbstractMap.SimpleEntry entry = parserController.parseProcess(f.getAbsolutePath());
                processIdList.add(new ProcessIdentifier((Class) entry.getValue(), (Process) entry.getKey(), f.getAbsolutePath()));
            }
        }
    }

    /**
     * Add a local script.
     * @param f File of the local script.
     * @return The process corresponding to the script.
     */
    public Process addLocalScript(File f){
        if (f.getName().endsWith(".groovy")) {
            AbstractMap.SimpleEntry entry = parserController.parseProcess(f.getAbsolutePath());
            if(entry != null && entry.getKey() != null && entry.getValue() != null){
                processIdList.add(new ProcessIdentifier(
                        (Class) entry.getValue(),
                        (Process) entry.getKey(),
                        f.getAbsolutePath()
                ));
                return (Process) entry.getKey();
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
    public GroovyObject executeProcess(Process process, Map<URI, Object> dataMap){
        GroovyObject groovyObject = createProcess(process, dataMap);
        groovyObject.invokeMethod("processing", null);
        return groovyObject;
    }

    /**
     * Create a groovy object corresponding to the process with the given datas.
     * @param process Process that will generate the groovy object.
     * @param dataMap Map of the data for the process.
     * @return A groovy object representing the process with the given datas.
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
            e.printStackTrace();
            return null;
        }
        try {
            for(Input i : process.getInput()) {
                System.out.println(i);
                Field f = getField(pi.getClazz(), i.getIdentifier());
                System.out.println(f);
                System.out.println(dataMap.get(i.getIdentifier()));
                f.setAccessible(true);
                f.set(groovyObject, dataMap.get(i.getIdentifier()));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
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
        return addLocalScript(f);
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
