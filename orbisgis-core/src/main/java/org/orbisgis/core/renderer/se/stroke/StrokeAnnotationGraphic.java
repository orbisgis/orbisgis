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

/*
import net.opengis.se._2_0.core.StrokeAnnotationGraphicType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.RelativeOrientation;
import org.orbisgis.core.renderer.se.common.Uom;

import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
*/
/**
 * {@code StrokeAnnotationGraphic} allows graphic icons to be rendered at any position 
 * along a line and can be used to render arrowheads or street directions, for instance.
 * </p>
 * <p>It is dependant upon three parameters :
 * <ul><li>Graphic : the icon to render, as a {@link GraphicCollection}. Compulsory parameter</li>
 * <li>RelativePosition : Position between the start and the end of the line geometry. It is 
 * a {@link RealaParameter} that must be in the [0,1] interval. If not set, defaulted to 0</li>
 * <li>RelativeOrientation : The orientation of the graphic against the line. Must
 * be one of the {@link RelativeOrientation} value.</li>
 * </ul>
 * @author Maxence Laurent, Alexis Guéganno
 */

/*public final class StrokeAnnotationGraphic implements SymbolizerNode {

        private SymbolizerNode parent;
        private GraphicCollection graphic;
        private RealParameter relativePosition;
        private RelativeOrientation orientation;
*/
        /**
         * Build a new {@code StrokeAnnotationGraphic}. It's oriented NORMAL,
         * at the beginning of the line and contain a default, alone, {@link MarkGraphic}
         * in its inner {@link GraphicCollection}.
         */
/*        public StrokeAnnotationGraphic() {
                GraphicCollection gc = new GraphicCollection();
                gc.addGraphic(new MarkGraphic());
                setGraphic(gc);
                setRelativeOrientation(RelativeOrientation.NORMAL_UP);
                setRelativePosition(new RealLiteral(0));
        }
*/
        /**
         * Build a new {@code StrokeAnnotationGraphic} from the given JAXB type.
         * @param sagt
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
         */
  /*      public StrokeAnnotationGraphic(StrokeAnnotationGraphicType sagt) throws InvalidStyle {
                if (sagt.getGraphic() != null) {
                        setGraphic(new GraphicCollection(sagt.getGraphic(), this));
                }

                if (sagt.getRelativeOrientation() != null) {
                        this.setRelativeOrientation(RelativeOrientation.readFromToken(sagt.getRelativeOrientation()));
                }

                if (sagt.getRelativePosition() != null) {
                        this.setRelativePosition(SeParameterFactory.createRealParameter(sagt.getRelativePosition()));
                }
        }
*/
        /**
         * Get the inner {@link GraphicCollection} contained in this {@code StrokeAnnotationGraphic}.
         * @return 
         */
/*        public GraphicCollection getGraphic() {
                return graphic;
        }
*/
        /**
         * Set the inner {@link GraphicCollection} contained in this {@code StrokeAnnotationGraphic}
         * to {@code graphic}.
         * @param graphic 
         */
  /*      public void setGraphic(GraphicCollection graphic) {
                this.graphic = graphic;
                if (graphic != null) {
                        graphic.setParent(this);
                }
        }
*/
        /**
         * Get the orientation of the graphic against the line.
         * @return 
         */
        /*public RelativeOrientation getRelativeOrientation() {
                return orientation;
        }

        /**
         * Set the orientation of the graphic against the line.
         * @param orientation 
         */
        /*public void setRelativeOrientation(RelativeOrientation orientation) {
                this.orientation = orientation;
        }

        /**
         * Get the position of the inner {@link GraphicCollection} between the start 
         * and the end of the line geometry. The returned value is a {@link RealParameter}
         * placed in a {@link RealParameterContext#PERCENTAGE_CONTEXT percentage context}.
         * @return 
         */
        /*public RealParameter getRelativePosition() {
                return relativePosition;
        }

        /**
         * St the position of the inner {@link GraphicCollection} between the start 
         * and the end of the line geometry. The {@link RealParameter} given in argument
         * will be considered as in a {@link RealParameterContext#PERCENTAGE_CONTEXT percentage 
         * context}, i.e. as contained between 0 and 1.
         * @param relativePosition 
         */
        /*public void setRelativePosition(RealParameter relativePosition) {
                this.relativePosition = relativePosition;

                if (relativePosition != null) {
                        relativePosition.setContext(RealParameterContext.PERCENTAGE_CONTEXT);
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
         * Get a JAXB representation of this {@code StrokeAnnotationGraphic}.
         * @return 
         */
        /*StrokeAnnotationGraphicType getJaxbType() {
                StrokeAnnotationGraphicType sagt = new StrokeAnnotationGraphicType();

                if (getGraphic() != null) {
                        sagt.setGraphic(graphic.getJAXBElement());
                }

                if (getRelativeOrientation() != null) {
                        sagt.setRelativeOrientation(orientation.getJAXBType());
                }

                if (getRelativePosition() != null) {
                        sagt.setRelativePosition(relativePosition.getJAXBParameterValueType());
                }


                return sagt;
        }

        /**
         * Get a String representation of the features this {@code StrokeAnnotationGraphic} 
         * depends on.
         * @return 
         */
        /*String dependsOnFeature() {
                String result = "";

                if (graphic != null) {
                        result += " " + graphic.dependsOnFeature();
                }
                if (relativePosition != null) {
                        result += " " + relativePosition.dependsOnFeature();
                }

                return result.trim();
        }

        @Override
        public String toString() {
                return "Annotation";
        }
}
 * 
 */
