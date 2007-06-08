package org.gdms;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SeveralSpatialFieldsDriver;

import com.vividsolutions.jts.geom.Geometry;

public class SourceTest extends BaseTest {

	private static final int SMALL_THRESHOLD = 100;

	public static String externalData = new String("../../datas2tests/");

	public static String internalData = new String("src/test/resources/");

	protected DataSourceFactory dsf = new DataSourceFactory();

	private List<TestData> testData = new ArrayList<TestData>();

	private Map<String, String> backupOriginal = new HashMap<String, String>();

	private boolean writingTests = true;

	public SourceTest() {
		TestData td;

		td = new FileTestData("cantons_dbf", false, TestData.DBF, 3705, false,
				"PTOT99", false, new FileSourceDefinition(new File(externalData
						+ "shp/bigshape2D/cantons.dbf")));
		td.setStringField("CODECANT");
		td.setNumericInfo("PTOT99", 0, 807071);
		testData.add(td);

		td = new ObjectTestData("memory_spatial_object", true, TestData.NONE,
				3, "alpha", false, new ObjectSourceDefinition(
						new SeveralSpatialFieldsDriver()));
		td.setNewGeometry(new Geometry[] { Geometries.getPoint() });
		testData.add(td);

		td = new FileTestData("hedgerow_shp", false, TestData.SHAPEFILE, 994,
				false, "TYPE", false, new FileSourceDefinition(new File(
						externalData + "shp/mediumshape2D/hedgerow.shp")));
		td.setStringField("TYPE");
		td.setNumericInfo("gid", 0, 993);
		td.setNewGeometry(new Geometry[] { Geometries.getLinestring() });
		testData.add(td);

		td = new FileTestData("cantons_shp", false, TestData.SHAPEFILE, 3705,
				false, "PTOT99", false, new FileSourceDefinition(new File(
						externalData + "shp/bigshape2D/cantons.shp")));
		td.setStringField("CODECANT");
		td.setNumericInfo("PTOT99", 0, 807071);
		td.setNewGeometry(new Geometry[] { Geometries.getPolygon() });
		testData.add(td);

		DBSource dbSource = new DBSource(null, 0, internalData + "testdb",
				null, null, "gisapps", "jdbc:hsqldb:file");
		td = new HSQLDBTestData("testdb", 6, true, "gis", false,
				new DBTableSourceDefinition(dbSource));
		td.setStringField("version");
		td.setNullField("version");
		td.setNumericInfo("points", 5, 10);
		td.setPKInfo("id", ValueFactory.createValue(6));
		testData.add(td);

		td = new FileTestData("test", true, TestData.CSV, 5, false, "id",
				false, new FileSourceDefinition(new File(internalData
						+ "test.csv")));
		testData.add(td);
		td = new FileTestData("repeatedRows", true, TestData.CSV, 5, false,
				"id", true, new FileSourceDefinition(new File(internalData
						+ "repeatedRows.csv")));
		testData.add(td);

		td = new SQLTestData("select", false, TestData.NONE, 5, false, "id",
				false);
		testData.add(td);
		/*
		 * td = new FileTestData("hedgerow", 5, false, "type", false, new
		 * SpatialFileSourceDefinition(new File(externalData +
		 * "shp/mediumshape2D/hedgerow.shp"))); td.setNewGeometry(new Geometry[] {
		 * Geometries.getMultilineString() }); td.setStringField("type");
		 * testData.add(td);
		 */

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

	private String[] getDataSet(Condition c) throws Exception {
		ArrayList<String> ret = new ArrayList<String>();
		for (TestData td : testData) {
			if (c.evaluateCondition(td)) {
				if (writingTests) {
					if (td.isWrite()) {
						String backupName = prepare(td);
						backupOriginal.put(backupName, td.getName());
						ret.add(backupName);
					}
				} else {
					String backupName = prepare(td);
					backupOriginal.put(backupName, td.getName());
					ret.add(backupName);
				}
			}
		}

		return ret.toArray(new String[0]);
	}

	/**
	 * If the test is going to write creates a backup and adds the backup to the
	 * DataSourceFactory
	 * 
	 * @param td
	 * 
	 * @return The name of the backup in the DataSourceFactory
	 * @throws IOException
	 */
	private String prepare(TestData td) throws Exception {
		File backupDir = new File(internalData + "backup");
		backupDir.mkdirs();
		return td.backup(backupDir, dsf);
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
		name = backupOriginal.get(name);
		for (TestData td : testData) {
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
				return td.getNewGeometry() == null;
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
				return td.getNewGeometry() != null;
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
		d.cancel();
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

	public String[] getResourcesOfFormat(final int driverTypes)
			throws Exception {
		return getDataSet(new Condition() {
			public boolean evaluateCondition(TestData td) {
				return (td.getDriver() & driverTypes) == td.getDriver();
			}
		});
	}

	/**
	 * creates the hsqldb table to test.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Class.forName("org.hsqldb.jdbcDriver");

		Connection c = java.sql.DriverManager
				.getConnection("jdbc:hsqldb:file:src/test/resources/testdb");
		Statement st = c.createStatement();

		st
				.execute("CREATE CACHED TABLE \"gisapps\" (\"id\" INTEGER IDENTITY PRIMARY KEY, \"gis\" VARCHAR(10), \"points\" INTEGER, \"version\" VARCHAR(10))");
		st.execute("INSERT INTO \"gisapps\" VALUES(0, 'orbisgis', 10, null)");
		st.execute("INSERT INTO \"gisapps\" VALUES(1, 'gvsig', 9, '1.1')");
		st.execute("INSERT INTO \"gisapps\" VALUES(2, 'kosmo', 8, '1.1')");
		st.execute("INSERT INTO \"gisapps\" VALUES(3, 'openjump', 7, 'a lot')");
		st
				.execute("INSERT INTO \"gisapps\" VALUES(4, 'qgis', 6, 'I don not know')");
		st.execute("INSERT INTO \"gisapps\" VALUES(5, 'orbiscad', 5, '1.0')");
		st.execute("SHUTDOWN");
		st.close();
		c.close();
	}

}
