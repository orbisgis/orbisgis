package org.urbsat.utilities;

import java.util.Formatter;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.indexes.SpatialIndex;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.NumericValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.spatial.GeometryValue;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.strategies.FirstStrategy;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

public class CreateGrid implements CustomQuery {
	// call CREATEGRID from landcover2000 values (4000, 1000);
	// select creategrid(4000,1000) from landcover2000;

	public DataSource evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values) throws ExecutionException {
		if (tables.length != 1)
			throw new ExecutionException(
					"CreateGrid only operates on one table");
		if (values.length != 2)
			throw new ExecutionException(
					"CreateGrid only operates with two values");

		try {
			final double deltaX = ((NumericValue) values[0]).doubleValue();
			final double deltaY = ((NumericValue) values[1]).doubleValue();

			final SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
					tables[0]);
			sds.open();

			final Envelope env = sds.getFullExtent();
			final int nbX = new Double(Math
					.ceil((env.getMaxX() - env.getMinX()) / deltaX)).intValue();
			final int nbY = new Double(Math
					.ceil((env.getMaxY() - env.getMinY()) / deltaY)).intValue();
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					new String[] { "the_geom", "index" }, new Type[] {
							TypeFactory.createType(Type.GEOMETRY),
							TypeFactory.createType(Type.INT) });
			final String resultDsName = new Formatter().format(
					"gridOver_%s_%.1f_%.1f", sds.getName(), deltaX, deltaY)
					.toString();
			dsf.getSourceManager().register(resultDsName, driver);

			final GeometryFactory geometryFactory = new GeometryFactory();
			int gridCellIndex = 0;

			double x = env.centre().x - (deltaX * nbX) / 2;
			for (int i = 0; i < nbX; i++, x += deltaX) {
				double y = env.centre().y - (deltaY * nbY) / 2;
				for (int j = 0; j < nbY; j++, y += deltaY) {
					gridCellIndex++;
					final Coordinate[] summits = new Coordinate[5];
					summits[0] = new Coordinate(x, y);
					summits[1] = new Coordinate(x + deltaX, y);
					summits[2] = new Coordinate(x + deltaX, y + deltaY);
					summits[3] = new Coordinate(x, y + deltaY);
					summits[4] = new Coordinate(x, y);
					final LinearRing g = geometryFactory
							.createLinearRing(summits);
					final Geometry gg = geometryFactory.createPolygon(g, null);
					driver.addValues(new Value[] { new GeometryValue(gg),
							ValueFactory.createValue(gridCellIndex) });
				}
			}
			sds.cancel();

			// spatial index for the new grid
			dsf.getIndexManager().buildIndex(resultDsName, "the_geom",
					SpatialIndex.SPATIAL_INDEX);
			FirstStrategy.indexes = true;
			return dsf.getDataSource(resultDsName);
		} catch (Exception e) {
			throw new ExecutionException(e);
		}
	}

	public String getName() {
		return "CREATEGRID";
	}

	public String getDescription() {
		return "select creategrid(4000,1000) from myTable;";
	}
}