package org.gdms.driver.h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.DefaultDBDriver;
import org.gdms.driver.DriverException;
import org.gdms.spatial.GeometryValue;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;

/**
 * DOCUMENT ME!
 *
 * @author Erwan Bocher
 *
 */
public class H2spatialDriver extends DefaultDBDriver implements
		DBReadWriteDriver {
	private static Exception driverException;

	public static WKBReader wkbreader = new WKBReader();

	public static final String DRIVER_NAME = "H2 driver";

	static {
		try {
			Class.forName("org.h2.Driver").newInstance();
		} catch (Exception ex) {
			driverException = ex;
		}
	}

	/**
	 * @see org.gdms.driver.DBDriver#getConnection(java.lang.String, int,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	public Connection getConnection(String host, int port, String dbName,
			String user, String password) throws SQLException {
		if (driverException != null) {
			throw new RuntimeException(driverException);
		}

		final String connectionString = "jdbc:h2:file:" + dbName;
		final Properties p = new Properties();
		p.put("shutdown", "true");

		return DriverManager.getConnection(connectionString, user, password);
	}

	@Override
	protected int getGDMSType(int jdbcType, int jdbcFieldNumber,
			String fieldName) throws DriverException {
		try {
			if (isTheGeometricField(jdbcFieldNumber, fieldName)) {
				return Type.GEOMETRY;
			} else {
				return super.getGDMSType(jdbcType, jdbcFieldNumber, fieldName);
			}
		} catch (SQLException e) {
			throw new DriverException(e);
		}
	}

	private boolean isTheGeometricField(final int jdbcFieldId, String fieldName)
			throws SQLException {
		final int typeCode = getResultsetMetadata().getColumnType(jdbcFieldId);

		return (fieldName.equalsIgnoreCase("the_geom") && (typeCode == Types.BLOB)) ? true
				: false;
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getFieldValue(long, int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		Value value = null;

		try {
			fieldId += 1;
			getResultSet().absolute((int) rowIndex + 1);
			if (isTheGeometricField(fieldId, getMetadata().getFieldName(
					fieldId - 1))) {
				Geometry geom = null;
				try {
					byte[] geomBytes = getResultSet().getBytes(fieldId);
					if (geomBytes != null) {
						geom = wkbreader.read(geomBytes);
						value = ValueFactory.createValue(geom);
					} else {
						value = ValueFactory.createNullValue();
					}
				} catch (ParseException e) {
					throw new DriverException(e);
				}
			} else {
				value = super.getFieldValue(rowIndex, fieldId - 1);
			}

			if (getResultSet().wasNull()) {
				return ValueFactory.createNullValue();
			} else {
				return value;
			}
		} catch (SQLException e) {
			throw new DriverException(e);
		}
	}

	/**
	 * @see com.hardcode.driverManager.Driver#getName()
	 */
	public String getName() {
		return DRIVER_NAME;
	}

	/**
	 * @see org.gdms.data.driver.DriverCommons#setDataSourceFactory(org.gdms.data.DataSourceFactory)
	 */
	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	/**
	 * @see org.gdms.data.values.ValueWriter#getStatementString(GeometryValue)
	 */
	public String getStatementString(Geometry g) {
		return "GEOMFROMTEXT('" + g.toText() + "'," + g.getSRID() + ")";
	}

	public boolean prefixAccepted(String prefix) {
		return "jdbc:h2".equals(prefix.toLowerCase());
	}

	public Number[] getScope(int dimension) throws DriverException {
		return null;
	}

	/**
	 * @see org.gdms.driver.DBTransactionalDriver#beginTrans(Connection)
	 */
	public void beginTrans(Connection con) throws SQLException {
		execute(con, "SET AUTOCOMMIT FALSE");
	}

	/**
	 * @see org.gdms.driver.DBTransactionalDriver#commitTrans(Connection)
	 */
	public void commitTrans(Connection con) throws SQLException {
		execute(con, "COMMIT;SET AUTOCOMMIT TRUE");
	}

	/**
	 * @see org.gdms.driver.DBTransactionalDriver#rollBackTrans(Connection)
	 */
	public void rollBackTrans(Connection con) throws SQLException {
		execute(con, "ROLLBACK;SET AUTOCOMMIT TRUE");
	}

	public String getChangeFieldNameSQL(String tableName, String oldName,
			String newName) {
		return "ALTER TABLE \"" + tableName + "\" ALTER COLUMN \"" + oldName
				+ "\" RENAME TO \"" + newName + "\"";
	}

	@Override
	protected String getSequenceKeyword() {
		return "IDENTITY";
	}

}