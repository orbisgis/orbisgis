package org.gdms.oldFunctionalities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;

import junit.framework.TestCase;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.ClosedDataSourceException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreation;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.DataSourceFinalizationException;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.persistence.Handler;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoContentHandler;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * DOCUMENT ME!
 * 
 * @author Fernando Gonzalez Cortes
 */
public class Tests extends TestCase {

	private DataSourceFactory ds = new DataSourceFactory();

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 * @throws RuntimeException
	 *             DOCUMENT ME!
	 */
	public void testDelegation() throws Exception {
		ds.getDelegatingStrategy().setDelegating(false);

		DataSource d;

		try {
			d = ds.executeSQL("select apellido from hsqldbpersona;");

			d.open();

			String aux = d.getAsString();
			d.cancel();
			ds.getDelegatingStrategy().setDelegating(true);

			d = ds.executeSQL("select apellido from hsqldbpersona;");
			d.open();
			assertTrue(aux.equals(d.getAsString()));
			d.cancel();
		} finally {
			try {
				ds.freeResources();
			} catch (DataSourceFinalizationException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws DriverException
	 * @throws RuntimeException
	 *             DOCUMENT ME!
	 */
	public void testViewRemoving() throws Exception {
		ds.getDelegatingStrategy().setDelegating(true);

		DataSource d1;

		try {
			d1 = ds.executeSQL("select apellido from hsqldbpersona;");
		} finally {
			try {
				ds.freeResources();
			} catch (DataSourceFinalizationException e) {
				throw new RuntimeException(e);
			}
		}

		try {
			d1.open();
			d1.cancel();
			assertTrue("Views not deleted", false);
		} catch (DriverException e) {
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws DriverException
	 * @throws RuntimeException
	 *             DOCUMENT ME!
	 */
	public void testQueryDataSources() throws Exception {
		DataSource d1;

		try {
			d1 = ds.getDataSource("hsqldbapellido");
			ds.getDelegatingStrategy().setDelegating(true);

			DataSource d2 = ds
					.executeSQL("select apellido from hsqldbpersona;");

			d1.open();
			d2.open();
			assertTrue(d1.getAsString().equals(d2.getAsString()));
			d1.cancel();
			d2.cancel();
		} finally {
			try {
				ds.freeResources();
			} catch (DataSourceFinalizationException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void testSeveralStartsOneStop(DataSource d) throws Exception {
		d.open();
		d.open();
		d.cancel();
		try {
			d.getFieldValue(0, 0);
			assertTrue(true);
		} catch (ClosedDataSourceException e) {
			assertTrue(false);
		}
		d.cancel();
		try {
			d.cancel();
			assertTrue(false);
		} catch (AlreadyClosedException e) {
			assertTrue(true);
		}
	}

	public void testSeveralStartsOneStop() throws Exception {
		testSeveralStartsOneStop(ds.getDataSource("persona"));
		testSeveralStartsOneStop(ds.getDataSource("hsqldbpersona"));
		testSeveralStartsOneStop(ds.getDataSource("hsqldbpersonatransactional"));
		testSeveralStartsOneStop(ds.getDataSource("objectpersona"));
		testSeveralStartsOneStop(ds.executeSQL("select * from hsqldbpersona;"));
	}

	public void testSaveAs() throws Exception {
		File f = new File("src/test/resources/nuevo.csv");
		if (f.exists()) {
			assertTrue(f.delete());
		}
		f = new File("src/test/resources/nuevo2.csv");
		if (f.exists()) {
			assertTrue(f.delete());
		}
		DefaultMetadata ddm = new DefaultMetadata();
		ddm.addField("id", Type.STRING, "STRING");
		ddm.addField("name", Type.STRING, "STRING");
		DataSourceCreation dsc = new FileSourceCreation(new File(
				"src/test/resources/nuevo.csv"), ddm);
		ds.createDataSource(dsc);
		DataSourceDefinition dsd = new FileSourceDefinition(
				"src/test/resources/nuevo.csv");
		ds.registerDataSource("nuevo", dsd);
		DataSource nuevo = ds.getDataSource("nuevo");
		nuevo.open();
		nuevo.insertFilledRow(new Value[] { ValueFactory.createValue("0"),
				ValueFactory.createValue("fernan") });
		nuevo.insertFilledRow(new Value[] { ValueFactory.createValue("1"),
				ValueFactory.createValue("paco") });
		nuevo.commit();

		DataSourceCreation dsc2 = new FileSourceCreation(new File(
				"src/test/resources/nuevo2.csv"), ddm);
		ds.createDataSource(dsc2);
		DataSourceDefinition dsd2 = new FileSourceDefinition(
				"src/test/resources/nuevo2.csv");
		ds.registerDataSource("nuevo2", dsd2);
		DataSource nuevo2 = ds.getDataSource("nuevo2");

		nuevo2.saveData(nuevo);
		nuevo2.open();
		nuevo.open();
		assertTrue(nuevo.getAsString().equals(nuevo2.getAsString()));
		nuevo2.cancel();
		nuevo.cancel();
	}

	/**
	 * Tests the persistence
	 * 
	 * @throws Throwable
	 *             DOCUMENT ME!
	 */
	public void testXMLMementoOfQueryDataSource() throws Throwable {
		DataSource d = ds.getDataSource("hsqldbapellido");
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

		DataSource n = mch.getDataSource(ds);

		n.open();
		d.open();
		assertTrue("Fallo en la persistencia", d.getAsString().equals(
				n.getAsString()));
		n.cancel();
		d.cancel();

		try {
			ds.freeResources();
		} catch (DataSourceFinalizationException e) {
			throw new RuntimeException(e);
		}
	}
}
