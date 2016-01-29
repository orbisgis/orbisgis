/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2016 IRSTV (FR CNRS 2488)
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

package org.orbisgis.legend.structure.recode;

import org.orbisgis.coremap.renderer.se.parameter.string.*;
import org.orbisgis.coremap.renderer.se.parameter.ParameterUtil;

/**
 * Specialized RecodedString instance. Validation is made before adding new values in the map
 *
 * @author alexis, Sylvain PALOMINOS
 */
public class RecodedDash extends RecodedString {

    public RecodedDash(StringParameter s) {
        super(s);
    }

    /**
     * Sets the ith key of the inner {@code Recode}.  Validation is made before adding new values in the map.
     *
     * @param i
     * @param key
     */
    @Override
    public void setKey(int i, String key) {
        if (ParameterUtil.validateDashArray(key)) {
            super.setKey(i, key);
        } else {
            throw new UnsupportedOperationException("The given key is not valid.");
        }
    }

    /**
     * Adds an item in the inner {@code Recode}.  Validation is made before adding new values in the map.
     *
     * @param key
     * @param value
     */
    @Override
    public void addItem(String key, String value) {
        if (ParameterUtil.validateDashArray(value)) {
            super.addItem(key, value);
        } else {
            throw new UnsupportedOperationException("The given key is not valid.");
        }
    }

    /**
     * Sets the value that is used when no match is found for a given parameter.
     *
     * @param s
     */
    @Override
    public void setFallbackValue(String s) {
        if (ParameterUtil.validateDashArray(s)) {
            super.setFallbackValue(s);
        } else {
            throw new UnsupportedOperationException("The given key is not valid.");
        }
    }
}
