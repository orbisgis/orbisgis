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
package org.gdms.data;

import java.io.File;

import org.gdms.SourceTest;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.orbisgis.utils.FileUtils;

public class DataSourceTest extends SourceTest {

	public void testReadWriteAccessInDataSourceOutOfTransaction()
			throws Exception {
		DataSource ds = dsf.getDataSource(super.getAnyNonSpatialResource());

		try {
			ds.getFieldValue(0, 0);
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.getMetadata();
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.getFieldIndexByName("");
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.getRowCount();
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.getScope(DataSource.X);
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.isNull(0, 0);
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.deleteRow(0);
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.insertEmptyRow();
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.insertEmptyRowAt(0);
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.insertFilledRow(new Value[0]);
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.insertFilledRowAt(0, new Value[0]);
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.isModified();
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.redo();
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.setFieldValue(0, 0, null);
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
		try {
			ds.undo();
			assertTrue(false);
		} catch (ClosedDataSourceException e) {
		}
	}

	public void testSaveDataWithOpenDataSource() throws Exception {
		DataSource ds = dsf.getDataSource(super.getAnyNonSpatialResource());

		ds.open();
		try {
			ds.saveData(null);
		} catch (IllegalStateException e) {
			assertTrue(true);
		}
		ds.close();
	}

	public void testRemovedDataSource() throws Exception {
		String dsName = super.getAnyNonSpatialResource();
		DataSource ds = dsf.getDataSource(dsName);

		ds.open();
		ds.close();
		dsf.getSourceManager().remove(ds.getName());

		try {
			dsf.getDataSource(dsName);
			assertTrue(false);
		} catch (NoSuchTableException e) {
			assertTrue(true);
		}
		ds.open();
		ds.getFieldNames();
		ds.close();
	}

	public void testAlreadyClosed() throws Exception {
		DataSource ds = dsf.getDataSource(super.getAnyNonSpatialResource());

		ds.open();
		ds.close();
		try {
			ds.close();
			assertFalse(true);
		} catch (AlreadyClosedException e) {
			assertTrue(true);
		}
	}

	public void testFailedOpenClosedDataSource() throws Exception {
		File volatileCsv = new File("target/test.csv");
		FileUtils.copy(new File("src/test/resources/test.csv"), volatileCsv);
		DataSource ds = dsf.getDataSource(volatileCsv);
		volatileCsv.delete();
		try {
			ds.open();
			assertTrue(false);
		} catch (DriverException e) {
			assertTrue(!ds.isOpen());
		}
	}

	public void testCommitNonEditableDataSource() throws Exception {
		DataSource ds = dsf.getDataSource(new ObjectDriver() {

			public Number[] getScope(int dimension) throws DriverException {
				return null;
			}

			public long getRowCount() throws DriverException {
				return 0;
			}

			public Value getFieldValue(long rowIndex, int fieldId)
					throws DriverException {
				return null;
			}

			public String getDriverId() {
				return null;
			}

			public void setDataSourceFactory(DataSourceFactory dsf) {
			}

			public Metadata getMetadata() throws DriverException {
				return new DefaultMetadata();
			}

			public void stop() throws DriverException {
			}

			public void start() throws DriverException {
			}

			public int getType() {
				return 0;
			}

			@Override
			public String getTypeDescription() {
				return null;
			}

			@Override
			public String getTypeName() {
				return null;
			}

		});

		ds.open();
		try {
			ds.commit();
			assertFalse(true);
		} catch (NonEditableDataSourceException e) {
		}
		ds.close();
	}

}
