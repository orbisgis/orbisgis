/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
import org.junit.Before;
import org.junit.Test;
import org.orbisgis.core.AbstractTest;

/**
 *
 * @author Alexis Guéganno
 */
public class LocalizedTextComparatorTest  extends AbstractTest {

    private LocalizedText lt1;
    private LocalizedText lt2;
    private LocalizedText lt3;
    private LocalizedText lt4;
    private LocalizedText lt5;
    private LocalizedText lt6;
    
    @Before
    @Override
    public void setUp(){
        lt1 = new LocalizedText("youhou", new Locale("en"));
        lt2 = new LocalizedText("youhou", new Locale("fr"));
        lt3 = new LocalizedText("hello", new Locale("en"));
        lt4 = new LocalizedText("hello", new Locale("en"));
        lt5 = new LocalizedText("youhou", null);
    }

    @Test
    public void testLocaleAndTextComparator(){
        LocaleAndTextComparator lc = new LocaleAndTextComparator();
        assertTrue(lc.compare(lt1, lt2)==-1);
        assertTrue(lc.compare(lt1, lt3)== 1);
        assertTrue(lc.compare(lt2, lt1)== 1);
        assertTrue(lc.compare(lt2, lt3)== 1);
        assertTrue(lc.compare(lt5, lt3)==-1);
        assertTrue(lc.compare(lt5, lt1)==-1);
        assertTrue(lc.compare(lt1, lt5)== 1);
        assertTrue(lc.compare(lt2, lt5)== 1);
        assertTrue(lc.compare(lt3, lt4)== 0);
        assertTrue(lc.compare(lt3, lt1)==-1);
    }

}
