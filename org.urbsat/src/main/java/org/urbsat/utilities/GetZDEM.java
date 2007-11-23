package org.urbsat.utilities;

import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.indexes.SpatialIndex;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.source.Source;
import org.gdms.source.SourceManager;
import org.gdms.spatial.GeometryValue;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.strategies.FirstStrategy;
import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;

/*
 * select Explode() from ile_de_nantes_bati;
 * select GetZDEM('MNT_Nantes_Lambert') from explode_ile_de_nantes_bati_...;
 * select GetZDEM('MNT_Nantes_Lambert','the_geom') from explode_ile_de_nantes_bati_...;
 * 
 * select GetZDEM('3x3') from shape;
 */

public class GetZDEM implements CustomQuery {
	/*
	 * This CustomQuery needs to be rewritten using : UPDATE ds SET the_geom =
	 * addZDEM(RasterLayerAlias) WHERE ...;
	 */

	private GeoRaster geoRaster;
	private ObjectMemoryDriver driver;
	private String outDsName;

	public DataSource evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values) throws ExecutionException {
		if (tables.length != 1) {
			throw new ExecutionException("GetZDEM only operates on one table");
		}
		if ((1 != values.length) && (2 != values.length)) {
			throw new ExecutionException(
					"GetZDEM only operates with one or two value(s) (the DEM and the geom field name)");
		}

		try {
			final Source dem = dsf.getSourceManager().getSource(
					values[0].toString());
			int type = dem.getType();
			if ((type & SourceManager.RASTER) == SourceManager.RASTER) {
				if (dem.isFileSource()) {
					geoRaster = GeoRasterFactory.createGeoRaster(dem.getFile()
							.getAbsolutePath());
					geoRaster.open();
				} else {
					throw new ExecutionException("The DEM must be a file !");
				}
			} else {
				throw new UnsupportedOperationException(
						"Cannot understand source type: " + type);
			}

			final SpatialDataSourceDecorator inSds = new SpatialDataSourceDecorator(
					tables[0]);
			inSds.open();
			if (2 == values.length) {
				// if no spatial's field's name is provided, the default (first)
				// one is arbitrarily choose.
				final String spatialFieldName = values[1].toString();
				inSds.setDefaultGeometry(spatialFieldName);
			}

			// built the driver for the resulting datasource and register it...
			driver = new ObjectMemoryDriver(new String[] { "index", "the_geom",
					"height" }, new Type[] { TypeFactory.createType(Type.INT),
					TypeFactory.createType(Type.GEOMETRY),
					TypeFactory.createType(Type.DOUBLE), });
			outDsName = dsf.getSourceManager().nameAndRegister(driver);

			double height;
			final int rowCount = (int) inSds.getRowCount();
			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				final Geometry g = inSds.getGeometry(rowIndex);
				// is the following line usefull ?
				final Geometry gg = new GeometryFactory().createGeometry(g);
				if (g instanceof GeometryCollection) {
					throw new ExecutionException(
							"The input datasource must be exploded first (no GeometryCollection object) !");
				} else {
					final Coordinate[] coordinates = gg.getCoordinates();
					Arrays.sort(coordinates, new Comparator<Coordinate>() {
						public int compare(Coordinate o1, Coordinate o2) {
							return new Double(o1.z - o2.z).intValue();
						}
					});
					final double globalGroundZ = getGroundZ(coordinates[0].x,
							coordinates[0].y);
					height = coordinates[coordinates.length - 1].z
							- globalGroundZ;
					if (Double.isNaN(height)) {
						height = 0;
					}
					for (Coordinate c : coordinates) {
						// final double localGroundZ = getGroundZ(c.x, c.y);
						// c.z = localGroundZ;
						c.z = globalGroundZ;
					}
				}
				driver
						.addValues(new Value[] {
								ValueFactory.createValue(rowIndex),
								new GeometryValue(gg),
								ValueFactory.createValue(height) });
			}
			inSds.cancel();

			// spatial index for the new grid
			dsf.getIndexManager().buildIndex(outDsName, "the_geom",
					SpatialIndex.SPATIAL_INDEX);
			FirstStrategy.indexes = true;
			return dsf.getDataSource(outDsName);

		} catch (FileNotFoundException e) {
			throw new ExecutionException(e);
		} catch (IOException e) {
			throw new ExecutionException(e);
		} catch (GeoreferencingException e) {
			throw new ExecutionException(e);
		} catch (DriverException e) {
			throw new ExecutionException(e);
		} catch (InvalidTypeException e) {
			throw new ExecutionException(e);
		} catch (IndexException e) {
			throw new ExecutionException(e);
		} catch (NoSuchTableException e) {
			throw new ExecutionException(e);
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		} catch (DataSourceCreationException e) {
			throw new ExecutionException(e);
		}
	}

	private double getGroundZ(final double x, final double y)
			throws IOException, GeoreferencingException {
		final Point2D point = geoRaster.getPixelCoords(x, y);
		return geoRaster.getGrapImagePlus().getPixelValue((int) point.getX(),
				(int) point.getY());
	}

	public String getDescription() {
		return "This custom query produces a new SDS with 3 fields : id, the_geom (ground geometry), height";
	}

	public String getName() {
		return "GetZDEM";
	}
}