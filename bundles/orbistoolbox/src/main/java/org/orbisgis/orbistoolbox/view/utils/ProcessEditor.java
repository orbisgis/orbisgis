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

package org.orbisgis.orbistoolbox.view.utils;

import net.miginfocom.swing.MigLayout;
import org.orbisgis.orbistoolbox.controller.processexecution.ExecutionWorker;
import org.orbisgis.orbistoolbox.model.Input;
import org.orbisgis.orbistoolbox.model.Output;
import org.orbisgis.orbistoolbox.model.Process;
import org.orbisgis.orbistoolbox.view.ToolBox;
import org.orbisgis.orbistoolbox.view.ui.ProcessUIPanel;
import org.orbisgis.orbistoolbox.view.ui.dataui.DataUI;
import org.orbisgis.orbistoolbox.view.ui.dataui.DataUIManager;
import org.orbisgis.sif.docking.DockingLocation;
import org.orbisgis.sif.docking.DockingPanelParameters;
import org.orbisgis.sif.edition.EditableElement;
import org.orbisgis.sif.edition.EditorDockable;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.util.AbstractMap;
import java.util.Map;

/**
 * @author Sylvain PALOMINOS
 */
public class ProcessEditor extends JPanel implements EditorDockable, PropertyChangeListener {
    private ProcessEditableElement pee;
    private ToolBox toolBox;
    private DockingPanelParameters dockingPanelParameters;
    /** TabbedPane containing the configuration panel, the info panel and the execution panel */
    private JTabbedPane tabbedPane;
    /** DataUIManager used to create the UI corresponding the the data */
    private DataUIManager dataUIManager;
    /** Label containing the state of the process (running, completed or idle) */
    private JLabel stateLabel;
    /**TextPane used to display the process execution log.*/
    private JTextPane logPane;

    private JPanel resultPanel;

    public ProcessEditor(ToolBox toolBox, ProcessEditableElement pee){
        this.toolBox = toolBox;
        this.pee = pee;
        this.pee.addPropertyChangeListener(this);
        dockingPanelParameters = new DockingPanelParameters();
        dockingPanelParameters.setTitleIcon(ToolBoxIcon.getIcon("script"));
        dockingPanelParameters.setDefaultDockingLocation(
                new DockingLocation(DockingLocation.Location.STACKED_ON, toolBox.getReference()));
        dockingPanelParameters.setTitle(pee.getProcessReference());
        this.setLayout(new BorderLayout());
        dataUIManager = toolBox.getDataUIManager();

        buildUI();

        //According to the process state, open the good tab
        switch(pee.getState()){
            case IDLE:
                tabbedPane.setSelectedIndex(0);
                break;
            case RUNNING:
                tabbedPane.setSelectedIndex(2);
                break;
            case COMPLETED:
            case ERROR:
                setOutputs(pee.getOutputDataMap(), pee.getState().toString());
                tabbedPane.setSelectedIndex(2);
                break;
        }
        //Print the process execution log.
        for(Map.Entry<String, Color> entry : pee.getLogMap().entrySet()){
            print(entry.getKey(), entry.getValue());
        }

        pee.setState(ProcessEditableElement.ProcessState.IDLE);
        this.revalidate();
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
        return editableElement instanceof ProcessEditableElement;
    }

    @Override
    public EditableElement getEditableElement() {
        return pee;
    }

    @Override
    public void setEditableElement(EditableElement editableElement) {
        this.pee = (ProcessEditableElement)editableElement;
        dockingPanelParameters.setTitle(pee.getProcessReference());
        pee.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if(propertyChangeEvent.getPropertyName().equals(ProcessEditableElement.STATE_PROPERTY)){
            stateLabel.setText(pee.getState().name());
        }
        if(propertyChangeEvent.getPropertyName().equals(ProcessEditableElement.LOG_PROPERTY)){
            AbstractMap.Entry<String, Color> entry = (AbstractMap.Entry)propertyChangeEvent.getNewValue();
            print(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Build the UI of the ProcessFrame with the data of the processUIData.
     */
    private void buildUI(){
        //Adds to the tabbedPane the 3 panels
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Configuration", buildUIConf());
        tabbedPane.addTab("Information", buildUIInfo());
        tabbedPane.addTab("Execution", buildUIExec());
        this.add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Run the process.
     * @return True if the process has already been launch, false otherwise.
     */
    public boolean runProcess(){
        if(pee.getState().equals(ProcessEditableElement.ProcessState.IDLE) ||
                pee.getState().equals(ProcessEditableElement.ProcessState.ERROR) ||
                pee.getState().equals(ProcessEditableElement.ProcessState.COMPLETED)) {
            clearLogPanel();
            //Check that all the data field were filled.
            pee.clearLog();
            pee.setState(ProcessEditableElement.ProcessState.RUNNING);
            //Run the process in a separated thread
            ExecutionWorker thread = new ExecutionWorker(pee, toolBox, this);
            thread.execute();
            //Select the execution tab
            stateLabel.setText(pee.getState().getValue());
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
            return false;
        }
        else{
            return true;
        }
    }

    /**
     * Indicated that the process has ended and register the outputs results.
     * @param outputMap Map of the outputs results.
     */
    public void endProcess(Map<URI, Object> outputMap){
        pee.setState(ProcessEditableElement.ProcessState.COMPLETED);
        this.setOutputs(outputMap, ProcessEditableElement.ProcessState.COMPLETED.getValue());
        pee.setState(ProcessEditableElement.ProcessState.IDLE);
    }

    /**
     * Build the UI of the given process according to the given data.
     * @return The UI for the configuration of the process.
     */
    private JComponent buildUIConf(){
        JPanel panel = new JPanel(new MigLayout("fill"));
        //For each input, display its title, its abstract and gets its UI from the dataUIManager
        for(Input i : pee.getProcess().getInput()){
            JPanel inputPanel = new JPanel(new MigLayout("fill"));
            inputPanel.setBorder(BorderFactory.createTitledBorder(i.getTitle()));
            JLabel inputAbstrac = new JLabel(i.getResume());
            inputAbstrac.setFont(inputAbstrac.getFont().deriveFont(Font.ITALIC));
            inputPanel.add(inputAbstrac, "wrap");
            DataUI dataUI = dataUIManager.getDataUI(i.getDataDescription().getClass());
            if(dataUI!=null) {
                inputPanel.add(dataUI.createUI(i, pee.getInputDataMap()), "growx, wrap");
            }
            panel.add(inputPanel, "growx, wrap");
        }

        //For each output, display its title, its abstract and gets its UI from the dataUIManager
        for(Output o : pee.getProcess().getOutput()){
            DataUI dataUI = dataUIManager.getDataUI(o.getDataDescription().getClass());
            if(dataUI!=null) {
                JComponent component = dataUI.createUI(o, pee.getOutputDataMap());
                if(component != null) {
                    JPanel outputPanel = new JPanel(new MigLayout("fill"));
                    outputPanel.setBorder(BorderFactory.createTitledBorder(o.getTitle()));
                    JLabel outputAbstrac = new JLabel(o.getResume());
                    outputAbstrac.setFont(outputAbstrac.getFont().deriveFont(Font.ITALIC));
                    outputPanel.add(outputAbstrac, "growx, wrap");
                    outputPanel.add(component, "growx, wrap");
                    panel.add(outputPanel, "growx, wrap");
                }
            }
        }
        JButton runButton = new JButton("Run");
        runButton.addActionListener(EventHandler.create(ActionListener.class, this, "runProcess"));
        panel.add(runButton, "growx, wrap");
        return new JScrollPane(panel);
    }

    /**
     * Build the UI of the given process according to the given data.
     * @return The UI for the configuration of the process.
     */
    private JComponent buildUIInfo(){
        JPanel panel = new JPanel(new MigLayout("fill"));
        Process p  = pee.getProcess();
        //Process info
        JLabel titleContentLabel = new JLabel(p.getTitle());
        JLabel abstracContentLabel = new JLabel();
        if(p.getResume() != null) {
            abstracContentLabel.setText(p.getResume());
        }
        else{
            abstracContentLabel.setText("-");
            abstracContentLabel.setFont(abstracContentLabel.getFont().deriveFont(Font.ITALIC));
        }

        JPanel processPanel = new JPanel(new MigLayout());
        processPanel.setBorder(BorderFactory.createTitledBorder("Process :"));
        processPanel.add(titleContentLabel, "wrap, align left");
        processPanel.add(abstracContentLabel, "wrap, align left");

        //Input info
        JPanel inputPanel = new JPanel(new MigLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Inputs :"));

        for(Input i : p.getInput()){
            inputPanel.add(new JLabel(dataUIManager.getIconFromData(i)));
            inputPanel.add(new JLabel(i.getTitle()), "align left, wrap");
            if(i.getResume() != null) {
                JLabel abstrac = new JLabel(i.getResume());
                abstrac.setFont(abstrac.getFont().deriveFont(Font.ITALIC));
                inputPanel.add(abstrac, "span 2, wrap");
            }
            else {
                inputPanel.add(new JLabel("-"), "span 2, wrap");
            }
        }

        //Output info
        JPanel outputPanel = new JPanel(new MigLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("Outputs :"));

        for(Output o : p.getOutput()){
            outputPanel.add(new JLabel(dataUIManager.getIconFromData(o)));
            outputPanel.add(new JLabel(o.getTitle()), "align left, wrap");
            if(o.getResume() != null) {
                JLabel abstrac = new JLabel(o.getResume());
                abstrac.setFont(abstrac.getFont().deriveFont(Font.ITALIC));
                outputPanel.add(abstrac, "span 2, wrap");
            }
            else {
                outputPanel.add(new JLabel("-"), "align center, span 2, wrap");
            }
        }

        panel.add(processPanel, "growx, wrap");
        panel.add(inputPanel, "growx, wrap");
        panel.add(outputPanel, "growx, wrap");

        return panel;
    }

    /**
     * Build the UI of the given process according to the given data.
     * @return The UI for the configuration of the process.
     */
    private JComponent buildUIExec(){
        JPanel panel = new JPanel(new MigLayout("fill"));

        JPanel executorPanel = new JPanel(new MigLayout());
        executorPanel.setBorder(BorderFactory.createTitledBorder("Executor :"));
        executorPanel.add(new JLabel("localhost"));

        JPanel statusPanel = new JPanel(new MigLayout());
        statusPanel.setBorder(BorderFactory.createTitledBorder("Status :"));
        stateLabel = new JLabel(pee.getState().getValue());
        statusPanel.add(stateLabel);

        resultPanel = new JPanel(new MigLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("Result :"));

        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Log :"));
        logPane = new JTextPane();
        logPane.setCaretPosition(0);
        JScrollPane scrollPane = new JScrollPane(logPane);
        logPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(executorPanel, "growx, wrap");
        panel.add(statusPanel, "growx, wrap");
        panel.add(resultPanel, "growx, wrap");
        panel.add(logPanel, "growx, growy, wrap");

        return panel;
    }

    /**
     * Sets the outputs label with the outputs results.
     * @param outputs Outputs results.
     */
    public void setOutputs(Map<URI, Object> outputs, String state) {
        resultPanel.removeAll();
        for(Output o : pee.getProcess().getOutput()) {
            JLabel title = new JLabel(o.getTitle()+" : ");
            JLabel result = new JLabel(outputs.get(o.getIdentifier()).toString());
            resultPanel.add(title);
            resultPanel.add(result, "wrap");
        }
        stateLabel.setText(state);
    }

    /**
     * Add the provided text with the provided color to the GUI document
     * @param text The text that will be added
     * @param color The color used to show the text
     */
    public void print(String text, Color color) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
                StyleConstants.Foreground, color);
        int len = logPane.getDocument().getLength();
        try {
            logPane.setCaretPosition(len);
            logPane.getDocument().insertString(len, text+"\n", aset);
        } catch (BadLocationException e) {
            LoggerFactory.getLogger(ProcessUIPanel.class).error("Cannot show the log message", e);
        }
        logPane.setCaretPosition(logPane.getDocument().getLength());
    }

    /**
     * Clear the log panel.
     */
    public void clearLogPanel(){
        try {
            logPane.getDocument().remove(1, logPane.getDocument().getLength() - 1);
        } catch (BadLocationException e) {
            LoggerFactory.getLogger(ProcessUIPanel.class).error(e.getMessage());
        }
    }
}
