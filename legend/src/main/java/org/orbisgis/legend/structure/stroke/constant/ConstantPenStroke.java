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
package org.orbisgis.legend.structure.stroke.constant;

import java.awt.Color;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFill;
import org.orbisgis.legend.structure.stroke.StrokeLegend;
/**
 * Interface to be implemented by legends that are associated to constant {@link PenStroke}.
 * @author Alexis Guéganno
 */
public interface ConstantPenStroke extends StrokeLegend {

        /**
         * Gets the unit of measure used to drawn the underlying {@link PenStroke}.
         * @return
         */
        Uom getStrokeUom();
        
        /**
         * Sets the unit of measure used to drawn the underlying {@link PenStroke}.
         * @param u 
         */
        void setStrokeUom(Uom u);

        /**
         * Gets the analysis obtained from the inner solid fill.
         * @return
         */
        ConstantSolidFill getFillLegend();

        /**
         * Gets the color of the associated {@code PenStroke}.
         * @return 
         */
        Color getLineColor();

        /**
         * Sets the Color of the associated {@code PenStroke}.
         * @param col
         */
        void setLineColor(Color col);

        /**
         * Gets the width of the associated {@code PenStroke}.
         * @return
         */
         double getLineWidth();

        /**
         * Sets the width of the associated {@code PenStroke}.
         * @param width
         */
         void setLineWidth(double width);

        /**
         * Gest the {@code String} that represents the dash pattern for the
         * associated {@code PenStroke}.
         * @return
         * The dash array in a string. Can't be null. Even if the underlying
         * {@code Symbolizer} does not have such an array, an empty {@code
         * String} is returned.
         */
         String getDashArray();

        /**
        * Sets the {@code String} that represents the dash pattern for the
         * associated {@code PenStroke}.
        * @param dashes
        */
        void setDashArray(String str);

        /**
         * Gets the opacity of the associated {@code PenStroke}.
         * @return
         */
         double getLineOpacity();

        /**
         * Sets the opacity of the associated {@code PenStroke}.
         * @param tr
         */
         void setLineOpacity(double tr);
}
