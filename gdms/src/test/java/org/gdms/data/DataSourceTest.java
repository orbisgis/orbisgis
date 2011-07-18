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

import org.gdms.BaseTest;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.ReadAccess;
import org.orbisgis.utils.FileUtils;

public class DataSourceTest extends BaseTest {

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
		DataSource ds = dsf.getDataSource(super.getNonDBSmallResources()[0]);

		ds.open();
		try {
			ds.saveData(null);
		} catch (IllegalStateException e) {
			assertTrue(true);
		}
		ds.close();
	}

	public void testRemovedDataSource() throws Exception {
		String dsName = super.getNonDBSmallResources()[0];
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
		DataSource ds = dsf.getDataSource(super.getNonDBSmallResources()[0]);

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
		FileUtils.copy(new File(internalData + "test.csv"), volatileCsv);
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

            @Override
            public boolean isCommitable() {
                return false;
            }

            @Override
            public TypeDefinition[] getTypesDefinitions() {
                return new TypeDefinition[0];
            }

            @Override
            public String validateMetadata(Metadata metadata) throws DriverException {
                return null;
            }

                        @Override
                        public Schema getSchema() throws DriverException {
                                Schema s = new DefaultSchema("test0");
                                s.addTable("main", new DefaultMetadata());
                                return s;
                        }

                        @Override
                        public ReadAccess getTable(String name) {
                                if (!name.equals("main")) {
                                        return null;
                                }
                                return new ReadAccess() {

                                        @Override
                                        public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
                                                return null;
                                        }

                                        @Override
                                        public long getRowCount() throws DriverException {
                                                return 0;
                                        }

                                        @Override
                                        public Number[] getScope(int dimension) throws DriverException {
                                                return null;
                                        }

                                        @Override
                                        public Metadata getMetadata() throws DriverException {
                                                return new DefaultMetadata();
                                        }
                                };
                        }

		},"main");

		ds.open();
		try {
			ds.commit();
			assertFalse(true);
		} catch (NonEditableDataSourceException e) {
		}
		ds.close();
	}

}
