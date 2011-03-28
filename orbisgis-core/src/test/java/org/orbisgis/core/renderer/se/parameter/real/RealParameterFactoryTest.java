/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
package org.orbisgis.core.renderer.se.parameter.real;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
*/
/**
 *
 * @author maxence
 */

/*
public class RealParameterFactoryTest extends TestCase {
    
    public RealParameterFactoryTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

	public void testCreateFromString() {
		try {
			DataSourceFactory dsf = new DataSourceFactory();
			DataSource ds = dsf.getDataSource(new File("../../datas2tests/shp/Swiss/g4districts98_region.shp"));
			System.out.println("createFromString");
			String expression = "Log(<OUI_EEE92>) + <NOV> * 10.0 - .1 + 2e10";
			RealParameter expResult = null;
			RealParameter result = RealParameterFactory.createFromString(expression, ds);
			assertEquals(expResult, result);
		} catch (DriverLoadException ex) {
			Logger.getLogger(RealParameterFactoryTest.class.getName()).log(Level.SEVERE, null, ex);
		} catch (DataSourceCreationException ex) {
			Logger.getLogger(RealParameterFactoryTest.class.getName()).log(Level.SEVERE, null, ex);
		} catch (DriverException ex) {
			Logger.getLogger(RealParameterFactoryTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
*/