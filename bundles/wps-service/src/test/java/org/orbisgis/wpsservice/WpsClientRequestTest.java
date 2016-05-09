package org.orbisgis.wpsservice;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;
import javax.xml.bind.*;

import net.opengis.wps.v_2_0.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * This test class perform tests about groovy wps scripts.
 * It loads several script in the wpsService and then test the DescribeProcess request.
 *
 * @author Sylvain PALOMINOS
 */
public class WpsClientRequestTest {
    WpsService wpsService;

    /**
     * Test the DataStore script DescribeProcess request.
     */
    @Test
    public void testDataStoreScript() throws JAXBException, IOException {
        //Start the wpsService
        initWpsService();
        Unmarshaller unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
        //Build the DescribeProcess object
        File describeProcessFile = new File(this.getClass().getResource("DataStoreDescribeProcess.xml").getFile());
        Object describeProcess = unmarshaller.unmarshal(describeProcessFile);
        //Marshall the DescribeProcess object into an OutputStream
        Marshaller marshaller = JaxbContainer.JAXBCONTEXT.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        marshaller.marshal(describeProcess, out);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        InputStream in = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        ByteArrayOutputStream xml = (ByteArrayOutputStream)wpsService.callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        InputStream resultXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.
        Object resultObject = unmarshaller.unmarshal(resultXml);
        File f = new File(this.getClass().getResource("DataStoreProcessOfferings.xml").getFile());
        Object resourceObject = unmarshaller.unmarshal(f);

        String message = "Error on unmarshalling the WpsService answer, the object is not the one expected.\n\n";
        Assert.assertTrue(message, resourceObject.equals(resultObject));
    }

    /**
     * Test the DataField script DescribeProcess request.
     */
    @Test
    public void testDataFieldScript() throws JAXBException, IOException {
        //Start the wpsService
        initWpsService();
        Unmarshaller unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
        //Build the DescribeProcess object
        File describeProcessFile = new File(this.getClass().getResource("DataFieldDescribeProcess.xml").getFile());
        Object describeProcess = unmarshaller.unmarshal(describeProcessFile);
        //Marshall the DescribeProcess object into an OutputStream
        Marshaller marshaller = JaxbContainer.JAXBCONTEXT.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        marshaller.marshal(describeProcess, out);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        InputStream in = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        ByteArrayOutputStream xml = (ByteArrayOutputStream)wpsService.callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        InputStream resultXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.
        Object resultObject = unmarshaller.unmarshal(resultXml);
        File f = new File(this.getClass().getResource("DataFieldProcessOfferings.xml").getFile());
        Object resourceObject = unmarshaller.unmarshal(f);

        String message = "Error on unmarshalling the WpsService answer, the object is not the one expected.\n\n";
        Assert.assertTrue(message, resourceObject.equals(resultObject));
    }

    /**
     * Test the DataField script DescribeProcess request.
     */
    @Test
    public void testFieldValueScript() throws JAXBException, IOException {
        //Start the wpsService
        initWpsService();
        Unmarshaller unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
        //Build the DescribeProcess object
        File describeProcessFile = new File(this.getClass().getResource("FieldValueDescribeProcess.xml").getFile());
        Object describeProcess = unmarshaller.unmarshal(describeProcessFile);
        //Marshall the DescribeProcess object into an OutputStream
        Marshaller marshaller = JaxbContainer.JAXBCONTEXT.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        marshaller.marshal(describeProcess, out);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        InputStream in = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        ByteArrayOutputStream xml = (ByteArrayOutputStream)wpsService.callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        InputStream resultXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.
        Object resultObject = unmarshaller.unmarshal(resultXml);
        File f = new File(this.getClass().getResource("FieldValueProcessOfferings.xml").getFile());
        Object resourceObject = unmarshaller.unmarshal(f);

        String message = "Error on unmarshalling the WpsService answer, the object is not the one expected.\n\n";
        Assert.assertTrue(message, resourceObject.equals(resultObject));
    }

    /**
     * Test the Enumeration script DescribeProcess request.
     */
    @Test
    public void testEnumerationScript() throws JAXBException, IOException {
        //Start the wpsService
        initWpsService();
        Unmarshaller unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
        //Build the DescribeProcess object
        File describeProcessFile = new File(this.getClass().getResource("EnumerationDescribeProcess.xml").getFile());
        Object describeProcess = unmarshaller.unmarshal(describeProcessFile);
        //Marshall the DescribeProcess object into an OutputStream
        Marshaller marshaller = JaxbContainer.JAXBCONTEXT.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        marshaller.marshal(describeProcess, out);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        InputStream in = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        ByteArrayOutputStream xml = (ByteArrayOutputStream)wpsService.callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        InputStream resultXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.
        Object resultObject = unmarshaller.unmarshal(resultXml);
        File f = new File(this.getClass().getResource("EnumerationProcessOfferings.xml").getFile());
        Object resourceObject = unmarshaller.unmarshal(f);

        String message = "Error on unmarshalling the WpsService answer, the object is not the one expected.\n\n";
        Assert.assertTrue(message, resourceObject.equals(resultObject));
    }

    /**
     * Test the GeometryData script DescribeProcess request.
     */
    @Test
    public void testGeometryDataScript() throws JAXBException, IOException {
        //Start the wpsService
        initWpsService();
        Unmarshaller unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
        //Build the DescribeProcess object
        File describeProcessFile = new File(this.getClass().getResource("GeometryDataDescribeProcess.xml").getFile());
        Object describeProcess = unmarshaller.unmarshal(describeProcessFile);
        //Marshall the DescribeProcess object into an OutputStream
        Marshaller marshaller = JaxbContainer.JAXBCONTEXT.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        marshaller.marshal(describeProcess, out);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        InputStream in = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        ByteArrayOutputStream xml = (ByteArrayOutputStream)wpsService.callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        InputStream resultXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.
        Object resultObject = unmarshaller.unmarshal(resultXml);
        File f = new File(this.getClass().getResource("GeometryDataProcessOfferings.xml").getFile());
        Object resourceObject = unmarshaller.unmarshal(f);

        String message = "Error on unmarshalling the WpsService answer, the object is not the one expected.\n\n";
        Assert.assertTrue(message, resourceObject.equals(resultObject));
    }

    /**
     * Test the RawData script DescribeProcess request.
     */
    @Test
    public void testRawDataScript() throws JAXBException, IOException {
        //Start the wpsService
        initWpsService();
        Unmarshaller unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
        //Build the DescribeProcess object
        File describeProcessFile = new File(this.getClass().getResource("RawDataDescribeProcess.xml").getFile());
        Object describeProcess = unmarshaller.unmarshal(describeProcessFile);
        //Marshall the DescribeProcess object into an OutputStream
        Marshaller marshaller = JaxbContainer.JAXBCONTEXT.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        marshaller.marshal(describeProcess, out);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        InputStream in = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        ByteArrayOutputStream xml = (ByteArrayOutputStream)wpsService.callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        InputStream resultXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.
        Object resultObject = unmarshaller.unmarshal(resultXml);
        File f = new File(this.getClass().getResource("RawDataProcessOfferings.xml").getFile());
        Object resourceObject = unmarshaller.unmarshal(f);

        String message = "Error on unmarshalling the WpsService answer, the object is not the one expected.\n\n";
        Assert.assertTrue(message, resourceObject.equals(resultObject));
    }

    /**
     * Test the GetCapabilities request.
     */
    @Test
    public void testGetCapabilities() throws JAXBException, IOException {
        //Start the wpsService
        initWpsService();
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
        ByteArrayOutputStream xml = (ByteArrayOutputStream)wpsService.callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        InputStream resultXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.
        Object resultObject = unmarshaller.unmarshal(resultXml);
        File f = new File(this.getClass().getResource("Capabilities.xml").getFile());
        Object resourceObject = unmarshaller.unmarshal(f);

        WPSCapabilitiesType result = (WPSCapabilitiesType) ((JAXBElement) resultObject).getValue();
        WPSCapabilitiesType resource = (WPSCapabilitiesType) ((JAXBElement) resourceObject).getValue();
        String message = "Error on unmarshalling the WpsService answer, the object is not the one expected.\n\n";
        Assert.assertTrue(message, resource.equals(result));
    }

    /**
     * Test the Execute, GetStatus and GetResult requests.
     */
    @Test
    public void testExecuteStatusResultRequest() throws JAXBException, IOException {
        //Start the wpsService
        initWpsService();
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
        ByteArrayOutputStream xml = (ByteArrayOutputStream)wpsService.callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        InputStream resultExecXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.
        Object resultObject = unmarshaller.unmarshal(resultExecXml);

        String message = "Error on unmarshalling the WpsService answer, the object is not the one expected.\n\n";
        //Impossible to test the result object because of the JobID which is each time different.
        Assert.assertTrue(message, resultObject != null && resultObject instanceof StatusInfo);

        //Now test the getStatus request
        UUID jobId = UUID.fromString(((StatusInfo)resultObject).getJobID());
        GetStatus getStatus = new GetStatus();
        getStatus.setJobID(jobId.toString());
        //Marshall the GetStatus object into an OutputStream
        ByteArrayOutputStream outStatus = new ByteArrayOutputStream();
        marshaller.marshal(getStatus, outStatus);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        in = new DataInputStream(new ByteArrayInputStream(outStatus.toByteArray()));
        xml = (ByteArrayOutputStream)wpsService.callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        InputStream resultStatusXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.
        resultObject = unmarshaller.unmarshal(resultStatusXml);

        //Impossible to test the result object because of the JobID which is each time different.
        Assert.assertTrue(message, resultObject != null && resultObject instanceof StatusInfo);

        //Now test the getResult request
        jobId = UUID.fromString(((StatusInfo)resultObject).getJobID());
        GetResult getResult = new GetResult();
        getResult.setJobID(jobId.toString());
        //Marshall the GetResult object into an OutputStream
        ByteArrayOutputStream outResult = new ByteArrayOutputStream();
        marshaller.marshal(getResult, outResult);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        in = new DataInputStream(new ByteArrayInputStream(outResult.toByteArray()));
        xml = (ByteArrayOutputStream)wpsService.callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        InputStream resultResultXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.
        resultObject = unmarshaller.unmarshal(resultResultXml);

        //Impossible to test the result object because of the JobID which is each time different.
        Assert.assertTrue(message, resultObject != null && resultObject instanceof Result);
    }

    /**
     * Initialise a wpsService to test the scripts.
     * The initialised wpsService can't execute the processes.
     */
    private void initWpsService() {
        if (wpsService == null) {
            //Start the WpsService
            LocalWpsServiceImplementation localWpsService = new LocalWpsServiceImplementation();
            localWpsService.init();
            //Try to load the groovy scripts
            try {
                URL url = this.getClass().getResource("DataStore.groovy");
                if (url != null) {
                    File f = new File(url.toURI());
                    localWpsService.addLocalScript(f, null, false);
                }
                url = this.getClass().getResource("DataField.groovy");
                if (url != null) {
                    File f = new File(url.toURI());
                    localWpsService.addLocalScript(f, null, false);
                }
                url = this.getClass().getResource("FieldValue.groovy");
                if (url != null) {
                    File f = new File(url.toURI());
                    localWpsService.addLocalScript(f, null, false);
                }
                url = this.getClass().getResource("Enumeration.groovy");
                if (url != null) {
                    File f = new File(url.toURI());
                    localWpsService.addLocalScript(f, null, false);
                }
                url = this.getClass().getResource("GeometryData.groovy");
                if (url != null) {
                    File f = new File(url.toURI());
                    localWpsService.addLocalScript(f, null, false);
                }
                url = this.getClass().getResource("RawData.groovy");
                if (url != null) {
                    File f = new File(url.toURI());
                    localWpsService.addLocalScript(f, null, false);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            wpsService = localWpsService;
        }
    }
}
