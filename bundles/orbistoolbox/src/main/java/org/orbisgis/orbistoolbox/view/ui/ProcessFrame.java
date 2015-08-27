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
import org.orbisgis.orbistoolbox.model.*;
import org.orbisgis.orbistoolbox.model.Output;
import org.orbisgis.orbistoolbox.model.Process;
import org.orbisgis.orbistoolbox.view.ToolBox;
import org.orbisgis.orbistoolbox.view.ui.dataui.DataUI;
import org.orbisgis.orbistoolbox.view.ui.dataui.DataUIManager;
import org.orbisgis.orbistoolbox.view.utils.ProcessExecutionData;
import org.orbisgis.orbistoolbox.view.utils.ToolBoxIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Frame used to configure and run a process.
 *
 * @author Sylvain PALOMINOS
 **/

public class ProcessFrame extends JFrame {

    /** TabbedPane containing the configuration panel, the info panel and the execution panel */
    private JTabbedPane tabbedPane;
    /** Map of the label containing the outputs values and their identifier*/
    private Map<URI, JLabel> outputURILabelMap;
    /** DataUIManager used to create the UI corresponding the the data */
    private DataUIManager dataUIManager;
    /** Label containing the state of the process (running, completed or idle) */
    private JLabel stateLabel;

    private ProcessExecutionData processExecutionData;

    /**
     * Main constructor with no ProcessExecutionData.
     * @param process Process represented.
     * @param toolBox Toolbox
     */
    public ProcessFrame(Process process, ToolBox toolBox) {
        this.setLayout(new BorderLayout());

        outputURILabelMap = new HashMap<>();
        dataUIManager = toolBox.getDataUIManager();

        processExecutionData = new ProcessExecutionData(toolBox, process);
        processExecutionData.setState(ProcessExecutionData.ProcessState.IDLE);
        processExecutionData.setProcessFrame(this);
        processExecutionData.setInputDataMap(dataUIManager.getInputDefaultValues(process));

        buildUI();
    }

    /**
     * Constructor with an existing processUIData.
     * @param processExecutionData Data for the UI
     * @param toolBox ToolBox
     */
    public ProcessFrame(ProcessExecutionData processExecutionData, ToolBox toolBox){
        this.setLayout(new BorderLayout());
        this.processExecutionData = processExecutionData;

        outputURILabelMap = new HashMap<>();
        dataUIManager = toolBox.getDataUIManager();

        buildUI();

        processExecutionData.setProcessFrame(this);
        if(processExecutionData.getState().equals(ProcessExecutionData.ProcessState.COMPLETED)){
            processExecutionData.validateProcessExecution();
        }
    }

    /**
     * Build the UI of the ProcessFrame with the data of the processUIData.
     */
    private void buildUI(){
        //Adds to the tabbedPane the 3 panels
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Configuration", buildUIConf(processExecutionData));
        tabbedPane.addTab("Information", buildUIInfo(processExecutionData));
        tabbedPane.addTab("Execution", buildUIExec(processExecutionData));
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
    public ProcessExecutionData getProcessExecutionData(){
        return processExecutionData;
    }

    /**
     * Run the process.
     */
    public void runProcess(){
        //Select the execution tab
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        processExecutionData.runProcess();
    }

    /**
     * Close this windows.
     */
    public void close(){
        processExecutionData.setProcessFrame(null);
        this.dispose();
    }

    /**
     * Build the UI of the given process according to the given data.
     * @param processExecutionData Process data.
     * @return The UI for the configuration of the process.
     */
    public JComponent buildUIConf(ProcessExecutionData processExecutionData){
        JPanel panel = new JPanel(new MigLayout("fill"));
        //For each input, display its title, its abstract and gets its UI from the dataUIManager
        for(Input i : processExecutionData.getProcess().getInput()){
            JPanel inputPanel = new JPanel(new MigLayout("fill"));
            inputPanel.setBorder(BorderFactory.createTitledBorder(i.getTitle()));
            JLabel inputAbstrac = new JLabel(i.getAbstrac());
            inputAbstrac.setFont(inputAbstrac.getFont().deriveFont(Font.ITALIC));
            inputPanel.add(inputAbstrac, "wrap");
            DataUI dataUI = dataUIManager.getDataUI(i.getDataDescription().getClass());
            if(dataUI!=null) {
                inputPanel.add(dataUI.createUI(i, processExecutionData.getInputDataMap()), "wrap");
            }
            panel.add(inputPanel, "growx, wrap");
        }

        //For each output, display its title, its abstract and gets its UI from the dataUIManager
        for(Output o : processExecutionData.getProcess().getOutput()){
            DataUI dataUI = dataUIManager.getDataUI(o.getDataDescription().getClass());
            if(dataUI!=null) {
                JComponent component = dataUI.createUI(o, processExecutionData.getOutputDataMap());
                if(component != null) {
                    JPanel outputPanel = new JPanel(new MigLayout("fill"));
                    outputPanel.setBorder(BorderFactory.createTitledBorder(o.getTitle()));
                    JLabel outputAbstrac = new JLabel(o.getAbstrac());
                    outputAbstrac.setFont(outputAbstrac.getFont().deriveFont(Font.ITALIC));
                    outputPanel.add(outputAbstrac, "wrap");
                    outputPanel.add(component, "wrap");
                    panel.add(outputPanel, "growx, wrap");
                }
            }
        }

        return panel;
    }

    /**
     * Build the UI of the given process according to the given data.
     * @param processExecutionData Process data.
     * @return The UI for the configuration of the process.
     */
    public JComponent buildUIInfo(ProcessExecutionData processExecutionData){
        JPanel panel = new JPanel(new MigLayout("fill"));
        Process p  = processExecutionData.getProcess();
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
     * @param processExecutionData Process data.
     * @return The UI for the configuration of the process.
     */
    public JComponent buildUIExec(ProcessExecutionData processExecutionData){
        JPanel panel = new JPanel(new MigLayout("fill"));

        JPanel executorPanel = new JPanel(new MigLayout());
        executorPanel.setBorder(BorderFactory.createTitledBorder("Executor :"));
        executorPanel.add(new JLabel("localhost"));

        JPanel statusPanel = new JPanel(new MigLayout());
        statusPanel.setBorder(BorderFactory.createTitledBorder("Status :"));
        stateLabel = new JLabel(processExecutionData.getState().getValue());
        statusPanel.add(stateLabel);

        JPanel resultPanel = new JPanel(new MigLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("Result :"));
        for(Output o : processExecutionData.getProcess().getOutput()) {
            JLabel title = new JLabel(o.getTitle()+" : ");
            JLabel result = new JLabel();
            result.putClientProperty("URI", o.getIdentifier());
            outputURILabelMap.put(o.getIdentifier(), result);
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
    public void setOutputs(Map<URI, Object> outputs, String state) {
        for (Map.Entry<URI, Object> entry : outputs.entrySet()) {
            outputURILabelMap.get(entry.getKey()).setText(entry.getValue().toString());
        }
        stateLabel.setText(state);
    }
}
