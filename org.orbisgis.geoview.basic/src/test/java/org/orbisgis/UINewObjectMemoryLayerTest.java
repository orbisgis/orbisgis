package org.orbisgis;

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
import org.orbisgis.core.OrbisgisCore;

import com.vividsolutions.jts.io.WKTReader;

public class UINewObjectMemoryLayerTest extends UITest {
	private static DataSourceFactory dsf = OrbisgisCore.getDSF();

	static {
		new QueryManager();
		new FunctionManager();
	}

	protected void setUp() throws Exception {
		super.setUp();

		final WKTReader wktr = new WKTReader();

		// the datasource
		final ObjectMemoryDriver driver1 = new ObjectMemoryDriver(new String[] {
				"pk", "geom" }, new Type[] {
				TypeFactory.createType(Type.INT,
						new Constraint[] { new PrimaryKeyConstraint() }),
				TypeFactory.createType(Type.GEOMETRY,
						new Constraint[] { new GeometryConstraint() }) });

		// insert all filled rows...
		String g1 = "POLYGON ((0 0, 1 0, 1 1,  0 1, 0 0))";
		driver1.addValues(new Value[] { ValueFactory.createValue(1),
				ValueFactory.createValue(wktr.read(g1)) });
		// and register this new driver...
		dsf.getSourceManager().register("ds1", driver1);
	}

	protected void tearDown() throws Exception {
		super.tearDown();

		if (dsf.exists("ds1")) {
			dsf.remove("ds1");
		}
	}

	public void testDragAndDropAnObjectMemoryLayer() throws Exception {
		// Following 3 lines of code should be uncommented !

		// ILayer myNewLayer = LayerFactory.createLayer("ds1");
		// viewContext.getRootLayer().put(myNewLayer);
		// viewContext.getRootLayer().remove(myNewLayer);
	}
}