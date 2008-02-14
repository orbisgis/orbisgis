package org.gdms.sql.strategies;

import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverLoadException;
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
	private DataSourceFactory dsf;

	public Instruction(DataSourceFactory dsf, Operator op, String sql) {
		this.op = op;
		this.sql = sql;
		this.dsf = dsf;
	}

	/**
	 * Executes the instruction and returns a source with the result of the
	 * query
	 *
	 * @return
	 * @throws ExecutionException
	 */
	public ObjectDriver execute() throws ExecutionException {
		ObjectDriver ret = op.getResult();
		op.operationFinished();

		return ret;
	}

	/**
	 * Executes the instruction, registers the result and returns a DataSource
	 * to explore the result. The resulting DataSource cannot be commited
	 *
	 * @return
	 * @throws ExecutionException
	 * @throws DataSourceCreationException
	 */
	public DataSource getDataSource() throws ExecutionException,
			DataSourceCreationException {
		ObjectDriver ret = op.getResult();
		op.operationFinished();

		String retName = dsf.getSourceManager().nameAndRegister(ret);
		try {
			return dsf.getDataSource(retName);
		} catch (DriverLoadException e) {
			throw new RuntimeException("bug!", e);
		} catch (NoSuchTableException e) {
			throw new RuntimeException("bug!", e);
		}
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
