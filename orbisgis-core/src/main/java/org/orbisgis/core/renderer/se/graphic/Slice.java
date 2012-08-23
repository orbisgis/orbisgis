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
package org.orbisgis.core.renderer.se.graphic;

import java.util.HashSet;
import net.opengis.se._2_0.thematic.SliceType;
import org.orbisgis.core.renderer.se.FillNode;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

/**
 * {@code Slice}s are used in {@code PieChart}s instances to determine the size
 * of each rendered area. They are defined using :
 * <ul>
 * <li>A name (compulsory).</li>
 * <li>the value this {@code Slice} represents (compulsory).</li>
 * <li>A {@code Fill} intance to render its interior (compulsory).</li>
 * <li>A gap (optional).</li>
 * </ul>
 * @author Alexis Guéganno
 */
public class Slice implements SymbolizerNode, FillNode {

        private String name;
        private RealParameter value;
        private Fill fill;
        private RealParameter gap;
        private SymbolizerNode parent;

        @Override
        public Fill getFill() {
                return fill;
        }

        @Override
        public void setFill(Fill fill) {
                this.fill = fill;
                fill.setParent(this);
        }

        /**
         * Get the gap that must be maintained around this {@code Slice}.
         * @return
         * The gap as a non-negative {@link RealParameter}, or null if not set
         * before.
         */
        public RealParameter getGap() {
                return gap;
        }

        /**
         * Set the gap that must be maintained around this {@code Slice}.
         * @param gap
         */
        public void setGap(RealParameter gap) {
                this.gap = gap;
                if (gap != null) {
                        gap.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
                }
        }

        /**
         * Get the name of this {@code Slice}.
         * @return
         * The name as a {@code String}.
         */
        public String getName() {
                return name;
        }

        /**
         * Set the name of this {@code Slice}.
         * @param name
         */
        public void setName(String name) {
                this.name = name;
        }

        /**
         * Get the value this slice represents.
         * @return
         * The value, as a {@link RealParameter}, so that external sources
         * can be used.
         */
        public RealParameter getValue() {
                return value;
        }

        /**
         * Set the value represented by this {@code Slice}.
         * @param value
         */
        public void setValue(RealParameter value) {
                this.value = value;
                if (value != null) {
                        value.setContext(RealParameterContext.REAL_CONTEXT);
                }
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
         * Get a {@code SliceType} that represents this {@code Slice}.
         * @return
         */
        public SliceType getJAXBType() {
                SliceType s = new SliceType();

                if (fill != null) {
                        s.setFill(fill.getJAXBElement());
                }
                if (gap != null) {
                        s.setGap(gap.getJAXBParameterValueType());
                }
                if (name != null) {
                        s.setName(name);
                }
                if (value != null) {
                        s.setValue(value.getJAXBParameterValueType());
                }

                return s;
        }

        @Override
        public HashSet<String> dependsOnFeature() {
                HashSet<String> result = new HashSet<String>();
                if (fill != null) {
                        result.addAll(fill.dependsOnFeature());
                }
                if (value != null) {
                        result.addAll(value.dependsOnFeature());
                }
                if (gap != null) {
                        result.addAll(gap.dependsOnFeature());
                }

                return result;
        }

        @Override
        public UsedAnalysis getUsedAnalysis() {
                UsedAnalysis result = new UsedAnalysis();
                if (fill != null) {
                        result.merge(fill.getUsedAnalysis());
                }
                if (value != null) {
                        result.include(value);
                }
                if (gap != null) {
                        result.include(gap);
                }

                return result;
        }
}
