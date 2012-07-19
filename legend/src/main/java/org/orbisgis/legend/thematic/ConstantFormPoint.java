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
package org.orbisgis.legend.thematic;

import java.awt.Color;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.legend.structure.graphic.ConstantFormWKN;

/**
 * This class gathers methods that are common to thematic analysis where
 * the {@code Stroke}, {@code Fill} and well-known name are constant.
 * @author alexis
 */
public abstract class ConstantFormPoint extends SymbolizerLegend {

    private PointSymbolizer pointSymbolizer;

    /**
     * Basically set the associated {@link PointSymbolizer}.
     * @param symbolizer
     */
    public ConstantFormPoint(PointSymbolizer symbolizer){
        pointSymbolizer = symbolizer;
    }

    /**
     * Gets the associated {@code PointSymbolizer} instance.
     * @return
     */
    @Override
    public Symbolizer getSymbolizer() {
        return pointSymbolizer;
    }

    /**
     * Gets the {@code MarkGraphicLegend} that must be associated to the inner {@code
     * PointSymbolizer}.
     * @return
     * An instance of {@code MarkGraphicLegend}.
     */
    public abstract ConstantFormWKN getMarkGraphic();

    /**
     * A {@code ConstantFormPoint} is associated to a {@code MarkGraphic}, that
     * is filled using a given {@code Color}.
     * @return
     */
    public Color getFillColor(){
        return getMarkGraphic().getFillColor();
    }

    /**
     * Set the {@code Color} that will be used to fill the {@code MarkGraphic}.
     * @param col
     */
    public void setFillColor(Color col){
        getMarkGraphic().setFillColor(col);
    }

    /**
     * Get the width of the line that outlines the inner {@code MarkGraphic}.
     * @return
     */
    public Double getLineWidth(){
        return getMarkGraphic().getLineWidth();
    }

    /**
     * Set the width of the line that outlines the inner {@code MarkGraphic}.
     * @param width
     */
    public void setLineWidth(Double width){
        getMarkGraphic().setLineWidth(width);
    }

    /**
     * Get the colour of the line that outlines the inner {@code MarkGraphic}.
     * @return
     */
    public Color getLineColor() {
        return getMarkGraphic().getLineColor();
    }

    /**
     * Set the colour of the line that outlines the inner {@code MarkGraphic}.
     * @param col
     */
    public void setLineColor(Color col) {
        getMarkGraphic().setLineColor(col);
    }

    /**
     * Gets the dash array used to draw the outer line of this PointSymbolizer.
     * @return
     */
    public String getDashArray(){
        return getMarkGraphic().getDashArray();
    }

    /**
     * Sets the dash array used to draw the outer line of this PointSymbolizer.
     * @param s
     */
    public void setDashArray(String s){
        getMarkGraphic().setDashArray(s);
    }

}
