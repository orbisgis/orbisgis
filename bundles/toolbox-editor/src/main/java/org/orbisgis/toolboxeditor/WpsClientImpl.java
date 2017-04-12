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

package org.orbisgis.toolboxeditor;

import net.opengis.ows._2.AcceptVersionsType;
import net.opengis.ows._2.CodeType;
import net.opengis.ows._2.GetCapabilitiesType.AcceptLanguages;
import net.opengis.ows._2.MetadataType;
import net.opengis.wps._2_0.*;
import net.opengis.wps._2_0.ObjectFactory;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.DatabaseProgressionListener;
import org.orbisgis.corejdbc.StateEvent;
import org.orbisgis.frameworkapi.CoreWorkspace;
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
import org.orbisgis.toolboxeditor.dataui.DataUIManager;
import org.orbisgis.toolboxeditor.editor.log.LogEditableElement;
import org.orbisgis.toolboxeditor.editor.log.LogEditor;
import org.orbisgis.toolboxeditor.editor.process.ProcessEditableElement;
import org.orbisgis.toolboxeditor.editor.process.ProcessEditor;
import org.orbisgis.toolboxeditor.utils.Job;
import org.orbisgis.toolboxeditor.utils.ToolBoxIcon;
import org.orbiswps.client.api.WpsClient;
import org.orbiswps.client.api.utils.ProcessExecutionType;
import org.orbiswps.client.api.utils.WpsJobStateListener;
import org.orbiswps.server.WpsServer;
import org.orbiswps.server.controller.process.ProcessIdentifier;
import org.orbiswps.server.model.*;
import org.orbiswps.server.utils.ProcessMetadata;
import org.orbiswps.server.utils.WpsServerListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.URI;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.orbisgis.toolboxeditor.utils.Job.*;

/**
 * Implementation of the InternalWpsClient for Orbisgis.
 *
 * @author Sylvain PALOMINOS
 * @author Erwan Bocher
 **/

@Component(immediate = true, service = {DockingPanel.class, ToolboxWpsClient.class})
public class WpsClientImpl
        implements DockingPanel, ToolboxWpsClient, PropertyChangeListener, WpsServerListener, DatabaseProgressionListener {

    private static final String TOOLBOX_PROPERTIES = "toolbox.properties";
    private static final String PROPERTY_SOURCES = "PROPERTY_SOURCES";
    /** String of the action Refresh. */
    private static final String ACTION_REFRESH = "ACTION_REFRESH";
    /** Client Local (language). */
    private static final String LANG = Locale.getDefault().toString();
    /** String reference of the ToolBox used for DockingFrame. */
    public static final String TOOLBOX_REFERENCE = "orbistoolbox";
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(WpsClientImpl.class);
    /**Array of the table type accepted. */
    private static final String[] SHOWN_TABLE_TYPES = new String[]{"TABLE","LINKED TABLE","VIEW","EXTERNAL"};
    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(WpsClientImpl.class);

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
    private DataManager dataManager;
    /** OrbisGIS WpsServer. */
    private WpsServer wpsServer;
    /** OrbisGIS CoreWorkspace. */
    private CoreWorkspace workspace;
    /** List of JobStateListener listening for the Job state execution. */
    private List<WpsJobStateListener> jobStateListenerList;
    /** Map of the running job. */
    private Map<UUID, Job> jobMap;
    private EditorManager editorManager;
    /** Boolean indicating if a refresh task has been scheduled or not. */
    private boolean isRefreshScheduled = false;
    /** True if the database is H2, false otherwise. */
    private boolean isH2;
    /** List of map containing the table with their basic information.
     * It is used as a buffer to avoid to reload all the table list to save time.
     */
    private List<Map<JdbcProperties, String>> tableList = new ArrayList<>();
    /** True if an updates happen while another on is running. */
    private boolean updateWhileAwaitingRefresh = false;
    /** True if a swing runnable is pending to refresh the content of the table list, false otherwise. */
    private AtomicBoolean awaitingRefresh=new AtomicBoolean(false);
    private Map<String, String> processUriPath = new HashMap<>();



    /***************************/
    /** WpsClientImpl methods **/
    /***************************/

    /** OSGI active/deactivate, set/unset methods **/

    @Activate
    public void activate(){
        if(wpsServer != null){
            wpsServer.setDataSource(dataManager.getDataSource());
            wpsServer.setExecutorService(executorService);
        }
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


        if(dataManager != null) {
            //Install database listeners
            dataManager.addDatabaseProgressionListener(this, StateEvent.DB_STATES.STATE_STATEMENT_END);
            //Call readDatabase when a SourceManager fire an event
            onDataManagerChange();
        }
        else{
            LOGGER.warn(I18N.tr("Warning, no DataManager found."));
        }
        testDBForMultiProcess();
        if(workspace != null) {
            Properties tbProperties = new Properties();
            //Load the property file
            File propertiesFile = new File(workspace.getWorkspaceFolder() + File.separator + TOOLBOX_PROPERTIES);
            if (propertiesFile.exists()) {
                try {
                    tbProperties.load(new FileInputStream(propertiesFile));
                } catch (IOException e) {
                    LOGGER.warn(I18N.tr("Unable to restore previous configuration of the ToolBox."));
                    tbProperties = new Properties();
                }
            }

            //Properties loading
            Object prop = tbProperties.getProperty(PROPERTY_SOURCES);
            if(prop != null && !prop.toString().isEmpty()){
                String str = prop.toString();
                for(String s : str.split(";")){
                    File f = new File(s);
                    addLocalSource(f.toURI(), null, true, new File(f.getParent()).getName());
                }
            }
        }
        else{
            LOGGER.warn("Warning, no CoreWorkspace found. Unable to load the previous state.");
        }

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
        //Try to save the local files loaded.
        try {
            Properties tbProperties = new Properties();
            String path = "";
            for(Map.Entry<String, String> entry : processUriPath.entrySet()){
                if(!path.isEmpty()){
                    path+=";";
                }
                path+=entry.getValue();
            }
            //Save the open process source path
            tbProperties.setProperty(PROPERTY_SOURCES, path);
            tbProperties.store(
                    new FileOutputStream(workspace.getWorkspaceFolder() + File.separator + TOOLBOX_PROPERTIES),
                    I18N.tr("Save of the OrbisGIS toolBox"));
        } catch (IOException e) {
            LOGGER.warn(I18N.tr("Unable to save ToolBox state."));
        }
    }

    @Reference
    public void setWpsServer(WpsServer wpsServer) {
        this.wpsServer = wpsServer;
        this.wpsServer.addWpsServerListener(this);
    }
    public void unsetWpsServer(WpsServer wpsServer) {
        this.wpsServer.removeWpsServerListener(this);
        this.wpsServer = null;
    }

    @Reference
    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    public void unsetDataManager(DataManager dataManager) {
        this.dataManager = null;
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

    @Reference
    public void setEditorManager(EditorManager editorManager) {
        this.editorManager = editorManager;
    }
    public void unsetEditorManager(EditorManager editorManager) {
        this.editorManager = null;
    }

    @Reference
    public void setCoreWorkspace(CoreWorkspace workspace) {
        this.workspace = workspace;
    }
    public void unsetCoreWorkspace(CoreWorkspace workspace) {
        this.workspace = null;
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
        ByteArrayOutputStream xml = (ByteArrayOutputStream) wpsServer.callOperation(in);

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
    public List<ProcessSummaryType> getCapabilities(){
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
    public List<ProcessOffering> getProcessOffering(URI processIdentifier){
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
        openFolderPanel.setAcceptAllFileFilterUsed(false);
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
        openFilePanel.setAcceptAllFileFilterUsed(false);
        openFilePanel.addFilter("groovy", "Groovy script");
        openFilePanel.setCurrentFilter(0);
        //Wait the window answer and if the user validate set and run the export thread.
        if(UIFactory.showDialog(openFilePanel)){
            for(File file : openFilePanel.getSelectedFiles()) {
                addLocalSource(file.toURI());
                openFilePanel.saveState();
            }
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
            List<ProcessIdentifier> piList = wpsServer.addProcess(file, iconName, isDefaultScript, nodePath);
            processUriPath.put(piList.get(0).getProcessDescriptionType().getIdentifier().getValue(), new File(uri).getAbsolutePath());
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
                    wpsServer.addProcess(f, iconName, isDefaultScript, nodePath);
                }
            }
        }
        refreshAvailableScripts();
    }
    /**
     * Link the different input/output together like the JDBCTable with its JDBCColumns,
     * the JDBCColumns with its JDBCValues ...
     * @param p Process to link.
     */
    private void link(ProcessDescriptionType p){
        //Link the JDBCColumn with its JDBCTable
        for(InputDescriptionType i : p.getInput()){
            if(i.getDataDescription().getValue() instanceof JDBCColumn){
                JDBCColumn jdbcColumn = (JDBCColumn)i.getDataDescription().getValue();
                for(InputDescriptionType jdbcTable : p.getInput()){
                    if(jdbcTable.getIdentifier().getValue().equals(jdbcColumn.getJDBCTableIdentifier().toString())){
                        ((JDBCTable)jdbcTable.getDataDescription().getValue()).addJDBCColumn(jdbcColumn);
                    }
                }
            }
        }
        //Link the JDBCValue with its JDBCColumn and its JDBCTable
        for(InputDescriptionType i : p.getInput()){
            if(i.getDataDescription().getValue() instanceof JDBCValue){
                JDBCValue jdbcValue = (JDBCValue)i.getDataDescription().getValue();
                for(InputDescriptionType input : p.getInput()){
                    if(input.getIdentifier().getValue().equals(jdbcValue.getJDBCColumnIdentifier().toString())){
                        JDBCColumn jdbcColumn = (JDBCColumn)input.getDataDescription().getValue();
                        jdbcColumn.addJDBCValue(jdbcValue);
                        jdbcValue.setJDBCTableIdentifier(jdbcColumn.getJDBCTableIdentifier());
                    }
                }
            }
        }
        //Link the JDBCColumn with its JDBCTable
        for(OutputDescriptionType o : p.getOutput()){
            if(o.getDataDescription().getValue() instanceof JDBCColumn){
                JDBCColumn jdbcColumn = (JDBCColumn)o.getDataDescription().getValue();
                for(OutputDescriptionType jdbcTable : p.getOutput()){
                    if(jdbcTable.getIdentifier().getValue().equals(jdbcColumn.getJDBCTableIdentifier().toString())){
                        ((JDBCTable)jdbcTable.getDataDescription().getValue()).addJDBCColumn(jdbcColumn);
                    }
                }
            }
        }
        //Link the JDBCValue with its JDBCColumn and its JDBCTable
        for(OutputDescriptionType o : p.getOutput()){
            if(o.getDataDescription().getValue() instanceof JDBCValue){
                JDBCValue jdbcValue = (JDBCValue)o.getDataDescription().getValue();
                for(OutputDescriptionType output : p.getOutput()){
                    if(output.getIdentifier().getValue().equals(jdbcValue.getJDBCColumnIdentifier().toString())){
                        JDBCColumn jdbcColumn = (JDBCColumn)output.getDataDescription().getValue();
                        jdbcColumn.addJDBCValue(jdbcValue);
                        jdbcValue.setJDBCTableIdentifier(jdbcColumn.getJDBCTableIdentifier());
                    }
                }
            }
        }
    }

    /**
     * Open the UI of the process selected in the ToolBoxPanel.
     */
    public void openProcess(){
        this.onDataManagerChange();
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
        String processPath = processUriPath.get(processIdentifier.toString());
        if(processPath == null){
            return true;
        }
        else if(!new File(processPath).exists()) {
            this.removeProcess(processIdentifier);
            processUriPath.remove(processIdentifier.toString());
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Remove the selected process in the tree.
     */
    public void removeProcess(URI processIdentifier){
        processUriPath.remove(processIdentifier.toString());
        wpsServer.removeProcess(processIdentifier);
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
        //Link the JDBCTable with the JDBCColumn and the JDBCColumn with the JDBCValue
        link(process);
        //Open the ProcessEditor
        Map<URI, Object> joinedDefaultValuesMap = new HashMap<>();
        joinedDefaultValuesMap.putAll(dataUIManager.getInputDefaultValues(listProcess.get(0).getProcess()));
        if(defaultValuesMap != null) {
            joinedDefaultValuesMap.putAll(defaultValuesMap);
        }
        ProcessOffering processOffering = listProcess.get(0);
        URI processUri = URI.create(processOffering.getProcess().getIdentifier().getValue());
        ProcessEditableElement processEditableElement = new ProcessEditableElement(processOffering , processUri, joinedDefaultValuesMap);
        processEditableElement.setProcessExecutionType(type);
        editorManager.openEditable(processEditableElement);
    }

    /** Other methods **/

    @Override
    public void refreshAvailableScripts(){
        isRefreshScheduled = false;
        //Removes all the processes from the UI of the toolbox
        toolBoxPanel.cleanAll();
        //Adds all the available processes
        for(ProcessSummaryType processSummary : getCapabilities()) {
            toolBoxPanel.addProcess(processSummary);
        }
    }

    @Override
    public List<String> getTableList(List<DataType> dataTypes, List<DataType> excludedTypes) {
        List<String> list = new ArrayList<>();
        String defaultSchema = (isH2)?"PUBLIC":"public";
        //Read the tableList to get the desired tables
        for(Map<JdbcProperties, String> map : tableList){
            if(map.containsKey(JdbcProperties.TABLE_LOCATION)) {
                TableLocation tablelocation = TableLocation.parse(map.get(JdbcProperties.TABLE_LOCATION), isH2);
                boolean isValid = false;
                if((dataTypes == null || dataTypes.isEmpty()) && (excludedTypes == null || excludedTypes.isEmpty())){
                    isValid = true;
                }
                else if(map.containsKey(JdbcProperties.COLUMN_TYPE)) {
                    try (Connection connection = dataManager.getDataSource().getConnection()) {
                        Map<String, Integer> types = SFSUtilities.getGeometryTypes(connection, tablelocation);
                        for (Map.Entry<String, Integer> entry : types.entrySet()) {
                            if(dataTypes != null) {
                                for (DataType dataType : dataTypes) {
                                    if (DataType.testGeometryType(dataType, entry.getValue())) {
                                        isValid = true;
                                    }
                                }
                            }
                            if(excludedTypes != null) {
                                for (DataType dataType : excludedTypes) {
                                    if (DataType.testGeometryType(dataType, entry.getValue())) {
                                        isValid = false;
                                    }
                                }
                            }
                        }
                    } catch (SQLException e) {
                        LOGGER.error(I18N.tr("Unable to get the connection.\nCause : {0}.",
                                e.getMessage()));
                    }
                }
                else {
                    try (Connection connection = dataManager.getDataSource().getConnection()) {
                        //Get the metadata of the table
                        ResultSet rs = connection.createStatement().executeQuery(String.format("select * from %s limit 1", tablelocation.getTable()));
                        ResultSetMetaData metaData = rs.getMetaData();
                        //For each column, get its DataType
                        for(int columnId = 1; columnId <= metaData.getColumnCount(); ++columnId) {
                            String columnTypeName = metaData.getColumnTypeName(columnId);
                            if(!columnTypeName.equalsIgnoreCase("geometry")) {
                                DataType dataType = DataType.getDataType(metaData.getColumnType(columnId));
                                //Tests if the DataType is compatible with the acceptedTypes and excludedTypes.
                                if(dataTypes != null && !dataTypes.isEmpty()) {
                                    for (DataType acceptedType : dataTypes) {
                                        if (dataType.equals(acceptedType)) {
                                            isValid = true;
                                        }
                                    }
                                }
                                else{
                                    isValid = true;
                                }
                                if(excludedTypes != null && !excludedTypes.isEmpty()) {
                                    for (DataType excludedType : excludedTypes) {
                                        if (excludedType.equals(dataType)) {
                                            isValid = false;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (SQLException e) {
                        LOGGER.error(I18N.tr("Unable to get the connection.\nCause : {0}.",
                                e.getMessage()));
                    }
                }

                if (isValid) {
                    //If the table is in the default schema, just add its name
                    if (tablelocation.getSchema(defaultSchema).equals(defaultSchema)) {
                        list.add(tablelocation.getTable());
                    }
                    //If not, add the schema name '.' the table name (SCHEMA.TABLE)
                    else {
                        list.add(tablelocation.getSchema() + "." + tablelocation.getTable());
                    }
                }
            }
        }
        return list;
    }

    @Override
    public List<Map<JdbcProperties, Object>> getColumnInformation(String tableName){
        List<Map<JdbcProperties, Object>> mapList = new ArrayList<>();
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            //Get the list of the columns of a table
            ResultSet rs1 = connection.createStatement().executeQuery(String.format("select * from %s limit 1", tableName));
            ResultSetMetaData metaData = rs1.getMetaData();
            //If the column isn't a geometry, add it to the map
            for(int i=1; i<=metaData.getColumnCount(); i++){
                if(!metaData.getColumnTypeName(i).equalsIgnoreCase("GEOMETRY")){
                    Map<JdbcProperties, Object> map = new HashMap<>();
                    map.put(JdbcProperties.COLUMN_NAME, metaData.getColumnLabel(i));
                    map.put(JdbcProperties.COLUMN_TYPE, metaData.getColumnTypeName(i));
                    map.put(JdbcProperties.COLUMN_SRID, 0);
                    map.put(JdbcProperties.COLUMN_DIMENSION, 0);
                    mapList.add(map);
                }
            }
            //Once the non geometric columns are get, do the same with the geometric one.
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM GEOMETRY_COLUMNS WHERE F_TABLE_NAME LIKE '" +
                    TableLocation.parse(tableName).getTable() + "';";
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                Map<JdbcProperties, Object> map = new HashMap<>();
                //Case of H2 database
                if(isH2) {
                    map.put(JdbcProperties.COLUMN_NAME, rs.getString(4));
                    map.put(JdbcProperties.COLUMN_TYPE, SFSUtilities.getGeometryTypeNameFromCode(rs.getInt(6)));
                    map.put(JdbcProperties.COLUMN_SRID, rs.getInt(8));
                    map.put(JdbcProperties.COLUMN_DIMENSION, rs.getInt(7));
                }
                //Other case
                else{
                    map.put(JdbcProperties.COLUMN_NAME, rs.getString(4));
                    map.put(JdbcProperties.COLUMN_TYPE, rs.getString(7));
                    map.put(JdbcProperties.COLUMN_SRID, rs.getInt(6));
                    map.put(JdbcProperties.COLUMN_DIMENSION, rs.getInt(5));
                }
                mapList.add(map);
            }
        } catch (SQLException e) {
            LOGGER.error(I18N.tr("Unable to get the column INFORMATION of the table {0} information.\nCause : {1}.",
                    tableName, e.getMessage()));
        }
        return mapList;
    }

    @Override
    public List<String> getColumnList(String tableName, List<DataType> dataTypes, List<DataType> excludedTypes){
        if(dataTypes == null){
            dataTypes = new ArrayList<>();
        }
        if(excludedTypes == null){
            excludedTypes = new ArrayList<>();
        }
        List<String> columnList = new ArrayList<>();
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            DatabaseMetaData dmd = connection.getMetaData();
            TableLocation tablelocation = TableLocation.parse(tableName, isH2);
            ResultSet result = dmd.getColumns(tablelocation.getCatalog(), tablelocation.getSchema(),
                    tablelocation.getTable(), "%");
            while(result.next()){
                if (!dataTypes.isEmpty()) {
                    for (DataType dataType : dataTypes) {
                        String type = result.getObject(6).toString();
                        if(type.equalsIgnoreCase("GEOMETRY")){
                            if (DataType.testGeometryType(dataType, SFSUtilities.getGeometryType(connection,
                                    tablelocation, result.getObject(4).toString().toUpperCase()))) {
                                columnList.add(result.getObject(4).toString());
                            }
                        }
                        else {
                            if (DataType.testDBType(dataType, result.getObject(6).toString().toUpperCase())) {
                                columnList.add(result.getObject(4).toString());
                            }
                        }
                    }
                } else if(!excludedTypes.isEmpty()){
                    boolean accepted = true;
                    for (DataType dataType : excludedTypes) {
                        if (DataType.testDBType(dataType, result.getObject(6).toString())) {
                            accepted = false;
                        }
                    }
                    if(accepted) {
                        columnList.add(result.getObject(4).toString());
                    }
                }else{
                    columnList.add(result.getObject(4).toString());
                }
            }
        } catch (SQLException e) {
            LOGGER.error(I18N.tr("Unable to get the table {0} column list.\nCause : {1}.", tableName, e.getMessage()));
        }
        return columnList;
    }

    @Override
    public List<String> getValueList(String tableName, String columnName) {
        List<String> values = new ArrayList<>();
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            tableName = TableLocation.parse(tableName, isH2).toString();
            values.addAll(JDBCUtilities.getUniqueFieldValues(connection,
                    tableName,
                    columnName));
        } catch (SQLException e) {
            LOGGER.error(I18N.tr("Unable to get the column {0}.{1} value list.\nCause : {2}.",
                    tableName, columnName, e.getMessage()));
        }
        return values;
    }

    @Override
    public List<String> getSRIDList(){
        List<String> sridList = new ArrayList<>();
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT srid, AUTH_NAME FROM SPATIAL_REF_SYS");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                sridList.add(resultSet.getString("AUTH_NAME") + ":" +resultSet.getInt("srid"));
            }
        } catch (SQLException e) {
            LOGGER.error(I18N.tr("Error on getting the SRID list.\nCause : {0}.", e.getMessage()));
        }
        return sridList;
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
            if(job != null) {
                job.setStatus(statusInfo);
            }
        }
        if(propertyChangeEvent.getPropertyName().equals(GET_RESULTS)){
            UUID jobID = (UUID)propertyChangeEvent.getNewValue();
            Job job = jobMap.get(jobID);
            Result result = this.getJobResult(jobID);
            job.setResult(result);
        }
    }

    @Override
    public void onScriptAdd() {
        //If no refresh task has been scheduled, creates on an schedule it in two seconds from now
        if(!isRefreshScheduled){
            isRefreshScheduled = true;
            Runnable refreshRunnable = new RefreshRunnable(this);
            final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
            executor.schedule(refreshRunnable, 2, TimeUnit.SECONDS);
        }
    }

    @Override
    public void onScriptRemoved() {
        //If no refresh task has been scheduled, creates on an schedule it in two seconds from now
        if(!isRefreshScheduled){
            isRefreshScheduled = true;
            Runnable refreshRunnable = new RefreshRunnable(this);
            final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
            executor.schedule(refreshRunnable, 1, TimeUnit.SECONDS);
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
                    if(entry.getValue() == null) {
                        data.getContent().add(null);
                    }
                    else {
                        data.getContent().add(entry.getValue().toString());
                    }
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

    /*********************/
    /** Utility classes **/
    /*********************/

    /**
     * Class implementing the runnable interface. It is used to create a task which aim is to refresh in the given
     * WpsClientImpl the list of processes.
     */
    private class RefreshRunnable implements Runnable{

        /** Client to refresh */
        private WpsClientImpl wpsClient;

        RefreshRunnable(WpsClientImpl wpsClient){
            this.wpsClient = wpsClient;
        }

        @Override
        public void run() {
            wpsClient.refreshAvailableScripts();
        }
    }
    
    /**
     * Test the database an returns if it allows the wps service to run more than one process at the same time.
     * @return True if more than one process can be run at the same time, false otherwise.
     */
    private boolean testDBForMultiProcess(){
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            if(dataManager != null){
                isH2 = JDBCUtilities.isH2DataBase(connection.getMetaData());
                if(isH2){
                    wpsServer.setDatabase(WpsServer.Database.H2GIS);
                }
                else{
                    wpsServer.setDatabase(WpsServer.Database.POSTGIS);
                }
                if(isH2) {
                    Statement statement = connection.createStatement();
                    ResultSet result = statement.executeQuery("select VALUE from INFORMATION_SCHEMA.SETTINGS AS s where NAME = 'MVCC';");
                    result.next();
                    if (!result.getString(1).equals("TRUE")) {
                        return false;
                    }
                    result = statement.executeQuery("select VALUE from INFORMATION_SCHEMA.SETTINGS AS s where NAME = 'MULTI_THREADED';");
                    result.next();
                    if (!result.getString(1).equals("1")) {
                        return false;
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }
    /*******************************************************/
    /** Methods for the listening of the database update. **/
    /*******************************************************/

    /**
     * Method called when a change happens in the DataManager (i.e. a table suppression, a table add ...)
     */
    public void onDataManagerChange() {
        //If not actually doing a refresh, do it.
        if(!awaitingRefresh.getAndSet(true)) {
            ReadDataManagerOnSwingThread worker = new ReadDataManagerOnSwingThread(this);
            ExecutorService executorService = getExecutorService();
            if(executorService != null){
                executorService.execute(worker);
            }
            else{
                worker.run();
            }
        } else {
            updateWhileAwaitingRefresh = true;
        }
    }

    @Override
    public void progressionUpdate(StateEvent state) {
        if (state.isUpdateDatabaseStructure()) {
            onDataManagerChange();
        }
    }

    /**
     * Read the table list in the database
     */
    private void readDatabase() {
        List<Map<JdbcProperties, String>> newTables = new ArrayList<>();
        try (Connection connection = dataManager.getDataSource().getConnection()) {
            final String defaultCatalog = connection.getCatalog();
            String defaultSchema = "PUBLIC";
            try {
                if (connection.getSchema() != null) {
                    defaultSchema = connection.getSchema();
                }
            } catch (AbstractMethodError | Exception ex) {
                // Driver has been compiled with JAVA 6, or is not implemented
            }
            // Fetch Geometry tables
            Map<String,String> tableGeometry = new HashMap<>();
            try(Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM "+defaultSchema+".geometry_columns")) {
                while(rs.next()) {
                    tableGeometry.put(new TableLocation(rs.getString("F_TABLE_CATALOG"),
                            rs.getString("F_TABLE_SCHEMA"), rs.getString("F_TABLE_NAME")).toString(), rs.getString("TYPE"));
                }
            } catch (SQLException ex) {
                LOGGER.warn(I18N.tr("Geometry columns information of tables are not available.", ex));
            }
            // Fetch all tables
            try(ResultSet rs = connection.getMetaData().getTables(null, null, null, SHOWN_TABLE_TYPES)) {
                while(rs.next()) {
                    Map<JdbcProperties, String> tableAttr = new HashMap<>();
                    TableLocation location = new TableLocation(rs);
                    if(location.getCatalog().isEmpty()) {
                        // PostGIS return empty catalog on metadata
                        location = new TableLocation(defaultCatalog, location.getSchema(), location.getTable());
                    }
                    // Make Label
                    StringBuilder label = new StringBuilder(addQuotesIfNecessary(location.getTable()));
                    if(!location.getSchema().isEmpty() && !location.getSchema().equalsIgnoreCase(defaultSchema)) {
                        label.insert(0, ".");
                        label.insert(0, addQuotesIfNecessary(location.getSchema()));
                    }
                    if(!location.getCatalog().isEmpty() && !location.getCatalog().equalsIgnoreCase(defaultCatalog)) {
                        label.insert(0, ".");
                        label.insert(0, addQuotesIfNecessary(location.getCatalog()));
                    }
                    // Shortcut location for H2 database
                    TableLocation shortLocation;
                    if(isH2) {
                        shortLocation = new TableLocation("",
                                location.getSchema().equals(defaultSchema) ? "" : location.getSchema(),
                                location.getTable());
                    } else {
                        shortLocation = new TableLocation(location.getCatalog().equalsIgnoreCase(defaultCatalog) ?
                                "" : location.getCatalog(),
                                location.getCatalog().equalsIgnoreCase(defaultCatalog) &&
                                        location.getSchema().equalsIgnoreCase(defaultSchema) ? "" : location.getSchema(),
                                location.getTable());
                    }
                    tableAttr.put(JdbcProperties.TABLE_LOCATION, shortLocation.toString(isH2));
                    tableAttr.put(JdbcProperties.TABLE_LABEL, label.toString());
                    String type = tableGeometry.get(location.toString());
                    if(type != null) {
                        tableAttr.put(JdbcProperties.COLUMN_TYPE, type);
                    }
                    newTables.add(tableAttr);
                }
            }
            tableList.clear();
            tableList.addAll(newTables);
        } catch (SQLException ex) {
            LOGGER.error(I18N.tr("Cannot read the table list", ex));
        }
    }

    /**
     * If needed, quote the table location part
     * @param tableLocationPart Table location part to quote.
     * @return Quoted table location part.
     */
    private static String addQuotesIfNecessary(String tableLocationPart) {
        if(tableLocationPart.contains(".")) {
            return "\""+tableLocationPart+"\"";
        } else {
            return tableLocationPart;
        }
    }

    /**
     * Refresh the list
     */
    private static class ReadDataManagerOnSwingThread implements Runnable {
        private WpsClientImpl wpsClient;

        private ReadDataManagerOnSwingThread(WpsClientImpl wpsClient) {
            this.wpsClient = wpsClient;
        }

        @Override
        public void run() {
            wpsClient.readDatabase();
            //Refresh the list on the swing thread
            wpsClient.awaitingRefresh.set(false);
            // An update occurs during fetching tables
            if(wpsClient.updateWhileAwaitingRefresh) {
                wpsClient.updateWhileAwaitingRefresh = false;
                wpsClient.onDataManagerChange();
            }
        }
    }
}
