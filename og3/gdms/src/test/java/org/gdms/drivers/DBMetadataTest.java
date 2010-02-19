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
package org.gdms.drivers;

import org.gdms.data.DataSource;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBSourceCreation;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.LengthConstraint;
import org.gdms.data.types.PrecisionConstraint;
import org.gdms.data.types.PrimaryKeyConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;

public class DBMetadataTest extends AbstractDBTest {

	public void testReadString() throws Exception {
		String tableName = "testpostgresqlmetadatastring";
		testString("CREATE TABLE \"" + tableName
				+ "\" (id integer primary key, limitedstring varchar(12), "
				+ "unlimitedstring varchar);", tableName,
				getPostgreSQLSource(tableName));
		sm.removeAll();
		tableName = "testhsqldbmetadatastring";
		testString("CREATE TABLE \"" + tableName
				+ "\" (id integer primary key, \"limitedstring\" varchar(12), "
				+ "\"unlimitedstring\" varchar);", tableName,
				getHSQLDBSource(tableName));
	}

	public void testH2ReadString() throws Exception {
		String tableName;
		tableName = "testh2metadatastring";
		DBSource source = getH2Source(tableName);
		deleteTable(source);
		executeScript(source, "CREATE TABLE \"" + tableName
				+ "\" (id integer primary key, \"limitedstring\" varchar(12), "
				+ "\"unlimitedstring\" varchar);");
		sm.register("source", source);

		DataSource ds = dsf.getDataSource("source");
		ds.open();
		Metadata m = ds.getMetadata();
		assertTrue(m.getFieldName(1).equals("limitedstring"));
		assertTrue(m.getFieldType(1).getConstraints().length == 0);
		assertTrue(m.getFieldName(2).equals("unlimitedstring"));
		assertTrue(m.getFieldType(2).getConstraints().length == 0);
		ds.close();
	}

	private void testString(String createSQL, String tableName, DBSource source)
			throws Exception {
		deleteTable(source);
		executeScript(source, createSQL);
		sm.register("source", source);

		DataSource ds = dsf.getDataSource("source");
		ds.open();
		Metadata m = ds.getMetadata();
		assertTrue(m.getFieldName(1).equals("limitedstring"));
		assertTrue(m.getFieldType(1).getIntConstraint(Constraint.LENGTH) == 12);
		assertTrue(m.getFieldType(1).getConstraints().length == 1);
		assertTrue(m.getFieldName(2).equals("unlimitedstring"));
		assertTrue(m.getFieldType(2).getConstraints().length == 0);
		ds.close();
	}

	public void testReadNumeric() throws Exception {
		String tableName = "testpostgresqlmetadatanumeric";
		testReadNumeric(getPostgreSQLSource(tableName), "CREATE TABLE "
				+ tableName
				+ " (id integer primary key, limitednumeric1 numeric(12),"
				+ " limitednumeric2 numeric(12, 3), "
				+ "unlimitedinteger int4) ; ");
		sm.removeAll();
		tableName = "testh2metadatanumeric";
		testReadNumeric(
				getH2Source(tableName),
				"CREATE TABLE \""
						+ tableName
						+ "\" (id integer primary key, \"limitednumeric1\" numeric(12),"
						+ " \"limitednumeric2\" numeric(12, 3), "
						+ "\"unlimitedinteger\" int4) ; ");
		sm.removeAll();
		tableName = "testhsqldbmetadatanumeric";
		testReadNumeric(
				getHSQLDBSource(tableName),
				"CREATE TABLE \""
						+ tableName
						+ "\" (id integer primary key, \"limitednumeric1\" numeric(12),"
						+ " \"limitednumeric2\" numeric(12, 3), "
						+ "\"unlimitedinteger\" int) ; ");
	}

	private void testReadNumeric(DBSource source, String createSQL)
			throws Exception {
		deleteTable(source);
		executeScript(source, createSQL);
		sm.register("source", source);

		DataSource ds = dsf.getDataSource("source");
		ds.open();
		Metadata m = ds.getMetadata();
		assertTrue(m.getFieldName(1).equals("limitednumeric1"));
		assertTrue(m.getFieldType(1).getIntConstraint(Constraint.PRECISION) == 12);
		assertTrue(m.getFieldType(1).getConstraints().length == 1);
		assertTrue(m.getFieldName(2).equals("limitednumeric2"));
		assertTrue(m.getFieldType(2).getIntConstraint(Constraint.PRECISION) == 12);
		assertTrue(m.getFieldType(2).getIntConstraint(Constraint.SCALE) == 3);
		assertTrue(m.getFieldType(2).getConstraints().length == 2);
		assertTrue(m.getFieldName(3).equals("unlimitedinteger"));
		assertTrue(m.getFieldType(3).getConstraints().length == 0);
		ds.close();
	}

	public void testWriteString() throws Exception {
		String tableName = "test_metadata_write_string";
		testWriteString(getPostgreSQLSource(tableName), 4, 4);
		tableName = "test_metadata_write_string";
		testWriteString(getH2Source(tableName), 4, -1);
		tableName = "test_metadata_write_string";
		testWriteString(getHSQLDBSource(tableName), 4, 4);
	}

	private void testWriteString(DBSource source, int lengthConstraint,
			int storedConstraint) throws Exception {
		// Create a metadata with String and a length constraint
		DefaultMetadata metadata = new DefaultMetadata();
		metadata.addField("id", Type.INT,
				new Constraint[] { new PrimaryKeyConstraint() });
		metadata.addField("myLimitedString", Type.STRING,
				new Constraint[] { new LengthConstraint(lengthConstraint) });
		metadata.addField("myUnlimitedString", Type.STRING);
		// Create the db source
		deleteTable(source);
		dsf.createDataSource(new DBSourceCreation(source, metadata));
		// read it
		DataSource ds = dsf.getDataSource(source);
		ds.open();
		Metadata m = ds.getMetadata();
		assertTrue(m.getFieldName(1).equals("myLimitedString"));
		assertTrue(m.getFieldType(1).getIntConstraint(Constraint.LENGTH) == storedConstraint);
		if (storedConstraint == -1) {
			assertTrue(m.getFieldType(1).getConstraints().length == 0);
		} else {
			assertTrue(m.getFieldType(1).getConstraints().length == 1);

		}
		assertTrue(m.getFieldName(2).equals("myUnlimitedString"));
		assertTrue(m.getFieldType(2).getConstraints().length == 0);
		ds.close();
	}

	public void testWriteNumeric() throws Exception {
		String tableName = "test_metadata_write_string";
		DBSource pgSource = getPostgreSQLSource(tableName);
		DBSource h2Source = getH2Source(tableName);
		DBSource hsqldbSource = getHSQLDBSource(tableName);

		testTypeIO(TypeFactory.createType(Type.BYTE), TypeFactory
				.createType(Type.SHORT), pgSource);
		testTypeIO(TypeFactory.createType(Type.BYTE), TypeFactory
				.createType(Type.BYTE), h2Source);
		testTypeIO(TypeFactory.createType(Type.BYTE), TypeFactory
				.createType(Type.BYTE), hsqldbSource);

		testTypeIO(TypeFactory.createType(Type.BYTE,
				new Constraint[] { new PrecisionConstraint(4) }), TypeFactory
				.createType(Type.SHORT), pgSource);
		testTypeIO(TypeFactory.createType(Type.BYTE,
				new Constraint[] { new PrecisionConstraint(4) }), TypeFactory
				.createType(Type.SHORT), h2Source);
		testTypeIO(TypeFactory.createType(Type.BYTE,
				new Constraint[] { new PrecisionConstraint(4) }), TypeFactory
				.createType(Type.SHORT), hsqldbSource);

		testTypeIO(TypeFactory.createType(Type.SHORT), TypeFactory
				.createType(Type.SHORT), pgSource);
		testTypeIO(TypeFactory.createType(Type.SHORT), TypeFactory
				.createType(Type.SHORT), h2Source);
		testTypeIO(TypeFactory.createType(Type.SHORT), TypeFactory
				.createType(Type.SHORT), hsqldbSource);

		testTypeIO(TypeFactory.createType(Type.INT), TypeFactory
				.createType(Type.INT), pgSource);
		testTypeIO(TypeFactory.createType(Type.INT), TypeFactory
				.createType(Type.INT), h2Source);
		testTypeIO(TypeFactory.createType(Type.INT), TypeFactory
				.createType(Type.INT), hsqldbSource);

		testTypeIO(TypeFactory.createType(Type.LONG), TypeFactory
				.createType(Type.LONG), pgSource);
		testTypeIO(TypeFactory.createType(Type.LONG), TypeFactory
				.createType(Type.LONG), h2Source);
		testTypeIO(TypeFactory.createType(Type.LONG), TypeFactory
				.createType(Type.LONG), pgSource);

		testTypeIO(TypeFactory.createType(Type.SHORT,
				new Constraint[] { new PrecisionConstraint(5) }), TypeFactory
				.createType(Type.INT), pgSource);
		testTypeIO(TypeFactory.createType(Type.SHORT,
				new Constraint[] { new PrecisionConstraint(5) }), TypeFactory
				.createType(Type.INT), h2Source);
		testTypeIO(TypeFactory.createType(Type.SHORT,
				new Constraint[] { new PrecisionConstraint(5) }), TypeFactory
				.createType(Type.INT), hsqldbSource);

		testTypeIO(TypeFactory.createType(Type.INT,
				new Constraint[] { new PrecisionConstraint(14) }), TypeFactory
				.createType(Type.LONG), pgSource);
		testTypeIO(TypeFactory.createType(Type.INT,
				new Constraint[] { new PrecisionConstraint(14) }), TypeFactory
				.createType(Type.LONG), h2Source);
		testTypeIO(TypeFactory.createType(Type.INT,
				new Constraint[] { new PrecisionConstraint(14) }), TypeFactory
				.createType(Type.LONG), hsqldbSource);

		testTypeIO(TypeFactory.createType(Type.INT,
				new Constraint[] { new PrecisionConstraint(34) }), TypeFactory
				.createType(Type.DOUBLE), pgSource);
		testTypeIO(TypeFactory.createType(Type.INT,
				new Constraint[] { new PrecisionConstraint(34) }), TypeFactory
				.createType(Type.DOUBLE), h2Source);
		testTypeIO(TypeFactory.createType(Type.INT,
				new Constraint[] { new PrecisionConstraint(34) }), TypeFactory
				.createType(Type.DOUBLE), hsqldbSource);

		testTypeIO(TypeFactory.createType(Type.INT, new Constraint[] {
				new PrecisionConstraint(6), new LengthConstraint(8) }),
				TypeFactory.createType(Type.INT), pgSource);
		testTypeIO(TypeFactory.createType(Type.INT, new Constraint[] {
				new PrecisionConstraint(6), new LengthConstraint(8) }),
				TypeFactory.createType(Type.INT), h2Source);
		testTypeIO(TypeFactory.createType(Type.INT, new Constraint[] {
				new PrecisionConstraint(6), new LengthConstraint(8) }),
				TypeFactory.createType(Type.INT), hsqldbSource);
	}

	private void testTypeIO(Type inType, Type outType, DBSource source)
			throws Exception {
		// Create a metadata with String and a length constraint
		DefaultMetadata metadata = new DefaultMetadata();
		metadata.addField("id", Type.INT,
				new Constraint[] { new PrimaryKeyConstraint() });
		metadata.addField("field", inType);
		// Create the db source
		deleteTable(source);
		dsf.createDataSource(new DBSourceCreation(source, metadata));
		// read it
		DataSource ds = dsf.getDataSource(source);
		ds.open();
		Metadata m = ds.getMetadata();
		Type readType = m.getFieldType(1);
		assertTrue(readType.getTypeCode() == outType.getTypeCode());
		Constraint[] readConstraints = readType.getConstraints();
		Constraint[] outConstraints = outType.getConstraints();
		assertTrue(readConstraints.length == outConstraints.length);
		for (int i = 0; i < outConstraints.length; i++) {
			int constr = readConstraints[i].getConstraintCode();
			assertTrue(readType.getConstraintValue(constr).equals(
					outType.getConstraintValue(constr)));
		}
		ds.close();
	}
}
