/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.parameter;

import java.io.File;
import junit.framework.TestCase;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.string.StringAttribute;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;

/**
 *
 * @author maxence
 */
public class PropertyNameTest extends TestCase {

    private DataSourceFactory dsf = new DataSourceFactory();
    private SpatialDataSourceDecorator sds;
    private DataSource ds;

    // Data to test
    File src = new File("src/test/resources/data/landcover2000.shp");


    public PropertyNameTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ds = dsf.getDataSource(src);
        sds = new SpatialDataSourceDecorator(ds);
        sds.open();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        sds.close();
    }


    /**
     * Test of getValue method, of class StringAttribute.
     * @throws Exception
     */
    public void testRealAttribute() throws Exception {
        RealParameter real = new RealAttribute("runoff_win");
        assertTrue(real.getValue(sds, 0) == 0.05);
        assertTrue(real.getValue(sds, 50) == 0.4);
        assertTrue(real.getValue(sds, 1221) == 0.4);
    }

    /**
     * Test of getValue method, of class StringAttribute.
     * @throws Exception
     */
    public void testStringAttribute() throws Exception {
        StringParameter string = new StringAttribute("type");

        assertTrue(string.getValue(sds, 56).equals("vegetables"));
        assertTrue(string.getValue(sds, 47).equals("corn"));
        assertTrue(string.getValue(sds, 40).equals("grassland"));
    }
    

}
