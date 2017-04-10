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
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.orbiswpsservice.controller.utils;

import net.opengis.wps._2_0.ProcessDescriptionType;
import org.orbisgis.orbiswpsservice.execution.ProcessExecutionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Server-side object created by a processing service in response for a particular process execution.
 *
 * @author Sylvain PALOMINOS
 */
public class Job implements ProcessExecutionListener, PropertyChangeListener {

    public static final String PROGRESS_PROPERTY = "progress";
    /** Process polling time in milliseconds. */
    private static final long MAX_PROCESS_POLLING_MILLIS = 10000;
    private static final long BASE_PROCESS_POLLING_MILLIS = 1000;
    /** WPS process */
    private ProcessDescriptionType process;
    /** Unique identifier of the job */
    private UUID id;
    /** Time when the process has been started */
    private long startTime;
    /** Map containing all the log messages */
    private Map<String, LogType> logMap;
    /** State of the process running */
    private ProcessState state;
    /** Map of the input/output data of the process execution */
    private Map<URI, Object> dataMap;
    private long processPollingTime;
    private int progress = 0;

    public Job(ProcessDescriptionType process, UUID id, Map<URI, Object> dataMap){
        this.process = process;
        this.id = id;
        logMap = new HashMap<>();
        state = ProcessState.ACCEPTED;
        this.dataMap = dataMap;
        processPollingTime = BASE_PROCESS_POLLING_MILLIS;
    }

    @Override
    public void setStartTime(long time) {
        startTime = time;
    }

    public long getStartTime(){
        return startTime;
    }

    @Override
    public void appendLog(LogType logType, String message) {
        logMap.put(message, logType);
    }

    @Override
    public void setProcessState(ProcessState processState) {
        state = processState;
    }

    /**
     * Returns the process state.
     * @return The process state.
     */
    public ProcessState getState(){
        return state;
    }

    /**
     * Returns the dataMap.
     * @return The dataMap.
     */
    public Map<URI, Object> getDataMap(){
        return dataMap;
    }

    /**
     * Returns the process.
     * @return The process.
     */
    public ProcessDescriptionType getProcess(){
        return process;
    }

    /**
     * Returns the job id.
     * @return the job id.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns the process polling time.
     * @return The process polling time.
     */
    public long getProcessPollingTime(){
        long time = processPollingTime;
        if(processPollingTime < MAX_PROCESS_POLLING_MILLIS) {
            processPollingTime += BASE_PROCESS_POLLING_MILLIS;
        }
        return time;
    }

    public void setProgress(int i){
        progress = i;
    }

    public int getProgress(){
        return progress;
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if(propertyChangeEvent.getPropertyName().equals(PROGRESS_PROPERTY)){
            setProgress((int)propertyChangeEvent.getNewValue());
        }
    }
}
