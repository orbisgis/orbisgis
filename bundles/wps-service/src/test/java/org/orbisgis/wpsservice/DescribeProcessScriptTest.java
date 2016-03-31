package org.orbisgis.wpsservice;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.opengis.ows.v_2_0.CodeType;
import net.opengis.wps.v_2_0.DescribeProcess;
import org.junit.Assert;
import org.junit.Test;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * This test class perform tests about groovy wps scripts.
 * It loads several script in the wpsService and then test the DescribeProcess request.
 *
 * @author Sylvain PALOMINOS
 */
public class DescribeProcessScriptTest {
    WpsService wpsService;

    /**
     * Test the DataStore script DescribeProcess request.
     */
    @Test
    public void testDataStoreScript(){
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
        try {
            Marshaller marshaller = JaxbContainer.JAXBCONTEXT.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            marshaller.marshal(describeProcess, out);
            //Write the OutputStream content into an Input stream before sending it to the wpsService
            InputStream in = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
            ByteArrayOutputStream xml = (ByteArrayOutputStream)wpsService.callOperation(in);
            //Get back the result of the DescribeProcess request as a BufferReader
            BufferedReader result = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(xml.toByteArray())));
            //Read the result BufferReader line by line and check they are the same as the ones from the
            //DataStoreProcessOffering xml file in the resources
            String responseLine;
            String resourcePath = this.getClass().getResource("DataStoreProcessOfferings.xml").getFile();
            BufferedReader resource = new BufferedReader(new FileReader(resourcePath));
            String resourceLine;
            while ((responseLine = result.readLine()) != null && (resourceLine = resource.readLine()) != null) {
                boolean condition = responseLine.equals(resourceLine);
                String message = "Error on the response xml : should be '"+resourceLine+"' instead of '"+responseLine+"'.";
                Assert.assertTrue(message, condition);
            }
            boolean condition = responseLine == null;
            String message = "Error on the response xml : should be empty (null) instead of '"+responseLine+"'.";
            Assert.assertTrue(message, condition);
        } catch (JAXBException e) {
            Assert.fail("Unable to get the JAXB Marshaller.\n"+e.getMessage());
        } catch (FileNotFoundException e) {
            Assert.fail("Unable to get the resource file 'DataStoreProcessOfferings.xml'.\n"+e.getMessage());
        } catch (IOException e) {
            Assert.fail("Unable to read the bufferReader.\n" + e.getMessage());
        }

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
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            wpsService = localWpsService;
        }
    }
}
