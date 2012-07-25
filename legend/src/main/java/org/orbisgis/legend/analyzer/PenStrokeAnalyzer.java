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
package org.orbisgis.legend.analyzer;

import org.orbisgis.legend.structure.stroke.constant.ConstantPenStrokeLegend;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.AbstractAnalyzer;
import org.orbisgis.legend.Analyzer;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.analyzer.parameter.RealParameterAnalyzer;
import org.orbisgis.legend.analyzer.parameter.StringParameterAnalyzer;
import org.orbisgis.legend.structure.categorize.Categorize2StringLegend;
import org.orbisgis.legend.structure.fill.constant.ConstantFillLegend;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFillLegend;
import org.orbisgis.legend.structure.interpolation.LinearInterpolationLegend;
import org.orbisgis.legend.structure.literal.RealLiteralLegend;
import org.orbisgis.legend.structure.literal.StringLiteralLegend;
import org.orbisgis.legend.structure.recode.Recode2StringLegend;
import org.orbisgis.legend.structure.stroke.*;

/**
 * This class is dedicated to the detection of thematic analysis made inside the
 * PenStroke elements. It will be particularly useful to locate analysis made on
 * dash patterns, or proportional lines.</p>
 * <p>This class is an example of {@link Analyzer} specialization that could
 * be dependant on more than one {@link LegendStructure} making the analysis of the
 * associated SE object. Indeed, we can find three places where to make an
 * analysis on such a {@code Stroke} : its width, its fill, and the pattern of 
 * its dash array (if set).
 *
 * @author Alexis Guéganno
 */
public class PenStrokeAnalyzer extends AbstractAnalyzer {

        private PenStroke penStroke;

        /**
         * Build  a new PenStrokeAnalyzer. The analysis will be made directly
         * dureing intialization. Consequently, it is meaningful to call {@code
         * getLegend()} directly after initialization.
         * @param ps
         */
        public PenStrokeAnalyzer(PenStroke ps){
                penStroke = ps;
                setLegend(analyzePenStroke());
        }

        /**
         * Analyze a PenStroke. Delegates the analysis to another private method,
         * depending on the presence of a dash array.
         * @return
         */
        private LegendStructure analyzePenStroke(){
                LegendStructure ret;
                //We first make the analysis of the width attribute.
                RealParameter width= penStroke.getWidth();
                RealParameterAnalyzer rpaWidth = new RealParameterAnalyzer(width);
                LegendStructure legdWidth = rpaWidth.getLegend();
                StringParameter dashes = penStroke.getDashArray();
                LegendStructure legdDash;
                Fill fill = penStroke.getFill();
                LegendStructure legdFill = null;
                if(fill != null){
                        FillAnalyzer fa = new FillAnalyzer(fill);
                        legdFill = fa.getLegend();
                }
                //The distinction is made here :
                if(dashes != null){
                        StringParameterAnalyzer spaDash = new StringParameterAnalyzer(dashes);
                        legdDash = spaDash.getLegend();
                        ret = analyzeWithDashes(legdDash, legdWidth, legdFill);

                } else {
                        ret = analyzeConstantDashes(legdWidth, legdFill, null);
                }
                return ret;
        }

        /**
         * We know we have dashes in our PenStroke. We first check if an analysis
         * is made on the dashes array. If not, we search it somewhere else.
         * @param dashes
         * @param width
         * @param fill
         * @return
         */
        private LegendStructure analyzeWithDashes(LegendStructure dashes, LegendStructure width, LegendStructure fill) {
                LegendStructure ret;
                boolean constantFill = fill == null || fill instanceof ConstantSolidFillLegend;
                boolean constantWidth = width == null || width instanceof RealLiteralLegend;
                if(dashes instanceof Categorize2StringLegend){
                        if(constantWidth && constantFill){
                                ret = new CategorizedDashesPSLegend(penStroke, width, fill, dashes);
                        } else {
                                //Too many analysis for us.
                                ret = new PenStrokeLegend(penStroke, width, fill, dashes);
                        }
                } else if (dashes instanceof Recode2StringLegend && constantFill){
                        ret = new RecodedDashesPSLegend(penStroke, width, fill, dashes);
                } else {
                        //We are dealing with a literal
                        ret = analyzeConstantDashes(width, fill, dashes);
                }
                return ret;
        }

        /**
         * We know that we don't have dashes, or that the dash array is constant,
         * in our PenStroke. Let's analyze it now, using just its fill and its width.
         * @param width
         * @param fill
         */
        private LegendStructure analyzeConstantDashes(LegendStructure width, LegendStructure fill, LegendStructure dash){
                LegendStructure ret = null;
                boolean constantFill = fill == null || fill instanceof ConstantSolidFillLegend;
                boolean constantWidth = width == null || width instanceof RealLiteralLegend;
                if (constantFill){
                        if(constantWidth){
                                ret = new ConstantPenStrokeLegend(penStroke, (RealLiteralLegend) width,
                                        (ConstantFillLegend) fill, (StringLiteralLegend) dash);
                        } else if(width instanceof LinearInterpolationLegend){
                                ret = new ProportionalStrokeLegend(penStroke,
                                        (LinearInterpolationLegend)width, fill, dash);
                        } else {
                                ret = new PenStrokeLegend(penStroke, width, fill, dash);
                        }
                } else if(width instanceof RealLiteralLegend){
                        throw new UnsupportedOperationException("We are not able to "
                                + "understand such a configuration yet");
                } else {
                        ret = new PenStrokeLegend(penStroke, width, fill, dash);
                }
                return ret;
        }
        
}
