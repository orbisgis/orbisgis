package org.orbisgis.orbistoolbox.controller.execution;

import org.orbisgis.orbistoolbox.view.utils.editor.process.ProcessEditableElement;

/**
 * Created by sylvain on 05/02/16.
 */
public interface ProcessExecutionListener {

    enum ProcessState{RUNNING, COMPLETED, ERROR, IDLE, WAITING}
    enum LogType{INFO, WARN, ERROR}

    void appendLog(long time, LogType logType, String message);
    void setProcessState(ProcessState processState);
}
