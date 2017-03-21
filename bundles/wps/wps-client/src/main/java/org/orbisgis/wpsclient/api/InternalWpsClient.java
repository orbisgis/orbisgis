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

package org.orbisgis.wpsclient.api;

import net.opengis.wps._2_0.ProcessDescriptionType;
import org.orbisgis.wpsclient.api.utils.ProcessExecutionType;
import org.orbisgis.wpsclient.api.utils.WpsJobStateListener;
import org.orbisgis.wpsservice.model.DataType;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The internal WPS client interface for the communication with WPS internal and external servers.
 * An internal WPS client contains all the methods for the communication with the OrbisGIS internal server.
 * It contains methods for the process tracking thanks to JobStateListeners and for direct interactions with the
 * internal WPS server like getting the table metadata.
 *
 * @author Sylvain PALOMINOS
 */
public interface InternalWpsClient extends WpsClient {


    /**************************/
    /** Internal WPS methods **/
    /**************************/

    /**
     * Execute and internal process. The internal process will be tracked by the client and job state updates will be
     * communicated to the listener if not null.
     *
     * @param processIdentifier Identifier of the process to execute.
     * @param dataMap Map containing the inputs/outputs values. The map key are the URI of the input/output and the
     *                value is the value Object.
     * @param listener WpsJobStateListener which is listening for the process state changes. Can be null.
     *
     * @return Unique identifier of the job executing the process.
     */
    UUID executeInternalProcess(URI processIdentifier, Map<URI, Object> dataMap, WpsJobStateListener listener);

    /**
     * Return the process with the given identifier. If no process is found, return null.
     *
     * @param processIdentifier The process identifier.
     *
     * @return The process with the given identifier or null if not found.
     */
    ProcessDescriptionType getInternalProcess(URI processIdentifier);

    /**
     * Open the UI of the process with the given identifier with the given default values. The execution type is used to
     * display the UI in normal mode or in bash mode.
     *
     * @param processIdentifier Process identifier of the process to open.
     * @param defaultValuesMap Map containing the default inputs/outputs values. The map key are the URI of the
     *                         input/output and the value is the value Object.
     * @param executionType Process execution type (normal or bash).
     */
    void openProcess(URI processIdentifier,
                     Map<URI, Object> defaultValuesMap,
                     ProcessExecutionType executionType);


    /*******************/
    /** Other methods **/
    /*******************/

    /**
     * Refresh the client list of available processes. The getCapabilities request should be re-asked to the WPS
     * servers.
     */
    void refreshAvailableScripts();

    /**
     * Return the list of the field contained by a table from a database connected to OrbisGIS.
     *
     * @param tableName Name of the table.
     * @param dataTypes Type of field accepted. If empty, accepts all the field.
     * @param excludedTypes Type of field excluded.
     *
     * @return The list of the field name matching the accepted and excluded types.
     */
    List<String> getTableFieldList(String tableName, List<DataType> dataTypes, List<DataType> excludedTypes);

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
     * Returns a map containing field information like table type, SRID...
     *
     * @param tableName Name of the table.
     * @param fieldName Name of the field.
     *
     * @return Map containing the field information.
     */
    Map<String, Object> getFieldInformation(String tableName, String fieldName);

    /**
     * Returns the list of distinct values contained by a field from a table from the database
     *
     * @param tableName Name of the table containing the field.
     * @param fieldName Name of the field containing the values.
     *
     * @return The list of distinct values of the field.
     */
    List<String> getFieldValueList(String tableName, String fieldName);

    /**
     * Returns the list of the available SRID.
     *
     * @return The list of the available SRID.
     */
    List<String> getSRIDList();

    /**
     * Adds a WpsJobListener. The listeners will be called when the clients detects an update of the state of the job
     * listened by the WpsJobStateListener
     *
     * @param listener WpsJobListener to manage.
     */
    void addJobListener(WpsJobStateListener listener);

    /**
     * Removes a WpsJobListener.
     *
     * @param listener WpsJobListener to remove.
     */
    void removeJobListener(WpsJobStateListener listener);
}
