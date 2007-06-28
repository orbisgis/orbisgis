package org.gdms.data.indexes;

import java.util.Iterator;

import org.gdms.SourceTest;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.spatial.SpatialDataSourceDecorator;

public class IndexesTest extends SourceTest {

	public void testGetIndexOnOpen() throws Exception {
		String dsName = super.getAnySpatialResource();
		String spatialField = super.getSpatialFieldName(dsName);
		SpatialDataSourceDecorator ds = new SpatialDataSourceDecorator(dsf
				.getDataSource(dsName));
		ds.open();
		Iterator<PhysicalDirection> it = ds.queryIndex(new SpatialIndexQuery(ds
				.getFullExtent(), spatialField));
		assertTrue(it == null);
		ds.cancel();

		dsf.getIndexManager().buildIndex(dsName, spatialField,
				SpatialIndex.SPATIAL_INDEX);
		ds.open();
		it = ds.queryIndex(new SpatialIndexQuery(ds.getFullExtent(),
				spatialField));
		assertTrue(it != null);
		ds.cancel();
	}

	public void testReplaceBaseIndexOnCommit() throws Exception {
		String dsName = super.getAnySpatialResource();
		String spatialField = super.getSpatialFieldName(dsName);
		dsf.getIndexManager().buildIndex(dsName, spatialField,
				SpatialIndex.SPATIAL_INDEX);
		SpatialDataSourceDecorator ds = new SpatialDataSourceDecorator(dsf
				.getDataSource(dsName));
		ds.open();
		for (int i = 0; i < ds.getRowCount(); i++) {
			ds.deleteRow(i);
		}
		ds.commit();
		ds.open();
		Iterator<PhysicalDirection> it = ds.queryIndex(new SpatialIndexQuery(ds
				.getFullExtent(), spatialField));
		assertTrue(it.hasNext() == false);
		ds.cancel();
	}
}
