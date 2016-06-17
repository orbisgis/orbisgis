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

package org.orbisgis.wpsclient.view.utils.editor.process;

import net.opengis.wps._2_0.DataOutputType;
import net.opengis.wps._2_0.ProcessDescriptionType;
import net.opengis.wps._2_0.ProcessOffering;
import net.opengis.wps._2_0.Result;
import org.orbisgis.commons.progress.ProgressMonitor;
import org.orbisgis.sif.edition.EditableElement;
import org.orbisgis.sif.edition.EditableElementException;
import org.orbisgis.wpsservice.controller.execution.ProcessExecutionListener;

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
public class ProcessEditableElement implements EditableElement, ProcessExecutionListener {
    public static final String STATE_PROPERTY = "STATE_PROPERTY";
    public static final String LOG_PROPERTY = "LOG_PROPERTY";
    public static final String CANCEL = "CANCEL";
    public static final String REFRESH_STATUS = "REFRESH_STATUS";
    public static final String GET_RESULTS = "GET_RESULTS";
    private ProcessOffering processOffering;
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
    private ProcessExecutionListener.ProcessState state;
    private long startTime;
    private UUID jobID;
    private Timer statusTimer;

    public ProcessEditableElement(ProcessOffering processOffering){
        this.processOffering = processOffering;
        this.outputDataMap = new HashMap<>();
        this.inputDataMap = new HashMap<>();
        this.logMap = new LinkedHashMap<>();
        this.propertyChangeListenerList = new ArrayList<>();
        this.ID = UUID.randomUUID().toString();
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
        return processOffering;
    }

    public String getProcessReference(){
        return processOffering.getProcess().getTitle().get(0).getValue();
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

    public ProcessDescriptionType getProcess() {
        return processOffering.getProcess();
    }

    public ProcessOffering getProcessOffering() {
        return processOffering;
    }

    public ProcessExecutionListener.ProcessState getProcessState() {
        return state;
    }

    public void setStartTime(long time){
        startTime = time + 3600000;
    }

    /**
     * Set the date when it should ask again the process execution job status to the WpsService.
     * @param date Date when the state should be asked.
     */
    public void setRefreshDate(XMLGregorianCalendar date){
        //If the time is already running stop it
        if(statusTimer != null && statusTimer.isRunning()){
            statusTimer.stop();
        }
        //If there is a new date, launch a timer
        if(date != null) {
            long delta = date.toGregorianCalendar().getTime().getTime() - new Date().getTime();
            if (delta <= 0) {
                delta = 1;
            }
            statusTimer = new Timer((int) delta, EventHandler.create(ActionListener.class, this, "askStatusRefresh"));
            statusTimer.setRepeats(false);
            statusTimer.start();
        }
    }

    /**
     * Fire an event to ask the refreshing of the status.
     */
    public void askStatusRefresh(){
        PropertyChangeEvent event = new PropertyChangeEvent(this, REFRESH_STATUS, null, null);
        firePropertyChangeEvent(event);
    }

    /**
     * Append to the log a new entry.
     * @param logType Type of the message (INFO, WARN, FAILED ...).
     * @param message Message.
     */
    public void appendLog(ProcessExecutionListener.LogType logType, String message){
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
        Date date = new Date(System.currentTimeMillis() - startTime);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String log = timeFormat.format(date) + " : " + logType.name() + " : " + message + "";
        logMap.put(log, color);
        firePropertyChangeEvent(new PropertyChangeEvent(
                this, LOG_PROPERTY, null, new AbstractMap.SimpleEntry<>(log, color)));
    }

    public void setProcessState(ProcessExecutionListener.ProcessState processState) {
        this.state = processState;
        if (state.equals(ProcessState.FAILED)) {
            appendLog(LogType.ERROR, state.toString());
        } else {
            appendLog(LogType.INFO, state.toString());
        }
        //If the process has ended with success, retrieve the results.
        //The firing of the process state change will be done later
        if (state.equals(ProcessState.SUCCEEDED)) {
            askResults();
        }
        //Else, fire the change of the process state.
        else {
            firePropertyChangeEvent(new PropertyChangeEvent(this, STATE_PROPERTY, null, state));
        }
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
     * Sets the jobID of the running process.
     * @param jobID The job ID.
     */
    public void setJobID(UUID jobID) {
        this.jobID = jobID;
    }

    /**
     * Returns the job ID of the running process.
     * @return The job ID.
     */
    public UUID getJobID(){
        return jobID;
    }

    /**
     * Throw a property change event to ask the result of the job.
     */
    public void askResults() {
        PropertyChangeEvent event = new PropertyChangeEvent(this, GET_RESULTS, null, null);
        firePropertyChangeEvent(event);
    }

    /**
     * Sets the Result of the process job.
     * @param result Result object.
     */
    public void setResult(Result result) {
        appendLog(LogType.INFO, "");
        appendLog(LogType.INFO, "Process result :");
        for(DataOutputType output : result.getOutput()){
            Object o = output.getData().getContent().get(0);
            appendLog(LogType.INFO, output.getId()+" = "+o.toString());
        }
        firePropertyChangeEvent(new PropertyChangeEvent(this, STATE_PROPERTY, null, state));
    }
}
