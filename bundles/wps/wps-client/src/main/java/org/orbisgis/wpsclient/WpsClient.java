package org.orbisgis.wpsclient;

import net.opengis.wps._2_0.ProcessDescriptionType;
import net.opengis.wps._2_0.Result;
import net.opengis.wps._2_0.StatusInfo;
import org.orbisgis.wpsclient.view.utils.WpsJobStateListener;
import org.orbisgis.wpsservice.LocalWpsServer;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

/**
 * Interface that should be implemented by the OrbisGIS wps client.
 *
 * @author Sylvain PALOMINOS
 */
public interface WpsClient {

    /**
     * Refresh the JTree containing the list of the available wps processes.
     */
    void refreshAvailableScripts();

    /**
     * Returns the instance of the localWpsService of OrbisGIS.
     * @return The local instance of the wps service.
     */
    LocalWpsServer getLocalWpsService();

    /**
     * Return the process with the given identifier. If no process is found, return null.
     *
     * @param identifier The process identifier.
     */
    ProcessDescriptionType getInternalProcess(URI identifier);

    /**
     * Build the Execution request, set it and then launch it in the WpsService.
     *
     * @param process The process to execute.
     * @param dataMap Map containing the inputs/outputs.
     */
    StatusInfo executeProcess(ProcessDescriptionType process, Map<URI,Object> dataMap);

    /**
     * Adds a WpsJobListener.
     * @param listener WpsJobListener to add.
     */
    void addJobListener(WpsJobStateListener listener);

    /**
     * Removes a WpsJobListener.
     * @param listener WpsJobListener to remove.
     */
    void removeJobListener(WpsJobStateListener listener);

    /**
     * Ask the WpsService the status of the job corresponding to the given ID.
     * @param jobID UUID of the job.
     * @return The status of a job.
     */
    public StatusInfo getJobStatus(UUID jobID);

    /**
     * Ask the WpsService the result of the job corresponding to the given ID.
     * @param jobID UUID of the job.
     * @return The result of a job.
     */
    public Result getJobResult(UUID jobID);

    /**
     * Ask the WpsService to dismiss the job corresponding to the given ID.
     * @param jobID UUID of the job.
     * @return The status of the job.
     */
    public StatusInfo dismissJob(UUID jobID);

    /**
     * Execute and internal process. The internal process will be tracked by the client and job state will be
     * communicated to the listener.
     * @param process Process to execute.
     * @param dataMap Map of the data for the process execution
     * @param listener WpsJobListener which is listening for the process execution. Can be null.
     */
    public UUID executeInternalProcess(ProcessDescriptionType process, Map<URI, Object> dataMap,
                                       WpsJobStateListener listener);
}
