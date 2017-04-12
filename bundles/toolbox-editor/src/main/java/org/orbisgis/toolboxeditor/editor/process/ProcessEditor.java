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

package org.orbisgis.toolboxeditor.editor.process;

import net.miginfocom.swing.MigLayout;
import net.opengis.wps._2_0.*;
import org.orbisgis.sif.components.actions.ActionCommands;
import org.orbisgis.sif.components.actions.ActionDockingListener;
import org.orbisgis.sif.components.actions.DefaultAction;
import org.orbisgis.sif.docking.DockingLocation;
import org.orbisgis.sif.docking.DockingPanelParameters;
import org.orbisgis.sif.edition.EditableElement;
import org.orbisgis.sif.edition.EditorDockable;
import org.orbiswps.client.api.utils.ProcessExecutionType;
import org.orbisgis.toolboxeditor.WpsClientImpl;
import org.orbisgis.toolboxeditor.dataui.DataUI;
import org.orbisgis.toolboxeditor.dataui.DataUIManager;
import org.orbisgis.toolboxeditor.utils.Job;
import org.orbisgis.toolboxeditor.utils.ToolBoxIcon;
import org.orbiswps.server.execution.ProcessExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.EventHandler;
import java.math.BigInteger;
import java.net.URI;
import java.util.*;
import java.util.List;

import static org.orbiswps.client.api.utils.ProcessExecutionType.BASH;
import static org.orbiswps.client.api.utils.ProcessExecutionType.STANDARD;

/**
 * UI for the configuration and the run of a WPS process.
 * It extends the EditorDockable interface to be able to be docked in OrbisGIS.
 *
 * @author Sylvain PALOMINOS
 */
public class ProcessEditor extends JPanel implements EditorDockable {

    /** Uni increment for the scrollbar (speed of the toolbar). */
    private static final int SCROLLBAR_UNIT_INCREMENT = 16;
    /** Properties used to pass information thanks to the swing components. */
    public static final String PROCESS_PROPERTY = "PROGRESS_PROPERTY";
    public static final String PANEL_PROPERTY = "PANEL_PROPERTY";
    public static final String SCROLLPANE_PROPERTY = "SCROLLPANE_PROPERTY";
    public static final String ACTION_TOGGLE_MODE = "ACTION_TOGGLE_MODE";
    /** Name of the EditorDockable. */
    public static final String NAME = "PROCESS_EDITOR";
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(ProcessEditor.class);
    /** Logger object */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessEditor.class);

    /** Editable element of this editor. */
    private ProcessEditableElement processEditableElement;
    /** OrbisGIS Wps client. */
    private WpsClientImpl wpsClient;
    /** Docking parameters. */
    private DockingPanelParameters dockingPanelParameters = new DockingPanelParameters();;
    /** DataUIManager used to create the UI corresponding the the data */
    private DataUIManager dataUIManager;
    /** Error label displayed when the process inputs and output are all defined. */
    private JLabel errorMessage;
    /** Execution mode of the processes. It can be BASH or STANDARD. */
    private ProcessExecutionType mode;
    /** Action changing the process execution mode from BASH to STANDARD or STANDARD to BASH. */
    private DefaultAction toggleModeAction;
    /** Map of the data that will be used to execute the processes.
     * In the STANDARD mode, the map contains entry with the input/output URI as key, and the configured value as value.
     * In the BASH mode, the map contains a string value as key and a map as value. The map value contains the
     * input/outputs URI and the values.
     */
    private Map<URI, Object> dataMap;

    /**
     * Main constructor of the ProcessEditor.
     * @param wpsClient OrbisGIS Wps client.
     * @param processEditableElement Editable element of this editor.
     */
    public ProcessEditor(WpsClientImpl wpsClient, ProcessEditableElement processEditableElement){
        if(processEditableElement.getProcessOffering(wpsClient) == null){
            LOGGER.error(I18N.tr("Unable to load the ProcessEditor {0}.", processEditableElement.getProcessURI()));
            return;
        }
        this.setLayout(new BorderLayout());
        //Sets the attributes
        this.wpsClient = wpsClient;
        this.processEditableElement = processEditableElement;
        this.dataMap = new HashMap<>();
        this.dataUIManager = wpsClient.getDataUIManager();

        //Sets the docking panel parameters
        dockingPanelParameters.setName(NAME+"_"+processEditableElement.getProcess().getTitle().get(0).getValue());
        dockingPanelParameters.setTitleIcon(ToolBoxIcon.getIcon(ToolBoxIcon.PROCESS));
        dockingPanelParameters.setDefaultDockingLocation(
                new DockingLocation(DockingLocation.Location.STACKED_ON, WpsClientImpl.TOOLBOX_REFERENCE));
        dockingPanelParameters.setTitle(processEditableElement.getProcess().getTitle().get(0).getValue());

        //Sets the actions
        ActionCommands dockingActions = new ActionCommands();
        dockingPanelParameters.setDockActions(dockingActions.getActions());
        dockingActions.addPropertyChangeListener(new ActionDockingListener(dockingPanelParameters));
        DefaultAction runAction = new DefaultAction("ACTION_RUN",
                I18N.tr("Run the script"),
                I18N.tr("Run the script"),
                ToolBoxIcon.getIcon(ToolBoxIcon.EXECUTE),
                EventHandler.create(ActionListener.class, this, "runProcess"),
                null);
        dockingActions.addAction(runAction);
        toggleModeAction = new DefaultAction(ACTION_TOGGLE_MODE,
                I18N.tr("Toggle execution mode."),
                I18N.tr("Uses the bash mode."),
                ToolBoxIcon.getIcon(ToolBoxIcon.TOGGLE_MODE),
                EventHandler.create(ActionListener.class, this, "toggleMode"),
                null);
        dockingActions.addAction(toggleModeAction);

        //Sets the execution type of the process and build the UI
        switch(processEditableElement.getProcessExecutionType()){
            case STANDARD:
                this.add(buildSimpleUI(), BorderLayout.CENTER);
                break;
            case BASH:
                this.add(buildBashUI(), BorderLayout.CENTER);
        }
        mode = processEditableElement.getProcessExecutionType();
        this.revalidate();
    }

    /**
     * Changes the process execution mode from STANDARD to BASH or from BASH to STANDARD and rebuild the UI.
     */
    public void toggleMode(){
        switch(mode) {
            case BASH:
                toggleModeAction.setToolTipText(I18N.tr("Uses the simple mode."));
                mode = STANDARD;
                this.removeAll();
                this.add(buildSimpleUI(), BorderLayout.CENTER);
                processEditableElement.resetDataMap();
                this.revalidate();
                break;
            case STANDARD:
                toggleModeAction.setToolTipText(I18N.tr("Uses the bash mode."));
                mode = BASH;
                this.removeAll();
                this.add(buildBashUI(), BorderLayout.CENTER);
                processEditableElement.resetDataMap();
                this.revalidate();
                break;
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
        return editableElement instanceof ProcessEditableElement && editableElement.getId().equals(processEditableElement.getId());
    }

    @Override
    public EditableElement getEditableElement() {
        return processEditableElement;
    }

    @Override
    public void setEditableElement(EditableElement editableElement) {
        this.processEditableElement = (ProcessEditableElement)editableElement;
        dockingPanelParameters.setTitle(processEditableElement.getProcess().getTitle().get(0).getValue());
    }

    /**
     * Run the process if all the mandatory process inputs are defined.
     */
    public void runProcess(){
        URI uri = URI.create(processEditableElement.getProcess().getIdentifier().getValue());
        switch(mode) {
            case STANDARD:
                //First check if all the inputs are defined.
                boolean allDefined = true;
                for (InputDescriptionType input : processEditableElement.getProcess().getInput()) {
                    URI identifier = URI.create(input.getIdentifier().getValue());
                    if (!input.getMinOccurs().equals(new BigInteger("0")) && !dataMap.containsKey(identifier)) {
                        allDefined = false;
                    }
                }

                if (allDefined) {
                    //Launch the process execution and build the Job object with the status info object.
                    StatusInfo statusInfo = wpsClient.executeProcess(uri, dataMap);
                    Job job = new Job(UUID.fromString(statusInfo.getJobID()), processEditableElement.getProcess());

                    //Ask the client to validate the execution by closing the editor an saving the Job.
                    wpsClient.validateInstance(this, job);


                    //Sets the job. It should be don after the client validation to be sure that the job has
                    // the listener add.
                    job.setStartTime(System.currentTimeMillis());
                    job.setStatus(statusInfo);
                    job.setProcessState(ProcessExecutionListener.ProcessState.IDLE);

                    //Dirty way to close the ProcessEditor
                    try {
                        Robot robot = new Robot();
                        robot.keyPress(KeyEvent.VK_CONTROL);
                        robot.keyPress(KeyEvent.VK_F4);
                        robot.delay(100);
                        robot.keyRelease(KeyEvent.VK_CONTROL);
                        robot.keyRelease(KeyEvent.VK_F4);
                    } catch (AWTException e) {
                        e.printStackTrace();
                    }
                } else {
                    errorMessage.setText("Please, configure all the inputs/outputs before executing.");
                }
                break;
            case BASH:
                //As the execution mode is BASH, the map contains all the maps for each process instance.
                // So a process is executed for each map.
                for(Map.Entry<URI, Object> entry : dataMap.entrySet()) {
                    Map<URI, Object> map = null;
                    if(entry.getValue() instanceof Map) {
                        map = (Map<URI, Object>) entry.getValue();
                    }
                    if(map != null) {
                        //First check if all the inputs are defined.
                        allDefined = true;
                        for (InputDescriptionType input : processEditableElement.getProcess().getInput()) {
                            URI identifier = URI.create(input.getIdentifier().getValue());
                            if (!input.getMinOccurs().equals(new BigInteger("0")) && !map.containsKey(identifier)) {
                                allDefined = false;
                            }
                        }

                        if (allDefined) {
                            //Launch the process execution and build the Job object with the status info object.
                            StatusInfo statusInfo = wpsClient.executeProcess(uri, map);
                            Job job = new Job(UUID.fromString(statusInfo.getJobID()), processEditableElement.getProcess());

                            //Ask the client to validate the execution by closing the editor an saving the Job.
                            wpsClient.validateInstance(this, job);

                            //Sets the job. It should be don after the client validation to be sure that the job has
                            // the listener add.
                            job.setStartTime(System.currentTimeMillis());
                            job.setStatus(statusInfo);
                            job.setProcessState(ProcessExecutionListener.ProcessState.IDLE);

                            //Dirty way to close the ProcessEditor
                            try {
                                Robot robot = new Robot();
                                robot.keyPress(KeyEvent.VK_CONTROL);
                                robot.keyPress(KeyEvent.VK_F4);
                                robot.delay(100);
                                robot.keyRelease(KeyEvent.VK_CONTROL);
                                robot.keyRelease(KeyEvent.VK_F4);
                            } catch (AWTException e) {
                                e.printStackTrace();
                            }
                        } else {
                            errorMessage.setText("Please, configure all the inputs/outputs before executing.");
                        }
                    }
                }
                break;
        }
    }

    /**
     * Build the UI of the given process according to the given data.
     * The UI is composed of a top panel with the process information and a bottom one with the swing component for the
     * data configuration. The bottom panel is created with the DataUI class declared in the DataUIManager.
     * @return The UI for the configuration of the process.
     */
    private JComponent buildSimpleUI(){
        ProcessDescriptionType process = processEditableElement.getProcess();
        //Build the DataMap that will contains the process data
        dataMap = new HashMap<>();
        dataMap.putAll(processEditableElement.getDataMap());

        JPanel returnPanel = new JPanel(new MigLayout("fill"));

        //Build the panel containing the process information
        AbstractScrollPane processPanel = new AbstractScrollPane();
        processPanel.setLayout(new MigLayout("fill, ins 0, gap 0"));
        //The abstract label
        JLabel label = new JLabel("<html>"+process.getAbstract().get(0).getValue()+"</html>");
        label.setFont(label.getFont().deriveFont(Font.ITALIC));
        processPanel.add(label, "growx, span");
        //The process version
        String versionStr = I18N.tr("Version : ");
        if(processEditableElement.getProcessOffering(wpsClient).getProcessVersion().isEmpty()){
            versionStr += I18N.tr("unknown");
        }
        else{
            versionStr += processEditableElement.getProcessOffering(wpsClient).getProcessVersion();
        }
        JLabel version = new JLabel(versionStr);
        version.setFont(version.getFont().deriveFont(Font.ITALIC));
        processPanel.add(version, "growx, span");
        //Build the border panel containing a scrollpane with the process information
        JPanel borderProcessPanel = new JPanel(new MigLayout("fill, ins 0, gap 0"));
        borderProcessPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY), I18N.tr("Description")));
        borderProcessPanel.add(new JScrollPane(processPanel), "growx");
        returnPanel.add(borderProcessPanel, "wrap, growx, height ::30%");

        //Build the panel with all the swing components for the data configuration.
        boolean noParameters = true;
        JPanel parameterPanel = new JPanel(new MigLayout("fill"));
        JScrollPane scrollPane = new JScrollPane(parameterPanel);

        //For each input, generate the UI and add it
        for(InputDescriptionType i : process.getInput()){
            DataUI dataUI = dataUIManager.getDataUI(i.getDataDescription().getValue().getClass());

            if(dataUI!=null) {
                //Retrieve the component containing all the UI components. The data map is passed to the DataUI to
                // allow it to put the data inside
                JComponent uiComponent = dataUI.createUI(i, dataMap, DataUI.Orientation.VERTICAL);
                if(uiComponent != null) {
                    noParameters = false;
                    //If the input is optional, hide it
                    if(i.getMinOccurs().equals(new BigInteger("0"))) {
                        uiComponent.setVisible(false);
                        //This panel is the one which contains the header with the title of the input and
                        // the hide/show button
                        JPanel contentPanel = new JPanel(new MigLayout("fill, ins 0, gap 0"));
                        JPanel hideShowPanel = new JPanel(new MigLayout("ins 0, gap 0"));
                        //Sets the button to make it shown as just an icon
                        JButton showButton = new JButton(ToolBoxIcon.getIcon(ToolBoxIcon.BTNRIGHT));
                        showButton.setBorderPainted(false);
                        showButton.setMargin(new Insets(0, 0, 0, 0));
                        showButton.setContentAreaFilled(false);
                        showButton.setOpaque(false);
                        showButton.setFocusable(false);
                        showButton.putClientProperty("upPanel", hideShowPanel);
                        showButton.addMouseListener(EventHandler.create(MouseListener.class,
                                this, "onClickButton", "source", "mouseClicked"));
                        hideShowPanel.add(showButton);
                        hideShowPanel.add(new JLabel(i.getTitle().get(0).getValue() + " (optional)"), "growx, span");
                        hideShowPanel.setToolTipText("Hide/Show option");
                        hideShowPanel.putClientProperty("body", uiComponent);
                        hideShowPanel.putClientProperty("parent", contentPanel);
                        hideShowPanel.putClientProperty("button", showButton);
                        hideShowPanel.putClientProperty("scrollPane", scrollPane);
                        hideShowPanel.addMouseListener(EventHandler.create(MouseListener.class,
                                this, "onClickHeader", "source", "mouseClicked"));
                        contentPanel.add(hideShowPanel, "growx, span");
                        parameterPanel.add(contentPanel, "growx, span");
                    }
                    else{
                        parameterPanel.add(new JLabel(i.getTitle().get(0).getValue()), "growx, span");
                        parameterPanel.add(uiComponent, "growx, span");
                    }
                    parameterPanel.add(new JSeparator(), "growx, span");
                }
            }
        }
        //For each input, generate the UI and add it
        for(OutputDescriptionType o : process.getOutput()){
            DataUI dataUI = dataUIManager.getDataUI(o.getDataDescription().getValue().getClass());
            if(dataUI!=null) {
                JComponent component = dataUI.createUI(o, dataMap, DataUI.Orientation.VERTICAL);
                if(component != null) {
                    noParameters = false;
                    parameterPanel.add(new JLabel(o.getTitle().get(0).getValue()), "growx, span");
                    parameterPanel.add(component, "growx, span");
                    parameterPanel.add(new JSeparator(), "growx, span");
                }
            }
        }
        //If there is input or output to display, adds the inside a JScrollPane
        if(!noParameters) {
            errorMessage = new JLabel();
            errorMessage.setForeground(Color.RED);
            parameterPanel.add(errorMessage, "growx, wrap");
            scrollPane.getVerticalScrollBar().setUnitIncrement(SCROLLBAR_UNIT_INCREMENT);
            scrollPane.getHorizontalScrollBar().setUnitIncrement(SCROLLBAR_UNIT_INCREMENT);
            JPanel borderParameterPanel = new JPanel(new MigLayout("fill, ins 0, gap 0"));
            borderParameterPanel.setBorder(BorderFactory.createTitledBorder(I18N.tr("Parameter(s)")));
            borderParameterPanel.add(scrollPane, "growx");

            returnPanel.add(borderParameterPanel, "wrap, growx, height ::70%");
        }
        return returnPanel;
    }

    /**
     * Build the UI of the given process according to the given data.
     * As the UI is in BASH mode, it is displayed by row, one row corresponding to a process instance.
     * The input/output title are displayed on the first row and then the swing component are displayed row by row.
     * On the start of each row, a button is used to remove the row.
     * At the end of the UI, an other button add a new row.
     * @return The UI for the configuration of the process.
     */
    private JComponent buildBashUI(){
        //Gets process basic information : process, uri and data map
        URI uri = URI.create(processEditableElement.getProcess().getIdentifier().getValue());
        ProcessDescriptionType process = wpsClient.getProcessCopy(uri);
        dataMap = new HashMap<>();

        JPanel returnPanel = new JPanel(new MigLayout("fill"));

        //Build the first part of the UI containing the process information
        AbstractScrollPane processPanel = new AbstractScrollPane();
        processPanel.setLayout(new MigLayout("fill, ins 0, gap 0"));
        //The process title
        JLabel label = new JLabel("<html>"+process.getAbstract().get(0).getValue()+"</html>");
        label.setFont(label.getFont().deriveFont(Font.ITALIC));
        processPanel.add(label, "growx, span");
        //The process version
        String versionStr = I18N.tr("Version : ");
        if(processEditableElement.getProcessOffering(wpsClient).getProcessVersion().isEmpty()){
            versionStr += I18N.tr("unknown");
        }
        else{
            versionStr += processEditableElement.getProcessOffering(wpsClient).getProcessVersion();
        }
        JLabel version = new JLabel(versionStr);
        version.setFont(version.getFont().deriveFont(Font.ITALIC));
        processPanel.add(version, "growx, span");
        //The container panel
        JPanel borderProcessPanel = new JPanel(new MigLayout("fill, ins 0, gap 0"));
        borderProcessPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY), I18N.tr("Description")));
        borderProcessPanel.add(new JScrollPane(processPanel), "growx");
        returnPanel.add(borderProcessPanel, "wrap, growx, height ::30%");

        JPanel panel = new JPanel(new MigLayout("fill"));
        JScrollPane scrollPane = new JScrollPane(panel);

        //Creates the panel that will contains all the inputs.
        JPanel borderParameterPanel = new JPanel(new MigLayout("fill, ins 0, gap 0"));
        borderParameterPanel.setBorder(BorderFactory.createTitledBorder(I18N.tr("Parameter(s)")));
        JPanel parameterPanel = new JPanel(new MigLayout("fill, ins 5"));

        //Gets the list of input/output title if they have a displayed component
        List<DescriptionType> descriptionTypeList = new ArrayList<>();
        for(OutputDescriptionType o : process.getOutput()){
            if(dataUIManager.getDataUI(o.getDataDescription().getValue().getClass()) != null) {
                descriptionTypeList.add(o);
            }
        }
        for(InputDescriptionType i : process.getInput()){
            if(dataUIManager.getDataUI(i.getDataDescription().getValue().getClass()) != null) {
                descriptionTypeList.add(i);
            }
        }

        //Add an empty label because of the miglayout : the first component of a row will be the remove button
        parameterPanel.add(new JLabel());

        //Adds all the input/output titles in the first row
        for(int i = 0; i < descriptionTypeList.size(); i++){
            String migOption = "growx";
            if(i == descriptionTypeList.size()-1){
                if(!migOption.isEmpty()){
                    migOption += ", ";
                }
                migOption += "wrap";
            }
            DescriptionType descriptionType = descriptionTypeList.get(i);
            DataUI dataUI = null;
            if(descriptionType instanceof InputDescriptionType) {
                dataUI = dataUIManager.getDataUI(
                        ((InputDescriptionType)descriptionType).getDataDescription().getValue().getClass());
            }
            if(descriptionType instanceof OutputDescriptionType) {
                dataUI = dataUIManager.getDataUI(
                        ((OutputDescriptionType)descriptionType).getDataDescription().getValue().getClass());
            }

            if(dataUI!=null) {
                //Retrieve the component containing all the UI components.
                JComponent uiComponent = dataUI.createUI(descriptionType,new HashMap<URI, Object>(), DataUI.Orientation.HORIZONTAL);
                if(uiComponent != null) {
                    parameterPanel.add(new JLabel(descriptionType.getTitle().get(0).getValue()), migOption);
                }
            }
        }
        parameterPanel.add(new JSeparator(), "growx, span");

        boolean isRowAdd = false;
        //Adds a first component row if the default data map is empty
        if(processEditableElement.getDataMap() == null || processEditableElement.getDataMap().size() == 0) {
            onAddBashRow(wpsClient.getProcessCopy(uri), parameterPanel, new HashMap<URI, Object>());
            isRowAdd = true;
        }
        //If the default data map contains maps of data, adds a row for each map
        else{
            for(Map.Entry<URI, Object> entry : processEditableElement.getDataMap().entrySet()){
                if(entry.getValue() instanceof Map) {
                    onAddBashRow(wpsClient.getProcessCopy(uri), parameterPanel, (Map<URI, Object>)entry.getValue());
                    isRowAdd = true;
                }
            }
        }
        if(!isRowAdd){
            onAddBashRow(wpsClient.getProcessCopy(uri), parameterPanel, processEditableElement.getDataMap());
        }

        //Adds the add new row button at the very en of the UI
        JButton addButton = new JButton(ToolBoxIcon.getIcon(ToolBoxIcon.ADD));
        addButton.putClientProperty(PROCESS_PROPERTY, process);
        addButton.putClientProperty(PANEL_PROPERTY, parameterPanel);
        addButton.putClientProperty(SCROLLPANE_PROPERTY, scrollPane);
        addButton.addActionListener(EventHandler.create(ActionListener.class, this, "onAddBashRow", "source"));
        addButton.setBorderPainted(false);
        addButton.setContentAreaFilled(false);
        parameterPanel.add(addButton, "wrap");

        //Add the panel in a scroll pane with the error message used when all the field are not configured on running
        panel.add(parameterPanel, "growx, span");
        errorMessage = new JLabel();
        errorMessage.setForeground(Color.RED);
        panel.add(errorMessage, "growx, wrap");
        scrollPane.getVerticalScrollBar().setUnitIncrement(SCROLLBAR_UNIT_INCREMENT);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(SCROLLBAR_UNIT_INCREMENT);
        borderParameterPanel.add(scrollPane, "span, growx");

        returnPanel.add(borderParameterPanel, "wrap, growx, height ::70%");
        return returnPanel;
    }

    /**
     * Method called on clicking on the add new row button.
     * Its adds a new row of swing components for running one more process instance.
     *
     * @param source Source of the click
     */
    public void onAddBashRow(Object source){
        if(source instanceof JButton){
            //First test if the JButton contains all the properties needed for the UI generation
            JButton addButton = (JButton) source;
            ProcessDescriptionType process;
            if(addButton.getClientProperty(PROCESS_PROPERTY) instanceof ProcessDescriptionType){
                process = (ProcessDescriptionType) addButton.getClientProperty(PROCESS_PROPERTY);
            }
            else{
                LOGGER.warn(I18N.tr("Unable to add a new wps bash row. The property process is invalid."));
                return;
            }
            JPanel parameterPanel;
            if(addButton.getClientProperty(PANEL_PROPERTY) instanceof JPanel){
                parameterPanel = (JPanel) addButton.getClientProperty(PANEL_PROPERTY);
            }
            else{
                LOGGER.warn(I18N.tr("Unable to add a new wps bash row. The property parameterPanel is invalid."));
                return;
            }
            JScrollPane scrollPane;
            if(addButton.getClientProperty(SCROLLPANE_PROPERTY) instanceof JScrollPane){
                scrollPane = (JScrollPane) addButton.getClientProperty(SCROLLPANE_PROPERTY);
            }
            else{
                LOGGER.warn(I18N.tr("Unable to add a new wps bash row. The property scrollPane is invalid."));
                return;
            }
            parameterPanel.remove(addButton);
            ProcessDescriptionType processCopy =
                    wpsClient.getProcessCopy(URI.create(process.getIdentifier().getValue()));
            if(processCopy != null) {
                onAddBashRow(processCopy, parameterPanel, null);
            }
            else{
                LOGGER.error(I18N.tr("Unable to get a copy of the process {0}.", process.getTitle().get(0).getValue()));
            }
            parameterPanel.add(addButton, "wrap");
            scrollPane.validate();
            scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
        }
    }

    /**
     * Adds to the parameterPanel a new row of swing components for the configuration of the data for the process
     * execution with the given default data map.
     *
     * @param process Process to execute, used to get the swing components with the DataUIManager class.
     * @param parameterPanel Panel where the row of components should be add.
     * @param defaultDataMap Map of the default data to use.
     */
    private void onAddBashRow(ProcessDescriptionType process, JPanel parameterPanel, Map<URI, Object> defaultDataMap){
        //Generate the data map to use and add it to the map containing the final data.
        // The final map will be used on execution the processes.
        HashMap<URI, Object> map = new HashMap<>();
        map.putAll(processEditableElement.getDataMap());
        if(defaultDataMap != null) {
            map.putAll(defaultDataMap);
        }
        URI key = URI.create(UUID.randomUUID().toString());
        dataMap.put(key, map);

        //Gets the list of input/output to display
        List<DescriptionType> descriptionTypeList = new ArrayList<>();
        for(OutputDescriptionType o : process.getOutput()){
            if(dataUIManager.getDataUI(o.getDataDescription().getValue().getClass()) != null) {
                descriptionTypeList.add(o);
            }
        }
        for(InputDescriptionType i : process.getInput()){
            if(dataUIManager.getDataUI(i.getDataDescription().getValue().getClass()) != null) {
                descriptionTypeList.add(i);
            }
        }
        //Adds the remove button at the start of the row
        JButton removeRow = new JButton(ToolBoxIcon.getIcon(ToolBoxIcon.DELETE));
        removeRow.setContentAreaFilled(false);
        removeRow.setBorderPainted(false);
        removeRow.putClientProperty("dataMap", dataMap);
        removeRow.putClientProperty("key", key);
        removeRow.putClientProperty("parameterPanel", parameterPanel);
        List<JComponent> componentList = new ArrayList<>();
        componentList.add(removeRow);
        removeRow.putClientProperty("componentList", componentList);
        removeRow.addActionListener(EventHandler.create(ActionListener.class, this, "onRemoveRow", "source"));
        parameterPanel.add(removeRow);
        //Adds the swing components of the inputs/outputs. The components ar also stored to remove them later form the
        // panel on clicking on the remove row button.
        for(int i = 0; i < descriptionTypeList.size(); i++){
            String migOption = "growx";
            if(i == descriptionTypeList.size()-1){
                if(!migOption.isEmpty()){
                    migOption += ", ";
                }
                migOption += "wrap";
            }
            DescriptionType descriptionType = descriptionTypeList.get(i);
            DataUI dataUI = null;
            if(descriptionType instanceof InputDescriptionType) {
                dataUI = dataUIManager.getDataUI(
                        ((InputDescriptionType)descriptionType).getDataDescription().getValue().getClass());
            }
            if(descriptionType instanceof OutputDescriptionType) {
                dataUI = dataUIManager.getDataUI(
                        ((OutputDescriptionType)descriptionType).getDataDescription().getValue().getClass());
            }

            if(dataUI!=null) {
                //Retrieve the component containing all the UI components.
                JComponent uiComponent = dataUI.createUI(descriptionType, map, DataUI.Orientation.HORIZONTAL);
                if(uiComponent != null) {
                    parameterPanel.add(uiComponent, migOption);
                    componentList.add(uiComponent);
                }
            }
        }
        JSeparator separator = new JSeparator();
        parameterPanel.add(separator, "growx, span");
        componentList.add(separator);
    }

    /**
     * Action done on clicking on the remove row button.
     *
     * @param source Source of the click
     */
    public void onRemoveRow(Object source){
        if(source instanceof JButton){
            //Gets the basic object used to remove the row
            JButton removeRow = (JButton)source;
            Map dataMap = (Map) removeRow.getClientProperty("dataMap");
            URI key = (URI) removeRow.getClientProperty("key");
            dataMap.remove(key);

            //Removes from the
            JPanel parameterPanel = (JPanel) removeRow.getClientProperty("parameterPanel");
            List<JComponent> componentList = (List<JComponent>) removeRow.getClientProperty("componentList");
            for(JComponent component : componentList){
                parameterPanel.remove(component);
            }
            parameterPanel.revalidate();
        }
    }

    /**
     * When the title arrow button is clicked, expand the input/output components.
     * @param source Arrow button.
     */
    public void onClickButton(Object source) {
        JButton button = (JButton)source;
        onClickHeader(button.getClientProperty("upPanel"));
    }

    /**
     * When the title is clicked, expand the input/output components. It is used for the optional input/output in the
     * STANDARD execution mode.
     * @param source Title text.
     */
    public void onClickHeader(Object source){
        JPanel panel = (JPanel)source;
        JButton showButton = (JButton)panel.getClientProperty("button");
        final JComponent body = (JComponent)panel.getClientProperty("body");
        JComponent parent = (JComponent)panel.getClientProperty("parent");
        final JScrollPane scrollPane = (JScrollPane)panel.getClientProperty("scrollPane");
        boolean isVisible = body.isVisible();
        if(isVisible) {
            body.setVisible(false);
            parent.remove(body);
            showButton.setIcon(ToolBoxIcon.getIcon(ToolBoxIcon.BTNRIGHT));
        }
        else{
            body.setVisible(true);
            parent.add(body, "growx, span");
            showButton.setIcon(ToolBoxIcon.getIcon(ToolBoxIcon.BTNDOWN));
            //Later scrollDown to the element
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    scrollPane.scrollRectToVisible(body.getBounds());
                }
            });
            scrollPane.scrollRectToVisible(body.getBounds());
        }
        parent.revalidate();
    }

    /**
     * This extension of JPanel implementing Scrollable is used to create a panel which is only able to have a vertical
     * scrolling.
     */
    private class AbstractScrollPane extends JPanel implements Scrollable{

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle rectangle, int i, int i1) {
            return 5;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle rectangle, int i, int i1) {
            return 5;
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
