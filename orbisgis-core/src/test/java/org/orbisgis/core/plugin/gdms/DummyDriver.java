package org.orbisgis.core.plugin.gdms;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.TypeDefinition;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileReadWriteDriver;
import org.orbisgis.progress.ProgressMonitor;

import java.io.File;
import java.io.IOException;

/**
 * Dummy class for unit test
 * @author Nicolas Fortin
 */
public class DummyDriver implements FileReadWriteDriver {

    @Override
    public void copy(File in, File out) throws IOException {

    }

    @Override
    public void writeFile(File file, DataSet dataSource, ProgressMonitor pm) throws DriverException {

    }

    @Override
    public void createSource(String path, Metadata metadata, DataSourceFactory dataSourceFactory) throws DriverException {

    }

    @Override
    public void open() throws DriverException {

    }

    @Override
    public void close() throws DriverException {

    }

    @Override
    public String[] getFileExtensions() {
        return new String[] {"utest"};
    }

    @Override
    public void setFile(File file) throws DriverException {

    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public Schema getSchema() throws DriverException {
        return null;
    }

    @Override
    public DataSet getTable(String name) {
        return null;
    }

    @Override
    public void setDataSourceFactory(DataSourceFactory dsf) {

    }

    @Override
    public int getSupportedType() {
        return 0;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public String getTypeName() {
        return "utest";
    }

    @Override
    public String getTypeDescription() {
        return "nothing";
    }

    @Override
    public String getDriverId() {
        return "unittest";
    }

    @Override
    public boolean isCommitable() {
        return false;
    }

    @Override
    public TypeDefinition[] getTypesDefinitions() {
        return new TypeDefinition[0];
    }

    @Override
    public String validateMetadata(Metadata metadata) throws DriverException {
        return "";
    }
}
