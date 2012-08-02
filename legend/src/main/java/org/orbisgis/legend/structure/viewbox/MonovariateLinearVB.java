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
package org.orbisgis.legend.structure.viewbox;

import org.orbisgis.core.renderer.se.graphic.ViewBox;
import org.orbisgis.legend.structure.parameter.NumericLegend;
import org.orbisgis.legend.structure.interpolation.LinearInterpolationLegend;

/**
 * A symbol can be said proportional is one of is dimensions variates linearly
 * upon a numeric field.
 * @author Alexis Guéganno
 */
public class MonovariateLinearVB extends DefaultViewBox {

        private boolean isHeight;

        /**
         * Build a new {@code MonovariateLinearVB} using the legends associated
         * to the height and width of the oridinal {@code ViewBox}. {@code
         * isHeight} is used to determine if the {@code InterpolationLegend} is
         * associated to the height or to the width.
         * @param height
         * @param width
         * @param view
         * @param isheight
         * If {@code true} we'll consider that the interpolation is made on
         * the height, and on the width otherwise.
         */
        public MonovariateLinearVB(NumericLegend height, NumericLegend width,
                        ViewBox view, boolean isheight){
                super(height, width, view);
                isHeight = isheight;
        }

        /**
         * If this method returns {@code true}, the interpolation is supposed to
         * be made on the height. If it returns {@code false}, the interpolation
         * is supposed to be mad on the width of the underlying {@code ViewBox}.
         * @return
         */
        public boolean isHeight(){
                return isHeight;
        }

        /**
         * Get the interpolation legend associated to this {@code Legend}.
         * @return
         */
        public LinearInterpolationLegend getInterpolation() {
                if(isHeight){
                        return (LinearInterpolationLegend) getHeightLegend();
                } else {
                        return (LinearInterpolationLegend) getWidthLegend();
                }
        }

}
