package org.gdms.data.persistence;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Abstract Memento object
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public interface Memento {
	/**
	 * Sets the content handler for the getXML method
	 * 
	 * @param saxHandler
	 *            content handler
	 */
	public void setContentHandler(ContentHandler saxHandler);

	/**
	 * Parses the memento and generates the corresponding SAX events in the
	 * content handler stablished by setContentHandler.
	 * 
	 * @throws SAXException
	 *             If error
	 */
	public void getXML() throws SAXException;
}
