package org.orbisgis.wpsservice;

import org.orbisgis.wpsservice.controller.execution.ProcessExecutionListener;
import org.orbisgis.wpsservice.controller.process.ProcessIdentifier;
import org.orbisgis.wpsservice.model.Process;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * @author Sylvain PALOMINOS
 */
public interface WpsService {

    List<ProcessIdentifier> getCapabilities();

    Process describeProcess(URI uri);

    /**
     * Execute the given process with the given dataMap.
     * The process results will be put into the dataMap.
     * The process execution will be log in the processExecutionListener (which ca be null).
     * @param process Process to execute.
     * @param dataMap DataMap containing all the data (input and output)
     * @param pel Process executionListener used to log the process execution (can be null).
     */
    void execute(Process process, Map<URI, Object> dataMap, ProcessExecutionListener pel);
}
