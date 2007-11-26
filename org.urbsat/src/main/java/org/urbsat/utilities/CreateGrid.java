package org.urbsat.utilities;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.indexes.SpatialIndex;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.NumericValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
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

/*
 select register('../../datas2tests/cir/face_unitaire.cir','face_unitaire');
 select creategrid(0.2,0.2) from face_unitaire;
 select sum(area(intersection(a.the_geom,b.the_geom))) from face_unitaire as a, grid_face_unitaire as b where intersects(a.the_geom,b.the_geom);
 select area(intersection(a.the_geom,b.the_geom)),index from face_unitaire as a, grid_face_unitaire as b where intersects(a.the_geom,b.the_geom) order by index;

 select creategrid(0.2,0.2,45) from face_unitaire;
 */

public class CreateGrid implements CustomQuery {
	private final static GeometryFactory geometryFactory = new GeometryFactory();

	private boolean isAnOrientedGrid;

	private double deltaX;
	private double deltaY;
	private double angle;
	private double cosAngle;
	private double sinAngle;
	private double cosInvAngle;
	private double sinInvAngle;
	private double llcX;
	private double llcY;

	private SpatialDataSourceDecorator inSds;
	private ObjectMemoryDriver driver;
	private String outDsName;

	public DataSource evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values) throws ExecutionException {
		if (tables.length != 1) {
			throw new ExecutionException(
					"CreateGrid only operates on one table");
		}
		if ((2 != values.length) && (3 != values.length)) {
			throw new ExecutionException(
					"CreateGrid only operates with two or three values (width, height[, angle])");
		}

		try {
			deltaX = ((NumericValue) values[0]).doubleValue();
			deltaY = ((NumericValue) values[1]).doubleValue();
			inSds = new SpatialDataSourceDecorator(tables[0]);
			inSds.open();

			// built the driver for the resulting datasource and register it...
			driver = new ObjectMemoryDriver(
					new String[] { "the_geom", "index" }, new Type[] {
							TypeFactory.createType(Type.GEOMETRY),
							TypeFactory.createType(Type.INT) });
			outDsName = dsf.getSourceManager().nameAndRegister(driver);

			if (3 == values.length) {
				isAnOrientedGrid = true;
				angle = (((NumericValue) values[2]).doubleValue() * Math.PI) / 180;
				createGrid(prepareOrientedGrid());
			} else {
				isAnOrientedGrid = false;
				createGrid(inSds.getFullExtent());
			}
			inSds.cancel();

			// spatial index for the new grid
			dsf.getIndexManager().buildIndex(outDsName, "the_geom",
					SpatialIndex.SPATIAL_INDEX);
			FirstStrategy.indexes = true;
			return dsf.getDataSource(outDsName);
		} catch (Exception e) {
			throw new ExecutionException(e);
		}
	}

	public String getName() {
		return "CREATEGRID";
	}

	public String getDescription() {
		return "Calculate a regular grid that may be optionnaly oriented";
	}

	public String getSqlOrder() {
		return "select creategrid(4000,1000[,15]) from myTable;";
	}

	private void createGrid(final Envelope env) throws DriverException {
		final int nbX = new Double(Math.ceil((env.getMaxX() - env.getMinX())
				/ deltaX)).intValue();
		final int nbY = new Double(Math.ceil((env.getMaxY() - env.getMinY())
				/ deltaY)).intValue();
		int gridCellIndex = 0;
		double x = env.centre().x - (deltaX * nbX) / 2;
		for (int i = 0; i < nbX; i++, x += deltaX) {
			double y = env.centre().y - (deltaY * nbY) / 2;
			for (int j = 0; j < nbY; j++, y += deltaY) {
				gridCellIndex++;
				final Coordinate[] summits = new Coordinate[5];
				summits[0] = invTranslateAndRotate(x, y);
				summits[1] = invTranslateAndRotate(x + deltaX, y);
				summits[2] = invTranslateAndRotate(x + deltaX, y + deltaY);
				summits[3] = invTranslateAndRotate(x, y + deltaY);
				summits[4] = invTranslateAndRotate(x, y);
				createGridCell(summits, gridCellIndex);
			}
		}
	}

	private Envelope prepareOrientedGrid() throws DriverException {
		double xMin = Double.MAX_VALUE;
		double xMax = Double.MIN_VALUE;
		double yMin = Double.MAX_VALUE;
		double yMax = Double.MIN_VALUE;

		cosAngle = Math.cos(angle);
		sinAngle = Math.sin(angle);
		cosInvAngle = Math.cos(-angle);
		sinInvAngle = Math.sin(-angle);
		final Envelope env = inSds.getFullExtent();
		llcX = env.getMinX();
		llcY = env.getMinY();

		final int rowCount = (int) inSds.getRowCount();
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			final Geometry g = inSds.getGeometry(rowIndex);
			final Coordinate[] allCoordinates = g.getCoordinates();
			for (Coordinate inCoordinate : allCoordinates) {
				final Coordinate outCoordinate = translateAndRotate(inCoordinate);
				if (outCoordinate.x < xMin) {
					xMin = outCoordinate.x;
				}
				if (outCoordinate.x > xMax) {
					xMax = outCoordinate.x;
				}
				if (outCoordinate.y < yMin) {
					yMin = outCoordinate.y;
				}
				if (outCoordinate.y > yMax) {
					yMax = outCoordinate.y;
				}
			}
		}
		return new Envelope(xMin, xMax, yMin, yMax);
	}

	private final Coordinate translateAndRotate(final Coordinate inCoordinate) {
		// do the rotation after the translation in the local coordinates system
		final double x = inCoordinate.x - llcX;
		final double y = inCoordinate.y - llcY;
		return new Coordinate(cosAngle * x - sinAngle * y, sinAngle * x
				+ cosAngle * y, inCoordinate.z);
	}

	private final Coordinate invTranslateAndRotate(final double x,
			final double y) {
		if (isAnOrientedGrid) {
			// do the (reverse) translation after the (reverse) rotation
			final double localX = cosInvAngle * x - sinInvAngle * y;
			final double localY = sinInvAngle * x + cosInvAngle * y;
			return new Coordinate(localX + llcX, localY + llcY);
		} else {
			return new Coordinate(x, y);
		}
	}

	private void createGridCell(final Coordinate[] summits,
			final int gridCellIndex) {
		final LinearRing g = geometryFactory.createLinearRing(summits);
		final Geometry gg = geometryFactory.createPolygon(g, null);
		driver.addValues(new Value[] { new GeometryValue(gg),
				ValueFactory.createValue(gridCellIndex) });
	}
}