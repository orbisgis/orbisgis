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
package org.gdms.drivers;

import org.gdms.driver.driverManager.DriverManager;
import org.junit.Before;
import java.io.File;
import org.gdms.TestBase;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.Driver;
import org.gdms.driver.driverManager.DriverManagerListener;
import org.gdms.driver.shapefile.ShapefileDriver;
import org.junit.Test;

import static org.junit.Assert.*;

import org.gdms.driver.io.Importer;

/**
 * Some tests about the DriverManager.
 * @author alexis
 */
public class DriverManagerTest extends TestBase {

        @Before
        public void setUp() throws Exception {
                super.setUpTestsWithoutEdition();
        }

        /**
         * To avoid concurrency problems, we must be sure that we use one, and exactly one,
         * driver per FileSourceDefinition. We must not let the DriverManager instanciate
         * many instance of a Driver for a common File.
         */
        @Test
        public void testDriverUnicity() {
                //Our source is a single, simple, ShapeFile
                File shape = super.getAnySpatialResource();
                //Our start point is FileSourceDefinition
                FileSourceDefinition fsd = new FileSourceDefinition(shape, "landcover");
                fsd.setDataSourceFactory(dsf);
                //There we are. We can try to retrieve the driver
                Driver d = fsd.getDriver();
                assertNotNull(d);
                Driver deprime = fsd.getDriver();
                assertTrue(d == deprime);
                //Everything's fine, we've retrieved twice the same reference...
                //Let's create another FileSourceDefinition from the same file
                FileSourceDefinition fsdeprime = new FileSourceDefinition(shape, "landcover");
                fsdeprime.setDataSourceFactory(dsf);
                deprime = fsdeprime.getDriver();
                assertTrue(d == deprime);
        }

        /**
         * Tests basic operations on DriverManagerListeners : add, remove, fired with the right values.
         */
        @Test
        public void testListenerAddedRemoved() {
                final DriverManager dm = sm.getDriverManager();

                TestDML l = new TestDML(dm);

                dm.registerListener(l);
                dm.unregisterDriver(ShapefileDriver.DRIVER_NAME);
                assertTrue(l.fired);
                dm.registerDriver(ShapefileDriver.class);
                assertFalse(l.fired);
                
                assertTrue(dm.unregisterListener(l));
                assertFalse(dm.unregisterListener(l));

        }

        private static class TestDML implements DriverManagerListener {

                private boolean fired = false;
                private DriverManager dm;

                public TestDML(DriverManager dm) {
                        this.dm = dm;
                }
                
                @Override
                public void driverAdded(String driverId, Class<? extends Driver> driverClass) {
                        assertEquals(ShapefileDriver.DRIVER_NAME, driverId);
                        assertEquals(ShapefileDriver.class, driverClass);
                        assertNotNull(dm.getDriverClassByName(ShapefileDriver.DRIVER_NAME));
                        fired = false;
                }

                @Override
                public void driverRemoved(String driverId, Class<? extends Driver> driverClass) {
                        assertEquals(ShapefileDriver.DRIVER_NAME, driverId);
                        assertEquals(ShapefileDriver.class, driverClass);
                        assertNull(dm.getDriverClassByName(ShapefileDriver.DRIVER_NAME));
                        fired = true;
                }

                @Override
                public void importerAdded(String driverId, Class<? extends Importer> importerClass) {
                        
                }

                @Override
                public void importerRemoved(String driverId, Class<? extends Importer> importerClass) {
                        
                }
        }
}
