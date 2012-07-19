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
package org.orbisgis.core.renderer.se.stroke;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.TextStrokeType;
import org.gdms.data.values.Value;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.label.LineLabel;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;

/**
 * {@code TexteStroke} is used to render text labels along a line. It is useful 
 * to add informations to the {@code CompoundStroke} elements.
 * It is dependant on a {@link LineLabel}, to store the text to render, and the styling
 * details used for the rendering.
 * @author maxence, alexis
 */
public final class TextStroke extends Stroke {

        private LineLabel lineLabel;

        /**
         * Builds a new {@code TexteStroke} with an inner default {@link LineLabel}.
         */
        public TextStroke() {
                setLineLabel(new LineLabel());
        }

        /**
         * Build a new {@code TexteStroke} using the given JAXB {@code TextStrokeType}.
         * @param tst
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
         */
        TextStroke(TextStrokeType tst) throws InvalidStyle {
                super(tst);
                if (tst.getLineLabel() != null) {
                        setLineLabel(new LineLabel(tst.getLineLabel()));
                }
        }

        /**
         * Build a new {@code TexteStroke} using the given {@code JABElement}.
         * @param s
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
         */
        TextStroke(JAXBElement<TextStrokeType> s) throws InvalidStyle {
                this(s.getValue());
        }

        /**
         * Get the {@link LineLabel} associated to this {@code TextStroke}.
         * @return
         * A {@link LineLabel} that contains all the informations needed to
         * render the text.
         */
        public LineLabel getLineLabel() {
                return lineLabel;
        }

        /**
         * Set the {@link LineLabel} associated to this {@code TextStroke}.
         * @param lineLabel
         */
        public void setLineLabel(LineLabel lineLabel) {
                this.lineLabel = lineLabel;

                if (lineLabel != null) {
                        lineLabel.setParent(this);
                }
        }

        @Override
        public void draw(Graphics2D g2, Map<String,Value> map, Shape shp,
                        boolean selected, MapTransform mt, double offset) throws ParameterException, IOException {
                if (this.lineLabel != null) {
                        lineLabel.draw(g2, map, shp, selected, mt, null);
                }
        }

        @Override
        public JAXBElement<TextStrokeType> getJAXBElement() {
                ObjectFactory of = new ObjectFactory();
                return of.createTextStroke(this.getJAXBType());
        }

        /**
         * Build a new {@link TextStrokeType} representing this {@code TextStroke}.
         * @return
         */
        public TextStrokeType getJAXBType() {
                TextStrokeType s = new TextStrokeType();

                this.setJAXBProperties(s);

                if (lineLabel != null) {
                        s.setLineLabel(lineLabel.getJAXBType());
                }

                return s;
        }

        @Override
        public HashSet<String> dependsOnFeature() {
                return lineLabel.dependsOnFeature();
        }

        @Override
        public UsedAnalysis getUsedAnalysis() {
                UsedAnalysis ua = new UsedAnalysis();
                if(lineLabel != null){
                    ua.merge(lineLabel.getUsedAnalysis());
                }
                return ua;
        }

        @Override
        public Double getNaturalLength(Map<String,Value> map,
                        Shape shp, MapTransform mt) throws ParameterException, IOException {
                Rectangle2D bounds = lineLabel.getLabel().getBounds(null, map, mt);
                return bounds.getWidth();
        }

        @Override
        public Uom getUom() {
                return parent.getUom();
        }
}
