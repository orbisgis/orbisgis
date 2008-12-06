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
package org.gdms.driver.h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Properties;

import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.jdbc.BinaryRule;
import org.gdms.driver.jdbc.BooleanRule;
import org.gdms.driver.jdbc.ConversionRule;
import org.gdms.driver.jdbc.DateRule;
import org.gdms.driver.jdbc.DefaultDBDriver;
import org.gdms.driver.jdbc.FloatRule;
import org.gdms.driver.jdbc.StringRule;
import org.gdms.driver.jdbc.TimeRule;
import org.gdms.driver.jdbc.TimestampRule;
import org.gdms.driver.postgresql.PGDoubleRule;
import org.gdms.driver.postgresql.PGIntRule;
import org.gdms.driver.postgresql.PGLongRule;
import org.gdms.driver.postgresql.PGShortRule;
import org.gdms.source.SourceManager;
import org.h2spatial.SQLCodegenerator;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKTWriter;

/**
 * DOCUMENT ME!
 * 
 * @author Erwan Bocher
 * 
 */
public class H2spatialDriver extends DefaultDBDriver implements
		DBReadWriteDriver {
	private static Exception driverException;

	private static WKBReader wkbreader = new WKBReader();

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
		String connectionString;
		if ((null == host) || (0 == host.length()) || (dbName.startsWith("/"))) {
			connectionString = "jdbc:h2:file:" + dbName;
		} else {
			connectionString = "jdbc:h2:tcp://" + host + ":" + port + "/"
					+ dbName;
		}
		final Properties p = new Properties();
		p.put("shutdown", "true");

		final Connection con = DriverManager.getConnection(connectionString,
				user, password);
		final Statement stat = con.createStatement();
		SQLCodegenerator.addSpatialFunctions(stat);
		stat.close();
		return con;
	}

	@Override
	protected Type getGDMSType(ResultSetMetaData resultsetMetadata,
			List<String> pkFieldsList, int jdbcFieldIndex) throws SQLException,
			DriverException, InvalidTypeException {
		if (isTheGeometricField(jdbcFieldIndex)) {
			return TypeFactory.createType(Type.GEOMETRY);
		} else {
			int jdbcType = resultsetMetadata.getColumnType(jdbcFieldIndex);
			switch (jdbcType) {
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			case Types.CLOB:
				if (resultsetMetadata.getColumnDisplaySize(jdbcFieldIndex) == 0) {
					List<Constraint> constraints = addGlobalConstraints(
							resultsetMetadata, pkFieldsList, jdbcFieldIndex);
					return TypeFactory.createType(Type.STRING, constraints
							.toArray(new Constraint[0]));
				}
			}
			return super.getGDMSType(resultsetMetadata, pkFieldsList,
					jdbcFieldIndex);
		}
	}

	private boolean isTheGeometricField(final int jdbcFieldId)
			throws SQLException {
		final int typeCode = getResultsetMetadata().getColumnType(jdbcFieldId);
		String fieldName = getResultsetMetadata().getColumnName(jdbcFieldId);
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
			if (isTheGeometricField(fieldId)) {
				Geometry geom = null;
				try {
					final byte[] geomBytes = getResultSet().getBytes(fieldId);
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
	 * @see com.hardcode.driverManager.Driver#getDriverId()
	 */
	public String getDriverId() {
		return DRIVER_NAME;
	}

	/**
	 * @see org.gdms.data.values.ValueWriter#getStatementString(GeometryValue)
	 */
	public String getStatementString(Geometry g) {
		WKTWriter writer;
		if (!Double.isNaN(g.getCoordinate().z)) {
			writer = new WKTWriter(3);
		} else {
			writer = new WKTWriter(2);
		}
		return "GEOMFROMTEXT('" + writer.write(g) + "'," + g.getSRID() + ")";
	}

	@Override
	public String[] getPrefixes() {
		return new String[] { "jdbc:h2" };
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

	public int getType() {
		return SourceManager.DB | SourceManager.VECTORIAL;
	}

	@Override
	public ConversionRule[] getConversionRules() {
		return new ConversionRule[] { new H2AutoincrementRule(),
				new BinaryRule(), new BooleanRule(), new TinyIntRule(),
				new DateRule(), new PGDoubleRule(), new PGIntRule(),
				new PGLongRule(), new PGShortRule(), new FloatRule(),
				new StringRule(), new TimestampRule(), new TimeRule(),
				new H2GeometryRule() };
	}

	public String validateMetadata(Metadata metadata) {
		return null;
	}

	@Override
	public int getDefaultPort() {
		return 9092;
	}

	@Override
	public String getTypeDescription() {
		return "H2 database engine";
	}

	@Override
	public String getTypeName() {
		return "H2";
	}
}