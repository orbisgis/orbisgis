/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.legend.structure.fill.constant;

import java.awt.Color;
import org.orbisgis.coremap.renderer.se.fill.SolidFill;
import org.orbisgis.coremap.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.coremap.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.legend.structure.fill.SolidFillLegend;
import org.orbisgis.legend.structure.literal.ColorLiteralLegend;
import org.orbisgis.legend.structure.literal.RealLiteralLegend;

/**
 * A {@code Legend} that represents a {@code SolidFill} where the color is a
 * {@code Literal} instance.
 * @author Alexis Guéganno
 */
public class ConstantSolidFillLegend extends SolidFillLegend implements ConstantSolidFill {

        /**
         * Builds a new {@code }ConstantSolidFillLegend} instance using the given {@code SolidFill}. Use at your own risks :
         * you must be sure that the given fill is valid for this legend.
         * @param sf
         * @throws ClassCastException if the color or legend can't be identified as literal.
         */
        public ConstantSolidFillLegend(SolidFill sf){
            super(sf, new ColorLiteralLegend((ColorLiteral)sf.getColor()), new RealLiteralLegend((RealLiteral)sf.getOpacity()));
        }

        /**
         * Build a new {@code ConstantSolidFillLegend} using the {@code SolidFill}
         * and {@code ColorLiteralLegend} given in parameter.
         * @param fill
         * @param colorLegend
         */
        public ConstantSolidFillLegend(SolidFill fill, ColorLiteralLegend colorLegend, RealLiteralLegend opacity) {
                super(fill, colorLegend, opacity);
        }

        @Override
        public Color getColor(){
            ColorLiteralLegend cll = (ColorLiteralLegend) getFillColorLegend();
            return cll.getColor();
        }

        @Override
        public void setColor(Color col) {
                ColorLiteralLegend cll = (ColorLiteralLegend) getFillColorLegend();
                cll.setColor(col);
        }

        @Override
        public double getOpacity(){
            RealLiteralLegend rll = (RealLiteralLegend) getFillOpacityLegend();
            return rll.getDouble();
        }

        @Override
        public void setOpacity(double d) {
            RealLiteralLegend rll = (RealLiteralLegend) getFillOpacityLegend();
                rll.setDouble(d);
        }

}
