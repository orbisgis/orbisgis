/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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

import org.orbisgis.coremap.renderer.se.fill.SolidFill;
import org.orbisgis.coremap.renderer.se.graphic.MarkGraphic;
import org.orbisgis.coremap.renderer.se.graphic.WellKnownName;
import org.orbisgis.coremap.renderer.se.stroke.PenStroke;

import java.awt.*;

/**
 * A simple java "tuple" that embeds the nine parameters needed to draw a graphic as defined with a {@link MarkGraphic}
 * configured with a well-known name.
 * @author alexis
 */
public class PointParameters extends AreaParameters {

    private Double width;
    private Double height;
    private WellKnownName wkn;
    /**
     * Builds a new {@code PointParameters} with the default values that are defined for a {@code SolidFill}, a {@code
     * PenStroke} and a {@link MarkGraphic} configured with a well-known name.
     */
    public PointParameters(){
        this(Color.BLACK,
                    1.0,
                    PenStroke.DEFAULT_WIDTH,
                    "",
                    new Color((int)SolidFill.GRAY50,(int)SolidFill.GRAY50,(int) SolidFill.GRAY50),
                    1.0,
                    MarkGraphic.DEFAULT_SIZE,
                    MarkGraphic.DEFAULT_SIZE,
                    WellKnownName.CIRCLE.toString());
    }

    /**
     * Build a new {@code PointParameters} with the given arguments.
     *
     * @param lineCol  The line color, set to {@link java.awt.Color#BLACK} if null
     * @param lineOp The opacity, set to 1.0 if null. If it's out of [0,1], will be set to the closest extremum of this
     *           interval.
     * @param lineWidth  The width, set to 0.25 if null.
     * @param lineDash  The dash pattern, set to the empty string if null or not valid.
     * @param fillCol the colour used to fill the area
     * @param fillOp the opacity of the fill
     * @param symWidth the width of the symbol
     * @param symHeight the height of the symbol
     * @param symWkn The well-known name that defines the shape of the mark.
     */
    public PointParameters(Color lineCol,
                          Double lineOp,
                          Double lineWidth,
                          String lineDash,
                          Color fillCol,
                          Double fillOp,
                          Double symWidth,
                          Double symHeight,
                          String symWkn) {
        super(lineCol, lineOp, lineWidth, lineDash, fillCol, fillOp);
        if(symWidth == null || Double.isNaN(symWidth)){
            if(symHeight == null || Double.isNaN(symHeight)){
                width = 3.0;
                height = 3.0;
            } else {
                height = symHeight;
                width = symHeight;
            }
        } else if(symHeight == null || Double.isNaN(symHeight)){
            width = symWidth;
            height = symWidth;
        } else {
            width = symWidth;
            height = symHeight;
        }
        wkn = WellKnownName.fromString(symWkn);
    }

    /**
     * Gets the width of the configuration
     * @return the width of the symbol configured with these parameters.
     */
    public Double getWidth(){
        return width;
    }

    /**
     * Gets the height of the configuration
     * @return the height of the symbol configured with these parameters.
     */
    public Double getHeight(){
        return height;
    }

    /**
     * Gets the well-known name that defines the form of the symbol defined by this configuration.
     * @return The well-known name as a string.
     */
    public String getWkn() {
        return wkn.toString();
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof PointParameters){
            PointParameters other = (PointParameters) o;
            boolean sup = super.equals(other);
            return sup && this.wkn.equals(other.wkn) && this.height.equals(other.height) && this.width.equals(other.width);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode(){
        int basis = super.hashCode();
        return basis + 209 * height.hashCode() + 91*width.hashCode() + 97 * wkn.hashCode();
    }
}
