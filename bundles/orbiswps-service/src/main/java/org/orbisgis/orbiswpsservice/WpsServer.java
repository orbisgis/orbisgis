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

package org.orbisgis.orbiswpsservice;

import net.opengis.wps._2_0.*;
import org.orbisgis.orbiswpsservice.controller.process.ProcessIdentifier;
import org.orbisgis.orbiswpsservice.utils.WpsServerListener;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This interface describe all the capabilities that should be implemented by a WPS server.
 *
 * A WPS Server is a web server that provides access to simple or complex computational processing services.
 *
 * @author Sylvain PALOMINOS
 */
public interface WpsServer {

    /**
     * This operation allows a client to retrieve service metadata, basic process offerings, and the available
     * processes present on a WPS server.
     *
     * @param getCapabilities Request to a WPS server to perform the GetCapabilities operation.
     *                        This operation allows a client to retrieve a Capabilities XML document providing
     *                        metadata for the specific WPS server.
     * @return WPS GetCapabilities operation response.
     *             This document provides clients with service metadata about a specific service instance,
     *             including metadata about the processes that can be executed.
     *             Since the server does not implement the updateSequence and Sections parameters,
     *             the server shall always return the complete Capabilities document,
     *             without the updateSequence parameter.
     */
    Object getCapabilities(GetCapabilitiesType getCapabilities);

    /**
     * The DescribeProcess operation allows WPS clients to query detailed process descriptions for the process
     * offerings.
     *
     * @param describeProcess WPS DescribeProcess operation request.
     * @return List structure that is returned by the WPS DescribeProcess operation.
     *         Contains XML descriptions for the queried process identifiers.
     */
    ProcessOfferings describeProcess(DescribeProcess describeProcess);

    /**
     * The Execute operation allows WPS clients to run a specified process implemented by a server,
     * using the input parameter values provided and returning the output values produced.
     * Inputs may be included directly in the Execute request (by value), or reference web accessible resources
     * (by reference).
     * The outputs may be returned in the form of an XML response document,
     * either embedded within the response document or stored as web accessible resources.
     * Alternatively, for a single output, the server may be directed to return that output in its raw form without
     * being wrapped in an XML response document.
     *
     * @param execute The Execute request is a common structure for synchronous and asynchronous execution.
     *                It inherits basic properties from the RequestBaseType and contains additional elements that
     *                identify the process that shall be executed, the data inputs and outputs, and the response type
     *                of the service.
     * @return Depending on the desired execution mode and the response type declared in the execute request,
     * the execute response may take one of three different forms:
     * A response document, a StatusInfo document, or raw data.
     */
    Object execute(ExecuteRequestType execute);

    /**
     * WPS GetStatus operation request. This operation is used to query status information of executed processes.
     * The response to a GetStatus operation is a StatusInfo document or an exception.
     * Depending on the implementation, a WPS may "forget" old process executions sooner or later.
     * In this case, there is no status information available and an exception shall be returned instead of a
     * StatusInfo response.
     *
     * @param getStatus GetStatus document. It contains an additional element that identifies the JobID of the
     *                  processing job, of which the status shall be returned.
     * @return StatusInfo document.
     */
    StatusInfo getStatus(GetStatus getStatus);

    /**
     * WPS GetResult operation request. This operation is used to query the results of asynchrously
     * executed processes. The response to a GetResult operation is a wps:ProcessingResult, a raw data response, or an exception.
     * Depending on the implementation, a WPS may "forget" old process executions sooner or later.
     * In this case, there is no result information available and an exception shall be returned.
     *
     * @param getResult GetResult document. It contains an additional element that identifies the JobID of the
     *                  processing job, of which the result shall be returned.
     * @return Result document.
     */
    Result getResult(GetResult getResult);

    /**
     * The dismiss operation allow a client to communicate that he is no longer interested in the results of a job.
     * In this case, the server may free all associated resources and “forget” the JobID.
     * For jobs that are still running, the server may cancel the execution at any time.
     * For jobs that were already finished, the associated status information and the stored results may be deleted
     * without further notice, regardless of the expiration time given in the last status report.
     * @param dismiss Dismiss request.
     * @return StatusInfo document.
     */
    StatusInfo dismiss(Dismiss dismiss);

    /**
     * Ask the WPS Server to execute the operation contained in the xml argument an returns the xml answer.
     * The xml is parsed and then the correct WPSService method is called.
     *
     * @param xml Xml containing the operation to execute.
     * @return The xml answer.
     */
    OutputStream callOperation(InputStream xml);

    /**
     * Cancel the running process corresponding to the given URI.
     * @param jobId Id of the job to cancel.
     */
    void cancelProcess(UUID jobId);

    /**
     * Enumeration of the supported databases
     */
    enum Database {H2GIS, POSTGIS}

    /**
     * Returns the database which is connected to the WPS server.
     * @return The database which is connected to the WPS server.
     */
    Database getDatabase();

    /**
     * Sets the database which is connected to the WPS server.
     * @param database The database which is connected to the WPS server.
     */
    void setDatabase(Database database);

    /**
     * Add a local groovy file or directory of processes to the wps service.
     * @param f  File object to add to the service.
     * @param iconName Icon file name associated to the script
     * @param isDefaultScript True if the scripts are default scripts (unremovable). False otherwise
     * @param nodePath
     * @return
     */
    List<ProcessIdentifier> addProcess(File f, String[] iconName, boolean isDefaultScript, String nodePath);

    /**
     * Remove the process corresponding to the given codeType.
     * @param identifier URI identifier of the process.
     */
    void removeProcess(URI identifier);

    /**
     * Adds to the server execution properties which will be set to the GroovyObject for the execution.
     * Those properties will be accessible inside the groovy script as variables which name is the map entry key.
     * For example :
     * If the propertiesMap contains <"message", "HelloWorld">, inside the groovy script you can print the message this
     * way : 'print message'
     * @param propertiesMap Map containing the properties to be passed to the GroovyObject
     */
    void addGroovyProperties(Map<String, Object> propertiesMap);

    /**
     * Removes the properties already set for the GroovyObject for the execution.
     * @param propertiesMap Map containing the properties to be removed
     */
    void removeGroovyProperties(Map<String, Object> propertiesMap);

    /**
     * Returns the path of the folder containing the WPS groovy scripts.
     * @return The path of the folder containing the WPS groovy scripts.
     */
    String getScriptFolder();

    /**
     * Sets the path of the folder containing the WPS groovy scripts.
     * @param scriptFolder The path of the folder containing the WPS groovy scripts.
     */
    void setScriptFolder(String scriptFolder);

    /**
     * Registers a WpsServerListener.
     * @param wpsServerListener WpsServerListener to register.
     */
    void addWpsServerListener(WpsServerListener wpsServerListener);

    /**
     * Unregisters a WpsServerListener.
     * @param wpsServerListener WpsServerListener to unregister.
     */
    void removeWpsServerListener(WpsServerListener wpsServerListener);
}
