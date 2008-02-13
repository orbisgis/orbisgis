package org.gdms.sql.strategies;

import java.io.ByteArrayInputStream;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.parser.SQLEngine;
import org.gdms.sql.parser.SimpleNode;

public class SQLProcessor {

	private DataSourceFactory dsf;

	public SQLProcessor(DataSourceFactory dsf) {
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

		Instruction instr = prepareInstruction(sql);

		// Execution
		return instr.execute();
	}

	private Operator parse(String sql) throws ParseException,
			SemanticException, DriverException {
		if (!sql.trim().endsWith(";")) {
			sql += ";";
		}

		SQLEngine parser = new SQLEngine(new ByteArrayInputStream(sql
				.getBytes()));
		parser.SQLStatement();
		LogicTreeBuilder lp = new LogicTreeBuilder(dsf);
		Operator op = (Operator) lp
				.buildTree((SimpleNode) parser.getRootNode());
		return op;
	}

	public Instruction prepareInstruction(String sql) throws ParseException,
			SemanticException, DriverException {
		// Compilation
		Operator op = parse(sql);

		// Preprocessor
		Preprocessor p = new Preprocessor(op);
		p.validate();

		return new Instruction(op, sql);
	}

	public Instruction[] prepareScript(String script) throws SemanticException,
			DriverException, ParseException {
		if (!script.trim().endsWith(";")) {
			script += ";";
		}

		SQLEngine parser = new SQLEngine(new ByteArrayInputStream(script
				.getBytes()));
		parser.SQLStatement();
		LogicTreeBuilder lp = new LogicTreeBuilder(dsf);
		return lp.getInstructions((SimpleNode) parser.getRootNode());
	}
}
