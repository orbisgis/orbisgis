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

import org.junit.Test;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.gdms.SQLBaseTest;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.db.DBSource;

import static org.junit.Assert.*;
import static org.junit.Assume.*;


public class DBDriverTest extends SQLBaseTest {

        @Test
        public void testShapefile2PostgreSQL() throws Exception {
                assumeTrue(SQLBaseTest.postGisAvailable);
                // Delete the table if exists
                // DBSource dbSource = new DBSource("127.0.0.1", 5432, "gdms",
                // "postgres",
                // "postgres", "testShapefile2PostgreSQL", "jdbc:postgresql");
                DBSource dbSource = new DBSource("127.0.0.1", 5432,
                        "gisdb", "gis", "gis",
                        "testShapefile2PostgreSQL", "jdbc:postgresql");
                try {
                        execute(dbSource, "DROP TABLE testShapefile2PostgreSQL;");
                } catch (SQLException e) {
                        e.printStackTrace();
                }

                // register both sources
                String registerDB = "CALL register('postgresql','"
                        + dbSource.getHost() + "'," + " '" + dbSource.getPort() + "','"
                        + dbSource.getDbName() + "','" + dbSource.getUser() + "','"
                        + dbSource.getPassword() + "'," + "'" + dbSource.getTableName()
                        + "','bati');";
                String registerFile = "CALL register('" + internalData
                        + "landcover2000.shp','parcels');";
                dsf.executeSQL(registerDB);
                dsf.executeSQL(registerFile);

                // Do the migration
                String load = "create table lands as select * " + "from parcels;";
                dsf.executeSQL(load);

                // Get each value
                SpatialDataSourceDecorator db = new SpatialDataSourceDecorator(dsf.getDataSource("lands"));
                SpatialDataSourceDecorator file = new SpatialDataSourceDecorator(dsf.getDataSource("parcels"));
                db.open();
                file.open();
                assertEquals(db.getRowCount(),file.getRowCount());
                for (int i = 0; i < db.getRowCount(); i++) {
                        assertTrue(db.getGeometry(i).equalsExact(file.getGeometry(i)));
                }
                db.close();
                file.close();
        }

        @Test
        public void testReadSchemaPostGreSQL() throws Exception {
                assumeTrue(SQLBaseTest.postGisAvailable);
                DBSource dbSource = new DBSource("127.0.0.1", 5432,
                        "gisdb", "gis", "gis", "gis_schema",
                        "administratif", "jdbc:postgresql");

                dsf.getSourceManager().register("data_source", dbSource);

                dsf.executeSQL("select * from data_source ; ");
        }

        @Test
        public void testReadMultiSchemasPostGreSQL() throws Exception {
                assumeTrue(SQLBaseTest.postGisAvailable);
                DBSource publicSchemaDbSource = new DBSource("localhost", 5432,
                        "gisdb", "gis", "gis",
                        "landcover2000", "jdbc:postgresql");

                String publicSchemaSourceName = dsf.getSourceManager().getUniqueName(publicSchemaDbSource.getTableName());
                dsf.getSourceManager().register(publicSchemaSourceName, publicSchemaDbSource);

                DBSource otherSchemaDbSource = new DBSource("localhost", 5432,
                        "gisdb", "gis", "gis", "gis_schema",
                        "parcels", "jdbc:postgresql");
                String otherSchemaSourceName = dsf.getSourceManager().getUniqueName(otherSchemaDbSource.getTableName());
                dsf.getSourceManager().register(otherSchemaSourceName, otherSchemaDbSource);

                SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(dsf.getDataSource(otherSchemaDbSource));
                sds.open();
                sds.isDefaultVectorial();
                sds.close();

                assertFalse(otherSchemaSourceName.equals(publicSchemaSourceName));

                dsf.executeSQL("select * from " + publicSchemaSourceName + " ;");

                dsf.executeSQL("select * from " + otherSchemaSourceName + " ;");

        }

        @Test
        public void testShapefile2H2() throws Exception {
                assumeTrue(SQLBaseTest.h2Available);
                // Delete the table if exists
                String fileName = internalData + "backup/testShapefile2H2";
                DBSource dbSource = new DBSource("", 0, "gdms", "sa", fileName,
                        "testShapefile2H2", "jdbc:h2");
                File[] database = new File(internalData + "/backup").listFiles(new FileFilter() {

                        public boolean accept(File pathname) {
                                return (pathname.getName().toLowerCase().startsWith("testShapefile2H2"));
                        }
                });
                for (File file : database) {
                        if (!file.delete()) {
                                throw new IOException("Cannot delete h2 tables:"
                                        + file.getAbsolutePath());
                        }
                }

                // register both sources
                String registerDB = "CALL register('h2','" + dbSource.getHost()
                        + "'," + " '" + dbSource.getPort() + "','"
                        + dbSource.getDbName() + "','" + dbSource.getUser() + "','"
                        + dbSource.getPassword() + "'," + "'" + dbSource.getTableName()
                        + "','bati');";
                String registerFile = "CALL register('" + internalData
                        + "landcover2000.shp','parcels');";
                dsf.executeSQL(registerDB);
                dsf.executeSQL(registerFile);

                // Do the migration
                String load = "create table lands as select * " + "from parcels";
                dsf.executeSQL(load);

                // Get each value
                SpatialDataSourceDecorator db = new SpatialDataSourceDecorator(dsf.getDataSource("lands"));
                SpatialDataSourceDecorator file = new SpatialDataSourceDecorator(dsf.getDataSource("parcels"));
                db.open();
                file.open();
                assertEquals(db.getRowCount(),file.getRowCount());
                for (int i = 0; i < db.getRowCount(); i++) {
                        assertTrue(db.getFieldValue(i, db.getSpatialFieldIndex()).equals(
                                file.getFieldValue(i, file.getSpatialFieldIndex())).getAsBoolean());
                }
                db.close();
                file.close();
        }

        private void execute(DBSource dbSource, String statement) throws Exception {
                Class.forName("org.postgresql.Driver").newInstance();
                String connectionString = dbSource.getPrefix() + ":";
                if (dbSource.getHost() != null) {
                        connectionString += "//" + dbSource.getHost();

                        if (dbSource.getPort() != -1) {
                                connectionString += (":" + dbSource.getPort());
                        }
                        connectionString += "/";
                }

                connectionString += (dbSource.getDbName());

                Connection c = DriverManager.getConnection(connectionString, dbSource.getUser(), dbSource.getPassword());

                Statement st = c.createStatement();
                st.execute(statement);
                st.close();
                c.close();
        }
}
