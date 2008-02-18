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
import org.orbisgis.IProgressMonitor;
import org.orbisgis.NullProgressMonitor;

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
	 * @param pm
	 *
	 * @return
	 * @throws ExecutionException
	 * @throws DriverException
	 * @throws SemanticException
	 */
	public ObjectDriver execute(IProgressMonitor pm) throws ExecutionException, SemanticException,
			DriverException {
		ObjectDriver ret = validateAndExecute(pm);

		return ret;
	}

	private ObjectDriver validateAndExecute(IProgressMonitor pm) throws ExecutionException,
			SemanticException, DriverException {
		validate();

		if (pm == null) {
			pm = new NullProgressMonitor();
		}

		ObjectDriver ret = op.getResult(pm);
		op.operationFinished();
		return ret;
	}

	private void validate() throws SemanticException, DriverException,
			NoSuchTableException {
		if (!op.isValidated()) {
			Preprocessor p = new Preprocessor(op);
			p.validate();
		}
	}

	/**
	 * Executes the instruction, registers the result and returns a DataSource
	 * to explore the result. The resulting DataSource cannot be commited
	 *
	 * @param pm
	 *
	 * @return
	 * @throws ExecutionException
	 * @throws DataSourceCreationException
	 * @throws DriverException
	 * @throws SemanticException
	 */
	public DataSource getDataSource(IProgressMonitor pm) throws ExecutionException,
			DataSourceCreationException, SemanticException, DriverException {
		ObjectDriver ret = validateAndExecute(pm);

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

	public Metadata getResultMetadata() throws DriverException,
			SemanticException {
		validate();
		return op.getResultMetadata();
	}

}
