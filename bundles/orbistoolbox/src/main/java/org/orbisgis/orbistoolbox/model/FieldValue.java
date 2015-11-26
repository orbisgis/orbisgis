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

package org.orbisgis.orbistoolbox.model;

import java.net.URI;

/**
 * FieldValue represent one or more values from a DataField.
 * @author Sylvain PALOMINOS
 */
public class FieldValue extends ComplexData{

    /** Identifier of the 'parent' DataField */
    private URI dataFieldIdentifier;
    /** Enable the selection of more than one value if true.*/
    private boolean multiSelection;

    /**
     * Main constructor
     * @param format Format of the data accepted.
     * @param dataFieldIdentifier Identifier of the 'parent' dataField.
     * @param multiSelection Enable or not the selection of more than one value.
     * @throws MalformedScriptException
     */
    public FieldValue(Format format, URI dataFieldIdentifier, boolean multiSelection) throws MalformedScriptException {
        super(format);
        this.multiSelection = multiSelection;
        this.dataFieldIdentifier = dataFieldIdentifier;
    }

    /**
     * Sets the URI of the parent DataField.
     * @param dataFieldIdentifier URI of the DataField.
     */
    public void setDataFieldIdentifier(URI dataFieldIdentifier){
        this.dataFieldIdentifier = dataFieldIdentifier;
    }

    /**
     * Returns the DataField identifier.
     * @return The DataField identifier.
     */
    public URI getDataFieldIdentifier(){
        return dataFieldIdentifier;
    }

    /**
     * Set if more than one value can be selected.
     * @param multiSelection True if more than one value can be selected, false otherwise.
     */
    public void setMultiSelection(boolean multiSelection){
        this.multiSelection = multiSelection;
    }

    /**
     * Tells if more than one value can be selected.
     * @return True if more than one value can be selected, false otherwise.
     */
    public boolean getMuliSelection(){
        return multiSelection;
    }
}
