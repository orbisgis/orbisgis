package org.orbisgis.core.rasterDrivers;

import java.io.File;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.source.SourceManager;

public class XYZDEMDriver implements FileDriver {

	public String getName() {
		return "xyzDEM driver";
	}

	public int getType() {
		return SourceManager.XYZDEM;
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	public void close() throws DriverException {
	}

	public String completeFileName(String fileName) {
		if (!fileName.toLowerCase().endsWith(".xyz")) {
			return fileName + ".xyz";
		} else {
			return fileName;
		}
	}

	public boolean fileAccepted(File f) {
		return f.getName().toUpperCase().endsWith(".XYZ");
	}

	public void open(File file) throws DriverException {
	}

	public Metadata getMetadata() throws DriverException {
		return new DefaultMetadata();
	}

	public TypeDefinition[] getTypesDefinitions() throws DriverException {
		return null;
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return null;
	}

	public long getRowCount() throws DriverException {
		return 0;
	}

	public Number[] getScope(int dimension) throws DriverException {
		return null;
	}

}
