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
package org.orbisgis.legend.structure.literal;

import org.orbisgis.coremap.renderer.se.parameter.SeParameter;
import org.orbisgis.coremap.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.legend.structure.parameter.ParameterLegend;

import java.awt.*;

/**
 * {@code LegendStructure} associated to a numeric constant, that is represented as a
 * {@code ColorLiteral} in the SE model.
 *
 * @author Alexis Guéganno
 */
public class ColorLiteralLegend implements ParameterLegend {

        private ColorLiteral cl;

        /**
         * Build a new {@code RealLiteralLegend} that is associated to the
         * {@code ColorLiteral r}.
         * @param literal the input {@link ColorLiteral}
         */
        public ColorLiteralLegend(ColorLiteral literal){
                cl = literal;
        }

        /**
         * Get the {@code ColorLiteral} associated with this {@code
         * RealLiteralLegend}.
         * @return
         */
        public ColorLiteral getLiteral(){
                return cl;
        }

        /**
         * Gets the {@code Color} contained in the inner {@code ColorLiteral}.
         * @return
         */
        public Color getColor() {
            return cl.getColor(null, 0);
        }

        /**
         * Sets the {@code Color} contained in the inner {@code ColorLiteral}.
         * @param col
         */
        public void setColor(Color col) {
            cl.setColor(col);
        }

        @Override
        public SeParameter getParameter() {
                return getLiteral();
        }
}
