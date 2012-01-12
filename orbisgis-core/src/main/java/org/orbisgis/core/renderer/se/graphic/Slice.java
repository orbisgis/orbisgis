package org.orbisgis.core.renderer.se.graphic;

import java.util.HashSet;
import net.opengis.se._2_0.thematic.SliceType;
import org.orbisgis.core.renderer.se.FillNode;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.Fill;
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
 * @author alexis
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

        /**
         * Get a String representation of the list of features this {@code Slice}
         * depends on.
         * @return
         * The features this {@code Slice} depends on, in a {@code String}.
         */
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
}
