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
import java.io.File;
import java.util.HashSet;

import junit.framework.TestCase;

import org.gdms.data.AllTypesObjectDriver;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.db.DBSource;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.gdms.source.SourceManager;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.parser.SQLEngine;
import org.gdms.sql.parser.SimpleNode;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.gdms.sql.strategies.LogicTreeBuilder;
import org.gdms.sql.strategies.Operator;
import org.gdms.sql.strategies.Preprocessor;
import org.gdms.sql.strategies.SemanticException;
import org.gdms.sql.strategies.SumQuery;

public class ProcessorTest extends TestCase {

	private String dbFile;
	private Metadata allTypesMetadata;
	private static DataSourceFactory dsf;

	@Override
	public void setUp() throws Exception {
		dsf = new DataSourceFactory();
		dsf.setTempDir("src/test/resources/backup");
		SourceManager sm = dsf.getSourceManager();
		AllTypesObjectDriver omd = new AllTypesObjectDriver();
		allTypesMetadata = omd.getMetadata();
		sm.register("alltypes", omd);
		sm.register("gis", new File("src/test/resources/test.csv"));
		dbFile = "src/test/resources/backup/processortest";
		sm.register("hsqldb", new DBSource(null, 0, dbFile, "", "", "alltypes",
				"jdbc:h2"));
		if (!new File(dbFile + ".data.db").exists()) {
			dsf.executeSQL("create table hsqldb as select * from alltypes;");
		}

		ZeroArgsFunction fnc = new ZeroArgsFunction();
		if (FunctionManager.getFunction(fnc.getName()) == null) {
			FunctionManager.addFunction(ZeroArgsFunction.class);
		}
	}

	public void testNormalWithoutFrom() throws Exception {
		failWithSemanticException("select register;");
	}

	public void testScalarProduct() throws Exception {
		getValidatedPreprocessor("select t1.int from alltypes t1, alltypes t2;");
	}

	public void testGetResultMetadataStar() throws Exception {
		Preprocessor p = getValidatedPreprocessor("select * from alltypes;");
		Metadata m1 = p.getResultMetadata();
		compareMetadata(m1, allTypesMetadata);
		p = getValidatedPreprocessor("select t.* from alltypes t;");
		m1 = p.getResultMetadata();
		compareMetadata(m1, allTypesMetadata);
		p = getValidatedPreprocessor("select string as mystr, t.* from alltypes t;");
		m1 = p.getResultMetadata();
		DefaultMetadata m2 = new DefaultMetadata();
		m2.addField("mystr", Type.STRING);
		m2.addAll(allTypesMetadata);
		compareMetadata(m1, m2);
	}

	public void testStarOfNonExistingTable() throws Exception {
		failWithSemanticException("select foo.* from alltypes "
				+ "where alltypes.float > 2;");
	}

	public void testGetResultMetadataField() throws Exception {
		Preprocessor p = getValidatedPreprocessor("select gis from gis;");
		Metadata m1 = p.getResultMetadata();
		DefaultMetadata m2 = new DefaultMetadata();
		m2.addField("gis", Type.STRING);
		compareMetadata(m1, m2);
	}

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
		Preprocessor p = getValidatedPreprocessor("select \"" + field1
				+ "\"*\"" + field2 + "\" from alltypes;");
		Metadata m1 = p.getResultMetadata();
		assertTrue(m1.getFieldType(0).getTypeCode() == resultType);
		p = getValidatedPreprocessor("select \"" + field1 + "\"+\"" + field2
				+ "\" from alltypes;");
		m1 = p.getResultMetadata();
		assertTrue(m1.getFieldType(0).getTypeCode() == resultType);
		p = getValidatedPreprocessor("select \"" + field1 + "\"-\"" + field2
				+ "\" from alltypes;");
		m1 = p.getResultMetadata();
		assertTrue(m1.getFieldType(0).getTypeCode() == resultType);
	}

	public void testDivisionOperators() throws Exception {
		Preprocessor p = getValidatedPreprocessor("select 3/0 from alltypes;");
		Metadata m1 = p.getResultMetadata();
		assertTrue(m1.getFieldType(0).getTypeCode() == Type.INT);
		p = getValidatedPreprocessor("select 3.0/3 from alltypes;");
		m1 = p.getResultMetadata();
		assertTrue(m1.getFieldType(0).getTypeCode() == Type.DOUBLE);
	}

	private void compareMetadata(Metadata m1, Metadata m2)
			throws DriverException {
		assertTrue(m1.getFieldCount() == m2.getFieldCount());
		for (int i = 0; i < m1.getFieldCount(); i++) {
			assertTrue(m1.getFieldName(i).equals(m2.getFieldName(i)));
			Type t1 = m1.getFieldType(i);
			Type t2 = m2.getFieldType(i);
			assertTrue(t1.getTypeCode() == t2.getTypeCode());
			assertTrue(t1.getConstraints().length == t2.getConstraints().length);
			for (int j = 0; j < t1.getConstraints().length; j++) {
				Constraint c1 = t1.getConstraints()[j];
				Constraint c2 = t2.getConstraint(c1.getConstraintCode());
				assertTrue(c2 != null);
				assertTrue(c1.getConstraintCode() == c2.getConstraintCode());
				assertTrue(c1.getConstraintValue().equals(
						c2.getConstraintValue()));
			}
		}
	}

	public void testIncompatibleTypes() throws Exception {
		failWithIncompatibleTypes("select 3+'text' from alltypes;");
		failWithIncompatibleTypes("select 'text'+'text' from alltypes;");
		failWithIncompatibleTypes("select 3+true from alltypes;");
		failWithIncompatibleTypes("select * from alltypes where 3+'text'=4;");
		failWithIncompatibleTypes("select * from alltypes where 'text'+'text'=4;");
		failWithIncompatibleTypes("select * from alltypes where 3+true=4;");
	}

	public void testIncompatibleTypesInFunctions() throws Exception {
		failWithIncompatibleTypes("select avg(4, 6) from alltypes;");
		failWithIncompatibleTypes("select avg('a string') from alltypes;");
		failWithIncompatibleTypes("select avg(*) from alltypes;");
		failWithIncompatibleTypes("select concatenate('string', StringtoInt('3')) from alltypes;");
		getValidatedPreprocessor("select avg(3) from alltypes;");
		failWithIncompatibleTypes("select * from alltypes where concatenate('e')=3;");
		failWithIncompatibleTypes("select * from alltypes where concatenate(4)='afs';");
		failWithIncompatibleTypes("select * from alltypes where zeroargs(*)=3;");
		failWithIncompatibleTypes("select * from alltypes where concatenate('string', StringtoInt('3')) = 'asd';");
		getValidatedPreprocessor("select * from alltypes where concatenate('asd', 'asd') = 'asdasd';");
	}

	private void failWithIncompatibleTypes(String sql) throws Exception {
		try {
			getValidatedPreprocessor(sql);
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
	}

	private void failWithSemanticException(String sql) throws Exception {
		try {
			getValidatedPreprocessor(sql);
			assertTrue(false);
		} catch (SemanticException e) {
		}
	}

	public void testProjectionAlias() throws Exception {
		Preprocessor p = getValidatedPreprocessor("select double as mydoublE, "
				+ "string as \"mySTR\" from alltypes;");
		Metadata m = p.getResultMetadata();
		assertTrue(m.getFieldName(0).equals("mydouble"));
		assertTrue(m.getFieldName(1).equals("mySTR"));
	}

	public void testAmbiguousName() throws Exception {
		failWithSemanticException("select long "
				+ " from alltypes t1, alltypes t2;");
		getValidatedPreprocessor("select t1.long "
				+ " from alltypes t1, alltypes t2;");
		getValidatedPreprocessor("select t2.long "
				+ " from alltypes t1, alltypes t2;");
		getValidatedPreprocessor("select t1.* "
				+ " from alltypes t1, alltypes;");
	}

	public void testNonExistingIds() throws Exception {
		// non
		failWithSemanticException("select non.integer from alltypes;");
		// ier
		failWithSemanticException("select alltypes.ier from alltypes;");
		failWithSemanticException("select * from alltypes where non.integer=3;");
		// idnteger
		failWithSemanticException("select idnteger from alltypes;");
		failWithSemanticException("select * from alltypes where idnteger = 2;");
		// notexist
		failWithSemanticException("select * from notexist t1;");
		// thisfunctiondoes...
		failWithSemanticException("select max(thisfunctiondoesntexist"
				+ "(boolean)) from alltypes;");
		failWithSemanticException("select * "
				+ "from alltypes where thisfunctiondoesnotexist(boolean);");
		// boolean case
		failWithSemanticException("select max(\"booleaN\") from alltypes;");
		failWithSemanticException("select * from alltypes where avg(boolean) = 2;");
	}

	public void testDuplicatedTableReferences() throws Exception {
		failWithSemanticException("select t1.long "
				+ " from alltypes t1, alltypes t1;");
	}

	public void testGetTableReferences() throws Exception {
		Operator op = getOperator("select gis.* "
				+ "from alltypes, alltypes t1, gis;");
		String[] referencesTables = op.getReferencedTables();
		HashSet<String> tables = new HashSet<String>();
		tables.add("alltypes");
		tables.add("gis");
		for (String table : referencesTables) {
			assertTrue(tables.contains(table));
		}
	}

	public void testNameCollisions() throws Exception {
		try {
			getValidatedPreprocessor("select string, string "
					+ "from alltypes;");
			assertTrue(false);
		} catch (SemanticException e) {
		}
		try {
			getValidatedPreprocessor("select string, * " + "from alltypes;");
			assertTrue(false);
		} catch (SemanticException e) {
		}
	}

	public void testComparisonOperators() throws Exception {
		validateAllComparison("double");
		validateAllComparison("string");
		validateAllComparison("date");
		validateAllComparison("time");
		validateAllComparison("timestamp");
		validateAllComparison("byte");
		validateAllComparison("short");
		validateAllComparison("int");
		validateAllComparison("long");
		validateAllComparison("float");
		validateAllComparison("double");

		failWithIncompatibleTypes("select boolean < boolean from alltypes;");
		failWithIncompatibleTypes("select binary < binary from alltypes;");

		validateEqualComparison("binary");
		validateEqualComparison("boolean");
	}

	private void validateAllComparison(String fieldName) throws Exception {
		getValidatedPreprocessor("select " + fieldName + ">" + fieldName
				+ " from alltypes;");
		getValidatedPreprocessor("select " + fieldName + ">=" + fieldName
				+ " from alltypes;");
		validateEqualComparison(fieldName);
		getValidatedPreprocessor("select " + fieldName + "<" + fieldName
				+ " from alltypes;");
		getValidatedPreprocessor("select " + fieldName + "<=" + fieldName
				+ " from alltypes;");
	}

	private void validateEqualComparison(String fieldName) throws Exception {
		getValidatedPreprocessor("select " + fieldName + "=" + fieldName
				+ " from alltypes;");
		getValidatedPreprocessor("select " + fieldName + "!=" + fieldName
				+ " from alltypes;");
		getValidatedPreprocessor("select " + fieldName + "<>" + fieldName
				+ " from alltypes;");
	}

	public void testFieldAliasUsageInWhere() throws Exception {
		failWithSemanticException("select string as length "
				+ "from alltypes where length = 'r' and string = 'e';");
	}

	public void testBetweenClauseTypes() throws Exception {
		failWithIncompatibleTypes("select * from alltypes where int not between 3 and 'e';");
	}

	public void testInClauseTypes() throws Exception {
		failWithIncompatibleTypes("select * from alltypes where int not in (3, 5, 'e');");
		getValidatedPreprocessor("select * from alltypes where int not in (3, 5, null);");
	}

	public void testLikeTypes() throws Exception {
		failWithIncompatibleTypes("select * from alltypes "
				+ "where int like 'a%';");
		failWithIncompatibleTypes("select * from alltypes "
				+ "where string not like int;");
		getValidatedPreprocessor("select * from alltypes "
				+ "where string like string;");
		getValidatedPreprocessor("select * from alltypes "
				+ "where string like 'string';");
	}

	public void testDelete() throws Exception {
		failWithSemanticException("delete from \"AllTypes\";");
		failWithIncompatibleTypes("delete from alltypes where int='e';");
		failWithSemanticException("delete from alltypes where \"Int\"=3;");
		failWithSemanticException("delete from alltypes where \"AllTypes\".int=3;");
		failWithIncompatibleTypes("delete from alltypes where strlength(string)='3';");
		getValidatedPreprocessor("delete from alltypes where concatenate(string, 'd')='adadsd';");
		getValidatedPreprocessor("delete from alltypes where INT=3;");
		getValidatedPreprocessor("delete from alltypes where alltypes.int=3;");
	}

	public void testDrop() throws Exception {
		failWithSemanticException("drop table \"AllTypes\";");
		failWithSemanticException("drop table alltypes, \"Gis\";");
		getValidatedPreprocessor("drop table alltypes, gis;");
	}

	public void testGroupBy() throws Exception {
		// INT field not found in group by
		failWithSemanticException("select int from alltypes "
				+ "group by \"Int\";");
		// int='e' type mistmatch
		failWithIncompatibleTypes("select int from alltypes "
				+ "group by int having int = 'e';");
		// INT field not found in having
		failWithSemanticException("select int from alltypes "
				+ "group by int having \"Int\" = 4;");
		// select fields reference not grouped attributes
		failWithSemanticException("select string from alltypes "
				+ "group by int having int = 4;");
		// having references not grouped attributes
		failWithSemanticException("select int from alltypes "
				+ "group by int having strign = 'e';");
		// mixing aggregated and not aggregated
		failWithSemanticException("select count(int), StringtoInt(string) from "
				+ "alltypes group by int having string = 'e';");
		// non aggregated type
		failWithSemanticException("select t1.int from alltypes t1, "
				+ "alltypes t2 group by t2.int;");
		// Selecting non grouped fields
		failWithSemanticException("select * from alltypes group by int;");

		getValidatedPreprocessor("select t1.int as st1, sum(t2.int) as st2 from "
				+ "alltypes t1, alltypes t2 group by t1.int having st1 = 3;");
		getValidatedPreprocessor("select sum(StringtoInt(string)) as st from "
				+ "alltypes group by int having st = 3;");
		getValidatedPreprocessor("select sum(t1.int) as st1, sum(t2.int) as st2 from "
				+ "alltypes t1, alltypes t2 group by t2.int having st1 = 3;");
		getValidatedPreprocessor("select int from alltypes "
				+ "group by int having int=5;");
		getValidatedPreprocessor("select int from alltypes t"
				+ " group by t.int;");
		getValidatedPreprocessor("select 2*int from alltypes "
				+ "group by int having int=5;");
	}

	public void testInsert() throws Exception {
		getValidatedPreprocessor("insert into alltypes (int) values (4);");
		getValidatedPreprocessor("insert into gis values ('2', '2');");
		// field-value count mistmatch
		failWithSemanticException("insert into gis values ('2', '2', '2');");
		// table not found
		failWithSemanticException("insert into alltypes values (1);");
		// field not found
		failWithSemanticException("insert into gis (id, \"Gis\") values ('a', 'a');");
		// Type mistmatch
		failWithIncompatibleTypes("insert into gis (id, gis) values ('a', 4);");
		// field references in values
		failWithSemanticException("insert into gis values ('2', id);");
	}

	public void testIs() throws Exception {
		failWithSemanticException("select * from \"Gis\" where id is not null;");
		getValidatedPreprocessor("select * from gis where id is not null;");
		getValidatedPreprocessor("select * from gis where id is null;");
	}

	public void testOrderBy() throws Exception {
		failWithSemanticException("select * from gis order by \"Id\";");
		failWithSemanticException("select * from gis order by \"Gis\".id;");
		failWithIncompatibleTypes("select * from alltypes order by boolean;");
		getValidatedPreprocessor("select * from gis, alltypes "
				+ "order by gis.id;");
		getValidatedPreprocessor("select * from gis g, alltypes "
				+ "order by g.id;");
		getValidatedPreprocessor("select * from alltypes " + "order by float;");
		getValidatedPreprocessor("select -2*float as myfloat from alltypes "
				+ "order by myfloat;");
		// order by non selected field
		getValidatedPreprocessor("select float as myfloat from alltypes "
				+ "order by int;");

		// order by field outside group
		failWithSemanticException("select float as myfloat from alltypes "
				+ "group by float order by int;");
		getValidatedPreprocessor("select float as myfloat from alltypes "
				+ "group by float order by float;");
	}

	public void testGroupAndOrderBy() throws Exception {
		getValidatedPreprocessor("select alltypes.string " + "from alltypes "
				+ "group by alltypes.string " + "order by alltypes.string;");
		getValidatedPreprocessor("select alltypes.string " + "from alltypes "
				+ "group by alltypes.string " + "order by string;");
		getValidatedPreprocessor("select alltypes.string " + "from alltypes "
				+ "group by string " + "order by alltypes.string;");
		getValidatedPreprocessor("select string " + "from alltypes "
				+ "group by alltypes.string " + "order by alltypes.string;");
		getValidatedPreprocessor("select string " + "from alltypes "
				+ "group by alltypes.string " + "order by string;");
		getValidatedPreprocessor("select string " + "from alltypes "
				+ "group by string " + "order by alltypes.string;");
		getValidatedPreprocessor("select string " + "from alltypes "
				+ "group by string " + "order by string;");
	}

	public void testUnion() throws Exception {
		failWithSemanticException("gis union alltypes;");
		failWithSemanticException("(select * from gis) union alltypes;");
		failWithSemanticException("alltypes union (select * from gis);");
		failWithSemanticException("alltypes union (select * from gis);");
		failWithSemanticException("alltypes union gis;");
		getValidatedPreprocessor("gis union gis;");
	}

	public void testUpdate() throws Exception {
		getValidatedPreprocessor("update gis set id = '3';");
		getValidatedPreprocessor("update gis set id = '3', "
				+ "gis = 'patatagis' where id='2';");
		// Field not found in assignment
		failWithSemanticException("update gis set \"Id\" = \"Id\" + 1;");
		// Type miss match in assignment
		failWithIncompatibleTypes("update gis set id = true;");
		// Field not found in where
		failWithSemanticException("update gis set id = '1' "
				+ "where \"Id\" = '2';");
		// Type miss match in where
		failWithIncompatibleTypes("update gis set id = 'd' "
				+ "where gis=false;");
		// table not found
		failWithSemanticException("update \"Gis\" set id='d';");
	}

	public void testCreate() throws Exception {
		dsf.getSourceManager().register("table", new File("/tmp/foo"));
		getValidatedPreprocessor("create table \"table\" as select * from gis;");
		failWithSemanticException("create table gis2 as select * from \"Gis\";");
	}

	public void testAggregatedFunctions() throws Exception {
		// id field not found
		failWithSemanticException("select avg(id), avg(double) "
				+ "from alltypes group by boolean;");

		getValidatedPreprocessor("select avg(int), avg(double) "
				+ "from alltypes group by boolean;");

		// non custom query without from
		failWithSemanticException("select avg(id);");

		// aggregated in where
		failWithSemanticException("select * from alltypes where avg(int)=0;");

		getValidatedPreprocessor("select int, string, avg(double) from"
				+ " alltypes where boolean group by int, string having int=3;");

	}

	public void testFunctions() throws Exception {
		// Non existing function in where clause
		failWithSemanticException("select * "
				+ "from alltypes where notexists(int);");

		// Non existing function in selection
		failWithSemanticException("select notexists(int) " + "from alltypes;");

		// Custom query in where clause
		failWithSemanticException("select * "
				+ "from alltypes where register('a', 'a');");

		getValidatedPreprocessor("select * "
				+ "from alltypes where strlength(string) > 0;");
		getValidatedPreprocessor("select strlength(string) " + "from alltypes;");

	}

	public void testValidateCustomQueries() throws Exception {
		SumQuery query = new SumQuery();
		if (QueryManager.getQuery(query.getName()) == null) {
			QueryManager.registerQuery(SumQuery.class);
		}
		getValidatedPreprocessor("select register('file', 'filename');");
		getValidatedPreprocessor("select sumquery() from gis;");
		getValidatedPreprocessor("select sumquery() from gis where id = '5';");
		getValidatedPreprocessor("select sumquery(gis.id) from "
				+ "gis where id = '5';");
		// where and two tables in from
		failWithSemanticException("select sumquery() from gis, alltypes "
				+ "where id = '5';");
		// field references as parameter of custom query
		// wrong number of arguments
		failWithIncompatibleTypes("select sumquery(3,6) from "
				+ "gis where id = '5';");
		// wrong type of arguments
		failWithIncompatibleTypes("select sumquery(4) from "
				+ "gis where id = '5';");
		// wrong number of tables
		failWithSemanticException("select sumquery(3) from " + "gis, alltypes;");
		// type error in where
		failWithIncompatibleTypes("select sumquery(6) from "
				+ "gis where id = 5;");
		// type error in argument
		failWithIncompatibleTypes("select sumquery(6+3*'e') from " + "gis;");

		// Test limited custom queries
		failWithSemanticException("select sumquery() from alltypes limit 2;");
		failWithSemanticException("select sumquery() from alltypes offset 2;");
		failWithSemanticException("select sumquery() from alltypes limit 2 offset 2;");
	}

	public void testProductOfThreeTables() throws Exception {
		getValidatedPreprocessor("select t3.timestamp, t2.string, t1.id "
				+ "from gis t1, alltypes t2, hsqldb t3;");
		getValidatedPreprocessor("select a.date as d1, h.date as h2, gis "
				+ "from alltypes a, gis g, hsqldb h "
				+ "where a.string = g.gis AND h.string= g.gis;");
	}

	public void testDifferentNumberOfFields() throws Exception {
		getValidatedPreprocessor("select t2.string, t1.id "
				+ "from gis t1, alltypes t2;");
	}

	public void testAggregatedFunctionDefaultName() throws Exception {
		Preprocessor p = getValidatedPreprocessor("select count(*) from alltypes;");
		Metadata m1 = p.getResultMetadata();
		assertTrue(m1.getFieldName(0).equals("unknown0"));
	}

	public void testGroupByWhereFullyClassifiedProj() throws Exception {
		getValidatedPreprocessor("select t.int from alltypes t"
				+ " where int=2 group by int;");
	}

	public void testCreateDropIndex() throws Exception {
		getValidatedPreprocessor("create index on alltypes (int);");
		getValidatedPreprocessor("drop index on alltypes (int);");
		failWithSemanticException("create index on \"allTypes\" (int);");
		failWithSemanticException("create index on alltypes (rint);");
		failWithSemanticException("drop index on \"allTypes\" (int);");
		failWithSemanticException("drop index on alltypes (rint);");
	}

	private Preprocessor getValidatedPreprocessor(String sql) throws Exception {
		return getValidatedPreprocessor(getOperator(sql));
	}

	private Preprocessor getValidatedPreprocessor(Operator op) throws Exception {
		Preprocessor p = new Preprocessor(op);
		p.validate();

		return p;
	}

	private Operator getOperator(String sql) throws Exception {
		SQLEngine parser = new SQLEngine(new ByteArrayInputStream(sql
				.getBytes()));

		parser.SQLStatement();
		LogicTreeBuilder lp = new LogicTreeBuilder(dsf);
		Operator op = (Operator) lp
				.buildTree((SimpleNode) parser.getRootNode());

		getValidatedPreprocessor(op);
		return op;
	}
}
