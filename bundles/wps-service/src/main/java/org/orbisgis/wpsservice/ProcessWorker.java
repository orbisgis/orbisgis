package org.orbisgis.wpsservice;

import net.opengis.wps._2_0.DescriptionType;
import net.opengis.wps._2_0.ProcessDescriptionType;
import org.orbisgis.wpsservice.controller.execution.DataProcessingManager;
import org.orbisgis.wpsservice.controller.execution.ProcessExecutionListener;
import org.orbisgis.wpsservice.controller.process.ProcessManager;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Class implementing the Runnable interface is dedicated to the WPS process execution.
 *
 * @author Sylvain PALOMINOS
 */
public class ProcessWorker implements Runnable {

    /** Process execution listener which will be watching the execution */
    private ProcessExecutionListener pel;
    /** Process to execute */
    private ProcessDescriptionType process;
    /** The class managing the DataProcessing classes */
    private DataProcessingManager dataProcessingManager;
    /** The process manager */
    private ProcessManager processManager;
    /** Map containing the process execution output/input data and URI */
    private Map<URI, Object> dataMap;

    public ProcessWorker(ProcessExecutionListener pel,
                         ProcessDescriptionType process,
                         DataProcessingManager dataProcessingManager,
                         ProcessManager processManager,
                         Map<URI, Object> dataMap){
        this.pel = pel;
        this.process = process;
        this.dataProcessingManager = dataProcessingManager;
        this.processManager = processManager;
        this.dataMap = dataMap;
    }

    /**
     * Returns the data Map.
     * @return The data Map.
     */
    public Map<URI, Object> getDataMap(){
        return dataMap;
    }

    @Override
    public void run() {
        if(pel != null) {
            pel.setStartTime(System.currentTimeMillis());
        }
        Map<URI, Object> stash = new HashMap<>();
        //Catch all the Exception that can be thrown during the script execution.
        try {
            //Print in the log the process execution start
            if(pel != null) {
                pel.appendLog(ProcessExecutionListener.LogType.INFO, "Start the process");
            }

            //Pre-process the data
            if(pel != null) {
                pel.appendLog(ProcessExecutionListener.LogType.INFO, "Pre-processing");
            }
            for(DescriptionType inputOrOutput : process.getOutput()){
                stash.putAll(dataProcessingManager.preProcessData(inputOrOutput, dataMap, pel));
            }
            for(DescriptionType inputOrOutput : process.getInput()){
                stash.putAll(dataProcessingManager.preProcessData(inputOrOutput, dataMap, pel));
            }

            //Execute the process and retrieve the groovy object.
            if(pel != null) {
                pel.appendLog(ProcessExecutionListener.LogType.INFO, "Execute the script");
            }
            processManager.executeProcess(process, dataMap);

            //Post-process the data
            if(pel != null) {
                pel.appendLog(ProcessExecutionListener.LogType.INFO, "Post-processing");
            }
            for(DescriptionType inputOrOutput : process.getOutput()){
                dataProcessingManager.postProcessData(inputOrOutput, dataMap, stash, pel);
            }
            for(DescriptionType inputOrOutput : process.getInput()){
                dataProcessingManager.postProcessData(inputOrOutput, dataMap, stash, pel);
            }

            //Print in the log the process execution end
            if(pel != null) {
                pel.appendLog(ProcessExecutionListener.LogType.INFO, "End of the process");
                pel.setProcessState(ProcessExecutionListener.ProcessState.SUCCEEDED);
            }
        }
        catch (Exception e) {
            if(pel != null) {
                //Print in the log the process execution error
                pel.appendLog(ProcessExecutionListener.LogType.ERROR, e.getMessage());
                //Post-process the data
                pel.appendLog(ProcessExecutionListener.LogType.INFO, "Post-processing");
                pel.setProcessState(ProcessExecutionListener.ProcessState.FAILED);
            }
            else{
                LoggerFactory.getLogger(ProcessWorker.class).error("Error on execution the WPS " +
                        "process '"+process.getTitle()+"'.\n"+e.getMessage());
            }
            for(DescriptionType inputOrOutput : process.getInput()){
                dataProcessingManager.postProcessData(inputOrOutput, dataMap, stash, pel);
            }
        }
    }
}
