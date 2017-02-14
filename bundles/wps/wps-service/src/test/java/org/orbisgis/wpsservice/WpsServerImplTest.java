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
package org.orbisgis.wpsservice;

import junit.framework.Assert;
import net.opengis.ows._2.*;
import net.opengis.wps._2_0.*;
import net.opengis.wps._2_0.GetCapabilitiesType;
import net.opengis.wps._2_0.ObjectFactory;
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
import java.util.List;
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
            URL url = this.getClass().getResource("JDBCTable.groovy");
            Assert.assertNotNull("Unable to load the script 'JDBCTable.groovy'", url);
            File f = new File(url.toURI());
            localWpsServer.addLocalSource(f, null, true, "test");

            url = this.getClass().getResource("JDBCTableField.groovy");
            Assert.assertNotNull("Unable to load the script 'JDBCTableField.groovy'", url);
            f = new File(url.toURI());
            localWpsServer.addLocalSource(f, null, true, "test");

            url = this.getClass().getResource("JDBCTableFieldValue.groovy");
            Assert.assertNotNull("Unable to load the script 'JDBCTableFieldValue.groovy'", url);
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

        //Ask for the GetCapabilities
        GetCapabilitiesType getCapabilitiesType = new GetCapabilitiesType();
        GetCapabilitiesType.AcceptLanguages acceptLanguages = new GetCapabilitiesType.AcceptLanguages();
        acceptLanguages.getLanguage().add("*");
        getCapabilitiesType.setAcceptLanguages(acceptLanguages);
        AcceptVersionsType acceptVersionsType = new AcceptVersionsType();
        acceptVersionsType.getVersion().add("2.0.0");
        getCapabilitiesType.setAcceptVersions(acceptVersionsType);
        SectionsType sectionsType = new SectionsType();
        sectionsType.getSection().add("All");
        getCapabilitiesType.setSections(sectionsType);
        Object object = wpsServer.getCapabilities(getCapabilitiesType);
        String reason = "";
        if(object instanceof ExceptionReport){
            for(ExceptionType exception : ((ExceptionReport)object).getException()){
                reason += exception.getExceptionCode();
                if(exception.getLocator() != null && !exception.getLocator().isEmpty()){
                    reason += " : " + exception.getLocator();
                }
                reason += "\n";
            }
        }
        if(reason.isEmpty()){
            reason = "Unknown reason";
        }
        Assert.assertTrue("The wps server capabilities is invalid : " + reason ,
                object instanceof WPSCapabilitiesType);
        WPSCapabilitiesType capabilities = (WPSCapabilitiesType)object;

        //Contents tests
        Assert.assertNotNull("The wps server contents should not be null.",
                capabilities.getContents());
        Assert.assertNotNull("The wps server process summary should not be null.",
                capabilities.getContents().getProcessSummary());
        Assert.assertTrue("The wps server process summary should be empty.",
                capabilities.getContents().getProcessSummary().isEmpty());

        //extensions tests
        Assert.assertNotNull("The wps server service identification extension should not be null.",
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

        Assert.assertNotNull("The wps server service identification fees should not be null",
                capabilities.getServiceIdentification().getFees());
        Assert.assertEquals("The wps server service identification fees should be 'NONE'",
                capabilities.getServiceIdentification().getFees(), "NONE");

        Assert.assertNotNull("The wps server service identification access constraint should not be null",
                capabilities.getServiceIdentification().getAccessConstraints());
        Assert.assertTrue("The wps server service identification access constraint should contain 'NONE'",
                capabilities.getServiceIdentification().getAccessConstraints().contains("NONE"));

        Assert.assertNotNull("The wps server service identification title should not be null",
                capabilities.getServiceIdentification().getTitle());
        Assert.assertFalse("The wps server service identification title should not be empty",
                capabilities.getServiceIdentification().getTitle().isEmpty());
        Assert.assertEquals("The wps server service identification title value should be 'OrbisGIS Local WPS Service'",
                capabilities.getServiceIdentification().getTitle().get(0).getValue(), "OrbisGIS Local WPS Service");
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
        Assert.assertNull("The wps server service contact should be null", serviceContact);

        //operation metadata tests
        Assert.assertNotNull("The wps server operation metadata should not be null",
                capabilities.getOperationsMetadata());
        Assert.assertNotNull("The wps server operation metadata operation should not be null",
                capabilities.getOperationsMetadata().getOperation());
        Assert.assertEquals("The wps server operation metadata operation should contains six elements",
                capabilities.getOperationsMetadata().getOperation().size(), 6);
        String[] operations={"GetCapabilities", "DescribeProcess", "Execute", "GetStatus", "GetResult", "Dismiss"};
        List<Operation> serverOperations = capabilities.getOperationsMetadata().getOperation();
        for(String operation : operations){
            boolean isOperationFound = false;
            for(Operation serverOperation : serverOperations){
                if(serverOperation.getName().equals(operation)){
                    isOperationFound = true;
                    String errorMessage = testOperation(serverOperation, operation);
                    Assert.assertNull(errorMessage, errorMessage);
                }
            }
            Assert.assertTrue("The operation "+operation+" is not found.", isOperationFound);
        }
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
        Assert.assertNotNull("The wps server languages should not be null",
                capabilities.getLanguages());
        Assert.assertNotNull("The wps server languages language should not be null",
                capabilities.getLanguages().getLanguage());
        Assert.assertFalse("The wps server languages language should not be empty",
                capabilities.getLanguages().getLanguage().isEmpty());
        Assert.assertEquals("The wps server languages language 0 should be 'en'",
                capabilities.getLanguages().getLanguage().get(0), "en");

        //version tests
        Assert.assertEquals("The wps server version should be '1.0.0'",
                capabilities.getVersion(), "1.0.0");

        //update sequence tests
        Assert.assertNotNull("The wps server update sequence should not be null",
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
    public void testJDBCTableScript() throws JAXBException, IOException {
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
                " the process identifier should be 'orbisgis:test:jdbctable'",
                processOffering.getProcess().getIdentifier().getValue(),
                "orbisgis:test:jdbctable");
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

        try {sleep(100);} catch (InterruptedException e) {}

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

        try {sleep(100);} catch (InterruptedException ignored) {}

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
        Assert.assertEquals("Error on unmarshalling the WpsService answer, the status info status should not be " +
                        "'RUNNING'",
                statusInfo.getStatus(), "RUNNING");
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the status info next poll should not be null",
                statusInfo.getNextPoll());
        Assert.assertNull("Error on unmarshalling the WpsService answer, the status info expiration date should be " +
                        "null",
                statusInfo.getExpirationDate());
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the status info estimated completion " +
                        "should not be null",
                statusInfo.getEstimatedCompletion());
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the status info percent complete" +
                        "should not be null",
                statusInfo.getPercentCompleted());

        //Wait to be sure that the process has ended. If it is not possible, raise a flag
        boolean hasWaited = true;
        try {sleep(1000);} catch (InterruptedException e) {hasWaited=false;}

        //Now test the getStatus request
        jobId = UUID.fromString(((StatusInfo)resultObject).getJobID());
        getStatus = new GetStatus();
        getStatus.setJobID(jobId.toString());
        //Marshall the GetStatus object into an OutputStream
        outStatus = new ByteArrayOutputStream();
        marshaller.marshal(getStatus, outStatus);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        in = new DataInputStream(new ByteArrayInputStream(outStatus.toByteArray()));
        xml = (ByteArrayOutputStream) wpsServer.callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        resultStatusXml = new ByteArrayInputStream(xml.toByteArray());
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
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the status info estimated completion " +
                        "should not be null",
                statusInfo.getEstimatedCompletion());
        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the status info percent complete" +
                        "should not be null",
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
        try {sleep(200);} catch (InterruptedException e) {}


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

    /**
     * Tests the GetCapabilities operation with a bad formed GetCapabilities.
     * @throws JAXBException
     * @throws IOException
     */
    @Test
    public void testBadGetCapabilities() throws JAXBException, IOException {
        Unmarshaller unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
        Marshaller marshaller = JaxbContainer.JAXBCONTEXT.createMarshaller();
        ObjectFactory factory = new ObjectFactory();

        //Null Capabilities test
        Object resultObject = wpsServer.getCapabilities(null);

        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the object should not be null",
                resultObject);
        Assert.assertTrue("Error on unmarshalling the WpsService answer, the object should be a ExceptionReport",
                resultObject instanceof ExceptionReport);
        Assert.assertEquals("Error on unmarshalling the WpsService answer, the exception should be 'NoApplicableCode'",
                ((ExceptionReport)resultObject).getException().get(0).getExceptionCode(), "NoApplicableCode");

        //Bad Section test
        //Build the GetCapabilities object
        GetCapabilitiesType element = new GetCapabilitiesType();
        SectionsType sectionsType = new SectionsType();
        sectionsType.getSection().add("All");
        sectionsType.getSection().add("all");
        sectionsType.getSection().add("AlL the ThinGS");
        element.setSections(sectionsType);
        //Marshall the DescribeProcess object into an OutputStream
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        marshaller.marshal(factory.createGetCapabilities(element), out);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        InputStream in = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        ByteArrayOutputStream xml = (ByteArrayOutputStream) wpsServer.callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        ByteArrayInputStream resultXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.
        resultObject = unmarshaller.unmarshal(resultXml);

        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the object should not be null",
                resultObject);
        Assert.assertTrue("Error on unmarshalling the WpsService answer, the object should be a ExceptionReport",
                resultObject instanceof ExceptionReport);
        Assert.assertEquals("Error on unmarshalling the WpsService answer, the exception should be 'InvalidParameterValue'",
                ((ExceptionReport)resultObject).getException().get(0).getExceptionCode(), "InvalidParameterValue");


        //Bad Language tests
        //Build the GetCapabilities object
        element = new GetCapabilitiesType();
        GetCapabilitiesType.AcceptLanguages acceptLanguages = new GetCapabilitiesType.AcceptLanguages();
        acceptLanguages.getLanguage().add("zz");
        element.setAcceptLanguages(acceptLanguages);
        //Marshall the DescribeProcess object into an OutputStream
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        out = new ByteArrayOutputStream();
        marshaller.marshal(factory.createGetCapabilities(element), out);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        in = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        xml = (ByteArrayOutputStream) wpsServer.callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        resultXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.
        resultObject = unmarshaller.unmarshal(resultXml);

        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the object should not be null",
                resultObject);
        Assert.assertTrue("Error on unmarshalling the WpsService answer, the object should be a ExceptionReport",
                resultObject instanceof ExceptionReport);
        Assert.assertEquals("Error on unmarshalling the WpsService answer, the exception should be 'InvalidParameterValue'",
                ((ExceptionReport)resultObject).getException().get(0).getExceptionCode(), "InvalidParameterValue");
        Assert.assertEquals("Error on unmarshalling the WpsService answer, the exception should be about the parameter" +
                        " 'AcceptLanguages'",
                ((ExceptionReport)resultObject).getException().get(0).getLocator(), "AcceptLanguages");


        //Bad version tests
        //Build the GetCapabilities object
        element = new GetCapabilitiesType();
        AcceptVersionsType acceptVersionsType = new AcceptVersionsType();
        acceptVersionsType.getVersion().add("99.99.99");
        element.setAcceptVersions(acceptVersionsType);
        //Marshall the DescribeProcess object into an OutputStream
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        out = new ByteArrayOutputStream();
        marshaller.marshal(factory.createGetCapabilities(element), out);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        in = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        xml = (ByteArrayOutputStream) wpsServer.callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        resultXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.
        resultObject = unmarshaller.unmarshal(resultXml);

        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the object should not be null",
                resultObject);
        Assert.assertTrue("Error on unmarshalling the WpsService answer, the object should be a ExceptionReport",
                resultObject instanceof ExceptionReport);
        Assert.assertEquals("Error on unmarshalling the WpsService answer, the exception should be 'VersionNegotiationFailed'",
                ((ExceptionReport)resultObject).getException().get(0).getExceptionCode(), "VersionNegotiationFailed");
    }

    /**
     * Tests the GetCapabilities operation with a bad formed GetCapabilities.
     * @throws JAXBException
     * @throws IOException
     */
    @Test
    public void testGetCapabilitiesLanguages() throws JAXBException, IOException {
        Unmarshaller unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
        Marshaller marshaller = JaxbContainer.JAXBCONTEXT.createMarshaller();
        ObjectFactory factory = new ObjectFactory();

        //'en' language request test
        //Build the GetCapabilities object
        GetCapabilitiesType element = new GetCapabilitiesType();
        GetCapabilitiesType.AcceptLanguages acceptLanguages = new GetCapabilitiesType.AcceptLanguages();
        acceptLanguages.getLanguage().add("en");
        element.setAcceptLanguages(acceptLanguages);
        //Marshall the DescribeProcess object into an OutputStream
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        marshaller.marshal(factory.createGetCapabilities(element), out);
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
        Assert.assertTrue("Error on unmarshalling the WpsService answer, it should contains a WPSCapabilitiesType",
                ((JAXBElement)resultObject).getValue() instanceof WPSCapabilitiesType);
        Assert.assertTrue("Error on unmarshalling the WpsService answer, the Languages should contains 'en'",
                ((WPSCapabilitiesType)((JAXBElement)resultObject).getValue()).getLanguages()
                        .getLanguage().contains("en"));

        //Any language request test
        //Build the GetCapabilities object
        element = new GetCapabilitiesType();
        acceptLanguages = new GetCapabilitiesType.AcceptLanguages();
        acceptLanguages.getLanguage().add("*");
        element.setAcceptLanguages(acceptLanguages);
        //Marshall the DescribeProcess object into an OutputStream
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        out = new ByteArrayOutputStream();
        marshaller.marshal(factory.createGetCapabilities(element), out);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        in = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        xml = (ByteArrayOutputStream) wpsServer.callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        resultXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.
        resultObject = unmarshaller.unmarshal(resultXml);

        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the object should not be null",
                resultObject);
        Assert.assertTrue("Error on unmarshalling the WpsService answer, the object should be a JAXBElement",
                resultObject instanceof JAXBElement);
        Assert.assertTrue("Error on unmarshalling the WpsService answer, it should contains a WPSCapabilitiesType",
                ((JAXBElement)resultObject).getValue() instanceof WPSCapabilitiesType);
        Assert.assertTrue("Error on unmarshalling the WpsService answer, the Languages should contains 'en'",
                ((WPSCapabilitiesType)((JAXBElement)resultObject).getValue()).getLanguages()
                        .getLanguage().contains("en"));


        //'en-CA' language request tests
        //Build the GetCapabilities object
        element = new GetCapabilitiesType();
        acceptLanguages = new GetCapabilitiesType.AcceptLanguages();
        acceptLanguages.getLanguage().add("fr");
        acceptLanguages.getLanguage().add("en-CA");
        element.setAcceptLanguages(acceptLanguages);
        //Marshall the DescribeProcess object into an OutputStream
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        out = new ByteArrayOutputStream();
        marshaller.marshal(factory.createGetCapabilities(element), out);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        in = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        xml = (ByteArrayOutputStream) wpsServer.callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        resultXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.
        resultObject = unmarshaller.unmarshal(resultXml);

        Assert.assertNotNull("Error on unmarshalling the WpsService answer, the object should not be null",
                resultObject);
        Assert.assertTrue("Error on unmarshalling the WpsService answer, the object should be a JAXBElement",
                resultObject instanceof JAXBElement);
        Assert.assertTrue("Error on unmarshalling the WpsService answer, it should contains a WPSCapabilitiesType",
                ((JAXBElement)resultObject).getValue() instanceof WPSCapabilitiesType);
        Assert.assertTrue("Error on unmarshalling the WpsService answer, the Languages should contains 'en'",
                ((WPSCapabilitiesType)((JAXBElement)resultObject).getValue()).getLanguages()
                        .getLanguage().contains("en"));
    }
}
