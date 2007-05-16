package org.gdms.data.object;

import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.DriverMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;


public class ObjectDataSourceSupport {
    private ObjectDriver driver;

    public ObjectDataSourceSupport(ObjectDriver driver) {
        this.driver = driver;
    }

    public Metadata getOriginalMetadata() throws DriverException {
        DriverMetadata dmd = driver.getDriverMetadata();
        boolean[] readOnly = new boolean[dmd.getFieldCount()];
        for (int i = 0; i < readOnly.length; i++) {
            readOnly[i] = driver.isReadOnly(i);
        }

        return new DefaultMetadata(dmd, driver, readOnly, driver.getPrimaryKeys());
    }

}
