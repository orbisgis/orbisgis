package org.gdms.data.file;

import java.io.File;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.OpenCloseCounter;
import org.gdms.data.edition.Field;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.DriverMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;


public class FileDataSourceSupport {
    private FileDriver driver;
    private File file;

    public FileDataSourceSupport(DataSource ds, File file, FileDriver driver) {
        this.file = file;
        this.driver = driver;
    }

    public FileDriver getDriver() {
        return driver;
    }

    public File getFile() {
        return file;
    }

    public DriverMetadata getDriverMetadata() throws DriverException {
        return driver.getDriverMetadata();
    }

    public String check(Field field, Value value) throws DriverException {
        return driver.check(field, value);
    }

    public Metadata getOriginalMetadata() throws DriverException {
        DriverMetadata dmd = getDriverMetadata();
        boolean[] readOnly = new boolean[dmd.getFieldCount()];
        for (int i = 0; i < readOnly.length; i++) {
            readOnly[i] = driver.isReadOnly(i);
        }

        return new DefaultMetadata(dmd, getDriver(), readOnly, new String[0]);
    }
}
