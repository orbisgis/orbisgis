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
import org.orbisgis.legend.structure.recode.*;
import org.orbisgis.legend.structure.recode.type.TypeEvent;
import org.orbisgis.legend.structure.recode.type.TypeListener;

import java.awt.*;
import java.beans.EventHandler;
import java.util.*;
import java.util.List;

/**
 * Represents {@code PenStroke} instances that just contain {@code Recode}
 * instances. These {@code Recode} must be linked to the same field and must
 * be simple analysis.
 * @author Alexis Guéganno
 */
public class RecodedPenStroke implements RecodedLegendStructure {

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
            this.dashLegend = new RecodedDash(sp);
            TypeListener tl = new TypeListener() {
                @Override
                public void typeChanged(TypeEvent te) {
                    replaceWidth(te.getSource().getParameter());
                }
            };
            widthLegend.addListener(tl);
            TypeListener tlZ = new TypeListener() {
                @Override
                public void typeChanged(TypeEvent te) {
                    replaceDash(te.getSource().getParameter());
                }
            };
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
                TypeListener tl = new TypeListener() {
                    @Override
                    public void typeChanged(TypeEvent te) {
                        replaceWidth(te.getSource().getParameter());
                    }
                };
                widthLegend.addListener(tl);
                TypeListener tlZ = EventHandler.create(TypeListener.class, this, "replaceDash", "getSource.getParameter");
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
         * Gets the legend that describe the width of the inner {@link PenStroke}.
         * @return The legend that describe the width of the inner {@link PenStroke}.
         */
        public final RecodedReal getWidthLegend() {
                return widthLegend;
        }

        /**
         * Gets the LegendStructure that is used to describe the dash patterns
         * in this PenStroke.
         * @return  The {@link RecodedString} representing the associated dash pattern.
         */
        public final RecodedString getDashLegend() {
                return dashLegend;
        }

        @Override
        public List<RecodedLegend> getRecodedLegends() {
            List<RecodedLegend> ret = fillLegend.getRecodedLegends();
            ret.add(dashLegend);
            ret.add(widthLegend);
            return ret;
        }
}
