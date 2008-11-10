package org.gdms.data.edition;

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
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.ObjectMemoryDriver;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class ConstraintTest extends TestCase {

	private DataSourceFactory dsf;
	private Type type;
	private Value[] validValues = new Value[0];
	private Value[] invalidValues = new Value[0];

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
		checkCannotSetAndOnlyAddNull(constraint);
	}

	public void testReadOnly() throws Exception {
		ReadOnlyConstraint constraint = new ReadOnlyConstraint();
		checkCannotSetAndOnlyAddNull(constraint);
	}

	/**
	 * Cannot set a value Cannot insert a new row with values different than
	 * null
	 * 
	 * @param constraint
	 * @throws DriverException
	 */
	private void checkCannotSetAndOnlyAddNull(Constraint constraint)
			throws DriverException {
		Value three = ValueFactory.createValue(3);
		Value nullV = ValueFactory.createNullValue();
		setType(TypeFactory.createType(Type.INT, constraint));
		DataSource ds = getDataSource();
		ds.open();
		ds.insertFilledRow(new Value[] { nullV });
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
		try {
			ds.setFieldValue(0, 0, nullV);
			assertTrue(false);
		} catch (DriverException e) {
		}
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
		setType(TypeFactory.createType(Type.DOUBLE, new PatternConstraint(
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
		}
		for (Value value : invalidValues) {
			try {
				dataSource.insertFilledRow(new Value[] { value });
				assertTrue(false);
			} catch (DriverException e) {
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
