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
package org.gdms.sql;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import org.gdms.data.DataSourceFactory;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.parser.SQLEngine;
import org.gdms.sql.parser.TokenMgrError;
import org.gdms.sql.strategies.SQLProcessor;

public class GrammarTest extends TestCase {

	public void testScriptWithWrongCharacters() throws Exception {
		SQLProcessor proc = new SQLProcessor(new DataSourceFactory());
		try {
			proc.getScriptInstructions("<!\nselect * from mytable;");
			assertTrue(false);
		} catch (ParseException e) {
		}
	}

	public void testAddPrimaryKey() throws Exception {
		parse("alter table toto add primary key (tata);");
		notParse("alter table toto add primarykey (tata);");
		notParse("alter table toto add primary key tata;");

	}

	public void testRenameTable() throws Exception {
		parse("alter table toto rename to tata;");
		notParse("alter table toto renameto tata;");
	}

	public void testRenameColumn() throws Exception {
		parse("alter table toto rename column test to gwen;");
		parse("alter table toto rename column test to test;");
	}

	public void testAddColumn() throws Exception {
		parse("alter table toto add column test text;");
		parse("alter table toto add column test numeric;");
		parse("alter table toto add column test integer;");
		notParse("alter table toto add column  integer test;");
		notParse("alter table toto, tata add column test text;");
		notParse("alter table tata add column test text, gwen numeric;");
	}

	public void testDropColumn() throws Exception {
		parse("alter table toto drop column test;");
		parse("alter table toto drop column test restrict;");
		parse("alter table toto drop column test cascade;");

	}

	public void testExcept() throws Exception {
		parse("select *{except myfield} from gis;");
		parse("select a.*{except field} from gis a;");
	}

	public void testOrderBy() throws Exception {
		parse("select * from gis where a=3 order by a;");
		parse("select * from gis order by area(the_geom);");
		parse("select * from gis where a=3 order by area(the_geom);");
	}

	public void testGroupBy() throws Exception {
		parse("select * from gis where a=3 group by a having a=4;");
		notParse("select * from gis where a=3 group by a desc having a=4;");
	}

	public void testScript() throws Exception {
		parse("select * from gis;select * from alltypes "
				+ "where exists select * from gis");
	}

	public void testQuotedId() throws Exception {
		parse("select NAME from mytable;");
		parse("select Name from mytable;");
		parse("select * from mytable_0;");
		notParse("select _field from table;");
		notParse("select \"table\".\"_field\" from \"table\";");
		notParse("select \"table\".\"_field\" , \"table\".\"_field2\" from \"table\";");
	}

	public void testFieldAlias() throws Exception {
		parse("select field as alias from mytable;");
	}

	public void testSelectExpression() throws Exception {
		parse("select FDECIMAL=FDECIMAL from mytable;");
	}

	public void testTableAlias() throws Exception {
		parse("select * from mytable as table_alias;");
		parse("select * from mytable table_alias;");
	}

	public void testwhereCondition() throws Exception {
		parse("select * from mytable where a=0.002;");
		parse("select * from mytable where a like '';");
		parse("select * from mytable where a is not null;");
		parse("select * from mytable where a in(3);");
		parse("select * from mytable where a not between 4 and 6;");
		parse("select * from mytable where exists (select * from mytable);");
	}

	public void testHaving() throws Exception {
		parse("select * from mytable group by field_name having a='3';");
		parse("select * from mytable group by field_name having a like '3';");
		parse("select * from mytable group by field_name having a is not null;");
		parse("select * from mytable group by field_name having a in (3, 6, 18);");
		parse("select field_name, count(*) from mytable group by field_name having a in (3, 6, 18);");
	}

	public void testMixedStar() throws Exception {
		parse("select *, field from mytable;");
		parse("select *, 3*field from mytable;");
		parse("select *, buffer(spatialField) from mytable;");
	}

	public void testStarOfTable() throws Exception {
		parse("select mytable.*, 3*field from mytable;");
		notParse("select field from mytable where mytable.* = 2;");
	}

	public void testStarInFunctionArguments() throws Exception {
		parse("select count(*) from mytable;");
	}

	public void testLimitAndOffset() throws Exception {
		parse("select * from mytable limit 10 offset 4;");
		parse("select * from mytable offset 4;");
		parse("select * from mytable limit 10;");

		notParse("select * from mytable limit 10.3;");
		notParse("select * from mytable offset '4';");
	}

	public void testDistinct() throws Exception {
		parse("select distinct * from mytable;");
		parse("select distinct(field) from mytable;");
	}

	public void testDelete() throws Exception {
		parse("delete from mytable where exists (select * from mytable);");
		parse("delete from mytable;");
		notParse("delete from table1, table2;");
	}

	public void testDropTable() throws Exception {
		parse("drop table table1, table2, table3;");
	}

	public void testUpdateWithExpression() throws Exception {
		parse("update table_name set field = 3 * abs(field);");
	}

	public void testCaseInsensitiveKeywords() throws Exception {
		parse("seLect * frOm a;");
	}

	public void testFunctionIsNull() throws Exception {
		parse("select * from a, b where intersection(a.the_geom,b.the_geom) is null;");
	}

	public void testInsert() throws Exception {
		notParse("insert into table1, table2 (field) values ('4');");
		parse("insert into mytable (field) values (3 * abs(field));");
		parse("insert into mytable (field1, field2, field3) values (1, 2, 3);");
	}

	public void testColumnSelectionInInsert() throws Exception {
		notParse("insert into table1 (field as alias) values ('4');");
		notParse("insert into table1 (distinct field) values ('4');");
		notParse("insert into table1 (field) values (field as alias);");
		notParse("insert into table1 (field) values (*);");
	}

	public void testUpdateSeveralTables() throws Exception {
		notParse("update table1, table2 set field='4';");
	}

	public void testCustomQueries() throws Exception {
		parse("select CreateGrid(4, 1.234) from table1;");
		parse("select CreateGrid(1.234, 4) from table1;");
		parse("select CreateGrid() from table1;");

		parse("select toLine() from table1;");
		parse("select toLine(spatialFieldName) from table1;");

		parse("select register ('file.shp', alias);");
		parse("select register ('127.0.0.1', 5432, 'testdb');");
		parse("select register ('127.0.0.1', 5432, 'testdb', 'tableName', 'alias');");
		parse("select unregister ('127.0.0.1', 5432, 'testdb');");
		parse("select unregister ('alias');");
		parse("select unregister;");
	}

	public void testCreateTableAs() throws Exception {
		parse("create table table2 as select CreateGrid() from table1;");
		parse("create table table2 as select * from table1;");
	}

	public void testCreateViewAs() throws Exception {
		parse("create view view1 as select CreateGrid() from table1;");
		parse("create view view2 as select * from table1;");
	}

	public void testScriptWithComments() throws Exception {
		parse("select * from mytable;-- comment\nselect * from mytable;");
		parse("select * from mytable;/* com\nment\n*/select * from mytable;");
	}

	public void testIntoAsCreateTable() throws Exception {
		parse("select * from mytable into toto;");
		parse("select * from mytable into toto, tata;");
		notParse("create table toto as select * from mytable into toto;");
	}

	/**
	 * Add subquery in SQL grammar
	 *
	 * @throws Exception
	 */
	public void testInSubQuery() throws Exception {
		parse("select * from mytable where myfield in (3, 4);");

	}

	private void notParse(String sql) {
		SQLEngine se = new SQLEngine(new ByteArrayInputStream(sql.getBytes()));
		try {
			se.SQLStatement();
			assertTrue(false);
		} catch (ParseException e) {
		} catch (TokenMgrError e) {
		}
	}

	private void parse(String sql) throws ParseException {
		SQLEngine eng = new SQLEngine(new ByteArrayInputStream(sql.getBytes()));
		eng.SQLStatement();
	}

}
