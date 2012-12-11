/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.renderer.se.parameter;

import java.io.File;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.string.StringAttribute;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;
import static org.junit.Assert.*;

/**
 *
 * @author Maxence Laurent
 */
public class PropertyNameTest {

    private DataSourceFactory dsf = new DataSourceFactory();
    private DataSource ds;

    // Data to test
    File src = new File("src/test/resources/data/landcover2000.shp");

    @Before
    public void setUp() throws Exception {
        ds = dsf.getDataSource(src);
        ds.open();
    }

    @After
    public void tearDown() throws Exception {
        ds.close();
    }


    /**
     * Test of getValue method, of class StringAttribute.
     * @throws Exception
     */
    @Test
    public void testRealAttribute() throws Exception {
        RealParameter real = new RealAttribute("runoff_win");
        assertTrue(real.getValue(ds, 0) == 0.05);
        assertTrue(real.getValue(ds, 50) == 0.4);
        assertTrue(real.getValue(ds, 1221) == 0.4);
    }

    /**
     * Test of getValue method, of class StringAttribute.
     * @throws Exception
     */
    @Test
    public void testStringAttribute() throws Exception {
        StringParameter string = new StringAttribute("type");

        assertTrue(string.getValue(ds, 56).equals("vegetables"));
        assertTrue(string.getValue(ds, 47).equals("corn"));
        assertTrue(string.getValue(ds, 40).equals("grassland"));
    }
    

}
