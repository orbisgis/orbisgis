package org.urbsat.utilities;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;

// call register('/import/leduc/dev/eclipse/datas2tests/cir/face_unitaire.cir','src');
// select Explode(ConvertIntoMultiPoint2D(the_geom)) from src;

/*
 call register('/import/leduc/dev/eclipse/datas2tests/shp/smallshape2D/points.shp','src');

 select id,AsWKT(the_geom),AsWKT(Explode(ConvertIntoMultiPoint2D(the_geom))) from points;
 */

public class Explode implements CustomQuery {
	private final static DataSourceFactory dsf = new DataSourceFactory();
	private static DataSource dataSource;
	private static long count = 0;

	static {
		final String uniqueName = "test";
		ObjectMemoryDriver omd;
		try {
			omd = new ObjectMemoryDriver(new String[] { "id", "the_geom" },
					new Type[] { TypeFactory.createType(Type.LONG),
							TypeFactory.createType(Type.GEOMETRY) });
			dsf.getSourceManager().register(uniqueName,
					new ObjectSourceDefinition(omd));
			dataSource = dsf.getDataSource(uniqueName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return "Explode";
	}

	public Value evaluate(Value[] args) throws FunctionException {
		final GeometryValue gv = (GeometryValue) args[0];
		final Geometry geom = gv.getGeom();

		try {
			dataSource.open();
			if (geom instanceof GeometryCollection) {
				dataSource.insertFilledRow(new Value[] {
						ValueFactory.createValue(count++), gv });
			} else {
				final long nbOfGeometries = geom.getNumGeometries();
				for (int i = 0; i < nbOfGeometries; i++) {
					dataSource.insertFilledRow(new Value[] {
							ValueFactory.createValue(count++),
							ValueFactory.createValue(geom.getGeometryN(i)) });
				}
			}
			dataSource.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getDescription() {
		return "Convert any GeometryCollection into a set of single Geometries";
	}

	public int getType(int[] paramTypes) {
		return Type.GEOMETRY;
	}

	public boolean isAggregate() {
		// TODO
		return false;
	}

	public DataSource evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values) throws ExecutionException {
		// TODO Auto-generated method stub
		return null;
	}
}