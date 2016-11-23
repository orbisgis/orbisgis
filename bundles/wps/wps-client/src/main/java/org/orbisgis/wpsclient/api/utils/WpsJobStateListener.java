package org.orbisgis.wpsclient.api.utils;

import java.util.UUID;

/**
 * This interface defines an object listening for the state changes of an executed job.
 * The methods onJobAccepted and onJobRunning might not be called because the of the refresh state of the job. A state
 * might no be seen by the client(i.e.
 *
 * @author Sylvain PALOMINOS
 */
public interface WpsJobStateListener {

    /**
     * Returns the id of the listened job.
     * @return The id of the listened job.
     */
    UUID getJobID();

    /**
     * Method called when the job has been accepted.
     */
    void onJobAccepted();

    /**
     * Method called when the job starts running.
     */
    void onJobRunning();

    /**
     * Method called when the job has end with success.
     */
    void onJobSuccess();

    /**
     * Method called when the job has end with failure.
     */
    void onJobFailed();
}
