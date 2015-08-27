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

package org.orbisgis.orbistoolbox.view.utils;

import org.orbisgis.orbistoolbox.controller.processexecution.ExecutionThread;
import org.orbisgis.orbistoolbox.model.Process;
import org.orbisgis.orbistoolbox.view.ToolBox;
import org.orbisgis.orbistoolbox.view.ui.ProcessFrame;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sylvain PALOMINOS
 **/

public class ProcessExecutionData {
    /** Map of input data (URI of the corresponding input) */
    private Map<URI, Object> inputDataMap;
    /** Map of output data (URI of the corresponding output) */
    private List<String> outputDataList;
    /** Process represented */
    private Process process;
    /**State of the process */
    private ProcessState state;
    private ToolBox toolBox;
    private ProcessFrame processFrame;

    public ProcessExecutionData(ToolBox toolBox, Process process){
        this.toolBox = toolBox;
        this.process = process;
        this.outputDataList = new ArrayList<>();
        this.inputDataMap = new HashMap<>();
    }

    public Map<URI, Object> getInputDataMap() {
        return inputDataMap;
    }

    public void setInputDataMap(Map<URI, Object> inputDataMap) {
        this.inputDataMap = inputDataMap;
    }

    public List<String> getOutputDataList() {
        return outputDataList;
    }

    public void setOutputDataList(List<String> outputDataList) {
        this.outputDataList = outputDataList;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public ProcessState getState() {
        return state;
    }

    public void setState(ProcessState state) {
        this.state = state;
    }

    public ProcessFrame getProcessFrame() {
        return processFrame;
    }

    public void setProcessFrame(ProcessFrame processFrame) {
        this.processFrame = processFrame;
    }

    /**
     * Run the process.
     */
    public void runProcess(){
        //Check that all the data field were filled.
        if(inputDataMap.size() == process.getInput().size()) {
            //Run the process in a separated thread
            ExecutionThread thread = new ExecutionThread(process, inputDataMap, toolBox, this);
            thread.start();
        }
    }

    /**
     * Indicated that the process has ended and register the outputs results.
     * @param outputList List of the outputs results.
     */
    public void endProcess(List<String> outputList){
        outputDataList.clear();
        outputDataList.addAll(outputList);
        state = ProcessState.COMPLETED;
        validateProcessExecution();
    }

    /**
     * Validate the process by writing the results in the processFrame.
     * Tells the toolbox that the process execution has been validated.
     * @return True if the process execution has been validated, false otherwise.
     */
    public boolean validateProcessExecution(){
        if(processFrame == null) {
            return false;
        }
        processFrame.setOutputs(outputDataList, ProcessState.COMPLETED.getValue());
        toolBox.validateProcessExecution(this);
        return true;
    }


    public enum ProcessState{
        RUNNING("Running"),
        COMPLETED("Completed"),
        IDLE("Idle");

        private String value;

        ProcessState(String value){
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
