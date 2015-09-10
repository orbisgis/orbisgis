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
import org.orbisgis.orbistoolbox.view.utils.ProcessExecutionData;
import org.orbisgis.orbistoolboxapi.annotations.model.DescriptionTypeAttribute;
import org.orbisgis.orbistoolboxapi.annotations.model.OutputAttribute;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Thread executing a process.
 *
 * @author Sylvain PALOMINOS
 **/

public class ExecutionThread extends Thread{

    /** Process to execute */
    private Process process;
    /** Input data map */
    private Map<URI, Object> inputDataMap;
    /** Output data map */
    private Map<URI, Object> outputDataMap;
    /** ToolBox */
    private ToolBox toolBox;
    /** Data for the process execution */
    private ProcessExecutionData processExecutionData;
    /** Start time*/
    private long startTime;

    /**
     * Main constructor.
     * @param process Process to execute.
     * @param outputDataMap Output data map.
     * @param inputDataMap Input data map.
     * @param toolBox ToolBox.
     * @param processExecutionData Execution data.
     */
    public ExecutionThread(Process process,
                           Map<URI, Object> outputDataMap,
                           Map<URI, Object> inputDataMap,
                           ToolBox toolBox,
                           ProcessExecutionData processExecutionData){
        this.process = process;
        this.inputDataMap = inputDataMap;
        this.outputDataMap = outputDataMap;
        this.toolBox = toolBox;
        this.processExecutionData = processExecutionData;
    }

    @Override
    public void run(){
        startTime = System.currentTimeMillis();
        //Catch all the Exception that can be get on executing the script.
        try {
            processExecutionData.appendLog(System.currentTimeMillis() - startTime,
                    ProcessExecutionData.LogType.INFO,
                    "Start process : " + process.getTitle());
            GroovyObject groovyObject = toolBox.getProcessManager().executeProcess(
                    process, inputDataMap, outputDataMap, toolBox.getProperties());
            List<String> listOutput = new ArrayList<>();
            for (Field field : groovyObject.getClass().getDeclaredFields()) {
                if (field.getAnnotation(OutputAttribute.class) != null) {
                        field.setAccessible(true);
                        URI uri = URI.create(field.getAnnotation(DescriptionTypeAttribute.class).identifier());
                        outputDataMap.remove(uri);
                        outputDataMap.put(uri, field.get(groovyObject));
                        listOutput.add(field.get(groovyObject).toString());
                }
            }
            processExecutionData.appendLog(System.currentTimeMillis() - startTime,
                    ProcessExecutionData.LogType.INFO,
                    "End process : "+process.getTitle());
            processExecutionData.endProcess(listOutput);
        }
        catch (Exception e) {
            processExecutionData.appendLog(System.currentTimeMillis() - startTime,
                    ProcessExecutionData.LogType.ERROR,
                    e.getMessage());
            LoggerFactory.getLogger(ExecutionThread.class).error(e.getMessage());
        }
    }
}
