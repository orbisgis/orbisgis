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
import net.opengis.ows._2.DescriptionType;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.orbisgis.coremap.renderer.se.Style;
/**
 *
 * @author Alexis Guéganno
 */
public class DescriptionTest {

    private String desc = DescriptionTest.class.getResource("../colorRecodeDescription.se").getFile();

    public Description getDescription() throws Exception {
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
        assertTrue(descr.getTitles().size()==3);
        assertTrue(descr.getTitle(new Locale("en")).equals("Hello"));
        assertTrue(descr.getTitle(new Locale("fr")).equals("Bonjour"));
        assertTrue(descr.getTitle(new Locale("br","FR")).equals("Salud"));
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
        assertTrue(dt.getTitle().size()==3);
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
