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
import java.util.Locale;

/**
 * A process is a function that for each input returns a corresponding output.
 *
 * For more information : http://docs.opengeospatial.org/is/14-065/14-065.html#31
 *
 * @author Sylvain PALOMINOS
 */

public class Process extends DescriptionType {
    /** List of the process input. */
    private List<Input> input;
    /** List of the process output. */
    private List<Output> output;
    /** Language of the process. */
    private Locale language;

    /**
     * Constructor providing the necessary attributes according to the WPS specification.
     * All the parameters should not be null.
     *
     * @param title      Not null title of the process.
     * @param identifier Not null unambiguous identifier of the process.
     * @param output     Not null output.
     * @throws MalformedScriptException Exception thrown if one of the parameters is null.
     */
    public Process(String title, URI identifier, Output output) throws MalformedScriptException {
        super(title, identifier);
        if(output == null) {
            throw new MalformedScriptException(this.getClass(), "output", "can not be null");
        }
        this.output = new ArrayList<>();
        this.output.add(output);
        this.input = new ArrayList<>();
    }

    /**
     * constructor providing the necessary attributes according to the WPS specification.
     * All the parameters should not be null.
     *
     * @param title      Not null title of a process, input, output.
     * @param identifier Not null unambiguous identifier of a process, input, and output.
     * @param outputList Not null list of not null output.
     * @throws MalformedScriptException Exception thrown if one of the parameters is null or an empty list or
     * containing value.
     */
    public Process(String title, URI identifier, List<Output> outputList) throws MalformedScriptException {
        super(title, identifier);
        if(outputList == null) {
            throw new MalformedScriptException(this.getClass(), "output", "can not contain a null value");
        }
        if(outputList.isEmpty()) {
            throw new MalformedScriptException(this.getClass(), "output", "can not be empty");
        }
        if(outputList.contains(null)) {
            throw new MalformedScriptException(this.getClass(), "output", "can not be null");
        }
        this.output = new ArrayList<>();
        this.output.addAll(outputList);
        this.input = new ArrayList<>();
    }

    /**
     * Returns the language of the script.
     * @return The language of the script
     */
    public Locale getLanguage() {
        return language;
    }

    /**
     * Sets the language of the script.
     * @param language The language of the script
     */
    public void setLanguage(Locale language) {
        this.language = language;
    }

    /**
     * Sets the list of inputs.
     * @param inputList List of input.
     * @throws MalformedScriptException Exception thrown if the parameters is null or empty or containing a null value.
     */
    public void setInput(List<Input> inputList) throws MalformedScriptException {
        this.input = new ArrayList<>();
        if(inputList != null){
            for(Input i : inputList) {
                this.addInput(i);
            }
        }
    }

    /**
     * Add a new input.
     * @param input The new Input.
     * @throws MalformedScriptException Exception thrown if the parameters is null.
     */
    public void addInput(Input input) throws MalformedScriptException {
        if(input == null){
            throw new MalformedScriptException(this.getClass(), "input", "can not be null");
        }
        if(this.input == null) {
            this.input = new ArrayList<>();
        }
        this.input.add(input);
    }

    /**
     * Removes the given input.
     * @param input The input to remove.
     * @throws MalformedScriptException Exception get on removing the last input or a null one.
     */
    public void removeInput(Input input) {
        this.input.remove(input);
        if(this.input.isEmpty()) {
            this.input = null;
        }
    }

    /**
     * Returns the list of input.
     * @return The list of inputs.
     */
    public List<Input> getInput() {
        return input;
    }

    /**
     * Sets the list of outputs.
     * @param outputList List of output.
     * @throws MalformedScriptException Exception thrown if the parameters is null or empty or containing a null value.
     */
    public void setOutput(List<Output> outputList) throws MalformedScriptException {
        if(outputList == null) {
            throw new MalformedScriptException(this.getClass(), "output", "can not be null");
        }
        if(outputList.isEmpty()) {
            throw new MalformedScriptException(this.getClass(), "output", "can not be empty");
        }
        if(outputList.contains(null)) {
            throw new MalformedScriptException(this.getClass(), "output", "can not contain null value");
        }
        this.output = new ArrayList<>();
        for(Output o : outputList) {
            this.addOutput(o);
        }
    }

    /**
     * Add a new output.
     * @param output The new output.
     * @throws MalformedScriptException Exception thrown if the parameters is null.
     */
    public void addOutput(Output output) throws MalformedScriptException {
        if(output == null) {
            throw new MalformedScriptException(this.getClass(), "output", "can not be null");
        }
        if(this.output == null) {
            this.output = new ArrayList<>();
        }
        this.output.add(output);
    }

    /**
     * Removes the given output.
     * @param output The input to remove.
     * @throws MalformedScriptException Exception get on removing the last output or a null one.
     */
    public void removeOutput(Output output) throws MalformedScriptException{
        if(output == null) {
            throw new MalformedScriptException(this.getClass(), "output", "can not be null");
        }
        if(this.output.size() == 1 && this.output.contains(output)) {
            throw new MalformedScriptException(this.getClass(), "output", "can not be empty");
        }
        this.output.remove(output);
        if(this.output.isEmpty()) {
            this.output = null;
        }
    }

    /**
     * Returns the list of output.
     * @return The list of output.
     */
    public List<Output> getOutput() {
        return output;
    }
}
