package org.gdms.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.gdms.SourceTest;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.persistence.Handler;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoContentHandler;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.instruction.TableNotFoundException;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.hardcode.driverManager.DriverLoadException;

public class DataSourceFactoryTests extends SourceTest {

	/**
	 * Tests the DataSource.remove method
	 *
	 * @throws RuntimeException
	 *             DOCUMENT ME!
	 */
	public void testRemoveDataSources() throws Exception {
		DataSource d = null;

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
		DataSource d = dsf.executeSQL("select * from "
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
		DataSource d = dsf.executeSQL("select * from "
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

		DataSource n = mch.getDataSource(dsf);

		n.open();
		d.open();
		assertTrue("Fallo en la persistencia", d.getAsString().equals(
				n.getAsString()));
		n.cancel();
		d.cancel();
	}

	public void testSeveralNames() throws Exception {
		String dsName = super.getAnyNonSpatialResource();
		testSeveralNames(dsName);
		testSeveralNames(dsf.executeSQL("select * from " + dsName).getName());
	}

	private void testSeveralNames(String dsName) throws TableNotFoundException,
			SourceAlreadyExistsException, DriverLoadException,
			NoSuchTableException, DataSourceCreationException, DriverException,
			AlreadyClosedException {
		String secondName = "secondName" + System.currentTimeMillis();
		dsf.addName(dsName, secondName);
		checkNames(dsName, secondName);
		try {
			dsf.addName("e" + System.currentTimeMillis(), "qosgsdq");
			assertTrue(false);
		} catch (TableNotFoundException e) {
		}
	}

	private void checkNames(String dsName, String secondName)
			throws DriverLoadException, NoSuchTableException,
			DataSourceCreationException, DriverException,
			AlreadyClosedException {
		DataSource ds1 = dsf.getDataSource(dsName);
		DataSource ds2 = dsf.getDataSource(secondName);
		ds1.open();
		ds2.open();
		assertTrue(equals(getDataSourceContents(ds1),
				getDataSourceContents(ds2)));
		ds1.cancel();
		ds2.cancel();
	}

	public void testSecondNameCollidesWithName() throws Exception {
		String dsName1 = super.getAnyNonSpatialResource();
		String dsName2 = super.getAnySpatialResource();
		try {
			dsf.addName(dsName1, dsName2);
			assertFalse(true);
		} catch (SourceAlreadyExistsException e) {
		}
	}

	public void testRegisteringCollission() throws Exception {
		String name = "e" + System.currentTimeMillis();
		ObjectSourceDefinition def = new ObjectSourceDefinition(
				new ObjectMemoryDriver(null, null));
		dsf.registerDataSource(name, def);
		try {
			dsf.registerDataSource(name, def);
			assertTrue(false);
		} catch (SourceAlreadyExistsException e) {
		}
	}

	public void testRenameFirstName() throws Exception {
		String dsName = super.getAnyNonSpatialResource();
		String newName = "test" + System.currentTimeMillis();
		String newName2 = "test" + System.currentTimeMillis() + 1;
		dsf.addName(dsName, newName);
		dsf.rename(dsName, newName2);
		checkNames(newName, newName2);
	}

	public void testRenameSecondName() throws Exception {
		String dsName = super.getAnyNonSpatialResource();
		String newName = "test" + System.currentTimeMillis();
		dsf.addName(dsName, newName);
		String otherName = "test" + System.currentTimeMillis() + 1;
		dsf.rename(newName, otherName);
		try {
			dsf.getDataSource(newName);
			assertTrue(false);
		} catch (NoSuchTableException e) {
		}
		checkNames(otherName, dsName);
	}

	public void testRenameFirstNameCollidesWithSecond() throws Exception {
		String dsName = super.getAnyNonSpatialResource();
		String newName = "test" + System.currentTimeMillis();
		dsf.addName(dsName, newName);
		try {
			dsf.rename(dsName, newName);
			assertTrue(false);
		} catch (SourceAlreadyExistsException e) {
		}
	}

	public void testRenameSecondNameCollidesWithFirst() throws Exception {
		String dsName = super.getAnyNonSpatialResource();
		String newName = "test" + System.currentTimeMillis();
		dsf.addName(dsName, newName);
		try {
			dsf.rename(newName, dsName);
			assertTrue(false);
		} catch (SourceAlreadyExistsException e) {
		}
	}

	public void testRemoveSourceRemovesAllNames() throws Exception {
		String dsName = super.getAnyNonSpatialResource();
		String secondName = "secondName" + System.currentTimeMillis();
		dsf.addName(dsName, secondName);
		dsf.remove(dsName);
		assertTrue(!dsf.existDS(secondName));
	}
}
