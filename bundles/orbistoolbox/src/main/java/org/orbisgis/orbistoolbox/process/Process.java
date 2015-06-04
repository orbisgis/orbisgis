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
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for moredetails.
 *
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.orbistoolbox.process;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sylvain PALOMINOS
 */

public class Process
        extends DescriptionType {
    private List<Input>
            input;
    private List<Output>
            output;
    private String
            language =
            "en";

    /**
     * constructor providing the necessary attributes according to the WPS specification.
     * All the parameters should not be null.
     *
     * @param title      Not null title of a process, input, output.
     * @param identifier Not null unambiguous identifier of a process, input, and output.
     * @throws IllegalArgumentException Exception thrown if one of the parameters is null.
     */
    public Process(String title,
                   URI identifier,
                   Output output)
            throws
            IllegalArgumentException {
        super(title,
                identifier);
        if (output ==
                null) {
            throw new IllegalArgumentException("The parameter \"output\" can not be null");
        }
        this.output =
                new ArrayList<>();
        this.output.add(output);
        this.input =
                new ArrayList<>();
    }

    /**
     * constructor providing the necessary attributes according to the WPS specification.
     * All the parameters should not be null.
     *
     * @param title      Not null title of a process, input, output.
     * @param identifier Not null unambiguous identifier of a process, input, and output.
     * @throws IllegalArgumentException Exception thrown if one of the parameters is null.
     */
    public Process(String title,
                   URI identifier,
                   List<Output> output)
            throws
            IllegalArgumentException {
        super(title,
                identifier);
        if (output ==
                null ||
                output.isEmpty()) {
            throw new IllegalArgumentException("The parameter \"output\" can not be null or empty");
        }
        this.output =
                new ArrayList<>();
        this.output.addAll(output);
        this.input =
                new ArrayList<>();
    }

    public String getLanguage() {
        return language;
    }

    public void setInput(List<Input> input) {
        this.input =
                new ArrayList<>();
        for (Input i : input) {
            this.addInput(i);
        }
    }

    public void addInput(Input input) {
        if (input ==
                null) {
            return;
        }
        if (this.input ==
                null) {
            this.input =
                    new ArrayList<>();
        }
        this.input.add(input);
    }

    public void addAllInput(List<Input> input) {
        for (Input i : input) {
            this.addInput(i);
        }
    }

    public void removeInput(Input input) {
        this.input.remove(input);
        if (this.input.isEmpty()) {
            this.input =
                    null;
        }
    }

    public void removeAllInput(List<Input> inputList) {
        for (Input i : inputList) {
            this.removeInput(i);
        }
    }

    public List<Input> getInput() {
        return input;
    }

    public void setOutput(List<Output> output) {
        if (output ==
                null |
                output.isEmpty()) {
            throw new IllegalArgumentException("The parameter \"output\" can not be null or empty");
        }
        this.output =
                new ArrayList<>();
        for (Output o : output) {
            this.addOutput(o);
        }
    }

    public void addOutput(Output output) {
        if (output ==
                null) {
            throw new IllegalArgumentException("The parameter \"output\" can not be null");
        }
        if (output ==
                null) {
            return;
        }
        if (this.output ==
                null) {
            this.output =
                    new ArrayList<>();
        }
        this.output.add(output);
    }

    public void addAllOutput(List<Output> output) {
        if (output ==
                null |
                output.isEmpty()) {
            throw new IllegalArgumentException("The parameter \"output\" can not be null or empty");
        }
        for (Output i : output) {
            this.addOutput(i);
        }
    }

    public void removeOutput(Output output) {
        if (this.output.size() ==
                1 &&
                this.output.contains(output)) {
            throw new IllegalArgumentException("The attribute \"output\" can not be empty");
        }
        this.output.remove(output);
        if (this.output.isEmpty()) {
            this.output =
                    null;
        }
    }

    public void removeAllOutput(List<Output> outputList) {
        for (Output i : outputList) {
            this.removeOutput(i);
        }
    }

    public List<Output> getOutput() {
        return output;
    }
}
