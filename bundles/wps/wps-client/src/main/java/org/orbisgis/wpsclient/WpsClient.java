/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2016 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.wpsclient;

import net.opengis.wps._2_0.ProcessDescriptionType;
import net.opengis.wps._2_0.Result;
import net.opengis.wps._2_0.StatusInfo;
import org.orbisgis.wpsclient.view.utils.WpsJobStateListener;
import org.orbisgis.wpsclient.view.utils.editor.process.ProcessEditor;
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
     * Open the process UI with the given default values.
     * @param processIdentifier Process identifier of the process to open.
     * @param defaultValuesMap Map of the default values to give to the UI. If their is no default values,
     *                         it should be null;
     */
    void openProcess(URI processIdentifier, Map<URI, Object> defaultValuesMap, ProcessEditor.ProcessExecutionType type);

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
