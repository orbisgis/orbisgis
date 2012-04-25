/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.data.edition;

import org.junit.Test;
import org.junit.Before;
import java.io.File;
import org.gdms.FileTestSource;

import org.gdms.TestBase;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ListenerCounter;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;

import static org.junit.Assert.*;

public class MetadataTest extends TestBase {

        @Before
        @Override
        public void setUp() throws Exception {
                super.setUp();
                setWritingTests(true);
        }

        @Test
        public void testEqualsMetadata() throws Exception {
                DefaultMetadata metadata1 = new DefaultMetadata();
                metadata1.addField("name", Type.STRING);
                metadata1.addField("surname", Type.STRING);
                metadata1.addField("location", Type.STRING);

                DefaultMetadata metadata2 = new DefaultMetadata();
                metadata2.addField("name", Type.STRING);
                metadata2.addField("gid", Type.INT);

                assertEquals(metadata1, metadata1);
                assertEquals(metadata2, metadata2);
                assertFalse(metadata1.equals(metadata2));
        }

        @Test
        public void testMetadataAddAndRename() throws Exception {
                DefaultMetadata metadata1 = new DefaultMetadata();
                metadata1.addField("name", Type.STRING);
                metadata1.addField("surname", Type.STRING);
                metadata1.addField("location", Type.STRING);

                DefaultMetadata metadata2 = new DefaultMetadata();
                metadata2.addField("name", Type.STRING);
                metadata2.addField("gid", Type.INT);

                metadata1.addAndRenameAll(metadata2);

                assertEquals(metadata1.getFieldIndex("name"), 0);
                assertEquals(metadata1.getFieldIndex("surname"), 1);
                assertEquals(metadata1.getFieldIndex("location"), 2);
                assertEquals(metadata1.getFieldIndex("name_0"), 3);
                assertEquals(metadata1.getFieldIndex("gid"), 4);
        }

        @Test
        public void testFieldIndex() throws Exception {
                DefaultMetadata metadata = new DefaultMetadata();
                metadata.addField("name", Type.STRING);
                metadata.addField("surname", Type.STRING);
                metadata.addField("location", Type.STRING);

                // field index should be 0-based
                assertEquals(metadata.getFieldIndex("surname"), 1);

        }

        @Test
        public void testFieldIndexDataSource() throws Exception {

                DataSource ds = dsf.getDataSource(new File(internalData
                        + "hedgerow.shp"));
                ds.open();
                assertEquals(ds.getMetadata().getFieldIndex(ds.getMetadata().getFieldName(1)), ds.getFieldIndexByName(ds.getFieldName(1)));
                ds.close();
        }

        private void testAddField(String dsName, Type type) throws Exception {
                DataSource d = dsf.getDataSource(dsName, DataSourceFactory.EDITABLE);

                d.open();
                Metadata m = d.getMetadata();
                int fc = m.getFieldCount();
                String name = "toto";
                int i = 0;
                while (m.getFieldIndex(name + "_" + i) != -1) {
                        i++;
                }
//                System.out.println(fc + " fields");
//                System.out.println("adding " + name + "_" + i);
                d.addField(name + "_" + i, type);
                d.commit();
                d.close();
                d.open();
                assertEquals(fc + 1, d.getMetadata().getFieldCount());
//                System.out.println(d.getMetadata().getFieldCount() + " fields");
//                for (int j = 0; j < d.getFieldCount(); j++) {
//                        System.out.println(j + " field " + d.getFieldName(j));
//                }
//                System.out.println(d.getFieldName(fc));
                assertEquals(d.getFieldName(fc), name + "_" + i);
                assertEquals(d.getFieldType(fc).getTypeCode(), Type.STRING);

                assertNull(d.getFieldType(fc).getConstraintValue(Constraint.PK));
                assertNull(d.getFieldType(fc).getConstraintValue(Constraint.READONLY));
                d.close();
        }

        @Test(expected = DriverException.class)
        public void testAddFieldExistsMetadata() throws Exception {
                DefaultMetadata metadata = new DefaultMetadata();

                // Add a String field
                metadata.addField("name", Type.STRING);
                // Add a second string field
                metadata.addField("surname", Type.STRING);
                // Add an existing field name
                metadata.addField("name", Type.STRING);

        }

        @Test
        public void testAddField() throws Exception {
                dsf.getSourceManager().remove("landcover");
                FileTestSource fts = new FileTestSource("landcover",TestBase.internalData+ "landcover2000.shp");
                fts.backup();
                dsf.getSourceManager().remove("landcover");
                dsf.getSourceManager().register(
                        "landcover",
                        new FileSourceCreation(fts.getBackupFile(), null));
                testAddField("landcover", TypeFactory.createType(Type.STRING, "String"));
        }

        private void testDeleteField(String dsName) throws Exception {
                DataSource d = dsf.getDataSource(dsName);

                d.open();
                Metadata m = d.getMetadata();
                int fc = m.getFieldCount();
                d.removeField(1);
                d.commit();
                d.close();
                d.open();
                assertEquals(fc - 1, m.getFieldCount());
                d.close();
        }

        @Test
        public void testDeleteField() throws Exception {
                dsf.getSourceManager().remove("big");
                FileTestSource fts = new FileTestSource("big",TestBase.internalData+ "landcover2000.shp");
                fts.backup();
                dsf.getSourceManager().remove("big");
                dsf.getSourceManager().register(
                        "big",
                        new FileSourceCreation(fts.getBackupFile(), null));
                testDeleteField("big");

        }

        private void testModifyField(String dsName) throws Exception {
                DataSource d = dsf.getDataSource(dsName);

                d.open();
                d.getMetadata();
                d.setFieldName(1, "nuevo");
                d.commit();
                d.close();
                d.open();
                assertEquals(d.getMetadata().getFieldName(1), "nuevo");
                d.close();
        }

        @Test
        public void testModifyField() throws Exception {
                String[] resources = super.getNonDBSmallResources();
                for (String resource : resources) {
                        testModifyField(resource);
                }
        }

        private void testMetadataEditionListenerTest(String dsName, Type type)
                throws Exception {
                DataSource d = dsf.getDataSource(dsName);

                d.open();
                ListenerCounter elc = new ListenerCounter();
                d.addMetadataEditionListener(elc);
                d.removeField(1);
                d.addField("nuevo", type);
                d.setFieldName(1, "jjjj");
                assertEquals(elc.fieldDeletions, 1);
                assertEquals(elc.fieldInsertions, 1);
                assertEquals(elc.fieldModifications, 1);
                assertEquals(elc.total, 3);
                d.close();
        }

        @Test
        public void testMetadataEditionListenerTest() throws Exception {
                dsf.getSourceManager().remove("big");
                dsf.getSourceManager().register(
                        "big",
                        new FileSourceCreation(new File(TestBase.internalData
                        + "hedgerow.shp"), null));
                testMetadataEditionListenerTest("big",
                        TypeFactory.createType(Type.STRING, "STRING"));
        }

        private void testEditionWithFieldAdded(String dsName, Type type)
                throws Exception {
                DataSource d = dsf.getDataSource(dsName, DataSourceFactory.EDITABLE);
                d.open();
                d.addField("extra", type);
                int fi = d.getFieldIndexByName("extra");
                new UndoRedoTests().testAlphanumericEditionUndoRedo(d);
                Value newValue = ValueFactory.createValue("hi");
                d.setFieldValue(0, fi, newValue);
                d.undo();
                d.redo();
                d.commit();
                d.close();
                d.open();
                assertTrue(equals(d.getFieldValue(0, d.getFieldIndexByName("extra")), newValue));
                d.close();
        }

        @Test
        public void testEditionWithFieldAdded() throws Exception {
                testEditionWithFieldAdded(super.getResourcesWithoutNumericField()[0], TypeFactory.createType(Type.STRING, "STRING"));
        }

        private void testEditionWithFieldRemoved(String dsName) throws Exception {
                DataSource d = dsf.getDataSource(dsName, DataSourceFactory.EDITABLE);
                d.open();
                String fieldName = d.getFieldName(1);
                Value testValue = d.getFieldValue(0, 2);
                d.removeField(1);
                assertTrue(equals(testValue, d.getFieldValue(0, 1)));
                new UndoRedoTests().testAlphanumericEditionUndoRedo(d);
                d.commit();
                d.close();

                d.open();
                assertEquals(d.getFieldIndexByName(fieldName), -1);
                d.close();
        }

        @Test
        public void testEditionWithFieldRemoved() throws Exception {
                dsf.getSourceManager().remove("ile");
                FileTestSource fts = new FileTestSource("ile",TestBase.internalData+ "landcover2000.shp");
                fts.backup();
                dsf.getSourceManager().remove("ile");
                dsf.getSourceManager().register(
                        "land",
                        new FileSourceCreation(fts.getBackupFile(), null));
                testEditionWithFieldRemoved("land");
        }

        private void testRemovePK(String dsName) throws Exception {
                DataSource d = dsf.getDataSource(dsName);
                d.open();
                int pkIndex = d.getFieldIndexByName(super.getPKFieldFor(dsName));
                try {
                        d.removeField(pkIndex);
                        fail();
                } catch (DriverException e) {
                }
                try {
                        d.setFieldName(pkIndex, "s1234d");
                        d.commit();
                        d.close();
                        d.open();
                        assertFalse(d.getFieldIndexByName("s1234d") == -1);
                        d.close();
                } catch (DriverException e) {
                }
        }

        @Test
        public void testRemovePK() throws Exception {
                String[] resources = super.getNonDBResourcesWithPK();
                for (String resource : resources) {
                        testRemovePK(resource);
                }

        }

        private void testFieldDeletionEditionWhileEdition(String dsName)
                throws Exception {
                DataSource d = dsf.getDataSource(dsName);
                d.open();
                Value[][] content = super.getDataSourceContents(d);
                d.deleteRow(0);
                d.setFieldValue(0, 2, d.getFieldValue(1, 2));
                d.removeField(1);
                assertTrue(equals(d.getFieldValue(0, 1), content[2][2]));
                d.setFieldValue(0, 0, d.getFieldValue(1, 0));
                assertTrue(equals(d.getFieldValue(0, 0), content[2][0]));
                d.close();
        }

        @Test
        public void testFieldDeletionEditionWhileEdition() throws Exception {
                dsf.getSourceManager().remove("big");
                dsf.getSourceManager().register(
                        "big",
                        new FileSourceCreation(new File(TestBase.internalData
                        + "landcover2000.shp"), null));
                testFieldDeletionEditionWhileEdition("big");
        }

        private void testFieldInsertionEditionWhileEdition(String dsName, Type type)
                throws Exception {
                DataSource d = dsf.getDataSource(dsName);
                d.open();
                String nouveau = "nouveau";
                Value newValue = ValueFactory.createValue(nouveau);
                Value testValue = d.getFieldValue(2, 1);
                int lastField = d.getMetadata().getFieldCount();
                d.deleteRow(0);
                d.setFieldValue(0, 1, d.getFieldValue(1, 1));
                d.addField(nouveau, type);
                d.setFieldValue(0, lastField, newValue);
                assertTrue(equals(d.getFieldValue(0, lastField), newValue));
                d.commit();
                d.close();

                d.open();
                assertEquals(d.getMetadata().getFieldName(lastField).toLowerCase(), nouveau);
                assertTrue(equals(d.getFieldValue(0, lastField), newValue));
                assertTrue(equals(d.getFieldValue(0, 1), testValue));
                d.close();
        }

        @Test
        public void testFieldInsertionEditionWhileEdition() throws Exception {
                testFieldInsertionEditionWhileEdition(super.getNonDBSmallResources()[0],
                        TypeFactory.createType(Type.STRING, "String"));
        }

        private void testTypeInAddField(String dsName) throws Exception {
                DataSource d = dsf.getDataSource(dsName);

                d.open();
                int fc = d.getMetadata().getFieldCount();
                Type type = (d.getDriver()).getTypesDefinitions()[0].createType();
                d.addField("new", type);
                assertEquals(d.getMetadata().getFieldType(fc).getTypeCode(), type.getTypeCode());
                d.commit();
                d.close();

                d = dsf.getDataSource(dsName);
                d.open();
                assertEquals(d.getMetadata().getFieldCount(), fc + 1);
                assertEquals(d.getMetadata().getFieldType(fc).getTypeCode(), type.getTypeCode());
                d.close();
        }

        @Test
        public void testTypeInAddField() throws Exception {
                String[] resources = super.getNonDBSmallResources();
                for (String resource : resources) {
                        testTypeInAddField(resource);
                }
        }
}
