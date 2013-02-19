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
package org.orbisgis.legend.structure.recode;

import org.junit.Assert;
import org.junit.Test;
import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.thematic.recode.RecodedLine;

import javax.swing.*;
import java.awt.*;

import static org.junit.Assert.assertTrue;

/**
 * Tests for the visitor pattern implemented upon RecodedLegend instances.
 * @author alexis
 */
public class VisitorTest extends AnalyzerTest{

    /**
     * Tests that RecodedLegend implementations behave as expected with a basic visitor.
     * @throws Exception
     */
    @Test
    public void testBasicVisitor() throws Exception {
        RecodedColor rc = new RecodedColor(new ColorLiteral(Color.WHITE));
        BasicVis bv = new BasicVis();
        rc.acceptVisitor(bv);
        Assert.assertEquals(bv.getI(),1);
        RecodedReal rr = new RecodedReal(new RealLiteral());
        rr.acceptVisitor(bv);
        Assert.assertEquals(bv.getI(),2);
        RecodedString rs = new RecodedString(new StringLiteral());
        rs.acceptVisitor(bv);
        Assert.assertEquals(bv.getI(),3);
    }

    @Test
    public void testSetNameVisitor() throws Exception {
        RecodedColor rc = getRecodedColor();
        assertTrue(rc.getLookupFieldName().equals("PREC_ALTI"));
        String str ="youhou";
        SetFieldVisitor sfv = new SetFieldVisitor(str);
        rc.acceptVisitor(sfv);
        assertTrue(rc.getLookupFieldName().equals(str));
    }

    @Test
    public void testGatherField() throws Exception {
        RecodedColor rc = getRecodedColor();
        String name = "PREC_ALTI";
        assertTrue(rc.getLookupFieldName().equals(name));
        FieldAggregatorVisitor fav = new FieldAggregatorVisitor();
        fav.visit(rc);
        assertTrue(fav.getFields().contains(name));
        RecodedColor rc2 = getRecodedColor();
        String name2 = "youhou";
        rc2.setField(name2);
        fav.visit(rc2);
        assertTrue(fav.getFields().contains(name));
        assertTrue(fav.getFields().contains(name2));
        assertTrue(fav.getFields().size() == 2);
        RecodedColor rc3= getRecodedColor();
        rc3.setField(null);
        fav.visit(rc3);
        assertTrue(fav.getFields().contains(name));
        assertTrue(fav.getFields().contains(name2));
        assertTrue(fav.getFields().size() == 2);
    }

    private RecodedColor getRecodedColor() throws Exception {
        Style s = getStyle(COLOR_RECODE);
        LineSymbolizer ls = (LineSymbolizer) s.getRules().get(0).getCompositeSymbolizer().getChildren().get(0);
        RecodedLine rl = new RecodedLine(ls);
        return rl.getLineColor();
    }

    private static class BasicVis implements RecodedParameterVisitor {
        private int i=0;
        @Override
        public void visit(RecodedLegend legend) {
            i++;
        }
        public int getI(){return i;}

    }
}
