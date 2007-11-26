package org.urbsat.utilities;

import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.IOException;

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
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.source.Source;
import org.gdms.source.SourceManager;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.strategies.FirstStrategy;
import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class RasterToPoints implements CustomQuery {
	private final static GeometryFactory geometryFactory = new GeometryFactory();

	public DataSource evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values) throws ExecutionException {
		if (tables.length != 0) {
			throw new ExecutionException(
					"RasterToPoints do not needs any table");
		}
		if (1 != values.length) {
			throw new ExecutionException(
					"RasterToPoints needs only one value (the raster source name)");
		}

		try {
			final Source raster = dsf.getSourceManager().getSource(
					values[0].toString());
			int type = raster.getType();
			GeoRaster geoRaster;

			if ((type & SourceManager.RASTER) == SourceManager.RASTER) {
				if (raster.isFileSource()) {
					geoRaster = GeoRasterFactory.createGeoRaster(raster
							.getFile().getAbsolutePath());
					geoRaster.open();
				} else {
					throw new ExecutionException("The raster must be a file !");
				}
			} else {
				throw new UnsupportedOperationException(
						"Cannot understand source type: " + type);
			}

			// built the driver for the resulting datasource and register it...
			ObjectMemoryDriver driver = new ObjectMemoryDriver(new String[] {
					"index", "the_geom", "height" }, new Type[] {
					TypeFactory.createType(Type.INT),
					TypeFactory.createType(Type.GEOMETRY),
					TypeFactory.createType(Type.DOUBLE), });
			String outDsName = dsf.getSourceManager().nameAndRegister(driver);

			for (int l = 0, i = 0; l < geoRaster.getHeight(); l++) {
				for (int c = 0; c < geoRaster.getWidth(); c++) {
					final double height = geoRaster.getGrapImagePlus()
							.getPixelValue(c, l);
					final Point2D point2D = geoRaster.pixelToWorldCoord(c, l);
					final Geometry point = geometryFactory
							.createPoint(new Coordinate(point2D.getX(), point2D
									.getY(), height));
					driver.addValues(new Value[] { ValueFactory.createValue(i),
							ValueFactory.createValue(point),
							ValueFactory.createValue(height) });
					i++;
				}
			}

			// spatial index for the new grid
			dsf.getIndexManager().buildIndex(outDsName, "the_geom",
					SpatialIndex.SPATIAL_INDEX);
			FirstStrategy.indexes = true;

			return dsf.getDataSource(outDsName);
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
		} catch (FileNotFoundException e) {
			throw new ExecutionException(e);
		} catch (IOException e) {
			throw new ExecutionException(e);
		} catch (GeoreferencingException e) {
			throw new ExecutionException(e);
		}
	}

	public String getDescription() {
		return "Transform a Raster into a spatial datasource (set of points)";
	}

	public String getName() {
		return "RasterToPoints";
	}

	public String getSqlOrder() {
		return "select RasterToPoints('myRaster');";
	}
}