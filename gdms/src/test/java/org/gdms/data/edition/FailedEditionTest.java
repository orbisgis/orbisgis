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
package org.gdms.data.edition;

import java.util.Iterator;

import org.gdms.BaseTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.indexes.DefaultSpatialIndexQuery;
import org.gdms.data.indexes.IndexManager;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;

public class FailedEditionTest extends BaseTest {

	private static final String SPATIAL_FIELD_NAME = "geom";

	private DataSourceFactory dsf;

	private void failedCommit(DataSource ds, IndexQuery query)
			throws DriverException, NonEditableDataSourceException {
		ds.deleteRow(2);
		ds.setFieldValue(0, 1, ValueFactory.createValue("nouveau"));
		Value[] row = ds.getRow(0);
		row[1] = ValueFactory.createValue("aaaaa");
		ds.insertFilledRow(row);
		Value[][] table = super.getDataSourceContents(ds);
		Iterator<Integer> it = ds.queryIndex(query);
		try {
			ReadDriver.failOnWrite = true;
			ds.commit();
			ds.close();
		} catch (DriverException e) {
			assertTrue(equals(table, super.getDataSourceContents(ds)));
			if (it != null) {
				assertTrue(ds.queryIndex(query) != null);
			} else {
				assertTrue(ds.queryIndex(query) == null);
			}
			ReadDriver.failOnWrite = false;
			ds.commit();
			ds.close();
		}
		ds.open();
		assertTrue(equals(table, super.getDataSourceContents(ds)));
		ds.close();
	}

	public void testAlphanumericObjectfailedCommit() throws Exception {
		DataSource ds = dsf.getDataSource("object");
		ds.open();
		failedCommit(ds, new FooQuery());
	}

	public void testSpatialObjectfailedCommit() throws Exception {
		SpatialDataSourceDecorator ds = new SpatialDataSourceDecorator(dsf
				.getDataSource("object"));
		ds.open();
		failedCommit(ds, new DefaultSpatialIndexQuery(ds.getFullExtent(),
				SPATIAL_FIELD_NAME));
	}

	public void testAlphanumericFileFailOnWrite() throws Exception {
		DataSource ds = dsf.getDataSource("writeFile");
		ds.open();
		failedCommit(ds, new FooQuery());
	}

	public void testAlphanumericDBFailOnWrite() throws Exception {
		DataSource ds = dsf.getDataSource("executeDB");
		ds.open();
		ReadDriver.setCurrentDataSource(ds);
		failedCommit(ds, new FooQuery());
	}

	public void testSpatialDBfailedOnWrite() throws Exception {
		SpatialDataSourceDecorator ds = new SpatialDataSourceDecorator(dsf
				.getDataSource("executeDB"));
		ds.open();
		ReadDriver.setCurrentDataSource(ds);
		failedCommit(ds, new DefaultSpatialIndexQuery(ds.getFullExtent(),
				SPATIAL_FIELD_NAME));
	}

	@Override
	protected void setUp() throws Exception {
		ReadDriver.initialize();
		ReadDriver.isEditable = true;
		ReadDriver.pk = true;

		dsf = new DataSourceFactory();
		dsf.setTempDir("src/test/resources/backup");
		DriverManager dm = new DriverManager();
		dm.registerDriver(ReadAndWriteDriver.class);

		SourceManager sourceManager = dsf.getSourceManager();
		sourceManager.setDriverManager(dm);
		sourceManager.register("object", new ObjectSourceDefinition(
				new ReadAndWriteDriver()));
		sourceManager.register("writeFile", new FakeFileSourceDefinition(
				new ReadAndWriteDriver()));
		sourceManager.register("closeFile", new FakeFileSourceDefinition(
				new ReadAndWriteDriver()));
		sourceManager.register("copyFile", new FakeFileSourceDefinition(
				new ReadAndWriteDriver()));
		sourceManager.register("executeDB", new FakeDBTableSourceDefinition(
				new ReadAndWriteDriver(), "jdbc:executefailing"));
		sourceManager.register("closeDB", new FakeDBTableSourceDefinition(
				new ReadAndWriteDriver(), "jdbc:closefailing"));
		dsf.getIndexManager().buildIndex("object", SPATIAL_FIELD_NAME,
				IndexManager.RTREE_SPATIAL_INDEX, null);
		dsf.getIndexManager().buildIndex("writeFile", SPATIAL_FIELD_NAME,
				IndexManager.RTREE_SPATIAL_INDEX, null);
		dsf.getIndexManager().buildIndex("executeDB", SPATIAL_FIELD_NAME,
				IndexManager.RTREE_SPATIAL_INDEX, null);
		dsf.getIndexManager().buildIndex("closeDB", SPATIAL_FIELD_NAME,
				IndexManager.RTREE_SPATIAL_INDEX, null);
		dsf.getIndexManager().buildIndex("copyFile", SPATIAL_FIELD_NAME,
				IndexManager.RTREE_SPATIAL_INDEX, null);
		dsf.getIndexManager().buildIndex("closeFile", SPATIAL_FIELD_NAME,
				IndexManager.RTREE_SPATIAL_INDEX, null);
	}

	private class FooQuery implements IndexQuery {

		public String getFieldName() {
			return "";
		}

		public String getIndexId() {
			return "";
		}

		public boolean isStrict() {
			return false;
		}

	}
}
