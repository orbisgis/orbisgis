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
