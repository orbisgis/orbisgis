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

import java.sql.Connection;
import java.sql.SQLException;

import org.gdms.data.AbstractDataSource;
import org.gdms.data.AbstractDataSourceDefinition;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.metadata.MetadataUtilities;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.DriverUtilities;
import org.gdms.driver.ReadOnlyDriver;
import org.gdms.source.directory.DbDefinitionType;
import org.gdms.source.directory.DefinitionType;
import org.orbisgis.progress.IProgressMonitor;

/**
 * @author Fernando Gonzalez Cortes
 */
public class DBTableSourceDefinition extends AbstractDataSourceDefinition {
	protected DBSource def;

	/**
	 * Creates a new DBTableSourceDefinition
	 *
	 * @param driverName
	 *            Name of the driver used to access the data
	 */
	public DBTableSourceDefinition(DBSource def) {
		this.def = def;
	}

	public DataSource createDataSource(String tableName, IProgressMonitor pm)
			throws DataSourceCreationException {

		((ReadOnlyDriver) getDriver())
				.setDataSourceFactory(getDataSourceFactory());

		AbstractDataSource adapter = new DBTableDataSourceAdapter(
				getSource(tableName), def, (DBDriver) getDriver());
		adapter.setDataSourceFactory(getDataSourceFactory());

		return adapter;
	}

	@Override
	protected ReadOnlyDriver getDriverInstance() {
		return DriverUtilities.getDriver(getDataSourceFactory()
				.getSourceManager().getDriverManager(), def.getPrefix());
	}

	public DBSource getSourceDefinition() {
		return def;
	}

	public String getPrefix() {
		return def.getPrefix();
	}

	public void createDataSource(DataSource contents, IProgressMonitor pm)
			throws DriverException {
		contents.open();
		DBReadWriteDriver driver = (DBReadWriteDriver) getDriver();
		((ReadOnlyDriver) driver).setDataSourceFactory(getDataSourceFactory());
		Connection con;
		try {
			con = driver.getConnection(def.getHost(), def.getPort(), def
					.getDbName(), def.getUser(), def.getPassword());
		} catch (SQLException e) {
			throw new DriverException(e);
		}
		if (driver instanceof DBReadWriteDriver) {
			try {
				((DBReadWriteDriver) driver).beginTrans(con);
			} catch (SQLException e) {
				throw new DriverException(e);
			}
		}
		contents = getDataSourceWithPK(contents);
		driver.createSource(def, contents.getMetadata());

		for (int i = 0; i < contents.getRowCount(); i++) {
			if (i / 100 == i / 100.0) {
				if (pm.isCancelled()) {
					break;
				} else {
					pm.progressTo((int) (100 * i / contents.getRowCount()));
				}
			}
			Value[] row = new Value[contents.getFieldNames().length];
			for (int j = 0; j < row.length; j++) {
				row[j] = contents.getFieldValue(i, j);
			}

			try {
				Type[] fieldTypes = MetadataUtilities.getFieldTypes(contents
						.getMetadata());
				String sqlInsert = driver.getInsertSQL(def.getTableName(),
						contents.getFieldNames(), fieldTypes, row);
				((DBReadWriteDriver) driver).execute(con, sqlInsert);
			} catch (SQLException e) {

				if (driver instanceof DBReadWriteDriver) {
					try {
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
				((DBReadWriteDriver) driver).commitTrans(con);
			} catch (SQLException e) {
				throw new DriverException(e);
			}
		}

		contents.close();
	}

	private DataSource getDataSourceWithPK(DataSource ds)
			throws DriverException {
		Metadata metadata = ds.getMetadata();
		for (int i = 0; i < metadata.getFieldCount(); i++) {
			Type fieldType = metadata.getFieldType(i);
			if (fieldType.getConstraint(Constraint.PK) != null) {
				return ds;
			}
		}

		return new PKDataSourceAdapter(ds);
	}

	public DefinitionType getDefinition() {
		DbDefinitionType ret = new DbDefinitionType();
		ret.setDbName(def.getDbName());
		ret.setHost(def.getHost());
		ret.setPort(Integer.toString(def.getPort()));
		ret.setTableName(def.getTableName());
		ret.setPassword(def.getPassword());
		ret.setUser(def.getUser());
		ret.setPrefix(def.getPrefix());

		return ret;
	}

	public static DataSourceDefinition createFromXML(DbDefinitionType definition) {
		DBSource dbSource = new DBSource(definition.getHost(), Integer
				.parseInt(definition.getPort()), definition.getDbName(),
				definition.getUser(), definition.getPassword(), definition
						.getTableName(), definition.getPrefix());
		return new DBTableSourceDefinition(dbSource);
	}

	@Override
	public boolean equals(DataSourceDefinition obj) {
		if (obj instanceof DBTableSourceDefinition) {
			DBTableSourceDefinition dsd = (DBTableSourceDefinition) obj;
			if (equals(dsd.def.getDbms(), def.getDbms())
					&& equals(dsd.def.getDbName(), def.getDbName())
					&& equals(dsd.def.getHost(), def.getHost())
					&& equals(dsd.def.getPassword(), def.getPassword())
					&& (dsd.def.getPort() == def.getPort())
					&& equals(dsd.def.getUser(), def.getUser())
					&& equals(dsd.def.getTableName(), def.getTableName())
					&& equals(dsd.def.getPrefix(), def.getPrefix())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private boolean equals(String str, String str2) {
		if (str == null) {
			return str == null;
		} else {
			return str.equals(str2);
		}
	}

}
