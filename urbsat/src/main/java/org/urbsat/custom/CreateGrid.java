package org.urbsat.custom;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.indexes.SpatialIndex;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.NumericValue;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.spatial.GeometryValue;
import org.gdms.spatial.SpatialDataSource;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.instruction.EvaluationException;
import org.gdms.sql.instruction.Expression;
import org.gdms.sql.strategies.FirstStrategy;

import com.hardcode.driverManager.DriverLoadException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

public class CreateGrid implements CustomQuery {

	public DataSource evaluate(DataSource[] tables, Expression[] values)
			throws ExecutionException {
		if (tables.length != 1)
			throw new ExecutionException(
					"CreateGrid only operates on one table");
		if (values.length != 2)
			throw new ExecutionException(
					"CreateGrid only operates with two values");

		DataSource resultDs = null;
		try {
			final double deltaX = ((NumericValue) values[0].evaluate())
					.doubleValue();
			final double deltaY = ((NumericValue) values[1].evaluate())
					.doubleValue();

			final SpatialDataSource sds = new SpatialDataSourceDecorator(
					tables[0]);
			sds.open();
			final Envelope env = sds.getFullExtent();
			int nbX = new Double(Math.ceil((env.getMaxX() - env.getMinX())
					/ deltaX)).intValue();
			int nbY = new Double(Math.ceil((env.getMaxY() - env.getMinY())
					/ deltaY)).intValue();
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					new String[] { "the_geom" }, new Type[] { TypeFactory
							.createType(Type.GEOMETRY) });

			final DataSourceFactory dsf = sds.getDataSourceFactory();
			resultDs = dsf.getDataSource(driver);
			resultDs.open();
			final GeometryFactory geometryFactory = new GeometryFactory();

			double x = env.centre().x - (deltaX * nbX) / 2;
			for (int i = 0; i < nbX; i++, x += deltaX) {
				double y = env.centre().y - (deltaY * nbY) / 2;
				for (int j = 0; j < nbY; j++, y += deltaY) {
					final Coordinate[] summits = new Coordinate[5];
					summits[0] = new Coordinate(x, y);
					summits[1] = new Coordinate(x + deltaX, y);
					summits[2] = new Coordinate(x + deltaX, y + deltaY);
					summits[3] = new Coordinate(x, y + deltaY);
					summits[4] = new Coordinate(x, y);
					final LinearRing g = geometryFactory.createLinearRing(summits);
					final Geometry gg = geometryFactory.createPolygon(g, null);
					resultDs
							.insertFilledRow(new Value[] { new GeometryValue(gg) });
				}
			}
			resultDs.commit();
			sds.cancel();
			// spatial index for the new grid
			dsf.getIndexManager().buildIndex(
					resultDs.getName(), "the_geom",
					SpatialIndex.SPATIAL_INDEX);
			FirstStrategy.indexes = true;
		} catch (DriverException e) {
			e.printStackTrace();
		} catch (InvalidTypeException e) {
			e.printStackTrace();
		} catch (DriverLoadException e) {
			e.printStackTrace();
		} catch (DataSourceCreationException e) {
			e.printStackTrace();
		} catch (FreeingResourcesException e) {
			e.printStackTrace();
		} catch (NonEditableDataSourceException e) {
			e.printStackTrace();
		} catch (EvaluationException e) {
			e.printStackTrace();
		} catch (IndexException e) {
			e.printStackTrace();
		}
		return resultDs;
		// custom CREATEGRID tables landcover2000 values (4000, 1000);
	}

	public String getName() {
		return "CREATEGRID";
	}
}