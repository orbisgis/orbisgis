package org.gdms.drivers;

import org.gdms.SourceTest;
import org.gdms.TestData;
import org.gdms.data.DataSource;

public class DriversTest extends SourceTest {

	private void testFormat(String dsName) throws Exception {
		DataSource sds = dsf.getDataSource(dsName);
		sds.open();
		for (int i = 0; i < sds.getRowCount(); i++) {
			for (int j = 0; j < sds.getMetadata().getFieldCount(); j++) {
				sds.getFieldValue(i, j);
			}
		}
		sds.cancel();
	}

	public void testReadFully() throws Exception {
		String[] resources = super.getResourcesOfFormat(TestData.SHAPEFILE
				| TestData.DBF);
		for (String resource : resources) {
			testFormat(resource);
		}
	}

	@Override
	protected void setUp() throws Exception {
		setWritingTests(false);
		super.setUp();
	}

}
