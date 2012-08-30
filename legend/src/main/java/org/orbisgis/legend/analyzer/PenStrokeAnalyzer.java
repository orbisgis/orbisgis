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

import java.util.List;
import java.util.Set;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.parameter.SeParameter;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;
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
import org.orbisgis.legend.structure.parameter.NumericLegend;
import org.orbisgis.legend.structure.recode.Recode2StringLegend;
import org.orbisgis.legend.structure.stroke.*;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStrokeLegend;
import org.orbisgis.legend.structure.stroke.constant.NullPenStrokeLegend;

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
        private NumericLegend legdWidth;
        private LegendStructure legdDash;
        private LegendStructure legdFill;

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
                if(penStroke == null){
                        return new NullPenStrokeLegend();
                }
                Set<String> features = penStroke.dependsOnFeature();
                instanciateLegends();
                if(features.size() < 2){
                        //The distinction is made here :
                        if(features.isEmpty()){
                                return analyzeConstantPenStroke();
                        } else {
                                //We have one feature.
                                //We validate the analysis we've found.
                                UsedAnalysis an = penStroke.getUsedAnalysis();
                                List<SeParameter> l = an.getAnalysis();
                                boolean interp = an.isInterpolateUsed();
                                boolean cat = an.isCategorizeUsed();
                                boolean rec = an.isRecodeUsed();
                                if(interp && !cat && !rec && l.size()==1){
                                        return analyzePSWithInterpolate();
                                } else if(!interp && !cat && rec){
                                        //If here, we know we don't have mixed
                                        //analysis : we deal with only one feature,
                                        //one type of analysis, and SeParameter instances
                                        //are validated by analyzers during instanciateLegend().
                                        
                                }
                        }
                }
                if(legdDash == null){
                        return analyzeConstantDashes(legdWidth, legdFill, legdDash);
                } else {
                        return analyzeWithDashes(legdDash, legdWidth, legdFill);
