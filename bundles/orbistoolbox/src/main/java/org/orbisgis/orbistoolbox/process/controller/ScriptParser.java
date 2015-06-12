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

package org.orbisgis.orbistoolbox.process.controller;

import groovy.lang.GroovyClassLoader;
import org.orbisgis.orbistoolbox.process.model.Process;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Class able to parse a groovy script and returning the corresponding process.
 *
 * @author Sylvain PALOMINOS
 */

public class ScriptParser {

    /**
     *
     * This method is able to read a WPS script, verify if the annotation ar correctly used.
     * Once done it instantiate the inputs and outputs of the process.
     * Then create and return the process
     * @param scriptAbsolutePath Path to the .groovy file.
     * @return The process from the groovy script.
     */
    public Process parseScript(String scriptAbsolutePath) throws IOException {
        ClassLoader parent = getClass().getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);
        Class groovyClass = loader.parseClass(new File(scriptAbsolutePath));

        Method processMethod = null;

        for(Method m : groovyClass.getDeclaredMethods()){
            for(Annotation a : m.getDeclaredAnnotations()){
                if(a instanceof annotation.Process){
                    processMethod = m;
                }
            }
        }

        if(processMethod == null){
            //throw error because the script if wrong.
        }

        List<Field> output = new ArrayList<>();
        List<Field> input = new ArrayList<>();

        for(Field f : groovyClass.getDeclaredFields()){
            for(Annotation a : f.getDeclaredAnnotations()){
                if(a instanceof annotation.Output){
                    output.add(f);
                }
            }
        }
        for(Field f : groovyClass.getDeclaredFields()){
            for(Annotation a : f.getDeclaredAnnotations()){
                if(a instanceof annotation.Input){
                    input.add(f);
                }
            }
        }
        Field
                f =
                groovyClass.getDeclaredField("stringField");
        for (Annotation a : f.getDeclaredAnnotations()) {
            System.out.println(a);
        }
        f =
                groovyClass.getDeclaredField("test");
        for (Annotation a : f.getDeclaredAnnotations()) {
            System.out.println(a);
            if (a instanceof WpsInput) {
                System.out.println(((WpsInput) a).title());
                System.out.println(((WpsInput) a).abstrac());
                System.out.println(((WpsInput) a).identifier());
                System.out.println(((WpsInput) a).keywords());
                System.out.println(((WpsInput) a).metadata());
            }
        }
        return null;
    }
}
