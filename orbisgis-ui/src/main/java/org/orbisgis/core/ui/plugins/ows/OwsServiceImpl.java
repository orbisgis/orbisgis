/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.ows;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Proxy implementation for the remote services. The REST services urls
 * are set within the helper class {@link OwsContextUtils}.
 * @author CŽdric Le Glaunec <cedric.leglaunec@gmail.com>
 */
public class OwsServiceImpl implements OwsService {

    private SAXParser parser;
    private final OwsFilesSAXHandler owsFilesSaxHandler;
    private final OwsWorkspacesSAXHandler owsWorkspacesSAXHandler;
    private DocumentBuilder builder;

    public OwsServiceImpl() throws ParserConfigurationException, SAXException {
        this.parser = SAXParserFactory.newInstance().newSAXParser();
        this.owsFilesSaxHandler = new OwsFilesSAXHandler();
        this.owsWorkspacesSAXHandler = new OwsWorkspacesSAXHandler();
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        // In order to get a xerces implementation which takes namespaces
        // into account
        dbf.setNamespaceAware(true); 
        builder = dbf.newDocumentBuilder();
    }
    
    

    @Override
    public List<OwsFileBasic> getAllOwsFiles(OwsWorkspace workspace) {
        
        List<OwsFileBasic> owsFiles = new ArrayList<OwsFileBasic>();
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("workspace", workspace.getName()));
        
        try {
            InputStream xml = OwsContextUtils.callServicePost(OwsContextUtils.getServiceGetAllUrl(), formparams, true);
            this.parser.parse(xml, this.owsFilesSaxHandler);
            xml.close();
            owsFiles = this.owsFilesSaxHandler.getFiles();
        } catch (SAXException ex) {
            Logger.getLogger(OwsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OwsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return owsFiles;
    }
    
    @Override
    public Node getOwsFile(OwsWorkspace workspace, int id) {
        
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("workspace", workspace.getName()));
        
        String url = OwsContextUtils.getServiceGetOneOwsUrl() + "/" + id;
        InputStream owsInput = OwsContextUtils.callServicePost(url, formparams, false);
        Node node = null;
        
        try {
            Document doc = builder.parse(owsInput);
            doc.getDocumentElement().normalize();
            node = doc.getElementsByTagName("OWSContext").item(0);
        } catch (SAXException ex) {
            Logger.getLogger(OwsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OwsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return node;

    }

    @Override
    public void saveOwsFileAs(String data) {
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("owc", data));
        String url = OwsContextUtils.getServiceExportOwsAsUrl();
        OwsContextUtils.callServicePost(url, formparams, true);
    }

    @Override
    public void saveOwsFile(String data, int projectId) {
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("owc", data));
        formparams.add(new BasicNameValuePair("id", Integer.toString(projectId)));
        String url = OwsContextUtils.getServiceExportOwsAsUrl();
        OwsContextUtils.callServicePost(url, formparams, true);
    }

    @Override
    public List<OwsWorkspace> getAllOwsWorkspaces() {
        List<OwsWorkspace> owsWorkspaces = new ArrayList<OwsWorkspace>();

        try {
            InputStream xml = OwsContextUtils.callServiceGet(OwsContextUtils.getServiceGetAllOwsWorkspace());
            this.parser.parse(xml, this.owsWorkspacesSAXHandler);
            xml.close();
            owsWorkspaces = this.owsWorkspacesSAXHandler.getWorkspaces();
        } catch (SAXException ex) {
            Logger.getLogger(OwsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OwsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return owsWorkspaces;
    }
    
    /**
     * SAX handler used for extracting a list of workspaces in the REST service.
     */
    private class OwsWorkspacesSAXHandler extends DefaultHandler {
        private final List<OwsWorkspace> workspaces;

        private String buffer;
        
        public OwsWorkspacesSAXHandler() {
            this.workspaces = new ArrayList<OwsWorkspace>();
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            this.buffer = new String(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if (qName.equals("item")) {
                workspaces.add(new OwsWorkspace(buffer));
            }
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            this.workspaces.clear();
        }

        public List<OwsWorkspace> getWorkspaces() {
            return workspaces;
        }
    }

    /**
     * SAX handler used for extracting a list of ows context files 
     * returned by the REST service.
     */
    private class OwsFilesSAXHandler extends DefaultHandler {

        private final List<OwsFileBasic> files;
        private int id;
        private String owsTitle;
        private String owsAbstract;
        private String buffer;

        public OwsFilesSAXHandler() {
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
