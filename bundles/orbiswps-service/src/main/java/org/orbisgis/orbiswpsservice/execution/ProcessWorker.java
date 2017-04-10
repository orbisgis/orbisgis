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
package org.orbisgis.orbiswpsservice.execution;

import net.opengis.wps._2_0.ProcessDescriptionType;
import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.orbiswpsservice.controller.utils.Job;
import org.orbisgis.orbiswpsservice.controller.process.ProcessIdentifier;
import org.orbisgis.orbiswpsservice.controller.process.ProcessManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Class extending the SwingWorkerPM class dedicated to the WPS process execution.
 *
 * @author Sylvain PALOMINOS
 */
public class ProcessWorker extends SwingWorkerPM {

    /** One hundred */
    private static final long ONE_HUNDRED = 100;

    /** Process execution listener which will be watching the execution */
    private Job job;
    /** Process to execute */
    private ProcessIdentifier processIdentifier;
    /** The process manager */
    private ProcessManager processManager;
    /** Map containing the process execution output/input data and URI */
    private Map<URI, Object> dataMap;
    /** Map containing the properties to give to the GroovyObject for the execution */
    private Map<String, Object> propertiesMap;
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(ProcessWorker.class);
    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessWorker.class);

    public ProcessWorker(Job job,
                         ProcessIdentifier processIdentifier,
                         ProcessManager processManager,
                         Map<URI, Object> dataMap,
                         Map<String, Object> propertiesMap){
        super(job.getProcess().getTitle().get(0).getValue(), ONE_HUNDRED);
        this.job = job;
        this.addPropertyChangeListener(Job.PROGRESS_PROPERTY, this.job);
        this.processIdentifier = processIdentifier;
        this.processManager = processManager;
        this.dataMap = dataMap;
        this.propertiesMap = propertiesMap;
    }

    /**
     * Returns the data Map.
     * @return The data Map.
     */
    public Map<URI, Object> getDataMap(){
        return dataMap;
    }

    @Override
    public Object doInBackground() {
        String title = job.getProcess().getTitle().get(0).getValue();
        this.setTaskName(I18N.tr("{0} : Preprocessing", title));
        if(job != null) {
            job.setStartTime(System.currentTimeMillis());
            job.setProcessState(ProcessExecutionListener.ProcessState.RUNNING);
        }
        Map<URI, Object> stash = new HashMap<>();
        ProcessDescriptionType process = processIdentifier.getProcessDescriptionType();
        //Catch all the Exception that can be thrown during the script execution.
        try {
            //Print in the log the process execution start
            if(job != null) {
                job.appendLog(ProcessExecutionListener.LogType.INFO, I18N.tr("Start the process."));
            }

            //Pre-process the data
            if(job != null) {
                job.appendLog(ProcessExecutionListener.LogType.INFO, I18N.tr("Pre-processing."));
            }

            //Execute the process and retrieve the groovy object.
            if(job != null) {
                job.appendLog(ProcessExecutionListener.LogType.INFO, I18N.tr("Execute the script."));
            }
            this.setTaskName(I18N.tr("{0} : Execution", title));
            processManager.executeProcess(job.getId(), processIdentifier, dataMap, propertiesMap, this.getProgressMonitor());

            this.setTaskName(I18N.tr("{0} : Postprocessing", title));
            //Post-process the data
            if(job != null) {
                job.appendLog(ProcessExecutionListener.LogType.INFO, I18N.tr("Post-processing."));
            }

            //Print in the log the process execution end
            if(job != null) {
                job.appendLog(ProcessExecutionListener.LogType.INFO, I18N.tr("End of the process."));
                job.setProcessState(ProcessExecutionListener.ProcessState.SUCCEEDED);
            }
        }
        catch (Exception e) {
            if(job != null) {
                job.setProcessState(ProcessExecutionListener.ProcessState.FAILED);
                LOGGER.error(e.getLocalizedMessage());
                //Print in the log the process execution error
                job.appendLog(ProcessExecutionListener.LogType.ERROR, e.getMessage());
            }
            else{
                LOGGER.error(I18N.tr("Error on execution the WPS  process {0}.\nCause : {1}.",
                        process.getTitle(),e.getMessage()));
            }
        }
        return null;
    }

    @Override
    public void cancel() {
        processManager.cancelProcess(job.getId());
        super.cancel();
    }
}
