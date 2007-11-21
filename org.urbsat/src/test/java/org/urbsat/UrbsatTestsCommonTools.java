package org.urbsat;

import junit.framework.TestCase;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.PrimaryKeyConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.memory.ObjectMemoryDriver;

import com.vividsolutions.jts.io.WKTReader;

public class UrbsatTestsCommonTools extends TestCase {
	public static DataSourceFactory dsf = new DataSourceFactory();

	static {
		try {
			new Register().start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		final WKTReader wktr = new WKTReader();

		// first datasource
		final ObjectMemoryDriver driver1 = new ObjectMemoryDriver(new String[] {
				"pk", "geom" }, new Type[] {
				TypeFactory.createType(Type.INT,
						new Constraint[] { new PrimaryKeyConstraint() }),
				TypeFactory.createType(Type.GEOMETRY,
						new Constraint[] { new GeometryConstraint() }) });

		// insert all filled rows...
		String g1 = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
		String g2 = "MULTILINESTRING ((1 0, 2 0, 2 1, 1 1, 1 0))";
		String g3 = "LINESTRING (1 1, 2 1, 2 2, 1 2, 1 1)";
		String g4 = "MULTIPOLYGON (((0 1, 1 1, 1 2, 0 2, 0 1)))";
		driver1.addValues(new Value[] { ValueFactory.createValue(1),
				ValueFactory.createValue(wktr.read(g1)) });
		driver1.addValues(new Value[] { ValueFactory.createValue(2),
				ValueFactory.createValue(wktr.read(g2)) });
		driver1.addValues(new Value[] { ValueFactory.createValue(3),
				ValueFactory.createValue(wktr.read(g3)) });
		driver1.addValues(new Value[] { ValueFactory.createValue(4),
				ValueFactory.createValue(wktr.read(g4)) });
		// and register this new driver...
		dsf.getSourceManager().register("ds1", driver1);
	}

	@Override
	protected void tearDown() throws Exception {
		if (dsf.getSourceManager().exists("ds1")) {
			dsf.getSourceManager().remove("ds1");
		}
		super.tearDown();
	}
}