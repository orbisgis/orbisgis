package org.gdms.sql.strategies;

import java.io.ByteArrayInputStream;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.parser.SQLEngine;
import org.gdms.sql.parser.SimpleNode;
import org.orbisgis.IProgressMonitor;

public class SQLProcessor {

	private DataSourceFactory dsf;

	public SQLProcessor(DataSourceFactory dsf) {
		this.dsf = dsf;
	}

	/**
	 * Executes the specified instruction using the specified DataSourceFactory
	 *
	 * @param sql
	 * @param pm
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
	public ObjectDriver execute(String sql, IProgressMonitor pm)
			throws ParseException, SemanticException, DriverException,
			ExecutionException {

		Instruction instr = prepareInstruction(sql);

		// Execution
		return instr.execute(pm);
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

	/**
	 * Prepares the instruction for the execution. The returned value is
	 * optimized and ready to execute
	 *
	 * @param script
	 *            text containing the sql script
	 * @return
	 * @throws SemanticException
	 *             If the instruction contains semantic errors: unknown or
	 *             ambiguous field references, operations with incompatible
	 *             types, etc.
	 * @throws DriverException
	 *             If there is an error accessing the sources involved in any of
	 *             the queries
	 * @throws ParseException
	 *             If there is any instruction with parse errors in the script
	 */
	public Instruction prepareInstruction(String sql) throws ParseException,
			SemanticException, DriverException {
		// Compilation
		Operator op = parse(sql);

		// Preprocessor
		Preprocessor p = new Preprocessor(op);
		p.validate();

		return new Instruction(dsf, op, sql);
	}

	/**
	 * Gets the instructions found in the script
	 *
	 * @param script
	 *            text containing the sql script
	 * @return
	 * @throws SemanticException
	 *             If the instruction contains semantic errors: unknown or
	 *             ambiguous field references, operations with incompatible
	 *             types, etc.
	 * @throws DriverException
	 *             If there is an error accessing the sources involved in any of
	 *             the queries
	 * @throws ParseException
	 *             If there is any instruction with parse errors in the script
	 */
	public Instruction[] prepareScript(String script) throws SemanticException,
			DriverException, ParseException {
		if (!script.trim().endsWith(";")) {
			script += ";";
		}

		SQLEngine parser = new SQLEngine(new ByteArrayInputStream(script
				.getBytes()));
		parser.SQLScript();
		LogicTreeBuilder lp = new LogicTreeBuilder(dsf);
		return lp.getInstructions((SimpleNode) parser.getRootNode());
	}
}
