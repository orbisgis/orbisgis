package org.gdms.data.edition;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import junit.framework.TestCase;

import org.gdms.Geometries;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.AutoIncrementConstraint;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.DimensionConstraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.LengthConstraint;
import org.gdms.data.types.MaxConstraint;
import org.gdms.data.types.MinConstraint;
import org.gdms.data.types.NotNullConstraint;
import org.gdms.data.types.PatternConstraint;
import org.gdms.data.types.PrecisionConstraint;
import org.gdms.data.types.PrimaryKeyConstraint;
import org.gdms.data.types.ReadOnlyConstraint;
import org.gdms.data.types.ScaleConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.types.UniqueConstraint;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.strategies.IncompatibleTypesException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class ConstraintTest extends TestCase {

	private DataSourceFactory dsf;
	private Type type;
	private Value[] validValues = new Value[0];
	private Value[] invalidValues = new Value[0];
	private Value binaryValue = ValueFactory.createValue(new byte[] { 2, 3, 4,
			5 });
	private Value booleanValue = ValueFactory.createValue(true);
	private Value byteValue = ValueFactory.createValue((byte) 3);
	private Value dateValue = ValueFactory.createValue(new Date());
	private Value doubleValue = ValueFactory.createValue(4.4d);
	private Value floatValue = ValueFactory.createValue(3.3f);
	private Value geomValue = ValueFactory.createValue(Geometries.getPoint());
	private Value intValue = ValueFactory.createValue(3);
	private Value longValue = ValueFactory.createValue(4L);
	private Value shortValue = ValueFactory.createValue((short) 3);
	private Value stringValue = ValueFactory.createValue("string");
	private Value timeValue = ValueFactory.createValue(new Time(2));
	private Value timestampValue = ValueFactory.createValue(new Timestamp(2));
	private ValueCollection collectionValue = ValueFactory
			.createValue(new Value[0]);

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dsf = new DataSourceFactory("target", "target");
	}

	public void testLength() throws Exception {
		setType(TypeFactory.createType(Type.STRING, new LengthConstraint(4)));
		setValidValues(ValueFactory.createValue("1234"), ValueFactory
				.createValue(""), ValueFactory.createNullValue());
		setInvalidValues(ValueFactory.createValue("12345"));
		doEdition();
	}

	public void testMax() throws Exception {
		setType(TypeFactory.createType(Type.INT, new MinConstraint(-10),
				new MaxConstraint(10)));
		setValidValues(ValueFactory.createValue(-10), ValueFactory
				.createValue(10), ValueFactory.createNullValue());
		setInvalidValues(ValueFactory.createValue(-11), ValueFactory
				.createValue(11));
		doEdition();
	}

	public void testNotNull() throws Exception {
		setType(TypeFactory.createType(Type.INT, new NotNullConstraint()));
		setValidValues(ValueFactory.createValue(0));
		setInvalidValues(ValueFactory.createNullValue());
		doEdition();
	}

	public void testAutoIncrement() throws Exception {
		AutoIncrementConstraint constraint = new AutoIncrementConstraint();
		checkOnlyCanSetAndAddNull(constraint);
	}

	public void testReadOnly() throws Exception {
		ReadOnlyConstraint constraint = new ReadOnlyConstraint();
		checkOnlyCanSetAndAddNull(constraint);
	}

	/**
	 * Cannot set a value Cannot insert a new row with values different than
	 * null
	 * 
	 * @param constraint
	 * @throws DriverException
	 */
	private void checkOnlyCanSetAndAddNull(Constraint constraint)
			throws DriverException {
		Value three = ValueFactory.createValue(3);
		Value nullV = ValueFactory.createNullValue();
		setType(TypeFactory.createType(Type.INT, constraint));
		DataSource ds = getDataSource();
		ds.open();
		ds.insertFilledRow(new Value[] { nullV });
		assertTrue(ds.check(0, nullV) == null);
		assertTrue(ds.check(0, three) != null);
		try {
			ds.insertFilledRow(new Value[] { three });
			assertTrue(false);
		} catch (DriverException e) {
		}
		try {
			ds.setFieldValue(0, 0, three);
			assertTrue(false);
		} catch (DriverException e) {
		}
		ds.setFieldValue(0, 0, nullV);
		ds.close();
	}

	public void testGeometryType() throws Exception {
		setType(TypeFactory.createType(Type.GEOMETRY, new GeometryConstraint(
				GeometryConstraint.POINT)));
		setValidValues(ValueFactory.createValue(Geometries.getPoint()),
				ValueFactory.createValue(new GeometryFactory()
						.createGeometryCollection(new Geometry[0])),
				ValueFactory.createNullValue());
		setInvalidValues(ValueFactory.createValue(Geometries.getMultiPoint3D()));
		doEdition();
	}

	public void testGeometryDimension() throws Exception {
		setType(TypeFactory.createType(Type.GEOMETRY,
				new DimensionConstraint(3)));
		setValidValues(ValueFactory.createValue(Geometries.getPoint3D()),
				ValueFactory.createNullValue());
		setInvalidValues(ValueFactory.createValue(Geometries
				.getMultiPolygon2D()));
		doEdition();
	}

	public void testPrecision() throws Exception {
		setType(TypeFactory.createType(Type.DOUBLE, new PrecisionConstraint(3)));
		setValidValues(ValueFactory.createValue(123), ValueFactory
				.createValue(12.3), ValueFactory.createValue(0.13),
				ValueFactory.createNullValue());
		setInvalidValues(ValueFactory.createValue(0.123), ValueFactory
				.createValue(1235));
		doEdition();
	}

	public void testScale() throws Exception {
		setType(TypeFactory.createType(Type.DOUBLE, new ScaleConstraint(3)));
		setValidValues(ValueFactory.createValue(123), ValueFactory
				.createValue(12.322), ValueFactory.createValue(0.133),
				ValueFactory.createNullValue());
		setInvalidValues(ValueFactory.createValue(0.1323), ValueFactory
				.createValue(1244.1235));
		doEdition();
	}

	public void testPattern() throws Exception {
		setType(TypeFactory.createType(Type.STRING, new PatternConstraint(
				"[hc]+at")));
		setValidValues(ValueFactory.createValue("hat"), ValueFactory
				.createValue("cat"), ValueFactory.createNullValue());
		setInvalidValues(ValueFactory.createValue("hate"), ValueFactory
				.createValue("at"));
		doEdition();
	}

	public void testUnique() throws Exception {
		setType(TypeFactory.createType(Type.INT, new UniqueConstraint()));
		checkUniqueness();
	}

	public void testPK() throws Exception {
		setType(TypeFactory.createType(Type.INT, new PrimaryKeyConstraint()));
		checkUniqueness();
	}

	public void testAddWrongTypeBinary() throws Exception {
		setType(TypeFactory.createType(Type.BINARY));
		setValidValues(binaryValue);
		setInvalidValues(booleanValue, byteValue, dateValue, doubleValue,
				floatValue, geomValue, intValue, longValue, shortValue,
				stringValue, timeValue, timestampValue, collectionValue);
		doEdition();
	}

	public void testAddWrongTypeBoolean() throws Exception {
		setType(TypeFactory.createType(Type.BOOLEAN));
		setValidValues(booleanValue, stringValue);
		setInvalidValues(binaryValue, byteValue, dateValue, doubleValue,
				floatValue, geomValue, intValue, longValue, shortValue,
				timeValue, timestampValue, collectionValue);
		doEdition();
	}

	public void testAddWrongTypeCollection() throws Exception {
		setType(TypeFactory.createType(Type.COLLECTION));
		setValidValues(collectionValue);
		setInvalidValues(binaryValue, booleanValue, byteValue, dateValue,
				doubleValue, floatValue, geomValue, intValue, longValue,
				shortValue, stringValue, timeValue, timestampValue);
		doEdition();
	}

	public void testAddWrongTypeDate() throws Exception {
		setType(TypeFactory.createType(Type.DATE));
		setValidValues(timeValue, dateValue, timestampValue, ValueFactory
				.createValue("1980-09-05"), byteValue, intValue, longValue,
				shortValue);
		setInvalidValues(binaryValue, booleanValue, doubleValue, floatValue,
				geomValue, stringValue, collectionValue);
		doEdition();
	}

	public void testAddWrongTypeGeometry() throws Exception {
		setType(TypeFactory.createType(Type.GEOMETRY));
		setValidValues(geomValue, ValueFactory.createValue("POINT (0 0)"));
		setInvalidValues(binaryValue, booleanValue, byteValue, dateValue,
				doubleValue, floatValue, intValue, longValue, shortValue,
				stringValue, timeValue, timestampValue, collectionValue);
		doEdition();
	}

	public void testAddWrongTypeString() throws Exception {
		setType(TypeFactory.createType(Type.STRING));
		setValidValues(binaryValue, booleanValue, byteValue, dateValue,
				doubleValue, floatValue, geomValue, intValue, longValue,
				shortValue, stringValue, timeValue, timestampValue,
				collectionValue);
		doEdition();
	}

	public void testAddWrongTypeTime() throws Exception {
		setType(TypeFactory.createType(Type.TIME));
		setValidValues(dateValue, ValueFactory
				.createValue("1980-09-05 12:00:20"), byteValue, intValue,
				longValue, shortValue, timeValue, timestampValue);
		setInvalidValues(binaryValue, booleanValue, doubleValue, floatValue,
				geomValue, stringValue, collectionValue);
		doEdition();
	}

	public void testAddWrongTypeTimestamp() throws Exception {
		setType(TypeFactory.createType(Type.TIMESTAMP));
		setValidValues(dateValue, ValueFactory
				.createValue("1980-09-05 12:00:24.12132"), byteValue, intValue,
				longValue, shortValue, timeValue, timestampValue);
		setInvalidValues(binaryValue, booleanValue, doubleValue, floatValue,
				geomValue, stringValue, collectionValue);
		doEdition();
	}

	public void testAddWrongTypeByte() throws Exception {
		setType(TypeFactory.createType(Type.BYTE));
		checkWholeNumber();
	}

	private void checkWholeNumber() throws Exception {
		setValidValues(byteValue, intValue, longValue, shortValue);
		setInvalidValues(binaryValue, booleanValue, dateValue, doubleValue,
				floatValue, geomValue, stringValue, timeValue, timestampValue,
				collectionValue);
		doEdition();
	}

	private void checkDecimalNumber() throws Exception {
		setValidValues(doubleValue, floatValue, byteValue, intValue, longValue,
				shortValue);
		setInvalidValues(binaryValue, booleanValue, dateValue, geomValue,
				stringValue, timeValue, timestampValue, collectionValue);
		doEdition();
	}

	public void testAddWrongTypeShort() throws Exception {
		setType(TypeFactory.createType(Type.SHORT));
		checkWholeNumber();
	}

	public void testAddWrongTypeInt() throws Exception {
		setType(TypeFactory.createType(Type.INT));
		checkWholeNumber();
	}

	public void testAddWrongTypeLong() throws Exception {
		setType(TypeFactory.createType(Type.LONG));
		checkWholeNumber();
	}

	public void testAddWrongTypeFloat() throws Exception {
		setType(TypeFactory.createType(Type.FLOAT));
		checkDecimalNumber();
	}

	public void testAddWrongTypeDouble() throws Exception {
		setType(TypeFactory.createType(Type.DOUBLE));
		checkDecimalNumber();
	}

	private void checkUniqueness() throws DriverException {
		DataSource ds = getDataSource();
		ds.open();
		ds.insertFilledRow(new Value[] { ValueFactory.createValue(2) });
		try {
			ds.insertFilledRow(new Value[] { ValueFactory.createValue(2) });
			assertTrue(false);
		} catch (DriverException e) {
		}
		ds.insertFilledRow(new Value[] { ValueFactory.createValue(3) });
		try {
			ds.setFieldValue(ds.getRowCount() - 1, 0, ValueFactory
					.createValue(2));
			assertTrue(false);
		} catch (DriverException e) {
		}
	}

	private void setValidValues(Value... values) {
		this.validValues = values;
	}

	private void setInvalidValues(Value... values) {
		this.invalidValues = values;
	}

	private void setType(Type type) {
		this.type = type;
	}

	private void doEdition() throws Exception {
		DataSource dataSource = getDataSource();
		dataSource.open();
		for (Value value : validValues) {
			dataSource.insertFilledRow(new Value[] { value });
			dataSource.setFieldValue(dataSource.getRowCount() - 1, 0, value);
			assertTrue(dataSource.check(0, value) == null);
		}
		for (Value value : invalidValues) {
			try {
				assertTrue(dataSource.check(0, value) != null);
				dataSource.insertFilledRow(new Value[] { value });
				assertTrue(false);
			} catch (DriverException e) {
			} catch (IncompatibleTypesException e) {
			}
			try {
				assertTrue(dataSource.check(0, value) != null);
				dataSource.setFieldValue(0, 0, value);
				assertTrue(false);
			} catch (DriverException e) {
			} catch (IncompatibleTypesException e) {
			}
		}
		dataSource.commit();
		dataSource.close();
	}

	private DataSource getDataSource() throws DriverException {
		ObjectMemoryDriver omd = new ObjectMemoryDriver(
				new String[] { "string" }, new Type[] { type });
		DataSource dataSource = dsf.getDataSource(omd);
		return dataSource;
	}
}
