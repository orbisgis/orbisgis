/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.coremap.renderer.se.common;

import java.util.Locale;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Alexis Guéganno
 */
public class LocalizedTextComparatorTest {

    private LocalizedText lt1;
    private LocalizedText lt2;
    private LocalizedText lt3;
    private LocalizedText lt4;
    private LocalizedText lt5;
    
    @Before
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
