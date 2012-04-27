/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.common;

import java.util.Locale;
import net.opengis.ows._2.DescriptionType;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.orbisgis.core.AbstractTest;
import org.orbisgis.core.renderer.se.Style;
/**
 *
 * @author alexis
 */
public class DescriptionTest  extends AbstractTest {

    private String desc = "src/test/resources/org/orbisgis/core/renderer/se/colorRecodeDescription.se";

    public Description getDescription() throws Exception {
        assertTrue(true);
        Style fts = new Style(null, desc);
        //We retrieve the Rule we want
        return fts.getRules().get(0).getDescription();
    }

    /**
     * We just prove that we count right, for now...
     * @throws Exception
     */
    @Test
    public void testDeserialization() throws Exception {
        Description descr = getDescription();
        assertNotNull(descr);
        assertTrue(descr.getTitles().size()==2);
        assertTrue(descr.getTitle(new Locale("en")).equals("Hello"));
        assertTrue(descr.getTitle(new Locale("fr")).equals("Bonjour"));
        assertTrue(descr.getAbstractTexts().size()==3);
        assertTrue(descr.getAbstract(new Locale("en")).equals("I've said hello !"));
        assertTrue(descr.getAbstract(new Locale("fr")).equals("J'ai dit bonjour !"));
        assertTrue(descr.getAbstract(new Locale("de")).equals("Ich habe guten Tag gesagt !"));
        assertTrue(descr.getKeywords().size()==2);
    }

    /**
     * Still just counting.
     * @throws Exception
     */
    @Test
    public void testMarshall() throws Exception {
        Description descr = getDescription();
        DescriptionType dt = descr.getJAXBType();
        assertNotNull(dt);
        assertTrue(dt.getTitle().size()==2);
        assertTrue(dt.getAbstract().size()==3);
        assertTrue(dt.getKeywords().size()==2);
    }

    @Test
    public void testAddTitle() throws Exception {
        Description descr = getDescription();
        assertTrue(descr.getTitle(new Locale("en")).equals("Hello"));
        descr.addTitle(new Locale("en","en"), "Good morning");
        assertTrue(descr.getTitle(new Locale("en","en")).equals("Good morning"));
        assertTrue(descr.getTitle(new Locale("en")).equals("Hello"));
    }

    @Test
    public void testAddAbstract() throws Exception {
        Description descr = getDescription();
        assertTrue(descr.getAbstract(new Locale("en")).equals("I've said hello !"));
        descr.addAbstract(new Locale("en","en"), "Good morning world");
        assertTrue(descr.getAbstract(new Locale("en","en")).equals("Good morning world"));
        assertTrue(descr.getAbstract(new Locale("en")).equals("I've said hello !"));
    }

    @Test
    public void testOverrideTitle() throws Exception {
        Description descr = getDescription();
        assertTrue(descr.getTitle(new Locale("en")).equals("Hello"));
        descr.addTitle(new Locale("en"), "Good morning");
        assertTrue(descr.getTitle(new Locale("en")).equals("Good morning"));
    }

    @Test
    public void testOverrideAbstract() throws Exception {
        Description descr = getDescription();
        assertTrue(descr.getAbstract(new Locale("en")).equals("I've said hello !"));
        descr.addAbstract(new Locale("en"), "Good morning world");
        assertTrue(descr.getAbstract(new Locale("en")).equals("Good morning world"));
    }

}
