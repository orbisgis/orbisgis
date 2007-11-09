/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
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
		ds.getSourceManager().register("nuevo", dsd);
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
		ds.getSourceManager().register("nuevo2", dsd2);
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
