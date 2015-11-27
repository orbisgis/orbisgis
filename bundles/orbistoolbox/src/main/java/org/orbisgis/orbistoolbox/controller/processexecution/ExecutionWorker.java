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
import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.orbistoolbox.view.ToolBox;
import org.orbisgis.orbistoolbox.model.Process;
import org.orbisgis.orbistoolbox.view.utils.ProcessExecutionData;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Thread executing a process.
 *
 * @author Sylvain PALOMINOS
 **/

public class ExecutionWorker extends SwingWorkerPM{

    /** Process to execute */
    private Process process;
    /** Input and output data map */
    private Map<URI, Object> dataMap;
    /** ToolBox */
    private ToolBox toolBox;
    /** Data for the process execution */
    private ProcessExecutionData processExecutionData;
    /** GroovyObject of the process execution */
    private GroovyObject groovyObject;

    /**
     * Main constructor.
     * @param process Process to execute.
     * @param outputDataMap Output data map.
     * @param inputDataMap Input data map.
     * @param toolBox ToolBox.
     * @param processExecutionData Execution data.
     */
    public ExecutionWorker(Process process,
                           Map<URI, Object> outputDataMap,
                           Map<URI, Object> inputDataMap,
                           ToolBox toolBox,
                           ProcessExecutionData processExecutionData){
        this.process = process;
        this.dataMap = new HashMap<>();
        this.dataMap.putAll(inputDataMap);
        this.dataMap.putAll(outputDataMap);
        this.toolBox = toolBox;
        this.processExecutionData = processExecutionData;
    }

    @Override
    protected Object doInBackground() throws Exception {
        long startTime = System.currentTimeMillis();
        //Catch all the Exception that can be get on executing the script.
        try {
            //Print in the log the process execution start
            processExecutionData.appendLog(System.currentTimeMillis() - startTime,
                    ProcessExecutionData.LogType.INFO,
                    "Start process : " + process.getTitle());

            //Execute the process and retrieve the groovy object.
            groovyObject = toolBox.getProcessManager().executeProcess(
                    process, dataMap, toolBox.getProperties());

            //Print in the log the process execution end
            processExecutionData.appendLog(System.currentTimeMillis() - startTime,
                    ProcessExecutionData.LogType.INFO,
                    "End process : " + process.getTitle());
        }
        catch (Exception e) {
            processExecutionData.setState(ProcessExecutionData.ProcessState.ERROR);
            //Print in the log the process execution error
            processExecutionData.appendLog(System.currentTimeMillis() - startTime,
                    ProcessExecutionData.LogType.ERROR,
                    e.getMessage());
            LoggerFactory.getLogger(ExecutionWorker.class).error(e.getMessage());
        }
        return null;
    }

    @Override
    protected void done(){
        //Retrieve the executed process output data
        List<String> listOutput = new ArrayList<>();
        for(Map.Entry<URI, Object> entry : dataMap.entrySet()){
            if(entry.getKey().toString().contains("output")) {
                listOutput.add(entry.getValue().toString());
            }
        }
        //Print in the log the process end the process
        processExecutionData.endProcess(listOutput);
    }

    public ToolBox getToolBox(){
        return toolBox;
    }

    public GroovyObject getGroovyObject(){
        return groovyObject;
    }

    public Process getProcess(){
        return process;
    }

    public Map<URI, Object> getDataMap(){
        return dataMap;
    }
}
