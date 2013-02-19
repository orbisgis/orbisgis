/*
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
package org.orbisgis.legend.thematic.recode;

import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.legend.structure.fill.RecodedSolidFillLegend;
import org.orbisgis.legend.structure.recode.*;
import org.orbisgis.legend.structure.stroke.RecodedPenStroke;
import org.orbisgis.legend.thematic.SymbolizerLegend;
import org.orbisgis.legend.thematic.uom.StrokeUom;

import java.util.List;

/**
 * Wrapper for lines made of a {@code PenStroke} where parameters are made of
 * {@code Recode} instances on a common field or of {@code Literal}.
 * @author Alexis Guéganno
 */
public class RecodedLine extends AbstractRecodedLegend implements StrokeUom {

        private final LineSymbolizer ls;
        private final RecodedPenStroke ps;

        /**
         * Default constructor. Builds a {@code RecodedLine} from scratch using a constant {@link LineSymbolizer} that
         * embeds a constant {@link PenStroke}.
         */
        public RecodedLine(){
            this(new LineSymbolizer());
        }

        /**
         * Builds a new {@code RecodedLine} instance from the given {@link LineSymbolizer}. Take care to validate the
         * configuration of the symbolizer before calling this constructor.
         * @param sym The original LineSymbolizer.
         * @throws ClassCastException
         * @throws UnsupportedOperationException If the inner stroke is not a {@link PenStroke} instance.
         */
        public RecodedLine(LineSymbolizer sym){
            ls=sym;
            Stroke p = ls.getStroke();
            if(p instanceof PenStroke){
                ps=new RecodedPenStroke((PenStroke)p);
            } else {
                throw new UnsupportedOperationException("Can't build a RecodedLine with such a Stroke: "+p.getClass().getName());
            }
        }

        /**
         * Gets the wrapper that manages the width of the line.
         * @return
         */
        public RecodedReal getLineWidth(){
                return ps.getWidthLegend();
        }

        /**
         * Gets the wrapper that manages the opacity of the line.
         * @return
         */
        public RecodedReal getLineOpacity(){
                return (RecodedReal) ps.getFillLegend().getFillOpacityLegend();
        }

        /**
         * Gets the wrapper that manages the color of the line.
         * @return
         */
        public RecodedColor getLineColor(){
                return (RecodedColor) ps.getFillLegend().getFillColorLegend();
        }

        /**
         * Gets the wrapper that manages the dash pattern of the line.
         * @return
         */
        public RecodedString getLineDash() {
                return ps.getDashLegend();
        }

        @Override
        public Symbolizer getSymbolizer() {
                return ls;
        }

        @Override
        public String getLegendTypeName() {
                return "Recoded Line";
        }

        @Override
        public Uom getStrokeUom() {
                return ls.getStroke().getUom();
        }

        @Override
        public void setStrokeUom(Uom u) {
                ls.getStroke().setUom(u);
        }

        @Override
        public List<RecodedLegend> getRecodedLegends() {
            return ps.getRecodedLegends();
        }
}
