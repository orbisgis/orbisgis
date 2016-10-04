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

/**
 * UI for the configuration and the run of a WPS process.
 * It extends the EditorDockable interface to be able to be docked in OrbisGIS.
 *
 * @author Sylvain PALOMINOS
 */
public class ProcessEditor extends JPanel implements EditorDockable, PropertyChangeListener {

    private static final int SCROLLBAR_UNIT_INCREMENT = 16;
    /** Name of the EditorDockable. */
    public static final String NAME = "PROCESS_EDITOR";
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(ProcessEditor.class);

    private ProcessEditableElement pee;
    private WpsClientImpl wpsClient;
    private DockingPanelParameters dockingPanelParameters;
    /** DataUIManager used to create the UI corresponding the the data */
    private DataUIManager dataUIManager;
    /** Tells if the this editor has been open or not. */
    private boolean alive;
    /** Error label displayed when the process inputs and output are all defined. */
    private JLabel errorMessage;

    public ProcessEditor(WpsClientImpl wpsClient, ProcessEditableElement pee){
        this.alive = true;
        this.wpsClient = wpsClient;
        this.pee = pee;
        this.pee.addPropertyChangeListener(this);
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
        this.add(buildUI(), BorderLayout.CENTER);
        this.revalidate();
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
            StatusInfo statusInfo = wpsClient.dismissJob(pee.getJobID());
            pee.setProcessState(ProcessExecutionListener.ProcessState.valueOf(statusInfo.getStatus().toUpperCase()));
            pee.setRefreshDate(statusInfo.getNextPoll());
        }
        if(propertyChangeEvent.getPropertyName().equals(ProcessEditableElement.REFRESH_STATUS)){
            StatusInfo statusInfo = wpsClient.getJobStatus(pee.getJobID());
            pee.setProcessState(ProcessExecutionListener.ProcessState.valueOf(statusInfo.getStatus().toUpperCase()));
            pee.setRefreshDate(statusInfo.getNextPoll());
        }
        if(propertyChangeEvent.getPropertyName().equals(ProcessEditableElement.GET_RESULTS)){
            Result result = wpsClient.getJobResult(pee.getJobID());
            pee.setResult(result);
        }
    }

    /**
     * Run the process if all the mandatory process inputs are defined.
     */
    public void runProcess(){
        //First check if all the inputs are defined.
        boolean allDefined = true;
        Map<URI, Object> inputDataMap = pee.getInputDataMap();
        for(InputDescriptionType input : pee.getProcess().getInput()){
            URI identifier = URI.create(input.getIdentifier().getValue());
            if(!input.getMinOccurs().equals(new BigInteger("0")) && !inputDataMap.containsKey(identifier)){
                allDefined = false;
            }
        }

        if(allDefined) {
            //Then launch the process execution
            wpsClient.validateInstance(this);
            pee.setProcessState(ProcessEditableElement.ProcessState.RUNNING);

            //Run the process in a separated thread
            StatusInfo statusInfo = wpsClient.executeProcess(pee.getProcess(),
                    pee.getInputDataMap(),
                    pee.getOutputDataMap());
            pee.setRefreshDate(statusInfo.getNextPoll());
            pee.setJobID(UUID.fromString(statusInfo.getJobID()));
        }
        else{
            errorMessage.setText("Please, configure all the inputs/outputs before executing.");
        }
    }

    /**
     * Build the UI of the given process according to the given data.
     * @return The UI for the configuration of the process.
     */
    private JComponent buildUI(){
        ProcessDescriptionType process = pee.getProcess();
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
        returnPanel.add(new JScrollPane(processPanel), "wrap, growx, height ::50%");

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
                JComponent uiComponent = dataUI.createUI(i, pee.getInputDataMap());
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
                JComponent component = dataUI.createUI(o, pee.getOutputDataMap());
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

            returnPanel.add(scrollPane, "wrap, growx, growy");
        }
        return returnPanel;
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