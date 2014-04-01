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
package org.orbisgis.coremap.renderer.se.stroke;

import java.util.ArrayList;
import java.util.List;
import net.opengis.se._2_0.core.StrokeElementType;
import org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.coremap.renderer.se.StrokeNode;
import org.orbisgis.coremap.renderer.se.SymbolizerNode;
import org.orbisgis.coremap.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameter;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameterContext;

/**
 * A {@code StrokeElement} is used to draw a "sub-stroke" of a render pattern. 
 * {@code StrokeElement} instances are combined to draw lines with complex rendering
 * patterns. They are used, for instance, in {@link CompoundStroke}.</p>
 * <p>A {@code StrokeElement} instance need the following parameters :
 * <ul><li>PreGap : to define how far to advance along the line before starting to plot content.
 * It's a {@link RealParameter} placed in a non-negative {@link RealParameterContext}. If not
 * given, defaulted to 0.</li>
 * <li>PostGap : to define how far from the end of the line to stop plotting.
 * It's a {@link RealParameter} placed in a non-negative {@link RealParameterContext}. If not
 * given, defaulted to 0.</li>
 * <li>Length  : The length along the line to draw using the inner {@code Stroke}. 
 * It's a {@link RealParameter} placed in a non-negative {@link RealParameterContext}. If not
 * given, defaulted to the length og the line.</li>
 * <li>Stroke : The way to style the line, as explained in {@link Stroke} and its subclasses.
 * It is a {@link Stroke} instance. This argument is compulsory.</li>
 * </ul>
 * @author Maxence Laurent
 */
public final class StrokeElement extends CompoundStrokeElement implements StrokeNode {

        private RealParameter length;
        private RealParameter preGap;
        private RealParameter postGap;
        private Stroke stroke;

        /**
         * Build a new, default, {@code StrokeElement}, with a default inner {@link PenStroke}.
         */
        public StrokeElement() {
                setStroke(new PenStroke());
        }

        /**
         * Build a {@code StrokeElement} from the JAXB type given in argument.
         * @param set
         * @throws org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle
         */
        public StrokeElement(StrokeElementType set) throws InvalidStyle {
                if (set.getPreGap() != null) {
                        setPreGap(SeParameterFactory.createRealParameter(set.getPreGap()));
                }

                if (set.getPreGap() != null) {
                        setPostGap(SeParameterFactory.createRealParameter(set.getPostGap()));
                }

                if (set.getLength() != null) {
                        setLength(SeParameterFactory.createRealParameter(set.getLength()));
                }

                if (set.getStroke() != null) {
                        Stroke s = Stroke.createFromJAXBElement(set.getStroke());
                        if (!(s instanceof CompoundStroke)) {
                                setStroke(Stroke.createFromJAXBElement(set.getStroke()));
                        } else {
                                throw new InvalidStyle("Not allowed to nest compound stroke within compound stroke");
                        }

                }
        }

        /**
         * Set the PreGap value embedded in this {@code StrokeElement}.It is used to 
         * define how far to advance along the line before starting to plot content.
         * The given {@link RealParameter} is placed in a non-negative {@link RealParameterContext}.
         * If it is less than 0, it will be set back to 0.
         * @param preGap 
         */
        public void setPreGap(RealParameter preGap) {
                this.preGap = preGap;

                if (preGap != null) {
                        this.preGap.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
                        this.preGap.setParent(this);
                }
        }

        /**
         * Set the PreGap value embedded in this {@code StrokeElement}.It is used to 
         * define how far from the end of the line to stop plotting.
         * The given {@link RealParameter} is placed in a non-negative {@link RealParameterContext}.
         * If it is less than 0, it will be set back to 0.
         * @param postGap 
         */
        public void setPostGap(RealParameter postGap) {
                this.postGap = postGap;

                if (postGap != null) {
                        this.postGap.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
                        this.postGap.setParent(this);
                }
        }

        /**
         * Get the PreGap value embedded in this {@code StrokeElement}.It is used to 
         * define how far to advance along the line before starting to plot content.
         * It is placed in a non-negative {@link RealParameterContext}, and is consequently
         * never negative.
         * @return 
         */
        public RealParameter getPreGap() {
                return preGap;
        }

        /**
         * Get the PreGap value embedded in this {@code StrokeElement}.It is used to 
         * define how far from the end of the line to stop plotting.
         * It is placed in a non-negative {@link RealParameterContext}, and is consequently
         * never negative.
         * @return 
         */
        public RealParameter getPostGap() {
                return postGap;
        }

        /**
         * Get the length defined in this {@code StrokeElement}, i.e. the length along
         * the line to draw using the inner {@code Stroke}.
         * It is placed in a non-negative {@link RealParameterContext}, and is consequently
         * never negative.
         * @return 
         */
        public RealParameter getLength() {
                return length;
        }

        /**
         * Set the length defined in this {@code StrokeElement}, i.e. the length along
         * the line to draw using the inner {@code Stroke}.
         * The given {@link RealParameter} is placed in a non-negative {@link RealParameterContext}.
         * If it is less than 0, it will be set back to 0.
         * @param length 
         */
        public void setLength(RealParameter length) {
                this.length = length;
                if (length != null) {
                        length.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
                        length.setParent(this);
                }
        }

        @Override
        public void setStroke(Stroke stroke) {
                this.stroke = stroke;
                if (stroke != null) {
                        stroke.setParent(this);
                }
        }

        @Override
        public Stroke getStroke() {
                return stroke;
        }

        @Override
        public Object getJAXBType() {
                StrokeElementType set = new StrokeElementType();

                if (this.getLength() != null) {
                        set.setLength(length.getJAXBParameterValueType());
                }

                if (this.getPreGap() != null) {
                        set.setPreGap(preGap.getJAXBParameterValueType());
                }

                if (this.getPostGap() != null) {
                        set.setPostGap(postGap.getJAXBParameterValueType());
                }

                if (this.getStroke() != null) {
                        set.setStroke(stroke.getJAXBElement());
                }

                return set;
        }

        @Override
        public List<SymbolizerNode> getChildren() {
                List<SymbolizerNode> ls = new ArrayList<SymbolizerNode>();
                if (length != null) {
                        ls.add(length);
                }
                if (preGap != null) {
                        ls.add(preGap);
                }
                if (postGap != null) {
                        ls.add(postGap);
                }
                if (stroke != null) {
                        ls.add(stroke);
                }
                return ls;
        }

        @Override
        public String toString() {
                return this.stroke.getClass().getSimpleName();
        }
}
