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

    public List<ProcessIdentifier> getCapabilities();

    public Process describeProcess(URI uri);

    public void execute(Process process, Map<URI, Object> dataMap, ProcessExecutionListener pel);
}
