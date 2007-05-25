package org.gdms.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.gdms.SourceTest;
import org.gdms.data.persistence.Handler;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class DataSourceFactoryTests extends SourceTest {

	/**
	 * Tests the InternalDataSource.remove method
	 *
	 * @throws RuntimeException
	 *             DOCUMENT ME!
	 */
	public void testRemoveDataSources() throws Exception {
		InternalDataSource d = null;

		String dsName = super.getAnyNonSpatialResource();
		d = dsf.getDataSource(dsName);
		d.remove();

		try {
			d = dsf.getDataSource(dsName);
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
	 * @throws Throwable
	 *             DOCUMENT ME!
	 */
	public void testOperationDataSourceName() throws Throwable {
		InternalDataSource d = dsf.executeSQL("select * from "
				+ super.getAnyNonSpatialResource() + ";");
		assertTrue(dsf.getDataSource(d.getName()) != null);
	}

	/**
	 * Tests the persistence
	 *
	 * @throws Throwable
	 *             DOCUMENT ME!
	 */
	public void testXMLMemento() throws Throwable {
		InternalDataSource d = dsf.executeSQL("select * from "
				+ super.getAnyNonSpatialResource() + ";");
		Memento m = d.getMemento();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Handler h = new Handler();
		PrintWriter pw = new PrintWriter(out);
		h.setOut(pw);
		m.setContentHandler(h);
		m.getXML();
		pw.close();

		XMLReader reader = XMLReaderFactory
				.createXMLReader("org.apache.crimson.parser.XMLReaderImpl");
		MementoContentHandler mch = new MementoContentHandler();
		reader.setContentHandler(mch);
		reader.parse(new InputSource(
				new ByteArrayInputStream(out.toByteArray())));

		InternalDataSource n = mch.getDataSource(dsf);

		n.open();
		d.open();
		assertTrue("Fallo en la persistencia", d.getAsString().equals(
				n.getAsString()));
		n.cancel();
		d.cancel();
	}

}
