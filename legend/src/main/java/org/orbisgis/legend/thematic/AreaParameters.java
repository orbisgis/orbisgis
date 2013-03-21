/**
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
package org.orbisgis.legend.thematic;

import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.stroke.PenStroke;

import java.awt.*;

/**
 * A simple java "tuple" that embeds the six parameters needed to draw a polygon as defined with a {@link
 * org.orbisgis.core.renderer.se.fill.SolidFill}.
 * @author alexis
 */
public class AreaParameters extends LineParameters {

    private Color fillColor;
    private Double fillOpacity;

    /**
     * Builds a new {@code AreaParameters} with the default values that are defined for a {@code SolidFill} and a {@code
     * PenStroke}.
     */
    public AreaParameters(){
        this(Color.BLACK,
                    1.0,
                    .25,
                    "",
                    new Color((int)SolidFill.GRAY50,(int)SolidFill.GRAY50,(int)SolidFill.GRAY50),
                    1.0);
    }

    /**
     * Build a new {@code AreaParameters} with the given arguments.
     *
     * @param lineCol  The line color, set to {@link java.awt.Color#BLACK} if null
     * @param lineOp The opacity, set to 1.0 if null. If it's out of [0,1], will be set to the closest extremum of this
     *           interval.
     * @param lineWidth  The width, set to 0.25 if null.
     * @param lineDash  The dash pattern, set to the empty string if null or not valid.
     */
    public AreaParameters(Color lineCol,
                          Double lineOp,
                          Double lineWidth,
                          String lineDash,
                          Color fillCol,
                          Double fillOp) {
        super(lineCol, lineOp, lineWidth, lineDash);
        fillColor = fillCol == null ? new Color((int)SolidFill.GRAY50,(int)SolidFill.GRAY50,(int)SolidFill.GRAY50) : fillCol;
        fillOpacity = fillOp == null ? SolidFill.DEFAULT_OPACITY : fillOp;
    }

    /**
     * Gets the Color of the fill of this configuration.
     * @return The color we want to use to fill the polygon.
     */
    public Color getFillColor() {
        return fillColor;
    }

    /**
     * Gets the opacity of the fill of this configuration.
     * @return The opacity of the colour we want to use to fill the polygon.
     */
    public Double getFillOpacity() {
        return fillOpacity;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof AreaParameters){
            AreaParameters other = (AreaParameters) o;
            boolean sup = super.equals(other);
            return sup && this.fillColor.equals(other.fillColor) && this.fillOpacity.equals(other.fillOpacity);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode(){
        int basis = super.hashCode();
        return basis + 113 * fillOpacity.hashCode() + 41*fillColor.hashCode();
    }
}
