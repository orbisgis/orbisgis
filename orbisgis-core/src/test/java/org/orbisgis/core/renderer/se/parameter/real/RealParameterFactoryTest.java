/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
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
    public void setUp() throws Exception {
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