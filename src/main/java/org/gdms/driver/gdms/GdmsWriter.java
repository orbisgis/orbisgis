package org.gdms.driver.gdms;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.indexes.btree.ReadWriteBufferManager;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Class to write gdms files
 * 
 * @author Fernando Gonzalez Cortes
 */
public class GdmsWriter {
	private ArrayList<Integer> rowIndexes = new ArrayList<Integer>();
	private RandomAccessFile raf;
	private ReadWriteBufferManager bm;
	private int previousRowEnd = -1;
	private Metadata metadata;
	private Envelope env = null;
	private int currentRow = 0;
	private int rowIndexesDirPos;

	public GdmsWriter(File file) throws IOException {
		raf = new RandomAccessFile(file, "rw");
		bm = new ReadWriteBufferManager(raf.getChannel());
	}

	public void close() throws IOException {
		bm.flush();
		raf.close();
	}

	public void write(DataSource dataSource, IProgressMonitor pm)
			throws IOException, DriverException {
		writeMetadata(dataSource.getRowCount(), dataSource.getMetadata());

		// Write the file building the row indexes in memory
		rowIndexes = new ArrayList<Integer>((int) dataSource.getRowCount());

		currentRow = 0;
		for (int i = 0; i < dataSource.getRowCount(); i++) {
			if (i / 100 == i / 100.0) {
				if (pm.isCancelled()) {
					break;
				} else {
					pm.progressTo((int) (100 * i / dataSource.getRowCount()));
				}
			}

			Value[] row = dataSource.getRow(i);

			addRow(row);
		}

		// write the row indexes
		writeRowIndexes();

		// write envelope
		writeExtent();
	}

	public void writeRowIndexes() throws IOException {
		int rowIndexesDir = previousRowEnd;
		bm.position(previousRowEnd);
		for (int index : rowIndexes) {
			bm.putInt(index);
		}

		// Write row indexes position
		bm.position(rowIndexesDirPos);
		bm.putInt(rowIndexesDir);
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
		int rowHeaderStart = bm.getPosition();
		int rowHeaderSize = metadata.getFieldCount() * 4;
		bm.position(rowHeaderStart + rowHeaderSize);

		// Write the row and keep the field positions in memory
		int[] fieldPositions = new int[metadata.getFieldCount()];
		for (int j = 0; j < metadata.getFieldCount(); j++) {
			fieldPositions[j] = bm.getPosition();
			Value value = row[j];
			byte[] bytes = value.getBytes();
			bm.putInt(bytes.length);
			bm.putInt(value.getType());
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
		for (int fieldPosition : fieldPositions) {
			bm.putInt(fieldPosition);
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
			bm.putInt(type.getTypeCode());
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
		bm.putInt(-1);
	}

	public void writeWritenRowCount() throws IOException {
		bm.position(0);
		// version
		bm.skip(1);
		// dimensions
		bm.putInt(currentRow);
	}
}
