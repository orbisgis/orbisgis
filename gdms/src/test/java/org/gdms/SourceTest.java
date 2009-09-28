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
package org.gdms;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreation;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.db.DBSource;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.DimensionConstraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.csvstring.CSVStringDriver;
import org.gdms.driver.dbf.DBFDriver;
import org.gdms.driver.driverManager.Driver;
import org.gdms.driver.h2.H2spatialDriver;
import org.gdms.driver.hsqldb.HSQLDBDriver;
import org.gdms.driver.postgresql.PostgreSQLDriver;
import org.gdms.driver.shapefile.ShapefileDriver;
import org.gdms.driver.solene.CirDriver;
import org.gdms.source.SourceManager;
import org.gdms.spatial.SeveralSpatialFieldsDriver;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

public class SourceTest extends BaseTest {

	private static final int SMALL_THRESHOLD = 5000;

	public static DataSourceFactory dsf = new DataSourceFactory();

	private static List<TestData> testMetaData = new ArrayList<TestData>();

	private boolean writingTests = true;

	private static File testDataInfo = new File(internalData,
			"test_data_info.csv");

	public static File backupDir = new File(internalData + "backup");;
	private static String fname = "name";
	private static String frowCount = "rowCount";
	private static String fisDB = "isDB";
	private static String fnoPKField = "noPKField";
	private static String fhasRepeatedRows = "hasRepeatedRows";
	private static String fpkField = "pkField";
	private static String fpkType = "pkType";
	private static String fnewPK = "newPK";
	private static String fstringField = "stringField";
	private static String fnullField = "nullField";
	private static String fnumericFieldName = "numericFieldName";
	private static String fmin = "min";
	private static String fmax = "max";
	private static String fspatialField = "spatialField";
	private static String fnewGeometry = "newGeometry";
	private static String fwrite = "write";

	private static List<TestSource> toTest = new ArrayList<TestSource>();

	static {
		try {
			dsf.setTempDir(internalData + "backup");

			toTest.add(new FileTestSource("hedgerow", internalData
					+ "hedgerow.shp"));
			toTest.add(new FileTestSource("landcover2000dbf", internalData
					+ "landcover2000.dbf"));
			toTest.add(new DBTestSource("pghedgerow", "org.postgresql.Driver",
					internalData + "hedgerow.sql", new DBSource("127.0.0.1",
							-1, "gdms", "postgres", "postgres", "hedgerow",
							"jdbc:postgresql")));
			toTest.add(new FileTestSource("landcover2000shp", internalData
					+ "landcover2000.shp"));
			toTest.add(new ObjectTestSource("memory_spatial_object",
					new SeveralSpatialFieldsDriver()));
			toTest.add(new DBTestSource("testh2", "org.h2.Driver", internalData
					+ "testh2.sql", new DBSource(null, 0, internalData
					+ "backup/testh2", "sa", "", "POINT", "jdbc:h2")));
			toTest.add(new DBTestSource("testhsqldb", "org.hsqldb.jdbcDriver",
					internalData + "testhsqldb.sql", new DBSource(null, 0,
							internalData + "backup/testhsqldb", "sa", "",
							"gisapps", "jdbc:hsqldb:file")));
			toTest
					.add(new FileTestSource("testcsv", internalData
							+ "test.csv"));
			toTest.add(new FileTestSource("repeatedRows", internalData
					+ "repeatedRows.csv"));
			toTest.add(new SQLTestSource("select_source", internalData
					+ "repeatedRows.csv"));
			toTest.add(new DBTestSource("postgres", "org.postgresql.Driver",
					internalData + "testpostgres.sql", new DBSource(
							"127.0.0.1", -1, "gdms", "postgres", "postgres",
							"gisapps", "jdbc:postgresql")));

			if (!testDataInfo.exists()) {
				createDB();
			}

			testMetaData = readDataInfo();

		} catch (Exception e) {
			testDataInfo.delete();
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	private static void createDB() throws Exception {
		ArrayList<TestSourceData> sources = new ArrayList<TestSourceData>();
		sources.add(new TestSourceData("hedgerow", null, false));
		sources.add(new TestSourceData("cantonsdbf", null, false));
		sources.add(new TestSourceData("pghedgerow", null, false));
		sources.add(new TestSourceData("memory_spatial_object", null, false));
		sources.add(new TestSourceData("cantonsshp", null, false));
		sources.add(new TestSourceData("testh2", null, false));
		sources.add(new TestSourceData("testhsqldb", "version", false));
		sources.add(new TestSourceData("testcsv", null, false));
		sources.add(new TestSourceData("repeatedRows", null, true));
		sources.add(new TestSourceData("select_source", null, false));
		sources.add(new TestSourceData("postgres", null, false));
		createTestDataInfo(sources);
	}

	private static List<TestData> readDataInfo() throws Exception {
		ArrayList<TestData> testData = new ArrayList<TestData>();
		DataSource info = dsf.getDataSource(testDataInfo);
		info.open();
		for (int i = 0; i < info.getRowCount(); i++) {
			TestData td = new TestData(info.getString(i, fname), Boolean
					.parseBoolean(info.getString(i, fwrite)), 0, Long
					.parseLong(info.getString(i, frowCount)), Boolean
					.parseBoolean(info.getString(i, fisDB)), info.getString(i,
					fnoPKField), Boolean.parseBoolean(info.getString(i,
					fhasRepeatedRows)));
			String value = info.getString(i, fspatialField);
			if (value != null) {
				WKTReader reader = new WKTReader();
				td.setNewGeometry(value, new Geometry[] { reader.read(info
						.getString(i, fnewGeometry)) });
			}

			td.setNullField(info.getString(i, fnullField));

			value = info.getString(i, fnumericFieldName);
			if (value != null) {
				td.setNumericInfo(value, Integer.parseInt(info.getString(i,
						fmin)), Integer.parseInt(info.getString(i, fmax)));
			}

			value = info.getString(i, fpkField);
			if (value != null) {
				String pk = info.getString(i, fnewPK);
				String type = info.getString(i, fpkType);
				td.setPKInfo(value, ValueFactory.createValueByType(pk, Integer
						.parseInt(type)));
			}

			td.setStringField(info.getString(i, fstringField));

			testData.add(td);
		}

		return testData;
	}

	private static void createTestDataInfo(ArrayList<TestSourceData> sources)
			throws Exception {
		DefaultMetadata m = new DefaultMetadata();
		m.addField(fname, Type.STRING);
		m.addField(frowCount, Type.STRING);
		m.addField(fisDB, Type.STRING);
		m.addField(fnoPKField, Type.STRING);
		m.addField(fhasRepeatedRows, Type.STRING);//
		m.addField(fpkField, Type.STRING);
		m.addField(fpkType, Type.STRING);
		m.addField(fnewPK, Type.STRING);
		m.addField(fstringField, Type.STRING);
		m.addField(fnullField, Type.STRING); //
		m.addField(fnumericFieldName, Type.STRING);
		m.addField(fmin, Type.STRING);
		m.addField(fmax, Type.STRING);
		m.addField(fspatialField, Type.STRING);
		m.addField(fnewGeometry, Type.STRING);
		m.addField(fwrite, Type.STRING);

		DataSourceCreation creation = new FileSourceCreation(testDataInfo, m);
		dsf.createDataSource(creation);
		DataSource ds = dsf.getDataSource(testDataInfo);
		ds.open();
		for (int i = 0; i < sources.size(); i++) {
			try {

				TestSourceData sourceData = sources.get(i);
				getTestSource(sourceData.name).backup();
				DataSource testData = dsf.getDataSource(sourceData.name);
				testData.open();
				ds.insertEmptyRow();
				long row = ds.getRowCount() - 1;
				ds.setString(row, fname, sourceData.name);
				ds.setString(row, frowCount, Long.toString(testData
						.getRowCount()));

				Driver driverName = testData.getDriver();
				if ((driverName instanceof H2spatialDriver)
						|| (driverName instanceof PostgreSQLDriver)
						|| (driverName instanceof HSQLDBDriver)) {
					ds.setString(row, fisDB, "true");
				} else {
					ds.setString(row, fisDB, "false");
				}
				String pkField = null;
				int pkType = -1;
				String newPK = null;
				String noPKField = null;
				String stringField = null;
				String numericField = null;
				String spatialField = null;
				int geometryType = -1;
				int dimension = 2;
				for (int j = 0; j < testData.getFieldCount(); j++) {
					Type fieldType = testData.getFieldType(j);
					Type type = fieldType;
					String fieldName = testData.getFieldName(j);
					// TODO This is due to a bug in the
					// parser. Remove when the bug is solved
					if (fieldName.startsWith("_")) {
						continue;
					}
					if (type.getConstraint(Constraint.PK) != null) {
						if (pkField == null) {
							pkField = fieldName;
							pkType = fieldType.getTypeCode();
							newPK = getPKFor(testData, testData
									.getFieldIndexByName(fieldName));
						}
					} else if (fieldType.getTypeCode() != Type.GEOMETRY) {
						noPKField = fieldName;
					}

					int typeCode = type.getTypeCode();

					switch (typeCode) {
					case Type.STRING:
						stringField = fieldName;
						break;
					case Type.BYTE:
					case Type.INT:
					case Type.LONG:
					case Type.SHORT:
						numericField = fieldName;
						break;
					case Type.GEOMETRY:
						spatialField = fieldName;
						GeometryConstraint c = (GeometryConstraint) fieldType
								.getConstraint(Constraint.GEOMETRY_TYPE);
						if (c != null) {
							geometryType = c.getGeometryType();
						}
						DimensionConstraint dc = (DimensionConstraint) fieldType
								.getConstraint(Constraint.GEOMETRY_DIMENSION);
						if (dc != null) {
							dimension = dc.getDimension();
						}
						break;
					}
				}
				ds.setString(row, fnoPKField, noPKField);
				ds.setString(row, fpkField, pkField);
				ds.setString(row, fpkType, Integer.toString(pkType));
				ds.setString(row, fnewPK, newPK);
				ds.setString(row, fstringField, stringField);
				ds.setString(row, fnumericFieldName, numericField);
				ds.setString(row, fspatialField, spatialField);
				WKTWriter writer = new WKTWriter();
				if (geometryType == -1) {
					ds.setString(row, fnewGeometry, writer.write(Geometries
							.getPoint()));
				} else {
					ds.setString(row, fnewGeometry, writer.write(Geometries
							.getGeometry(geometryType, dimension)));
				}

				if ((driverName instanceof DBFDriver)
						|| (driverName instanceof ShapefileDriver)
						|| (driverName instanceof HSQLDBDriver)
						|| (driverName instanceof H2spatialDriver)
						|| (driverName instanceof PostgreSQLDriver)
						|| (driverName instanceof CSVStringDriver)
						|| (driverName instanceof CirDriver)) {
					ds.setString(row, fwrite, "true");
				} else {
					ds.setString(row, fwrite, "false");
				}

				if (numericField != null) {
					int min = Integer.MAX_VALUE;
					int max = Integer.MIN_VALUE;
					for (int j = 0; j < testData.getRowCount(); j++) {
						int value = testData.getInt(j, numericField);
						if (value < min) {
							min = value;
						}
						if (value > max) {
							max = value;
						}
					}
					ds.setString(row, fmin, Integer.toString(min));
					ds.setString(row, fmax, Integer.toString(max));
				}

				ds.setString(row, fhasRepeatedRows, Boolean
						.toString(sourceData.repeatedRows));
				ds.setString(row, fnullField, sourceData.nullField);
				testData.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ds.commit();
		ds.close();
	}

	private static String getPKFor(DataSource testData, int fieldId)
			throws Exception {
		Value max = null;
		for (int i = 0; i < testData.getRowCount(); i++) {
			Value sampleValue = testData.getFieldValue(i, fieldId);
			if (max == null) {
				max = sampleValue;
			} else if (sampleValue.greater(max).getAsBoolean()) {
				max = sampleValue;
			}
		}

		if (max == null) {
			return "1";
		} else {
			return max.suma(ValueFactory.createValue(1)).toString();
		}
	}

	/**
	 * returns the resources with less than SMALL_THRESOLD number of rows
	 *
	 * @return
	 * @throws IOException
	 */
	public String[] getSmallResources() throws Exception {
		return getDataSet(new Condition() {
			public boolean evaluateCondition(TestData td) {
				return td.getRowCount() < SMALL_THRESHOLD;
			}
		});
	}

	/**
	 * returns the resources with less than SMALL_THRESOLD number of rows
	 *
	 * @return
	 * @throws IOException
	 */
	public String[] getResourcesSmallerThan(final int size) throws Exception {
		return getDataSet(new Condition() {
			public boolean evaluateCondition(TestData td) {
				return td.getRowCount() < size;
			}
		});
	}

	private String[] getDataSet(Condition c) throws Exception {
		ArrayList<String> ret = new ArrayList<String>();
		for (TestData td : testMetaData) {
			if (c.evaluateCondition(td)) {
				TestSource testSource = getTestSource(td.getName());
				if (testSource != null) {
					if (writingTests) {
						if (td.isWrite()) {
							backup(testSource);
							ret.add(td.getName());
						}
					} else {
						backup(testSource);
						ret.add(td.getName());
					}
				}
			}
		}

		return ret.toArray(new String[0]);
	}

	private static TestSource getTestSource(String testDataName) {
		for (TestSource ts : toTest) {
			if (ts.name.equals(testDataName)) {
				return ts;
			}
		}

		return null;
	}

	/**
	 * If the test is going to write creates a backup and adds the backup to the
	 * DataSourceFactory
	 *
	 * @param testSource
	 *
	 * @return The name of the backup in the DataSourceFactory
	 * @throws IOException
	 */
	private void backup(TestSource testSource) throws Exception {
		backupDir.mkdirs();
		SourceManager sourceManager = dsf.getSourceManager();
		if (sourceManager.exists(testSource.name)) {
			sourceManager.remove(testSource.name);
		}
		testSource.backup();
	}

	/**
	 * Get all the resources with primary keys
	 *
	 * @return
	 * @throws IOException
	 */
	public String[] getResourcesWithPK() throws Exception {
		return getDataSet(new Condition() {
			public boolean evaluateCondition(TestData td) {
				return td.getPKInfo() != null;
			}
		});
	}

	private TestData getTestData(String name) {
		for (TestData td : testMetaData) {
			if (td.getName().equals(name)) {
				return td;
			}
		}

		throw new RuntimeException("?");
	}

	/**
	 * Gets a new unique primary key for the specified resource. This method
	 * only should receive as parameters the return values from
	 * getResourcesWithPK
	 *
	 * @param dsName
	 * @return
	 */
	public Value getNewPKFor(String dsName) {
		return getTestData(dsName).getPKInfo().getNewPK();
	}

	/**
	 * Gets the primary key field index for the specified resource. This method
	 * only should receive as parameters the return values from
	 * getResourcesWithPK
	 *
	 * @param dsName
	 * @return
	 */
	public String getPKFieldFor(String dsName) {
		TestData td = getTestData(dsName);
		if (td.getPKInfo() == null) {
			return null;
		} else {
			return td.getPKInfo().getPkField();
		}
	}

	/**
	 * Gets the index of a string field in the specified resource
	 *
	 * @param dsName
	 * @return
	 */
	public String getStringFieldFor(String dsName) {
		return getTestData(dsName).getStringField();
	}

	/**
	 * Gets database resources
	 *
	 * @return
	 * @throws IOException
	 */
	public String[] getDBResources() throws Exception {
		return getDataSet(new Condition() {
			public boolean evaluateCondition(TestData td) {
				return td.isDB();
			}
		});
	}

	/**
	 * returns the index of a field that can be set to null and doesn't have to
	 * have unique values
	 *
	 * @param dsName
	 * @return
	 */
	public String getNoPKFieldFor(String dsName) {
		return getTestData(dsName).getNoPKField();
	}

	/**
	 * Gets any resource without spatial fields
	 *
	 * @return
	 * @throws IOException
	 */
	public String getAnyNonSpatialResource() throws Exception {
		return getDataSet(new Condition() {
			public boolean evaluateCondition(TestData td) {
				return (td.getRowCount() < SMALL_THRESHOLD)
						&& (td.getNewGeometry() == null);
			}
		})[0];
	}

	/**
	 * Gets any resource with spatial fields
	 *
	 * @return
	 * @throws IOException
	 */
	public String getAnySpatialResource() throws Exception {
		return getDataSet(new Condition() {
			public boolean evaluateCondition(TestData td) {
				return (td.getRowCount() < SMALL_THRESHOLD)
						&& (td.getNewGeometry() != null);
			}
		})[0];
	}

	/**
	 * Gets resources with null values
	 *
	 * @return
	 * @throws IOException
	 */
	public String[] getSmallResourcesWithNullValues() throws Exception {
		return getDataSet(new Condition() {
			public boolean evaluateCondition(TestData td) {
				return (td.getNullField() != null)
						&& (td.getRowCount() < SMALL_THRESHOLD);
			}
		});
	}

	/**
	 * Returns any numeric field for the given resource.
	 *
	 * @param resource
	 * @return
	 */
	public String getNumericFieldNameFor(String resource) {
		return getTestData(resource).getNumericInfo().getNumericFieldName();
	}

	/**
	 * Return resources which have at leasst one numeric field
	 *
	 * @return
	 * @throws IOException
	 */
	public String[] getResourcesWithNumericField() throws Exception {
		return getDataSet(new Condition() {
			public boolean evaluateCondition(TestData td) {
				return td.getNumericInfo() != null;
			}
		});
	}

	/**
	 * Returns resources that contain null values
	 *
	 * @return
	 * @throws IOException
	 */
	public String[] getResourcesWithNullValues() throws Exception {
		return getDataSet(new Condition() {
			public boolean evaluateCondition(TestData td) {
				return td.getNullField() != null;
			}
		});
	}

	/**
	 * Returns the name of a field containing null values in the specified data
	 * source
	 *
	 * @param ds
	 * @return
	 */
	public String getContainingNullFieldNameFor(String ds) {
		return getTestData(ds).getNullField();
	}

	/**
	 * Gets the minimum value for the specified field in the specified data
	 * source
	 *
	 * @param ds
	 * @param numericFieldName
	 * @return
	 */
	public double getMinimumValueFor(String ds, String numericFieldName) {
		return getTestData(ds).getNumericInfo().getMin();
	}

	/**
	 * Gets the maximum value for the specified field in the specified data
	 * source
	 *
	 * @param ds
	 * @param numericFieldName
	 * @return
	 */
	public double getMaximumValueFor(String ds, String numericFieldName) {
		return getTestData(ds).getNumericInfo().getMax();
	}

	/**
	 * Gets the resources with repeated rows
	 *
	 * @return
	 * @throws IOException
	 */
	public String[] getResourcesWithRepeatedRows() throws Exception {
		return getDataSet(new Condition() {
			public boolean evaluateCondition(TestData td) {
				return td.hasRepeatedRows();
			}
		});
	}

	/**
	 * Gets new geometries of a type suitable to be added to the specified data
	 * source
	 *
	 * @param dsName
	 * @return
	 */
	public Geometry[] getNewGeometriesFor(String dsName) {
		return getTestData(dsName).getNewGeometry();
	}

	/**
	 * Gets new geometries of a type suitable to be added to the specified data
	 * source
	 *
	 * @param dsName
	 * @return
	 */
	public String getSpatialFieldName(String dsName) {
		return getTestData(dsName).getSpatialField();
	}

	/**
	 * returns all the spatial resources
	 *
	 * @return
	 * @throws IOException
	 */
	public String[] getSpatialResources() throws Exception {
		return getDataSet(new Condition() {
			public boolean evaluateCondition(TestData td) {
				return td.getNewGeometry() != null;
			}
		});
	}

	protected String[] getFieldNames(String ds) throws NoSuchTableException,
			DataSourceCreationException, DriverException {
		DataSource d = dsf.getDataSource(ds);
		d.open();
		String[] fields = d.getFieldNames();
		d.close();
		return fields;
	}

	private interface Condition {
		public boolean evaluateCondition(TestData td);
	}

	/**
	 * Tell the test system that the tests are going to perform modifications in
	 * the data sources
	 *
	 * @param writeTests
	 */
	public void setWritingTests(boolean writingTests) {
		this.writingTests = writingTests;
	}

	@Override
	protected void setUp() throws Exception {
		dsf = new DataSourceFactory();
		dsf.getSourceManager().removeAll();
	}

	@Override
	protected void tearDown() throws Exception {
		dsf.freeResources();
	}

	public String[] getNonSpatialResourcesSmallerThan(final int threshold)
			throws Exception {
		return getDataSet(new Condition() {

			public boolean evaluateCondition(TestData td) {
				return td.getRowCount() < threshold
						&& td.getNewGeometry() == null;
			}

		});
	}
}