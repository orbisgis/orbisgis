package org.gdms.driver.gdms;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import org.gdms.driver.ReadWriteBufferManager;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.orbisgis.progress.ProgressMonitor;

import com.vividsolutions.jts.geom.Envelope;
import java.util.List;
import org.gdms.driver.DataSet;

/**
 * Class to write gdms files
 *
 * @author Fernando Gonzalez Cortes
 */
public class GdmsWriter {
	private List<Long> rowIndexes = new ArrayList<Long>();
	private RandomAccessFile raf;
	private ReadWriteBufferManager bm;
	private long previousRowEnd = -1;
	private Metadata metadata;
	private Envelope env = null;
	private int currentRow = 0;
	private long rowIndexesDirPos;

	public GdmsWriter(File file) throws IOException {
		raf = new RandomAccessFile(file, "rw");
		bm = new ReadWriteBufferManager(raf.getChannel());
	}

	public void close() throws IOException {
		bm.close();
		raf.close();
	}

	public void write(DataSet dataSource, ProgressMonitor pm)
			throws IOException, DriverException {
		writeMetadata(dataSource.getRowCount(), dataSource.getMetadata());
		// Write the file building the row indexes in memory
		final long rowcount =  dataSource.getRowCount();
                pm.startTask("Writing file", rowcount);
                final int colCount = dataSource.getMetadata().getFieldCount();
		rowIndexes = new ArrayList<Long>((int)rowcount);
		currentRow = 0;
		for (int i = 0; i < rowcount; i++) {
			if (i >= 100 && i % 100 == 0) {
				if (pm.isCancelled()) {
					break;
				} else {
					pm.progressTo(i);
				}
			}

			Value[] row = new Value[colCount];
                        for (int j = 0; j < row.length; j++) {
                                row[j] = dataSource.getFieldValue(i, j);
                        }

			addRow(row);
		}
		// write the row indexes
		writeRowIndexes();
                pm.progressTo(rowcount);
		// write envelope
		writeExtent();
                pm.endTask();
	}

	/**
	 * Write the address of each row in the file we are writing in.
	 * @throws IOException
	 *	If a problem occurs while writing the file
	 */
	public void writeRowIndexes() throws IOException {
		long rowIndexesDir = previousRowEnd;
		bm.position(previousRowEnd);
		for (long index : rowIndexes) {
			bm.putLong(index);
		}
		// Write row indexes position
		bm.position(rowIndexesDirPos);
		bm.putLong(rowIndexesDir);
	}

	public void addValues(Value[] row) throws DriverException {
		try {
			addRow(row);
		} catch (IOException e) {
			throw new DriverException("Cannot add values", e);
		}
	}

	private void addRow(Value[] row) throws DriverException, IOException {
		if (previousRowEnd == -1) {
			previousRowEnd = bm.getPosition();
		}
		rowIndexes.add(previousRowEnd);
		bm.position(previousRowEnd);
		// Leave space for the row header
		long rowHeaderStart = bm.getPosition();
		//The row header will contain the adress of the associated field.
		//This adress is stored in a long, we need 8 bytes.
		int rowHeaderSize = metadata.getFieldCount() * 8;
		bm.position(rowHeaderStart + rowHeaderSize);

		// Write the row and keep the field positions in memory
		long[] fieldPositions = new long[metadata.getFieldCount()];
		for (int j = 0; j < metadata.getFieldCount(); j++) {
			fieldPositions[j] = bm.getPosition();
			Value value = row[j];
			byte[] bytes = value.getBytes();
			//We put the number of bytes we need to store the value.
			bm.putInt(bytes.length);
			//We put the type of the value.
			bm.putInt(value.getType());
			//And finally, we put the value, as an array of bytes.
			bm.put(bytes);
			int typeCode = metadata.getFieldType(j).getTypeCode();
			Envelope fieldEnvelope = null;
			if (!value.isNull() && (typeCode == Type.GEOMETRY)) {
				fieldEnvelope = new Envelope(value.getAsGeometry()
						.getEnvelopeInternal());
			} else if (!value.isNull() && (typeCode == Type.RASTER)) {
				fieldEnvelope = new Envelope(value.getAsRaster().getMetadata()
						.getEnvelope());
			}
			if (fieldEnvelope != null) {
				if (env == null) {
					env = new Envelope(fieldEnvelope);
				} else {
					env.expandToInclude(fieldEnvelope);
				}
			}
		}

		previousRowEnd = bm.getPosition();

		// Write row header
		bm.position(rowHeaderStart);
		for (long fieldPosition : fieldPositions) {
			bm.putLong(fieldPosition);
		}

		currentRow++;
	}

	public void writeExtent() throws IOException {
		if (env != null) {
			bm.position(0);
			// version
			bm.skip(1);
			// dimensions
			bm.skip(4);
			bm.skip(4);

			// write extent
			bm.putDouble(env.getMinX());
			bm.putDouble(env.getMinY());
			bm.putDouble(env.getMaxX());
			bm.putDouble(env.getMaxY());
		}
	}

	public void writeMetadata(long rowCount, Metadata metadata)
			throws IOException, DriverException {
		this.metadata = metadata;

		bm.position(0);

		// Write version number
		bm.put(GdmsDriver.VERSION_NUMBER);

		// write dimensions
		bm.putInt((int) rowCount);
		bm.putInt(metadata.getFieldCount());

		// write default extent
		bm.putDouble(0);
		bm.putDouble(0);
		bm.putDouble(0);
		bm.putDouble(0);

		// write field metadata
		for (int i = 0; i < metadata.getFieldCount(); i++) {
			// write name
			byte[] fieldName = metadata.getFieldName(i).getBytes();
			bm.putInt(fieldName.length);
			bm.put(fieldName);

			// write type
			Type type = metadata.getFieldType(i);
			int typeCode = type.getTypeCode();
			bm.putInt(typeCode);
			Constraint[] constrs = type.getConstraints();

			bm.putInt(constrs.length);
			for (Constraint constraint : constrs) {
				bm.putInt(constraint.getConstraintCode());
				byte[] constraintContent = constraint.getBytes();
				bm.putInt(constraintContent.length);
				bm.put(constraintContent);
			}
		}

		rowIndexesDirPos = bm.getPosition();

		// Skip rowIndexes position
		bm.putLong(-1);
	}

	public void writeWritenRowCount() throws IOException {
		bm.position(0);
		// version
		bm.skip(1);
		// dimensions
		bm.putInt(currentRow);
	}
}
