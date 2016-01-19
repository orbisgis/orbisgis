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

package org.orbisgis.orbistoolbox.view.utils.editor.process;

import org.orbisgis.orbistoolbox.model.Process;
import org.orbisgis.commons.progress.ProgressMonitor;
import org.orbisgis.sif.edition.EditableElement;
import org.orbisgis.sif.edition.EditableElementException;

import java.awt.*;
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
public class ProcessEditableElement implements EditableElement{
    public static final String STATE_PROPERTY = "STATE_PROPERTY";
    public static final String LOG_PROPERTY = "LOG_PROPERTY";
    private Process process;
    private boolean isOpen;

    /** Map of input data (URI of the corresponding input) */
    private Map<URI, Object> inputDataMap;
    /** Map of output data (URI of the corresponding output) */
    private Map<URI, Object> outputDataMap;
    /** Map of the log message and their color.*/
    private Map<String, Color> logMap;
    /** List of listeners for the processState*/
    private List<PropertyChangeListener> propertyChangeListenerList;
    /** Unique identifier of this ProcessEditableElement. */
    private final String ID;
    private ProcessState state;

    public ProcessEditableElement(Process process){
        this.process = process;
        this.outputDataMap = new HashMap<>();
        this.inputDataMap = new HashMap<>();
        this.logMap = new LinkedHashMap<>();
        this.propertyChangeListenerList = new ArrayList<>();
        this.ID = process.getTitle()+System.currentTimeMillis();
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
        return ID;
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
        return process;
    }

    public String getProcessReference(){
        return process.getTitle();
    }

    public Map<String, Color> getLogMap(){
        return logMap;
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

    public Process getProcess() {
        return process;
    }
    public ProcessState getProcessState() {
        return state;
    }

    /**
     * Append to the log a new entry.
     * @param time Time since the beginning when it appends.
     * @param logType Type of the message (INFO, WARN, ERROR ...).
     * @param message Message.
     */
    public void appendLog(long time, LogType logType, String message){
        Color color;
        switch(logType){
            case ERROR:
                color = Color.RED;
                break;
            case WARN:
                color = Color.ORANGE;
                break;
            case INFO:
            default:
                color = Color.BLACK;
        }
        Date date = new Date(time - 3600000);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String log = timeFormat.format(date) + " : " + logType.name() + " : " + message + "";
        logMap.put(log, color);
        firePropertyChangeEvent(new PropertyChangeEvent(
                this, LOG_PROPERTY, null, new AbstractMap.SimpleEntry<>(log, color)));
    }

    public void setProcessState(ProcessState processState){
        this.state = processState;
        firePropertyChangeEvent(new PropertyChangeEvent(
                this, STATE_PROPERTY, null, processState));
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

    public enum ProcessState{
        RUNNING("Running"),
        COMPLETED("Completed"),
        ERROR("Error"),
        IDLE("Idle");

        private String value;

        ProcessState(String value){
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum LogType{INFO, WARN, ERROR}
}
