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
package org.orbisgis.coremap.renderer.se.parameter.string;

import java.sql.ResultSet;
import java.util.Map;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.orbisgis.coremap.renderer.se.parameter.SeParameter;


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
    String getValue(ResultSet sds, long fid) throws ParameterException;

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
    String getValue(Map<String,Object> feature) throws ParameterException;

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
