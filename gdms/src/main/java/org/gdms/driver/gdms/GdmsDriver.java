/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.driver.gdms;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.indexes.btree.ReadWriteBufferManager;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.metadata.MetadataUtilities;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.ByteProvider;
import org.gdms.data.values.RasterValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.DriverUtilities;
import org.gdms.driver.FileReadWriteDriver;
import org.gdms.driver.GDMSModelDriver;
import org.gdms.driver.ReadBufferManager;
import org.gdms.source.SourceManager;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class GdmsDriver extends GDMSModelDriver implements FileReadWriteDriver {

	private static final byte VERSION_NUMBER = 2;
	private ReadBufferManager rbm;
	private int rowCount;
	private int fieldCount;
	private int[] rowIndexes;
	private DefaultMetadata metadata;
	private FileInputStream fis;
	private Envelope fullExtent;
	private HashMap<Point, Value> rasterValueCache = new HashMap<Point, Value>();

	public void copy(File in, File out) throws IOException {
		DriverUtilities.copy(in, out);
	}

	public void createSource(String path, Metadata metadata,
			DataSourceFactory dataSourceFactory) throws DriverException {
		try {
			RandomAccessFile raf = new RandomAccessFile(new File(path), "rw");
			ReadWriteBufferManager bm = new ReadWriteBufferManager(raf
					.getChannel());
			writeMetadata(bm, getEnvelope(null), 0, metadata);
			bm.flush();
			raf.close();
		} catch (IOException e) {
			throw new DriverException("Could not create source: ", e);
		}
	}

	public void writeFile(File file, DataSource dataSource, IProgressMonitor pm)
			throws DriverException {
		try {
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			ReadWriteBufferManager bm = new ReadWriteBufferManager(raf
					.getChannel());
			write(bm, dataSource, pm);
			bm.flush();
			raf.close();
		} catch (IOException e) {
			throw new DriverException(e.getMessage(), e);
		}
	}

	public void close() throws DriverException {
		try {
			fis.close();
			fis = null;
			rbm = null;
		} catch (IOException e) {
			throw new DriverException(e);
		}
	}

	public String completeFileName(String fileName) {
		if (!fileName.toLowerCase().endsWith(".gdms")) {
			return fileName + ".gdms";
		} else {
			return fileName;
		}
	}

	public boolean fileAccepted(File f) {
		return f.getName().toUpperCase().endsWith(".GDMS");
	}

	public void open(File file) throws DriverException {
		try {
			fis = new FileInputStream(file);
			rbm = new ReadBufferManager(fis.getChannel());
			readMetadata();
			rasterValueCache.clear();
		} catch (IOException e) {
			throw new DriverException(e);
		}
	}

	private void write(ReadWriteBufferManager bm, DataSource dataSource, IProgressMonitor pm)
			throws IOException, DriverException {
		Metadata metadata = dataSource.getMetadata();
		Envelope env = getEnvelope(dataSource);
		writeMetadata(bm, env, dataSource.getRowCount(), metadata);

		// Leave space for the row indexes
		int rowIndexesStart = bm.getPosition();
		int rowIndexesSize = (int) (4 * dataSource.getRowCount());
		bm.position(rowIndexesStart + rowIndexesSize);

		// Write the file building the row indexes in memory
		int previousRowEnd = bm.getPosition();
		int[] rowIndexes = new int[(int) dataSource.getRowCount()];
		for (int i = 0; i < dataSource.getRowCount(); i++) {
			if (i / 100 == i / 100.0) {
				if (pm.isCancelled()) {
					break;
				} else {
					pm.progressTo((int) (100 * i / dataSource.getRowCount()));
				}
			}

			rowIndexes[i] = previousRowEnd;
			bm.position(previousRowEnd);
			// Leave space for the row header
			int rowHeaderStart = bm.getPosition();
			int rowHeaderSize = metadata.getFieldCount() * 4;
			bm.position(rowHeaderStart + rowHeaderSize);

			// Write the row and keep the field positions in memory
			int[] fieldPositions = new int[metadata.getFieldCount()];
			for (int j = 0; j < metadata.getFieldCount(); j++) {
				fieldPositions[j] = bm.getPosition();
				Value value = dataSource.getFieldValue(i, j);
				byte[] bytes = value.getBytes();
				bm.putInt(bytes.length);
				bm.putInt(value.getType());
				bm.put(bytes);
			}

			previousRowEnd = bm.getPosition();

			// Write row header
			bm.position(rowHeaderStart);
			for (int fieldPosition : fieldPositions) {
				bm.putInt(fieldPosition);
			}
		}

		// write the row indexes
		bm.position(rowIndexesStart);
		for (int index : rowIndexes) {
			bm.putInt(index);
		}
	}

	private Envelope getEnvelope(DataSource dataSource) throws DriverException {
		if (dataSource != null) {
			if ((MetadataUtilities.isGeometry(dataSource.getMetadata()) || (MetadataUtilities
					.isRaster(dataSource.getMetadata())))) {
				return new SpatialDataSourceDecorator(dataSource)
						.getFullExtent();
			}
		}
		return new Envelope(0, 0, 0, 0);
	}

	private void writeMetadata(ReadWriteBufferManager bm, Envelope fullExtent,
			long rowCount, Metadata metadata) throws IOException,
			DriverException {
		// Write version number
		bm.put(VERSION_NUMBER);

		// write dimensions
		bm.putInt((int) rowCount);
		bm.putInt(metadata.getFieldCount());

		// write extent
		bm.putDouble(fullExtent.getMinX());
		bm.putDouble(fullExtent.getMinY());
		bm.putDouble(fullExtent.getMaxX());
		bm.putDouble(fullExtent.getMaxY());

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
	}

	private void readMetadata() throws IOException {
		// Read version
		byte version = rbm.get();
		if (version != VERSION_NUMBER) {
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

		// read row indexes
		rowIndexes = new int[rowCount];
		for (int i = 0; i < rowCount; i++) {
			rowIndexes[i] = rbm.getInt();
		}
	}

	public Metadata getMetadata() throws DriverException {
		return metadata;
	}

	public int getType() {
		return SourceManager.GDMS | SourceManager.VECTORIAL
				| SourceManager.FILE;
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	public String getName() {
		return "GDMS driver";
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

	public long getRowCount() throws DriverException {
		return rowCount;
	}

	public Number[] getScope(int dimension) throws DriverException {
		if (dimension == X) {
			return new Number[] { fullExtent.getMinX(), fullExtent.getMaxX() };
		} else if (dimension == Y) {
			return new Number[] { fullExtent.getMinY(), fullExtent.getMaxY() };
		} else {
			return null;
		}
	}

	public boolean isCommitable() {
		return true;
	}

	private class RasterByteProvider implements ByteProvider {

		private long rowIndex;
		private int fieldId;

		public RasterByteProvider(long rowIndex, int fieldId) {
			this.rowIndex = rowIndex;
			this.fieldId = fieldId;
		}

		public byte[] getBytes() throws IOException {
			synchronized (GdmsDriver.this) {
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

	public String validateMetadata(Metadata metadata) {
		return null;
	}

}
