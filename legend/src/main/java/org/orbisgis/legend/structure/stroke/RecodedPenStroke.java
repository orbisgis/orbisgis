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

import org.apache.log4j.NDC;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.structure.fill.FillLegend;
import org.orbisgis.legend.structure.fill.RecodedSolidFillLegend;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFillLegend;
import org.orbisgis.legend.structure.fill.constant.NullSolidFillLegend;
import org.orbisgis.legend.structure.parameter.NumericLegend;
import org.orbisgis.legend.structure.recode.RecodedReal;
import org.orbisgis.legend.structure.recode.RecodedString;

/**
 * Represents {@code PenStroke} instances that just contain {@code Recode}
 * instances. These {@code Recode} must be linked to the same field and must
 * be simple analysis.
 * @author Alexis Guéganno
 */
public class RecodedPenStroke implements LegendStructure {

        private PenStroke stroke;
        private FillLegend fillLegend;
        private RecodedReal widthLegend;
        private LegendStructure dashLegend;

        /**
         * Builds a {@link RecodedPenStroke} from the given stroke. You must be sure that the given parameter is valid.
         * You'll receive {@link ClassCastException} and {@link UnsupportedOperationException} if it's not...
         * @param stroke
         */
        public RecodedPenStroke(PenStroke stroke){
            this.stroke = stroke;
            this.fillLegend = new RecodedSolidFillLegend((SolidFill) stroke.getFill());
            this.widthLegend = new RecodedReal(stroke.getWidth());
            StringParameter sp = stroke.getDashArray();
            this.dashLegend = sp == null ? null : new RecodedString(sp);
        }

        public RecodedPenStroke(PenStroke stroke,
                        RecodedSolidFillLegend fillLegend,
                        RecodedReal widthLegend,
                        LegendStructure dashLegend) {
                this.stroke = stroke;
                this.fillLegend = fillLegend;
                this.widthLegend = widthLegend;
                this.dashLegend = dashLegend;
        }

        public final FillLegend getFillLegend() {
                return fillLegend;
        }

        /**
         * Sets the {@code LegendStructure} that describes the fill associated to
         * the inner {@code PenStroke}.
         * @param fill
         */
        public final void setFillLegend(FillLegend fill) {
                if(fill instanceof ConstantSolidFillLegend || fill instanceof RecodedSolidFillLegend){
                        this.fillLegend = fill;
                        stroke.setFill(fill.getFill());
                } else if(fill == null || fill instanceof NullSolidFillLegend){
                        stroke.setFill(null);
                } else {
                        throw new IllegalArgumentException("Can't set the fill legend to something"
                                + "that is neither a ConstantSolidFillLegend nor"
                                + "a RecodedSolidFillLegend.");
                }
        }

        /**
         * Gets the legend that describe the width of the inner {@link PenStroke}.
         * @return The legend that describe the width of the inner {@link PenStroke}.
         */
        public final NumericLegend getWidthLegend() {
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
         * @return
         */
        public final LegendStructure getDashLegend() {
                return dashLegend;
        }

        /**
         * Sets the LegendStructure used to describe the dash patterns in this
         * {@code PenStroke}.
         * @param dash
         */
        public final void setDashLegend(LegendStructure dash) {
                this.dashLegend = dash;
        }

}
