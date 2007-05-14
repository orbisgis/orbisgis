package org.gdms.data.object;

import org.gdms.data.driver.DriverException;
import org.gdms.data.driver.ObjectDriver;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.DriverMetadata;
import org.gdms.data.metadata.Metadata;


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
