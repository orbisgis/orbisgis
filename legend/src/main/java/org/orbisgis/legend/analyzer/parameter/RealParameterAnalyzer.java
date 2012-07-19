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
package org.orbisgis.legend.analyzer.parameter;

import org.orbisgis.core.renderer.se.parameter.real.*;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.analyzer.function.AbstractLiteralValidator;
import org.orbisgis.legend.analyzer.function.InterpolationAnalyzer;
import org.orbisgis.legend.structure.categorize.Categorize2RealLegend;
import org.orbisgis.legend.structure.literal.RealLiteralLegend;
import org.orbisgis.legend.structure.recode.Recode2RealLegend;

/**
 * This class analyzes instances of {@code RealParameter} to determine if they
 * are literal values, or richer function.
 * @author alexis
 */
public class RealParameterAnalyzer extends AbstractLiteralValidator{

        private RealParameter rp;

        /**
         * Build a new {@code RealParameterAnalyzer}, using {@code input}.
         * @param input
         */
        public RealParameterAnalyzer(RealParameter input){
                //We want to store the RealParameter for further use...
                rp = input;
                //...and we want to analyze it.
                setLegend(analyzeRealParameter());
        }

        private LegendStructure analyzeRealParameter(){
                if(rp instanceof Interpolate2Real){
                        InterpolationAnalyzer ia = new InterpolationAnalyzer((Interpolate2Real) rp);
                        return ia.getLegend();
                } else if(rp instanceof RealLiteral){
                        return new RealLiteralLegend((RealLiteral) rp);
                } else if(rp instanceof Categorize2Real){
                        Categorize2Real c2r = (Categorize2Real) rp;
                        if(validateCategorize(c2r)){
                                return new Categorize2RealLegend(c2r);
                        } else {
                                throw new UnsupportedOperationException("Not spported yet");
                        }
                } else if(rp instanceof Recode2Real){
                        Recode2Real r2r = (Recode2Real) rp;
                        if(validateRecode(r2r)){
                                return new Recode2RealLegend(r2r);
                        } else {
                                throw new UnsupportedOperationException("Not spported yet");
                        }

                } else {
                        throw new UnsupportedOperationException("Not spported yet");
                }
        }

}
