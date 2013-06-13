package org.orbisgis.core.renderer.se.common;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.junit.Test;
import org.orbisgis.core.AbstractTest;
import org.orbisgis.core.renderer.se.parameter.string.Recode2String;
import org.orbisgis.core.renderer.se.parameter.string.StringAttribute;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;

import javax.media.jai.PlanarImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Alexis Guéganno
 */
public class VariableOnlineResourceTest extends AbstractTest {

    @Test
    public void testCacheEmptying() throws Exception {
        URL resource = VariableOnlineResourceTest.class.getResource("3.gif");
        StringLiteral lit = new StringLiteral(resource.toString());
        VariableOnlineResource vor = new VariableOnlineResource(lit);
        PlanarImage first = vor.getPlanarJAI(null);
        PlanarImage second = vor.getPlanarJAI(null);
        assertTrue(first == second);
        lit.setValue(VariableOnlineResourceTest.class.getResource("21.gif").toString());
        lit.setValue(VariableOnlineResourceTest.class.getResource("3.gif").toString());
        PlanarImage third = vor.getPlanarJAI(null);
        assertFalse(third == second);
    }

    @Test
    public void testCacheRecode() throws Exception {
        Map<String,Value> input = new HashMap<String,Value>();
        input.put("field", ValueFactory.createValue("s"));
        URL resourceF = VariableOnlineResourceTest.class.getResource("3.gif");
        URL resourceD = VariableOnlineResourceTest.class.getResource("21.gif");
        StringLiteral fb = new StringLiteral(resourceF.toString());
        Recode2String rs = new Recode2String(fb,new StringAttribute("field"));
        rs.addMapItem("s",new StringLiteral(resourceD.toString()));
        VariableOnlineResource vor = new VariableOnlineResource(rs);
        PlanarImage first = vor.getPlanarJAI(input);
        PlanarImage second = vor.getPlanarJAI(input);
        assertTrue(first == second);
        rs.addMapItem("s",new StringLiteral(resourceF.toString()));
        rs.addMapItem("s",new StringLiteral(resourceD.toString()));
        PlanarImage third = vor.getPlanarJAI(input);
        assertFalse(third == second);
        second = vor.getPlanarJAI(input);
        fb.setValue(resourceD.toString());
        third = vor.getPlanarJAI(input);
        assertFalse(second == third);
    }
}
