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

import org.orbisgis.core.renderer.se.parameter.ParameterUtil;

import java.awt.*;

/**
 * A simple java "tuple" that embeds the fours parameters needed to draw a line defined with a {@link
 * org.orbisgis.core.renderer.se.stroke.PenStroke}.
 * @author alexis
 */
public class LineParameters {

    private Color color;
    private Double opacity;
    private Double width;
    private String dash;

    /**
     * Build a new {@code LineParameters} with the given arguments.
     * @param c The color, set to {@link Color#BLACK} if null
     * @param op The opacity, set to 1.0 if null. If it's out of [0,1], will be set to the closest extremum of this
     *           interval.
     * @param w The width, set to 0.25 if null.
     * @param d The dash pattern, set to the empty string if null or not valid.
     */
    public  LineParameters(Color c, Double op, Double w, String d){
        color = c == null ? Color.BLACK : c;
        opacity = op == null ? 1.0 : Math.min(1.0, Math.max(0,op));
        width = w == null ? .25 : w;
        dash = d == null || !ParameterUtil.validateDashArray(d)? "" : d;
    }

    /**
     * The color of the line
     * @return The color of the line
     */
    public Color getLineColor() {
        return color;
    }

    /**
     * The opacity of the line
     * @return The opacity of the line
     */
    public Double getLineOpacity() {
        return opacity;
    }

    /**
     * The width of the line
     * @return The width of the line
     */
    public Double getLineWidth() {
        return width;
    }

    /**
     * The dash pattern of the line
     * @return The dash pattern of the line
     */
    public String getLineDash() {
        return dash;
    }
}
