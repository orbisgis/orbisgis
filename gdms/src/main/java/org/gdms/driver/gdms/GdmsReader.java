package org.gdms.driver.gdms;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.ByteProvider;
import org.gdms.data.values.RasterValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadAccess;
import org.gdms.driver.ReadBufferManager;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class GdmsReader {

	private FileInputStream fis;
	private ReadBufferManager rbm;
	private int rowCount;
	private int fieldCount;
	private Envelope fullExtent;
	private DefaultMetadata metadata;
	private int[] rowIndexes;
	private HashMap<Point, Value> rasterValueCache = new HashMap<Point, Value>();
	private byte version;

	public GdmsReader(File file) throws IOException {
		fis = new FileInputStream(file);
		rbm = new ReadBufferManager(fis.getChannel());
	}

	public void close() throws IOException {
		fis.close();
		fis = null;
		rbm = null;
	}

	public void readMetadata() throws IOException, DriverException {
		// Read version
		version = rbm.get();
		if ((version != 2) && (version != 3)) {
			throw new IOException("Unsupported gdms format version: " + version);
		}

		// read dimensions
		rowCount = rbm.getInt();
		fieldCount = rbm.getInt();

		// read Envelope
		Coordinate min = new Coordinate(rbm.getDouble(), rbm.getDouble());
		Coordinate max = new Coordinate(rbm.getDouble(), rbm.getDouble());
		fullExtent = new Envelope(min, max);

		// read field metadata
		String[] fieldNames = new String[fieldCount];
		Type[] fieldTypes = new Type[fieldCount];
		for (int i = 0; i < fieldCount; i++) {
			// read name
			int nameLength = rbm.getInt();
			byte[] nameBytes = new byte[nameLength];
			rbm.get(nameBytes);
			fieldNames[i] = new String(nameBytes);

			// read type
			int typeCode = rbm.getInt();
			int numConstraints = rbm.getInt();
			Constraint[] constraints = new Constraint[numConstraints];
			for (int j = 0; j < numConstraints; j++) {
				int type = rbm.getInt();
				int size = rbm.getInt();
				byte[] constraintBytes = new byte[size];
				rbm.get(constraintBytes);
				constraints[j] = ConstraintFactory.createConstraint(type,
						constraintBytes);
			}
			fieldTypes[i] = TypeFactory.createType(typeCode, constraints);
		}
		metadata = new DefaultMetadata();
		for (int i = 0; i < fieldTypes.length; i++) {
			Type type = fieldTypes[i];
			metadata.addField(fieldNames[i], type);
		}

		this.rowIndexes = new int[rowCount];
		if (version == 2) {
			// read row indexes after metadata
			for (int i = 0; i < rowCount; i++) {
				this.rowIndexes[i] = rbm.getInt();
			}
		} else if (version == GdmsDriver.VERSION_NUMBER) {
			if (rowCount > 0) {
				// read row indexes at the end of the file
				int rowIndexesDir = rbm.getInt();
				rbm.position(rowIndexesDir);
				for (int i = 0; i < rowCount; i++) {
					this.rowIndexes[i] = rbm.getInt();
				}
			}
		}
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		synchronized (this) {
			int fieldType = metadata.getFieldType(fieldId).getTypeCode();
			if (fieldType == Type.RASTER) {
				Point point = new Point((int) rowIndex, fieldId);
				Value ret = rasterValueCache.get(point);
				if (ret != null) {
					return ret;
				} else {
					try {
						// ignore value size
						moveBufferAndGetSize(rowIndex, fieldId);
						int valueType = rbm.getInt();
						if (valueType == Type.NULL) {
							return ValueFactory.createNullValue();
						} else {
							// Read header
							byte[] valueBytes = new byte[RasterValue.HEADER_SIZE];
							rbm.get(valueBytes);
							Value lazyRasterValue = ValueFactory
									.createLazyValue(fieldType, valueBytes,
											new RasterByteProvider(rowIndex,
													fieldId));
							lazyRasterValue.getAsRaster().open();
							rasterValueCache.put(point, lazyRasterValue);
							return lazyRasterValue;
						}
					} catch (IOException e) {
						throw new DriverException(e.getMessage(), e);
					}
				}
			} else {
				return getFullValue(rowIndex, fieldId);
			}
		}
	}

	private Value getFullValue(long rowIndex, int fieldId)
			throws DriverException {
		try {
			int valueSize = moveBufferAndGetSize(rowIndex, fieldId);
			int valueType = rbm.getInt();
			byte[] valueBytes = new byte[valueSize];
			rbm.get(valueBytes);
			return ValueFactory.createValue(valueType, valueBytes);
		} catch (IOException e) {
			throw new DriverException(e.getMessage(), e);
		}
	}

	private int moveBufferAndGetSize(long rowIndex, int fieldId)
			throws IOException {
		int rowBytePosition = rowIndexes[(int) rowIndex];
		rbm.position(rowBytePosition + 4 * fieldId);
		int fieldBytePosition = rbm.getInt();
		rbm.position(fieldBytePosition);

		// read byte array size
		int valueSize = rbm.getInt();
		return valueSize;
	}

	private class RasterByteProvider implements ByteProvider {

		private long rowIndex;
		private int fieldId;

		public RasterByteProvider(long rowIndex, int fieldId) {
			this.rowIndex = rowIndex;
			this.fieldId = fieldId;
		}

		public byte[] getBytes() throws IOException {
			synchronized (GdmsReader.this) {
				int valueSize = moveBufferAndGetSize(rowIndex, fieldId);
				// Ignore type. If it's null it's not read lazily
				rbm.getInt();
				byte[] valueBytes = new byte[valueSize];
				rbm.get(valueBytes);

				// Restore buffer size
				moveBufferAndGetSize(rowIndex, fieldId);
				rbm.get();

				return valueBytes;
			}
		}
	}

	public Envelope getFullExtent() {
		return fullExtent;
	}

	public long getRowCount() {
		return rowCount;
	}

	public Number[] getScope(int dimension) {
		if (dimension == ReadAccess.X) {
			return new Number[] { getFullExtent().getMinX(),
					getFullExtent().getMaxX() };
		} else if (dimension == ReadAccess.Y) {
			return new Number[] { getFullExtent().getMinY(),
					getFullExtent().getMaxY() };
		} else {
			return null;
		}
	}
}
