package org.gdms.sql;

import java.io.File;

import junit.framework.TestCase;

import org.gdms.SourceTest;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.driver.DriverException;
import org.gdms.sql.evaluator.FunctionOperator;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.Operator;
import org.gdms.sql.strategies.OperatorFilter;
import org.gdms.sql.strategies.SQLProcessor;
import org.gdms.sql.strategies.SelectionOp;
import org.gdms.sql.strategies.SemanticException;

public class OptimizationTests extends TestCase {

	private DataSourceFactory dsf;

	@Override
	protected void setUp() {
		dsf = new DataSourceFactory();
		dsf.setTempDir(SourceTest.backupDir.getAbsolutePath());
		dsf.getSourceManager().register(
				"communes",
				new File(SourceTest.externalData
						+ "/shp/bigshape2D/communes.shp"));
	}

	public void testPushToAllChild() throws Exception {
		String sql = "SELECT a.* FROM communes a, communes b "
				+ "WHERE a.the_geom = b.the_geom "
				+ "AND a.\"NOM_COMM\"='ARMIX' " + "AND b.\"NOM_COMM\"='ARGIS'";
		long time = execute(sql);
		assertTrue(time < 15000);
		int selectionOpCount = getSelectionOperators(sql);
		assertTrue(selectionOpCount == 3);
	}

	public void testPushAll() throws Exception {
		String sql = "SELECT * FROM communes "
				+ "WHERE communes.\"NOM_COMM\"='ARMIX'";
		int selectionOpCount = getSelectionOperators(sql);
		assertTrue(selectionOpCount == 2);
	}

	private int getSelectionOperators(String sql) throws ParseException,
			SemanticException, DriverException {
		SQLProcessor pr = new SQLProcessor(dsf);
		Operator op = pr.prepareInstruction(sql).getOperator();
		Operator[] selections = op.getOperators(new OperatorFilter() {

			public boolean accept(Operator op) {
				return op instanceof SelectionOp;
			}

		});
		return selections.length;
	}

	public void testNoSelectionPushedDown() throws Exception {
		String sql = "SELECT a.* FROM communes a, communes b "
				+ "WHERE a.the_geom = b.the_geom;";
		int selectionOpCount = getSelectionOperators(sql);
		assertTrue(selectionOpCount == 1);
	}

	private long execute(String sql) throws ParseException, SemanticException,
			DriverException, ExecutionException {
		long t1 = System.currentTimeMillis();
		dsf.executeSQL(sql);
		long t2 = System.currentTimeMillis();
		return t2 - t1;
	}

	public void testOrFinishFast() throws Exception {
		FunctionManager.addFunction(FailingFunction.class);
		String sql = "SELECT * FROM communes " + "WHERE true " + "OR failing()";
		execute(sql);
	}

	public void testAndFinishFast() throws Exception {
		FunctionManager.addFunction(FailingFunction.class);
		String sql = "SELECT * FROM communes " + "WHERE false "
				+ "AND failing()";
		execute(sql);
	}

	public void testAutonumericLiteral() throws Exception {
		FunctionOperator op = new FunctionOperator("autonumeric");
		assertTrue(op.evaluate().less(op.evaluate()).getAsBoolean());
	}
}
