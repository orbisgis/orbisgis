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
package org.orbisgis.legend.structure.viewbox;

import org.orbisgis.core.renderer.se.graphic.ViewBox;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.legend.structure.literal.RealLiteralLegend;
import org.orbisgis.legend.structure.parameter.NumericLegend;

/**
 * {@code ViewBox} instances can all be described as being associated to two
 * random {@code NumericLegend} instances. Consequently, this class is the base
 * legend description of {@code ViewBox}, and can be reused freely for more
 * accurate descriptions.
 * @author Alexis Guéganno
 */
public class DefaultViewBox implements ViewBoxLegend{

        private ViewBox viewBox;
        private NumericLegend height;
        private NumericLegend width;

        /**
         * Builds a new DefaultViewBox with 3 as height and width.
         */
        public DefaultViewBox(){
                viewBox = new ViewBox();
                height = new RealLiteralLegend(new RealLiteral(3));
                width = new RealLiteralLegend(new RealLiteral(3));
                viewBox.setWidth((RealParameter)width.getParameter());
                viewBox.setHeight((RealParameter)height.getParameter());
        }
        /**
         * Build a new {@code DefaultViewBox} with the given parameters, using
         * directly the two needed {@code NumericLegend} instances.
         * @param height
         * @param width
         * @param view
         */
        public DefaultViewBox(NumericLegend height, NumericLegend width, ViewBox view){
                viewBox = view;
                this.height = height;
                this.width = width;
        }

        /**
         * Build a new {@code DefaultViewBox}. We have just one {@code
         * NumericLegend} here. Its meaning is dependant on {@code isHeight}. If
         * {@code isHeight}, {@code nl} is supposed to be the description of the
         * height of {@code view}. If not {@ode isHeight}, it is the description
         * of the width.
         * @param nl
         * @param isHeight
         * @param view
         */
        public DefaultViewBox(NumericLegend nl, boolean isHeight, ViewBox view){
                if(isHeight){
                        height = nl;
                        width = null;
                } else {
                        height = null;
                        width = nl;
                }
                viewBox = view;
        }

        /**
         * Gets the {@code ViewBox} associated to this LegendStructure.
         * @return
         */
        @Override
        public ViewBox getViewBox() {
                return viewBox;
        }

        /**
         * Gets the {@code LegendStructure} associated to the height of this {@code
         * ViewBox}.
         * @return
         */
        public NumericLegend getHeightLegend() {
                return height;
        }

        /**
         * Gets the {@code LegendStructure} associated to the width of this {@code
         * ViewBox}.
         * @return
         */
        public NumericLegend getWidthLegend() {
                return width;
        }

        /**
         * Gets the {@code LegendStructure} associated to the height of this {@code
         * ViewBox}.
         * @return
         */
        public void setHeightLegend(NumericLegend nl) {
                height = nl;
        }

        /**
         * Gets the {@code LegendStructure} associated to the width of this {@code
         * ViewBox}.
         * @param nl
         */
        public void setWidthLegend(NumericLegend nl) {
                width = nl;
        }

}
