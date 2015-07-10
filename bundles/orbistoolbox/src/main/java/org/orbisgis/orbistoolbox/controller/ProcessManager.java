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
import org.orbisgis.orbistoolbox.view.ProcessIdentifier;
import org.orbisgis.orbistoolboxapi.annotations.model.DescriptionTypeAttribute;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.*;

/**
 * Class used to manage process.
 *
 * @author Sylvain PALOMINOS
 **/

public class ProcessManager {
    private List<ProcessIdentifier> processIdList;
    private ParserController parserController;
    private Map<URI, GroovyObject> processGroovyObjectMap;

    public ProcessManager(){
        processIdList = new ArrayList<>();
        parserController = new ParserController();
        processGroovyObjectMap = new HashMap<>();
    }

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

    public Process addScript(File f){
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

    public GroovyObject executeProcess(Process process, Map<URI, Object> dataMap){
        GroovyObject groovyObject = createProcess(process, dataMap);
        groovyObject.invokeMethod("processing", null);
        return groovyObject;
    }

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
                Field f = getField(pi.getClazz(), i.getIdentifier());
                f.setAccessible(true);
                f.set(groovyObject, dataMap.get(i.getIdentifier()));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
        return groovyObject;
    }

    public Process getProcess(URI uri){
        for(ProcessIdentifier pi : processIdList){
            if(pi.getProcess().getIdentifier().equals(uri)){
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
        return addScript(f);
    }

    public Field getField(Class clazz, URI uri){
        for(Field f : clazz.getDeclaredFields()){
            for(Annotation a : f.getDeclaredAnnotations()){
                if(a instanceof DescriptionTypeAttribute){
                    if(URI.create(((DescriptionTypeAttribute)a).identifier()).equals(uri)){
                        return f;
                    }
                }
                if(uri.toString().endsWith(":input:"+f.getName()) || uri.toString().endsWith(":output:"+f.getName())){
                    return f;
                }
            }
        }
        return null;
    }
}
