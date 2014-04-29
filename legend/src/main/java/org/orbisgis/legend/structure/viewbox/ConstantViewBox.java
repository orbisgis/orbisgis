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
package org.orbisgis.legend.structure.viewbox;

import org.orbisgis.coremap.renderer.se.graphic.ViewBox;
import org.orbisgis.coremap.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.legend.structure.literal.RealLiteralLegend;

/**
 * Represents a ViewBox that does not change, whatever the processed data is.
 *
 * @author Alexis Guéganno
 */
public class ConstantViewBox extends DefaultViewBox {

        /**
         * Build a new instance of {@code ConstantViewBox}. We directly use {@code
         * RealLiteralLegend} instances.
         *
         * @param height
         * @param width
         * @param view
         */
        public ConstantViewBox(RealLiteralLegend height, RealLiteralLegend width, ViewBox view) {
                super(height, width, view);
        }

        /**
         * As {@code ViewBox} instances can be defined with only one dimension
         * parameter, we must be able to treat this case.
         * @param realLiteralLegend
         * @param isHeight
         * @param vb 
         */
        public ConstantViewBox(RealLiteralLegend realLiteralLegend, boolean isHeight, ViewBox vb) {
                super(realLiteralLegend, isHeight, vb);
        }

        /**
         * Gets the width of the associated {@code ViewBox}. As it is defined
         * as a {@code Literal}, its value is constant whatever the input data
         * are.
         * @return
         * A {@code Double} that can be null. A {@code ViewBox} can be defined with
         * only one dimension set.
         */
        public Double getWidth() {
            Double ret = null;
            RealLiteralLegend rll = (RealLiteralLegend) getWidthLegend();
            if(rll != null){
                ret = rll.getDouble();
            }
            return ret;
        }

        /**
         * Gets the height of the associated {@code ViewBox}. As it is defined
         * as a {@code Literal}, its value is constant whatever the input data
         * are.
         * @return
         * A {@code Double} that can be null. A {@code ViewBox} can be defined with
         * only one dimension set.
         */
        public Double getHeight() {
            Double ret = null;
            RealLiteralLegend rll = (RealLiteralLegend) getHeightLegend();
            if(rll != null){
                ret = rll.getDouble();
            }
            return ret;
        }

        /**
         * Set the Width of this ViewBox.
         * @param d
         * A {@code Double} that can be null. A {@code ViewBox} can be defined with
         * only one dimension set.
         * @throws IllegalArgumentException
         * If {@code d} is null and {@code getHeight()} returns null too. At
         * least one of the dimensions must be not null.
         */
        public void setHeight(Double d) {
            if(d==null){
                if(getWidth() != null){
                    RealLiteral rl = new RealLiteral(getWidth());
                    setHeightLegend(new RealLiteralLegend(rl));
                    getViewBox().setHeight(rl);
                } else {
                    throw new IllegalArgumentException("you're not supposed to"
                            + "set both height and width of a viewbox to null.");
                }
            } else {
                RealLiteralLegend rll = (RealLiteralLegend) getHeightLegend();
                if(rll == null){
                    RealLiteral rlit = new RealLiteral(d);
                    getViewBox().setHeight(rlit);
                    rll = new RealLiteralLegend(rlit);
                    setHeightLegend(rll);
                }
                rll.setDouble(d);
            }
        }

        /**
         * Set the Width of this ViewBox.
         * @param d
         * A {@code Double} that can be null. A {@code ViewBox} can be defined with
         * only one dimension set.
         * @throws IllegalArgumentException
         * If {@code d} is null and {@code getHeight()} returns null too. At
         * least one of the dimensions must be not null.
         */
        public void setWidth(Double d) {
            if(d==null){
                if(getHeight() != null){
                    RealLiteral rl = new RealLiteral(getHeight());
                    setWidthLegend(new RealLiteralLegend(rl));
                    getViewBox().setWidth(rl);
                } else {
                    throw new IllegalArgumentException("you're not supposed to"
                            + "set both height and width of a viewbox to null.");
                }
            } else {
                RealLiteralLegend rll = (RealLiteralLegend) getWidthLegend();
                if(rll == null){
                    RealLiteral rlit = new RealLiteral(d);
                    getViewBox().setWidth(rlit);
                    rll = new RealLiteralLegend(rlit);
                    setWidthLegend(rll);
                }
                rll.setDouble(d);
            }
        }
}
