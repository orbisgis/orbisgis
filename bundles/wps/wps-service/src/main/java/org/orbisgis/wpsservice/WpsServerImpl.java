package org.orbisgis.wpsservice;

import net.opengis.ows._2.CodeType;
import net.opengis.wps._2_0.*;
import net.sourceforge.cobertura.CoverageIgnore;
import org.orbisgis.corejdbc.DataSourceService;
import org.orbisgis.wpsservice.controller.execution.DataProcessingManager;
import org.orbisgis.wpsservice.controller.execution.ProcessExecutionListener;
import org.orbisgis.wpsservice.controller.process.ProcessIdentifier;
import org.orbisgis.wpsservice.controller.process.ProcessManager;
import org.orbisgis.wpsservice.utils.ProcessTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * This class is the implementation of a WPS server.
 * It is used a a base for the OrbisGIS local WPS server.
 *
 * @author Sylvain PALOMINOS
 */
public class WpsServerImpl implements WpsServer {

    private static final String OPTION_SYNC_EXEC = "sync-execute";
    private static final String OPTION_ASYNC_EXEC = "async-execute";
    /** Process polling time in milliseconds. */
    private static final long PROCESS_POLLING_MILLIS = 10000;
    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(WpsServerImpl.class);
    /** JAXB object factory for the WPS objects. */
    private static final ObjectFactory wpsObjectFactory = new ObjectFactory();

    /** List of the jobControlOption available (like ASYNC_EXECUTE, SYNC_EXECUTE) */
    private List<String> jobControlOptions;
    /** Basic WpsCapabilitiesType object of the Wps Service.
     * It contains all the basics information about the service excepts the Contents (list of available processes)*/
    private WPSCapabilitiesType basicCapabilities;
    /** Process manager which contains all the loaded scripts. */
    private ProcessManager processManager;
    /** DataSource Service from OrbisGIS */
    private DataSourceService dataSourceService;
    /** Map containing the WPS Jobs and their UUID */
    private Map<UUID, Job> jobMap;
    /** Class managing the DataProcessing classes */
    private DataProcessingManager dataProcessingManager;
    /** ExecutorService of OrbisGIS */
    private ExecutorService executorService;
    /** Database connected to the WPS server */
    private Database database;


    /**********************************************/
    /** Initialisation method of the WPS service **/
    /**********************************************/

    /**
     * Initialization of the LocalWpsServiceImplementation required by OSGI.
     */
    public void init(){
        //Initialisation of the wps service itself
        initWpsService();
        //Create the attribute for the processes execution
        processManager = new ProcessManager(dataSourceService, this);
        dataProcessingManager = new DataProcessingManager();
        jobMap = new HashMap<>();
    }

    /**
     * Initialize everything about the Wps Service
     * Generates the basic WPSCapabilitiesType of the WpsService from a resource file
     */
    private void initWpsService(){
        //Get the basic WpsCapabilitiesType from the WpsServiceBasicCapabilities.xml file
        WPSCapabilitiesType capabilitiesType = null;
        try {
            //Create the unmarshaller
            Unmarshaller unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
            if(unmarshaller != null) {
                //Get the URL of the wps service basic capabilities resource file
                URL url = this.getClass().getResource("WpsServiceBasicCapabilities.xml");
                if (url != null) {
                    //Unmarshall the wps capabilities
                    Object unmarshalledObject = unmarshaller.unmarshal(url.openStream());
                    if (unmarshalledObject instanceof JAXBElement) {
                        //Retrieve the capabilities
                        Object value = ((JAXBElement) unmarshalledObject).getValue();
                        if (value instanceof WPSCapabilitiesType) {
                            capabilitiesType = (WPSCapabilitiesType) value;
                        } else {
                            LOGGER.error("The unmarshalled WPSCapabilitiesType is not a valid WPSCapabilitiesType.");
                        }
                    } else if(unmarshalledObject != null) {
                        LOGGER.error("The unmarshalled WPSCapabilitiesType is invalid.");
                    } else {
                        LOGGER.error("The unmarshalled WPSCapabilitiesType is null.");
                    }
                } else {
                    LOGGER.error("Unable to load the WpsServiceBasicCapabilities.xml file containing the " +
                            "service basic capabilities.");
                }
            } else {
                LOGGER.error("Unable to create the unmarshaller");
            }
        } catch (JAXBException | IOException e) {
            LOGGER.error("Error on using the unmarshaller.\n"+e.getMessage());
        }
        if(capabilitiesType != null){
            basicCapabilities = capabilitiesType;
        }
        else{
            basicCapabilities = wpsObjectFactory.createWPSCapabilitiesType();
        }

        //Generate the jobControlOption list
        jobControlOptions = new ArrayList<>();
        jobControlOptions.add(OPTION_ASYNC_EXEC);
    }

    /*******************************************************************/
    /** Methods from the WpsService class.                            **/
    /** All of these methods are defined by the WPS 2.0 OGC standard **/
    /*******************************************************************/

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
            ProcessOffering processOffering = null;
            List<ProcessIdentifier> piList = processManager.getAllProcessIdentifier();
            for(ProcessIdentifier pi : piList){
                if(pi.getProcessDescriptionType().getIdentifier().getValue().equals(id.getValue())){
                    processOffering = pi.getProcessOffering();
                }
            }
            if(processOffering != null) {
                //Build the new ProcessOffering which will be return
                ProcessOffering po = new ProcessOffering();
                po.setProcessVersion(processOffering.getProcessVersion());
                po.getJobControlOptions().clear();
                po.getJobControlOptions().addAll(jobControlOptions);
                //Get the translated process and add it to the ProcessOffering
                List<DataTransmissionModeType> listTransmission = new ArrayList<>();
                listTransmission.add(DataTransmissionModeType.VALUE);
                po.getOutputTransmission().clear();
                po.getOutputTransmission().addAll(listTransmission);
                ProcessDescriptionType process = processOffering.getProcess();
                po.setProcess(ProcessTranslator.getTranslatedProcess(process, describeProcess.getLang()));
                processOfferingList.add(po);
            }
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
                processIdentifier,
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
    public StatusInfo dismiss(Dismiss dismiss) {
        UUID jobId = UUID.fromString(dismiss.getJobID());
        cancelProcess(jobId);
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
            else if(o instanceof Dismiss){
                result = dismiss((Dismiss)o);
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
    public void cancelProcess(UUID jobId){
        processManager.cancelProcess(jobId);
    }

    /*************************/
    /** Getters and setters **/
    /*************************/

    @CoverageIgnore
    protected void setDatabase(Database database){
        this.database = database;
    }

    @CoverageIgnore
    @Override
    public Database getDatabase() {
        return database;
    }

    @CoverageIgnore
    protected void setDataSourceService(DataSourceService dataSourceService){
        this.dataSourceService = dataSourceService;
    }

    @CoverageIgnore
    protected void setDataProcessingManager(DataProcessingManager dataProcessingManager){
        this.dataProcessingManager = dataProcessingManager;
    }

    protected void setExecutorService(ExecutorService executorService){
        this.executorService = executorService;
    }

    protected ExecutorService getExecutorService(){
        return executorService;
    }

    protected Map<UUID, Job> getJobMap(){
        return jobMap;
    }

    protected ProcessManager getProcessManager(){
        return processManager;
    }

    /************************/
    /** Utilities methods. **/
    /************************/

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
