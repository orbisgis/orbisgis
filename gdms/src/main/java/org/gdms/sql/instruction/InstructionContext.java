package org.gdms.sql.instruction;

import org.gdms.data.InternalDataSource;
import org.gdms.data.DataSourceFactory;


/**
 * Context information of the executing instruction
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class InstructionContext {
	/** query executing */
	private String sql;

	/**
	 * InternalDataSource which data will be used to read field values at expression
	 * evaluation
	 */
	private InternalDataSource ds;

	/** DataSources of the 'from' clause */
	private InternalDataSource[] fromTables;

	/** DataSourceFactory involved in the execution */
	private DataSourceFactory dsFActory;

	/**
	 * DOCUMENT ME!
	 *
	 * @return Returns the ds.
	 */
	public InternalDataSource getDs() {
		return ds;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param ds The ds to set.
	 */
	public void setDs(InternalDataSource ds) {
		this.ds = ds;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return Returns the dsFActory.
	 */
	public DataSourceFactory getDSFactory() {
		return dsFActory;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param dsFActory The dsFActory to set.
	 */
	public void setDSFActory(DataSourceFactory dsFActory) {
		this.dsFActory = dsFActory;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return Returns the fromTables.
	 */
	public InternalDataSource[] getFromTables() {
		return fromTables;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param fromTables The fromTables to set.
	 */
	public void setFromTables(InternalDataSource[] fromTables) {
		this.fromTables = fromTables;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param fromTable DOCUMENT ME!
	 */
	public void setFromTable(InternalDataSource fromTable) {
		setFromTables(new InternalDataSource[] { fromTable });
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return Returns the sql.
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param sql The sql to set.
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}
}
