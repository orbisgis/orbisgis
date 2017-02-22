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
package org.orbisgis.legend;

import org.orbisgis.coremap.renderer.se.parameter.ParameterException;

/**
 * Interface for interpolation legends.
 *
 * @author Adam Gouge
 */
public interface IInterpolationLegend extends LookupFieldName {

    /**
     * Gets the data of the first interpolation point.
     *
     * @return First interpolation point data
     */
    double getFirstData();

    /**
     * Gets the data of the second interpolation point.
     *
     * @return Second interpolation point data
     */
    double getSecondData();

    /**
     * Sets the data of the first interpolation point.
     *
     * @param d First interpolation point data to set
     */
    void setFirstData(double d);

    /**
     * Sets the data of the second interpolation point
     *
     * @param d Second interpolation point data to set
     */
    void setSecondData(double d);

    /**
     * Gets the value of the first interpolation point as a double.
     *
     * @return The first interpolation point's value
     * @throws ParameterException If a problem is encountered while retrieving
     *                            the double value.
     */
    double getFirstValue() throws ParameterException;

    /**
     * Sets the value of the first interpolation point as a double.
     *
     * @param d First value to set
     */
    void setFirstValue(double d);

    /**
     * Gets the value of the second interpolation point as a double.
     *
     * @return The second interpolation point's value
     * @throws ParameterException If a problem is encountered while retrieving
     *                            the double value.
     */
    double getSecondValue() throws ParameterException;

    /**
     * Set the value of the second interpolation point as a double.
     *
     * @param d Second value to set
     */
    void setSecondValue(double d);
}
