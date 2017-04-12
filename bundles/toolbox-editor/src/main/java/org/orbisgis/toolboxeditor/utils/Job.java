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
package org.orbisgis.toolboxeditor.utils;

import net.opengis.wps._2_0.*;
import org.orbiswps.server.execution.ProcessExecutionListener;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.Timer;
import javax.xml.datatype.XMLGregorianCalendar;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * This class represents the WPS server Job object but in the client side.
 * The aim of the class is to follow the life cycle of a server side Job.
 *
 * When it is construct, the UUID of the server side Job is given and will be used later for refreshing the job status.
 * Once created, some property listener can be add for listening the update of the Process state.
 * Then, once configured, the result of the process execution (the StatusInfo object) is given with the setStatus()
 * methods. The job will save the state, and fire two PropertyEvents : one with the new state of the execution and
 * another with the execution log.
 * After firering the event, with the refresh date of the StatusInfo object, a timer is launched and will wake up the
 * Job. When the Job is woken up by the timer, it will fire a PropertyEvent with the request of the update of the job status.
 * The class in charge of that (often the WpsClient) will get the new StatusInfo object form the Wps server thanks to the
 * GetStatus request and give it back to the client side Job object. Then the status update is done, the timer is reset
 * and so on.
 * Once the status is 'succedded' or 'failed', the Job fire a PropertyEvent for getting the execution result from its
 * listeners. The listener in charge (often the WpsClient) gives back the Result object from the WpsServer and the Job
 * fire the last two status and log PropertyEvents.
 *
 * @author Sylvain PALOMINOS
 */
public class Job implements ProcessExecutionListener{

    /** Static strings used for the PropertyEvents. */
    public static final String STATE_PROPERTY = "STATE_PROPERTY";
    public static final String LOG_PROPERTY = "LOG_PROPERTY";
    public static final String CANCEL = "CANCEL";
    public static final String REFRESH_STATUS = "REFRESH_STATUS";
    public static final String GET_RESULTS = "GET_RESULTS";
    public static final String PERCENT_COMPLETED_PROPERTY = "PERCENT_COMPLETED_PROPERTY";
    public static final String ESTIMATED_COMPLETION_PROPERTY = "ESTIMATED_COMPLETION_PROPERTY";

    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(ProcessExecutionListener.class);
    /** Id of the server side Job. */
    private UUID id;
    /** Process state at the last refresh date. */
    private ProcessExecutionListener.ProcessState state;
    /** Time when the process has started. */
    private Long startTime;
    /** Map containing the logs of the execution of their display color. */
    private Map<String, Color> logMap;
    /** Process executed by the Job */
    private ProcessDescriptionType process;
    /** List of property listener which are waiting for any changes on the process*/
    private List<PropertyChangeListener> propertyChangeListenerList;

    /**
     * Constructs a Job with the id of the server side Job and the ProcessDescriptionType of the process to execute.
     * This way, while no PropertyListener is add, the Job will only update it's internal state without communicating
     * it to other class.
     *
     * @param id UUID of the server side Job.
     * @param process ProcessDescriptionType of the process to execute.
     */
    public Job(UUID id, ProcessDescriptionType process){
        this.logMap = new LinkedHashMap<>();
        this.propertyChangeListenerList = new ArrayList<>();
        this.process = process;
        this.id = id;
    }

    /**
     * Returns the UUID of the track server side Job.
     * @return The UUID of the server side Job.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns the state of the track server side Job.
     * @return The state of the server side Job.
     */
    public ProcessExecutionListener.ProcessState getState() {
        return state;
    }

    @Override
    public void setProcessState(ProcessState processState) {
        this.state = processState;
        if (processState.equals(ProcessExecutionListener.ProcessState.FAILED)) {
            appendLog(ProcessExecutionListener.LogType.ERROR, processState.toString());
        } else {
            appendLog(ProcessExecutionListener.LogType.INFO, processState.toString());
        }
        //If the process has ended with success, retrieve the results.
        //The firing of the process state change will be done later
        if (processState.equals(ProcessExecutionListener.ProcessState.SUCCEEDED)) {
            firePropertyChangeEvent(new PropertyChangeEvent(this, GET_RESULTS, id, id));
        }
        //Else, fire the change of the process state.
        else {
            firePropertyChangeEvent(new PropertyChangeEvent(this, STATE_PROPERTY, null, processState));
        }
    }

    public void setPercentCompleted(Integer percentCompleted) {
        firePropertyChangeEvent(new PropertyChangeEvent(this, PERCENT_COMPLETED_PROPERTY, null, percentCompleted));
    }

    public void setEstimatedCompletion(XMLGregorianCalendar completionDate) {
        long completionMillis = completionDate.toGregorianCalendar().getTime().getTime() - new Date().getTime();
        firePropertyChangeEvent(new PropertyChangeEvent(this, ESTIMATED_COMPLETION_PROPERTY, null, completionMillis));
    }

    /**
     * Returns the map of the Job execution logs.
     * @return The map of the Job execution logs.
     */
    public Map<String, Color> getLogMap() {
        return logMap;
    }

    /**
     * Put a log in the Job log map
     * @param log String text of the log.
     * @param color Color of the log text.
     */
    public void putLog(String log, Color color) {
        this.logMap.put(log, color);
    }

    @Override
    public void setStartTime(long time) {
        this.startTime = time + 60*60*1000;
    }

    @Override
    public void appendLog(LogType logType, String message) {
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
        putLog(log, color);
        firePropertyChangeEvent(new PropertyChangeEvent(
                this, LOG_PROPERTY, null, new AbstractMap.SimpleEntry<>(log, color)));
    }



    /**
     * Start a timer with the date when the Job should ask again the process execution job status to the WpsService.
     * @param date Date when the state should be asked.
     */
    private void startRefreshTimer(XMLGregorianCalendar date){
        if(date != null) {
            long delta = date.toGregorianCalendar().getTime().getTime() - new Date().getTime();
            if (delta <= 0) {
                delta = 1;
            }
            Timer timer = new Timer((int) delta, EventHandler.create(ActionListener.class, this, "askStatusRefresh"));
            timer.setRepeats(false);
            timer.start();
        }
    }

    /**
     * Fire an event to ask the refreshing of the status.
     */
    public void askStatusRefresh(){
        PropertyChangeEvent event = new PropertyChangeEvent(this, REFRESH_STATUS, this.getId(), this.getId());
        firePropertyChangeEvent(event);
    }

    /**
     * Sets the new status of the Job with the Wps server answer object StatusInfo.
     * @param statusInfo StatusInfo object coming from the Wps server.
     */
    public void setStatus(StatusInfo statusInfo){
        setProcessState(ProcessExecutionListener.ProcessState.valueOf(statusInfo.getStatus().toUpperCase()));
        startRefreshTimer(statusInfo.getNextPoll());
        setPercentCompleted(statusInfo.getPercentCompleted());
        if(statusInfo.getEstimatedCompletion() != null) {
            setEstimatedCompletion(statusInfo.getEstimatedCompletion());
        }
    }



    /**
     * Sets the Result of the process job.
     * @param result Result object coming from the Wps server.
     */
    public void setResult(Result result) {
        appendLog(ProcessExecutionListener.LogType.INFO, "");
        appendLog(ProcessExecutionListener.LogType.INFO, I18N.tr("Process result :"));
        //For each output, try to build a human readable string containing the output which will be displayed in the log.
        for(DataOutputType output : result.getOutput()){
            Object o = output.getData().getContent().get(0);
            for (OutputDescriptionType outputDescriptionType : process.getOutput()) {
                if (outputDescriptionType.getIdentifier().getValue().equals(output.getId())) {
                    appendLog(ProcessExecutionListener.LogType.INFO,
                            outputDescriptionType.getTitle().get(0).getValue() + " = " + o.toString());
                }
            }
        }
        firePropertyChangeEvent(new PropertyChangeEvent(this, STATE_PROPERTY, null, getState()));
    }

    /**
     * Returns the process executed by the server side Job.
     * @return The process executed by the server side Job.
     */
    public ProcessDescriptionType getProcess() {
        return process;
    }

    /**
     * Adds a PropertyChangeListener which will track the updates of the Job.
     * @param listener Listeners track the Job.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener){
        this.propertyChangeListenerList.add(listener);
    }

    /**
     * Removes a PropertyChangeListener .
     * @param listener Listeners to remove.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener){
        this.propertyChangeListenerList.remove(listener);
    }

    /**
     * Fires a PropertyEvent to all the registered PropertyListeners.
     * @param event Event to transmit to all the listeners.
     */
    public void firePropertyChangeEvent(PropertyChangeEvent event){
        if(!propertyChangeListenerList.isEmpty()) {
            for (PropertyChangeListener listener : propertyChangeListenerList) {
                listener.propertyChange(event);
            }
        }
    }
}
