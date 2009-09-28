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
package org.gdms.data.edition;

import junit.framework.TestCase;

import org.gdms.DBTestSource;
import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.db.DBSource;

public class PKEditionTest extends TestCase {

	public void testUpdatePK() throws Exception {
		DBSource dbSource = new DBSource("127.0.0.1", 5432, "gdms", "postgres",
				"postgres", "gisapps", "jdbc:postgresql");
		DBTestSource src = new DBTestSource("source", "org.postgresql.Driver",
				SourceTest.internalData + "postgresEditablePK.sql", dbSource);
		src.backup();
		DataSource d = SourceTest.dsf.getDataSource("source");

		d.open();
		d.setInt(0, "id", 7);
		d.setString(0, "gis", "gilberto");
		d.commit();
		d.close();

		d = d.getDataSourceFactory().getDataSourceFromSQL(
				"select * from source where id = 7;");
		d.open();
		assertTrue(d.getRowCount() == 1);
		assertTrue(d.getInt(0, "id") == 7);
		assertTrue(d.getString(0, "gis").equals("gilberto"));
		d.close();
	}

	public void testDeleteUpdatedPK() throws Exception {
		DBSource dbSource = new DBSource("127.0.0.1", 5432, "gdms", "postgres",
				"postgres", "gisapps", "jdbc:postgresql");
		DBTestSource src = new DBTestSource("source", "org.postgresql.Driver",
				SourceTest.internalData + "postgresEditablePK.sql", dbSource);
		src.backup();
		DataSource d = SourceTest.dsf.getDataSource("source");

		d.open();
		d.setInt(2, "id", 9);
		d.deleteRow(2);
		d.commit();
		d.close();

		d = d.getDataSourceFactory().getDataSourceFromSQL(
				"select * from source where id = 9;");
		d.open();
		assertTrue(0 == d.getRowCount());
		d.close();
	}

	@Override
	protected void setUp() throws Exception {
		SourceTest.dsf.getSourceManager().removeAll();
	}

}