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
package org.orbisgis.core.renderer.se.graphic;

import java.util.ArrayList;
import java.util.List;
import net.opengis.se._2_0.thematic.CategoryType;
import org.orbisgis.core.renderer.se.AbstractSymbolizerNode;
import org.orbisgis.core.renderer.se.FillNode;
import org.orbisgis.core.renderer.se.GraphicNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.StrokeNode;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.stroke.Stroke;

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
 * @author Maxence Laurent
 * @todo add support for stacked bar (means category fill / stroke are mandatory) and others are forbiden
 */
public final class Category  extends AbstractSymbolizerNode implements FillNode, StrokeNode, GraphicNode {

        private RealParameter measure;

        /* in order to draw bars, optionnal */
        private Fill fill;
        private Stroke stroke;

        /* In order to draw points, optionnal */
        private GraphicCollection graphic;
        private String name;

        /**
         * Build a new, empty, {@code Category}.
         */
        public Category() {
                graphic = new GraphicCollection();
                name = "";
                graphic.setParent(this);
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
                this.measure.setParent(this);
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
        public List<SymbolizerNode> getChildren() {
            List<SymbolizerNode> ls = new ArrayList<SymbolizerNode>();
            if (this.getFill() != null) {
                ls.add(this.getFill());
            }
            if (this.getStroke() != null) {
                ls.add(this.getStroke());
            }
            if (this.getGraphicCollection() != null) {
                ls.add(this.getGraphicCollection());
            }
            if (this.getMeasure() != null) {
                ls.add(this.getMeasure());
            }
            return ls;
        }
}
