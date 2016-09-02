package org.orbisgis.wpsservice;

import junit.framework.Assert;
import net.opengis.ows._2.Operation;
import net.opengis.ows._2.ResponsiblePartySubsetType;
import net.opengis.wps._2_0.*;
import org.junit.Before;
import org.junit.Test;
import org.orbisgis.wpsservice.model.JaxbContainer;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

/**
 * Test class for the WpsServerImpl.
 *
 * @author Sylvain PALOMINOS
 */
public class WpsServerImplTest {

    private WpsServerImpl wpsServer;

    /**
     * Initialize a wps server for processing all the tests.
     */
    @Before
    public void initialize(){
        LocalWpsServerImpl localWpsServer = new LocalWpsServerImpl();
        localWpsServer.init();

        try {
            URL url = this.getClass().getResource("DataStore.groovy");
            Assert.assertNotNull("Unable to load the script 'DataStore.groovy'", url);
            File f = new File(url.toURI());
            localWpsServer.addLocalSource(f, null, true, "test");

            url = this.getClass().getResource("DataField.groovy");
            Assert.assertNotNull("Unable to load the script 'DataField.groovy'", url);
            f = new File(url.toURI());
            localWpsServer.addLocalSource(f, null, true, "test");

            url = this.getClass().getResource("FieldValue.groovy");
            Assert.assertNotNull("Unable to load the script 'FieldValue.groovy'", url);
            f = new File(url.toURI());
            localWpsServer.addLocalSource(f, null, true, "test");

            url = this.getClass().getResource("Enumeration.groovy");
            Assert.assertNotNull("Unable to load the script 'Enumeration.groovy'", url);
            f = new File(url.toURI());
            localWpsServer.addLocalSource(f, null, true, "test");

            url = this.getClass().getResource("EnumerationLongProcess.groovy");
            Assert.assertNotNull("Unable to load the script 'EnumerationLongProcess.groovy'", url);
            f = new File(url.toURI());
            localWpsServer.addLocalSource(f, null, true, "test");

            url = this.getClass().getResource("GeometryData.groovy");
            Assert.assertNotNull("Unable to load the script 'GeometryData.groovy'", url);
            f = new File(url.toURI());
            localWpsServer.addLocalSource(f, null, true, "test");

            url = this.getClass().getResource("RawData.groovy");
            Assert.assertNotNull("Unable to load the script 'RawData.groovy'", url);
            f = new File(url.toURI());
            localWpsServer.addLocalSource(f, null, true, "test");

        }
        catch (URISyntaxException e) {
            Assert.fail("Error on loading the scripts : "+e.getMessage());
        }

        localWpsServer.setExecutorService(Executors.newFixedThreadPool(1));

        wpsServer = localWpsServer;
    }

    /**
     * Check if once initialized, the wps server has loaded it basic capabilities.
     */
    @Test
    public void initialisationTest(){
        WpsServerImpl wpsServer = new WpsServerImpl();
        wpsServer.init();

        //Contents tests
        WPSCapabilitiesType capabilities = wpsServer.getCapabilities(new GetCapabilitiesType());
        Assert.assertNotNull("The wps server contents should not be null.",
                capabilities.getContents());
        Assert.assertNotNull("The wps server process summary should not be null.",
                capabilities.getContents().getProcessSummary());
        Assert.assertTrue("The wps server process summary should be empty.",
                capabilities.getContents().getProcessSummary().isEmpty());

        //extensions tests
        Assert.assertNull("The wps server service identification extension should be null.",
                capabilities.getExtension());

        //service identification tests
        Assert.assertNotNull("The wps server service identification service identification should not be null.",
                capabilities.getServiceIdentification());
        Assert.assertNotNull("The wps server service identification service type should not be null.",
                capabilities.getServiceIdentification().getServiceType());
        Assert.assertEquals("The wps server service identification service type should 'WPS'.",
                capabilities.getServiceIdentification().getServiceType().getValue(), "WPS");

        Assert.assertNotNull("The wps server service identification service type version should not be null",
                capabilities.getServiceIdentification().getServiceTypeVersion());
        Assert.assertFalse("The wps server service identification service type version should not be empty",
                capabilities.getServiceIdentification().getServiceTypeVersion().isEmpty());
        Assert.assertEquals("The wps server service identification service type version should be '2.0.0'",
                capabilities.getServiceIdentification().getServiceTypeVersion().get(0), "2.0.0");

        Assert.assertNotNull("The wps server service identification profile should not be null",
                capabilities.getServiceIdentification().getProfile());
        Assert.assertTrue("The wps server service identification profile should be empty",
                capabilities.getServiceIdentification().getProfile().isEmpty());

        Assert.assertNull("The wps server service identification fees should be null",
                capabilities.getServiceIdentification().getFees());

        Assert.assertNotNull("The wps server service identification access constraint should not be null",
                capabilities.getServiceIdentification().getAccessConstraints());
        Assert.assertTrue("The wps server service identification access constraint should be empty",
                capabilities.getServiceIdentification().getAccessConstraints().isEmpty());

        Assert.assertNotNull("The wps server service identification title should not be null",
                capabilities.getServiceIdentification().getTitle());
        Assert.assertFalse("The wps server service identification title should not be empty",
                capabilities.getServiceIdentification().getTitle().isEmpty());
        Assert.assertEquals("The wps server service identification title value should be 'OrbisGIS Local WPS'",
                capabilities.getServiceIdentification().getTitle().get(0).getValue(), "OrbisGIS Local WPS");
        Assert.assertEquals("The wps server service identification title language should be 'en'",
                capabilities.getServiceIdentification().getTitle().get(0).getLang(), "en");

        Assert.assertNotNull("The wps server service identification abstract should not be null",
                capabilities.getServiceIdentification().getAbstract());
        Assert.assertFalse("The wps server service identification abstract should not be empty",
                capabilities.getServiceIdentification().getAbstract().isEmpty());
        Assert.assertEquals("The wps server service identification abstract value should be " +
                "'OrbisGIS local instance of the WPS Service'",
                capabilities.getServiceIdentification().getAbstract().get(0).getValue(),
                "OrbisGIS local instance of the WPS Service");
        Assert.assertEquals("The wps server service identification abstract language should be 'en'",
                capabilities.getServiceIdentification().getAbstract().get(0).getLang(), "en");

        Assert.assertNotNull("The wps server service identification keywords should not be null",
                capabilities.getServiceIdentification().getKeywords());
        Assert.assertFalse("The wps server service identification keywords should not be empty",
                capabilities.getServiceIdentification().getKeywords().isEmpty());
        Assert.assertNotNull("The wps server service identification keywords 0 should not be null",
                capabilities.getServiceIdentification().getKeywords().get(0));
        Assert.assertNotNull("The wps server service identification keywords 0 keyword should not be null",
                capabilities.getServiceIdentification().getKeywords().get(0).getKeyword());
        Assert.assertFalse("The wps server service identification keywords 0 keyword should not be empty",
                capabilities.getServiceIdentification().getKeywords().get(0).getKeyword().isEmpty());
        Assert.assertEquals("The wps server service identification keywords 0 keyword value should be 'Toolbox'",
                capabilities.getServiceIdentification().getKeywords().get(0).getKeyword().get(0).getValue(), "Toolbox");
        Assert.assertEquals("The wps server service identification keywords 0 keyword language should be 'en'",
                capabilities.getServiceIdentification().getKeywords().get(0).getKeyword().get(0).getLang(), "en");
        Assert.assertNotNull("The wps server service identification keywords 1 should not be null",
                capabilities.getServiceIdentification().getKeywords().get(1));
        Assert.assertNotNull("The wps server service identification keywords 1 keyword should not be null",
                capabilities.getServiceIdentification().getKeywords().get(1).getKeyword());
        Assert.assertFalse("The wps server service identification keywords 1 keyword should not be empty",
                capabilities.getServiceIdentification().getKeywords().get(1).getKeyword().isEmpty());
        Assert.assertEquals("The wps server service identification keywords 1 keyword value should be 'WPS'",
                capabilities.getServiceIdentification().getKeywords().get(1).getKeyword().get(0).getValue(), "WPS");
        Assert.assertEquals("The wps server service identification keywords 1 keyword language should be 'en'",
                capabilities.getServiceIdentification().getKeywords().get(1).getKeyword().get(0).getLang(), "en");
        Assert.assertNotNull("The wps server service identification keywords 2 should not be null",
                capabilities.getServiceIdentification().getKeywords().get(2));
        Assert.assertNotNull("The wps server service identification keywords 2 keyword should not be null",
                capabilities.getServiceIdentification().getKeywords().get(2).getKeyword());
        Assert.assertFalse("The wps server service identification keywords 2 keyword should not be empty",
                capabilities.getServiceIdentification().getKeywords().get(2).getKeyword().isEmpty());
        Assert.assertEquals("The wps server service identification keywords 2 keyword value should be 'OrbisGIS'",
                capabilities.getServiceIdentification().getKeywords().get(2).getKeyword().get(0).getValue(),
                "OrbisGIS");
        Assert.assertEquals("The wps server service identification keywords 2 keyword language should be 'en'",
                capabilities.getServiceIdentification().getKeywords().get(2).getKeyword().get(0).getLang(), "en");

        //service provider tests
        Assert.assertNotNull("The wps server service provider should not be null",
                capabilities.getServiceProvider());
        Assert.assertEquals("The wps server service provider name should be 'OrbisGIS'",
                capabilities.getServiceProvider().getProviderName(), "OrbisGIS");

        Assert.assertNotNull("The wps server service provider site should not be null",
                capabilities.getServiceProvider().getProviderSite());
        Assert.assertEquals("The wps server service provider site href should be 'http://orbisgis.org/'",
                capabilities.getServiceProvider().getProviderSite().getHref(), "http://orbisgis.org/");
        Assert.assertNull("The wps server service provider site role should be null",
                capabilities.getServiceProvider().getProviderSite().getRole());
        Assert.assertNull("The wps server service provider site arcrole should be null",
                capabilities.getServiceProvider().getProviderSite().getArcrole());
        Assert.assertNull("The wps server service provider site title should be null",
                capabilities.getServiceProvider().getProviderSite().getTitle());
        Assert.assertNull("The wps server service provider site show should be null",
                capabilities.getServiceProvider().getProviderSite().getShow());
        Assert.assertNull("The wps server service provider site actuate should be null",
                capabilities.getServiceProvider().getProviderSite().getActuate());

        ResponsiblePartySubsetType serviceContact = capabilities.getServiceProvider().getServiceContact();
        Assert.assertNotNull("The wps server service contact should not be null",
                serviceContact);
        Assert.assertNull("The wps server service contact individual name should be null",
                serviceContact.getIndividualName());
        Assert.assertNull("The wps server service contact individual name should be null",
                serviceContact.getPositionName());
        Assert.assertNotNull("The wps server service contact info name should not be null",
                serviceContact.getContactInfo());
        Assert.assertNull("The wps server service contact info phone should be null",
                serviceContact.getContactInfo().getPhone());
        Assert.assertNotNull("The wps server service contact info address should not be null",
                serviceContact.getContactInfo().getAddress());
        Assert.assertNotNull("The wps server service contact info address delivery point should not be null",
                serviceContact.getContactInfo().getAddress().getDeliveryPoint());
        Assert.assertTrue("The wps server service contact info address delivery point should be empty",
                serviceContact.getContactInfo().getAddress().getDeliveryPoint().isEmpty());
        Assert.assertNull("The wps server service contact info address city should be null",
                serviceContact.getContactInfo().getAddress().getCity());
        Assert.assertNull("The wps server service contact info address administrative area should be null",
                serviceContact.getContactInfo().getAddress().getAdministrativeArea());
        Assert.assertNull("The wps server service contact info address postal code should be null",
                serviceContact.getContactInfo().getAddress().getPostalCode());
        Assert.assertNull("The wps server service contact info address country should be null",
                serviceContact.getContactInfo().getAddress().getCountry());
        Assert.assertNotNull("The wps server service contact info address email should not be null",
                serviceContact.getContactInfo().getAddress().getElectronicMailAddress());
        Assert.assertFalse("The wps server service contact info address email should not be empty",
                serviceContact.getContactInfo().getAddress().getElectronicMailAddress().isEmpty());
        Assert.assertEquals("The wps server service contact info address email should be 'info_at_orbisgis.org",
                serviceContact.getContactInfo().getAddress().getElectronicMailAddress().get(0), "info_at_orbisgis.org");
        Assert.assertNull("The wps server service contact info online resource should be null",
                serviceContact.getContactInfo().getOnlineResource());
        Assert.assertNull("The wps server service contact info hours of service should be null",
                serviceContact.getContactInfo().getHoursOfService());
        Assert.assertNull("The wps server service contact info contact instructions should be null",
                serviceContact.getContactInfo().getContactInstructions());
        Assert.assertNull("The wps server service contact role should be null",
                serviceContact.getRole());

        //operation metadata tests
        Assert.assertNotNull("The wps server operation metadata should not be null",
                capabilities.getOperationsMetadata());
        Assert.assertNotNull("The wps server operation metadata operation should not be null",
                capabilities.getOperationsMetadata().getOperation());
        Assert.assertEquals("The wps server operation metadata operation should contains five elements",
                capabilities.getOperationsMetadata().getOperation().size(), 5);
        Assert.assertNotNull("The wps server operation metadata operation one should not be null",
                capabilities.getOperationsMetadata().getOperation().get(0));
        String errorMessage = testOperation(
                capabilities.getOperationsMetadata().getOperation().get(0), "GetCapabilities");
        Assert.assertNull(errorMessage, errorMessage);
        Assert.assertNotNull("The wps server operation metadata operation two should not be null",
                capabilities.getOperationsMetadata().getOperation().get(1));
        errorMessage = testOperation(
                capabilities.getOperationsMetadata().getOperation().get(1), "DescribeProcess");
        Assert.assertNull(errorMessage, errorMessage);
        Assert.assertNotNull("The wps server operation metadata operation three should not be null",
                capabilities.getOperationsMetadata().getOperation().get(2));
        errorMessage = testOperation(
                capabilities.getOperationsMetadata().getOperation().get(2), "Execute");
        Assert.assertNull(errorMessage, errorMessage);
        Assert.assertNotNull("The wps server operation metadata operation four should not be null",
                capabilities.getOperationsMetadata().getOperation().get(3));
        errorMessage = testOperation(
                capabilities.getOperationsMetadata().getOperation().get(3), "GetStatus");
        Assert.assertNull(errorMessage, errorMessage);
        Assert.assertNotNull("The wps server operation metadata operation five should not be null",
                capabilities.getOperationsMetadata().getOperation().get(4));
        errorMessage = testOperation(
                capabilities.getOperationsMetadata().getOperation().get(4), "GetResult");
        Assert.assertNull(errorMessage, errorMessage);
        Assert.assertNotNull("The wps server operation metadata parameter should not be null",
                capabilities.getOperationsMetadata().getParameter());
        Assert.assertTrue("The wps server operation metadata parameter should be empty",
                capabilities.getOperationsMetadata().getParameter().isEmpty());
        Assert.assertNotNull("The wps server operation metadata constraint should not be null",
                capabilities.getOperationsMetadata().getConstraint());
        Assert.assertTrue("The wps server operation metadata constraint should be empty",
                capabilities.getOperationsMetadata().getConstraint().isEmpty());
        Assert.assertNull("The wps server operation metadata extended capabilities should be null",
                capabilities.getOperationsMetadata().getExtendedCapabilities());

        //language tests
        Assert.assertNull("The wps server languages should be null",
                capabilities.getLanguages());

        //version tests
        Assert.assertEquals("The wps server version should be '2.0.0'",
                capabilities.getVersion(), "2.0.0");

        //update sequence tests
        Assert.assertNull("The wps server update sequence should be null",
                capabilities.getUpdateSequence());
    }

    /**
     * Test the operation of the wps server operation metadata.
     * @param operation Operation to test.
     * @param name Name of the operation.
     * @return Null if there is no error, the error message otherwise.
     */
    private String testOperation(Operation operation, String name){
        String error = null;
        if(operation.getDCP() == null){
            error = "The wps operation metadata operation '"+name+"' dcp should not be null";
        }
        else if(operation.getDCP().isEmpty()) {
            error = "The wps operation metadata operation '"+name+"' dcp should not be empty";
        }
        else if(operation.getDCP().get(0) == null) {
            error = "The wps operation metadata operation '"+name+"' dcp 0 should not be null";
        }
        else if(operation.getDCP().get(0).getHTTP() == null) {
            error = "The wps operation metadata operation '"+name+"' dcp 0 HTTP should not be null";
        }
        else if(operation.getDCP().get(0).getHTTP().getGetOrPost() == null) {
            error = "The wps operation metadata operation '"+name+"' dcp 0 HTTP Get or Post should not be null";
        }
        else if(operation.getDCP().get(0).getHTTP().getGetOrPost().isEmpty()) {
            error = "The wps operation metadata operation '"+name+"' dcp 0 HTTP Get or Post should not be empty";
        }
        else if(operation.getDCP().get(0).getHTTP().getGetOrPost().get(0) == null) {
            error = "The wps operation metadata operation '"+name+"' dcp 0 HTTP Get or Post 0 should not be null";
        }
        else if(operation.getDCP().get(0).getHTTP().getGetOrPost().get(1) == null) {
            error = "The wps operation metadata operation '"+name+"' dcp 0 HTTP Get or Post 1 should not be null";
        }
        else if(operation.getParameter() == null) {
            error = "The wps operation metadata operation '"+name+"' parameter should not be null";
        }
        else if(!operation.getParameter().isEmpty()) {
            error = "The wps operation metadata operation '"+name+"' parameter should be empty";
        }
        else if(operation.getConstraint() == null) {
            error = "The wps operation metadata operation '"+name+"' constraint should not be null";
        }
        else if(!operation.getConstraint().isEmpty()) {
            error = "The wps operation metadata operation '"+name+"' constraint should be empty";
        }
        else if(operation.getMetadata() == null) {
            error = "The wps operation metadata operation '" + name + "' metadata should not be null";
        }
        else if(!operation.getMetadata().isEmpty()) {
            error = "The wps operation metadata operation '" + name + "' metadata should be empty";
        }
        else if(!operation.getName().equals(name)) {
            error = "The wps operation metadata operation '"+name+"' name should be "+name;
        }
        return error;
    }

    /**
     * Test< the GetCapabilities operation.
     * @throws JAXBException
     * @throws IOException
     */
    @Test
    public void testGetCapabilities() throws JAXBException, IOException {
        Unmarshaller unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
        //Build the GetCapabilities object
        File getCapabilitiesFile = new File(this.getClass().getResource("GetCapabilities.xml").getFile());
        Object element = unmarshaller.unmarshal(getCapabilitiesFile);
        //Marshall the DescribeProcess object into an OutputStream
        Marshaller marshaller = JaxbContainer.JAXBCONTEXT.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        marshaller.marshal(element, out);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        InputStream in = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        ByteArrayOutputStream xml = (ByteArrayOutputStream) wpsServer.callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        InputStream resultXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.
        Object resultObject = unmarshaller.unmarshal(resultXml);

        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the object should not be null",
                resultObject);
        Assert.assertTrue("Error on unmarshalling the WpsService answer, the object should be a JAXBElement",
                resultObject instanceof JAXBElement);
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the object value should not be null",
                ((JAXBElement)resultObject).getValue());
        Assert.assertTrue("Error on unmarshalling the WpsService answer, the value should be a WPSCapabilitiesType",
                ((JAXBElement)resultObject).getValue() instanceof WPSCapabilitiesType);
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the WPSCapabilitiesType should not be null",
                ((JAXBElement)resultObject).getValue());
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the contents should not be null",
                ((JAXBElement<WPSCapabilitiesType>)resultObject).getValue().getContents());
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the process summary should not be null",
                ((JAXBElement<WPSCapabilitiesType>)resultObject).getValue().getContents().getProcessSummary());
        Assert.assertEquals("Error on unmarshalling the WpsService answer, the process summary should have 6 elements",
                ((JAXBElement<WPSCapabilitiesType>)resultObject).getValue().getContents().getProcessSummary().size(),
                7);
    }

    /**
     * Tests the DescribeProcess operation.
     * @throws JAXBException
     * @throws IOException
     */
    @Test
    public void testDataStoreScript() throws JAXBException, IOException {
        Unmarshaller unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
        //Build the DescribeProcess object
        File describeProcessFile = new File(this.getClass().getResource("DescribeProcess.xml").getFile());
        Object describeProcess = unmarshaller.unmarshal(describeProcessFile);
        //Marshall the DescribeProcess object into an OutputStream
        Marshaller marshaller = JaxbContainer.JAXBCONTEXT.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        marshaller.marshal(describeProcess, out);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        InputStream in = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        ByteArrayOutputStream xml = (ByteArrayOutputStream) wpsServer.callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        InputStream resultXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.
        Object resultObject = unmarshaller.unmarshal(resultXml);

        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the object should not be null",
                resultObject);
        Assert.assertTrue("Error on unmarshalling the WpsService answer, the object should be a ProcessOfferings",
                resultObject instanceof ProcessOfferings);
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the ProcessOfferings should not be null",
                ((ProcessOfferings)resultObject).getProcessOffering());
        Assert.assertFalse("Error on unmarshalling the WpsService answer, the ProcessOfferings should not be empty",
                ((ProcessOfferings)resultObject).getProcessOffering().isEmpty());
        ProcessOffering processOffering = ((ProcessOfferings)resultObject).getProcessOffering().get(0);
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the ProcessOffering 0 should not be null",
                processOffering);
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the process should not be null",
                processOffering.getProcess());
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the process identifier should not be null",
                processOffering.getProcess().getIdentifier());
        Assert.assertEquals("Error on unmarshalling the WpsService answer," +
                " the process identifier should be 'orbisgis:test:datastore'",
                processOffering.getProcess().getIdentifier().getValue(),
                "orbisgis:test:datastore");
        Assert.assertNull("Error on unmarshalling the WpsService answer, the process offering any should be null",
                processOffering.getAny());
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the process offering job control options " +
                        " should not be null",
                processOffering.getJobControlOptions());
        Assert.assertFalse("Error on unmarshalling the WpsService answer, the process offering job control options " +
                        " should not be empty",
                processOffering.getJobControlOptions().isEmpty());
        Assert.assertEquals("Error on unmarshalling the WpsService answer, the process offering job control options 0 " +
                        " should be 'async-execute'",
                processOffering.getJobControlOptions().get(0), "async-execute");
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the process offering output transmission " +
                        " should not be null",
                processOffering.getOutputTransmission());
        Assert.assertFalse("Error on unmarshalling the WpsService answer, the process offering output transmission " +
                        " should not be empty",
                processOffering.getOutputTransmission().isEmpty());
        Assert.assertEquals("Error on unmarshalling the WpsService answer, the process offering output transmission 0 " +
                        " should be '" + DataTransmissionModeType.VALUE + "'",
                processOffering.getOutputTransmission().get(0), DataTransmissionModeType.VALUE);
        Assert.assertEquals("Error on unmarshalling the WpsService answer, the process version should be ''",
                processOffering.getProcessVersion(), "");
        Assert.assertEquals("Error on unmarshalling the WpsService answer, the process model should be 'native'",
                processOffering.getProcessModel(), "native");
    }

    /**
     * Test the Execute, GetStatus and GetResult requests.
     */
    @Test
    public void testExecuteStatusResultRequest() throws JAXBException, IOException {
        Unmarshaller unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
        //Build the Execute object
        File executeFile = new File(this.getClass().getResource("ExecuteRequest.xml").getFile());
        Object element = unmarshaller.unmarshal(executeFile);
        //Marshall the Execute object into an OutputStream
        Marshaller marshaller = JaxbContainer.JAXBCONTEXT.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream outExecute = new ByteArrayOutputStream();
        marshaller.marshal(element, outExecute);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        InputStream in = new DataInputStream(new ByteArrayInputStream(outExecute.toByteArray()));
        ByteArrayOutputStream xml = (ByteArrayOutputStream) wpsServer.callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        InputStream resultExecXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.
        Object resultObject = unmarshaller.unmarshal(resultExecXml);

        Assert.assertTrue("Error on unmarshalling the WpsService answer, the object should not be null",
                resultObject != null);
        Assert.assertTrue("Error on unmarshalling the WpsService answer, the object should be a Statusinfo",
                resultObject instanceof StatusInfo);
        StatusInfo statusInfo = (StatusInfo)resultObject;
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the status info job id should not be null",
                statusInfo.getJobID());
        Assert.assertEquals("Error on unmarshalling the WpsService answer, the status info status should not be " +
                        "'ACCEPTED'",
                statusInfo.getStatus(), "ACCEPTED");
        Assert.assertNull("Error on unmarshalling the WpsService answer, the status info expiration date should be " +
                        "null",
                statusInfo.getExpirationDate());
        Assert.assertNull("Error on unmarshalling the WpsService answer, the status info estimated completion " +
                        " should be null",
                statusInfo.getEstimatedCompletion());
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the status info next poll should not be" +
                " null",
                statusInfo.getNextPoll());
        Assert.assertNull("Error on unmarshalling the WpsService answer, the status info percent complete" +
                        " should be null",
                statusInfo.getPercentCompleted());

        //Wait to be sure that the process has ended. If it is not possible, raise a flag
        boolean hasWaited = true;
        try {sleep(1000);} catch (InterruptedException e) {hasWaited=false;}

        //Now test the getStatus request
        UUID jobId = UUID.fromString(((StatusInfo)resultObject).getJobID());
        GetStatus getStatus = new GetStatus();
        getStatus.setJobID(jobId.toString());
        //Marshall the GetStatus object into an OutputStream
        ByteArrayOutputStream outStatus = new ByteArrayOutputStream();
        marshaller.marshal(getStatus, outStatus);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        in = new DataInputStream(new ByteArrayInputStream(outStatus.toByteArray()));
        xml = (ByteArrayOutputStream) wpsServer.callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        InputStream resultStatusXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.
        resultObject = unmarshaller.unmarshal(resultStatusXml);

        Assert.assertTrue("Error on unmarshalling the WpsService answer, the object should not be null",
                resultObject != null);
        Assert.assertTrue("Error on unmarshalling the WpsService answer, the object should be a Statusinfo",
                resultObject instanceof StatusInfo);
        statusInfo = (StatusInfo)resultObject;
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the status info job id should not be null",
                statusInfo.getJobID());
        if(hasWaited) {
            Assert.assertEquals("Error on unmarshalling the WpsService answer, the status info status should not be " +
                            "'SUCCEEDED'",
                    statusInfo.getStatus(), "SUCCEEDED");
            Assert.assertNull("Error on unmarshalling the WpsService answer, the status info next poll should be null",
                    statusInfo.getNextPoll());
        }
        else{
            Assert.assertTrue("Error on unmarshalling the WpsService answer, the status info status should not be " +
                            "'SUCCEEDED' or 'RUNNING'",
                    statusInfo.getStatus().equals("SUCCEEDED") || statusInfo.getStatus().equals("RUNNING"));
        }
        Assert.assertNull("Error on unmarshalling the WpsService answer, the status info expiration date should be " +
                        "null",
                statusInfo.getExpirationDate());
        Assert.assertNull("Error on unmarshalling the WpsService answer, the status info estimated completion " +
                        " should be null",
                statusInfo.getEstimatedCompletion());
        Assert.assertNull("Error on unmarshalling the WpsService answer, the status info percent complete" +
                        " should be null",
                statusInfo.getPercentCompleted());

        //Now test the getResult request
        jobId = UUID.fromString(((StatusInfo)resultObject).getJobID());
        GetResult getResult = new GetResult();
        getResult.setJobID(jobId.toString());
        //Marshall the GetResult object into an OutputStream
        ByteArrayOutputStream outResult = new ByteArrayOutputStream();
        marshaller.marshal(getResult, outResult);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        in = new DataInputStream(new ByteArrayInputStream(outResult.toByteArray()));
        xml = (ByteArrayOutputStream) wpsServer.callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        InputStream resultResultXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.
        resultObject = unmarshaller.unmarshal(resultResultXml);

        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the object should not be null",
                resultObject);
        Assert.assertTrue("Error on unmarshalling the WpsService answer, the object should be a Result.",
                resultObject instanceof Result);
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the result job id should not be null",
                ((Result)resultObject).getJobID());
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the result expiration date should not be null",
                ((Result)resultObject).getExpirationDate());
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the result outputs should not be null",
                ((Result)resultObject).getOutput());
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the result outputs should not be null",
                ((Result)resultObject).getOutput());
        Assert.assertFalse("Error on unmarshalling the WpsService answer, the result outputs should not be empty",
                ((Result)resultObject).getOutput().isEmpty());
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the result output 0 should not be null",
                ((Result)resultObject).getOutput().get(0));
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the result output 0 id should not be null",
                ((Result)resultObject).getOutput().get(0).getId());
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the result output 0 data should not be" +
                " null",
                ((Result)resultObject).getOutput().get(0).getData());
        Assert.assertEquals("Error on unmarshalling the WpsService answer, the result output 0 id should be " +
                        "'orbisgis:test:enumeration:output'",
                ((Result)resultObject).getOutput().get(0).getId(), "orbisgis:test:enumeration:output");
    }

    /**
     * Test the Execute, GetStatus and GetResult requests.
     */
    @Test
    public void testDismissRequest() throws JAXBException, IOException {
        Unmarshaller unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
        //Build the Execute object
        File executeFile = new File(this.getClass().getResource("ExecuteRequest.xml").getFile());
        Object element = unmarshaller.unmarshal(executeFile);
        //Marshall the Execute object into an OutputStream
        Marshaller marshaller = JaxbContainer.JAXBCONTEXT.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream outExecute = new ByteArrayOutputStream();
        marshaller.marshal(element, outExecute);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        InputStream in = new DataInputStream(new ByteArrayInputStream(outExecute.toByteArray()));
        ByteArrayOutputStream xml = (ByteArrayOutputStream) wpsServer.callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        InputStream resultExecXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.
        Object resultObject = unmarshaller.unmarshal(resultExecXml);

        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the object should not be null",
                resultObject);
        Assert.assertTrue("Error on unmarshalling the WpsService answer, the object should be a StatusInfo.",
                resultObject instanceof StatusInfo);
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the StatusInfo job id should not be null",
                ((StatusInfo)resultObject).getJobID());


        //Wait to be sure that the process has started. If it is not possible, raise a flag
        boolean hasWaited = true;
        try {sleep(200);} catch (InterruptedException e) {hasWaited=false;}


        UUID jobId = UUID.fromString(((StatusInfo)resultObject).getJobID());
        Dismiss dismiss = new Dismiss();
        dismiss.setJobID(jobId.toString());
        //Marshall the GetResult object into an OutputStream
        ByteArrayOutputStream outResult = new ByteArrayOutputStream();
        marshaller.marshal(dismiss, outResult);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        in = new DataInputStream(new ByteArrayInputStream(outResult.toByteArray()));
        xml = (ByteArrayOutputStream) wpsServer.callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        InputStream resultResultXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.
        resultObject = unmarshaller.unmarshal(resultResultXml);

        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the object should not be null",
                resultObject);
        Assert.assertTrue("Error on unmarshalling the WpsService answer, the object should be a StatusInfo.",
                resultObject instanceof StatusInfo);
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the StatusInfo job id should not be null",
                ((StatusInfo)resultObject).getJobID());
        Assert.assertEquals("Error on unmarshalling the WpsService answer, the StatusInfo status should be 'RUNNING'",
                ((StatusInfo)resultObject).getStatus(), "RUNNING");


        //Wait to be sure that the process has started. If it is not possible, raise a flag
        try {sleep(200);} catch (InterruptedException e) {}


        //Now test the getResult request
        jobId = UUID.fromString(((StatusInfo)resultObject).getJobID());
        GetResult getResult = new GetResult();
        getResult.setJobID(jobId.toString());
        //Marshall the GetResult object into an OutputStream
        outResult = new ByteArrayOutputStream();
        marshaller.marshal(getResult, outResult);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        in = new DataInputStream(new ByteArrayInputStream(outResult.toByteArray()));
        xml = (ByteArrayOutputStream) wpsServer.callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        resultResultXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.
        resultObject = unmarshaller.unmarshal(resultResultXml);


        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the object should not be null",
                resultObject);
        Assert.assertTrue("Error on unmarshalling the WpsService answer, the object should be a Result.",
                resultObject instanceof Result);
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the result job id should not be null",
                ((Result)resultObject).getJobID() != null);
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the result expiration date should not be null",
                ((Result)resultObject).getExpirationDate() != null);
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the result outputs should not be null",
                ((Result)resultObject).getOutput());
        Assert.assertTrue("Error on unmarshalling the WpsService answer, the result outputs should be empty",
                ((Result)resultObject).getOutput().isEmpty());
    }
}
