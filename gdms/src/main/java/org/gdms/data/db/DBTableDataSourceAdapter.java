package org.gdms.data.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCommonImpl;
import org.gdms.data.DriverDataSource;
import org.gdms.data.DriverDataSourceImpl;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.InnerDBUtils;
import org.gdms.data.OpenCloseCounter;
import org.gdms.data.edition.DBMetadataEditionSupport;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.edition.PKEditableDataSource;
import org.gdms.data.edition.PKOrientedEditionSupport;
import org.gdms.data.metadata.DriverMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DBTransactionalDriver;
import org.gdms.driver.DriverException;

/**
 * Adaptador de la interfaz DBDriver a la interfaz DataSource. Adapta las
 * interfaces de los drivers de base de datos a la interfaz DataSource.
 *
 * @author Fernando Gonzalez Cortes
 */
@DriverDataSource
public class DBTableDataSourceAdapter extends DataSourceCommonImpl implements
		DataSource, PKEditableDataSource {

	private DBDriver driver;

	private DBDataSourceSupport dbDataSource;

	private DBSource def;

	private PKOrientedEditionSupport pkOrientedEditionSupport;

	private DBMetadataEditionSupport mes;

	private DriverDataSourceImpl driverDataSourceSupport;

	protected OpenCloseCounter ocCounter;

	protected Connection con;

	/**
	 * Creates a new DBTableDataSourceAdapter
	 *
	 */
	public DBTableDataSourceAdapter(String name, String alias, DBSource def,
			DBDriver driver) {
		super(name, alias);
		this.def = def;
		mes = new DBMetadataEditionSupport(this, def.getTableName(), driver);
		dbDataSource = new DBDataSourceSupport(this, def, driver);
		pkOrientedEditionSupport = new PKOrientedEditionSupport(this, def
				.getTableName(), driver, mes);
		driverDataSourceSupport = new DriverDataSourceImpl(driver);
		ocCounter = new OpenCloseCounter(this);
		this.driver = driver;
	}

	public int getFieldCount() throws DriverException {
		return mes.getFieldCount();
	}

	public int getFieldIndexByName(String fieldName) throws DriverException {
		return mes.getFieldIndexByName(fieldName);
	}

	public void rollBackTrans() throws DriverException, AlreadyClosedException {
		if (ocCounter.stop()) {
			try {
				driver.close(con);
				pkOrientedEditionSupport.rollBackTrans();
				con.close();
				con = null;
			} catch (SQLException e) {
				ocCounter.start();
				throw new DriverException(e);
			}
		}
	}

	/**
	 * @see org.gdms.driver.ObjectDriver#getRowCount()
	 */
	public long getRowCount() throws DriverException {
		return pkOrientedEditionSupport.getRowCount();
	}

	/**
	 * @see org.gdms.data.edition.EditableDataSource#getFieldName(int)
	 */
	public String getFieldName(int fieldId) throws DriverException {
		return getDataSourceMetadata().getFieldName(fieldId);
	}

	/**
	 * @see org.gdms.driver.ObjectDriver#getFieldType(int)
	 */
	public int getFieldType(int i) throws DriverException {
		return getDataSourceMetadata().getFieldType(i);
	}

	/**
	 * @see org.gdms.driver.ObjectDriver#getFieldValue(long, int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return pkOrientedEditionSupport.getFieldValue(rowIndex, fieldId);
	}

	public Value getOriginalFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return dbDataSource.getOriginalFieldValue(rowIndex, fieldId);
	}

	public long getOriginalRowCount() throws DriverException {
		return dbDataSource.getOriginalRowCount();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return
	 */
	public DBDriver getDriver() {
		return driver;
	}

	/**
	 * Executes the 'sql' instruction
	 *
	 * @throws SQLException
	 *             If the execution fails
	 */
	public void execute(String sql) throws SQLException {
		driver.execute(con, sql);
	}

	/**
	 * Get's a connection to the driver
	 *
	 * @return Connection
	 *
	 * @throws SQLException
	 *             if the connection cannot be established
	 */
	public Connection getConnection() throws SQLException {
		if (con == null) {
			con = driver.getConnection(def.getHost(), def.getPort(), def
					.getDbName(), def.getUser(), def.getPassword());
		}
		return con;
	}

	/**
	 * @see org.gdms.data.DataSource#getDBMS()
	 */
	public String getDBMS() {
		return def.getDbms();
	}

	public void setTableName(String tableName) {
		def.setTableName(tableName);
	}

	public DBTableSourceDefinition getDataSourceDefinition() {
		return new DBTableSourceDefinition(def);
	}

	public void deleteRow(long rowId) throws DriverException {
		pkOrientedEditionSupport.deleteRow(rowId);
	}

	public void insertFilledRow(Value[] values) throws DriverException {
		pkOrientedEditionSupport.insertFilledRow(values);
	}

	public void insertEmptyRow() throws DriverException {
		pkOrientedEditionSupport.insertEmptyRow();
	}

	public void insertFilledRowAt(long index, Value[] values)
			throws DriverException {
		pkOrientedEditionSupport.insertFilledRowAt(index, values);
	}

	public void insertEmptyRowAt(long index) throws DriverException {
		pkOrientedEditionSupport.insertEmptyRowAt(index);
	}

	public void beginTrans() throws DriverException {
		if (ocCounter.start()) {
			try {
				con = getConnection();
				((DBDriver) driver).open(con, def.getTableName(), null);

			} catch (SQLException e) {
				throw new DriverException(e);
			}

			mes.start();
			pkOrientedEditionSupport.beginTrans();
		}
	}

	public void commitTrans() throws DriverException, FreeingResourcesException {
		if (ocCounter.stop()) {
			try {
				pkOrientedEditionSupport.commitTrans();
			} catch (DriverException e) {
				ocCounter.start();
				throw new DriverException(e);
			}
			try {
				driver.close(con);
				con.close();
				con = null;
			} catch (SQLException e) {
				throw new FreeingResourcesException(e);
			} catch (DriverException e) {
				throw new FreeingResourcesException(e);
			}
		}
	}

	public void setFieldValue(long row, int fieldId, Value value)
			throws DriverException {
		pkOrientedEditionSupport.setFieldValue(row, fieldId, value);
	}

	/**
	 * @see org.gdms.data.DataSource#getPKName(int)
	 */
	public String getPKName(int fieldId) throws DriverException {
		return dbDataSource.getPKName(fieldId);
	}

	/**
	 * @see org.gdms.data.DataSource#getPKCardinality()
	 */
	public int getPKCardinality() throws DriverException {
		return dbDataSource.getPKCardinality();
	}

	/**
	 * @see org.gdms.data.DataSource#getPKNames()
	 */
	public String[] getPKNames() throws DriverException {
		return dbDataSource.getPKNames();
	}

	/**
	 * @see org.gdms.data.DataSource#saveData(org.gdms.data.DataSource)
	 */
	public void saveData(DataSource dataSource) throws DriverException {
		dataSource.beginTrans();

		if (driver instanceof DBTransactionalDriver) {
			Connection con;
			try {
				con = getConnection();
				((DBTransactionalDriver) driver).beginTrans(con);
			} catch (SQLException e) {
				throw new DriverException(e);
			}
		}

		for (int i = 0; i < dataSource.getRowCount(); i++) {
			Value[] row = new Value[dataSource.getFieldNames().length];
			for (int j = 0; j < row.length; j++) {
				row[j] = dataSource.getFieldValue(i, j);
			}

			try {
				driver.execute(getConnection(), InnerDBUtils
						.createInsertStatement(def.getTableName(), row,
								dataSource.getFieldNames(), driver));
			} catch (SQLException e) {

				if (driver instanceof DBTransactionalDriver) {
					try {
						Connection con = getConnection();
						((DBTransactionalDriver) driver).rollBackTrans(con);
					} catch (SQLException e1) {
						throw new DriverException(e1);
					}
				}

				throw new DriverException(e);
			}
		}

		if (driver instanceof DBTransactionalDriver) {
			try {
				Connection con = getConnection();
				((DBTransactionalDriver) driver).commitTrans(con);
			} catch (SQLException e) {
				throw new DriverException(e);
			}
		}

		dataSource.rollBackTrans();
	}

	public ValueCollection getPKValue(long rowIndex) throws DriverException {
		return dbDataSource.getPKValue(rowIndex);
	}

	public void addEditionListener(EditionListener listener) {
		pkOrientedEditionSupport.addEditionListener(listener);
	}

	public void removeEditionListener(EditionListener listener) {
		pkOrientedEditionSupport.removeEditionListener(listener);
	}

	public void setDispatchingMode(int dispatchingMode) {
		pkOrientedEditionSupport.setDispatchingMode(dispatchingMode);
	}

	public int getDispatchingMode() {
		return pkOrientedEditionSupport.getDispatchingMode();
	}

	public void addField(String name, String type) throws DriverException {
		addField(name, type, new String[0], new String[0]);
	}

	public void addField(String name, String type, String[] paramNames,
			String[] paramValues) throws DriverException {
		mes.addField(name, type, paramNames, paramValues);
		pkOrientedEditionSupport.addField();
	}

	public void addMetadataEditionListener(MetadataEditionListener listener) {
		mes.addMetadataEditionListener(listener);
	}

	public Metadata getDataSourceMetadata() {
		return mes.getDataSourceMetadata();
	}

	public int getOriginalFieldCount() throws DriverException {
		return mes.getOriginalFieldCount();
	}

	public void removeField(int index) throws DriverException {
		mes.removeField(index);
		pkOrientedEditionSupport.removeField(index);
	}

	public void removeMetadataEditionListener(MetadataEditionListener listener) {
		mes.removeMetadataEditionListener(listener);
	}

	public void setFieldName(int index, String name) throws DriverException {
		mes.setFieldName(index, name);
		pkOrientedEditionSupport.setFieldName();
	}

	public Metadata getOriginalMetadata() throws DriverException {
		return dbDataSource.getDataSourceMetadata();
	}

	public DriverMetadata getDriverMetadata() throws DriverException {
		return mes.getDriverMetadata();
	}

	public int getType(String driverType) {
		return driverDataSourceSupport.getType(driverType);
	}

	public DriverMetadata getOriginalDriverMetadata() throws DriverException {
		return dbDataSource.getDriverMetadata();
	}

	public String check(int fieldId, Value value) throws DriverException {
		return dbDataSource.check(mes.getField(fieldId), value);
	}

	public void endUndoRedoAction() {
		pkOrientedEditionSupport.endUndoRedoAction();
	}

	public void startUndoRedoAction() {
		pkOrientedEditionSupport.startUndoRedoAction();
	}

	public boolean isModified() {
		return pkOrientedEditionSupport.isModified();
	}

	public long[] getWhereFilter() throws IOException {
		return null;
	}

	public boolean isOpen() {
		return ocCounter.isOpen();
	}

}
