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

package org.orbisgis.wpsclient.view.utils.editor.process;

import net.opengis.wps._2_0.*;
import org.orbisgis.commons.progress.ProgressMonitor;
import org.orbisgis.sif.edition.EditableElement;
import org.orbisgis.sif.edition.EditableElementException;
import org.orbisgis.wpsservice.controller.execution.ProcessExecutionListener;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * EditableElement of a process which contains all the information about a process instance
 * (input data, output data, state ...).
 *
 * @author Sylvain PALOMINOS
 */

public class ProcessEditableElement implements EditableElement {
    public static final String STATE_PROPERTY = "STATE_PROPERTY";
    public static final String LOG_PROPERTY = "LOG_PROPERTY";
    public static final String CANCEL = "CANCEL";
    public static final String REFRESH_STATUS = "REFRESH_STATUS";
    public static final String GET_RESULTS = "GET_RESULTS";
    private ProcessOffering processOffering;
    private boolean isOpen;

    /** List of listeners for the processState*/
    private List<PropertyChangeListener> propertyChangeListenerList;
    private Map<UUID, Job> jobMap;
    private Map<URI, Object> inputDataMap;
    private Map<URI, Object> outputDataMap;

    public ProcessEditableElement(ProcessOffering processOffering){
        this.processOffering = processOffering;
        this.propertyChangeListenerList = new ArrayList<>();
        this.jobMap = new HashMap<>();
        this.outputDataMap = new HashMap<>();
        this.inputDataMap = new HashMap<>();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeListenerList.add(listener);
    }

    @Override
    public void addPropertyChangeListener(String prop, PropertyChangeListener listener) {
        propertyChangeListenerList.add(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeListenerList.remove(listener);
    }

    @Override
    public void removePropertyChangeListener(String prop, PropertyChangeListener listener) {
        propertyChangeListenerList.remove(listener);
    }

    @Override
    public String getId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void setModified(boolean modified) {}

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public String getTypeId() {
        return null;
    }

    @Override
    public void open(ProgressMonitor progressMonitor) throws UnsupportedOperationException, EditableElementException {
        isOpen = true;
    }

    @Override
    public void save() throws UnsupportedOperationException, EditableElementException {

    }

    @Override
    public void close(ProgressMonitor progressMonitor) throws UnsupportedOperationException, EditableElementException {
        isOpen = false;
    }

    @Override
    public Object getObject() throws UnsupportedOperationException {
        return processOffering;
    }

    public String getProcessReference(){
        return processOffering.getProcess().getTitle().get(0).getValue();
    }

    public Map<String, Color> getLogMap(UUID jobId){
        return jobMap.get(jobId).getLogMap();
    }

    public Map<URI, Object> getInputDataMap() {
        return inputDataMap;
    }

    public void setInputDataMap(Map<URI, Object> inputDataMap) {
        this.inputDataMap = inputDataMap;
    }

    public Map<URI, Object> getOutputDataMap() {
        return outputDataMap;
    }

    public void setOutputDataMap(Map<URI, Object> outputDataMap) {
        this.outputDataMap = outputDataMap;
    }

    public ProcessDescriptionType getProcess() {
        return processOffering.getProcess();
    }

    public ProcessOffering getProcessOffering() {
        return processOffering;
    }

    public ProcessExecutionListener.ProcessState getProcessState(UUID jobId) {
        return jobMap.get(jobId).getState();
    }

    public void firePropertyChangeEvent(PropertyChangeEvent event){
        for(PropertyChangeListener pcl : propertyChangeListenerList){
            pcl.propertyChange(event);
        }
    }

    public void setDefaultInputValues(Map<URI,Object> defaultInputValues) {
        for(Map.Entry<URI, Object> entry : defaultInputValues.entrySet()){
            inputDataMap.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Throw a property change event to ask the result of the job.
     */
    public void askResults(UUID jobId) {
        PropertyChangeEvent event = new PropertyChangeEvent(this, GET_RESULTS, jobId, jobId);
        firePropertyChangeEvent(event);
    }

    public Job getJob(UUID jobID) {
        if(jobMap.containsKey(jobID)) {
            return jobMap.get(jobID);
        }
        else{
            return null;
        }
    }

    public Job newJob(UUID jobId) {
        Job job = new Job(this, jobId);
        this.jobMap.put(jobId, job);
        return job;
    }
}
