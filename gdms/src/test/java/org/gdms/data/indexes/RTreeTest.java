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
import java.io.IOException;

import junit.framework.TestCase;

import org.gdms.BaseTest;
import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.indexes.rtree.DiskRTree;
import org.gdms.data.indexes.rtree.RTree;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.source.SourceManager;

import com.vividsolutions.jts.geom.Envelope;

public class RTreeTest extends TestCase {

	private File indexFile;
	private DataSourceFactory dsf;

	private void checkLookUp(RTree tree, DataSource ds, int fieldIndex)
			throws Exception {
		tree.checkTree();
		assertTrue(tree.size() == tree.getAllValues().length);
		Envelope[] keys = tree.getAllValues();
		for (int i = 0; i < keys.length; i++) {
			int[] indexes = tree.getRow(keys[i]);
			assertTrue(contains(indexes, ds, fieldIndex, keys[i]));
		}

	}

	private boolean contains(int[] indexes, DataSource ds, int fieldIndex,
			Envelope geometry) throws Exception {
		for (int i : indexes) {
			if (ds.getFieldValue(i, fieldIndex).getAsGeometry()
					.getEnvelopeInternal().equals(geometry)) {
				return true;
			}
		}

		return false;
	}

	public void testIndexNGreaterThanBlock() throws Exception {
		testIndexRealData("points", 256, 32, 1000.0);
	}

	public void testIndexPoints() throws Exception {
		testIndexRealData("points", 16, 1024, 1000.0);
	}

	public void testIndexPointsWithSmallN() throws Exception {
		testIndexRealData("points", 3, 32, 1000.0);
	}

	public void testIndexLines() throws Exception {
		testIndexRealData("lines", 16, 1024, 100.0);
	}

	public void testIndexLinesBigN() throws Exception {
		testIndexRealData("lines", 256, 1024, 100.0);
	}

	public void testIndexLinesSmallN() throws Exception {
		testIndexRealData("lines", 3, 1024, 100.0);
	}

	public void testIndexPolygons() throws Exception {
		testIndexRealData("pols", 16, 1024, 500.0);
	}

	public void testIndexPolygonsBigN() throws Exception {
		testIndexRealData("pols", 256, 1024, 500.0);
	}

	public void testIndexPolygonsSmallN() throws Exception {
		testIndexRealData("pols", 3, 1024, 500.0);
	}

	public void testEmptyIndex() throws Exception {
		RTree tree = new DiskRTree(5, 64, false);
		tree.newIndex(indexFile);
		tree.save();
		tree.close();
		tree.openIndex(indexFile);
		assertTrue(tree.size() == 0);
		tree.checkTree();
	}

	public void testIndexWithZeroElements() throws Exception {
		RTree tree = new DiskRTree(5, 64, false);
		tree.newIndex(indexFile);
		testIndexRealData("small", 100.0, tree);
		assertTrue(tree.size() == 0);
		tree.save();
		tree.close();
		tree.openIndex(indexFile);
		assertTrue(tree.size() == 0);
	}

	public void testNotExistentValues() throws Exception {
		RTree tree = new DiskRTree(5, 32, false);
		tree.newIndex(indexFile);
		// populate the index
		DataSource ds = dsf.getDataSource("small");
		ds.open();
		for (int i = 0; i < ds.getRowCount(); i++) {
			tree.insert(ds.getFieldValue(i, 0).getAsGeometry()
					.getEnvelopeInternal(), i);
		}
		String snapshot = tree.toString();
		tree.delete(ds.getFieldValue(0, 0).getAsGeometry()
				.getEnvelopeInternal(), 2359);
		tree.checkTree();
		String snapshot2 = tree.toString();
		assertTrue(snapshot.equals(snapshot2));
		tree.close();
		ds.close();
	}

	private void testIndexRealData(String source, int n, int blockSize,
			double checkPeriod) throws Exception {
		RTree tree = new DiskRTree(n, blockSize, false);
		tree.newIndex(indexFile);
		testIndexRealData(source, checkPeriod, tree);
		tree.close();
	}

	private void testIndexRealData(String source, double checkPeriod, RTree tree)
			throws NoSuchTableException, DataSourceCreationException,
			DriverException, IOException, Exception {
		DataSource ds = dsf.getDataSource(source);
		String fieldName = "the_geom";

		ds.open();
		int fieldIndex = ds.getFieldIndexByName(fieldName);
		long t1 = System.currentTimeMillis();
		for (int i = 0; i < ds.getRowCount(); i++) {
			if (i / (int) checkPeriod == i / checkPeriod) {
				tree.checkTree();
				tree.close();
				tree.openIndex(indexFile);
				tree.checkTree();
				checkLookUp(tree, ds, fieldIndex);
			}
			Envelope value = ds.getFieldValue(i, fieldIndex).getAsGeometry()
					.getEnvelopeInternal();
			tree.insert(value, i);
		}
		long t2 = System.currentTimeMillis();
		System.out.println(((t2 - t1) / 1000.0) + " secs");
		for (int i = 0; i < ds.getRowCount(); i++) {
			if (i / (int) checkPeriod == i / checkPeriod) {
				tree.checkTree();
				tree.save();
				tree.checkTree();
				checkLookUp(tree, ds, fieldIndex);
			}
			Value value = ds.getFieldValue(i, fieldIndex);
			tree.delete(value.getAsGeometry().getEnvelopeInternal(), i);
		}

		ds.close();
	}

	@Override
	protected void setUp() throws Exception {
		indexFile = new File(SourceTest.backupDir, "rtreetest.idx");
		if (indexFile.exists()) {
			if (!indexFile.delete()) {
				throw new IOException("Cannot delete the index file");
			}
		}

		dsf = new DataSourceFactory();
		dsf.setTempDir(SourceTest.backupDir.getAbsolutePath());

		SourceManager sm = dsf.getSourceManager();
		sm.register("points", new File("src/test/resources/points.shp"));
		sm.register("lines", new File(BaseTest.internalData
				+ "hedgerow.shp"));
		sm.register("pols", new File(BaseTest.internalData
				+ "landcover2000.shp"));
		sm.register("small", "select * from pols limit 15;");
	}
}
