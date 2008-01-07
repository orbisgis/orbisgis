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

public class CreateWebGrid implements CustomQuery {
	private final static double DPI = 2 * Math.PI;
	private final static GeometryFactory geometryFactory = new GeometryFactory();

	private double deltaR;
	private double deltaT;
	private Coordinate centroid;

	private SpatialDataSourceDecorator inSds;
	private ObjectMemoryDriver driver;
	private String outDsName;

	public DataSource evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values) throws ExecutionException {
		if (tables.length != 1) {
			throw new ExecutionException(
					"CreateCircGrid only operates on one table");
		}
		if ((2 != values.length) && (3 != values.length)) {
			throw new ExecutionException(
					"CreateCircGrid only operates with two or three values (width, height[, angle])");
		}

		try {
			deltaR = ((NumericValue) values[0]).doubleValue();
			deltaT = ((NumericValue) values[1]).doubleValue();
			inSds = new SpatialDataSourceDecorator(tables[0]);
			inSds.open();

			// built the driver for the resulting datasource and register it...
			driver = new ObjectMemoryDriver(
					new String[] { "the_geom", "index" }, new Type[] {
							TypeFactory.createType(Type.GEOMETRY),
							TypeFactory.createType(Type.INT) });
			outDsName = dsf.getSourceManager().nameAndRegister(driver);
			createGrid(inSds.getFullExtent());
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
		return "CreateWebGrid";
	}

	public String getDescription() {
		return "Calculate a regular grid that may be optionnaly oriented";
	}

	public String getSqlOrder() {
		return "select CreateWebGrid(4000,1000) from myTable;";
	}

	private void createGrid(final Envelope env) throws DriverException {
		final double R = 0.5 * Math.sqrt(env.getWidth() * env.getWidth()
				+ env.getHeight() * env.getHeight());
		centroid = env.centre();
		final double perimeter = DPI * R;
		final int Nr = (int) Math.ceil(R / deltaR);
		deltaR = R / Nr; // TODO : to be comment
		final int Nt = (int) Math.ceil(perimeter / (2 * deltaT));
		deltaT = DPI / Nt;

		int gridCellIndex = 0;
		for (int t = 0; t < Nt; t++) {
			for (int r = 0; r < Nr; r++) {
				createGridCell(r, t, gridCellIndex);
				gridCellIndex++;
			}
		}
	}

	private void createGridCell(final int r, final int t,
			final int gridCellIndex) {
		final Coordinate[] summits = new Coordinate[5];
		summits[0] = polar2cartesian(r, t);
		summits[1] = polar2cartesian(r + 1, t);
		summits[2] = polar2cartesian(r + 1, t + 1);
		summits[3] = polar2cartesian(r, t + 1);
		summits[4] = summits[0];
		createGridCell(summits, gridCellIndex);
	}

	private Coordinate polar2cartesian(final int r, final int t) {
		final double rr = r * deltaR;
		final double tt = t * deltaT;
		return new Coordinate(centroid.x + rr * Math.cos(tt), centroid.y + rr
				* Math.sin(tt));
	}

	private void createGridCell(final Coordinate[] summits,
			final int gridCellIndex) {
		final LinearRing g = geometryFactory.createLinearRing(summits);
		final Geometry gg = geometryFactory.createPolygon(g, null);
		driver.addValues(new Value[] { new GeometryValue(gg),
				ValueFactory.createValue(gridCellIndex) });
	}
}