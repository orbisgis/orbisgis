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
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
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
package org.gdms.sql.strategies;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.io.WKTReader;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

import org.gdms.Geometries;
import org.gdms.TestBase;
import org.gdms.TestResourceHandler;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.DigestUtilities;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.IncompatibleTypesException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.data.values.ValueWriter;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.gdms.sql.engine.ParseException;
import org.gdms.sql.engine.SemanticException;

public class SQLTest extends TestBase {

        private static final String SHPTABLE = "landcover2000";

        @Test
        public void testInsertWithFunction() throws Exception {
                DefaultMetadata metadata = new DefaultMetadata();
                metadata.addField("f1", Type.INT);
                dsf.getSourceManager().register("source",
                        new MemoryDataSetDriver(metadata));
                dsf.executeSQL("insert into source (f1) values (abs('2' :: int));");
        }

        @Test
        public void testInsertWithSelect() throws Exception {

                DefaultMetadata metadata = new DefaultMetadata();
                metadata.addField("f1", Type.DOUBLE);
                dsf.getSourceManager().register("source",
                        new MemoryDataSetDriver(metadata));
                dsf.executeSQL("insert into source (f1) select runoff_win from " + SHPTABLE + ";");
        }

        @Test
        public void testCreateAsTableCustomQuery() throws Exception {
                dsf.executeSQL("create table custom as select * from st_explode(select * from "
                        + SHPTABLE + " where gid > 12);");

        }

        @Test
        public void testCaseInsensitiveness() throws Exception {
                dsf.executeSQL("seLECt st_BuffER(the_geom, 20) From " + SHPTABLE + ";");
                String path = TestResourceHandler.TESTRESOURCES + "/points.shp";
                dsf.executeSQL("CaLL REGisteR('" + path + "');");
        }

        @Test
        public void testDropColumn() throws Exception {
                dsf.getSourceManager().remove("temp");
                dsf.executeSQL("create table temp as select * from "
                        + SHPTABLE + ";");
                dsf.executeSQL("alter table temp drop column \"type\";");

        }

        @Test
        public void testDropColumnIfExits() throws Exception {
                dsf.getSourceManager().remove("temp");
                dsf.executeSQL("create table temp as select * from "
                        + SHPTABLE + ";");
                // should work: type exists
                dsf.executeSQL("alter table temp drop column if exists \"type\";");
                // should work: it does not exist anymore, but "if exists" is there
                dsf.executeSQL("alter table temp drop column if exists \"type\";");
                // should fail: no "if exists" an non-existant column
                try {
                        dsf.executeSQL("alter table temp drop column \"type\";");
                        fail();
                } catch (SemanticException e) {
                }
        }

        @Test
        public void testDeleteTable() throws Exception {
                dsf.getSourceManager().remove("temp");
                dsf.executeSQL("create table temp as select * from "
                        + SHPTABLE + ";");
                dsf.executeSQL("delete from temp where gid = 1;");

                DataSource ds = dsf.getDataSource("temp");

                ds.open();
                int fieldIndex = ds.getFieldIndexByName("gid");
                for (int i = 0; i < ds.getRowCount(); i++) {
                        int value = ds.getFieldValue(i, fieldIndex).getAsInt();
                        assertFalse(value == 1);

                }
                ds.close();
        }

        @Test
        public void testDeleteExistsTable() throws Exception {
                dsf.getSourceManager().remove("temp");
                dsf.executeSQL("create table temp as select * from "
                        + SHPTABLE + ";");
                dsf.executeSQL("create table centroid as select gid, st_centroid(the_geom) as the_geom from "
                        + SHPTABLE + ";");
                dsf.executeSQL("delete from temp where exists (select a.gid from centroid a, temp b where st_intersects(a.the_geom, b.the_geom));");

                DataSource ds = dsf.getDataSource("temp");

                ds.open();
                assertEquals(ds.getRowCount(), 0);
                ds.close();

        }

        @Test
        public void testDropTablePurge() throws Exception {
                dsf.getSourceManager().remove("temp");
                dsf.executeSQL("create table temp as select * from "
                        + SHPTABLE + ";");
                dsf.executeSQL("drop table temp purge;");
                dsf.executeSQL("create table tatoche as select * from "
                        + SHPTABLE + ";");
                dsf.executeSQL("drop table tatoche purge;");
        }

        @Test
        public void testDropTable() throws Exception {
                dsf.executeSQL("drop table " + SHPTABLE + ";");
        }

        @Test
        public void testDropTableIfExists() throws Exception {
                dsf.executeSQL("drop table if exists toto;");
                assertFalse(dsf.getSourceManager().exists("toto"));
                assertTrue(dsf.getSourceManager().exists("landcover2000"));
                dsf.executeSQL("drop table if exists landcover2000;");
                assertFalse(dsf.getSourceManager().exists("landcover2000"));

        }

        @Test
        public void testRenameTable() throws Exception {
                dsf.executeSQL("alter table landcover2000 rename to erwan;");
                assertTrue(dsf.getSourceManager().exists("erwan"));
        }

        @Test
        public void testDropSchema() throws Exception {
                dsf.executeSQL("alter table landcover2000 rename to toto.erwan;");
                dsf.executeSQL("drop schema toto;");
                assertFalse(dsf.getSourceManager().exists("toto.erwan"));
                assertFalse(dsf.getSourceManager().schemaExists("toto"));
        }

        @Test
        public void testRenameColumn() throws Exception {
                dsf.executeSQL("create table diwall as select *  from landcover2000;");
                dsf.executeSQL("alter table diwall rename column \"type\" to erwan;");

                DataSource ds = dsf.getDataSource("diwall");
                ds.open();
                Metadata metadata = ds.getMetadata();
                assertTrue(metadata.getFieldIndex("erwan") != -1);
                ds.close();
        }

        @Test
        public void testRenameColumnExists() throws Exception {
                dsf.executeSQL("create table temp as select *  from " + SHPTABLE + ";");

                // should fail
                try {
                        dsf.executeSQL("alter table temp rename column \"type\" to \"type\";");
                        fail();
                } catch (Exception e) {
                }
        }

        @Test
        public void testAddColumn() throws Exception {
                dsf.executeSQL("create table temp as select *  from " + SHPTABLE + ";");
                dsf.executeSQL("alter table temp add column gwen text;");
        }

        @Test
        public void testAlterColumn() throws Exception {
                dsf.executeSQL("CREATE TABLE temp as select *, autonumeric() as cons from " + SHPTABLE + ";");
                dsf.executeSQL("ALTER TABLE temp ALTER cons TYPE double;");
                DataSource d = dsf.getDataSource("temp");
                d.open();
                assertEquals(Type.DOUBLE, d.getFieldType(d.getFieldIndexByName("cons")).getTypeCode());
                assertEquals(4, d.getDouble(4, "cons"), 1e-15);
                assertFalse(d.isNull(0, "cons"));
                d.close();

                // no implicit conversion from double to int: this should fail
                try {
                        dsf.executeSQL("ALTER TABLE temp ALTER cons TYPE int;");
                        fail();
                } catch (IncompatibleTypesException ex) {
                }
        }

        @Test
        public void testAlterColumnWithExpr() throws Exception {
                dsf.executeSQL("CREATE TABLE temp as select *, autonumeric() as cons from " + SHPTABLE + ";");
                dsf.executeSQL("ALTER TABLE temp ALTER cons TYPE double USING cons + ceil(12);");
                DataSource d = dsf.getDataSource("temp");
                d.open();
                assertEquals(Type.DOUBLE, d.getFieldType(d.getFieldIndexByName("cons")).getTypeCode());
                assertEquals(16, d.getDouble(4, d.getFieldIndexByName("cons")), 1e-15);
                d.close();

                // no implicit conversion from text to double: this should fail
                try {
                        dsf.executeSQL("ALTER TABLE temp ALTER cons TYPE double USING 12 :: text;");
                        fail();
                } catch (IncompatibleTypesException ex) {
                }
        }

        @Test
        public void testAddDuplicateColumn() throws Exception {

                dsf.executeSQL("create table temp as select * from " + SHPTABLE + ";");
                dsf.executeSQL("alter table temp add column gwen text;");

                // should fail
                try {
                        dsf.executeSQL("alter table temp add column gwen text;");
                        fail();
                } catch (Exception e) {
                }
        }

        @Test
        public void testExcept() throws Exception {
                dsf.executeSQL("create table temp as select * except \"type\" from " + SHPTABLE + ";");
                DataSource dsOut = dsf.getDataSource("temp");
                dsOut.open();
                assertEquals(dsOut.getFieldIndexByName("type"), -1);
                dsOut.close();
        }

        @Test
        public void testExceptList() throws Exception {
                dsf.executeSQL("create table temp as select * except (\"type\", the_geom) from landcover2000;");
                DataSource dsOut = dsf.getDataSource("temp");
                dsOut.open();
                assertEquals(-1, dsOut.getFieldIndexByName("type"));
                assertEquals(-1, dsOut.getFieldIndexByName("the_geom"));
                dsOut.close();
        }

        @Test
        public void testExceptAlias() throws Exception {
                dsf.executeSQL("create table temp as select a.* except the_geom from " + SHPTABLE + " a;");
                DataSource dsOut = dsf.getDataSource("temp");
                dsOut.open();
                assertEquals(dsOut.getFieldIndexByName("the_geom"), -1);
                dsOut.close();
        }

        @Ignore("We have no test resource with NULL values in them... yet!")
        @Test
        public void testIsClause() throws Exception {
                String fieldName = "somefield";
                DataSource d = dsf.getDataSourceFromSQL("select * from " + SHPTABLE
                        + " where " + fieldName + " is not null;");
                d.open();
                int index = d.getFieldIndexByName(fieldName);
                for (int i = 0; i < d.getRowCount(); i++) {
                        assertFalse(d.isNull(i, index));
                }
                d.close();
        }

        @Test
        public void testBetweenClause() throws Exception {
                String ds = SHPTABLE;
                DataSource d = dsf.getDataSourceFromSQL("SELECT MIN(runoff_win), MAX(runoff_win) FROM " + SHPTABLE + ";");
                d.open();
                double low = d.getDouble(0, 0);
                double high = d.getDouble(0, 1);
                d.close();


                String numericField = "runoff_win";
                d = dsf.getDataSourceFromSQL("select * from " + ds
                        + " where " + numericField + " between " + low + " and " + high
                        + ";");
                d.open();
                for (int i = 0; i < d.getRowCount(); i++) {
                        double fieldValue = d.getDouble(i, numericField);
                        assertTrue((low <= fieldValue) && (fieldValue <= high));
                }
                d.close();
        }

        @Test
        public void testInClause() throws Exception {
                dsf.executeSQL("CREATE TABLE testIn AS SELECT * FROM VALUES (1), (3), (5) as toto;");
                dsf.executeSQL("CREATE TABLE testIn2 AS SELECT * FROM VALUES (1), (2), (3), (4), (5) as toto;");
                DataSource d = dsf.getDataSourceFromSQL("SELECT * FROM testIn2 WHERE testIn2.exp0 IN (SELECT * FROM testIn);");
                d.open();
                assertEquals(3, d.getRowCount());
                assertEquals(1, d.getInt(0, 0));
                assertEquals(3, d.getInt(1, 0));
                assertEquals(5, d.getInt(2, 0));
                d.close();

                dsf.getSourceManager().delete("testIn");
                dsf.getSourceManager().delete("testIn2");
        }

        @Test
        public void testCorrelatedInClause() throws Exception {
                dsf.executeSQL("CREATE TABLE testIn AS SELECT * FROM VALUES (1), (3), (5) as toto;");
                dsf.executeSQL("CREATE TABLE testIn2 AS SELECT * FROM VALUES (1), (2), (3), (4), (5) as toto;");
                DataSource d = dsf.getDataSourceFromSQL("SELECT * FROM testIn2 WHERE EXISTS ("
                        + "SELECT 1 FROM testIn WHERE testIn.exp0 = testIn2.exp0);");
                d.open();
                assertEquals(3, d.getRowCount());
                assertEquals(1, d.getInt(0, 0));
                assertEquals(3, d.getInt(1, 0));
                assertEquals(5, d.getInt(2, 0));
                d.close();

                dsf.getSourceManager().delete("testIn");
                dsf.getSourceManager().delete("testIn2");
        }

        @Test
        public void testInClauseWithNull() throws Exception {
                dsf.executeSQL("CREATE TABLE testIn AS SELECT NULL :: int AS toto, 42 AS tutu;");
                DataSource d = dsf.getDataSourceFromSQL("SELECT 1 IN (SELECT toto FROM testIn);");
                d.open();
                assertEquals(1, d.getRowCount());
                assertTrue(d.getFieldValue(0, 0).isNull());
                d.close();

                dsf.executeSQL("UPDATE testIn SET toto = 1;");

                d = dsf.getDataSourceFromSQL("SELECT 1 IN (SELECT toto FROM testIn);");
                d.open();
                assertEquals(1, d.getRowCount());
                assertTrue(d.getBoolean(0, 0));
                d.close();

                dsf.getSourceManager().delete("testIn");
        }

        @Test
        public void testSimpleScalarSubQuery() throws Exception {
                DataSource d = dsf.getDataSourceFromSQL(" SELECT (SELECT * FROM VALUES (1) as toto);");
                d.open();
                assertEquals(1, d.getFieldCount());
                assertEquals(1, d.getRowCount());
                assertEquals(1, d.getInt(0, 0));
                d.close();
        }

        @Test
        public void testScalarSubquery() throws Exception {
                dsf.executeSQL("CREATE TABLE testIn AS SELECT * FROM VALUES (1), (3), (5) as toto;");
                dsf.executeSQL("CREATE TABLE testIn2 AS SELECT * FROM VALUES (1), (2), (3), (4), (5) as toto;");
                DataSource d = dsf.getDataSourceFromSQL("SELECT * FROM testIn WHERE exp0 = ("
                        + "SELECT testIn2.exp0 FROM testIn2 ORDER BY testIn2.exp0 DESC LIMIT 1);");
                d.open();
                assertEquals(1, d.getRowCount());
                assertEquals(1, d.getFieldCount());
                assertEquals(5, d.getInt(0, 0));
                d.close();
        }

        @Test
        public void testCorrelatedScalarSubquery() throws Exception {
                dsf.executeSQL("CREATE TABLE testIn AS SELECT * FROM VALUES (1, 'hi'), (3, 'hi2'), (5, 'hi3') as toto;");
                dsf.executeSQL("CREATE TABLE testIn2 AS SELECT * FROM VALUES (1, 'hi'), (2, 'hi2'), (3, 'hi'), (4, 'hi3'"
                        + "), (5, 'hi4') as toto;");
                DataSource d = dsf.getDataSourceFromSQL("SELECT exp0 FROM testIn WHERE exp0 >= ("
                        + "SELECT testIn2.exp0 FROM testIn2 WHERE testIn2.exp1 = testIn.exp1 "
                        + "ORDER BY testIn2.exp0 DESC LIMIT 1);");
                // result shoud be:
                // 3
                // 5
                d.open();
                assertEquals(2, d.getRowCount());
                assertEquals(1, d.getFieldCount());
                assertEquals(3, d.getInt(0, 0));
                assertEquals(5, d.getInt(1, 0));
                d.close();
        }

        @Test
        public void testCorrelatedScalarSubqueryInUpdate() throws Exception {
                dsf.executeSQL("CREATE TABLE testIn AS SELECT * FROM VALUES (1, 'hi'), (3, 'hi2'), (5, 'hi3') as toto;");
                dsf.executeSQL("UPDATE testIn SET exp1 = 'hello' WHERE "
                        + "exp0 = (SELECT max(a.exp0) FROM testIn AS a);");
                DataSource d = dsf.getDataSource("testIn");
                d.open();
                assertEquals("hi", d.getString(0, 1));
                assertEquals("hi2", d.getString(1, 1));
                assertEquals("hello", d.getString(2, 1));
                d.close();
        }

        @Test
        public void testAggregate() throws Exception {
                String ds = SHPTABLE;
                DataSource d = dsf.getDataSourceFromSQL("SELECT MIN(runoff_win), MAX(runoff_win) FROM " + SHPTABLE + ";");
                d.open();
                double low = d.getDouble(0, 0);
                double high = d.getDouble(0, 1);
                d.close();

                String numericField = "runoff_win";
                d = dsf.getDataSourceFromSQL("select count(" + numericField
                        + ") from " + ds + " where " + numericField + " < " + high
                        + ";");

                DataSource original = dsf.getDataSource(ds);
                original.open();
                int count = 0;
                for (int i = 0; i < original.getRowCount(); i++) {
                        double fieldValue = original.getDouble(i, numericField);
                        if (fieldValue < high) {
                                count++;
                        }
                }
                original.close();

                d.open();
                assertEquals(count, d.getDouble(0, 0), 0);
                d.close();
        }

        @Test
        public void testTwoTimesTheSameAggregate() throws Exception {
                String ds = SHPTABLE;
                DataSource d = dsf.getDataSourceFromSQL("SELECT MIN(runoff_win), MAX(runoff_win) FROM " + SHPTABLE + ";");
                d.open();
                double low = d.getDouble(0, 0);
                double high = d.getDouble(0, 1);
                d.close();

                String numericField = "runoff_win";

                d = dsf.getDataSourceFromSQL("select count(" + numericField
                        + "), count(" + numericField + ") from " + ds + " where "
                        + numericField + " < " + high + ";");

                d.open();
                assertTrue(equals(d.getFieldValue(0, 0), d.getFieldValue(0, 1)));
                d.close();
        }

        @Test
        public void testOrderByFunction() throws Exception {
                DataSource resultDataSource = dsf.getDataSourceFromSQL("select * from "
                        + SHPTABLE + " order by ST_Area(the_geom);");
                resultDataSource.open();
                assertTrue(resultDataSource.getRowCount() > 0);
                resultDataSource.close();
        }

        @Test
        public void testOrderByAsc() throws Exception {
                String fieldName = "runoff_win";
                String sql = "select * from " + SHPTABLE + " order by \"" + fieldName + "\" asc;";

                DataSource resultDataSource = dsf.getDataSourceFromSQL(sql);
                resultDataSource.open();
                int fieldIndex = resultDataSource.getFieldIndexByName(fieldName);
                for (int i = 1; i < resultDataSource.getRowCount(); i++) {
                        Value v1 = resultDataSource.getFieldValue(i - 1, fieldIndex);
                        Value v2 = resultDataSource.getFieldValue(i, fieldIndex);
                        if (v1.getType() != Type.NULL) {
                                assertTrue(v1.lessEqual(v2).getAsBoolean());
                        }
                }
                resultDataSource.close();
        }

        @Test
        public void testOrderByDesc() throws Exception {
                String fieldName = "runoff_win";
                String sql = "select * from " + SHPTABLE + " order by \"" + fieldName
                        + "\" desc;";
                DataSource resultDataSource = dsf.getDataSourceFromSQL(sql);
                resultDataSource.open();
                int fieldIndex = resultDataSource.getFieldIndexByName(fieldName);
                for (int i = 1; i < resultDataSource.getRowCount(); i++) {
                        Value v1 = resultDataSource.getFieldValue(i - 1, fieldIndex);
                        Value v2 = resultDataSource.getFieldValue(i, fieldIndex);
                        if (v2.getType() != Type.NULL) {
                                if (!v1.greaterEqual(v2).getAsBoolean()) {
                                }
                                assertTrue(v1.greaterEqual(v2).getAsBoolean());
                        }
                }
                resultDataSource.close();
        }

        @Ignore("We have no test resource with NULL values")
        @Test
        public void testOrderByWithNullValues() throws Exception {
                String fieldName = "somefield";
                String sql = "select * from " + SHPTABLE + " order by \"" + fieldName + "\" asc;";

                DataSource resultDataSource = dsf.getDataSourceFromSQL(sql);
                resultDataSource.open();
                int fieldIndex = resultDataSource.getFieldIndexByName(fieldName);
                boolean[] nullValues = new boolean[(int) resultDataSource.getRowCount()];
                for (int i = 1; i < resultDataSource.getRowCount(); i++) {
                        if (resultDataSource.isNull(i, fieldIndex)) {
                                nullValues[i] = true;
                        } else {
                                nullValues[i] = false;
                                if (!resultDataSource.isNull(i - 1, fieldIndex)) {
                                        Value v1 = resultDataSource.getFieldValue(i - 1, fieldIndex);
                                        Value v2 = resultDataSource.getFieldValue(i, fieldIndex);
                                        assertTrue(v1.lessEqual(v2).getAsBoolean());
                                }
                        }
                }
                nullValues[0] = resultDataSource.isNull(0, fieldIndex);

                for (int i = 1; i < nullValues.length - 1; i++) {
                        assertFalse("All null together", !nullValues[i]
                                && nullValues[i - 1] && nullValues[i + 1]);
                }
                resultDataSource.close();
        }

        @Test
        public void testOrderByMultipleFields() throws Exception {
                String ds = "hedgerow";
                dsf.getSourceManager().register(ds, new File(TestResourceHandler.TESTRESOURCES, "hedgerow.shp"));
                String sql = "select * from " + ds + " order by \"type\", gid asc;";

                DataSource resultDataSource = dsf.getDataSourceFromSQL(sql);
                resultDataSource.open();
                int fieldIndex1 = resultDataSource.getFieldIndexByName("type");
                int fieldIndex2 = resultDataSource.getFieldIndexByName("gid");
                for (int i = 1; i < resultDataSource.getRowCount(); i++) {
                        Value v1 = resultDataSource.getFieldValue(i - 1, fieldIndex1);
                        Value v2 = resultDataSource.getFieldValue(i, fieldIndex1);
                        if (v1.less(v2).getAsBoolean()) {
                        } else {
                                v1 = resultDataSource.getFieldValue(i - 1, fieldIndex2);
                                v2 = resultDataSource.getFieldValue(i, fieldIndex2);
                                assertTrue(v1.lessEqual(v2).getAsBoolean());
                        }
                }
                resultDataSource.close();

        }

        private void testDistinct(String ds) throws Exception {
                String[] fields = super.getFieldNames(ds);
                DataSource d = dsf.getDataSourceFromSQL("select distinct " + fields[0]
                        + " from " + ds + " ;");

                d.open();
                int fieldIndex = d.getFieldIndexByName(fields[0]);
                Set<Value> valueSet = new HashSet<Value>();
                for (int i = 0; i < d.getRowCount(); i++) {
                        assertFalse(valueSet.contains(d.getFieldValue(i, fieldIndex)));
                        valueSet.add(d.getFieldValue(i, fieldIndex));
                }
                d.close();
        }

        @Test
        public void testDistinct() throws Exception {
                String resource = TestResourceHandler.getFilesWithRepeatedRows().iterator().next();

                String name = sm.nameAndRegister(new File(TestResourceHandler.TESTRESOURCES, resource));
                testDistinct(name);
                sm.delete(name);

        }

        @Test
        public void regressionTest611() throws Exception {
                dsf.executeSQL("CREATE TABLE test AS SELECT * FROM VALUES (1.0), (1.1) a;");

                DataSource d = dsf.getDataSourceFromSQL("SELECT DISTINCT exp0 FROM test;");
                d.open();
                assertEquals(2, d.getRowCount());
                d.close();
        }

        @Test
        public void testDistinctOnOneFieldCase() throws Exception {
                dsf.getSourceManager().register("hedgerow", new File(TestResourceHandler.TESTRESOURCES, "hedgerow.shp"));
                DataSource d = dsf.getDataSourceFromSQL("select distinct \"type\" from hedgerow ;");

                d.open();
                int fieldIndex = d.getFieldIndexByName("type");
                Set<Value> valueSet = new HashSet<Value>();
                for (int i = 0; i < d.getRowCount(); i++) {
                        assertFalse(valueSet.contains(d.getFieldValue(i, fieldIndex)));
                        valueSet.add(d.getFieldValue(i, fieldIndex));
                }
                d.close();
        }

        private void testDistinctManyFields(String ds) throws Exception {
                String[] fields = super.getFieldNames(ds);
                DataSource d = dsf.getDataSourceFromSQL("select distinct " + fields[0]
                        + ", " + fields[1] + " from " + ds + " ;");

                d.open();
                int fieldIndex1 = d.getFieldIndexByName(fields[0]);
                int fieldIndex2 = d.getFieldIndexByName(fields[1]);
                Set<Value> valueSet = new HashSet<Value>();
                for (int i = 0; i < fields.length; i++) {
                        Value v1 = d.getFieldValue(i, fieldIndex1);
                        Value v2 = d.getFieldValue(i, fieldIndex2);
                        Value col = ValueFactory.createValue(new Value[]{v1, v2});
                        assertFalse(valueSet.contains(col));
                        valueSet.add(col);
                }
                d.close();
        }

        @Test
        public void testDistinctManyFields() throws Exception {
                String resource = TestResourceHandler.getFilesWithRepeatedRows().iterator().next();
                String name = sm.nameAndRegister(new File(TestResourceHandler.TESTRESOURCES, resource));
                testDistinctManyFields(name);
                sm.delete(name);
        }

        private void testDistinctAllFields(String ds) throws Exception {
                String[] fields = super.getFieldNames(ds);
                DataSource d = dsf.getDataSourceFromSQL("select distinct * from " + ds
                        + " ;");

                d.open();
                Set<Value> valueSet = new HashSet<Value>();
                for (int i = 0; i < fields.length; i++) {
                        Value col = ValueFactory.createValue(d.getRow(i));
                        assertFalse(valueSet.contains(col));
                        valueSet.add(col);
                }
                d.close();
        }

        @Test
        public void testDistinctAllFields() throws Exception {
                String resource = TestResourceHandler.getFilesWithRepeatedRows().iterator().next();
                String name = sm.nameAndRegister(new File(TestResourceHandler.TESTRESOURCES, resource));
                testDistinctAllFields(name);
                sm.delete(name);
        }

        @Test
        public void testDistinctOnGeometricField() throws Exception {
                final WKTReader wktr = new WKTReader();
                final MemoryDataSetDriver driver = new MemoryDataSetDriver(
                        new String[]{"the_geom"}, new Type[]{TypeFactory.createType(Type.POINT)});

                final String g1 = "POINT (0 0)";
                driver.addValues(new Value[]{ValueFactory.createValue(wktr.read(g1))});
                driver.addValues(new Value[]{ValueFactory.createValue(wktr.read(g1))});
                dsf.getSourceManager().register("ds1", driver);
                final DataSource dsResult = dsf.getDataSourceFromSQL("select distinct the_geom from ds1;");
                dsResult.open();
                assertEquals(dsResult.getRowCount(), 1);
                dsResult.close();
        }

        @Test
        public void testUnion() throws Exception {
                String ds = sm.nameAndRegister(super.getAnyNonSpatialResource());
                DataSource d = dsf.getDataSourceFromSQL("select * from " + ds
                        + " union select  * from " + ds + ";");

                d.open();
                DataSource originalDS = dsf.getDataSource(ds);
                originalDS.open();

                String[] fieldNames = d.getFieldNames();
                Value[] row = d.getRow(0);
                String sql = "select * from " + d.getName() + " where ";
                String separator = "";
                for (int j = 0; j < row.length; j++) {
                        sql += separator + " \"" + fieldNames[j] + "\"";
                        if (row[j].isNull()) {
                                sql += " is "
                                        + row[j].getStringValue(ValueWriter.DEFAULTWRITER);
                        } else {
                                sql += "="
                                        + row[j].getStringValue(ValueWriter.DEFAULTWRITER);
                        }
                        separator = " and ";
                }
                sql += ";";

                /*
                 * We only test if there is an even number of equal rows
                 */
                DataSource testDS = dsf.getDataSourceFromSQL(sql);
                testDS.open();
                assertEquals((testDS.getRowCount() / 2), (testDS.getRowCount() / 2.0), 0);
                testDS.close();

                originalDS.close();
                d.close();
        }

        @Test
        public void testSelect() throws Exception {
                String ds = SHPTABLE;
                DataSource d = dsf.getDataSourceFromSQL("SELECT MIN(runoff_win), MAX(runoff_win) FROM " + SHPTABLE + ";");
                d.open();
                double low = d.getDouble(0, 0);
                double high = d.getDouble(0, 1);
                double median = high - low / 2;
                d.close();

                String numericField = "runoff_win";

                d = dsf.getDataSourceFromSQL("select * from " + ds
                        + " where " + numericField + "<" + median + ";");

                d.open();
                int fieldIndex = d.getFieldIndexByName(numericField);
                for (int i = 0; i < d.getRowCount(); i++) {
                        assertTrue(d.getDouble(i, fieldIndex) < median);
                }
                d.close();
        }

        @Test
        public void testSelectWhere() throws Exception {
                dsf.getSourceManager().register("hedgerow", new File(TestResourceHandler.TESTRESOURCES, "hedgerow.shp"));
                String query = "SELECT * FROM hedgerow where \"type\" = 'talus';";
                DataSource ds = dsf.getDataSourceFromSQL(query);
                ds.open();
                assertTrue(ds.getRowCount() > 0);
                assertEquals(ds.getFieldValue(0, ds.getFieldIndexByName("type")).getAsString(), "talus");
                ds.close();

        }

        @Test
        public void testGetDataSourceFactory() throws Exception {
                DataSource d = dsf.getDataSourceFromSQL("select * from " + SHPTABLE + ";");

                DataSource d2 = dsf.getDataSourceFromSQL("select * from " + d.getName() + ";");

                assertEquals(dsf, d2.getDataSourceFactory());
        }

        @Test
        public void testCreateTable() throws Exception {
                dsf.executeSQL("create table temptable (toto int primary key, tutu string unique);");
                DataSource ds = dsf.getDataSource("temptable");
                ds.open();
                assertEquals(Type.INT, ds.getFieldType(0).getTypeCode());
                assertEquals(Type.STRING, ds.getFieldType(1).getTypeCode());
                assertEquals(1, ds.getFieldType(0).getConstraints().length);
                assertEquals(1, ds.getFieldType(1).getConstraints().length);
                assertEquals(Constraint.PK, ds.getFieldType(0).getConstraints()[0].getConstraintCode());
                assertEquals(Constraint.UNIQUE, ds.getFieldType(1).getConstraints()[0].getConstraintCode());
                ds.close();
        }

        @Test
        public void testCreateAsSelect() throws Exception {
                dsf.getSourceManager().remove("newShape");
                dsf.executeSQL("create table newShape as select * from " + SHPTABLE + ";");
                DataSource newDs = dsf.getDataSource("newShape");
                DataSource sourceDs = dsf.getDataSource(SHPTABLE);
                newDs.open();
                sourceDs.open();
                byte[] d1 = DigestUtilities.getDigest(newDs);
                byte[] d2 = DigestUtilities.getDigest(sourceDs);
                assertTrue(DigestUtilities.equals(d1, d2));
                newDs.close();
                sourceDs.close();
        }

        @Test
        public void testCreateAsUnion() throws Exception {
                dsf.getSourceManager().remove("newShape");
                dsf.executeSQL("create table newShape as select * from " + SHPTABLE + " union select * from " + SHPTABLE + ";");
                DataSource newDs = dsf.getDataSource("newShape");
                DataSource sourceDs = dsf.getDataSource(SHPTABLE);
                newDs.open();
                sourceDs.open();
                assertEquals(newDs.getRowCount() / 2.0, sourceDs.getRowCount(), 0);
                newDs.close();
                sourceDs.close();
        }

        @Test
        public void testCreateAsView() throws Exception {
                String dsName = SHPTABLE;
                dsf.executeSQL("create view myview as select * from " + dsName + ";");
                DataSource ds = dsf.getDataSource("myview");
                DataSource dsIn = dsf.getDataSource(dsName);
                ds.open();
                dsIn.open();
                assertEquals(ds.getRowCount(), dsIn.getRowCount());
                ds.close();
                dsIn.close();
        }

        @Test
        public void testCreateDropFunction() throws Exception {
                dsf.executeSQL("create function myfunct as 'org.gdms.sql.function.math.Sin' language 'java';");
                assertTrue(dsf.getFunctionManager().contains("myfunct"));
                assertEquals("Sin", dsf.getFunctionManager().getFunction("myfunct").getName());

                dsf.executeSQL("create or replace function myfunct as 'org.gdms.sql.function.math.Cos' language 'java';");
                assertEquals("Cos", dsf.getFunctionManager().getFunction("myfunct").getName());

                dsf.executeSQL("drop function myfunct;");
                assertFalse(dsf.getFunctionManager().contains("myfunct"));

                dsf.executeSQL("drop function if exists myfunct;");
        }

        @Test
        public void testDropAsView() throws Exception {
                String dsName = SHPTABLE;
                dsf.executeSQL("create view myview as select * from " + dsName + ";");
                dsf.executeSQL("drop view myview;");
                assertFalse(dsf.getSourceManager().exists("myview"));
        }

        @Test
        public void testAliasInFunction() throws Exception {
                String dsName = SHPTABLE;
                String alias = "myalias";
                DataSource ds = dsf.getDataSourceFromSQL("select st_Buffer(the_geom, 20) as " + alias
                        + " from " + dsName + ";");
                ds.open();
                assertEquals(ds.getFieldName(0), alias);
                ds.close();
        }

        @Test
        public void testGroupByAndSumDouble() throws Exception {
                dsf.getSourceManager().register("groupcsv",
                        new File(TestResourceHandler.TESTRESOURCES, "groupby.csv"));
                DataSource ds = dsf.getDataSourceFromSQL("select count(category), category"
                        + " from groupcsv group by category;");
                ds.open();
                assertEquals(ds.getRowCount(), 2);
                ds.close();

                ds = dsf.getDataSourceFromSQL("select Sum(id :: double), Count(id), country, category"
                        + " from groupcsv group by country, category order by country, category;");
                ds.open();
                assertEquals(ds.getRowCount(), 6);
                assertEquals(ds.getInt(0, 0), 5);
                assertEquals(ds.getInt(1, 0), 9);
                assertEquals(ds.getInt(2, 0), 8);
                assertEquals(ds.getInt(3, 0), 11);
                assertEquals(ds.getInt(4, 0), 3);
                assertEquals(ds.getInt(5, 0), 0);
                ds.close();
        }

        @Test
        public void testGroupByAliasedReference() throws Exception {
                dsf.getSourceManager().register("groupcsv",
                        new File(TestResourceHandler.TESTRESOURCES, "groupby.csv"));
                DataSource ds = dsf.getDataSourceFromSQL("select category"
                        + " from groupcsv g group by g.category;");
                ds.open();
                ds.getRow(0);
                ds.close();
        }

        @Test
        public void testGroupByInsideExpression() throws Exception {
                dsf.getSourceManager().register("groupcsv",
                        new File(TestResourceHandler.TESTRESOURCES, "groupby.csv"));
                DataSource ds = dsf.getDataSourceFromSQL("select 'toto ' || category || ' '"
                        + " from groupcsv g group by g.category;");
                ds.open();
                ds.getRow(0);
                ds.close();
        }

        @Test
        public void testGroupByAlias() throws Exception {
                dsf.getSourceManager().register("groupcsv",
                        new File(TestResourceHandler.TESTRESOURCES, "groupby.csv"));
                DataSource ds = dsf.getDataSourceFromSQL("select (category || ' ') AS groupp"
                        + " from groupcsv group by groupp;");
                ds.open();
                ds.getRow(0);
                ds.close();
        }

        @Test
        public void regressionTest699() throws Exception {
                dsf.executeSQL("create table test as SELECT MAX(abs(runoff_win)) as mrun FROM " + SHPTABLE + ";");
        }

        @Test
        public void testHavingAggregateAlias() throws Exception {
                dsf.getSourceManager().register("groupcsv",
                        new File(TestResourceHandler.TESTRESOURCES, "groupby.csv"));
                DataSource ds = dsf.getDataSourceFromSQL("SELECT Sum(id :: double) AS agg"
                        + " FROM groupcsv GROUP BY country, category HAVING agg >= 8"
                        + " ORDER BY country, category;");
                ds.open();
                assertEquals(ds.getRowCount(), 3);
                assertEquals(ds.getInt(0, 0), 9);
                assertEquals(ds.getInt(1, 0), 8);
                assertEquals(ds.getInt(2, 0), 11);
                ds.close();
        }
        
        @Test
        public void testHavingDirectAggregate() throws Exception {
                dsf.getProperties().setProperty("output.explain", "true");
                dsf.getSourceManager().register("groupcsv",
                        new File(TestResourceHandler.TESTRESOURCES, "groupby.csv"));
                DataSource ds = dsf.getDataSourceFromSQL("SELECT country"
                        + " FROM groupcsv GROUP BY country, category HAVING Sum(id :: double) >= 8"
                        + " ORDER BY country;");
                ds.open();
                assertEquals(ds.getRowCount(), 3);
                ds.close();
        }
        
        @Test
        public void testHavingDirectAggregateNoGroupBy() throws Exception {
                dsf.getProperties().setProperty("output.explain", "true");
                dsf.getSourceManager().register("groupcsv",
                        new File(TestResourceHandler.TESTRESOURCES, "groupby.csv"));
                DataSource ds = dsf.getDataSourceFromSQL("SELECT 1"
                        + " FROM groupcsv HAVING Sum(id :: double) >= 8;");
                ds.open();
                assertEquals(1, ds.getRowCount());
                ds.close();
        }

        @Test
        public void testLimitOffset() throws Exception {
                String resource = SHPTABLE;
                testLimitOffset("select * from " + resource + " where true");
                testLimit("select * from " + resource + " where true");
                testOffset("select * from " + resource + " where true");

                testLimit("select * from " + resource + " ");
                testOffset("select * from " + resource + " ");
                testLimitOffset("select * from " + resource + " ");

                String field = "runoff_win";
                testLimitOffset("select " + field + " from " + resource + " order by " + field + "");
                testLimit("select " + field + " from " + resource + " order by " + field + "");
                testOffset("select " + field + " from " + resource + " order by " + field + "");
        }

        private void testLimitOffset(String sql) throws Exception {
                String limitedSQL = sql + " limit 10 offset 10;";
                DataSource ds = dsf.getDataSourceFromSQL(limitedSQL);
                DataSource original = dsf.getDataSourceFromSQL(sql + ";");
                ds.open();
                original.open();
                assertEquals(ds.getRowCount(), 10);
                for (int i = 0; i < 10; i++) {
                        assertTrue(equals(ds.getRow(i), original.getRow(i + 10)));
                }
                ds.close();
                original.close();
        }

        private void testLimit(String sql) throws Exception {
                String limitedSQL = sql + " limit 10;";
                DataSource ds = dsf.getDataSourceFromSQL(limitedSQL);
                DataSource original = dsf.getDataSourceFromSQL(sql + ";");
                ds.open();
                original.open();
                assertEquals(ds.getRowCount(), 10);
                for (int i = 0; i < 10; i++) {
                        assertTrue(equals(ds.getRow(i), original.getRow(i)));
                }
                ds.close();
                original.close();
        }

        private void testOffset(String sql) throws Exception {
                String limitedSQL = sql + " offset 10;";
                DataSource ds = dsf.getDataSourceFromSQL(limitedSQL);
                DataSource original = dsf.getDataSourceFromSQL(sql + ";");
                ds.open();
                original.open();
                assertEquals(ds.getRowCount(), original.getRowCount() - 10);
                for (int i = 0; i < ds.getRowCount(); i++) {
                        assertTrue(equals(ds.getRow(i), original.getRow(i + 10)));
                }
                ds.close();
                original.close();
        }

        @Test
        public void testGroupAndOrderBy() throws Exception {
                String resource = SHPTABLE;
                String field = "runoff_win";
                String sql = "select " + field + " from " + resource + " group by "
                        + resource + "." + field + " order by " + resource + "."
                        + field + ";";
                DataSource ds = dsf.getDataSourceFromSQL(sql);
                ds.open();
                assertEquals(ds.getMetadata().getFieldCount(), 1);
                ds.close();
        }

        @Test
        public void testNot() throws Exception {
                String resource = SHPTABLE;
                String stringField = "\"type\"";
                String sql = "select * from " + resource + " where " + stringField
                        + " <> 'a';";
                DataSource ds = dsf.getDataSourceFromSQL(sql);
                ds.open();
                long rc1 = ds.getRowCount();
                ds.close();
                sql = "select * from " + resource + " where not " + stringField
                        + " <> 'a';";
                ds = dsf.getDataSourceFromSQL(sql);
                ds.open();
                long rc2 = ds.getRowCount();
                ds.close();
                assertTrue(rc1 != rc2);
        }

        @Test
        public void testNegativeValues() throws Exception {
                String resource = SHPTABLE;
                String sql = "select 1 from " + resource
                        + " where -4 >= -128 and -4 < 0;";
                DataSource ds = dsf.getDataSourceFromSQL(sql);
                ds.open();
                assertTrue(ds.getRowCount() > 0);
                ds.close();
        }

        @Test
        public void testLike() throws Exception {
                String resource = SHPTABLE;
                String stringField = "\"type\"";
                String sql = "select * from " + resource + " where " + stringField
                        + " NOT LIKE '%';";
                DataSource ds = dsf.getDataSourceFromSQL(sql);
                ds.open();
                assertEquals(ds.getRowCount(), 0);
                ds.close();
        }

        @Test
        public void testSourceOnlyReadOnce() throws Exception {
                final StringBuffer tics = new StringBuffer("");
                MemoryDataSetDriver omd = new MemoryDataSetDriver(
                        new String[]{"the_geom"}, new Type[]{TypeFactory.createType(Type.GEOMETRY)}) {

                        @Override
                        public Value getFieldValue(long rowIndex, int fieldId)
                                throws DriverException {
                                tics.append("x");
                                return super.getFieldValue(rowIndex, fieldId);
                        }
                };
                omd.addValues(new Value[]{ValueFactory.createValue(Geometries.getPoint())});
                dsf.getSourceManager().register("oneline", omd);
                dsf.executeSQL("create table tata as select st_buffer(the_geom, 10) from oneline;");
                assertEquals(1, tics.length());
        }

        @Test
        public void testImplicitRegisterInCreate() throws Exception {
                String resourceName = SHPTABLE;
                dsf.executeSQL("create table newtable as select * from " + resourceName
                        + ";");
                DataSource dataSource1 = dsf.getDataSource("newtable");
                DataSource dataSource2 = dsf.getDataSource(resourceName);
                dataSource1.open();
                dataSource2.open();
                assertTrue(equals(super.getDataSourceContents(dataSource1), super.getDataSourceContents(dataSource2)));
                dataSource1.close();
                dataSource2.close();
        }

        @Test
        public void testAggregatedExecution() throws Exception {
                MemoryDataSetDriver omd = new MemoryDataSetDriver(new String[]{
                                "the_geom", "alpha"}, new Type[]{
                                TypeFactory.createType(Type.GEOMETRY),
                                TypeFactory.createType(Type.STRING)});
                omd.addValues(new Value[]{
                                ValueFactory.createValue(Geometries.getPolygon()),
                                ValueFactory.createValue("a")});
                omd.addValues(new Value[]{
                                ValueFactory.createValue(Geometries.getPolygon()),
                                ValueFactory.createValue("a")});
                omd.addValues(new Value[]{
                                ValueFactory.createValue(Geometries.getPolygon()),
                                ValueFactory.createValue("b")});
                dsf.getSourceManager().register("source", omd);
                DataSource ds = dsf.getDataSourceFromSQL("select st_union(the_geom) from source "
                        + "group by alpha;");
                ds.open();
                assertEquals(ds.getRowCount(), 2);
                for (int i = 0; i < ds.getRowCount(); i++) {
                        assertNotNull(ds.getFieldValue(i, 0).getAsGeometry());
                }
                ds.close();

        }

        @Test
        public void testSelectCountStar() throws Exception {
                DataSource dsSource = dsf.getDataSource("landcover2000");
                DataSource ds = dsf.getDataSourceFromSQL("select  count(*) as star from landcover2000;");

                ds.open();
                dsSource.open();
                assertEquals(ds.getFieldValue(0, 0).getAsInt(), dsSource.getRowCount());
                ds.close();
                dsSource.close();
        }

        @Test
        public void testCreateIndex() throws Exception {
                String resource = SHPTABLE;
                dsf.executeSQL("create index on " + resource + " (the_geom);");
                dsf.executeSQL("create index on " + resource + " (runoff_win);");
                dsf.executeSQL("create index on " + resource + " (runoff_win, runoff_sum);");
                dsf.executeSQL("drop index on " + resource + " (the_geom);");
                dsf.executeSQL("drop index on " + resource + " (runoff_win);");
                dsf.executeSQL("drop index on " + resource + " (runoff_win, runoff_sum);");
        }

        @Test
        public void testDeepAggregatedFunction() throws Exception {
                String resource = SHPTABLE;
                String nfName = "runoff_win";
                String sql = "select max(" + nfName
                        + ") :: double + 3 from " + resource + " group by " + nfName + ";";
                DataSource ds = dsf.getDataSourceFromSQL(sql);
                ds.open();
                for (int i = 0; i < ds.getRowCount(); i++) {
                        assertFalse(ds.isNull(i, 0));
                }
                ds.close();
        }

        @Test
        public void testJoinThreeTablesTwoAreTheSameBug() throws Exception {
                createSource("table1", "id", 1, 2, 3, 4, 5, 6, 7, 8);
                createSource("table2", "id", 1, 2);
                DataSource ds = dsf.getDataSourceFromSQL("SELECT n1.id as n1, n2.id as n2, e.id "
                        + "FROM table1 n1, table1 n2, table2 e "
                        + "WHERE n1.id = e.id;");
                ds.open();
                assertEquals(ds.getRowCount(), 16);
                ds.close();
        }

        @Test
        public void regressionTest700() throws Exception {
                dsf.executeSQL("CREATE TABLE toto1 AS SELECT * FROM " + SHPTABLE + " LIMIT 1;");
                sm.addName("toto1", "toto2");
                dsf.executeSQL("CREATE TABLE toto AS SELECT toto1.the_geom FROM toto1 LEFT JOIN toto2 ON "
                        + "toto1.runoff_win = toto2.runoff_win;");
        }

        @Test
        public void regressionTest702() throws Exception {
                sm.addName(SHPTABLE, "toto");
                try {
                        dsf.executeSQL("CREATE TABLE test AS SELECT *, count(*) as toto FROM toto;");
                        fail();
                } catch (SemanticException e) {
                }

                try {
                        dsf.executeSQL("CREATE TABLE test AS SELECT the_geom, count(*) as toto FROM toto;");
                        fail();
                } catch (SemanticException e) {
                }
        }

        @Test
        public void testSortDoesNotIncludeField() throws Exception {
                dsf.getSourceManager().register("test",
                        new File(TestResourceHandler.OTHERRESOURCES, "test.csv"));
                DataSource ds = dsf.getDataSourceFromSQL("SELECT gis from test order by id;");
                ds.open();
                assertEquals(ds.getMetadata().getFieldCount(), 1);
                ds.close();
        }

        @Test
        public void testExistsSelect() throws Exception {

                Type intType = TypeFactory.createType(Type.INT);
                Type stringType = TypeFactory.createType(Type.STRING);

                MemoryDataSetDriver town = new MemoryDataSetDriver(new String[]{
                                "town", "sales"}, new Type[]{stringType, intType});
                town.addValues(ValueFactory.createValue("plourivo"), ValueFactory.createValue(1500));
                town.addValues(ValueFactory.createValue("paimpol"), ValueFactory.createValue(250));
                town.addValues(ValueFactory.createValue("nantes"), ValueFactory.createValue(300));
                town.addValues(ValueFactory.createValue("nozay"), ValueFactory.createValue(700));

                MemoryDataSetDriver geography = new MemoryDataSetDriver(new String[]{
                                "region_name", "town"}, new Type[]{stringType, intType});
                geography.addValues(ValueFactory.createValue("bretagne"), ValueFactory.createValue("plourivo"));
                geography.addValues(ValueFactory.createValue("bretagne"), ValueFactory.createValue("paimpol"));
                geography.addValues(ValueFactory.createValue("pays de la loire"),
                        ValueFactory.createValue("nantes"));
                geography.addValues(ValueFactory.createValue("pays de la loire"),
                        ValueFactory.createValue("nozay"));

                dsf.getSourceManager().register("town", town);
                dsf.getSourceManager().register("geography", geography);

                String query = "SELECT SUM(sales) FROM town " + "WHERE EXISTS "
                        + "(SELECT 1 FROM geography WHERE region_name = 'bretagne');";

                DataSource ds = dsf.getDataSourceFromSQL(query);
                ds.open();

                assertEquals(ds.getInt(0, 0), 2750);

                ds.close();

        }

        @Test
        public void testNestedSelectWhereInSubquery() throws Exception {
                Type intType = TypeFactory.createType(Type.INT);
                Type stringType = TypeFactory.createType(Type.STRING);
                MemoryDataSetDriver dict = new MemoryDataSetDriver(new String[]{
                                "code", "data"}, new Type[]{intType, stringType});
                dict.addValues(ValueFactory.createValue(0), ValueFactory.createValue("good"));
                dict.addValues(ValueFactory.createValue(1), ValueFactory.createValue("bad"));
                MemoryDataSetDriver thetable = new MemoryDataSetDriver(
                        new String[]{"dict_code"}, new Type[]{intType});
                thetable.addValues(ValueFactory.createValue(0));
                thetable.addValues(ValueFactory.createValue(1));
                thetable.addValues(ValueFactory.createValue(0));
                thetable.addValues(ValueFactory.createValue(1));
                thetable.addValues(ValueFactory.createValue(1));
                thetable.addValues(ValueFactory.createValue(0));
                dsf.getSourceManager().register("dict", dict);
                dsf.getSourceManager().register("thetable", thetable);
                DataSource ds = dsf.getDataSourceFromSQL("select * from thetable where dict_code in "
                        + "(select code from dict where \"data\" = 'good');");
                ds.open();
                for (int i = 0; i < ds.getRowCount(); i++) {
                        assertEquals(ds.getInt(i, 0), 0);
                }
                ds.close();
        }

        @Test
        public void testCorrelatedSelectWhereInSubquery() throws Exception {
                Type intType = TypeFactory.createType(Type.INT);
                Type stringType = TypeFactory.createType(Type.STRING);
                MemoryDataSetDriver dict = new MemoryDataSetDriver(new String[]{
                                "code", "data"}, new Type[]{intType, stringType});
                dict.addValues(ValueFactory.createValue(0), ValueFactory.createValue("good"));
                dict.addValues(ValueFactory.createValue(1), ValueFactory.createValue("bad"));
                MemoryDataSetDriver thetable = new MemoryDataSetDriver(
                        new String[]{"dict_code", "data"}, new Type[]{intType, stringType});
                thetable.addValues(ValueFactory.createValue(0), ValueFactory.createValue("good"));
                thetable.addValues(ValueFactory.createValue(1), ValueFactory.createValue("good"));
                thetable.addValues(ValueFactory.createValue(0), ValueFactory.createValue("bad"));
                thetable.addValues(ValueFactory.createValue(1), ValueFactory.createValue("bad"));
                thetable.addValues(ValueFactory.createValue(1), ValueFactory.createValue("bad"));
                thetable.addValues(ValueFactory.createValue(0), ValueFactory.createValue("good"));
                dsf.getSourceManager().register("dict", dict);
                dsf.getSourceManager().register("thetable", thetable);
                DataSource ds = dsf.getDataSourceFromSQL("select * from thetable where dict_code in "
                        + "(select code from dict where dict.\"data\" = thetable.\"data\");");
                ds.open();
                assertEquals(4l, ds.getRowCount());
                for (int i = 0; i < ds.getRowCount(); i++) {
                        assertTrue((ds.getInt(i, 0) == 0 && "good".equals(ds.getString(i, 1)))
                                || (ds.getInt(i, 0) == 1 && "bad".equals(ds.getString(i, 1))));
                }
                ds.close();
        }

        @Test
        public void testExecuteTwiceOnTwoSourcesWithSameName() throws Exception {
                createSource("source", "toto", 1, 2, 3);
                String sql = "select * from source where toto < 2;";
                DataSource ds = dsf.getDataSourceFromSQL(sql);
                ds.open();
                assertEquals(ds.getRowCount(), 1);
                ds.close();
                dsf.getSourceManager().remove("source");

                createSource("source", "toto", 10);
                ds = dsf.getDataSourceFromSQL(sql);
                ds.open();
                assertEquals(ds.getRowCount(), 0);
                ds.close();
        }

        @Test
        public void testUpdate() throws Exception {
                createSource("source", "toto", 0, 1, 2, 3);
                dsf.executeSQL("update source SET toto = toto + '1' :: int;", null);
                DataSource ds = dsf.getDataSource("source");
                ds.open();
                assertEquals(ds.getRowCount(), 4);
                for (int i = 0; i < ds.getRowCount(); i++) {
                        assertEquals(i + 1, ds.getInt(i, 0));
                }
                ds.close();
        }

        @Test
        public void testUpdateGeometry() throws Exception {
                dsf.executeSQL("create table temp as select st_addz(the_geom, gid ) as the_geom, gid  from landcover2000 where gid = 1;");

                DataSource sds = dsf.getDataSource("temp");

                sds.open();

                Coordinate[] coords = sds.getGeometry(0).getCoordinates();

                for (Coordinate coordinate : coords) {

                        assertEquals(coordinate.z, 1, 0);
                }

                sds.close();

        }

        @Test
        public void testUpdateWhere() throws Exception {
                createSource("source", "toto", 0, 1, 2, 3);
                dsf.executeSQL("update source SET toto = 1 WHERE toto = 0;", null);
                DataSource ds = dsf.getDataSource("source");
                ds.open();
                assertEquals(4, ds.getRowCount());
                assertEquals(1, ds.getInt(0, 0));
                ds.close();
        }

        @Test
        public void testUpdateInSubquery() throws Exception {
                dsf.getSourceManager().register("communes",
                        new File(TestResourceHandler.TESTRESOURCES, "ile_de_nantes.shp"));

                String subQuery = "select a.gid from communes b, landcover2000 a where st_intersects(a.the_geom, b.the_geom)";

                DataSource dsSubQuery = dsf.getDataSourceFromSQL(subQuery + ";");
                dsSubQuery.open();
                long count = dsSubQuery.getRowCount();
                dsSubQuery.close();

                dsf.executeSQL("create table landcoverUpdated as select * from landcover2000;");

                dsf.executeSQL("update landcoverUpdated SET gid = 9999 "
                        + "WHERE gid in (" + subQuery + ");", null);

                DataSource dsResultQuery = dsf.getDataSourceFromSQL("select * from landcoverUpdated where gid =9999;");
                dsResultQuery.open();
                long countRes = dsResultQuery.getRowCount();
                dsResultQuery.close();

                DataSource ds = dsf.getDataSource("landcoverUpdated");
                ds.open();
                assertEquals(countRes, count);
                ds.close();
        }

        @Test
        public void testSetResetShowProperty() throws Exception {
                dsf.executeSQL("SET custom.myproperty TO 'some value';");
                assertEquals("some value", dsf.getProperties().getProperty("custom.myproperty"));

                DataSource d = dsf.getDataSourceFromSQL("SHOW custom.myproperty;");
                d.open();
                assertEquals(1l, d.getRowCount());
                assertEquals("custom.myproperty", d.getFieldValue(0, 0).getAsString());
                assertEquals("some value", d.getFieldValue(0, 1).getAsString());
                d.close();

                dsf.executeSQL("RESET custom.myproperty;");
                assertFalse(dsf.getProperties().containsKey("custom.myproperty"));

                dsf.executeSQL("SET custom.myproperty TO 'some value';");
                dsf.executeSQL("RESET ALL;");

                assertFalse(dsf.getProperties().containsKey("custom.myproperty"));

                dsf.executeSQL("SET custom.myproperty TO 'some value';");
                dsf.executeSQL("SET custom.myproperty TO DEFAULT;");
                assertFalse(dsf.getProperties().containsKey("custom.myproperty"));
        }

        @Test
        public void testSelectAlone() throws Exception {
                DataSource d = dsf.getDataSourceFromSQL("SELECT 18 as myIntField, 'toto', abs(-42);");
                d.open();
                assertEquals(1, d.getRowCount());
                assertEquals("myIntField", d.getFieldName(0));
                assertEquals("exp1", d.getFieldName(1));
                assertEquals("exp2", d.getFieldName(2));

                assertEquals(18, d.getInt(0, 0));
                assertEquals("toto", d.getString(0, 1));
                assertEquals(42, d.getInt(0, 2));
                d.close();
        }

        private void createSource(String name, String fieldName, int... values) {
                MemoryDataSetDriver omd = new MemoryDataSetDriver(
                        new String[]{fieldName}, new Type[]{TypeFactory.createType(Type.INT)});
                for (int value : values) {
                        try {
                                omd.addValues(new Value[]{ValueFactory.createValue(value)});
                        } catch (DriverException ex) {
                                Logger.getLogger(SQLTest.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }

                dsf.getSourceManager().register(name, omd);
        }

        @Before
        public void setUp() throws Exception {
                super.setUpTestsWithoutEdition();
                sm.register(SHPTABLE, new File(TestResourceHandler.TESTRESOURCES, "landcover2000.shp"));
        }
}
