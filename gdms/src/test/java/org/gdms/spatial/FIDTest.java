package org.gdms.spatial;

import org.gdms.SourceTest;
import org.gdms.data.edition.ReadDriver;
import org.gdms.data.values.BooleanValue;
import org.gdms.data.values.Value;

public class FIDTest extends SourceTest {

	public void testGetByFID() throws Exception {
		SpatialDataSource sds = new SpatialDataSourceDecorator(dsf
				.getDataSource(super.getAnySpatialResource()));

		sds.open();
		Value[] row = sds.getRow(1);
		FID fid = sds.getFID(1);
		Value[] testRow = new Value[row.length];
		for (int i = 0; i < testRow.length; i++) {
			testRow[i] = sds.getFieldValue(fid, i);
		}

		for (int i = 0; i < testRow.length; i++) {
			assertTrue(((BooleanValue) row[i].equals(testRow[i])).getValue());
		}
	}

	public void testInsertRowAt() throws Exception {
		SpatialDataSource sds = new SpatialDataSourceDecorator(dsf
				.getDataSource(super.getAnySpatialResource()));

		sds.open();
		Value[] row = sds.getRow(1);
		FID fid = sds.getFID(1);
		sds.insertEmptyRowAt(0);
		sds.insertEmptyRowAt(0);
		assertTrue(equals(sds.getRow(3), row));
		assertTrue(equals(sds.getRow(sds.getRow(fid)), row));
	}

	public void testDriverSpecificFID() throws Exception {
		ReadDriver.initialize();
		ReadDriver fd = new ReadDriver();
		SpatialDataSource ds = new SpatialDataSourceDecorator(dsf
				.getDataSource(fd));
		ds.open();
		assertTrue(fd.getFid(0).equals(ds.getFID(0)));
		ds.cancel();
	}
}
