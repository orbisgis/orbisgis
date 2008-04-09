package org.gdms.driver.gdms;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.DriverUtilities;
import org.gdms.driver.FileReadWriteDriver;
import org.gdms.driver.ReadBufferManager;
import org.orbisgis.IProgressMonitor;

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
		throw new UnsupportedOperationException("not implemented yet");
	}

	public void writeFile(File file, DataSource dataSource, IProgressMonitor pm)
			throws DriverException {
		// TODO Auto-generated method stub

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
//			TODO	constraints[j] = ConstraintFactory
//						.createConstraint(type, constraintBytes);
			}
//	TODO		fieldTypes[i] = TypeFactory.createType(typeCode, constraints);
		}
		metadata = new DefaultMetadata();
		for (int i = 0; i < fieldTypes.length; i++) {
			Type type = fieldTypes[i];
//TODO			metadata.addField(fieldNames[i], type.getTypeCode(), type
//					.getConstraints());
		}

		// read field indexes
		rowIndexes = new int[rowCount];
		for (int i = 0; i < rowCount; i++) {
			rowIndexes[i] = rbm.getInt();
		}
	}

	public Metadata getMetadata() throws DriverException {
		return metadata;
	}

	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}

	public TypeDefinition[] getTypesDefinitions() throws DriverException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		// TODO Auto-generated method stub
		return null;
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
