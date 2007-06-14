package org.gdms.data.indexes;

import java.util.Iterator;

import org.gdms.SourceTest;
import org.gdms.spatial.SpatialDataSource;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.gdms.sql.strategies.Row;

public class IndexesTest extends SourceTest {

	public void testGetIndexOnOpen() throws Exception {
		String dsName = super.getAnySpatialResource();
		String spatialField = super.getSpatialFieldName(dsName);
		SpatialDataSource ds = new SpatialDataSourceDecorator(dsf
				.getDataSource(dsName));
		ds.open();
		Iterator<Row> it = ds.queryIndex(new SpatialIndexQuery(ds
				.getFullExtent(), spatialField));
		assertTrue(it == null);
		ds.cancel();

		dsf.getIndexManager().buildIndex(dsName, spatialField,
				SpatialIndex.SPATIAL_INDEX);
		ds.open();
		it = ds.queryIndex(new SpatialIndexQuery(ds
				.getFullExtent(), spatialField));
		assertTrue(it != null);
		ds.cancel();
	}

	public void testReplaceBaseIndexOnCommit() throws Exception {
		String dsName = super.getAnySpatialResource();
		String spatialField = super.getSpatialFieldName(dsName);
		dsf.getIndexManager().buildIndex(dsName, spatialField,
				SpatialIndex.SPATIAL_INDEX);
		SpatialDataSource ds = new SpatialDataSourceDecorator(dsf
				.getDataSource(dsName));
		ds.open();
		for (int i = 0; i < ds.getRowCount(); i++) {
			ds.deleteRow(i);
		}
		ds.commit();
		ds.open();
		Iterator<Row> it = ds.queryIndex(new SpatialIndexQuery(ds
				.getFullExtent(), spatialField));
		assertTrue(it.hasNext() == false);
		ds.cancel();
	}
}
