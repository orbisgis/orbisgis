/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.common;

import net.opengis.ows._2.DescriptionType;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.orbisgis.core.AbstractTest;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Style;
/**
 *
 * @author alexis
 */
public class DescriptionTest  extends AbstractTest {

    private String desc = "src/test/resources/org/orbisgis/core/renderer/se/colorRecodeDescription.se";

    /**
     * We just prove that we count right, for now...
     * @throws Exception
     */
    @Test
    public void testDeserialization() throws Exception {
        assertTrue(true);
        Style fts = new Style(null, desc);
        //We retrieve the Rule we want
        Rule r = fts.getRules().get(0);
        Description descr = r.getDescription();
        assertNotNull(descr);
        assertTrue(descr.getTitles().size()==2);
        assertTrue(descr.getAbstractTexts().size()==3);
        assertTrue(descr.getKeywords().size()==2);
    }

    /**
     * Still just counting.
     * @throws Exception
     */
    @Test
    public void testMarshall() throws Exception {
        assertTrue(true);
        Style fts = new Style(null, desc);
        //We retrieve the Rule we want
        Rule r = fts.getRules().get(0);
        Description descr = r.getDescription();
        DescriptionType dt = descr.getJAXBType();
        assertNotNull(dt);
        assertTrue(dt.getTitle().size()==2);
        assertTrue(dt.getAbstract().size()==3);
        assertTrue(dt.getKeywords().size()==2);
    }

}
