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
import org.gdms.data.edition.PKInternalDataSource;
import org.gdms.data.edition.PKOrientedEditionSupport;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.DriverException;

/**
 * Adaptador de la interfaz DBDriver a la interfaz DataSource. Adapta las
 * interfaces de los drivers de base de datos a la interfaz DataSource.
 * 
 * @author Fernando Gonzalez Cortes
 */
@DriverDataSource
public class DBTableDataSourceAdapter extends DataSourceCommonImpl implements
		PKInternalDataSource {

	private DBDriver driver;

	private DBDataSourceSupport dbDataSource;

	private DBSource def;

	private PKOrientedEditionSupport pkOrientedEditionSupport;

	private DBMetadataEditionSupport dbmes;

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
		dbmes = new DBMetadataEditionSupport(this, def.getTableName(), driver);
		dbDataSource = new DBDataSourceSupport(this, def, driver);
		pkOrientedEditionSupport = new PKOrientedEditionSupport(this, def
				.getTableName(), driver, dbmes);
		driverDataSourceSupport = new DriverDataSourceImpl(driver);
		ocCounter = new OpenCloseCounter(this);
		this.driver = driver;
	}

	public int getFieldCount() throws DriverException {
		return dbmes.getFieldCount();
	}

	public int getFieldIndexByName(String fieldName) throws DriverException {
		return dbmes.getFieldIndexByName(fieldName);
	}

	public void cancel() throws DriverException, AlreadyClosedException {
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
		return getMetadata().getFieldName(fieldId);
	}

	/**
	 * @see org.gdms.driver.ObjectDriver#getFieldType(int)
	 */
	public Type getFieldType(int i) throws DriverException {
		return getMetadata().getFieldType(i);
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
		((DBReadWriteDriver) driver).execute(con, sql);
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

	public void open() throws DriverException {
		if (ocCounter.start()) {
			try {
				con = getConnection();
				((DBDriver) driver).open(con, def.getTableName(), null);

			} catch (SQLException e) {
				throw new DriverException(e);
			}

			dbmes.start();
			pkOrientedEditionSupport.beginTrans();
		}
	}

	public void commit() throws DriverException, FreeingResourcesException {
		if (ocCounter.nextStopCloses()) {
			try {
				pkOrientedEditionSupport.commitTrans();
			} catch (DriverException e) {
				throw new DriverException(e);
			}
			/*
			 * If we close before the pkOrientedEditionSupport.commitTrans() the
			 * calls to execute fire an AlreadyClosedException
			 */
			ocCounter.stop();
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
	 * @throws InvalidTypeException
	 * @see org.gdms.data.DataSource#getPKName(int)
	 */
	public String getPKName(int fieldId) throws DriverException,
			InvalidTypeException {
		return dbDataSource.getPKName(fieldId);
	}

	/**
	 * @throws InvalidTypeException
	 * @see org.gdms.data.DataSource#getPKCardinality()
	 */
	public int getPKCardinality() throws DriverException, InvalidTypeException {
		return dbDataSource.getPKCardinality();
	}

	/**
	 * @throws InvalidTypeException 
	 * @throws DriverException 
	 * @throws InvalidTypeException
	 * @see org.gdms.data.DataSource#getPKNames()
	 */
	public String[] getPKNames() throws DriverException {
		
		return dbDataSource.getPKNames();
	}

	/**
	 * @see org.gdms.data.DataSource#saveData(org.gdms.data.DataSource)
	 */
	public void saveData(DataSource dataSource) throws DriverException {
		dataSource.open();

		if (driver instanceof DBReadWriteDriver) {
			Connection con;
			try {
				con = getConnection();
				((DBReadWriteDriver) driver).beginTrans(con);
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
				((DBReadWriteDriver) driver).execute(getConnection(),
						InnerDBUtils.createInsertStatement(def.getTableName(),
								row, dataSource.getFieldNames(), driver));
			} catch (SQLException e) {

				if (driver instanceof DBReadWriteDriver) {
					try {
						Connection con = getConnection();
						((DBReadWriteDriver) driver).rollBackTrans(con);
					} catch (SQLException e1) {
						throw new DriverException(e1);
					}
				}

				throw new DriverException(e);
			}
		}

		if (driver instanceof DBReadWriteDriver) {
			try {
				Connection con = getConnection();
				((DBReadWriteDriver) driver).commitTrans(con);
			} catch (SQLException e) {
				throw new DriverException(e);
			}
		}

		dataSource.cancel();
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

	public void addField(String name, Type type) throws DriverException {
		dbmes.addField(name, type);
		pkOrientedEditionSupport.addField();
	}

	public void addMetadataEditionListener(MetadataEditionListener listener) {
		dbmes.addMetadataEditionListener(listener);
	}

	public Metadata getMetadata() {
		return dbmes.getDataSourceMetadata();
	}

	public int getOriginalFieldCount() throws DriverException {
		return dbmes.getOriginalFieldCount();
	}

	public void removeField(int index) throws DriverException {
		dbmes.removeField(index);
		pkOrientedEditionSupport.removeField(index);
	}

	public void removeMetadataEditionListener(MetadataEditionListener listener) {
		dbmes.removeMetadataEditionListener(listener);
	}

	public void setFieldName(int index, String name) throws DriverException {
		dbmes.setFieldName(index, name);
		pkOrientedEditionSupport.setFieldName();
	}

	public Metadata getOriginalMetadata() throws DriverException {
		return dbDataSource.getMetadata();
	}

	public String check(int fieldId, Value value) throws DriverException {
		return getMetadata().getFieldType(fieldId).check(value);
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