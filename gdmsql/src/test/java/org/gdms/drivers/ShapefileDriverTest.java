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

import junit.framework.TestCase;

import org.gdms.SQLBaseTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreation;
import org.gdms.data.SQLDataSourceFactory;
import org.gdms.data.file.FileSourceCreation;


public class ShapefileDriverTest extends TestCase {
	private SQLDataSourceFactory dsf;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dsf = new SQLDataSourceFactory();
		dsf.setTempDir(SQLBaseTest.internalData + "backup");
	}

	public void testSaveSQL() throws Exception {
		dsf.getSourceManager().register(
				"shape",
				new FileSourceCreation(new File(SQLBaseTest.internalData
						+ "landcover2000.shp"), null));

		DataSource sql = dsf.getDataSourceFromSQL(
				"select st_Buffer(the_geom, 20) from shape;",
				SQLDataSourceFactory.DEFAULT);
		DataSourceCreation target = new FileSourceCreation(new File(
				SQLBaseTest.backupDir, "outputtestSaveSQL.shp"), null);
		dsf.getSourceManager().register("buffer", target);
		dsf.saveContents("buffer", sql);

		DataSource ds = dsf.getDataSource("buffer");
		ds.open();
		sql.open();
		assertTrue(ds.getRowCount() == sql.getRowCount());
		sql.close();
		ds.close();
	}
}