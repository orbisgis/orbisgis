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
import org.orbisgis.orbistoolbox.model.DescriptionType;
import org.orbisgis.orbistoolbox.view.ToolBox;
import org.orbisgis.orbistoolbox.model.Process;
import org.orbisgis.orbistoolbox.view.utils.ProcessEditableElement;
import org.orbisgis.orbistoolbox.view.utils.ProcessEditor;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
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
    /** Process element containing all the information it */
    private ProcessEditableElement pee;
    /** GroovyObject of the process execution */
    private GroovyObject groovyObject;
    /** Process UI */
    private ProcessEditor processEditor;

    /**
     * Main constructor.
     * @param pee ProcessEditableElement.
     * @param toolBox ToolBox.
     */
    public ExecutionWorker(ProcessEditableElement pee,ToolBox toolBox, ProcessEditor processEditor){
        this.pee = pee;
        this.process = pee.getProcess();
        this.dataMap = new HashMap<>();
        this.dataMap.putAll(pee.getInputDataMap());
        this.dataMap.putAll(pee.getOutputDataMap());
        this.toolBox = toolBox;
        this.processEditor = processEditor;
    }

    @Override
    protected Object doInBackground() throws Exception {
        long startTime = System.currentTimeMillis();
        //Catch all the Exception that can be get on executing the script.
        try {
            //Print in the log the process execution start
            pee.appendLog(System.currentTimeMillis() - startTime,
                    ProcessEditableElement.LogType.INFO,
                    "Start process : " + process.getTitle());

            //pre process the data
            pee.appendLog(System.currentTimeMillis() - startTime,
                    ProcessEditableElement.LogType.INFO,
                    "preProcess : " + process.getTitle());
            for(DescriptionType inputOrOutput : pee.getProcess().getOutput()){
                toolBox.getDataProcessingManager().preProcessData(inputOrOutput, dataMap);
            }
            for(DescriptionType inputOrOutput : pee.getProcess().getInput()){
                toolBox.getDataProcessingManager().preProcessData(inputOrOutput, dataMap);
            }

            //Execute the process and retrieve the groovy object.
            groovyObject = toolBox.getProcessManager().executeProcess(
                    process, dataMap, toolBox.getProperties());

            //post process the data
            pee.appendLog(System.currentTimeMillis() - startTime,
                    ProcessEditableElement.LogType.INFO,
                    "postProcess : " + process.getTitle());
            for(DescriptionType inputOrOutput : pee.getProcess().getOutput()){
                toolBox.getDataProcessingManager().postProcessData(inputOrOutput, dataMap);
            }
            for(DescriptionType inputOrOutput : pee.getProcess().getInput()){
                toolBox.getDataProcessingManager().postProcessData(inputOrOutput, dataMap);
            }

            //Print in the log the process execution end
            pee.appendLog(System.currentTimeMillis() - startTime,
                    ProcessEditableElement.LogType.INFO,
                    "End process : " + process.getTitle());
        }
        catch (Exception e) {
            pee.setState(ProcessEditableElement.ProcessState.ERROR);
            //Print in the log the process execution error
            pee.appendLog(System.currentTimeMillis() - startTime,
                    ProcessEditableElement.LogType.ERROR,
                    e.getMessage());
            LoggerFactory.getLogger(ExecutionWorker.class).error(e.getMessage());
        }
        return null;
    }

    @Override
    protected void done(){
        //Retrieve the executed process output data
        Map<URI, Object> mapOutput = new HashMap<>();
        for(Map.Entry<URI, Object> entry : dataMap.entrySet()){
            if(entry.getKey().toString().contains("output")) {
                mapOutput.put(entry.getKey(), entry.getValue());
            }
        }
        //Print in the log the process end the process
        processEditor.endProcess(mapOutput);
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
