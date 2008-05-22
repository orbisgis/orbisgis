package org.gdms.driver.gdms;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.indexes.btree.ReadWriteBufferManager;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.DriverUtilities;
import org.gdms.driver.FileReadWriteDriver;
import org.gdms.driver.ReadBufferManager;
import org.gdms.source.SourceManager;
import org.orbisgis.progress.IProgressMonitor;

public class GdmsDriver implements FileReadWriteDriver {

	private ReadBufferManager rbm;
	private int rowCount;
	private int fieldCount;
	private int[] rowIndexes;
	private DefaultMetadata metadata;
	private FileInputStream fis;

	public void copy(File in, File out) throws IOException {
		DriverUtilities.copy(in, out);
	}

	public void createSource(String path, Metadata metadata,
			DataSourceFactory dataSourceFactory) throws DriverException {
		try {
			RandomAccessFile raf = new RandomAccessFile(new File(path), "rw");
			ReadWriteBufferManager bm = new ReadWriteBufferManager(raf
					.getChannel());
			writeMetadata(bm, 0, metadata);
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
			write(bm, dataSource);
			bm.flush();
			raf.close();
		} catch (IOException e) {
			throw new DriverException(e.getMessage(), e);
		}
	}

	public void close() throws DriverException {
		try {
			fis.close();
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
		} catch (IOException e) {
			throw new DriverException(e);
		}
	}

	private void write(ReadWriteBufferManager bm, DataSource dataSource)
			throws IOException, DriverException {
		Metadata metadata = dataSource.getMetadata();
		writeMetadata(bm, dataSource.getRowCount(), metadata);

		// Leave space for the row indexes
		int rowIndexesStart = bm.getPosition();
		int rowIndexesSize = (int) (4 * dataSource.getRowCount());
		bm.position(rowIndexesStart + rowIndexesSize);

		// Write the file building the row indexes in memory
		int previousRowEnd = bm.getPosition();
		int[] rowIndexes = new int[(int) dataSource.getRowCount()];
		for (int i = 0; i < dataSource.getRowCount(); i++) {
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

	private void writeMetadata(ReadWriteBufferManager bm, long rowCount,
			Metadata metadata) throws IOException, DriverException {
		// write dimensions
		bm.putInt((int) rowCount);
		bm.putInt(metadata.getFieldCount());

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
		// read dimensions
		rowCount = rbm.getInt();
		fieldCount = rbm.getInt();

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

	public TypeDefinition[] getTypesDefinitions() throws DriverException {
		throw new UnsupportedOperationException();
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	public String getName() {
		return "GDMS driver";
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		try {
			// go to field position
			int rowBytePosition = rowIndexes[(int) rowIndex];
			rbm.position(rowBytePosition + 4 * fieldId);
			int fieldBytePosition = rbm.getInt();
			rbm.position(fieldBytePosition);

			// read byte array size
			int valueSize = rbm.getInt();
			byte[] valueBytes = new byte[valueSize];
			rbm.get(valueBytes);

			// return value
			return ValueFactory.createValue(metadata.getFieldType(fieldId)
					.getTypeCode(), valueBytes);
		} catch (IOException e) {
			throw new DriverException(e.getMessage(), e);
		}
	}

	public long getRowCount() throws DriverException {
		return rowCount;
	}

	public Number[] getScope(int dimension) throws DriverException {
		return null;
	}

	public boolean isCommitable() {
		return true;
	}

}
