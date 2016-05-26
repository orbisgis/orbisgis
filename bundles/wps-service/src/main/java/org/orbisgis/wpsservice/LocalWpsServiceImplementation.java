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

package org.orbisgis.wpsservice;

import net.opengis.ows._2.*;
import net.opengis.wps._2_0.*;
import net.opengis.wps._2_0.GetCapabilitiesType;
import net.opengis.wps._2_0.DescriptionType;
import net.opengis.wps._2_0.ObjectFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.h2gis.h2spatialapi.DriverFunction;
import org.h2gis.h2spatialapi.EmptyProgressVisitor;
import org.h2gis.h2spatialapi.ProgressVisitor;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.*;
import org.orbisgis.dbjobs.api.DriverFunctionContainer;
import org.orbisgis.frameworkapi.CoreWorkspace;
import org.orbisgis.wpsservice.controller.execution.DataProcessingManager;
import org.orbisgis.wpsservice.controller.execution.ProcessExecutionListener;
import org.orbisgis.wpsservice.controller.process.ProcessIdentifier;
import org.orbisgis.wpsservice.controller.process.ProcessManager;
import org.orbisgis.wpsservice.model.DataType;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.swing.*;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.*;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is an implementation of the LocalWpsService interface and is declared a OSGI component.
 * It gives all the methods needed by the a WPS client to be able to get a process, to configure it and to run it.
 * It also implements the DatabaseProgressionListener to be able to know the table list in the database.
 */
@Component(service = {LocalWpsService.class})
public class LocalWpsServiceImplementation implements LocalWpsService, DatabaseProgressionListener {
    /** String of the Groovy file extension. */
    public static final String GROOVY_EXTENSION = "groovy";
    private static final String WPS_SCRIPT_FOLDER = "Scripts";
    private static final String TOOLBOX_PROPERTIES = "toolbox.properties";
    private static final String PROPERTY_SOURCES = "PROPERTY_SOURCES";

    private static final String OPTION_SYNC_EXEC = "sync-execute";
    private static final String OPTION_ASYNC_EXEC = "async-execute";
    /**Array of the table type accepted. */
    private static final String[] SHOWN_TABLE_TYPES = new String[]{"TABLE","LINKED TABLE","VIEW","EXTERNAL"};
    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalWpsServiceImplementation.class);
    /** Process polling time in milliseconds. */
    private static final long PROCESS_POLLING_MILLIS = 10000;

    /** CoreWorkspace of OrbisGIS */
    private CoreWorkspace coreWorkspace;
    /** ExecutorService of OrbisGIS */
    private ExecutorService executorService;
    /** True if the H2 configuration allows the multiThread, false otherwise */
    private boolean multiThreaded;
    /** True if the database is H2, false otherwise. */
    private boolean isH2;
    /** OrbisGIS DataManager. */
    private DataManager dataManager;
    /** Class managing the DataProcessing classes */
    private DataProcessingManager dataProcessingManager;
    /** OrbisGIS DriverFunctionContainer. */
    private DriverFunctionContainer driverFunctionContainer;
    /** Process manager which contains all the loaded scripts. */
    private ProcessManager processManager;
    /** DataSource Service from OrbisGIS */
    private DataSourceService dataSourceService;
    /** Map containing object that can be used to cancel the loading of an URI. */
    private Map<URI, Object> cancelLoadMap;
    /** True if a swing runnable is pending to refresh the content of the table list, false otherwise. */
    private AtomicBoolean awaitingRefresh=new AtomicBoolean(false);
    /** True if an updates happen while another on is running. */
    private boolean updateWhileAwaitingRefresh = false;
    /** List of map containing the table with their basic information.
     * It is used as a buffer to avoid to reload all the table list to save time.
     */
    private List<Map<String, String>> tableList;
    /** Map containing the WPS Jobs and their UUID */
    private Map<UUID, Job> jobMap;

    /** Basic WpsCapabilitiesType object of the Wps Service.
     * It contains all the basics information about the service excepts the Contents (list of available processes)*/
    private WPSCapabilitiesType basicCapabilities;
    /** List of the jobControlOption avaliable (like ASYNC_EXECUTE, SYNC_EXECUTE) */
    private List<String> jobControlOptions;
    /** JAXB object factory for the WPS objects. */
    private static final ObjectFactory wpsObjectFactory = new ObjectFactory();

    @Activate
    public void init(){
        initWpsService();
        processManager = new ProcessManager(dataSourceService, this);
        dataProcessingManager = new DataProcessingManager(this);
        cancelLoadMap = new HashMap<>();
        jobMap = new HashMap<>();
        setScriptFolder();
        loadPreviousState();
        initDataBaseLink();
    }

    @Deactivate
    public void dispose(){
        //Try to save the local files loaded.
        try {
            Properties tbProperties = new Properties();
            tbProperties.setProperty(PROPERTY_SOURCES, processManager.getListSourcesAsString());
            tbProperties.store(
                    new FileOutputStream(coreWorkspace.getWorkspaceFolder() + File.separator + TOOLBOX_PROPERTIES),
                    "Save of the OrbisGIS toolBox");
        } catch (IOException e) {
            LOGGER.warn("Unable to save ToolBox state.");
        }
    }

    @Reference
    public void setCoreWorkspace(CoreWorkspace coreWorkspace) {
        this.coreWorkspace = coreWorkspace;
    }
    public void unsetCoreWorkspace(CoreWorkspace coreWorkspace) {
        this.coreWorkspace = null;
    }

    @Reference
    public void setDriverFunctionContainer(DriverFunctionContainer driverFunctionContainer) {
        this.driverFunctionContainer = driverFunctionContainer;
    }
    public void unsetDriverFunctionContainer(DriverFunctionContainer driverFunctionContainer) {
        this.driverFunctionContainer = null;
    }

    @Reference
    public void setDataSource(DataSource ds) {
        dataSourceService = (DataSourceService)ds;
    }
    public void unsetDataSource(DataSource ds) {
        dataSourceService = null;
    }

    @Reference
    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    public void unsetDataManager(DataManager dataManager) {
        this.dataManager = null;
    }

    @Reference
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
    public void unsetExecutorService(ExecutorService executorService) {
        this.executorService = null;
    }

    /**
     * Initialize all the mechanism using the database
     */
    private void initDataBaseLink(){
        if(dataManager != null) {
            //Find if the database used is H2 or not.
            //If yes, make all the processes wait for the previous one.
            multiThreaded = testDBForMultiProcess();
            if (!multiThreaded) {
                if (isH2) {
                    LOGGER.warn("Warning, because of the H2 configuration," +
                            " the toolbox won't be able to run more than one process at the same time.\n" +
                            "Try to use the following setting for H2 : 'MVCC=TRUE; LOCK_TIMEOUT=100000;" +
                            " MULTI_THREADED=TRUE'");
                } else {
                    LOGGER.warn("Warning, because of the database configuration," +
                            " the toolbox won't be able to run more than one process at the same time.");
                }
            }
            //Install database listeners
            dataManager.addDatabaseProgressionListener(this, StateEvent.DB_STATES.STATE_STATEMENT_END);
            //Call readDatabase when a SourceManager fire an event
            onDataManagerChange();
        }
        else{
            LOGGER.warn("Warning, no DataManager found.");
        }
    }

    /**
     * Initialize everything about the Wps Service
     */
    private void initWpsService(){
        //Get the basic WpsCapabilitiesType from the WpsServiceBasicCapabilities.xml file
        Unmarshaller unmarshaller = null;
        WPSCapabilitiesType capabilitiesType = null;
        try {
            unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
            URL url = this.getClass().getResource("WpsServiceBasicCapabilities.xml");
            if(url != null) {
                Object unmarshalledObject = unmarshaller.unmarshal(url.openStream());
                if (unmarshalledObject instanceof JAXBElement) {
                    Object value = ((JAXBElement) unmarshalledObject).getValue();
                    if (value instanceof WPSCapabilitiesType) {
                        capabilitiesType = (WPSCapabilitiesType) value;
                    }
                }
            }
            else{
                LOGGER.error("Unable to load the WpsServiceBasicCapabilities.xml file containing the " +
                        "service basic capabilities.");
            }
        } catch (JAXBException e) {
            LOGGER.error("Error on using the unmarshaller.\n"+e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Error on using the unmarshaller.\n"+e.getMessage());
        }
        if(unmarshaller != null && capabilitiesType != null){
            basicCapabilities = capabilitiesType;
        }
        else{
            basicCapabilities = wpsObjectFactory.createWPSCapabilitiesType();
        }

        //Generate the jobControlOption list
        jobControlOptions = new ArrayList<>();
        jobControlOptions.add(OPTION_ASYNC_EXEC);
    }

    /**
     * Sets all the default OrbisGIS WPS script into the script folder of the .OrbisGIS folder.
     */
    private void setScriptFolder(){
        if(coreWorkspace != null) {
            //Sets the WPS script folder
            File wpsScriptFolder = new File(coreWorkspace.getApplicationFolder(), WPS_SCRIPT_FOLDER);
            //Empty the script folder or create it
            if (wpsScriptFolder.exists()) {
                if (wpsScriptFolder.listFiles() != null) {
                    List<File> toDelete = Arrays.asList(wpsScriptFolder.listFiles());
                    for (File f : toDelete) {
                        f.delete();
                    }
                }
            } else {
                if (!wpsScriptFolder.mkdir()) {
                    LOGGER.warn("Unable to find or create a script folder.\nNo basic script will be available.");
                }
            }
            if (wpsScriptFolder.exists() && wpsScriptFolder.isDirectory()) {
                try {
                    //Retrieve all the scripts url
                    String folderPath = LocalWpsServiceImplementation.class.getResource("scripts").getFile();
                    Enumeration<URL> enumUrl = FrameworkUtil.getBundle(LocalWpsServiceImplementation.class).findEntries(folderPath, "*", false);
                    //For each url
                    while (enumUrl.hasMoreElements()) {
                        URL scriptUrl = enumUrl.nextElement();
                        String scriptPath = scriptUrl.getFile();
                        //Test if it's a groovy file
                        if (scriptPath.endsWith("." + GROOVY_EXTENSION)) {
                            //If the script is already in the .OrbisGIS folder, remove it.
                            for (File existingFile : wpsScriptFolder.listFiles()) {
                                if (existingFile.getName().endsWith(scriptPath) && existingFile.delete()) {
                                    LOGGER.warn("Replacing script " + existingFile.getName() + " by the default one");
                                }
                            }
                            //Copy the script into the .OrbisGIS folder.
                            OutputStream out = new FileOutputStream(
                                    new File(wpsScriptFolder.getAbsolutePath(),
                                            new File(scriptPath).getName()));
                            InputStream in = scriptUrl.openStream();
                            IOUtils.copy(in, out);
                            out.close();
                            in.close();
                        }
                    }
                    addLocalSource(wpsScriptFolder.toURI(), "orbisgis", true);
                } catch (IOException e) {
                    LOGGER.warn("Unable to copy the scripts. \n" +
                            "No basic script will be available. \n" +
                            "Error : " + e.getMessage());
                }
            }
        }
        else{
            LOGGER.warn("Warning, no CoreWorkspace found.");
        }
    }

    private void loadPreviousState(){
        if(coreWorkspace != null) {
            Properties tbProperties = new Properties();
            File propertiesFile = new File(coreWorkspace.getWorkspaceFolder() + File.separator + TOOLBOX_PROPERTIES);
            if (propertiesFile.exists()) {
                try {
                    tbProperties.load(new FileInputStream(propertiesFile));
                } catch (IOException e) {
                    LOGGER.warn("Unable to restore previous configuration of the ToolBox");
                    tbProperties = null;
                }
            }
            if(tbProperties != null){
                Object prop = tbProperties.getProperty(PROPERTY_SOURCES);
                if(prop != null && !prop.toString().isEmpty()){
                    String str = prop.toString();
                    for(String s : str.split(";")){
                        addLocalScript(new File(URI.create(s)), null, false);
                    }
                }
            }
        }
        else{
            LOGGER.warn("Warning, no CoreWorkspace found.");
        }
    }

    public void addLocalSource(URI uri, String iconName, boolean isDefaultScript){
        File file = new File(uri);
        if(file.isFile()){
            return;
        }
        for(File f : file.listFiles()) {
            addLocalScript(f, iconName, isDefaultScript);
        }
    }

    public ProcessIdentifier addLocalScript(File f, String iconName, boolean isDefaultScript){
        if(f.getName().endsWith(GROOVY_EXTENSION)) {
            processManager.addLocalScript(f.toURI(), iconName, isDefaultScript);
            ProcessIdentifier pi = processManager.getProcessIdentifier(f.toURI());
            if(pi != null) {
                return pi;
            }
        }
        return null;
    }

    public void removeProcess(URI uri){
        processManager.removeProcess(processManager.getProcess(uri));
    }

    @Override
    public boolean checkProcess(URI uri){
        ProcessIdentifier pi = processManager.getProcessIdentifier(uri);
        //If the file corresponding to the URI does not exist anymore, remove if and warn the user.
        File f = new File(uri);
        if(!f.exists()){
            processManager.removeProcess(pi.getProcessDescriptionType());
            LOGGER.error("The script '"+f.getAbsolutePath()+"' does not exist anymore.");
            return false;
        }
        //If the URI correspond to a ProcessIdentifier remove it before adding it again
        if(pi != null){
            processManager.removeProcess(pi.getProcessDescriptionType());
            return (processManager.addLocalScript(uri, pi.getCategory(), pi.isDefault()) != null);
        }
        return false;
    }

    @Override
    public WPSCapabilitiesType getCapabilities(GetCapabilitiesType getCapabilities) {
        WPSCapabilitiesType capabilitiesType = new WPSCapabilitiesType();
        capabilitiesType.setExtension(basicCapabilities.getExtension());
        capabilitiesType.setLanguages(basicCapabilities.getLanguages());
        capabilitiesType.setOperationsMetadata(basicCapabilities.getOperationsMetadata());
        capabilitiesType.setServiceIdentification(basicCapabilities.getServiceIdentification());
        capabilitiesType.setServiceProvider(basicCapabilities.getServiceProvider());
        capabilitiesType.setUpdateSequence(basicCapabilities.getUpdateSequence());
        capabilitiesType.setVersion(basicCapabilities.getVersion());

        /** Sets the Contents **/
        Contents contents = new Contents();
        List<ProcessSummaryType> processSummaryTypeList = new ArrayList<>();
        List<ProcessDescriptionType> processList = getProcessList();
        for(ProcessDescriptionType process : processList) {
            ProcessSummaryType processSummaryType = new ProcessSummaryType();
            processSummaryType.getJobControlOptions().clear();
            processSummaryType.getJobControlOptions().addAll(jobControlOptions);
            processSummaryType.getAbstract().clear();
            processSummaryType.getAbstract().addAll(process.getAbstract());
            processSummaryType.setIdentifier(process.getIdentifier());
            processSummaryType.getKeywords().clear();
            processSummaryType.getKeywords().addAll(process.getKeywords());
            processSummaryType.getMetadata().clear();
            processSummaryType.getMetadata().addAll(process.getMetadata());
            processSummaryType.getTitle().clear();
            processSummaryType.getTitle().addAll(process.getTitle());

            processSummaryTypeList.add(processSummaryType);
        }
        contents.getProcessSummary().clear();
        contents.getProcessSummary().addAll(processSummaryTypeList);
        capabilitiesType.setContents(contents);

        return capabilitiesType;
    }

    @Override
    public ProcessOfferings describeProcess(DescribeProcess describeProcess) {
        List<CodeType> idList = describeProcess.getIdentifier();

        ProcessOfferings processOfferings = new ProcessOfferings();
        List<ProcessOffering> processOfferingList = new ArrayList<>();
        for(CodeType id : idList) {
            ProcessOffering processOffering = new ProcessOffering();
            processOffering.getJobControlOptions().clear();
            processOffering.getJobControlOptions().addAll(jobControlOptions);
            //Get the translated process and add it to the ProcessOffering
            ProcessDescriptionType process = getProcessFromIdentifier(id);
            List<DataTransmissionModeType> listTransmission = new ArrayList<>();
            listTransmission.add(DataTransmissionModeType.VALUE);
            processOffering.getOutputTransmission().clear();
            processOffering.getOutputTransmission().addAll(listTransmission);
            processOffering.setProcess(getTranslatedProcess(process, describeProcess.getLang()));
            processOfferingList.add(processOffering);
        }
        processOfferings.getProcessOffering().clear();
        processOfferings.getProcessOffering().addAll(processOfferingList);
        return processOfferings;
    }

    @Override
    public Object execute(ExecuteRequestType execute) {
        //Generate the DataMap
        Map<URI, Object> dataMap = new HashMap<>();
        for(DataInputType input : execute.getInput()){
            URI id = URI.create(input.getId());
            Object data;
            if(input.getData().getContent().size() == 1){
                data = input.getData().getContent().get(0);
            }
            else{
                data = input.getData().getContent();
            }
            dataMap.put(id, data);
        }
        //Generation of the StatusInfo
        StatusInfo statusInfo = new StatusInfo();
        //Generation of the Job unique ID
        UUID jobId = UUID.randomUUID();
        statusInfo.setJobID(jobId.toString());
        //Get the Process
        ProcessIdentifier processIdentifier = processManager.getProcessIdentifier(execute.getIdentifier());
        //Generate the processInstance
        Job job = new Job(processIdentifier.getProcessDescriptionType(), jobId, dataMap);
        jobMap.put(jobId, job);
        statusInfo.setStatus(job.getState().name());

        //Process execution in new thread
        ProcessWorker worker = new ProcessWorker(job,
                processIdentifier.getProcessDescriptionType(),
                dataProcessingManager,
                processManager,
                dataMap);

        if(executorService != null){
            executorService.execute(worker);
        }
        else {
            worker.run();
        }
        statusInfo.setStatus(job.getState().name());
        XMLGregorianCalendar date = getXMLGregorianCalendar(PROCESS_POLLING_MILLIS);
        statusInfo.setNextPoll(date);
        return statusInfo;
    }

    @Override
    public StatusInfo getStatus(GetStatus getStatus) {
        //Get the job concerned by the getStatus request
        UUID jobId = UUID.fromString(getStatus.getJobID());
        Job job = jobMap.get(jobId);
        //Generate the StatusInfo to return
        StatusInfo statusInfo = new StatusInfo();
        statusInfo.setJobID(jobId.toString());
        statusInfo.setStatus(job.getState().name());
        if(!job.getState().equals(ProcessExecutionListener.ProcessState.FAILED) &&
                !job.getState().equals(ProcessExecutionListener.ProcessState.SUCCEEDED)) {
            XMLGregorianCalendar date = getXMLGregorianCalendar(PROCESS_POLLING_MILLIS);
            statusInfo.setNextPoll(date);
        }
        return statusInfo;
    }

    @Override
    public Result getResult(GetResult getResult) {
        Result result = new Result();
        //generate the XMLGregorianCalendar Object to put in the Result Object
        //TODO make the service be able to set the expiration date
        XMLGregorianCalendar date = getXMLGregorianCalendar(0);
        result.setExpirationDate(date);
        //Get the concerned Job
        UUID jobId = UUID.fromString(getResult.getJobID());
        Job job = jobMap.get(jobId);
        result.setJobID(jobId.toString());
        //Get the list of outputs to transmit
        List<DataOutputType> listOutput = new ArrayList<>();
        for(Map.Entry<URI, Object> entry : job.getDataMap().entrySet()){
            //Test if the URI is an Output URI.
            boolean contained = false;
            for(OutputDescriptionType output : job.getProcess().getOutput()){
                if(output.getIdentifier().getValue().equals(entry.getKey().toString())){
                    contained = true;
                }
            }
            if(contained) {
                //Create the DataOutputType object, set it and add it to the output list.
                DataOutputType output = new DataOutputType();
                output.setId(entry.getKey().toString());
                Data data = new Data();
                data.setEncoding("simple");
                data.setMimeType("");
                //TODO make the difference between the different data type from the map.
                List<Serializable> serializableList = new ArrayList<>();
                serializableList.add(entry.getValue().toString());
                data.getContent().clear();
                data.getContent().addAll(serializableList);
                output.setData(data);
                listOutput.add(output);
            }
        }
        result.getOutput().clear();
        result.getOutput().addAll(listOutput);
        return result;
    }

    @Override
    public OutputStream callOperation(InputStream xml) {
        Object result = null;
        ObjectFactory factory = new ObjectFactory();
        try {
            Unmarshaller unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
            Object o = unmarshaller.unmarshal(xml);
            if(o instanceof JAXBElement){
                o = ((JAXBElement) o).getValue();
            }
            //Call the WPS method associated to the unmarshalled object
            if(o instanceof GetCapabilitiesType){
                result = factory.createCapabilities(getCapabilities((GetCapabilitiesType)o));
            }
            else if(o instanceof DescribeProcess){
                result = describeProcess((DescribeProcess)o);
            }
            else if(o instanceof ExecuteRequestType){
                result = execute((ExecuteRequestType)o);
            }
            else if(o instanceof GetStatus){
                result = getStatus((GetStatus)o);
            }
            else if(o instanceof GetResult){
                result = getResult((GetResult)o);
            }
        } catch (JAXBException e) {
            LOGGER.error("Unable to parse the incoming xml\n" + e.getMessage());
            return new ByteArrayOutputStream();
        }
        //Write the request answer in an ByteArrayOutputStream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if(result != null){
            try {
                //Marshall the WpsService answer
                Marshaller marshaller = JaxbContainer.JAXBCONTEXT.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                marshaller.marshal(result, out);
            } catch (JAXBException e) {
                LOGGER.error("Unable to parse the outcoming xml\n" + e.getMessage());
            }
        }
        return out;
    }

    @Override
    public boolean checkFolder(URI uri){
        File f = new File(uri);
        if(f.exists() && f.isDirectory()){
            for(File file : f.listFiles()){
                if(file.getAbsolutePath().endsWith("."+GROOVY_EXTENSION)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Map<String, String> getImportableFormat(boolean onlySpatial){
        Map<String, String> formatMap = new HashMap<>();
        //Try to get the available import format
        if(driverFunctionContainer != null) {
            for (DriverFunction df : driverFunctionContainer.getDriverFunctionList()) {
                for (String ext : df.getImportFormats()) {
                    if (df.isSpatialFormat(ext) || !onlySpatial) {
                        formatMap.put(ext, df.getFormatDescription(ext));
                    }
                }
            }
        }
        return formatMap;
    }

    @Override
    public Map<String, String> getExportableFormat(boolean onlySpatial){
        Map<String, String> formatMap = new HashMap<>();
        //Try to get the available export format
        if(driverFunctionContainer != null) {
            for (DriverFunction df : driverFunctionContainer.getDriverFunctionList()) {
                for (String ext : df.getExportFormats()) {
                    if (df.isSpatialFormat(ext) || !onlySpatial) {
                        formatMap.put(ext, df.getFormatDescription(ext));
                    }
                }
            }
        }
        return formatMap;
    }

    @Override
    public Map<String, Boolean> getGeocatalogTableList(boolean onlySpatial) {
        Map<String, Boolean> mapTable = new HashMap<>();
        String defaultSchema = (isH2)?"PUBLIC":"public";
        //Read the tableList to get the desired tables
        for(Map<String, String> map : tableList){
            if(onlySpatial){
                //Test if the table contains a geometrical field (if the table is spatial)
                if(map.containsKey(GEOMETRY_TYPE)){
                    if(map.containsKey(TABLE_LOCATION)) {
                        TableLocation tablelocation = TableLocation.parse(map.get(TABLE_LOCATION), isH2);
                        //If the table is in the default schema, just add its name
                        if (tablelocation.getSchema(defaultSchema).equals(defaultSchema)) {
                            mapTable.put(tablelocation.getTable(), map.containsKey(GEOMETRY_TYPE));
                        }
                        //If not, add the schema name '.' the table name (SCHEMA.TABLE)
                        else {
                            mapTable.put(tablelocation.getSchema() + "." + tablelocation.getTable(),
                                    map.containsKey(GEOMETRY_TYPE));
                        }
                    }
                }
            }
            //Else add all the tables
            else{
                if(map.containsKey(TABLE_LOCATION)) {
                    TableLocation tablelocation = TableLocation.parse(map.get(TABLE_LOCATION), isH2);
                    //If the table is in the default schema, just add its name
                    if (tablelocation.getSchema(defaultSchema).equals(defaultSchema)) {
                        mapTable.put(tablelocation.getTable(), map.containsKey(GEOMETRY_TYPE));
                    }
                    //If not, add the schema name '.' the table name (SCHEMA.TABLE)
                    else {
                        mapTable.put(tablelocation.getSchema() + "." + tablelocation.getTable(), map.containsKey(GEOMETRY_TYPE));
                    }
                }
            }
        }
        return mapTable;
    }

    @Override
    public Map<String, Object> getFieldInformation(String tableName, String fieldName){
        Map<String, Object> map = new HashMap<>();
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            TableLocation tableLocation = TableLocation.parse(tableName);
            List<String> geometricFields = SFSUtilities.getGeometryFields(connection, tableLocation);
            boolean isGeometric = false;
            for(String field : geometricFields){
                if(field.equals(fieldName)){
                    isGeometric = true;
                }
            }
            if(isGeometric) {
                int geometryId = SFSUtilities.getGeometryType(connection, tableLocation, fieldName);
                String geometryType = SFSUtilities.getGeometryTypeNameFromCode(geometryId);
                int srid = SFSUtilities.getSRID(connection, tableLocation);
                //TODO : move this statement to SFSUtilities or JDBCUtilities to request the table dimension.
                Statement statement = connection.createStatement();
                String query = "SELECT COORD_DIMENSION FROM GEOMETRY_COLUMNS WHERE F_TABLE_NAME LIKE '" +
                        TableLocation.parse(tableName).getTable() + "' AND F_GEOMETRY_COLUMN LIKE '" +
                        TableLocation.quoteIdentifier(fieldName) + "';";
                ResultSet rs = statement.executeQuery(query);
                int dimension;
                if (rs.next()) {
                    dimension = rs.getInt(1);
                } else {
                    dimension = 0;
                }
                map.put(GEOMETRY_TYPE, geometryType);
                map.put(TABLE_SRID, srid);
                map.put(TABLE_DIMENSION, dimension);
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to the the field '" + tableName+"."+fieldName+"' information.\n"+ e.getMessage());
        }
        return map;
    }

    @Override
    public List<String> getTableFieldList(String tableName, List<DataType> dataTypes, List<DataType> excludedTypes){
        if(dataTypes == null){
            dataTypes = new ArrayList<>();
        }
        if(excludedTypes == null){
            excludedTypes = new ArrayList<>();
        }
        List<String> fieldList = new ArrayList<>();
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            DatabaseMetaData dmd = connection.getMetaData();
            TableLocation tablelocation = TableLocation.parse(tableName, isH2);
            ResultSet result = dmd.getColumns(tablelocation.getCatalog(), tablelocation.getSchema(),
                    tablelocation.getTable(), "%");
            while(result.next()){
                if (!dataTypes.isEmpty()) {
                    for (DataType dataType : dataTypes) {
                        if (DataType.testDBType(dataType, result.getObject(6).toString())) {
                            fieldList.add(result.getObject(4).toString());
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
                        fieldList.add(result.getObject(4).toString());
                    }
                }else{
                    fieldList.add(result.getObject(4).toString());
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to get the table '"+tableName+"' field list.\n"+e.getMessage());
        }
        return fieldList;
    }

    @Override
    public List<String> getFieldValueList(String tableName, String fieldName) {
        List<String> fieldValues = new ArrayList<>();
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            tableName = TableLocation.parse(tableName, isH2).toString();
            List<String> fieldNames = JDBCUtilities.getFieldNames(connection.getMetaData(), tableName);
            if(fieldNames.isEmpty()){
                return fieldValues;
            }
            for(String field : fieldNames){
                if(field.equalsIgnoreCase(fieldName)){
                    fieldName = field;
                    break;
                }
            }
            fieldValues.addAll(JDBCUtilities.getUniqueFieldValues(connection,
                    tableName,
                    fieldName));
        } catch (SQLException e) {
            LOGGER.error("Unable to get the field '"+tableName+"."+fieldName+"' value list.\n"+e.getMessage());
        }
        return fieldValues;
    }

    @Override
    public void removeTempTable(String tableName){
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            tableName = TableLocation.parse(tableName, isH2).getTable();
            Statement statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS " + tableName);
        } catch (SQLException e) {
            LOGGER.error("Cannot remove the table '"+tableName+"'.\n"+e.getMessage());
        }
    }

    @Override
    public void saveURI(URI uri, String tableName){
        try {
            tableName = TableLocation.parse(tableName, isH2).getTable();
            File f = new File(uri);
            if(!f.exists()){
                f.createNewFile();
            }
            //Find the good driver and save the file.
            String extension = FilenameUtils.getExtension(f.getAbsolutePath());
            DriverFunction driver = driverFunctionContainer.getImportDriverFromExt(
                    extension, DriverFunction.IMPORT_DRIVER_TYPE.COPY);
            driver.exportTable(dataManager.getDataSource().getConnection(), tableName,
                    f, new EmptyProgressVisitor());
        } catch (SQLException|IOException e) {
            LOGGER.error("Cannot save the table '"+tableName+"'\n"+e.getMessage());
        }
    }

    @Override
    public String loadURI(URI uri, boolean copyInBase) {
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            File f = new File(uri);
            //Get the table name of the file
            String baseName = TableLocation.capsIdentifier(FilenameUtils.getBaseName(f.getName()), isH2);
            String tableName = dataManager.findUniqueTableName(baseName).replaceAll("\"", "");
            //Find the corresponding driver and load the file
            String extension = FilenameUtils.getExtension(f.getAbsolutePath());
            Statement statement = connection.createStatement();
            if(extension.equalsIgnoreCase("csv")){
                //Save the statement to be able to cancel it.
                cancelLoadMap.put(uri, statement);
                statement.execute("CREATE TEMPORARY TABLE "+tableName+" AS SELECT * FROM CSVRead('"+f.getAbsolutePath()+"', NULL, 'fieldSeparator=;');");
                cancelLoadMap.remove(uri);
            }
            else {
                ProgressVisitor pv = new EmptyProgressVisitor();
                //Save the progress visitor to be able to cancel it.
                cancelLoadMap.put(uri, pv);
                if(copyInBase || !isH2){
                    DriverFunction driver = driverFunctionContainer.getImportDriverFromExt(
                            extension, DriverFunction.IMPORT_DRIVER_TYPE.COPY);
                    driver.importFile(dataManager.getDataSource().getConnection(), tableName, f, pv);
                }
                else {
                    DriverFunction driver = driverFunctionContainer.getImportDriverFromExt(
                            extension, DriverFunction.IMPORT_DRIVER_TYPE.LINK);
                    driver.importFile(dataManager.getDataSource().getConnection(), tableName, f, pv);
                }
                cancelLoadMap.remove(uri);
            }
            return tableName;
        } catch (SQLException|IOException e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    @Override
    public void cancelLoadUri(URI uri){
        Object object = cancelLoadMap.get(uri);
        //If the object from the map is a Statement, cancel and close it.
        if(object instanceof Statement){
            try {
                ((Statement)object).cancel();
                ((Statement)object).close();
            } catch (SQLException e) {
                LOGGER.error("Unable to cancel the lodaing of '"+uri+"'.\n"+e.getMessage());
            }
        }
        //If the object from the map is a ProgressVisitor, cancel it.
        else if(object instanceof ProgressVisitor){
            ((ProgressVisitor)object).cancel();
        }
    }

    @Override
    public boolean isH2(){
        return isH2;
    }

    private boolean testDBForMultiProcess(){
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            if(dataManager != null){
                isH2 = JDBCUtilities.isH2DataBase(connection.getMetaData());
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

    @Override
    public void cancelProcess(URI uri){
        processManager.cancelProcess(processManager.getProcess(uri));
    }

    /**
     * Method called when a change happens in the DataManager (i.e. a table suppression, a table add ...)
     */
    public void onDataManagerChange() {
        //If not actually doing a refresh, do it.
        if(!awaitingRefresh.getAndSet(true)) {
            ReadDataManagerOnSwingThread worker = new ReadDataManagerOnSwingThread(this);
            worker.execute();
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
    protected void readDatabase() {
        List<Map<String, String>> newTables = new ArrayList<>();
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
                LOGGER.warn("Geometry columns information of tables are not available", ex);
            }
            // Fetch all tables
            try(ResultSet rs = connection.getMetaData().getTables(null, null, null, SHOWN_TABLE_TYPES)) {
                while(rs.next()) {
                    Map<String, String> tableAttr = new HashMap<>();
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
                    tableAttr.put(TABLE_LOCATION, shortLocation.toString(isH2));
                    tableAttr.put(TABLE_LABEL, label.toString());
                    String type = tableGeometry.get(location.toString());
                    if(type != null) {
                        tableAttr.put(GEOMETRY_TYPE, type);
                    }
                    newTables.add(tableAttr);
                }
            }
            tableList = newTables;
        } catch (SQLException ex) {
            LOGGER.error("Cannot read the table list", ex);
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
     * Refresh the JList on the swing thread
     */
    private static class ReadDataManagerOnSwingThread extends SwingWorker<Boolean, Boolean> {
        private LocalWpsServiceImplementation wpsService;

        private ReadDataManagerOnSwingThread(LocalWpsServiceImplementation wpsService) {
            this.wpsService = wpsService;
        }

        @Override
        protected Boolean doInBackground() throws Exception {
            wpsService.readDatabase();
            return true;
        }

        @Override
        protected void done() {
            //Refresh the JList on the swing thread
            wpsService.awaitingRefresh.set(false);
            // An update occurs during fetching tables
            if(wpsService.updateWhileAwaitingRefresh) {
                wpsService.updateWhileAwaitingRefresh = false;
                wpsService.onDataManagerChange();
            }
        }
    }

    /**
     * Returns the list of processes managed by the wpsService.
     * @return The list of processes managed by the wpsService.
     */
    private List<ProcessDescriptionType> getProcessList(){
        List<ProcessDescriptionType> processList = new ArrayList<>();
        List<ProcessIdentifier> piList = processManager.getAllProcessIdentifier();
        for(ProcessIdentifier pi : piList){
            processList.add(pi.getProcessDescriptionType());
        }
        return processList;
    }

    /**
     * Returns the process containing the given identifier.
     * @param identifier Identifier contained by the needed process.
     * @return The process containing the identifier.
     */
    private ProcessDescriptionType getProcessFromIdentifier(CodeType identifier){
        List<ProcessIdentifier> piList = processManager.getAllProcessIdentifier();
        for(ProcessIdentifier pi : piList){
            if(pi.getProcessDescriptionType().getIdentifier().getValue().equals(identifier.getValue())){
                return pi.getProcessDescriptionType();
            }
        }
        return null;
    }

    /**
     * Return the process with the given language translation.
     * If the asked translation doesn't exists, use the english one. If it doesn't exists too, uses one of the others.
     * @param process Process to traduce.
     * @param language Language asked.
     * @return The traduced process.
     */
    private ProcessDescriptionType getTranslatedProcess(ProcessDescriptionType process, String language){
        ProcessDescriptionType translatedProcess = new ProcessDescriptionType();
        translatedProcess.setLang(language);
        List<InputDescriptionType> inputList = new ArrayList<>();
        for(InputDescriptionType input : process.getInput()){
            InputDescriptionType translatedInput = new InputDescriptionType();
            translatedInput.setDataDescription(input.getDataDescription());
            translatedInput.setMaxOccurs(input.getMaxOccurs());
            translatedInput.setMinOccurs(input.getMinOccurs());
            translateDescriptionType(translatedInput, input, language);
            inputList.add(translatedInput);
        }
        translatedProcess.getInput().clear();
        translatedProcess.getInput().addAll(inputList);
        List<OutputDescriptionType> outputList = new ArrayList<>();
        for(OutputDescriptionType output : process.getOutput()){
            OutputDescriptionType translatedOutput = new OutputDescriptionType();
            translatedOutput.setDataDescription(output.getDataDescription());
            translateDescriptionType(translatedOutput, output, language);
            outputList.add(translatedOutput);
        }
        translatedProcess.getOutput().clear();
        translatedProcess.getOutput().addAll(outputList);
        translateDescriptionType(translatedProcess, process, language);
        return translatedProcess;
    }

    /**
     * Sets the given translatedDescriptionType with the traduced elements of the source descriptionType.
     * If the asked translation doesn't exists, use the english one. If it doesn't exists too, uses one of the others.
     *
     * @param translatedDescriptionType Translated DescriptionType.
     * @param descriptionType Source DescriptionType.
     * @param language Language asked.
     */
    private void translateDescriptionType(DescriptionType translatedDescriptionType, DescriptionType descriptionType, String language){
        String enLanguage = "en";
        translatedDescriptionType.setIdentifier(descriptionType.getIdentifier());
        translatedDescriptionType.getMetadata().clear();
        translatedDescriptionType.getMetadata().addAll(descriptionType.getMetadata());
        //Find the good abstract
        LanguageStringType translatedAbstract = new LanguageStringType();
        boolean defaultAbstrFound = false;
        for(LanguageStringType abstr : descriptionType.getAbstract()){
            if(abstr.getLang() != null && abstr.getLang().equals(language)){
                translatedAbstract = abstr;
                break;
            }
            else if(abstr.getLang() != null && abstr.getLang().equals(enLanguage)){
                translatedAbstract = abstr;
                defaultAbstrFound = true;
            }
            else if(!defaultAbstrFound){
                translatedAbstract = abstr;
            }
        }
        List<LanguageStringType> abstrList = new ArrayList<>();
        abstrList.add(translatedAbstract);
        translatedDescriptionType.getAbstract().clear();
        translatedDescriptionType.getAbstract().addAll(abstrList);
        //Find the good title
        LanguageStringType translatedTitle = new LanguageStringType();
        boolean defaultTitleFound = false;
        for(LanguageStringType title : descriptionType.getTitle()){
            if(title.getLang() != null && title.getLang().equals(language)){
                translatedTitle = title;
                break;
            }
            else if(title.getLang() != null && title.getLang().equals(enLanguage)){
                translatedTitle = title;
                defaultTitleFound = true;
            }
            else if(!defaultTitleFound){
                translatedTitle = title;
            }
        }
        List<LanguageStringType> titleList = new ArrayList<>();
        titleList.add(translatedTitle);
        translatedDescriptionType.getTitle().clear();
        translatedDescriptionType.getTitle().addAll(titleList);
        //Find the good keywords
        List<KeywordsType> keywordsList = new ArrayList<>();
        KeywordsType translatedKeywords = new KeywordsType();
        List<LanguageStringType> keywordList = new ArrayList<>();
        for(KeywordsType keywords : descriptionType.getKeywords()) {
            LanguageStringType translatedKeyword = new LanguageStringType();
            boolean defaultKeywordFound = false;
            for (LanguageStringType keyword : keywords.getKeyword()) {
                if (keyword.getLang() != null && keyword.getLang().equals(language)) {
                    translatedKeyword = keyword;
                    break;
                } else if (keyword.getLang() != null && keyword.getLang().equals(enLanguage)) {
                    translatedKeyword = keyword;
                    defaultKeywordFound = true;
                } else if (!defaultKeywordFound) {
                    translatedKeyword = keyword;
                }
            }
            keywordList.add(translatedKeyword);
        }
        translatedKeywords.getKeyword().clear();
        translatedKeywords.getKeyword().addAll(keywordList);
        keywordsList.add(translatedKeywords);
        translatedDescriptionType.getKeywords().clear();
        translatedDescriptionType.getKeywords().addAll(keywordsList);
    }

    /**
     * Creates a XMLGregorianCalendar object which represent the date of now + durationInMillis.
     * @param durationInMillis Duration in milliseconds to add to thenow date.
     * @return A XMLGregorianCalendar object which represent the date of now + durationInMillis.
     */
    private XMLGregorianCalendar getXMLGregorianCalendar(long durationInMillis){
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        XMLGregorianCalendar date = null;
        try {
            DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
            date = datatypeFactory.newXMLGregorianCalendar(calendar);
            Duration duration = datatypeFactory.newDuration(durationInMillis);
            date.add(duration);
        } catch (DatatypeConfigurationException e) {
            LOGGER.error("Unable to generate the XMLGregorianCalendar object.\n"+e.getMessage());
        }
        return date;
    }
}
