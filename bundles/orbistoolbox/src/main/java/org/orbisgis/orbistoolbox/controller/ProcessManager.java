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
import org.orbisgis.orbistoolbox.model.Process;
import org.orbisgis.orbistoolboxapi.annotations.model.DescriptionTypeAttribute;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class used to manage process.
 *
 * @author Sylvain PALOMINOS
 **/

public class ProcessManager {
    private Map<Process, Class> mapProcessClass;
    private ParserController parserController;
    private Map<URI, GroovyObject> processGroovyObjectMap;

    public ProcessManager(){
        mapProcessClass = new HashMap<>();
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
                mapProcessClass.put((Process)entry.getKey(), (Class)entry.getValue());
            }
        }
    }

    public void setProcessData(Map<URI, Object> inputMap, URI process){
        Class clazz = mapProcessClass.get(getProcess(process));
        GroovyObject groovyObject;
        try {
            groovyObject = (GroovyObject)clazz.newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            e.printStackTrace();
            return;
        }
        try {
            for(URI uri : inputMap.keySet()) {
                Field f = getField(clazz, uri);
                f.setAccessible(true);
                f.set(groovyObject, inputMap.get(uri));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }
        processGroovyObjectMap.put(process, groovyObject);
    }

    public void executeProcess(URI process){
        GroovyObject groovyObject = processGroovyObjectMap.get(process);
        groovyObject.invokeMethod("processing", null);
    }

    public Process getProcess(URI uri){
        for(Process p : mapProcessClass.keySet()){
            if(p.getIdentifier().equals(uri)){
                return p;
            }
        }
        return null;
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
