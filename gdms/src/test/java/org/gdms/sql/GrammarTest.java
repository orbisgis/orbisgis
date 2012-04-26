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
package org.gdms.sql;

import org.junit.Test;

import static org.junit.Assert.fail;

import org.gdms.sql.engine.Engine;
import org.gdms.sql.engine.ParseException;
import org.gdms.sql.engine.SemanticException;

public class GrammarTest {
        
        @Test
        public void testScriptWithWrongCharacters() throws Exception {
                notParse("<!\nselect * from mytable;");

                notParse("select * from mytable; totootereazjzgj");
        }

        @Test
        public void testAddPrimaryKey() throws Exception {
                parse("alter table toto add primary key (tata);");
                notParse("alter table toto add primarykey (tata);");
                notParse("alter table toto add primary key tata;");

        }

        @Test
        public void testRenameTable() throws Exception {
                parse("alter table toto rename to tata;");
                notParse("alter table toto renameto tata;");
        }

        @Test
        public void testRenameColumn() throws Exception {
                parse("alter table toto rename column test to gwen;");
                parse("alter table toto rename column test to test;");
        }

        @Test
        public void testAddColumn() throws Exception {
                parse("alter table toto add column test text;");
                parse("alter table toto add column test numeric;");
                parse("alter table toto add column test integer;");
                notParse("alter table toto, tata add column test text;");
                notParse("alter table tata add column test text, gwen numeric;");
        }

        @Test
        public void testDropColumn() throws Exception {
                parse("alter table toto drop column test;");
                parse("alter table toto drop column test restrict;");
                parse("alter table toto drop column test cascade;");

        }

        @Test
        public void testExcept() throws Exception {
                parse("select * except myfield from gis;");
                parse("select a.* except field from gis a;");
        }

        @Test
        public void testExists() throws Exception {
                parse("SELECT * FROM toto WHERE EXISTS (SELECT * FROM tutu);");
                parse("SELECT * FROM toot WHERE EXISTS (SELECT aa, oo FROM tutu WHERE toot.tt = tutu.tt)"
                        + " AND myOtherField IS NOT NULL;");
                notParse("SELECT * FROM toto WHERE EXISTS (tata);");
                notParse("SELECT * FROM toto WHERE EXISTS SELECT * FROM tutu;");
        }

        @Test
        public void testOrderBy() throws Exception {
                parse("select * from gis where a=3 order by a;");
                parse("select * from gis order by ST_area(geom);");
                parse("select * from gis order by ST_area(geom) desc;");
                parse("select * from gis where a=3 order by ST_area(the_geom);");
        }

        @Test
        public void testGroupBy() throws Exception {
                parse("select * from gis where a=3 group by a having a=4;");
                notParse("select * from gis where a=3 group by a desc having a=4;");
        }

        @Test
        public void testScript() throws Exception {
                parse("select * from gis; select * from alltypes, (select * from gis) as tutu where false;");
        }

        @Test
        public void testQuotedId() throws Exception {
                parse("select NAME from mytable;");
                parse("select Name from mytable;");
                parse("select \"Name\" from mytable;");
                parse("select * from mytable_0;");
                notParse("select _field from table;");
                parse("select \"table\".\"_field\" from \"table\";");
                parse("select \"table\".\"_field\" , \"table\".\"_field2\" from \"table\";");
        }

        @Test
        public void testFieldAlias() throws Exception {
                parse("select field as alias from mytable;");
        }

        @Test
        public void testSelectExpression() throws Exception {
                parse("select FDECIMAL=FDECIMAL from mytable;");
        }

        @Test
        public void testTableAlias() throws Exception {
                parse("select * from mytable as table_alias;");
                parse("select * from mytable table_alias;");
        }

        @Test
        public void testwhereCondition() throws Exception {
                parse("select * from mytable where a=0.002;");
                parse("select * from mytable where a like '';");
                parse("select * from mytable where a is not null;");
                parse("select * from mytable where a in(3);");
                parse("select * from mytable where a not between 4 and 6;");
                parse("select * from mytable where exists (select * from mytable);");
        }

        @Test
        public void testHaving() throws Exception {
                parse("select * from mytable group by field_name having a='3';");
                parse("select * from mytable group by field_name having a like '3';");
                parse("select * from mytable group by field_name having a is not null;");
                parse("select * from mytable group by field_name having a in (3, 6, 18);");
                parse("select field_name, count(*) from mytable group by field_name having a in (3, 6, 18);");
        }

        @Test
        public void testMixedStar() throws Exception {
                parse("select *, field from mytable;");
                parse("select *, 3*field from mytable;");
                parse("select *, st_buffer(spatialField) from mytable;");
        }

        @Test
        public void testStarOfTable() throws Exception {
                parse("select mytable.*, 3*field from mytable;");
                notParse("select field from mytable where mytable.* = 2;");
        }

        @Test
        public void testStarInFunctionArguments() throws Exception {
                parse("select count(*) from mytable;");
        }

        @Test
        public void testLimitAndOffset() throws Exception {
                parse("select * from mytable limit 10 offset 4;");
                parse("select * from mytable offset 4;");
                parse("select * from mytable limit 10;");

                notParse("select * from mytable limit 10.3;");
                notParse("select * from mytable offset '4';");
        }

        @Test
        public void testDistinct() throws Exception {
                parse("select distinct * from mytable;");
                parse("select distinct ON (field) * from mytable;");
        }

        @Test
        public void testDelete() throws Exception {
                parse("delete from mytable where toto > 27;");
                parse("delete from mytable;");
                notParse("delete from table1, table2;");
        }

        @Test
        public void testDropTable() throws Exception {
                parse("drop table table1, table2, table3;");
                parse("drop table table1 purge;");
                parse("drop table table1, table2 purge;");
        }

        @Test
        public void testDropTableIfExists() throws Exception {
                parse("drop table if exists table1, table2, table3;");
                parse("drop table if exists table1 purge;");
        }

        @Test
        public void testDropSchema() throws Exception {
                parse("drop schema table1, table2, table3;");
                parse("drop schema table1 purge;");
                parse("drop schema if exists table1, table2, table3;");
                parse("drop schema if exists table1 purge;");
        }

        @Test
        public void testUpdateWithExpression() throws Exception {
                parse("update table_name set field = 3 * abs(field);");
        }

        @Test
        public void testUpdateWhereSelect() throws Exception {
                parse("update table_name set field = 3 where a In (select * from toto);");
        }

        @Test
        public void testCaseInsensitiveKeywords() throws Exception {
                parse("seLect * frOm a;");
                parse("select * from toto6555;");
                notParse("select * from 6555toto;");
                notParse("select * from _6555toto;");
                notParse("select * from 6555;");
        }

        @Test
        public void testFunctionIsNull() throws Exception {
                parse("select * from a, b where st_intersection(a.the_geom,b.the_geom) is null;");
        }

        @Test
        public void testInsert() throws Exception {
                notParse("insert into table1, table2 (field) values ('4');");
                notParse("insert into mytable (field) values (3 * abs(field));");
                parse("insert into mytable (field1, field2, field3) values (1, 2, 3);");
        }

        @Test
        public void testInsertSelect() throws Exception {
                notParse("insert into table1, table2 (field) select * from tutu;");
                parse("insert into mytable (field1, field2, field3) select * from tata order by something;");
        }

        @Test
        public void testColumnSelectionInInsert() throws Exception {
                notParse("insert into table1 (field as alias) values ('4');");
                notParse("insert into table1 (distinct field) values ('4');");
                notParse("insert into table1 (field) values (field as alias);");
                notParse("insert into table1 (field) values (*);");
        }

        @Test
        public void testUpdateSeveralTables() throws Exception {
                notParse("update table1, table2 set field='4';");
        }

        @Test
        public void testCustomQueries() throws Exception {
                parse("select * from st_CreateGrid(table1, 4, 1.234);");
                parse("select * from st_CreateGrid(table1, 1.234, 4);");
                parse("select * from st_CreateGrid(table1);");

                parse("CALL register ('file.shp', alias);");
                parse("CALL register ('127.0.0.1', 5432, 'testdb');");
                parse("CALL register ('127.0.0.1', 5432, 'testdb', 'tableName', 'alias');");
                parse("CALL unregister ('127.0.0.1', 5432, 'testdb');");
                parse("CALL unregister ('alias');");
                parse("CALL unregister();");
        }

        @Test
        public void testCreateTable() throws Exception {
                parse("CREATE TABLE toto (tata int, tutu double);");
                notParse("CREATE TABLE toto;");
                notParse("CREATE TABLE toto ();");
                parse("CREATE TABLE toto (tata int not null);");
                parse("CREATE TABLE toto (tata int unique);");
                parse("CREATE TABLE toto (tata int primary key);");
        }

        @Test
        public void testCreateTableAs() throws Exception {
                parse("create table table2 as select * from st_CreateGrid(table1);");
                parse("create table table2 as select * from table1;");
        }

        @Test
        public void testCreateViewAs() throws Exception {
                parse("create view view1 as  select * from st_CreateGrid(table1);");
                parse("create view view2 as select * from table1;");
        }

        @Test
        public void testCreateFunction() throws Exception {
                parse("CREATE FUNCTION tata AS 'somestring' LANGUAGE 'java';");
                parse("CREATE OR REPLACE FUNCTION tata AS 'somestring' LANGUAGE 'java';");
                notParse("CREATE FUNCTION tata AS 'somestring' LANGUAGE 'toto';");
                notParse("CREATE OR REPLACE FUNCTION tata AS 'somestring' LANGUAGE 'toto';");
                notParse("CREATE FUNCTION tata LANGUAGE 'java';");
                notParse("CREATE FUNCTION tata AS 'somestring';");
                notParse("CREATE FUNCTION tata AS 'somestring' LANGUAGE 18;");
                notParse("CREATE FUNCTION tata AS 42' LANGUAGE 'toto;");
        }

        @Test
        public void testScriptWithComments() throws Exception {
                parse("select * from mytable;-- comment\nselect * from mytable;");
                parse("select * from mytable;/* com\nment\n*/select * from mytable;");
        }

        @Test
        public void testInSubQuery() throws Exception {
                parse("select * from mytable where myfield in (3, 4);");

                parse("SELECT * FROM myTable WHERE myField in (SELECT toto FROM tata);");
                parse("UPDATE toto SET va = 17 WHERE vert IN (SELECT tt FROM tutu);");
                notParse("SELECT * FROM myTable WHERE myField in (UPDATE toto SET tata = 1);");

        }

        @Test
        public void testSet() throws Exception {
                parse("SET toto TO 'value';");
                parse("SET toto = 'value';");
                parse("SET toto.tata.tutu = '42';");
                parse("SET toto TO DEFAULT;");
                parse("SET toto = DEFAULT;");

                notParse("SET toto = value;");
                notParse("SET toto = 18.2;");
                notParse("SET toto TO 42;");
        }

        @Test
        public void testReset() throws Exception {
                parse("RESET toto;");
                parse("RESET tutu.toto;");
        }

        @Test
        public void testShow() throws Exception {
                parse("SHOW toto;");
                parse("SHOW tutu.toto;");
        }

        @Test
        public void testSelectAlone() throws Exception {
                parse("SELECT 18, 'toto', abs(-18);");
                notParse("SELECT myField, 'eerr'");
                notParse("SELECT 18, 'aa' ORDER BY toto");
                notParse("SELECT 18, 'aa' LIMIT 1");
        }

        private void notParse(String sql) {
                try {
                        Engine.parse(sql);
                        fail();
                } catch (Exception e) {
                }
        }

        private void parse(String sql) throws ParseException, SemanticException {
                Engine.parse(sql);
        }
}
