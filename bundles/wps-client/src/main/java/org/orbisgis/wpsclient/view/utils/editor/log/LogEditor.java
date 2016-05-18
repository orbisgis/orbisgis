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
import org.orbisgis.sif.docking.DockingLocation;
import org.orbisgis.sif.docking.DockingPanelParameters;
import org.orbisgis.sif.edition.EditableElement;
import org.orbisgis.sif.edition.EditorDockable;
import org.orbisgis.wpsclient.WpsClient;
import org.orbisgis.wpsclient.view.utils.ToolBoxIcon;
import org.orbisgis.wpsclient.view.utils.editor.process.ProcessEditableElement;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * UI for the configuration and the run of a WPS process.
 * It extends the EditorDockable interface to be able to be docked in OrbisGIS.
 *
 * @author Sylvain PALOMINOS
 */
public class LogEditor extends JPanel implements EditorDockable, PropertyChangeListener {
    private static final int FIVE_SECOND = 5000;
    /** Name of the EditorDockable. */
    private static final String NAME = "LOG_EDITOR";

    /** LogEditableElement. */
    private LogEditableElement lee;
    /** DockingParameters. */
    private DockingPanelParameters dockingPanelParameters;
    /** Map of LogPanel to display. */
    private Map<String, LogPanel> componentMap;
    /** List of process ended, waiting the 5 seconds before beeing removed.*/
    private LinkedList<String> endProcessFIFO;
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
        dockingPanelParameters = new DockingPanelParameters();
        dockingPanelParameters.setTitleIcon(ToolBoxIcon.getIcon("log"));
        dockingPanelParameters.setDefaultDockingLocation(
                new DockingLocation(DockingLocation.Location.STACKED_ON, WpsClient.TOOLBOX_REFERENCE));
        dockingPanelParameters.setTitle("WPS log");
        dockingPanelParameters.setName(NAME);
        dockingPanelParameters.setCloseable(false);

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
     * Adds a new running process.
     * Its log will be displayed dynamically.
     * @param pee ProcessEditableElement of the running process to add.
     */
    private void addNewLog(ProcessEditableElement pee){
        LogPanel panel = new LogPanel(pee.getProcess().getTitle().get(0).getValue(), this);
        panel.setState(ProcessEditableElement.ProcessState.RUNNING);
        componentMap.put(pee.getId(), panel);
        contentPanel.add(panel, "growx, span");
        processRunning.setText("Process running : "+componentMap.size());
    }

    public void cancelProcess(ActionEvent ae){
        Object logPanelSource = ((JButton)ae.getSource()).getClientProperty("logPanel");
        for(Map.Entry<String, LogPanel> entry : componentMap.entrySet()){
            if(entry.getValue().equals(logPanelSource)){
                lee.cancelProcess(entry.getKey());
            }
        }
    }

    /**
     * The LogPanel corresponding to the process represented by the ProcessEditableElement is prepared to be removed
     * in 5 seconds and its log is copied to the OrbisGIS log.
     * @param pee ProcessEditableElement corresponding to the process.
     * @param successful True if the process has been successfully run, false otherwise.
     */
    private void removeLog(ProcessEditableElement pee, boolean successful){
        LogPanel lp = componentMap.get(pee.getId());
        String log = "\n=====================================\n"+
                "WPS Process : "+pee.getProcess().getTitle().get(0).getValue() +"\n"+
                "=====================================\n"+
                "Result : "+pee.getProcessState()+"\n"+
                "Log : \n";
        for(Map.Entry<String, Color> entry : pee.getLogMap().entrySet()){
            log+=entry.getKey()+"\n";
        }
        if(successful) {
            LoggerFactory.getLogger(LogEditor.class).info(log);
            lp.setState(ProcessEditableElement.ProcessState.SUCCEEDED);
        }
        else{
            LoggerFactory.getLogger(LogEditor.class).error(log);
            lp.setState(ProcessEditableElement.ProcessState.FAILED);
        }
        lp.stop();
        lp.addLogText("(This window will be automatically closed in 5 seconds)\n" +
                "(The process log will be printed in the OrbisGIS log)");
        endProcessFIFO.add(pee.getId());
        //Start the time to fully remove the log in 5 second.
        Timer timer5S = new Timer(FIVE_SECOND, EventHandler.create(ActionListener.class, this, "endInstance"));
        timer5S.setRepeats(false);
        timer5S.start();
    }

    /**
     * Fully remove the log from the UI.
     */
    public void endInstance(){
        String id = endProcessFIFO.removeFirst();
        if(componentMap.get(id) != null) {
            contentPanel.remove(componentMap.get(id));
        }
        componentMap.remove(id);
        processRunning.setText("Process running : "+componentMap.size());
        this.repaint();
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
        dockingPanelParameters.setTitle("WPS log");
        lee.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if(event.getPropertyName().equals(ProcessEditableElement.LOG_PROPERTY)){
            ProcessEditableElement pee = (ProcessEditableElement)event.getSource();
            AbstractMap.SimpleEntry<String, Color> entry = (AbstractMap.SimpleEntry<String, Color>)event.getNewValue();
            componentMap.get(pee.getId()).addLogText(entry.getKey());
        }
        if(event.getPropertyName().equals(ProcessEditableElement.STATE_PROPERTY)){
            ProcessEditableElement pee = (ProcessEditableElement)event.getSource();
            switch((ProcessEditableElement.ProcessState)event.getNewValue()){
                case SUCCEEDED:
                    removeLog(pee, true);
                    break;
                case FAILED:
                    removeLog(pee, false);
                    break;
                case RUNNING:
                    addNewLog(pee);
                    break;
            }
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
