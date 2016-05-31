package org.orbisgis.wpsservice;

import net.opengis.wps._2_0.*;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This interface describe all the capabilities that should be implemented by a WPS service.
 *
 * @author Sylvain PALOMINOS
 */
public interface WpsService {

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
    WPSCapabilitiesType getCapabilities(GetCapabilitiesType getCapabilities);

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
     * Ask the WPS Service to execute the operation contained in the xml argument an returns the xml answer.
     * The xml is parsed and then the correct WPSService method is called.
     *
     * @param xml Xml containing the operation to execute.
     * @return The xml answer.
     */
    OutputStream callOperation(InputStream xml);
}
