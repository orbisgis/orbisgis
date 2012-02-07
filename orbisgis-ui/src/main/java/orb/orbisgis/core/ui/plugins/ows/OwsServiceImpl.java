/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package orb.orbisgis.core.ui.plugins.ows;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author cleglaun
 */
public class OwsServiceImpl implements OwsService {

    private static String SERVICE_URL_GETALL = "http://poulpe.heig-vd.ch/scapc2/serviceapi/web/index.php/context";
    private SAXParser parser;
    private final OwsSAXHandler owsSaxHandler;

    public OwsServiceImpl() throws ParserConfigurationException, SAXException {
        this.parser = SAXParserFactory.newInstance().newSAXParser();
        this.owsSaxHandler = new OwsSAXHandler();
    }

    public List<OwsFileBasic> getAllOwsFiles() {
        List<OwsFileBasic> owsFiles = new ArrayList<OwsFileBasic>();

        try {
            this.parser.parse(this.callService(SERVICE_URL_GETALL), this.owsSaxHandler);
            owsFiles = this.owsSaxHandler.getFiles();
        } catch (SAXException ex) {
            Logger.getLogger(OwsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OwsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return owsFiles;
    }

    private InputStream callService(String serviceUrl) {
        InputStream instream = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(serviceUrl);
            HttpResponse response = httpClient.execute(httpGet);
            System.out.println(response.getStatusLine());

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                instream = entity.getContent();

                // When HttpClient instance is no longer needed,
                // shut down the connection manager to ensure
                // immediate deallocation of all system resources
                httpClient.getConnectionManager().shutdown();
            }
        } catch (ClientProtocolException ex) {
            Logger.getLogger(OwsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OwsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return instream;
    }

    private class OwsSAXHandler extends DefaultHandler {

        private final List<OwsFileBasic> files;
        private int id;
        private String owsTitle;
        private String owsAbstract;
        private String buffer;

        public OwsSAXHandler() {
            this.files = new ArrayList<OwsFileBasic>();
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            this.files.clear();
        }
        
        
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if (qName.equals("item")) {
                OwsFileBasic file = new OwsFileBasic(id, owsTitle, owsAbstract);
                this.files.add(file);
            }
            else {
                if (qName.equals("id")) {
                    this.id = Integer.parseInt(buffer);
                }
                else if (qName.equals("title")) {
                    this.owsTitle = buffer;
                }
                else if (qName.equals("abstract")) {
                    this.owsAbstract = buffer;
                }
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            this.buffer = new String(ch, start, length);
        }

        
        public List<OwsFileBasic> getFiles() {
            return new ArrayList<OwsFileBasic>(this.files);
        }
    }
}
