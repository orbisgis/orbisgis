package org.orbisgis.core.renderer.se.graphic;

import java.util.HashSet;
import net.opengis.se._2_0.thematic.CategoryType;
import org.orbisgis.core.renderer.se.FillNode;
import org.orbisgis.core.renderer.se.GraphicNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.core.renderer.se.StrokeNode;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;

/**
 * A {@code Category} is a part of an {@link AxisChart}. It embeds a value, and
 * hints that must be used to render it. It depends on the following parameters :
 * <ul>
 * <li>A measure, ie the value to represent with this {@code Category}.</li>
 * <li>A {@link Fill} to fill its representation.</li>
 * <li>A {@link Stroke} that will be used to draw its boundaries.</li>
 * <li>A {@link GraphicCollection} to represent it.</li>
 * <li>A name (as a String).</li>
 * </ul>
 * @author maxence
 * @todo add support for stacked bar (means category fill / stroke are mandatory) and others are forbiden
 */
public final class Category implements SymbolizerNode, FillNode, StrokeNode, GraphicNode {

        private RealParameter measure;

        /* in order to draw bars, optionnal */
        private Fill fill;
        private Stroke stroke;

        /* In order to draw points, optionnal */
        private GraphicCollection graphic;
        private SymbolizerNode parent;
        private String name;

        /**
         * Build a new, empty, {@code Category}.
         */
        public Category() {
                graphic = new GraphicCollection();
                graphic.setParent(this);
                name = "";
        }

        /**
         * Build a new {@code Category} from the given {@code CategoryType}.
         * @param c
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle
         */
        public Category(CategoryType c) throws InvalidStyle {
                if (c.getFill() != null) {
                        setFill(Fill.createFromJAXBElement(c.getFill()));
                }

                if (c.getGraphic() != null) {
                        setGraphicCollection(new GraphicCollection(c.getGraphic(), this));
                }

                if (c.getStroke() != null) {
                        setStroke(Stroke.createFromJAXBElement(c.getStroke()));
                }

                if (c.getValue() != null) {
                        setMeasure(SeParameterFactory.createRealParameter(c.getValue()));
                }

                if (c.getName() != null) {
                        setName(c.getName());
                }
        }

        @Override
        public Fill getFill() {
                return fill;
        }

        @Override
        public void setFill(Fill fill) {
                this.fill = fill;
                fill.setParent(this);
        }

        @Override
        public Stroke getStroke() {
                return stroke;
        }

        @Override
        public void setStroke(Stroke stroke) {
                this.stroke = stroke;
                stroke.setParent(this);
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

        @Override
        public GraphicCollection getGraphicCollection() {
                return graphic;
        }

        @Override
        public void setGraphicCollection(GraphicCollection graphic) {
                this.graphic = graphic;
        }

        /**
         * Set the name of this {@code Category}.
         * @param name
         */
        public void setName(String name) {
                this.name = name;
        }

        /**
         * Get the name of this {@code Category}.
         * @return
         * The name of this {@code Category}, as a String instance.
         */
        public String getName() {
                return name;
        }

        /**
         * The measure associated to this {@code Category}.
         * @return
         * A {@link RealParameter}. That means this {@code Category} can
         * be linked to a value in a table, for instance.
         */
        public RealParameter getMeasure() {
                return measure;
        }

        /**
         * Set the {@link RealParameter} used to retrieve the value associated
         * to this {@code Category}.
         * @param measure
         */
        public void setMeasure(RealParameter measure) {
                this.measure = measure;
        }

        /**
         * Get a JAXB representation of this {@code Category}.
         * @return
         * A {@code CategoryType}, that has been built using the values embedded
         * in this {@code Category} instance.
         */
        CategoryType getJAXBType() {
                CategoryType ct = new CategoryType();

                if (this.getFill() != null) {
                        ct.setFill(this.getFill().getJAXBElement());
                }

                if (this.getStroke() != null) {
                        ct.setStroke(getStroke().getJAXBElement());
                }

                if (this.getGraphicCollection() != null) {
                        ct.setGraphic(getGraphicCollection().getJAXBElement());
                }

                if (this.getMeasure() != null) {
                        ct.setValue(getMeasure().getJAXBParameterValueType());
                }

                if (this.getName() != null) {
                        ct.setName(getName());
                }

                return ct;
        }
        
        @Override
        public HashSet<String> dependsOnFeature() {
            HashSet<String> ret = new HashSet<String>();
            if (this.getFill() != null) {
                ret.addAll(this.getFill().dependsOnFeature());
            }

            if (this.getStroke() != null) {
                ret.addAll(getStroke().dependsOnFeature());
            }

            if (this.getGraphicCollection() != null) {
                ret.addAll(getGraphicCollection().dependsOnFeature());
            }

            if (this.getMeasure() != null) {
                ret.addAll(getMeasure().dependsOnFeature());
            }
            return ret;
        }
}
