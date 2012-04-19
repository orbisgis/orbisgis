/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.analyzer;

import org.orbisgis.legend.structure.recode.Recode2StringLegend;
import org.orbisgis.legend.structure.stroke.RecodedDashesPSLegend;
import org.orbisgis.legend.structure.categorize.Categorize2StringLegend;
import org.orbisgis.legend.structure.stroke.CategorizedDashesPSLegend;
import org.orbisgis.legend.structure.interpolation.LinearInterpolationLegend;
import org.orbisgis.legend.structure.stroke.PenStrokeLegend;
import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.Style;
import javax.xml.bind.JAXBElement;
import java.io.File;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBContext;
import net.opengis.se._2_0.core.StyleType;
import org.junit.Test;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.structure.stroke.ProportionalStrokeLegend;
import static org.junit.Assert.*;
/**
 * test that we're able to understand what people are doing with PenStrokes.
 * @author alexis
 */
public class PenStrokeAnalyzerTest extends AnalyzerTest {

        @Test
        public void testProportionalStroke() throws Exception {
                //We have some XML where a proportional line is defined :
                Style st = getStyle("src/test/resources/org/orbisgis/legend/linearProportional.se");
                //I want to work on the PenStroke.
                 LineSymbolizer ls =(LineSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                 PenStroke ps = (PenStroke) (ls.getStroke());
                 PenStrokeAnalyzer psa = new PenStrokeAnalyzer(ps);
                 PenStrokeLegend psl = (PenStrokeLegend) (psa.getLegend());
                 assertTrue(psl.getWidthAnalysis() instanceof LinearInterpolationLegend);
                 assertTrue(psl instanceof ProportionalStrokeLegend);
        }

        @Test
        public void testCategorizedDashArray() throws Exception {
                //We have some XML where a categorized dash array is defined :
                Style st = getStyle("src/test/resources/org/orbisgis/legend/dashClassif.se");
                //I want to work on the PenStroke.
                 LineSymbolizer ls =(LineSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                 PenStroke ps = (PenStroke) (ls.getStroke());
                 PenStrokeAnalyzer psa = new PenStrokeAnalyzer(ps);
                 PenStrokeLegend psl = (PenStrokeLegend) (psa.getLegend());
                 assertTrue(psl.getDashAnalysis() instanceof Categorize2StringLegend);
                 assertTrue(psl instanceof CategorizedDashesPSLegend);
        }

        @Test
        public void testRecodedDashArray() throws Exception {
                //We have some XML where a recoded dash array is defined :
                Style st = getStyle("src/test/resources/org/orbisgis/legend/dashRecode.se");
                //I want to work on the PenStroke.
                 LineSymbolizer ls =(LineSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                 PenStroke ps = (PenStroke) (ls.getStroke());
                 PenStrokeAnalyzer psa = new PenStrokeAnalyzer(ps);
                 PenStrokeLegend psl = (PenStrokeLegend) (psa.getLegend());
                 assertTrue(psl.getDashAnalysis() instanceof Recode2StringLegend);
                 assertTrue(psl instanceof RecodedDashesPSLegend);
        }

        @Test
        public void testTooComplexPenStroke() throws Exception {
                //We have some XML where the pen stroke is too complex to be analyzed :
                Style st = getStyle("src/test/resources/org/orbisgis/legend/complexPenStroke.se");
                //I want to work on the PenStroke.
                 LineSymbolizer ls =(LineSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                 PenStroke ps = (PenStroke) (ls.getStroke());
                 PenStrokeAnalyzer psa = new PenStrokeAnalyzer(ps);
                 PenStrokeLegend psl = (PenStrokeLegend) (psa.getLegend());
                 assertTrue(psl.getDashAnalysis() instanceof Categorize2StringLegend);
                 assertTrue(psl.getWidthAnalysis() instanceof LinearInterpolationLegend);
                 assertFalse(psl instanceof RecodedDashesPSLegend);
                 assertFalse(psl instanceof CategorizedDashesPSLegend);
                 assertFalse(psl instanceof ProportionalStrokeLegend);
                 assertTrue(psl instanceof PenStrokeLegend);
        }

}
