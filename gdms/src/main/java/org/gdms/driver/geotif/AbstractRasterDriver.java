package org.gdms.driver.geotif;

import java.io.File;
import java.io.IOException;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.RasterTypeConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.DriverUtilities;
import org.gdms.driver.FileReadWriteDriver;
import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Envelope;

public abstract class AbstractRasterDriver implements FileReadWriteDriver {

	protected GeoRaster geoRaster;
	protected RasterMetadata metadata;
	protected Envelope envelope;

	public void open(File file) throws DriverException {
		try {
			geoRaster = GeoRasterFactory
					.createGeoRaster(file.getAbsolutePath());
			geoRaster.open();
			metadata = geoRaster.getMetadata();
			envelope = metadata.getEnvelope();
		} catch (IOException e) {
			throw new DriverException("Cannot access the source: " + file, e);
		} catch (GeoreferencingException e) {
			throw new DriverException("Cannot open the raster: " + file, e);
		}
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	public void close() throws DriverException {
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		switch (fieldId) {
		case 0:
			return ValueFactory.createValue(geoRaster);
		default:
			throw new DriverException("No such field:" + fieldId);

		}
	}

	public void createSource(String path, Metadata metadata,
			DataSourceFactory dataSourceFactory) throws DriverException {
		throw new UnsupportedOperationException("Cannot create an empty raster");
	}

	public long getRowCount() throws DriverException {
		return 1;
	}

	public void copy(File in, File out) throws IOException {
		DriverUtilities.copy(in, out);
	}

	public void writeFile(File file, DataSource dataSource, IProgressMonitor pm)
			throws DriverException {
		checkMetadata(dataSource.getMetadata());
		if (dataSource.getRowCount() == 0) {
			throw new DriverException("Cannot store an empty raster");
		} else if (dataSource.getRowCount() > 1) {
			throw new DriverException("Cannot store more than one raster");
		} else {
			SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
					dataSource);
			GeoRaster gr = sds.getRaster(0);
			try {
				gr.save(file.getAbsolutePath());
			} catch (IOException e) {
				throw new DriverException("Cannot write raster", e);
			} catch (GeoreferencingException e) {
				throw new DriverException("Cannot write raster", e);
			}
		}
	}

	protected void checkMetadata(Metadata metadata) throws DriverException {
		if (metadata.getFieldCount() != 1) {
			throw new DriverException("This source only "
					+ "accepts an unique raster field");
		} else {
			Type fieldType = metadata.getFieldType(0);
			if (fieldType.getTypeCode() != Type.RASTER) {
				throw new DriverException("Raster field expected");
			}
		}
	}

	public Metadata getMetadata() throws DriverException {
		DefaultMetadata metadata = new DefaultMetadata();
		try {
			metadata.addField("raster", TypeFactory.createType(Type.RASTER,
					new RasterTypeConstraint(geoRaster.getType())));
		} catch (IOException e) {
			throw new DriverException("Cannot read the raster type", e);
		} catch (GeoreferencingException e) {
			throw new DriverException("Cannot read the raster type", e);
		}
		return metadata;
	}

	public Number[] getScope(int dimension) throws DriverException {
		switch (dimension) {
		case X:
			return new Number[] { envelope.getMinX(), envelope.getMaxX() };
		case Y:
			return new Number[] { envelope.getMinY(), envelope.getMaxY() };
		default:
			return null;
		}
	}

}
