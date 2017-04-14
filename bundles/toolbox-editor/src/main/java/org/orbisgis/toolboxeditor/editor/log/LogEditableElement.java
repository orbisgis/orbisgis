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

package org.orbisgis.toolboxeditor.editor.log;

import org.orbisgis.commons.progress.ProgressMonitor;
import org.orbisgis.sif.edition.EditableElement;
import org.orbisgis.sif.edition.EditableElementException;
import org.orbisgis.toolboxeditor.utils.Job;
import org.orbiswps.server.execution.ProcessExecutionListener;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import static org.orbisgis.toolboxeditor.utils.Job.*;

/**
 * EditableElement associated to the LogEditor. It is used to communicate to the LogEditor the changes in the job state
 * and process execution.
 *
 * @author Sylvain PALOMINOS
 */
public class LogEditableElement implements EditableElement, PropertyChangeListener {
    /** Unique id of the LogEditableElement. */
    public static final String ID = "LOG_EDITABLE_ELEMENT";
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(LogEditableElement.class);
    /** List of ProcessEditableElements displayed by the Editor. */
    private Map<UUID, Job> jobMap = new HashMap<>();
    /** List of listeners. */
    private List<PropertyChangeListener> changeListenerList = new ArrayList<>();

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if(!changeListenerList.contains(listener)) {
            changeListenerList.add(listener);
        }
    }

    @Override
    public void addPropertyChangeListener(String prop, PropertyChangeListener listener) {
        changeListenerList.add(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeListenerList.remove(listener);
    }

    @Override
    public void removePropertyChangeListener(String prop, PropertyChangeListener listener) {
        changeListenerList.remove(listener);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void setModified(boolean modified) {
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public String getTypeId() {
        return null;
    }

    @Override
    public void open(ProgressMonitor progressMonitor) throws UnsupportedOperationException, EditableElementException {
    }

    @Override
    public void save() throws UnsupportedOperationException, EditableElementException {

    }

    @Override
    public void close(ProgressMonitor progressMonitor) throws UnsupportedOperationException, EditableElementException {
    }

    @Override
    public Object getObject() throws UnsupportedOperationException {
        return jobMap;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if(event.getPropertyName().equals(STATE_PROPERTY)){
            firePropertyChange(event);
        }
        if(event.getPropertyName().equals(LOG_PROPERTY)){
            firePropertyChange(event);
        }
        if(event.getPropertyName().equals(PERCENT_COMPLETED_PROPERTY)){
            firePropertyChange(event);
        }
    }

    public void firePropertyChange(PropertyChangeEvent event){
        for(PropertyChangeListener listener : changeListenerList){
            listener.propertyChange(event);
        }
    }

    /**
     * Fire the property event for the cancelling of the given process.
     * @param id Id of the process
     */
    public void cancelProcess(UUID id) {
        Job job = jobMap.get(id);
        job.firePropertyChangeEvent(new PropertyChangeEvent(this, CANCEL, id, id));
        job.appendLog(ProcessExecutionListener.LogType.ERROR, I18N.tr("Process cancelled by the user"));
    }

    /**
     * Removes a process. After the LogEditableElement won't communicate any Job state change.
     * @param id Id of the job to remove.
     */
    public void removeProcess(UUID id) {
        jobMap.remove(id);
    }

    /**
     * Adds a job. The future Job state changes will be communicated to the listeners.
     * @param job Job to follow.
     */
    public void addJob(Job job){
        this.jobMap.put(job.getId(), job);
    }
}
