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
package org.orbisgis.legend.structure.categorize;

import org.junit.Test;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.string.Categorize2String;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.legend.structure.recode.type.TypeEvent;
import org.orbisgis.legend.structure.recode.type.TypeListener;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author alexis
 */
public class CategorizedStringTest {

    /**
     * Let's check we fire the expected event when explicitly setting a new Parameter in our CategorizedString.
     * @throws Exception
     */
    @Test
    public void testFiredTypeEvent() throws Exception{
        RealAttribute vf = new RealAttribute("height");
        Categorize2String c2s = new Categorize2String(new StringLiteral("youhou"),new StringLiteral("fallback"), vf);
        CategorizedString cs = new CategorizedString(c2s);
        DummyTypeListener tl = new DummyTypeListener();
        cs.addListener(tl);
        cs.setParameter(new StringLiteral("literal"));
        assertTrue(tl.count == 1);
        cs.setParameter(c2s);
        assertTrue(tl.count == 2);
    }

    @Test
    public void testGetFallback() throws Exception{
        CategorizedString cs = new CategorizedString(getCategorize2String());
        assertTrue(cs.getFallbackValue().equals("fallback"));
        cs = new CategorizedString(new StringLiteral("yo"));
        assertTrue(cs.getFallbackValue().equals("yo"));
    }

    @Test
    public void testSetFallback() throws Exception{
        CategorizedString cs = new CategorizedString(getCategorize2String());
        cs.setFallbackValue("fall");
        assertTrue(cs.getFallbackValue().equals("fall"));
        assertTrue(((Categorize2String)cs.getParameter()).getFallbackValue().getValue(null).equals("fall"));
        cs = new CategorizedString(new StringLiteral("yo"));
        cs.setFallbackValue("oy");
        assertTrue(cs.getFallbackValue().equals("oy"));
        assertTrue(((StringLiteral)cs.getParameter()).getValue(null).equals("oy"));
    }

    @Test
    public void testGetFromCategorize(){
        CategorizedString cs = new CategorizedString(getCategorize2String());
        assertTrue(cs.get(20.0).equals("Greater"));
        assertTrue(cs.get(Double.NEGATIVE_INFINITY).equals("youhou"));
        assertNull(cs.get(25.0));
        assertTrue(cs.get(30.0).equals("EvenGreater"));
        cs = new CategorizedString(new StringLiteral("yo"));
        assertNull(cs.get(25.0));
        assertTrue(cs.get(Double.NEGATIVE_INFINITY).equals("yo"));
    }

    @Test
    public void testPutInfLiteral() throws Exception{
        CategorizedString cs = new CategorizedString(new StringLiteral("yo"));
        //The only mapping in cs is -INF -> yo
        cs.put(Double.NEGATIVE_INFINITY, "potato");
        assertTrue(cs.get(Double.NEGATIVE_INFINITY).equals("potato"));
        assertTrue(cs.getParameter() instanceof StringLiteral);
        assertTrue(((StringLiteral)cs.getParameter()).getValue(null).equals("potato"));
    }

    @Test
    public void testPutNotInfLiteral() throws Exception{
        CategorizedString cs = new CategorizedString(new StringLiteral("yo"));
        cs.setField("hi");
        //The only mapping in cs is -INF -> yo
        cs.put(8.0, "potato");
        assertTrue(cs.get(Double.NEGATIVE_INFINITY).equals("yo"));
        assertTrue(cs.get(8.0).equals("potato"));
        assertTrue(cs.getParameter() instanceof Categorize2String);
    }

    @Test
    public void testPutInfCategorize()throws Exception {
        CategorizedString cs = new CategorizedString(getCategorize2String());
        cs.put(Double.NEGATIVE_INFINITY, "potato");
        assertTrue(cs.get(Double.NEGATIVE_INFINITY).equals("potato"));
        assertTrue(cs.get(20.0).equals("Greater"));
        assertTrue(cs.get(30.0).equals("EvenGreater"));
    }

    @Test
    public void testPutNotInfCategorize()throws Exception {
        CategorizedString cs = new CategorizedString(getCategorize2String());
        cs.put(20.0, "potato");
        assertTrue(cs.get(Double.NEGATIVE_INFINITY).equals("youhou"));
        assertTrue(cs.get(20.0).equals("potato"));
        assertTrue(cs.get(30.0).equals("EvenGreater"));
    }

    @Test
    public void testPutNewMappingCategorize()throws Exception {
        CategorizedString cs = new CategorizedString(getCategorize2String());
        cs.put(25.0, "potato");
        assertTrue(cs.get(Double.NEGATIVE_INFINITY).equals("youhou"));
        assertTrue(cs.get(20.0).equals("Greater"));
        assertTrue(cs.get(25.0).equals("potato"));
        assertTrue(cs.get(30.0).equals("EvenGreater"));
    }

    @Test
    public void testRemoveFromLiteral(){
        CategorizedString cs = new CategorizedString(new StringLiteral("yo"));
        assertNull(cs.remove(50.0));
        assertNull(cs.remove(Double.NEGATIVE_INFINITY));
    }

    @Test
    public void testRemoveFromCat(){
        CategorizedString cs = new CategorizedString(getCategorize2String());
        assertTrue(cs.remove(20.0).equals("Greater"));
        assertNull(cs.remove(20.0));
    }

    @Test
    public void testRemoveInfFromCat(){
        CategorizedString cs = new CategorizedString(getCategorize2String());
        assertTrue(cs.remove(Double.NEGATIVE_INFINITY).equals("youhou"));
        assertTrue(cs.get(Double.NEGATIVE_INFINITY).equals("Greater"));
    }

    /**
     * Here the fallback value and -INF mapping are different, we don't get a literal in the end.
     */
    @Test
    public void testRemoveAll1(){
        CategorizedString cs = new CategorizedString(getCategorize2String());
        cs.remove(20.0);
        cs.remove(30.0);
        assertNull(cs.remove(Double.NEGATIVE_INFINITY));
        assertTrue(cs.getParameter() instanceof Categorize2String);
    }

    /**
     * Here the fallback value and -INF mapping are equal, we get a literal in the end.
     */
    @Test
    public void testRemoveAll2(){
        CategorizedString cs = new CategorizedString(getCategorize2String());
        cs.setFallbackValue(cs.get(Double.NEGATIVE_INFINITY));
        cs.remove(20.0);
        cs.remove(30.0);
        assertTrue(cs.getParameter() instanceof StringLiteral);
    }

    @Test
    public void testVisit(){
        CategorizedString cs = new CategorizedString(getCategorize2String());
        cs.acceptVisitor(new Dummyvisitor());
        assertTrue(cs.getField().equals(Dummyvisitor.FIELD));
    }


    /**
     * A visitor that sets the field. Dummy.
     */
    public class Dummyvisitor implements CategorizedParameterVisitor {

        public static final String FIELD = "My Beautiful Pony field";

        @Override
        public void visit(CategorizedLegend legend) {
            legend.setField(FIELD);
        }
    }

    public class DummyTypeListener implements TypeListener {
        public int count = 0;
        @Override
        public void typeChanged(TypeEvent te) {
            count++;
        }
    }

    private  Categorize2String getCategorize2String(){
        RealAttribute vf = new RealAttribute("height");
        Categorize2String c2s = new Categorize2String(new StringLiteral("youhou"),new StringLiteral("fallback"), vf);
        c2s.put(new RealLiteral(20),new StringLiteral("Greater"));
        c2s.put(new RealLiteral(30), new StringLiteral("EvenGreater"));
        return c2s;
    }

}
