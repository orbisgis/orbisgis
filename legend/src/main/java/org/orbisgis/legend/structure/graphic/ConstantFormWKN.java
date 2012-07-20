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
package org.orbisgis.legend.structure.graphic;

import java.awt.Color;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.structure.fill.ConstantSolidFillLegend;
import org.orbisgis.legend.structure.literal.StringLiteralLegend;
import org.orbisgis.legend.structure.stroke.ConstantPenStrokeLegend;

/**
 * This abstract class is a common {@code LegendStructure} description for all the {@code
 * MarkGraphic} instances where the only varying parameters are the dimensions
 * of the associated {@code ViewBox}. That means the {@code Fill}, the {@code
 * Stroke} and the {@code StringParameter} containing the well-known name
 * definition are constants.
 * @author Alexis Guéganno
 */
public abstract class ConstantFormWKN extends MarkGraphicLegend {
    /**
     * Build a default {@code LegendStructure} that describes a {@code MarkGraphic}
     * instance.
     * @param viewBoxLegend
     * @param fillLegend
     * @param strokeLegend
     */
    public ConstantFormWKN(MarkGraphic mark, StringLiteralLegend wknLegend,
            LegendStructure viewBoxLegend, ConstantSolidFillLegend fillLegend,
            ConstantPenStrokeLegend strokeLegend) {
        super(mark, wknLegend, viewBoxLegend, fillLegend, strokeLegend);
    }

    /**
     * Get the {@code Color} that is used to paint the {@code SolidFill}
     * associated to this {@code ConstantWKNLegend}.
     * @return
     */
    public Color getFillColor(){
        ConstantSolidFillLegend cfl = (ConstantSolidFillLegend)getFillLegend();
        return cfl.getColor();
    }

    /**
     * Sets the {@code Color} used to paint the {@code SolidFill}
     * associated to this {@code ConstantWKNLegend}.
     * @param col
     */
    public void setFillColor(Color col){
        ConstantSolidFillLegend cfl = (ConstantSolidFillLegend)getFillLegend();
        cfl.setColor(col);
    }

    /**
     * Get the width of the line used to outline the associated {@code
     * MarkGraphic}.
     * @return
     */
    public double getLineWidth() {
        return ((ConstantPenStrokeLegend) getStrokeLegend()).getLineWidth();
    }

    /**
     * Set the width of the line used to outline the associated
     * {@code MarkGraphic}.
     * @param width
     */
    public void setLineWidth(double width) {
        ((ConstantPenStrokeLegend) getStrokeLegend()).setLineWidth(width);
    }

    /**
     * Get the color of the line used to outline the associated {@code
     * MarkGraphic}
     * @return
     */
    public Color getLineColor() {
        return ((ConstantPenStrokeLegend) getStrokeLegend()).getLineColor();
    }

    /**
     * Set the color of the line used to outline the associated {@code
     * MarkGraphic}
     * @param col
     */
    public void setLineColor(Color col) {
        ((ConstantPenStrokeLegend) getStrokeLegend()).setLineColor(col);
    }

    /**
     * Gets the dash array used to draw the outer line of this PointSymbolizer.
     * @return
     */
    public String getDashArray(){
        return ((ConstantPenStrokeLegend) getStrokeLegend()).getDashArray();
    }

    /**
     * Sets the dash array used to draw the outer line of this PointSymbolizer.
     * @param s
     */
    public void setDashArray(String s){
        ((ConstantPenStrokeLegend) getStrokeLegend()).setDashArray(s);
    }
}
