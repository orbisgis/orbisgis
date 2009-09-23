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

import java.io.File;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.indexes.ExpressionBasedAlphaQuery;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.indexes.IndexManager;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.evaluator.Equals;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;
import org.gdms.sql.evaluator.FunctionOperator;
import org.gdms.sql.evaluator.Literal;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.OnePassScalarProduct;
import org.gdms.sql.strategies.Operator;
import org.gdms.sql.strategies.OperatorFilter;
import org.gdms.sql.strategies.SQLProcessor;
import org.gdms.sql.strategies.ScalarProductOp;
import org.gdms.sql.strategies.ScanOperator;
import org.gdms.sql.strategies.SelectionOp;
import org.gdms.sql.strategies.SemanticException;
import org.gdms.sql.strategies.joinOptimization.IndexScan;
import org.orbisgis.progress.NullProgressMonitor;

public class OptimizationTests extends TestCase {

	private DataSourceFactory dsf;
	private OperatorFilter selectionAndScan;
	private OperatorFilter selectionAndScalarProductOp;

	@Override
	protected void setUp() {
		if (FunctionManager.getFunction(new FailingFunction().getName()) == null) {
			FunctionManager.addFunction(FailingFunction.class);
		}
		dsf = new DataSourceFactory();
		dsf.setTempDir(SourceTest.backupDir.getAbsolutePath());
		dsf.getSourceManager().register(
				"points",
				new File(SourceTest.internalData
						+ "points.shp"));
		dsf.getSourceManager().register(
				"hedgerow",
				new File(SourceTest.internalData
						+ "hedgerow.shp"));
		dsf.getSourceManager().register(
				"landcover2000",
				new File(SourceTest.internalData
						+ "landcover2000.shp"));

		selectionAndScan = new OperatorFilter() {

			public boolean accept(Operator op) {
				return (op instanceof SelectionOp)
						&& (op.getOperator(0) instanceof ScanOperator);
			}

		};
		selectionAndScalarProductOp = new OperatorFilter() {

			public boolean accept(Operator op) {
				return (op instanceof SelectionOp)
						&& (op.getOperator(0) instanceof ScalarProductOp)
						&& !(op.getOperator(0) instanceof OnePassScalarProduct);
			}

		};

	}

	public void testPushToAllChild() throws Exception {
		String sql = "SELECT a.* FROM communes a, communes b "
				+ "WHERE a.the_geom = b.the_geom "
				+ "AND a.\"NOM_COMM\"='ARMIX' " + "AND b.\"NOM_COMM\"='ARGIS'";
		Operator[] ops = getOperators(sql, selectionAndScan);
		assertTrue(ops.length == 2);

		ops = getOperators(sql, selectionAndScalarProductOp);
		assertTrue(ops.length == 1);
	}

	private Operator[] getOperators(String sql, OperatorFilter operatorFilter)
			throws ParseException, SemanticException, DriverException {
		SQLProcessor processor = new SQLProcessor(dsf);
		Operator op = processor.prepareInstruction(sql).getOperator();
		Operator[] ops = op.getOperators(operatorFilter);
		return ops;
	}

	public void testPushAll() throws Exception {
		String sql = "SELECT * FROM communes "
				+ "WHERE communes.\"NOM_COMM\"='ARMIX'";
		Operator[] ops = getOperators(sql, selectionAndScan);
		assertTrue(ops.length == 1);
	}

	public void testNoSelectionPushedDown() throws Exception {
		String sql = "SELECT a.* FROM communes a, communes b "
				+ "WHERE a.the_geom = b.the_geom;";
		Operator[] ops = getOperators(sql, selectionAndScalarProductOp);
		assertTrue(ops.length == 1);
	}

	public void testOrFinishFast() throws Exception {
		String sql = "SELECT * FROM communes " + "WHERE true " + "OR failing()";
		getDataSource(sql);
	}

	public void testAndFinishFast() throws Exception {
		String sql = "SELECT * FROM communes " + "WHERE false "
				+ "AND failing()";
		getDataSource(sql);
	}

	public void testAutonumericLiteral() throws Exception {
		FunctionOperator op = new FunctionOperator("autonumeric");
		assertTrue(op.evaluate().less(op.evaluate()).getAsBoolean());
	}

	public void testIndexUsageEquals() throws Exception {
		dsf.getIndexManager().buildIndex("communes", "NOM_COMM",
				IndexManager.BTREE_ALPHANUMERIC_INDEX, null);
		String sql = "SELECT * FROM communes" + " WHERE \"NOM_COMM\"='ARMIX'";
		checkAlphaIndexSpeedUp(sql);
	}

	public void testIndexUsageLess() throws Exception {
		dsf.getIndexManager().buildIndex("communes", "NOM_COMM",
				IndexManager.BTREE_ALPHANUMERIC_INDEX, null);
		String sql = "SELECT * FROM communes" + " WHERE \"NOM_COMM\"<'ARMIX'";
		checkAlphaIndexSpeedUp(sql);
	}

	public void testIndexUsageLessEquals() throws Exception {
		dsf.getIndexManager().buildIndex("communes", "NOM_COMM",
				IndexManager.BTREE_ALPHANUMERIC_INDEX, null);
		String sql = "SELECT * FROM communes" + " WHERE \"NOM_COMM\"<='ARMIX'";
		checkAlphaIndexSpeedUp(sql);
	}

	public void testIndexUsageGreater() throws Exception {
		dsf.getIndexManager().buildIndex("communes", "NOM_COMM",
				IndexManager.BTREE_ALPHANUMERIC_INDEX, null);
		String sql = "SELECT * FROM communes" + " WHERE \"NOM_COMM\">'RMIX'";
		checkAlphaIndexSpeedUp(sql);
	}

	private void checkAlphaIndexSpeedUp(String sql) throws IndexException,
			ParseException, SemanticException, DriverException,
			ExecutionException, NoSuchTableException {
		SQLProcessor processor = new SQLProcessor(dsf);
		Operator op = processor.prepareInstruction(sql).getOperator();
		Operator[] ops = op.getOperators(new OperatorFilter() {

			public boolean accept(Operator op) {
				return (op instanceof ScanOperator);
			}

		});
		assertTrue(ops.length == 1);
		assertTrue(((ScanOperator) ops[0]).isIndexScan());
	}

	public void testIndexUsageGreaterEquals() throws Exception {
		dsf.getIndexManager().buildIndex("communes", "NOM_COMM",
				IndexManager.BTREE_ALPHANUMERIC_INDEX, null);
		String sql = "SELECT * FROM communes" + " WHERE \"NOM_COMM\">='R'";
		checkAlphaIndexSpeedUp(sql);
	}

	public void testNotEquals() throws Exception {
		ObjectMemoryDriver omd = new ObjectMemoryDriver(new String[] { "id" },
				new Type[] { TypeFactory.createType(Type.STRING) });
		omd.addValues(new Value[] { ValueFactory.createValue("1") });
		omd.addValues(new Value[] { ValueFactory.createValue("2") });
		dsf.getSourceManager().register("test", omd);
		String sql = "SELECT a.* " + "from test a, test b "
				+ "where a.id<>b.id;";
		DataSource ds = dsf.getDataSourceFromSQL(sql);
		ds.open();
		assertTrue(ds.getRowCount() == 2);
		ds.close();
	}

	public void testJoinOptimization() throws Exception {
		executeAndRead("select intersection(a.the_geom, b.the_geom) "
				+ "from landcover2000 b, bzh5_communes a "
				+ "where intersects(a.the_geom, b.the_geom);");
		executeAndRead("select intersection(a.the_geom, b.the_geom) "
				+ "from landcover2000 a, bzh5_communes b "
				+ "where intersects(a.the_geom, b.the_geom);");
	}

	private DataSource executeAndRead(String sql)
			throws DataSourceCreationException, DriverException,
			ParseException, SemanticException {
		DataSource ds = getDataSource(sql);
		ds.open();
		Metadata metadata = ds.getMetadata();
		for (int i = 0; i < ds.getRowCount(); i++) {
			for (int j = 0; j < metadata.getFieldCount(); j++) {
				ds.getFieldValue(i, j);
			}
		}
		ds.getRow(0);
		ds.close();
		return ds;
	}

	private DataSource getDataSource(String sql) throws ParseException,
			SemanticException, DriverException, DataSourceCreationException {
		SQLProcessor processor = new SQLProcessor(dsf);
		Operator op = processor.prepareInstruction(sql).getOperator();
		Operator[] ops = op.getOperators(new OperatorFilter() {

			public boolean accept(Operator op) {
				return (op instanceof ScalarProductOp)
						&& !(op instanceof OnePassScalarProduct);
			}

		});
		assertTrue(ops.length == 0);

		DataSource ds = dsf.getDataSourceFromSQL(sql, null);
		return ds;
	}

	public void testJoinAndConditionOptimization() throws Exception {
		String sql = "select intersection(a.the_geom, b.the_geom) "
				+ "from landcover2000 a, bzh5_communes b "
				+ "where isValid(a.the_geom) and intersects(a.the_geom, b.the_geom);";
		executeAndRead(sql);
	}

	public void testJoinWithAliasOptimization() throws Exception {
		dsf.getSourceManager().addName("landcover2000", "landi");
		dsf.getSourceManager().addName("bzh5_communes", "commi");
		String sql = "select intersection(a.the_geom, b.the_geom) "
				+ "from landi a, commi b "
				+ "where intersects(a.the_geom, b.the_geom);";
		executeAndRead(sql);
	}

	public void testJoinWithAliasedTableNameReference() throws Exception {
		dsf.getSourceManager().addName("landcover2000", "landi");
		dsf.getSourceManager().addName("bzh5_communes", "commi");
		String sql = "select intersection(a.the_geom, commi.the_geom) "
				+ "from landi a, commi b "
				+ "where intersects(landi.the_geom, commi.the_geom);";
		executeAndRead(sql);
	}

	public void testSameTableDifferentAlias() throws Exception {
		String sql = "select a.*, b.\"CODE_CANT\" as codecant "
				+ "from bzh5_communes a, bzh5_communes b "
				+ "where a.\"CODE_DEPT\"=b.\"CODE_CANT\";";
		DataSource ds = getDataSource(sql);
		ds.open();
		int fIndex1 = ds.getFieldIndexByName("CODE_DEPT");
		int fIndex2 = ds.getFieldIndexByName("codecant");
		for (int i = 0; i < ds.getRowCount(); i++) {
			Value codDept = ds.getFieldValue(i, fIndex1);
			Value codCant = ds.getFieldValue(i, fIndex2);
			assertTrue(codDept.equals(codCant).getAsBoolean());
		}
		ds.close();
	}

	public void testJoinWithoutTableReferences() throws Exception {
		// Create a identical source with different field names
		DataSource ds = dsf.getDataSource("bzh5_communes");
		ds.open();
		long rowcount = ds.getRowCount();
		Metadata sourceMetadata = ds.getMetadata();
		DefaultMetadata metadata = new DefaultMetadata();
		for (int i = 0; i < sourceMetadata.getFieldCount(); i++) {
			metadata.addField("o" + sourceMetadata.getFieldName(i),
					sourceMetadata.getFieldType(i));
		}
		ObjectMemoryDriver omd = new ObjectMemoryDriver(metadata);
		for (int i = 0; i < ds.getRowCount(); i++) {
			omd.addValues(ds.getRow(i));
		}
		ds.close();
		dsf.getSourceManager().register("obzh5_communes", omd);

		// Execute the join
		String sql = "select bzh5_communes.*, \"oOBJECTID\" "
				+ "from bzh5_communes, obzh5_communes "
				+ "where \"OBJECTID\"=\"oOBJECTID\";";
		ds = getDataSource(sql);
		ds.open();
		assertTrue(rowcount == ds.getRowCount());
		for (int i = 0; i < ds.getRowCount(); i++) {
			assertTrue(ds.getInt(i, "OBJECTID") == ds.getInt(i, "oOBJECTID"));
		}
		ds.close();
	}

	public void testNoIndex() throws Exception {
		String sql = "select intersection(a.the_geom, b.the_geom) "
				+ "from landcover2000 b, bzh5_communes a;";

		SQLProcessor processor = new SQLProcessor(dsf);
		Operator op = processor.prepareInstruction(sql).getOperator();
		Operator[] ops = op.getOperators(new OperatorFilter() {

			public boolean accept(Operator op) {
				return (op instanceof ScalarProductOp)
						&& !(op instanceof OnePassScalarProduct);
			}

		});

		assertTrue(ops.length == 1);
	}

	public void testIndexLiteralUsage() throws Exception {
		dsf.getIndexManager().buildIndex("bzh5_communes", "NOM_COMM",
				IndexManager.BTREE_ALPHANUMERIC_INDEX, null);
		String sql = "select * from bzh5_communes where \"NOM_COMM\" < 'FF';";
		Operator[] ops = getOperators(sql, new OperatorFilter() {

			public boolean accept(Operator op) {
				if (op instanceof SelectionOp) {
					SelectionOp sel = (SelectionOp) op;
					return sel.getIndexQueries().length > 0;
				} else {
					return false;
				}
			}

		});

		assertTrue(ops.length == 1);

		dsf.getIndexManager().deleteIndex("bzh5_communes", "NOM_COMM");
		ops = getOperators(sql, new OperatorFilter() {

			public boolean accept(Operator op) {
				if (op instanceof SelectionOp) {
					SelectionOp sel = (SelectionOp) op;
					return sel.getIndexQueries().length > 0;
				} else {
					return false;
				}
			}

		});

		assertTrue(ops.length == 0);
	}

	public void testSameField() throws Exception {
		dsf.getIndexManager().buildIndex("bzh5_communes", "NOM_COMM",
				IndexManager.BTREE_ALPHANUMERIC_INDEX, null);
		String sql = "select * from bzh5_communes where \"NOM_COMM\" = \"NOM_COMM\";";
		testNoIndexUsed(sql);
	}

	public void testSameFieldWithAlias() throws Exception {
		dsf.getIndexManager().buildIndex("bzh5_communes", "NOM_COMM",
				IndexManager.BTREE_ALPHANUMERIC_INDEX, null);
		String sql = "select * from bzh5_communes t where \"NOM_COMM\" = t.\"NOM_COMM\";";
		testNoIndexUsed(sql);
	}

	private void testNoIndexUsed(String sql) throws ParseException,
			SemanticException, DriverException {
		Operator[] ops = getOperators(sql, new OperatorFilter() {

			public boolean accept(Operator op) {
				if (op instanceof SelectionOp) {
					SelectionOp sel = (SelectionOp) op;
					return sel.getIndexQueries().length > 0;
				} else {
					return false;
				}
			}

		});
		assertTrue(ops.length == 0);
	}

	public void testAdHocIndexOnSelection() throws Exception {
		testIndexOnSelection("select t1.* "
				+ "from bzh5_communes t1, bzh5_communes t2 "
				+ "where t1.\"NOM_COMM\" = 'ARGOL'"
				+ " AND t2.\"NOM_COMM\"='ARZANO' "
				+ "AND t1.\"NOM_COMM\" = t2.\"NOM_COMM\";");
	}

	public void testPrecalculatedIndexOnSelection() throws Exception {
		dsf.getIndexManager().buildIndex("bzh5_communes", "NOM_COMM",
				IndexManager.BTREE_ALPHANUMERIC_INDEX, null);
		testIndexOnSelection("select t1.* "
				+ "from bzh5_communes t1, bzh5_communes t2 "
				+ "where t1.\"NOM_COMM\" = 'ARGOL'"
				+ " AND t2.\"NOM_COMM\"='ARZANO' "
				+ "AND t1.\"NOM_COMM\" = t2.\"NOM_COMM\";");
	}

	private void testIndexOnSelection(String sql) throws ParseException,
			SemanticException, DriverException, DataSourceCreationException {
		DataSource ds = getDataSource(sql);
		ds.open();
		assertTrue(ds.getRowCount() == 0);
		ds.close();
	}

	public void testSeveralIndexScans() throws Exception {
		ObjectMemoryDriver omd = new ObjectMemoryDriver(new String[] { "id",
				"id2" }, new Type[] { TypeFactory.createType(Type.STRING),
				TypeFactory.createType(Type.STRING) });
		omd.addValues(new Value[] { ValueFactory.createValue("1"),
				ValueFactory.createValue("1") });
		omd.addValues(new Value[] { ValueFactory.createValue("2"),
				ValueFactory.createValue("1") });
		dsf.getSourceManager().register("test", omd);

		// Build select * from test a, test b where a.id='1' and a.id2='1'
		ScanOperator scan1 = new ScanOperator(dsf, "test", "a");
		scan1.initialize();
		ScanOperator scan2 = new ScanOperator(dsf, "test", "b");
		scan2.initialize();
		OnePassScalarProduct scalar = new OnePassScalarProduct(dsf
				.getIndexManager());
		scalar.addChild(scan1);
		ArrayList<IndexScan> indexScans = new ArrayList<IndexScan>();
		Literal l1 = new Literal(ValueFactory.createValue("1"));
		Expression exp1 = new Equals(l1, new Field("a", "id"));
		Expression exp2 = new Equals(l1, new Field("a", "id2"));
		IndexScan is1 = new IndexScan(new ExpressionBasedAlphaQuery("id", l1),
				true, exp1);
		IndexScan is2 = new IndexScan(new ExpressionBasedAlphaQuery("id2", l1),
				true, exp2);
		indexScans.add(is1);
		indexScans.add(is2);
		scalar.setIndexScan(indexScans, scan2);
		scalar.operationFinished();
		assertTrue((scalar.getResult(new NullProgressMonitor()).getRowCount()) == 2);
	}

}
