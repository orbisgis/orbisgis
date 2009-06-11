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
package org.gdms.data.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DriverDataSource;
import org.gdms.data.edition.Commiter;
import org.gdms.data.edition.DeleteEditionInfo;
import org.gdms.data.edition.EditionInfo;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.metadata.MetadataUtilities;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.DriverException;
import org.gdms.source.CommitListener;
import org.gdms.source.DefaultSourceManager;
import org.gdms.source.Source;

/**
 * Adapter to the DataSource interface for database drivers
 * 
 * @author Fernando Gonzalez Cortes
 */
public class DBTableDataSourceAdapter extends DriverDataSource implements
		Commiter, CommitListener {

	private DBDriver driver;

	private DBSource def;

	protected Connection con;

	private int[] cachedPKIndices;

	/**
	 * Creates a new DBTableDataSourceAdapter
	 * 
	 */
	public DBTableDataSourceAdapter(Source src, DBSource def, DBDriver driver) {
		super(src);
		this.def = def;
		this.driver = driver;
	}

	public void close() throws DriverException, AlreadyClosedException {
		driver.close(con);
		fireCancel(this);
		try {
			con.close();
			con = null;
		} catch (SQLException e) {
			throw new DriverException(e);
		}

		DefaultSourceManager sm = (DefaultSourceManager) getDataSourceFactory()
				.getSourceManager();
		sm.removeCommitListener(this);
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
	 * Get's a connection to the driver
	 * 
	 * @return Connection
	 * 
	 * @throws SQLException
	 *             if the connection cannot be established
	 */
	private Connection getConnection() throws SQLException {
		if (con == null) {
			con = driver.getConnection(def.getHost(), def.getPort(), def
					.getDbName(), def.getUser(), def.getPassword());
		}
		return con;
	}

	public void open() throws DriverException {
		try {
			con = getConnection();
			((DBDriver) driver).open(con, def.getTableName());
			fireOpen(this);
		} catch (SQLException e) {
			throw new DriverException(e);
		}

		DefaultSourceManager sm = (DefaultSourceManager) getDataSourceFactory()
				.getSourceManager();
		sm.addCommitListener(this);
	}

	/**
	 * @throws InvalidTypeException
	 * @throws DriverException
	 * @throws InvalidTypeException
	 * @see org.gdms.data.DataSource#getPKNames()
	 */
	private String[] getPKNames() throws DriverException {
		final String[] ret = new String[getPKCardinality()];

		for (int i = 0; i < ret.length; i++) {
			ret[i] = getPKName(i);
		}

		return ret;
	}

	/**
	 * @see org.gdms.data.DataSource#saveData(org.gdms.data.DataSource)
	 */
	public void saveData(DataSource dataSource) throws DriverException {
		dataSource.open();

		DBReadWriteDriver readWriteDriver = ((DBReadWriteDriver) driver);
		if (driver instanceof DBReadWriteDriver) {
			Connection con;
			try {
				con = getConnection();
				readWriteDriver.beginTrans(con);
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
				Type[] fieldTypes = MetadataUtilities.getFieldTypes(dataSource
						.getMetadata());
				String sql = readWriteDriver.getInsertSQL(def.getTableName(),
						dataSource.getFieldNames(), fieldTypes, row);

				readWriteDriver.execute(con, sql);
			} catch (SQLException e) {

				if (driver instanceof DBReadWriteDriver) {
					try {
						Connection con = getConnection();
						readWriteDriver.rollBackTrans(con);
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
				readWriteDriver.commitTrans(con);
			} catch (SQLException e) {
				throw new DriverException(e);
			}
		}

		dataSource.close();
	}

	public long[] getWhereFilter() throws IOException {
		return null;
	}

	@Override
	public boolean commit(List<PhysicalDirection> rowsDirections,
			String[] fieldNames, ArrayList<EditionInfo> schemaActions,
			ArrayList<EditionInfo> editionActions,
			ArrayList<DeleteEditionInfo> deletedPKs, DataSource modifiedSource)
			throws DriverException {
		try {
			((DBReadWriteDriver) driver).beginTrans(getConnection());
		} catch (SQLException e) {
			throw new DriverException(e);
		}

		String sql = null;
		try {
			for (EditionInfo info : schemaActions) {
				sql = info.getSQL(def.getTableName(), getPKNames(), fieldNames,
						(DBReadWriteDriver) driver);
				((DBReadWriteDriver) driver).execute(con, sql);
			}
			for (DeleteEditionInfo info : deletedPKs) {
				sql = info.getSQL(def.getTableName(), getPKNames(), fieldNames,
						(DBReadWriteDriver) driver);
				((DBReadWriteDriver) driver).execute(con, sql);
			}
			for (EditionInfo info : editionActions) {
				sql = info.getSQL(def.getTableName(), getPKNames(), fieldNames,
						(DBReadWriteDriver) driver);
				if (sql != null) {
					sql = info.getSQL(def.getTableName(), getPKNames(),
							fieldNames, (DBReadWriteDriver) driver);
					((DBReadWriteDriver) driver).execute(con, sql);
				}
			}
		} catch (SQLException e) {
			try {
				((DBReadWriteDriver) driver).rollBackTrans(getConnection());
			} catch (SQLException e1) {
				throw new DriverException(e1);
			}
			throw new DriverException(e.getMessage() + ":" + sql, e);
		}

		try {
			((DBReadWriteDriver) driver).commitTrans(getConnection());
		} catch (SQLException e) {
			throw new DriverException(e);
		}

		fireCommit(this);

		return true;
	}

	/**
	 * @throws InvalidTypeException
	 * @see org.gdms.data.DataSource#getPrimaryKeys()
	 */
	private int[] getPrimaryKeys() throws DriverException {
		if (cachedPKIndices == null) {
			cachedPKIndices = MetadataUtilities.getPKIndices(getMetadata());
		}
		return cachedPKIndices;
	}

	private String getPKName(int fieldId) throws DriverException {
		int[] fieldsId = getPrimaryKeys();
		return getMetadata().getFieldName(fieldsId[fieldId]);
	}

	private int getPKCardinality() throws DriverException {
		return getPrimaryKeys().length;
	}

	public void commitDone(String name) throws DriverException {
		sync();
	}

	public void syncWithSource() throws DriverException {
		sync();
	}

	private void sync() throws DriverException {
		try {
			driver.close(con);
			con.close();
			con = null;
			con = getConnection();
			((DBDriver) driver).open(con, def.getTableName());
		} catch (SQLException e) {
			throw new DriverException("Cannot close driver", e);
		}
	}

	public void isCommiting(String name, Object source) {
	}

}