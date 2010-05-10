/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.parameter;

import java.io.File;
import junit.framework.TestCase;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
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
    private DataSource ds;

    // Data to test
    File src = new File("../../datas2tests/shp/Swiss/g4districts98_region.shp");



    public PropertyNameTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ds = dsf.getDataSource(src);
        ds.open();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        ds.close();
    }


    /**
     * Test of getValue method, of class StringAttribute.
     * @throws Exception
     */
    public void testRealAttribute() throws Exception {
        long n = ds.getRowCount();

        RealParameter name = new RealAttribute("DEC01", ds);

        long i;
        for (i=0;i<n;i++){
            System.out.println ("DEC01 " + i + ": " + name.getValue(ds, i));
        }
    }

    /**
     * Test of getValue method, of class StringAttribute.
     * @throws Exception
     */
    public void testStringAttribute() throws Exception {
        long n = ds.getRowCount();

        StringParameter name = new StringAttribute("NAME_ANSI", ds);

        long i;
        for (i=0;i<n;i++){
            System.out.println ("Name " + i + ": " + name.getValue(ds, i));
        }

        ((StringAttribute)name).setColumnName("AK", ds);
        for (i=0;i<n;i++){
            System.out.println ("AK " + i + ": " + name.getValue(ds, i));
        }
    }
    

}
