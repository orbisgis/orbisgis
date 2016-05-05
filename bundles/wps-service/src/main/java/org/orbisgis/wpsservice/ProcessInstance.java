package org.orbisgis.wpsservice;

import net.opengis.wps.v_2_0.ProcessDescriptionType;
import org.orbisgis.wpsservice.controller.execution.ProcessExecutionListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Sylvain PALOMINOS
 */
public class ProcessInstance implements ProcessExecutionListener {

    private ProcessDescriptionType process;
    private UUID id;
    private long startTime;
    private Map<String, LogType> logMap;
    private ProcessState state;

    public ProcessInstance(ProcessDescriptionType process, UUID id){
        this.process = process;
        this.id = id;
        logMap = new HashMap<>();
        state = ProcessState.IDLE;
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
}
