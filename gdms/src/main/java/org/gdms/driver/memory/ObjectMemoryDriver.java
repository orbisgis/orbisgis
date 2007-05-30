package org.gdms.driver.memory;

import java.io.File;
import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.edition.Field;
import org.gdms.data.metadata.DefaultDriverMetadata;
import org.gdms.data.metadata.DriverMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectReadWriteDriver;
import org.gdms.spatial.FID;
import org.gdms.spatial.PTTypes;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class ObjectMemoryDriver implements ObjectReadWriteDriver {

	private static final String GEOMETRY = "GEOMETRY";

	public static String[] typeNames = new String[] { "BINARY", "BOOLEAN",
			"BYTE", "DATE", "DOUBLE", "FLOAT", "INT", "LONG", "SHORT",
			"STRING", "TIMESTAMP", "TIME", };

	private ArrayList<ArrayList<Value>> contents = new ArrayList<ArrayList<Value>>();

	private String[] columnsNames;

	private int[] columnsTypes;

	/**
	 * Create a new empty source of data in memory. The source will have as many
	 * columns as specified in the 'columnsNames' parameter. The values in this
	 * array are the names of the columns and the values in the 'columnsTypes'
	 * array are constants in the org.gdms.data.values.Value interface and
	 * specify the type of each column.
	 *
	 * @param types
	 */
	public ObjectMemoryDriver(String[] columnsNames, int[] columnsTypes) {
		this.columnsNames = columnsNames;
		this.columnsTypes = columnsTypes;
	}

	public ObjectMemoryDriver(DataSource dataSource) throws DriverException {
		dataSource.open();
		this.write(dataSource);
		Metadata m = dataSource.getDataSourceMetadata();
		this.columnsNames = new String[m.getFieldCount()];
		this.columnsTypes = new int[m.getFieldCount()];
		for (int i = 0; i < columnsNames.length; i++) {
			columnsNames[i] = m.getFieldName(i);
			columnsTypes[i] = m.getFieldType(i);
		}
		dataSource.cancel();
	}

	public void write(DataSource dataSource) throws DriverException {
		ArrayList<ArrayList<Value>> newContents = new ArrayList<ArrayList<Value>>();
		for (int i = 0; i < dataSource.getRowCount(); i++) {
			Value[] row = dataSource.getRow(i);
			ArrayList<Value> rowArray = new ArrayList<Value>();
			for (int j = 0; j < row.length; j++) {
				rowArray.add(row[j]);
			}
			newContents.add(rowArray);
		}

		contents = newContents;
	}

	public String[] getPrimaryKeys() {
		return new String[0];
	}

	public boolean isReadOnly(int i) {
		return false;
	}

	public void start() throws DriverException {

	}

	public void stop() throws DriverException {

	}

	public String check(Field field, Value value) throws DriverException {
		if (field.getType() == value.getType()) {
			return null;
		} else {
			return "Type mistmatch: ";
		}
	}

	public String[] getAvailableTypes() throws DriverException {
		return typeNames;
	}

	public CoordinateReferenceSystem getCRS(String fieldName)
			throws DriverException {
		return null;
	}

	public DriverMetadata getDriverMetadata() throws DriverException {
		DefaultDriverMetadata ret = new DefaultDriverMetadata();
		for (int i = 0; i < columnsNames.length; i++) {
			String typeName;
			if (columnsTypes[i] == PTTypes.GEOMETRY) {
				typeName = GEOMETRY;
			} else {
				typeName = typeNames[columnsTypes[i]];
			}
			ret.addField(columnsNames[i], typeName);
		}
		return ret;
	}

	public FID getFid(long row) {
		return null;
	}

	public String[] getParameters(String driverType) throws DriverException {
		return new String[0];
	}

	public int getType(String driverType) {
		for (int i = 0; i < typeNames.length; i++) {
			if (typeNames[i].equals(driverType)) {
				return i;
			}
		}

		if (driverType.equals(GEOMETRY)) {
			return PTTypes.GEOMETRY;
		}

		throw new RuntimeException();
	}

	public boolean hasFid() {
		return false;
	}

	public boolean isValidParameter(String driverType, String paramName,
			String paramValue) {
		return false;
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	public String getName() {
		return "Object memory driver";
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return contents.get((int) rowIndex).get(fieldId);
	}

	public long getRowCount() throws DriverException {
		return contents.size();
	}

	public Number[] getScope(int dimension, String fieldName)
			throws DriverException {
		return null;
	}

	public boolean isCommitable() {
		return true;
	}

	public static void main(String[] args) throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();

		ObjectMemoryDriver omd = new ObjectMemoryDriver(new String[] { "a" },
				new int[] { Value.INT });
		dsf.registerDataSource("ds1", new ObjectSourceDefinition(omd));
		DataSource ds = dsf.getDataSource("ds1");
		ds.open();
		ds.insertEmptyRow();
		ds.insertEmptyRow();
		ds.setFieldValue(0, 0, ValueFactory.createValue(0));
		ds.setFieldValue(1, 0, ValueFactory.createValue(1));
		System.out.println(ds.getAsString());
		ds.commit();

		ds = dsf.executeSQL("select * from ds1, ds1");
		ds.open();
		System.out.println(ds.getAsString());
		ds.cancel();
	}

}
