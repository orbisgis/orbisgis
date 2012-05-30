/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * @author maxence
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
