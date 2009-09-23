/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.data.indexes;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;

import junit.framework.TestCase;

import org.gdms.BaseTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.source.Source;
import org.gdms.source.SourceManager;
import org.gdms.sql.strategies.FullIterator;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.utils.FileUtils;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class IndexesTest extends TestCase {

	private DataSourceFactory dsf;
	private SourceManager sm;
	private IndexManager im;

	public void testIndexPersistence() throws Exception {
		im.buildIndex("source", "gid", IndexManager.BTREE_ALPHANUMERIC_INDEX,
				null);
		sm.saveStatus();
		instantiateDSF();
		DataSource ds = dsf.getDataSource("source");
		ds.open();
		ds.queryIndex(new DefaultAlphaQuery("gid", null, true, ValueFactory
				.createValue(10), false));
		ds.close();
	}

	public void testRemoveIndexFilesOnIndexRemoval() throws Exception {
		im.buildIndex("source", "gid", IndexManager.BTREE_ALPHANUMERIC_INDEX,
				null);
		sm.saveStatus();
		instantiateDSF();
		Source src = sm.getSource("source");
		String propertyName = IndexManager.INDEX_PROPERTY_PREFIX + "-gid-"
				+ IndexManager.BTREE_ALPHANUMERIC_INDEX;
		File indexFile = src.getFileProperty(propertyName);
		im.deleteIndex("source", "gid");

		DataSource ds = dsf.getDataSource("source");
		ds.open();
		DefaultAlphaQuery DefaultAlphaQuery = new DefaultAlphaQuery("gid",
				null, true, null, false);
		Iterator<Integer> queryResult = ds.queryIndex(DefaultAlphaQuery);
		assertTrue(queryResult instanceof FullIterator);
		ds.close();
		assertTrue(!indexFile.exists());
	}

	public void testCreateIndexTwice() throws Exception {
		int numFiles1 = sm.getSourceInfoDirectory().listFiles().length;
		im.buildIndex("source", "gid", IndexManager.BTREE_ALPHANUMERIC_INDEX,
				null);
		int numFiles2 = sm.getSourceInfoDirectory().listFiles().length;
		assertTrue(numFiles2 - 1 == numFiles1);
		try {
			im.buildIndex("source", "gid",
					IndexManager.BTREE_ALPHANUMERIC_INDEX, null);
			assertTrue(false);
		} catch (IndexException e) {
		}
		int numFiles3 = sm.getSourceInfoDirectory().listFiles().length;
		assertTrue(numFiles2 == numFiles3);
		sm.saveStatus();
		int numFiles4 = sm.getSourceInfoDirectory().listFiles().length;
		assertTrue(numFiles3 == numFiles4);
	}

	public void testCreateIndexOnWrongField() throws Exception {
		int numFiles1 = sm.getSourceInfoDirectory().listFiles().length;
		try {
			im.buildIndex("source", "the_geom",
					IndexManager.BTREE_ALPHANUMERIC_INDEX, null);
			assertTrue(false);
		} catch (IndexException e) {
		}
		int numFiles2 = sm.getSourceInfoDirectory().listFiles().length;
		assertTrue(numFiles2 == numFiles1);
		sm.saveStatus();
		int numFiles3 = sm.getSourceInfoDirectory().listFiles().length;
		assertTrue(numFiles3 == numFiles2);

	}

	public void testCancelIndexCreation() throws Exception {
		int numFiles1 = sm.getSourceInfoDirectory().listFiles().length;
		im.buildIndex("source", "the_geom",
				IndexManager.BTREE_ALPHANUMERIC_INDEX,
				new NullProgressMonitor() {

					@Override
					public boolean isCancelled() {
						return true;
					}

				});
		int numFiles2 = sm.getSourceInfoDirectory().listFiles().length;
		assertTrue(numFiles2 == numFiles1);
		sm.saveStatus();
		int numFiles3 = sm.getSourceInfoDirectory().listFiles().length;
		assertTrue(numFiles3 == numFiles2);

	}

	public void testAlreadyInstantiatedDataSourceUsesIndex() throws Exception {
		DataSource ds = dsf.getDataSource("source");
		ds.open();
		im.buildIndex("source", "gid", IndexManager.BTREE_ALPHANUMERIC_INDEX,
				null);
		DefaultAlphaQuery DefaultAlphaQuery = new DefaultAlphaQuery("gid",
				null, true, ValueFactory.createValue(10), false);
		Iterator<Integer> it = ds.queryIndex(DefaultAlphaQuery);
		int count = getCount(it);
		im.deleteIndex("source", "gid");
		it = ds.queryIndex(DefaultAlphaQuery);
		assertTrue(it instanceof FullIterator);
		ds.close();
		assertTrue(count == 9);
	}

	private int getCount(Iterator<Integer> it) {
		int count = 0;
		while (it.hasNext()) {
			it.next();
			count++;
		}
		return count;
	}

	public void testDeleteNonExistingIndex() throws Exception {
		try {
			im.deleteIndex("source", "gid");
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
	}

	public void testEditIndexedDataSource() throws Exception {
		im.buildIndex("source", "gid", IndexManager.BTREE_ALPHANUMERIC_INDEX,
				null);
		DefaultAlphaQuery DefaultAlphaQuery = new DefaultAlphaQuery("gid",
				null, true, ValueFactory.createValue(10), false);
		DataSource ds1 = dsf.getDataSource("source");
		DataSource ds2 = dsf.getDataSource("source");
		ds1.open();
		ds2.open();
		ds2.deleteRow(0);
		int countOriginal = getCount(ds1.queryIndex(DefaultAlphaQuery));
		int countEdited = getCount(ds2.queryIndex(DefaultAlphaQuery));
		ds1.close();
		ds2.close();

		assertTrue(countOriginal - 1 == countEdited);
	}

	public void testAlphaDeletion() throws Exception {
		im.buildIndex("source", "gid", IndexManager.BTREE_ALPHANUMERIC_INDEX,
				null);

		DefaultAlphaQuery DefaultAlphaQuery = new DefaultAlphaQuery("gid",
				null, false, null, false);
		testDeletion(DefaultAlphaQuery);
	}

	public void testSpatialDeletion() throws Exception {
		im.buildIndex("source", "the_geom", IndexManager.RTREE_SPATIAL_INDEX,
				null);

		DataSource ds = dsf.getDataSource("source");
		ds.open();
		Envelope env = new SpatialDataSourceDecorator(ds).getFullExtent();
		ds.close();
		SpatialIndexQuery spatialQuery = new DefaultSpatialIndexQuery(env,
				"the_geom");
		testDeletion(spatialQuery);
	}

	private void testDeletion(IndexQuery query) throws NoSuchTableException,
			DataSourceCreationException, DriverException {
		DataSource ds = dsf.getDataSource("source");
		ds.open();
		for (int i = 0; i < ds.getRowCount();) {
			ds.deleteRow(0);
		}
		Iterator<Integer> it = ds.queryIndex(query);
		int count = getCount(it);
		assertTrue(count == 0);
		ds.close();
	}

	public void testAlphaInsertionAtTheBeginning() throws Exception {
		im.buildIndex("source", "gid", IndexManager.BTREE_ALPHANUMERIC_INDEX,
				null);
		IndexQuery DefaultAlphaQuery = new DefaultAlphaQuery("gid", null,
				false, null, false);
		testInsertionAtTheBeginning(DefaultAlphaQuery);
	}

	public void testSpatialInsertionAtTheBeginning() throws Exception {
		im.buildIndex("source", "the_geom", IndexManager.RTREE_SPATIAL_INDEX,
				null);

		DataSource ds = dsf.getDataSource("source");
		ds.open();
		Envelope env = new SpatialDataSourceDecorator(ds).getFullExtent();
		ds.close();
		SpatialIndexQuery spatialQuery = new DefaultSpatialIndexQuery(env,
				"the_geom");
		testInsertionAtTheBeginning(spatialQuery);
	}

	private void testInsertionAtTheBeginning(IndexQuery DefaultAlphaQuery)
			throws NoSuchTableException, DataSourceCreationException,
			DriverException {
		DataSource ds = dsf.getDataSource("source");
		ds.open();
		ds.insertFilledRowAt(0, ds.getRow(2));
		ds.insertEmptyRowAt(0);
		Iterator<Integer> it = ds.queryIndex(DefaultAlphaQuery);
		// There is no element at row 0. There is no repeated element
		HashSet<Integer> repeated = new HashSet<Integer>();
		while (it.hasNext()) {
			int row = it.next();
			assertTrue(Integer.toString(row), !repeated.contains(row));
			repeated.add(row);
			assertTrue(row > 0);
		}
		ds.close();
	}

	public void testReplaceBaseIndexOnCommit() throws Exception {
		im.buildIndex("source", "gid", IndexManager.BTREE_ALPHANUMERIC_INDEX,
				null);
		im.buildIndex("source", "the_geom", IndexManager.RTREE_SPATIAL_INDEX,
				null);
		DataSource ds = dsf.getDataSource("source");
		ds.open();
		for (int i = 0; i < ds.getRowCount();) {
			ds.deleteRow(0);
		}
		ds.commit();
		checkReplacedIndex(ds);
		ds.close();
		checkReplacedIndex(ds);
		instantiateDSF();
		checkReplacedIndex(dsf.getDataSource("source"));
	}

	private void checkReplacedIndex(DataSource ds) throws DriverException {
		ds.open();
		DefaultAlphaQuery DefaultAlphaQuery = new DefaultAlphaQuery("gid",
				null, false, null, false);
		SpatialIndexQuery spatialQuery = new DefaultSpatialIndexQuery(
				new SpatialDataSourceDecorator(ds).getFullExtent(), "the_geom");
		Iterator<Integer> it = ds.queryIndex(DefaultAlphaQuery);
		int count = getCount(it);
		assertTrue(count == 0);
		it = ds.queryIndex(spatialQuery);
		count = getCount(it);
		assertTrue(count == 0);
		ds.close();
	}

	public void testDataSourceInEditionLosesIndex() throws Exception {
		im.buildIndex("source", "gid", IndexManager.BTREE_ALPHANUMERIC_INDEX,
				null);
		im.buildIndex("source", "the_geom", IndexManager.RTREE_SPATIAL_INDEX,
				null);
		IndexQuery DefaultAlphaQuery = new DefaultAlphaQuery("gid",
				ValueFactory.createValue(Integer.MAX_VALUE), false,
				ValueFactory.createValue(Integer.MIN_VALUE), false);
		DataSource ds = dsf.getDataSource("source");
		ds.open();
		Geometry geom = ds.getFieldValue(1, 0).getAsGeometry();
		ds.deleteRow(0);
		Iterator<Integer> it = ds.queryIndex(DefaultAlphaQuery);
		int count = getCount(it);
		assertTrue(count == 0);
		im.deleteIndex("source", "gid");
		// Check the alpha index is gone
		it = ds.queryIndex(DefaultAlphaQuery);
		count = getCount(it);
		assertTrue(count == ds.getRowCount());
		// Check the spatial index remains
		SpatialIndexQuery spatialQuery = new DefaultSpatialIndexQuery(geom
				.getEnvelopeInternal(), "the_geom");
		it = ds.queryIndex(spatialQuery);
		assertTrue(contains(ds, it, geom));
		ds.close();
	}

	private boolean contains(DataSource ds, Iterator<Integer> it, Geometry geom)
			throws IncompatibleTypesException, DriverException {
		while (it.hasNext()) {
			int row = it.next();
			Geometry geom2 = ds.getFieldValue(row, 0).getAsGeometry();
			if (geom2.equals(geom)) {
				return true;
			}
		}

		return false;
	}

	public void testCreateIndexOnEditedSource() throws Exception {
		DataSource ds = dsf.getDataSource("source");
		ds.open();
		ds.setInt(0, "gid", 999999);
		im.buildIndex("source", "gid", IndexManager.BTREE_ALPHANUMERIC_INDEX,
				null);
		IndexQuery DefaultAlphaQuery = new DefaultAlphaQuery("gid",
				ValueFactory.createValue(999999));
		Iterator<Integer> it = ds.queryIndex(DefaultAlphaQuery);
		assertTrue(it.next() == 0);
		assertTrue(!it.hasNext());
		DataSource ds2 = dsf.getDataSource("source");
		ds2.open();
		it = ds2.queryIndex(DefaultAlphaQuery);
		assertTrue(!it.hasNext());
		ds2.close();
		ds.close();
	}

	public void testIndexedEditionAfterCommit() throws Exception {
		DataSource ds = dsf.getDataSource("source");
		ds.open();
		ds.deleteRow(0);
		ds.commit();
		ds.deleteRow(0);
		im.buildIndex("source", "gid", IndexManager.BTREE_ALPHANUMERIC_INDEX,
				null);
		DefaultAlphaQuery tenQuery = new DefaultAlphaQuery("gid", null, true,
				ValueFactory.createValue(10), true);
		int indexResultCount = getCount(ds.queryIndex(tenQuery));
		assertTrue(indexResultCount <= 10);
		ds.commit();
		ds.close();
	}

	public void testSyncWithIndexedSource() throws Exception {
		DefaultAlphaQuery tenQuery = new DefaultAlphaQuery("gid", null, true,
				ValueFactory.createValue(10), true);
		DataSource ds = dsf.getDataSource("source");
		ds.open();
		int indexResultCount = getCount(ds.queryIndex(tenQuery));
		im.buildIndex("source", "gid", IndexManager.BTREE_ALPHANUMERIC_INDEX,
				null);
		int count2 = getCount(ds.queryIndex(tenQuery));
		assertTrue(count2 < indexResultCount);
		ds.deleteRow(0);
		ds.syncWithSource();
		int countReverted = getCount(ds.queryIndex(tenQuery));
		assertTrue(countReverted < indexResultCount);
		ds.close();
	}

	public void testCreateIndexRevertAndModify() throws Exception {
		DataSource ds = dsf.getDataSource("source");
		ds.open();
		im.buildIndex("source", "the_geom", IndexManager.RTREE_SPATIAL_INDEX,
				null);
		ds.setFieldValue(3, 0, ds.getFieldValue(1, 0));
		ds.syncWithSource();
		ds.setFieldValue(3, 0, ds.getFieldValue(1, 0));

		SpatialIndexQuery spatialQuery = new DefaultSpatialIndexQuery(
				new SpatialDataSourceDecorator(ds).getFullExtent(), "the_geom");
		assertTrue(getCount(ds.queryIndex(spatialQuery)) == ds.getRowCount());
		ds.close();
	}

	@Override
	protected void setUp() throws Exception {
		instantiateDSF();

		sm.removeAll();
		File parent = new File(BaseTest.internalData);
		File backup = new File("src/test/resources/backup");
		File destshp = new File(backup, "hedgerow.shp");
		File destdbf = new File(backup, "hedgerow.dbf");
		File destshx = new File(backup, "hedgerow.shx");
		destshp.delete();
		destdbf.delete();
		destshx.delete();
		FileUtils.copy(new File(parent, "hedgerow.shp"), destshp);
		FileUtils.copy(new File(parent, "hedgerow.dbf"), destdbf);
		FileUtils.copy(new File(parent, "hedgerow.shx"), destshx);
		sm.register("source", destshp);
	}

	private void instantiateDSF() {
		dsf = new DataSourceFactory();
		dsf.setTempDir("src/test/resources/backup");
		sm = dsf.getSourceManager();
		im = dsf.getIndexManager();
	}

}
