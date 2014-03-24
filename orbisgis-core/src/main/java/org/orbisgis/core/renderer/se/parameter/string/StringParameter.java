/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.renderer.se.parameter.string;

import java.util.Map;
import org.gdms.data.values.Value;
import org.gdms.driver.DataSet;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameter;


/**
 * A String value, stored as a SE parameter.</p><p>
 * A restriction list can be associated to a {@code StringParameter}. This list is used
 * to force values of this parameter to match one of the entries of the list.
 * @author Maxence Laurent, Alexis Guéganno.
 * @todo implement 05-077r4 11.6.1, 11.6.2, 11.6.3 (String, number and date formating)
 */
public interface StringParameter extends SeParameter {
    
    
    //TODO Is (DataSet, featureId) the right way to access a feature ?
    /**
         * Retrieve the {@code String} value associated to this {@code
         * StringParameter}, using informations stored in {@code sds} at index
         * {@code fid}. It can be retrieved using the given {@code datasource}
         * or not depending on the realization of this interface.
         * @param sds
         * The {@code DataSet} where to search.
         * @param fid
         * The entry where to get the value in the data source. Note that as we don't 
         * know the column where to search, this information must be given externally
         * (as in {@code Valuereference}, for instance.
         * @return
         * A {@code String} instance.
         * @throws ParameterException 
         */
    String getValue(DataSet sds, long fid) throws ParameterException;

    /**
     * Retrieve the {@code String} value associated to this {@code StringParameter}
     * using the informations stored in the given map. If this parameter depends
     * on one or more external values, they are supposed to be available in the
     * given map through their {@code Value} representation.
     * @param feature
     * Values that may be needed in this {@code StringParameter}, mapped to
     * the name of the field they come from.
     * @return
     * A {@code String} instance.
     * @throws ParameterException
     */
    String getValue(Map<String,Value> feature) throws ParameterException;

    /**
     * Set the list of restrictions</p><p>
     * Restrictions are used to force {@code StringParameter} instances to match one 
     * of the {@code String}s of the list. Note that comparisons are made ignoring
     * case.
     * @param list
     * @throws InvalidString
     */
    void setRestrictionTo(String[] list);
}
