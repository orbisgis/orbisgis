/*
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
package org.orbisgis.legend.structure.stroke;

import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.SeParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.structure.fill.FillLegend;
import org.orbisgis.legend.structure.fill.RecodedSolidFillLegend;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFillLegend;
import org.orbisgis.legend.structure.fill.constant.NullSolidFillLegend;
import org.orbisgis.legend.structure.recode.RecodedReal;
import org.orbisgis.legend.structure.recode.RecodedString;
import org.orbisgis.legend.structure.recode.type.TypeListener;

import java.awt.*;
import java.beans.EventHandler;

/**
 * Represents {@code PenStroke} instances that just contain {@code Recode}
 * instances. These {@code Recode} must be linked to the same field and must
 * be simple analysis.
 * @author Alexis Guéganno
 */
public class RecodedPenStroke implements LegendStructure {

        private PenStroke stroke;
        private RecodedSolidFillLegend fillLegend;
        private RecodedReal widthLegend;
        private RecodedString dashLegend;

        /**
         * Builds a {@link RecodedPenStroke} from the given stroke. You must be sure that the given parameter is valid.
         * You'll receive {@link ClassCastException} and {@link UnsupportedOperationException} if it's not...
         * @param stroke The original {@code PenStroke} we want to manage through this legend.
         */
        public RecodedPenStroke(PenStroke stroke){
            this.stroke = stroke;
            SolidFill sf = (SolidFill) stroke.getFill();
            this.fillLegend = new RecodedSolidFillLegend( sf == null ?  new SolidFill(Color.BLACK,1.0) : sf);
            this.widthLegend = new RecodedReal(stroke.getWidth());
            StringParameter sp = stroke.getDashArray();
            this.dashLegend = new RecodedString(sp);
            TypeListener tl = EventHandler.create(TypeListener.class, this, "replaceWidth", "source.parameter");
            widthLegend.addListener(tl);
            TypeListener tlZ = EventHandler.create(TypeListener.class, this, "replaceDash", "source.parameter");
            dashLegend.addListener(tlZ);
        }

        public RecodedPenStroke(PenStroke stroke,
                        RecodedSolidFillLegend fillLegend,
                        RecodedReal widthLegend,
                        RecodedString dashLegend) {
                this.stroke = stroke;
                this.fillLegend = fillLegend;
                this.widthLegend = widthLegend;
                this.dashLegend = dashLegend;
                TypeListener tl = EventHandler.create(TypeListener.class, this, "replaceWidth", "source.parameter");
                widthLegend.addListener(tl);
                TypeListener tlZ = EventHandler.create(TypeListener.class, this, "replaceDash", "source.parameter");
                dashLegend.addListener(tlZ);
        }

        /**
         * Replace the {@code StringParameter} embedded in the inner PenStroke with {@code sp}. This method is called
         * when a type change occurs in the associated {@link RecodedString} happens.
         * @param sp The new {@code StringParameter}
         * @throws ClassCastException if sp is not a {@code StringParameter}
         */
        public void replaceDash(SeParameter sp){
            stroke.setDashArray((StringParameter) sp);
        }

        /**
         * Replace the {@code RealParameter} embedded in the inner PenStroke with {@code sp}. This method is called
         * when a type change occurs in the associated {@link RecodedString} happens.
         * @param sp The new {@code RealParameter}
         * @throws ClassCastException if sp is not a {@code RealParameter}
         */
        public void replaceWidth(SeParameter sp){
            stroke.setWidth((RealParameter)sp);
        }

        /**
         * Gets the inner {@link RecodedSolidFillLegend}.
         * @return the inner {@link RecodedSolidFillLegend}.
         */
        public final RecodedSolidFillLegend getFillLegend() {
                return fillLegend;
        }

        /**
         * Sets the {@code LegendStructure} that describes the fill associated to
         * the inner {@code PenStroke}.</p>
         * <p>
         *     Even if you pass null as an input, you'll get a suitable {@code Fill} in your symbology tree. This method
         *     will generate the appropriate default {@link SolidFill} as described in Symbology Encoding. We explicitly
         *     set it because it's safer and easier to manage in the upper layers.
         * </p>
         * <p>
         *     The type of {@code fill} can be :
         *     <ul><li>null</li>
         *     <li>{@link RecodedSolidFillLegend}</li>
         *     <li>{@link ConstantSolidFillLegend}</li>
         *     <li>{@link NullSolidFillLegend}</li>
         *     </ul>
         * </p>
         * @param fill The original {@link FillLegend}. It will be transformed to a {@code RecodedSolidFillLegend} if
         *             needed.
         * @throws IllegalArgumentException If the type of {@code fill}is not accepted.
         */
        public final void setFillLegend(FillLegend fill) {
                if(fill instanceof  RecodedSolidFillLegend) {
                    this.fillLegend = (RecodedSolidFillLegend) fill;
                } else if(fill instanceof ConstantSolidFillLegend){
                    this.fillLegend = new RecodedSolidFillLegend((SolidFill) fill.getFill());
                } else if(fill == null || fill instanceof NullSolidFillLegend){
                    //We must generate the expected value for SE.
                    SolidFill sf = new SolidFill(Color.BLACK,1.0);
                    fillLegend = new RecodedSolidFillLegend(sf);
                } else {
                        throw new IllegalArgumentException("Can't set the fill legend to something"
                                + "that is neither a ConstantSolidFillLegend nor"
                                + "a RecodedSolidFillLegend.");
                }
                stroke.setFill(fillLegend.getFill());
        }

        /**
         * Gets the legend that describe the width of the inner {@link PenStroke}.
         * @return The legend that describe the width of the inner {@link PenStroke}.
         */
        public final RecodedReal getWidthLegend() {
                return widthLegend;
        }

        /**
         * Sets the legend describing the behaviour of the PenStroke's width to {@code width}. The width of the inner
         * {@link PenStroke} is changed accordingly.
         * @param width The new description of the width.
         */
        public final void setWidthLegend(RecodedReal width) {
                this.widthLegend = width;
                stroke.setWidth(width.getParameter());
        }

        /**
         * Gets the LegendStructure that is used to describe the dash patterns
         * in this PenStroke.
         * @return  The {@link RecodedString} representing the associated dash pattern.
         */
        public final RecodedString getDashLegend() {
                return dashLegend;
        }

        /**
         * Sets the LegendStructure used to describe the dash patterns in this
         * {@code PenStroke}.
         * @param dash  The new dash configuration.
         */
        public final void setDashLegend(RecodedString dash) {
            this.dashLegend = dash;
            if(dash == null){
                stroke.setDashArray(null);
            } else {
                stroke.setDashArray(dash.getParameter());
            }
        }

}
