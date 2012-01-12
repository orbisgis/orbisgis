/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.renderer.se.graphic;

import java.awt.geom.Point2D;
import java.util.HashSet;
import org.gdms.data.DataSource;
import net.opengis.se._2_0.core.ViewBoxType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

/**
 * {@code ViewBox} supplies a simplen and convenient method to change the view box of a graphic,
 * in a {@link MarkGraphic} for instance.
 * {@code ViewBox} is bult using the following parameters :
 * <ul><li>X : the width of the box.</li>
 * <li>Y : the height of the box.</li></ul>
 * If only one of these two is given, they are considered to be equal.</p>
 * <p>The main difference between this class and {@link Scale} is that a {@code Scale}
 * will use a reference graphic, that already has a size, and process an affine transformation
 * on it, while here the size of the graphic will be defined directly using its height
 * and width.</p>
 * <p>The values given for the height and the width can be negative. If that
 * happens, the coordinate of the rendered graphic will be flipped.
 * @author alexis, maxence
 */
public final class ViewBox implements SymbolizerNode {
        
        private SymbolizerNode parent;
        private RealParameter x;
        private RealParameter y;

        /**
         * Build a new {@code ViewBox}, with empty parameters.
         */
        public ViewBox() {
                setWidth(null);
                setHeight(null);
        }

        /**
         * Build a new {@code ViewBox}, using the given width.
         */
        public ViewBox(RealParameter width) {
                setWidth(width);
        }

        /**
         * Build a new {@code ViewBox} using the given JAXB type.
         * @param viewBox
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
         */
        public ViewBox(ViewBoxType viewBox) throws InvalidStyle {
                if (viewBox.getHeight() != null) {
                        this.setHeight(SeParameterFactory.createRealParameter(viewBox.getHeight()));
                }

                if (viewBox.getWidth() != null) {
                        this.setWidth(SeParameterFactory.createRealParameter(viewBox.getWidth()));
                }
        }

        /**
         * A {@code ViewBox} can be used if and only if one, at least, of its two parameters
         * has been set.
         * @return 
         */
        public boolean usable() {
                return this.x != null || this.y != null;
        }

        /**
         * Set the wifth of this {@code ViewBox}.
         * @param width 
         */
        public void setWidth(RealParameter width) {
                x = width;
                if (x != null) {
                        x.setContext(RealParameterContext.REAL_CONTEXT);
                }
        }
        /**
         * Get the wifth of this {@code ViewBox}.
         * @return 
         */
        public RealParameter getWidth() {
            return x;
                //return x == null ? y : x;
        }

        /**
         * Set the height of this {@code ViewBox}.
         * @param height 
         */
        public void setHeight(RealParameter height) {
                y = height;
                if (y != null) {
                        y.setContext(RealParameterContext.REAL_CONTEXT);
                }
        }

        /**
         * Get the height of this {@code ViewBox}.
         * @return 
         */
        public RealParameter getHeight() {
            return y;
                //return y == null ? x : y;
        }

        @Override
        public Uom getUom() {
                return parent.getUom();
        }

        @Override
        public SymbolizerNode getParent() {
                return parent;
        }

        @Override
        public void setParent(SymbolizerNode node) {
                parent = node;
        }

    /**
     * Get a String representation of the list of features this {@code ViewBox}
     * depends on.
     * @return
     * The features this {@code Label} depends on, in a {@code String}.
     */
    @Override
        public HashSet<String> dependsOnFeature() {
            HashSet<String> hs = null;
            if (x != null) {
                    hs = x.dependsOnFeature();
            }
            if (y != null) { 
                if(hs == null) {
                    hs = y.dependsOnFeature();
                } else {
                    hs.addAll(y.dependsOnFeature());
                }
            }

            return hs;
        }

        /**
         * Return the final dimension described by this view box, in [px].
         * @param ds DataSource, i.e. the layer
         * @param fid feature id
         * @param ratio required final ratio (if either width or height isn't defined)
         * @return
         * @throws ParameterException
         */
        public Point2D getDimensionInPixel(DataSource sds, long fid, double height, double width, Double scale, Double dpi) throws ParameterException {
                double dx, dy;

                double ratio = height / width;

                if (x != null && y != null) {
                        dx = x.getValue(sds, fid);
                        dy = y.getValue(sds, fid);
                } else if (x != null) {
                        dx = x.getValue(sds, fid);
                        dy = dx * ratio;
                } else if (y != null) {
                        dy = y.getValue(sds, fid);
                        dx = dy / ratio;
                } else { // nothing is defined
                        dx = width;
                        dy = height;
                        //return null; 
                }


                dx = Uom.toPixel(dx, this.getUom(), dpi, scale, width);
                dy = Uom.toPixel(dy, this.getUom(), dpi, scale, height);

                if (dx <= 0.00021 || dy <= 0.00021) {
                        throw new ParameterException("View-box is too small: (" + dx + ";" + dy + ")");
                }

                return new Point2D.Double(dx, dy);
        }

        /**
         * Retrieve this {@code ViewBox} as a JAXB type.
         * @return 
         */
        public ViewBoxType getJAXBType() {
                ViewBoxType v = new ViewBoxType();

                if (x != null) {
                        v.setWidth(x.getJAXBParameterValueType());
                }

                if (y != null) {
                        v.setHeight(y.getJAXBParameterValueType());
                }

                return v;
        }

        /**
         * Gets a String representation of this {@code ViewBox}.
         * @return
         * A String containing the wifth and height of the {@code ViewBox}..
         */
        @Override
        public String toString() {
                String result = "ViewBox:";

                if (this.x != null) {
                        result += "  Width: " + x.toString();
                }

                if (this.y != null) {
                        result += "  Height: " + y.toString();
                }

                return result;
        }
}
