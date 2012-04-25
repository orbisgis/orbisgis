/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.data.edition;


import org.junit.Before;
import org.junit.Test;
import org.gdms.DBTestSource;
import org.gdms.TestBase;
import org.gdms.data.DataSource;
import org.gdms.data.db.DBSource;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

public class PKEditionTest extends TestBase {

        @Test
        public void testUpdatePK() throws Exception {
                assumeTrue(TestBase.postGisAvailable);
                DBSource dbSource = new DBSource("127.0.0.1", 5432, "gdms", "postgres",
                        "postgres", "gisapps", "jdbc:postgresql");
                DBTestSource src = new DBTestSource("source", "org.postgresql.Driver",
                        TestBase.internalData + "postgresEditablePK.sql", dbSource);
                src.backup();
                DataSource d = TestBase.dsf.getDataSource("source");

                d.open();
                d.setInt(0, "id", 7);
                d.setString(0, "gis", "gilberto");
                d.commit();
                d.close();

                d = TestBase.dsf.getDataSourceFromSQL(
                        "select * from source where id = 7;");
                d.open();
                assertEquals(d.getRowCount(), 1);
                assertEquals(d.getInt(0, "id"), 7);
                assertEquals(d.getString(0, "gis"), "gilberto");
                d.close();
        }

        @Test
        public void testDeleteUpdatedPK() throws Exception {
                assumeTrue(TestBase.postGisAvailable);
                DBSource dbSource = new DBSource("127.0.0.1", 5432, "gdms", "postgres",
                        "postgres", "gisapps", "jdbc:postgresql");
                DBTestSource src = new DBTestSource("source", "org.postgresql.Driver",
                        TestBase.internalData + "postgresEditablePK.sql", dbSource);
                src.backup();
                DataSource d = TestBase.dsf.getDataSource("source");

                d.open();
                d.setInt(2, "id", 9);
                d.deleteRow(2);
                d.commit();
                d.close();

                d = TestBase.dsf.getDataSourceFromSQL(
                        "select * from source where id = 9;");
                d.open();
                assertEquals(0, d.getRowCount());
                d.close();
        }

        @Before
        public void setUp() throws Exception {
                TestBase.dsf.getSourceManager().removeAll();
        }
}