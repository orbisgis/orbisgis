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

import org.orbisgis.core.renderer.se.parameter.string.Categorize2String;
import org.orbisgis.core.renderer.se.parameter.string.Recode2String;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.analyzer.function.AbstractLiteralValidator;
import org.orbisgis.legend.structure.categorize.Categorize2StringLegend;
import org.orbisgis.legend.structure.literal.StringLiteralLegend;
import org.orbisgis.legend.structure.recode.Recode2StringLegend;

/**
 *
 * @author alexis
 */
public class StringParameterAnalyzer extends AbstractLiteralValidator {

        private StringParameter sp;

        /**
         * Build a new {@code Analyzer} from the given {@code StringParameter}.
         * Note that the thematic analysis extraction is made during
         * initialization.
         * @param sp
         */
        public StringParameterAnalyzer(StringParameter sp) {
                this.sp = sp;
                setLegend(analyzeParameter());
        }

        private LegendStructure analyzeParameter(){
                if(sp instanceof StringLiteral){
                        return new StringLiteralLegend((StringLiteral)sp);
                } else if(sp instanceof Categorize2String){
                        if(validateCategorize((Categorize2String) sp)){
                                return new Categorize2StringLegend((Categorize2String) sp);
                        }
                } else if(sp instanceof Recode2String){
                        if(validateRecode((Recode2String) sp)){
                                return new Recode2StringLegend((Recode2String) sp);
                        }
                } else {
                        throw new UnsupportedOperationException("We've been unable to recognize this StringParameter.");
                }
                throw new UnsupportedOperationException("We've been unable to recognize this StringParameter.");
        }

}
