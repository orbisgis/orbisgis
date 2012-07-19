/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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

import java.net.URI;
import java.util.HashMap;
import java.util.Locale;
import java.util.SortedSet;
import net.opengis.ows._2.DescriptionType;
import net.opengis.ows._2.KeywordsType;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.orbisgis.core.AbstractTest;
import org.orbisgis.core.renderer.se.Style;

/**
 *
 * @author alexis
 */
public class KeywordsTest extends AbstractTest {

    private String desc = "src/test/resources/org/orbisgis/core/renderer/se/colorRecodeDescription.se";

    public Description getDescription() throws Exception {
        Style fts = new Style(null, desc);
        //We retrieve the Rule we want
        return fts.getRules().get(0).getDescription();
    }

    @Test
    public void testUnmarshall() throws Exception {
        HashMap<URI, Keywords> ks = getDescription().getKeywords();
        assertTrue(ks.size() == 2);
        Keywords k1 = ks.get(null);
        SortedSet<LocalizedText> words = k1.getKeywords();
        assertTrue(words.size() == 2);
        assertTrue(k1.getKeywords(new Locale("en")).size() ==2);
        assertTrue(k1.getType().equals("politesse"));
        Keywords k2 = ks.get(new URI("http://www.orbisgis.org"));
        assertTrue(k2.getKeywords().size()==3);
        assertTrue(k2.getKeywords(new Locale("fr")).size()==2);
        assertTrue(k2.getKeywords(new Locale("en")).size()==0);
        assertTrue(k2.getKeywords(new Locale("de")).size()==1);
        assertTrue(k2.getType().equals("politesse"));
    }

    @Test
    public void testMarshall() throws Exception {
        Description descr = getDescription();
        DescriptionType dt = descr.getJAXBType();
        for(KeywordsType kt : dt.getKeywords()){
            if(kt.getType().getCodeSpace() != null){
                assertTrue(kt.getType().getCodeSpace().equals("http://www.orbisgis.org"));
                assertTrue(kt.getType().getValue().equals("politesse"));
                assertTrue(kt.getKeyword().size() == 3);
            } else {
                assertTrue(kt.getType().getValue().equals("politesse"));
                assertTrue(kt.getKeyword().size() == 2);
            }
        }
    }

    @Test
    public void testPut() throws Exception {
        Description descr = getDescription();
        Keywords kds = new Keywords();
        kds.addLocalizedText(new LocalizedText("oh hai", Locale.UK));
        kds.setType("val");
        descr.putKeywords(new URI("http://www.irstv.fr"), kds);
        assertTrue(descr.getKeywords().size() == 3);
        assertTrue(descr.getKeywords(new URI("http://www.irstv.fr")) == kds);
    }

    @Test
    public void testPutTwice() throws Exception {
        Description descr = getDescription();
        Keywords kds = new Keywords();
        kds.addLocalizedText(new LocalizedText("oh hai", Locale.UK));
        kds.setType("val");
        descr.putKeywords(new URI("http://www.irstv.fr"), kds);
        Keywords kdsb = new Keywords();
        kdsb.addLocalizedText(new LocalizedText("oh hai", Locale.UK));
        kdsb.setType("val");
        descr.putKeywords(new URI("http://www.irstv.fr"), kdsb);
        assertTrue(descr.getKeywords().size() == 3);
        assertTrue(descr.getKeywords(new URI("http://www.irstv.fr")) == kdsb);
    }

    @Test
    public void testPutTwiceNull() throws Exception {
        Description descr = getDescription();
        Keywords kds = new Keywords();
        kds.addLocalizedText(new LocalizedText("oh hai", Locale.UK));
        kds.setType("val");
        descr.putKeywords(null, kds);
        Keywords kdsb = new Keywords();
        kdsb.addLocalizedText(new LocalizedText("oh hai", Locale.UK));
        kdsb.setType("val");
        descr.putKeywords(null, kdsb);
        assertTrue(descr.getKeywords().size() == 2);
        assertTrue(descr.getKeywords(null) == kdsb);
    }

}
