/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 * <p/>
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 * <p/>
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p/>
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 * <p/>
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.orbistoolbox.controller.processexecution;

import groovy.lang.GroovyObject;
import org.orbisgis.orbistoolbox.view.ToolBox;
import org.orbisgis.orbistoolbox.model.Process;
import org.orbisgis.orbistoolbox.view.ui.ProcessFrame;
import org.orbisgis.orbistoolbox.view.utils.ProcessUIData;
import org.orbisgis.orbistoolboxapi.annotations.model.OutputAttribute;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Sylvain PALOMINOS
 **/

public class ExecutionThread extends Thread{

    private Process process;
    private Map<URI, Object> dataMap;
    private ToolBox toolBox;
    private ProcessUIData processUIData;

    private List<String> listOutput;

    public ExecutionThread(Process process, Map<URI, Object> dataMap, ToolBox toolBox, ProcessUIData processUIData){
        this.process = process;
        this.dataMap = dataMap;
        this.toolBox = toolBox;
        this.processUIData = processUIData;

        listOutput = new ArrayList<>();
    }

    @Override
    public void run(){
        GroovyObject groovyObject = toolBox.getProcessManager().executeProcess(process, dataMap);
        for (Field field : groovyObject.getClass().getDeclaredFields()) {
            if(field.getAnnotation(OutputAttribute.class) != null) {
                try {
                    field.setAccessible(true);
                    listOutput.add(field.get(groovyObject).toString());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        processUIData.setOutputs(listOutput);
    }
}
