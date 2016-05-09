package org.orbisgis.wpsservice;

import net.opengis.wps.v_2_0.ProcessDescriptionType;
import org.orbisgis.wpsservice.controller.execution.ProcessExecutionListener;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Sylvain PALOMINOS
 */
public class Job implements ProcessExecutionListener {

    private ProcessDescriptionType process;
    private UUID id;
    private long startTime;
    private Map<String, LogType> logMap;
    private ProcessState state;
    private Map<URI, Object> dataMap;

    public Job(ProcessDescriptionType process, UUID id, Map<URI, Object> dataMap){
        this.process = process;
        this.id = id;
        logMap = new HashMap<>();
        state = ProcessState.ACCEPTED;
        this.dataMap = dataMap;
    }

    @Override
    public void setStartTime(long time) {
        startTime = time;
    }

    @Override
    public void appendLog(LogType logType, String message) {
        logMap.put(message, logType);
    }

    @Override
    public void setProcessState(ProcessState processState) {
        state = processState;
    }

    public ProcessState getState(){
        return state;
    }

    public Map<URI, Object> getDataMap(){
        return dataMap;
    }

    public ProcessDescriptionType getProcess(){
        return process;
    }
}
