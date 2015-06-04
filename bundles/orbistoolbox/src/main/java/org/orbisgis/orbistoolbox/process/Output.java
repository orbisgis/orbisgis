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
import java.util.List;

/**
 * @author Sylvain PALOMINOS
 */

public class Output
        extends DescriptionType {
    private DataDescription
            dataDescription;
    private List<Output>
            output;

    /**
     * Unique constructor providing the necessary attributes according to the WPS specification.
     * All the parameters should not be null.
     *
     * @param title      Not null title of a process, input, output.
     * @param identifier Not null unambiguous identifier of a process, input, and output.
     * @throws IllegalArgumentException Exception thrown if one of the parameters is null.
     */
    public Output(String title,
                  URI identifier)
            throws
            IllegalArgumentException {
        super(title,
                identifier);
    }

    public void setDataDescription(DataDescription dataDescription) {
        if (dataDescription ==
                null) {
            return;
        }
        output =
                null;
        this.dataDescription =
                dataDescription;
    }

    public DataDescription getDataDescription() {
        return dataDescription;
    }

    public void setOutput(List<Output> outputList) {
        if (outputList ==
                null ||
                outputList.isEmpty()) {
            return;
        }
        output =
                null;
        this.output =
                outputList;
    }

    public List<Output> getOutput() {
        return output;
    }
}
