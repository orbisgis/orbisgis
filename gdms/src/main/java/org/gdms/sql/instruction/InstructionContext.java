package org.gdms.sql.instruction;

import org.gdms.data.DataSource;
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
	 * DataSource which data will be used to read field values at expression
	 * evaluation
	 */
	private DataSource ds;

	/** DataSources of the 'from' clause */
	private DataSource[] fromTables;

	/** DataSourceFactory involved in the execution */
	private DataSourceFactory dsFActory;

	/**
	 * Gets the datasource of the select instruction without taking into account
	 * the where clause
	 *
	 * @return Returns the ds.
	 */
	public DataSource getDs() {
		return ds;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param ds
	 *            The ds to set.
	 */
	public void setDs(DataSource ds) {
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
	 * @param dsFActory
	 *            The dsFActory to set.
	 */
	public void setDSFActory(DataSourceFactory dsFActory) {
		this.dsFActory = dsFActory;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return Returns the fromTables.
	 */
	public DataSource[] getFromTables() {
		return fromTables;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param fromTables
	 *            The fromTables to set.
	 */
	public void setFromTables(DataSource[] fromTables) {
		this.fromTables = fromTables;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param fromTable
	 *            DOCUMENT ME!
	 */
	public void setFromTable(DataSource fromTable) {
		setFromTables(new DataSource[] { fromTable });
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
	 * @param sql
	 *            The sql to set.
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}
}
