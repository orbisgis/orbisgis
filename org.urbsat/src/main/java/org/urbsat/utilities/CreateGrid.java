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

	private double deltaX;
	private double deltaY;
	private double angle;
	private double cosAngle;
	private double sinAngle;
	private double cosInvAngle;
	private double sinInvAngle;
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
					"CreateGrid only operates with two or three values");
		}

		try {
			deltaX = ((NumericValue) values[0]).doubleValue();
			deltaY = ((NumericValue) values[1]).doubleValue();
			inSds = new SpatialDataSourceDecorator(tables[0]);
			inSds.open();
			driver = new ObjectMemoryDriver(
					new String[] { "the_geom", "index" }, new Type[] {
							TypeFactory.createType(Type.GEOMETRY),
							TypeFactory.createType(Type.INT) });
			outDsName = "grid_" + inSds.getName() + "_"
					+ System.currentTimeMillis();
			dsf.getSourceManager().register(outDsName, driver);
			if (3 == values.length) {
				angle = (((NumericValue) values[2]).doubleValue() * Math.PI) / 180;
				cosAngle = Math.cos(angle);
				sinAngle = Math.sin(angle);
				cosInvAngle = Math.cos(-angle);
				sinInvAngle = Math.sin(-angle);
				createOrientedGrid();
			} else {
				createGrid();
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
		return "select creategrid(4000,1000) from myTable;";
	}

	private void createGrid() throws DriverException {
		final Envelope env = inSds.getFullExtent();
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
				summits[0] = new Coordinate(x, y);
				summits[1] = new Coordinate(x + deltaX, y);
				summits[2] = new Coordinate(x + deltaX, y + deltaY);
				summits[3] = new Coordinate(x, y + deltaY);
				summits[4] = new Coordinate(x, y);
				createGridCell(summits, gridCellIndex);
			}
		}
	}

	private void createOrientedGrid() throws DriverException {
		double xMin = Double.MAX_VALUE;
		double xMax = Double.MIN_VALUE;
		double yMin = Double.MAX_VALUE;
		double yMax = Double.MIN_VALUE;

		final int rowCount = (int) inSds.getRowCount();
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			final Geometry g = inSds.getGeometry(rowIndex);
			final Coordinate[] allCoordinates = g.getCoordinates();
			for (Coordinate inCoordinate : allCoordinates) {
				final Coordinate outCoordinate = rotate(inCoordinate);
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

		final Envelope env = new Envelope(xMin, xMax, yMin, yMax);
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
				summits[0] = invRotate(new Coordinate(x, y));
				summits[1] = invRotate(new Coordinate(x + deltaX, y));
				summits[2] = invRotate(new Coordinate(x + deltaX, y + deltaY));
				summits[3] = invRotate(new Coordinate(x, y + deltaY));
				summits[4] = invRotate(new Coordinate(x, y));
				createGridCell(summits, gridCellIndex);
			}
		}
		// driver.addValues(new Value[] {
		// new GeometryValue(geometryFactory
		// .createMultiPoint(new Coordinate[] {
		// invRotate(new Coordinate(xMin, yMin)),
		// invRotate(new Coordinate(xMax, yMin)),
		// invRotate(new Coordinate(xMax, yMax)),
		// invRotate(new Coordinate(xMin, yMax)),
		// invRotate(new Coordinate(xMin, yMin)) })),
		// ValueFactory.createValue(gridCellIndex) });
	}

	private Coordinate rotate(final Coordinate inCoordinate) {
		return new Coordinate(cosAngle * inCoordinate.x - sinAngle
				* inCoordinate.y, sinAngle * inCoordinate.x + cosAngle
				* inCoordinate.y, inCoordinate.z);
	}

	private Coordinate invRotate(final Coordinate inCoordinate) {
		return new Coordinate(cosInvAngle * inCoordinate.x - sinInvAngle
				* inCoordinate.y, sinInvAngle * inCoordinate.x + cosInvAngle
				* inCoordinate.y, inCoordinate.z);
	}

	private void createGridCell(final Coordinate[] summits,
			final int gridCellIndex) {
		final LinearRing g = geometryFactory.createLinearRing(summits);
		final Geometry gg = geometryFactory.createPolygon(g, null);
		driver.addValues(new Value[] { new GeometryValue(gg),
				ValueFactory.createValue(gridCellIndex) });
	}
}