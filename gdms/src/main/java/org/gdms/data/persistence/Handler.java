package org.gdms.data.persistence;

import java.io.PrintWriter;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


/**
 * Handler that receives the SAX events from a Memento and stores the XML in a
 * PrinWriter setted by the user
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class Handler implements ContentHandler {
    private PrintWriter out;
    private String sangrado = "";
    private Stack<Boolean> empty = new Stack<Boolean>();

    /**
     * Crea un nuevo Handler.
     */
    public Handler() {
        empty.push(Boolean.FALSE);
    }

    /**
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    public void endDocument() throws SAXException {
        out.close();
    }

    /**
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    public void startDocument() throws SAXException {
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    }

    /**
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int length)
        throws SAXException {
    }

    /**
     * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
     */
    public void ignorableWhitespace(char[] ch, int start, int length)
        throws SAXException {
    }

    /**
     * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
     */
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    /**
     * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
     */
    public void skippedEntity(String name) throws SAXException {
    }

    /**
     * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
     */
    public void setDocumentLocator(Locator locator) {
    }

    /**
     * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String,
     *      java.lang.String)
     */
    public void processingInstruction(String target, String data)
        throws SAXException {
    }

    /**
     * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String,
     *      java.lang.String)
     */
    public void startPrefixMapping(String prefix, String uri)
        throws SAXException {
    }

    /**
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void endElement(String namespaceURI, String localName, String qName)
        throws SAXException {
        sangrado = sangrado.substring(0, sangrado.length() - 2);

        boolean vacio = ((Boolean) empty.pop()).booleanValue();

        if (vacio) {
            out.println("/>");
        } else {
            out.println(sangrado + "</" + qName + ">");
        }
    }

    /**
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
     *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String namespaceURI, String localName,
        String qName, Attributes atts) throws SAXException {
        boolean vacio = ((Boolean) empty.peek()).booleanValue();
        String cadena = "";

        if (vacio) {
            cadena = ">\n";
            empty.pop();
            empty.push(Boolean.FALSE);
        }

        String attsString = "";

        for (int i = 0; i < atts.getLength(); i++) {
            attsString += (" " + atts.getQName(i) + "=\"" + atts.getValue(i) +
            "\"");
        }

        cadena += (sangrado + "<" + qName + attsString);
        out.print(cadena);
        sangrado += "  ";
        empty.push(Boolean.TRUE);
    }

    /**
     * Sets the PrintWriter to write the XML to
     *
     * @param writer
     */
    public void setOut(PrintWriter writer) {
        out = writer;
    }
}
