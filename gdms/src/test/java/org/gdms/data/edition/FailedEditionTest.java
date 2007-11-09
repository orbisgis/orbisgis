/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
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
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.indexes.SpatialIndex;
import org.gdms.data.indexes.SpatialIndexQuery;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;
import org.gdms.spatial.SpatialDataSourceDecorator;

public class FailedEditionTest extends BaseTest {

	private static final String SPATIAL_FIELD_NAME = "geom";

	private DataSourceFactory dsf;

	private void failedCommit(DataSource ds, IndexQuery query)
			throws DriverException, FreeingResourcesException,
			NonEditableDataSourceException {
		ds.deleteRow(2);
		ds.setFieldValue(0, 1, ValueFactory.createValue("nouveau"));
		ds.insertFilledRow(ds.getRow(0));
		Value[][] table = super.getDataSourceContents(ds);
		Iterator<PhysicalDirection> it = ds.queryIndex(query);
		try {
			ReadDriver.failOnWrite = true;
			ds.commit();
		} catch (DriverException e) {
			assertTrue(equals(table, super.getDataSourceContents(ds)));
			if (it != null) {
				assertTrue(ds.queryIndex(query) != null);
			} else {
				assertTrue(ds.queryIndex(query) == null);
			}
			ReadDriver.failOnWrite = false;
			ds.commit();
		}
		ds.open();
		assertTrue(equals(table, super.getDataSourceContents(ds)));
		ds.cancel();

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
		failedCommit(ds, new SpatialIndexQuery(ds.getFullExtent(),
				SPATIAL_FIELD_NAME));
	}

	public void testAlphanumericFileFailOnWrite() throws Exception {
		DataSource ds = dsf.getDataSource("writeFile");
		ds.open();
		failedCommit(ds, new FooQuery());
	}

	private void failedClose(DataSource ds, boolean isFile)
			throws DriverException, DriverLoadException, NoSuchTableException,
			DataSourceCreationException, FreeingResourcesException,
			NonEditableDataSourceException {
		ds.deleteRow(2);
		ds.setFieldValue(0, 1, ValueFactory.createValue("nuevo"));
		Value[][] table = super.getDataSourceContents(ds);
		try {
			ReadDriver.failOnClose = true;
			ds.commit();
		} catch (FreeingResourcesException e) {
			ReadDriver.failOnClose = false;
			assertTrue(true);
			/*
			 * Check if its a file because in that case the contents have been
			 * saved to another temporal location
			 */
			if (!isFile) {
				ds = dsf.getDataSource(ds.getName());
				ds.open();
				assertTrue(equals(table, super.getDataSourceContents(ds)));
				ds.cancel();
			}
		} catch (DriverException e) {
			assertTrue(false);
		}
	}

	public void testAlphanumericFileFailOnClose() throws Exception {
		DataSource ds = dsf.getDataSource("closeFile");
		ds.open();
		failedClose(ds, true);
	}

	private void failedCopy(DataSource ds, boolean isFile) throws Exception {
		ds.deleteRow(2);
		ds.setFieldValue(0, 1, ValueFactory.createValue("nuevo"));
		super.getDataSourceContents(ds);
		try {
			ReadDriver.failOnCopy = true;
			ds.commit();
		} catch (FreeingResourcesException e) {
			assertTrue(true);
		} catch (DriverException e) {
			assertTrue(false);
		}
	}

	public void testAlphanumericFileFailOnCopy() throws Exception {
		DataSource ds = dsf.getDataSource("copyFile");
		ds.open();
		failedCopy(ds, true);
	}

	public void testSpatialFilefailedOnWrite() throws Exception {
		SpatialDataSourceDecorator ds = new SpatialDataSourceDecorator(dsf
				.getDataSource("writeFile"));
		ds.open();
		failedCommit(ds, new SpatialIndexQuery(ds.getFullExtent(),
				SPATIAL_FIELD_NAME));
	}

	public void testSpatialFilefailedOnClose() throws Exception {
		SpatialDataSourceDecorator ds = new SpatialDataSourceDecorator(dsf
				.getDataSource("closeFile"));
		ds.open();
		failedClose(ds, true);
	}

	public void testSpatialFilefailedCopy() throws Exception {
		SpatialDataSourceDecorator ds = new SpatialDataSourceDecorator(dsf
				.getDataSource("copyFile"));
		ds.open();
		failedCopy(ds, true);
	}

	public void testAlphanumericDBFailOnWrite() throws Exception {
		DataSource ds = dsf.getDataSource("executeDB");
		ds.open();
		ReadDriver.setCurrentDataSource(ds);
		failedCommit(ds, new FooQuery());
	}

	public void testAlphanumericDBFailOnClose() throws Exception {
		DataSource ds = dsf.getDataSource("closeDB");
		ds.open();
		ReadDriver.setCurrentDataSource(ds);
		failedClose(ds, false);
	}

	public void testSpatialDBfailedOnWrite() throws Exception {
		SpatialDataSourceDecorator ds = new SpatialDataSourceDecorator(dsf
				.getDataSource("executeDB"));
		ds.open();
		ReadDriver.setCurrentDataSource(ds);
		failedCommit(ds, new SpatialIndexQuery(ds.getFullExtent(),
				SPATIAL_FIELD_NAME));
	}

	public void testSpatialDBfailedOnClose() throws Exception {
		SpatialDataSourceDecorator ds = new SpatialDataSourceDecorator(dsf
				.getDataSource("closeDB"));
		ds.open();
		ReadDriver.setCurrentDataSource(ds);
		failedClose(ds, false);
	}

	@Override
	protected void setUp() throws Exception {
		ReadDriver.initialize();
		ReadDriver.isEditable = true;

		dsf = new DataSourceFactory();
		DriverManager dm = new DriverManager();
		dm.registerDriver("failingdriver", ReadAndWriteDriver.class);

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
				SpatialIndex.SPATIAL_INDEX);
		dsf.getIndexManager().buildIndex("writeFile", SPATIAL_FIELD_NAME,
				SpatialIndex.SPATIAL_INDEX);
		dsf.getIndexManager().buildIndex("executeDB", SPATIAL_FIELD_NAME,
				SpatialIndex.SPATIAL_INDEX);
		dsf.getIndexManager().buildIndex("closeDB", SPATIAL_FIELD_NAME,
				SpatialIndex.SPATIAL_INDEX);
		dsf.getIndexManager().buildIndex("copyFile", SPATIAL_FIELD_NAME,
				SpatialIndex.SPATIAL_INDEX);
		dsf.getIndexManager().buildIndex("closeFile", SPATIAL_FIELD_NAME,
				SpatialIndex.SPATIAL_INDEX);
	}

	private class FooQuery implements IndexQuery {

		public String getFieldName() {
			return "";
		}

		public String getIndexId() {
			return "";
		}

	}
}
