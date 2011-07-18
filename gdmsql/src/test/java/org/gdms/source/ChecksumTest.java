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
package org.gdms.source;


import java.io.File;
import junit.framework.TestCase;

import org.gdms.SQLTestSource;
import org.gdms.SQLBaseTest;
import org.gdms.data.DataSource;
import org.gdms.data.SQLDataSourceFactory;

public class ChecksumTest extends TestCase {

	private SQLDataSourceFactory dsf;
	private SourceManager sm;

	public void testModifyingSourceOutsideFactory() throws Exception {

                String name = "sql";
		String sql = "select count(id) from file;";
		SQLTestSource sts = new SQLTestSource(name, sql);
		sts.backup();
		dsf.register(name, sql);
		testModifyingSourceOutsideFactory(name, true);

	}

	private synchronized void testModifyingSourceOutsideFactory(String name,
			boolean upToDateValue) throws Exception {
		assertTrue(sm.getSource(name).isUpToDate() == false);
		sm.saveStatus();
		assertTrue(sm.getSource(name).isUpToDate() == true);

		DataSource ds = SQLBaseTest.dsf.getDataSource(name);
		ds.open();
		ds.deleteRow(0);
		if (upToDateValue) {
			ds.close();
		} else {
			// To change modification time
			wait(2000);
			ds.commit();
			ds.close();
		}

		instantiateDSF();
		assertTrue(sm.getSource(name).isUpToDate() == upToDateValue);
	}

	@Override
	protected void setUp() throws Exception {
		SQLBaseTest.dsf.getSourceManager().removeAll();
		instantiateDSF();
		sm.removeAll();
                sm.register("file", new File(SQLBaseTest.internalData,"landcover2000.shp"));
	}

	private void instantiateDSF() {
		dsf = new SQLDataSourceFactory(SQLBaseTest.internalData
				+ "source-management");
		sm = dsf.getSourceManager();
	}
}
