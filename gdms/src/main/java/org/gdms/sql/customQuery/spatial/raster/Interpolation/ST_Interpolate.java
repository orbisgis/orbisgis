package org.gdms.sql.customQuery.spatial.raster.Interpolation;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.generic.GenericObjectDriver;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.grap.model.GeoRaster;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

/**
 * This function is used to build a raster using a set of points. We assume that
 * the geometry contains the z value.
 * 
 * We used the delaunay triangulation method developed by Martin Schlueter
 * 
 * @author bocher
 * 
 */
public class ST_Interpolate implements CustomQuery {

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		final SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
				tables[0]);
		String zField = null;
		boolean isBaseOnfield = false;
		double pixelSize;
		if (values.length == 3) {
			zField = values[1].getAsString();
			isBaseOnfield = true;
			pixelSize = values[2].getAsDouble();
		} else {
			pixelSize = values[1].getAsDouble();
		}
		try {
			sds.open();
			sds.setDefaultGeometry(values[0].toString());
			int zFieldIndex = -1;
			if (zField != null) {
				zFieldIndex = sds.getFieldIndexByName(zField);
			}

			final long rowCount = sds.getRowCount();

			int numberOfPoints = (int) rowCount;

			// Initialize coordinate arrays:
			double[] xVcl = new double[(numberOfPoints + 4)]; // X coordinates
			// (input) (xVcl[0]
			// is not used)
			double[] yVcl = new double[(numberOfPoints + 4)]; // Y coordinates
			// (input) (yVcl[0]
			// is not used)
			double[] zVcl = new double[(numberOfPoints + 4)]; // Z coordinates
			// (input) (zVcl[0]

			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {

				if (rowIndex / 100 == rowIndex / 100.0) {
					if (pm.isCancelled()) {
						break;
					} else {
						pm.progressTo((int) (100 * rowIndex / rowCount));
					}
				}

				final Geometry geometry = sds.getGeometry(rowIndex);

				double zFromField = Double.NaN;
				if (isBaseOnfield) {
					zFromField = sds.getFieldValue(rowIndex, zFieldIndex)
							.getAsDouble();
				}
				if (geometry instanceof Point) {
					final Point p = (Point) geometry;
					final double x = p.getCoordinate().x;
					final double y = p.getCoordinate().y;
					final double z = p.getCoordinate().z;

					xVcl[rowIndex + 1] = x;
					yVcl[rowIndex + 1] = y;

					if (zField != null) {
						zVcl[rowIndex + 1] = zFromField;
					} else {
						zVcl[rowIndex + 1] = z;
					}

				}

			}
			sds.close();

			TINToRaster tinToRaster = new TINToRaster(pixelSize,
					numberOfPoints, xVcl, yVcl, zVcl);

			GeoRaster georaster = tinToRaster.getGeoRaster();

			/*
			 * To understand the bug uncomment the code try { georaster.show();
			 * } catch (IOException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); }
			 */
			final GenericObjectDriver driver = new GenericObjectDriver(
					getMetadata(null));

			driver
					.addValues(new Value[] { ValueFactory
							.createValue(georaster) });

			return driver;
		} catch (DriverException e) {
			throw new ExecutionException(e);
		}
	}

	public TableDefinition[] getTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.GEOMETRY };
	}

	public String getDescription() {
		return "Build a raster using an interpolate method based on delaunay triangulation";
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] {
				new Arguments(Argument.GEOMETRY, Argument.NUMERIC,
						Argument.NUMERIC),
				new Arguments(Argument.GEOMETRY, Argument.NUMERIC) };
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] { TypeFactory
				.createType(Type.RASTER) }, new String[] { "raster" });
	}

	public String getName() {
		return "ST_Interpolate";
	}

	public String getSqlOrder() {
		return "select ST_Interpolate(the_geom, pixelsize) from mydata;";
	}
}