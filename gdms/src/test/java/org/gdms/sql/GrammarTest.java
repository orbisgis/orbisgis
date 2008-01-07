package org.gdms.sql;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import org.gdms.sql.parser.ParseException;
import org.gdms.sql.parser.SQLEngine;

public class GrammarTest extends TestCase {

	public void testParserBug() throws Exception {
		parse("select _field from table;");
	}

	public void testMixedStar() throws Exception {
		parse("select *, field from table;");
		parse("select *, 3*field from table;");
		parse("select *, buffer(spatialField) from table;");
	}

	public void testFieldAlias() throws Exception {
		parse("select field as alias from mytable;");
	}

	public void testTableAlias() throws Exception {
		parse("select * from mytable as table_alias;");
		parse("select * from mytable table_alias;");
	}

	public void testwhereCondition() throws Exception {
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

	public void testStarInFunctionArguments() throws Exception {
		parse("select count(*) from mytable;");
	}

	public void testLimitAndOffset() throws Exception {
		parse("select * from mytable limit 10 offset 4;");
	}

	public void testDistinct() throws Exception {
		parse("select distinct * from mytable;");
		parse("select distinct(field) from mytable;");
	}

	public void testDelete() throws Exception {
		parse("delete from mytable where exists (select * from mytable);");
	}

	public void testDropTable() throws Exception {
		parse("drop table1, table2, table3;");
	}

	public void testUpdateWithExpression() throws Exception {
		parse("update table_name set field = 3 * abs(field);");
	}

	public void testInsertExpression() throws Exception {
		parse("insert into mytable (field) values (3 * abs(field));");
	}

	public void testCaseInsensitiveKeywords() throws Exception {
		parse("seLect * frOm a;");
	}

	public void testFunctionIsNull() throws Exception {
		parse("select * from a, b where intersection(a.the_geom,b.the_geom) is null;");
	}

	public void testDeleteSeveralTables() throws Exception {
		notParse("delete from table1, table2;");
	}

	public void testInsertSeveralTables() throws Exception {
		notParse("insert into table1, table2 (field) values ('4');");
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

	private void notParse(String sql) {
		SQLEngine se = new SQLEngine(new ByteArrayInputStream(sql.getBytes()));
		try {
			System.out.println("\n\n\n\nPARSING: " + sql);
			se.SQLStatement();
			new RuntimeException("This instruction should raise a parse error")
					.printStackTrace();
		} catch (ParseException e) {
		}
	}

	private void parse(String sql) throws ParseException {
		SQLEngine eng = new SQLEngine(new ByteArrayInputStream(sql.getBytes()));
		eng.SQLStatement();
	}

}
