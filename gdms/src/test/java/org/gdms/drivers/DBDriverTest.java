/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
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
package org.gdms.drivers;

import org.gdms.data.types.LengthConstraint;
import org.gdms.data.types.AutoIncrementConstraint;
import org.gdms.data.types.NotNullConstraint;
import org.gdms.data.types.PrimaryKeyConstraint;
import org.junit.Test;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.gdms.DBTestSource;
import org.gdms.Geometries;
import org.gdms.TestBase;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBSourceCreation;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Dimension3DConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import static org.junit.Assert.*;
import static org.junit.Assume.*;
import org.junit.Before;
import org.orbisgis.utils.FileUtils;

import org.gdms.TestResourceHandler;

public class DBDriverTest extends TestBase {

        private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        private static final SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss");
        private static final HashMap<Integer, Value> sampleValues = new HashMap<Integer, Value>();
        private static Type[] geometryConstraints = new Type[]{
                TypeFactory.createType(Type.POINT),
                TypeFactory.createType(Type.LINESTRING),
                TypeFactory.createType(Type.POLYGON),
                TypeFactory.createType(Type.MULTIPOINT),
                TypeFactory.createType(Type.MULTILINESTRING),
                TypeFactory.createType(Type.MULTIPOLYGON), null};

        static {
                try {
                        sampleValues.put(Type.BINARY, ValueFactory.createValue(new byte[]{
                                        (byte) 4, (byte) 5, (byte) 6}));
                        sampleValues.put(Type.BOOLEAN, ValueFactory.createValue(true));
                        sampleValues.put(Type.BYTE, ValueFactory.createValue((byte) 200));
                        sampleValues.put(Type.DATE, ValueFactory.createValue(sdf.parse("1980-09-05")));
                        sampleValues.put(Type.DOUBLE, ValueFactory.createValue(4.5d));
                        sampleValues.put(Type.FLOAT, ValueFactory.createValue(4.5f));
                        sampleValues.put(Type.GEOMETRY, ValueFactory.createValue(new GeometryFactory().createPoint(new Coordinate(193, 9285))));
                        sampleValues.put(Type.INT, ValueFactory.createValue(324));
                        sampleValues.put(Type.LONG, ValueFactory.createValue(1290833232L));
                        sampleValues.put(Type.SHORT, ValueFactory.createValue((short) 64000));
                        sampleValues.put(Type.STRING, ValueFactory.createValue("kasdjusk"));
                        sampleValues.put(Type.TIME, ValueFactory.createValue(new Time(stf.parse("15:34:40").getTime())));
                        sampleValues.put(Type.TIMESTAMP, ValueFactory.createValue(Timestamp.valueOf("1980-07-23 15:34:40.2345")));
                } catch (ParseException e) {
                        e.printStackTrace();
                }
        }
	private DBSource postgreSQLDBSource = new DBSource("127.0.0.1", 5432,
                "GeographicalDataBase", "claudeau", "claudeau", "alltypes", "jdbc:postgresql");
        private DBTestSource postgreSQLSrc = new DBTestSource("source",
                "org.postgresql.Driver", TestResourceHandler.OTHERRESOURCES
                + "postgresAllTypes.sql", postgreSQLDBSource);
        private DBSource hsqldbDBSource = new DBSource(null, 0,
                TestResourceHandler.OTHERRESOURCES + "hsqldbAllTypes", "sa",
                null, "alltypes", "jdbc:hsqldb:file");
        private DBTestSource hsqldbSrc;
        private DBSource schemaPostgreSQLDBSource = new DBSource("127.0.0.1", 5432,
                "gisdb", "gis", "gis", "gis_schema", "schema_test", "jdbc:postgresql");
        private DBTestSource schemaPostgreSQLSrc = new DBTestSource("source",
                "org.postgresql.Driver", TestResourceHandler.OTHERRESOURCES
                + "postgresSchemaTest.sql", schemaPostgreSQLDBSource);
        private DBSource schemaHsqldbDBSource = new DBSource(null, 0,
                TestResourceHandler.OTHERRESOURCES + "hsqldbSchemaTest", "sa",
                null, "gis_schema", "schema_test", "jdbc:hsqldb:file");
        private DBTestSource schemaHsqldbSrc;

        @Before
        public void setUp() throws Exception{
                super.setUpTestsWithEdition(false);
        }

        private void testReadAllTypes(DBSource dbSource, DBTestSource src)
                throws Exception {
                src.create(dsf);
                readAllTypes();
        }

        private void readAllTypes() throws NoSuchTableException,
                DataSourceCreationException, DriverException,
                NonEditableDataSourceException {
                DataSource ds = dsf.getDataSource("source");

                ds.open();
                Metadata m = ds.getMetadata();
                Value[] newRow = new Value[m.getFieldCount()];
                for (int i = 0; i < m.getFieldCount(); i++) {
                        Type fieldType = m.getFieldType(i);
                        if (MetadataUtilities.isWritable(fieldType)) {
                                newRow[i] = sampleValues.get(fieldType.getTypeCode());
                        }
                }
                ds.insertFilledRow(newRow);
                Value[] firstRow = ds.getRow(0);
                ds.commit();
                ds.close();
                ds.open();
                Value[] commitedRow = ds.getRow(0);
                ds.commit();
                ds.close();
                ds.open();
                Value[] reCommitedRow = ds.getRow(0);
                assertTrue(equals(reCommitedRow, commitedRow, ds.getMetadata()));
                assertTrue(equals(firstRow, commitedRow, ds.getMetadata()));
                ds.commit();
                ds.close();
        }

        @Test
        public void testReadSchemaPostgreSQL() throws Exception {
                assumeTrue(postGisAvailable);
                testReadAllTypes(schemaPostgreSQLDBSource, schemaPostgreSQLSrc);
        }

        @Test
        public void testReadSchemaHSQLDB() throws Exception {
                assumeTrue(hsqlDbAvailable);
                testReadAllTypes(schemaHsqldbDBSource, schemaHsqldbSrc);
        }

        @Test
        public void testReadAllTypesPostgreSQL() throws Exception {
                assumeTrue(postGisAvailable);
                testReadAllTypes(postgreSQLDBSource, postgreSQLSrc);
        }

        @Test
        public void testReadAllTypesHSQLDB() throws Exception {
                assumeTrue(hsqlDbAvailable);
                testReadAllTypes(hsqldbDBSource, hsqldbSrc);
        }

        @Test
        public void testDBSourceClone() throws Exception {
                DBSource cloned = schemaPostgreSQLDBSource.clone();
                assertTrue(cloned!=schemaPostgreSQLDBSource);
                assertTrue(cloned.equals(schemaPostgreSQLDBSource));
        }

        private void testCreateAllTypes(DBSource dbSource, boolean byte_,
                boolean stringLength) throws Exception {
                DefaultMetadata metadata = new DefaultMetadata();
                metadata.addField("f1", Type.BINARY);
                metadata.addField("f2", Type.BOOLEAN);
                if (byte_) {
                        metadata.addField("f3", Type.BYTE);
                }
                metadata.addField("f4", Type.DATE);
                metadata.addField("f5", Type.DOUBLE);
                metadata.addField("f6", Type.FLOAT);
                metadata.addField("f7", Type.INT, new NotNullConstraint(),
                        new AutoIncrementConstraint());
                metadata.addField("f8", Type.LONG);
                metadata.addField("f9", Type.SHORT, new NotNullConstraint());
                metadata.addField("f10", Type.STRING, new LengthConstraint(50));
                metadata.addField("f11", Type.TIME);
                metadata.addField("f12", Type.TIMESTAMP);

                DBSourceCreation dsc = new DBSourceCreation(dbSource, metadata);
                dsf.createDataSource(dsc);
                readAllTypes();

                DataSource ds = dsf.getDataSource("source");
                ds.open();
                if (stringLength) {
                        assertTrue(check("f10", Constraint.LENGTH, "50", ds));
                }
                assertTrue(check("f9", Constraint.NOT_NULL, ds));
                assertTrue(check("f7", Constraint.NOT_NULL, ds));
                assertTrue(check("f7", Constraint.AUTO_INCREMENT, ds));
                assertTrue(check("f7", Constraint.PK, ds));
                assertTrue(check("f7", Constraint.READONLY, ds));
        }

        private boolean check(String fieldName, int constraint, String value,
                DataSource ds) throws DriverException {
                int fieldId = ds.getFieldIndexByName(fieldName);
                Type type = ds.getMetadata().getFieldType(fieldId);
                return (type.getConstraintValue(constraint).equals(value));
        }

        private boolean check(String fieldName, int constraint, DataSource ds)
                throws DriverException {
                int fieldId = ds.getFieldIndexByName(fieldName);
                Type type = ds.getMetadata().getFieldType(fieldId);
                return (type.getConstraint(constraint) != null);
        }

        @Test
        public void testCreateAllTypesHSQLDB() throws Exception {
                assumeTrue(TestBase.hsqlDbAvailable);
                DBTestSource src = new DBTestSource("source", "org.hsqldb.jdbcDriver",
                        TestResourceHandler.TESTRESOURCES + "removeAllTypes.sql", hsqldbDBSource);
                src.create(dsf);
                testCreateAllTypes(hsqldbDBSource, true, true);
        }

        @Test
        public void testCreateAllTypesPostgreSQL() throws Exception {
                assumeTrue(TestBase.postGisAvailable);
                DBTestSource src = new DBTestSource("source", "org.postgresql.Driver",
                        TestResourceHandler.TESTRESOURCES + "removeAllTypes.sql",
                        postgreSQLDBSource);
                src.create(dsf);
                testCreateAllTypes(postgreSQLDBSource, false, true);
        }

        private void testSQLGeometryConstraint(DBSource dbSource,
                Type geometryConstraint, int dimension)
                throws Exception {
                DefaultMetadata metadata = new DefaultMetadata();
                Constraint[] constraints;
                constraints = new Constraint[]{new Dimension3DConstraint(dimension)};
                metadata.addField("f1", Type.GEOMETRY, constraints);
                metadata.addField("f2", Type.INT, new PrimaryKeyConstraint());
                DBSourceCreation dsc = new DBSourceCreation(dbSource, metadata);
                dsf.createDataSource(dsc);

                DataSource ds = dsf.getDataSource(dbSource);
                ds.open();
                int spatialIndex = ds.getFieldIndexByName("f1");
                Metadata met = ds.getMetadata();
                Type spatialType = met.getFieldType(spatialIndex);
                Dimension3DConstraint dc = (Dimension3DConstraint) spatialType.getConstraint(Constraint.DIMENSION_3D_GEOMETRY);
                assertTrue((spatialType== null) || (spatialType.getTypeCode() == geometryConstraint.getTypeCode()));
                assertTrue((dc == null) || dc.getDimension() == dimension);
                ds.close();
        }

        @Test
        public void testPostgreSQLGeometryConstraint() throws Exception {
                assumeTrue(TestBase.postGisAvailable);
                DBTestSource src = new DBTestSource("source", "org.postgresql.Driver",
                        TestResourceHandler.TESTRESOURCES + "removeAllTypes.sql",
                        postgreSQLDBSource);
                for (int i = 0; i < geometryConstraints.length; i++) {
                        for (int dim = 2; dim <= 3; dim++) {
                                src.create(dsf);
                                testSQLGeometryConstraint(postgreSQLDBSource, geometryConstraints[i], dim);
                        }
                }
        }

        @Test
        public void testPostgreSQLRemoveColumnAddColumnSameName() throws Exception {
                assumeTrue(TestBase.postGisAvailable);
                DBTestSource src = postgreSQLSrc;
                src.create(dsf);
                DataSource ds = dsf.getDataSource(postgreSQLDBSource);
                ds.open();
                ds.addField("the_geom", TypeFactory.createType(Type.GEOMETRY));
                ds.commit();
                ds.close();
                ds.open();
                ds.removeField(ds.getFieldIndexByName("the_geom"));
                ds.commit();
                ds.close();
                ds.open();
                ds.addField("the_geom", TypeFactory.createType(Type.POINT));
                ds.commit();
                ds.close();
        }

        @Test
        public void testPostgreSQLReadWriteAllGeometryTypes() throws Exception {
                assumeTrue(TestBase.postGisAvailable);
                DBTestSource src = new DBTestSource("source", "org.postgresql.Driver",
                        TestResourceHandler.TESTRESOURCES + "removeAllTypes.sql",
                        postgreSQLDBSource);
                src.create(dsf);

                DefaultMetadata metadata = new DefaultMetadata();
                metadata.addField("f1", Type.GEOMETRY);
                metadata.addField("f2", Type.INT, new PrimaryKeyConstraint(),
                        new AutoIncrementConstraint());
                DBSourceCreation dsc = new DBSourceCreation(postgreSQLDBSource,
                        metadata);
                dsf.createDataSource(dsc);
                DataSource ds = dsf.getDataSource(postgreSQLDBSource);
                ds.open();
                int spatialIndex = ds.getFieldIndexByName("f1");
                Geometry[] geometries = new Geometry[]{Geometries.getPoint3D(),
                        Geometries.getLineString3D(), Geometries.getPolygon3D(),
                        Geometries.getMultiPoint3D(),
                        Geometries.getMultilineString3D(),
                        Geometries.getMultiPolygon3D()};
                for (int i = 0; i < geometries.length; i++) {
                        ds.insertEmptyRow();
                        Value geom = ValueFactory.createValue(geometries[i]);
                        ds.setFieldValue(ds.getRowCount() - 1, spatialIndex, geom);
                }

                ds.commit();
                ds.close();

                ds.open();
                for (int i = 0; i < geometries.length; i++) {
                        assertEquals(geometries[i], ds.getGeometry(i));
                }
                ds.close();
        }

        @Test
        public void testHSQLDBCommitTwice() throws Exception {
                assumeTrue(TestBase.hsqlDbAvailable);
                DBSource dbSource = new DBSource(null, -1,
                        "src/test/resources/backup/testHSQLDBCommit", "sa", "",
                        "mytable", "jdbc:hsqldb:file");
                DefaultMetadata metadata = new DefaultMetadata(new Type[]{
                                TypeFactory.createType(Type.STRING, new PrimaryKeyConstraint())},
                        new String[]{"field1"});
                testCommitTwice(dbSource, metadata);
        }
        
        @Test
        public void testShapefile2PostgreSQL() throws Exception {
                assumeTrue(TestBase.postGisAvailable);
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
                String registerFile = "CALL register('" 
                        + new File(TestResourceHandler.TESTRESOURCES, "landcover2000.shp").getAbsolutePath()
                        + "','parcels');";
                dsf.executeSQL(registerDB);
                dsf.executeSQL(registerFile);

                // Do the migration
                String load = "create table lands as select * " + "from parcels;";
                dsf.executeSQL(load);

                // Get each value
                DataSource db = dsf.getDataSource("lands");
                DataSource file = dsf.getDataSource("parcels");
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
                assumeTrue(TestBase.postGisAvailable);
                DBSource dbSource = new DBSource("127.0.0.1", 5432,
                        "gisdb", "gis", "gis", "gis_schema",
                        "administratif", "jdbc:postgresql");

                sm.register("data_source", dbSource);

                dsf.executeSQL("select * from data_source ; ");
        }

        @Test
        public void testReadMultiSchemasPostGreSQL() throws Exception {
                assumeTrue(TestBase.postGisAvailable);
                DBSource publicSchemaDbSource = new DBSource("localhost", 5432,
                        "gisdb", "gis", "gis",
                        "landcover2000", "jdbc:postgresql");

                String publicSchemaSourceName = sm.getUniqueName(publicSchemaDbSource.getTableName());
                sm.register(publicSchemaSourceName, publicSchemaDbSource);

                DBSource otherSchemaDbSource = new DBSource("localhost", 5432,
                        "gisdb", "gis", "gis", "gis_schema",
                        "parcels", "jdbc:postgresql");
                String otherSchemaSourceName = sm.getUniqueName(otherSchemaDbSource.getTableName());
                sm.register(otherSchemaSourceName, otherSchemaDbSource);

                DataSource sds = dsf.getDataSource(otherSchemaDbSource);
                sds.open();
                sds.isVectorial();
                sds.close();

                assertFalse(otherSchemaSourceName.equals(publicSchemaSourceName));

                dsf.executeSQL("select * from " + publicSchemaSourceName + " ;");

                dsf.executeSQL("select * from " + otherSchemaSourceName + " ;");

        }

        private void testCommitTwice(DBSource dbSource, Metadata metadata)
                throws Exception, DataSourceCreationException,
                NonEditableDataSourceException {
                try {
                        execute(dbSource, "drop table \"mytable\";");
                } catch (SQLException e) {
                        // ignore, something else will fail
                }
                dsf.createDataSource(new DBSourceCreation(dbSource, metadata));
                sm.register("tototable", dbSource);
                DataSource ds = dsf.getDataSource(dbSource);
                ds.open();
                Value[] row = new Value[metadata.getFieldCount()];
                for (int i = 0; i < row.length; i++) {
                        row[i] = ValueFactory.createValue("value");
                }
                ds.insertFilledRow(row);
                ds.commit();
                ds.deleteRow(0);
                ds.commit();
                ds.close();
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
