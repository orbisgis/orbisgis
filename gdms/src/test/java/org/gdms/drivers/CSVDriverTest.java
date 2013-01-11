/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
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
package org.gdms.drivers;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.gdms.TestBase;
import org.gdms.data.DataSource;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.ValueFactory;

public class CSVDriverTest extends TestBase {

        private File file;
        
        @Before
        public void setUp() throws Exception {
                super.setUpTestsWithEdition(false);
                File file1 = File.createTempFile("csvdrivertest", ".csv");
                file1.delete();
                file1.deleteOnExit();
                file = file1;

                DefaultMetadata metadata = new DefaultMetadata();
                metadata.addField("f1", Type.STRING);
                metadata.addField("f2", Type.STRING);
                FileSourceCreation fsc = new FileSourceCreation(file, metadata);
                dsf.createDataSource(fsc);
        }
        
        @After
        public void after() {
                file.delete();
        }

        @Test
        public void testScapeSemiColon() throws Exception {
                DataSource ds = dsf.getDataSource(file);
                ds.open();
                ds.insertEmptyRow();
                ds.setFieldValue(0, 0, ValueFactory.createValue("a;b"));
                ds.setFieldValue(0, 1, ValueFactory.createValue("c\\d"));
                ds.commit();
                ds.close();

                ds.open();
                assertEquals("a;b", ds.getString(0, 0));
                assertEquals("c\\d", ds.getString(0, 1));
                ds.close();
        }

        @Test
        public void testNullValues() throws Exception {
                DataSource ds = dsf.getDataSource(file);
                ds.open();
                ds.insertEmptyRow();
                ds.setFieldValue(0, 0, ValueFactory.createNullValue());
                ds.setFieldValue(0, 1, ValueFactory.createNullValue());
                ds.commit();
                ds.close();

                ds.open();
                assertTrue(ds.isNull(0, 0));
                assertTrue(ds.isNull(0, 1));
                ds.close();
        }
}
