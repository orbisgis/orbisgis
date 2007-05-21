package org.gdms.data;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.gdms.data.edition.Field;
import org.gdms.data.metadata.DefaultDriverMetadata;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.DriverMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.spatial.FID;
import org.gdms.spatial.PTTypes;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class AllTypesObjectDriver implements ObjectDriver {

	private Value[][] values = new Value[2][12];

	private String[] names = new String[] { "binary", "boolean", "byte",
			"date", "double", "float", "int", "long", "short", "string",
			"timestamp", "time" };

	private int[] types = new int[] { Value.BINARY, Value.BOOLEAN, Value.BYTE,
			Value.DATE, Value.DOUBLE, Value.FLOAT, Value.INT, Value.LONG,
			Value.SHORT, Value.STRING, Value.TIMESTAMP, Value.TIME };

	public AllTypesObjectDriver() throws ParseException {

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		values[0][0] = ValueFactory.createValue(new byte[] { 1, 2 });
		values[0][1] = ValueFactory.createValue(true);
		values[0][2] = ValueFactory.createValue((byte) 4);
		values[0][3] = ValueFactory.createValue(df.parse("1980-9-5"));
		values[0][4] = ValueFactory.createValue(3d);
		values[0][5] = ValueFactory.createValue(3f);
		values[0][6] = ValueFactory.createValue(3);
		values[0][7] = ValueFactory.createValue(3L);
		values[0][8] = ValueFactory.createValue((short) 3);
		values[0][9] = ValueFactory.createValue("3");
		values[0][10] = ValueFactory.createValue(Timestamp
				.valueOf("1980-9-5 10:30:00.666666666"));
		values[0][11] = ValueFactory.createValue(Time.valueOf("10:30:00"));

		values[1][0] = ValueFactory.createValue(new byte[] { 0, 2 });
		values[1][1] = ValueFactory.createValue(false);
		values[1][2] = ValueFactory.createValue((byte) 5);
		values[1][3] = ValueFactory.createValue(df.parse("1986-9-5"));
		values[1][4] = ValueFactory.createValue(4d);
		values[1][5] = ValueFactory.createValue(4f);
		values[1][6] = ValueFactory.createValue(4);
		values[1][7] = ValueFactory.createValue(4L);
		values[1][8] = ValueFactory.createValue((short) 4);
		values[1][9] = ValueFactory.createValue("4");
		values[1][10] = ValueFactory.createValue(Timestamp
				.valueOf("1984-9-5 10:30:00.666666666"));
		values[1][11] = ValueFactory.createValue(Time.valueOf("10:31:40"));

	}

	/**
	 * @see org.gdms.driver.ObjectDriver#write(org.gdms.data.edition.DataWare)
	 */
	public void write(DataSource dataWare) throws DriverException {
		names = new String[dataWare.getDataSourceMetadata().getFieldCount()];
		types = new int[names.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = dataWare.getDataSourceMetadata().getFieldName(i);
			types[i] = dataWare.getDataSourceMetadata().getFieldType(i);
		}
		Value[][] newValues = new Value[(int) dataWare.getRowCount()][dataWare
				.getDataSourceMetadata().getFieldCount()];
		for (int i = 0; i < dataWare.getRowCount(); i++) {
			for (int j = 0; j < dataWare.getDataSourceMetadata()
					.getFieldCount(); j++) {
				newValues[i][j] = dataWare.getFieldValue(i, j);
			}
		}

		values = newValues;
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getFieldValue(long, int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return values[(int) rowIndex][fieldId];
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getFieldCount()
	 */
	public int getFieldCount() throws DriverException {
		return names.length;
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getFieldName(int)
	 */
	public String getFieldName(int fieldId) throws DriverException {
		return names[fieldId];
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getRowCount()
	 */
	public long getRowCount() throws DriverException {
		return values.length;
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getFieldType(int)
	 */
	public int getFieldType(int i) throws DriverException {
		return types[i];
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	public String getName() {
		return null;
	}

	public Metadata getMetadata() throws DriverException {
		return new DefaultMetadata(types, names, null, null);
	}

	/**
	 * @see org.gdms.driver.ReadOnlyDriver#getDriverMetadata()
	 */
	public DriverMetadata getDriverMetadata() throws DriverException {
		DefaultDriverMetadata ret = new DefaultDriverMetadata();
		for (int i = 0; i < getFieldCount(); i++) {
			int type = getFieldType(i);
			ret.addField(getFieldName(i), PTTypes.typesDescription.get(type));
		}

		return ret;
	}

	public int getType(String driverType) {
		if ("STRING".equals(driverType)) {
			return Value.STRING;
		} else if ("LONG".equals(driverType)) {
			return Value.LONG;
		} else if ("BOOLEAN".equals(driverType)) {
			return Value.BOOLEAN;
		} else if ("DATE".equals(driverType)) {
			return Value.DATE;
		} else if ("DOUBLE".equals(driverType)) {
			return Value.DOUBLE;
		} else if ("INT".equals(driverType)) {
			return Value.INT;
		} else if ("FLOAT".equals(driverType)) {
			return Value.FLOAT;
		} else if ("SHORT".equals(driverType)) {
			return Value.SHORT;
		} else if ("BYTE".equals(driverType)) {
			return Value.BYTE;
		} else if ("BINARY".equals(driverType)) {
			return Value.BINARY;
		} else if ("TIMESTAMP".equals(driverType)) {
			return Value.TIMESTAMP;
		} else if ("TIME".equals(driverType)) {
			return Value.TIME;
		}

		throw new RuntimeException();
	}

	public String check(Field field, Value value) throws DriverException {
		return null;
	}

	public boolean isReadOnly(int i) {
		return false;
	}

	public String[] getPrimaryKeys() {
		return new String[0];
	}

	public String[] getAvailableTypes() throws DriverException {
		return null;
	}

	public String[] getParameters(String driverType) throws DriverException {
		return null;
	}

	public boolean isValidParameter(String driverType, String paramName,
			String paramValue) {
		return false;
	}

	public void start() throws DriverException {

	}

	public void stop() throws DriverException {

	}

	public Number[] getScope(int dimension, String fieldName)
			throws DriverException {
		return null;
	}

	public FID getFid(long row) {
		return null;
	}

	public boolean hasFid() {
		return false;
	}

	public CoordinateReferenceSystem getCRS(String fieldName)
			throws DriverException {
		// TODO Auto-generated method stub
		return null;
	}
}