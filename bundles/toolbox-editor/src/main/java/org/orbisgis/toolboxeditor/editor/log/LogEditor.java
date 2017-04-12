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

import net.miginfocom.swing.MigLayout;
import net.opengis.wps._2_0.ProcessDescriptionType;
import org.orbisgis.sif.docking.DockingLocation;
import org.orbisgis.sif.docking.DockingPanelParameters;
import org.orbisgis.sif.edition.EditableElement;
import org.orbisgis.sif.edition.EditorDockable;
import org.orbisgis.toolboxeditor.WpsClientImpl;
import org.orbisgis.toolboxeditor.utils.ToolBoxIcon;
import org.orbisgis.toolboxeditor.utils.Job;
import org.orbiswps.server.execution.ProcessExecutionListener;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import static org.orbisgis.toolboxeditor.utils.Job.*;

/**
 * UI dosplaying the log of the running processes.
 * It extends the EditorDockable interface to be able to be docked in OrbisGIS.
 *
 * @author Sylvain PALOMINOS
 */
public class LogEditor extends JPanel implements EditorDockable, PropertyChangeListener {
    /** Five seconds in milliseconds. */
    private static final int FIVE_SECOND = 5000;
    /** Name of the EditorDockable. */
    private static final String NAME = "LOG_EDITOR";
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(LogPanel.class);

    /** LogEditableElement. */
    private LogEditableElement lee;
    /** DockingParameters. */
    private DockingPanelParameters dockingPanelParameters;
    /** Map of LogPanel to display. */
    private Map<UUID, LogPanel> componentMap;
    /** List of process ended, waiting the 5 seconds before beeing removed.*/
    private LinkedList<UUID> endProcessFIFO;
    /** Content panel which is only scrollable vertically. */
    private VerticalScrollablePanel contentPanel;
    /** Label displaying the actual number of process running. */
    private JLabel processRunning;

    /**
     * Main constructor.
     * @param lee LogEditableElement associated to this LogEditor.
     */
    public LogEditor(LogEditableElement lee){

        this.setLayout(new MigLayout("fill, top"));
        this.lee = lee;
        this.lee.addPropertyChangeListener(this);
        //Sets the docking parameters
        dockingPanelParameters = new DockingPanelParameters();
        dockingPanelParameters.setTitleIcon(ToolBoxIcon.getIcon(ToolBoxIcon.LOG));
        dockingPanelParameters.setDefaultDockingLocation(
                new DockingLocation(DockingLocation.Location.STACKED_ON, WpsClientImpl.TOOLBOX_REFERENCE));
        dockingPanelParameters.setTitle(I18N.tr("WPS log"));
        dockingPanelParameters.setName(NAME);
        dockingPanelParameters.setCloseable(true);
        //Sets the content of the editor
        contentPanel = new VerticalScrollablePanel();
        contentPanel.setLayout(new MigLayout("fill"));
        JScrollPane scrollPane = new JScrollPane(contentPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scrollPane, "growx, growy");
        processRunning = new JLabel("Process running : "+0);
        contentPanel.add(processRunning, "growx, wrap");
        contentPanel.add(new JSeparator(), "growx, wrap");

        componentMap = new HashMap<>();
        endProcessFIFO = new LinkedList<>();
    }

    /**
     * Adds the log of a running process. The log will be displayed dynamically inside a LogPanel.
     *
     * @param process ProcessDescriptionType of the running process.
     * @param job Job to follow to get the log updates.
     */
    public void addNewLog(ProcessDescriptionType process, Job job){
        LogPanel panel = new LogPanel(process.getTitle().get(0).getValue(), this);
        panel.setState(ProcessExecutionListener.ProcessState.RUNNING);
        componentMap.put(job.getId(), panel);
        lee.addJob(job);
        contentPanel.add(panel, "growx, span");
        processRunning.setText(I18N.tr("Process running : {0}", componentMap.size()));
    }

    /**
     * The LogPanel corresponding to the process represented by the ProcessEditableElement is prepared to be removed
     * in 5 seconds and its log is copied to the OrbisGIS log.
     *
     * @param job Job which log is displayed.
     * @param successful True if the process has been successfully run, false otherwise.
     */
    private void removeLog(Job job, boolean successful){
        //If the process is already waiting to be remove, does nothing
        if(endProcessFIFO.contains(job.getId())){
            return;
        }
        //Gets the LogPanel, stop it and adds a messages to indicates that the log will be remove
        LogPanel lp = componentMap.get(job.getId());
        lp.stop();
        lp.addLogText(I18N.tr("(This window will be automatically closed in 5 seconds)\n" +
                "(The process log will be printed in the OrbisGIS log)"));
        //Copy the contain of the log map of the job  to put it in the OrbisGIS logger.
        String log = "\n=====================================\n"+
                I18N.tr("WPS Process : {0}\n", job.getProcess().getTitle().get(0).getValue()) +
                "=====================================\n"+
                I18N.tr("Result : {0}\n", job.getState())+
                I18N.tr("Log : \n");
        for(Map.Entry<String, Color> entry : job.getLogMap().entrySet()){
            log+=entry.getKey()+"\n";
        }
        //If the process is successful, print the log as an INFO one, else print it as en ERROR one
        if(successful) {
            LoggerFactory.getLogger(LogEditor.class).info(log);
        }
        else{
            LoggerFactory.getLogger(LogEditor.class).error(log);
        }
        //Adds the job to the endProcessFIFO stask
        endProcessFIFO.add(job.getId());
        //Start the time to fully remove the log in 5 second.
        Timer timer5S = new Timer(FIVE_SECOND, EventHandler.create(ActionListener.class, this, "endInstance"));
        timer5S.setRepeats(false);
        timer5S.start();
    }

    /**
     * Fully remove the log from the UI.
     */
    public void endInstance(){
        UUID id = endProcessFIFO.removeFirst();
        lee.removeProcess(id);
        if(componentMap.get(id) != null) {
            contentPanel.remove(componentMap.get(id));
        }
        componentMap.remove(id);
        processRunning.setText(I18N.tr("Process running : {0}", componentMap.size()));
        this.repaint();
    }

    public void cancelProcess(Object source){
        Object logPanelSource = ((JButton)source).getClientProperty("logPanel");
        for(Map.Entry<UUID, LogPanel> entry : componentMap.entrySet()){
            if(entry.getValue().equals(logPanelSource)){
                lee.cancelProcess(entry.getKey());
            }
        }
    }

    @Override
    public DockingPanelParameters getDockingParameters() {
        return dockingPanelParameters;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public boolean match(EditableElement editableElement) {
        //Return true if the editable is the one contained by the Process editor
        return editableElement instanceof LogEditableElement;
    }

    @Override
    public EditableElement getEditableElement() {
        return lee;
    }

    @Override
    public void setEditableElement(EditableElement editableElement) {
        this.lee = (LogEditableElement)editableElement;
        dockingPanelParameters.setTitle(I18N.tr("WPS log"));
        lee.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if(event.getPropertyName().equals(LOG_PROPERTY)){
            Job job = (Job) event.getSource();
            AbstractMap.SimpleEntry<String, Color> entry = (AbstractMap.SimpleEntry<String, Color>)event.getNewValue();
            componentMap.get(job.getId()).addLogText(entry.getKey());
        }
        if(event.getPropertyName().equals(STATE_PROPERTY)){
            ProcessExecutionListener.ProcessState state = (ProcessExecutionListener.ProcessState)event.getNewValue();
            Job job = (Job) event.getSource();
            componentMap.get(job.getId()).setState(state);
            switch(state){
                case SUCCEEDED:
                    removeLog(job, true);
                    break;
                case FAILED:
                    removeLog(job, false);
                    break;
            }
        }
        if(event.getPropertyName().equals(PERCENT_COMPLETED_PROPERTY)){
            Job job = (Job) event.getSource();
            Integer percent = (Integer)event.getNewValue();
            componentMap.get(job.getId()).setPercentCompleted(percent);
        }
        if(event.getPropertyName().equals(ESTIMATED_COMPLETION_PROPERTY)){
            Job job = (Job) event.getSource();
            long estimatedCompletion = (long)event.getNewValue();
            componentMap.get(job.getId()).setEstimatedCompletion(estimatedCompletion);
        }
    }

    /**
     * This class extends the JPanel class and implement the Scrollable interface.
     * By implementing Scrollable, this class take all the width of the JScrollPane and won't be scrolled horizontally.
     */
    private class VerticalScrollablePanel extends JPanel implements Scrollable{
        private static final int MAGIC_UNIT_INCREMENT = 16;
        private static final int MAGIC_BLOCK_INCREMENT = 16;
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return this.getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle rectangle, int i, int i1) {
            return MAGIC_UNIT_INCREMENT;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle rectangle, int i, int i1) {
            return MAGIC_BLOCK_INCREMENT;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
