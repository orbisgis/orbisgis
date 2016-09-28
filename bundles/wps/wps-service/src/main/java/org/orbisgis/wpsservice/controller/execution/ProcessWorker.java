package org.orbisgis.wpsservice.controller.execution;

import net.opengis.wps._2_0.DescriptionType;
import net.opengis.wps._2_0.ProcessDescriptionType;
import org.orbisgis.wpsservice.controller.utils.Job;
import org.orbisgis.wpsservice.controller.process.ProcessIdentifier;
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
    private Job job;
    /** Process to execute */
    private ProcessIdentifier processIdentifier;
    /** The class managing the DataProcessing classes */
    private DataProcessingManager dataProcessingManager;
    /** The process manager */
    private ProcessManager processManager;
    /** Map containing the process execution output/input data and URI */
    private Map<URI, Object> dataMap;
    /** Map containing the properties to give to the GroovyObject for the execution */
    private Map<String, Object> propertiesMap;

    public ProcessWorker(Job job,
                         ProcessIdentifier processIdentifier,
                         DataProcessingManager dataProcessingManager,
                         ProcessManager processManager,
                         Map<URI, Object> dataMap,
                         Map<String, Object> propertiesMap){
        this.job = job;
        this.processIdentifier = processIdentifier;
        this.dataProcessingManager = dataProcessingManager;
        this.processManager = processManager;
        this.dataMap = dataMap;
        this.propertiesMap = propertiesMap;
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
        if(job != null) {
            job.setStartTime(System.currentTimeMillis());
            job.setProcessState(ProcessExecutionListener.ProcessState.RUNNING);
        }
        Map<URI, Object> stash = new HashMap<>();
        ProcessDescriptionType process = processIdentifier.getProcessDescriptionType();
        //Catch all the Exception that can be thrown during the script execution.
        try {
            //Print in the log the process execution start
            if(job != null) {
                job.appendLog(ProcessExecutionListener.LogType.INFO, "Start the process");
            }

            //Pre-process the data
            if(job != null) {
                job.appendLog(ProcessExecutionListener.LogType.INFO, "Pre-processing");
            }
            for(DescriptionType inputOrOutput : process.getInput()){
                stash.putAll(dataProcessingManager.preProcessData(inputOrOutput, dataMap, job));
            }

            //Execute the process and retrieve the groovy object.
            if(job != null) {
                job.appendLog(ProcessExecutionListener.LogType.INFO, "Execute the script");
            }
            processManager.executeProcess(job.getId(), processIdentifier, dataMap, propertiesMap);

            //Post-process the data
            if(job != null) {
                job.appendLog(ProcessExecutionListener.LogType.INFO, "Post-processing");
            }
            for(DescriptionType inputOrOutput : process.getOutput()){
                dataProcessingManager.postProcessData(inputOrOutput, dataMap, stash, job);
            }
            for(DescriptionType inputOrOutput : process.getInput()){
                dataProcessingManager.postProcessData(inputOrOutput, dataMap, stash, job);
            }

            //Print in the log the process execution end
            if(job != null) {
                job.appendLog(ProcessExecutionListener.LogType.INFO, "End of the process");
                job.setProcessState(ProcessExecutionListener.ProcessState.SUCCEEDED);
            }
        }
        catch (Exception e) {
            if(job != null) {
                job.setProcessState(ProcessExecutionListener.ProcessState.FAILED);
                LoggerFactory.getLogger(ProcessWorker.class).error(e.getLocalizedMessage());
                //Print in the log the process execution error
                job.appendLog(ProcessExecutionListener.LogType.ERROR, e.getMessage());
            }
            else{
                LoggerFactory.getLogger(ProcessWorker.class).error("Error on execution the WPS " +
                        "process '"+process.getTitle()+"'.\n"+e.getMessage());
            }
            for(DescriptionType inputOrOutput : process.getInput()){
                dataProcessingManager.postProcessData(inputOrOutput, dataMap, stash, job);
            }
        }
    }
}
