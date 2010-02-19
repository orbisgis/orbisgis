/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.drivers;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.ValueFactory;

public class CSVDriverTest extends TestCase {

	private File file;
	private DataSourceFactory dsf;

	@Override
	protected void setUp() throws Exception {
		File file1 = new File("src/test/resources/backup/csvdrivertest.csv");
		if (file1.exists()) {
			if (!file1.delete()) {
				throw new IOException("Cannot delete file " + file1);
			}
		}
		file = file1;

		dsf = new DataSourceFactory();
		DefaultMetadata metadata = new DefaultMetadata();
		metadata.addField("f1", Type.STRING);
		metadata.addField("f2", Type.STRING);
		FileSourceCreation fsc = new FileSourceCreation(file, metadata);
		dsf.createDataSource(fsc);
	}

	public void testScapeSemiColon() throws Exception {
		DataSource ds = dsf.getDataSource(file);
		ds.open();
		ds.insertEmptyRow();
		ds.setFieldValue(0, 0, ValueFactory.createValue("a;b"));
		ds.setFieldValue(0, 1, ValueFactory.createValue("c\\d"));
		ds.commit();
		ds.close();

		ds.open();
		assertTrue(ds.getString(0, 0).equals("a;b"));
		assertTrue(ds.getString(0, 1).equals("c\\d"));
		ds.close();
	}

	public void testNullValues() throws Exception {
		DataSource ds = dsf.getDataSource(file);
		ds.open();
		ds.insertEmptyRow();
		ds.setFieldValue(0, 0, ValueFactory.createNullValue());
		ds.setFieldValue(0, 1, ValueFactory.createNullValue());
		ds.commit();
		ds.close();

		ds.open();
		assertTrue(ds.isNull(0, 0));
		assertTrue(ds.isNull(0, 1));
		ds.close();
	}
}
