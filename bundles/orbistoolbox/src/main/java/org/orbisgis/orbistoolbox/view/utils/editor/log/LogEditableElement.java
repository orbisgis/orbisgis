/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 * <p/>
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 * <p/>
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p/>
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 * <p/>
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.orbistoolbox.view.utils.editor.log;

import org.orbisgis.commons.progress.ProgressMonitor;
import org.orbisgis.orbistoolbox.view.utils.editor.process.ProcessEditableElement;
import org.orbisgis.sif.edition.EditableElement;
import org.orbisgis.sif.edition.EditableElementException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * EditableElement associated to the LogEditor.
 *
 * @author Sylvain PALOMINOS
 */
public class LogEditableElement implements EditableElement, PropertyChangeListener {
    /** Unique id of the LogEditableElement. */
    public static final String ID = "LOG_EDITABLE_ELEMENT";
    /** List of ProcessEditableElements displayed by the Editor. */
    private List<ProcessEditableElement> listPee = new ArrayList<>();
    /** List of listeners. */
    private List<PropertyChangeListener> changeListenerList = new ArrayList<>();

    public void addProcessEditableElement(ProcessEditableElement pee){
        listPee.add(pee);
        pee.addPropertyChangeListener(this);
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
        return listPee;
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

    public void cancelProcess(String id) {
        for(ProcessEditableElement pee : listPee){
            if(pee.getId().equals(id)){
                pee.firePropertyChangeEvent(new PropertyChangeEvent(this, ProcessEditableElement.CANCEL, null, null));
            }
        }
    }
}
