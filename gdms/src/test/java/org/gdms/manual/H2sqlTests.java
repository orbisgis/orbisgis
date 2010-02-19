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
package org.gdms.manual;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;

public class H2sqlTests {

	private static final String DB_PATH = "/tmp/h2/myH2db";

	/**
	 * @param args
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws NonEditableDataSourceException
	 * @throws FreeingResourcesException
	 * @throws DriverException
	 * @throws DataSourceCreationException
	 * @throws NoSuchTableException
	 * @throws DriverLoadException
	 */
	public static void main(String[] args) throws Exception {

		long start = System.currentTimeMillis();

		createH2db();

		DataSourceFactory dsf = new DataSourceFactory();

		dsf.getSourceManager().register("point", new DBTableSourceDefinition(
				new DBSource(null, 0, DB_PATH, "sa", "", "POINT",
						"jdbc:h2:file")));



		DataSource d;

		d = dsf.getDataSource("point");

		d.open();

		System.out.println("Number of lines " + d.getRowCount());

		int fieldCount = d.getMetadata().getFieldCount();

		for (int k = 0; k < d.getRowCount(); k++) {
			for (int t = 0; t < fieldCount; t++) {

				System.out.println("Metadonnées "
						+ d.getMetadata().getFieldName(t)
						+ d.getMetadata().getFieldType(t).getTypeCode());

				System.out.println("Valeur " + d.getFieldValue(k, t));

			}

		}

		d.commit();
		d.close();

		System.out
				.println("Total time " + (System.currentTimeMillis() - start));

	}

	public static void createH2db() throws ClassNotFoundException, SQLException {

		Class.forName("org.h2.Driver");

		Connection c = DriverManager.getConnection("jdbc:h2:" + DB_PATH, "sa",
				"");

		Statement st = c.createStatement();

		st.execute("DROP TABLE point IF EXISTS");

		st
				.execute("CREATE TABLE point (id INTEGER, nom VARCHAR(10), nom2 VARCHAR(100), length DECIMAL(20, 2), area DOUBLE, start DATE, prenom VARCHAR(100),  PRIMARY KEY(id), the_geom BLOB)");

		for (int i = 0; i < 4; i++) {

			st
					.execute("INSERT INTO point VALUES("
							+ i
							+ ", 'BOCHER', 'bocher', "
							+ (215.45 + i)
							+ ", 222,'2007-06-15', 'ERWAN', GeomFromText('POINT(0 1)', '-1'))");

		}

		st.close();
		c.close();

	}

}
