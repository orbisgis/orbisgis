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

package org.orbisgis.orbistoolbox.process.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Dexcribes the possible literal value that can be use. It can be :
 * - A list of allowed values
 *      or
 * - A list valid with a renference
 *      or
 * - Any values
 *
 * For more informations : http://docs.opengeospatial.org/is/14-065/14-065.html#26
 *
 * @author Sylvain PALOMINOS
 */

public class PossibleLiteralValuesChoice {
    /** List of all valid values and/or ranges of values for this quantity. */
    private List<Values> allowedValues;
    /** Specifies that any value is allowed for this quantity. */
    private boolean anyValue;
    /** Reference to list of all valid values and/or ranges of values for this quantity. */
    private URI valuesReference;

    /**
     * Sets the allowed values as a list of valid values.
     * @param allowedValues List of valid values.
     * @throws IllegalArgumentException Exception ge on setting a list which is null, empty or containing null value.
     */
    public PossibleLiteralValuesChoice(List<Values> allowedValues) throws IllegalArgumentException {
        //Verify if the parameters are not null
        if (allowedValues == null || allowedValues.isEmpty() || allowedValues.contains(null)) {
            throw new IllegalArgumentException("The parameter \"allowedValues\" can not be null or empty or " +
                    "containing null value");
        }
        this.allowedValues = new ArrayList<>();
        this.allowedValues.addAll(allowedValues);
        this.anyValue = false;
        this.valuesReference = null;
    }

    /**
     * Sets the allowed values as all the values valid with a reference.
     * @param valuesReference Reference for the valid values.
     * @throws IllegalArgumentException Exception get on setting a null reference.
     */
    public PossibleLiteralValuesChoice(URI valuesReference) throws IllegalArgumentException {
        if (valuesReference == null) {
            throw new IllegalArgumentException("The parameter \"valuesReference\" can not be null");
        }
        this.allowedValues = null;
        this.anyValue = false;
        this.valuesReference = valuesReference;
    }

    /**
     * Sets all the values as valid.
     */
    public PossibleLiteralValuesChoice() {
        this.allowedValues = null;
        this.anyValue = true;
        this.valuesReference = null;
    }
}
