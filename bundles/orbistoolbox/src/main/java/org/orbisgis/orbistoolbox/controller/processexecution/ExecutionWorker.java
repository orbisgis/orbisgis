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

import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.orbistoolbox.model.DescriptionType;
import org.orbisgis.orbistoolbox.view.ToolBox;
import org.orbisgis.orbistoolbox.model.Process;
import org.orbisgis.orbistoolbox.view.utils.editor.process.ProcessEditableElement;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Swing worker executing a process.
 * This worker execute a WPS process in three steps:
 *  - Pre-process the input/output : Some input/output need to be prepared before been used for the execution.
 *  - Execute : execute the process by running the Groovy script.
 *  - Post-process input/output : Some input/output need to be processed to be on the good form
 *      (like exporting a DataStore in the CSV file format.)
 * It uses the ProcessEditableElement to communicates the execution log and process state to the rest of the ToolBox.
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

    /**
     * Main constructor.
     * @param pee ProcessEditableElement which will be used to communicate the state and the log of the process.
     * @param toolBox ToolBox.
     */
    public ExecutionWorker(ProcessEditableElement pee,ToolBox toolBox){
        this.pee = pee;
        this.process = pee.getProcess();
        this.dataMap = new HashMap<>();
        this.dataMap.putAll(pee.getInputDataMap());
        this.dataMap.putAll(pee.getOutputDataMap());
        this.toolBox = toolBox;
    }

    @Override
    protected Object doInBackground() throws Exception {
        long startTime = System.currentTimeMillis();
        //Catch all the Exception that can be thrown during the script execution.
        try {
            //Print in the log the process execution start
            pee.appendLog(System.currentTimeMillis() - startTime,
                    ProcessEditableElement.LogType.INFO,
                    "Start the process");

            //Pre-process the data
            pee.appendLog(System.currentTimeMillis() - startTime,
                    ProcessEditableElement.LogType.INFO,
                    "Pre-processing");
            Map<URI, Object> stash = new HashMap<>();
            for(DescriptionType inputOrOutput : pee.getProcess().getOutput()){
                stash.putAll(toolBox.getDataProcessingManager().preProcessData(inputOrOutput, dataMap));
            }
            for(DescriptionType inputOrOutput : pee.getProcess().getInput()){
                stash.putAll(toolBox.getDataProcessingManager().preProcessData(inputOrOutput, dataMap));
            }

            //Execute the process and retrieve the groovy object.
            pee.appendLog(System.currentTimeMillis() - startTime,
                    ProcessEditableElement.LogType.INFO,
                    "Execute the script");
            toolBox.getProcessManager().executeProcess(process, dataMap, toolBox.getProperties());

            //Post-process the data
            pee.appendLog(System.currentTimeMillis() - startTime,
                    ProcessEditableElement.LogType.INFO,
                    "Post-processing");
            for(DescriptionType inputOrOutput : pee.getProcess().getOutput()){
                toolBox.getDataProcessingManager().postProcessData(inputOrOutput, dataMap, stash);
            }
            for(DescriptionType inputOrOutput : pee.getProcess().getInput()){
                toolBox.getDataProcessingManager().postProcessData(inputOrOutput, dataMap, stash);
            }

            //Print in the log the process execution end
            pee.appendLog(System.currentTimeMillis() - startTime,
                    ProcessEditableElement.LogType.INFO,
                    "End of the process");
            pee.setProcessState(ProcessEditableElement.ProcessState.COMPLETED);
        }
        catch (Exception e) {
            pee.setProcessState(ProcessEditableElement.ProcessState.ERROR);
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
    }

}
