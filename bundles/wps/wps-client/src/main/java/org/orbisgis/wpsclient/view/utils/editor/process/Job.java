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
    private ProcessEditableElement pee;
    private WpsClient wpsClient;

    public Job(ProcessEditableElement pee, UUID id){
        this.logMap = new LinkedHashMap<>();
        this.pee = pee;
        this.wpsClient = null;
        this.id = id;
    }

    public Job(WpsClient client, UUID id){
        this.logMap = new LinkedHashMap<>();
        this.pee = null;
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
            for(OutputDescriptionType outputDescriptionType : pee.getProcessOffering().getProcess().getOutput()){
                if(outputDescriptionType.getIdentifier().getValue().equals(output.getId())){
                    appendLog(ProcessExecutionListener.LogType.INFO,
                            outputDescriptionType.getTitle().get(0).getValue()+" = "+o.toString());
                }
            }
        }
        firePropertyChangeEvent(new PropertyChangeEvent(this, STATE_PROPERTY, null, getState()));
    }

    public ProcessDescriptionType getProcess() {
        return pee.getProcess();
    }

    private void firePropertyChangeEvent(PropertyChangeEvent event){
        if(pee != null){
            pee.firePropertyChangeEvent(event);
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
