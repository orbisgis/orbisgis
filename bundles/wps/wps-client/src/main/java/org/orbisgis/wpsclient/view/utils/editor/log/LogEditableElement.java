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

package org.orbisgis.wpsclient.view.utils.editor.log;

import org.orbisgis.commons.progress.ProgressMonitor;
import org.orbisgis.sif.edition.EditableElement;
import org.orbisgis.sif.edition.EditableElementException;
import org.orbisgis.wpsclient.view.utils.editor.process.Job;
import org.orbisgis.wpsclient.view.utils.editor.process.ProcessEditableElement;
import org.orbisgis.wpsservice.controller.execution.ProcessExecutionListener;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * EditableElement associated to the LogEditor.
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

    public void addJob(Job job){
        this.jobMap.put(job.getId(), job);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeListenerList.add(listener);
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
        if(event.getPropertyName().equals(ProcessEditableElement.STATE_PROPERTY)){
            firePropertyChange(event);
        }
        if(event.getPropertyName().equals(ProcessEditableElement.LOG_PROPERTY)){
            firePropertyChange(event);
        }
    }

    public void firePropertyChange(PropertyChangeEvent event){
        for(PropertyChangeListener listener : changeListenerList){
            listener.propertyChange(event);
        }
    }

    public void cancelProcess(UUID id) {
        Job job = jobMap.get(id);
        job.firePropertyChangeEvent(new PropertyChangeEvent(this, ProcessEditableElement.CANCEL, id, id));
        job.appendLog(ProcessExecutionListener.LogType.ERROR, I18N.tr("Process cancelled by the user"));
    }
}
