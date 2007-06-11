package org.gdms.data;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.gdms.data.edition.Field;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.DefaultTypeDefinition;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.spatial.FID;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class AllTypesObjectDriver implements ObjectDriver {

	private Value[][] values = new Value[2][12];

	private static String[] names = new String[] { "binary", "boolean", "byte",
			"date", "double", "float", "int", "long", "short", "string",
			"timestamp", "time" };

	private static int[] typesCodes = new int[] { Type.BINARY, Type.BOOLEAN,
			Type.BYTE, Type.DATE, Type.DOUBLE, Type.FLOAT, Type.INT,
			Type.LONG, Type.SHORT, Type.STRING, Type.TIMESTAMP, Type.TIME };

	private static Type[] types;

	private static Metadata metadata;

	static {
		final int fc = names.length;
		final Type[] fieldsTypes = new Type[fc];
		final String[] fieldsNames = new String[fc];
		TypeDefinition csvTypeDef;

		try {
			for (int i = 0; i < fc; i++) {
				csvTypeDef = new DefaultTypeDefinition(names[i], typesCodes[i],
						null);
				fieldsNames[i] = names[i];
				fieldsTypes[i] = csvTypeDef.createType(null);
			}

			metadata = new DefaultMetadata(fieldsTypes, fieldsNames);
		} catch (InvalidTypeException e) {
			throw new RuntimeException("Bug in the static part", e);
		}
	}

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
				.valueOf("1980-09-05 10:30:00.666666666"));
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
				.valueOf("1984-09-05 10:30:00.666666666"));
		values[1][11] = ValueFactory.createValue(Time.valueOf("10:31:40"));

	}

	/**
	 * @see org.gdms.driver.ObjectDriver#write(org.gdms.data.edition.DataWare)
	 */
	public void write(DataSource dataSource) throws DriverException {
		final int fc = dataSource.getMetadata().getFieldCount();
		names = new String[fc];
		types = new Type[fc];

		for (int i = 0; i < fc; i++) {
			names[i] = dataSource.getMetadata().getFieldName(i);
			types[i] = dataSource.getMetadata().getFieldType(i);
		}
		Value[][] newValues = new Value[(int) dataSource.getRowCount()][dataSource
				.getMetadata().getFieldCount()];
		for (int i = 0; i < dataSource.getRowCount(); i++) {
			for (int j = 0; j < dataSource.getMetadata()
					.getFieldCount(); j++) {
				newValues[i][j] = dataSource.getFieldValue(i, j);
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
	public Type getFieldType(int i) throws DriverException {
		return types[i];
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	public String getName() {
		return null;
	}

	/**
	 * @see org.gdms.driver.ReadOnlyDriver#getMetadata()
	 */
	public Metadata getMetadata() throws DriverException {
		return metadata;
	}

	public int getType(String driverType) {
		if ("STRING".equals(driverType)) {
			return Type.STRING;
		} else if ("LONG".equals(driverType)) {
			return Type.LONG;
		} else if ("BOOLEAN".equals(driverType)) {
			return Type.BOOLEAN;
		} else if ("DATE".equals(driverType)) {
			return Type.DATE;
		} else if ("DOUBLE".equals(driverType)) {
			return Type.DOUBLE;
		} else if ("INT".equals(driverType)) {
			return Type.INT;
		} else if ("FLOAT".equals(driverType)) {
			return Type.FLOAT;
		} else if ("SHORT".equals(driverType)) {
			return Type.SHORT;
		} else if ("BYTE".equals(driverType)) {
			return Type.BYTE;
		} else if ("BINARY".equals(driverType)) {
			return Type.BINARY;
		} else if ("TIMESTAMP".equals(driverType)) {
			return Type.TIMESTAMP;
		} else if ("TIME".equals(driverType)) {
			return Type.TIME;
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

	public TypeDefinition[] getTypesDefinitions() throws DriverException {
		final TypeDefinition[] result = new TypeDefinition[typesCodes.length];
		for (int i = 0; i < typesCodes.length; i++) {
			try {
				result[i] = new DefaultTypeDefinition(names[i], typesCodes[i]);
			} catch (InvalidTypeException e) {
				throw new DriverException("Invalid type");
			}
		}
		return result;
	}
}