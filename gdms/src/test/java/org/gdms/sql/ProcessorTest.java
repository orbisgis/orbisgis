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
package org.gdms.sql;

import org.junit.Before;
import org.junit.Test;
import java.io.File;

import org.gdms.TestBase;
import org.gdms.data.NoSuchTableException;

import org.gdms.data.AllTypesObjectDriver;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.gdms.sql.engine.ParseException;
import org.gdms.data.types.IncompatibleTypesException;
import org.gdms.sql.engine.SemanticException;
import org.gdms.sql.engine.SQLStatement;
import org.gdms.sql.engine.UnknownFieldException;
import org.gdms.sql.strategies.SumQuery;

import static org.junit.Assert.*;

import org.gdms.TestResourceHandler;
import org.gdms.sql.engine.Engine;

public class ProcessorTest extends TestBase {

        private Metadata allTypesMetadata;

        @Before
        public void setUp() throws Exception {
                super.setUpTestsWithoutEdition();

                AllTypesObjectDriver omd = new AllTypesObjectDriver();
                allTypesMetadata = omd.getTable("main").getMetadata();
                sm.register("alltypes", omd);
                sm.register("gis", new File(TestResourceHandler.OTHERRESOURCES, "test.csv"));

                omd = new AllTypesObjectDriver();
                sm.register("hsqldb", omd);

                dsf.getFunctionManager().addFunction(ZeroArgsFunction.class);
        }

        @Test
        public void testNormalWithoutFrom() throws Exception {
                failWithSemanticException("select toto;");
        }

        @Test
        public void testScalarProduct() throws Exception {
                getValidatedStatement("select t1.\"int\" from alltypes t1, alltypes t2;");
        }

        @Test
        public void testGetResultMetadataStar() throws Exception {
                SQLStatement p = getValidatedStatement("select * from alltypes;");
                p.setDataSourceFactory(dsf);
                p.prepare();
                Metadata m1 = p.getResultMetadata();
                p.cleanUp();
                compareMetadata(m1, allTypesMetadata);
                p = getValidatedStatement("select t.* from alltypes t;");
                p.setDataSourceFactory(dsf);
                p.prepare();
                m1 = p.getResultMetadata();
                p.cleanUp();
                compareMetadata(m1, allTypesMetadata);
                p = getValidatedStatement("select string as mystr, t.* from alltypes t;");
                p.setDataSourceFactory(dsf);
                p.prepare();
                m1 = p.getResultMetadata();
                p.cleanUp();
                DefaultMetadata m2 = new DefaultMetadata();
                m2.addField("mystr", Type.STRING);
                m2.addAll(allTypesMetadata);
                compareMetadata(m1, m2);
        }

        @Test
        public void testStarOfNonExistingTable() throws Exception {
                failWithNoSuchTableException("select foo.* from alltypes "
                        + "where alltypes.\"float\" > 2;");
        }

        @Test
        public void testGetResultMetadataField() throws Exception {
                SQLStatement p = getValidatedStatement("select gis from gis;");
                p.setDataSourceFactory(dsf);
                p.prepare();
                Metadata m1 = p.getResultMetadata();
                p.cleanUp();
                DefaultMetadata m2 = new DefaultMetadata();
                m2.addField("gis", Type.STRING);
                compareMetadata(m1, m2);
        }

        @Test
        public void testGetResultMetadataOperations() throws Exception {
                checkMultiplySumAndSubstractionOperators("byte", "byte", Type.BYTE);
                checkMultiplySumAndSubstractionOperators("byte", "short", Type.SHORT);
                checkMultiplySumAndSubstractionOperators("byte", "int", Type.INT);
                checkMultiplySumAndSubstractionOperators("byte", "long", Type.LONG);
                checkMultiplySumAndSubstractionOperators("byte", "float", Type.FLOAT);
                checkMultiplySumAndSubstractionOperators("byte", "double", Type.DOUBLE);

                checkMultiplySumAndSubstractionOperators("short", "byte", Type.SHORT);
                checkMultiplySumAndSubstractionOperators("short", "short", Type.SHORT);
                checkMultiplySumAndSubstractionOperators("short", "int", Type.INT);
                checkMultiplySumAndSubstractionOperators("short", "long", Type.LONG);
                checkMultiplySumAndSubstractionOperators("short", "float", Type.FLOAT);
                checkMultiplySumAndSubstractionOperators("short", "double", Type.DOUBLE);

                checkMultiplySumAndSubstractionOperators("int", "byte", Type.INT);
                checkMultiplySumAndSubstractionOperators("int", "short", Type.INT);
                checkMultiplySumAndSubstractionOperators("int", "int", Type.INT);
                checkMultiplySumAndSubstractionOperators("int", "long", Type.LONG);
                checkMultiplySumAndSubstractionOperators("int", "float", Type.FLOAT);
                checkMultiplySumAndSubstractionOperators("int", "double", Type.DOUBLE);

                checkMultiplySumAndSubstractionOperators("long", "byte", Type.LONG);
                checkMultiplySumAndSubstractionOperators("long", "short", Type.LONG);
                checkMultiplySumAndSubstractionOperators("long", "int", Type.LONG);
                checkMultiplySumAndSubstractionOperators("long", "long", Type.LONG);
                checkMultiplySumAndSubstractionOperators("long", "float", Type.FLOAT);
                checkMultiplySumAndSubstractionOperators("long", "double", Type.DOUBLE);

                checkMultiplySumAndSubstractionOperators("float", "byte", Type.FLOAT);
                checkMultiplySumAndSubstractionOperators("float", "short", Type.FLOAT);
                checkMultiplySumAndSubstractionOperators("float", "int", Type.FLOAT);
                checkMultiplySumAndSubstractionOperators("float", "long", Type.FLOAT);
                checkMultiplySumAndSubstractionOperators("float", "float", Type.FLOAT);
                checkMultiplySumAndSubstractionOperators("float", "double", Type.DOUBLE);

                checkMultiplySumAndSubstractionOperators("double", "byte", Type.DOUBLE);
                checkMultiplySumAndSubstractionOperators("double", "short", Type.DOUBLE);
                checkMultiplySumAndSubstractionOperators("double", "int", Type.DOUBLE);
                checkMultiplySumAndSubstractionOperators("double", "long", Type.DOUBLE);
                checkMultiplySumAndSubstractionOperators("double", "float", Type.DOUBLE);
                checkMultiplySumAndSubstractionOperators("double", "double",
                        Type.DOUBLE);
        }

        private void checkMultiplySumAndSubstractionOperators(String field1,
                String field2, int resultType) throws Exception, DriverException {
                SQLStatement p = getValidatedStatement("select \"" + field1
                        + "\" * \"" + field2 + "\" from alltypes;");
                p.setDataSourceFactory(dsf);
                p.prepare();
                Metadata m1 = p.getResultMetadata();
                p.cleanUp();
                assertEquals(m1.getFieldType(0).getTypeCode(), resultType);
                p = getValidatedStatement("select \"" + field1 + "\" + \"" + field2
                        + "\" from alltypes;");
                p.setDataSourceFactory(dsf);
                p.prepare();
                m1 = p.getResultMetadata();
                p.cleanUp();
                assertEquals(m1.getFieldType(0).getTypeCode(), resultType);
                p = getValidatedStatement("select \"" + field1 + "\" - \"" + field2
                        + "\" from alltypes;");
                p.setDataSourceFactory(dsf);
                p.prepare();
                m1 = p.getResultMetadata();
                p.cleanUp();
                assertEquals(m1.getFieldType(0).getTypeCode(), resultType);
        }

        @Test
        public void testDivisionOperators() throws Exception {
                SQLStatement p = getValidatedStatement("select 3/0 from alltypes;");
                p.setDataSourceFactory(dsf);
                p.prepare();
                Metadata m1 = p.getResultMetadata();
                p.cleanUp();
                assertEquals(Type.FLOAT, m1.getFieldType(0).getTypeCode());
                p = getValidatedStatement("select (3.0 :: double)/3 from alltypes;");
                p.setDataSourceFactory(dsf);
                p.prepare();
                m1 = p.getResultMetadata();
                p.cleanUp();
                assertEquals(Type.DOUBLE, m1.getFieldType(0).getTypeCode());
        }

        private void compareMetadata(Metadata m1, Metadata m2)
                throws DriverException {
                assertEquals(m1.getFieldCount(), m2.getFieldCount());
                for (int i = 0; i < m1.getFieldCount(); i++) {
                        assertTrue(m1.getFieldName(i).equals(m2.getFieldName(i)));
                        Type t1 = m1.getFieldType(i);
                        Type t2 = m2.getFieldType(i);
                        assertEquals(t1.getTypeCode(), t2.getTypeCode());
                        assertEquals(t1.getConstraints().length, t2.getConstraints().length);
                        for (int j = 0; j < t1.getConstraints().length; j++) {
                                Constraint c1 = t1.getConstraints()[j];
                                Constraint c2 = t2.getConstraint(c1.getConstraintCode());
                                assertTrue(c2 != null);
                                assertEquals(c1.getConstraintCode(), c2.getConstraintCode());
                                assertEquals(c1.getConstraintValue(), c2.getConstraintValue());
                        }
                }
        }

        @Test
        public void testIncompatibleTypes() throws Exception {
                failWithIncompatibleTypes("select 3+'text' from alltypes;");
                failWithIncompatibleTypes("select 'text'+'text' from alltypes;");
                failWithIncompatibleTypes("select 3+true from alltypes;");
                failWithIncompatibleTypes("select * from alltypes where 3+'text'=4;");
                failWithIncompatibleTypes("select * from alltypes where 'text'+'text'=4;");
                failWithIncompatibleTypes("select * from alltypes where 3+true=4;");
        }

        @Test
        public void testIncompatibleTypesInFunctions() throws Exception {
                failWithIncompatibleTypes("select avg(4, 6) from alltypes;");
                failWithIncompatibleTypes("select avg('a string') from alltypes;");
                failWithIncompatibleTypes("select avg(*) from alltypes;");
                failWithIncompatibleTypes("select replace('string', '3' :: int, '') from alltypes;");
                getFullyValidatedStatement("select avg(3) from alltypes;");
                failWithIncompatibleTypes("select * from alltypes where replace('e', 'e', 'e')=3;");
                failWithIncompatibleTypes("select * from alltypes where replace(4, 'e', 'f')='afs';");
                failWithIncompatibleTypes("select * from alltypes where replace('string', '3' :: int, 22) = 'asd';");
                getFullyValidatedStatement("select * from alltypes where replace('asd', 'asd', 'asdasd') = 'asdasd';");
        }

        private void failWithIncompatibleTypes(String sql) throws Exception {
                try {
                        getFullyValidatedStatement(sql);
                        fail();
                } catch (IncompatibleTypesException e) {
                }
        }

        private void failPreparedWithIncompatibleTypes(String sql) throws Exception {
                try {
                        getFullyValidatedStatement(sql);
                        fail();
                } catch (IncompatibleTypesException e) {
                }
        }

        private void failWithSemanticException(String sql) throws Exception {
                try {
                        getValidatedStatement(sql);
                        fail();
                } catch (SemanticException e) {
                } catch (ParseException p) {
                        if (!(p.getCause() instanceof SemanticException)) {
                                p.printStackTrace();
                                fail();
                        }
                }
        }

        private void failPreparedWithSemanticException(String sql) throws Exception {
                try {
                        getFullyValidatedStatement(sql);
                        fail();
                } catch (SemanticException e) {
                } catch (ParseException p) {
                        if (!(p.getCause() instanceof SemanticException)) {
                                p.printStackTrace();
                                fail();
                        }
                }
        }
        
        private void failPreparedWithUnknownFieldException(String sql) throws Exception {
                try {
                        getFullyValidatedStatement(sql);
                        fail();
                } catch (UnknownFieldException e) {
                }
        }

        private void failWithParseException(String sql) throws Exception {
                try {
                        getValidatedStatement(sql);
                        fail();
                } catch (ParseException e) {
                }
        }

        private void failWithNoSuchTableException(String sql) throws Exception {
                try {
                        SQLStatement p = getValidatedStatement(sql);
                        p.setDataSourceFactory(dsf);
                        p.prepare();
                        fail();
                } catch (NoSuchTableException e) {
                }
        }

        @Test
        public void testProjectionAlias() throws Exception {
                SQLStatement p = getValidatedStatement("select \"double\" as mydoublE, "
                        + "string as mySTR from alltypes;");
                p.setDataSourceFactory(dsf);
                p.prepare();
                Metadata m = p.getResultMetadata();
                p.cleanUp();
                assertFalse(m.getFieldName(0).equals("mydouble"));
                assertTrue(m.getFieldName(1).equals("mySTR"));
        }

        @Test
        public void testEmptyProjection() throws Exception {
                getFullyValidatedStatement("SELECT exp0 FROM VALUES('toto') test;");

                // projections that remove every fields are not allowed
                failPreparedWithSemanticException("SELECT * EXCEPT exp0 FROM VALUES('toto') test;");
        }

        @Test
        public void testAmbiguousName() throws Exception {
                failPreparedWithSemanticException("select \"long\" "
                        + " from alltypes t1, alltypes t2;");
                getValidatedStatement("select t1.\"long\" "
                        + " from alltypes t1, alltypes t2;");
                getValidatedStatement("select t2.\"long\" "
                        + " from alltypes t1, alltypes t2;");
                getValidatedStatement("select t1.* "
                        + " from alltypes t1, alltypes;");
        }

        @Test
        public void testNonExistingIds() throws Exception {
                // non
                failPreparedWithUnknownFieldException("select non.\"integer\" from alltypes;");
                // ier
                failPreparedWithSemanticException("select alltypes.ier from alltypes;");
                failPreparedWithUnknownFieldException("select * from alltypes where non.\"integer\"=3;");
                // idnteger
                failPreparedWithUnknownFieldException("select idnteger from alltypes;");
                failPreparedWithUnknownFieldException("select * from alltypes where idnteger = 2;");
                // notexist
                failWithNoSuchTableException("select * from notexist t1;");
                // thisfunctiondoes...
                failPreparedWithSemanticException("select max(thisfunctiondoesntexist"
                        + "(idnteger)) from alltypes;");
                failPreparedWithSemanticException("select * "
                        + "from alltypes where thisfunctiondoesnotexist(idnteger);");
                // boolean case
                failPreparedWithUnknownFieldException("select max(booleaN) from alltypes;");
                failPreparedWithSemanticException("select * from alltypes where avg(\"boolean\") = 2;");
        }

        @Test
        public void testDuplicatedTableReferences() throws Exception {
                failWithSemanticException("select t1.\"long\" "
                        + " from alltypes t1, alltypes t1;");
        }

        @Test
        public void testComparisonOperators() throws Exception {
                validateAllComparison("double");
//                validateAllComparison("date");
//                validateAllComparison("time");        no comparison for these value types...
//                validateAllComparison("timestamp");
                validateAllComparison("byte");
                validateAllComparison("short");
                validateAllComparison("int");
                validateAllComparison("long");
                validateAllComparison("float");
                validateAllComparison("double");

                failPreparedWithIncompatibleTypes("select \"boolean\" < \"boolean\" from alltypes;");
                failPreparedWithIncompatibleTypes("select \"binary\" < \"binary\" from alltypes;");

                validateEqualComparison("binary");
                validateEqualComparison("boolean");
        }

        private void validateAllComparison(String fieldName) throws Exception {
                getFullyValidatedStatement("select \"" + fieldName + "\" > \"" + fieldName
                        + "\" from alltypes;");
                getFullyValidatedStatement("select \"" + fieldName + "\" >= \"" + fieldName
                        + "\" from alltypes;");
                validateEqualComparison(fieldName);
                getFullyValidatedStatement("select \"" + fieldName + "\" < \"" + fieldName
                        + "\" from alltypes;");
                getFullyValidatedStatement("select \"" + fieldName + "\" <= \"" + fieldName
                        + "\" from alltypes;");
        }

        private void validateEqualComparison(String fieldName) throws Exception {
                getFullyValidatedStatement("select \"" + fieldName + "\" = \"" + fieldName
                        + "\" from alltypes;");
                getFullyValidatedStatement("select \"" + fieldName + "\" != \"" + fieldName
                        + "\" from alltypes;");
                getFullyValidatedStatement("select \"" + fieldName + "\" <> \"" + fieldName
                        + "\" from alltypes;");
        }

        @Test
        public void testFieldAliasUsageInWhere() throws Exception {
                failPreparedWithSemanticException("select string as length "
                        + "from alltypes where length = 'r' and string = 'e';");
        }

        @Test
        public void testBetweenClauseTypes() throws Exception {
                failPreparedWithIncompatibleTypes("select * from alltypes where \"int\" not between 3 and 'e';");
        }

        @Test
        public void testInClauseTypes() throws Exception {
                failPreparedWithIncompatibleTypes("select * from alltypes where \"int\" not in (3, 5, 'e');");
                getValidatedStatement("select * from alltypes where \"int\" not in (3, 5, null);");
        }

        @Test
        public void testLikeTypes() throws Exception {
                failPreparedWithIncompatibleTypes("select * from alltypes "
                        + "where \"int\" like 'a%';");
                failPreparedWithIncompatibleTypes("select * from alltypes "
                        + "where string not like \"int\";");
                getValidatedStatement("select * from alltypes "
                        + "where string like string;");
                getValidatedStatement("select * from alltypes "
                        + "where string like 'string';");
        }

        @Test
        public void testDelete() throws Exception {
                failWithParseException("delete from 'AllTypes';");
                failPreparedWithIncompatibleTypes("delete from alltypes where \"int\"='e';");
                failPreparedWithSemanticException("delete from alltypes where \"Int\"=3;");
                failPreparedWithSemanticException("delete from alltypes where AllTypes.\"int\"=3;");
                failPreparedWithIncompatibleTypes("delete from alltypes where strlength(string)='3';");
                getValidatedStatement("delete from alltypes where string || 'd' = 'adadsd';");
                getValidatedStatement("delete from alltypes where \"int\"=3;");
                getValidatedStatement("delete from alltypes where alltypes.\"int\"=3;");
        }

        @Test
        public void testDrop() throws Exception {
                failWithNoSuchTableException("drop table AllTypes;");
                failWithNoSuchTableException("drop table alltypes, Gis;");
                getValidatedStatement("drop table alltypes, gis;");
        }

        @Test
        public void testDropSchema() throws Exception {
                failPreparedWithSemanticException("drop schema AllTypes;");
                failPreparedWithSemanticException("drop schema PUBLIC, Gis;");
                getValidatedStatement("drop schema PUBLIC;");
        }

        @Test
        public void testGroupBy() throws Exception {
                // INT field not found in group by
                failPreparedWithSemanticException("select \"int\" from alltypes "
                        + "group by \"Int\";");
                // int='e' type mistmatch
                failWithIncompatibleTypes("select \"int\" from alltypes "
                        + "group by \"int\" having \"int\" = 'e';");
                // INT field not found in having
                failPreparedWithSemanticException("select \"int\" from alltypes "
                        + "group by \"int\" having \"Int\" = 4;");
                // select fields reference not grouped attributes
                failPreparedWithSemanticException("select string from alltypes "
                        + "group by \"int\" having \"int\" = 4;");
                // having references not grouped attributes
                failPreparedWithSemanticException("select \"int\" from alltypes "
                        + "group by \"int\" having strign = 'e';");
                // mixing aggregated and not aggregated
                failPreparedWithSemanticException("select count(\"int\"), string :: int from "
                        + "alltypes group by \"int\" having string = 'e';");
                // non aggregated type
                failPreparedWithSemanticException("select t1.\"int\" from alltypes t1, "
                        + "alltypes t2 group by t2.\"int\";");
                // Selecting non grouped fields
                failPreparedWithSemanticException("select string from alltypes group by \"int\";");
                // Star select with group by
                failPreparedWithSemanticException("select * from alltypes group by \"int\";");

                getFullyValidatedStatement("select t1.\"int\" as st1, sum(t2.\"int\") as st2 from "
                        + "alltypes t1, alltypes t2 group by st1 having st1 = 3;");
                getFullyValidatedStatement("select sum(string :: int) as st from "
                        + "alltypes group by \"int\" having st = 3;");
                getFullyValidatedStatement("select sum(t1.\"int\") as st1, sum(t2.\"int\") as st2 from "
                        + "alltypes t1, alltypes t2 group by t2.\"int\" having st1 = 3;");
                getFullyValidatedStatement("select \"int\" from alltypes "
                        + "group by \"int\" having \"int\"=5;");
                getFullyValidatedStatement("select \"int\" from alltypes t"
                        + " group by t.\"int\";");
                getFullyValidatedStatement("select 2*\"int\" from alltypes "
                        + "group by \"int\" having \"int\"=5;");
        }

        @Test
        public void testInsert() throws Exception {
                getValidatedStatement("insert into alltypes (\"int\") values (4);");
                getValidatedStatement("insert into gis values ('2', '2');");
                getValidatedStatement("insert into gis (id, gis) values ('2', '2');");

                // field-value count mistmatch
                failPreparedWithSemanticException("insert into gis values ('2', '2', '2');");
                // table not found
                failPreparedWithSemanticException("insert into alltypes values (1);");
                // field not found
                failPreparedWithSemanticException("insert into gis (id, Gis) values ('a', 'a');");
                // Type mistmatch
                failPreparedWithIncompatibleTypes("insert into gis (id, gis) values ('a', 4);");
                // field references in values
                failWithSemanticException("insert into gis values ('2', id);");
        }

        @Test
        public void testInsertSelect() throws Exception {
                getValidatedStatement("insert into alltypes (\"int\") select 1, 18 from toto;");

                // field-value count mistmatch
                failPreparedWithSemanticException("insert into gis select * from values ('2', '2', '2') toto;");
                // table not found
                failPreparedWithSemanticException("insert into alltypes select * from values (1) toto;");
                // field not found
                failPreparedWithSemanticException("insert into gis (id, Gis) select * from values ('a', 'a') toto;");
                // Type mistmatch
                failPreparedWithIncompatibleTypes("insert into gis (id, gis) select * from values ('a', 4) toto;");
                // field references in values
                failWithSemanticException("insert into gis select * from values ('2', id) toto;");
        }

        @Test
        public void testIs() throws Exception {
                failWithNoSuchTableException("select * from Gis where id is not null;");
                getValidatedStatement("select * from gis where id is not null;");
                getValidatedStatement("select * from gis where id is null;");
        }

        @Test
        public void testOrderBy() throws Exception {
                failPreparedWithSemanticException("select * from gis order by Id;");
                failPreparedWithSemanticException("select * from gis order by Gis.id;");
                getValidatedStatement("select * from gis, alltypes "
                        + "order by gis.id;");
                getValidatedStatement("select * from gis g, alltypes "
                        + "order by g.id;");
                getValidatedStatement("select * from alltypes order by \"float\";");
                getValidatedStatement("select -2*\"float\" as myfloat from alltypes "
                        + "order by myfloat;");
                // order by non selected field
                getValidatedStatement("select \"float\" as myfloat from alltypes "
                        + "order by int;");

                // order by field outside group
                failPreparedWithSemanticException("select \"float\" as myfloat from alltypes "
                        + "group by \"float\" order by int;");
                getValidatedStatement("select \"float\" as myfloat from alltypes "
                        + "group by \"float\" order by \"float\";");
        }

        @Test
        public void testGroupAndOrderBy() throws Exception {
                getValidatedStatement("select alltypes.string " + "from alltypes "
                        + "group by alltypes.string " + "order by alltypes.string;");
                getValidatedStatement("select alltypes.string " + "from alltypes "
                        + "group by alltypes.string " + "order by string;");
                getValidatedStatement("select alltypes.string " + "from alltypes "
                        + "group by string " + "order by alltypes.string;");
                getValidatedStatement("select string " + "from alltypes "
                        + "group by alltypes.string " + "order by alltypes.string;");
                getValidatedStatement("select string " + "from alltypes "
                        + "group by alltypes.string " + "order by string;");
                getValidatedStatement("select string " + "from alltypes "
                        + "group by string " + "order by alltypes.string;");
                getValidatedStatement("select string " + "from alltypes "
                        + "group by string " + "order by string;");
        }

        @Test
        public void testUnion() throws Exception {
                failPreparedWithSemanticException("select * from gis union select * from alltypes;");
                getFullyValidatedStatement("select * from gis union select * from gis;");
        }

        @Test
        public void testUpdate() throws Exception {
                getValidatedStatement("update gis set id = '3';");
                getValidatedStatement("update gis set id = '3', "
                        + "gis = 'patatagis' where id='2';");
                // Field not found in assignment
                failPreparedWithSemanticException("update gis set Id = 'Id' + 1;");
                // Type miss match in assignment
                failPreparedWithIncompatibleTypes("update gis set id = true;");
                // Field not found in where
                failPreparedWithSemanticException("update gis set id = '1' "
                        + "where Id = '2';");
                // Type miss match in where
                failPreparedWithIncompatibleTypes("update gis set id = 'd' "
                        + "where gis=false;");
                // table not found
                failWithNoSuchTableException("update Gis set id='d';");
        }

        @Test
        public void testCreateAs() throws Exception {
                getValidatedStatement("create table table2 as select * from gis;");
                failWithNoSuchTableException("create table gis2 as select * from Gis;");
        }

        @Test
        public void testCreateTable() throws Exception {
                getFullyValidatedStatement("CREATE TABLE toto (ff int, gg double);");
                failPreparedWithSemanticException("CREATE TABLE toto (ff tutu);");

                getValidatedStatement("CREATE TABLE toto (ff int not null);");
                getValidatedStatement("CREATE TABLE toto (ff int primary key);");
                getValidatedStatement("CREATE TABLE toto (ff int unique);");
        }

        @Test
        public void testAggregatedFunctions() throws Exception {
                // id field not found
                failPreparedWithSemanticException("select avg(id), avg(\"double\") "
                        + "from alltypes group by \"boolean\";");

                getFullyValidatedStatement("select avg(\"int\"), avg(\"double\") "
                        + "from alltypes group by \"boolean\";");

                // aggregate without from
                failWithSemanticException("select avg(id);");

                // aggregated in where
                failPreparedWithSemanticException("select * from alltypes where avg(\"int\")=0;");

                getFullyValidatedStatement("select \"int\", string, avg(\"double\") from"
                        + " alltypes where \"boolean\" group by \"int\", string having \"int\"=3;");

        }

        @Test
        public void testFunctions() throws Exception {
                // Non existing function in where clause
                failPreparedWithSemanticException("select * "
                        + "from alltypes where notexists(\"int\");");

                // Non existing function in selection
                failPreparedWithSemanticException("select notexists(\"int\") from alltypes;");

                // Custom query in where clause
                failPreparedWithSemanticException("select * "
                        + "from alltypes where st_explode(alltypes,geometry);");

                getFullyValidatedStatement("select * "
                        + "from alltypes where strlength(string) > 0;");
                getFullyValidatedStatement("select strlength(string) " + "from alltypes;");

        }

        @Test
        public void testValidateCustomQueries() throws Exception {
                SumQuery query = new SumQuery();
                if (dsf.getFunctionManager().getFunction(query.getName()) == null) {
                        dsf.getFunctionManager().addFunction(SumQuery.class);
                }
                getValidatedStatement("CALL register('file', 'filename');");
                getValidatedStatement("select * from sumquery(gis);");
                getValidatedStatement("select * from sumquery(select * from gis where id = '5');");
                getValidatedStatement("select * from sumquery(select * from gis where id = '5', 'id');");

                // wrong number of arguments
                failWithIncompatibleTypes("select * from sumquery(select * from gis where id = '5', 3,6);");
                // wrong type of arguments
                failWithIncompatibleTypes("select * from sumquery(select * from gis where id = '5', 4);");
                // wrong number of tables
                failPreparedWithSemanticException("select * from sumquery(gis, alltypes, 3);");
                // type error in where
                failWithIncompatibleTypes("select * from sumquery(select * from gis where id = 5, 4);");
                // type error in argument
                failWithIncompatibleTypes("select * from sumquery(gis, 6+3*'e');");
                // field in argument
                failWithSemanticException("select * from sumquery(gis, 6+3*myField);");
        }

        @Test
        public void testProductOfThreeTables() throws Exception {
                getValidatedStatement("select t3.\"timestamp\", t2.string, t1.id "
                        + "from gis t1, alltypes t2, hsqldb t3;");
                getValidatedStatement("select a.\"date\" as d1, h.\"date\" as h2, gis "
                        + "from alltypes a, gis g, hsqldb h "
                        + "where a.string = g.gis AND h.string= g.gis;");
        }

        @Test
        public void testDifferentNumberOfFields() throws Exception {
                getValidatedStatement("select t2.string, t1.id "
                        + "from gis t1, alltypes t2;");
        }

        @Test
        public void testAggregatedFunctionDefaultName() throws Exception {
                SQLStatement p = getValidatedStatement("select count(*) from alltypes;");
                p.setDataSourceFactory(dsf);
                p.prepare();
                Metadata m1 = p.getResultMetadata();
                p.cleanUp();
                // default name if the name of the aggregate function
                // incremented only if necessary
                assertEquals("Count", m1.getFieldName(0));
        }

        @Test
        public void testGroupByWhereFullyClassifiedProj() throws Exception {
                getValidatedStatement("select t.\"int\" from alltypes t"
                        + " where \"int\"=2 group by \"int\";");
        }

        @Test
        public void testCreateDropIndex() throws Exception {
                getValidatedStatement("create index on alltypes (\"int\");");
                getValidatedStatement("drop index on alltypes (\"int\");");
                failWithNoSuchTableException("create index on allTypes (\"int\");");
                
                SQLStatement st = getFullyValidatedStatement("create index on alltypes (rint);");
                
                // index creation can only fail during execution
                // this is a limitation of the current SourceManager
                try {
                        getFullyValidatedStatement("create index on alltypes (rint);").execute();
                        fail();
                } catch (DriverException e) {
                        if (!(e.getCause() instanceof UnknownFieldException)) {
                                fail();
                        }
                } finally {
                        st.cleanUp();
                }
                
                failWithNoSuchTableException("drop index on allTypes (\"int\");");
        }

        @Test
        public void regressionTest690() throws Exception {
                // regression test for #690
                dsf.executeSQL("CREATE TABLE toto (field1 INT, field2 TEXT);");
                failPreparedWithSemanticException("SELECT *, 42 AS field1 FROM toto;");
                getFullyValidatedStatement("SELECT *, 42 AS field3 FROM toto;");

                dsf.getSourceManager().delete("toto");
        }
        
        @Test
        public void regressionTestInitRootOperation() throws Exception {
                getFullyValidatedStatement("UPDATE alltypes SET \"int\" = abs(\"int\");");
        }

        private SQLStatement getValidatedStatement(String sql) throws Exception {
                return Engine.parse(sql, dsf.getProperties());
        }

        private SQLStatement getFullyValidatedStatement(String sql) throws Exception {
                final SQLStatement st = Engine.parse(sql, dsf.getProperties());
                st.setDataSourceFactory(dsf);
                st.prepare();
                return st;
        }
}
