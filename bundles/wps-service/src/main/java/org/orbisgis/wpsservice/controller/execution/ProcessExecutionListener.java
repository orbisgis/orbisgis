package org.orbisgis.wpsservice.controller.execution;

/**
 * @author Sylvain PALOMINOS
 */
public interface ProcessExecutionListener {

    enum ProcessState{RUNNING, COMPLETED, ERROR, IDLE, WAITING}
    enum LogType{INFO, WARN, ERROR}

    void appendLog(long time, LogType logType, String message);
    void setProcessState(ProcessState processState);
}
