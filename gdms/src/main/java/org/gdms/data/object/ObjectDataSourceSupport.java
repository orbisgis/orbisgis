package org.gdms.data.object;

import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;

public class ObjectDataSourceSupport {
	private ObjectDriver driver;

	public ObjectDataSourceSupport(ObjectDriver driver) {
		this.driver = driver;
	}

	public Metadata getOriginalMetadata() throws DriverException {
		return new DefaultMetadata(driver.getMetadata());
		// Metadata dmd = driver.getMetadata();
		// boolean[] readOnly = new boolean[dmd.getFieldCount()];
		// for (int i = 0; i < readOnly.length; i++) {
		// readOnly[i] = driver.isReadOnly(i);
		// }
		// return new DefaultMetadata(dmd);
		//		
		// return new DefaultMetadata(dmd, driver, readOnly, driver
		// .getPrimaryKeys());
	}

}
