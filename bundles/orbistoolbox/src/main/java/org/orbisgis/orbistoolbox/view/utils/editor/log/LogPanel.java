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

import net.miginfocom.swing.MigLayout;
import org.orbisgis.orbistoolbox.view.utils.ToolBoxIcon;
import org.orbisgis.orbistoolbox.view.utils.editor.process.ProcessEditableElement;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Sylvain PALOMINOS
 */
public class LogPanel extends JPanel {

    private JLabel icon;
    private JLabel time;
    private long startTime;
    private Timer timer;
    private JTextArea logArea;

    private boolean running;

    public LogPanel(String processName){
        startTime = System.currentTimeMillis();
        running = true;

        this.setLayout(new MigLayout("fill"));
        icon = new JLabel();
        this.add(icon);
        JLabel processLabel = new JLabel(processName);
        this.add(processLabel);
        time = new JLabel();
        this.add(time, "wrap, alignx right");
        setTime();
        logArea = new JTextArea();
        logArea.setRows(3);
        ((DefaultCaret)logArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(scrollPane, "growx, span");

        timer = new Timer(1000, EventHandler.create(ActionListener.class, this, "setTime"));
        timer.start();
    }

    public void stop(){
        timer.stop();
        running = false;
    }

    public void setTime(){
        if(running) {
            Date date = new Date(System.currentTimeMillis() - startTime - 3600 * 1000);
            time.setText("Time elapsed : " + new SimpleDateFormat("HH:mm:ss").format(date));
            this.revalidate();
        }
    }

    public void setState(ProcessEditableElement.ProcessState state){
        switch(state){
            case COMPLETED:
                icon = new JLabel(ToolBoxIcon.getIcon("process_completed"));
                break;
            case ERROR:
                icon = new JLabel(ToolBoxIcon.getIcon("process_error"));
                break;
            case RUNNING:
                icon = new JLabel(ToolBoxIcon.getIcon("process_running"));
                break;
        }
    }

    public void addLogText(String newLine){
        logArea.setText(logArea.getText()+"\n"+newLine);
    }
}
