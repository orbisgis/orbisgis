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
package org.orbisgis.legend.analyzer;

import java.awt.Color;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.parameter.string.InvalidString;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.analyzer.symbolizers.PointSymbolizerAnalyzer;
import org.orbisgis.legend.structure.viewbox.ConstantViewBox;
import org.orbisgis.legend.thematic.constant.UniqueSymbolPoint;

/**
 *
 * @author Alexis Guéganno
 */
public class PointSymbolizerAnalyzerTest extends AnalyzerTest {
    @Test
    public void testLegendConstructor() throws Exception {
                Style st = getStyle(CONSTANT_POINT);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
                assertTrue(true);
    }

    @Test
    public void testLegendFromAnalyzer() throws Exception {
                Style st = getStyle(CONSTANT_POINT);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                PointSymbolizerAnalyzer psa = new PointSymbolizerAnalyzer(ps);
                assertTrue(psa.getLegend() instanceof UniqueSymbolPoint);

    }

    @Test
    public void testConstantNullFill() throws Exception {
                Style st = getStyle(CONSTANT_POINT);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
                mg.setFill(null);
                UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
                PointSymbolizerAnalyzer psa = new PointSymbolizerAnalyzer(ps);
                assertTrue(psa.getLegend() instanceof UniqueSymbolPoint);
    }

    @Test
    public void testConstantNullStroke() throws Exception {
                Style st = getStyle(CONSTANT_POINT);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
                mg.setStroke(null);
                UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
                PointSymbolizerAnalyzer psa = new PointSymbolizerAnalyzer(ps);
                assertTrue(psa.getLegend() instanceof UniqueSymbolPoint);
    }
    
    /**
     * We sometimes expect an exception to be thrown.
     * @throws Exception 
     */
    @Test
    public void testLegendConstructorFail() throws Exception {
                Style st = getStyle(PROPORTIONAL_POINT);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                try{
                    UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
                    fail();
                } catch(IllegalArgumentException cce){
                    assertTrue(true);
                }
    }

    @Test
    public void testGetWKN() throws Exception {
        Style st = getStyle(CONSTANT_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        assertTrue(uvp.getWellKnownName().equals("Circle"));
    }

    @Test
    public void testSetWKN() throws Exception {
        Style st = getStyle(CONSTANT_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        uvp.setWellKnownName("Star");
        assertTrue(uvp.getWellKnownName().equals("Star"));
        //We must check the PointSymbolizer too !
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        StringLiteral sl = (StringLiteral) mg.getWkn();
        assertTrue(sl.getValue(null).equalsIgnoreCase("star"));
    }

    @Test
    public void testSetWrongWKN() throws Exception {
        Style st = getStyle(CONSTANT_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        uvp.setWellKnownName("Star");
        try{
                uvp.setWellKnownName("hello !");
                fail();
        } catch (InvalidString is){
                assertTrue(uvp.getWellKnownName().equalsIgnoreCase("CIRCLE"));
       }
    }

    @Test
    public void testSetOnVertex() throws Exception{
        Style st = getStyle(CONSTANT_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        assertFalse(uvp.isOnVertex());
        assertFalse(ps.isOnVertex());
        uvp.setOnVertex();
        assertTrue(uvp.isOnVertex());
        assertTrue(ps.isOnVertex());
        uvp.setOnCentroid();
        assertFalse(uvp.isOnVertex());
        assertFalse(ps.isOnVertex());

    }

    @Test
    public void testGetFillColor() throws Exception {
        Style st = getStyle(CONSTANT_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        //Let's check the color that is used for this Symbolizer. It should be something
        //like GRAY50
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        Color c = ((SolidFill)mg.getFill()).getColor().getColor(null, 0);
        assertTrue(c.equals(new Color((int)SolidFill.GRAY50, (int)SolidFill.GRAY50, (int)SolidFill.GRAY50)));
        //We can continue... Let's build the UniqueSymbolPoint
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        assertTrue(uvp.getFillLegend().getColor().equals(new Color((int)SolidFill.GRAY50, (int)SolidFill.GRAY50, (int)SolidFill.GRAY50)));
    }

    @Test
    public void testSetFillColor() throws Exception {
        Style st = getStyle(CONSTANT_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        //Let's check the color that is used for this Symbolizer. It should be something
        //like GRAY50
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        Color c = ((SolidFill)mg.getFill()).getColor().getColor(null, 0);
        assertTrue(c.equals(new Color((int)SolidFill.GRAY50, (int)SolidFill.GRAY50, (int)SolidFill.GRAY50)));
        //We can continue... Let's build the UniqueSymbolPoint
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        uvp.getFillLegend().setColor(new Color(4,4,4));
        assertTrue(uvp.getFillLegend().getColor().equals(new Color(4,4,4)));
    }

    @Test
    public void testGetLineWidth() throws Exception {
        Style st = getStyle(CONSTANT_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        //Let's get the current width
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        double width = ((PenStroke)mg.getStroke()).getWidth().getValue(null, 0);
        assertTrue(width == 1.0);
        //We've checked the width from the symbolizer, let's get it from the Legend
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        assertTrue(uvp.getPenStroke().getLineWidth() == 1.0);
    }

    @Test
    public void testSetLineWidth() throws Exception {
        Style st = getStyle(CONSTANT_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        //Let's get the current width
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        double width = ((PenStroke)mg.getStroke()).getWidth().getValue(null, 0);
        assertTrue(width == 1.0);
        //We've checked the width from the symbolizer, let's get it from the Legend
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        uvp.getPenStroke().setLineWidth(4.0);
        assertTrue(uvp.getPenStroke().getLineWidth() == 4.0);
    }

    @Test
    public void testGetLineColor() throws Exception {
        Style st = getStyle(CONSTANT_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        //Let's get the current width
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        Color col = ((SolidFill)((PenStroke)mg.getStroke()).getFill()).getColor().getColor(null, 0);
        assertTrue(col.equals(Color.BLACK));
        //We've checked the color we searched in the symbolizer, let's get it from the Legend
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        assertTrue(uvp.getPenStroke().getLineColor().equals(Color.BLACK));
    }

    @Test
    public void testSetLineColor() throws Exception {
        Style st = getStyle(CONSTANT_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        //Let's get the current width
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        Color col = ((SolidFill)((PenStroke)mg.getStroke()).getFill()).getColor().getColor(null, 0);
        assertTrue(col.equals(Color.BLACK));
        //We've checked the color we searched in the symbolizer, let's get it from the Legend
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        uvp.getPenStroke().setLineColor(Color.BLUE);
        assertTrue(uvp.getPenStroke().getLineColor().equals(Color.BLUE));
    }

    @Test
    public void testGetLineDash() throws Exception {
        Style st = getStyle(CONSTANT_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        //Let's get the current width
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        Color col = ((SolidFill)((PenStroke)mg.getStroke()).getFill()).getColor().getColor(null, 0);
        assertTrue(col.equals(Color.BLACK));
        //We've checked the color we searched in the symbolizer, let's get it from the Legend
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        assertTrue(uvp.getPenStroke().getDashArray().isEmpty());
    }

    @Test
    public void testSetLineDash() throws Exception {
        Style st = getStyle(CONSTANT_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        //Let's get the current width
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        Color col = ((SolidFill)((PenStroke)mg.getStroke()).getFill()).getColor().getColor(null, 0);
        assertTrue(col.equals(Color.BLACK));
        //We've checked the color we searched in the symbolizer, let's get it from the Legend
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        uvp.getPenStroke().setDashArray("2 2");
        assertTrue(uvp.getPenStroke().getDashArray().equals("2 2"));
    }

    @Test
    public void testGetViewBoxDimensions() throws Exception {
        Style st = getStyle(CONSTANT2D_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        //Let's get the current width
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        assertTrue(mg.getViewBox().getHeight().getValue(null, 0) == 5);
        assertTrue(mg.getViewBox().getWidth().getValue(null, 0) == 5);
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        assertTrue(uvp.getViewBoxHeight() == 5);
        assertTrue(uvp.getViewBoxWidth() == 5);
    }

    @Test
    public void testGetViewBoxOneDimensionOnly() throws Exception {
        Style st = getStyle(CONSTANT_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        //Let's get the current width
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        assertTrue(mg.getViewBox().getHeight() == null);
        assertTrue(mg.getViewBox().getWidth().getValue(null, 0) == 5);
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        assertTrue(uvp.getViewBoxHeight() == null);
        assertTrue(uvp.getViewBoxWidth() == 5);
    }

    @Test
    public void testSetViewBoxDimensions() throws Exception {
        Style st = getStyle(CONSTANT2D_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        //Let's get the current width
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        assertTrue(mg.getViewBox().getHeight().getValue(null, 0) == 5);
        assertTrue(mg.getViewBox().getWidth().getValue(null, 0) == 5);
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        uvp.setViewBoxHeight(8.0);
        uvp.setViewBoxWidth(3.0);
        assertTrue(uvp.getViewBoxHeight() == 8);
        assertTrue(uvp.getViewBoxWidth() == 3);
        assertTrue(mg.getViewBox().getHeight().getValue(null, 0) == 8);
        assertTrue(mg.getViewBox().getWidth().getValue(null, 0) == 3);
        assertTrue(mg.getViewBox().getHeight() == 
                ((ConstantViewBox)uvp.getMarkGraphic().getViewBoxLegend()).getViewBox().getHeight());
        assertTrue(mg.getViewBox().getWidth() ==
                ((ConstantViewBox)uvp.getMarkGraphic().getViewBoxLegend()).getViewBox().getWidth());
    }

    @Test
    public void testSetViewBoxOneDimensionOnly() throws Exception {
        Style st = getStyle(CONSTANT_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        //Let's get the current width
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        assertTrue(mg.getViewBox().getHeight() == null);
        assertTrue(mg.getViewBox().getWidth().getValue(null, 0) == 5);
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        uvp.setViewBoxWidth(15.0);
        assertTrue(uvp.getViewBoxHeight() == null);
        assertTrue(uvp.getViewBoxWidth() == 15.0);
        uvp.setViewBoxHeight(16.0);
        assertTrue(uvp.getViewBoxHeight() == 16.0);
        assertTrue(uvp.getViewBoxWidth() == 15.0);
        uvp.setViewBoxWidth(null);
        assertTrue(uvp.getViewBoxHeight() == 16.0);
        assertTrue(uvp.getViewBoxWidth() == null);
        uvp.setViewBoxWidth(15.0);
        assertTrue(uvp.getViewBoxHeight() == 16.0);
        assertTrue(uvp.getViewBoxWidth() == 15.0);
        uvp.setViewBoxHeight(null);
        assertTrue(uvp.getViewBoxHeight() == null);
        assertTrue(uvp.getViewBoxWidth() == 15.0);
        try{
            uvp.setViewBoxWidth(null);
            fail();
        } catch(IllegalArgumentException iae){
            assertTrue(true);
        }
        uvp.setViewBoxHeight(16.0);
        uvp.setViewBoxWidth(null);
        try{
            uvp.setViewBoxHeight(null);
            fail();
        } catch(IllegalArgumentException iae){
            assertTrue(true);
        }
    }

    @Test
    public void testGetUom() throws Exception {
        Style st = getStyle(CONSTANT_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        assertTrue(uvp.getSymbolUom() == Uom.PX);
        assertTrue(uvp.getStrokeUom() == Uom.PX);
    }

    @Test
    public void testSetUoms() throws Exception {
        Style st = getStyle(CONSTANT_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        UniqueSymbolPoint uvp = new UniqueSymbolPoint(ps);
        assertTrue(uvp.getSymbolUom() == Uom.PX);
        uvp.setSymbolUom(Uom.IN);
        assertTrue(uvp.getSymbolUom() == Uom.IN);
        assertTrue(uvp.getStrokeUom() == Uom.IN);
        uvp.setStrokeUom(Uom.GM);
        assertTrue(uvp.getStrokeUom() == Uom.GM);
    }

}
