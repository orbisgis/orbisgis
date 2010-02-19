package org.gdms.sql.strategies;

import java.io.File;
import java.io.IOException;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.gdms.GdmsDriver;
import org.gdms.driver.gdms.GdmsReader;
import org.gdms.driver.gdms.GdmsWriter;

/**
 * This driver writes all the content added with the {@link #addValues(Value[])}
 * method to a file saving main memory. One all calls to
 * {@link #addValues(Value[])} are done, the method {@link #writingFinished()}
 * must be called.
 *
 *
 *
 */
public class DiskBufferDriver extends AbstractBasicSQLDriver implements
		ObjectDriver {

	private Metadata metadata;
	private GdmsWriter writer;
	private File file;
	private boolean firstRow = true;
	private GdmsReader reader;

	public DiskBufferDriver(DataSourceFactory dsf, Metadata metadata)
			throws DriverException {
		this.metadata = metadata;

		// Create a temp file to populate
		file = new File(dsf.getTempFile("gdms"));
		try {
			writer = new GdmsWriter(file);
		} catch (IOException e) {
			throw new DriverException("Cannot start writing process", e);
		}
	}

	@Override
	public void start() throws DriverException {
		writingFinished();

		try {
			// Open file
			reader = new GdmsReader(file);
			reader.readMetadata();
		} catch (IOException e) {
			throw new DriverException("Cannot open temporal file for reading",
					e);
		}
		writer = null;
	}

	/**
	 * This method must be called when all the contents have been added to the
	 * file
	 *
	 * @throws DriverException
	 */
	public void writingFinished() throws DriverException {
		// Close writing
		try {
			if (writer != null) {
				writer.writeRowIndexes();
				writer.writeExtent();
				writer.writeWritenRowCount();
				writer.close();
				writer = null;
			}
		} catch (IOException e) {
			throw new DriverException("Cannot finalize writing process", e);
		}
	}

	@Override
	public void stop() throws DriverException {
		try {
			reader.close();
		} catch (IOException e) {
			throw new DriverException("Cannot close gdms reader", e);
		}
	}

	@Override
	public Metadata getMetadata() throws DriverException {
		return metadata;
	}

	@Override
	public int getType() {
		return new GdmsDriver().getType();
	}

	@Override
	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	@Override
	public String getDriverId() {
		return new GdmsDriver().getDriverId();
	}

	@Override
	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return reader.getFieldValue(rowIndex, fieldId);
	}

	@Override
	public long getRowCount() throws DriverException {
		return reader.getRowCount();
	}

	@Override
	public Number[] getScope(int dimension) throws DriverException {
		return reader.getScope(dimension);
	}

	/**
	 * Add a new row to the file
	 *
	 * @param row
	 * @throws DriverException
	 */
	public void addValues(Value[] row) throws DriverException {
		if (firstRow) {
			firstRow = false;
			try {
				writer.writeMetadata(0, getMetadata());
			} catch (IOException e) {
				throw new DriverException("Cannot write metadata", e);
			}
		}
		writer.addValues(row);
	}

	public File getFile() {
		return file;
	}
}
