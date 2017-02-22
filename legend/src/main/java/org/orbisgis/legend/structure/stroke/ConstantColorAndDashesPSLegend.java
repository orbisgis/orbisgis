/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.legend.structure.stroke;

import java.awt.Color;

import org.orbisgis.coremap.renderer.se.parameter.ParameterUtil;
import org.orbisgis.coremap.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.coremap.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFillLegend;
import org.orbisgis.legend.structure.literal.StringLiteralLegend;
import org.orbisgis.legend.structure.parameter.NumericLegend;


/**
 * Represents a {@code PenStroke} where the Color and the dash pattern are
 * constant.
 * @author Alexis Guéganno
 */
public abstract class ConstantColorAndDashesPSLegend extends PenStrokeLegend {

    public ConstantColorAndDashesPSLegend(){
        super();
    }
    /**
     * Build an instance of {@code ConstantColorAndDashesPSLegend} using the
     * given parameters.
     * @param ps
     * @param width
     * @param fill
     * @param dash 
     */
    public ConstantColorAndDashesPSLegend(PenStroke ps, NumericLegend width,
            LegendStructure fill, LegendStructure dash) {
        super(ps, width, fill, dash);
    }

    /**
     * Gets the {@code Color} of the associated {@code PenStroke}.
     * @return
     */
    public Color getLineColor() {
        return ((ConstantSolidFillLegend)getFillAnalysis()).getColor();
    }

    /**
     * Sets the {@code Color} of the associated {@code PenStroke}.
     * @param col
     */
    public void setLineColor(Color col) {
        ((ConstantSolidFillLegend)getFillAnalysis()).setColor(col);
    }

    /**
     * Gets the {@code Color} of the associated {@code PenStroke}.
     * @return
     */
    public double getLineOpacity() {
        return ((ConstantSolidFillLegend)getFillAnalysis()).getOpacity();
    }

    /**
     * Sets the {@code Color} of the associated {@code PenStroke}.
     * @param opacity The new opacity.
     */
    public void setLineOpacity(double opacity) {
        ((ConstantSolidFillLegend)getFillAnalysis()).setOpacity(opacity);
    }

        /**
         * Get the {@code String} that represent the dash pattern for the
         * associated {@code PenStroke}.
         * @return
         * The dash array in a string. Can't be null. Even if the underlying
         * {@code Symbolizer} does not have such an array, an empty {@code
         * String} is returned.
         */
        public String getDashArray() {
            String ret = "";
            StringLiteralLegend sll = (StringLiteralLegend) getDashLegend();
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
        * @param str The new dash array.
        */
        public void setDashArray(String str) {
            PenStroke ps = (PenStroke) getStroke();
            StringLiteral rl = (StringLiteral) ps.getDashArray();
            String da = ParameterUtil.validateDashArray(str) ? str : "";
            if(rl == null){
                rl = new StringLiteral(da);
                ps.setDashArray(rl);
                setDashLegend(new StringLiteralLegend(rl));
            } else {
                rl.setValue(da);
            }
        }

}
