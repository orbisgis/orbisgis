/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 *
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 *
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.wpsservice.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Outputs are the return values of a process. Outputs have a cardinality of one.
 * Output elements may be simple (i.e. the output has no sub-outputs attached)
 * or aggregate (i.e. the output has one or more sub-output elements attached).
 * A simple output includes a realization of the DataDescription element.
 * An aggregate output contains one or more sub-outputs.
 *
 * For more information : http://docs.opengeospatial.org/is/14-065/14-065.html#30
 *
 * @author Sylvain PALOMINOS
 */

@Deprecated
public class Output extends DescriptionType {
    /** Data type and domain of this output. */
    private DataDescription dataDescription;
    /** Nested Output. It is recommended to keep the nesting level as low as possible. */
    private List<Output> output;



    /**
     * Unique constructor providing the necessary attributes according to the WPS specification.
     * Sets the data type and domain of this output (DataDescription argument) and sets the output list to null.
     * The default values of minOccurs and maxOccurs is 1.
     * All the parameters should not be null.
     *
     * @param title      Not null title of a process, output, output.
     * @param identifier Not null unambiguous identifier of a process, output, and output.
     * @param dataDescription Not null DataDescription of this output.
     * @throws MalformedScriptException Exception thrown if one of the parameters is null.
     */
    public Output(String title, URI identifier, DataDescription dataDescription) throws MalformedScriptException {
        super(title, identifier);
        if(dataDescription == null ){
            throw new MalformedScriptException(this.getClass(), "dataDescription", "can not be null");
        }
        this.dataDescription = dataDescription;
        this.output = null;
    }

    /**
     * Unique constructor providing the necessary attributes according to the WPS specification.
     * Sets the output list with the List<Output> argument and sets the DataDescription to null;
     * The default values of minOccurs and maxOccurs is 1.
     * All the parameters should not be null or empty.
     *
     * @param title      Not null title of a process, output, output.
     * @param identifier Not null unambiguous identifier of a process, output, and output.
     * @param outputList Not null DataDescription of this output.
     * @throws MalformedScriptException Exception thrown if one of the parameters is null or empty or containing null.
     */
    public Output(String title, URI identifier, List<Output> outputList) throws MalformedScriptException {
        super(title, identifier);
        if(outputList == null){
            throw new MalformedScriptException(this.getClass(), "outputList", "can not be null");
        }
        if(outputList.isEmpty()){
            throw new MalformedScriptException(this.getClass(), "outputList", "can not be empty");
        }
        if(outputList.contains(null)){
            throw new MalformedScriptException(this.getClass(), "outputList", "can not contain a null value");
        }
        this.dataDescription = null;
        this.output = outputList;
    }

    /**
     * Sets the DataDescription of this output (the argument should not be null) and set to null the output list.
     * @param dataDescription New DataDescription.
     * @throws MalformedScriptException Exception thrown if one of the parameters is null.
     */
    public void setDataDescription(DataDescription dataDescription) throws MalformedScriptException {
        if (dataDescription == null) {
            throw new MalformedScriptException(this.getClass(), "dataDescription", "can not be null");
        }
        output = null;
        this.dataDescription = dataDescription;
    }

    /**
     * Returns the DataDescription of this output.
     * @return The DataDescription of this output.
     */
    public DataDescription getDataDescription() {
        return dataDescription;
    }


    /**
     * Sets the output list with the not null/empty list and set the dataDescription to null.
     * The list should not contain null value.
     * @param outputList List of nested output.
     * @throws MalformedScriptException Exception thrown if one of the parameters is null or empty or containing null.
     */
    public void setOutput(List<Output> outputList) throws MalformedScriptException {
        if (outputList == null) {
            throw new MalformedScriptException(this.getClass(), "outputList", "can not be null");
        }
        if (outputList.isEmpty()) {
            throw new MalformedScriptException(this.getClass(), "outputList", "can not empty");
        }
        if (outputList.contains(null)) {
            throw new MalformedScriptException(this.getClass(), "outputList", "can not contain a null value");
        }
        output = new ArrayList<>();
        for(Output i : outputList) {
            this.output.add(i);
        }
        this.dataDescription = null;
    }

    /**
     * Returns the nested output.
     * @return The nested output.
     */
    public List<Output> getOutputs() {
        return output;
    }
}
