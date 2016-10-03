package org.orbisgis.wpsservice;

import net.opengis.ows._2.*;
import net.opengis.wps._2_0.*;
import net.opengis.wps._2_0.GetCapabilitiesType;
import net.opengis.wps._2_0.ObjectFactory;
import org.orbisgis.corejdbc.DataSourceService;
import org.orbisgis.wpsservice.controller.execution.DataProcessingManager;
import org.orbisgis.wpsservice.controller.execution.ProcessExecutionListener;
import org.orbisgis.wpsservice.controller.execution.ProcessWorker;
import org.orbisgis.wpsservice.controller.process.ProcessIdentifier;
import org.orbisgis.wpsservice.controller.process.ProcessManager;
import org.orbisgis.wpsservice.controller.utils.Job;
import org.orbisgis.wpsservice.model.JaxbContainer;
import org.orbisgis.wpsservice.utils.ProcessTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

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
 * This class is an implementation of a WPS server.
 * It is used a a base for the OrbisGIS local WPS server.
 *
 * @author Sylvain PALOMINOS
 */
public class WpsServerImpl implements WpsServer {

    /** Execution options */
    private static final String OPTION_SYNC_EXEC = "sync-execute";
    private static final String OPTION_ASYNC_EXEC = "async-execute";
    /** Process polling time in milliseconds. */
    private static final long PROCESS_POLLING_MILLIS = 10000;
    /** Server version. */
    private static final String SERVER_VERSION = "2.0.0";
    /** Server default language. */
    private static final String DEFAULT_LANGUAGE = "en";
    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(WpsServerImpl.class);
    /** JAXB object factory for the WPS objects. */
    private static final ObjectFactory wpsObjectFactory = new ObjectFactory();
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(WpsServerImpl.class);

    /** Server supported languages. */
    private List<String> supportedLanguages;
    /** List of the jobControlOption available (like ASYNC_EXECUTE, SYNC_EXECUTE) */
    private List<String> jobControlOptions;
    /** List of basic capabilities.
     * There should be one basic capabilities for each languages supported by the server.
     * The basic capabilities is used to build the answer to the GetCapabilities request.*/
    private List<WPSCapabilitiesType> basicCapabilities;
    /** The default wps capabilities. */
    private WPSCapabilitiesType defaultBasicCapabilities;
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
    /** Map containing all the properties to give to the groovy object.
     * The following words are reserved and SHOULD NOT be used as keys : 'logger', 'sql', 'isH2'. */
    protected Map<String, Object> propertiesMap;

    private enum SectionName {ServiceIdentification, ServiceProvider, OperationMetadata, Contents, Languages, All}


    /**********************************************/
    /** Initialisation method of the WPS service **/
    /**********************************************/

    /**
     * Initialization of the WpsServiceImpl.
     */
    public void init(){
        basicCapabilities = new ArrayList<>();
        dataProcessingManager = new DataProcessingManager();
        jobMap = new HashMap<>();
        supportedLanguages = new ArrayList<>();
        supportedLanguages.add(DEFAULT_LANGUAGE);
        supportedLanguages.add("fr");
        propertiesMap = new HashMap<>();
        //Initialisation of the wps service itself
        initWpsService();
        //Creates the attribute for the processes execution
        processManager = new ProcessManager(dataSourceService, this);
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
                            LOGGER.error(I18N.tr("The unmarshalled WPSCapabilitiesType is not a valid" +
                                    " WPSCapabilitiesType."));
                        }
                    } else if(unmarshalledObject != null) {
                        LOGGER.error(I18N.tr("The unmarshalled WPSCapabilitiesType is invalid."));
                    } else {
                        LOGGER.error(I18N.tr("The unmarshalled WPSCapabilitiesType is null."));
                    }
                } else {
                    LOGGER.error(I18N.tr("Unable to load the WpsServiceBasicCapabilities.xml file containing the " +
                            "service basic capabilities."));
                }
            } else {
                LOGGER.error(I18N.tr("Unable to create the unmarshaller"));
            }
        } catch (JAXBException | IOException e) {
            LOGGER.error(I18N.tr("Error on using the unmarshaller.\nCause : {0}.", e.getMessage()));
        }
        if(capabilitiesType != null){
            defaultBasicCapabilities = capabilitiesType;
        }
        else{
            defaultBasicCapabilities = wpsObjectFactory.createWPSCapabilitiesType();
            defaultBasicCapabilities.setVersion(SERVER_VERSION);
            CapabilitiesBaseType.Languages languages = new CapabilitiesBaseType.Languages();
            languages.getLanguage().add(DEFAULT_LANGUAGE);
            defaultBasicCapabilities.setLanguages(languages);
            defaultBasicCapabilities.setContents(new Contents());
            defaultBasicCapabilities.setOperationsMetadata(new OperationsMetadata());
        }
        basicCapabilities.add(defaultBasicCapabilities);

        //Generate the jobControlOption list
        jobControlOptions = new ArrayList<>();
        jobControlOptions.add(OPTION_ASYNC_EXEC);
    }

    /*******************************************************************/
    /** Methods from the WpsService class.                            **/
    /** All of these methods are defined by the WPS 2.0 OGC standard  **/
    /*******************************************************************/

    @Override
    public Object getCapabilities(GetCapabilitiesType getCapabilities){
        /*** First check the getCapabilities for exceptions **/
        ExceptionReport exceptionReport = new ExceptionReport();
        if(getCapabilities == null){
            ExceptionType exceptionType = new ExceptionType();
            exceptionType.setExceptionCode("NoApplicableCode");
            exceptionReport.getException().add(exceptionType);
            return exceptionReport;
        }
        //Accepted versions check
        //If the version is not supported, add an ExceptionType with the error.
        if(getCapabilities.getAcceptVersions() != null &&
                getCapabilities.getAcceptVersions().getVersion() != null &&
                !getCapabilities.getAcceptVersions().getVersion().contains(SERVER_VERSION)){
            ExceptionType exceptionType = new ExceptionType();
            exceptionType.setExceptionCode("VersionNegotiationFailed");
            exceptionReport.getException().add(exceptionType);
        }
        //Sections check
        //Check if all the section values are one of the 'SectionName' enum values.
        //If not, add an ExceptionType with the error.
        List<SectionName> requestedSections = new ArrayList<>();
        if(getCapabilities.getSections() != null && getCapabilities.getSections().getSection() != null) {
            for (String section : getCapabilities.getSections().getSection()) {
                boolean validSection = false;
                for (SectionName sectionName : SectionName.values()) {
                    if (section.equals(sectionName.name())) {
                        validSection = true;
                    }
                }
                if (!validSection) {
                    ExceptionType exceptionType = new ExceptionType();
                    exceptionType.setExceptionCode("InvalidParameterValue");
                    exceptionType.setLocator("Sections:"+section);
                    exceptionReport.getException().add(exceptionType);
                }
                else{
                    requestedSections.add(SectionName.valueOf(section));
                }
            }
        }
        else{
            requestedSections.add(SectionName.All);
        }
        //TODO be able to manage the UpdateSequence parameter
        //TODO be able to manage the AcceptFormat parameter
        //Languages check
        //If the language is not supported, add an ExceptionType with the error.
        String requestLanguage = DEFAULT_LANGUAGE;
        if(getCapabilities.getAcceptLanguages() != null &&
                getCapabilities.getAcceptLanguages().getLanguage() != null &&
                !getCapabilities.getAcceptLanguages().getLanguage().isEmpty()) {
            List<String> requestedLanguages = getCapabilities.getAcceptLanguages().getLanguage();
            boolean isAnyLanguage = requestedLanguages.contains("*");
            boolean languageFound = false;
            //First try to find the first languages requested by the client which is supported by the server
            for (String language : requestedLanguages) {
                if (supportedLanguages.contains(language)) {
                    requestLanguage = language;
                    languageFound = true;
                    break;
                }
            }
            //If not language was found, try to get one with best-effort semantic
            if(!languageFound){
                for (String language : requestedLanguages) {
                    //avoid to test "*" language
                    if(!language.equals("*")) {
                        String baseLanguage = language.substring(0, 2);
                        for (String serverLanguage : supportedLanguages) {
                            if (serverLanguage.substring(0, 2).equals(baseLanguage)) {
                                requestLanguage = language;
                                languageFound = true;
                                break;
                            }
                        }
                    }
                }
            }
            //If not language was found, try to use any language if allowed
            if(!languageFound  && isAnyLanguage){
                requestLanguage = DEFAULT_LANGUAGE;
                languageFound = true;
            }
            //If no compatible language has been found and not any language are accepted
            if (!languageFound) {
                ExceptionType exceptionType = new ExceptionType();
                exceptionType.setExceptionCode("InvalidParameterValue");
                exceptionType.setLocator("AcceptLanguages");
                exceptionReport.getException().add(exceptionType);
            }
        }

        if(!exceptionReport.getException().isEmpty()){
            exceptionReport.setLang(requestLanguage);
            exceptionReport.setVersion(SERVER_VERSION);
            return exceptionReport;
        }

        /** Building of the WPSCapabilitiesTypeAnswer **/

        //Retrieve the basic capabilities which have the good language
        WPSCapabilitiesType capabilities = null;
        for(WPSCapabilitiesType capabilitiesType : basicCapabilities){
            if(capabilitiesType.getLanguages().getLanguage().contains(requestLanguage)){
                capabilities = capabilitiesType;
            }
        }
        if(capabilities == null){
            capabilities = defaultBasicCapabilities;
        }
        //TODO add the UpdateSequence element
        //Copy the content of the basicCapabilities into the new one
        WPSCapabilitiesType capabilitiesType = new WPSCapabilitiesType();
        capabilitiesType.setExtension(capabilities.getExtension());
        capabilitiesType.setUpdateSequence(capabilities.getUpdateSequence());
        capabilitiesType.setVersion(capabilities.getVersion());
        if(requestedSections.contains(SectionName.All) || requestedSections.contains(SectionName.Languages)) {
            capabilitiesType.setLanguages(capabilities.getLanguages());
        }
        if(requestedSections.contains(SectionName.All) || requestedSections.contains(SectionName.OperationMetadata)) {
            capabilitiesType.setOperationsMetadata(capabilities.getOperationsMetadata());
        }
        if(requestedSections.contains(SectionName.All) || requestedSections.contains(SectionName.ServiceIdentification)) {
            capabilitiesType.setServiceIdentification(capabilities.getServiceIdentification());
        }
        if(requestedSections.contains(SectionName.All) || requestedSections.contains(SectionName.ServiceProvider)) {
            capabilitiesType.setServiceProvider(capabilities.getServiceProvider());
        }

        /** Sets the Contents **/
        if(requestedSections.contains(SectionName.All) || requestedSections.contains(SectionName.Contents)) {
            Contents contents = new Contents();
            List<ProcessSummaryType> processSummaryTypeList = new ArrayList<>();
            List<ProcessDescriptionType> processList = getProcessList();
            for (ProcessDescriptionType process : processList) {
                ProcessDescriptionType translatedProcess = ProcessTranslator.getTranslatedProcess(
                        process, requestLanguage, DEFAULT_LANGUAGE);
                ProcessSummaryType processSummaryType = new ProcessSummaryType();
                processSummaryType.getJobControlOptions().clear();
                processSummaryType.getJobControlOptions().addAll(jobControlOptions);
                processSummaryType.setIdentifier(translatedProcess.getIdentifier());
                processSummaryType.getMetadata().clear();
                processSummaryType.getMetadata().addAll(translatedProcess.getMetadata());
                processSummaryType.getAbstract().clear();
                processSummaryType.getAbstract().addAll(translatedProcess.getAbstract());
                processSummaryType.getTitle().clear();
                processSummaryType.getTitle().addAll(translatedProcess.getTitle());
                processSummaryType.getKeywords().clear();
                processSummaryType.getKeywords().addAll(translatedProcess.getKeywords());

                processSummaryTypeList.add(processSummaryType);
            }
            contents.getProcessSummary().clear();
            contents.getProcessSummary().addAll(processSummaryTypeList);
            capabilitiesType.setContents(contents);
        }

        return capabilitiesType;
    }

    @Override
    public ProcessOfferings describeProcess(DescribeProcess describeProcess) {
        //Get the list of the ids of the process to describe
        List<CodeType> idList = describeProcess.getIdentifier();

        ProcessOfferings processOfferings = new ProcessOfferings();
        List<ProcessOffering> processOfferingList = new ArrayList<>();
        //For each of the processes
        for(CodeType id : idList) {
            ProcessOffering processOffering = null;
            List<ProcessIdentifier> piList = processManager.getAllProcessIdentifier();
            //Find the process registered in the server with the same id
            for(ProcessIdentifier pi : piList){
                if(pi.getProcessDescriptionType().getIdentifier().getValue().equals(id.getValue())){
                    processOffering = pi.getProcessOffering();
                }
            }
            //Once the process found, build the corresponding processOffering to send to the client
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
                po.setProcess(ProcessTranslator.getTranslatedProcess(process, describeProcess.getLang(), DEFAULT_LANGUAGE));
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
                dataMap,
                propertiesMap);

        //Run the worker
        if(executorService != null){
            executorService.execute(worker);
        }
        else {
            worker.run();
        }
        //Return the StatusInfo to the user
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
                Object answer = getCapabilities((GetCapabilitiesType)o);
                if(answer instanceof WPSCapabilitiesType) {
                    result = factory.createCapabilities((WPSCapabilitiesType)answer);
                }
                else{
                    result = answer;
                }
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
            LOGGER.error(I18N.tr("Unable to parse the incoming xml.\nCause : {0}.", e.getMessage()));
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
                LOGGER.error(I18N.tr("Unable to parse the outcoming xml.\nCause : {0}.", e.getMessage()));
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


    protected void setDatabase(Database database){
        this.database = database;
    }
    @Override
    public Database getDatabase() {
        return database;
    }

    protected void setDataSourceService(DataSourceService dataSourceService){
        this.dataSourceService = dataSourceService;
    }
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
            LOGGER.error(I18N.tr("Unable to generate the XMLGregorianCalendar object.\nCause : {0}.", e.getMessage()));
        }
        return date;
    }
}
