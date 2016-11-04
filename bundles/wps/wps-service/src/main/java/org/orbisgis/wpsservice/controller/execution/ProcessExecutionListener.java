package org.orbisgis.wpsservice.controller.execution;

/**
 * @author Sylvain PALOMINOS
 */
public interface ProcessExecutionListener {

    enum ProcessState{RUNNING, SUCCEEDED, FAILED, ACCEPTED, IDLE}
    enum LogType{INFO, WARN, ERROR}

    void setStartTime(long time);
    void appendLog(LogType logType, String message);
    void setProcessState(ProcessState processState);
}
