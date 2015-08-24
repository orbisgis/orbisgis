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

package org.orbisgis.orbistoolbox.view.ui;

import net.miginfocom.swing.MigLayout;
import org.orbisgis.orbistoolbox.controller.processexecution.ExecutionThread;
import org.orbisgis.orbistoolbox.model.*;
import org.orbisgis.orbistoolbox.model.Output;
import org.orbisgis.orbistoolbox.model.Process;
import org.orbisgis.orbistoolbox.view.ToolBox;
import org.orbisgis.orbistoolbox.view.ui.dataui.DataUI;
import org.orbisgis.orbistoolbox.view.ui.dataui.DataUIManager;
import org.orbisgis.orbistoolbox.view.utils.ProcessUIData;
import org.orbisgis.orbistoolbox.view.utils.ToolBoxIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Frame used to configure and run a process.
 *
 * @author Sylvain PALOMINOS
 **/

public class ProcessFrame extends JFrame {

    /** Toolbox */
    private ToolBox toolBox;

    /** TabbedPane containing the configuration panel, the info panel and the execution panel */
    private JTabbedPane tabbedPane;
    /** List of the label containing the outputs */
    private List<JLabel> outputLabelList;
    /** DataUIManager used to create the UI corresponding the the data */
    private DataUIManager dataUIManager;
    /** Label containing the state of the process (running, completed or idle) */
    private JLabel stateLabel;

    private ProcessUIData processUIData;

    /**
     * Main constructor.
     * @param process Process represented.
     * @param toolBox Toolbox
     */
    public ProcessFrame(Process process, ToolBox toolBox) {
        this.setLayout(new BorderLayout());

        outputLabelList = new ArrayList<>();
        dataUIManager = toolBox.getDataUIManager();

        processUIData = new ProcessUIData(toolBox, process);
        processUIData.setState(ProcessUIData.ProcessState.IDLE);
        processUIData.setProcessFrame(this);
        processUIData.setInputDataMap(dataUIManager.getInputDefaultValues(process));

        buildUI(processUIData);
    }

    public ProcessFrame(ProcessUIData processUIData, ToolBox toolBox){
        this.setLayout(new BorderLayout());
        this.processUIData = processUIData;

        outputLabelList = new ArrayList<>();
        dataUIManager = toolBox.getDataUIManager();

        buildUI(processUIData);
    }

    private void buildUI(ProcessUIData processUIData){
        //Adds to the tabbedPane the 3 panels
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Configuration", buildUIConf(processUIData));
        tabbedPane.addTab("Information", buildUIInfo(processUIData));
        tabbedPane.addTab("Execution", buildUIExec(processUIData));
        this.add(tabbedPane, BorderLayout.CENTER);

        //Create and add the run button and the cancel button
        JPanel buttons = new JPanel(new MigLayout());
        JButton runButton = new JButton("run");
        runButton.addActionListener(EventHandler.create(ActionListener.class, this, "runProcess"));
        buttons.add(runButton);
        JButton cancelButton = new JButton("close");
        cancelButton.addActionListener(EventHandler.create(ActionListener.class, this, "close"));
        buttons.add(cancelButton);
        this.add(buttons, BorderLayout.PAGE_END);
    }

    /**
     * Returns the output data.
     * @return The output data.
     */
    public ProcessUIData getProcessUIData(){
        return processUIData;
    }

    /**
     * Run the process.
     */
    public void runProcess(){
        //Select the execution tab
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        processUIData.runProcess();
    }

    /**
     * Close this windows.
     */
    public void close(){
        processUIData.setProcessFrame(null);
        this.dispose();
    }

    /**
     * Build the UI of the given process according to the given data.
     * @param processUIData Process data.
     * @return The UI for the configuration of the process.
     */
    public JComponent buildUIConf(ProcessUIData processUIData){
        JPanel panel = new JPanel(new MigLayout("fill"));
        //For each input, display its title, its abstract and gets its UI from the dataUIManager
        for(Input i : processUIData.getProcess().getInput()){
            JPanel inputPanel = new JPanel(new MigLayout("fill"));
            inputPanel.setBorder(BorderFactory.createTitledBorder(i.getTitle()));
            JLabel inputAbstrac = new JLabel(i.getAbstrac());
            inputAbstrac.setFont(inputAbstrac.getFont().deriveFont(Font.ITALIC));
            inputPanel.add(inputAbstrac, "wrap");
            DataUI dataUI = dataUIManager.getDataUI(i.getDataDescription().getClass());
            if(dataUI!=null) {
                inputPanel.add(dataUI.createUI(i, processUIData.getInputDataMap()), "wrap");
            }
            panel.add(inputPanel, "growx, wrap");
        }

        return panel;
    }

    /**
     * Build the UI of the given process according to the given data.
     * @param processUIData Process data.
     * @return The UI for the configuration of the process.
     */
    public JComponent buildUIInfo(ProcessUIData processUIData){
        JPanel panel = new JPanel(new MigLayout("fill"));
        Process p  =processUIData.getProcess();
        //Process info
        JLabel titleContentLabel = new JLabel(p.getTitle());
        JLabel abstracContentLabel = new JLabel();
        if(p.getAbstrac() != null) {
            abstracContentLabel.setText(p.getAbstrac());
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
            inputPanel.add(new JLabel(getIconFromData(i.getDataDescription())));
            inputPanel.add(new JLabel(i.getTitle()), "align left, wrap");
            if(i.getAbstrac() != null) {
                JLabel abstrac = new JLabel(i.getAbstrac());
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
            outputPanel.add(new JLabel(getIconFromData(o.getDataDescription())));
            outputPanel.add(new JLabel(o.getTitle()), "align left, wrap");
            if(o.getAbstrac() != null) {
                JLabel abstrac = new JLabel(o.getAbstrac());
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
     * Read the given DataDescription and try to find an icon corresponding to the type represented.
     * @param dataDescription DataDescription containing the data type.
     * @return An ImageIcon corresponding to the type.
     */
    private ImageIcon getIconFromData(DataDescription dataDescription) {
        if(dataDescription instanceof LiteralData) {
            LiteralData ld = (LiteralData)dataDescription;
            DataType dataType = DataType.STRING;
            if(ld.getValue() != null && ld.getValue().getDataType()!= null) {
                dataType = ld.getValue().getDataType();
            }
            switch (dataType) {
                case STRING:
                    return ToolBoxIcon.getIcon("string");
                case UNSIGNED_BYTE:
                case SHORT:
                case LONG:
                case BYTE:
                case INTEGER:
                case DOUBLE:
                case FLOAT:
                    return ToolBoxIcon.getIcon("number");
                case BOOLEAN:
                    return ToolBoxIcon.getIcon("boolean");
                default:
                    return ToolBoxIcon.getIcon("undefined");
            }
        }
        return ToolBoxIcon.getIcon("undefined");
    }

    /**
     * Build the UI of the given process according to the given data.
     * @param processUIData Process data.
     * @return The UI for the configuration of the process.
     */
    public JComponent buildUIExec(ProcessUIData processUIData){
        JPanel panel = new JPanel(new MigLayout("fill"));

        JPanel executorPanel = new JPanel(new MigLayout());
        executorPanel.setBorder(BorderFactory.createTitledBorder("Executor :"));
        executorPanel.add(new JLabel("localhost"));

        JPanel statusPanel = new JPanel(new MigLayout());
        statusPanel.setBorder(BorderFactory.createTitledBorder("Status :"));
        stateLabel = new JLabel(ProcessUIData.ProcessState.IDLE.getValue());
        statusPanel.add(stateLabel);

        JPanel resultPanel = new JPanel(new MigLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("Result :"));
        for(Output o : processUIData.getProcess().getOutput()) {
            JLabel title = new JLabel(o.getTitle()+" : ");
            JLabel result = new JLabel();
            result.putClientProperty("URI", o.getIdentifier());
            outputLabelList.add(result);
            resultPanel.add(title);
            resultPanel.add(result, "wrap");
        }

        panel.add(executorPanel, "growx, wrap");
        panel.add(statusPanel, "growx, wrap");
        panel.add(resultPanel, "growx, wrap");

        return panel;
    }

    /**
     * Sets the outputs label with the outputs results.
     * @param outputs Outputs results.
     */
    public void setOutputs(List<String> outputs, String state) {
        for (int i=0; i<outputs.size(); i++) {
            outputLabelList.get(i).setText(outputs.get(i));
        }
        stateLabel.setText(state);
    }
}
