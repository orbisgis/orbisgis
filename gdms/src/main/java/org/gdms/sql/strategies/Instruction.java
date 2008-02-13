package org.gdms.sql.strategies;

import java.util.ArrayList;

import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.parser.ParseException;

/**
 * Class that embeds an optimized instruction
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public class Instruction {
	private String sql;
	private Operator op;

	public Instruction(Operator op, String sql) {
		this.op = op;
		this.sql = sql;
	}

	public ObjectDriver execute() throws ExecutionException {
		ObjectDriver ret = op.getResult();
		op.operationFinished();

		return ret;
	}

	public String getSQL() {
		return sql;
	}

	public String[] getReferencedSources() throws ParseException,
			SemanticException, DriverException {
		return getReferencedTables(op);
	}

	private String[] getReferencedTables(Operator op) {
		ArrayList<String> ret = new ArrayList<String>();
		String[] tables = op.getReferencedTables();
		for (String table : tables) {
			ret.add(table);
		}
		for (int i = 0; i < op.getOperatorCount(); i++) {
			tables = getReferencedTables(op.getOperator(i));
			for (String table : tables) {
				ret.add(table);
			}
		}

		return ret.toArray(new String[0]);
	}

	public Metadata getResultMetadata() throws DriverException {
		return op.getResultMetadata();
	}

}
