/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
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
import org.gdms.data.metadata.Metadata;
import org.gdms.data.metadata.MetadataUtilities;
import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;
import org.gdms.driver.driverManager.Driver;

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

	public DataSource createDataSource(String tableName, String driverName)
			throws DataSourceCreationException {

		Driver d = getDataSourceFactory().getDriverManager().getDriver(
				driverName);
		((ReadOnlyDriver) d).setDataSourceFactory(getDataSourceFactory());

		AbstractDataSource adapter = new DBTableDataSourceAdapter(tableName,
				def, (DBDriver) d);
		adapter.setDataSourceFactory(getDataSourceFactory());

		return adapter;
	}

	public DBSource getSourceDefinition() {
		return def;
	}

	public String getPrefix() {
		return def.getPrefix();
	}

	public void createDataSource(String driverName, DataSource contents)
			throws DriverException {
		contents.open();
		DBReadWriteDriver driver = (DBReadWriteDriver) getDataSourceFactory()
				.getDriverManager().getDriver(driverName);
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

		contents.cancel();
	}

	private DataSource getDataSourceWithPK(DataSource ds)
			throws DriverException {
		Metadata metadata = ds.getMetadata();
		for (int i = 0; i < metadata.getFieldCount(); i++) {
			Type fieldType = metadata.getFieldType(i);
			if (fieldType.getConstraint(ConstraintNames.PK) != null) {
				return ds;
			}
		}

		return new PKDataSourceAdapter(ds);
	}
}
