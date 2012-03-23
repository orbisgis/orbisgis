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
package org.gdms.driver.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;

import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.hsqldb.TinyIntRule;
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

/**
 * Driver to access the HSQLDB Sources
 * 
 */
public final class HSQLDBDriver extends DefaultDBDriver {
	private static Exception driverException;
	public static final String DRIVER_NAME = "HSQLDB driver";

        private static final Logger LOG = Logger.getLogger(HSQLDBDriver.class);

	static {
		try {
			Class.forName("org.hsqldb.jdbcDriver").newInstance();
		} catch (Exception ex) {
			driverException = ex;
		}
	}

	@Override
	public Connection getConnection(String host, int port, boolean ssl, String dbName,
			String user, String password) throws SQLException {
            LOG.trace("Retrieveing connection");
		if (driverException != null) {
			throw new UnsupportedOperationException(driverException);
		}

		final String connectionString = "jdbc:hsqldb:file:" + dbName;
		final Properties p = new Properties();
		p.put("shutdown", "true");

		return DriverManager.getConnection(connectionString, p);
	}

	@Override
	public String getDriverId() {
		return DRIVER_NAME;
	}

	@Override
	public String getStatementString(Timestamp ts) {
		return "'" + ts.toString() + "'";
	}

	@Override
	protected String getCreateTableKeyWord() {
		return "CREATE CACHED TABLE";
	}

	@Override
	public String[] getPrefixes() {
		return new String[] { "jdbc:hsqldb:file" };
	}

	public Number[] getScope(int dimension) throws DriverException {
		return null;
	}

	@Override
	public void beginTrans(Connection con) throws SQLException {
            LOG.trace("Beginning transaction");
		execute(con, "SET AUTOCOMMIT FALSE");
	}

	@Override
	public void commitTrans(Connection con) throws SQLException {
            LOG.trace("Commiting transaction");
		execute(con, "COMMIT;SET AUTOCOMMIT TRUE");
	}

	@Override
	public void rollBackTrans(Connection con) throws SQLException {
            LOG.trace("Transaction rollback");
		execute(con, "ROLLBACK;SET AUTOCOMMIT TRUE");
	}

	@Override
	protected String getAutoIncrementDefaultValue() {
		return "null";
	}

	/**
         * @param tableName
         * @param oldName
         * @param newName
         * @return
         * @see org.gdms.driver.jdbc.DefaultDBDriver#getChangeFieldNameSQL(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public String getChangeFieldNameSQL(String tableName, String oldName,
			String newName) {
		return "ALTER TABLE \"" + tableName + "\" ALTER COLUMN \"" + oldName
				+ "\" RENAME TO \"" + newName + "\"";
	}
        
       @Override
        public int getSupportedType() {
                return SourceManager.DB;
        }

        @Override
	public int getType() {
		return SourceManager.DB;
	}

	@Override
	protected Type getGDMSType(ResultSetMetaData resultsetMetadata,
			List<String> pkFieldsList, List<String> fkFieldsList, int jdbcFieldIndex) throws SQLException,
			DriverException {
		int jdbcType = resultsetMetadata.getColumnType(jdbcFieldIndex);
		switch (jdbcType) {
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
		case Types.CLOB:
			int columnSize = resultsetMetadata
					.getColumnDisplaySize(jdbcFieldIndex);
			if ((columnSize == 32766) || (columnSize == 0)) {
				List<Constraint> constraints = addGlobalConstraints(
						resultsetMetadata, pkFieldsList, fkFieldsList, jdbcFieldIndex);
				return TypeFactory.createType(Type.STRING, constraints
						.toArray(new Constraint[constraints.size()]));
			}
		}
		return super.getGDMSType(resultsetMetadata, pkFieldsList, fkFieldsList,
				jdbcFieldIndex);
	}

	@Override
	public ConversionRule[] getConversionRules() {
		return new ConversionRule[] { new HSQLDBAutoincrementRule(),
				new TinyIntRule(), new BinaryRule(), new BooleanRule(),
				new PGDoubleRule(), new PGIntRule(), new PGLongRule(),
				new PGShortRule(), new DateRule(), new FloatRule(),
				new StringRule(), new TimestampRule(), new TimeRule() };
	}

        @Override
	public String validateMetadata(Metadata metadata) {
		return null;
	}

	@Override
	public int getDefaultPort() {
		return 9001;
	}

	@Override
	public String getTypeDescription() {
		return "HSQL database";
	}

	@Override
	public String getTypeName() {
		return "HSQLDB";
	}

}