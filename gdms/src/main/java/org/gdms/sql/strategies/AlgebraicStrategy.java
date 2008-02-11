package org.gdms.sql.strategies;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.parser.SQLEngine;
import org.gdms.sql.parser.SimpleNode;

public class AlgebraicStrategy {

	private DataSourceFactory dsf;

	public AlgebraicStrategy(DataSourceFactory dsf) {
		this.dsf = dsf;
	}

	/**
	 * Executes the specified instruction using the specified DataSourceFactory
	 *
	 * @param dsf
	 * @param sql
	 * @return
	 * @throws ParseException
	 *             If the sql is not well formed
	 * @throws SemanticException
	 *             If the instruction contains semantic errors: unknown or
	 *             ambiguous field references, operations with incompatible
	 *             types, etc.
	 * @throws DriverException
	 *             If there is a problem accessing the sources
	 * @throws ExecutionException
	 *             If there is a problem while executing the SQL
	 */
	public ObjectDriver execute(String sql) throws ParseException,
			SemanticException, DriverException, ExecutionException {

		// Compilation
		Operator op = parse(sql);

		// Preprocessor
		Preprocessor p = new Preprocessor(op);
		p.validate();

		// Execution
		ObjectDriver ret = op.getResult();
		op.operationFinished();

		return ret;
	}

	private Operator parse(String sql) throws ParseException,
			SemanticException, DriverException {
		SQLEngine parser = new SQLEngine(new ByteArrayInputStream(sql
				.getBytes()));
		parser.SQLStatement();
		LogicTreeBuilder lp = new LogicTreeBuilder(dsf);
		Operator op = (Operator) lp
				.buildTree((SimpleNode) parser.getRootNode());
		return op;
	}

	public String[] getReferencedSources(String sql) throws ParseException,
			SemanticException, DriverException {
		Operator op = parse(sql);

		return getReferencedTables(op);
	}

	private String[] getReferencedTables(Operator op) {
		ArrayList<String> ret = new ArrayList<String>();
		String[] tables = op.getReferencedTables();
		for (String table : tables) {
			ret.add(table);
		}
		for (int i = 0; i < op.getOperatorCount(); i++) {
			tables = op.getOperator(i).getReferencedTables();
			for (String table : tables) {
				ret.add(table);
			}
		}

		return ret.toArray(new String[0]);
	}

	public Metadata getResultMetadata(String sql) throws ParseException,
			SemanticException, DriverException {
		Operator op = parse(sql);

		Preprocessor p = new Preprocessor(op);
		p.validate();

		return op.getResultMetadata();
	}

}
