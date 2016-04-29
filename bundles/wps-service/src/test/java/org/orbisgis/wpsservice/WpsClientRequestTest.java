package org.orbisgis.wpsservice;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.*;

import net.opengis.ows.v_2_0.*;
import net.opengis.wps.v_2_0.*;
import net.opengis.wps.v_2_0.GetCapabilitiesType;
import net.opengis.wps.v_2_0.ObjectFactory;
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
        //Build the DescribeProcess object
        DescribeProcess describeProcess = new DescribeProcess();
        describeProcess.setLang("fr");
        List<CodeType> identifierList = new ArrayList<>();
        CodeType dataStoreId = new CodeType();
        dataStoreId.setValue("orbisgis:test:datastore");
        identifierList.add(dataStoreId);
        describeProcess.setIdentifier(identifierList);
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
        Unmarshaller unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
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
        //Build the DescribeProcess object
        DescribeProcess describeProcess = new DescribeProcess();
        describeProcess.setLang("fr");
        List<CodeType> identifierList = new ArrayList<>();
        CodeType dataFieldId = new CodeType();
        dataFieldId.setValue("orbisgis:test:datafield");
        identifierList.add(dataFieldId);
        describeProcess.setIdentifier(identifierList);
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
        Unmarshaller unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
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
        //Build the DescribeProcess object
        DescribeProcess describeProcess = new DescribeProcess();
        describeProcess.setLang("fr");
        List<CodeType> identifierList = new ArrayList<>();
        CodeType fieldValueId = new CodeType();
        fieldValueId.setValue("orbisgis:test:fieldvalue");
        identifierList.add(fieldValueId);
        describeProcess.setIdentifier(identifierList);
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
        Unmarshaller unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
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
        //Build the DescribeProcess object
        DescribeProcess describeProcess = new DescribeProcess();
        describeProcess.setLang("fr");
        List<CodeType> identifierList = new ArrayList<>();
        CodeType enumerationId = new CodeType();
        enumerationId.setValue("orbisgis:test:enumeration");
        identifierList.add(enumerationId);
        describeProcess.setIdentifier(identifierList);
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
        Unmarshaller unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
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
        //Build the DescribeProcess object
        DescribeProcess describeProcess = new DescribeProcess();
        describeProcess.setLang("fr");
        List<CodeType> identifierList = new ArrayList<>();
        CodeType geometryDataId = new CodeType();
        geometryDataId.setValue("orbisgis:test:geometry");
        identifierList.add(geometryDataId);
        describeProcess.setIdentifier(identifierList);
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
        Unmarshaller unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
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
        //Build the DescribeProcess object
        DescribeProcess describeProcess = new DescribeProcess();
        describeProcess.setLang("fr");
        List<CodeType> identifierList = new ArrayList<>();
        CodeType rawDataId = new CodeType();
        rawDataId.setValue("orbisgis:test:rawdata");
        identifierList.add(rawDataId);
        describeProcess.setIdentifier(identifierList);
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
        Unmarshaller unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
        Object resultObject = unmarshaller.unmarshal(resultXml);
        File f = new File(this.getClass().getResource("RawDataProcessOfferings.xml").getFile());
        Object resourceObject = unmarshaller.unmarshal(f);

        String message = "Error on unmarshalling the WpsService answer, the object is not the one expected.\n\n";
        Assert.assertTrue(message, resourceObject.equals(resultObject));
    }

    /**
     * Test the RawData script DescribeProcess request.
     */
    @Test
    public void testGetCapabilities() throws JAXBException, IOException {
        //Start the wpsService
        initWpsService();
        //Build the GetCapabilities object
        GetCapabilitiesType getCapabilities = new ObjectFactory().createGetCapabilitiesType();
        JAXBElement element = new ObjectFactory().createGetCapabilities(getCapabilities);
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
        Unmarshaller unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
        Object resultObject = unmarshaller.unmarshal(resultXml);
        File f = new File(this.getClass().getResource("Capabilities.xml").getFile());
        Object resourceObject = unmarshaller.unmarshal(f);

        WPSCapabilitiesType result = (WPSCapabilitiesType) ((JAXBElement) resultObject).getValue();
        WPSCapabilitiesType resource = (WPSCapabilitiesType) ((JAXBElement) resultObject).getValue();
        String message = "Error on unmarshalling the WpsService answer, the object is not the one expected.\n\n";
        Assert.assertTrue(message, resource.equals(result));
    }

    /**
     * Initialise a wpsService to test the scripts.
     * The initialised wpsService can't execute the processes.
     */
    private void initWpsService() {
        if (wpsService == null) {
            //Start the WpsService
            LocalWpsServiceImplementation localWpsService = new LocalWpsServiceImplementation();
            localWpsService.initTest();
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
