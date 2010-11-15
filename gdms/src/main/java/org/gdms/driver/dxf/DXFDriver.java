package org.gdms.driver.dxf;

import java.io.File;
import java.io.IOException;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.driver.generic.GenericObjectDriver;
import org.gdms.source.SourceManager;

public class DXFDriver implements FileDriver {

	private GenericObjectDriver result;

	@Override
	public void close() throws DriverException {
		result = null;
	}

	@Override
	public String[] getFileExtensions() {
		return new String[] { "dxf" };
	}

	@Override
	public void open(File file) throws DriverException {
		try {
			DxfFile dxfFile = DxfFile.createFromFile(file);
			result = dxfFile.read();
			System.gc();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Metadata getMetadata() throws DriverException {
		return DxfFile.DXF_SCHEMA;
	}

	@Override
	public int getType() {
		return SourceManager.FILE | SourceManager.VECTORIAL;
	}

	@Override
	public String getTypeDescription() {
		return "DXF format";
	}

	@Override
	public String getTypeName() {
		return "DXF";
	}

	@Override
	public void setDataSourceFactory(DataSourceFactory dsf) {

	}

	@Override
	public String getDriverId() {
		return "DXF driver";
	}

	@Override
	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return result.getFieldValue(rowIndex, fieldId);
	}

	@Override
	public long getRowCount() throws DriverException {
		return result.getRowCount();
	}

	@Override
	public Number[] getScope(int dimension) throws DriverException {
		return result.getScope(dimension);
	}

}
