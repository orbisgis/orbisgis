/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.common;

import java.util.Locale;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.orbisgis.core.AbstractTest;

/**
 *
 */
public class LocalizedTextTest extends AbstractTest {

        @Test
        public void testLocaleSeparation() {
                String[] parts = LocalizedText.separateLocale("en-");
                assertTrue("Invalid Locale parts size:"+parts.length,
                        parts.length==1);                
                parts = LocalizedText.separateLocale("en");
                assertTrue("Invalid Locale parts size:"+parts.length,
                        parts.length==1);
                parts = LocalizedText.separateLocale("en-US");
                assertTrue("Invalid Locale parts size:"+parts.length,
                        parts.length==2);
                parts = LocalizedText.separateLocale("en-US-x-twain");
                assertTrue("Invalid Locale parts size:"+parts.length,
                        parts.length==3);
        }

        /**
        * Serialisation and de-serialisation of Locale
        * @throws Exception
        */
        @Test
        public void testLocaleDeserialization() throws Exception {
                Locale loc = new Locale("fr","FR");
                String locSer = LocalizedText.toLanguageTag(loc);
                Locale locDeser = LocalizedText.forLanguageTag(locSer);
                assertTrue(locDeser.toString()+" != "+loc.toString(),
                        locDeser.equals(loc));
        }


        /**
        * Serialisation and de-serialisation of Locale
        * @throws Exception
        */
        @Test
        public void testLocaleWithVariantDeserialization() throws Exception {
                Locale loc = new Locale("en","US","x-twain");
                String locSer = LocalizedText.toLanguageTag(loc);
                Locale locDeser = LocalizedText.forLanguageTag(locSer);
                assertTrue(locDeser.toString()+" != "+loc.toString(),
                        locDeser.equals(loc));
        }
}
