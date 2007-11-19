package org.gdms.sql;

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.PrimaryKeyConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.function.FunctionManager;

import com.vividsolutions.jts.io.WKTReader;

public class SpatialConvertCommonTools extends TestCase {
	public static DataSourceFactory dsf = new DataSourceFactory();

	static {
		new QueryManager();
		new FunctionManager();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		final WKTReader wktr = new WKTReader();

		// first datasource
		if (dsf.getSourceManager().exists("ds1")) {
			dsf.getSourceManager().remove("ds1");
		}

		final ObjectMemoryDriver driver1 = new ObjectMemoryDriver(new String[] {
				"pk", "geom" }, new Type[] {
				TypeFactory.createType(Type.INT,
						new Constraint[] { new PrimaryKeyConstraint() }),
				TypeFactory.createType(Type.GEOMETRY,
						new Constraint[] { new GeometryConstraint() }) });

		// insert all filled rows...
		String g1 = "MULTIPOLYGON (((0 0, 1 1, 0 1, 0 0)), ((1 1, 1 2, 2 2, 1 1)))";
		String g2 = "MULTILINESTRING ((0 0, 1 0, 1 1), (0 1, 2 2, 0 2, 2 -2))";
		String g3 = "MULTIPOINT (0 0, 0 1, 3 3, 4 4, 5 5, 15 10)";
		driver1.addValues(new Value[] { ValueFactory.createValue(1),
				ValueFactory.createValue(wktr.read(g1)) });
		driver1.addValues(new Value[] { ValueFactory.createValue(2),
				ValueFactory.createValue(wktr.read(g2)) });
		driver1.addValues(new Value[] { ValueFactory.createValue(3),
				ValueFactory.createValue(wktr.read(g3)) });
		// and register this new driver...
		dsf.getSourceManager().register("ds1", driver1);

		// second datasource
		if (dsf.getSourceManager().exists("ds2")) {
			dsf.getSourceManager().remove("ds2");
		}

		final ObjectMemoryDriver driver2 = new ObjectMemoryDriver(new String[] {
				"pk", "geom" }, new Type[] {
				TypeFactory.createType(Type.INT,
						new Constraint[] { new PrimaryKeyConstraint() }),
				TypeFactory.createType(Type.GEOMETRY,
						new Constraint[] { new GeometryConstraint() }) });

		final DataSource dataSource2 = dsf.getDataSource(driver2);
		dataSource2.open();
		// insert all filled rows...
		g1 = "MULTIPOINT (0 0, 1 1, 2 2, 1 1)";
		g2 = "MULTIPOINT (0 0, 1 0, 0 1)";
		g3 = "MULTIPOINT (0 0, 0 1, 3 3)";
		dataSource2.insertFilledRow(new Value[] { ValueFactory.createValue(11),
				ValueFactory.createValue(wktr.read(g1)) });
		dataSource2.insertFilledRow(new Value[] { ValueFactory.createValue(22),
				ValueFactory.createValue(wktr.read(g2)) });
		dataSource2.insertFilledRow(new Value[] { ValueFactory.createValue(33),
				ValueFactory.createValue(wktr.read(g3)) });
		dataSource2.commit();
		// and register this new driver...
		dsf.getSourceManager().register("ds2", driver2);
	}
}