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

package org.orbisgis.wpsserviceorbisgis;

import org.orbisgis.wpsservice.WpsServer;
import org.orbisgis.wpsservice.model.DataType;
import org.orbisgis.wpsserviceorbisgis.utils.OrbisGISWpsServerListener;

import java.net.URI;
import java.util.*;

/**
 * @author Sylvain PALOMINOS
 */
public interface OrbisGISWpsServer extends WpsServer {

    String TABLE_SRID = "TABLE_SRID";
    String TABLE_IS_SPATIAL = "TABLE_IS_SPATIAL";
    String TABLE_DIMENSION = "TABLE_DIMENSION";
    String COLUMN_NAME = "COLUMN_NAME";
    String COLUMN_TYPE = "COLUMN_TYPE";
    String TABLE_LOCATION = "TABLE_LOCATION";
    String TABLE_LABEL = "TABLE_LABEL";

    /**
     * Verify if the process corresponding to the identifier is a valid and well formed groovy wps script.
     * @param identifier URI identifier of the process to check.
     * @return True if the script is valid, false otherwise.
     */
    boolean checkProcess(URI identifier);

    /**
     * Returns the list of the table from a database connected to OrbisGIS which contains the fields with the given
     * dataTypes and without the given excludedTypes
     *
     * @param dataTypes Type of field accepted. If empty, accepts all the field.
     * @param excludedTypes Type of field excluded.
     *
     * @return The list of valid tables.
     */
    List<String> getTableList(List<DataType> dataTypes, List<DataType> excludedTypes);

    /**
     * Returns a list of map containing column information (table type, SRID, ...)
     * @param tableName Name of the table.
     * @return List of map containing the column information.
     */
    List<Map<String, Object>> getColumnInformation(String tableName);

    /**
     * Return the list of the field of a table.
     * @param tableName Name of the table.
     * @param dataTypes Type of the field accepted. If empty, accepts all the field.
     * @param excludedTypes
     * @return The list of the field name.
     */
    List<String> getColumnList(String tableName, List<DataType> dataTypes, List<DataType> excludedTypes);


    /**
     * Returns the list of distinct values contained by a field from a table from the database
     * @param tableName Name of the table containing the field.
     * @param fieldName Name of the field containing the values.
     * @return The list of distinct values of the field.
     */
    List<String> getValueList(String tableName, String fieldName);

    /**
     * Returns the list of the available SRID.
     *
     * @return The list of the available SRID.
     */
    List<String> getSRIDList();

    /**
     * Registers a WpsServerListener.
     * @param wpsServerListener WpsServerListener to register.
     */
    void addOrbisGISWpsServerListener(OrbisGISWpsServerListener wpsServerListener);

    /**
     * Unregisters a WpsServerListener.
     * @param wpsServerListener WpsServerListener to unregister.
     */
    void removeOrbisGISWpsServerListener(OrbisGISWpsServerListener wpsServerListener);
}
