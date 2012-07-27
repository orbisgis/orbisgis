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

import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.legend.AbstractAnalyzer;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.analyzer.parameter.ColorParameterAnalyzer;
import org.orbisgis.legend.analyzer.parameter.RealParameterAnalyzer;
import org.orbisgis.legend.structure.categorize.Categorize2ColorLegend;
import org.orbisgis.legend.structure.fill.CategorizedSolidFillLegend;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFillLegend;
import org.orbisgis.legend.structure.fill.RecodedSolidFillLegend;
import org.orbisgis.legend.structure.fill.constant.NullSolidFillLegend;
import org.orbisgis.legend.structure.literal.ColorLiteralLegend;
import org.orbisgis.legend.structure.literal.RealLiteralLegend;
import org.orbisgis.legend.structure.recode.Recode2ColorLegend;

/**
 * Used to make analysis and produce {@code LegendStructure} on instances of {@code Fill}.
 * @author Alexis Guéganno
 */
public class FillAnalyzer extends AbstractAnalyzer{

        /**
         * Build a new {@code FillAnalyzer}, and directly analyses the {@code
         * Fill} given in argument.
         * @param fill
         */
        public FillAnalyzer(Fill fill) {
                setLegend(analyzeFill(fill));
        }

        private LegendStructure analyzeFill(Fill f){
                if(f== null || f instanceof SolidFill){
                        return analyzeSolidFill((SolidFill) f);
                }
                throw new UnsupportedOperationException("We can't do such an anlysis "
                        + "on Fill instances yet");

        }

        private LegendStructure analyzeSolidFill(SolidFill sf){
                if(sf == null){
                        return new NullSolidFillLegend();
                }
                ColorParameterAnalyzer colorPA = new ColorParameterAnalyzer(sf.getColor());
                LegendStructure colorLegend = colorPA.getLegend();
                RealParameterAnalyzer rpa = new RealParameterAnalyzer(sf.getOpacity());
                LegendStructure ls = rpa.getLegend();
                if(ls instanceof RealLiteralLegend){
                    RealLiteralLegend rll = (RealLiteralLegend) ls;
                    if(colorLegend instanceof ColorLiteralLegend){
                            return new ConstantSolidFillLegend(sf, (ColorLiteralLegend) colorLegend, rll);
                    } else if(colorLegend instanceof Categorize2ColorLegend){
                            return new CategorizedSolidFillLegend(sf, (Categorize2ColorLegend) colorLegend, rll);
                    } else if(colorLegend instanceof Recode2ColorLegend){
                            return new RecodedSolidFillLegend(sf, (Recode2ColorLegend) colorLegend, rll);
                    }
                }
                throw new UnsupportedOperationException("We can't do such an anlysis "
                        + "on Fill instances yet");
                
        }
}
