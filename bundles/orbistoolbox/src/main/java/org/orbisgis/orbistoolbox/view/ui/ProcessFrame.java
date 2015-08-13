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

import groovy.lang.GroovyObject;
import net.miginfocom.swing.MigLayout;
import org.orbisgis.orbistoolbox.model.*;
import org.orbisgis.orbistoolbox.model.Output;
import org.orbisgis.orbistoolbox.model.Process;
import org.orbisgis.orbistoolbox.view.ToolBox;
import org.orbisgis.orbistoolbox.view.ui.dataui.DataUI;
import org.orbisgis.orbistoolbox.view.ui.dataui.DataUIManager;
import org.orbisgis.orbistoolbox.view.utils.ToolBoxIcon;
import org.orbisgis.orbistoolboxapi.annotations.model.OutputAttribute;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.lang.reflect.Field;
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

    /** Map of input data (URI of the corresponding input)*/
    private Map<URI, Object> inputDataMap = new HashMap<>();
    /** Map of output data (URI of the corresponding output)*/
    private Map<URI, Object> outputDataMap = new HashMap<>();

    /** Toolbox */
    private ToolBox toolBox;
    /** Process represented */
    private Process process;

    private JTabbedPane tabbedPane;
    private List<JLabel> labelList;
    private DataUIManager dataUIManager;

    /**
     * Main constructor.
     * @param process Process represented.
     * @param toolBox Toolbox
     */
    public ProcessFrame(Process process, ToolBox toolBox) {
        this.setLayout(new BorderLayout());

        labelList = new ArrayList<>();
        dataUIManager = toolBox.getDataUIManager();

        this.toolBox = toolBox;
        this.process = process;

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Configuration", buildUIConf(process, inputDataMap));
        tabbedPane.addTab("Information", buildUIInfo(process));
        tabbedPane.addTab("Execution", buildUIExec(process));
        this.add(tabbedPane, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new MigLayout());
        JButton runButton = new JButton("run");
        runButton.addActionListener(EventHandler.create(ActionListener.class, this, "runProcess"));
        buttons.add(runButton);
        JButton cancelButton = new JButton("cancel");
        cancelButton.addActionListener(EventHandler.create(ActionListener.class, this, "close"));
        buttons.add(cancelButton);
        this.add(buttons, BorderLayout.PAGE_END);
    }

    /**
     * Returns the output data.
     * @return The output data.
     */
    public Map<URI, Object> getOutputData(){
        return outputDataMap;
    }

    /**
     * Run the process.
     */
    public void runProcess(){
        if(!inputDataMap.isEmpty()) {
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
            if(process != null) {
                GroovyObject groovyObject = toolBox.getProcessManager().executeProcess(process, inputDataMap);
                int i = 0;
                for (Field field : groovyObject.getClass().getDeclaredFields()) {
                    if (field.getAnnotation(OutputAttribute.class) != null) {
                        field.setAccessible(true);
                        try {
                            labelList.get(i).setText(field.get(groovyObject).toString());
                            outputDataMap.put((URI)labelList.get(i).getClientProperty("URI"), field.get(groovyObject).toString());
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        i++;
                    }
                }
            }

        }
    }

    /**
     * Close this windows.
     */
    public void close(){
        this.dispose();
    }

    /**
     * Build the UI of the given process according to the given data.
     * @param p Process to use.
     * @param dataMap Data to use.
     * @return The UI for the configuration of the process.
     */
    public JComponent buildUIConf(Process p, Map<URI, Object> dataMap){
        JPanel panel = new JPanel(new MigLayout("fill"));

        for(Input i : p.getInput()){
            JPanel inputPanel = new JPanel(new MigLayout("fill"));
            inputPanel.setBorder(BorderFactory.createTitledBorder(i.getTitle()));
            JLabel inputAbstrac = new JLabel(i.getAbstrac());
            inputAbstrac.setFont(inputAbstrac.getFont().deriveFont(Font.ITALIC));
            inputPanel.add(inputAbstrac, "wrap");
            DataUI dataUI = dataUIManager.getDataUI(i.getDataDescription().getClass());
            if(dataUI!=null) {
                inputPanel.add(dataUI.createUI(i, dataMap), "wrap");
            }
            panel.add(inputPanel, "growx, wrap");
        }

        return panel;
    }

    /**
     * Build the UI of the given process according to the given data.
     * @param p Process to use.
     * @return The UI for the configuration of the process.
     */
    public JComponent buildUIInfo(Process p){
        JPanel panel = new JPanel(new MigLayout("fill"));

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
     * @param p Process to use.
     * @return The UI for the configuration of the process.
     */
    public JComponent buildUIExec(Process p){
        JPanel panel = new JPanel(new MigLayout("fill"));

        JPanel executorPanel = new JPanel(new MigLayout());
        executorPanel.setBorder(BorderFactory.createTitledBorder("Executor :"));
        executorPanel.add(new JLabel("localhost"));

        JPanel statusPanel = new JPanel(new MigLayout());
        statusPanel.setBorder(BorderFactory.createTitledBorder("Status :"));
        statusPanel.add(new JLabel("iddle"));

        JPanel resultPanel = new JPanel(new MigLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("Result :"));
        for(Output o : p.getOutput()) {
            JLabel title = new JLabel(o.getTitle()+" : ");
            JLabel result = new JLabel();
            result.putClientProperty("URI", o.getIdentifier());
            labelList.add(result);
            resultPanel.add(title);
            resultPanel.add(result, "wrap");
        }

        panel.add(executorPanel, "growx, wrap");
        panel.add(statusPanel, "growx, wrap");
        panel.add(resultPanel, "growx, wrap");

        return panel;
    }
}
