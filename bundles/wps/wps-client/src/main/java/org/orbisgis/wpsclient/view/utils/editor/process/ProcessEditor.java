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

package org.orbisgis.wpsclient.view.utils.editor.process;

import net.miginfocom.swing.MigLayout;
import net.opengis.wps._2_0.*;
import org.orbisgis.sif.components.actions.ActionCommands;
import org.orbisgis.sif.components.actions.ActionDockingListener;
import org.orbisgis.sif.components.actions.DefaultAction;
import org.orbisgis.sif.docking.DockingLocation;
import org.orbisgis.sif.docking.DockingPanelParameters;
import org.orbisgis.sif.edition.EditableElement;
import org.orbisgis.sif.edition.EditorDockable;
import org.orbisgis.wpsclient.WpsClientImpl;
import org.orbisgis.wpsclient.view.ui.dataui.DataUI;
import org.orbisgis.wpsclient.view.ui.dataui.DataUIManager;
import org.orbisgis.wpsclient.view.utils.ToolBoxIcon;
import org.orbisgis.wpsservice.controller.execution.ProcessExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigInteger;
import java.net.URI;
import java.util.*;
import java.util.List;

/**
 * UI for the configuration and the run of a WPS process.
 * It extends the EditorDockable interface to be able to be docked in OrbisGIS.
 *
 * @author Sylvain PALOMINOS
 */
public class ProcessEditor extends JPanel implements EditorDockable, PropertyChangeListener {

    private static final int SCROLLBAR_UNIT_INCREMENT = 16;
    public static final String PROCESS_PROPERTY = "PROCESS_PROPERTY";
    public static final String PANEL_PROPERTY = "PANEL_PROPERTY";
    public static final String SCROLLPANE_PROPERTY = "SCROLLPANE_PROPERTY";
    public static final String ACTION_TOGGLE_MODE = "ACTION_TOGGLE_MODE";
    public static final String SIMPLE_MODE = "SIMPLE_MODE";
    public static final String BASH_MODE = "BASH_MODE";
    /** Name of the EditorDockable. */
    public static final String NAME = "PROCESS_EDITOR";
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(ProcessEditor.class);
    /** Logger object */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessEditor.class);

    private ProcessEditableElement pee;
    private WpsClientImpl wpsClient;
    private DockingPanelParameters dockingPanelParameters;
    /** DataUIManager used to create the UI corresponding the the data */
    private DataUIManager dataUIManager;
    /** Tells if the this editor has been open or not. */
    private boolean alive;
    /** Error label displayed when the process inputs and output are all defined. */
    private JLabel errorMessage;
    private String mode;
    private DefaultAction toggleModeAction;
    private HashMap<URI, Object> dataMap;

    public ProcessEditor(WpsClientImpl wpsClient, ProcessEditableElement pee){
        this.alive = true;
        this.wpsClient = wpsClient;
        this.pee = pee;
        this.pee.addPropertyChangeListener(this);
        this.dataMap = new HashMap<>();
        dockingPanelParameters = new DockingPanelParameters();
        dockingPanelParameters.setName(NAME+"_"+pee.getProcess().getTitle());
        dockingPanelParameters.setTitleIcon(ToolBoxIcon.getIcon("process"));
        dockingPanelParameters.setDefaultDockingLocation(
                new DockingLocation(DockingLocation.Location.STACKED_ON, WpsClientImpl.TOOLBOX_REFERENCE));
        dockingPanelParameters.setTitle(pee.getProcessReference());
        this.setLayout(new BorderLayout());
        dataUIManager = wpsClient.getDataUIManager();

        ActionCommands dockingActions = new ActionCommands();
        dockingPanelParameters.setDockActions(dockingActions.getActions());
        dockingActions.addPropertyChangeListener(new ActionDockingListener(dockingPanelParameters));
        DefaultAction runAction = new DefaultAction("ACTION_RUN",
                "ACTION_RUN",
                I18N.tr("Run the script"),
                ToolBoxIcon.getIcon("execute"),
                EventHandler.create(ActionListener.class, this, "runProcess"),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK)).setButtonGroup("custom");
        dockingActions.addAction(runAction);
        toggleModeAction = new DefaultAction(ACTION_TOGGLE_MODE,
                ACTION_TOGGLE_MODE,
                I18N.tr("Uses the bash mode."),
                ToolBoxIcon.getIcon(ToolBoxIcon.TOGGLE_MODE),
                EventHandler.create(ActionListener.class, this, "toggleMode"),
                null);
        dockingActions.addAction(toggleModeAction);
        mode = SIMPLE_MODE;
        this.add(buildSimpleUI(), BorderLayout.CENTER);
        this.revalidate();
    }

    public void toggleMode(){
        switch(mode) {
            case BASH_MODE:
                toggleModeAction.setToolTipText(I18N.tr("Uses the simple mode."));
                mode = SIMPLE_MODE;
                this.removeAll();
                this.add(buildSimpleUI(), BorderLayout.CENTER);
                pee.setInputDataMap(new HashMap<URI, Object>());
                pee.setOutputDataMap(new HashMap<URI, Object>());
                break;
            case SIMPLE_MODE:
                toggleModeAction.setToolTipText(I18N.tr("Uses the bash mode."));
                mode = BASH_MODE;
                this.removeAll();
                this.add(buildBashUI(), BorderLayout.CENTER);
                pee.setInputDataMap(new HashMap<URI, Object>());
                pee.setOutputDataMap(new HashMap<URI, Object>());
                break;
        }
    }

    /**
     * Sets if this editor has been open..
     * @param alive True if this editor is open, false otherwise.
     */
    public void setAlive(boolean alive){
        this.alive = alive;
    }

    @Override
    public DockingPanelParameters getDockingParameters() {
        //if this editor is not visible but was open, close it.
        if(!dockingPanelParameters.isVisible() && alive){
            alive = false;
            wpsClient.killEditor(this);
        }
        return dockingPanelParameters;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public boolean match(EditableElement editableElement) {
        //Return true if the editable is the one contained by the Process editor
        return editableElement instanceof ProcessEditableElement && editableElement.getId().equals(pee.getId());
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
        if(propertyChangeEvent.getPropertyName().equals(ProcessEditableElement.LOG_PROPERTY)){
            AbstractMap.Entry<String, Color> entry = (AbstractMap.Entry)propertyChangeEvent.getNewValue();
        }
        if(propertyChangeEvent.getPropertyName().equals(ProcessEditableElement.CANCEL)){
            StatusInfo statusInfo = wpsClient.dismissJob((UUID)propertyChangeEvent.getNewValue());
            Job job = pee.getJob(UUID.fromString(statusInfo.getJobID()));
            job.setProcessState(ProcessExecutionListener.ProcessState.valueOf(statusInfo.getStatus().toUpperCase()));
            job.addRefreshDate(statusInfo.getNextPoll());
        }
        if(propertyChangeEvent.getPropertyName().equals(ProcessEditableElement.REFRESH_STATUS)){
            StatusInfo statusInfo = wpsClient.getJobStatus((UUID)propertyChangeEvent.getNewValue());
            Job job = pee.getJob(UUID.fromString(statusInfo.getJobID()));
            job.setProcessState(ProcessExecutionListener.ProcessState.valueOf(statusInfo.getStatus().toUpperCase()));
            job.addRefreshDate(statusInfo.getNextPoll());
        }
        if(propertyChangeEvent.getPropertyName().equals(ProcessEditableElement.GET_RESULTS)){
            Result result = wpsClient.getJobResult((UUID)propertyChangeEvent.getNewValue());
            Job job = pee.getJob(UUID.fromString(result.getJobID()));
            job.setResult(result);
        }
    }

    /**
     * Run the process if all the mandatory process inputs are defined.
     */
    public void runProcess(){
        switch(mode) {
            case SIMPLE_MODE:
                //First check if all the inputs are defined.
                boolean allDefined = true;
                for (InputDescriptionType input : pee.getProcess().getInput()) {
                    URI identifier = URI.create(input.getIdentifier().getValue());
                    if (!input.getMinOccurs().equals(new BigInteger("0")) && !dataMap.containsKey(identifier)) {
                        allDefined = false;
                    }
                }

                if (allDefined) {

                    //Run the process in a separated thread
                    StatusInfo statusInfo = wpsClient.executeProcess(pee.getProcess(), dataMap);
                    Job job = pee.newJob(UUID.fromString(statusInfo.getJobID()));

                    //Then launch the process execution
                    wpsClient.validateInstance(this, job.getId());

                    job.setStartTime(System.currentTimeMillis());
                    job.setProcessState(ProcessExecutionListener.ProcessState.RUNNING);
                    job.addRefreshDate(statusInfo.getNextPoll());
                } else {
                    errorMessage.setText("Please, configure all the inputs/outputs before executing.");
                }
                break;
            case BASH_MODE:
                for(Map.Entry<URI, Object> entry : dataMap.entrySet()) {
                    Map<URI, Object> map = null;
                    if(entry.getValue() instanceof Map) {
                        map = (Map<URI, Object>) entry.getValue();
                    }
                    if(map != null) {
                        //First check if all the inputs are defined.
                        allDefined = true;
                        for (InputDescriptionType input : pee.getProcess().getInput()) {
                            URI identifier = URI.create(input.getIdentifier().getValue());
                            if (!input.getMinOccurs().equals(new BigInteger("0")) && !map.containsKey(identifier)) {
                                allDefined = false;
                            }
                        }

                        if (allDefined) {
                            //Run the process in a separated thread
                            StatusInfo statusInfo = wpsClient.executeProcess(pee.getProcess(), map);
                            Job job = pee.newJob(UUID.fromString(statusInfo.getJobID()));

                            //Then launch the process execution
                            wpsClient.validateInstance(this, job.getId());

                            job.setStartTime(System.currentTimeMillis());
                            job.setProcessState(ProcessExecutionListener.ProcessState.RUNNING);
                            job.addRefreshDate(statusInfo.getNextPoll());

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
     * @return The UI for the configuration of the process.
     */
    private JComponent buildSimpleUI(){
        ProcessDescriptionType process = pee.getProcess();
        dataMap = new HashMap<>();
        dataMap.putAll(pee.getInputDataMap());
        dataMap.putAll(pee.getOutputDataMap());

        JPanel returnPanel = new JPanel(new MigLayout("fill"));

        AbstractScrollPane processPanel = new AbstractScrollPane();
        processPanel.setLayout(new MigLayout("fill, ins 0, gap 0"));
        processPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY), I18N.tr("Description")));
        JLabel label = new JLabel("<html>"+process.getAbstract().get(0).getValue()+"</html>");
        label.setFont(label.getFont().deriveFont(Font.ITALIC));
        processPanel.add(label, "growx, span");
        String versionStr = I18N.tr("Version : ");
        if(pee.getProcessOffering().getProcessVersion().isEmpty()){
            versionStr += I18N.tr("unknown");
        }
        else{
            versionStr += pee.getProcessOffering().getProcessVersion();
        }
        JLabel version = new JLabel(versionStr);
        version.setFont(version.getFont().deriveFont(Font.ITALIC));
        processPanel.add(version, "growx, span");
        returnPanel.add(new JScrollPane(processPanel), "wrap, growx, height ::30%");

        JPanel panel = new JPanel(new MigLayout("fill"));
        JScrollPane scrollPane = new JScrollPane(panel);

        boolean noParameters = true;
        // Put all the default values in the datamap
        pee.setDefaultInputValues(dataUIManager.getInputDefaultValues(process));
        //Creates the panel that will contains all the inputs.
        JPanel parameterPanel = new JPanel(new MigLayout("fill"));
        parameterPanel.setBorder(BorderFactory.createTitledBorder(I18N.tr("Parameter(s)")));

        for(InputDescriptionType i : process.getInput()){
            DataUI dataUI = dataUIManager.getDataUI(i.getDataDescription().getValue().getClass());

            if(dataUI!=null) {
                //Retrieve the component containing all the UI components.
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
                        JButton showButton = new JButton(ToolBoxIcon.getIcon("btnright"));
                        showButton.setBorderPainted(false);
                        showButton.setMargin(new Insets(0, 0, 0, 0));
                        showButton.setContentAreaFilled(false);
                        showButton.setOpaque(false);
                        showButton.setFocusable(false);
                        showButton.putClientProperty("upPanel", hideShowPanel);
                        showButton.addMouseListener(EventHandler.create(MouseListener.class,
                                this, "onClickButton", "source", "mouseClicked"));
                        hideShowPanel.add(showButton);
                        hideShowPanel.add(new JLabel(i.getTitle().get(0).getValue()), "growx, span");
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
        if(!noParameters) {
            panel.add(parameterPanel, "growx, span");
            errorMessage = new JLabel();
            errorMessage.setForeground(Color.RED);
            panel.add(errorMessage, "growx, wrap");
            scrollPane.getVerticalScrollBar().setUnitIncrement(SCROLLBAR_UNIT_INCREMENT);
            scrollPane.getHorizontalScrollBar().setUnitIncrement(SCROLLBAR_UNIT_INCREMENT);

            returnPanel.add(scrollPane, "wrap, growx, height ::70%");
        }
        return returnPanel;
    }

    /**
     * Build the UI of the given process according to the given data.
     * @return The UI for the configuration of the process.
     */
    private JComponent buildBashUI(){
        ProcessDescriptionType process = pee.getProcess();
        dataMap = new HashMap<>();

        JPanel returnPanel = new JPanel(new MigLayout("fill"));

        AbstractScrollPane processPanel = new AbstractScrollPane();
        processPanel.setLayout(new MigLayout("fill, ins 0, gap 0"));
        processPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY), I18N.tr("Description")));
        JLabel label = new JLabel("<html>"+process.getAbstract().get(0).getValue()+"</html>");
        label.setFont(label.getFont().deriveFont(Font.ITALIC));
        processPanel.add(label, "growx, span");
        String versionStr = I18N.tr("Version : ");
        if(pee.getProcessOffering().getProcessVersion().isEmpty()){
            versionStr += I18N.tr("unknown");
        }
        else{
            versionStr += pee.getProcessOffering().getProcessVersion();
        }
        JLabel version = new JLabel(versionStr);
        version.setFont(version.getFont().deriveFont(Font.ITALIC));
        processPanel.add(version, "growx, span");
        returnPanel.add(new JScrollPane(processPanel), "wrap, growx, height ::30%");

        JPanel panel = new JPanel(new MigLayout("fill"));
        JScrollPane scrollPane = new JScrollPane(panel);

        // Put all the default values in the datamap
        pee.setDefaultInputValues(dataUIManager.getInputDefaultValues(process));
        //Creates the panel that will contains all the inputs.
        JPanel parameterPanel = new JPanel(new MigLayout("fill, ins 5"));
        parameterPanel.setBorder(BorderFactory.createTitledBorder(I18N.tr("Parameter(s)")));

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

        for(int i = 0; i < descriptionTypeList.size(); i++){
            String migOption = "width 10%::60%";
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

        addBashLine(process, parameterPanel, scrollPane);

        JButton addButton = new JButton(ToolBoxIcon.getIcon(ToolBoxIcon.ADD));
        addButton.putClientProperty(PROCESS_PROPERTY, process);
        addButton.putClientProperty(PANEL_PROPERTY, parameterPanel);
        addButton.putClientProperty(SCROLLPANE_PROPERTY, scrollPane);
        addButton.addActionListener(EventHandler.create(ActionListener.class, this, "addBashLine", "source"));
        addButton.setBorderPainted(false);
        addButton.setContentAreaFilled(false);
        parameterPanel.add(addButton, "wrap");

        panel.add(parameterPanel, "growx, span");
        errorMessage = new JLabel();
        errorMessage.setForeground(Color.RED);
        panel.add(errorMessage, "growx, wrap");
        scrollPane.getVerticalScrollBar().setUnitIncrement(SCROLLBAR_UNIT_INCREMENT);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(SCROLLBAR_UNIT_INCREMENT);

        returnPanel.add(scrollPane, "wrap, growx, height ::70%");
        return returnPanel;
    }

    public void addBashLine(Object source){
        if(source instanceof JButton){
            JButton addButton = (JButton) source;
            ProcessDescriptionType process;
            if(addButton.getClientProperty(PROCESS_PROPERTY) instanceof ProcessDescriptionType){
                process = (ProcessDescriptionType) addButton.getClientProperty(PROCESS_PROPERTY);
            }
            else{
                LOGGER.warn(I18N.tr("Unable to add a nex wps bash line. The property process is invalid."));
                return;
            }
            JPanel parameterPanel;
            if(addButton.getClientProperty(PANEL_PROPERTY) instanceof JPanel){
                parameterPanel = (JPanel) addButton.getClientProperty(PANEL_PROPERTY);
            }
            else{
                LOGGER.warn(I18N.tr("Unable to add a nex wps bash line. The property parameterPanel is invalid."));
                return;
            }
            JScrollPane scrollPane;
            if(addButton.getClientProperty(SCROLLPANE_PROPERTY) instanceof JScrollPane){
                scrollPane = (JScrollPane) addButton.getClientProperty(SCROLLPANE_PROPERTY);
            }
            else{
                LOGGER.warn(I18N.tr("Unable to add a nex wps bash line. The property scrollPane is invalid."));
                return;
            }
            parameterPanel.remove(addButton);
            addBashLine(process, parameterPanel, scrollPane);
            parameterPanel.add(addButton, "wrap");
            scrollPane.validate();
            scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
        }
    }

    private void addBashLine(ProcessDescriptionType process, JPanel parameterPanel, JScrollPane scrollPane){
        HashMap<URI, Object> map = new HashMap<>();
        map.putAll(pee.getInputDataMap());
        map.putAll(pee.getOutputDataMap());
        dataMap.put(URI.create(Integer.toString(dataMap.size())), map);

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
        for(int i = 0; i < descriptionTypeList.size(); i++){
            String migOption = "width 10%::60%";
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
                    //If the input is optional, hide it
                    if(descriptionType instanceof InputDescriptionType &&
                            ((InputDescriptionType)descriptionType).getMinOccurs().equals(new BigInteger("0"))) {
                        uiComponent.setVisible(false);
                        //This panel is the one which contains the header with the title of the input and
                        // the hide/show button
                        JPanel contentPanel = new JPanel(new BorderLayout());
                        JPanel hideShowPanel = new JPanel(new MigLayout("ins 0, gap 0"));
                        JPanel centerPanel = new JPanel(new MigLayout("fill, ins 0, gap 0"));
                        //Sets the button to make it shown as just an icon
                        JButton showButton = new JButton(ToolBoxIcon.getIcon("btnright"));
                        showButton.setBorderPainted(false);
                        showButton.setMargin(new Insets(0, 0, 0, 0));
                        showButton.setContentAreaFilled(false);
                        showButton.setOpaque(false);
                        showButton.setFocusable(false);
                        showButton.putClientProperty("upPanel", hideShowPanel);
                        showButton.addMouseListener(EventHandler.create(MouseListener.class,
                                this, "onClickButton", "source", "mouseClicked"));
                        hideShowPanel.add(showButton);
                        hideShowPanel.setToolTipText("Hide/Show option");
                        hideShowPanel.putClientProperty("body", uiComponent);
                        hideShowPanel.putClientProperty("parent", centerPanel);
                        hideShowPanel.putClientProperty("button", showButton);
                        hideShowPanel.putClientProperty("scrollPane", scrollPane);
                        hideShowPanel.addMouseListener(EventHandler.create(MouseListener.class,
                                this, "onClickHeader", "source", "mouseClicked"));
                        contentPanel.add(hideShowPanel, BorderLayout.LINE_START);
                        contentPanel.add(centerPanel, BorderLayout.CENTER);
                        parameterPanel.add(contentPanel, migOption);
                    }
                    else{
                        parameterPanel.add(uiComponent, migOption);
                    }
                }
            }
        }
        parameterPanel.add(new JSeparator(), "growx, span");
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
     * When the title is clicked, expand the input/output components.
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
            showButton.setIcon(ToolBoxIcon.getIcon("btnright"));
        }
        else{
            body.setVisible(true);
            parent.add(body, "growx, span");
            showButton.setIcon(ToolBoxIcon.getIcon("btndown"));
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
