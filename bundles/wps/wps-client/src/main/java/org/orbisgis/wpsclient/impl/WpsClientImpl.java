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
 * Copyright (C) 2015-2016 CNRS (Lab-STICC UMR CNRS 6285)
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

package org.orbisgis.wpsclient.impl;

import net.opengis.ows._2.*;
import net.opengis.wps._2_0.*;
import net.opengis.ows._2.GetCapabilitiesType.AcceptLanguages;
import net.opengis.wps._2_0.GetCapabilitiesType;
import net.opengis.wps._2_0.ObjectFactory;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.OpenFilePanel;
import org.orbisgis.sif.components.OpenFolderPanel;
import org.orbisgis.sif.components.actions.ActionCommands;
import org.orbisgis.sif.components.actions.ActionDockingListener;
import org.orbisgis.sif.components.actions.DefaultAction;
import org.orbisgis.sif.docking.DockingManager;
import org.orbisgis.sif.docking.DockingPanel;
import org.orbisgis.sif.docking.DockingPanelParameters;
import org.orbisgis.sif.edition.EditorDockable;
import org.orbisgis.sif.edition.EditorManager;
import org.orbisgis.wpsclient.api.InternalWpsClient;
import org.orbisgis.wpsclient.api.WpsClient;
import org.orbisgis.wpsclient.api.utils.ProcessExecutionType;
import org.orbisgis.wpsclient.impl.dataui.DataUIManager;
import org.orbisgis.wpsclient.impl.utils.ToolBoxIcon;
import org.orbisgis.wpsclient.api.utils.WpsJobStateListener;
import org.orbisgis.wpsclient.impl.editor.log.LogEditableElement;
import org.orbisgis.wpsclient.impl.editor.log.LogEditor;
import org.orbisgis.wpsclient.impl.utils.Job;
import org.orbisgis.wpsclient.impl.editor.process.ProcessEditableElement;
import org.orbisgis.wpsclient.impl.editor.process.ProcessEditor;
import org.orbisgis.wpsservice.model.*;
import org.orbisgis.wpsservice.LocalWpsServer;
import org.orbisgis.wpsservice.utils.ProcessMetadata;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.util.*;
import javax.swing.*;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.concurrent.ExecutorService;

import static org.orbisgis.wpsclient.impl.utils.Job.CANCEL;
import static org.orbisgis.wpsclient.impl.utils.Job.GET_RESULTS;
import static org.orbisgis.wpsclient.impl.utils.Job.REFRESH_STATUS;

/**
 * Implementation of the InternalWpsClient for Orbisgis.
 *
 * @author Sylvain PALOMINOS
 **/

@Component(immediate = true, service = {DockingPanel.class, InternalWpsClient.class})
public class WpsClientImpl implements DockingPanel, InternalWpsClient, PropertyChangeListener {

    /** String of the action Refresh. */
    private static final String ACTION_REFRESH = "ACTION_REFRESH";
    /** Client Local (language). */
    private static final String LANG = Locale.getDefault().toString();
    /** String reference of the ToolBox used for DockingFrame. */
    public static final String TOOLBOX_REFERENCE = "orbistoolbox";
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(WpsClientImpl.class);

    /** Docking parameters used by DockingFrames. */
    private DockingPanelParameters parameters;
    /** Displayed JPanel. */
    private ToolBoxPanel toolBoxPanel;
    /** Object creating the UI corresponding to the data. */
    private DataUIManager dataUIManager;

    /** EditableElement associated to the logEditor. */
    private LogEditableElement lee;
    /** EditorDockable for the displaying of the running processes log. */
    private LogEditor le;
    /** List of open EditorDockable. Used to close them when the ToolBox is close (Not stopped, just not visible). */
    private List<EditorDockable> openEditorList;

    /** OrbigGIS DockingManager. */
    private DockingManager dockingManager;
    /** OrbigGIS ExecutorService. */
    private ExecutorService executorService;
    /** OrbisGIS DataManager. */
    private static DataManager dataManager;
    /** OrbisGIS WpsServer. */
    private LocalWpsServer wpsService;
    /** List of JobStateListener listening for the Job state execution. */
    private List<WpsJobStateListener> jobStateListenerList;
    /** Map of the running job. */
    private Map<UUID, Job> jobMap;



    /***************************/
    /** WpsClientImpl methods **/
    /***************************/

    /** OSGI active/deactivate, set/unset methods **/

    @Activate
    public void activate(){
        toolBoxPanel = new ToolBoxPanel(this);
        dataUIManager = new DataUIManager(this);

        parameters = new DockingPanelParameters();
        parameters.setTitle(I18N.tr("ToolBox"));
        parameters.setTitleIcon(ToolBoxIcon.getIcon(ToolBoxIcon.ORBIS_TOOLBOX));
        parameters.setCloseable(true);
        parameters.setName(TOOLBOX_REFERENCE);

        ActionCommands dockingActions = new ActionCommands();
        parameters.setDockActions(dockingActions.getActions());
        dockingActions.addPropertyChangeListener(new ActionDockingListener(parameters));
        dockingActions.addAction(
                new DefaultAction(ACTION_REFRESH,
                        ACTION_REFRESH,
                        I18N.tr("Refresh the selected node."),
                        ToolBoxIcon.getIcon(ToolBoxIcon.REFRESH),
                        EventHandler.create(ActionListener.class, this, "refreshAvailableScripts"),
                        null)
        );
        //Creates the LogEditableElement and the LogEditor
        lee = new LogEditableElement();
        openEditorList = new ArrayList<>();
        le = new LogEditor(lee);
        dockingManager.addDockingPanel(le);
        openEditorList.add(le);

        jobStateListenerList = new ArrayList<>();
        jobMap = new HashMap<>();
        refreshAvailableScripts();
    }

    @Deactivate
    public void deactivate() {
        //Removes all the EditorDockable that were added
        for (EditorDockable ed : openEditorList) {
            dockingManager.removeDockingPanel(ed.getDockingParameters().getName());
        }
        openEditorList = new ArrayList<>();
        toolBoxPanel.dispose();
    }

    @Reference
    public void setLocalWpsService(LocalWpsServer wpsService) {
        this.wpsService = wpsService;
    }
    public void unsetLocalWpsService(LocalWpsServer wpsService) {
        this.wpsService = null;
    }

    @Reference
    public void setDataManager(DataManager dataManager) {
        WpsClientImpl.dataManager = dataManager;
    }
    public void unsetDataManager(DataManager dataManager) {
        WpsClientImpl.dataManager = null;
    }
    public DataManager getDataManager(){
        return dataManager;
    }

    @Reference
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
    public void unsetExecutorService(ExecutorService executorService) {
        this.executorService = null;
    }
    public ExecutorService getExecutorService(){
        return executorService;
    }

    @Reference
    public void setDockingManager(DockingManager dockingManager) {
        this.dockingManager = dockingManager;
    }
    public void unsetDockingManager(DockingManager dockingManager) {
        this.dockingManager = null;
    }

    /** Other methods **/

    /**
     * Uses the request object to ask it to the WPS service, get the result and unmarshall it.
     * @param request The request to ask to the WPS service.
     * @return The result object.
     */
    private Object askRequest(Object request){
        Marshaller marshaller;
        Unmarshaller unmarshaller;
        try {
            marshaller = JaxbContainer.JAXBCONTEXT.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
        } catch (JAXBException e) {
            LoggerFactory.getLogger(WpsClient.class).error(
                    I18N.tr("Unable to create the marshall objects.\nCause : {0}.", e.getMessage()));
            return null;
        }

        //Marshall the WpsService ask
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            marshaller.marshal(request, out);
        } catch (JAXBException e) {
            LoggerFactory.getLogger(WpsClient.class).error(
                    I18N.tr("Unable to marshall the request object : {0}.\nCause : {1}.",
                            request.getClass().getName(), e.getMessage()!=null?e.getMessage():e.getCause().getMessage()));
            return null;
        }
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));

        //Ask the WpsService
        ByteArrayOutputStream xml = (ByteArrayOutputStream)wpsService.callOperation(in);

        //unmarshall the WpsService answer
        InputStream resultResultXml = new ByteArrayInputStream(xml.toByteArray());
        Object resultObject;
        try {
            resultObject = unmarshaller.unmarshal(resultResultXml);
        } catch (JAXBException e) {
            LoggerFactory.getLogger(WpsClient.class).error(I18N.tr("Unable to marshall the answer xml.\nCause : {0}.",
                    e.getMessage()));
            return null;
        }
        if(resultObject instanceof JAXBElement){
            resultObject = ((JAXBElement)resultObject).getValue();
        }
        return resultObject;
    }

    /**
     * Ask to the Wps server the getCapabilities request and returns the list of ProcessSummaryType.
     *
     * @return The list of ProcessSummaryType.
     */
    private List<ProcessSummaryType> getCapabilities(){
        //Sets the getCapabilities request
        GetCapabilitiesType getCapabilities = new GetCapabilitiesType();
        //Sets the language
        AcceptLanguages acceptLanguages = new AcceptLanguages();
        acceptLanguages.getLanguage().add(LANG);
        acceptLanguages.getLanguage().add("*");
        getCapabilities.setAcceptLanguages(acceptLanguages);
        //Sets the version
        AcceptVersionsType acceptVersions = new AcceptVersionsType();
        acceptVersions.getVersion().add("2.0.0");
        getCapabilities.setAcceptVersions(acceptVersions);

        JAXBElement<GetCapabilitiesType> request = new ObjectFactory().createGetCapabilities(getCapabilities);

        //Ask the service
        Object capabilities = askRequest(request);

        //Retrieve the process list from the Capabilities answer
        if(capabilities != null && capabilities instanceof WPSCapabilitiesType){
            WPSCapabilitiesType wpsCapabilities = (WPSCapabilitiesType) capabilities;
            if(wpsCapabilities.getContents() != null && wpsCapabilities.getContents().getProcessSummary() != null){
                return wpsCapabilities.getContents().getProcessSummary();
            }
        }
        return new ArrayList<>();
    }

    /**
     * Return the list of the ProcessOffering contained by the WpsService corresponding to the given CodeType.
     * @param processIdentifier Identifier of the processes asked.
     * @return The list of the ProcessOffering
     */
    private List<ProcessOffering> getProcessOffering(URI processIdentifier){
        //Sets the describeProcess request
        DescribeProcess describeProcess = new DescribeProcess();
        //Sets the language
        describeProcess.setLang(LANG);
        //Sets the process
        CodeType codeType = new CodeType();
        codeType.setValue(processIdentifier.toString());
        describeProcess.getIdentifier().add(codeType);

        //Ask the service
        Object answer = askRequest(describeProcess);

        //Retrieve the process list from the Capabilities answer
        if(answer != null && answer instanceof ProcessOfferings){
            ProcessOfferings processOfferings = (ProcessOfferings) answer;
            if(processOfferings.getProcessOffering() != null && !processOfferings.getProcessOffering().isEmpty()){
                return processOfferings.getProcessOffering();
            }
        }
        return new ArrayList<>();
    }

    @Override
    public DockingPanelParameters getDockingParameters() {
        return parameters;
    }

    @Override
    public JComponent getComponent() {
        return toolBoxPanel;
    }

    /**
     * Close the given EditorDockable if it was add by the ToolBox (contained by openEditorList).
     * @param ed EditorDockable to close.
     */
    public void killEditor(EditorDockable ed) {
        if(openEditorList.contains(ed)){
            dockingManager.removeDockingPanel(ed.getDockingParameters().getName());
        }
        openEditorList.remove(ed);
    }

    /**
     * Open a file browser to find a local script folder and add it.
     * Used in an EvenHandler in view.ui.ToolBoxPanel
     */
    public void addNewLocalSource(){
        OpenFolderPanel openFolderPanel = new OpenFolderPanel("ToolBox.AddSource", I18N.tr("Add a source"));
        openFolderPanel.getFileChooser();
        openFolderPanel.loadState();
        //Wait the window answer and if the user validate set and run the export thread.
        if(UIFactory.showDialog(openFolderPanel)){
            addLocalSource(openFolderPanel.getSelectedFile().toURI());
            openFolderPanel.saveState();
        }
    }
    /**
     * Open a file browser to find a local script folder and add it.
     * Used in an EvenHandler in view.ui.ToolBoxPanel
     */
    public void addNewLocalScript(){
        OpenFilePanel openFilePanel = new OpenFilePanel("ToolBox.AddSource", I18N.tr("Add a source"));
        openFilePanel.getFileChooser();
        openFilePanel.loadState();
        //Wait the window answer and if the user validate set and run the export thread.
        if(UIFactory.showDialog(openFilePanel)){
            addLocalSource(openFilePanel.getSelectedFile().toURI());
            openFilePanel.saveState();
        }
    }

    /**
     * Adds a folder as a local script source.
     * @param uri Folder URI where the script are located.
     */
    public void addLocalSource(URI uri){
        addLocalSource(uri, null, true, new File(uri).getName());
    }

    /**
     * Adds a folder as a local script source.
     * @param uri Folder URI where the script are located.
     * @param iconName Name of the icon to use for this node.
     */
    private void addLocalSource(URI uri, String[] iconName, boolean isDefaultScript, String nodePath){
        File file = new File(uri);
        if(file.isFile()){
            wpsService.addLocalSource(file, iconName, isDefaultScript, nodePath);
        }
        //If the folder doesn't contains only folders, add it
        else if(file.isDirectory()){
            boolean isFolderAdd = false;
            for(File f : file.listFiles()){
                if(f.isFile()){
                    if(!isFolderAdd){
                        toolBoxPanel.addFolder(file.toURI(), file.getParentFile().toURI());
                        isFolderAdd = true;
                    }
                    wpsService.addLocalSource(f, iconName, isDefaultScript, nodePath);
                }
            }
        }
        refreshAvailableScripts();
    }
    /**
     * Link the different input/output together like the JDBCTable with its JDBCTableFields,
     * the JDBCTableFields with its JDBCTableFieldValues ...
     * @param p Process to link.
     */
    private void link(ProcessDescriptionType p){
        //Link the JDBCTableField with its JDBCTable
        for(InputDescriptionType i : p.getInput()){
            if(i.getDataDescription().getValue() instanceof JDBCTableField){
                JDBCTableField jdbcTableField = (JDBCTableField)i.getDataDescription().getValue();
                for(InputDescriptionType jdbcTable : p.getInput()){
                    if(jdbcTable.getIdentifier().getValue().equals(jdbcTableField.getJDBCTableIdentifier().toString())){
                        ((JDBCTable)jdbcTable.getDataDescription().getValue()).addJDBCTableField(jdbcTableField);
                    }
                }
            }
        }
        //Link the JDBCTableFieldValue with its JDBCTableField and its JDBCTable
        for(InputDescriptionType i : p.getInput()){
            if(i.getDataDescription().getValue() instanceof JDBCTableFieldValue){
                JDBCTableFieldValue jdbcTableFieldValue = (JDBCTableFieldValue)i.getDataDescription().getValue();
                for(InputDescriptionType input : p.getInput()){
                    if(input.getIdentifier().getValue().equals(jdbcTableFieldValue.getJDBCTableFieldIdentifier().toString())){
                        JDBCTableField jdbcTableField = (JDBCTableField)input.getDataDescription().getValue();
                        jdbcTableField.addJDBCTableFieldValue(jdbcTableFieldValue);
                        jdbcTableFieldValue.setJDBCTableIdentifier(jdbcTableField.getJDBCTableIdentifier());
                    }
                }
            }
        }
        //Link the JDBCTableField with its JDBCTable
        for(OutputDescriptionType o : p.getOutput()){
            if(o.getDataDescription().getValue() instanceof JDBCTableField){
                JDBCTableField jdbcTableField = (JDBCTableField)o.getDataDescription().getValue();
                for(OutputDescriptionType jdbcTable : p.getOutput()){
                    if(jdbcTable.getIdentifier().getValue().equals(jdbcTableField.getJDBCTableIdentifier().toString())){
                        ((JDBCTable)jdbcTable.getDataDescription().getValue()).addJDBCTableField(jdbcTableField);
                    }
                }
            }
        }
        //Link the JDBCTableFieldValue with its JDBCTableField and its JDBCTable
        for(OutputDescriptionType o : p.getOutput()){
            if(o.getDataDescription().getValue() instanceof JDBCTableFieldValue){
                JDBCTableFieldValue jdbcTableFieldValue = (JDBCTableFieldValue)o.getDataDescription().getValue();
                for(OutputDescriptionType output : p.getOutput()){
                    if(output.getIdentifier().getValue().equals(jdbcTableFieldValue.getJDBCTableFieldIdentifier().toString())){
                        JDBCTableField jdbcTableField = (JDBCTableField)output.getDataDescription().getValue();
                        jdbcTableField.addJDBCTableFieldValue(jdbcTableFieldValue);
                        jdbcTableFieldValue.setJDBCTableIdentifier(jdbcTableField.getJDBCTableIdentifier());
                    }
                }
            }
        }
    }

    /**
     * Open the UI of the process selected in the ToolBoxPanel.
     */
    public void openProcess(){
        openProcess(toolBoxPanel.getSelectedNode().getIdentifier(),
                new HashMap<URI, Object>(), ProcessExecutionType.STANDARD);
    }

    /**
     * Once the process(es) is(are) configured and run, add it(them) to the LogEditor and removes the ProcessEditor (close it).
     * @param pe ProcessEditor to close.
     * @param job Job to validate.
     */
    public void validateInstance(ProcessEditor pe, Job job){
        this.jobMap.put(job.getId(), job);
        //Adds the process information to the log managing classes (LogEditableElement, LogEditor and Job)
        ProcessEditableElement processEditableElement = (ProcessEditableElement) pe.getEditableElement();
        le.addNewLog(processEditableElement.getProcess(), job);
        job.addPropertyChangeListener(this);
        job.addPropertyChangeListener(lee);
        //First test if the ProcessEditor has not been already deleted.
        //Test if the ProcessEditor is in the editor list before removing it.
        if(openEditorList.contains(pe)) {
            openEditorList.remove(pe);
        }
        dockingManager.removeDockingPanel(pe.getDockingParameters().getName());
    }

    /**
     * Returns a deep copy of the given process.
     * The copy is generated by requesting to the WPS server the process.
     *
     * @param processIdentifier Identifier of the process to copy.
     * @return A deep copy of the process.
     */
    public ProcessDescriptionType getProcessCopy(URI processIdentifier){
        List<ProcessOffering> processOfferingList = getProcessOffering(processIdentifier);
        if(!processOfferingList.isEmpty()){
            ProcessDescriptionType processCopy = processOfferingList.get(0).getProcess();
            link(processCopy);
            return processCopy;
        }
        return null;
    }

    /**
     * Verify if the given process is a well formed script.
     * @param processIdentifier Identifier of the process.
     * @return True if the file is well formed, false otherwise.
     */
    public boolean checkProcess(URI processIdentifier){
        return wpsService.checkProcess(processIdentifier);
    }

    /**
     * Remove the selected process in the tree.
     */
    public void removeProcess(URI processIdentifier){
        wpsService.removeProcess(processIdentifier);
    }

    /**
     * Returns the DataUIManager.
     * @return The DataUIManager.
     */
    public DataUIManager getDataUIManager(){
        return dataUIManager;
    }

    private void fireJobStateEvent(StatusInfo statusInfo){
        List<WpsJobStateListener> list = new ArrayList<>();
        list.addAll(jobStateListenerList);
        for(WpsJobStateListener listener : list){
            if(listener.getJobID() != null && listener.getJobID().equals(UUID.fromString(statusInfo.getJobID()))){
                switch(statusInfo.getStatus()){
                    case "ACCEPTED":
                        listener.onJobAccepted();
                        break;
                    case "RUNNING":
                        listener.onJobRunning();
                        break;
                    case "SUCCEEDED":
                        listener.onJobSuccess();
                        break;
                    case "FAILED":
                        listener.onJobFailed();
                        break;
                }
            }
        }
    }



    /*******************************/
    /** InternalWpsClient methods **/
    /*******************************/

    /** Internal WPS methods **/

    @Override
    public UUID executeInternalProcess(URI processIdentifier, Map<URI, Object> dataMap, WpsJobStateListener listener) {
        ProcessDescriptionType process = getInternalProcess(processIdentifier);
        //If there is a listener for this process execution, register it
        if(listener != null) {
            addJobListener(listener);
        }
        //Call the process execution and get the StatusInfo object answer
        StatusInfo statusInfo = executeProcess(processIdentifier, dataMap);
        //Get the Server job id and build a client side job
        UUID jobID = UUID.fromString(statusInfo.getJobID());
        Job job = new Job(jobID, process);
        job.addPropertyChangeListener(this);
        job.setStartTime(System.currentTimeMillis());
        job.setStatus(statusInfo);
        this.jobMap.put(jobID, job);
        //Returns the job id
        return jobID;
    }

    @Override
    public ProcessDescriptionType getInternalProcess(URI processIdentifier) {
        List<ProcessOffering> processOfferingList = getProcessOffering(processIdentifier);
        if(processOfferingList.isEmpty()) {
            return null;
        }
        else{
            return processOfferingList.get(0).getProcess();
        }
    }

    @Override
    public void openProcess(URI processIdentifier,
                            Map<URI, Object> defaultValuesMap,
                            ProcessExecutionType type) {
        //Get the list of ProcessOffering
        List<ProcessOffering> listProcess = getProcessOffering(processIdentifier);
        if(listProcess == null || listProcess.isEmpty()){
            LoggerFactory.getLogger(WpsClient.class).warn(I18N.tr("Unable to retrieve the process {0}.",
                    processIdentifier.toString()));
            return;
        }
        //Get the process
        ProcessDescriptionType process = listProcess.get(0).getProcess();
        for(MetadataType metadata : process.getMetadata()){
            if(metadata.getRole().equalsIgnoreCase(ProcessMetadata.CONFIGURATION_MODE_NAME) &&
                    metadata.getTitle().equalsIgnoreCase(ProcessMetadata.CONFIGURATION_MODE.STANDARD_MODE_ONLY.name()) &&
                    type.equals(ProcessExecutionType.BASH)){
                type = ProcessExecutionType.STANDARD;
                defaultValuesMap = new HashMap<>();
            }
        }
        //Link the JDBCTable with the JDBCTableField and the JDBCTableField with the JDBCTableFieldValue
        link(process);
        //Open the ProcessEditor
        Map<URI, Object> joinedDefaultValuesMap = new HashMap<>();
        joinedDefaultValuesMap.putAll(dataUIManager.getInputDefaultValues(listProcess.get(0).getProcess()));
        if(defaultValuesMap != null) {
            joinedDefaultValuesMap.putAll(defaultValuesMap);
        }
        ProcessEditableElement processEditableElement = new ProcessEditableElement(listProcess.get(0), joinedDefaultValuesMap);
        processEditableElement.setProcessExecutionType(type);
        ProcessEditor pe = new ProcessEditor(this, processEditableElement);
        //Find if there is already a ProcessEditor open with the same process.
        //If not, add the new one.
        boolean alreadyOpen = false;
        for(EditorDockable ed : openEditorList){
            if(ed.getDockingParameters().getName().equals(pe.getDockingParameters().getName())){
                alreadyOpen = true;
            }
        }
        if(!alreadyOpen) {
            dockingManager.addDockingPanel(pe);
            openEditorList.add(pe);
        }
        else{
            LoggerFactory.getLogger(WpsClient.class).warn(I18N.tr("The process {0} is already open.",
                    processEditableElement.getProcess().getTitle().get(0).getValue()));
        }
    }

    /** Other methods **/

    @Override
    public void refreshAvailableScripts(){
        //Removes all the processes from the UI of the toolbox
        toolBoxPanel.cleanAll();
        //Adds all the available processes
        for(ProcessSummaryType processSummary : getCapabilities()) {
            toolBoxPanel.addProcess(processSummary);
        }
    }

    @Override
    public List<String> getTableFieldList(String tableName, List<DataType> dataTypes, List<DataType> excludedTypes){
        return wpsService.getTableFieldList(tableName, dataTypes, excludedTypes);
    }

    @Override
    public List<String> getTableList(List<DataType> dataTypes, List<DataType> excludedTypes){
        return wpsService.getTableList(dataTypes, excludedTypes);
    }

    @Override
    public Map<String, Object> getFieldInformation(String tableName, String fieldName){
        return wpsService.getFieldInformation(tableName, fieldName);
    }

    @Override
    public List<String> getFieldValueList(String tableName, String fieldName){
        return wpsService.getFieldValueList(tableName, fieldName);
    }

    @Override
    public void addJobListener(WpsJobStateListener listener) {
        if(!jobStateListenerList.contains(listener)) {
            jobStateListenerList.add(listener);
        }
    }

    @Override
    public void removeJobListener(WpsJobStateListener listener) {
        jobStateListenerList.remove(listener);
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if(propertyChangeEvent.getPropertyName().equals(CANCEL)){
            UUID jobID = (UUID)propertyChangeEvent.getNewValue();
            StatusInfo statusInfo = this.dismissJob(jobID);
            Job job = jobMap.get(jobID);
            job.setStatus(statusInfo);
        }
        if(propertyChangeEvent.getPropertyName().equals(REFRESH_STATUS)){
            UUID jobID = (UUID)propertyChangeEvent.getNewValue();
            StatusInfo statusInfo = this.getJobStatus(jobID);
            Job job = jobMap.get(jobID);
            job.setStatus(statusInfo);
        }
        if(propertyChangeEvent.getPropertyName().equals(GET_RESULTS)){
            UUID jobID = (UUID)propertyChangeEvent.getNewValue();
            Job job = jobMap.get(jobID);
            Result result = this.getJobResult(jobID);
            job.setResult(result);
        }
    }


    /***********************/
    /** WpsClient methods **/
    /***********************/

    /** WPS methods **/

    @Override
    public StatusInfo getJobStatus(UUID jobID) {
        //Build the GetStatus object used to request a job status to the WPS server.
        GetStatus getStatus = new GetStatus();
        getStatus.setJobID(jobID.toString());
        //Launch the request on the server
        StatusInfo result = (StatusInfo) askRequest(getStatus);
        //Fire a job state event to tells the JobStateListeners that a job state has been updated.
        fireJobStateEvent(result);
        return result;
    }

    @Override
    public Result getJobResult(UUID jobID) {
        jobMap.remove(jobID);
        //Build the GetResult object used to request a job result to the WPS server.
        GetResult getResult = new GetResult();
        getResult.setJobID(jobID.toString());
        //Launch the execution on the server
        return (Result) askRequest(getResult);
    }

    @Override
    public StatusInfo dismissJob(UUID jobID) {
        //Build the Dismiss object used to request the dismiss of a job to the WPS server.
        Dismiss dismiss = new Dismiss();
        dismiss.setJobID(jobID.toString());
        //Launch the execution on the server
        StatusInfo result = (StatusInfo) askRequest(dismiss);
        //Fire a job state event to tells the JobStateListeners that a job state has been updated.
        fireJobStateEvent(result);
        return result;
    }

    @Override
    public StatusInfo executeProcess(URI processIdentifier, Map<URI,Object> dataMap) {
        //Get the ProcessDescriptionType corresponding to the process identifier
        ProcessDescriptionType process = getInternalProcess(processIdentifier);
        //Build the ExecuteRequest object
        ExecuteRequestType executeRequest = new ExecuteRequestType();
        //Sets the identifier of the process to execute
        executeRequest.setIdentifier(process.getIdentifier());
        //For each entry of the map, test if it is an input or an output and so add it to the execute request
        for(Map.Entry<URI, Object> entry : dataMap.entrySet()){
            //Sets the inputs
            List<DataInputType> inputList = executeRequest.getInput();
            //Test if the entry is one of the inputs
            for(InputDescriptionType input : process.getInput()){
                if(URI.create(input.getIdentifier().getValue()).equals(entry.getKey())){
                    //Build the data object containing the data on the input
                    Data data = new Data();
                    data.getContent().add(entry.getValue().toString());
                    //Build the DataInput object containing the input identifier and the data to process
                    DataInputType dataInput = new DataInputType();
                    dataInput.setId(entry.getKey().toString());
                    dataInput.setData(data);
                    //Register the DataInput
                    inputList.add(dataInput);
                }
            }
            //Sets the outputs
            List<OutputDefinitionType> outputList = executeRequest.getOutput();
            //Test if the entry is one of the outputs
            for(OutputDescriptionType output : process.getOutput()){
                if(URI.create(output.getIdentifier().getValue()).equals(entry.getKey())){
                    //Build the OutputDefinition object which contains the processing information for the output
                    OutputDefinitionType out = new OutputDefinitionType();
                    out.setId(entry.getKey().toString());
                    out.setTransmission(DataTransmissionModeType.VALUE);
                    out.setMimeType("text/plain");
                    outputList.add(out);
                }
            }
        }
        //Launch the execution on the server
        JAXBElement<ExecuteRequestType> jaxbElement = new ObjectFactory().createExecute(executeRequest);
        //Return the StatusInfo answer of the server
        return (StatusInfo) askRequest(jaxbElement);
    }
}
