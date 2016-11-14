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

package org.orbisgis.wpsclient;

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
import org.orbisgis.wpsclient.view.ui.ToolBoxPanel;
import org.orbisgis.wpsclient.view.ui.dataui.DataUIManager;
import org.orbisgis.wpsclient.view.utils.ToolBoxIcon;
import org.orbisgis.wpsclient.view.utils.WpsJobStateListener;
import org.orbisgis.wpsclient.view.utils.editor.log.LogEditableElement;
import org.orbisgis.wpsclient.view.utils.editor.log.LogEditor;
import org.orbisgis.wpsclient.view.utils.editor.process.Job;
import org.orbisgis.wpsclient.view.utils.editor.process.ProcessEditableElement;
import org.orbisgis.wpsclient.view.utils.editor.process.ProcessEditor;
import org.orbisgis.wpsservice.controller.execution.ProcessExecutionListener;
import org.orbisgis.wpsservice.model.JaxbContainer;
import org.orbisgis.wpsservice.LocalWpsServer;
import org.orbisgis.wpsservice.model.DataField;
import org.orbisgis.wpsservice.model.DataStore;
import org.orbisgis.wpsservice.model.FieldValue;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.net.URI;
import java.util.*;
import javax.swing.*;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.concurrent.ExecutorService;

/**
 * Start point of the OrbisToolBox.
 * This class instantiate all the tool and allow the different parts to communicates.
 *
 * @author Sylvain PALOMINOS
 **/

@Component(immediate = true, service = {DockingPanel.class, WpsClient.class})
public class WpsClientImpl implements DockingPanel, WpsClient {

    public static final String ACTION_REFRESH = "ACTION_REFRESH";

    public static final String LANG = Locale.getDefault().toString();
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
    private LocalWpsServer wpsService;
    private ProcessEditor pe;
    private List<WpsJobStateListener> jobStateListenerList;

    @Activate
    public void init(){
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

        openEditorList = new ArrayList<>();
        lee = new LogEditableElement();
        le = null;
        jobStateListenerList = new ArrayList<>();
        refreshAvailableScripts();
    }

    @Override
    public void refreshAvailableScripts(){
        toolBoxPanel.cleanAll();
        for(ProcessSummaryType processSummary : getAvailableProcesses()) {
            toolBoxPanel.addProcess(processSummary);
        }
    }

    public LocalWpsServer getLocalWpsService(){
        return wpsService;
    }

    @Override
    public ProcessDescriptionType getInternalProcess(URI identifier) {
        CodeType codeType = new CodeType();
        codeType.setValue(identifier.toString());
        List<ProcessOffering> processOfferingList = getProcessOffering(codeType);
        if(processOfferingList.isEmpty()) {
            return null;
        }
        else{
            return processOfferingList.get(0).getProcess();
        }
    }

    /**
     * Uses the request object to ask it to the WPS service, get the result and unmarshall it.
     * @param request The request to ask to the WPS service.
     * @return The result object.
     */
    public Object askService(Object request){
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
                            request.getClass().getName(), e.getMessage()));
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

    private List<ProcessSummaryType> getAvailableProcesses(){
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
        Object capabilities = askService(request);

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
     * @param processIdentifier CodeType of the processes asked.
     * @return The list of the ProcessOffering
     */
    private List<ProcessOffering> getProcessOffering(CodeType processIdentifier){
        //Sets the describeProcess request
        DescribeProcess describeProcess = new DescribeProcess();
        //Sets the language
        describeProcess.setLang(LANG);
        //Sets the process
        describeProcess.getIdentifier().add(processIdentifier);

        //Ask the service
        Object answer = askService(describeProcess);

        //Retrieve the process list from the Capabilities answer
        if(answer != null && answer instanceof ProcessOfferings){
            ProcessOfferings processOfferings = (ProcessOfferings) answer;
            if(processOfferings.getProcessOffering() != null && !processOfferings.getProcessOffering().isEmpty()){
                return processOfferings.getProcessOffering();
            }
        }
        return new ArrayList<>();
    }

    @Deactivate
    public void dispose() {
        //Removes all the EditorDockable that were added
        for (EditorDockable ed : openEditorList) {
            dockingManager.removeDockingPanel(ed.getDockingParameters().getName());
        }
        openEditorList = new ArrayList<>();
        toolBoxPanel.dispose();
    }

    @Override
    public DockingPanelParameters getDockingParameters() {
        /*//when the toolBox is not visible, it mean that is was closed, so close all the toolbox editors
        if(!parameters.isVisible()){
            for(EditorDockable ed : openEditorList) {
                if (ed instanceof ProcessEditor) {
                    ((ProcessEditor)ed).setAlive(false);
                }
                dockingManager.removeDockingPanel(ed.getDockingParameters().getName());
            }
            openEditorList = new ArrayList<>();
            lee.removePropertyChangeListener(le);
            le = null;
        }*/
        return parameters;
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

    @Override
    public JComponent getComponent() {
        return toolBoxPanel;
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
    public void addLocalSource(URI uri, String[] iconName, boolean isDefaultScript, String nodePath){
        File file = new File(uri);
        if(file.isFile()){
            wpsService.addLocalSource(file, iconName, isDefaultScript, nodePath);
        }
        //If the folder doesn't contains only folders, add it
        else if(file.isDirectory()){
            boolean onlyDirectory = true;
            for(File f : file.listFiles()){
                if(f.isFile()){
                    onlyDirectory = false;
                }
            }
            if(!onlyDirectory) {
                toolBoxPanel.addFolder(file.toURI(), file.getParentFile().toURI());
                for (File f : file.listFiles()) {
                    if (f.isFile()) {
                        wpsService.addLocalSource(f, iconName, isDefaultScript, nodePath);
                    }
                }
            }
        }
        refreshAvailableScripts();
    }

    /**
     * Open the process window for the selected process.
     * @param scriptIdentifier Script URI to execute as a process.
     * @return The ProcessEditableElement which contains the running process information (log, state, ...).
     */
    public ProcessEditableElement openProcess(CodeType scriptIdentifier){
        //Get the list of ProcessOffering
        List<ProcessOffering> listProcess = getProcessOffering(scriptIdentifier);
        if(listProcess == null || listProcess.isEmpty()){
            LoggerFactory.getLogger(WpsClient.class).warn(I18N.tr("Unable to retrieve the process {0}.",
                    scriptIdentifier.getValue()));
            return null;
        }
        //Get the process
        ProcessDescriptionType process = listProcess.get(0).getProcess();
        //Link the DataStore with the DataField, with the FieldValue
        link(process);
        //Open the ProcessEditor
        ProcessEditableElement processEditableElement = new ProcessEditableElement(listProcess.get(0));
        pe = new ProcessEditor(this, processEditableElement);
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
        return processEditableElement;
    }
    /**
     * Link the deiffrents input/output together like the DataStore with its DataFields,
     * the DataFields with its FieldValues ...
     * @param p Process to link.
     */
    private void link(ProcessDescriptionType p){
        //Link the DataField with its DataStore
        for(InputDescriptionType i : p.getInput()){
            if(i.getDataDescription().getValue() instanceof DataField){
                DataField dataField = (DataField)i.getDataDescription().getValue();
                for(InputDescriptionType dataStore : p.getInput()){
                    if(dataStore.getIdentifier().getValue().equals(dataField.getDataStoreIdentifier().toString())){
                        ((DataStore)dataStore.getDataDescription().getValue()).addDataField(dataField);
                    }
                }
            }
        }
        //Link the FieldValue with its DataField and its DataStore
        for(InputDescriptionType i : p.getInput()){
            if(i.getDataDescription().getValue() instanceof FieldValue){
                FieldValue fieldValue = (FieldValue)i.getDataDescription().getValue();
                for(InputDescriptionType input : p.getInput()){
                    if(input.getIdentifier().getValue().equals(fieldValue.getDataFieldIdentifier().toString())){
                        DataField dataField = (DataField)input.getDataDescription().getValue();
                        dataField.addFieldValue(fieldValue);
                        fieldValue.setDataStoredIdentifier(dataField.getDataStoreIdentifier());
                    }
                }
            }
        }
        //Link the DataField with its DataStore
        for(OutputDescriptionType o : p.getOutput()){
            if(o.getDataDescription().getValue() instanceof DataField){
                DataField dataField = (DataField)o.getDataDescription().getValue();
                for(OutputDescriptionType dataStore : p.getOutput()){
                    if(dataStore.getIdentifier().getValue().equals(dataField.getDataStoreIdentifier().toString())){
                        ((DataStore)dataStore.getDataDescription().getValue()).addDataField(dataField);
                    }
                }
            }
        }
        //Link the FieldValue with its DataField and its DataStore
        for(OutputDescriptionType o : p.getOutput()){
            if(o.getDataDescription().getValue() instanceof FieldValue){
                FieldValue fieldValue = (FieldValue)o.getDataDescription().getValue();
                for(OutputDescriptionType output : p.getOutput()){
                    if(output.getIdentifier().getValue().equals(fieldValue.getDataFieldIdentifier().toString())){
                        DataField dataField = (DataField)output.getDataDescription().getValue();
                        dataField.addFieldValue(fieldValue);
                        fieldValue.setDataStoredIdentifier(dataField.getDataStoreIdentifier());
                    }
                }
            }
        }
    }

    public void openProcess(){
        openProcess(toolBoxPanel.getSelectedNode().getIdentifier());
    }

    /**
     * Once the process(es) is(are) configured and run, add it(them) to the LogEditor and removes the ProcessEditor (close it).
     * @param pe ProcessEditor to close.
     * @param id Identifier of the job to validate.
     */
    public void validateInstance(ProcessEditor pe, UUID id){
        ProcessEditableElement processEditableElement = (ProcessEditableElement) pe.getEditableElement();
        //If the LogEditor is not displayed, just do it <°>.
        if(le == null) {
            le = new LogEditor(lee);
            dockingManager.addDockingPanel(le);
            openEditorList.add(le);
        }
        le.addNewLog(processEditableElement, id);

        lee.addProcessEditableElement(processEditableElement);
        //First test if the ProcessEditor has not been already deleted.
        if(dockingManager.getPanels().contains(pe)) {
            dockingManager.removeDockingPanel(pe.getDockingParameters().getName());
        }
        //Test if the ProcessEditor is in the edotor list before removing it.
        if(openEditorList.contains(pe)) {
            openEditorList.remove(pe);
        }
    }

    /**
     * Returns a deep copy of the given process.
     * The copy is generated by requesting to the WPS server the process.
     *
     * @param processIdentifier Identifier of the process to copy.
     * @return A deep copy of the process.
     */
    public ProcessDescriptionType getProcessCopy(CodeType processIdentifier){
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
     * @param identifier Identifier of the process.
     * @return True if the file is well formed, false otherwise.
     */
    public boolean checkProcess(CodeType identifier){
        return wpsService.checkProcess(identifier);
    }

    /**
     * Remove the selected process in the tree.
     */
    public void removeProcess(CodeType codeType){
        wpsService.removeProcess(codeType);
    }

    /**
     * Returns the DataUIManager.
     * @return The DataUIManager.
     */
    public DataUIManager getDataUIManager(){
        return dataUIManager;
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

    public ProcessEditor getProcessEditor() {
        return pe;
    }

    /**
     * Build the Execution request, set it and then launch it in the WpsService.
     *
     * @param process The process to execute.
     * @param dataMap Map containing the inputs/outputs.
     */
    public StatusInfo executeProcess(ProcessDescriptionType process, Map<URI,Object> dataMap) {
        //Build the ExecuteRequest object
        ExecuteRequestType executeRequest = new ExecuteRequestType();
        executeRequest.setIdentifier(process.getIdentifier());
        List<DataInputType> inputList = executeRequest.getInput();
        //Sets the inputs
        for(Map.Entry<URI, Object> entry : dataMap.entrySet()){
            for(InputDescriptionType input : process.getInput()){
                if(URI.create(input.getIdentifier().getValue()).equals(entry.getKey())){
                    DataInputType dataInput = new DataInputType();
                    dataInput.setId(entry.getKey().toString());
                    Data data = new Data();
                    data.getContent().add(entry.getValue());
                    dataInput.setData(data);
                    inputList.add(dataInput);
                }
            }
            List<OutputDefinitionType> outputList = executeRequest.getOutput();
            for(OutputDescriptionType output : process.getOutput()){
                if(URI.create(output.getIdentifier().getValue()).equals(entry.getKey())){
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
        StatusInfo result = (StatusInfo)askService(jaxbElement);
        return result;
    }

    @Override
    public UUID executeInternalProcess(ProcessDescriptionType process, Map<URI, Object> dataMap,
                                       WpsJobStateListener listener) {
        if(listener != null) {
            addJobListener(listener);
        }
        StatusInfo statusInfo = executeProcess(process, dataMap);
        UUID jobId = UUID.fromString(statusInfo.getJobID());
        Job job = new Job(this, jobId);
        job.setStartTime(System.currentTimeMillis());
        job.setStatus(statusInfo);
        return jobId;
    }

    private void fireJobStateEvent(StatusInfo statusInfo){
        for(WpsJobStateListener listener : jobStateListenerList){
            if(listener.getJobId() != null && listener.getJobId().equals(UUID.fromString(statusInfo.getJobID()))){
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
    public StatusInfo getJobStatus(UUID jobID) {
        GetStatus getStatus = new GetStatus();
        getStatus.setJobID(jobID.toString());
        //Launch the execution on the server
        StatusInfo result = (StatusInfo)askService(getStatus);
        fireJobStateEvent(result);
        return result;
    }

    @Override
    public Result getJobResult(UUID jobID) {
        GetResult getResult = new GetResult();
        getResult.setJobID(jobID.toString());
        //Launch the execution on the server
        Result result = (Result)askService(getResult);
        return result;
    }

    @Override
    public StatusInfo dismissJob(UUID jobID) {
        Dismiss dismiss = new Dismiss();
        dismiss.setJobID(jobID.toString());
        //Launch the execution on the server
        StatusInfo result = (StatusInfo)askService(dismiss);
        fireJobStateEvent(result);
        return result;
    }
}
