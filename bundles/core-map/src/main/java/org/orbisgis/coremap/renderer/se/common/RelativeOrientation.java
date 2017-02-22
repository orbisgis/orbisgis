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
package org.orbisgis.coremap.renderer.se.common;

import net.opengis.se._2_0.core.RelativeOrientationType;

/**
 * {@code RelativeOrientation} is used to display Graphic instances properly.
 * @author Alexis Guéganno
 */
public enum RelativeOrientation {

    PORTRAYAL, NORMAL, NORMAL_UP, LINE;
    // TODO NORMAL_REVERSE, LINE_REVERSE ?

    /**
     * Build a new {@code RelativeOrientation} using the JAXB type given in 
     * argument.
     * @param rot
     * @return 
     * A {@code RelativeOrientation} instance.
     */
    public static RelativeOrientation readFromToken(RelativeOrientationType rot) {
        String token = rot.value();
        if (token.equalsIgnoreCase("normal")) {
            return RelativeOrientation.NORMAL;
        } else if (token.equalsIgnoreCase("normalup")) {
            return RelativeOrientation.NORMAL_UP;
        } else if (token.equalsIgnoreCase("line")) {
            return RelativeOrientation.LINE;
        } else {
            return RelativeOrientation.PORTRAYAL;
        }
    }

    /**
     * Get the {@code RelativeOrientationType} corresponding to this {@code
     * RelativeOrientation}.
     * @return 
     */
    public RelativeOrientationType getJAXBType() {
        switch (this) {
            case LINE:
                return RelativeOrientationType.LINE;
            case NORMAL:
                return RelativeOrientationType.NORMAL;
            case NORMAL_UP:
                return RelativeOrientationType.NORMAL_UP;
            case PORTRAYAL:
                return RelativeOrientationType.PORTRAYAL;
        }
        return null;

    }

    /**
     * Get a String representation of this {@code RelativeOrientation}.
     * @return 
     */
    public String getAsString() {
        switch (values()[ordinal()]) {
            case LINE:
                return "line";
            case NORMAL:
                return "normal";
            case NORMAL_UP:
                return "normalUp";
            case PORTRAYAL:
                return "portrayal";
        }
        return null;
    }
}
