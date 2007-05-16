package org.gdms.data;

import java.util.ArrayList;
import java.util.List;

import org.gdms.SourceTest;
import org.gdms.data.edition.ReadDriver;
import org.gdms.data.values.BooleanValue;
import org.gdms.data.values.Value;
import org.gdms.spatial.FID;
import org.gdms.spatial.SpatialDataSource;
import org.gdms.spatial.SpatialDataSourceDecorator;

import com.vividsolutions.jts.geom.Envelope;

public class FIDTest extends SourceTest {

	public void testRowFIDMapping() throws Exception {
		SpatialDataSource sds = new SpatialDataSourceDecorator(dsf
				.getDataSource(super.getAnySpatialResource()));

		sds.beginTrans();
		sds.buildIndex("geom2");
		Envelope fe = sds.getFullExtent();
		List list = sds.queryIndex("geom2", fe);
		assertTrue(list.size() == sds.getRowCount());
		ArrayList<Integer> array = new ArrayList<Integer>();
		for (int i = 0; i < list.size(); i++) {
			array.add(new Integer(i));
		}
		for (int i = 0; i < list.size(); i++) {
			FID fid = (FID) list.get(i);
			array.remove(new Integer((int) sds.getRow(fid)));
		}

		assertTrue(array.size() == 0);
	}

	public void testGetByFID() throws Exception {
		SpatialDataSource sds = new SpatialDataSourceDecorator(dsf
				.getDataSource(super.getAnySpatialResource()));

		sds.beginTrans();
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

		sds.beginTrans();
		Value[] row = sds.getRow(1);
		FID fid = sds.getFID(1);
		sds.insertEmptyRowAt(0);
		sds.insertEmptyRowAt(0);
		assertTrue(equals(sds.getRow(3), row));
		assertTrue(equals(sds.getRow(sds.getRow(fid)), row));
	}

	public void testDriverSpecificFID() throws Exception {
		ReadDriver fd = new ReadDriver();
		SpatialDataSource ds = new SpatialDataSourceDecorator(dsf
				.getDataSource(fd));
		ds.beginTrans();
		assertTrue(fd.getFid(0).equals(ds.getFID(0)));
		ds.rollBackTrans();
	}
}
