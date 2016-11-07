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
import org.orbisgis.wpsclient.WpsClient;
import org.orbisgis.wpsservice.controller.execution.ProcessExecutionListener;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.Timer;
import javax.xml.datatype.XMLGregorianCalendar;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.orbisgis.wpsclient.view.utils.editor.process.ProcessEditableElement.LOG_PROPERTY;
import static org.orbisgis.wpsclient.view.utils.editor.process.ProcessEditableElement.REFRESH_STATUS;
import static org.orbisgis.wpsclient.view.utils.editor.process.ProcessEditableElement.STATE_PROPERTY;
import static org.orbisgis.wpsclient.view.utils.editor.process.ProcessEditableElement.GET_RESULTS;

/**
 * This class represents the WPS Job object by in the client side.
 *
 * @author Sylvain PALOMOINOS
 */
public class Job implements ProcessExecutionListener{

    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(ProcessExecutionListener.class);
    private UUID id;
    private ProcessExecutionListener.ProcessState state;
    private Long startTime;
    private Map<String, Color> logMap;
    private ProcessEditableElement processEditableElement;
    private WpsClient wpsClient;

    public Job(ProcessEditableElement processEditableElement, UUID id){
        this.logMap = new LinkedHashMap<>();
        this.processEditableElement = processEditableElement;
        this.wpsClient = null;
        this.id = id;
    }

    public Job(WpsClient client, UUID id){
        this.logMap = new LinkedHashMap<>();
        this.processEditableElement = null;
        this.wpsClient = client;
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

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
     * Adds a date when it should ask again the process execution job status to the WpsService.
     * @param date Date when the state should be asked.
     */
    public void addRefreshDate(XMLGregorianCalendar date){

        if(date != null) {
            long delta = date.toGregorianCalendar().getTime().getTime() - new Date().getTime();
            if (delta <= 0) {
                delta = 1;
            }
            Timer timer = new Timer((int) delta, EventHandler.create(ActionListener.class, this, "askStatusRefresh", "source"));
            timer.setRepeats(false);
            timer.start();
        }
    }

    /**
     * Fire an event to ask the refreshing of the status.
     */
    public void askStatusRefresh(Object source){
        PropertyChangeEvent event = new PropertyChangeEvent(this, REFRESH_STATUS, this.getId(), this.getId());
        firePropertyChangeEvent(event);
    }

    public void setStatus(StatusInfo statusInfo){
        setProcessState(ProcessExecutionListener.ProcessState.valueOf(statusInfo.getStatus().toUpperCase()));
        addRefreshDate(statusInfo.getNextPoll());
    }



    /**
     * Sets the Result of the process job.
     * @param result Result object.
     */
    public void setResult(Result result) {
        appendLog(ProcessExecutionListener.LogType.INFO, "");
        appendLog(ProcessExecutionListener.LogType.INFO, I18N.tr("Process result :"));
        for(DataOutputType output : result.getOutput()){
            Object o = output.getData().getContent().get(0);
            for(OutputDescriptionType outputDescriptionType : processEditableElement.getProcessOffering().getProcess().getOutput()){
                if(outputDescriptionType.getIdentifier().getValue().equals(output.getId())){
                    appendLog(ProcessExecutionListener.LogType.INFO,
                            outputDescriptionType.getTitle().get(0).getValue()+" = "+o.toString());
                }
            }
        }
        firePropertyChangeEvent(new PropertyChangeEvent(this, STATE_PROPERTY, null, getState()));
    }

    public ProcessDescriptionType getProcess() {
        return processEditableElement.getProcess();
    }

    private void firePropertyChangeEvent(PropertyChangeEvent event){
        if(processEditableElement != null){
            processEditableElement.firePropertyChangeEvent(event);
        }
        else if(wpsClient != null){
            switch(event.getPropertyName()){
                case STATE_PROPERTY:
                    //Nothing to do
                    break;
                case REFRESH_STATUS:
                    wpsClient.getJobStatus(id);
                    break;
                case GET_RESULTS:
                    setResult(wpsClient.getJobResult(id));
                    break;
                case LOG_PROPERTY:
                    //Nothing to do
                    break;
            }
        }
    }
}
