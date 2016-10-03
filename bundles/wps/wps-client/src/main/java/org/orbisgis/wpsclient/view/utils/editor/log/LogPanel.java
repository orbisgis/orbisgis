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

package org.orbisgis.wpsclient.view.utils.editor.log;

import net.miginfocom.swing.MigLayout;
import org.orbisgis.wpsclient.view.utils.ToolBoxIcon;
import org.orbisgis.wpsclient.view.utils.editor.process.ProcessEditableElement;
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

    private static final int ONE_SECOND = 1000;
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(LogPanel.class);

    /** Icon of the state of the process. */
    private JLabel icon;
    /** Running time of the process. */
    private JLabel time;
    private JButton stopButton;
    /** Time in milliseconds when the process has started. */
    private long startTime;
    /** Timer of 1 second used to refresh the process running time. */
    private Timer timer;
    /** TextArea where the process log is displayed. */
    private JTextArea logArea;
    /** Tells if the log is running or not. */
    private boolean running;

    /**
     * Main Constructor.
     * @param processName Name of the running process.
     */
    public LogPanel(String processName, LogEditor logEditor){
        startTime = System.currentTimeMillis();
        running = true;
        //Build the UI
        this.setLayout(new MigLayout("fill"));
        JPanel rightPanel = new JPanel(new MigLayout("fill"));
        icon = new JLabel();
        rightPanel.add(icon);
        JLabel processLabel = new JLabel(processName);
        rightPanel.add(processLabel);
        this.add(rightPanel, "alignx left");
        JPanel leftPanel = new JPanel(new MigLayout("fill"));
        time = new JLabel();
        leftPanel.add(time);
        stopButton = new JButton(ToolBoxIcon.getIcon("stop"));
        stopButton.setBorderPainted(false);
        stopButton.setContentAreaFilled(false);
        stopButton.putClientProperty("logPanel", this);
        stopButton.addActionListener(EventHandler.create(ActionListener.class, logEditor, "cancelProcess", ""));
        leftPanel.add(stopButton);
        this.add(leftPanel, "wrap, alignx right");
        setTime();
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
     * Refresh the running time displayed.
     */
    public void setTime(){
        if(running) {
            Date date = new Date(System.currentTimeMillis() - startTime - 3600 * 1000);
            time.setText(I18N.tr("Time elapsed : {0}.", new SimpleDateFormat("HH:mm:ss").format(date)));
            this.revalidate();
        }
    }

    /**
     * Sets the process state.
     * @param state State of the running process.
     */
    public void setState(ProcessEditableElement.ProcessState state){
        switch(state){
            case SUCCEEDED:
                icon.setIcon(ToolBoxIcon.getIcon("process"));
                break;
            case FAILED:
                icon.setIcon(ToolBoxIcon.getIcon("process_error"));
                break;
            case RUNNING:
                icon.setIcon(ToolBoxIcon.getIcon("process_running"));
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
}
