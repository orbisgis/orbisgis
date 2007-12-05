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
package org.gdms.drivers;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.gdms.BaseTest;
import org.gdms.DBTestSource;
import org.gdms.Geometries;
import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBSourceCreation;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.metadata.MetadataUtilities;
import org.gdms.data.types.AutoIncrementConstraint;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.LengthConstraint;
import org.gdms.data.types.NotNullConstraint;
import org.gdms.data.types.PrimaryKeyConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class DBDriverTest extends SourceTest {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	private static SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss");

	private static HashMap<Integer, Value> sampleValues = new HashMap<Integer, Value>();

	private static int[] geometryTypes = new int[] {
			GeometryConstraint.POINT_2D, GeometryConstraint.POINT_3D,
			GeometryConstraint.LINESTRING_2D, GeometryConstraint.LINESTRING_3D,
			GeometryConstraint.POLYGON_2D, GeometryConstraint.POLYGON_3D,
			GeometryConstraint.MULTI_POINT_2D,
			GeometryConstraint.MULTI_POINT_3D,
			GeometryConstraint.MULTI_LINESTRING_2D,
			GeometryConstraint.MULTI_LINESTRING_3D,
			GeometryConstraint.MULTI_POLYGON_2D,
			GeometryConstraint.MULTI_POLYGON_3D, GeometryConstraint.MIXED };

	static {
		try {
			sampleValues.put(Type.BINARY, ValueFactory.createValue(new byte[] {
					(byte) 4, (byte) 5, (byte) 6 }));
			sampleValues.put(Type.BOOLEAN, ValueFactory.createValue(true));
			sampleValues.put(Type.BYTE, ValueFactory.createValue((byte) 200));
			sampleValues.put(Type.DATE, ValueFactory.createValue(sdf
					.parse("1980-09-05")));
			sampleValues.put(Type.DOUBLE, ValueFactory.createValue(4.5d));
			sampleValues.put(Type.FLOAT, ValueFactory.createValue(4.5f));
			sampleValues.put(Type.GEOMETRY, ValueFactory
					.createValue(new GeometryFactory()
							.createPoint(new Coordinate(193, 9285))));
			sampleValues.put(Type.INT, ValueFactory.createValue(324));
			sampleValues.put(Type.LONG, ValueFactory.createValue(1290833232L));
			sampleValues.put(Type.SHORT, ValueFactory
					.createValue((short) 64000));
			sampleValues.put(Type.STRING, ValueFactory.createValue("kasdjusk"));
			sampleValues.put(Type.TIME, ValueFactory.createValue(new Time(stf
					.parse("15:34:40").getTime())));
			sampleValues.put(Type.TIMESTAMP, ValueFactory.createValue(Timestamp
					.valueOf("1980-07-23 15:34:40.2345")));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private DBSource postgreSQLDBSource = new DBSource("127.0.0.1", 5432,
			"gdms", "postgres", "postgres", "alltypes", "jdbc:postgresql");
	private DBTestSource postgreSQLSrc = new DBTestSource("source",
			"org.postgresql.Driver", SourceTest.internalData
					+ "postgresAllTypes.sql", postgreSQLDBSource);
	private DBSource hsqldbDBSource = new DBSource(null, 0,
			SourceTest.backupDir + File.separator + "hsqldbAllTypes", "sa",
			null, "alltypes", "jdbc:hsqldb:file");
	private DBTestSource hsqldbSrc = new DBTestSource("source",
			"org.hsqldb.jdbcDriver", SourceTest.internalData
					+ "hsqldbAllTypes.sql", hsqldbDBSource);
	private DBSource h2DBSource = new DBSource(null, 0, SourceTest.backupDir
			+ File.separator + "h2AllTypes", "sa", null, "alltypes", "jdbc:h2");
	private DBTestSource h2Src = new DBTestSource("source", "org.h2.Driver",
			SourceTest.internalData + "h2AllTypes.sql", h2DBSource);

	private void testReadAllTypes(DBSource dbSource, DBTestSource src)
			throws Exception {
		src.backup();
		readAllTypes();
	}

	private void readAllTypes() throws NoSuchTableException,
			DataSourceCreationException, DriverException,
			FreeingResourcesException, NonEditableDataSourceException {
		DataSource ds = SourceTest.dsf.getDataSource("source");

		ds.open();
		Metadata m = ds.getMetadata();
		ds.insertEmptyRow();
		for (int i = 0; i < m.getFieldCount(); i++) {
			Type fieldType = m.getFieldType(i);
			if (MetadataUtilities.isWritable(fieldType)) {
				ds.setFieldValue(0, i, sampleValues
						.get(fieldType.getTypeCode()));
			}
		}
		Value[] firstRow = ds.getRow(0);
		ds.commit();
		ds.open();
		Value[] commitedRow = ds.getRow(0);
		ds.commit();
		ds.open();
		Value[] reCommitedRow = ds.getRow(0);
		assertTrue(BaseTest
				.equals(reCommitedRow, commitedRow, ds.getMetadata()));
		assertTrue(BaseTest.equals(firstRow, commitedRow, ds.getMetadata()));
		ds.commit();
	}

	public void testReadAllTypesPostgreSQL() throws Exception {
		testReadAllTypes(postgreSQLDBSource, postgreSQLSrc);
	}

	public void testReadAllTypesHSQLDB() throws Exception {
		testReadAllTypes(hsqldbDBSource, hsqldbSrc);
	}

	public void testReadAllTypesH2() throws Exception {
		testReadAllTypes(h2DBSource, h2Src);
	}

	private void testCreateAllTypes(DBSource dbSource, boolean byte_,
			boolean stringLength) throws Exception {
		DefaultMetadata metadata = new DefaultMetadata();
		metadata.addField("f1", Type.BINARY);
		metadata.addField("f2", Type.BOOLEAN);
		if (byte_) {
			metadata.addField("f3", Type.BYTE);
		}
		metadata.addField("f4", Type.DATE);
		metadata.addField("f5", Type.DOUBLE);
		metadata.addField("f6", Type.FLOAT);
		metadata.addField("f7", Type.INT, new Constraint[] {
				new PrimaryKeyConstraint(), new AutoIncrementConstraint() });
		metadata.addField("f8", Type.LONG);
		metadata.addField("f9", Type.SHORT,
				new Constraint[] { new NotNullConstraint() });
		metadata.addField("f10", Type.STRING,
				new Constraint[] { new LengthConstraint(50) });
		metadata.addField("f11", Type.TIME);
		metadata.addField("f12", Type.TIMESTAMP);

		DBSourceCreation dsc = new DBSourceCreation(dbSource, metadata);
		dsf.createDataSource(dsc);
		readAllTypes();

		DataSource ds = dsf.getDataSource("source");
		ds.open();
		if (stringLength) {
			assertTrue(check("f10", ConstraintNames.LENGTH, "50", ds));
		}
		assertTrue(check("f9", ConstraintNames.NOT_NULL, ds));
		assertTrue(check("f7", ConstraintNames.NOT_NULL, ds));
		assertTrue(check("f7", ConstraintNames.AUTO_INCREMENT, ds));
		assertTrue(check("f7", ConstraintNames.PK, ds));
		assertTrue(check("f7", ConstraintNames.READONLY, ds));
	}

	private boolean check(String fieldName, ConstraintNames constraintName,
			String value, DataSource ds) throws DriverException {
		int fieldId = ds.getFieldIndexByName(fieldName);
		Type type = ds.getMetadata().getFieldType(fieldId);
		return (type.getConstraintValue(constraintName).equals(value));
	}

	private boolean check(String fieldName, ConstraintNames constraintName,
			DataSource ds) throws DriverException {
		int fieldId = ds.getFieldIndexByName(fieldName);
		Type type = ds.getMetadata().getFieldType(fieldId);
		return (type.getConstraint(constraintName) != null);
	}

	public void testCreateAllTypesH2() throws Exception {
		DBTestSource src = new DBTestSource("source", "org.h2.Driver",
				SourceTest.internalData + "removeAllTypes.sql", h2DBSource);
		SourceTest.dsf.getSourceManager().removeAll();
		src.backup();
		testCreateAllTypes(h2DBSource, true, false);
	}

	public void testCreateAllTypesHSQLDB() throws Exception {
		DBTestSource src = new DBTestSource("source", "org.hsqldb.jdbcDriver",
				SourceTest.internalData + "removeAllTypes.sql", hsqldbDBSource);
		SourceTest.dsf.getSourceManager().removeAll();
		src.backup();
		testCreateAllTypes(hsqldbDBSource, true, true);
	}

	public void testCreateAllTypesPostgreSQL() throws Exception {
		DBTestSource src = new DBTestSource("source", "org.postgresql.Driver",
				SourceTest.internalData + "removeAllTypes.sql",
				postgreSQLDBSource);
		SourceTest.dsf.getSourceManager().removeAll();
		src.backup();
		testCreateAllTypes(postgreSQLDBSource, false, true);
	}

	private void testSQLGeometryConstraint(DBSource dbSource, int geometryType)
			throws Exception {
		DefaultMetadata metadata = new DefaultMetadata();
		metadata.addField("f1", Type.GEOMETRY,
				new Constraint[] { new GeometryConstraint(geometryType) });
		metadata.addField("f2", Type.INT,
				new Constraint[] { new PrimaryKeyConstraint() });
		DBSourceCreation dsc = new DBSourceCreation(dbSource, metadata);
		dsf.createDataSource(dsc);

		DataSource ds = dsf.getDataSource(dbSource);
		ds.open();
		int spatialIndex = ds.getFieldIndexByName("f1");
		Metadata met = ds.getMetadata();
		Type spatialType = met.getFieldType(spatialIndex);
		GeometryConstraint gc = (GeometryConstraint) spatialType
				.getConstraint(ConstraintNames.GEOMETRY);
		assertTrue(gc != null);
		assertTrue(gc.getGeometryType() == geometryType);
		ds.cancel();
	}

	public void testPostgreSQLGeometryConstraint() throws Exception {
		DBTestSource src = new DBTestSource("source", "org.postgresql.Driver",
				SourceTest.internalData + "removeAllTypes.sql",
				postgreSQLDBSource);
		for (int i = 0; i < geometryTypes.length; i++) {
			SourceTest.dsf.getSourceManager().removeAll();
			src.backup();
			testSQLGeometryConstraint(postgreSQLDBSource, geometryTypes[i]);
		}
	}

	public void testPostgreSQLRemoveColumnAddColumnSameName() throws Exception {
		DBTestSource src = postgreSQLSrc;
		SourceTest.dsf.getSourceManager().removeAll();
		src.backup();
		DataSource ds = SourceTest.dsf.getDataSource(postgreSQLDBSource);
		ds.open();
		ds.addField("the_geom", TypeFactory.createType(Type.GEOMETRY));
		ds.commit();
		ds.open();
		ds.removeField(ds.getFieldIndexByName("the_geom"));
		ds.commit();
		ds.open();
		ds.addField("the_geom", TypeFactory.createType(Type.GEOMETRY,
				new Constraint[] { new GeometryConstraint(
						GeometryConstraint.POINT_2D) }));
		ds.commit();
	}

	public void testPostgreSQLReadWriteAllGeometryTypes() throws Exception {
		DBTestSource src = new DBTestSource("source", "org.postgresql.Driver",
				SourceTest.internalData + "removeAllTypes.sql",
				postgreSQLDBSource);
		SourceTest.dsf.getSourceManager().removeAll();
		src.backup();

		DefaultMetadata metadata = new DefaultMetadata();
		metadata.addField("f1", Type.GEOMETRY);
		metadata.addField("f2", Type.INT, new Constraint[] {
				new PrimaryKeyConstraint(), new AutoIncrementConstraint() });

		DBSourceCreation dsc = new DBSourceCreation(postgreSQLDBSource,
				metadata);
		dsf.createDataSource(dsc);
		DataSource ds = dsf.getDataSource(postgreSQLDBSource);
		ds.open();
		int spatialIndex = ds.getFieldIndexByName("f1");
		Geometry[] geometries = new Geometry[] { Geometries.getPoint3D(),
				Geometries.getLineString3D(), Geometries.getPolygon3D(),
				Geometries.getMultiPoint3D(),
				Geometries.getMultilineString3D(),
				Geometries.getMultiPolygon3D() };
		for (int i = 0; i < geometries.length; i++) {
			ds.insertEmptyRow();
			Value geom = ValueFactory.createValue(geometries[i]);
			ds.setFieldValue(ds.getRowCount() - 1, spatialIndex, geom);
		}

		ds.commit();

		ds.open();
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		for (int i = 0; i < geometries.length; i++) {
			assertTrue(geometries[i].equals(sds.getGeometry(i)));
		}
		ds.cancel();
	}

	public void testShapefile2PostgreSQL() throws Exception {
		// Delete the table if exists
		DBSource dbSource = new DBSource("192.168.10.53", 5432, "gdms",
				"postgres", "postgres", "testShapefile2PostgreSQL",
				"jdbc:postgresql");
		try {
			execute(dbSource, "DROP TABLE \"testShapefile2PostgreSQL\";");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// register both sources
		String registerDB = "select register('postgresql','"
				+ dbSource.getHost() + "'," + " '" + dbSource.getPort() + "','"
				+ dbSource.getDbName() + "','" + dbSource.getUser() + "','"
				+ dbSource.getPassword() + "'," + "'" + dbSource.getTableName()
				+ "','bati');";
		String registerFile = "select register('" + externalData
				+ "cours/shape/ile_de_nantes_bati.shp','ile_de_nantes_bati');";
		dsf.executeSQL(registerDB);
		dsf.executeSQL(registerFile);

		// Do the migration
		String load = "create table bati as select * "
				+ "from ile_de_nantes_bati";
		dsf.executeSQL(load);

		// Get each value
		SpatialDataSourceDecorator db = new SpatialDataSourceDecorator(dsf
				.getDataSource("bati"));
		SpatialDataSourceDecorator file = new SpatialDataSourceDecorator(dsf
				.getDataSource("ile_de_nantes_bati"));
		db.open();
		file.open();
		assertTrue(db.getRowCount() == file.getRowCount());
		for (int i = 0; i < db.getRowCount(); i++) {
			assertTrue(db.getGeometry(i).equalsExact(file.getGeometry(i)));
		}
		db.cancel();
		file.cancel();
	}

	private void execute(DBSource dbSource, String statement) throws Exception {
		Class.forName("org.postgresql.Driver").newInstance();
		String connectionString = dbSource.getPrefix() + ":";
		if (dbSource.getHost() != null) {
			connectionString += "//" + dbSource.getHost();

			if (dbSource.getPort() != -1) {
				connectionString += (":" + dbSource.getPort());
			}
			connectionString += "/";
		}

		connectionString += (dbSource.getDbName());

		Connection c = DriverManager.getConnection(connectionString, dbSource
				.getUser(), dbSource.getPassword());

		Statement st = c.createStatement();
		st.execute(statement);
		st.close();
		c.close();
	}
}
