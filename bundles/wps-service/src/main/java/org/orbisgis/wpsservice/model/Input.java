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
 * Inputs are arguments to a process.
 * Inputs have a cardinality in order to (1) pass multiple values with the same identifier to a process,
 * or (2) declare process inputs as optional (cardinality “0”).
 * Input elements may be simple (i.e. the input has no sub-inputs attached) or aggregate
 * (i.e. the input has one or more sub-input elements attached).
 * A simple input includes a realization of the DataDescription element.
 * An aggregate input contains one or more sub-inputs.
 *
 * For more information : http://docs.opengeospatial.org/is/14-065/14-065.html#30
 *
 * @author Sylvain PALOMINOS
 */

public class Input extends DescriptionType {
    /** Minimum number of times that values for this parameter are required. */
    private int minOccurs;
    /** Maximum number of times that this parameter may be present. */
    private int maxOccurs;
    /** Data type and domain of this input. */
    private DataDescription dataDescription;
    /** Nested Input. It is recommended to keep the nesting level as low as possible.*/
    private List<Input> input;

    /**
     * Unique constructor providing the necessary attributes according to the WPS specification.
     * Sets the data type and domain of this input (DataDescription argument) and sets the input list to null.
     * The default values of minOccurs and maxOccurs is 1.
     * All the parameters should not be null.
     *
     * @param title      Not null title of a process, input, input.
     * @param identifier Not null unambiguous identifier of a process, input, and input.
     * @param dataDescription Not null DataDescription of this input.
     * @throws MalformedScriptException Exception thrown if one of the parameters is null.
     */
    public Input(String title, URI identifier, DataDescription dataDescription) throws MalformedScriptException {
        super(title, identifier);
        if(dataDescription == null ){
            throw new MalformedScriptException(this.getClass(), "dataDescription", "can not be null");
        }
        this.minOccurs = 1;
        this.maxOccurs = 1;
        this.dataDescription = dataDescription;
        this.input = null;
    }

    /**
     * Unique constructor providing the necessary attributes according to the WPS specification.
     * Sets the input list with the List<Input> argument and sets the DataDescription to null;
     * The default values of minOccurs and maxOccurs is 1.
     * All the parameters should not be null or empty.
     *
     * @param title      Not null title of a process, input, input.
     * @param identifier Not null unambiguous identifier of a process, input, and input.
     * @param inputList Not null DataDescription of this input.
     * @throws MalformedScriptException Exception thrown if one of the parameters is null or empty or containing null.
     */
    public Input(String title, URI identifier, List<Input> inputList) throws MalformedScriptException {
        super(title, identifier);
        if(inputList == null){
            throw new MalformedScriptException(this.getClass(), "inputList", "can not be null");
        }
        if(inputList.isEmpty()){
            throw new MalformedScriptException(this.getClass(), "inputList", "can not be empty");
        }
        if(inputList.contains(null)){
            throw new MalformedScriptException(this.getClass(), "inputList", "can not contain a null value");
        }
        this.minOccurs = 1;
        this.maxOccurs = 1;
        this.dataDescription = null;
        this.input = inputList;
    }

    /**
     * Sets the DataDescription of this input (the argument should not be null) and set to null the input list.
     * @param dataDescription New DataDescription.
     */
    public void setDataDescription(DataDescription dataDescription) throws MalformedScriptException {
        if (dataDescription == null) {
            throw new MalformedScriptException(this.getClass(), "dataDescription", "can not be null");
        }
        input = null;
        this.dataDescription = dataDescription;
    }

    /**
     * Returns the DataDescription of this input.
     * @return The DataDescription of this input.
     */
    public DataDescription getDataDescription() {
        return dataDescription;
    }


    /**
     * Sets the input list with the not null/empty list and set the dataDescription to null.
     * The list should not contain null value.
     * @param inputList List of nested input.
     * @throws MalformedScriptException Exception thrown if one of the parameters is null or empty or containing null.
     */
    public void setInput(List<Input> inputList) throws MalformedScriptException {
        if (inputList == null) {
            throw new MalformedScriptException(this.getClass(), "inputList", "can not be null");
        }
        if (inputList.isEmpty()) {
            throw new MalformedScriptException(this.getClass(), "inputList", "can not be empty");
        }
        if (inputList.contains(null)) {
            throw new MalformedScriptException(this.getClass(), "inputList", "can not contain a null value");
        }
        input = new ArrayList<>();
        for(Input i : inputList) {
            this.input.add(i);
        }
        this.dataDescription = null;
    }

    /**
     * Returns the nested input.
     * @return The nested input.
     */
    public List<Input> getInputs() {
        return input;
    }

    /**
     * Returns the minimum number of times that values for this parameter are required.
     * @return The minimum number of times that values for this parameter are required.
     */
    public int getMinOccurs() {
        return minOccurs;
    }

    /**
     * Sets the minimum number of times that values for this parameter are required.
     * An occurrence of 0 means that the input is optional
     * A negative value sets the occurrence to 0.
     * If minOccurs > maxOccurs, minOccurs = maxOccurs.
     * @@param The minimum number of times that values for this parameter are required.
     */
    public void setMinOccurs(int minOccurs) {
        if(minOccurs<0){
            this.minOccurs = 0;
        }
        else{
            this.minOccurs = minOccurs;
        }
        if(this.maxOccurs <= this.minOccurs){
            this.minOccurs = this.maxOccurs;
        }
    }

    /**
     * Returns the maximum number of times that values for this parameter are required.
     * @return The maximum number of times that values for this parameter are required.
     */
    public int getMaxOccurs() {
        return maxOccurs;
    }

    /**
     * Sets the maximum number of times that values for this parameter are required.
     * An occurrence of 0 means that the input is optional
     * A negative value sets the occurrence to 0.
     * If minOccurs < maxOccurs, maxOccurs= minOccurs.
     * @@param The maximum number of times that values for this parameter are required.
     */
    public void setMaxOccurs(int maxOccurs) {
        if(maxOccurs<0){
            this.maxOccurs = 0;
        }
        else{
            this.maxOccurs = maxOccurs;
        }
        if(this.maxOccurs <= this.minOccurs){
            this.maxOccurs = this.minOccurs;
        }
    }
}
