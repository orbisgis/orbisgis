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
package org.orbisgis.legend.structure.stroke.constant;

import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.structure.fill.ConstantFillLegend;
import org.orbisgis.legend.structure.literal.RealLiteralLegend;
import org.orbisgis.legend.structure.literal.StringLiteralLegend;
import org.orbisgis.legend.structure.stroke.ConstantColorAndDashesPSLegend;

/**
 * This class is used to represent instances of {@code PenStroke} whose
 * parameters (width, fill and dash array) are all constant.
 * @author Alexis Guéganno
 */
public class ConstantPenStrokeLegend extends ConstantColorAndDashesPSLegend implements ConstantPenStroke{

        /**
         * Build a new instance of {@code ConstantPenStrokeLegend}.
         * @param ps
         * @param width
         * @param fill
         * @param dash
         */
        public ConstantPenStrokeLegend(PenStroke ps, RealLiteralLegend width,
                        ConstantFillLegend fill, StringLiteralLegend dash) {
                super(ps, width, fill, dash);
        }

        /**
         * Get the width of the associated {@code PenStroke}.
         * @return
         */
    @Override
        public double getLineWidth() {
            return ((RealLiteralLegend) getWidthAnalysis()).getDouble();
        }

        /**
         * Set the width of the associated {@code PenStroke}.
         * @param width
         */
    @Override
        public void setLineWidth(double width) {
            ((RealLiteralLegend) getWidthAnalysis()).setDouble(width);
        }

        /**
         * Get the {@code String} that represent the dash pattern for the
         * associated {@code PenStroke}.
         * @return
         * The dash array in a string. Can't be null. Even if the underlying
         * {@code Symbolizer} does not have such an array, an empty {@code
         * String} is returned.
         */
    @Override
        public String getDashArray() {
            String ret = "";
            StringLiteralLegend sll = (StringLiteralLegend) getDashAnalysis();
            if(sll == null){
                return ret;
            }
            StringLiteral lit = sll.getLiteral();
            if(lit != null){
                ret = lit.getValue(null, 0);
            }
            return ret == null ? "" : ret;
        }

        /**
        * Set the {@code String} that represent the dash pattern for the
         * associated {@code PenStroke}.
        * @param dashes
        */
    @Override
        public void setDashArray(String str) {
            PenStroke ps = getPenStroke();
            StringLiteral rl = (StringLiteral) ps.getDashArray();
            String da = validateDashArray(str) ? str : "";
            if(!da.isEmpty()){
                if(rl == null){
                    rl = new StringLiteral(da);
                    ps.setDashArray(rl);
                    setDashAnalysis(new StringLiteralLegend(rl));
                } else {
                    rl.setValue(da);
                }
            } else {
                ps.setDashArray(null);
                setDashAnalysis(null);
            }
        }

        private boolean validateDashArray(String str) {
            String[] splits = str.split(" ");
            for(String s : splits){
                try{
                    double d = Double.valueOf(s);
                    if(d<0){
                            return false;
                    }
                } catch(NumberFormatException nfe){
                    return false;
                }
            }
            return true;
        }
}
