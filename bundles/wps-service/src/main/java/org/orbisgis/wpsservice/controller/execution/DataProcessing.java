/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 * <p/>
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 * <p/>
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p/>
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 * <p/>
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.wpsservice.controller.execution;

import net.opengis.wps.v_2_0.DataDescriptionType;
import net.opengis.wps.v_2_0.DescriptionType;
import org.orbisgis.wpsservice.LocalWpsService;

import java.net.URI;
import java.util.Map;

/**
 * Interface to define the DataProcessing classes.
 * The method contained by this interface will be called before and after the process to adapt the input and
 * output data to the execution
 *
 * @author Sylvain PALOMINOS
 */
public interface DataProcessing {

    void setLocalWpsService(LocalWpsService wpsService);
    Class<? extends DataDescriptionType> getDataClass();

    /**
     * Preprocess the input/output to adapt it to the process (i.e. convert a File into a table name).
     * @param inputOrOutput The DescriptionType representing the input or output.
     * @param dataMap DataMap containing the input or output values.
     * @param pel ProcessExecutionListener to log the preprocessing (can be null).
     * @return Return a stash map containing information for post processing.
     */
    Map<URI, Object> preProcessData(DescriptionType inputOrOutput, Map<URI, Object> dataMap,
                                    ProcessExecutionListener pel);

    /**
     * Postprocess the input/output to adapt it to the process (i.e. convert a File into a table name).
     * @param inputOrOutput The DescriptionType representing the input or output.
     * @param dataMap DataMap containing the input or output values.
     * @param stash DataMap containing the information coming from the preprocessing.
     * @param pel ProcessExecutionListener to log the postprocessing (can be null).
     */
    void postProcessData(DescriptionType inputOrOutput, Map<URI, Object> dataMap, Map<URI, Object> stash,
                         ProcessExecutionListener pel);
}
