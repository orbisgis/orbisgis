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
import org.gdms.data.types.GeometryTypeConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.gdms.data.types.ConstraintFactory;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

public class DBDriverTest extends TestBase {

        private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        private static SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss");
        private static HashMap<Integer, Value> sampleValues = new HashMap<Integer, Value>();
        private static Constraint[] geometryConstraints = new Constraint[]{
                ConstraintFactory.createConstraint(Constraint.GEOMETRY_TYPE, GeometryTypeConstraint.POINT),
                ConstraintFactory.createConstraint(Constraint.GEOMETRY_TYPE, GeometryTypeConstraint.LINESTRING),
                ConstraintFactory.createConstraint(Constraint.GEOMETRY_TYPE, GeometryTypeConstraint.POLYGON),
                ConstraintFactory.createConstraint(Constraint.GEOMETRY_TYPE, GeometryTypeConstraint.MULTI_POINT),
                ConstraintFactory.createConstraint(Constraint.GEOMETRY_TYPE, GeometryTypeConstraint.MULTI_LINESTRING),
                ConstraintFactory.createConstraint(Constraint.GEOMETRY_TYPE, GeometryTypeConstraint.MULTI_POLYGON), null};

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
//	private DBSource postgreSQLDBSource = new DBSource("127.0.0.1", 5432,
//			 "gdms", "postgres", "postgres", "alltypes", "jdbc:postgresql");
        private DBSource postgreSQLDBSource = new DBSource("127.0.0.1", 5432,
                "GeographicalDataBase", "claudeau", "claudeau", "alltypes", "jdbc:postgresql");
        private DBTestSource postgreSQLSrc = new DBTestSource("source",
                "org.postgresql.Driver", TestBase.internalData
                + "postgresAllTypes.sql", postgreSQLDBSource);
        private DBSource hsqldbDBSource = new DBSource(null, 0,
                TestBase.backupDir + File.separator + "hsqldbAllTypes", "sa",
                null, "alltypes", "jdbc:hsqldb:file");
        private DBTestSource hsqldbSrc = new DBTestSource("source",
                "org.hsqldb.jdbcDriver", TestBase.internalData
                + "hsqldbAllTypes.sql", hsqldbDBSource);
        private DBSource h2DBSource = new DBSource(null, 0, TestBase.backupDir
                + File.separator + "h2AllTypes", "sa", null, "alltypes", "jdbc:h2");
        private DBTestSource h2Src = new DBTestSource("source", "org.h2.Driver",
                TestBase.internalData + "h2AllTypes.sql", h2DBSource);
        private DBSource schemaPostgreSQLDBSource = new DBSource("127.0.0.1", 5432,
                "gisdb", "gis", "gis", "gis_schema", "schema_test", "jdbc:postgresql");
        private DBTestSource schemaPostgreSQLSrc = new DBTestSource("source",
                "org.postgresql.Driver", TestBase.internalData
                + "postgresSchemaTest.sql", schemaPostgreSQLDBSource);
        private DBSource schemaHsqldbDBSource = new DBSource(null, 0,
                TestBase.backupDir + File.separator + "hsqldbSchemaTest", "sa",
                null, "gis_schema", "schema_test", "jdbc:hsqldb:file");
        private DBTestSource schemaHsqldbSrc = new DBTestSource("source",
                "org.hsqldb.jdbcDriver", TestBase.internalData
                + "hsqldbSchemaTest.sql", schemaHsqldbDBSource);
        private DBSource schemaH2DBSource = new DBSource(null, 0, TestBase.backupDir
                + File.separator + "h2SchemaTest", "sa", null, "gis_schema", "schema_test", "jdbc:h2");
        private DBTestSource schemaH2Src = new DBTestSource("source", "org.h2.Driver",
                TestBase.internalData + "h2SchemaTest.sql", schemaH2DBSource);

        private void testReadAllTypes(DBSource dbSource, DBTestSource src)
                throws Exception {
                src.backup();
                readAllTypes();
        }

        private void readAllTypes() throws NoSuchTableException,
                DataSourceCreationException, DriverException,
                NonEditableDataSourceException {
                DataSource ds = TestBase.dsf.getDataSource("source");

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
                assumeTrue(TestBase.postGisAvailable);
                testReadAllTypes(schemaPostgreSQLDBSource, schemaPostgreSQLSrc);
        }

        @Test
        public void testReadSchemaHSQLDB() throws Exception {
                testReadAllTypes(schemaHsqldbDBSource, schemaHsqldbSrc);
        }

        @Test
        public void testReadSchemaH2() throws Exception {
                testReadAllTypes(schemaH2DBSource, schemaH2Src);
        }

        @Test
        public void testReadAllTypesPostgreSQL() throws Exception {
                assumeTrue(TestBase.postGisAvailable);
                testReadAllTypes(postgreSQLDBSource, postgreSQLSrc);
        }

        @Test
        public void testReadAllTypesHSQLDB() throws Exception {
                testReadAllTypes(hsqldbDBSource, hsqldbSrc);
        }

        @Test
        public void testReadAllTypesH2() throws Exception {
                testReadAllTypes(h2DBSource, h2Src);
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
                metadata.addField("f7", Type.INT, ConstraintFactory.createConstraint(Constraint.NOT_NULL),
                        ConstraintFactory.createConstraint(Constraint.AUTO_INCREMENT));
                metadata.addField("f8", Type.LONG);
                metadata.addField("f9", Type.SHORT, ConstraintFactory.createConstraint(Constraint.NOT_NULL));
                metadata.addField("f10", Type.STRING, ConstraintFactory.createConstraint(Constraint.LENGTH, 50));
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
        public void testCreateAllTypesH2() throws Exception {
                assumeTrue(TestBase.h2Available);
                DBTestSource src = new DBTestSource("source", "org.h2.Driver",
                        TestBase.internalData + "removeAllTypes.sql", h2DBSource);
                TestBase.dsf.getSourceManager().removeAll();
                src.backup();
                testCreateAllTypes(h2DBSource, true, false);
        }

        @Test
        public void testCreateAllTypesHSQLDB() throws Exception {
                assumeTrue(TestBase.hsqlDbAvailable);
                DBTestSource src = new DBTestSource("source", "org.hsqldb.jdbcDriver",
                        TestBase.internalData + "removeAllTypes.sql", hsqldbDBSource);
                TestBase.dsf.getSourceManager().removeAll();
                src.backup();
                testCreateAllTypes(hsqldbDBSource, true, true);
        }

        @Test
        public void testCreateAllTypesPostgreSQL() throws Exception {
                assumeTrue(TestBase.postGisAvailable);
                DBTestSource src = new DBTestSource("source", "org.postgresql.Driver",
                        TestBase.internalData + "removeAllTypes.sql",
                        postgreSQLDBSource);
                TestBase.dsf.getSourceManager().removeAll();
                src.backup();
                testCreateAllTypes(postgreSQLDBSource, false, true);
        }

        private void testSQLGeometryConstraint(DBSource dbSource,
                GeometryTypeConstraint geometryConstraint, int dimension)
                throws Exception {
                DefaultMetadata metadata = new DefaultMetadata();
                Constraint[] constraints;
                if (geometryConstraint == null) {
                        constraints = new Constraint[]{ConstraintFactory.createConstraint(
                                Constraint.DIMENSION_3D_GEOMETRY, dimension)};
                } else {
                        constraints = new Constraint[]{geometryConstraint,
                                ConstraintFactory.createConstraint(Constraint.DIMENSION_3D_GEOMETRY, dimension)};
                }
                metadata.addField("f1", Type.GEOMETRY, constraints);
                metadata.addField("f2", Type.INT, ConstraintFactory.createConstraint(Constraint.PK));
                DBSourceCreation dsc = new DBSourceCreation(dbSource, metadata);
                dsf.createDataSource(dsc);

                DataSource ds = dsf.getDataSource(dbSource);
                ds.open();
                int spatialIndex = ds.getFieldIndexByName("f1");
                Metadata met = ds.getMetadata();
                Type spatialType = met.getFieldType(spatialIndex);
                GeometryTypeConstraint gc = (GeometryTypeConstraint) spatialType.getConstraint(Constraint.GEOMETRY_TYPE);
                Dimension3DConstraint dc = (Dimension3DConstraint) spatialType.getConstraint(Constraint.DIMENSION_3D_GEOMETRY);
                assertTrue((gc == null) || (gc.getGeometryType() == geometryConstraint.getGeometryType()));
                assertTrue((dc == null) || dc.getDimension() == dimension);
                ds.close();
        }

        @Test
        public void testPostgreSQLGeometryConstraint() throws Exception {
                assumeTrue(TestBase.postGisAvailable);
                DBTestSource src = new DBTestSource("source", "org.postgresql.Driver",
                        TestBase.internalData + "removeAllTypes.sql",
                        postgreSQLDBSource);
                for (int i = 0; i < geometryConstraints.length; i++) {
                        for (int dim = 2; dim <= 3; dim++) {
                                TestBase.dsf.getSourceManager().removeAll();
                                src.backup();
                                testSQLGeometryConstraint(postgreSQLDBSource, (GeometryTypeConstraint) geometryConstraints[i], dim);
                        }
                }
        }

        @Test
        public void testPostgreSQLRemoveColumnAddColumnSameName() throws Exception {
                assumeTrue(TestBase.postGisAvailable);
                DBTestSource src = postgreSQLSrc;
                TestBase.dsf.getSourceManager().removeAll();
                src.backup();
                DataSource ds = TestBase.dsf.getDataSource(postgreSQLDBSource);
                ds.open();
                ds.addField("the_geom", TypeFactory.createType(Type.GEOMETRY));
                ds.commit();
                ds.close();
                ds.open();
                ds.removeField(ds.getFieldIndexByName("the_geom"));
                ds.commit();
                ds.close();
                ds.open();
                ds.addField("the_geom", TypeFactory.createType(Type.GEOMETRY,
                        ConstraintFactory.createConstraint(Constraint.GEOMETRY_TYPE, GeometryTypeConstraint.POINT)));
                ds.commit();
                ds.close();
        }

        @Test
        public void testPostgreSQLReadWriteAllGeometryTypes() throws Exception {
                assumeTrue(TestBase.postGisAvailable);
                DBTestSource src = new DBTestSource("source", "org.postgresql.Driver",
                        TestBase.internalData + "removeAllTypes.sql",
                        postgreSQLDBSource);
                TestBase.dsf.getSourceManager().removeAll();
                src.backup();

                DefaultMetadata metadata = new DefaultMetadata();
                metadata.addField("f1", Type.GEOMETRY);
                metadata.addField("f2", Type.INT, ConstraintFactory.createConstraint(Constraint.PK),
                        ConstraintFactory.createConstraint(Constraint.AUTO_INCREMENT));
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
                                TypeFactory.createType(Type.STRING, ConstraintFactory.createConstraint(Constraint.PK))},
                        new String[]{"field1"});
                testCommitTwice(dbSource, metadata);
        }

        @Test
        public void testH2CommitTwice() throws Exception {
                assumeTrue(TestBase.h2Available);
                DBSource dbSource = new DBSource(null, -1,
                        "src/test/resources/backup/testH2Commit", "sa", "", "mytable",
                        "jdbc:h2");
                DefaultMetadata metadata = new DefaultMetadata(new Type[]{
                                TypeFactory.createType(Type.STRING, ConstraintFactory.createConstraint(Constraint.PK))},
                        new String[]{"field1"});
                testCommitTwice(dbSource, metadata);
        }

        @Test
        public void testDoublePrimaryKey() throws Exception {
                assumeTrue(TestBase.h2Available);
                DefaultMetadata metadata = new DefaultMetadata(
                        new Type[]{
                                TypeFactory.createType(Type.STRING,
                                ConstraintFactory.createConstraint(Constraint.PK)),
                                TypeFactory.createType(Type.STRING,
                                ConstraintFactory.createConstraint(Constraint.PK))}, new String[]{
                                "field1", "field2"});
                DBSource dbSource = new DBSource(null, -1,
                        "src/test/resources/backup/testH2Commit", "sa", "", "mytable",
                        "jdbc:h2");
                testCommitTwice(dbSource, metadata);
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
                dsf.getSourceManager().register("tototable", dbSource);
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
