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
import org.orbisgis.toolboxeditor.utils.ToolBoxIcon;
import org.orbiswps.server.execution.ProcessExecutionListener;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class display all the usefull information about a running process.
 *
 * @author Sylvain PALOMINOS
 */
public class LogPanel extends JPanel {

    /** One second in milliseconds. */
    private static final int ONE_SECOND = 1000;
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(LogPanel.class);

    /** Icon of the state of the process. */
    private JLabel icon;
    /** Running time of the process. */
    private JLabel runningTime;
    /** Completion time of the process. */
    private JLabel completionTime;
    /** Percent completed of the process. */
    private JLabel percentCompleted;
    /** Time in milliseconds when the process has started. */
    private long startTime;
    /** Timer of 1 second used to refresh the process running runningTime. */
    private Timer timer;
    /** TextArea where the process log is displayed. */
    private JTextArea logArea;
    /** Tells if the log is running or not. */
    private boolean running;
    /** Process percent completed. */
    int percent = 0;
    /** Estimated time to completion. */
    long timeToCompletion = -1;

    /**
     * Main Constructor.
     * @param processName Name of the running process.
     */
    public LogPanel(String processName, LogEditor logEditor){
        startTime = System.currentTimeMillis();
        running = true;
        //Build the UI
        this.setLayout(new MigLayout("fill"));
        //Sets the right panel with the icon and the process title
        JPanel rightPanel = new JPanel(new MigLayout("fill"));
        icon = new JLabel();
        rightPanel.add(icon);
        JLabel processLabel = new JLabel(processName);
        rightPanel.add(processLabel, "wrap");
        runningTime = new JLabel();
        rightPanel.add(runningTime, "span");
        this.add(rightPanel, "alignx left");
        //Sets the left panel with the running runningTime and the stop button.
        JPanel leftPanel = new JPanel(new MigLayout("fill"));
        percentCompleted = new JLabel();
        leftPanel.add(percentCompleted);
        JButton stopButton = new JButton(ToolBoxIcon.getIcon(ToolBoxIcon.STOP));
        stopButton.setBorderPainted(false);
        stopButton.setContentAreaFilled(false);
        stopButton.putClientProperty("logPanel", this);
        stopButton.addActionListener(EventHandler.create(ActionListener.class, logEditor, "cancelProcess", "source"));
        leftPanel.add(stopButton, "wrap");
        completionTime = new JLabel();
        leftPanel.add(completionTime, "span");
        this.add(leftPanel, "wrap, alignx right");
        setTime();
        //Sets the main textArea which contains the log
        logArea = new JTextArea();
        logArea.setRows(3);
        ((DefaultCaret)logArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(scrollPane, "growx, span");

        timer = new Timer(ONE_SECOND, EventHandler.create(ActionListener.class, this, "setTime"));
        timer.start();
    }

    /**
     * Stop the log.
     */
    public void stop(){
        timer.stop();
        running = false;
    }

    /**
     * Refresh the running runningTime displayed.
     */
    public void setTime(){
        if(running) {
            Date date = new Date(System.currentTimeMillis() - startTime - 3600 * 1000);
            runningTime.setText(I18N.tr("Time elapsed : {0}", new SimpleDateFormat("HH:mm:ss").format(date)));

            if(timeToCompletion != -1) {
                Date dateToCompletion = new Date(timeToCompletion);
                completionTime.setText(I18N.tr("Time to completion : {0}",
                        new SimpleDateFormat("HH:mm:ss").format(dateToCompletion)));
            }
            else{
                completionTime.setText(I18N.tr("Time to completion : --:--:--"));
            }

            percentCompleted.setText(I18N.tr("{0}% done", percent));

            this.revalidate();
        }
    }

    /**
     * Sets the process state.
     * @param state State of the running process.
     */
    public void setState(ProcessExecutionListener.ProcessState state){
        switch(state){
            case SUCCEEDED:
            case ACCEPTED:
                icon.setIcon(ToolBoxIcon.getIcon(ToolBoxIcon.PROCESS));
                break;
            case FAILED:
                icon.setIcon(ToolBoxIcon.getIcon(ToolBoxIcon.PROCESS_ERROR));
                break;
            case RUNNING:
                icon.setIcon(ToolBoxIcon.getIcon(ToolBoxIcon.PROCESS_RUNING));
                break;
        }
    }

    /**
     * Adds text to the log.
     * @param newLine New text line to add to the log.
     */
    public void addLogText(String newLine){
        if(!logArea.getText().isEmpty()){
            logArea.setText(logArea.getText()+"\n");
        }
        logArea.setText(logArea.getText()+newLine);
    }

    /**
     * Sets the percent completion.
     * @param percentCompleted Percent completion of the process.
     */
    public void setPercentCompleted(int percentCompleted){
        percent = percentCompleted;
    }

    /**
     * Sets the estimated time in millisecond to completion.
     * @param estimatedCompletion Time in millis
     */
    public void setEstimatedCompletion(long estimatedCompletion){
        timeToCompletion = estimatedCompletion;
    }
}
