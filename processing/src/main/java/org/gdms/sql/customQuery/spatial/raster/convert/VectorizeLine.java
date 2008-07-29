package org.gdms.sql.customQuery.spatial.raster.convert;

import ij.process.ImageProcessor;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.DimensionConstraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.function.Arguments;
import org.grap.model.GeoRaster;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

public class VectorizeLine implements CustomQuery {
	private static final GeometryFactory geometryFactory = new GeometryFactory();
	private static final int[] NEIGHBORS_X = { 1, 1, 0, -1, -1, -1, 0, 1 };
	private static final int[] NEIGHBORS_Y = { 0, -1, -1, -1, 0, 1, 1, 1 };

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		final SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
				tables[0]);

		try {
			sds.open();
			if (1 == values.length) {
				// if no raster's field's name is provided, the default (first)
				// one is arbitrarily chosen.
				sds.setDefaultGeometry(values[0].toString());
			}

			final Map<Float, Set<LineString>> map = new HashMap<Float, Set<LineString>>();

			final long rowCount = sds.getRowCount();
			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				final GeoRaster geoRasterSrc = sds.getRaster(rowIndex);
				final ImageProcessor processor = geoRasterSrc.getImagePlus()
						.getProcessor();
				final float ndv = Double.isNaN(geoRasterSrc.getNoDataValue()) ? GeoRaster.FLOAT_NO_DATA_VALUE
						: (float) geoRasterSrc.getNoDataValue();
				final int ncols = geoRasterSrc.getWidth();
				final int nrows = geoRasterSrc.getHeight();
				final boolean[] alreadyVisited = new boolean[ncols * nrows];
				final int[] neighborsIndices = new int[] { 1, -ncols + 1,
						-ncols, -ncols - 1, -1, ncols - 1, ncols, ncols + 1 };

				for (int y = 0, i = 0; y < nrows; y++) {

					if (y / 100 == y / 100.0) {
						if (pm.isCancelled()) {
							break;
						} else {
							pm
									.progressTo((int) (100 * y * rowIndex / (geoRasterSrc
											.getHeight() * rowCount)));
						}
					}

					for (int x = 0; x < ncols; x++, i++) {
						if (!alreadyVisited[i]) {
							alreadyVisited[i] = true;
							final float pixelValue = processor.getPixelValue(x,
									y);
							// TODO : please simplify following test !
							if ((pixelValue != ndv)
									&& (pixelValue != GeoRaster.FLOAT_NO_DATA_VALUE)
									&& (pixelValue != GeoRaster.SHORT_NO_DATA_VALUE)
									&& (pixelValue != GeoRaster.BYTE_NO_DATA_VALUE)) {
								LineString lineString = fromPixelsToLineString(
										geoRasterSrc, processor,
										alreadyVisited, pixelValue, x, y, i,
										neighborsIndices);
								if (null != lineString) {
									lineString = SimplificationUtilities
											.simplifyGeometry(lineString);
									if (map.containsKey(pixelValue)) {
										map.get(pixelValue).add(lineString);
									} else {
										final Set<LineString> set = new HashSet<LineString>();
										set.add(lineString);
										map.put(pixelValue, set);
									}
								}
							}
						}
					}
				}
			}
			sds.close();

			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					getMetadata(null));
			for (float pixelValue : map.keySet()) {
				final Set<LineString> setOfLineString = map.get(pixelValue);
				final LineString[] arrayOfLineString = setOfLineString
						.toArray(new LineString[0]);

				driver.addValues(new Value[] {
						ValueFactory.createValue(pixelValue),
						ValueFactory.createValue(geometryFactory
								.createMultiLineString(arrayOfLineString)) });
			}
			return driver;
		} catch (DriverException e) {
			throw new ExecutionException(e);
		} catch (IOException e) {
			throw new ExecutionException(e);
		}
	}

	private LineString fromPixelsToLineString(final GeoRaster geoRasterSrc,
			final ImageProcessor processor, final boolean[] alreadyVisited,
			final float pixelValue, final int x0, final int y0, final int i0,
			final int[] neighborsIndices) {
		final List<Coordinate> coordinates = new ArrayList<Coordinate>();
		final int ncols = processor.getWidth();
		int x = x0;
		int y = y0;
		Integer i = i0;

		do {
			alreadyVisited[i] = true;

			// add the current point to the linestring
			final Point2D point2D = geoRasterSrc.fromPixelToRealWorld(x, y);
			coordinates.add(new Coordinate(point2D.getX(), point2D.getY()));

			// then find the next neighbor
			i = findTheFirstNextNeighbor(processor, alreadyVisited, pixelValue,
					x, y, i, neighborsIndices);

			if (null != i) {
				x = i % ncols;
				y = i / ncols;
			}
		} while (null != i);

		if (1 < coordinates.size()) {
			return geometryFactory.createLineString(coordinates
					.toArray(new Coordinate[0]));
		} else {
			return null;
		}
	}

	private Integer findTheFirstNextNeighbor(final ImageProcessor processor,
			final boolean[] alreadyVisited, final float pixelValue, int x0,
			int y0, Integer i0, int[] neighborsIndices) {
		final int nrows = processor.getHeight();
		final int ncols = processor.getWidth();

		for (int i = 0; i < 8; i++) {
			int x = x0 + NEIGHBORS_X[i];
			int y = y0 + NEIGHBORS_Y[i];
			int iNext = i0 + neighborsIndices[i]; // y * ncols + x;

			if ((0 <= x) && (x < ncols) && (0 <= y) && (y < nrows)
					&& !alreadyVisited[iNext]) {
				final float pv = processor.getPixelValue(x, y);
				if (pixelValue == pv) {
					return iNext;
				}
			}
		}
		return null;
	}

	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.RASTER };
	}

	public String getDescription() {
		return "This custom query converts a (set of) GeoRaster(s) into a set of MultiLineString";
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments() };
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.DOUBLE),
				TypeFactory.createType(Type.GEOMETRY, new GeometryConstraint(
						GeometryConstraint.MULTI_LINESTRING),
						new DimensionConstraint(2)) }, new String[] { "gid",
				"the_geom" });
	}

	public String getName() {
		return "VectorizeLine";
	}

	public String getSqlOrder() {
		return "select VectorizeLine() from mydata;";
	}
}