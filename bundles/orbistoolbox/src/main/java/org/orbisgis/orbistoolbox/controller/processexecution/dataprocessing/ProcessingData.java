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

package org.orbisgis.orbistoolbox.controller.processexecution.dataprocessing;

import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;
import org.orbisgis.orbistoolbox.model.DataDescription;
import org.orbisgis.orbistoolbox.model.DescriptionType;

import java.net.URI;
import java.util.Map;

/**
 * Interface for the pre and post processing of data.
 *
 * @author Sylvain PALOMINOS
 **/

public interface ProcessingData {

    /**
     * Returns the DataDescription class that this class can pre and post process.
     * @return DataDescription class.
     */
    Class<? extends DataDescription> getDataClass();

    /**
     * PreProcess the data.
     * @param inputOrOutput Input or Output to preProcess.
     * @param inputDataMap Data map of the input.
     * @param outputDataMap Data map of the output.
     * @param shell GroovyShell used to manipulate the data (like loading a shapeFile in OrbisGIS).
     */
    void preProcessing(DescriptionType inputOrOutput,
                                Map<URI, Object> inputDataMap,
                                Map<URI, Object> outputDataMap,
                                GroovyShell shell);
    /**
     * PostProcess the data.
     * @param inputOrOutput Input or Output to preProcess.
     * @param inputDataMap Data map of the input.
     * @param outputDataMap Data map of the output.
     * @param shell GroovyShell used to manipulate the data (like exporting a shapeFile).
     * @param groovyObject GroovyObject containing the process result.
     */
    void postProcessing(DescriptionType inputOrOutput,
                                Map<URI, Object> inputDataMap,
                                Map<URI, Object> outputDataMap,
                                GroovyShell shell,
                                GroovyObject groovyObject);
}
