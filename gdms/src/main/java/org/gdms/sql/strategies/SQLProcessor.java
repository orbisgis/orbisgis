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
package org.gdms.sql.strategies;

import java.io.ByteArrayInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.parser.Node;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.parser.SQLEngine;
import org.gdms.sql.parser.SimpleNode;
import org.gdms.sql.parser.Token;
import org.orbisgis.progress.IProgressMonitor;

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
		// Compilation
		Operator op = parse(sql);
		try {
			op.initialize();
			Instruction instr = prepareInstruction(op, sql, false);

			// Execution
			ObjectDriver ret = instr.execute(pm);
			return ret;
		} finally {
			op.operationFinished();
		}
	}

	private Operator parse(String sql) throws ParseException,
			SemanticException, DriverException {
		if (!sql.trim().endsWith(";")) {
			sql += ";";
		}

		SQLEngine parser = new SQLEngine(new ByteArrayInputStream(sql
				.getBytes()));
		try {
			parser.SQLStatement();
		} catch (ParseException e) {
			throw new ParseException("Parse error in " + sql + ": "
					+ e.getMessage());
		}
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
	 *            text containing the sql instruction
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
		return prepareInstruction(op, sql, true);
	}

	/**
	 * Creates an instruction object that will be able to execute the specified
	 * operator
	 * 
	 * @param op
	 *            Operation
	 * @param sql
	 *            SQL query
	 * @param doOpenClose
	 *            flag to make it initialize and finalize the operator tree
	 * @return
	 * @throws ParseException
	 * @throws SemanticException
	 * @throws DriverException
	 */
	private Instruction prepareInstruction(Operator op, String sql,
			boolean doOpenClose) throws ParseException, SemanticException,
			DriverException {
		// Preprocessor
		Preprocessor p = new Preprocessor(op);
		if (doOpenClose) {
			op.initialize();
			p.validate();
			p.optimize(dsf);
			op.operationFinished();
		} else {
			p.validate();
			p.optimize(dsf);
		}

		return new Instruction(op, sql, doOpenClose);
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
	public String[] getScriptInstructions(String script)
			throws SemanticException, DriverException, ParseException {
		if (!script.trim().endsWith(";")) {
			script += ";";
		}

		SQLEngine parser = new SQLEngine(new ByteArrayInputStream(script
				.getBytes()));
		parser.SQLScript();
		SimpleNode scriptNode = (SimpleNode) parser.getRootNode();
		String[] ret = new String[scriptNode.jjtGetNumChildren()];
		for (int i = 0; i < scriptNode.jjtGetNumChildren(); i++) {
			SimpleNode statement = (SimpleNode) scriptNode.jjtGetChild(i);
			int ini = getPosition(script, statement.first_token);
			int fin = getPosition(script, statement.last_token.beginLine,
					statement.last_token.beginColumn);
			ret[i] = script.substring(ini, fin);
		}

		return ret;
	}

	public String getScriptComment(String script) throws ParseException {
		SQLEngine parser = new SQLEngine(new ByteArrayInputStream(script
				.getBytes()));
		parser.SQLScript();
		Node root = parser.getRootNode();
		int index;
		Token startingToken = ((SimpleNode) root).first_token;
		index = getPosition(script, startingToken);
		if (index > 0) {
			String comment = script.substring(0, index - 2);
			return comment.substring(2);
		} else {
			return "";
		}
	}

	public static int getPosition(String script, Token token) {
		return getPosition(script, token.beginLine, token.beginColumn);
	}

	public static int getPosition(String script, int line, int column) {
		line = line - 1; // 0-based line
		column = column - 1;// 0-based column
		int linePos = 0;
		while (line >= 0) {
			String patternString = "\n";
			Pattern pattern = Pattern.compile(patternString);
			Matcher matcher = pattern.matcher(script);
			if (!matcher.find()) {
				return linePos + column;
			}
			int crPosition = matcher.start();

			int cut = crPosition + 1;
			if (line == 0) {
				cut = column;
			}
			linePos += cut;
			script = script.substring(cut);
			line--;
		}
		return linePos;
	}

	public String getScriptBody(String script) throws ParseException {
		SQLEngine parser = new SQLEngine(new ByteArrayInputStream(script
				.getBytes()));
		parser.SQLScript();
		Node root = parser.getRootNode();

		int index;
		index = getPosition(script, ((SimpleNode) root).first_token);

		return script.substring(index);
	}
}
