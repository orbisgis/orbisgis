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

/**
 * Some tests about the DriverManager.
 * @author alexis
 */
public class DriverManagerTest extends TestBase {

        @Before
        @Override
        public void setUp() throws Exception {
                super.setUp();
                setWritingTests(true);
        }

        /**
         * To avoid concurrency problems, we must be sure that we use one, and exactly one,
         * driver per FileSourceDefinition. We must not let the DriverManager instanciate
         * many instance of a Driver for a common File.
         */
        @Test
        public void testDriverUnicity() {
                //Our source is a single, simple, ShapeFile
                File shape = new File(TestBase.internalData + "landcover2000.shp");
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
                final DriverManager dm = dsf.getSourceManager().getDriverManager();

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
        }
}
