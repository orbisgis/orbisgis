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

import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.structure.fill.FillLegend;
import org.orbisgis.legend.structure.fill.RecodedSolidFillLegend;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFillLegend;
import org.orbisgis.legend.structure.fill.constant.NullSolidFillLegend;
import org.orbisgis.legend.structure.parameter.NumericLegend;
import org.orbisgis.legend.structure.recode.RecodedReal;

/**
 * Represents {@code PenStroke} instances that just contain {@code Recode}
 * instances. These {@code Recode} must be linked to the same field and must
 * be simple analysis.
 * @author Alexis Guéganno
 */
public class RecodedPenStroke {

        private PenStroke stroke;
        private FillLegend fillLegend;
        private RecodedReal widthLegend;
        private LegendStructure dashLegend;

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

        public final NumericLegend getWidthLegend() {
                return widthLegend;
        }

        public final void setWidthLegend(NumericLegend width) {
                if(width instanceof RecodedReal){
                        this.widthLegend = (RecodedReal) width;
                        stroke.setWidth((RealParameter)width.getParameter());
                } else {
                        throw new IllegalArgumentException("Can't set the fill legend to something"
                                + "that is neither a ConstantSolidFillLegend nor"
                                + "a RecodedSolidFillLegend.");
                }
        }

        public final LegendStructure getDashLegend() {
                return dashLegend;
        }

        public final void setDashLegend(LegendStructure dash) {
                this.dashLegend = dash;
        }



}
