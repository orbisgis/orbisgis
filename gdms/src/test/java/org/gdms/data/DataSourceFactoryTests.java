package org.gdms.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.persistence.Handler;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class DataSourceFactoryTests extends SourceTest {

	/**
	 * Tests the DataSource.remove method
	 *
	 * @throws RuntimeException DOCUMENT ME!
	 */
	public void testRemoveDataSources() throws Exception {
	    DataSource d = null;

	    d = dsf.getDataSource("persona");
	    d.remove();

	    try {
	        d = dsf.getDataSource("persona");
	        assertTrue(false);
	    } catch (NoSuchTableException e) {
	    }
	}

	/**
	 * Tests the DataSourceFactory.removeAllDataSources method
	 */
	public void testRemoveAllDataSources() {
	    dsf.removeAllDataSources();
	    assertTrue(dsf.getDataSourcesDefinition().length == 0);
	}

	/**
	 * Tests the naming of operation layer datasource
	 *
	 * @throws Throwable DOCUMENT ME!
	 */
	public void testOperationDataSourceName() throws Throwable {
	    DataSource d = dsf.executeSQL("select * from persona;");
	    assertTrue(dsf.getDataSource(d.getName()) != null);
	}

	/**
	 * Tests the persistence
	 *
	 * @throws Throwable DOCUMENT ME!
	 */
	public void testXMLMemento() throws Throwable {
	    DataSource d = dsf.executeSQL("select * from persona;");
	    Memento m = d.getMemento();

	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    Handler h = new Handler();
	    PrintWriter pw = new PrintWriter(out);
	    h.setOut(pw);
	    m.setContentHandler(h);
	    m.getXML();
	    pw.close();

	    XMLReader reader = XMLReaderFactory.createXMLReader(
	            "org.apache.crimson.parser.XMLReaderImpl");
	    MementoContentHandler mch = new MementoContentHandler();
	    reader.setContentHandler(mch);
	    reader.parse(new InputSource(
	            new ByteArrayInputStream(out.toByteArray())));

	    DataSource n = mch.getDataSource(dsf);

	    n.beginTrans();
	    d.beginTrans();
	    assertTrue("Fallo en la persistencia",
	        d.getAsString().equals(n.getAsString()));
	    n.rollBackTrans();
	    d.rollBackTrans();
	}

}
