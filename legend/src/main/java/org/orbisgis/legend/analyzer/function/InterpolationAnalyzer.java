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
package org.orbisgis.legend.analyzer.function;

import java.util.List;
import org.orbisgis.core.renderer.se.parameter.Interpolate;
import org.orbisgis.core.renderer.se.parameter.color.Interpolate2Color;
import org.orbisgis.core.renderer.se.parameter.real.Interpolate2Real;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealFunction;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.legend.AbstractAnalyzer;
import org.orbisgis.legend.structure.interpolation.InterpolationLegend;
import org.orbisgis.legend.structure.interpolation.LinearInterpolationLegend;
import org.orbisgis.legend.structure.interpolation.SqrtInterpolationLegend;

/**
 * Analyzes an interpolation node. This class will try to determine which legend
 * can be associated to the interpolation method used in this interpolation and
 * to the FES function applied on the input value (if any).
 * @author Alexis Guéganno
 */
public class InterpolationAnalyzer extends AbstractAnalyzer{

        /**
         * Build A new {@code InterpolationAnalyzer} from the {@code Interpolate}
         * given in parameter.
         * @param inter
         */
        public InterpolationAnalyzer(Interpolate inter){
                if(inter instanceof Interpolate2Real){
                        analyzeRealInterpolation((Interpolate2Real) inter);
                } else if(inter instanceof Interpolate2Color){
                        throw new UnsupportedOperationException("Raster are not supported yet");
                }
        }

        /**
         * We determine which case we are treating.
         * @param inter
         */
        private void analyzeRealInterpolation(Interpolate2Real inter) {
                RealParameter rp =  inter.getLookupValue();
                if(rp instanceof RealAttribute){
                        setLegend(new LinearInterpolationLegend(inter));
                } else if(rp instanceof RealFunction){
                        RealFunction rf = (RealFunction) rp;
                        List<RealParameter> ops = rf.getOperands();
                        if(!ops.isEmpty()){
                                if(rf.getOperator().equals(RealFunction.Operators.SQRT)
                                        && ops.size() == 1 && ops.get(0) instanceof RealAttribute){
                                        setLegend(new SqrtInterpolationLegend(inter));
                                } else {
                                        setLegend(new InterpolationLegend(inter));
                                }
                        }
                } else {
                        setLegend(new InterpolationLegend(inter));
                }
        }
}
